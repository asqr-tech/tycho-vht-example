package org.eclipse.vtp.modules.webservice.ui.automata;

import org.eclipse.vtp.desktop.model.core.schema.ElementItem;

public class ElementSuggestionCommand extends SuggestionCommand
{
	private ElementItem element;

	public ElementSuggestionCommand(ElementItem element)
	{
		super();
		this.element = element;
	}

	public ElementItem getElement()
	{
		return element;
	}
}
