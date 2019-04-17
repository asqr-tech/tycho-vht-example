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
package org.eclipse.vtp.desktop.model.elements.core.internal;

import org.eclipse.vtp.desktop.model.core.internal.design.Design;
import org.eclipse.vtp.desktop.model.core.internal.design.DesignElement;
import org.eclipse.vtp.desktop.model.core.internal.design.ElementFactory;

public class DialogElementFactory implements ElementFactory
{

	public DesignElement createElement(Design design, Object data)
	{
		String specializationId = (String)data;
		DialogElement de = new DialogElement(DialogElementManager.getDefault().getName(specializationId));
		design.getDocument().addDialogDesign(de.getId(), DialogElementManager.getDefault().getTemplate(specializationId));
		return de;
	}

}
