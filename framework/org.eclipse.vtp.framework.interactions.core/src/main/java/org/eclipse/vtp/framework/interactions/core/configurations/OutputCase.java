package org.eclipse.vtp.framework.interactions.core.configurations;

import org.eclipse.vtp.framework.interactions.core.media.IContentFactory;
import org.w3c.dom.Element;

public class OutputCase implements InteractionsConstants
{
	private String script = ""; //$NON-NLS-1$
	private String scriptingLanguage = ""; //$NON-NLS-1$
	private OutputNode[] nodes = null;

	public OutputCase() {
	}

	public OutputCase(String script, String scriptingLanguage,
			OutputNode[] nodes) {
		this.script = script;
		this.scriptingLanguage = scriptingLanguage;
		this.nodes = nodes;
	}

	public String getScriptingLanguage() {
		return scriptingLanguage;
	}

	public void setScriptingLanguage(String scriptingLanguage) {
		this.scriptingLanguage = scriptingLanguage;
	}

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

	public OutputNode[] getNodes() {
		return nodes;
	}

	public void setNodes(OutputNode[] nodes) {
		this.nodes = nodes;
	}

	void load(Element configurationElement, IContentFactory contentFactory) {
		script = configurationElement.getAttribute(NAME_SCRIPT);
		scriptingLanguage = configurationElement
				.getAttribute(NAME_SCRIPTING_LANGUGAGE);
		nodes = OutputNode.loadAll(configurationElement, contentFactory);
	}

	void save(Element configurationElement) {
		String outputNodeName = NAME_OUTPUT_NODE_CASE;
		String prefix = configurationElement.getPrefix();
		if (prefix != null && prefix.length() > 0)
			outputNodeName = prefix + ":" + outputNodeName; //$NON-NLS-1$
		Element outputNodeElement = configurationElement.getOwnerDocument()
				.createElementNS(NAMESPACE_URI, outputNodeName);
		if (nodes != null)
			for (int j = 0; j < nodes.length; ++j)
				nodes[j].save(outputNodeElement);
		outputNodeElement.setAttribute(NAME_SCRIPT, script);
		outputNodeElement.setAttribute(NAME_SCRIPTING_LANGUGAGE,
				scriptingLanguage);
		configurationElement.appendChild(outputNodeElement);
	}
}
