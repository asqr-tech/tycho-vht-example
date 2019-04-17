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
package org.eclipse.vtp.desktop.core.dialogs;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.util.Policy;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.jface.window.SameShellProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * This is a dialog type window the presents its contents within a stylized
 * border.  It also provides a side bar area that can be used for navigation.
 * 
 * @author Trip
 */
public class FramedDialog extends Window
{
	/**	Background color for the content area */
	private Color contentBackground;
	/**	Background color for the border frame */
	private Color frameColor;
	/**	Background color for the side bar area */
	private Color sideBarColor;
	/**	The current control hosted in the side bar area */
	private Control sideBarControl;
	/**	The current control hosted in the content area */
	private Control contentControl;
	/**	The current control hosted in the button bar area */
	private Control buttonBarControl;
	/**	The main composite hosting the frame, content, and side bar */
	private Composite frameComposite;
	/**	The current width of the side bar area */
	private int sideBarSize = 10;
	/** The current title of this dialog */
	private String title;

	/**
	 * Constructs a new FramedDialog with the given shell as its parent window.
	 * 
	 * @param parentShell The parent window of this dialog
	 */
	public FramedDialog(Shell parentShell)
	{
		this(new SameShellProvider(parentShell));

		if((parentShell == null) && Policy.DEBUG_DIALOG_NO_PARENT)
		{
			Policy.getLog().log(new Status(IStatus.INFO, Policy.JFACE,
					IStatus.INFO, this.getClass() + " created with no shell", //$NON-NLS-1$
					new Exception()));
		}
	}

