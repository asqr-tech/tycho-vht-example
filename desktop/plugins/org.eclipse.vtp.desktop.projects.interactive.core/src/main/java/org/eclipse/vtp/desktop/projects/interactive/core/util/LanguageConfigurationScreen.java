/*--------------------------------------------------------------------------
 * Copyright (c) 2009 OpenMethods, LLC
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Trip Gilman (OpenMethods)
 *    - initial API and implementation
 -------------------------------------------------------------------------*/
package org.eclipse.vtp.desktop.projects.interactive.core.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.vtp.desktop.model.core.branding.BrandManager;
import org.eclipse.vtp.desktop.projects.interactive.core.util.InteractionSupportManager.SupportRecord;

/**
 * A configuration UI for an application's build path.
 * 
 * @author Trip Gilman
 */
public class LanguageConfigurationScreen implements InteractionSupportListener
{
	/** The supported interaction types */
	private InteractionSupportManager supportManager = null;
	/** The brand configuration. */
	private BrandManager brandManager = null;
	private Composite comp = null;
	private Composite mappingComp = null;

	/**
	 * Creates a new LanguageConfigurationScreen.
	 */
	public LanguageConfigurationScreen()
	{
		super();
	}
	
	public void init(BrandManager brandManager, InteractionSupportManager supportManager)
	{
		this.brandManager = brandManager;
		this.supportManager = supportManager;
		supportManager.addListener(this);
	}

	/**
	 * @param parent
	 * @return
	 */
	public Control createContents(Composite parent)
	{
		final FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		ScrolledForm sf = toolkit.createScrolledForm(parent);
		comp = sf.getForm().getBody();
		comp.setBackground(parent.getBackground());
		comp.setBackgroundMode(SWT.INHERIT_DEFAULT);
		comp.setLayout(new FillLayout());

		fillScreens();
		
		return comp;
	}
	
	private void fillScreens()
	{
		mappingComp = new Composite(comp, SWT.NONE);
		mappingComp.setLayout(new GridLayout(1, true));
		boolean hasScreen = false;
		for(SupportRecord sr : supportManager.getCurrentSupport())
		{
			if(sr.isSupported())
			{
				LanguageMappingScreen screen = new LanguageMappingScreen();
				screen.init(brandManager, sr);
				Composite child = new Composite(mappingComp, SWT.NONE);
				child.setLayoutData(new GridData(GridData.FILL_BOTH));
				child.setLayout(new FillLayout());
				screen.createContents(child);
				hasScreen = true;
			}
		}
		if(!hasScreen)
		{
			Composite messageComp = new Composite(mappingComp, SWT.NONE);
			messageComp.setLayoutData(new GridData(GridData.FILL_BOTH));
			messageComp.setLayout(new FillLayout());
			Label errorMessage = new Label(messageComp, SWT.NONE);
			errorMessage.setText("You have not selected any interaction types.  Please go back to the Interaction Types screen and select one.");
		}
		comp.layout(true, true);
	}

	public void supportChanged()
	{
		mappingComp.dispose();
		fillScreens();
	}
}
