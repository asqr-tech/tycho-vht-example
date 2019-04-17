package org.eclipse.vtp.desktop.model.core;

import org.eclipse.vtp.desktop.model.core.design.IDesign;

public interface IDesignDocumentListener
{
	public void dialogDesignAdded(IDesignDocument designDocument, IDesign dialogDesign);
	
	public void dialogDesignRemoved(IDesignDocument designDocument, String dialogId);
}
