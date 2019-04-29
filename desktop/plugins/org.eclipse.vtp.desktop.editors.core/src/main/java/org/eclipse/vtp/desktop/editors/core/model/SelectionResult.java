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
/**
 * 
 */
package org.eclipse.vtp.desktop.editors.core.model;

public class SelectionResult
{
	private boolean hit = false;
	private boolean selectionChanged = false;
	private boolean primaryChanged = false;
	
	public SelectionResult()
	{
		super();
	}
	
	public SelectionResult(boolean hit, boolean selectionChanged, boolean primaryChanged)
	{
		super();
		this.hit = hit;
		this.selectionChanged = selectionChanged;
		this.primaryChanged = primaryChanged;
	}

	public boolean wasHit()
	{
		return hit;
	}

	public void setHit(boolean hit)
	{
		this.hit = hit;
	}

	public boolean wasPrimaryChanged()
	{
		return primaryChanged;
	}

	public void setPrimaryChanged(boolean primaryChanged)
	{
		this.primaryChanged = primaryChanged;
	}

	public boolean wasSelectionChanged()
	{
		return selectionChanged;
	}

	public void setSelectionChanged(boolean selectionChanged)
	{
		this.selectionChanged = selectionChanged;
	}
}