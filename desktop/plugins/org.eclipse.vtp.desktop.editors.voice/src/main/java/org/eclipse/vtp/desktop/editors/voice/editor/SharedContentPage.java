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
package org.eclipse.vtp.desktop.editors.voice.editor;

import java.util.Iterator;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.vtp.desktop.model.interactive.core.InteractiveWorkflowCore;
import org.eclipse.vtp.desktop.model.interactive.voice.internal.VoiceModel;
import org.eclipse.vtp.framework.interactions.core.media.Content;
import org.eclipse.vtp.framework.interactions.core.media.ContentComposite;
import org.eclipse.vtp.framework.interactions.core.media.FileContent;
import org.eclipse.vtp.framework.interactions.core.media.FormattableContent;
import org.eclipse.vtp.framework.interactions.core.media.ReferencedContent;
import org.eclipse.vtp.framework.interactions.core.media.TextContent;

/**
 * 
 * @author lonnie
 */
public class SharedContentPage extends LabelProvider implements
		IStructuredContentProvider, ITableLabelProvider, IDoubleClickListener,
		ISelectionChangedListener, SelectionListener {
	private VoiceModel model = null;
	private TableViewer sharedContentTable = null;
	private Button addSharedContentButton = null;
	private Button editSharedContentButton = null;
	private Button removeSharedContentButton = null;

	public SharedContentPage() {
	}

	/**
	 * @param parent
	 * @param model
	 * @return
	 */
	public Control initialize(Composite parent, VoiceModel model) {
		this.model = model;
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(4, false));
		Table table = new Table(composite, SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.BORDER | SWT.MULTI);
		table.setHeaderVisible(true);
		TableColumn nameColumn = new TableColumn(table, SWT.LEFT);
		nameColumn.setText("Name");
		nameColumn.setWidth(100);
		TableColumn valueColumn = new TableColumn(table, SWT.LEFT);
		valueColumn.setWidth(100);
		valueColumn.setText("Value");
		table.pack();
		sharedContentTable = new TableViewer(table);
		sharedContentTable.getControl().setLayoutData(
				new GridData(GridData.FILL, GridData.FILL, true, true, 4, 1));
		sharedContentTable.addDoubleClickListener(this);
		sharedContentTable.addSelectionChangedListener(this);
		sharedContentTable.setLabelProvider(this);
		sharedContentTable.setContentProvider(this);
		sharedContentTable.setInput(model);
		sharedContentTable.getControl().addKeyListener(new KeyListener()
		{
			public void keyPressed(KeyEvent e)
			{
			}

			public void keyReleased(KeyEvent e)
			{
				if(e.keyCode == SWT.DEL || e.keyCode == SWT.BS)
					removeSharedContent();
			}
		});
		addSharedContentButton = new Button(composite, SWT.PUSH);
		addSharedContentButton.setLayoutData(new GridData(GridData.FILL,
				GridData.FILL, false, false));
		addSharedContentButton.setText("Add Shared Content");
		addSharedContentButton.addSelectionListener(this);
		editSharedContentButton = new Button(composite, SWT.PUSH);
		editSharedContentButton.setLayoutData(new GridData(GridData.FILL,
				GridData.FILL, false, false));
		editSharedContentButton.setText("Edit Shared Content");
		editSharedContentButton.setEnabled(false);
		editSharedContentButton.addSelectionListener(this);
		removeSharedContentButton = new Button(composite, SWT.PUSH);
		removeSharedContentButton.setLayoutData(new GridData(GridData.FILL,
				GridData.FILL, false, false));
		removeSharedContentButton.setText("Remove Shared Content");
		removeSharedContentButton.setEnabled(false);
		removeSharedContentButton.addSelectionListener(this);
		return composite;
	}

	private void addSharedContent() {
		VoiceContentDialog dialog = new VoiceContentDialog(sharedContentTable
				.getControl().getShell());
		dialog.initialize(null, model.getSharedContentNames(), null,
			InteractiveWorkflowCore.getDefault().getInteractiveWorkflowModel().convertToMediaProject(model.getProject()).getMediaProvider());
		if (dialog.open() != VoiceContentDialog.OK)
			return;
		model.putSharedContent(dialog.getItemName(), dialog.getContentItem());
		sharedContentTable.refresh();
	}

	private void editSharedContent() {
		IStructuredSelection selection = (IStructuredSelection) sharedContentTable
				.getSelection();
		if (selection.size() != 1)
			return;
		String itemName = (String) selection.getFirstElement();
		VoiceContentDialog dialog = new VoiceContentDialog(sharedContentTable
				.getControl().getShell());
		dialog.initialize(itemName, model.getSharedContentNames(),
			model.getSharedContent(itemName),
			InteractiveWorkflowCore.getDefault().getInteractiveWorkflowModel().convertToMediaProject(model.getProject()).getMediaProvider());
		if (dialog.open() != VoiceContentDialog.OK)
			return;
		if (!itemName.equals(dialog.getItemName()))
			model.removeSharedContent(itemName);
		model.putSharedContent(dialog.getItemName(), dialog.getContentItem());
		sharedContentTable.refresh();
	}

	private void removeSharedContent() {
		IStructuredSelection selection = (IStructuredSelection) sharedContentTable
				.getSelection();
		if (selection.isEmpty())
			return;
		else if (selection.size() == 1) {
			if (!MessageDialog.openConfirm(sharedContentTable.getControl()
					.getShell(), "Remove Item",
					"Are you sure you want to remove the item \""
							+ selection.getFirstElement()
							+ "\" and its content?"))
				return;
		} else {
			if (!MessageDialog.openConfirm(sharedContentTable.getControl()
					.getShell(), "Remove Item",
					"Are you sure you want to remove the " + selection.size()
							+ " selected items and their content?"))
				return;
		}
		for (@SuppressWarnings("rawtypes")
		Iterator i = selection.iterator(); i.hasNext();)
			model.removeSharedContent((String) i.next());
		sharedContentTable.refresh();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object inputElement) {
		return model.getSharedContentNames();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.BaseLabelProvider#dispose()
	 */
	public void dispose() {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
	 */
	public String getColumnText(Object element, int columnIndex) {
		if (columnIndex == 0)
			return (String)element;
		else if (columnIndex == 1) {

			StringBuffer buf = new StringBuffer();
			Content content = model.getSharedContent((String) element);
			if (content instanceof FormattableContent) {
				FormattableContent fc = (FormattableContent) content;
				buf.append(fc.getContentTypeName() + "(" + fc.getFormatName()
						+ ", " + fc.getValue() + ")");
			} else if (content instanceof TextContent) {
				buf.append(((TextContent) content).getText());
			} else if (content instanceof ReferencedContent) {
				buf.append("REFERENCE("
						+ ((ReferencedContent) content).getReferencedName()
						+ ")");
			} else if (content instanceof FileContent) {
				FileContent fc = (FileContent) content;
				buf.append(fc.getFileTypeName() + "(" + fc.getPath() + ")");
			} else if (content instanceof ContentComposite) {
				buf.append("COMPOSITE");
			}
			return buf.toString();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
	 */
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IDoubleClickListener#doubleClick(org.eclipse.jface.viewers.DoubleClickEvent)
	 */
	public void doubleClick(DoubleClickEvent event) {
		editSharedContent();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
	 */
	public void selectionChanged(SelectionChangedEvent event) {
		int size = 0;
		IStructuredSelection selection = (IStructuredSelection) sharedContentTable
				.getSelection();
		if (selection != null)
			size = selection.size();
		editSharedContentButton.setEnabled(size == 1);
		removeSharedContentButton.setEnabled(size > 0);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	public void widgetSelected(SelectionEvent e) {
		Object source = e.getSource();
		if (addSharedContentButton == source)
			addSharedContent();
		else if (editSharedContentButton == source)
			editSharedContent();
		else if (removeSharedContentButton == source)
			removeSharedContent();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	public void widgetDefaultSelected(SelectionEvent e) {
	}

}
