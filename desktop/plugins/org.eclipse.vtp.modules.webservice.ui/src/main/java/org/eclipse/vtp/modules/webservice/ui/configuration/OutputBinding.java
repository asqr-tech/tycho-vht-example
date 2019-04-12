package org.eclipse.vtp.modules.webservice.ui.configuration;

import java.util.List;

import org.eclipse.vtp.framework.util.XMLUtilities;
import org.w3c.dom.Element;

public class OutputBinding
{
	private String variableName = "";
	private boolean process = true;
	private String scriptText = "";

	public OutputBinding()
	{
		super();
	}

	public String getVariableName()
	{
		return variableName;
	}
	
	public void setVariableName(String variableName)
	{
		this.variableName = variableName;
	}
	
	public boolean shouldProcess()
	{
		return process;
	}
	
	public void setShouldProcess(boolean process)
	{
		this.process = process;
	}
	
	public String getScriptText()
	{
		return scriptText;
	}
	
	public void setScriptText(String scriptText)
	{
		this.scriptText = scriptText;
	}

	public void readConfiguration(Element configuration)
	{
		variableName = configuration.getAttribute("variable-name");
		String processString = configuration.getAttribute("should-process");
		process = !processString.equalsIgnoreCase("false");
		List<Element> scriptElementList = XMLUtilities.getElementsByTagName(configuration, "script", true);
		if(scriptElementList.size() > 0)
		{
			scriptText = XMLUtilities.getElementTextDataNoEx(scriptElementList.get(0), true);
			if(scriptText == null)
				scriptText = "";
		}
	}
	
	public void writeConfiguration(Element configuration)
	{
		configuration.setAttribute("variable-name", variableName);
		configuration.setAttribute("should-process", Boolean.toString(process));
		Element scriptElement = configuration.getOwnerDocument().createElement("script");
		configuration.appendChild(scriptElement);
		scriptElement.setTextContent(scriptText);
	}
}
