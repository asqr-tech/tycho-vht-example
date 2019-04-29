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
package org.eclipse.vtp.modules.attacheddata.ui.configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.vtp.desktop.editors.core.configuration.ComponentPropertiesPanel;
import org.eclipse.vtp.desktop.editors.core.configuration.ConnectorPropertiesListener;
import org.eclipse.vtp.desktop.editors.core.configuration.ConnectorPropertiesPanel;
import org.eclipse.vtp.desktop.model.core.branding.IBrand;
import org.eclipse.vtp.desktop.model.core.design.IDesignConnector;
import org.eclipse.vtp.desktop.model.core.design.IDesignElement;
import org.eclipse.vtp.desktop.model.core.design.IDesignElementConnectionPoint;
import org.eclipse.vtp.desktop.model.core.internal.branding.BrandContext;
import org.eclipse.vtp.desktop.model.core.internal.design.ConnectorRecord;
import org.eclipse.vtp.desktop.model.interactive.core.InteractionType;
import org.eclipse.vtp.desktop.model.interactive.core.internal.context.InteractionTypeContext;
import org.eclipse.vtp.desktop.model.interactive.core.internal.context.LanguageContext;
import org.eclipse.vtp.modules.attacheddata.ui.configuration.post.AttachedDataBinding;
import org.eclipse.vtp.modules.attacheddata.ui.configuration.post.AttachedDataBindingItem;
import org.eclipse.vtp.modules.attacheddata.ui.configuration.post.AttachedDataItemEntry;
import org.eclipse.vtp.modules.attacheddata.ui.configuration.post.AttachedDataManager;
import org.eclipse.vtp.modules.attacheddata.ui.dialogs.AttachedDataEntryDialog;

public class AttachedDataPropertiesPanel extends ComponentPropertiesPanel implements ConnectorPropertiesListener
{
	private IDesignConnector connector = null;
	private TreeViewer dataViewer = null;
	private AttachedDataManager attachedDataManager = null;
	private IBrand currentBrand = null;
	private String interactionType = null;
	private String currentLanguage = null;
	private List<IDesignElementConnectionPoint> currentExits = new ArrayList<IDesignElementConnectionPoint>();

	public AttachedDataPropertiesPanel(IDesignConnector connector)
	{
		super("Attached Data");
		this.connector = connector;
		IDesignElement origin = connector.getOrigin();
		attachedDataManager = (AttachedDataManager)origin.getConfigurationManager("org.eclipse.vtp.configuration.attacheddata");
	}
	
	public void resolve()
	{
		List<ComponentPropertiesPanel> panels = getContainer().getPanels();
		for(ComponentPropertiesPanel panel : panels)
		{
			if(panel instanceof ConnectorPropertiesPanel)
			{
				((ConnectorPropertiesPanel)panel).addEndPointListener(this);
				break;
			}
		}
	}

	public void createControls(Composite parent)
	{
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setBackground(parent.getBackground());
		comp.setLayout(new GridLayout(1, false));
		Tree dataTree = new Tree(comp, SWT.FULL_SELECTION | SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL);
		dataTree.setHeaderVisible(true);
		TreeColumn nameColumn = new TreeColumn(dataTree, SWT.NONE);
		nameColumn.setText("Name");
		nameColumn.setWidth(150);
		TreeColumn valueColumn = new TreeColumn(dataTree, SWT.NONE);
		valueColumn.setText("Value");
		valueColumn.setWidth(150);
		dataViewer = new TreeViewer(dataTree);
		GridData layoutData = new GridData(GridData.FILL_BOTH);
//		layoutData.heightHint = 400;
		dataViewer.getControl().setLayoutData(layoutData);
		dataViewer.setContentProvider(new AttachedDataContentProvider());
		dataViewer.setLabelProvider(new AttachedDataLabelProvider());
		dataViewer.setInput(this);
		
/*		Composite buttonComp = new Composite(comp, SWT.NONE);
		buttonComp.setBackground(comp.getBackground());
		buttonComp.setLayout(new GridLayout(1, false));
		GridData gd = new GridData();
		gd.verticalAlignment = SWT.TOP;
		buttonComp.setLayoutData(gd);
		
		addDataButton = new Button(buttonComp, SWT.PUSH);
		addDataButton.setText("Add Attached Data");
		gd = new GridData(GridData.FILL_HORIZONTAL);
		addDataButton.setLayoutData(gd);
		addDataButton.addSelectionListener(new SelectionListener()
		{

			public void widgetDefaultSelected(SelectionEvent e)
            {
            }

			public void widgetSelected(SelectionEvent e)
            {
            }
			
		});
		
		editButton = new Button(buttonComp, SWT.PUSH);
		editButton.setText("Edit Attached Data");
		editButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		removeButton = new Button(buttonComp, SWT.PUSH);
		removeButton.setText("Remove Attached Data");
		removeButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
*/		this.setControl(comp);
		hookContextMenu();
		hookDoubleClickAction();
	}

