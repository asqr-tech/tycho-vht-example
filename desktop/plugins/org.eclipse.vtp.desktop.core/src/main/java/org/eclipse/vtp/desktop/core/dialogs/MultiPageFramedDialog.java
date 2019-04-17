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
package org.eclipse.vtp.desktop.core.dialogs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.window.IShellProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.vtp.desktop.core.custom.LinkViewerSelectionListener;
import org.eclipse.vtp.desktop.core.custom.TextLinkViewer;

/**
 * This subclass of FramedDialog provides a paged style display with navigation
 * in the side bar and OK/Cancel buttons in the button bar.  The navigation is
 * populated with the names of the pages registered with this dialog.
 * 
 * @author Trip
 */
public class MultiPageFramedDialog extends FramedDialog
	implements LinkViewerSelectionListener
{
	/**	The widget used for page navigation */
	private TextLinkViewer linkViewer;
	/**	The list of pages hosted by this dialog */
	private List<ContentPage> pages = new ArrayList<ContentPage>();
	/**	The layout that performs the paging */
	private StackLayout stackLayout;
	/**	The root control of each page indexed by page name */
	private Map<String, Control> pageControls = new HashMap<String, Control>();
	/**	The OK button for this dialog */
	private Button okButton = null;
	/**	The currently displayed page */
	private ContentPage currentPage = null;
	
	/**
	 * Constructs a new MultiPageFramedDialog with the given shell as its
	 * parent window.
	 * 
	 * @param parentShell The parent window of this dialog
	 */
	public MultiPageFramedDialog(Shell parentShell)
	{
		super(parentShell);
		init();
	}

	/**
	 * Constructs a new MultiPageFramedDialog with the information provided from
	 * the given shell provider.
	 * 
	 * @param shellProvider The shell provider
	 */
	public MultiPageFramedDialog(IShellProvider shellProvider)
	{
		super(shellProvider);
		init();
	}

	/**
	 * Performs some initialization.  Called from all constructor types.
	 */
	private void init()
	{
		this.setSideBarSize(110);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.dialogs.FramedDialog#createButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	protected void createButtonBar(Composite parent)
	{
		parent.setLayout(new GridLayout(1, true));

		Composite buttons = new Composite(parent, SWT.NONE);
		buttons.setBackground(parent.getBackground());

		GridData buttonsData = new GridData(GridData.FILL_BOTH);
		buttonsData.horizontalAlignment = SWT.RIGHT;
		buttons.setLayoutData(buttonsData);

		RowLayout rl = new RowLayout();
		rl.pack = false;
		rl.spacing = 5;
		buttons.setLayout(rl);

		okButton = new Button(buttons, SWT.NONE);
		okButton.setText("Ok");
		okButton.addSelectionListener(new SelectionListener()
		{
			public void widgetSelected(SelectionEvent e)
			{
				okPressed();
			}
			
			public void widgetDefaultSelected(SelectionEvent e)
			{
			}
		});
		final Button cancelButton = new Button(buttons, SWT.NONE);
		cancelButton.setText("Cancel");
		cancelButton.addSelectionListener(new SelectionListener()
		{
			public void widgetSelected(SelectionEvent e)
			{
				cancelPressed();
			}
			
			public void widgetDefaultSelected(SelectionEvent e)
			{
			}
		});

		if(Display.getCurrent().getDismissalAlignment() == SWT.RIGHT)
		{
			cancelButton.moveAbove(okButton);
		}
		this.getShell().setDefaultButton(okButton);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.dialogs.FramedDialog#createDialogContents(org.eclipse.swt.widgets.Composite)
	 */
	protected void createDialogContents(Composite parent)
	{
		stackLayout = new StackLayout();
		parent.setLayout(stackLayout);

		for(int i = 0; i < pages.size(); i++)
		{
			ContentPage p = pages.get(i);
			Control c = p.createPage(parent);
			pageControls.put(p.getName(), c);

			if(i == 0)
			{
				stackLayout.topControl = c;
				currentPage = p;
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.dialogs.FramedDialog#createSideBar(org.eclipse.swt.widgets.Composite)
	 */
	protected void createSideBar(Composite parent)
	{
		parent.setLayout(new FillLayout());
		linkViewer = new TextLinkViewer(parent, SWT.NONE);

		for(ContentPage page : pages)
		{
			linkViewer.addLink(page.getName());
		}

		linkViewer.addSelectionListener(this);
	}

	/**
	 * Adds the given page to the set of pages hosted by this dialog.  The page
	 * is added to the end of the list.
	 * 
	 * @param page The content page to add
	 */
	public void addPage(ContentPage page)
	{
		pages.add(page);
		page.setRootShell(getParentShell());
	}

	/**
	 * Called when the cancel button is pressed.  Sets this dialog's return code
	 * and closes the dialog.
	 */
	protected void cancelPressed()
	{
		this.setReturnCode(SWT.CANCEL);
		close();
	}

	/**
	 * Performs any actions required in response to the OK button being pressed.
	 * Subclasses should provide any appropriate behavior here.  The return
	 * value acts as a veto over the closing of the dialog and setting of the
	 * dialog's return value, effectively cancelling the OK press.
	 * 
	 * @return true if the dialog can close, false otherwise
	 */
	protected boolean processOk()
	{
		return true;
	}

	/**
	 * Called when the OK button is pressed.  Subclasses should override
	 * processOK() to perform save actions.
	 */
	protected void okPressed()
	{
		if(processOk())
		{
			this.setReturnCode(Window.OK);
			close();
		}
	}

	/**
	 * @return The list of pages currently hosted by this dialog
	 */
	protected List<ContentPage> getPages()
	{
		return pages;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.ui.shared.custom.LinkViewerSelectionListener#selectionChanged(java.lang.String)
	 */
	public void selectionChanged(String selection)
	{
		stackLayout.topControl = pageControls.get(selection);
		stackLayout.topControl.getParent().layout();
		for(ContentPage page : pages)
		{
			if(page.getName().equals(selection))
			{
				currentPage = page;
				break;
			}
		}
	}
	
	/**
	 * @return The currently viewed content page
	 */
	public ContentPage getCurrentPage()
	{
		return currentPage;
	}
	
	/**
	 * Indicates that this dialog can or cannot finish.  This effectively
	 * manages the enablement of the OK button.
	 * 
	 * @param canFinish true if OK should be enabled, false otherwise
	 */
	public void setCanFinish(boolean canFinish)
	{
		if(okButton != null)
			okButton.setEnabled(canFinish);
	}
}
