package org.eclipse.vtp.desktop.model.elements.core.export;

import org.eclipse.vtp.desktop.export.IDefinitionBuilder;
import org.eclipse.vtp.framework.common.IArrayObject;
import org.eclipse.vtp.framework.common.IDateObject;
import org.eclipse.vtp.framework.common.configurations.AssignmentConfiguration;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class ConfigurationExportHelper
{
	/**
	 * Configures an assignment action.
	 * @param configuration 
	 * @param actionElement 
	 */
	public static void configureAssignmentAction(Element configuration,
			Element actionElement)
	{
		String uri = "http://www.eclipse.org/vtp/namespaces/config";//$NON-NLS-1$
		NodeList variableList = ((Element)((Element)configuration
				.getElementsByTagNameNS(uri, "custom-config").item(0)) //$NON-NLS-1$
				.getElementsByTagNameNS(uri, "declarations").item(0)) //$NON-NLS-1$
				.getElementsByTagNameNS(uri, "variable"); //$NON-NLS-1$
		for (int i = 0; i < variableList.getLength(); ++i)
		{
			Element variable = (Element)variableList.item(i);
			AssignmentConfiguration config = new AssignmentConfiguration();
			config.setName(variable.getAttribute("name")); //$NON-NLS-1$
			config.setSecured(Boolean.parseBoolean(variable.getAttribute("secured")));
			if(variable.getAttributeNode("type") != null) //legacy support
			{
				if ("1".equals(variable.getAttribute("multiplicity")) //$NON-NLS-1$ //$NON-NLS-2$
						|| "array".equalsIgnoreCase(variable.getAttribute("multiplicity"))) //$NON-NLS-1$ //$NON-NLS-2$
					config.setType(IArrayObject.TYPE_NAME);
				else if ("DateTime".equals(variable.getAttribute("type"))) //$NON-NLS-1$
					config.setType(IDateObject.TYPE_NAME); //$NON-NLS-1$
				else
					config.setType(variable.getAttribute("type")); //$NON-NLS-1$
			}
			else
			{
				Element fieldTypeElement = (Element)variable.getElementsByTagName("data-type").item(0); //$NON-NLS-1$
				String typeStr = fieldTypeElement.getAttribute("type");
				if(typeStr.indexOf(':') != -1)
				{
					typeStr = typeStr.substring(typeStr.indexOf(':') + 1);
				}
				if ("DateTime".equals(typeStr))
					typeStr = IDateObject.TYPE_NAME;
				config.setType(typeStr);
			}
			String value = variable.getAttribute("value"); //$NON-NLS-1$
			if (value != null && value.length() > 0)
				config.setValue(value);
			Element configElement = actionElement.getOwnerDocument().createElementNS(IDefinitionBuilder.NAMESPACE_URI_COMMON,
					"common:assignment"); //$NON-NLS-1$
			config.save(configElement);
			actionElement.appendChild(configElement);
		}
	}


}
