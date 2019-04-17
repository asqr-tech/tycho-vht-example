package org.eclipse.vtp.desktop.projects.interactive.core.view;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.ui.actions.CopyFilesAndFoldersOperation;
import org.eclipse.ui.actions.MoveFilesAndFoldersOperation;
import org.eclipse.ui.navigator.CommonDropAdapter;
import org.eclipse.ui.navigator.CommonDropAdapterAssistant;
import org.eclipse.vtp.desktop.model.interactive.core.IMediaContainer;
import org.eclipse.vtp.desktop.model.interactive.core.IMediaFile;

public class MediaFileDropAssistant extends CommonDropAdapterAssistant
{

	public MediaFileDropAssistant()
	{
		super();
	}

	@Override
	public IStatus handleDrop(CommonDropAdapter aDropAdapter,
		DropTargetEvent aDropTargetEvent, Object aTarget)
	{
		IMediaContainer mediaContainer = null;
		if(aTarget instanceof IMediaContainer)
			mediaContainer = (IMediaContainer)aTarget;
		else if(aTarget instanceof IMediaFile)
		{
			mediaContainer = ((IMediaFile)aTarget).getParentMediaContainer();
		}
		else
		{
			return Status.CANCEL_STATUS;
		}
		CopyFilesAndFoldersOperation operation = null;
		if(aDropTargetEvent.detail == DND.DROP_MOVE)
			operation = new MoveFilesAndFoldersOperation(this.getShell());
		else
			operation = new CopyFilesAndFoldersOperation(this.getShell());
		Object objs =
			FileTransfer.getInstance()
						.nativeToJava(aDropTargetEvent.currentDataType);
		if(objs instanceof String[])
		{
			String[] files = (String[])objs;
            IContainer container = mediaContainer.getUnderlyingFolder();

            operation.copyFiles(files, container);
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
						List<IResource> toCopy = new LinkedList<IResource>();
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
								toCopy.add(resource);
							}
						}
						operation.copyResources(toCopy.toArray(new IResource[toCopy.size()]), mediaContainer.getUnderlyingFolder());
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
					return Status.OK_STATUS;
				}
			}
		}
		return Status.OK_STATUS;
	}
	
	@Override
	public IStatus validateDrop(Object target, int operation,
		TransferData transferType)
	{
		if(operation != DND.DROP_COPY && operation != DND.DROP_MOVE)
			return Status.CANCEL_STATUS;
		if(!(target instanceof IMediaContainer) && !(target instanceof IMediaFile))
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
		return FileTransfer.getInstance().isSupportedType(
				aTransferType) || super.isSupportedType(aTransferType);
	}
	
	
}
