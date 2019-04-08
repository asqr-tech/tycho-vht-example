/*--------------------------------------------------------------------------
 * Copyright (c) 2004, 2006-2007 OpenMethods, LLC
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Trip Gilman (OpenMethods), Lonnie G. Pryor (OpenMethods)
 *    - initial API and implementation
 -------------------------------------------------------------------------*/
package org.eclipse.vtp.framework.common.configurations;

import org.eclipse.vtp.framework.core.IConfiguration;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * A configuration for a variable assignment.
 * 
 * @author Lonnie Pryor
 */
public class BranchConfiguration implements IConfiguration, CommonConstants
{
	/** The <code>==</code> comparison type. */
	public static final int COMPARISON_TYPE_EQUAL = 0;
	/** The <code>&lt;</code> comparison type. */
	public static final int COMPARISON_TYPE_LESS_THAN = 1;
	/** The <code>&lt;=</code> comparison type. */
	public static final int COMPARISON_TYPE_LESS_THAN_OR_EQUAL = 2;
	/** The <code>&gt;</code> comparison type. */
	public static final int COMPARISON_TYPE_GREATER_THAN = 3;
	/** The <code>&gt;=</code> comparison type. */
	public static final int COMPARISON_TYPE_GREATER_THAN_OR_EQUAL = 4;
	/** The <code>!=</code> comparison type. */
	public static final int COMPARISON_TYPE_NOT_EQUAL = 5;
	/** The variable operand type. */
	public static final int OPERAND_TYPE_VARIABLE = 0;
	/** The expression operand type. */
	public static final int OPERAND_TYPE_EXPRESSION = 1;

	/** The type of comparison to make. */
	private int type = COMPARISON_TYPE_EQUAL;
	/** The path to follow if the comparison is true. */
	private String path = ""; //$NON-NLS-1$
	/** The type of the left-hand operand. */
	private int leftType = OPERAND_TYPE_VARIABLE;
	/** The value to use for the left-hand side of the operation. */
	private String leftValue = ""; //$NON-NLS-1$
	private boolean leftSecured = false;
	/** The scripting language to use for the left-hand side of the operation. */
	private String leftScriptingLanguage = null; //$NON-NLS-1$
	/** The type of the right-hand operand. */
	private int rightType = OPERAND_TYPE_VARIABLE;
	/** The value to use for the right-hand side of the operation. */
	private String rightValue = ""; //$NON-NLS-1$
	private boolean rightSecured = false;
	/** The scripting language to use for the right-hand side of the operation. */
	private String rightScriptingLanguage = null; //$NON-NLS-1$

	/**
	 * Creates a new AssignmentConfiguration.
	 */
	public BranchConfiguration()
	{
	}

	/**
	 * Returns the type of comparison to make.
	 * 
	 * @return The type of comparison to make.
	 */
	public int getType()
	{
		return type;
	}

	/**
	 * Sets the type of comparison to make.
	 * 
	 * @param type The type of comparison to make.
	 */
	public void setType(int type)
	{
		this.type = type;
	}

	/**
	 * Returns the path to follow if the comparison is true.
	 * 
	 * @return The path to follow if the comparison is true.
	 */
	public String getPath()
	{
		return path;
	}

	/**
	 * Sets the path to follow if the comparison is true.
	 * 
	 * @param path The path to follow if the comparison is true.
	 */
	public void setPath(String path)
	{
		this.path = path == null ? "" : path; //$NON-NLS-1$;
	}

	/**
	 * Returns the type of the left-hand operand.
	 * 
	 * @return The type of the left-hand operand.
	 */
	public int getLeftType()
	{
		return leftType;
	}

	/**
	 * Returns the value to use for the left-hand side of the operation.
	 * 
	 * @return The value to use for the left-hand side of the operation.
	 */
	public String getLeftValue()
	{
		return leftValue;
	}
	
	public boolean isLeftSecured()
	{
		return leftSecured;
	}

	/**
	 * Returns the scripting language to use for the left-hand side of the
	 * operation.
	 * 
	 * @return The scripting language to use for the left-hand side of the
	 *         operation.
	 */
	public String getLeftScriptingLanguage()
	{
		return leftScriptingLanguage;
	}

