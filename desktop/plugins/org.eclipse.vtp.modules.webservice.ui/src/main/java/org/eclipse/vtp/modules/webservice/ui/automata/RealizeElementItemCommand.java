package org.eclipse.vtp.modules.webservice.ui.automata;

import org.eclipse.vtp.modules.webservice.ui.configuration.document.DocumentItem;

public class RealizeElementItemCommand extends RealizeDocumentItemCommand
{
	private boolean textOnly = false;

	public RealizeElementItemCommand(DocumentItem item, boolean isValid)
	{
		super(item, isValid);
	}
	
	public boolean isTextOnly()
	{
		return textOnly;
	}
	
	public void setTextOnly(boolean textOnly)
	{
		this.textOnly = textOnly;
	}

}
