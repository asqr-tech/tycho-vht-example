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

import java.util.Collections;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
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
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.vtp.desktop.model.core.design.Variable;
import org.eclipse.vtp.desktop.model.interactive.core.configuration.generic.PromptBindingCase;
import org.eclipse.vtp.desktop.model.interactive.core.configuration.generic.PromptBindingEntry;
import org.eclipse.vtp.desktop.model.interactive.core.configuration.generic.PromptBindingItem;
import org.eclipse.vtp.desktop.model.interactive.core.configuration.generic.PromptBindingNode;
import org.eclipse.vtp.desktop.model.interactive.core.configuration.generic.PromptBindingSwitch;
import org.eclipse.vtp.framework.interactions.core.media.Content;
import org.eclipse.vtp.framework.interactions.core.media.FileContent;
import org.eclipse.vtp.framework.interactions.core.media.FormattableContent;
import org.eclipse.vtp.framework.interactions.core.media.IMediaProvider;
import org.eclipse.vtp.framework.interactions.core.media.PlaceholderContent;
import org.eclipse.vtp.framework.interactions.core.media.ReferencedContent;
import org.eclipse.vtp.framework.interactions.core.media.TextContent;

/**
 * A dialog for editing a list of content objects.
 * 
 * @author Lonnie Pryor
 */
public abstract class ContentTreeDialog extends Dialog {
	/** The entry that will be worked with. */
	private PromptBindingItem treeContent = null;
	/** The media provider the list is bound to. */
	private IMediaProvider mediaProvider = null;
	/** The table viewer the content list is displayed in. */
	private TreeViewer viewer = null;
	List<ContentPlaceholder> placeholders;

	/**
	 * Creates a new <code>ContentDialog</code>.
	 * 
	 * @param parentShell
	 *            The shell to create the dialog from.
	 */
	public ContentTreeDialog(Shell parentShell) {
		super(parentShell);
	}

	/**
	 * Creates a new <code>ContentDialog</code>.
	 * 
	 * @param parentShellProvider
	 *            The shell provider to create the dialog from.
	 */
	public ContentTreeDialog(IShellProvider parentShellProvider) {
		super(parentShellProvider);
	}

	/** Returns the entry that will be worked with. */
	protected PromptBindingItem getTreeContent() {
		return treeContent;
	}

	/** Sets the entry that will be worked with. */
	protected void setTreeContent(PromptBindingItem treeContent) {
		this.treeContent = treeContent;
		if (viewer != null)
			viewer.setInput(treeContent);
	}

