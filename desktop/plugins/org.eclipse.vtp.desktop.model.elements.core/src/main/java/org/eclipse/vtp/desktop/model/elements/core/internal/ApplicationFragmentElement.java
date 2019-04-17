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
package org.eclipse.vtp.desktop.model.elements.core.internal;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.eclipse.swt.graphics.Image;
import org.eclipse.vtp.desktop.model.core.IDesignDocument;
import org.eclipse.vtp.desktop.model.core.IWorkflowEntry;
import org.eclipse.vtp.desktop.model.core.IWorkflowExit;
import org.eclipse.vtp.desktop.model.core.IWorkflowProject;
import org.eclipse.vtp.desktop.model.core.IWorkflowReference;
import org.eclipse.vtp.desktop.model.core.WorkflowCore;
import org.eclipse.vtp.desktop.model.core.configuration.ConfigurationManager;
import org.eclipse.vtp.desktop.model.core.design.IDesign;
import org.eclipse.vtp.desktop.model.core.design.IDesignElement;
import org.eclipse.vtp.desktop.model.core.design.IDesignElementConnectionPoint;
import org.eclipse.vtp.desktop.model.core.internal.WorkflowTraversalHelper;
import org.eclipse.vtp.desktop.model.core.internal.design.ConnectorRecord;
import org.eclipse.vtp.desktop.model.core.internal.design.DesignElement;
import org.eclipse.vtp.desktop.model.core.internal.design.ElementResolutionVisitor;
import org.eclipse.vtp.desktop.model.elements.core.configuration.FragmentConfigurationManager;

public class ApplicationFragmentElement extends DesignElement implements IWorkflowReference
{
	public static final String ELEMENT_TYPE = "org.eclipse.vtp.desktop.model.elements.core.include";

	private List<ConnectorRecord> connectorRecords = new ArrayList<ConnectorRecord>();
	private String fragmentId;
	private String entryId;
	private boolean missingModelMode = true;
	private IWorkflowProject workflowProject = null;

	public ApplicationFragmentElement(String fragmentId, String name)
	{
		super(name);
		getProperties().setProperty("instanceId", fragmentId);
		setFragmentId(fragmentId, true);
	}

	public ApplicationFragmentElement(String id, String name, Properties properties)
	{
		super(id, name, properties);
		setFragmentId(properties.getProperty("instanceId"), Boolean.parseBoolean(properties.getProperty("followExternalReferences", "true")));
	}
	
	public String getType()
	{
		return ELEMENT_TYPE;
	}
	
	public void setFragmentId(String fragmentId, boolean followExternalReferences)
	{
		this.fragmentId = fragmentId;
		if(followExternalReferences)
		{
			workflowProject = WorkflowCore.getDefault().getWorkflowModel().getWorkflowProject(fragmentId);
			if(workflowProject != null)
			{
				missingModelMode = false;
			}
		}
	}
	
	private void setEntry(String entryId)
	{
		this.entryId = entryId;
		if(!missingModelMode)
		{
			List<ConnectorRecord> oldCRs = connectorRecords;
			connectorRecords = new LinkedList<ConnectorRecord>();
			IWorkflowEntry entry = null;
			if(entryId == null || entryId.equals(""))
			{
				entry = workflowProject.getWorkflowEntryByName("Begin");
			}
			else
			{
				entry = workflowProject.getWorkflowEntry(entryId);
			}
			if(entry != null)
			{
				List<IDesignDocument> workingCopies = new ArrayList<IDesignDocument>();
				workingCopies.add(getDesign().getDocument());
				WorkflowTraversalHelper wth = new WorkflowTraversalHelper(workflowProject, workingCopies);
				List<IWorkflowExit> exitPoints = wth.getDownStreamWorkflowExits(entry);
outer:			for(IWorkflowExit exit : exitPoints)
				{
					for(ConnectorRecord oldCR : oldCRs)
					{
						if(oldCR.getName().equals(exit.getName()) && oldCR.getType().equals(exit.getType().equals(IWorkflowExit.NORMAL) ? IDesignElementConnectionPoint.ConnectionPointType.EXIT_POINT : 
												IDesignElementConnectionPoint.ConnectionPointType.ERROR_POINT))
						{
							connectorRecords.add(oldCR);
							continue outer;
						}
					}
					ConnectorRecord cr =
						new ConnectorRecord(this,
											exit.getName(),
											exit.getType().equals(IWorkflowExit.NORMAL) ? 
												IDesignElementConnectionPoint.ConnectionPointType.EXIT_POINT : 
												IDesignElementConnectionPoint.ConnectionPointType.ERROR_POINT);
					connectorRecords.add(cr);
				}
				//Remove connectors not associated with the current entry
				for(ConnectorRecord oldCR : oldCRs)
				{
					if(!connectorRecords.contains(oldCR) && oldCR.getDesignConnector() != null)
					{
						getDesign().removeDesignConnector(oldCR.getDesignConnector());
					}
				}
			}
		}
	}
	
