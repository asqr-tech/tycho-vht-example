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

import java.beans.PropertyChangeEvent;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.vtp.desktop.editors.themes.core.ElementFrame;
import org.eclipse.vtp.desktop.editors.themes.core.Theme;
import org.eclipse.vtp.desktop.editors.themes.core.commands.BeginConnector;
import org.eclipse.vtp.desktop.editors.themes.core.commands.CommandListener;
import org.eclipse.vtp.desktop.editors.themes.core.commands.StartMove;
import org.eclipse.vtp.desktop.model.core.design.IDesignConstants;
import org.eclipse.vtp.desktop.model.core.design.IDesignElement;

/**
 * This is the Mantis theme's implementation of the element frame interface.
 * It follows the typical block format with the element name within the box's
 * bounds.  This frame also displays a secondary tray under the primary box that
 * contains the connector hot spot.  This theme was created with explicit
 * visual support for expansion.
 * 
 * @author trip
 */
public class MantisElementFrame extends MantisComponentFrame implements ElementFrame
{
	/**	The ui element this represents */
	private IDesignElement uiElement;
	/**	Indicates whether this element is selected */
	private boolean selected = false;
	/**	The upper left corner of the area maintained by this frame */
	private Point upperLeft;
	/**	The lower right corner of the area maintained by this frame */
	private Point lowerRight;
	/**	The icon for this element type if one exists */
	Image icon = null;
	
