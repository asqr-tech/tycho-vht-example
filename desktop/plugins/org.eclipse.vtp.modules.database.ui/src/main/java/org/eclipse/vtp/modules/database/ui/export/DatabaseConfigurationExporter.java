package org.eclipse.vtp.modules.database.ui.export;

import org.eclipse.vtp.desktop.export.IConfigurationExporter;
import org.eclipse.vtp.desktop.export.IDefinitionBuilder;
import org.eclipse.vtp.desktop.export.IFlowElement;
import org.eclipse.vtp.framework.databases.configurations.DatabaseCriteriaConfiguration;
import org.eclipse.vtp.framework.databases.configurations.DatabaseMappingConfiguration;
import org.eclipse.vtp.framework.databases.configurations.DatabaseQueryConfiguration;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class DatabaseConfigurationExporter implements IConfigurationExporter
{
	public DatabaseConfigurationExporter()
	{
	}

	public void exportConfiguration(IFlowElement flowElement, Element actionElement)
	{
		String uri = "http://www.eclipse.org/vtp/namespaces/config"; //$NON-NLS-1$
		DatabaseQueryConfiguration query = new DatabaseQueryConfiguration();
		Element settings = (Element)((Element)flowElement.getConfiguration()
				.getElementsByTagNameNS(uri, "custom-config").item(0)) //$NON-NLS-1$
				.getElementsByTagNameNS(uri, "settings").item(0); //$NON-NLS-1$
		query.setDatabase(settings.getAttribute("db-name"));
		query.setTable(settings.getAttribute("db-table"));
		query.setResultName(settings.getAttribute("var-name"));
		query.setResultType(settings.getAttribute("var-type"));
		query.setResultSecured(Boolean.parseBoolean(settings.getAttribute("var-secured")));
		query.setResultArray("1".equals(settings.getAttribute("var-multi")));
		if (query.isResultArray())
			query.setResultLimit(Integer.parseInt(settings
					.getAttribute("db-result-limit")));
		NodeList list = ((Element)settings
				.getElementsByTagNameNS(uri, "mappings").item(0))
				.getElementsByTagNameNS(uri, "mapping");
		for (int i = 0; i < list.getLength(); ++i)
		{
			Element mappingElement = (Element)list.item(i);
			DatabaseMappingConfiguration mapping = new DatabaseMappingConfiguration();
			mapping.setName(mappingElement.getAttribute("name"));
			if ("0".equals(mappingElement.getAttribute("type")))
				mapping.setType(DatabaseMappingConfiguration.TYPE_STATIC);
			else if ("1".equals(mappingElement.getAttribute("type")))
				mapping.setType(DatabaseMappingConfiguration.TYPE_COLUMN);
			else
				mapping.setType(DatabaseMappingConfiguration.TYPE_NONE);
			if (mapping.getType() != DatabaseMappingConfiguration.TYPE_NONE)
				mapping.setValue(mappingElement.getAttribute("value"));
			query.addMapping(mapping);
		}
		list = ((Element)settings.getElementsByTagNameNS(uri, "criteria").item(0))
				.getElementsByTagNameNS(uri, "criterium");
		for (int i = 0; i < list.getLength(); ++i)
		{
			Element criteriumElement = (Element)list.item(i);
			DatabaseCriteriaConfiguration criteria = new DatabaseCriteriaConfiguration();
			criteria.setName(criteriumElement.getAttribute("name"));
			if ("0".equals(criteriumElement.getAttribute("type")))
				criteria.setType(DatabaseCriteriaConfiguration.TYPE_STATIC);
			else if ("1".equals(criteriumElement.getAttribute("type")))
				criteria.setType(DatabaseCriteriaConfiguration.TYPE_VARIABLE);
			else
				criteria.setType(DatabaseCriteriaConfiguration.TYPE_NONE);
			if (criteria.getType() != DatabaseCriteriaConfiguration.TYPE_NONE)
			{
				if ("1".equals(criteriumElement.getAttribute("comp")))
					criteria
							.setComparison(DatabaseCriteriaConfiguration.COMPARISON_NOT_EQUAL);
				else if ("2".equals(criteriumElement.getAttribute("comp")))
					criteria
							.setComparison(DatabaseCriteriaConfiguration.COMPARISON_LESS_THAN);
				else if ("3".equals(criteriumElement.getAttribute("comp")))
					criteria
							.setComparison(DatabaseCriteriaConfiguration.COMPARISON_LESS_THAN_OR_EQUAL);
				else if ("4".equals(criteriumElement.getAttribute("comp")))
					criteria
							.setComparison(DatabaseCriteriaConfiguration.COMPARISON_GREATER_THAN);
				else if ("5".equals(criteriumElement.getAttribute("comp")))
					criteria
							.setComparison(DatabaseCriteriaConfiguration.COMPARISON_GREATER_THAN_OR_EQUAL);
				else
					criteria
							.setComparison(DatabaseCriteriaConfiguration.COMPARISON_EQUAL);
				criteria.setValue(criteriumElement.getAttribute("value"));
			}
			query.addCriteria(criteria);
		}
		Element configElement = actionElement.getOwnerDocument().createElementNS(
				IDefinitionBuilder.NAMESPACE_URI_DATABASES, "database:query"); //$NON-NLS-1$
		query.save(configElement);
		actionElement.appendChild(configElement);
	}

	public String getActionId(IFlowElement flowElement)
	{
		return "org.eclipse.vtp.framework.databases.actions.database-query";
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
