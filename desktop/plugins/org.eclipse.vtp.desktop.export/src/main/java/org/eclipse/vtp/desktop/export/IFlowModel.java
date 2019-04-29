package org.eclipse.vtp.desktop.export;

import java.util.Collection;
import java.util.Map;

public interface IFlowModel {
	
	Collection<IFlowElement> getEntries();
	
	Map<String, IFlowElement> getElementsById();
	
	IDefinitionBuilder getDefinitionBuilder();
}
