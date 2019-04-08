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
package org.eclipse.vtp.framework.common.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.vtp.framework.common.IBrand;
import org.eclipse.vtp.framework.common.IBrandRegistry;
import org.eclipse.vtp.framework.common.IScriptable;
import org.eclipse.vtp.framework.common.configurations.BrandConfiguration;
import org.eclipse.vtp.framework.common.support.ScriptableArray;

/**
 * Implementation of {@link IBrandRegistry}.
 * 
 * @author Lonnie Pryor
 */
public class BrandRegistry implements IBrandRegistry, IScriptable
{
	/** The default brand instance. */
	private final Brand defaultBrand;
	/** The brand instances by name. */
	private final Map<String, Brand> brandsByName;
	private final Map<String, Brand> brandsById;

	/**
	 * Creates a new BrandRegistry.
	 * 
	 * @param configuration The configuration of the root brand.
	 */
	public BrandRegistry(BrandConfiguration configuration)
	{
		this.defaultBrand = new Brand(configuration, null);
		Map<String, Brand> brandsByName = new HashMap<String, Brand>();
		Map<String, Brand> brandsById = new HashMap<String, Brand>();
		LinkedList<Brand> toIndex = new LinkedList<Brand>();
		toIndex.add(defaultBrand);
		while (!toIndex.isEmpty())
		{
			Brand brand = toIndex.removeFirst();
			brandsByName.put(brand.getName(), brand);
			brandsById.put(brand.getId(), brand);
			for (int i = 0; i < brand.children.length; ++i)
				toIndex.addLast(brand.children[i]);
		}
		this.brandsByName = Collections.unmodifiableMap(new HashMap<String, Brand>(brandsByName));
		this.brandsById = Collections.unmodifiableMap(new HashMap<String, Brand>(brandsById));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IBrandRegistry#getDefaultBrand()
	 */
	public IBrand getDefaultBrand()
	{
		return defaultBrand;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IBrandRegistry#getBrand(
	 *      java.lang.String)
	 */
	public IBrand getBrand(String name)
	{
		return brandsByName.get(name);
	}
	
	public IBrand getBrandById(String id)
	{
		return brandsById.get(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#getName()
	 */
	public final String getName()
	{
		return "Brands"; //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#hasValue()
	 */
	public boolean hasValue()
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#toValue()
	 */
	public Object toValue()
	{
		return null;
	}

	public String[] getPropertyNames()
	{
		List<String> propNames = new ArrayList<String>();
		for(Map.Entry<String, Brand> entry : brandsByName.entrySet())
		{
			propNames.add(entry.getKey());
		}
		return propNames.toArray(new String[brandsByName.size()]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#getFunctionNames()
	 */
	public final String[] getFunctionNames()
	{
		return new String[] {"getBrand"};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#invokeFunction(
	 *      java.lang.String, java.lang.Object[])
	 */
	public final Object invokeFunction(String name, Object[] arguments)
	{
		if("getBrand".equals(name) && arguments != null && arguments.length > 0 && arguments[0] != null)
		{
			Brand brand = defaultBrand;
			String nameString = arguments[0].toString();
			String[] parts = nameString.split("[\\./\\\\]");
outer:		for(int i = 0; i < parts.length; i++)
			{
				if(i == 0 && parts[i].equals("Default"))
					continue;
				for(Brand b : brand.children)
					if(b.getName().equals(parts[i]))
					{
						brand = b;
						continue outer;
					}
				return null;
			}
			return brand;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#hasItem(int)
	 */
	public final boolean hasItem(int index)
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#hasProperty(
	 *      java.lang.String)
	 */
	public final boolean hasEntry(String name)
	{
		return brandsByName.containsKey(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#getItem(int)
	 */
	public final Object getItem(int index)
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#getProperty(
	 *      java.lang.String)
	 */
	public final Object getEntry(String name)
	{
		return brandsByName.get(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#setItem(int,
	 *      java.lang.Object)
	 */
	public final boolean setItem(int index, Object value)
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#setProperty(
	 *      java.lang.String, java.lang.Object)
	 */
	public final boolean setEntry(String name, Object value)
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#clearItem(int)
	 */
	public final boolean clearItem(int index)
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#clearProperty(
	 *      java.lang.String)
	 */
	public final boolean clearEntry(String name)
	{
		return false;
	}

	/**
	 * Implementation of {@link IBrand}.
	 * 
	 * @author Lonnie Pryor
	 */
	private static final class Brand implements IBrand, IScriptable
	{
		final String id;
		/** The name of this brand. */
		final String name;
		/** The parent of this brand. */
		final Brand parent;
		/** The children of this brand. */
		final Brand[] children;
		/** The children of this brand as a scriptable array. */
		final ScriptableArray childArray;

		/**
		 * Creates a new Brand.
		 * 
		 * @param configuration The configuration of this brand.
		 * @param parent The parent of this brand.
		 */
		Brand(BrandConfiguration configuration, Brand parent)
		{
			this.id = configuration.getId();
			this.name = configuration.getName();
			this.parent = parent;
			BrandConfiguration[] childConfigss = configuration.getChildren();
			if (childConfigss != null)
			{
				this.children = new Brand[childConfigss.length];
				for (int i = 0; i < childConfigss.length; ++i)
					this.children[i] = new Brand(childConfigss[i], this);
			}
			else
				this.children = new Brand[0];
			this.childArray = new ScriptableArray("children", this.children); //$NON-NLS-1$
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.core.IBrand#isDefault()
		 */
		public boolean isDefault()
		{
			return parent == null;
		}
		
		public String getId()
		{
			return id;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.core.IBrand#getName()
		 */
		public String getName()
		{
			return name;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.core.IBrand#getParentBrand()
		 */
		public IBrand getParentBrand()
		{
			return parent;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.core.IBrand#getChildBrands()
		 */
		public IBrand[] getChildBrands()
		{
			IBrand[] brands = new IBrand[children.length];
			System.arraycopy(children, 0, brands, 0, children.length);
			return brands;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#hasValue()
		 */
		public boolean hasValue()
		{
			return true;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#toValue()
		 */
		public Object toValue()
		{
			return name;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#getFunctionNames()
		 */
		public String[] getFunctionNames()
		{
			return new String[] {};
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#invokeFunction(
		 *      java.lang.String, java.lang.Object[])
		 */
		public Object invokeFunction(String name, Object[] arguments)
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#hasItem(int)
		 */
		public boolean hasItem(int index)
		{
			return false;
		}

		public String[] getPropertyNames()
		{
			return new String[] {"name", "id", "parent", "children"};
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#hasProperty(
		 *      java.lang.String)
		 */
		public boolean hasEntry(String name)
		{
			return "name".equals(name) //$NON-NLS-1$
					|| "id".equals(name)
					|| "parent".equals(name) //$NON-NLS-1$
					|| "children".equals(name); //$NON-NLS-1$
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#getItem(int)
		 */
		public Object getItem(int index)
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#getProperty(
		 *      java.lang.String)
		 */
		public Object getEntry(String name)
		{
			if ("name".equals(name)) //$NON-NLS-1$
				return this.name;
			if ("id".equals(name)) //$NON-NLS-1$
				return id;
			if ("parent".equals(name)) //$NON-NLS-1$
				return parent;
			if ("children".equals(name)) //$NON-NLS-1$
				return childArray;
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#setItem(int,
		 *      java.lang.Object)
		 */
		public boolean setItem(int index, Object value)
		{
			return false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#setProperty(
		 *      java.lang.String, java.lang.Object)
		 */
		public boolean setEntry(String name, Object value)
		{
			return false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#clearItem(int)
		 */
		public boolean clearItem(int index)
		{
			return false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#clearProperty(
		 *      java.lang.String)
		 */
		public boolean clearEntry(String name)
		{
			return false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		public String toString()
		{
			return name;
		}

		@Override
		public boolean isMutable()
		{
			return false;
		}
	}
	
	public static void main(String[] args)
	{
		String nameString = "brand1.brand2/brand3\\brand4";
		String[] parts = nameString.split("[\\./\\\\]");
		for(String part : parts)
		{
			System.out.println(part);
		}
	}

	@Override
	public boolean isMutable()
	{
		return false;
	}
}
