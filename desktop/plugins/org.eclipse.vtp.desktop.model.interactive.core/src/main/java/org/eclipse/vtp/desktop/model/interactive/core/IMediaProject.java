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
package org.eclipse.vtp.desktop.model.interactive.core;

import org.eclipse.core.resources.IProject;
import org.eclipse.vtp.framework.interactions.core.media.IMediaProvider;


/**
 * This interface represents the top level project folder resource
 * for persona language support projects.  The project layout for
 * persona projects is highly structured to facilitate easy
 * enumeration of the artifacts used in its creation and definition.
 *
 * This interface also provides access to information stored in
 * auxillary "." files that are hidden from general view and
 * modification.
 *
 * @author Trip Gilman
 * @version 2.0
 */
public interface IMediaProject extends IMediaObjectContainer
{
	/**
	 * @return The unique identifier for this persona project
	 * @throws CoreException If an error occured during retrieval of the id
	 */
	public String getId();

	/**
	 * @return The prompt definition resource for this persona project
	 */
	public IPromptSet getPromptSet();

	/**
	 * @return The root folder for all media resources of this persona project
	 */
	public IMediaFilesFolder getMediaFilesFolder();
	
	/**
	 * @return
	 */
	public IMediaProvider getMediaProvider();
	
	/**
	 * @return
	 * @throws CoreException
	 */
	public String getLanguagePackId();
	
	public InteractionType getInteractionType();

	/**
	 * Allows access to the underlying Eclipse project model
	 *
	 * @return The eclipse project object for this project
	 */
	public IProject getUnderlyingProject();
}
