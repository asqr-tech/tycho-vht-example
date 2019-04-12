package org.eclipse.vtp.desktop.model.elements.core.export;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.vtp.desktop.export.IConfigurationExporter;
import org.eclipse.vtp.desktop.export.IFlowElement;
import org.osgi.framework.Bundle;
import org.w3c.dom.Element;

public class PrimitiveElementConfigurationExporter implements
        IConfigurationExporter
{
	public static String primitiveExtensionPointId = "org.eclipse.vtp.desktop.model.elements.core.primitiveElementExporter";
	private static Map<String, IConfigurationExporter> primitiveExporters = new HashMap<String, IConfigurationExporter>();
	
	static
	{
		IConfigurationElement[] primitiveExtensions = Platform.getExtensionRegistry().getConfigurationElementsFor(primitiveExtensionPointId);
		for(int i = 0; i < primitiveExtensions.length; i++)
		{
			String id = primitiveExtensions[i].getAttribute("id");
			Bundle contributor = Platform.getBundle(primitiveExtensions[i].getContributor().getName());
			String className = primitiveExtensions[i].getAttribute("class");
			try
			{
				@SuppressWarnings("unchecked")
				Class<IConfigurationExporter> exporterClass = (Class<IConfigurationExporter>)contributor.loadClass(className);
				primitiveExporters.put(id, exporterClass.newInstance());
			}
			catch (Exception e)
			{
				e.printStackTrace();
				continue;
			}
		}
	}

	public void exportConfiguration(IFlowElement flowElement,
	        Element actionElement)
	{
		IConfigurationExporter exporter = primitiveExporters.get(flowElement.getProperties().getProperty("type"));
		if(exporter != null)
			exporter.exportConfiguration(flowElement, actionElement);
		else
			System.err.println("Unable to locate primitive exporter for " + flowElement.getProperties().getProperty("type"));
	}

	public String getActionId(IFlowElement flowElement)
	{
		IConfigurationExporter exporter = primitiveExporters.get(flowElement.getProperties().getProperty("type"));
		if(exporter != null)
			return exporter.getActionId(flowElement);
		System.err.println("Unable to locate primitive exporter for " + flowElement.getProperties().getProperty("type"));
		return flowElement.getProperties().getProperty("type");
	}

	public String getDefaultPath(IFlowElement flowElement)
	{
		IConfigurationExporter exporter = primitiveExporters.get(flowElement.getProperties().getProperty("type"));
		if(exporter != null)
			return exporter.getDefaultPath(flowElement);
		return "Continue";
	}
	
	public String translatePath(IFlowElement flowElement, String uiPath)
	{
		IConfigurationExporter exporter = primitiveExporters.get(flowElement.getProperties().getProperty("type"));
		if(exporter != null)
			return exporter.translatePath(flowElement, uiPath);
		return uiPath;
	}

	public String getTargetId(IFlowElement flowElement, Element afterTransitionElement)
	{
		IConfigurationExporter exporter = primitiveExporters.get(flowElement.getProperties().getProperty("type"));
		if(exporter != null)
			return exporter.getTargetId(flowElement, afterTransitionElement);
		return flowElement.getDefaultTargetId(afterTransitionElement);
	}
	
	public boolean isEntryPoint(IFlowElement flowElement)
	{
		IConfigurationExporter exporter = primitiveExporters.get(flowElement.getProperties().getProperty("type"));
		if(exporter != null)
			return exporter.isEntryPoint(flowElement);
		return false;
	}
}
