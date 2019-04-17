/*--------------------------------------------------------------------------
 * Copyright (c) 2004, 2006-2007 OpenMethods, LLC
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Trip Gilman (OpenMethods), Lonnie G. Pryor (OpenMethods)
 *    - initial API and implementation
 -------------------------------------------------------------------------*/
package org.eclipse.vtp.desktop.core.custom;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

/**
 * TextLink is a UI widget designed to provide a clickable piece of text that
 * mimics links found on web pages.
 * 
 * @author Trip
 */
public class TextLink extends Canvas implements MouseListener,
	MouseTrackListener, PaintListener
{
	/**	The text to display as a clickable link */
	private String linkText;
	/**	The number of pixels to reserve on each side of the text */
	private int marginWidth = 4;
	/**	The number of pixels to reserve on the top and the bottom of the text */
	private int marginHeight = 2;
	/**	Indicates if the mouse is currently over this link */
	private boolean mouseOver = false;
	/**	Indicates if this link has been selected */
	private boolean selected = false;
	/**	The renderer to use to produce this link's visual representation */
	private LinkRenderer renderer = new DefaultLinkRenderer();
	/**	The current set of listeners registered with this link */
	private List<LinkSelectionListener> listeners =
		new ArrayList<LinkSelectionListener>();

	/**
	 * Creates a new TextLink that has the given text, parent composite, and
	 * SWT style bits.
	 * 
	 * @param linkText The text to display
	 * @param parent The parent composite for this UI widget
	 * @param style The SWT style bits to apply to this link
	 */
	public TextLink(String linkText, Composite parent, int style)
	{
		super(parent, style | SWT.DOUBLE_BUFFERED);
		this.linkText = linkText;
		setBackground(parent.getBackground());
		addMouseListener(this);
		addMouseTrackListener(this);
		addPaintListener(this);
	}

	/**
	 * Returns the current text displayed by this link.
	 * 
	 * @return The link text displayed
	 */
	public String getLinkText()
	{
		return linkText;
	}

	/**
	 * Changes the text that is displayed by this link.  This does not directly
	 * cause a repaint of this component so the caller will need to update this
	 * widget or it's parent to display the new value.
	 * 
	 * @param linkText The new text to display
	 */
	public void setLinkText(String linkText)
	{
		this.linkText = linkText;
	}

	/**
	 * @return The <code>LinkRenderer</code> that will be used to display this
	 * text link
	 */
	public LinkRenderer getRenderer()
	{
		return renderer;
	}

	/**
	 * Provides a new <code>LinkRenderer</code> instance to use when displaying
	 * this text link.
	 * 
	 * @param renderer The new renderer to use
	 */
	public void setRenderer(LinkRenderer renderer)
	{
		this.renderer = renderer;
	}

	/**
	 * Sets the selected state for this text link.  This causes the link to be
	 * redrawn and initiates a selection event for this link.
	 * 
	 * @param selected The new state for this text link
	 */
	public void setSelected(boolean selected)
	{
		this.selected = selected;
		fireSelected();
		redraw();
	}

	/**
	 * Adds the given selection listener to this link.  The listener will be
	 * added to the end of the list of current listeners.  If the listener was
	 * already in the list, it is removed and placed at the end.
	 * 
	 * @param listener The selection listener to add
	 */
	public void addSelectionListener(LinkSelectionListener listener)
	{
		listeners.remove(listener);
		listeners.add(listener);
	}

	/**
	 * Removes the given selection listener from this text link.  This operation
	 * has no effect if the listener was not present in the list.
	 * 
	 * @param listener The listener to remove
	 */
	public void removeSelectionListener(LinkSelectionListener listener)
	{
		listeners.remove(listener);
	}

	/**
	 * Fires a selection event to all registered selection listeners of this
	 * text link.
	 */
	private void fireSelected()
	{
		for(LinkSelectionListener listener : listeners)
		{
			listener.linkSelected(this);
		}
	}

	/**
	 * @return <code>true</code> if this text link is selected, <code>false</code>
	 * otherwise
	 */
	public boolean isSelected()
	{
		return selected;
	}

	/**
	 * @return <code>true</code> if the mouse cursor is currently hovering over
	 * this text link
	 */
	public boolean isMouseOver()
	{
		return mouseOver;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.MouseTrackListener#mouseEnter(org.eclipse.swt.events.MouseEvent)
	 */
	public void mouseEnter(MouseEvent e)
	{
		mouseOver = true;
		redraw();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.MouseTrackListener#mouseExit(org.eclipse.swt.events.MouseEvent)
	 */
	public void mouseExit(MouseEvent e)
	{
		mouseOver = false;
		redraw();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.MouseTrackListener#mouseHover(org.eclipse.swt.events.MouseEvent)
	 */
	public void mouseHover(MouseEvent e)
	{
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.PaintListener#paintControl(org.eclipse.swt.events.PaintEvent)
	 */
	public void paintControl(PaintEvent e)
	{
		renderer.render(this, e.gc);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.widgets.Composite#computeSize(int, int, boolean)
	 */
	public Point computeSize(int wHint, int hHint, boolean changed)
	{
		GC g = new GC(this);
		Point generalExtent = g.stringExtent(linkText);
		g.dispose();
		generalExtent.x += (marginWidth * 2);
		generalExtent.y += (marginHeight * 2);

		if(wHint == SWT.DEFAULT)
		{
			wHint = generalExtent.x;
		}

		if(hHint == SWT.DEFAULT)
		{
			hHint = generalExtent.y;
		}

		return new Point(wHint, hHint);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.MouseListener#mouseDoubleClick(org.eclipse.swt.events.MouseEvent)
	 */
	public void mouseDoubleClick(MouseEvent e)
	{
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.MouseListener#mouseDown(org.eclipse.swt.events.MouseEvent)
	 */
	public void mouseDown(MouseEvent e)
	{
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.MouseListener#mouseUp(org.eclipse.swt.events.MouseEvent)
	 */
	public void mouseUp(MouseEvent e)
	{
		setSelected(true);
	}

	/**
	 * A default link renderer implementation.
	 * 
	 * @author trip
	 */
	private class DefaultLinkRenderer implements LinkRenderer
	{
		/* (non-Javadoc)
		 * @see org.eclipse.vtp.desktop.ui.shared.custom.LinkRenderer#render(org.eclipse.vtp.desktop.ui.shared.custom.TextLink, org.eclipse.swt.graphics.GC)
		 */
		public void render(TextLink link, GC g)
		{
			String text = link.getLinkText();
			Point textExtent = g.stringExtent(text);

			while((textExtent.x + (marginWidth * 2)) > link.getSize().x)
			{
				text = text.substring(0, text.length() - 4) + "...";
				textExtent = g.stringExtent(text);
			}

			if(isMouseOver())
			{
				g.setBackground(link.getDisplay().getSystemColor(SWT.COLOR_BLUE));
			}
			else
			{
				g.setBackground(link.getBackground());
			}

			g.fillRectangle(0, 0, link.getSize().x, link.getSize().y);

			if(isMouseOver())
			{
				g.setForeground(link.getDisplay().getSystemColor(SWT.COLOR_WHITE));
			}
			else if(isSelected())
			{
				g.setForeground(link.getDisplay()
									.getSystemColor(SWT.COLOR_DARK_BLUE));
			}

			g.drawString(text, marginWidth, marginHeight, true);
		}
	}
}
