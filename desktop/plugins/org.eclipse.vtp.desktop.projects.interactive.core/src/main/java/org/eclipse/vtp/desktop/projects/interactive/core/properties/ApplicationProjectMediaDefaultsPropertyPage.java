package org.eclipse.vtp.desktop.projects.interactive.core.properties;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
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
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.vtp.desktop.media.core.IMediaDefaultPanel;
import org.eclipse.vtp.desktop.media.core.MediaDefaultsPanelManager;
import org.eclipse.vtp.desktop.model.core.IWorkflowProject;
import org.eclipse.vtp.desktop.model.core.WorkflowCore;
import org.eclipse.vtp.desktop.model.interactive.core.InteractionType;
import org.eclipse.vtp.desktop.model.interactive.core.InteractionTypeManager;
import org.eclipse.vtp.desktop.model.interactive.core.internal.InteractiveWorkflowProject;
import org.eclipse.vtp.desktop.model.interactive.core.mediadefaults.IMediaDefaultSettings;

public class ApplicationProjectMediaDefaultsPropertyPage extends PropertyPage
{
	private InteractiveWorkflowProject applicationProject = null;
	private IMediaDefaultSettings mediaDefaultSettings = null;
	ScrolledComposite scrollComp = null;
	Composite stackComp = null;
	StackLayout stack = null;
	Combo interactionSelector = null;
	List<InteractionType> interactionTypes = null;
	List<Composite> interactionComposites = new ArrayList<Composite>();
	List<IMediaDefaultPanel> settingPanels = new ArrayList<IMediaDefaultPanel>();
	
	public ApplicationProjectMediaDefaultsPropertyPage()
	{
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.dialogs.PropertyPage#setElement(org.eclipse.core.runtime.IAdaptable)
	 */
	public void setElement(IAdaptable element)
	{
		super.setElement(element);
		try
        {
	        if(element instanceof InteractiveWorkflowProject)
	        	applicationProject = (InteractiveWorkflowProject)element;
	        else if(element instanceof IProject)
	        {
	        	IProject project = (IProject)element;
	        	if(WorkflowCore.getDefault().getWorkflowModel().isWorkflowProject(project))
	        	{
	        		IWorkflowProject workflowProject = WorkflowCore.getDefault().getWorkflowModel().convertToWorkflowProject(project);
	        		if(workflowProject instanceof InteractiveWorkflowProject)
	        			applicationProject = (InteractiveWorkflowProject)workflowProject;
	        	}
	        	else
	        		throw new RuntimeException("Unsupported element type");
	        }
	        else
        		throw new RuntimeException("Unsupported element type");
	        mediaDefaultSettings = applicationProject.getMediaDefaultSettings();
			if(stackComp != null) //already created controls
			{
				for(int i = 0; i < settingPanels.size(); i++)
				{
					settingPanels.get(i).setDefaultSettings(mediaDefaultSettings);
				}
			}
        }
        catch(Exception e)
        {
	        e.printStackTrace();
        }
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
		scrollComp.getVerticalBar().setIncrement(4);
		scrollComp.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 2;
		gridData.heightHint = 400;
		scrollComp.setLayoutData(gridData);
		stack = new StackLayout();
		stackComp = new Composite(scrollComp, SWT.NONE);
		stackComp.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		stackComp.setLayout(stack);
		
		interactionTypes = InteractionTypeManager.getInstance().getInteractionTypes();
		for(int i = 0; i < interactionTypes.size(); i++)
		{
			InteractionType interactionType = interactionTypes.get(i);
			interactionSelector.add(interactionType.getName());
			Composite interactionComp = new Composite(stackComp, SWT.NONE);
			interactionComp.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
			interactionComposites.add(interactionComp);
			interactionComp.setLayout(new GridLayout(1, false));
			Map<String, IMediaDefaultPanel> panelMap = MediaDefaultsPanelManager.getInstance().getIndexedMediaDefaultsPanels(interactionType.getId());
			for(IMediaDefaultPanel panel : panelMap.values())
			{
				Control panelControl = panel.createControls(interactionComp, true);
				panelControl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
				settingPanels.add(panel);
				if(mediaDefaultSettings != null)
				{
					panel.setDefaultSettings(mediaDefaultSettings);
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
	 * @see org.eclipse.jface.preference.PreferencePage#performOk()
	 */
	public boolean performOk()
	{
		for(int i = 0; i < settingPanels.size(); i++)
		{
			settingPanels.get(i).save();
		}
		applicationProject.storeMediaDefaultSettings();
		return true;
	}
	

}
