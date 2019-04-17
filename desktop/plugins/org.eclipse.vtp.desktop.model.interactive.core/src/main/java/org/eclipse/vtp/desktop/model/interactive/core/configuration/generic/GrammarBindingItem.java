/*--------------------------------------------------------------------------
 * Copyright (c) 2009 OpenMethods, LLC
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Trip Gilman (OpenMethods)
 *    - initial API and implementation
 -------------------------------------------------------------------------*/
package org.eclipse.vtp.desktop.model.interactive.core.configuration.generic;

import org.eclipse.vtp.desktop.model.interactive.core.input.InputLoadingManager;
import org.eclipse.vtp.framework.interactions.core.media.InputGrammar;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This implementation of the <code>BindingItem</code> interface represents an
 * input grammar binding.  Input grammars are used to validate user input.
 * 
 * @author trip
 */
public class GrammarBindingItem implements BindingItem
{
	/**	The unique identifier for this binding item type */
	public static final String TYPE_ID = "org.eclipse.vtp.configuration.generic.items.grammar";
	
	/**	The bound input grammar */
	private InputGrammar grammar = null;

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.configuration.generic.BindingItem#getType()
	 */
	public String getType()
	{
		return TYPE_ID;
	}

	/**
	 * @return the input grammar being bound
	 */
	public InputGrammar getGrammar()
	{
		return grammar;
	}
	
	/**
	 * Binds the given input grammar to this binding structure.
	 * 
	 * @param grammar The input grammar to bind
	 */
	public void setGrammar(InputGrammar grammar)
	{
		this.grammar = grammar;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.configuration.generic.BindingItem#readConfiguration(org.w3c.dom.Element)
	 */
	public void readConfiguration(Element configuration)
	{
		NodeList inputList = configuration.getChildNodes();
		for (int i = 0; i < inputList.getLength(); i++)
		{
			if(inputList.item(i).getNodeType() == Node.ELEMENT_NODE)
			{
				setGrammar(InputLoadingManager.getInstance().loadInput((Element) inputList.item(i)));
				break;
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.configuration.generic.BindingItem#writeConfiguration(org.w3c.dom.Element)
	 */
	public void writeConfiguration(Element configuration)
	{
		grammar.store(configuration);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public Object clone()
	{
		GrammarBindingItem copy = new GrammarBindingItem();
		copy.grammar = grammar;
		return copy;
	}
}
