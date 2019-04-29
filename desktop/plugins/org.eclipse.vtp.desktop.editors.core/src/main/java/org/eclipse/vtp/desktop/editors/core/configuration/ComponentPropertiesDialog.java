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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.vtp.desktop.core.dialogs.ContentPage;
import org.eclipse.vtp.desktop.core.dialogs.MultiPageFramedDialog;
import org.eclipse.vtp.desktop.model.core.IWorkflowProject;
import org.eclipse.vtp.desktop.model.core.design.IDesign;

/**
 * This is the generic dialog that is used to display and edit the properties
 * of a module instance included in a call flow.  This dialog is typically
 * accessed from the "Properties" option of the module's context menu or by
 * double-clicking the module itself.  The properties are organized into
 * pages.  The list of available pages is displayed in the navigation bar
 * located on the left side of the dialog.  The selected page is presented
 * in the main content area of the dialog.  The dialog offers two buttons:
 * OK and Cancel.  The Cancel button is used to discard any changes made to
 * the module properties while this dialog was open.  The OK button will
 * cause all changes to be saved for the module.  The propagation of changes
 * to other interested parties is left up to the pages or the opener of this
 * dialog.
 *
 * @author Trip
 * @version 1.0
 */
public class ComponentPropertiesDialog extends MultiPageFramedDialog implements ConfigurationContextSelectorListener
{
	private IWorkflowProject project = null;
	private ConfigurationContextSelector selector = null;
	
	/**
	 * Creates a new <code>ComponentPropertiesDialog</code> instance.
	 *
	 * @param parentShell The SWT shell object which will serve as the
	 * parent window of this dialog
	 * @param page The current canvas of the selected voice application editor
	 */
	public ComponentPropertiesDialog(IDesign design, Shell parentShell)
	{
		super(parentShell);
		this.setSideBarSize(120);
		this.project = design.getDocument().getProject();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	protected boolean processOk()
	{
		try
		{
			List<ContentPage> pages = getPages();
			Iterator<ContentPage> iterator = pages.iterator();
	
			while(iterator.hasNext())
			{
				((ComponentPropertiesPanel)iterator.next()).save();
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.dialogs.MultiPageFramedDialog#cancelPressed()
	 */
	protected void cancelPressed()
    {
		try
		{
			List<ContentPage> pages = getPages();
			Iterator<ContentPage> iterator = pages.iterator();
	
			while(iterator.hasNext())
			{
				((ComponentPropertiesPanel)iterator.next()).cancel();
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	    super.cancelPressed();
    }
	
	public void cancelDialog()
	{
		cancelPressed();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.window.Window#handleShellCloseEvent()
	 */
	protected void handleShellCloseEvent()
    {
		try
		{
			List<ContentPage> pages = getPages();
			Iterator<ContentPage> iterator = pages.iterator();
	
			while(iterator.hasNext())
			{
				((ComponentPropertiesPanel)iterator.next()).cancel();
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	    super.handleShellCloseEvent();
    }

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.dialogs.MultiPageFramedDialog#createDialogContents(org.eclipse.swt.widgets.Composite)
	 */
	protected void createDialogContents(Composite parent)
	{
		try
		{
			parent.setLayout(new GridLayout(1, false));
			Composite topComp = new Composite(parent, SWT.NONE);
			topComp.setBackgroundMode(SWT.INHERIT_DEFAULT);
			topComp.setBackground(parent.getBackground());
			topComp.setLayout(new FillLayout());
			GridData gridData = new GridData();
			gridData.horizontalAlignment = SWT.CENTER;
			topComp.setLayoutData(gridData);
			selector = new ConfigurationContextSelector(project);
			selector.createControls(topComp);
			Composite bottomComp = new Composite(parent, SWT.NONE);
			bottomComp.setBackground(parent.getBackground());
			bottomComp.setLayout(new FillLayout());
			bottomComp.setLayoutData(new GridData(GridData.FILL_BOTH));
			Composite c = new Composite(bottomComp, SWT.NONE);
			c.setBackground(bottomComp.getBackground());
			super.createDialogContents(c);
			selector.addListener(this);
			selector.setContextFilter(((ComponentPropertiesPanel)this.getCurrentPage()).getApplicableContexts());
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public void contextSelectionChanged(Map<String, Object> values)
	{
		List<ContentPage> pages = getPages();
		Iterator<ContentPage> iterator = pages.iterator();
		while(iterator.hasNext())
		{
			((ComponentPropertiesPanel)iterator.next()).setConfigurationContext(values);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.dialogs.MultiPageFramedDialog#addPage(org.eclipse.vtp.desktop.core.dialogs.ContentPage)
	 */
	public void addPage(ContentPage page)
    {
	    if(page instanceof ComponentPropertiesPanel)
	    {
	    	((ComponentPropertiesPanel)page).setContainer(this);
	    }
	    super.addPage(page);
    }

	@Override
	public void selectionChanged(String selection)
	{
		super.selectionChanged(selection);
		selector.setContextFilter(((ComponentPropertiesPanel)this.getCurrentPage()).getApplicableContexts());
	}
	
	public List<ComponentPropertiesPanel> getPanels()
	{
		List<ComponentPropertiesPanel> ret = new LinkedList<ComponentPropertiesPanel>();
		for(ContentPage contentPage : this.getPages())
		{
			if(contentPage instanceof ComponentPropertiesPanel)
				ret.add((ComponentPropertiesPanel)contentPage);
		}
		return ret;
	}
}
