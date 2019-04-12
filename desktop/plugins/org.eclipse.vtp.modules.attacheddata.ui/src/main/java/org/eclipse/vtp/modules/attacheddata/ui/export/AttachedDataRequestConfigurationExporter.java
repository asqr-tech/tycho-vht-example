package org.eclipse.vtp.modules.attacheddata.ui.export;

import org.eclipse.vtp.desktop.export.IConfigurationExporter;
import org.eclipse.vtp.desktop.export.IDefinitionBuilder;
import org.eclipse.vtp.desktop.export.IFlowElement;
import org.eclipse.vtp.framework.interactions.core.configurations.MetaDataConfiguration;
import org.eclipse.vtp.framework.interactions.core.configurations.MetaDataItemConfiguration;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class AttachedDataRequestConfigurationExporter implements IConfigurationExporter
{

	public AttachedDataRequestConfigurationExporter()
	{
	}

	public void exportConfiguration(IFlowElement flowElement, Element actionElement)
	{
		String uri = "http://eclipse.org/vtp/xml/configuration/attacheddata/request"; //$NON-NLS-1$
		NodeList bindingsList = flowElement.getConfiguration().getElementsByTagNameNS(uri,
				"bindings"); //$NON-NLS-1$
		Element attachedData = null;
		for (int i = 0; attachedData == null && i < bindingsList.getLength(); ++i)
		{
			Element bindings = (Element)bindingsList.item(i);
			NodeList itemList = bindings.getElementsByTagNameNS(uri,
					"attached-data-binding"); //$NON-NLS-1$
			for (int j = 0; attachedData == null && j < itemList.getLength(); ++j)
			{
				Element item = (Element)itemList.item(j);
				if ("default".equals(item.getAttribute("name"))) //$NON-NLS-1$
					attachedData = item;
			}
		}
		if (attachedData == null)
			return;
		MetaDataConfiguration config = new MetaDataConfiguration();
		NodeList itemList = attachedData.getElementsByTagNameNS(uri, "item"); //$NON-NLS-1$
		int count = 0;
		for (int i = 0; i < itemList.getLength(); ++i)
		{
			Element item = (Element)itemList.item(i);
			NodeList entryList = item.getElementsByTagNameNS(uri, "entry"); //$NON-NLS-1$
			if (entryList.getLength() > 0)
			{
				MetaDataItemConfiguration[] metaDataItems = new MetaDataItemConfiguration[entryList
						.getLength()];
				for (int j = 0; j < entryList.getLength(); ++j)
				{
					Element entry = (Element)entryList.item(j);
					MetaDataItemConfiguration metaDataItem = new MetaDataItemConfiguration();
					metaDataItem.setName(entry.getAttribute("name")); //$NON-NLS-1$
					System.out.println("Meta Data Entry Type: " + entry.getAttribute("type"));
					if ("variable".equalsIgnoreCase(entry.getAttribute("type"))) //$NON-NLS-1$
						metaDataItem.setVariableValue(entry.getAttribute("value")); //$NON-NLS-1$
					else if ("expression".equalsIgnoreCase(entry.getAttribute("type"))) //$NON-NLS-1$
						metaDataItem.setExpressionValue(entry.getAttribute("value"), //$NON-NLS-1$
								"JavaScript"); //$NON-NLS-1$
					else if("map".equalsIgnoreCase(entry.getAttribute("type")))
						metaDataItem.setMapValue(entry.getAttribute("value"));
					else
						metaDataItem.setStaticValue(entry.getAttribute("value")); //$NON-NLS-1$
					metaDataItems[j] = metaDataItem;
				}
				config.setItem(item.getAttribute("key"), metaDataItems); //$NON-NLS-1$
				++count;
			}
		}
		if (count == 0)
			return;
		Element configElement = actionElement.getOwnerDocument().createElementNS(
				IDefinitionBuilder.NAMESPACE_URI_INTERACTIONS_CORE, "interactions:meta-data"); //$NON-NLS-1$
		config.save(configElement);
		actionElement.appendChild(configElement);
	}

	public String getActionId(IFlowElement flowElement)
	{
		return "org.eclipse.vtp.framework.interactions.core.actions.meta-data-request";
	}

	public String getDefaultPath(IFlowElement flowElement)
	{
		return "Continue";
	}
	
	public String translatePath(IFlowElement flowElement, String uiPath)
	{
		return uiPath;
	}

	public String getTargetId(IFlowElement flowElement, Element afterTransitionElement)
	{
		return flowElement.getDefaultTargetId(afterTransitionElement);
	}
	
	public boolean isEntryPoint(IFlowElement flowElement)
	{
		return false;
	}
}
