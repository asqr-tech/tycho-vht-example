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
package org.eclipse.vtp.desktop.views.preferences;

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
import org.eclipse.vtp.desktop.views.Activator;
import org.eclipse.vtp.desktop.views.pallet.PalletManager;
import org.eclipse.vtp.desktop.views.pallet.PalletManager.PalletRecord;

public class PalletPreferencePage extends PreferencePage implements
        IWorkbenchPreferencePage
{
	private List<PalletRecord> pallets = PalletManager.getDefault().getInstalledPallets();
	Combo palletCombo = null;
	
	public PalletPreferencePage()
	{
		super();
	}
	
	public PalletPreferencePage(String title)
	{
		super(title);
	}

	public PalletPreferencePage(String title, ImageDescriptor image)
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
		
		palletCombo = new Combo(comp, SWT.DROP_DOWN | SWT.READ_ONLY);
		int selected = -1;
		int defaultSelected = -1;
		for(int i = 0; i < pallets.size(); i++)
		{
			PalletRecord pallet = pallets.get(i);
			palletCombo.add(pallet.getName());
			if(pallet.getId().equals(PalletManager.defaultPalletId))
			{
				defaultSelected = i;
			}
			if(pallet.getId().equals(PalletManager.getDefault().getCurrentPallet()))
			{
				selected = i;
			}
		}
		if(selected != -1)
		{
			palletCombo.select(selected);
		}
		else if(defaultSelected != -1)
		{
			palletCombo.select(defaultSelected);
		}
		palletCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
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
		this.getPreferenceStore().setValue("CurrentPallet", pallets.get(palletCombo.getSelectionIndex()).getId());
		return super.performOk();
	}
}
