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

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.vtp.framework.interactions.core.media.IResourceManager;

/**
 * MediaFileChooserDialog.
 * 
 * @author lonnie
 */
public class ResourceChooserDialog extends Dialog {
	private IResourceManager resourceManager = null;
	/** Comment for value. */
	private String value = null;

	/** Comment for viewer. */
	private TreeViewer viewer;

	/**
	 * Creates a new MediaFileChooserDialog.
	 * 
	 * @param parentShell
	 */
	public ResourceChooserDialog(Shell parentShell,
			IResourceManager resourceManager, String initialValue) {
		super(parentShell);
		super.setShellStyle(super.getShellStyle() | SWT.RESIZE);
		this.resourceManager = resourceManager;
		this.value = initialValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.window.Window#createContents(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createContents(Composite parent) {
		Control control = super.createContents(parent);
		getShell().setText("Select a File");
		getShell().pack();
		getShell().setSize(250, 300);

		return control;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		viewer = new TreeViewer(composite, SWT.SINGLE | SWT.H_SCROLL
				| SWT.V_SCROLL);
		viewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.setContentProvider(new FilesContentProvider());
		viewer.setLabelProvider(new FilesLabelProvider());
		viewer.setSorter(new FilesSorter());
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event
						.getSelection();

				if ((selection == null) || selection.isEmpty()) {
					ResourceChooserDialog.this.getButton(OK).setEnabled(false);
				} else {
					ResourceChooserDialog.this.getButton(OK).setEnabled(
							!selection.getFirstElement().toString().endsWith(
									"/"));
				}
			}
		});
		viewer.setInput(resourceManager);
		viewer.setAutoExpandLevel(1);

		return composite;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	protected void okPressed() {
		value = ((IStructuredSelection)viewer.getSelection()).getFirstElement().toString();
		super.okPressed();
	}

	/**
	 * Returns the value.
	 * 
	 * @return The value.
	 */
	public String getValue() {
		return value;
	}

	/**
	 * FilesContentProvider.
	 * 
	 * @author Lonnie Pryor
	 */
	private final class FilesContentProvider implements
			IStructuredContentProvider, ITreeContentProvider {
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer,
		 *      java.lang.Object, java.lang.Object)
		 */
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
		 */
		public Object[] getElements(Object parent) {
			return getChildren(parent);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
		 */
		public Object getParent(Object child) {
			if (child == resourceManager)
				return null;
			String str = child.toString();
			while (str.endsWith("/"))
				str = str.substring(0, str.length() - 1);
			if (str.lastIndexOf('/') < 0)
				return resourceManager;
			return str.substring(0, str.lastIndexOf('/') + 1);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
		 */
		public Object[] getChildren(Object parent) {
			if (parent == resourceManager)
				parent = "";
			String[] children = resourceManager
					.listResources(parent.toString());
			if (children == null)
				return new Object[0];
			Object[] results = new Object[children.length];
			for (int i = 0; i < results.length; i++)
				results[i] = parent + children[i]; 
			return results;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
		 */
		public boolean hasChildren(Object parent) {
			return parent == resourceManager || parent.toString().endsWith("/");
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
		 */
		public void dispose() {
		}
	}

	/**
	 * FilesLabelProvider.
	 * 
	 * @author Lonnie Pryor
	 */
	private final class FilesLabelProvider extends LabelProvider {
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
		 */
		public String getText(Object obj) {
			if (obj == resourceManager)
				return "Media Files";
			String str = obj.toString();
			while (str.endsWith("/"))
				str = str.substring(0, str.length() - 1);
			return str.substring(str.lastIndexOf('/') + 1);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
		 */
		public Image getImage(Object obj) {
			if (obj == resourceManager || obj.toString().endsWith("/"))
				return PlatformUI.getWorkbench().getSharedImages().getImage(
						ISharedImages.IMG_OBJ_FOLDER);
			return PlatformUI.getWorkbench().getSharedImages().getImage(
					ISharedImages.IMG_OBJ_FILE);
		}
	}

	/**
	 * FilesSorter.
	 * 
	 * @author Lonnie Pryor
	 */
	private final class FilesSorter extends ViewerSorter {
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.ViewerSorter#category(java.lang.Object)
		 */
		public int category(Object element) {
			if (element == resourceManager || element.toString().endsWith("/"))
				return 1;
			return 2;
		}
	}
}
