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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.vtp.framework.interactions.core.media.Content;
import org.eclipse.vtp.framework.interactions.core.media.ReferencedContent;

public class ReferenceContentCreatorPanel extends DynamicContentCreatorPanel
{
	Combo options = null;

	public ReferenceContentCreatorPanel()
	{
		super();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.media.core.ContentCreatorPanel#createContent()
	 */
	public Content createContent()
	{
		ReferencedContent content = new ReferencedContent();
		if (isDynamicSelected())
			content.setVariableReferencedName(getDynamicSelection());
		else
			if(options == null || options.getSelectionIndex() == -1)
				return content;
			content.setStaticReferencedName(options.getItem(options
					.getSelectionIndex()));
		return content;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.media.core.DynamicContentCreatorPanel#createStaticControls(org.eclipse.swt.widgets.Composite)
	 */
	public Control createStaticControls(Composite parent)
	{
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new GridLayout(2, false));
		options = new Combo(comp, SWT.DROP_DOWN | SWT.READ_ONLY);
		options.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		for (String sharedContent : getMediaProvider().getSharedContentProvider()
				.listSharedContent())
			options.add(sharedContent);
		return comp;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.media.core.DynamicContentCreatorPanel#setInitialContent(org.eclipse.vtp.framework.interactions.core.media.Content)
	 */
	public void setInitialContent(Content content)
	{
		if (content instanceof ReferencedContent)
		{
			ReferencedContent referencedContent = (ReferencedContent)content;
			if (referencedContent.getReferenceType() == ReferencedContent.STATIC_REF)
			{
				setDynamicSelected(false);
				String referencedName = referencedContent.getReferencedName();
				for (int i = 0; i < options.getItemCount(); i++)
				{
					if (options.getItem(i).equals(referencedName))
					{
						options.select(i);
						return;
					}
				}
				options.deselectAll();
			}
			else
			{
				setDynamicSelected(true);
				setDynamicSelection(referencedContent.getReferencedName());
			}
		}
	}

}
