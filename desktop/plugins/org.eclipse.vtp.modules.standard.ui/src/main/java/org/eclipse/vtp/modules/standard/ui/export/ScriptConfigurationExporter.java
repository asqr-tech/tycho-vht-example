package org.eclipse.vtp.modules.standard.ui.export;

import org.eclipse.vtp.desktop.export.IConfigurationExporter;
import org.eclipse.vtp.desktop.export.IDefinitionBuilder;
import org.eclipse.vtp.desktop.export.IFlowElement;
import org.eclipse.vtp.framework.common.configurations.ScriptConfiguration;
import org.eclipse.vtp.framework.util.XMLUtilities;
import org.w3c.dom.Element;

public class ScriptConfigurationExporter implements IConfigurationExporter
{
	public ScriptConfigurationExporter()
	{
	}

	public void exportConfiguration(IFlowElement flowElement, Element actionElement)
	{
		String uri = "http://www.eclipse.org/vtp/namespaces/config";//$NON-NLS-1$
		ScriptConfiguration config = new ScriptConfiguration();
		config.setScriptingLanguage("JavaScript");
		Element customConfig = (Element)flowElement.getConfiguration().getElementsByTagNameNS(
				"http://www.eclipse.org/vtp/namespaces/config", //$NON-NLS-1$
				"custom-config").item(0); //$NON-NLS-1$
		config.setSecured(Boolean.parseBoolean(customConfig.getAttribute("secured")));
		String scriptText = XMLUtilities.getElementTextDataNoEx(
					(Element)customConfig.getElementsByTagNameNS(
							uri, "script").item(0), true);//$NON-NLS-1$
		if(scriptText == null)
			scriptText = "";
		config.setScript(scriptText);//$NON-NLS-1$
		Element configElement = actionElement.getOwnerDocument().createElementNS(
				IDefinitionBuilder.NAMESPACE_URI_COMMON,	"common:script"); //$NON-NLS-1$
		config.save(configElement);
		actionElement.appendChild(configElement);
	}

	public String getActionId(IFlowElement flowElement)
	{
		return "org.eclipse.vtp.framework.common.actions.scripted";
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
