/*--------------------------------------------------------------------------
 * Copyright (c) 2004, 2006-2009 OpenMethods, LLC
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Trip Gilman (OpenMethods), Lonnie G. Pryor (OpenMethods)
 *    - initial API and implementation
 -------------------------------------------------------------------------*/
package org.eclipse.vtp.desktop.editors.themes.mantis;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.vtp.desktop.editors.themes.core.CanvasFrame;
import org.eclipse.vtp.desktop.model.core.design.IDesign;

/**
 * This is the Mantis theme's implementation of the canvas frame interface.
 * 
 * @author trip
 */
public class MantisCanvasFrame implements CanvasFrame
{
	/**	The ui canvas instance this frame represents */
	private IDesign uiCanvas;
	/**	The current bounds of this canvas frame */
	private Rectangle bounds = new Rectangle(0, 0, 0, 0);
	
	/**
	 * Creates a new <code>MantisCanvasFrame</code> instance that represents the
	 * the given ui canvas object.
	 * 
	 * @param uiCanvas The ui canvas represented by this canvas frame
	 */
	public MantisCanvasFrame(IDesign uiCanvas)
	{
		super();
		this.uiCanvas = uiCanvas;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.editors.core.theme.CanvasFrame#getInsets()
	 */
	public Rectangle getInsets()
	{
		return new Rectangle(1, 1, 1, 1);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.editors.core.theme.CanvasFrame#setBounds(org.eclipse.swt.graphics.Rectangle)
	 */
	public void setBounds(Rectangle rect)
	{
		this.bounds = rect;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.editors.core.theme.ThematicFrame#renderFrame(org.eclipse.swt.graphics.GC, int, int, java.util.Map)
	 */
	public void renderFrame(GC gc, int stage, int renderFlags, Map<String, Object> resourceMap)
	{
		Color lightGray = (Color)resourceMap.get("LIGHT_GRAY");
		if(lightGray == null)
		{
			lightGray = new Color(gc.getDevice(), 204, 204, 204);
			resourceMap.put("LIGHT_GRAY", lightGray);
		}
		Color darkGray = (Color)resourceMap.get("DARK_GRAY");
		if(darkGray == null)
		{
			darkGray = new Color(gc.getDevice(), 128, 128, 128);
			resourceMap.put("DARK_GRAY", darkGray);
		}
		int w = uiCanvas.getWidth();
		int h = uiCanvas.getHeight();
		Color foreground = gc.getForeground();
		Color background = gc.getBackground();
		gc.setForeground(lightGray);
		gc.setBackground(darkGray);
		gc.fillGradientRectangle(bounds.x, bounds.y, bounds.width, bounds.height, true);
		gc.setForeground(gc.getDevice().getSystemColor(SWT.COLOR_BLACK));
		Rectangle rect = new Rectangle(((bounds.width - w) / 2) - 1, ((bounds.height - h) / 2) - 1, w + 1, h + 1);
		gc.drawRectangle(rect);
		gc.setBackground(foreground);
		rect.x += 3;
		rect.y += 3;
		gc.fillRectangle(rect);
		gc.setForeground(foreground);
		gc.setBackground(background);
	}

}
