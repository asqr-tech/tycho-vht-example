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
import org.eclipse.vtp.desktop.model.interactive.core.IGrammarFile;
import org.eclipse.vtp.desktop.model.interactive.core.IMediaContainer;

/**
 * This is a concrete implementation of <code>IGrammarFile</code>
 * and provides the default behavior of that interface.
 *
 * @author Trip Gilman
 * @version 2.0
 */
public class GrammarFile extends MediaFile implements IGrammarFile
{
	/**
	 * Creates a new <code>GrammarFile</code> in the given media
	 * container with the provided eclipse file resource.
	 *
	 * @param container The parent media container
	 * @param file The eclipse file resource this grammar file represents
	 */
	public GrammarFile(IMediaContainer container, IFile file)
	{
		super(container, file);
	}
}
