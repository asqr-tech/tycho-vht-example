package org.eclipse.vtp.desktop.model.core;

import java.util.List;

import org.eclipse.core.resources.IFolder;

public interface IDesignItemContainer extends IWorkflowResourceContainer
{
	public IDesignFolder getDesignFolder(String name);
	
	public IDesignFolder createDesignFolder(String name);
	
	public List<IDesignFolder> getDesignFolders();
	
	public void deleteDesignFolder(IDesignFolder designFolder);
	
	public void deleteDesignFolder(String name);
	
	public IDesignDocument getDesignDocument(String name);
	
	public IDesignDocument createDesignDocument(String name);
	
	public List<IDesignDocument> getDesignDocuments();
	
	public void deleteDesignDocument(IDesignDocument document);
	
	public void deleteDesignDocument(String name);

	public IFolder getUnderlyingFolder();
}
