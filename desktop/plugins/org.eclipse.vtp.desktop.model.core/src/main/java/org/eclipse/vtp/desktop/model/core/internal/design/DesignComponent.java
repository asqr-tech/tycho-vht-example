/*--------------------------------------------------------------------------
 * Copyright (c) 2004, 2006-2009 OpenMethods, LLC
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Trip Gilman (OpenMethods), Lonnie G. Pryor (OpenMethods)
 *    - initial API and implementation
 -------------------------------------------------------------------------*/
package org.eclipse.vtp.desktop.model.core.internal.design;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.vtp.desktop.model.core.design.IDesignComponentListener;
import org.eclipse.vtp.desktop.model.core.design.IDesignComponent;
import org.eclipse.vtp.desktop.model.core.design.IDesignConstants;
import org.eclipse.vtp.framework.util.Guid;

public abstract class DesignComponent implements IDesignComponent
{
	private String id;
	private Design design;
	List<IDesignComponentListener> listeners = new LinkedList<IDesignComponentListener>();
	List<PropertyChangeListener> propertyListeners = new LinkedList<PropertyChangeListener>();
	
	public DesignComponent()
	{
		super();
		this.id = Guid.createGUID();
	}
	
	/**
	 * @param id
	 */
	public DesignComponent(String id)
	{
		super();
		this.id = id;
	}
	
	/**
	 * @return
	 */
	public Design getDesign()
	{
		return design;
	}
	
	/**
	 * @param design
	 */
	public void setDesign(Design design)
	{
		this.design = design;
	}
	
	/**
	 * @return
	 */
	public String getId()
	{
		return id;
	}
	
	/**
	 * @param id
	 */
	protected void setId(String id)
	{
		System.out.println("Changing the id of: " + this + " to " + id);
		String oldId = this.id;
		this.id = id;
		this.firePropertyChange(IDesignConstants.PROP_ID, oldId, id);
	}
	
	/**
	 * @param listener
	 */
	public void addListener(IDesignComponentListener listener)
	{
		listeners.remove(listener);
		listeners.add(listener);
	}
	
	/**
	 * @param listener
	 */
	public void removeListener(IDesignComponentListener listener)
	{
		listeners.remove(listener);
	}
	
	/**
	 * @param listener
	 */
	public void addPropertyListener(PropertyChangeListener listener)
	{
		propertyListeners.remove(listener);
		propertyListeners.add(listener);
	}
	
	/**
	 * @param listener
	 */
	public void removePropertyListener(PropertyChangeListener listener)
	{
		propertyListeners.remove(listener);
	}
	
	/**
	 * @param propertyName
	 * @param oldValue
	 * @param newValue
	 */
	public void firePropertyChange(String propertyName, Object oldValue, Object newValue)
	{
		PropertyChangeEvent event = new PropertyChangeEvent(this, propertyName, oldValue, newValue);
		for(PropertyChangeListener listener : propertyListeners)
		{
			listener.propertyChange(event);
		}
	}
	
	protected void fireChange()
	{
		for(IDesignComponentListener listener : listeners)
		{
			listener.componentChanged(this);
		}
	}
	
	protected void fireDelete()
	{
		for(IDesignComponentListener listener : listeners)
		{
			listener.componentDeleted(this);
		}
	}
	
	protected void delete()
	{
		fireDelete();
	}
}
