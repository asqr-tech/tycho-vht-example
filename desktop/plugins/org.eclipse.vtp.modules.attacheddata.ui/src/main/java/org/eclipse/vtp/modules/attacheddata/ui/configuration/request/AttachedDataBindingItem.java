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
package org.eclipse.vtp.modules.attacheddata.ui.configuration.request;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class provides an attached data request specific implementation of
 * <code>GenericBindingitem</code>.  Each item holds a list of item entries
 * that represent the each data point that will be requested.
 * 
 * @author trip
 */
public class AttachedDataBindingItem extends GenericBindingItem
{
	/**
	 * Local member to hold the item entries.
	 */
	private List<AttachedDataItemEntry> entries;

	/**
	 * Creates a new <code>AttachedDataBindingItem</code> instance.
	 */
	public AttachedDataBindingItem()
	{
		super();
		entries = new ArrayList<AttachedDataItemEntry>();
	}

	/**
	 * Returns an unmodifiable list of this item's entries.
	 * 
	 * @return List of entries
	 */
	public List<AttachedDataItemEntry> getEntries()
	{
		return Collections.unmodifiableList(entries);
	}
	
	/**
	 * Adds the given entry to this item.  The entry is appended to the end of
	 * the current list.  If the entry was already in the list, it is removed
	 * from the list and added to the end of the list.
	 * 
	 * @param entry The entry to add to this item
	 */
	public void addEntry(AttachedDataItemEntry entry)
	{
		entries.remove(entry);
		entries.add(entry);
	}
	
	/**
	 * Adds the given entry to this item.  The entry is inserted into the
	 * current list at the specified index.  If the entry was already in the
	 * list, it is removed prior to being inserted.
	 * 
	 * @param entry The entry to add to this item
	 * @param index The index in the list to insert the entry
     * @throws IndexOutOfBoundsException if the index is out of range
     *		  (index &lt; 0 || index &gt; size()).
	 */
	public void addEntry(AttachedDataItemEntry entry, int index)
	{
		entries.remove(entry);
		entries.add(index, entry);
	}
	
	/**
	 * Removes the given entry from this item's list.
	 * 
	 * @param entry The entry to remove from this item
	 */
	public void removeEntry(AttachedDataItemEntry entry)
	{
		entries.remove(entry);
	}
	
	/**
	 * Removes all entries from this item's list.
	 */
	public void clearEntries()
	{
		entries.clear();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.configuration.adrequest.GenericBindingItem#clone()
	 */
	public Object clone()
	{
		AttachedDataBindingItem ret = new AttachedDataBindingItem();
		for(int i = 0; i < entries.size(); i++)
		{
			AttachedDataItemEntry pbie = entries.get(i);
			AttachedDataItemEntry pbie_clone = new AttachedDataItemEntry();
			pbie_clone.setValue(pbie.getValue());
			ret.addEntry(pbie_clone);
		}
		return ret;
	}
}
