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
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * The <code>TextLinkViewer</code> is a complex UI widget designed to manage a
 * set of text links.
 * 
 * @author Trip
 */
public class TextLinkViewer extends Composite implements PaintListener,
	LinkSelectionListener
{
	/**	The current set of links managed by this viewer */
	private List<TextLink> links = new ArrayList<TextLink>();
	/**	The currently selected link */
	private TextLink selectedLink;
	/**	The set of selection listeners registered with this viewer */
	private List<LinkViewerSelectionListener> listeners = 
		new ArrayList<LinkViewerSelectionListener>();

	/**
	 * Creates a new <code>TextLinkViewer</code> with the given parent composite
	 * and SWT style bits.  The style bits are passed directly to the superclass
	 * constructor.
	 * 
	 * @param parent The parent composite for this viewer
	 * @param style The SWT style bits for this viewer
	 */
	public TextLinkViewer(Composite parent, int style)
	{
		super(parent, style);
		setBackground(parent.getBackground());

		GridLayout gl = new GridLayout(1, true);
		gl.horizontalSpacing = 0;
		gl.verticalSpacing = 1;
		gl.marginHeight = 1;
		gl.marginWidth = 0;
		setLayout(gl);
		addPaintListener(this);
	}

	/**
	 * Creates a new <code>TextLink</code> with the given text.  The link is
	 * added to the end of the set of links managed by this viewer.
	 * 
	 * @param link The text to display as a link
	 */
	public void addLink(String link)
	{
		TextLink linkControl = new TextLink(link, this, SWT.NONE);
		GridData linkControlData = new GridData(GridData.FILL_HORIZONTAL);
		linkControl.setLayoutData(linkControlData);
		links.add(linkControl);

		if(links.size() == 1)
		{
			linkControl.setSelected(true);
			selectedLink = linkControl;
		}

		linkControl.addSelectionListener(this);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.PaintListener#paintControl(org.eclipse.swt.events.PaintEvent)
	 */
	public void paintControl(PaintEvent e)
	{
		e.gc.setForeground(e.display.getSystemColor(SWT.COLOR_DARK_GRAY));

		if(links.size() > 0)
		{
			e.gc.drawLine(0, 0, getSize().x, 0);
		}

		for(int i = 0; i < links.size(); i++)
		{
			Control c = links.get(i);
			e.gc.drawLine(0, c.getLocation().y + c.getSize().y, getSize().x,
				c.getLocation().y + c.getSize().y);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.ui.shared.custom.LinkSelectionListener#linkSelected(org.eclipse.vtp.desktop.ui.shared.custom.TextLink)
	 */
	public void linkSelected(TextLink link)
	{
		if(link.isSelected())
		{
			if(link != this.selectedLink)
			{
				selectedLink.setSelected(false);
				selectedLink = link;
				fireSelectionChanged();
			}
		}
	}

	/**
	 * Adds the given selection listener to the set of listeners registered with
	 * this viewer.  If the listener was already registered, it is removed from
	 * the list of listeners and added to the end.
	 * 
	 * @param l The listener to register.
	 */
	public void addSelectionListener(LinkViewerSelectionListener l)
	{
		listeners.remove(l);
		listeners.add(l);
	}

	/**
	 * Removes the given selection listener from this viewer.  No action is taken
	 * if the given listener was not already registered.
	 * 
	 * @param l The listener to remove
	 */
	public void removeSelectionListener(LinkViewerSelectionListener l)
	{
		listeners.remove(l);
	}

	/**
	 * Initiates a selection changed event for this viewer that is delivered to
	 * all registered selection listeners.
	 */
	private void fireSelectionChanged()
	{
		for(LinkViewerSelectionListener listener : listeners)
		{
			listener.selectionChanged(selectedLink.getLinkText());
		}
	}
}