	/**
	 * Hooks our actions into the context menu.
	 */
	private void hookContextMenu()
	{
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener()
			{
				public void menuAboutToShow(IMenuManager manager)
				{
					AttachedDataPropertiesPanel.this.fillContextMenu(manager);
				}
			});

		Menu menu = menuMgr.createContextMenu(dataViewer.getControl());
		dataViewer.getControl().setMenu(menu);
	}

	/**
	 * Hooks our double-click handler.
	 */
	private void hookDoubleClickAction()
	{
		dataViewer.addDoubleClickListener(new IDoubleClickListener()
			{
				public void doubleClick(DoubleClickEvent event)
				{
					final IStructuredSelection sel = (IStructuredSelection)dataViewer.getSelection();
					if(sel.getFirstElement() instanceof EntryRecord)
					{
						final EntryRecord entryRecord = (EntryRecord)sel.getFirstElement();
				        try
                        {
	                        Shell workbenchShell = Display.getCurrent().getActiveShell();
	                        AttachedDataEntryDialog aded = new AttachedDataEntryDialog(workbenchShell);
	                        aded.setConnector(connector, entryRecord.connectorRecord.getName());
							aded.setEntry(entryRecord.entry);
	                        if(aded.open() == Dialog.OK)
	                        {
	                        	entryRecord.binding.putAttachedDataItem(currentBrand.getName(), interactionType, currentLanguage, entryRecord.item);
	                        	dataViewer.refresh();
	                        }
                        }
                        catch(RuntimeException e)
                        {
	                        // TODO Auto-generated catch block
	                        e.printStackTrace();
                        }
					}
				}
			});
	}
	
	/**
	 * fillContextMenu.
	 *
	 * @param manager
	 */
	private void fillContextMenu(IMenuManager manager)
	{
		if(!dataViewer.getSelection().isEmpty())
		{
			final IStructuredSelection sel = (IStructuredSelection)dataViewer.getSelection();
			if(sel.getFirstElement() instanceof ConnectorRecord)
			{
				manager.add(new Action("Add Attached Data")
				{
					public void run()
					{
				        try
                        {
				        	ConnectorRecord cr = (ConnectorRecord)sel.getFirstElement();
	                        Shell workbenchShell = Display.getCurrent().getActiveShell();
	                        AttachedDataEntryDialog aded = new AttachedDataEntryDialog(workbenchShell);
	                        aded.setConnector(connector, cr.getName());
	                        if(aded.open() == Dialog.OK)
	                        {
	                        	AttachedDataBinding dataBinding = attachedDataManager.getAttachedDataBinding(cr.getName());
	                        	AttachedDataBindingItem item = dataBinding.getAttachedDataItem(currentBrand.getName(), interactionType, currentLanguage);
	                        	item.addEntry(aded.getEntry());
	                        	dataBinding.putAttachedDataItem(currentBrand.getName(), interactionType, currentLanguage, item);
	                        	dataViewer.refresh();
	                        }
                        }
                        catch(RuntimeException e)
                        {
	                        // TODO Auto-generated catch block
	                        e.printStackTrace();
                        }
					}
				});
			}
			else //attached data entry
			{
				final EntryRecord entryRecord = (EntryRecord)sel.getFirstElement();
				manager.add(new Action("Edit This")
				{
					public void run()
					{
				        try
                        {
	                        Shell workbenchShell = Display.getCurrent().getActiveShell();
	                        AttachedDataEntryDialog aded = new AttachedDataEntryDialog(workbenchShell);
	                        aded.setConnector(connector, entryRecord.connectorRecord.getName());
							aded.setEntry(entryRecord.entry);
	                        if(aded.open() == Dialog.OK)
	                        {
	                        	entryRecord.binding.putAttachedDataItem(currentBrand.getName(), interactionType, currentLanguage, entryRecord.item);
	                        	dataViewer.refresh();
	                        }
                        }
                        catch(RuntimeException e)
                        {
	                        e.printStackTrace();
                        }
					}
				});
				manager.add(new Action("Remove This")
				{
					public void run()
					{
 						entryRecord.item.removeEntry(entryRecord.entry);
						entryRecord.binding.putAttachedDataItem(currentBrand.getName(), interactionType, currentLanguage, entryRecord.item);
						dataViewer.refresh();
					}
				});
			}
		}
	}

	public void save()
	{
		connector.getOrigin().commitConfigurationChanges(attachedDataManager);
	}
	
	public void cancel()
	{
		connector.getOrigin().rollbackConfigurationChanges(attachedDataManager);
	}

	public List<String> getApplicableContexts()
	{
		List<String> ret = super.getApplicableContexts();
		ret.add(LanguageContext.CONTEXT_ID);
		ret.add(InteractionTypeContext.CONTEXT_ID);
		return ret;
	}

	public void setConfigurationContext(Map<String, Object> values)
	{
		currentBrand = (IBrand)values.get(BrandContext.CONTEXT_ID);
		currentLanguage = (String)values.get(LanguageContext.CONTEXT_ID);
		this.interactionType = ((InteractionType)values.get(InteractionTypeContext.CONTEXT_ID)).getId();
		dataViewer.refresh();
	}

	private class AttachedDataContentProvider implements ITreeContentProvider
	{

		public Object[] getChildren(Object parentElement)
        {
			System.out.println("Parent element: " + parentElement);
			if(parentElement instanceof ConnectorRecord)
			{
				ConnectorRecord cr = (ConnectorRecord)parentElement;
				System.out.println("connector: " + cr.getName());
				AttachedDataBinding dataBinding = attachedDataManager.getAttachedDataBinding(cr.getName());
				System.out.println("data binding: " + dataBinding);
				AttachedDataBindingItem item = dataBinding.getAttachedDataItem(currentBrand.getName(), interactionType, currentLanguage);
				System.out.println("binding item: " + item);
				List<AttachedDataItemEntry> copy = item.getEntries();
				System.out.println("item entries: " + copy + " [" + copy.size() + "]");
				List<EntryRecord> ret = new ArrayList<EntryRecord>();
				for(int i = 0; i < copy.size(); i++)
                {
	                AttachedDataItemEntry entry = copy.get(i);
	                System.out.println("Entry: " + entry.getName() + " " + entry.getDataType() + " " + entry.getValue());
	                ret.add(new EntryRecord(cr, dataBinding, item, entry));
                }
				return ret.toArray();
			}
			return null;
        }

		public Object getParent(Object element)
        {
			if(element instanceof EntryRecord)
				return ((EntryRecord)element).connectorRecord;
	        return null;
        }

		public boolean hasChildren(Object element)
        {
	        return element instanceof ConnectorRecord;
        }

		public Object[] getElements(Object inputElement)
        {
	        return currentExits.toArray();
        }

		public void dispose()
        {
        }

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
        {
        }
		
	}
	
	private class AttachedDataLabelProvider extends LabelProvider implements ITableLabelProvider
	{

		public Image getColumnImage(Object element, int columnIndex)
        {
	        return null;
        }

		public String getColumnText(Object element, int columnIndex)
        {
			if(columnIndex == 0) //name column
			{
				if(element instanceof ConnectorRecord)
				{
					return ((ConnectorRecord)element).getName();
				}
				return ((EntryRecord)element).entry.getName();
			}
			else if(columnIndex == 1)
			{
				if(element instanceof EntryRecord)
				{
					return ((EntryRecord)element).entry.getValue();
				}
			}
	        return null;
        }
		
	}
	
	private class EntryRecord
	{
		ConnectorRecord connectorRecord;
		AttachedDataBinding binding;
		AttachedDataBindingItem item;
		AttachedDataItemEntry entry;
		
		public EntryRecord(ConnectorRecord connectorRecord, AttachedDataBinding binding, AttachedDataBindingItem item, AttachedDataItemEntry entry)
		{
			super();
			this.connectorRecord = connectorRecord;
			this.binding = binding;
			this.item = item;
			this.entry = entry;
		}
	}

	public void exitPointSelectionChanged(List<IDesignElementConnectionPoint> selection)
    {
		currentExits = selection;
		if(dataViewer != null)
		{
			dataViewer.getTree().getDisplay().asyncExec(new Runnable(){
				public void run()
				{
					dataViewer.refresh();
				}
			});
		}
    }
}
