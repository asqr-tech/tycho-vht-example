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
package org.eclipse.vtp.desktop.projects.interactive.core.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.vtp.desktop.model.core.branding.BrandManager;
import org.eclipse.vtp.desktop.model.core.branding.BrandManagerListener;
import org.eclipse.vtp.desktop.model.core.branding.IBrand;
import org.eclipse.vtp.desktop.model.core.internal.branding.Brand;
import org.eclipse.vtp.desktop.model.interactive.core.IMediaProject;
import org.eclipse.vtp.desktop.model.interactive.core.InteractiveWorkflowCore;
import org.eclipse.vtp.desktop.model.interactive.core.internal.InteractionTypeSupport;
import org.eclipse.vtp.desktop.model.interactive.core.internal.LanguageSupport;
import org.eclipse.vtp.desktop.projects.core.dialogs.BrandDialog;
import org.eclipse.vtp.desktop.projects.interactive.core.dialogs.LanguageConfigurationDialog;
import org.eclipse.vtp.desktop.projects.interactive.core.util.InteractionSupportManager.SupportRecord;
import org.eclipse.vtp.framework.util.Guid;

/**
 * A configuration UI for an application's build path.
 * 
 * @author Trip Gilman
 */
public class LanguageMappingScreen
{
	/** The supported interaction types */
	private SupportRecord supportRecord = null;
	/** The brand configuration. */
	private BrandManager brandManager = null;
	/** The brands in the application. */
	private TreeViewer brandViewer = null;
	private InteractionTypeSupport support = null;
	private List<IMediaProject> mediaProjects = null;

	/**
	 * Creates a new LanguageConfigurationScreen.
	 */
	public LanguageMappingScreen()
	{
		super();
		mediaProjects = InteractiveWorkflowCore.getDefault().getInteractiveWorkflowModel().listMediaProjects();
	}
	
	public void init(BrandManager brandManager, SupportRecord supportRecord)
	{
		this.brandManager = brandManager;
		brandManager.addListener(new BrandManagerListener()
		{
			
			public void brandRemoved(IBrand brand)
			{
			}
			
			public void brandParentChanged(IBrand brand, IBrand oldParent)
			{
			}
			
			public void brandNameChanged(IBrand brand, String oldName)
			{
				if(brandViewer != null)
				{
					brandViewer.refresh();
				}
			}
			
			public void brandAdded(IBrand brand)
			{
				if(brandViewer != null)
				{
					brandViewer.refresh();
				}
			}
		});
		this.supportRecord = supportRecord;
		support = supportRecord.getSupport();
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
		brandSection.setText(supportRecord.getName());
		
		final Composite treeComp = new Composite(comp, SWT.NONE);
		treeComp.setLayout(new FillLayout());
		GridData gridData2 = new GridData(GridData.FILL_HORIZONTAL);
		gridData2.minimumHeight = 125;
		gridData2.heightHint = 125;
		treeComp.setLayoutData(gridData2);
		createTree(treeComp);
		
		final Button languageButton = new Button(comp, SWT.PUSH);
		languageButton.setText("Configure Languages");
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalAlignment = SWT.RIGHT;
		languageButton.setLayoutData(gd);
		languageButton.addSelectionListener(new SelectionListener()
		{
			public void widgetDefaultSelected(SelectionEvent e)
			{
			}

			public void widgetSelected(SelectionEvent e)
			{
				LanguageConfigurationDialog lcd = new LanguageConfigurationDialog(languageButton.getShell());
				lcd.setCurrentSupport(support);
				if(lcd.open() == Dialog.OK)
				{
					brandViewer.getControl().dispose();
					createTree(treeComp);
					treeComp.layout(true, true);
				}
			}
		});
		
		hookContextMenu();
		return comp;
	}
	
	private void createTree(Composite parent)
	{
		Tree tree = new Tree(parent, SWT.BORDER | SWT.SINGLE | SWT.FULL_SELECTION);
		tree.setHeaderVisible(true);
		brandViewer = new TreeViewer(tree);

		TreeColumn brandColumn = new TreeColumn(tree, SWT.NONE);
		brandColumn.setWidth(100);
		brandColumn.setText("Brands");
		
		for(LanguageSupport language : support.getSupportedLanguages())
		{
			TreeViewerColumn languageColumn = new TreeViewerColumn(brandViewer, SWT.NONE);
			languageColumn.getColumn().setText(language.getLanguage());
			languageColumn.getColumn().setWidth(100);
			ProjectEditingSupport pes = new ProjectEditingSupport(brandViewer, language);
			languageColumn.setEditingSupport(pes);
		}
		
		brandViewer.setContentProvider(new BrandContentProvider());
		brandViewer.setLabelProvider(new BrandLabelProvider());
		brandViewer.setInput(this);
		
	}

