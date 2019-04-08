/*--------------------------------------------------------------------------
 * Copyright (c) 2004, 2006-2007 OpenMethods, LLC
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Trip Gilman (OpenMethods), Lonnie G. Pryor (OpenMethods), 
 *    Randy Childers (OpenMethods)
 *    - initial API and implementation
 -------------------------------------------------------------------------*/
package org.eclipse.vtp.framework.common.actions;

import java.math.BigDecimal;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import org.eclipse.vtp.framework.common.IDataObject;
import org.eclipse.vtp.framework.common.ILastResult;
import org.eclipse.vtp.framework.common.ILastResultData;
import org.eclipse.vtp.framework.common.IScriptingEngine;
import org.eclipse.vtp.framework.common.IScriptingService;
import org.eclipse.vtp.framework.common.IVariableRegistry;
import org.eclipse.vtp.framework.common.configurations.BranchConfiguration;
import org.eclipse.vtp.framework.core.IAction;
import org.eclipse.vtp.framework.core.IActionContext;
import org.eclipse.vtp.framework.core.IActionResult;
import org.eclipse.vtp.framework.core.IReporter;

/**
 * An action that branches the process flow on certain conditions.
 * 
 * @author Lonnie Pryor
 */
public class BranchAction implements IAction
{
	private static final String[] COMPARISONS = { "=", "<", "<=", ">", ">=", "!=" };

	/** The context to use. */
	private final IActionContext context;
	/** The variable registry to use. */
	private final IVariableRegistry variableRegistry;
	/** The scripting service to use. */
	private final IScriptingService scriptingService;
	/** The configurations to use. */
	private final BranchConfiguration[] configurations;
	private final ILastResult lastResult;

