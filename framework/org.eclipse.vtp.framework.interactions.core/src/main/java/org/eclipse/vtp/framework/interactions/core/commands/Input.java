/**
 * 
 */
package org.eclipse.vtp.framework.interactions.core.commands;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author trip
 *
 */
public class Input
{
	private int type = 0;
	private Map<String, String> properties = new HashMap<String, String>();
	
	/**
	 * 
	 */
	public Input(int type)
	{
		super();
		this.type = type;
	}

	public int getType()
	{
		return type;
	}

	public String getProperty(String key)
	{
		return properties.get(key);
	}
	
	public void setProperty(String key, String value)
	{
		properties.put(key, value);
	}
	
	public Set<String> getPropertyNames()
	{
		return properties.keySet();
	}
}
