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
package org.eclipse.vtp.desktop.model.core.design;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

public class PaperSizeManager
{
	public static String paperSizeExtensionPointId = "org.eclipse.vtp.desktop.model.core.paperSize";
	private static PaperSizeManager instance = new PaperSizeManager();
	
	public static PaperSizeManager getDefault()
	{
		return instance;
	}
	
	/** a list of available PaperSizes */
	private List<PaperSize> paperSizes;
	
	public PaperSizeManager()
	{
		super();
		paperSizes = new ArrayList<PaperSize>();
		IConfigurationElement[] primitiveExtensions = Platform.getExtensionRegistry().getConfigurationElementsFor(paperSizeExtensionPointId);
		for(int i = 0; i < primitiveExtensions.length; i++)
		{
			String id = primitiveExtensions[i].getAttribute("id");
			String name = primitiveExtensions[i].getAttribute("name");
			int portraitWidth = Integer.parseInt(primitiveExtensions[i].getAttribute("portraitWidth"));
			int portraitHeight = Integer.parseInt(primitiveExtensions[i].getAttribute("portraitHeight"));
			int landscapeWidth = Integer.parseInt(primitiveExtensions[i].getAttribute("landscapeWidth"));
			int landscapeHeight = Integer.parseInt(primitiveExtensions[i].getAttribute("landscapeHeight"));
			PaperSize paperSize = new PaperSize(id, name, portraitWidth, portraitHeight, landscapeWidth, landscapeHeight);
			paperSizes.add(paperSize);
		}
	}

	/**
	 * Returns an unmodifiableList of available PaperSizes
	 * @return a list of PaperSizes
	 */
	public List<PaperSize> getPaperSizes()
	{
		return Collections.unmodifiableList(paperSizes);
	}
	
	/**
	 * Returns the PaperSize with the specified id
	 * @param id - the id of the PaperSize to return
	 * @return - the PaperSize with the specified id
	 */
	public PaperSize getPaperSize(String id)
	{
		for(PaperSize paperSize : paperSizes)
        {
	        if(paperSize.getId().equals(id))
	        	return paperSize;
        }
		return null;
	}
}
