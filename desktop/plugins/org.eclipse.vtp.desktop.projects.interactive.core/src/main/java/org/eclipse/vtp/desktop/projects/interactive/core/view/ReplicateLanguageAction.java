/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.vtp.desktop.projects.interactive.core.view;

import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.SelectionListenerAction;
import org.eclipse.vtp.desktop.model.core.IWorkflowResource;
import org.eclipse.vtp.desktop.model.core.WorkflowCore;
import org.eclipse.vtp.desktop.model.interactive.core.IInteractiveWorkflowProject;
import org.eclipse.vtp.desktop.projects.interactive.core.dialogs.ReplicateLanguageDialog;

/**
 * Standard action for copying the currently selected resources to the clipboard.
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 * 
 * @since 2.0
 */
public class ReplicateLanguageAction extends SelectionListenerAction {

    /**
     * The id of this action.
     */
    public static final String ID = "OpenVXML.ReplicateLanguageAction"; //$NON-NLS-1$

    /**
     * The shell in which to show any dialogs.
     */
    private Shell shell;

    /**
     * Creates a new action.
     *
     * @param shell the shell for any dialogs
     * @param clipboard a platform clipboard
     */
    public ReplicateLanguageAction(Shell shell) {
        super("Replication Language"); 
        Assert.isNotNull(shell);
        this.shell = shell;
        setToolTipText("Replicate media configuration to another language"); 
        setId(ReplicateLanguageAction.ID);
    }

    /**
     * The <code>CopyAction</code> implementation of this method defined 
     * on <code>IAction</code> copies the selected resources to the 
     * clipboard.
     */
	public void run() {
    	if(PlatformUI.getWorkbench().saveAllEditors(true))
    	{
	        @SuppressWarnings("unchecked")
			List<IResource> selectedResources = getSelectedResources();
	        IWorkflowResource wr = WorkflowCore.getDefault().getWorkflowModel().convertToWorkflowResource(selectedResources.get(0));
	        if(wr != null && wr instanceof IInteractiveWorkflowProject)
	        {
	        	IInteractiveWorkflowProject project = (IInteractiveWorkflowProject)wr;
	        	List<String> languages = project.getSupportedLanguages("org.eclipse.vtp.framework.interactions.voice.interaction");
	        	ReplicateLanguageDialog rld = new ReplicateLanguageDialog(shell);
	        	rld.setLanguage(languages);
	        	rld.setProject(project);
	        	rld.open();
	        }
    	}
    }

    /**
     * The <code>CopyAction</code> implementation of this
     * <code>SelectionListenerAction</code> method enables this action if 
     * one or more resources of compatible types are selected.
     */
    protected boolean updateSelection(IStructuredSelection selection) {
        if (!super.updateSelection(selection)) {
			return false;
		}

        if (getSelectedNonResources().size() > 0) {
			return false;
		}

        @SuppressWarnings("unchecked")
		List<IResource> selectedResources = getSelectedResources();
        if (selectedResources.size() != 1) {
			return false;
		}

        return selectionIsOfType(IResource.PROJECT);
    }

}
