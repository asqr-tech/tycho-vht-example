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
package org.eclipse.vtp.desktop.core.dialogs;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * This is the base class for all pages to be displayed in a
 * MultiPageFramedDialog.  Subclasses need to provide an implementation of
 * createPage() that performs any UI construction and returns the root control
 * for their page.
 * 
 * @author Trip
 */
public abstract class ContentPage
{
	/**	The name of this page.  Used by the dialog as a label. */
	private String name;
	/**	The shell that will contain this page. */
	private Shell rootShell;

	/**
	 * Constructs a new content page with the given name.
	 * 
	 * @param name The name of this content page
	 */
	public ContentPage(String name)
	{
		super();
		this.name = name;
	}

	/**
	 * @return The shell that contains this content page
	 */
	public Shell getRootShell()
	{
		return rootShell;
	}

	/**
	 * Provides a reference to the shell that contains this content page.
	 * 
	 * @param rootShell The shell that contains this content page
	 */
	void setRootShell(Shell rootShell)
	{
		this.rootShell = rootShell;
	}

	/**
	 * Subclasses must provide an implementation for this method that constructs
	 * their UI and returns the root control for the page.
	 * 
	 * @param parent The composite that will contain this page
	 * @return The control that was added to the parent composite and is the
	 * root of all UI elements of this page
	 */
	protected abstract Control createPage(Composite parent);

	/**
	 * @return The name of this page
	 */
	public String getName()
	{
		return name;
	}
}
