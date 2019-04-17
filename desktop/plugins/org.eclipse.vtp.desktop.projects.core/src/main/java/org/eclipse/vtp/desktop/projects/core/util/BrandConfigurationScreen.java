/*--------------------------------------------------------------------------
 * Copyright (c) 2009 OpenMethods, LLC
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Trip Gilman (OpenMethods)
 *    - initial API and implementation
 -------------------------------------------------------------------------*/
package org.eclipse.vtp.desktop.projects.core.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.vtp.desktop.model.core.branding.BrandManager;
import org.eclipse.vtp.desktop.model.core.branding.IBrand;
import org.eclipse.vtp.desktop.model.core.internal.branding.Brand;
import org.eclipse.vtp.desktop.projects.core.dialogs.BrandDialog;
import org.eclipse.vtp.framework.util.Guid;

/**
 * A configuration UI for an application's build path.
 * 
 * @author Trip Gilman
 */
public class BrandConfigurationScreen
{
	/** The brands in the application. */
	private TreeViewer brandViewer = null;
	/** The brand configuration. */
	private BrandManager brands = null;

	/**
	 * Creates a new BrandConfigurationScreen.
	 */
	public BrandConfigurationScreen()
	{
		super();
	}
	
	public void init(BrandManager brandManager)
	{
		this.brands = brandManager;
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
		comp.setLayout(new GridLayout());
		comp.setBackground(parent.getBackground());

		Section brandSection = toolkit.createSection(comp, Section.TITLE_BAR);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		brandSection.setLayoutData(gridData);
		brandSection.setText("Brands");

		brandViewer = new TreeViewer(comp, SWT.BORDER | SWT.SINGLE
				| SWT.FULL_SELECTION);
		brandViewer.setContentProvider(new BrandContentProvider());
		brandViewer.setLabelProvider(new BrandLabelProvider());
		brandViewer.setInput(this);
		GridData gridData2 = new GridData(GridData.FILL_HORIZONTAL);
		gridData2.minimumHeight = 125;
		gridData2.heightHint = 125;
		brandViewer.getControl().setLayoutData(gridData2);
		hookContextMenu();
		return comp;
	}

	private void hookContextMenu()
	{
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener()
		{
			public void menuAboutToShow(IMenuManager manager)
			{
				BrandConfigurationScreen.this.fillContextMenu(manager);
			}
		});

		Menu menu = menuMgr.createContextMenu(brandViewer.getControl());
		brandViewer.getControl().setMenu(menu);
	}

	/**
	 * @param manager
	 */
	private void fillContextMenu(IMenuManager manager)
	{
		IStructuredSelection sel = ((IStructuredSelection)brandViewer
				.getSelection());
		if (!sel.isEmpty())
		{
			final IBrand brand = (IBrand)sel.getFirstElement();
			manager.add(new Action("Add Brand")
			{
				public void run()
				{
					Shell workbenchShell = Display.getCurrent().getActiveShell();
					BrandDialog bd = new BrandDialog(workbenchShell);
					bd.setReservedNames(getBrandNames(brand));
					if (bd.open() == Dialog.OK)
					{
						Brand nbrand = new Brand(Guid.createGUID(), bd.getBrandName());
						nbrand.setParent(brand);
						brandViewer.refresh(brand);
						brandViewer.reveal(nbrand);
					}
				}
			});
			if (!brands.getDefaultBrand().getId().equals(brand.getId()))
				manager.add(new Action("Remove Brand")
				{
					public void run()
					{
						IBrand parentBrand = brand.getParent();
						brand.delete();
						brandViewer.refresh(parentBrand);
					}

				});
		}
	}

	private class BrandContentProvider implements IStructuredContentProvider,
			ITreeContentProvider
	{

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
		 */
		public Object[] getElements(Object inputElement)
		{
			return new Object[] { brands.getDefaultBrand() };
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
		 */
		public void dispose()
		{
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
		 */
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
		{
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
		 */
		public Object[] getChildren(Object parentElement)
		{
			return ((IBrand)parentElement).getChildBrands().toArray();
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
		 */
		public Object getParent(Object element)
		{
			return ((IBrand)element).getParent();
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
		 */
		public boolean hasChildren(Object element)
		{
			return ((IBrand)element).getChildBrands().size() > 0;
		}
	}

	private class BrandLabelProvider extends LabelProvider
	{

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
		 */
		public String getText(Object element)
		{
			return ((IBrand)element).getName();
		}

	}

	private List<String> getBrandNames(IBrand brand)
	{
		List<String> ret = new ArrayList<String>();
		for(IBrand child : brand.getChildBrands())
		{
			ret.add(child.getName());
		}
		return ret;
	}
}
