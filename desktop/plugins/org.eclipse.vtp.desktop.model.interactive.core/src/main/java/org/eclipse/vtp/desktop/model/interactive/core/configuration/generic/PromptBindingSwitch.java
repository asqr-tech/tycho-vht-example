/**
 * 
 */
package org.eclipse.vtp.desktop.model.interactive.core.configuration.generic;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Implementation of the prompt node selector.
 * 
 * @author Lonnie Pryor
 */
public final class PromptBindingSwitch extends PromptBindingNode {

	private List<PromptBindingCase> children = new ArrayList<PromptBindingCase>();

	public List<PromptBindingCase> getChildren() {
		return new ArrayList<PromptBindingCase>(children);
	}

	public PromptBindingCase getChild(int index) {
		return children.get(index);
	}

	public void addChild(PromptBindingCase child) {
		children.add(child);
		child.setParent(this);
	}

	public void addChild(int index, PromptBindingCase child) {
		children.add(index, child);
		child.setParent(this);
	}

	public void removeChild(Object child) {
		if (children.remove(child))
			((PromptBindingCase)child).setParent(null);
	}

	public void removeChild(int index) {
		children.remove(index).setParent(null);
	}

	public void clearChildren() {
		for (PromptBindingCase child : children)
			child.setParent(null);
		children.clear();
	}

	/* Read the configuration for this node. */
	@Override
	void readConfiguration(Element configuration) {
		NodeList contentList = configuration.getChildNodes();
		for (int i = 0; i < contentList.getLength(); i++) {
			if (!(contentList.item(i) instanceof Element))
				continue;
			Element element = (Element) contentList.item(i);
			PromptBindingCase child = new PromptBindingCase();
			child.readConfiguration(element);
			addChild(child);
		}
	}

	/* Write the configuration for this node. */
	@Override
	void writeConfiguration(Element configuration) {
		Element thisElement = configuration.getOwnerDocument().createElement(
				"binding-branch");
		configuration.appendChild(thisElement);
		for (PromptBindingCase child : children)
			child.writeConfiguration(thisElement);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() {
		PromptBindingSwitch copy = new PromptBindingSwitch();
		for (PromptBindingCase child : children)
			copy.addChild((PromptBindingCase)child.clone());
		return copy;
	}

}
