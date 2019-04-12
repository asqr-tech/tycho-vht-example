package org.eclipse.vtp.modules.standard.ui.actions;

import org.eclipse.vtp.desktop.editors.core.actions.DesignElementAction;
import org.eclipse.vtp.desktop.editors.themes.core.commands.CommandListener;
import org.eclipse.vtp.desktop.editors.themes.core.commands.LocateElement;
import org.eclipse.vtp.desktop.model.core.design.IDesignElement;

public class JumpToPortalEntryAction extends DesignElementAction
{
	String entryId = "";

	/**
	 * @param element
	 * @param commandListener
	 * @param canvasId
	 * @param entriesOnCanvas
	 */
	public JumpToPortalEntryAction(IDesignElement element, CommandListener commandListener, String designId, int entriesOnCanvas)
	{
		super(element, commandListener);
		//TODO implement this action
//		String canvasName = "";
//		List elementsList = new ArrayList();
//		
//		List uiCanvases = ((BasicController)commandListener).getRenderedCanvas().getUICanvas().getUIModel().listUICanvases();
//		for(int b = 0; b < uiCanvases.size(); b++)
//		{
//			if(((UICanvas)uiCanvases.get(b)).getId().equals(canvasId))
//			{
//				canvasName = ((UICanvas)uiCanvases.get(b)).getName();
//				elementsList = ((UICanvas)uiCanvases.get(b)).listUIElements();
//				break;
//			}
//		}
//		
//		for(int b = 0; b < elementsList.size(); b++)
//		{
//			if((((UIElement)elementsList.get(b)).getElement() instanceof PrimitiveElement) && ((PrimitiveElement)((UIElement)elementsList.get(b)).getElement()).getSubTypeId().equals("org.eclipse.vtp.desktop.editors.core.portalEntry") && ((PortalEntryInformationProvider)((PrimitiveElement)((UIElement)elementsList.get(b)).getElement()).getInformationProvider()).getExitId().equals(element.getId()))
//			{
//				entryId = ((UIElement)elementsList.get(b)).getElement().getId();
//			}
//		}
//		this.setText("Jump to Canvas " + canvasName + " (" + entriesOnCanvas + " Entries)");
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#run()
	 */
	public void run()
	{
		try
		{
			getCommandListener().executeCommand(new LocateElement(entryId));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}