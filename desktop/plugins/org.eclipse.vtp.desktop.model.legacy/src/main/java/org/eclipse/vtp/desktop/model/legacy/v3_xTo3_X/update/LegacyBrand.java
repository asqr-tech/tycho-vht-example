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
package org.eclipse.vtp.desktop.model.legacy.v3_xTo3_X.update;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A brand represents a layer of configuration within an application.  Brands
 * are arranged into a hierarchy and support a form of inheritance.  Typically,
 * child brands should inherit any configuration or settings available to their
 * parent brand.  There is no limit to the number of branches in the brand
 * structure.
 * 
 * This class holds the information for a single brand.  It tracks the parent
 * brand and any children this brand has.  It also maintains an index of the
 * media providers available to this brand.
 * 
 * @author trip
 */
public class LegacyBrand
{
	/**	The unique identifier for this brand */
	private String id;
	/**	The human readable name of this brand */
	private String name;
	/**	The parent of this brand */
	private LegacyBrand parentBrand;
	/**	The list of brands who are children to this brand */
	private List<LegacyBrand> childBrands;
	/**	The brand manager responsible for resolving the brand structure */
	private LegacyBrandManager manager;
	
	/**
	 * Creates a new brand with the given unique identifier and name.  The brand
	 * initially has no parent, media providers, or child brands.
	 * 
	 * @param id The unique identifier for this brand
	 * @param name The human readable name for this brand
	 */
	public LegacyBrand(String id, String name)
	{
		super();
		this.id = id;
		this.name = name;
		childBrands = new ArrayList<LegacyBrand>();
	}
	
	/**
	 * @return The unique identifier for this brand
	 */
	public String getId()
	{
		return id;
	}
	
	/**
	 * @return the human readable name for this brand
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Sets the name of this brand to the given value.  If the new name is equal
	 * to the old name, no action is taken.  Otherwise, the new name is made the
	 * current name and a property change event is propagated to this brand's
	 * manager.
	 * 
	 * @param newName The new name for this brand
	 */
	public void setName(String newName)
	{
		if(newName.equals(name))
			return;
		name = newName;
	}
	
	/**
	 * @return This brand's parent brand or null if this brand has no parent.
	 */
	public LegacyBrand getParent()
	{
		return parentBrand;
	}
	
	/**
	 * Sets the given brand as the parent of this brand.  If the current parent
	 * is the same as the new parent, no action is taken.  Prior to setting the
	 * new parent, the brand hierarchy is checked to ensure a cycle has not been
	 * introduced.  If a cycle is detected, an illegal argument exception is
	 * thrown.  This brand is added to the parent brand as a child.  If this
	 * brand did not have a parent previously, a brand added event is dispatched
	 * to the brand manager.  Otherwise, a brand parent changed event is
	 * delivered instead.
	 * 
	 * @param brand The new parent of this brand
	 * @throws IllegalArgumentException If the parent brand is null or applying
	 * the new structure would result in a cycle in the hierarchy.
	 */
	public void setParent(LegacyBrand brand)
	{
		if(brand == parentBrand) //no change happened
			return;
		//check for circular references
		checkParent(brand);
		this.parentBrand = brand;
		parentBrand.addChild(this);
	}
	
	/**
	 * @return The list of brands contained by this brand in the order they were
	 * added.
	 */
	public List<LegacyBrand> getChildBrands()
	{
		return Collections.unmodifiableList(childBrands);
	}
	
	/**
	 * Adds the given brand to this brand as a child.  If the brand was already
	 * a child of this brand, it is first removed and then added at the end of
	 * the list of child brands.  The manager which contains this brand is set
	 * into the child brand.
	 * 
	 * @param child The child brand to add
	 */
	protected void addChild(LegacyBrand child)
	{
		this.childBrands.remove(child);
		this.childBrands.add(child);
		child.setManager(manager);
	}
	
	/**
	 * Removes the given brand from the list of this brand's child brands.  If
	 * the given brand was not this brand's child no action is taken.
	 * 
	 * @param child The child to remove
	 */
	protected void removeChild(LegacyBrand child)
	{
		this.childBrands.remove(child);
	}
	
	/**
	 * Ensures the given brand is eligible to be this brand's parent.  An
	 * exception is raised if a circular structure would be created by adding
	 * this brand to the given one.
	 * 
	 * @param newParent The brand to check
	 */
	private void checkParent(LegacyBrand newParent)
	{
		if(newParent == null)
			throw new IllegalArgumentException("Parent cannot be null");
		for(int i = 0; i < childBrands.size(); i++)
		{
			LegacyBrand child = childBrands.get(i);
			if(child.equals(newParent))
				throw new IllegalArgumentException("Circular reference detected in brand structure.");
			child.checkParent(newParent);
		}
	}
	
	/**
	 * Sets the brand manager to use for resolution
	 * 
	 * @param manager The brand manager to use to resolve brand information
	 */
	void setManager(LegacyBrandManager manager)
	{
		this.manager = manager;
	}
	
	/**
	 * Removes this brand from the current brand structure.  This will remove
	 * this brand from its parent and also delete all of this brand's child
	 * brands.
	 */
	public void delete()
	{
		for(int i = 0; i < childBrands.size(); i++)
		{
			LegacyBrand child = childBrands.get(i);
			child.delete();
		}
		parentBrand.removeChild(this);
	}
	
}
