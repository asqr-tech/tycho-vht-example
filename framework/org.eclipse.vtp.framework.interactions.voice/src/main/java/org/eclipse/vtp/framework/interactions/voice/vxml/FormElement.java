/*--------------------------------------------------------------------------
 * Copyright (c) 2004, 2006 OpenMethods, LLC
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Trip Gilman (OpenMethods), Lonnie G. Pryor (OpenMethods),
 *    T.D. Barnes (OpenMethods) - initial API and implementation
 -------------------------------------------------------------------------*/
package org.eclipse.vtp.framework.interactions.voice.vxml;

import org.eclipse.vtp.framework.interactions.core.support.Widget;
import org.xml.sax.helpers.AttributesImpl;

/**
 * The <code>FormElement</code> class is an abstraction of the family of VXML
 * elements that represent the units of interaction with the caller contained
 * within a form element. Each form element is identified by a name that is
 * unique within the scope of the document that contains its parent form. The
 * optional expression has different meanings for each subclass. For more
 * information, refer to the documentation for each subclass. An optional
 * condition can be specified that must be true for the form element to
 * processed, such as, another form element having a specific value.
 * 
 * @author Trip Gilman
 * @author Lonnie Pryor
 * @version 2.0
 */
public abstract class FormElement extends Widget implements VXMLConstants
{
	/** The name of the form element. */
	private String name;
	/** The expression that is evaluated before processing of the element. */
	private String expression = null;
	/** The condition that must be true for the element to be processed. */
	private String condition = null;

	/**
	 * Creates a new instance of <code>FormElement</code> with the specified
	 * name. The expression and condition fields are initially <code>null</code>.
	 * Throws an IllegalArgumentException if the name argument is
	 * <code>null</code> or an empty string.
	 * 
	 * @param name The name of the form element.
	 * @throws IllegalArgumentException If the specified name is empty.
	 * @throws NullPointerException If the specified name is <code>null</code>.
	 */
	public FormElement(String name) throws IllegalArgumentException,
			NullPointerException
	{
		setName(name);
	}

	/**
	 * Creates a new instance of <code>FormElement</code> with the specified
	 * name and expression. The condition field is initially <code>null</code>.
	 * Throws an IllegalArgumentException if the name argument is
	 * <code>null</code> or an empty string. Throws an IllegalArgumentException
	 * if the expression argument is an empty string.
	 * 
	 * @param name The name of the form element.
	 * @param expression An expression that is evaluated before processing of the
	 *          form element.
	 * @throws IllegalArgumentException If the specified name is empty.
	 * @throws IllegalArgumentException If the specified expression is empty.
	 * @throws NullPointerException If the specified name is <code>null</code>.
	 */
	public FormElement(String name, String expression)
			throws IllegalArgumentException, NullPointerException
	{
		setName(name);
		setExpression(expression);
	}

	/**
	 * Creates a new instance of <code>FormElement</code> with the specified
	 * name, expression, and condition. Throws an IllegalArgumentException if the
	 * name argument is <code>null</code> or an empty string. Throws an
	 * IllegalArgumentException if the expression argument is an empty string.
	 * Throws an IllegalArgumentException if the condition argument is an empty
	 * string.
	 * 
	 * @param name The name of the form element.
	 * @param expression An expression that is evaluated before processing of the
	 *          form element.
	 * @param condition A conditional statement that must evaluate to true for the
	 *          form element to be processed.
	 * @throws IllegalArgumentException If the specified name is empty.
	 * @throws IllegalArgumentException If the specified expression is empty.
	 * @throws IllegalArgumentException If the specified condition is empty.
	 * @throws NullPointerException If the specified name is <code>null</code>.
	 */
	public FormElement(String name, String expression, String condition)
			throws IllegalArgumentException, NullPointerException
	{
		setName(name);
		setExpression(expression);
		setCondition(condition);
	}

	/**
	 * Returns the name of the form element.
	 * 
	 * @return The name of the form element.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Returns the expression to evaluate prior to processing the form element.
	 * 
	 * @return The expression to evaluate prior to processing the form element.
	 */
	public String getExpression()
	{
		return expression;
	}

	/**
	 * Returns the condition that must be true for the form element to be.
	 * processed.
	 * 
	 * @return The condition that must be true for the form element to be.
	 *         processed.
	 */
	public String getCondition()
	{
		return condition;
	}

	/**
	 * Sets the name of the form element. Throws an IllegalArgumentException if
	 * the name argument is <code>null</code> or an empty string.
	 * 
	 * @param name The new name of the form element
	 * @throws IllegalArgumentException If the specified name is empty.
	 * @throws NullPointerException If the specified name is <code>null</code>.
	 */
	public void setName(String name) throws IllegalArgumentException,
			NullPointerException
	{
		if (name == null)
			throw new NullPointerException("name"); //$NON-NLS-1$
		if (name.length() == 0)
			throw new IllegalArgumentException("name"); //$NON-NLS-1$
		this.name = name;
	}

	/**
	 * Sets the expression to evaluate prior to processing the form element.
	 * Throws an IllegalArgumentException if the expression argument is an empty
	 * string.
	 * 
	 * @param expression The new expression to evaluate.
	 * @throws IllegalArgumentException If the specified expression is empty.
	 */
	public void setExpression(String expression) throws IllegalArgumentException
	{
		if (expression != null && expression.length() == 0)
			throw new IllegalArgumentException("expression"); //$NON-NLS-1$
		this.expression = expression;
	}

	/**
	 * Sets the condition that must evaluate to true before the form element is
	 * processed. Throws an IllegalArgumentException if the condition argument is
	 * an empty string.
	 * 
	 * @param condition The new condition to evaluate.
	 * @throws IllegalArgumentException If the specified condition is empty.
	 */
	public void setCondition(String condition) throws IllegalArgumentException
	{
		if (condition != null && condition.length() == 0)
			throw new IllegalArgumentException("condition"); //$NON-NLS-1$
		this.condition = condition;
	}

	/**
	 * Write the attribute members of this form element to the supplied set.
	 * 
	 * @param attributes The attribute set to write to.
	 * @throws NullPointerException If the supplied attribute set is
	 *           <code>null</code>.
	 */
	protected void writeAttributes(AttributesImpl attributes)
	{
		writeAttribute(attributes, null, null, NAME_NAME, TYPE_CDATA, name);
		if (expression != null)
			writeAttribute(attributes, null, null, NAME_EXPR, TYPE_CDATA, expression);
		if (condition != null)
			writeAttribute(attributes, null, null, NAME_COND, TYPE_CDATA, condition);
	}
}
