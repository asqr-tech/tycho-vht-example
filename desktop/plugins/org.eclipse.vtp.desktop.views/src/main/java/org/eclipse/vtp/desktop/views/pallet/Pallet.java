package org.eclipse.vtp.desktop.views.pallet;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.vtp.desktop.model.core.design.IDesign;

public interface Pallet
{
	/**
	 * Sets the container this pallet is mapped to.
	 * 
	 * @param container The container this pallet is mapped to.
	 */
	public void setContainer(IDesign container);

	/**
	 * Creates the control used for this pallet.
	 * 
	 * @param parent The parent element that contains this pallet.
	 */
	public void createControl(Composite parent);

	/**
	 * Returns the control used for this pallet.
	 * 
	 * @return The control used for this pallet.
	 */
	public Control getControl();

	/**
	 * Disposes this pallet.
	 */
	public void destroy();

}
