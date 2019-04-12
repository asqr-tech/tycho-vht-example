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
package org.eclipse.vtp.desktop.projects.interactive.core.util;

import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ICheckStateProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.vtp.desktop.projects.interactive.core.util.InteractionSupportManager.SupportRecord;

/**
 * A configuration UI for an application's build path.
 * 
 * @author Lonnie Pryor
 */
public class InteractionTypeConfigurationScreen implements ICheckStateListener
{
	private InteractionSupportManager supportManager = null;
	private CheckboxTableViewer viewer = null;

	/**
	 * Creates a new InteractionTypeConfigurationScreen.
	 */
	public InteractionTypeConfigurationScreen()
	{
	}

	public void setSupport(InteractionSupportManager supportManager)
	{
		this.supportManager = supportManager;
	}

	/**
	 * @param parent
	 * @return
	 */
	public Control createContents(Composite parent)
	{
		final FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		ScrolledForm sf = toolkit.createScrolledForm(parent);
		Composite comp = sf.getForm().getBody();
		comp.setLayout(new GridLayout(1, true));
		comp.setBackground(parent.getBackground());
		
		Section brandSection = toolkit.createSection(comp, Section.TITLE_BAR);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		brandSection.setLayoutData(gridData);
		brandSection.setText("Supported Interaction Types");

		Table table = new Table(comp, SWT.BORDER | SWT.FULL_SELECTION | SWT.CHECK | SWT.SINGLE);
		table.setHeaderVisible(true);
		TableLayout layout = new TableLayout();
		layout.addColumnData(new ColumnWeightData(1));
		table.setLayout(layout);
		TableColumn column = new TableColumn(table, SWT.NONE);
		column.setText("Interaction Type");
		column.setWidth(100);
		column.setResizable(true);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.heightHint = 200;
		table.setLayoutData(gd);
		
		viewer = new CheckboxTableViewer(table);
		viewer.setContentProvider(new InteractionTypeContentProvider());
		InteractionTypeLabelProvider labelProvider = new InteractionTypeLabelProvider();
		viewer.setLabelProvider(labelProvider);
		viewer.setCheckStateProvider(labelProvider);
		viewer.addCheckStateListener(this);
		viewer.setInput(this);
		
		return comp;
	}

	public class InteractionTypeContentProvider implements IStructuredContentProvider
	{

		public Object[] getElements(Object inputElement)
		{
			return supportManager.getCurrentSupport().toArray();
		}

		public void dispose()
		{
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
		{
		}
		
	}
	
	public class InteractionTypeLabelProvider extends BaseLabelProvider implements ITableLabelProvider, ICheckStateProvider
	{
		public Image getColumnImage(Object element, int columnIndex)
		{
			return null;
		}

		public String getColumnText(Object element, int columnIndex)
		{
			SupportRecord sr = (SupportRecord)element;
			return sr.getName() + (sr.isInstalled() ? "" : " (Not Installed)");
		}

		public boolean isChecked(Object element)
		{
			SupportRecord sr = (SupportRecord)element;
			return sr.isSupported();
		}

		public boolean isGrayed(Object element)
		{
			return false;
		}

	}

	public void checkStateChanged(CheckStateChangedEvent event)
	{
		SupportRecord sr = (SupportRecord)event.getElement();
		if(event.getChecked())
			supportManager.addSupport(sr);
		else
			supportManager.removeSupport(sr);
	}
}
