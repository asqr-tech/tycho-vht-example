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

import org.osgi.framework.ServiceReference;

/**
 * A customizer interface for the {@link SingletonTracker} class.
 * 
 * @author Lonnie Pryor
 * @version 1.0
 * @since 3.0
 */
public interface SingletonTrackerCustomizer
{
	@SuppressWarnings("rawtypes")
	/**
	 * Called when a service is selected as the singleton.
	 * 
	 * @param reference The selected singleton service reference.
	 * @return The singleton service object or <code>null</code> if the
	 *         specified reference should not be selected.
	 */
	Object selectingService(ServiceReference reference);

	@SuppressWarnings("rawtypes")
	/**
	 * Called when another service besides the currently selected singleton is
	 * selected as the new singleton. Implementations must release the old
	 * singleton if the new singleton is accepted.
	 * 
	 * @param oldReference The currently selected singleton service reference.
	 * @param oldService The currently selected singleton service object.
	 * @param newReference The newly selected singleton service reference.
	 * @return The new singleton service object or <code>null</code> if the new
	 *         reference should not be selected and the old reference should be
	 *         maintained.
	 */
	Object changingSelectedService(ServiceReference oldReference,
			Object oldService, ServiceReference newReference);

	@SuppressWarnings("rawtypes")
	/**
	 * Called when the properties of the currently selected singleton service have
	 * changed.
	 * 
	 * @param reference The currently selected singleton service reference.
	 * @param service The currently selected singleton service object.
	 */
	void selectedServiceModified(ServiceReference reference, Object service);

	@SuppressWarnings("rawtypes")
	/**
	 * Called when the selected service must be released and no replacement is
	 * available.
	 * 
	 * @param reference The currently selected singleton service reference.
	 * @param service The currently selected singleton service object.
	 */
	void releasedSelectedService(ServiceReference reference, Object service);
}
