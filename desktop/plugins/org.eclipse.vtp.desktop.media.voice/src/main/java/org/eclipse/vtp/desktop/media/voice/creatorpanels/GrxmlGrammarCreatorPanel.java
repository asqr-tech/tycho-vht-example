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
package org.eclipse.vtp.desktop.media.voice.creatorpanels;

import org.eclipse.vtp.desktop.media.core.GrammarFileCreatorPanel;
import org.eclipse.vtp.framework.interactions.core.media.FileInputGrammar;
import org.eclipse.vtp.framework.interactions.voice.media.GrxmlInputGrammar;

public class GrxmlGrammarCreatorPanel extends GrammarFileCreatorPanel
{

	public GrxmlGrammarCreatorPanel()
	{
		super();
	}

	protected FileInputGrammar createNewGrammar()
	{
		return new GrxmlInputGrammar();
	}

}
