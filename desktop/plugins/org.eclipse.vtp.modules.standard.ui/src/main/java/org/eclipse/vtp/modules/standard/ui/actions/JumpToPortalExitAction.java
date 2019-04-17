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
package org.eclipse.vtp.modules.standard.ui.actions;

import org.eclipse.vtp.desktop.editors.core.actions.DesignElementAction;
import org.eclipse.vtp.desktop.editors.themes.core.commands.CommandListener;
import org.eclipse.vtp.desktop.editors.themes.core.commands.LocateElement;
import org.eclipse.vtp.desktop.model.core.design.IDesignElement;
import org.eclipse.vtp.desktop.model.elements.core.internal.PrimitiveElement;
import org.eclipse.vtp.modules.standard.ui.PortalEntryInformationProvider;

public class JumpToPortalExitAction extends DesignElementAction
{

	/**
	 * @param element
	 * @param commandListener
	 */
	public JumpToPortalExitAction(IDesignElement element,
	        CommandListener commandListener)
	{
		super(element, commandListener);
		this.setText("Jump to Exit");
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#run()
	 */
	public void run()
	{
		PrimitiveElement primitiveElement = (PrimitiveElement)getElement();
		String exitId = ((PortalEntryInformationProvider)primitiveElement.getInformationProvider()).getExitId();
		getCommandListener().executeCommand(new LocateElement(exitId));
	}
}
