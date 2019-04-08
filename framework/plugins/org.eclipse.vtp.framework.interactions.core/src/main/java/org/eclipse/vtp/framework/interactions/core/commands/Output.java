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
public class Output
{
	public static final String TYPE_FILE = "org.eclipse.vtp.output.File";
	public static final String TYPE_TEXT = "org.eclipse.vtp.output.Text";
	
	private String type;
	private Map<String, String> properties = new HashMap<String, String>();
	
	public Output(String type)
	{
		super();
		this.type = type;
	}

	public String getType()
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
