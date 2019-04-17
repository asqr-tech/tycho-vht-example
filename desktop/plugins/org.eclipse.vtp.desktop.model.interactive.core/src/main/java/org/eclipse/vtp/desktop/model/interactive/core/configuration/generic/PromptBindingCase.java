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
public final class PromptBindingCase extends PromptBindingNode {

	private String condition = null;

	private List<PromptBindingNode> children = new ArrayList<PromptBindingNode>();

	public PromptBindingCase() {
	}

	public PromptBindingCase(String condition) {
		this.condition = condition;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public List<PromptBindingNode> getChildren() {
		return new ArrayList<PromptBindingNode>(children);
	}

	public PromptBindingNode getChild(int index) {
		return children.get(index);
	}

	public void addChild(PromptBindingNode child) {
		children.add(child);
		child.setParent(this);
	}

	public void addChild(int index, PromptBindingNode child) {
		children.add(index, child);
		child.setParent(this);
	}

	public void removeChild(int index) {
		children.remove(index).setParent(null);
	}

	public void removeChild(PromptBindingNode child) {
		if (children.remove(child))
			child.setParent(null);
	}

	public void clearChildren() {
		for (PromptBindingNode child : children)
			child.setParent(null);
		children.clear();
	}

	/* Read the configuration for this node. */
	@Override
	void readConfiguration(Element configuration) {
		condition = configuration.getAttribute("condition");
		if (condition.length() == 0)
			condition = null;
		NodeList contentList = configuration.getChildNodes();
		for (int i = 0; i < contentList.getLength(); i++)
			if (contentList.item(i) instanceof Element)
				addChild(PromptBindingNode.load((Element) contentList.item(i)));
	}

	/* Write the configuration for this node. */
	@Override
	void writeConfiguration(Element configuration) {
		Element thisElement = configuration.getOwnerDocument().createElement(
				"binding-case");
		configuration.appendChild(thisElement);
		if (condition != null)
			thisElement.setAttribute("condition", condition);
		for (PromptBindingNode child : children)
			child.writeConfiguration(thisElement);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() {
		PromptBindingCase copy = new PromptBindingCase();
		copy.condition = condition;
		for (PromptBindingNode child : children)
			copy.addChild((PromptBindingNode) child.clone());
		return copy;
	}

}
