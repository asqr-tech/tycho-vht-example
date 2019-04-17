package org.eclipse.vtp.modules.standard.ui.export;

import java.util.Iterator;
import java.util.TreeMap;

import org.eclipse.vtp.desktop.export.IConfigurationExporter;
import org.eclipse.vtp.desktop.export.IDefinitionBuilder;
import org.eclipse.vtp.desktop.export.IFlowElement;
import org.eclipse.vtp.framework.common.configurations.BranchConfiguration;
import org.eclipse.vtp.modules.standard.ui.properties.Branch;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class BranchConfigurationExporter implements IConfigurationExporter
{
	public BranchConfigurationExporter()
	{
	}

	public void exportConfiguration(IFlowElement flowElement, Element actionElement)
	{
		TreeMap<Integer, Branch> branchMap = extractBranchesToTreeMap(flowElement);
		
		Iterator<Integer> i = branchMap.keySet().iterator();
		while(i.hasNext())
		{
			int b = i.next();
			Branch branch = branchMap.get(b);
			BranchConfiguration config = new BranchConfiguration();

			config.setType(BranchConfiguration.COMPARISON_TYPE_EQUAL);
			config.setPath(branch.getName());
			
			config.setLeftExpressionValue(branch.getExpression(), "JavaScript");
			config.setLeftSecured(branch.isSecure());
			
			config.setRightExpressionValue("true", "JavaScript");
			config.setRightSecured(branch.isSecure());
			
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
		return "Default";
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

	private TreeMap<Integer, Branch> extractBranchesToTreeMap(IFlowElement flowElement)
	{
		TreeMap<Integer,Branch> branchMap = new TreeMap<Integer,Branch>();
		String uri = "http://www.eclipse.org/vtp/namespaces/config";
		NodeList branchNodeList = ((Element)flowElement.getConfiguration().getElementsByTagNameNS(
				uri, "custom-config").item(0)).getElementsByTagNameNS(uri, "branch");
		for(int b = 0; b < branchNodeList.getLength(); b++)
		{
			String name;
			String expression;
			boolean secured;
			int number;

			Element branchElement = (Element)branchNodeList.item(b);

			name = branchElement.getAttribute("name");
			expression = branchElement.getAttribute("expression");
			secured = Boolean.parseBoolean(branchElement.getAttribute("secure"));
			number = Integer.parseInt(branchElement.getAttribute("number"));
			
			branchMap.put(number, new Branch(name, expression, secured, number));
		}
		return branchMap;
	}
}