	private void hookContextMenu()
	{
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener()
		{
			public void menuAboutToShow(IMenuManager manager)
			{
				LanguageMappingScreen.this.fillContextMenu(manager);
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
					}
				}
			});
			if (!brandManager.getDefaultBrand().getId().equals(brand.getId()))
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
			return new Object[] { brandManager.getDefaultBrand() };
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

	private class BrandLabelProvider extends LabelProvider implements ITableLabelProvider
	{

		public Image getColumnImage(Object element, int columnIndex)
		{
			return null;
		}

		public String getColumnText(Object element, int columnIndex)
		{
			IBrand brand = (IBrand)element;
			if(columnIndex == 0)
				return brand.getName();
			LanguageSupport language = support.getSupportedLanguages().get(columnIndex - 1);
			String mediaProjectId = language.getMediaProjectId(brand, false);
			if(mediaProjectId != null)
			{
				IMediaProject mediaProject = InteractiveWorkflowCore.getDefault().getInteractiveWorkflowModel().getMediaProject(mediaProjectId);
				if(mediaProject == null)
					return "Not Found (" + mediaProjectId + ")";
				return mediaProject.getName();
			}
			if(brand.equals(brandManager.getDefaultBrand()))
			{
				return "Not Configured";
			}
			else
			{
				return language.getMediaProjectId(brand) == null ? "NotConfigured" : "Inherit From Parent";
			}
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
	
	public class ProjectEditingSupport extends EditingSupport
	{
		ComboBoxCellEditor defaultBrandEditor = null;
		ComboBoxCellEditor subBrandEditor = null;
		LanguageSupport language = null;

		public ProjectEditingSupport(TreeViewer viewer, LanguageSupport language)
		{
			super(viewer);
			this.language = language;
			String[] defaultItems = new String[mediaProjects.size() + 1];
			defaultItems[0] = "Not Configured";
			String[] subItems = new String[mediaProjects.size() + 1];
			subItems[0] = "Inherit From Parent";
			for(int i = 0; i < mediaProjects.size(); i++)
			{
				defaultItems[i + 1] = mediaProjects.get(i).getName();
				subItems[i + 1] = mediaProjects.get(i).getName();
			}
			defaultBrandEditor = new ComboBoxCellEditor(viewer.getTree(), defaultItems, SWT.DROP_DOWN | SWT.READ_ONLY);
			subBrandEditor = new ComboBoxCellEditor(viewer.getTree(), subItems, SWT.DROP_DOWN | SWT.READ_ONLY);
		}

		public void modify(Object element, String property, Object value)
		{
			IBrand brand = (IBrand)((TreeItem)element).getData();
			LanguageSupport ls = support.getLanguageSupport(property);
			if(value.equals("Inherit From Parent") || value.equals("Not Configured"))
			{
				ls.assignMediaProject(brand.getId(), null);
			}
			else
			{
				IMediaProject mediaProject = (IMediaProject)value;
				ls.assignMediaProject(brand.getId(), mediaProject.getId());
			}
			brandViewer.refresh();
		}
	
		@Override
		protected boolean canEdit(Object element)
		{
			return true;
		}
	
		@Override
		protected CellEditor getCellEditor(Object element)
		{
			IBrand brand = (IBrand)element;
			if(brand.getParent() == null)
				return defaultBrandEditor;
			return subBrandEditor;
		}
	
		@Override
		protected Object getValue(Object element)
		{
			Integer ret = new Integer(0);
			System.out.println("in get value");
			try
			{
				IBrand brand = (IBrand)element;
				String mediaProjectId = language.getMediaProjectId(brand, false);
				System.out.println("media project id: " + mediaProjectId);
				if(mediaProjectId == null)
				{
					ret = new Integer(0);
				}
				else
				{
					for(int i = 0; i < mediaProjects.size(); i++)
					{
						if(mediaProjects.get(i).getId().equals(mediaProjectId))
						{
							ret = new Integer(i + 1);
							break;
						}
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			System.out.println(ret);
			return ret;
		}
	
		@Override
		protected void setValue(Object element, Object value)
		{
			System.out.println(value);
			IBrand brand = (IBrand)element;
			int vi = ((Integer)value).intValue();
			if(vi == 0)
			{
				language.assignMediaProject(brand.getId(), null);
			}
			else
			{
				language.assignMediaProject(brand.getId(), mediaProjects.get(vi - 1).getId());
			}
			this.getViewer().refresh();
		}
	}
}
