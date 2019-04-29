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
package org.eclipse.vtp.desktop.editors.core.configuration;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.vtp.desktop.core.dialogs.ContentPage;
import org.eclipse.vtp.desktop.model.core.internal.branding.BrandContext;

/**
 * @author Trip
 * @version 1.0
 */
public abstract class ComponentPropertiesPanel extends ContentPage
{
	private Control control;
	private ComponentPropertiesDialog container;
	/**
	 * @param name
	 */
	public ComponentPropertiesPanel(String name)
	{
		super(name);
	}
	
	/**
	 * @param container
	 */
	void setContainer(ComponentPropertiesDialog container)
	{
		this.container = container;
	}
	
	/**
	 * @return
	 */
	protected ComponentPropertiesDialog getContainer()
	{
		return container;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.dialogs.ContentPage#createPage(org.eclipse.swt.widgets.Composite)
	 */
	public Control createPage(Composite parent)
	{
		Composite page = new Composite(parent, SWT.NONE);
		page.setBackground(parent.getBackground());
		page.setLayout(new FillLayout());
		createControls(page);

		return page;
	}
	
	/**
	 * @param control
	 */
	public void setControl(Control control)
	{
		this.control = control;
	}
	
	/**
	 * @return
	 */
	public Control getControl()
	{
		return control;
	}
	
	/**
	 * @param brand
	 * @param interactionType
	 * @param language
	 */
	public abstract void setConfigurationContext(Map<String, Object> values);
	
	public List<String> getApplicableContexts()
	{
		List<String> ret = new LinkedList<String>();
		ret.add(BrandContext.CONTEXT_ID);
		return ret;
	}

	/**
	 * @param parent
	 */
	public abstract void createControls(Composite parent);
	
	public void resolve()
	{
	}

	public abstract void save();
	
	public abstract void cancel();

	public int getRanking()
	{
		return 0;
	}
}
