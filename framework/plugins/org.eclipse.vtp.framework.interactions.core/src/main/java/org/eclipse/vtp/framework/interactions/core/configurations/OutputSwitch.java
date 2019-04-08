package org.eclipse.vtp.framework.interactions.core.configurations;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.vtp.framework.interactions.core.media.IContentFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class OutputSwitch extends OutputNode implements InteractionsConstants
{
	private OutputCase[] cases = null;

	public OutputSwitch() {
	}

	public OutputSwitch(OutputCase[] cases) {
		this.cases = cases;
	}

	public OutputCase[] getCases() {
		return cases;
	}

	public void setCases(OutputCase[] cases) {
		this.cases = cases;
	}

	@Override
	void load(Element configurationElement, IContentFactory contentFactory) {
		NodeList childElements = configurationElement.getChildNodes();
		List<OutputCase> cases = new ArrayList<OutputCase>(
				childElements.getLength());
		for (int j = 0; j < childElements.getLength(); j++)
			if (childElements.item(j) instanceof Element) {
				OutputCase c = new OutputCase();
				c.load((Element) childElements.item(j), contentFactory);
				cases.add(c);
			}
		this.cases = cases.toArray(new OutputCase[cases.size()]);
	}

	@Override
	void save(Element configurationElement) {
		String outputNodeName = NAME_OUTPUT_NODE_SWITCH;
		String prefix = configurationElement.getPrefix();
		if (prefix != null && prefix.length() > 0)
			outputNodeName = prefix + ":" + outputNodeName; //$NON-NLS-1$
		Element outputNodeElement = configurationElement.getOwnerDocument()
				.createElementNS(NAMESPACE_URI, outputNodeName);
		if (cases != null)
			for (int j = 0; j < cases.length; ++j)
				cases[j].save(outputNodeElement);
		configurationElement.appendChild(outputNodeElement);
	}
}
