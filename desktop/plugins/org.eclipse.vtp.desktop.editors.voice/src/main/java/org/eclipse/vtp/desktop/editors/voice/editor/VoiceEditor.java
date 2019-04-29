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
package org.eclipse.vtp.desktop.editors.voice.editor;

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.vtp.desktop.model.interactive.voice.internal.VoiceModel;

/**
 * An example showing how to create a multi-page editor. This example has 3
 * pages:
 * <ul>
 * <li>page 0 contains a nested text editor.
 * <li>page 1 allows you to change the font used in page 2
 * <li>page 2 shows the words in page 0 in sorted order
 * </ul>
 */
public class VoiceEditor extends MultiPageEditorPart implements Runnable,
		IResourceChangeListener {
	private SharedContentPage sharedContentPage = new SharedContentPage();
//	private IPromptSet promptSet = null;
	private VoiceModel model = null;
	private boolean dirty = false;

	/**
	 * Creates a multi-page editor example.
	 */
	public VoiceEditor() {
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.MultiPageEditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
	 */
	public void init(IEditorSite site, IEditorInput editorInput)
			throws PartInitException {
		if (!(editorInput instanceof IFileEditorInput))
			throw new PartInitException(
					"Invalid Input: Must be IFileEditorInput");
		super.init(site, editorInput);
		IFile descriptor = ((IFileEditorInput) editorInput).getFile();
//		promptSet = InteractiveWorkflowCore.getDefault().getInteractiveWorkflowModel().convertToMediaProject(descriptor.getProject()).getPromptSet();
		model = new VoiceModel(descriptor);
		try {
			model.load();
		} catch (IOException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
		model.addChangeObserver(this);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.MultiPageEditorPart#dispose()
	 */
	public void dispose() {
		if (model != null)
			model.removeChangeObserver(this);
		try {
			ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		} finally {
			super.dispose();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.MultiPageEditorPart#createPages()
	 */
	protected void createPages() {
		setPageText(
				addPage(sharedContentPage.initialize(getContainer(), model)),
				"Shared Content");
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.MultiPageEditorPart#isDirty()
	 */
	public boolean isDirty() {
		return dirty;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
	 */
	public boolean isSaveAsAllowed() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void doSave(IProgressMonitor monitor) {
		try {
			model.save(monitor);
		} catch (IOException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
		dirty = false;
		firePropertyChange(PROP_DIRTY);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#doSaveAs()
	 */
	public void doSaveAs() {
		throw new IllegalStateException("Save as is not supported.");
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		dirty = true;
		firePropertyChange(PROP_DIRTY);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
	 */
	public void resourceChanged(final IResourceChangeEvent event) {
		if (event.getType() == IResourceChangeEvent.PRE_CLOSE) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					IWorkbenchPage[] pages = getSite().getWorkbenchWindow()
							.getPages();
					for (int i = 0; i < pages.length; i++) {
						// if (((FileEditorInput) editor.getEditorInput())
						// .getFile().getProject().equals(
						// event.getResource())) {
						// IEditorPart editorPart = pages[i].findEditor(editor
						// .getEditorInput());
						// pages[i].closeEditor(editorPart, true);
						// }
					}
				}
			});
		}
	}
}