	public String getFragmentId()
	{
		return fragmentId;
	}
	
	public boolean isModelPresent()
	{
		return !missingModelMode;
	}
	
	public IWorkflowProject getReferencedModel()
	{
		return workflowProject;
	}

	public IDesignElementConnectionPoint getConnectorRecord(String recordName)
	{
		for(int i = 0; i < connectorRecords.size(); i++)
		{
			ConnectorRecord cr = connectorRecords.get(i);
			if(cr.getName().equals(recordName))
				return cr;
		}
		if(missingModelMode)
		{
			ConnectorRecord cr =
				new ConnectorRecord(this,
									recordName,
									recordName.startsWith("error.") ? 
										IDesignElementConnectionPoint.ConnectionPointType.ERROR_POINT :
										IDesignElementConnectionPoint.ConnectionPointType.EXIT_POINT);
			connectorRecords.add(cr);
			return cr;
		}
		return null;
	}

	public List<IDesignElementConnectionPoint> getConnectorRecords()
	{
		return new LinkedList<IDesignElementConnectionPoint>(connectorRecords);
	}
	
	public List<IDesignElementConnectionPoint> getConnectorRecords(IDesignElementConnectionPoint.ConnectionPointType... types)
	{
		List<IDesignElementConnectionPoint> ret = new ArrayList<IDesignElementConnectionPoint>();
		for(ConnectorRecord cr : connectorRecords)
		{
			if(cr.getType().isSet(IDesignElementConnectionPoint.ConnectionPointType.getFlagSet(types)));
				ret.add(cr);
		}
		return ret;
	}
	
	@Override
	public void addConfigurationManager(ConfigurationManager manager)
	{
		super.addConfigurationManager(manager);
		if(manager.getType().equals(FragmentConfigurationManager.TYPE_ID))
		{
			FragmentConfigurationManager fragmentConfigurationManager = (FragmentConfigurationManager)manager;
			String currentEntry = fragmentConfigurationManager.getEntryId();
			if(currentEntry == null || currentEntry.equals(""))
			{
				IWorkflowEntry entry = workflowProject.getWorkflowEntryByName("Begin Fragment");
				if(entry != null)
				{
					currentEntry = entry.getId();
					fragmentConfigurationManager.setEntryId(currentEntry);
				}
			}
			setEntry(currentEntry);
		}
	}

	@Override
	public void commitConfigurationChanges(ConfigurationManager manager)
	{
		super.commitConfigurationChanges(manager);
		if(manager.getType().equals(FragmentConfigurationManager.TYPE_ID))
			setEntry(((FragmentConfigurationManager)manager).getEntryId());
	}

	public void readCustomConfiguration(org.w3c.dom.Element configuration)
	{
	}

	public void writeCustomConfiguration(org.w3c.dom.Element customElement)
	{
	}
	
	public boolean acceptsConnector(IDesignElement origin)
	{
		return true;
	}

	public String getTypeName()
	{
		return "Application Fragment";
	}

	public Image getIcon()
	{
		return null;
	}
	
	public void resolve(ElementResolutionVisitor resolutionVisitor)
	{
	}
	
	public boolean hasConnectors()
    {
	    return !connectorRecords.isEmpty();
    }
	
	public boolean canBeContainedBy(IDesign design)
	{
		return design.equals(design.getDocument().getMainDesign());
	}

	@SuppressWarnings("unchecked")
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter)
	{
//		System.out.println("Adapting fragment element to: " + adapter.getName() + " " + adapter.isAssignableFrom(getClass()));
		if(adapter.isAssignableFrom(getClass()))
			return this;
		return null;
	}

	@Override
	public String getTargetId() {
		return fragmentId;
	}

	@Override
	public String getEntryId()
	{
		return entryId;
	}
}