	public void setPlaceholders(List<ContentPlaceholder> placeholders)
	{
		this.placeholders = placeholders;
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

	private void addEntry(Object parent) {
		ContentEntryDialog pbed = new ContentEntryDialog(getShell());
		pbed.setMediaProvider(mediaProvider);
		pbed.setVariables(getVariables());
		pbed.setPlaceholders(placeholders);
		int result = pbed.open();
		if (result == ContentEntryDialog.OK) {
			Content c = pbed.getContent();
			if (parent instanceof PromptBindingCase)
				((PromptBindingCase) parent)
						.addChild(new PromptBindingEntry(c));
			else if (parent instanceof PromptBindingItem)
				((PromptBindingItem) parent)
						.addEntry(new PromptBindingEntry(c));
			viewer.refresh();
			contentChanged();
		}
	}

	private void addSwitch(Object parent) {
		if (parent instanceof PromptBindingCase)
			((PromptBindingCase) parent).addChild(new PromptBindingSwitch());
		else if (parent instanceof PromptBindingItem)
			((PromptBindingItem) parent).addEntry(new PromptBindingSwitch());
		viewer.refresh();
		contentChanged();
	}

	private void addCase(Object parent) {
		if (parent instanceof PromptBindingSwitch) {
			ScriptDialog sd = new ScriptDialog(getShell());
			if (sd.open() == ScriptDialog.OK) {
				((PromptBindingSwitch) parent).addChild(new PromptBindingCase(
						sd.getScript()));
				viewer.refresh();
				contentChanged();
			}
		}
	}

	private void editNode(PromptBindingNode target) {
		if (target instanceof PromptBindingCase) {
			ScriptDialog sd = new ScriptDialog(getShell());
			sd.setScript(((PromptBindingCase)target).getCondition());
			if (sd.open() == ScriptDialog.OK) {
				((PromptBindingCase)target).setCondition(sd.getScript());
				viewer.refresh();
				contentChanged();
			}
		} else {
			PromptBindingEntry node = (PromptBindingEntry) target;
			ContentEntryDialog pbed = new ContentEntryDialog(getShell());
			pbed.setMediaProvider(mediaProvider);
			pbed.setVariables(getVariables());
			pbed.setPlaceholders(placeholders);
			pbed.setContent(node.getContent());
			int result = pbed.open();
			if (result == ContentEntryDialog.OK) {
				node.setContent(pbed.getContent());
				viewer.refresh();
				contentChanged();
			}
		}
	}

	private void removeNode(PromptBindingNode target) {
		PromptBindingNode parent = target.getParent();
		if (parent == null)
			treeContent.getEntries().remove(target);
		else if ((parent instanceof PromptBindingSwitch))
			((PromptBindingSwitch) parent).removeChild(target);
		else if ((parent instanceof PromptBindingCase))
			((PromptBindingCase) parent).removeChild(target);
		viewer.refresh();
		contentChanged();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.dialogs.Dialog#createContents(org.eclipse.swt.widgets
	 * .Composite)
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
	 * org.eclipse.swt.widgets.Composite)
	 */
	protected Control createDialogArea(Composite parent) {
		this.getShell().setText("Contents");
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new GridLayout(2, false));
		comp.setLayoutData(new GridData());
		Tree tree = new Tree(comp, SWT.FULL_SELECTION | SWT.V_SCROLL
				| SWT.SINGLE);
		tree.setHeaderVisible(true);
		GridData gd = new GridData();
		gd.widthHint = 255;
		gd.heightHint = 200;
		tree.setLayoutData(gd);
		TreeColumn entryColumn = new TreeColumn(tree, SWT.NONE);
		entryColumn.setText("Entries");
		entryColumn.setWidth(250);
		viewer = new TreeViewer(tree);
		viewer.setContentProvider(new PromptBindingContentProvider());
		viewer.setLabelProvider(new PromptBindingLabelProvider());
		if (treeContent != null)
			viewer.setInput(treeContent);
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				if (!event.getSelection().isEmpty()) {
					try {
						IStructuredSelection selection = (IStructuredSelection) event
								.getSelection();
						editNode((PromptBindingNode) selection
								.getFirstElement());
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		});
		viewer.getControl().addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
			}

			public void keyReleased(KeyEvent e) {
				if (e.keyCode == SWT.DEL || e.keyCode == SWT.BS) {
					MessageBox confirmationDialog = new MessageBox(Display
							.getCurrent().getActiveShell(), SWT.YES | SWT.NO
							| SWT.ICON_WARNING);
					confirmationDialog
							.setMessage("Are you sure you want to delete the selected item?");
					int result = confirmationDialog.open();
					if (result == SWT.YES) {
						try {
							IStructuredSelection selection = (IStructuredSelection) viewer
									.getSelection();
							removeNode((PromptBindingNode) selection
									.getFirstElement());
						} catch (Exception ex) {
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
		final Button addEntryButton = new Button(buttonComp, SWT.PUSH);
		addEntryButton.setText("Add Entry");
		addEntryButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		addEntryButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				try {
					IStructuredSelection selection = (IStructuredSelection) viewer
							.getSelection();
					Object selected = selection.getFirstElement();
					if (selected == null)
						addEntry(treeContent);
					else if (selected instanceof PromptBindingEntry)
						addEntry(parentOf(selected));
					else if (selected instanceof PromptBindingSwitch)
						addEntry(parentOf(selected));
					else
						addEntry(selected);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
		final Button addChoicesButton = new Button(buttonComp, SWT.PUSH);
		addChoicesButton.setText("Add Choices");
		addChoicesButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		addChoicesButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				try {
					IStructuredSelection selection = (IStructuredSelection) viewer
							.getSelection();
					Object selected = selection.getFirstElement();
					if (selected == null)
						addSwitch(treeContent);
					else if (selected instanceof PromptBindingEntry)
						addSwitch(parentOf(selected));
					else if (selected instanceof PromptBindingSwitch)
						addSwitch(parentOf(selected));
					else
						addSwitch(selected);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
		final Button addChoiceButton = new Button(buttonComp, SWT.PUSH);
		addChoiceButton.setText("Add Choice");
		addChoiceButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		addChoiceButton.setEnabled(false);
		addChoiceButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				try {
					IStructuredSelection selection = (IStructuredSelection) viewer
							.getSelection();
					Object selected = selection.getFirstElement();
					if (selected instanceof PromptBindingCase)
						addCase(parentOf(selected));
					else if (selected instanceof PromptBindingSwitch)
						addCase(selected);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
		final Button editButton = new Button(buttonComp, SWT.PUSH);
		editButton.setText("Edit");
		editButton.setEnabled(false);
		editButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		editButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				try {
					IStructuredSelection selection = (IStructuredSelection) viewer
							.getSelection();
					editNode((PromptBindingNode) selection.getFirstElement());
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
		final Button removeButton = new Button(buttonComp, SWT.PUSH);
		removeButton.setText("Remove");
		removeButton.setEnabled(false);
		removeButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		removeButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				MessageBox confirmationDialog = new MessageBox(Display
						.getCurrent().getActiveShell(), SWT.YES | SWT.NO
						| SWT.ICON_WARNING);
				confirmationDialog
						.setMessage("Are you sure you want to delete the selected item?");

				int result = confirmationDialog.open();

				if (result == SWT.YES) {
					try {
						IStructuredSelection selection = (IStructuredSelection) viewer
								.getSelection();
						removeNode((PromptBindingNode) selection
								.getFirstElement());
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		});

		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event
						.getSelection();
				addChoiceButton.setEnabled((selection.getFirstElement() instanceof PromptBindingSwitch)
						|| (selection.getFirstElement() instanceof PromptBindingCase));
				editButton.setEnabled(!(selection.getFirstElement() instanceof PromptBindingSwitch));
				removeButton.setEnabled(!selection.isEmpty());
			}
		});
		return comp;
	}

	private Object parentOf(Object element) {
		if (element instanceof PromptBindingNode) {
			PromptBindingNode parent = ((PromptBindingNode) element)
					.getParent();
			return parent == null ? treeContent : parent;
		}
		return null;
	}

	private class PromptBindingContentProvider implements ITreeContentProvider {

		@Override
		public Object getParent(Object element) {
			return parentOf(element);
		}

		@Override
		public boolean hasChildren(Object element) {
			return (element instanceof PromptBindingItem)
					|| (element instanceof PromptBindingSwitch)
					|| (element instanceof PromptBindingCase);
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			if (parentElement instanceof PromptBindingItem)
				return ((PromptBindingItem) parentElement).getEntries()
						.toArray();
			if (parentElement instanceof PromptBindingSwitch)
				return ((PromptBindingSwitch) parentElement).getChildren()
						.toArray();
			if (parentElement instanceof PromptBindingCase)
				return ((PromptBindingCase) parentElement).getChildren()
						.toArray();
			return new Object[0];
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.IStructuredContentProvider#getElements(
		 * java.lang.Object)
		 */
		public Object[] getElements(Object inputElement) {
			return getChildren(inputElement);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
		 */
		public void dispose() {
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse
		 * .jface.viewers.Viewer, java.lang.Object, java.lang.Object)
		 */
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

	}

	private class PromptBindingLabelProvider extends LabelProvider implements
			ITableLabelProvider {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java
		 * .lang.Object, int)
		 */
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.
		 * lang.Object, int)
		 */
		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof PromptBindingSwitch)
				return "Choices";
			else if (element instanceof PromptBindingCase)
				return ((PromptBindingCase) element).getCondition();
			else {
				StringBuffer buf = new StringBuffer();
				Content content = ((PromptBindingEntry) element).getContent();
				if (content instanceof FormattableContent) {
					FormattableContent fc = (FormattableContent) content;
					buf.append(fc.getContentTypeName() + "("
							+ fc.getFormatName() + ", " + fc.getValue() + ")");
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
				else if(content instanceof PlaceholderContent)
				{
					buf.append("Placeholder(");
					buf.append(((PlaceholderContent)content).getPlaceholder());
					buf.append(")");
				}
				return buf.toString();
			}
		}

	}
}