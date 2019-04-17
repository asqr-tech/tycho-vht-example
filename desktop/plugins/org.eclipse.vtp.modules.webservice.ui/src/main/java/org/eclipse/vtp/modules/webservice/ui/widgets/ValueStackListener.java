/*--------------------------------------------------------------------------
 * Copyright (c) 2009 OpenMethods, LLC
 * All rights reserved. 
 *
 * Contributors:
 *    Trip Gilman (OpenMethods)
 *    - initial API and implementation
 -------------------------------------------------------------------------*/
package org.eclipse.vtp.modules.webservice.ui.widgets;

/**
 * Listener interface for detecting when the value type of a value stack is
 * changed.
 * 
 * @author trip
 */
public interface ValueStackListener
{
	/**
	 * Executed when the value type of a value stack is changed.
	 * 
	 * @param valueStack The value stack that changed value types
	 */
	public void valueTypeChanged(ValueStack valueStack);
}
