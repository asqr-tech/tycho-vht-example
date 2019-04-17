package org.eclipse.vtp.desktop.projects.core.view;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.ide.ResourceUtil;
import org.eclipse.ui.navigator.ILinkHelper;
import org.eclipse.ui.part.FileEditorInput;

public class WorkflowLinkHelper implements ILinkHelper
{

	public void activateEditor(IWorkbenchPage aPage,
		IStructuredSelection aSelection)
	{
		System.out.println("in link helper");
		if (aSelection == null || aSelection.isEmpty() || !(aSelection.getFirstElement() instanceof IAdaptable))
			return;
		IFile file = (IFile)((IAdaptable)aSelection.getFirstElement()).getAdapter(IFile.class);
		if (file != null)
		{
			IEditorInput fileInput = new FileEditorInput(file);
			IEditorPart editor = null;
			if ((editor = aPage.findEditor(fileInput)) != null)
				aPage.bringToTop(editor);
		}
	}

	public IStructuredSelection findSelection(IEditorInput anInput)
	{
		IFile file = ResourceUtil.getFile(anInput);
		if (file != null) {
			return new StructuredSelection(file);
		}
		return StructuredSelection.EMPTY;
	}

}
