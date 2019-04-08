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
 * A process engine that can create process instances from descriptors.
 * 
 * @author Lonnie Pryor
 */
public interface IProcessEngine
{
	/**
	 * Creates a new process instance from the specified descriptor.
	 * 
	 * @param definition The definition of the process to create.
	 * @param descriptor The descriptor of the process to create.
	 * @return A new process instance from the specified descriptor.
	 * @throws NullPointerException If the specified definition is
	 *           <code>null</code>.
	 * @throws NullPointerException If the specified descriptor is
	 *           <code>null</code>.
	 */
	IProcess createProcess(IProcessDefinition definition,
			IProcessDescriptor descriptor) throws NullPointerException;

	/**
	 * Adds an observer that will be notified of changes to this process engine.
	 * 
	 * @param observer The observer to add.
	 * @throws NullPointerException If the supplied observer is <code>null</code>.
	 */
	void addProcessEngineObserver(IProcessEngineObserver observer)
			throws NullPointerException;

	/**
	 * Removes an observer form the list of this process engine's observers.
	 * 
	 * @param observer The observer to remove.
	 * @throws NullPointerException If the supplied observer is <code>null</code>.
	 */
	void removeProcessEngineObserver(IProcessEngineObserver observer)
			throws NullPointerException;
}
