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

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.vtp.framework.interactions.core.media.Content;
import org.eclipse.vtp.framework.interactions.core.media.IMediaProvider;

public abstract class ContentCreatorPanel
{
	private IMediaProvider mediaProvider;
	private Control control;

	public ContentCreatorPanel()
	{
		super();
	}
	
	/**
	 * @param mediaProvider
	 */
	public void setMediaProvider(IMediaProvider mediaProvider)
	{
		this.mediaProvider = mediaProvider;
	}
	
	/**
	 * @return
	 */
	public IMediaProvider getMediaProvider() {
		return mediaProvider;
	}
	
	/**
	 * @param content
	 */
	public abstract void setInitialContent(Content content);
	
	/**
	 * @return
	 */
	public Control getControl()
	{
		return control;
	}
	
	/**
	 * @param control
	 */
	public void setControl(Control control)
	{
		this.control = control;
	}

	/**
	 * @param parent
	 */
	public abstract void createControls(Composite parent);
	
	/**
	 * @return
	 */
	public abstract Content createContent();
}
