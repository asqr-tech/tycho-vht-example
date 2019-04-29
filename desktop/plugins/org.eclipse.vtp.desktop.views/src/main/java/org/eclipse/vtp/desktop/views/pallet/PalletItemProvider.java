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
package org.eclipse.vtp.desktop.views.pallet;

import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IMenuManager;

public interface PalletItemProvider
{
	String getName();

	int getRanking();

	List<PalletItem> getPalletItems();

	void createMenu(IAdaptable container, IMenuManager manager,
			PalletItem[] selectedItems);

	void addPalletItemObserver(PalletItemObserver observer);

	void removePalletItemObserver(PalletItemObserver observer);
}