	/**
	 * Creates a new instance that represents the given ui element.
	 * 
	 * @param uiElement The ui element this frame represents
	 */
	public MantisElementFrame(IDesignElement uiElement)
	{
		super(uiElement);
		this.uiElement = uiElement;
		uiElement.addListener(this);
		uiElement.addPropertyListener(this);
		if(uiElement.getIcon() != null)
			icon = uiElement.getIcon();
		else
			icon = org.eclipse.vtp.desktop.core.Activator.getDefault().getImageRegistry().get("ICON_MODULE");
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.editors.core.theme.ElementFrame#alignEdge(int, int)
	 */
	public void alignEdge(int edge, int xy)
	{
		int diffX = 0;
		int diffY = 0;
		if(edge == SWT.LEFT)
		{
			diffX = xy - upperLeft.x;
		}
		else if(edge == SWT.TOP)
		{
			diffY = xy - upperLeft.y;
		}
		else if(edge == SWT.RIGHT)
		{
			diffX = xy - lowerRight.x;
		}
		else if(edge == SWT.BOTTOM)
		{
			diffY = xy - lowerRight.y;
		}
		else
			return;
		adjustPosition(diffX, diffY);
		this.fireChange();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.editors.core.theme.ElementFrame#alignCenter(int, int)
	 */
	public void alignCenter(int orientation, int xy)
	{
		int diffX = 0;
		int diffY = 0;
		Point center = uiElement.getCenterPoint();
		if(orientation == SWT.HORIZONTAL)
		{
			diffY = xy - center.y;
		}
		else if(orientation == SWT.VERTICAL)
		{
			diffX = xy - center.x;
		}
		else
			return;
		adjustPosition(diffX, diffY);
		this.fireChange();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.editors.core.theme.ElementFrame#getUIElement()
	 */
	public IDesignElement getDesignElement()
	{
		return uiElement;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.editors.core.theme.ElementFrame#setSize(int, int)
	 */
	public void setSize(int width, int height)
	{
		this.fireChange();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.editors.core.theme.ComponentFrame#adjustPosition(int, int)
	 */
	public void adjustPosition(int xoffset, int yoffset)
	{
		Point newCenter = new Point(uiElement.getCenterPoint().x + xoffset, uiElement.getCenterPoint().y + yoffset);
		uiElement.setCenterPoint(newCenter);
		upperLeft.x = upperLeft.x + xoffset;
		upperLeft.y = upperLeft.y + yoffset;
		lowerRight.x = lowerRight.x + xoffset;
		lowerRight.y = lowerRight.y + yoffset;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.editors.core.theme.ComponentFrame#getBounds()
	 */
	public Rectangle getBounds()
	{
		if(upperLeft == null)
			return new Rectangle(0, 0, 0, 0);
		return new Rectangle(upperLeft.x, upperLeft.y, lowerRight.x - upperLeft.x, lowerRight.y - upperLeft.y);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.editors.themes.mantis.MantisComponentFrame#mouseDoubleClick(org.eclipse.vtp.desktop.editors.core.commands.CommandListener, int, int, int)
	 */
	public void mouseDoubleClick(CommandListener commandListener, int x, int y, int modifiers)
	{
		super.mouseDoubleClick(commandListener, x, y, modifiers);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.editors.core.theme.ComponentFrame#mouseDown(org.eclipse.vtp.desktop.editors.core.commands.CommandListener, int, int, int)
	 */
	public void mouseDown(CommandListener commandListener, int x, int y, int modifiers)
	{
		if(uiElement.getConnectorRecords().size() > 0 && new Rectangle(lowerRight.x - 17, lowerRight.y - 17, 14, 14).contains(x, y))
		{
			commandListener.executeCommand(new BeginConnector());
		}
		else if(getBounds().contains(x, y))
		{
			commandListener.executeCommand(new StartMove());
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.editors.core.theme.ComponentFrame#mouseMove(org.eclipse.vtp.desktop.editors.core.commands.CommandListener, int, int, int)
	 */
	public void mouseMove(CommandListener commandListener, int x, int y, int modifiers)
	{
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.editors.core.theme.ComponentFrame#mouseUp(org.eclipse.vtp.desktop.editors.core.commands.CommandListener, int, int, int)
	 */
	public void mouseUp(CommandListener commandListener, int x, int y, int modifiers)
	{
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.editors.themes.mantis.MantisComponentFrame#renderFrame(org.eclipse.swt.graphics.GC, int, int, java.util.Map)
	 */
	public void renderFrame(GC gc, int renderingPhase, int options, Map<String, Object> resourceMap)
	{
		Font originalFont = gc.getFont();
		Color foreground = gc.getForeground();
		Color background = gc.getBackground();
		Color selectedColor = getColor(gc, resourceMap, "rust.selection.color", 82, 140, 55);
		Color elementBlue = getColor(gc, resourceMap, "rust.element.color", 207, 234, 195);
		Color elementGradBlue = getColor(gc, resourceMap, "rust.element.color.gradient", 161, 211, 137);
		Font nameFont = new Font(gc.getDevice(), "Arial", 10, SWT.NORMAL);
		gc.setFont(nameFont);
		if(upperLeft == null)
		{
			initializeGraphics(gc, resourceMap);
		}
		int width = lowerRight.x - upperLeft.x;
		int height = lowerRight.y - upperLeft.y;
		int mainBodyHeight = height - (uiElement.hasConnectors() ? 12 : 0);
		
		if(uiElement.hasConnectors())
		{
			//draw inspector tray
			gc.setBackground(elementBlue);
			gc.fillRoundRectangle(upperLeft.x, upperLeft.y + 3, width - 1, height - 3, 20, 20);
	
			//draw inspector tray border
			if(selected && (options & Theme.RENDER_FLAG_NO_SELECTION) == 0)
			{
				gc.setForeground(selectedColor);
			}
			gc.drawRoundRectangle(upperLeft.x, upperLeft.y + 3, width - 1, height - 3, 20, 20);
		}

		//draw main body
		gc.setBackground(elementBlue);
		gc.fillRoundRectangle(upperLeft.x, upperLeft.y, width - 1, mainBodyHeight - 1, 20, 20);
		gc.setBackground(elementGradBlue);
		gc.fillRoundRectangle(upperLeft.x + 1,
			upperLeft.y
			+ (mainBodyHeight / 2) - 1,
			lowerRight.x - upperLeft.x - 2,
			(mainBodyHeight / 2) + (mainBodyHeight % 2), 20, 20);
//		gc.setForeground(elementBlue);
//		gc.fillGradientRectangle(upperLeft.x + 2,
//			upperLeft.y
//			+ (mainBodyHeight / 3),
//			lowerRight.x - upperLeft.x - 4,
//			((mainBodyHeight / 3) * 2) - 5,
//			true);
		gc.setForeground(foreground);
		gc.setBackground(background);
		
		//draw main body border
		if(selected && (options & Theme.RENDER_FLAG_NO_SELECTION) == 0)
		{
			gc.setForeground(selectedColor);
		}
		gc.drawRoundRectangle(upperLeft.x, upperLeft.y, width - 1, mainBodyHeight - 1, 20, 20);
		
		gc.setForeground(foreground);
		//draw connector hot spot
		if(uiElement.hasConnectors())
		{
			gc.drawLine(lowerRight.x - 19, lowerRight.y - 8, lowerRight.x - 5, lowerRight.y - 8);
//			gc.drawLine(lowerRight.x - 8, lowerRight.y - 15, lowerRight.x - 3, lowerRight.y - 10);
			gc.drawLine(lowerRight.x - 10, lowerRight.y - 3, lowerRight.x - 5, lowerRight.y - 8);
		}
		
		//draw icon
		if((options & Theme.RENDER_FLAG_NO_ICONS) == 0)
		{
			gc.drawImage(icon, upperLeft.x + 5, upperLeft.y + (((mainBodyHeight - 16) / 2) + ((mainBodyHeight - 16) % 2)));
		}

		//draw element name
		int curX = upperLeft.x + 25;
		int curY = upperLeft.y + 5;
		String[] parts = this.getDesignElement().getName().split(" ");
		Point stringExtent = gc.stringExtent(parts[0]);
		int ew = stringExtent.x;
		gc.drawString(parts[0], curX, curY, true);
		curX += stringExtent.x;
		for(int i = 1; i < parts.length; i++)
		{
			stringExtent = gc.stringExtent(" " + parts[i]);
			boolean wrapped = false;
			if(ew + stringExtent.x > 110) //wrap it
			{
				stringExtent = gc.stringExtent(parts[i]);
				ew = stringExtent.x;
				curY += 2 + stringExtent.y;
				curX = upperLeft.x + 25;
				wrapped = true;
			}
			else
				ew += stringExtent.x;
			gc.drawString((wrapped ? "" : " ") + parts[i], curX, curY, true);
			curX += stringExtent.x;
		}
		
		//draw decorator icons
		if((options & Theme.RENDER_FLAG_NO_MARKERS) == 0)
		{
			if(uiElement.hasErrors())
				gc.drawImage(org.eclipse.vtp.desktop.core.Activator.getDefault().getImageRegistry().get("ICON_ERROR"), lowerRight.x - 17, upperLeft.y);
			else if(uiElement.hasWarnings())
				gc.drawImage(org.eclipse.vtp.desktop.core.Activator.getDefault().getImageRegistry().get("ICON_WARNING"), lowerRight.x - 16, upperLeft.y);
			else if(uiElement.hasTodo())
				gc.drawImage(org.eclipse.vtp.desktop.core.Activator.getDefault().getImageRegistry().get("ICON_TASK"), lowerRight.x - 18, upperLeft.y + 2);
		}
		
		//resource cleanup
		if(selected)
		{
			gc.setForeground(foreground);
		}
		gc.setFont(originalFont);
		nameFont.dispose();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.editors.core.theme.ComponentFrame#setSelected(boolean)
	 */
	public void setSelected(boolean selected)
	{
		this.selected = selected;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.editors.core.theme.ComponentFrame#touchesComponent(int, int)
	 */
	public boolean touchesComponent(int x, int y)
	{
		if(upperLeft == null)
			return false;
		return new Rectangle(upperLeft.x, upperLeft.y, lowerRight.x - upperLeft.x, lowerRight.y - upperLeft.y).contains(x, y);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.editors.core.theme.ElementFrame#touchesElement(org.eclipse.swt.graphics.Rectangle)
	 */
	public boolean touchesElement(Rectangle rect)
	{
		if(upperLeft == null)
			return false;
		return new Rectangle(upperLeft.x, upperLeft.y, lowerRight.x - upperLeft.x, lowerRight.y - upperLeft.y).intersects(rect);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.editors.core.theme.ElementFrame#getIncursionPoint(int, int)
	 */
	public Point getIncursionPoint(int x, int y)
	{
		if((upperLeft == null) || (lowerRight == null))
		{
			return null;
		}

		Point p1 = new Point(x, y);
		Point p2 = new Point(uiElement.getCenterPoint().x, uiElement.getCenterPoint().y);
		Point ret =	getIntersection(p1, p2,
				new Point(upperLeft.x, upperLeft.y),
				new Point(lowerRight.x, upperLeft.y));

		if(ret == null)
		{
			ret = getIntersection(p1, p2,
					new Point(lowerRight.x, upperLeft.y),
					new Point(lowerRight.x, lowerRight.y));
		}

		if(ret == null)
		{
			ret = getIntersection(p1, p2,
					new Point(upperLeft.x, upperLeft.y),
					new Point(upperLeft.x, lowerRight.y));
		}

		if(ret == null)
		{
			ret = getIntersection(p1, p2,
					new Point(upperLeft.x, lowerRight.y),
					new Point(lowerRight.x, lowerRight.y));
		}

		return ret;
	}

	/**
	 * Calculates the point that the lines described by the four given points
	 * intersect.  This point is used to place the point of the arrow head of
	 * incoming connectors and where the connector should begin drawing if this
	 * element is its source.
	 * 
	 * @param p1 The source point of the first line 
	 * @param p2 The destination point of the first line
	 * @param p3 The source point of the second line
	 * @param p4 The destination point of the second line
	 * @return The point the two lines intersect or null if the lines don't touch
	 */
	protected Point getIntersection(Point p1, Point p2, Point p3, Point p4)
	{
		double x1 = p1.x;
		double y1 = p1.y;
		double x2 = p2.x;
		double y2 = p2.y;
		double x3 = p3.x;
		double y3 = p3.y;
		double x4 = p4.x;
		double y4 = p4.y;
		double ix;
		double iy;
		double uan = ((x4 - x3) * (y1 - y3)) - ((y4 - y3) * (x1 - x3));
		double ud = ((y4 - y3) * (x2 - x1)) - ((x4 - x3) * (y2 - y1));

		if(ud == 0)
		{
			return null; //lines are either parallel or coincident
		}

		double ua = uan / ud;
		double ubn = ((x2 - x1) * (y1 - y3)) - ((y2 - y1) * (x1 - x3));
		double ub = ubn / ud;

		if((ua <= 0) || (ua >= 1) || (ub <= 0) || (ub >= 1))
		{
			return null;
		}

		ix = x1 + (ua * (x2 - x1));
		iy = y1 + (ua * (y2 - y1));

		return new Point((int)ix, (int)iy);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.editors.core.theme.ComponentFrame#initializeGraphics(org.eclipse.swt.graphics.GC, java.util.Map)
	 */
	public void initializeGraphics(GC gc, Map<String, Object> resourceMap)
	{
		Font originalFont = gc.getFont();
		Font nameFont = new Font(gc.getDevice(), "Arial", 10, SWT.NORMAL);
		gc.setFont(nameFont);
		Point extent = gc.stringExtent(this.getDesignElement().getName());
		if(extent.x > 110) //too long, attempt to wrap text
		{
			extent.x = 0;
			String[] parts = this.getDesignElement().getName().split(" ");
			Point stringExtent = gc.stringExtent(parts[0]);
			int ew = 0;
			ew += stringExtent.x;
			extent.x = stringExtent.x;
			for(int i = 1; i < parts.length; i++)
			{
				stringExtent = gc.stringExtent(" " + parts[i]);
				if(ew + stringExtent.x > 110) //wrap it
				{
					stringExtent = gc.stringExtent(parts[i]);
					if(extent.x < ew)
						extent.x = ew;
					ew = stringExtent.x;
					extent.y += 2 + stringExtent.y;
				}
				else
					ew += stringExtent.x;
			}
			if(extent.x < ew)
				extent.x = ew;
		}
		int width = (extent.x + 35);
		int height = (extent.y + 10 + (uiElement.hasConnectors() ? 12 : 0));
		Point centerPoint = uiElement.getCenterPoint();
		upperLeft = new Point(centerPoint.x - (width / 2), centerPoint.y - (height / 2));
		int buffer = width % 2;
		lowerRight = new Point(upperLeft.x + width + buffer, upperLeft.y + height + buffer);
		gc.setFont(originalFont);
		nameFont.dispose();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.editors.themes.mantis.MantisComponentFrame#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent event)
	{
		if(IDesignConstants.PROP_NAME.equals(event.getPropertyName()))
		{
			upperLeft = null;
			this.fireRepaintRequest();
		}
		else
			super.propertyChange(event);
	}
}
