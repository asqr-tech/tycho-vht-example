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
package org.eclipse.vtp.desktop.model.interactive.core.internal;

import org.eclipse.core.resources.IFile;
import org.eclipse.vtp.desktop.model.interactive.core.IMediaContainer;
import org.eclipse.vtp.desktop.model.interactive.core.IMediaFile;

public class GrammarFileFactory implements IMediaFileFactory
{

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.projects.core.IMediaFileFactory#createMediaFile(org.eclipse.vtp.desktop.projects.core.IMediaContainer, org.eclipse.core.resources.IFile)
	 */
	public IMediaFile createMediaFile(IMediaContainer container, IFile file)
	{
		return new GrammarFile(container, file);
	}

}