	/**
	 * Creates a new BranchAction.
	 * 
	 * @param context The context to use.
	 * @param variableRegistry The variable registry to use.
	 * @param scriptingService The scripting service to use.
	 * @param configurations The configurations to use.
	 */
	public BranchAction(IActionContext context,
			IVariableRegistry variableRegistry, IScriptingService scriptingService,
			BranchConfiguration[] configurations, ILastResult lastResult)
	{
		this.context = context;
		this.variableRegistry = variableRegistry;
		this.scriptingService = scriptingService;
		this.configurations = configurations;
		this.lastResult = lastResult;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IAction#execute()
	 */
	public IActionResult execute()
	{
		try
		{
			if(context.isInfoEnabled()) context.info("Performing comparison(s)");
			for (int i = 0; i < configurations.length; ++i)
			{
				Object left = evaluate(configurations[i].getLeftType(),
						configurations[i].getLeftValue(), configurations[i]
								.getLeftScriptingLanguage());
				Object right = evaluate(configurations[i].getRightType(),
						configurations[i].getRightValue(), configurations[i]
								.getRightScriptingLanguage());
				boolean result = compare(left, right, configurations[i]);
				if(context.isReportingEnabled())
				{
					Dictionary<String, Object> props = new Hashtable<String, Object>();
					props.put("event", "branch");
					props.put("branch.left", isSecured(left, configurations[i].isLeftSecured()) ? "**Secured**" : String.valueOf(left));
					props.put("branch.operator", COMPARISONS[configurations[i].getType()]);
					props.put("branch.right", isSecured(right, configurations[i].isRightSecured()) ? "**Secured**" : String.valueOf(right));
					props.put("branch.result", String.valueOf(result));
					context.report(IReporter.SEVERITY_INFO, "Comparison \""
						+ configurations[i].getLeftValue() + "\" "
						+ COMPARISONS[configurations[i].getType()] + " \""
						+ configurations[i].getRightValue() + "\" evaluated to " + result,
						props);
				}
				if (result)
					return context.createResult(configurations[i].getPath());
			}
			return context.createResult(IActionResult.RESULT_NAME_DEFAULT);
		}
		catch (RuntimeException e)
		{
			return context.createResult("error.branch", e); //$NON-NLS-1$
		}
	}

	/**
	 * Evaluates an operand of a branch.
	 * 
	 * @param operandType The type of the operand.
	 * @param operandValue The value of the operand.
	 * @param scriptingLanguage The scripting language to use if the operand is an
	 *          expression.
	 * @return The value of the operand.
	 */
	private Object evaluate(int operandType, String operandValue,
			String scriptingLanguage)
	{
		if (operandType == BranchConfiguration.OPERAND_TYPE_VARIABLE)
		{
			if(operandValue.startsWith("LastResult"))
			{
				List<ILastResultData> results = lastResult.getResults();
				if(results.size() < 1)
					return null;
				ILastResultData data = results.get(0);
				String varName = operandValue.substring(11);
				if("confidence".equals(varName))
				{
					return new Integer(data.getConfidence());
				}
				if("utterance".equals(varName))
				{
					return data.getUtterence();
				}
				if("inputmode".equals(varName))
				{
					return data.getInputMode();
				}
				if("interpretation".equals(varName))
				{
					return data.getInterpretation();
				}
				return null;
			}
			return variableRegistry.getVariable(operandValue);
		}
		IScriptingEngine engine = scriptingService
				.createScriptingEngine(scriptingLanguage);
		if (engine == null)
			return null;
		return engine.execute(operandValue);
	}

	/**
	 * Compares two objects using the specified comparison.
	 * 
	 * @param left The left-hand side of the comparison.
	 * @param right The right-hand side of the comparison.
	 * @param comparison The operation to perform.
	 * @return The result of the operation.
	 */
	private boolean compare(Object left, Object right, BranchConfiguration configuration)
	{
		int comparison = configuration.getType();
		if(context.isInfoEnabled()) context.info("Left object: " + (left == null ? "null" : left.getClass().getName())+ " (" + (isSecured(left, configuration.isLeftSecured()) ? "**Secured**" : String.valueOf(left)) + ") Right object: " + (right == null ? "null" : right.getClass().getName()) + " (" + (isSecured(right, configuration.isRightSecured()) ? "**Secured**" : String.valueOf(right)) + ")");
		if (left instanceof IDataObject)
		{
			IDataObject data = (IDataObject)left;
			switch (comparison)
			{
			case BranchConfiguration.COMPARISON_TYPE_EQUAL:
				return data.isEqualTo(right);
			case BranchConfiguration.COMPARISON_TYPE_LESS_THAN:
				return data.isLessThan(right);
			case BranchConfiguration.COMPARISON_TYPE_LESS_THAN_OR_EQUAL:
				return data.isLessThanOrEqualTo(right);
			case BranchConfiguration.COMPARISON_TYPE_GREATER_THAN:
				return data.isGreaterThan(right);
			case BranchConfiguration.COMPARISON_TYPE_GREATER_THAN_OR_EQUAL:
				return data.isGreaterThanOrEqualTo(right);
			case BranchConfiguration.COMPARISON_TYPE_NOT_EQUAL:
				return !data.isEqualTo(right);
			}
			return false;
		}
		else if (right instanceof IDataObject)
		{
			IDataObject data = (IDataObject)right;
			switch (comparison)
			{
			case BranchConfiguration.COMPARISON_TYPE_EQUAL:
				return data.isEqualTo(left);
			case BranchConfiguration.COMPARISON_TYPE_GREATER_THAN:
				return data.isLessThan(left);
			case BranchConfiguration.COMPARISON_TYPE_GREATER_THAN_OR_EQUAL:
				return data.isLessThanOrEqualTo(left);
			case BranchConfiguration.COMPARISON_TYPE_LESS_THAN:
				return data.isGreaterThan(left);
			case BranchConfiguration.COMPARISON_TYPE_LESS_THAN_OR_EQUAL:
				return data.isGreaterThanOrEqualTo(left);
			case BranchConfiguration.COMPARISON_TYPE_NOT_EQUAL:
				return !data.isEqualTo(left);
			}
			return false;
		}
		else if (left == right)
			return comparison == BranchConfiguration.COMPARISON_TYPE_EQUAL
					|| comparison == BranchConfiguration.COMPARISON_TYPE_LESS_THAN_OR_EQUAL
					|| comparison == BranchConfiguration.COMPARISON_TYPE_GREATER_THAN_OR_EQUAL;
		else if (left == null || right == null)
			return comparison == BranchConfiguration.COMPARISON_TYPE_NOT_EQUAL;
		if (!(left instanceof String) && left instanceof Comparable && left.getClass().equals(right.getClass()))
		{
			return performCastComparison(left, right, comparison);
		}
		else
		{
			String leftString  = left.toString();
			String rightString  = right.toString();
			try
			{
				BigDecimal leftBD = new BigDecimal(leftString);
				BigDecimal rightBD = new BigDecimal(rightString);
				return performComparison(leftBD, rightBD, comparison);
			}
			catch(Exception nfe)
			{
			}
			return performComparison(leftString, rightString, comparison);
		}
	}

	/**
	 *  Suppress warnings because we know both objects are the same concrete type.
	 *  
	 *  @param left The left-hand comparison operand.
	 *  @param right The right-hand comparison operand.
	 *  @param comparison The type of comparison to perform.
	 *  @return The result of the comparison operation.
	 */
	@SuppressWarnings("unchecked")
	private boolean performCastComparison (Object left, Object right, int comparison)
	{
		return performComparison((Comparable<Object>)left, right, comparison);
	}

	/**
	 *  Perform a type-safe comparison.
	 *  
	 *  @param left The left-hand comparison operand.
	 *  @param right The right-hand comparison operand.
	 *  @param comparison The type of comparison to perform.
	 *  @return The result of the comparison operation.
	 */
	private <T> boolean performComparison (Comparable<? super T> left, T right, int comparison)
	{
		switch (comparison)
		{
		case BranchConfiguration.COMPARISON_TYPE_EQUAL:
			return left.compareTo(right) == 0;
		case BranchConfiguration.COMPARISON_TYPE_LESS_THAN:
			return left.compareTo(right) < 0;
		case BranchConfiguration.COMPARISON_TYPE_LESS_THAN_OR_EQUAL:
			return left.compareTo(right) <= 0;
		case BranchConfiguration.COMPARISON_TYPE_GREATER_THAN:
			return left.compareTo(right) > 0;
		case BranchConfiguration.COMPARISON_TYPE_GREATER_THAN_OR_EQUAL:
			return left.compareTo(right) >= 0;
		case BranchConfiguration.COMPARISON_TYPE_NOT_EQUAL:
			return left.compareTo(right) != 0;
		default:
			return false;
		}
	}
	
	private boolean isSecured(Object value, boolean override)
	{
		if(override)
			return true;
		if(value instanceof IDataObject)
			return ((IDataObject)value).isSecured();
		return false;
	}
}
