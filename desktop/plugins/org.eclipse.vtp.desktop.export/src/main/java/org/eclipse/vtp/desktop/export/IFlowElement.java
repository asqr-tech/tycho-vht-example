package org.eclipse.vtp.desktop.export;

import java.util.Properties;

import org.eclipse.vtp.framework.interactions.core.configurations.MediaConfiguration;
import org.w3c.dom.Element;

public interface IFlowElement {
	
	String getId();
	
	String getName();
	
	int getContext();
	
	Element getConfiguration();
	
	Properties getProperties();
	
	IFlowElement getResultPath(String path);

	MediaConfiguration loadMediaBindings(String elementTypeId, Element genericConfigElement);

	String getTargetID(Element afterTransitionElement);
	
	String getDefaultTargetId(Element afterTansitionElement);
	
	MediaConfiguration loadMediaBindings(String elementTypeId);
	
	void buildObservers(String designPath, Element afterTrasition);
	
	IFlowModel getModel();
	
}
