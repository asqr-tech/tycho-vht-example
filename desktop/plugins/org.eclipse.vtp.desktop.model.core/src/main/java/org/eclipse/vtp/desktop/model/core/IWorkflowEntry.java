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

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.vtp.desktop.model.core.design.Variable;

/**
 * @author trip
 */
public interface IWorkflowEntry extends IAdaptable
{
	public String getId();
	
	public String getName();
	
	public List<Variable> getInputVariables();
}
