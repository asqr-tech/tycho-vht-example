/*--------------------------------------------------------------------------
 * Copyright (c) 2008 OpenMethods, LLC
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Trip Gilman (OpenMethods)
 *    - initial API and implementation
 -------------------------------------------------------------------------*/
package org.eclipse.vtp.desktop.editors.themes.core.preferences;

import java.util.List;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.vtp.desktop.editors.themes.core.Activator;
import org.eclipse.vtp.desktop.editors.themes.core.Theme;
import org.eclipse.vtp.desktop.editors.themes.core.ThemeManager;

/**
 * The preference page displayed in Eclipse preferences and used to change the active graphical theme.
 */
public class ThemePreferencePage extends PreferencePage implements
        IWorkbenchPreferencePage
{
	/** A list of available themes */
	private List<Theme> themes = ThemeManager.getDefault().getInstalledThemes();
	/** A combo box with which the user selects the a theme to use */
	Combo themeCombo = null;
	
    /**
     * Creates a new preference page with an empty title and no image.
     */
	public ThemePreferencePage()
	{
		super();
	}
	
    /**
     * Creates a new preference page with the given title and no image.
     *
     * @param title the title of this preference page
     */
	public ThemePreferencePage(String title)
	{
		super(title);
	}

    /**
     * Creates a new preference page with the given title and image.
     *
     * @param title the title of this preference page
     * @param image the image for this preference page,
     *  or <code>null</code> if none
     */
	public ThemePreferencePage(String title, ImageDescriptor image)
	{
		super(title, image);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createContents(Composite parent)
	{
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new GridLayout(2, false));
		Label themeLabel = new Label(comp, SWT.NONE);
		themeLabel.setText("Editor Theme:");
		themeLabel.setLayoutData(new GridData());
		
		themeCombo = new Combo(comp, SWT.DROP_DOWN | SWT.READ_ONLY);
		int selected = -1;
		int defaultSelected = -1;
		for(int i = 0; i < themes.size(); i++)
		{
			Theme theme = themes.get(i);
			themeCombo.add(theme.getName());
			if(theme.getId().equals(ThemeManager.getDefault().getDefaultTheme().getId()))
			{
				defaultSelected = i;
			}
			if(theme.getId().equals(ThemeManager.getDefault().getCurrentTheme().getId()))
			{
				selected = i;
			}
		}
		if(selected != -1)
		{
			themeCombo.select(selected);
		}
		else if(defaultSelected != -1)
		{
			themeCombo.select(defaultSelected);
		}
		themeCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		return comp;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench)
	{
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#performOk()
	 */
	public boolean performOk()
	{
		this.getPreferenceStore().setValue("CurrentTheme", themes.get(themeCombo.getSelectionIndex()).getId());
		return super.performOk();
	}
}
