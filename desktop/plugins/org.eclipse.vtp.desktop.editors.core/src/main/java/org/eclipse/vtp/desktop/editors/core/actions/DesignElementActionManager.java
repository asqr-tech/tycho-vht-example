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
package org.eclipse.vtp.desktop.editors.core.actions;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.vtp.desktop.editors.themes.core.commands.CommandListener;
import org.eclipse.vtp.desktop.model.core.design.IDesignElement;
import org.osgi.framework.Bundle;

public class DesignElementActionManager
{
	public static String designElementActionExtensionPointId = "org.eclipse.vtp.desktop.editors.core.designElementAction";
	private static DesignElementActionManager instance = new DesignElementActionManager();
	
	public static DesignElementActionManager getDefault()
	{
		return instance;
	}
	
	private List<ActionRecord> actionRecords;
	
	public DesignElementActionManager()
	{
		super();
		actionRecords = new ArrayList<ActionRecord>();
		IConfigurationElement[] primitiveExtensions = Platform.getExtensionRegistry().getConfigurationElementsFor(designElementActionExtensionPointId);
		for(int i = 0; i < primitiveExtensions.length; i++)
		{
			ActionRecord ar = new ActionRecord();
			String filterClassName = primitiveExtensions[i].getAttribute("filter-class");
			String actionClassName = primitiveExtensions[i].getAttribute("action-class");
			Bundle contributor = Platform.getBundle(primitiveExtensions[i].getContributor().getName());
			try
			{
				@SuppressWarnings("unchecked")
				Class<DesignElementActionFilter> filterClass = (Class<DesignElementActionFilter>)contributor.loadClass(filterClassName);
				ar.filter = filterClass.newInstance();
				@SuppressWarnings("unchecked")
				Class<DesignElementAction> actionClass = (Class<DesignElementAction>)contributor.loadClass(actionClassName);
				ar.actionClass = actionClass;
				ar.con = actionClass.getConstructor(IDesignElement.class, CommandListener.class);
				actionRecords.add(ar);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public List<DesignElementAction> getActions(IDesignElement element, CommandListener commandListener)
	{
		List<DesignElementAction> ret = new LinkedList<DesignElementAction>();
		for(ActionRecord ar : actionRecords)
		{
			if(ar.filter.isApplicable(element))
			{
				ret.add(ar.createAction(element, commandListener));
			}
		}
		return ret;
	}

	private class ActionRecord
	{
		@SuppressWarnings("unused")
		Class<DesignElementAction> actionClass = null;
		Constructor<DesignElementAction> con = null;
		DesignElementActionFilter filter = null;
		
		DesignElementAction createAction(IDesignElement element, CommandListener commandListener)
		{
			try
			{
				return con.newInstance(element, commandListener);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			return null;
		}
	}
}
