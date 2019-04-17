/*--------------------------------------------------------------------------
 * Copyright (c) 2009 OpenMethods, LLC
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Trip Gilman (OpenMethods)
 *    - initial API and implementation
 -------------------------------------------------------------------------*/
package org.eclipse.vtp.desktop.model.core;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.vtp.desktop.model.core.branding.BrandManager;

/**
 * @author trip
 */
public interface IWorkflowProject extends IWorkflowResourceContainer
{
	/**
	 * @return The unique identifier of this workflow project
	 */
	public String getId();
	
	/**
	 * @return The <code>IBusinessObjectSet</code> folder resource that contains
	 * the set of business objects defined for this application
	 */
	public IBusinessObjectSet getBusinessObjectSet();

	/**
	 * @return The <code>IDependencySet</code> folder resource that contains
	 * the set of web services defined for this application
	 */
	public IDependencySet getDependencySet();
	
	/**
	 * @return The root folder that contains this project's design documents and
	 * other design folders
	 */
	public IDesignRootFolder getDesignRootFolder();
	
	public IDatabaseSet getDatabaseSet();
	
	public IWebserviceSet getWebserviceSet();
	
	/**
	 * @return The brand manager this project uses for brand resolution
	 */
	public BrandManager getBrandManager();

	/**
	 * Allows access to the underlying Eclipse project model
	 *
	 * @return The eclipse project object for this project
	 */
	public IProject getUnderlyingProject();
	
	public IWorkflowEntry getWorkflowEntry(String id);
	
	public IWorkflowEntry getWorkflowEntryByName(String name);
	
	/**
	 * @return A list of the entry points into this workflow project
	 */
	public List<IWorkflowEntry> getWorkflowEntries();
	
	/**
	 * @return A list of exit points out of this workflow project that are
	 * reachable from the given entry point.
	 */
	public List<IWorkflowExit> getWorkflowExits(IWorkflowEntry entryPoint);
	
	public void navigateToElement(String elementId);

}
