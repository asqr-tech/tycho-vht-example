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
package org.eclipse.vtp.desktop.media.core;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.vtp.framework.interactions.core.media.InputGrammar;

public class EmptyInputGrammarCreatorPanel extends InputGrammarCreatorPanel
{

	public EmptyInputGrammarCreatorPanel()
	{
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.media.core.InputGrammarCreatorPanel#createControls(org.eclipse.swt.widgets.Composite)
	 */
	public void createControls(Composite parent)
	{
		Composite comp = new Composite(parent, SWT.NONE);
		setControl(comp);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.media.core.InputGrammarCreatorPanel#createGrammar()
	 */
	public InputGrammar createGrammar()
	{
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.media.core.InputGrammarCreatorPanel#setInitialInput(org.eclipse.vtp.framework.interactions.core.media.InputGrammar)
	 */
	public void setInitialInput(InputGrammar content)
	{
	}

}
