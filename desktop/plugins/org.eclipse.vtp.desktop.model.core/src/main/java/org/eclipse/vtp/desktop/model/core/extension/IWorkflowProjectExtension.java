package org.eclipse.vtp.desktop.model.core.extension;

import java.util.List;

public interface IWorkflowProjectExtension
{
	public String getId();
	
	public String getName();
	
	public List<String> getReservedNames();
	
}
