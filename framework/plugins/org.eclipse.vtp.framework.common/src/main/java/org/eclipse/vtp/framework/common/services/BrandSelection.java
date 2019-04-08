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

import org.eclipse.vtp.framework.common.IBrand;
import org.eclipse.vtp.framework.common.IBrandRegistry;
import org.eclipse.vtp.framework.common.IBrandSelection;
import org.eclipse.vtp.framework.common.IScriptable;
import org.eclipse.vtp.framework.core.ISessionContext;

/**
 * A support implementation of the {@link IBrandSelection} interface.
 * 
 * @author Lonnie Pryor
 */
public class BrandSelection implements IBrandSelection, IScriptable
{
	/** The key the selected brand name is stored under. */
	private static final String ATTRIBUTE_KEY = "SelectedBrand"; //$NON-NLS-1$
	/** The context to use. */
	private final ISessionContext context;
	/** The brand registry to use. */
	private final IBrandRegistry brandRegistry;

	/**
	 * Creates a new BrandSelection.
	 * 
	 * @param context The context to use.
	 * @param brandRegistry The brand registry to use.
	 */
	public BrandSelection(ISessionContext context, IBrandRegistry brandRegistry)
	{
		this.context = context;
		this.brandRegistry = brandRegistry;
	}


	/* (non-Javadoc)
	 * @see org.eclipse.vtp.framework.common.IBrandSelection#getSelectedBrand()
	 */
	public final IBrand getSelectedBrand()
	{
		Object name = context.getAttribute(ATTRIBUTE_KEY);
		if (name == null)
			name = context.getInheritedAttribute(ATTRIBUTE_KEY);
		if (name == null)
			return brandRegistry.getDefaultBrand();
		IBrand brand = brandRegistry.getBrand(name.toString());
		if (brand == null)
			return brandRegistry.getDefaultBrand();
		return brand;
	}


	/* (non-Javadoc)
	 * @see org.eclipse.vtp.framework.common.IBrandSelection#setSelectedBrand(org.eclipse.vtp.framework.common.IBrand)
	 */
	public final boolean setSelectedBrand(IBrand brand)
	{
		if (brand == null)
			context.clearAttribute(ATTRIBUTE_KEY);
		else
		{
			IBrand toSelect = brandRegistry.getBrand(brand.getName());
			if (toSelect == null)
				return false;
			context.setAttribute(ATTRIBUTE_KEY, toSelect.getName());
			context.info("Set brand to:" + toSelect.getName());
		}
		return true;
	}


	/* (non-Javadoc)
	 * @see org.eclipse.vtp.framework.common.IScriptable#getName()
	 */
	public final String getName()
	{
		return "SelectedBrand"; //$NON-NLS-1$
	}


	/* (non-Javadoc)
	 * @see org.eclipse.vtp.framework.common.IScriptable#hasValue()
	 */
	public boolean hasValue()
	{
		return true;
	}


	/* (non-Javadoc)
	 * @see org.eclipse.vtp.framework.common.IScriptable#toValue()
	 */
	public Object toValue()
	{
		return getSelectedBrand();
	}


	/* (non-Javadoc)
	 * @see org.eclipse.vtp.framework.common.IScriptable#getFunctionNames()
	 */
	public final String[] getFunctionNames()
	{
		return new String[] {};
	}


	/* (non-Javadoc)
	 * @see org.eclipse.vtp.framework.common.IScriptable#invokeFunction(java.lang.String, java.lang.Object[])
	 */
	public final Object invokeFunction(String name, Object[] arguments)
	{
		return null;
	}


	/* (non-Javadoc)
	 * @see org.eclipse.vtp.framework.common.IScriptable#hasItem(int)
	 */
	public final boolean hasItem(int index)
	{
		return false;
	}


	/* (non-Javadoc)
	 * @see org.eclipse.vtp.framework.common.IScriptable#hasEntry(java.lang.String)
	 */
	public final boolean hasEntry(String name)
	{
		return "value".equals(name); //$NON-NLS-1$
	}
	
	public String[] getPropertyNames()
	{
		return new String[] {"value"};
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.framework.common.IScriptable#getItem(int)
	 */
	public final Object getItem(int index)
	{
		return null;
	}


	/* (non-Javadoc)
	 * @see org.eclipse.vtp.framework.common.IScriptable#getEntry(java.lang.String)
	 */
	public final Object getEntry(String name)
	{
		if ("value".equals(name)) //$NON-NLS-1$
			return getSelectedBrand();
		return null;
	}


	/* (non-Javadoc)
	 * @see org.eclipse.vtp.framework.common.IScriptable#setItem(int, java.lang.Object)
	 */
	public final boolean setItem(int index, Object value)
	{
		return false;
	}


	/* (non-Javadoc)
	 * @see org.eclipse.vtp.framework.common.IScriptable#setEntry(java.lang.String, java.lang.Object)
	 */
	public final boolean setEntry(String name, Object value)
	{
		if ("value".equals(name) && value instanceof IBrand) //$NON-NLS-1$
		{
			setSelectedBrand((IBrand)value);
			return true;
		}
		return false;
	}


	/* (non-Javadoc)
	 * @see org.eclipse.vtp.framework.common.IScriptable#clearItem(int)
	 */
	public final boolean clearItem(int index)
	{
		return false;
	}


	/* (non-Javadoc)
	 * @see org.eclipse.vtp.framework.common.IScriptable#clearEntry(java.lang.String)
	 */
	public final boolean clearEntry(String name)
	{
		if ("value".equals(name)) //$NON-NLS-1$
		{
			setSelectedBrand(null);
			return true;
		}
		return false;
	}


	@Override
	public boolean isMutable()
	{
		// TODO Auto-generated method stub
		return false;
	}
}
