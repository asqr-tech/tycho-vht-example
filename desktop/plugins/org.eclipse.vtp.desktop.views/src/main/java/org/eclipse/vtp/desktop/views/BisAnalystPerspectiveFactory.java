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
package org.eclipse.vtp.desktop.views;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/**
 * BisAnalystPerspectiveFactory.
 *
 * @author Lonnie Pryor
 */
public class BisAnalystPerspectiveFactory implements IPerspectiveFactory
{
	/**
	 * Creates a new BisAnalystPerspectiveFactory.
	 *
	 */
	public BisAnalystPerspectiveFactory()
	{
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IPerspectiveFactory#createInitialLayout(org.eclipse.ui.IPageLayout)
	 */
	public void createInitialLayout(IPageLayout layout)
	{
		IFolderLayout flayout =
			layout.createFolder("org.eclipse.vtp.desktop.ui.app.viewfolder",
				IPageLayout.LEFT, .20f, IPageLayout.ID_EDITOR_AREA);
		flayout.addView("org.eclipse.ui.navigator.ProjectExplorer");
		IFolderLayout flayout3 = layout.createFolder("org.eclipse.vtp.desktop.ui.app.viewbottomfolder", IPageLayout.BOTTOM, .80f, IPageLayout.ID_EDITOR_AREA);
		flayout3.addView("org.eclipse.ui.views.ProblemView");
		IFolderLayout flayout2 =
			layout.createFolder("org.eclipse.vtp.desktop.ui.app.viewfolderright",
				IPageLayout.RIGHT, .80f, IPageLayout.ID_EDITOR_AREA);
		flayout2.addView("org.eclipse.vtp.desktop.views.pallet");
//		flayout2.addView("org.eclipse.vtp.desktop.views.canvasbrowser");
	}
}