	/**
	 * Sets the value to use for the left-hand side of the operation.
	 * 
	 * @param leftValue The name of the variable to use for the left-hand side of
	 *          the operation.
	 */
	public void setLeftVariableValue(String leftValue)
	{
		this.leftType = OPERAND_TYPE_VARIABLE;
		this.leftValue = leftValue == null ? "" : leftValue; //$NON-NLS-1$;
		this.leftScriptingLanguage = null;
	}

	/**
	 * Sets the value to use for the left-hand side of the operation.
	 * 
	 * @param leftValue The expression to use for the left-hand side of the
	 *          operation.
	 * @param leftScriptingLanguage The scripting language to use for the
	 *          left-hand side of the operation.
	 */
	public void setLeftExpressionValue(String leftValue,
			String leftScriptingLanguage)
	{
		this.leftType = OPERAND_TYPE_EXPRESSION;
		this.leftValue = leftValue == null ? "" : leftValue; //$NON-NLS-1$;
		this.leftScriptingLanguage = leftScriptingLanguage;
	}
	
	public void setLeftSecured(boolean leftSecured)
	{
		this.leftSecured = leftSecured;
	}

	/**
	 * Returns the type of the right-hand operand.
	 * 
	 * @return The type of the right-hand operand.
	 */
	public int getRightType()
	{
		return rightType;
	}

	/**
	 * Returns the value to use for the right-hand side of the operation.
	 * 
	 * @return The value to use for the right-hand side of the operation.
	 */
	public String getRightValue()
	{
		return rightValue;
	}
	
	public boolean isRightSecured()
	{
		return rightSecured;
	}

	/**
	 * Returns the rightScriptingLanguage.
	 * 
	 * @return The rightScriptingLanguage.
	 */
	public String getRightScriptingLanguage()
	{
		return rightScriptingLanguage;
	}

	/**
	 * Sets the value to use for the right-hand side of the operation.
	 * 
	 * @param rightValue The name of the variable to use for the right-hand side
	 *          of the operation.
	 */
	public void setRightVariableValue(String rightValue)
	{
		this.rightType = OPERAND_TYPE_VARIABLE;
		this.rightValue = rightValue == null ? "" : rightValue; //$NON-NLS-1$;
		this.rightScriptingLanguage = null;
	}

	/**
	 * Sets the value to use for the right-hand side of the operation.
	 * 
	 * @param rightValue The expression to use for the right-hand side of the
	 *          operation.
	 * @param rightScriptingLanguage The scripting language to use for the
	 *          right-hand side of the operation.
	 */
	public void setRightExpressionValue(String rightValue,
			String rightScriptingLanguage)
	{
		this.rightType = OPERAND_TYPE_EXPRESSION;
		this.rightValue = rightValue == null ? "" : rightValue; //$NON-NLS-1$;
		this.rightScriptingLanguage = rightScriptingLanguage;
	}
	
