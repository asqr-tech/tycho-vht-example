/*--------------------------------------------------------------------------
 * Copyright (c) 2010 OpenMethods, LLC
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Trip Gilman (OpenMethods)
 *    - initial API and implementation
 -------------------------------------------------------------------------*/
package org.eclipse.vtp.desktop.core.custom;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

/**
 * This class provides a simple UI widget that presents a round button with a
 * text character inside.  It operates as a toggle button changing color based
 * on its current state.  It can be placed into a mode that only allows it to
 * be toggled on/down by the user, requiring programmatic intervention to
 * turn it off.  This allows a group of these buttons to operate as a set of
 * radio buttons.
 * 
 * @author trip
 */
public class ToggleButton extends Canvas implements PaintListener, MouseListener, MouseTrackListener
{
	/**	Indicates whether this button is toggled on or off */
	private boolean selected = false;
	/**	Indicates whether this button can only be toggled on by the user */
	private boolean toggleDownOnly = false;
	/**	Indicates whether the mouse is currently over this button */
	private boolean over = false;
	/**	The text character to display as a label for this button */
	private String text = "";
	/**	List of currently registered listeners */
	private List<ToggleButtonListener> listeners = new LinkedList<ToggleButtonListener>();
	
	/**
	 * Constructs a new <code>ToggleButton</code> with the given composite as
	 * it's parent.
	 * 
	 * @param parent The parent of this widget
	 */
	public ToggleButton(Composite parent)
	{
		super(parent, SWT.NONE);
		this.addPaintListener(this);
		this.addMouseListener(this);
		this.addMouseTrackListener(this);
		this.setCursor(parent.getDisplay().getSystemCursor(SWT.CURSOR_HAND));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.PaintListener#paintControl(org.eclipse.swt.events.PaintEvent)
	 */
	public void paintControl(PaintEvent arg0)
    {
		GC g = arg0.gc;
		Color background = g.getBackground();
		Color deactivatedColor = new Color(g.getDevice(), 200, 200, 200);
		Color hoverColor = new Color(g.getDevice(), 175, 175, 175);
		Color activatedColor = new Color(g.getDevice(), 137, 173, 233);
		Color foreground = g.getForeground();
		Color white = g.getDevice().getSystemColor(SWT.COLOR_WHITE);
		g.setForeground(white);
		if(selected)
		{
			g.setBackground(activatedColor);
		}
		else
		{
			if(over)
				g.setBackground(hoverColor);
			else
				g.setBackground(deactivatedColor);
		}
		g.setAntialias(SWT.ON);
		g.fillOval(0, 0, 16, 16);
		g.setBackground(background);
		Point extent = g.stringExtent(text);
		int sx = (16 - extent.x) / 2;
		int sy = (16 - extent.y) / 2;
		g.drawString(text, sx, sy, true);
		g.setForeground(foreground);
		deactivatedColor.dispose();
		activatedColor.dispose();
		hoverColor.dispose();
    }

	/* (non-Javadoc)
	 * @see org.eclipse.swt.widgets.Control#computeSize(int, int)
	 */
    public Point computeSize(int hint, int hint2)
    {
	    return new Point(16, 16);
    }

	/**
	 * Sets the text to use as the label for this button.  As the button does
	 * not change size based on it's text, this value should contain a single
	 * character.
	 * 
	 * @param text The text to use as a label
	 */
	public void setText(String text)
	{
		this.text = text;
		if(this.text.length() > 1)
		{
			this.text = text.substring(0, 1);
		}
	}
	
	/**
	 * Sets the selection state of this toggle button to the given value.  This
	 * does not generate a selection event.
	 * 
	 * @param selected The new selected state
	 */
	public void setSelected(boolean selected)
	{
		this.selected = selected;
		this.redraw();
	}
	
	/**
	 * @return true if this button is toggled on/down, false otherwise
	 */
	public boolean isSelected()
	{
		return this.selected;
	}
	
	/**
	 * Determines whether this toggle button will allow the user to toggle the
	 * button state off/up once it has been toggled on/down.
	 * 
	 * @param toggleDownOnly true to disallow the user, false otherwise
	 */
	public void setToggleDownOnly(boolean toggleDownOnly)
	{
		this.toggleDownOnly = toggleDownOnly;
		if(this.selected && toggleDownOnly)
			this.setCursor(Display.getCurrent().getSystemCursor(SWT.CURSOR_ARROW));
		else
			this.setCursor(Display.getCurrent().getSystemCursor(SWT.CURSOR_HAND));
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.MouseListener#mouseDoubleClick(org.eclipse.swt.events.MouseEvent)
	 */
	public void mouseDoubleClick(MouseEvent arg0)
    {
    }

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.MouseListener#mouseDown(org.eclipse.swt.events.MouseEvent)
	 */
	public void mouseDown(MouseEvent arg0)
    {
    }

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.MouseListener#mouseUp(org.eclipse.swt.events.MouseEvent)
	 */
	public void mouseUp(MouseEvent arg0)
    {
		if(this.selected && toggleDownOnly)
			return;
		this.selected = !selected;
		this.redraw();
		if(this.selected && toggleDownOnly)
			this.setCursor(Display.getCurrent().getSystemCursor(SWT.CURSOR_ARROW));
		for(ToggleButtonListener listener : listeners)
		{
			listener.toggleButtonSelected(this);
		}
    }
	
	/**
	 * Registers the given listener with this toggle button.  If the listener is
	 * already registered, it is removed from the list and added to the end.
	 * 
	 * @param listener The listener to register
	 */
	public void addSelectionListener(ToggleButtonListener listener)
	{
		listeners.remove(listener);
		listeners.add(listener);
	}

	/**
	 * Removes the given listener from the set of registered listeners for this
	 * toggle button.  If the listener was not registered with this button, no
	 * action is taken.
	 * 
	 * @param listener The listener to remove
	 */
	public void removeSelectionListener(ToggleButtonListener listener)
	{
		listeners.remove(listener);
	}
	
	/**
	 * Interface for selection listeners for ToggleButton.
	 * 
	 * @author trip
	 */
	public interface ToggleButtonListener
	{
		/**
		 * Called when a toggle button's selection changes due to user
		 * interaction.
		 * 
		 * @param button The button whose state changed
		 */
		public void toggleButtonSelected(ToggleButton button);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.MouseTrackListener#mouseEnter(org.eclipse.swt.events.MouseEvent)
	 */
	public void mouseEnter(MouseEvent arg0)
    {
		over = true;
		this.redraw();
		if((toggleDownOnly && !selected) || !toggleDownOnly)
		{
			this.setCursor(Display.getCurrent().getSystemCursor(SWT.CURSOR_HAND));
		}
    }

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.MouseTrackListener#mouseExit(org.eclipse.swt.events.MouseEvent)
	 */
	public void mouseExit(MouseEvent arg0)
    {
		over = false;
		this.redraw();
    }

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.MouseTrackListener#mouseHover(org.eclipse.swt.events.MouseEvent)
	 */
	public void mouseHover(MouseEvent arg0)
    {
    }

}
