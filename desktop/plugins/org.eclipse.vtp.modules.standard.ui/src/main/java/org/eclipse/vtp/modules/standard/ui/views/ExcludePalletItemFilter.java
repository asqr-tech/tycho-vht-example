/**
 * 
 */
package org.eclipse.vtp.modules.standard.ui.views;

import org.eclipse.vtp.desktop.model.core.design.IDesign;
import org.eclipse.vtp.desktop.model.elements.core.internal.views.PalletItemFilter;

/**
 * @author trip
 *
 */
public class ExcludePalletItemFilter implements PalletItemFilter
{

	/**
	 * 
	 */
	public ExcludePalletItemFilter()
	{
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.elements.core.internal.views.PalletItemFilter#canBeContainedBy(org.eclipse.vtp.desktop.model.core.design.IDesign)
	 */
	public boolean canBeContainedBy(IDesign design)
	{
		return false;
	}

}
