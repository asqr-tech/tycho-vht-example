package org.eclipse.vtp.framework.interactions.core.configurations;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.vtp.framework.interactions.core.media.Content;
import org.eclipse.vtp.framework.interactions.core.media.IContentFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class OutputContent extends OutputNode implements InteractionsConstants
{
	private Content[] content = null;

	public OutputContent() {
	}

	public OutputContent(Content[] content) {
		this.content = content;
	}

	public Content[] getContent() {
		return content;
	}

	public void setContent(Content[] content) {
		this.content = content;
	}

	@Override
	void load(Element configurationElement, IContentFactory contentFactory) {
		NodeList contentElements = configurationElement.getChildNodes();
		List<Content> content = new ArrayList<Content>(
				contentElements.getLength());
		for (int j = 0; j < contentElements.getLength(); j++)
			if (contentElements.item(j) instanceof Element) {
				Element item = (Element) contentElements.item(j);
				Content loadContent = contentFactory.loadContent(item);
				content.add(loadContent);
			}
		this.content = content.toArray(new Content[content.size()]);
	}

	@Override
	void save(Element configurationElement) {
		String outputNodeName = NAME_OUTPUT_NODE_CONTENT;
		String prefix = configurationElement.getPrefix();
		if (prefix != null && prefix.length() > 0)
			outputNodeName = prefix + ":" + outputNodeName; //$NON-NLS-1$
		Element outputNodeElement = configurationElement.getOwnerDocument()
				.createElementNS(NAMESPACE_URI, outputNodeName);
		if (content != null)
			for (int j = 0; j < content.length; ++j)
				content[j].store(outputNodeElement);
		configurationElement.appendChild(outputNodeElement);
	}
}
