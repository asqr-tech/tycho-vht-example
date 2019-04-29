package org.eclipse.vtp.desktop.model.interactive.core.configuration.generic;

import org.w3c.dom.Element;

/**
 * Base class for nodes in a prompt binding item.
 * 
 * @author Lonnie Pryor
 */
public abstract class PromptBindingNode implements Cloneable {
	
	public static PromptBindingNode load(Element configuration) {
		PromptBindingNode result = null;
		if ("binding-case".equals(configuration.getTagName()))
			result = new PromptBindingCase();
		else if ("binding-branch".equals(configuration.getTagName()))
			result = new PromptBindingSwitch();
		else
			result = new PromptBindingEntry();
		result.readConfiguration(configuration);
		return result;
	}
	
	/** The parent of this entry. */
	private PromptBindingNode parent = null;

	/**
	 * @return The parent object of this entry
	 */
	public PromptBindingNode getParent() {
		return parent;
	}

	/**
	 * Sets the parent of this entry. Any previous content
	 * is forgotten.
	 * 
	 * @param parent
	 *            The new parent of this entry
	 */
	void setParent(PromptBindingNode parent) {
		this.parent = parent;
	}
	
	public abstract Object clone();
	
	/** Reads the configuration for this node. */
	abstract void readConfiguration(Element configuration);
	
	/** Writes the configuration for this node. */
	abstract void writeConfiguration(Element configuration);

}
