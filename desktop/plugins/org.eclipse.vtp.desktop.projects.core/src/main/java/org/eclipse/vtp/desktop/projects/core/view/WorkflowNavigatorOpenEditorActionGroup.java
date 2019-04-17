/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Anton Leherbauer (Wind River Systems) - adaptations for Common Navigator
 *******************************************************************************/

package org.eclipse.vtp.desktop.projects.core.view;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.actions.OpenFileAction;
import org.eclipse.ui.actions.OpenWithMenu;
import org.eclipse.ui.navigator.ICommonMenuConstants;


/**
 * This is basically a clone of {@link org.eclipse.cdt.internal.ui.cview.OpenFileGroup},
 * but without explicit dependency on CView. This opens it up for use in the
 * Common Navigator framework.
 * <p>
 * Contributes an "Open" action for the default editor, an "Open With" sub-menu
 * for all applicable editors if one or more files are selected.
 * For all container selections, an "Open In New Window" action is contributed.
 * </p>
 * 
 * @see org.eclipse.cdt.internal.ui.cview.OpenFileGroup
 * @see org.eclipse.ui.actions.OpenFileAction
 * @see org.eclipse.ui.actions.OpenWithMenu
 * @see org.eclipse.ui.actions.OpenInNewWindowAction
 */
public class WorkflowNavigatorOpenEditorActionGroup extends AbstractWorkflowNavigatorActionGroup {

	/** The open file action. */
	private OpenFileAction fOpenFileAction;

	/**
	 * Create an action group for the given view part.
	 * 
	 * @param viewPart
	 */
	public WorkflowNavigatorOpenEditorActionGroup(IViewPart viewPart) {
		super(viewPart);
	}

	@Override
	protected void makeActions() {
		fOpenFileAction= new OpenFileAction(getViewPart().getSite().getPage());
	}

	@Override
	public void fillContextMenu(IMenuManager menu) {
        IStructuredSelection elements= (IStructuredSelection) getContext().getSelection();
		List<Object> selectedList = new LinkedList<Object>();
		for(Object obj : elements.toList())
		{
			if(obj instanceof IResource)
			{
				selectedList.add(obj);
				continue;
			}
			else if(obj instanceof IAdaptable)
			{
				IResource adapted = (IResource)((IAdaptable)obj).getAdapter(IResource.class);
				if(adapted != null)
				{
					selectedList.add(adapted);
					continue;
				}
			}
			selectedList.add(obj);
		}
		elements = new StructuredSelection(selectedList.toArray());

		fOpenFileAction.selectionChanged(elements);
		if (fOpenFileAction.isEnabled()) {
			menu.appendToGroup(ICommonMenuConstants.GROUP_OPEN, fOpenFileAction);
			fillOpenWithMenu(menu, elements);
		}

//		addNewWindowAction(menu, elements);
	}

	/**
	 * Adds the OpenWith submenu to the context menu.
	 * 
	 * @param menu
	 *            the context menu
	 * @param selection
	 *            the current selection
	 */
	private void fillOpenWithMenu(IMenuManager menu, IStructuredSelection selection) {
		// Only supported if exactly one file is selected.
		if (selection.size() != 1) {
			return;
		}
		Object element= selection.getFirstElement();
		if (!(element instanceof IFile)) {
			return;
		}

		MenuManager submenu= new MenuManager("Open Wit&h", ICommonMenuConstants.GROUP_OPEN_WITH); 
		submenu.add(new OpenWithMenu(getViewPart().getSite().getPage(), (IFile) element));
		menu.insertAfter(ICommonMenuConstants.GROUP_OPEN_WITH, submenu);
	}

	/**
	 * Adds the Open in New Window action to the context menu.
	 * 
	 * @param menu
	 *            the context menu
	 * @param selection
	 *            the current selection
	 */
/*	private void addNewWindowAction(IMenuManager menu, IStructuredSelection selection) {

		// Only supported if exactly one container (i.e open project or folder) is selected.
		if (selection.size() != 1) {
			return;
		}
		Object element= selection.getFirstElement();
		if (element instanceof ICElement) {
			element= ((ICElement)element).getResource();
		}
		if (!(element instanceof IContainer)) {
			return;
		}
		if (element instanceof IProject && !(((IProject) element).isOpen())) {
			return;
		}

		menu.appendToGroup(ICommonMenuConstants.GROUP_OPEN, new OpenInNewWindowAction(getViewPart().getSite().getWorkbenchWindow(), (IContainer) element));
	}
*/
    /*
     * @see org.eclipse.ui.actions.ActionGroup#fillActionBars(org.eclipse.ui.IActionBars)
     */
    @Override
	public void fillActionBars(IActionBars actionBars) {
    }

    /*
     * @see org.eclipse.ui.actions.ActionGroup#updateActionBars()
     */
    @Override
	public void updateActionBars() {
        IStructuredSelection celements= (IStructuredSelection) getContext().getSelection();
		fOpenFileAction.selectionChanged(celements);
    }

	/**
	 * Returns the open action managed by this action group. 
	 *
	 * @return the open action
	 */
	IAction getOpenAction() {
		return fOpenFileAction;
	}

}
