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
package org.eclipse.vtp.desktop.model.interactive.voice;

import org.eclipse.vtp.desktop.model.interactive.core.IMediaProject;


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
public interface IVoiceMediaProject extends IMediaProject
{
}
