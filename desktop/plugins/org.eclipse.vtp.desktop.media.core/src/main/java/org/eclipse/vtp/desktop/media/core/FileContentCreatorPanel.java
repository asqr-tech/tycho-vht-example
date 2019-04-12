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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.vtp.framework.interactions.core.media.Content;
import org.eclipse.vtp.framework.interactions.core.media.FileContent;

public abstract class FileContentCreatorPanel extends
		DynamicContentCreatorPanel implements SelectionListener
{
	Text text = null;

	public FileContentCreatorPanel()
	{
		super();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.media.core.ContentCreatorPanel#createContent()
	 */
	public Content createContent()
	{
		FileContent content = createNewContent();
		if (isDynamicSelected())
			content.setVariablePath(getDynamicSelection());
		else
			content.setStaticPath(text.getText());
		return content;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.media.core.DynamicContentCreatorPanel#createStaticControls(org.eclipse.swt.widgets.Composite)
	 */
	public Control createStaticControls(Composite parent)
	{
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new GridLayout(1, false));
		text = new Text(comp, SWT.BORDER | SWT.FLAT | SWT.SINGLE);
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//		text.setEditable(false);
		Button browseButton = new Button(comp, SWT.PUSH);
		browseButton.setText("Browse...");
		browseButton.setLayoutData(new GridData());
		browseButton.addSelectionListener(this);
		return comp;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.media.core.DynamicContentCreatorPanel#setInitialContent(org.eclipse.vtp.framework.interactions.core.media.Content)
	 */
	public void setInitialContent(Content content)
	{
		if (content instanceof FileContent)
		{
			FileContent fileContent = (FileContent)content;
			if (fileContent.getPathType() == FileContent.STATIC_PATH)
			{
				setDynamicSelected(false);
				text.setText(fileContent.getPath());
			}
			else
			{
				setDynamicSelected(true);
				setDynamicSelection(fileContent.getPath());
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	public void widgetSelected(SelectionEvent e)
	{
		ResourceChooserDialog dialog = new ResourceChooserDialog(text.getShell(),
				getMediaProvider().getResourceManager(), text.getText());
		if (dialog.open() != ResourceChooserDialog.OK)
			return;
		text.setText(dialog.getValue());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	public void widgetDefaultSelected(SelectionEvent e)
	{
	}

	/**
	 * @return
	 */
	protected abstract FileContent createNewContent();
}
