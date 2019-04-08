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
package org.eclipse.vtp.framework.util;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import org.osgi.framework.Constants;
import org.osgi.service.cm.ConfigurationAdmin;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Implementation of a ConfigurationAdmin dictionary that can be loaded and
 * saved to and from an XML structure..
 * 
 * @author Lonnie Pryor
 */
@SuppressWarnings("rawtypes")
public class ConfigurationDictionary extends Dictionary implements Comparator<String>,
		Serializable
{
	/** Set of reserved property keys. */
	private static final Set RESERVED_KEYS = Collections
			.unmodifiableSet(new HashSet<String>(Arrays.asList(new String[] {
					Constants.SERVICE_PID, ConfigurationAdmin.SERVICE_FACTORYPID,
					ConfigurationAdmin.SERVICE_BUNDLELOCATION })));
	/** Index of simple types. */
	private static final Set<Class> SIMPLE_TYPES;
	/** Index of supported types by tag name. */
	private static final Map<String, Class> TYPES_BY_NAME;
	/** Index of supported tag names by type. */
	private static final Map NAMES_BY_TYPE;
	/** The serialization version. */
	private static final long serialVersionUID = 1L;

	/** Initialize the supported type index. */
	static
	{
		Map<String, Class> typesByName = new HashMap<String, Class>(27);
		typesByName.put("boolean", Boolean.class); //$NON-NLS-1$
		typesByName.put("byte", Byte.class); //$NON-NLS-1$
		typesByName.put("short", Short.class); //$NON-NLS-1$
		typesByName.put("character", Character.class); //$NON-NLS-1$
		typesByName.put("integer", Integer.class); //$NON-NLS-1$
		typesByName.put("float", Float.class); //$NON-NLS-1$
		typesByName.put("long", Long.class); //$NON-NLS-1$
		typesByName.put("double", Double.class); //$NON-NLS-1$
		typesByName.put("string", String.class); //$NON-NLS-1$
		SIMPLE_TYPES = Collections
				.unmodifiableSet(new HashSet<Class>(typesByName.values()));
		typesByName.put("booleans", Boolean[].class); //$NON-NLS-1$
		typesByName.put("primitive-booleans", boolean[].class); //$NON-NLS-1$
		typesByName.put("bytes", Byte[].class); //$NON-NLS-1$
		typesByName.put("primitive-bytes", byte[].class); //$NON-NLS-1$
		typesByName.put("shorts", Short[].class); //$NON-NLS-1$
		typesByName.put("primitive-shorts", short[].class); //$NON-NLS-1$
		typesByName.put("characters", Character[].class); //$NON-NLS-1$
		typesByName.put("primitive-characters", char[].class); //$NON-NLS-1$
		typesByName.put("integers", Integer[].class); //$NON-NLS-1$
		typesByName.put("primitive-integers", int[].class); //$NON-NLS-1$
		typesByName.put("floats", Float[].class); //$NON-NLS-1$
		typesByName.put("primitive-floats", float[].class); //$NON-NLS-1$
		typesByName.put("longs", Long[].class); //$NON-NLS-1$
		typesByName.put("primitive-longs", long[].class); //$NON-NLS-1$
		typesByName.put("doubles", Double[].class); //$NON-NLS-1$
		typesByName.put("primitive-doubles", double[].class); //$NON-NLS-1$
		typesByName.put("strings", String[].class); //$NON-NLS-1$
		typesByName.put("vector", Vector.class); //$NON-NLS-1$
		TYPES_BY_NAME = Collections.unmodifiableMap(typesByName);
		Map<Class, String> namesByType = new HashMap<Class, String>(typesByName.size());
		for (Map.Entry<String, Class> entry : typesByName.entrySet())
		{
			namesByType.put(entry.getValue(), entry.getKey());
		}
		NAMES_BY_TYPE = Collections.unmodifiableMap(namesByType);
	}

	/**
	 * Loads all the data from the supplied element into a new array.
	 * 
	 * @param configurations The configurations data to load.
	 * @return The configuration dictionaries loaded from the supplied data.
	 */
	public static ConfigurationDictionary[] loadAll(Element configurations)
	{
		if (configurations == null
				|| !"configurations".equals(configurations.getTagName())) //$NON-NLS-1$
			return null;
		NodeList nodeList = configurations.getChildNodes();
		List<ConfigurationDictionary> dictionaries = new ArrayList<ConfigurationDictionary>(nodeList.getLength());
		ConfigurationDictionary dictionary = null;
		for (int i = 0; i < nodeList.getLength(); ++i)
		{
			Node node = nodeList.item(i);
			if (!(node instanceof Element))
				continue;
			if (dictionary == null)
				dictionary = new ConfigurationDictionary();
			if (!dictionary.load((Element)node))
				continue;
			dictionaries.add(dictionary);
			dictionary = null;
		}
		return dictionaries
				.toArray(new ConfigurationDictionary[dictionaries.size()]);
	}

	/**
	 * Converts the contents of all the supplied dictionaries to an XML structure.
	 * 
	 * @param document The document to use to create the XML structure.
	 * @param dictionaries The dictionaries to save.
	 * @return The XML representation of the contents of the supplied
	 *         dictionaries or <code>null</code> if the supplied document was
	 *         <code>null</code>.
	 */
	public static Element saveAll(Document document,
			ConfigurationDictionary[] dictionaries)
	{
		if (document == null)
			return null;
		Element configurations = document.createElement("configurations"); //$NON-NLS-1$
		if (dictionaries != null)
		{
			for (int i = 0; i < dictionaries.length; ++i)
			{
				if (dictionaries[i] == null)
					continue;
				Element configuration = dictionaries[i].save(document);
				if (configuration != null)
					configurations.appendChild(configuration);
			}
		}
		return configurations;
	}

	/**
	 * Encodes the specified XML element into a supported value.
	 * 
	 * @param property The XML property to decode.
	 * @return A decoded value or <code>null</code> if the XML cannot be
	 *         decoded.
	 */
	@SuppressWarnings("unchecked")
	private static Object decodeValue(Element property)
	{
		if (property == null)
			return null;
		Class type = TYPES_BY_NAME.get(property.getTagName());
		if (type == null)
			return null;
		if (type.equals(Vector.class))
		{
			Element arrayProperty = null;
			NodeList nodeList = property.getChildNodes();
			for (int i = 0; arrayProperty == null && i < nodeList.getLength(); ++i)
				if (nodeList.item(i) instanceof Element)
					arrayProperty = (Element)nodeList.item(i);
			if (arrayProperty == null)
				return new Vector();
			else
			{
				Object value = decodeValue(arrayProperty);
				if (value == null || !value.getClass().isArray()
						|| !SIMPLE_TYPES.contains(value.getClass().getComponentType()))
					return null;
				Object[] array = (Object[])value;
				Vector vector = new Vector(array.length);
				for (int i = 0; i < array.length; ++i)
					vector.add(array[i]);
				return vector;
			}
		}
		else if (type.isArray())
		{
			List items = new LinkedList();
			NodeList nodeList = property.getChildNodes();
			for (int i = 0; i < nodeList.getLength(); ++i)
			{
				if (!(nodeList.item(i) instanceof Element))
					continue;
				Element item = (Element)nodeList.item(i);
				if ("null".equals(item.getTagName()) //$NON-NLS-1$
						&& !type.getComponentType().isPrimitive())
					items.add(null);
				else if ("item".equals(item.getTagName())) //$NON-NLS-1$
				{
					String string = null;
					try
					{
						string = XMLUtilities.getElementTextData(property, true);
					}
					catch (Exception e)
					{
						continue;
					}
					Object value = parse(string, type.getComponentType());
					if (value != null)
						items.add(value);
				}
			}
			Object array = Array.newInstance(type.getComponentType(), items.size());
			int i = 0;
			for (Iterator j = items.iterator(); j.hasNext(); ++i)
				Array.set(array, i, j.next());
			return array;
		}
		else
		{
			String value = null;
			try
			{
				value = XMLUtilities.getElementTextData(property, true);
			}
			catch (Exception e)
			{
				return null;
			}
			return parse(value, type);
		}
	}

	/**
	 * Attempts to parse the supplied string into the specified type.
	 * 
	 * @param value The value to parse.
	 * @param type The type to parse the value into.
	 * @return The parsed value or <code>null</code> if the value could not be
	 *         parsed.
	 */
	private static Object parse(String value, Class type)
	{
		if (value == null || type == null || !type.isPrimitive()
				&& !SIMPLE_TYPES.contains(type))
			return null;
		try
		{
			if (type.equals(Boolean.TYPE) || type.equals(Boolean.class))
				return Boolean.valueOf(value);
			else if (type.equals(Byte.TYPE) || type.equals(Byte.class))
				return Byte.valueOf(value);
			else if (type.equals(Short.TYPE) || type.equals(Short.class))
				return Short.valueOf(value);
			else if (type.equals(Character.TYPE) || type.equals(Character.class))
				return value.length() != 1 ? null : Character.valueOf(value.charAt(0));
			else if (type.equals(Integer.TYPE) || type.equals(Integer.class))
				return Integer.valueOf(value);
			else if (type.equals(Float.TYPE) || type.equals(Float.class))
				return Float.valueOf(value);
			else if (type.equals(Long.TYPE) || type.equals(Long.class))
				return Long.valueOf(value);
			else if (type.equals(Double.TYPE) || type.equals(Double.class))
				return Double.valueOf(value);
			else
				return value;
		}
		catch (NumberFormatException e)
		{
			return null;
		}
	}

	/**
	 * Encodes the specified supported value into an XML element.
	 * 
	 * @param document The document to create elements with.
	 * @param value The value to encode.
	 * @return The new XML element or <code>null</code> if the value was
	 *         <code>null</code>.
	 */
	@SuppressWarnings("unchecked")
	private static Element encodeValue(Document document, Object value)
	{
		if (value == null)
			return null;
		String name = (String)NAMES_BY_TYPE.get(value.getClass());
		if (name == null)
			return null;
		Element property = document.createElement(name);
		if (value instanceof Vector)
		{
			Vector vector = (Vector)value;
			int size = vector.size();
			if (size > 0)
			{
				Object element = null;
				for (int i = 0; element == null && i < size; ++i)
					element = vector.get(i);
				Class componentType = String.class;
				if (element != null)
					componentType = element.getClass();
				property.appendChild(encodeValue(document, vector
						.toArray((Object[])Array.newInstance(componentType, size))));
			}
		}
		else if (value.getClass().isArray())
		{
			int length = Array.getLength(value);
			for (int i = 0; i < length; ++i)
			{
				Object element = Array.get(value, i);
				if (element == null)
					property.appendChild(document.createElement("null")); //$NON-NLS-1$
				else
				{
					Element item = document.createElement("item"); //$NON-NLS-1$
					item.appendChild(document.createTextNode(element.toString()));
					property.appendChild(item);
				}
			}
		}
		else
			property.appendChild(document.createTextNode(value.toString()));
		return property;
	}

	/**
	 * Clones the specified value.
	 * 
	 * @param input The value to clone.
	 * @return The safe copy of the value.
	 */
	@SuppressWarnings("unchecked")
	private static Object cloneValue(Object input)
	{
		if (input == null)
			return null;
		if (input.getClass().equals(Vector.class))
			return new Vector((Vector)input);
		if (!input.getClass().isArray())
			return input;
		int length = Array.getLength(input);
		Object output = Array.newInstance(input.getClass().getComponentType(),
				length);
		System.arraycopy(input, 0, output, 0, length);
		return output;
	}

	/** The properties in this dictionary. */
	private final Map<String, Object> properties = new TreeMap<String, Object>(this);

	/**
	 * Creates a new ConfigurationDictionary.
	 * 
	 */
	public ConfigurationDictionary()
	{
		//TODO find out if this constructor should be removed or shifted to protected TG
	}

	/**
	 * Creates a new ConfigurationDictionary.
	 * 
	 * @param pid The PID of this dictionary.
	 */
	public ConfigurationDictionary(String pid)
	{
		setPid(pid);
	}

	/**
	 * Creates a new ConfigurationDictionary.
	 * 
	 * @param pid The PID of this dictionary.
	 * @param factoryPid The factory PID of this dictionary.
	 */
	public ConfigurationDictionary(String pid, String factoryPid)
	{
		setPid(pid);
		setFactoryPid(factoryPid);
	}

	/**
	 * Creates a new ConfigurationDictionary.
	 * 
	 * @param pid The PID of this dictionary.
	 * @param factoryPid The factory PID of this dictionary.
	 * @param bundleLocation The bundle location of this dictionary.
	 */
	public ConfigurationDictionary(String pid, String factoryPid,
			String bundleLocation)
	{
		setPid(pid);
		setFactoryPid(factoryPid);
		setBundleLocation(bundleLocation);
	}

	/**
	 * Creates a new ConfigurationDictionary.
	 * 
	 * @param configuration The configuration data for this dictionary's
	 *          properties.
	 * @throws NullPointerException If the supplied configuration is
	 *           <code>null</code>.
	 */
	public ConfigurationDictionary(Element configuration)
			throws NullPointerException
	{
		load(configuration);
	}

	/**
	 * Returns the value of the "service.pid" property.
	 * 
	 * @return The value of the "service.pid" property.
	 */
	public synchronized String getPid()
	{
		return (String)properties.get(Constants.SERVICE_PID);
	}

	/**
	 * Returns the value of the "service.factoryPid" property.
	 * 
	 * @return The value of the "service.factoryPid" property.
	 */
	public synchronized String getFactoryPid()
	{
		return (String)properties.get(ConfigurationAdmin.SERVICE_FACTORYPID);
	}

	/**
	 * Returns the value of the "service.bundleLocation" property.
	 * 
	 * @return The value of the "service.bundleLocation" property.
	 */
	public synchronized String getBundleLocation()
	{
		return (String)properties.get(ConfigurationAdmin.SERVICE_BUNDLELOCATION);
	}

	/**
	 * Sets the value of the "service.pid" property.
	 * 
	 * @param pid The value of the "service.pid" property.
	 */
	public synchronized void setPid(String pid)
	{
		if (pid == null || pid.length() == 0)
			properties.remove(Constants.SERVICE_PID);
		else
			properties.put(Constants.SERVICE_PID, pid);
	}

	/**
	 * Sets the value of the "service.factoryPid" property.
	 * 
	 * @param factoryPid The value of the "service.factoryPid" property.
	 */
	public synchronized void setFactoryPid(String factoryPid)
	{
		if (factoryPid == null || factoryPid.length() == 0)
			properties.remove(ConfigurationAdmin.SERVICE_FACTORYPID);
		else
			properties.put(ConfigurationAdmin.SERVICE_FACTORYPID, factoryPid);
	}

	/**
	 * Sets the value of the "service.bundleLocation" property.
	 * 
	 * @param bundleLocation The value of the "service.bundleLocation" property.
	 */
	public synchronized void setBundleLocation(String bundleLocation)
	{
		if (bundleLocation == null || bundleLocation.length() == 0)
			properties.remove(ConfigurationAdmin.SERVICE_BUNDLELOCATION);
		else
			properties.put(ConfigurationAdmin.SERVICE_BUNDLELOCATION, bundleLocation);
	}

	/**
	 * Loads the data from the supplied element into this dictionary.
	 * 
	 * @param configuration The configuration data to load or <code>null</code>
	 *          to clear this dictionary.
	 * @return True if the data in this dictionary changed.
	 */
	public synchronized boolean load(Element configuration)
	{
		if (configuration == null)
		{
			properties.clear();
			return true;
		}
		if (!"configuration".equals(configuration.getTagName())) //$NON-NLS-1$
			return false;
		properties.clear();
		setPid(configuration.getAttribute("pid")); //$NON-NLS-1$
		setFactoryPid(configuration.getAttribute("factoryPid")); //$NON-NLS-1$
		setBundleLocation(configuration.getAttribute("bundleLocation")); //$NON-NLS-1$
		NodeList list = configuration.getChildNodes();
		for (int i = 0; i < list.getLength(); ++i)
		{
			if (!(list.item(i) instanceof Element))
				continue;
			Element property = (Element)list.item(i);
			String key = property.getAttribute("key"); //$NON-NLS-1$
			if (key == null || key.length() == 0 || RESERVED_KEYS.contains(key))
				continue;
			Object value = decodeValue(property);
			if (value == null)
				continue;
			properties.put(key, value);
		}
		return true;
	}

	/**
	 * Converts the contents of this dictionary to an XML structure.
	 * 
	 * @param document The document to use to create the XML structure.
	 * @return The XML representation of the contents of this dictionary or
	 *         <code>null</code> if the supplied document was <code>null</code>.
	 */
	public synchronized Element save(Document document)
	{
		if (document == null)
			return null;
		Element configuration = document.createElement("configuration"); //$NON-NLS-1$
		String pid = getPid();
		if (pid != null)
			configuration.setAttribute("pid", pid); //$NON-NLS-1$
		String factoryPid = getFactoryPid();
		if (factoryPid != null)
			configuration.setAttribute("factoryPid", factoryPid); //$NON-NLS-1$
		String bundleLocation = getBundleLocation();
		if (bundleLocation != null)
			configuration.setAttribute("bundleLocation", bundleLocation); //$NON-NLS-1$
		for (Iterator i = properties.entrySet().iterator(); i.hasNext();)
		{
			Map.Entry entry = (Map.Entry)i.next();
			String key = (String)entry.getKey();
			if (RESERVED_KEYS.contains(key))
				continue;
			Element property = encodeValue(document, entry.getValue());
			if (property == null)
				continue;
			property.setAttribute("key", key); //$NON-NLS-1$
			configuration.appendChild(property);
		}
		return configuration;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Dictionary#isEmpty()
	 */
	public synchronized boolean isEmpty()
	{
		return properties.isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Dictionary#size()
	 */
	public synchronized int size()
	{
		return properties.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Dictionary#keys()
	 */
	@SuppressWarnings("unchecked")
	public synchronized Enumeration keys()
	{
		return Collections.enumeration(new ArrayList(properties.keySet()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Dictionary#elements()
	 */
	@SuppressWarnings("unchecked")
	public synchronized Enumeration elements()
	{
		final Enumeration e = Collections.enumeration(new ArrayList(properties
				.values()));
		return new Enumeration()
		{
			public boolean hasMoreElements()
			{
				return e.hasMoreElements();
			}

			public Object nextElement()
			{
				return cloneValue(e.nextElement());
			}
		};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Dictionary#get(java.lang.Object)
	 */
	public synchronized Object get(Object key)
	{
		if (key == null)
			throw new NullPointerException("key"); //$NON-NLS-1$
		if (!(key instanceof String) || ((String)key).length() == 0)
			throw new IllegalArgumentException("key"); //$NON-NLS-1$
		return cloneValue(properties.get(key));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Dictionary#put(java.lang.Object, java.lang.Object)
	 */
	public synchronized Object put(Object key, Object value)
	{
		if (key == null)
			throw new NullPointerException("key"); //$NON-NLS-1$
		if (!(key instanceof String) || ((String)key).length() == 0)
			throw new IllegalArgumentException("key"); //$NON-NLS-1$
		if (value == null)
			throw new NullPointerException("value"); //$NON-NLS-1$
		if (!NAMES_BY_TYPE.containsKey(value.getClass()))
			throw new IllegalArgumentException("value"); //$NON-NLS-1$
		if (RESERVED_KEYS.contains(key))
			return cloneValue(properties.get(key));
		// Verify that all non-null elements in vectors are of the same simple type.
		if (value instanceof Vector)
		{
			Vector v = (Vector)value;
			int size = v.size();
			if (size > 0)
			{
				Object element = null;
				int index = 0;
				for (; element == null && index < size; ++index)
					element = v.get(index);
				if (element != null)
				{
					Class type = element.getClass();
					if (!SIMPLE_TYPES.contains(type))
						throw new IllegalArgumentException("value[" + //$NON-NLS-1$
								index + "]"); //$NON-NLS-1$
					for (++index; index < size; ++index)
					{
						Object other = v.get(index);
						if (other == null)
							continue;
						if (!type.equals(other.getClass()))
							throw new IllegalArgumentException("value[" + //$NON-NLS-1$
									index + "]"); //$NON-NLS-1$
					}
				}
			}
		}
		return properties.put((String)key, cloneValue(value));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Dictionary#remove(java.lang.Object)
	 */
	public synchronized Object remove(Object key)
	{
		if (key == null)
			throw new NullPointerException("key"); //$NON-NLS-1$
		if (!(key instanceof String) || ((String)key).length() == 0)
			throw new IllegalArgumentException("key"); //$NON-NLS-1$
		if (RESERVED_KEYS.contains(key))
			return cloneValue(properties.get(key));
		return cloneValue(properties.remove(key));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(String left, String right)
	{
		return left.compareToIgnoreCase(right);
	}
}
