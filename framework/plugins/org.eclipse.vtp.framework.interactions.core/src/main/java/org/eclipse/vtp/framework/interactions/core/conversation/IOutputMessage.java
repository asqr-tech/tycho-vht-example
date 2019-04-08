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
package org.eclipse.vtp.framework.interactions.core.conversation;

/**
 * A message that displays data to the agent on the other end of a conversation.
 * 
 * @author Lonnie Pryor
 */
public interface IOutputMessage extends IInteraction
{
	/**
	 * Sets the value of a parameter configured on this interaction.
	 * 
	 * @param name The name of the parameter to set the value of.
	 * @param value The value to set the parameter to.
	 */
	void setParameterValue(String name, String value);

	/**
	 * Sets all the values configured for the specified parameter on this
	 * interaction.
	 * 
	 * @param name The name of the parameter to set the values of.
	 * @param values The values to set the parameter to.
	 */
	void setParameterValues(String name, String[] values);
}
