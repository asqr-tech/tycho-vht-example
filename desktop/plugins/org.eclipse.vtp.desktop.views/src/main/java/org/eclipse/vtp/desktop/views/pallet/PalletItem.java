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
package org.eclipse.vtp.desktop.views.pallet;

import org.eclipse.swt.graphics.Image;
import org.eclipse.vtp.desktop.model.core.design.IDesign;
import org.eclipse.vtp.desktop.model.core.internal.design.Design;
import org.eclipse.vtp.desktop.model.core.internal.design.DesignElement;
import org.eclipse.vtp.desktop.model.core.internal.design.ElementFactory;

public class PalletItem
{
	String name;
	private ElementFactory factory;
	private Object data;
	private Image icon;
	private boolean popOnDrop = false;
	
	public PalletItem(String name, Image icon, ElementFactory factory, Object data)
	{
		super();
		this.name = name;
		this.icon = icon;
		this.factory = factory;
		this.data = data;
	}
	
	public DesignElement createElement(Design design)
	{
		return factory.createElement(design, data);
	}
	
	public Object getData()
	{
		return data;
	}
	
	public String getName()
	{
		return name;
	}
	
	public Image getIcon()
	{
		return icon;
	}
	
	public boolean canBeContainedBy(IDesign iDesign)
	{
		return true;
	}

	public boolean isPopOnDrop()
    {
    	return popOnDrop;
    }

	public void setPopOnDrop(boolean popOnDrop)
    {
    	this.popOnDrop = popOnDrop;
    }

}
