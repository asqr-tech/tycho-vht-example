/**
 * 
 */
package org.eclipse.vtp.modules.interactive.ui.views;

import org.eclipse.vtp.desktop.model.core.design.IDesign;
import org.eclipse.vtp.desktop.model.elements.core.internal.views.PalletItemFilter;
import org.eclipse.vtp.desktop.model.interactive.core.IInteractiveWorkflowProject;

/**
 * @author trip
 *
 */
public class InteractiveOnlyPalletItemFilter implements PalletItemFilter
{

	/**
	 * 
	 */
	public InteractiveOnlyPalletItemFilter()
	{
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.elements.core.internal.views.PalletItemFilter#canBeContainedBy(org.eclipse.vtp.desktop.model.core.design.IDesign)
	 */
	public boolean canBeContainedBy(IDesign design)
	{
		return design.getDocument().getProject() instanceof IInteractiveWorkflowProject;
	}

}
