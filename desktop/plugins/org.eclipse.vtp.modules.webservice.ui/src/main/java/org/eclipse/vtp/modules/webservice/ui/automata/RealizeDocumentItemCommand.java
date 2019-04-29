package org.eclipse.vtp.modules.webservice.ui.automata;

import org.eclipse.vtp.modules.webservice.ui.configuration.document.DocumentItem;

public class RealizeDocumentItemCommand extends Command
{
	private DocumentItem item = null;
	private boolean isValid = true;

	public RealizeDocumentItemCommand(DocumentItem item, boolean isValid)
	{
		super();
		this.item = item;
		this.isValid = isValid;
	}

	public DocumentItem getDocumentItem()
	{
		return item;
	}
	
	public boolean isValid()
	{
		return isValid;
	}
}
