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
import org.eclipse.vtp.desktop.model.core.branding.IBrand;
import org.eclipse.vtp.desktop.model.core.design.IDesignElement;


public abstract class MediaConfigurationScreen
{
	private MediaConfigurationScreenContainer container;
	private IDesignElement element;
	private Control control;
	
	/**
	 * @param element
	 */
	public MediaConfigurationScreen(MediaConfigurationScreenContainer container)
	{
		super();
		this.container = container;
		this.element = container.getDesignElement();
	}
	
	/**
	 * @return
	 */
	public abstract String getInteractionType();
	
	/**
	 * @param parent
	 */
	public abstract void createControls(Composite parent);
	
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
	protected void setControl(Control control)
	{
		this.control = control;
	}
	
	public MediaConfigurationScreenContainer getContainer()
	{
		return container;
	}
	
	/**
	 * @return
	 */
	public IDesignElement getElement()
	{
		return element;
	}
	
	/**
	 * @param brand
	 */
	public abstract void setBrand(IBrand brand);
	
	/**
	 * @param language
	 */
	public abstract void setLanguage(String language);
	
	public abstract void save();
	
	public abstract void cancel();
}
