package org.eclipse.vtp.desktop.model.core.configuration;

import java.util.List;
import java.util.Map;

import org.eclipse.vtp.desktop.model.core.IWorkflowProject;

public interface ConfigurationContext
{
	public String getId();
	
	public String getName();
	
	public void setProject(IWorkflowProject project);
	
	public boolean setConfigurationContext(Map<String, Object> values);
	
	public String getLabel(Object obj);
	
	public List<Object> getValues();
}
