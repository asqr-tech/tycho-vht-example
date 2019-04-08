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
/*
 * InlineGRXMLGrammar.java Created on Jan 7, 2004
 */
package org.eclipse.vtp.framework.interactions.voice.vxml;

import java.util.LinkedList;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * The <code>InlineGrammar</code> class represents the &lt;grammar&gt; VXML
 * element with the grammar structure embedded as child elements.
 * 
 * @author Trip Gilman
 * @author Lonnie Pryor
 * @version 2.0
 */
public class InlineGrammar extends Grammar
{
	/** The list of rules in this grammar. */
	private final LinkedList<Rule> rules = new LinkedList<Rule>();

	/**
	 * Creates a new InlineGrammar.
	 * 
	 * @throws IllegalArgumentException If the specified mode is not one of "dtmf"
	 *           or "voice".
	 * @throws NullPointerException If the specified mode is <code>null</code>.
	 */
	public InlineGrammar(String mode) throws IllegalArgumentException,
			NullPointerException
	{
		super(mode);
	}

	/**
	 * Returns the list of rules in this grammar.
	 * 
	 * @return The list of rules in this grammar.
	 */
	public Rule[] getRules()
	{
		return rules.toArray(new Rule[rules.size()]);
	}

	/**
	 * Adds a rule to this grammar's list.
	 * 
	 * @param rule The rule to add.
	 * @throws NullPointerException If the specified rule is <code>null</code>.
	 */
	public void addRule(Rule rule) throws NullPointerException
	{
		if (rule == null)
			throw new NullPointerException("rule"); //$NON-NLS-1$
		rules.add(rule);
	}

	/**
	 * Removes a rule from this grammar's list.
	 * 
	 * @param rule The rule to remove.
	 * @throws NullPointerException If the specified rule is <code>null</code>.
	 */
	public void removeRule(Rule rule) throws NullPointerException
	{
		if (rule == null)
			throw new NullPointerException("rule"); //$NON-NLS-1$
		rules.remove(rule);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.voice.output.Grammar#writeAttributes(
	 *      org.xml.sax.helpers.AttributesImpl)
	 */
	protected void writeAttributes(AttributesImpl attributes)
	{
		super.writeAttributes(attributes);
		writeAttribute(attributes, null, null, NAME_ROOT, TYPE_CDATA, rules
				.getFirst().getId());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.voice.output.Grammar#writeChildren(
	 *      org.xml.sax.ContentHandler)
	 */
	protected void writeChildren(ContentHandler outputHandler)
			throws NullPointerException, SAXException
	{
		writeChildren(outputHandler, rules);
	}
}