	public void setRightSecured(boolean rightSecured)
	{
		this.rightSecured = rightSecured;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IConfiguration#load(
	 *      org.w3c.dom.Element)
	 */
	public void load(Element configurationElement)
	{
		type = COMPARISON_TYPE_EQUAL;
		String typeStr = configurationElement.getAttribute(NAME_TYPE);
		if ("equal".equals(typeStr)) //$NON-NLS-1$
			type = COMPARISON_TYPE_EQUAL;
		else if ("less-than".equals(typeStr)) //$NON-NLS-1$
			type = COMPARISON_TYPE_LESS_THAN;
		else if ("less-than-or-equal".equals(typeStr)) //$NON-NLS-1$
			type = COMPARISON_TYPE_LESS_THAN_OR_EQUAL;
		else if ("greater-than".equals(typeStr)) //$NON-NLS-1$
			type = COMPARISON_TYPE_GREATER_THAN;
		else if ("greater-than-or-equal".equals(typeStr)) //$NON-NLS-1$
			type = COMPARISON_TYPE_GREATER_THAN_OR_EQUAL;
		else if ("not-equal".equals(typeStr)) //$NON-NLS-1$
			type = COMPARISON_TYPE_NOT_EQUAL;
		path = configurationElement.getAttribute(NAME_PATH);
		leftType = OPERAND_TYPE_VARIABLE;
		leftValue = ""; //$NON-NLS-1$
		leftScriptingLanguage = null;
		NodeList leftList = configurationElement.getElementsByTagNameNS(
				NAMESPACE_URI, NAME_LEFT_OPERAND);
		if (leftList.getLength() > 0)
		{
			Element left = (Element)leftList.item(0);
			if ("expression".equalsIgnoreCase(left.getAttribute(NAME_TYPE))) //$NON-NLS-1$
				leftType = OPERAND_TYPE_EXPRESSION;
			else
				leftType = OPERAND_TYPE_VARIABLE;
			leftValue = left.getAttribute(NAME_VALUE);
			leftSecured = Boolean.parseBoolean(left.getAttribute(NAME_SECURED));
			if (left.hasAttribute(NAME_SCRIPTING_LANGUGAGE))
				leftScriptingLanguage = left.getAttribute(NAME_SCRIPTING_LANGUGAGE);
		}
		rightType = OPERAND_TYPE_VARIABLE;
		rightValue = ""; //$NON-NLS-1$
		rightScriptingLanguage = null;
		NodeList rightList = configurationElement.getElementsByTagNameNS(
				NAMESPACE_URI, NAME_RIGHT_OPERAND);
		if (rightList.getLength() > 0)
		{
			Element right = (Element)rightList.item(0);
			if ("expression".equalsIgnoreCase(right.getAttribute(NAME_TYPE))) //$NON-NLS-1$
				rightType = OPERAND_TYPE_EXPRESSION;
			else
				rightType = OPERAND_TYPE_VARIABLE;
			rightValue = right.getAttribute(NAME_VALUE);
			rightSecured = Boolean.parseBoolean(right.getAttribute(NAME_SECURED));
			if (right.hasAttribute(NAME_SCRIPTING_LANGUGAGE))
				rightScriptingLanguage = right.getAttribute(NAME_SCRIPTING_LANGUGAGE);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IConfiguration#save(
	 *      org.w3c.dom.Element)
	 */
	public void save(Element configurationElement)
	{
		String typeStr = null;
		switch (type)
		{
		case 0:
			typeStr = "equal"; //$NON-NLS-1$
			break;
		case 1:
			typeStr = "less-than"; //$NON-NLS-1$
			break;
		case 2:
			typeStr = "less-than-or-equal"; //$NON-NLS-1$
			break;
		case 3:
			typeStr = "greater-than"; //$NON-NLS-1$
			break;
		case 4:
			typeStr = "greater-than-or-equal"; //$NON-NLS-1$
			break;
		case 5:
			typeStr = "not-equal"; //$NON-NLS-1$
			break;
		}
		configurationElement.setAttribute(NAME_TYPE, typeStr);
		String leftName = NAME_LEFT_OPERAND;
		String rightName = NAME_RIGHT_OPERAND;
		String prefix = configurationElement.getPrefix();
		if (prefix != null && prefix.length() > 0)
		{
			leftName = prefix + ":" + leftName; //$NON-NLS-1$
			rightName = prefix + ":" + rightName; //$NON-NLS-1$
		}
		configurationElement.setAttribute(NAME_PATH, path);
		Element left = configurationElement.getOwnerDocument().createElementNS(
				NAMESPACE_URI, leftName);
		left.setAttribute(NAME_TYPE, leftType == OPERAND_TYPE_VARIABLE ? //
		"variable" //$NON-NLS-1$
				: "expression"); //$NON-NLS-1$
		left.setAttribute(NAME_VALUE, leftValue);
		left.setAttribute(NAME_SECURED, Boolean.toString(leftSecured));
		if (leftScriptingLanguage != null)
			left.setAttribute(NAME_SCRIPTING_LANGUGAGE, leftScriptingLanguage);
		configurationElement.appendChild(left);
		Element right = configurationElement.getOwnerDocument().createElementNS(
				NAMESPACE_URI, rightName);
		right.setAttribute(NAME_TYPE, rightType == OPERAND_TYPE_VARIABLE ? //
		"variable" //$NON-NLS-1$
				: "expression"); //$NON-NLS-1$
		right.setAttribute(NAME_VALUE, rightValue);
		right.setAttribute(NAME_SECURED, Boolean.toString(rightSecured));
		if (rightScriptingLanguage != null)
			right.setAttribute(NAME_SCRIPTING_LANGUGAGE, rightScriptingLanguage);
		configurationElement.appendChild(right);
	}
}
