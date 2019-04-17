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
package org.eclipse.vtp.desktop.projects.interactive.core.dialogs;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.vtp.desktop.model.core.IDesignDocument;
import org.eclipse.vtp.desktop.model.core.IDesignFolder;
import org.eclipse.vtp.desktop.model.core.IDesignItemContainer;
import org.eclipse.vtp.desktop.model.core.design.IDesign;
import org.eclipse.vtp.desktop.model.core.design.IDesignElement;
import org.eclipse.vtp.desktop.model.interactive.core.IInteractiveWorkflowProject;
import org.eclipse.vtp.desktop.model.interactive.core.configuration.generic.GenericBindingManager;
import org.eclipse.vtp.desktop.model.interactive.core.configuration.generic.InteractionBinding;
import org.eclipse.vtp.desktop.model.interactive.core.configuration.generic.NamedBinding;

public class ReplicateLanguageDialog extends Dialog
{
	List<String> languages = new ArrayList<String>();
	IInteractiveWorkflowProject project;
	Combo sourceCombo = null;
	Combo destinationCombo = null;
	Button okButton = null;

	/**
	 * @param parentShell
	 */
	public ReplicateLanguageDialog(Shell parentShell)
	{
		super(parentShell);
	}

	/**
	 * @param parentShell
	 */
	public ReplicateLanguageDialog(IShellProvider parentShell)
	{
		super(parentShell);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createDialogArea(Composite parent)
	{
		this.getShell().setText("Replicate Language Configuration");
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new GridLayout(3, false));
		Label sourceLabel = new Label(comp, SWT.NONE);
		sourceLabel.setText("Source");
		sourceLabel.setLayoutData(new GridData());
		Label iconLabel = new Label(comp, SWT.NONE);
		iconLabel.setText("->");
		GridData gd = new GridData();
		gd.verticalSpan = 2;
		gd.verticalAlignment = SWT.CENTER;
		iconLabel.setLayoutData(gd);
		
		Label destLabel = new Label(comp, SWT.NONE);
		destLabel.setText("Destination");
		destLabel.setLayoutData(new GridData());

		sourceCombo = new Combo(comp, SWT.DROP_DOWN | SWT.READ_ONLY);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		sourceCombo.setLayoutData(gd);
		for(String lang : languages)
		{
			sourceCombo.add(lang);
		}
		sourceCombo.select(0);
		
		destinationCombo = new Combo(comp, SWT.DROP_DOWN | SWT.READ_ONLY);
		destinationCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		destinationCombo.add("Select Destination");
		for(int i = 1; i < languages.size(); i++)
		{
			destinationCombo.add(languages.get(i));
		}
		destinationCombo.select(0);
		
		sourceCombo.addSelectionListener(new SelectionListener()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				if(sourceCombo.getSelectionIndex() == -1)
					sourceCombo.select(0);
				destinationCombo.removeAll();
				destinationCombo.add("Select Destination");
				for(int i = 0; i < sourceCombo.getSelectionIndex(); i++)
				{
					destinationCombo.add(languages.get(i));
				}
				for(int i = sourceCombo.getSelectionIndex() + 1; i < languages.size(); i++)
				{
					destinationCombo.add(languages.get(i));
				}
				destinationCombo.select(0);
				okButton.setEnabled(false);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{
			}
		});
		
		destinationCombo.addSelectionListener(new SelectionListener()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				okButton.setEnabled(destinationCombo.getSelectionIndex() > 0);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) 
			{
			}
		});

		return comp;
	}
	
	
	
	@Override
	protected Control createButtonBar(Composite parent)
	{
		Control ret = super.createButtonBar(parent);
		okButton = getButton(IDialogConstants.OK_ID);
		okButton.setEnabled(false);
		return ret;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	protected void okPressed()
	{
		final String sourceLanguage = sourceCombo.getItem(sourceCombo.getSelectionIndex());
		final String destinationLanguage = destinationCombo.getItem(destinationCombo.getSelectionIndex());
		super.okPressed();
		ProgressMonitorDialog pmd = new ProgressMonitorDialog(getShell());
		try {
			pmd.run(true, false, new IRunnableWithProgress()
			{
				@Override
				public void run(IProgressMonitor monitor)
						throws InvocationTargetException, InterruptedException
				{
					IDesignItemContainer root = project.getDesignRootFolder();
					int work = countContainer(root);
					monitor.beginTask("Replicating language configuration", work);
					processContainer(root, monitor);
					monitor.done();
					System.out.println("Done");
				}
				
				private void processContainer(IDesignItemContainer container, IProgressMonitor monitor)
				{
					List<IDesignFolder> folders = container.getDesignFolders();
					for(IDesignFolder folder : folders)
					{
						processContainer(folder, monitor);
					}
					List<IDesignDocument> docs = container.getDesignDocuments();
					for(IDesignDocument doc : docs)
					{
						monitor.setTaskName("Processing " + doc.getUnderlyingFile().getProjectRelativePath().toString());
						doc.becomeWorkingCopy();
						processDesign(doc.getMainDesign(), monitor);
						for(IDesign dialog : doc.getDialogDesigns())
						{
							processDesign(dialog, monitor);
						}
						try
						{
							doc.commitWorkingCopy();
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
					}
				}
				
				private void processDesign(IDesign design, IProgressMonitor monitor)
				{
					monitor.subTask(design.getName());
					List<IDesignElement> elements = design.getDesignElements();
					for(IDesignElement element : elements)
					{
						GenericBindingManager manager = (GenericBindingManager)element.getConfigurationManager(GenericBindingManager.TYPE_ID);
						InteractionBinding ib = manager.getInteractionBinding("org.eclipse.vtp.framework.interactions.voice.interaction");
						List<NamedBinding> nbs = ib.getNamedBindings();
						for(NamedBinding nb : nbs)
						{
							nb.duplicateLanguageBinding(sourceLanguage, destinationLanguage, true);
						}
						element.commitConfigurationChanges(manager);
					}
					monitor.worked(1);
				}
				
				private int countContainer(IDesignItemContainer container)
				{
					int ret = 0;
					List<IDesignFolder> folders = container.getDesignFolders();
					for(IDesignFolder folder : folders)
					{
						ret += countContainer(folder);
					}
					List<IDesignDocument> docs = container.getDesignDocuments();
					ret += docs.size();
					return ret;
				}
			});
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void setLanguage(List<String> languages)
	{
		this.languages = languages;
	}
	
	public void setProject(IInteractiveWorkflowProject project)
	{
		this.project = project;
	}
}
