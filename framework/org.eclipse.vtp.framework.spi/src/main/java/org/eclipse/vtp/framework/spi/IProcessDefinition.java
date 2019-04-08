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

import org.w3c.dom.Element;

/**
 * Defines the structure of a process created by the process engine.
 * 
 * @author Lonnie Pryor
 */
public interface IProcessDefinition
{
	// Services //

	/**
	 * @param serviceDescriptorID
	 * @return
	 */
	Element[] getServiceConfiguration(String serviceDescriptorID);

	// Actions //

	/**
	 * @return
	 */
	String[] getStartActionInstanceIDs();

	/**
	 * @return
	 */
	String[] getActionInstanceIDs();

	/**
	 * @param actionInstanceID
	 * @return
	 */
	String getActionDescriptorID(String actionInstanceID);

	/**
	 * @param actionInstanceID
	 * @return
	 */
	String getActionName(String actionInstanceID);

	/**
	 * @param actionInstanceID
	 * @return
	 */
	Element[] getActionConfiguration(String actionInstanceID);

	/**
	 * @param actionInstanceID
	 * @return
	 */
	String[] getActionResultIDs(String actionInstanceID);

	/**
	 * @param actionInstanceID
	 * @param actionResultID
	 * @return
	 */
	String getActionResultTargetInstanceID(String actionInstanceID,
			String actionResultID);

	// Observers //

	/**
	 * @param actionInstanceID
	 * @return
	 */
	String[] getBeforeObserverInstanceIDs(String actionInstanceID);

	/**
	 * @param actionInstanceID
	 * @param actionResultID
	 * @return
	 */
	String[] getAfterObserverInstanceIDs(String actionInstanceID,
			String actionResultID);

	/**
	 * @param observerInstanceID
	 * @return
	 */
	String getObserverDescriptorID(String observerInstanceID);

	/**
	 * @param observerInstanceID
	 * @return
	 */
	Element[] getObserverConfiguration(String observerInstanceID);
}
