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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
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
import org.eclipse.jface.window.IShellProvider;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.vtp.desktop.model.core.design.Variable;
import org.eclipse.vtp.framework.interactions.core.media.Content;
import org.eclipse.vtp.framework.interactions.core.media.FileContent;
import org.eclipse.vtp.framework.interactions.core.media.FormattableContent;
import org.eclipse.vtp.framework.interactions.core.media.IMediaProvider;
import org.eclipse.vtp.framework.interactions.core.media.ReferencedContent;
import org.eclipse.vtp.framework.interactions.core.media.TextContent;

/**
 * A dialog for editing a list of content objects.
 * 
 * @author Lonnie Pryor
 */
public abstract class ContentDialog extends Dialog {
	/** The list of configured content objects. */
	protected final List<Content> contents = new ArrayList<Content>();
	/** The media provider the list is bound to. */
	private IMediaProvider mediaProvider = null;
	/** The table viewer the content list is displayed in. */
	private TableViewer viewer = null;

	/**
	 * Creates a new <code>ContentDialog</code>.
	 * 
	 * @param parentShell
	 *            The shell to create the dialog from.
	 */
	public ContentDialog(Shell parentShell) {
		super(parentShell);
	}

	/**
	 * Creates a new <code>ContentDialog</code>.
	 * 
	 * @param parentShellProvider
	 *            The shell provider to create the dialog from.
	 */
	public ContentDialog(IShellProvider parentShellProvider) {
		super(parentShellProvider);
	}

	/**
	 * Sets the media provider the list is bound to.
	 * 
	 * @param mediaProvider
	 *            The media provider the list is bound to.
	 */
	public void setMediaProvider(IMediaProvider mediaProvider) {
		this.mediaProvider = mediaProvider;
	}

	/**
	 * Called when the content of the dialog has changed.
	 */
	protected void contentChanged() {
		getButton(OK).setEnabled(isContentValid());
	}

	/**
	 * Returns true if the content of this dialog is valid and the OK button
	 * should be enabled.
	 * 
	 * @return True if the content of this dialog is valid and the OK button
	 *         should be enabled.
	 */
	protected boolean isContentValid() {
		return true;
	}
	
