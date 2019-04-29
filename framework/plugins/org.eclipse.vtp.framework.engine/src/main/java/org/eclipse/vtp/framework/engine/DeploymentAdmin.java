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
package org.eclipse.vtp.framework.engine;

import java.util.Dictionary;

/**
 * DeplymentAdmin.
 * 
 * @author Lonnie Pryor
 */
public interface DeploymentAdmin
{
	String deploy(Dictionary<?, ?> properties);

	boolean update(String id, Dictionary<?, ?> properties);

	boolean undelpoy(String id);
}
