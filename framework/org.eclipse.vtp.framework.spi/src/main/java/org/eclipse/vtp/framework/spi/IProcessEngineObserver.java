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
package org.eclipse.vtp.framework.spi;

/**
 * An observer that is notified of changes to the process engine.
 * 
 * @author Lonnie Pryor
 */
public interface IProcessEngineObserver
{
	/** The event fired when a component has been selected for an identifier. */
	int EVENT_TYPE_SELECTED = 1;
	/** The event fired when the component for an identifier changes. */
	int EVENT_TYPE_CHANGED = 2;
	/** The event fired when the component for an identifier is released. */
	int EVENT_TYPE_RELEASED = 3;

	/** Represents that an action changed. */
	int COMPONENT_TYPE_ACTION = 1;
	/** Represents that a configuration changed. */
	int COMPONENT_TYPE_CONFIGURATION = 2;
	/** Represents that an observer changed. */
	int COMPONENT_TYPE_OBSERVER = 3;
	/** Represents that a service changed. */
	int COMPONENT_TYPE_SERVICE = 4;

	/**
	 * Called when a change has occurred to the internals of the process engine.
	 * 
	 * @param eventType The type of event that ocurred.
	 * @param componentType The type of component that changed.
	 * @param componentIdentifier The identifier of the component that changed.
	 */
	void processEngineUpdated(int eventType, int componentType,
			String componentIdentifier);
}
