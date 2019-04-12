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
package org.eclipse.vtp.desktop.media.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.vtp.desktop.model.core.design.Variable;
import org.eclipse.vtp.desktop.model.interactive.core.content.ContentType;
import org.eclipse.vtp.framework.interactions.core.media.Content;
import org.eclipse.vtp.framework.interactions.core.media.IContentType;
import org.eclipse.vtp.framework.interactions.core.media.IMediaProvider;

/**
 * A dialog for editing an individual content object.
 * 
 * @author Lonnie Pryor
 */
public class ContentEntryDialog extends Dialog {
	/** The media provider the list is bound to. */
	private IMediaProvider mediaProvider = null;
	List<IContentType> supportedTypes;
	List<ContentPlaceholder> placeholders = Collections.emptyList();
	List<Variable> variables;
	List<ContentCreatorPanel> creatorControls = new ArrayList<ContentCreatorPanel>();
	StackLayout stackLayout;
	Combo typeCombo = null;
	Content content;
	Composite creatorComp;

	/**
	 * Creates a new <code>ContentEntryDialog</code>.
	 * 
	 * @param parentShell
	 *            The shell to create the dialog from.
	 */
	public ContentEntryDialog(Shell parentShell) {
		super(parentShell);
	}

	/**
	 * Creates a new <code>ContentEntryDialog</code>.
	 * 
	 * @param parentShellProvider
	 *            The shell provider to create the dialog from.
	 */
	public ContentEntryDialog(IShellProvider parentShellProvider) {
		super(parentShellProvider);
	}

	public void setVariables(List<Variable> variables)
	{
		this.variables = variables;
	}
	
	public void setPlaceholders(List<ContentPlaceholder> placeholders)
	{
		this.placeholders = placeholders;
	}

	/**
	 * Sets the media provider the item is bound to.
	 * 
	 * @param mediaProvider
	 *            The media provider the item is bound to.
	 */
	public void setMediaProvider(IMediaProvider mediaProvider) {
		this.mediaProvider = mediaProvider;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(
	 *      org.eclipse.swt.widgets.Composite)
	 */
	protected Control createDialogArea(Composite parent) {
		this.getShell().setText("Prompt Entry");
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new GridLayout());
		comp.setLayoutData(new GridData(GridData.FILL_BOTH));

		// content type selector section
		Label typeLabel = new Label(comp, SWT.NONE);
		typeLabel.setText("Content Type");
		typeLabel.setLayoutData(new GridData());
		typeCombo = new Combo(comp, SWT.DROP_DOWN | SWT.READ_ONLY);
		typeCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		supportedTypes = mediaProvider.getSupportedContentTypes();
		if(placeholders.size() > 0)
		{
			boolean inserted = false;
			ContentType placeholderType = new ContentType("org.eclipse.vtp.framework.interactions.core.media.content.placeholder", "Placeholder");
			for(int i = 0; i < supportedTypes.size(); i++)
			{
				if(supportedTypes.get(i).getName().compareToIgnoreCase("Placeholder") > 0)
				{
					inserted = true;
					supportedTypes.set(i, placeholderType);
					break;
				}
			}
			if(!inserted)
				supportedTypes.add(placeholderType);
		}
		creatorComp = new Composite(comp, SWT.NONE);
		creatorComp.setLayoutData(new GridData(GridData.FILL_BOTH));
		creatorComp.setLayout(stackLayout = new StackLayout());

		for (int i = 0; i < supportedTypes.size(); i++) {
			ContentType ct = (ContentType) supportedTypes.get(i);
			typeCombo.add(ct.getName());
			ContentCreatorPanel ccp = ContentCreatorPanelManager.getInstance()
					.getCreatorPanel(ct);
			ccp.setMediaProvider(mediaProvider);
			if (ccp instanceof DynamicContentCreatorPanel)
				((DynamicContentCreatorPanel)ccp).setVariables(variables);
			if (ccp instanceof PlaceholderContentCreatorPanel)
				((PlaceholderContentCreatorPanel)ccp).setPlaceholders(placeholders);
			creatorControls.add(ccp);
			ccp.createControls(creatorComp);
			if (content != null && content.getContentType().equals(ct.getId())) {
				typeCombo.select(i);
				stackLayout.topControl = ccp.getControl();
				ccp.setInitialContent(content);
			}
		}
		
		if (content == null) {
			stackLayout.topControl = creatorControls.get(0).getControl();
			typeCombo.select(0);
		}
		typeCombo.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				stackLayout.topControl = creatorControls
						.get(typeCombo.getSelectionIndex()).getControl();
				creatorComp.layout(true, true);
			}

		});
		return comp;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	protected void okPressed() {
		ContentCreatorPanel ccp = creatorControls
			.get(typeCombo.getSelectionIndex());
		content = ccp.createContent();
		super.okPressed();
	}

	/**
	 * @return
	 */
	public Content getContent() {
		return content;
	}

	/**
	 * @param content
	 */
	public void setContent(Content content) {
		this.content = content;
	}
}
