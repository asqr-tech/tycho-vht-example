package org.eclipse.vtp.desktop.projects.core.view;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.ui.actions.CopyFilesAndFoldersOperation;
import org.eclipse.ui.navigator.CommonDropAdapter;
import org.eclipse.ui.navigator.CommonDropAdapterAssistant;
import org.eclipse.vtp.desktop.model.core.IDependency;
import org.eclipse.vtp.desktop.model.core.IDependencySet;

public class DependencyDropAssistant extends CommonDropAdapterAssistant
{

	public DependencyDropAssistant()
	{
		super();
	}

	@Override
	public IStatus handleDrop(CommonDropAdapter aDropAdapter,
		DropTargetEvent aDropTargetEvent, Object aTarget)
	{
		IDependencySet dependencySet = null;
		if(aTarget instanceof IDependencySet)
			dependencySet = (IDependencySet)aTarget;
		else if(aTarget instanceof IDependency)
		{
			dependencySet = ((IDependency)aTarget).getParent();
		}
		else
		{
			return Status.CANCEL_STATUS;
		}
		Object objs =
			FileTransfer.getInstance()
						.nativeToJava(aDropTargetEvent.currentDataType);
		if(objs instanceof String[])
		{
			System.out.println("is file transfer");
			String[] files = (String[])objs;
			for(String filePath : files)
			{
				File f = new File(filePath);
				if(f.exists())
				{
					try
					{
						dependencySet.createDependency(f.getName(), new FileInputStream(f));
					}
					catch (FileNotFoundException e)
					{
						e.printStackTrace();
					}
				}
			}
			return Status.OK_STATUS;
		}
		if(objs == null)
		{
			for(TransferData type : aDropTargetEvent.dataTypes)
			{
				if(LocalSelectionTransfer.getTransfer().isSupportedType(type))
				{
					System.out.println("is local selection");
					try
					{
						IStructuredSelection selection = (IStructuredSelection)LocalSelectionTransfer.getTransfer().getSelection();
						List<IFile> toCopy = new LinkedList<IFile>();
						for(Object obj : selection.toList())
						{
							IResource resource = null;
							if(obj instanceof IResource)
								resource = (IResource)obj;
							else if(obj instanceof IAdaptable)
							{
								resource = (IResource)((IAdaptable)obj).getAdapter(IResource.class);
							}
							if(resource != null)
							{
								addResource(resource, toCopy);
							}
						}
						CopyFilesAndFoldersOperation operation = new CopyFilesAndFoldersOperation(
							this.getShell());
						operation.copyResources(toCopy.toArray(new IResource[toCopy.size()]), dependencySet.getUnderlyingFolder());
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
					return Status.OK_STATUS;
				}
			}
		}
		return Status.CANCEL_STATUS;
	}
	
	private void addResource(IResource resource, List<IFile> toCopy) throws CoreException
	{
		if(resource instanceof IFile)
			toCopy.add((IFile)resource);
		else if(resource instanceof IFolder)
		{
			IFolder folder = (IFolder)resource;
			IResource[] members = folder.members();
			for(IResource member : members)
			{
				addResource(member, toCopy);
			}
		}
	}

	@Override
	public IStatus validateDrop(Object target, int operation,
		TransferData transferType)
	{
		System.out.println("in validate transfer drop");
		if(!(target instanceof IDependencySet) && !(target instanceof IDependency))
		{
			return Status.CANCEL_STATUS;
		}
		if(FileTransfer.getInstance().isSupportedType(transferType))
			return Status.OK_STATUS;
		if(LocalSelectionTransfer.getTransfer().isSupportedType(transferType))
		{
			IStructuredSelection selection = (IStructuredSelection)LocalSelectionTransfer.getTransfer().getSelection();
			for(Object obj : selection.toList())
			{
				System.out.println("selection object: " + obj);
				IResource resource = null;
				if(obj instanceof IResource)
					resource = (IResource)obj;
				else if(obj instanceof IAdaptable)
				{
					resource = (IResource)((IAdaptable)obj).getAdapter(IResource.class);
				}
				if(resource == null)
				{
					return Status.CANCEL_STATUS;
				}
			}
			return Status.OK_STATUS;
		}
		return Status.CANCEL_STATUS;
	}

	public boolean isSupportedType(TransferData aTransferType)
	{
		System.out.println("is supported type");
		return FileTransfer.getInstance().isSupportedType(
				aTransferType) || super.isSupportedType(aTransferType);
	}
}
