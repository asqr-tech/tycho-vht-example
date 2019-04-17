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
package org.eclipse.vtp.desktop.model.elements.core.internal.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.vtp.desktop.model.core.IWorkflowProject;
import org.eclipse.vtp.desktop.model.core.WorkflowCore;
import org.eclipse.vtp.desktop.model.core.internal.design.Design;
import org.eclipse.vtp.desktop.model.elements.core.internal.ApplicationFragmentElementFactory;
import org.eclipse.vtp.desktop.views.pallet.PalletItem;
import org.eclipse.vtp.desktop.views.pallet.PalletItemObserver;
import org.eclipse.vtp.desktop.views.pallet.PalletItemProvider;

public class ApplicationFragmentPalletProvider implements PalletItemProvider, IResourceChangeListener
{
	List<PalletItem> primitiveItems = new ArrayList<PalletItem>();
	List<PalletItemObserver> observers = new ArrayList<PalletItemObserver>();

	public ApplicationFragmentPalletProvider()
	{
		super();
		loadProjects();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
	}
	
	private void loadProjects()
	{
		primitiveItems.clear();
		List<IWorkflowProject> projects = WorkflowCore.getDefault().getWorkflowModel().listWorkflowProjects();
		for (IWorkflowProject project : projects)
		{
			PalletItem item = new PalletItem(project.getName(), null,
				new ApplicationFragmentElementFactory(), project.getId())
			{
				@SuppressWarnings("unused")
				public boolean canBeContainedBy(Design design)
				{
					return design.equals(design.getDocument().getMainDesign());
				}
			};
			item.setPopOnDrop(true);
			primitiveItems.add(item);
		}
	}

	public String getName()
	{
		return "Workflows";
	}

	public List<PalletItem> getPalletItems()
	{
		return primitiveItems;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.desktop.views.pallet.PalletItemProvider#getRanking()
	 */
	public int getRanking()
	{
		return 30;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.desktop.views.pallet.PalletItemProvider#createMenu(
	 *      org.eclipse.jface.action.IMenuManager,
	 *      org.eclipse.vtp.desktop.views.pallet.PalletItem[])
	 */
	public void createMenu(final IAdaptable container, IMenuManager manager, PalletItem[] selectedItems)
	{
/*		manager.add(new Action("Create Application Fragment...")
		{
			public void run()
			{
				CreateApplicationFragmentWizard wizard = new CreateApplicationFragmentWizard();
				wizard.init(null, new StructuredSelection(container));
				WizardDialog dialog = new WizardDialog(Display.getCurrent()
						.getActiveShell(), wizard);
				dialog.open();
			}
		});
*/	}
	
	public void fireUpdate()
	{
		for(PalletItemObserver observer : observers)
        {
	        observer.palletItemsChanged();
        }
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.desktop.views.pallet.PalletItemProvider#
	 *      addPalletItemObserver(
	 *      org.eclipse.vtp.desktop.views.pallet.PalletItemObserver)
	 */
	public void addPalletItemObserver(PalletItemObserver observer)
	{
		observers.remove(observer);
		observers.add(observer);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.desktop.views.pallet.PalletItemProvider#
	 *      removePalletItemObserver(
	 *      org.eclipse.vtp.desktop.views.pallet.PalletItemObserver)
	 */
	public void removePalletItemObserver(PalletItemObserver observer)
	{
		observers.remove(observer);
	}

	public void resourceChanged(IResourceChangeEvent event)
    {
		final boolean[] bs = new boolean[] {false};

		try
		{
			if(event.getDelta() != null)
			{
				event.getDelta().accept(new IResourceDeltaVisitor()
					{
						public boolean visit(IResourceDelta delta)
							throws CoreException
						{
							if((delta.getKind() == IResourceDelta.ADDED) && 
								delta.getResource() instanceof IProject &&
								WorkflowCore.getDefault().getWorkflowModel().isWorkflowProject((IProject)delta.getResource()))
							{
								bs[0] = true;
							}
							else if((delta.getKind() == IResourceDelta.REMOVED) && 
								delta.getResource() instanceof IProject &&
								WorkflowCore.getDefault().getWorkflowModel().isWorkflowProject((IProject)delta.getResource()))
							{
								bs[0] = true;
							}
	
							return true;
						}
					});
			}
		}
		catch(CoreException e)
		{
			e.printStackTrace();
		}

		if(bs[0])
		{
			loadProjects();
			fireUpdate();
		}
    }

}