	/**
	 * @return
	 */
	protected List<Variable> getVariables() {
		return Collections.emptyList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#createContents(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createContents(Composite parent) {
		Control createdContents = super.createContents(parent);
		contentChanged();
		return createdContents;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(
	 *      org.eclipse.swt.widgets.Composite)
	 */
	protected Control createDialogArea(Composite parent) {
		this.getShell().setText("Contents");
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new GridLayout(2, false));
		comp.setLayoutData(new GridData());
		Table table = new Table(comp, SWT.FULL_SELECTION | SWT.V_SCROLL
				| SWT.BORDER | SWT.SINGLE);
		table.setHeaderVisible(true);
		GridData gd = new GridData();
		gd.widthHint = 255;
		gd.heightHint = 200;
		table.setLayoutData(gd);
		TableColumn entryColumn = new TableColumn(table, SWT.NONE);
		entryColumn.setText("Entries");
		entryColumn.setWidth(250);
		viewer = new TableViewer(table);
		viewer.setContentProvider(new PromptBindingContentProvider());
		viewer.setLabelProvider(new PromptBindingLabelProvider());
		viewer.setInput(this);
		viewer.addDoubleClickListener(new IDoubleClickListener()
		{
			public void doubleClick(DoubleClickEvent event)
			{
				if(!event.getSelection().isEmpty())
				{
					try {
						ContentEntryDialog pbed = new ContentEntryDialog(getShell());
						pbed.setMediaProvider(mediaProvider);
						pbed.setVariables(getVariables());
						IStructuredSelection selection = (IStructuredSelection)event.getSelection();
						pbed.setContent((Content) selection.getFirstElement());
						int result = pbed.open();
						if (result == ContentEntryDialog.OK) {
							Content c = pbed.getContent();
							int idex = contents
									.indexOf(selection.getFirstElement());
							contents.remove(idex);
							contents.add(idex, c);
							viewer.refresh();
							contentChanged();
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		});
		viewer.getControl().addKeyListener(new KeyListener()
		{
			public void keyPressed(KeyEvent e)
			{
			}

			public void keyReleased(KeyEvent e)
			{
				if(e.keyCode == SWT.DEL || e.keyCode == SWT.BS)
				{
					MessageBox confirmationDialog =
						new MessageBox(Display.getCurrent().getActiveShell(),
							SWT.YES | SWT.NO | SWT.ICON_WARNING);
					confirmationDialog.setMessage(
						"Are you sure you want to delete the selected item?");
		
					int result = confirmationDialog.open();
		
					if(result == SWT.YES)
					{
						try
						{
							IStructuredSelection selection = (IStructuredSelection) viewer
							.getSelection();
							contents.remove(selection.getFirstElement());
							viewer.refresh();
							contentChanged();
						}
						catch (Exception ex)
						{
							ex.printStackTrace();
						}
					}
				}
			}
		});
		Composite buttonComp = new Composite(comp, SWT.NONE);
		buttonComp.setLayout(new GridLayout());
		gd = new GridData();
		gd.verticalIndent = 20;
		gd.verticalAlignment = SWT.TOP;
		buttonComp.setLayoutData(gd);
		Button addButton = new Button(buttonComp, SWT.PUSH);
		addButton.setText("Add Entry");
		addButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		addButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				try {
					ContentEntryDialog pbed = new ContentEntryDialog(getShell());
					pbed.setMediaProvider(mediaProvider);
					pbed.setVariables(getVariables());
					int result = pbed.open();
					if (result == ContentEntryDialog.OK) {
						Content c = pbed.getContent();
						contents.add(c);
						viewer.refresh();
						contentChanged();
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
		final Button editButton = new Button(buttonComp, SWT.PUSH);
		editButton.setText("Edit Entry");
		editButton.setEnabled(false);
		editButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		editButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				try {
					ContentEntryDialog pbed = new ContentEntryDialog(getShell());
					pbed.setMediaProvider(mediaProvider);
					pbed.setVariables(getVariables());
					IStructuredSelection selection = (IStructuredSelection) viewer
							.getSelection();
					pbed.setContent((Content) selection.getFirstElement());
					int result = pbed.open();
					if (result == ContentEntryDialog.OK) {
						Content c = pbed.getContent();
						int idex = contents
								.indexOf(selection.getFirstElement());
						contents.remove(idex);
						contents.add(idex, c);
						viewer.refresh();
						contentChanged();
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
		final Button removeButton = new Button(buttonComp, SWT.PUSH);
		removeButton.setText("Remove Entry");
		removeButton.setEnabled(false);
		removeButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		removeButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				MessageBox confirmationDialog =
					new MessageBox(Display.getCurrent().getActiveShell(),
						SWT.YES | SWT.NO | SWT.ICON_WARNING);
				confirmationDialog.setMessage(
					"Are you sure you want to delete the selected item?");
	
				int result = confirmationDialog.open();
	
				if(result == SWT.YES)
				{
					try
					{
						IStructuredSelection selection = (IStructuredSelection) viewer
								.getSelection();
						contents.remove(selection.getFirstElement());
						viewer.refresh();
						contentChanged();
					}
					catch (Exception ex)
					{
						ex.printStackTrace();
					}
				}
			}
		});

		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event
						.getSelection();
				editButton.setEnabled(!selection.isEmpty());
				removeButton.setEnabled(!selection.isEmpty());
			}
		});
		return comp;
	}

	private class PromptBindingContentProvider implements
			IStructuredContentProvider {

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
		 */
		public Object[] getElements(Object inputElement) {
			return contents.toArray();
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
		 */
		public void dispose() {
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
		 */
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

	}

	private class PromptBindingLabelProvider extends LabelProvider implements
			ITableLabelProvider {

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
		 */
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
		 */
		public String getColumnText(Object element, int columnIndex) {
			StringBuffer buf = new StringBuffer();
			Content content = (Content) element;
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
			}
			return buf.toString();
		}

	}
}