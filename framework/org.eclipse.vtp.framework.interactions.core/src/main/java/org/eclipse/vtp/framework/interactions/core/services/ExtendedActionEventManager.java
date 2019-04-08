package org.eclipse.vtp.framework.interactions.core.services;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

public class ExtendedActionEventManager
{
	/**	The identifier for the event extension point */
	public static String eventExtensionPointId = "org.eclipse.vtp.framework.interactions.core.extendedEvents";
	/**	The shared instance of the event manager */
	private static ExtendedActionEventManager instance = new ExtendedActionEventManager();
	
	/**
	 * @return The shared instance of the event manager
	 */
	public static ExtendedActionEventManager getDefault()
	{
		return instance;
	}
	
	/**	Indexes the currently registered events */
	private List<String> extendedEvents;
	
	/**
	 * Constructs a new <code>ExtendedActionEventManager</code>.
	 */
	public ExtendedActionEventManager()
	{
		super();
		extendedEvents = new ArrayList<String>();
		IConfigurationElement[] extensions = Platform.getExtensionRegistry().getConfigurationElementsFor(eventExtensionPointId);
		for(int i = 0; i < extensions.length; i++)
		{
			String name = extensions[i].getAttribute("name");
			extendedEvents.add(name);
		}
	}
	
	/**
	 * Returns a list of extended events for the action with the given id
	 * @return the list of events
	 */
	public List<String> getExtendedEvents()
	{
		List<String> ret = new ArrayList<String>();
		ret.addAll(extendedEvents);
		return ret;
	}
	
}
