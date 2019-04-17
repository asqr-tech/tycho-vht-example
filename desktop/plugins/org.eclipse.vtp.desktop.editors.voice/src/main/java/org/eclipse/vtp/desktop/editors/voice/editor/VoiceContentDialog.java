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
/**
 * 
 */
package org.eclipse.vtp.desktop.editors.voice.editor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.vtp.desktop.media.core.ContentDialog;
import org.eclipse.vtp.framework.interactions.core.media.Content;
import org.eclipse.vtp.framework.interactions.core.media.ContentComposite;
import org.eclipse.vtp.framework.interactions.core.media.IMediaProvider;

/**
 * A dialog for editing a list of content objects in a voice project.
 * 
 * @author Lonnie Pryor
 */
public class VoiceContentDialog extends ContentDialog implements ModifyListener {
	/** The name of the content set. */
	private String itemName = "";
	/** The names the content set cannot have. */
	private Set<String> unavailableNames = new HashSet<String>();
	/** The name of the content set. */
	private Text itemNameField = null;

	/**
	 * Creates a new <code>VoiceContentDialog</code>.
	 * 
	 * @param parentShell
	 *            The shell to create the dialog from.
	 */
	public VoiceContentDialog(Shell parentShell) {
		super(parentShell);
	}

	/**
	 * Creates a new <code>VoiceContentDialog</code>.
	 * 
	 * @param parentShellProvider
	 *            The shell provider to create the dialog from.
	 */
	public VoiceContentDialog(IShellProvider parentShellProvider) {
		super(parentShellProvider);
	}

	/**
	 * Sets the name, content item, and media provider for this dialog edits.
	 * 
	 * @param itemName
	 *            The name of the item being edited.
	 * @param contentItem
	 *            The content item this dialog edits.
	 * @param mediaProvider
	 *            The media provider this dialog uses.
	 */
	public void initialize(String itemName, String[] unavailableNames,
			Content contentItem, IMediaProvider mediaProvider) {
		this.itemName = itemName == null ? "" : itemName;
		this.unavailableNames.addAll(Arrays.asList(unavailableNames));
		this.unavailableNames.remove(itemName);
		contents.clear();
		if (contentItem instanceof ContentComposite)
		{
			for (Content c : ((ContentComposite) contentItem).listContent())
				contents.add(c);
		}
		else if (contentItem != null)
			contents.add(contentItem);
		setMediaProvider(mediaProvider);
	}

	/**
	 * Returns the name of the item being edited
	 * @return the name of the item being edited
	 */
	public String getItemName()
	{
		return itemName;
	}

	/**
	 * @return
	 */
	public Content getContentItem()
	{
		if (contents.isEmpty())
			return null;
		else if (contents.size() == 1)
			return contents.get(0);
		else
		{
			ContentComposite composite = new ContentComposite();
			for (Content c : contents)
				composite.addContent(c);
			return composite;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.desktop.media.core.ContentDialog#isContentValid()
	 */
	protected boolean isContentValid() {
		return !(itemName.length() == 0 || unavailableNames.contains(itemName) || contents
				.isEmpty());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.desktop.media.core.ContentDialog#createDialogArea(
	 *      org.eclipse.swt.widgets.Composite)
	 */
	protected Control createDialogArea(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		Label label = new Label(composite, SWT.None);
		label.setText("Item Name:");
		label.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER,
				false, false));
		itemNameField = new Text(composite, SWT.BORDER);
		itemNameField.setText(itemName);
		itemNameField.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
				true, false));
		itemNameField.addModifyListener(this);
		Control child = super.createDialogArea(composite);
		child.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true,
				true, 2, 1));
		return composite;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
	 */
	public void modifyText(ModifyEvent e) {
		itemName = itemNameField.getText();
		if (itemName == null)
			itemName = "";
		contentChanged();
	}
}
