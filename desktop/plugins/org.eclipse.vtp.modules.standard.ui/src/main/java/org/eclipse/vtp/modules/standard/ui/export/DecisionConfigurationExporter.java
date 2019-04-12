package org.eclipse.vtp.modules.standard.ui.export;

import org.eclipse.vtp.desktop.export.IConfigurationExporter;
import org.eclipse.vtp.desktop.export.IDefinitionBuilder;
import org.eclipse.vtp.desktop.export.IFlowElement;
import org.eclipse.vtp.framework.common.configurations.BranchConfiguration;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class DecisionConfigurationExporter implements IConfigurationExporter
{
	public DecisionConfigurationExporter()
	{
	}

	public void exportConfiguration(IFlowElement flowElement, Element actionElement)
	{
		String uri = "http://www.eclipse.org/vtp/namespaces/config";//$NON-NLS-1$
		NodeList comparisonList = ((Element)flowElement.getConfiguration().getElementsByTagNameNS(
				uri, "custom-config").item(0)) //$NON-NLS-1$
				.getElementsByTagNameNS(uri, "comparison"); //$NON-NLS-1$
		if (comparisonList.getLength() > 0)
		{
			Element comparison = (Element)comparisonList.item(0);
			BranchConfiguration config = new BranchConfiguration();
			String typeStr = comparison.getAttribute("type");
			if ("equal".equals(typeStr)) //$NON-NLS-1$
				config.setType(BranchConfiguration.COMPARISON_TYPE_EQUAL);
			else if ("less-than".equals(typeStr)) //$NON-NLS-1$
				config.setType(BranchConfiguration.COMPARISON_TYPE_LESS_THAN);
			else if ("less-than-or-equal".equals(typeStr)) //$NON-NLS-1$
				config
						.setType(BranchConfiguration.COMPARISON_TYPE_LESS_THAN_OR_EQUAL);
			else if ("greater-than".equals(typeStr)) //$NON-NLS-1$
				config.setType(BranchConfiguration.COMPARISON_TYPE_GREATER_THAN);
			else if ("greater-than-or-equal".equals(typeStr)) //$NON-NLS-1$
				config
						.setType(BranchConfiguration.COMPARISON_TYPE_GREATER_THAN_OR_EQUAL);
			else if ("not-equal".equals(typeStr)) //$NON-NLS-1$
				config.setType(BranchConfiguration.COMPARISON_TYPE_NOT_EQUAL);
			config.setPath("True"); //$NON-NLS-1$
			if ("expression".equalsIgnoreCase( //$NON-NLS-1$
					comparison.getAttribute("left-type"))) //$NON-NLS-1$
			{
				config.setLeftExpressionValue(comparison.getAttribute("left-value"), //$NON-NLS-1$
						"JavaScript"); //$NON-NLS-1$
				config.setLeftSecured(Boolean.parseBoolean(comparison.getAttribute("left-secured")));
			}
			else
				config.setLeftVariableValue(comparison.getAttribute("left-value")); //$NON-NLS-1$
			if ("expression".equalsIgnoreCase( //$NON-NLS-1$
					comparison.getAttribute("right-type"))) //$NON-NLS-1$
			{
				config.setRightExpressionValue(
						comparison.getAttribute("right-value"), //$NON-NLS-1$
						"JavaScript"); //$NON-NLS-1$
				config.setRightSecured(Boolean.parseBoolean(comparison.getAttribute("right-secured")));
			}
			else
				config.setRightVariableValue(comparison.getAttribute("right-value")); //$NON-NLS-1$
			Element configElement = actionElement.getOwnerDocument().createElementNS(
					IDefinitionBuilder.NAMESPACE_URI_COMMON, "common:branch"); //$NON-NLS-1$
			config.save(configElement);
			actionElement.appendChild(configElement);
		}
	}

	public String getActionId(IFlowElement flowElement)
	{
		return "org.eclipse.vtp.framework.common.actions.branch";
	}

	public String getDefaultPath(IFlowElement flowElement)
	{
		return "False";
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
