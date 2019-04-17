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
package org.eclipse.vtp.desktop.model.core.branding;

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
public interface IBrand
{
	/**
	 * @return The unique identifier for this brand
	 */
	public String getId();
	
	/**
	 * @return the human readable name for this brand
	 */
	public String getName();
	
	/**
	 * Sets the name of this brand to the given value.  If the new name is equal
	 * to the old name, no action is taken.  Otherwise, the new name is made the
	 * current name and a property change event is propagated to this brand's
	 * manager.
	 * 
	 * @param newName The new name for this brand
	 */
	public void setName(String newName);
	
	/**
	 * @return This brand's parent brand or null if this brand has no parent.
	 */
	public IBrand getParent();
	
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
	public void setParent(IBrand brand);
	
	/**
	 * @return The list of brands contained by this brand in the order they were
	 * added.
	 */
	public List<IBrand> getChildBrands();
	
	/**
	 * Removes this brand from the current brand structure.  This will remove
	 * this brand from its parent and also delete all of this brand's child
	 * brands.
	 */
	public void delete();
}
