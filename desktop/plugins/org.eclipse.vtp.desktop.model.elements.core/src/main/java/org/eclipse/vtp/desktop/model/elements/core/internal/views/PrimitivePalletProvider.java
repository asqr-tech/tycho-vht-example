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
package org.eclipse.vtp.desktop.model.elements.core.internal.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.vtp.desktop.core.Activator;
import org.eclipse.vtp.desktop.model.core.design.IDesign;
import org.eclipse.vtp.desktop.model.elements.core.internal.PrimitiveElementFactory;
import org.eclipse.vtp.desktop.model.elements.core.internal.PrimitiveElementManager;
import org.eclipse.vtp.desktop.views.pallet.PalletItem;
import org.eclipse.vtp.desktop.views.pallet.PalletItemObserver;
import org.eclipse.vtp.desktop.views.pallet.PalletItemProvider;
import org.osgi.framework.Bundle;

public class PrimitivePalletProvider implements PalletItemProvider
{
	List<PalletItem> primitiveItems = new ArrayList<PalletItem>();

	public PrimitivePalletProvider()
	{
		super();
		IConfigurationElement[] primitiveExtensions = Platform
				.getExtensionRegistry().getConfigurationElementsFor(
						PrimitiveElementManager.primitiveExtensionPointId);
		for (int i = 0; i < primitiveExtensions.length; i++)
		{
			String id = primitiveExtensions[i].getAttribute("id");
			String name = primitiveExtensions[i].getAttribute("name");
			boolean popOnDrop = false;
			String popOnDropString = primitiveExtensions[i]
					.getAttribute("pop-on-drop");
			if (popOnDropString != null)
			{
				popOnDrop = Boolean.parseBoolean(popOnDropString);
			}
			Bundle contributor = Platform.getBundle(primitiveExtensions[i]
					.getContributor().getName());
			String className = primitiveExtensions[i].getAttribute("filter");
			if (className != null)
			{
				try
				{
					@SuppressWarnings("unchecked")
					Class<PalletItemFilter> filterClass = (Class<PalletItemFilter>)contributor.loadClass(className);
					final PalletItemFilter filter = filterClass.newInstance();
					PalletItem palletItem = new PalletItem(name,
							Activator.getDefault().getImageRegistry().get(id),
							new PrimitiveElementFactory(),
							id)
					{
						public boolean canBeContainedBy(IDesign container)
						{
							return filter.canBeContainedBy(container);
						}
					};
					palletItem.setPopOnDrop(popOnDrop);
					primitiveItems.add(palletItem);
				}
				catch (Exception e)
				{
					e.printStackTrace();
					continue;
				}
			}
			else
			{
				PalletItem palletItem = new PalletItem(name,
						org.eclipse.vtp.desktop.core.Activator.getDefault()
								.getImageRegistry().get(id), new PrimitiveElementFactory(), id);
				palletItem.setPopOnDrop(popOnDrop);
				primitiveItems.add(palletItem);
			}
		}
	}

	public String getName()
	{
		return "Common";
	}

	public List<PalletItem> getPalletItems()
	{
		return primitiveItems;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.desktop.views.pallet.PalletItemProvider#getRanking()
	 */
	public int getRanking()
	{
		return 10;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.desktop.views.pallet.PalletItemProvider#createMenu(
	 *      org.eclipse.jface.action.IMenuManager,
	 *      org.eclipse.vtp.desktop.views.pallet.PalletItem[])
	 */
	public void createMenu(IAdaptable container, IMenuManager manager, PalletItem[] selectedItems)
	{
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.desktop.views.pallet.PalletItemProvider#
	 *      addPalletItemObserver(
	 *      org.eclipse.vtp.desktop.views.pallet.PalletItemObserver)
	 */
	public void addPalletItemObserver(PalletItemObserver observer)
	{
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.desktop.views.pallet.PalletItemProvider#
	 *      removePalletItemObserver(
	 *      org.eclipse.vtp.desktop.views.pallet.PalletItemObserver)
	 */
	public void removePalletItemObserver(PalletItemObserver observer)
	{
		// TODO Auto-generated method stub

	}

}