	/**
	 * Constructs a new FramedDialog with the information provided from the
	 * given shell provider.
	 * 
	 * @param shellProvider The shell provider
	 */
	public FramedDialog(IShellProvider shellProvider)
	{
		super(shellProvider);
		setShellStyle(SWT.DIALOG_TRIM | SWT.RESIZE | SWT.APPLICATION_MODAL
			| getDefaultOrientation());
		setBlockOnOpen(true);
		contentBackground = shellProvider.getShell().getDisplay()
										 .getSystemColor(SWT.COLOR_WHITE);
		frameColor = shellProvider.getShell().getDisplay()
								  .getSystemColor(SWT.COLOR_DARK_GRAY);
		sideBarColor = shellProvider.getShell().getDisplay()
									.getSystemColor(SWT.COLOR_GRAY);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.window.Window#createContents(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createContents(Composite parent)
	{
		frameComposite = new Composite(parent,
				SWT.NO_BACKGROUND | SWT.DOUBLE_BUFFERED);
		frameComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		frameComposite.addPaintListener(new PaintListener()
			{
				public void paintControl(PaintEvent e)
				{
					GC g = e.gc;
					g.setAntialias(SWT.ON);
					g.setBackground(contentBackground);
					g.fillRectangle(0, 0, frameComposite.getSize().x,
						frameComposite.getSize().y);
					g.setBackground(frameColor);
					g.fillRectangle(0, 0, frameComposite.getSize().x, 30);
					g.fillRectangle(0, frameComposite.getSize().y - 10,
						frameComposite.getSize().x, 10);
					g.fillRectangle(0, 30, sideBarSize,
						frameComposite.getSize().y - 35);
					g.fillRectangle(sideBarSize, 30,
						contentControl.getLocation().x - sideBarSize, 20);
					g.fillRectangle(sideBarSize,
						frameComposite.getSize().y - 30,
						contentControl.getLocation().x - sideBarSize, 20);
					g.setBackground(contentBackground);
					g.fillArc(sideBarSize, 30,
						(contentControl.getLocation().x - sideBarSize) * 2, 40,
						180, -90);
					g.fillArc(sideBarSize, frameComposite.getSize().y - 50,
						(contentControl.getLocation().x - sideBarSize) * 2, 40,
						180, 90);
				}
			});
		frameComposite.setLayout(new FormLayout());
		buttonBarControl = new Composite(frameComposite, SWT.NONE);
		buttonBarControl.setBackground(contentBackground);

		FormData buttonBarControlData = new FormData();
		buttonBarControlData.left = new FormAttachment(0, sideBarSize + 30);
		buttonBarControlData.bottom = new FormAttachment(100, -10);
		buttonBarControlData.right = new FormAttachment(100, 0);
		buttonBarControl.setLayoutData(buttonBarControlData);
		createButtonBar((Composite)buttonBarControl);
		sideBarControl = new Composite(frameComposite, SWT.NONE);
		sideBarControl.setBackground(sideBarColor);

		FormData sideBarControlData = new FormData();
		sideBarControlData.left = new FormAttachment(0);
		sideBarControlData.top = new FormAttachment(0, 50);
		sideBarControlData.right = new FormAttachment(buttonBarControl, -30);
		sideBarControlData.bottom = new FormAttachment(100, -30);
		sideBarControl.setLayoutData(sideBarControlData);
		createSideBar((Composite)sideBarControl);
		contentControl = new Composite(frameComposite, SWT.NONE);
		contentControl.setBackground(contentBackground);

		FormData contentControlData = new FormData();
		contentControlData.left = new FormAttachment(sideBarControl, 30);
		contentControlData.top = new FormAttachment(0, 30);
		contentControlData.right = new FormAttachment(100, 0);
		contentControlData.bottom = new FormAttachment(buttonBarControl, 0);
		contentControl.setLayoutData(contentControlData);
		createDialogContents((Composite)contentControl);

		return frameComposite;
	}

	/**
	 * Creates the contents of the button bar area of this dialog.  Subclasses
	 * should override this method to provide an appropriate UI.
	 * 
	 * @param parent The parent composite for button bar controls
	 */
	protected void createButtonBar(Composite parent)
	{
		FormData data = (FormData)parent.getLayoutData();
		data.height = 30;
	}

	/**
	 * Creates the contents of the side bar area of this dialog.  Subclasses
	 * should override this method to provide an appropriate UI.
	 * 
	 * @param parent The parent composite for side bar controls
	 */
	protected void createSideBar(Composite parent)
	{
	}

	/**
	 * Creates the contents of the dialog content area of this dialog.
	 * Subclasses should override this method to provide an appropriate UI.
	 * 
	 * @param parent The parent composite for this dialog's contents
	 */
	protected void createDialogContents(Composite parent)
	{
	}

	/**
	 * @return The background color for the frame border
	 */
	public Color getFrameColor()
	{
		return frameColor;
	}

	/**
	 * Sets the background color used to paint the frame border.
	 * 
	 * @param frameColor The new background color
	 */
	public void setFrameColor(Color frameColor)
	{
		this.frameColor = frameColor;
	}

	/**
	 * @return The background color of the side bar area
	 */
	public Color getSideBarColor()
	{
		return sideBarColor;
	}

	/**
	 * Sets the background color of the side bar area.
	 * 
	 * @param sideBarColor The new background color
	 */
	public void setSideBarColor(Color sideBarColor)
	{
		this.sideBarColor = sideBarColor;

		if(sideBarControl != null)
		{
			sideBarControl.setBackground(sideBarColor);
		}
	}

	/**
	 * @return The control hosted in the side bar area
	 */
	public Control getSideBarControl()
	{
		return sideBarControl;
	}

	/**
	 * Sets the control to be hosted in the side bar area.
	 * 
	 * @param sideBarControl The control to host in the side bar area
	 */
	public void setSideBarControl(Control sideBarControl)
	{
		this.sideBarControl = sideBarControl;
	}

	/**
	 * @return The width of the side bar area
	 */
	public int getSideBarSize()
	{
		return sideBarSize;
	}

	/**
	 * Sets the width of the side bar area.
	 * 
	 * @param sideBarSize The new width of the side bar area
	 */
	public void setSideBarSize(int sideBarSize)
	{
		this.sideBarSize = sideBarSize;

		if(buttonBarControl != null)
		{
			FormData data = (FormData)buttonBarControl.getLayoutData();
			data.left.offset = sideBarSize + 40;
			frameComposite.layout(true, true);
			frameComposite.redraw();
		}
	}

	/**
	 * @return The title of this dialog
	 */
	public String getTitle()
	{
		return title;
	}

	/**
	 * Sets the title of this dialog.
	 * 
	 * @param title The new title of this dialog
	 */
	public void setTitle(String title)
	{
		this.title = (title == null) ? "" : title;

		if(getShell() != null)
		{
			getShell().setText(title);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.window.Window#create()
	 */
	public void create()
	{
		super.create();
		getShell().setText(title);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.window.Window#getParentShell()
	 */
	public Shell getParentShell()
	{
		return super.getParentShell();
	}
}
