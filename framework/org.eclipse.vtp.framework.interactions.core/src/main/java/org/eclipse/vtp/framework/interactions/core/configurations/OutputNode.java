package org.eclipse.vtp.framework.interactions.core.configurations;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.vtp.framework.interactions.core.media.IContentFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public abstract class OutputNode implements InteractionsConstants
{
		static OutputNode[] loadAll(Element parentElement,
				IContentFactory contentFactory) {
			NodeList childElements = parentElement.getChildNodes();
			List<OutputNode> nodes = new ArrayList<OutputNode>(
					childElements.getLength());
			for (int j = 0; j < childElements.getLength(); ++j) {
				if (!(childElements.item(j) instanceof Element))
					continue;
				Element childElement = (Element) childElements.item(j);
				if (!NAMESPACE_URI.equals(childElement.getNamespaceURI()))
					continue;
				if (NAME_OUTPUT_NODE_CONTENT
						.equals(childElement.getLocalName())) {
					OutputContent node = new OutputContent();
					node.load(childElement, contentFactory);
					nodes.add(node);
				} else if (NAME_OUTPUT_NODE_SWITCH.equals(childElement
						.getLocalName())) {
					OutputSwitch node = new OutputSwitch();
					node.load(childElement, contentFactory);
					nodes.add(node);
				}
			}
			return nodes.toArray(new OutputNode[nodes.size()]);
		}

		abstract void load(Element configurationElement,
				IContentFactory contentFactory);

		abstract void save(Element configurationElement);
}
