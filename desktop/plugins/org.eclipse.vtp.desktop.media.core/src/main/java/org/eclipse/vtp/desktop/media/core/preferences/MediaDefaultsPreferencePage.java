/*--------------------------------------------------------------------------
 * Copyright (c) 2008 OpenMethods, LLC
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Trip Gilman (OpenMethods)
 *    - initial API and implementation
 -------------------------------------------------------------------------*/
package org.eclipse.vtp.desktop.media.core.preferences;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.vtp.desktop.core.Activator;
import org.eclipse.vtp.desktop.media.core.IMediaDefaultPanel;
import org.eclipse.vtp.desktop.media.core.MediaDefaultsPanelManager;
import org.eclipse.vtp.desktop.model.interactive.core.InteractionType;
import org.eclipse.vtp.desktop.model.interactive.core.InteractionTypeManager;
import org.eclipse.vtp.desktop.model.interactive.core.internal.mediadefaults.WorkspaceMediaDefaultSettings;
import org.eclipse.vtp.desktop.model.interactive.core.mediadefaults.IMediaDefaultSettings;


public class MediaDefaultsPreferencePage extends PreferencePage implements
        IWorkbenchPreferencePage
{
	IMediaDefaultSettings wmd = WorkspaceMediaDefaultSettings.getInstance();
	ScrolledComposite scrollComp = null;
	Composite stackComp = null;
	StackLayout stack = null;
	Combo interactionSelector = null;
	List<InteractionType> interactionTypes = null;
	List<Composite> interactionComposites = new ArrayList<Composite>();
	List<IMediaDefaultPanel> settingPanels = new ArrayList<IMediaDefaultPanel>();
	
	public MediaDefaultsPreferencePage()
	{
		super();
	}
	
	/**
	 * @param title
	 */
	public MediaDefaultsPreferencePage(String title)
	{
		super(title);
	}

	/**
	 * @param title
	 * @param image
	 */
	public MediaDefaultsPreferencePage(String title, ImageDescriptor image)
	{
		super(title, image);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createContents(Composite parent)
	{
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new GridLayout(2, false));
		
		Label interactionSelectorLabel = new Label(comp, SWT.NONE);
		interactionSelectorLabel.setText("Interaction Type:");
		interactionSelectorLabel.setLayoutData(new GridData());
		interactionSelector = new Combo(comp, SWT.DROP_DOWN | SWT.READ_ONLY);
		interactionSelector.setLayoutData(new GridData());
		
		scrollComp = new ScrolledComposite(comp, SWT.V_SCROLL | SWT.BORDER);
		scrollComp.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		scrollComp.getVerticalBar().setIncrement(4);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 2;
		gridData.heightHint = 400;
		scrollComp.setLayoutData(gridData);
		stack = new StackLayout();
		stackComp = new Composite(scrollComp, SWT.NONE);
		stackComp.setBackground(scrollComp.getBackground());
		stackComp.setLayout(stack);
		
		interactionTypes = InteractionTypeManager.getInstance().getInteractionTypes();
		for(InteractionType interactionType : interactionTypes)
		{
			interactionSelector.add(interactionType.getName());
			Composite interactionComp = new Composite(stackComp, SWT.NONE);
			interactionComp.setBackground(stackComp.getBackground());
			interactionComposites.add(interactionComp);
			interactionComp.setLayout(new GridLayout(1, false));
			Map<String, IMediaDefaultPanel> panelMap = MediaDefaultsPanelManager.getInstance().getIndexedMediaDefaultsPanels(interactionType.getId());
			for(IMediaDefaultPanel panel : panelMap.values())
			{
				Control panelControl = panel.createControls(interactionComp, false);
				panelControl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
				settingPanels.add(panel);
				if(wmd != null)
				{
					panel.setDefaultSettings(wmd);
				}
			}
		}
		interactionSelector.select(0);
		stack.topControl = interactionComposites.get(0);
		stackComp.layout(true, true);
		scrollComp.setContent(stackComp);
		scrollComp.setMinWidth(stackComp.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
		scrollComp.setMinHeight(stackComp.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).y);
		scrollComp.setExpandHorizontal(true);
		scrollComp.setExpandVertical(true);
		
		interactionSelector.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent arg0)
            {
            }

			public void widgetSelected(SelectionEvent arg0)
            {
				stack.topControl = interactionComposites.get(interactionSelector.getSelectionIndex());
				stackComp.layout(true, true);
				Point preferred = stackComp.computeSize(scrollComp.getMinWidth(), SWT.DEFAULT, true);
				scrollComp.setMinSize(preferred);
				stackComp.layout();
				if(preferred.y > scrollComp.getClientArea().height) //need to re-adjust because the scroll bar appeared
				{
					preferred = stackComp.computeSize(scrollComp.getClientArea().width, SWT.DEFAULT, true);
					scrollComp.setMinSize(preferred);
					stackComp.layout();
				}
				stackComp.getDisplay().asyncExec(new Runnable(){
                	public void run()
                	{
                		stackComp.layout(true, true);
                	}
                });
            }
			
		});
		
		return comp;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench)
	{
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		if(stackComp != null) //already created controls
		{
			for(int i = 0; i < settingPanels.size(); i++)
			{
				settingPanels.get(i).setDefaultSettings(wmd);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#performOk()
	 */
	public boolean performOk()
	{
		for(int i = 0; i < settingPanels.size(); i++)
		{
			settingPanels.get(i).save();
		}
		return super.performOk();
	}
	
}
