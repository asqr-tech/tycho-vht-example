/**
 * 
 */
package org.eclipse.vtp.desktop.model.core.internal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.vtp.desktop.model.core.IDesignDocument;
import org.eclipse.vtp.desktop.model.core.IDesignFolder;
import org.eclipse.vtp.desktop.model.core.IDesignItemContainer;
import org.eclipse.vtp.desktop.model.core.IWorkflowResource;
import org.eclipse.vtp.framework.util.Guid;

/**
 * @author trip
 *
 */
public abstract class DesignItemContainer extends WorkflowResource implements
	IDesignItemContainer
{
	private static final String HASHPREFIX = "DesignItemContainer";
	private IFolder folder = null;

	/**
	 * 
	 */
	public DesignItemContainer(IFolder folder)
	{
		super();
		this.folder = folder;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.internal.WorkflowResource#getObjectId()
	 */
	@Override
	protected String getObjectId()
	{
		return folder.getFullPath().toPortableString();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.IDesignItemContainer#createDesignDocument(java.lang.String)
	 */
	public IDesignDocument createDesignDocument(String name)
	{
		IDesignDocument document = null;
		try
		{
			String fileName = name + ".canvas";
			IFile designDocument = folder.getFile(fileName);
			InputStream templateIn = this.getClass().getClassLoader().getResourceAsStream("design_document_template.xml");
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buf = new byte[10240];
			int len = templateIn.read(buf);
			while(len != -1)
			{
				baos.write(buf, 0, len);
				len = templateIn.read(buf);
			}
			templateIn.close();
			String template = baos.toString();
			template = template.replaceAll("\\[\\[flow_id\\]\\]", Guid.createGUID());
			template = template.replaceAll("\\[\\[portal_id\\]\\]", Guid.createGUID());
			template = template.replaceAll("\\[\\[portal_name\\]\\]", name);
			template = template.replaceAll("\\[\\[canvas_name\\]\\]", name);
			
			document = new DesignDocument(this, designDocument);
			designDocument.create(new ByteArrayInputStream(template.getBytes()), true, null);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return document;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.IDesignItemContainer#createDesignFolder(java.lang.String)
	 */
	public IDesignFolder createDesignFolder(String name)
	{
		DesignFolder designFolder = null;
		IFolder newFolder = folder.getFolder(name);
		if(newFolder.exists())
		{
			throw new IllegalArgumentException("Folder with that name already exists");
		}
		try
		{
			designFolder = new DesignFolder(this, newFolder);
			newFolder.create(true, true, null);
		}
		catch (CoreException e)
		{
			e.printStackTrace();
		}
		return designFolder;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.IDesignItemContainer#deleteDesignDocument(org.eclipse.vtp.desktop.model.core.IDesignDocument)
	 */
	public void deleteDesignDocument(IDesignDocument document)
	{
		try
		{
			document.getUnderlyingFile().delete(true, null);
		}
		catch (CoreException e)
		{
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.IDesignItemContainer#deleteDesignDocument(java.lang.String)
	 */
	public void deleteDesignDocument(String name)
	{
		IDesignDocument document = getDesignDocument(name);
		if(document != null)
		{
			try
			{
				document.getUnderlyingFile().delete(true, null);
			}
			catch (CoreException e)
			{
				e.printStackTrace();
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.IDesignItemContainer#deleteDesignFolder(org.eclipse.vtp.desktop.model.core.IDesignFolder)
	 */
	public void deleteDesignFolder(IDesignFolder designFolder)
	{
		try
		{
			designFolder.getUnderlyingFolder().delete(true, null);
		}
		catch (CoreException e)
		{
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.IDesignItemContainer#deleteDesignFolder(java.lang.String)
	 */
	public void deleteDesignFolder(String name)
	{
		IDesignFolder designFolder = getDesignFolder(name);
		if(designFolder != null)
		{
			try
			{
				designFolder.getUnderlyingFolder().delete(true, null);
			}
			catch (CoreException e)
			{
				e.printStackTrace();
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.IDesignItemContainer#getDesignDocument(java.lang.String)
	 */
	public IDesignDocument getDesignDocument(String name)
	{
		try
		{
			for(IResource child : folder.members())
			{
				if(child.getType() == IResource.FILE && child.getName().equals(name))
				{
					DesignDocument designDocument = new DesignDocument(this, (IFile)child);
					return designDocument;
				}
			}
		}
		catch (CoreException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.IDesignItemContainer#getDesignDocuments()
	 */
	public List<IDesignDocument> getDesignDocuments()
	{
		List<IDesignDocument> documents = new ArrayList<IDesignDocument>();
		try
		{
			for(IResource child : folder.members())
			{
				if(child.getType() == IResource.FILE && child.getFileExtension() != null && child.getFileExtension().equals("canvas"))
				{
					DesignDocument dd = new DesignDocument(this, (IFile)child);
					documents.add(dd);
				}
			}
		}
		catch (CoreException e)
		{
			e.printStackTrace();
		}
		return documents;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.IDesignItemContainer#getDesignFolder(java.lang.String)
	 */
	public IDesignFolder getDesignFolder(String name)
	{
		try
		{
			for(IResource child : folder.members())
			{
				if(child.getType() == IResource.FOLDER && child.getName().equals(name))
				{
					DesignFolder designFolder = new DesignFolder(this, (IFolder)child);
					return designFolder;
				}
			}
		}
		catch (CoreException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.IDesignItemContainer#getDesignFolders()
	 */
	public List<IDesignFolder> getDesignFolders()
	{
		List<IDesignFolder> folders = new ArrayList<IDesignFolder>();
		try
		{
			for(IResource child : folder.members())
			{
				if(child.getType() == IResource.FOLDER)
				{
					DesignFolder designFolder = new DesignFolder(this, (IFolder)child);
					folders.add(designFolder);
				}
			}
		}
		catch (CoreException e)
		{
			e.printStackTrace();
		}
		return folders;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.IWorkflowResourceContainer#getChildren()
	 */
	public List<IWorkflowResource> getChildren()
	{
		List<IWorkflowResource> children = new ArrayList<IWorkflowResource>();
		children.addAll(getDesignDocuments());
		children.addAll(getDesignFolders());
		return children;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.IWorkflowResource#getName()
	 */
	public String getName()
	{
		return folder.getName();
	}
	
	public IFolder getUnderlyingFolder()
	{
		return folder;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.internal.WorkflowResource#getAdapter(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapterClass)
	{
		if(IResource.class.isAssignableFrom(adapterClass) && adapterClass.isAssignableFrom(folder.getClass()))
			return folder;
		return super.getAdapter(adapterClass);
	}
	
	public boolean equals(Object obj)
	{
		if(obj instanceof DesignItemContainer)
		{
			return folder.equals(((DesignItemContainer)obj).getUnderlyingFolder());
		}
		return false;
	}
	
	public int hashCode()
	{
		return (HASHPREFIX + folder.toString()).hashCode();
	}
}
