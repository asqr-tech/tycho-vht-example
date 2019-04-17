package org.eclipse.vtp.modules.webservice.ui.automata;

public class SuggestionCommand extends Command
{
	private boolean required = false;

	public SuggestionCommand()
	{
	}

	public boolean isRequired()
	{
		return required;
	}
	
	public void setRequired(boolean required)
	{
		this.required = required;
	}
}
