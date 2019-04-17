package org.eclipse.vtp.desktop.editors.core.widgets;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

public class RowDivider extends Canvas implements PaintListener
{
	/**
	 * @param parent
	 * @param style
	 */
	public RowDivider(Composite parent, int style)
	{
		super(parent, style);
		this.addPaintListener(this);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.PaintListener#paintControl(org.eclipse.swt.events.PaintEvent)
	 */
	public void paintControl(PaintEvent e)
	{
		Point size = getSize();
		e.gc.drawLine(30, 1, size.x - 30, 1);
	}
}
