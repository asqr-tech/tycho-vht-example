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
package org.eclipse.vtp.desktop.editors.themes.attraction;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.vtp.desktop.editors.themes.core.ComponentFrame;
import org.eclipse.vtp.desktop.editors.themes.core.ComponentFrameListener;
import org.eclipse.vtp.desktop.editors.themes.core.commands.CommandListener;
import org.eclipse.vtp.desktop.editors.themes.core.commands.ShowProperties;
import org.eclipse.vtp.desktop.model.core.design.IDesignComponent;
import org.eclipse.vtp.desktop.model.core.design.IDesignComponentListener;

/**
 * This is the Attraction theme's implementation of the <code>ComponentFrame</code>
 * interface.  It is the base class for the the connector and element frames of
 * this theme.<br>
 * 
 * This class primarily manages the set of component frame listeners registered
 * with instances, contains a reference to the ui component being represented, 
 * handles default user input behavior, and provides convenience functions for
 * creating color and font objects.
 * 
 */
public abstract class AttractionComponentFrame implements ComponentFrame, IDesignComponentListener, PropertyChangeListener
{
	/**	A list of the component frame listeners registered with this instance */
	private List<ComponentFrameListener> listeners = new ArrayList<ComponentFrameListener>();
	/**	The ui component represented by this instance */
	private IDesignComponent uiComponent = null;
	
	/**
	 * Creates a new instance of this class that will represent the given ui
	 * component.
	 * 
	 * @param uiComponent The ui component being represented
	 */
	protected AttractionComponentFrame(IDesignComponent uiComponent)
	{
		super();
		this.uiComponent = uiComponent;
	}
	
	/**
	 * @return The design component being wrapped by this frame
	 */
	public IDesignComponent getDesignComponent()
	{
		return uiComponent;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.editors.core.theme.ComponentFrame#addListener(org.eclipse.vtp.desktop.editors.core.theme.ComponentFrameListener)
	 */
	public void addListener(ComponentFrameListener listener)
	{
		listeners.remove(listener);
		listeners.add(listener);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.editors.core.theme.ComponentFrame#removeListener(org.eclipse.vtp.desktop.editors.core.theme.ComponentFrameListener)
	 */
	public void removeListener(ComponentFrameListener listener)
	{
		listeners.remove(listener);
	}
	
	/**
	 * Dispatches a change event to all the component frame listeners registered
	 * with this instance.  Each listener is notified of the event in the order
	 * they were registered.
	 */
	protected void fireChange()
	{
		for(ComponentFrameListener listener : listeners)
		{
			listener.componentFrameChanged(this);
		}
	}
	
	/**
	 * Dispatches an event indicating that this component frame is being
	 * deleted.  Each listener is notified of the event in the order they were
	 * registered.  This instance is guaranteed to be valid until this function
	 * finishes.
	 */
	protected void fireDelete()
	{
		for(ComponentFrameListener listener : listeners)
		{
			listener.componentFrameDeleted(this);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.editors.core.model.UIComponentListener#componentChanged(org.eclipse.vtp.desktop.editors.core.model.UIComponent)
	 */
	public void componentChanged(IDesignComponent component)
	{
		this.fireChange();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.editors.core.theme.ComponentFrame#mouseDoubleClick(org.eclipse.vtp.desktop.editors.core.commands.CommandListener, int, int, int)
	 */
	public void mouseDoubleClick(CommandListener commandListener, int x, int y, int modifiers)
	{
		commandListener.executeCommand(new ShowProperties());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.editors.core.model.UIComponentListener#componentDeleted(org.eclipse.vtp.desktop.editors.core.model.UIComponent)
	 */
	public void componentDeleted(IDesignComponent component)
	{
		fireDelete();
	}

	/**
	 * Dispatches a request that this component frame be repainted within its
	 * canvas.  This is to allow internal changes to the component to trigger
	 * a timely visual update.  Each listener is notified of the event in the
	 * order they were registered.
	 */
	protected void fireRepaintRequest()
	{
		for(ComponentFrameListener listener : listeners)
		{
			listener.componentFrameRepaintRequested(this);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.editors.core.theme.ThematicFrame#renderFrame(org.eclipse.swt.graphics.GC, int, int, java.util.Map)
	 */
	public void renderFrame(GC graphicsContext, int stage, int renderFlags, Map<String, Object> resourceMap)
	{
		// TODO Auto-generated method stub
	}
	
	/* (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent event)
	{
		
	}
	
	/**
	 * Attempts to retrieve a color object from the resource cache with the
	 * given name.  If a color object that matches can not be found, a new
	 * object with the given parameters is created and placed into the cache
	 * before being returned to the caller.
	 * 
	 * @param gc The graphics context that owns/should own the object
	 * @param resourceMap The resource cache
	 * @param name The name of the resource
	 * @param r The red value of the color.  0-255
	 * @param g The green value of the color.  0-255
	 * @param b The blue value of the color.  0-255
	 * @return Either the cached color object or a new object with the given parameters
	 */
	protected Color getColor(GC gc, Map<String, Object> resourceMap, String name, int r, int g, int b)
	{
		Object obj = resourceMap.get(name);
		if(obj == null)
		{
			obj = new Color(gc.getDevice(), r, g, b);
			resourceMap.put(name, obj);
		}
		return (Color)obj;
	}

	/**
	 * Attempts to retrieve a font object from the resource cache with the
	 * given name.  If a font object that matches can not be found, a new
	 * object with the given parameters is created and placed into the cache
	 * before being returned to the caller.
	 * 
	 * @param gc The graphics context that owns/should own the object
	 * @param resourceMap The resource cache
	 * @param name The name of the resource
	 * @param fontName The name of the font
	 * @param size The size value of the font
	 * @param style The style of the font
	 * @return Either the cached font object or a new object with the given parameters
	 */
	protected Font getFont(GC gc, Map<String, Object> resourceMap, String name, String fontName, int size, int style)
	{
		Object obj = resourceMap.get(name);
		if(obj == null)
		{
			obj = new Font(gc.getDevice(), fontName, size, style);
			resourceMap.put(name, obj);
		}
		return (Font)obj;
	}

}
