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
package org.eclipse.vtp.framework.common.configurations;

/**
 * Configuration constants for the common extensions.
 * 
 * @author Lonnie Pryor
 */
public interface CommonConstants
{
	/** The name space URI of the common configuration objects. */
	String NAMESPACE_URI = //
	"http://eclipse.org/vtp/xml/framework/common/configurations"; //$NON-NLS-1$

	/** The "assignment" name constant. */
	String NAME_ASSIGNMENT = "assignment"; //$NON-NLS-1$
	/** The "branch" name constant. */
	String NAME_BRANCH = "branch"; //$NON-NLS-1$
	/** The "brand" name constant. */
	String NAME_BRAND = "brand"; //$NON-NLS-1$
	/** The "data-type" name constant. */
	String NAME_DATA_TYPE = "data-type"; //$NON-NLS-1$
	/** The "dispatch" name constant. */
	String NAME_DISPATCH = "dispatch"; //$NON-NLS-1$
	/** The "entry" name constant. */
	String NAME_ENTRY = "entry"; //$NON-NLS-1$
	/** The "exit" name constant. */
	String NAME_EXIT = "exit"; //$NON-NLS-1$
	/** The "field" name constant. */
	String NAME_FIELD = "field"; //$NON-NLS-1$
	/** The "initial-value" name constant. */
	String NAME_INITIAL_VALUE = "initial-value"; //$NON-NLS-1$
	/** The "key" name constant. */
	String NAME_KEY = "key"; //$NON-NLS-1$
	/** The "left-operand" name constant. */
	String NAME_LEFT_OPERAND = "left-operand"; //$NON-NLS-1$
	/** The "id" name constant. */
	String NAME_ID = "id"; //$NON-NLS-1$
	/** The "name" name constant. */
	String NAME_NAME = "name"; //$NON-NLS-1$
	/** The "outgoing" name constant. */
	String NAME_OUTGOING = "outgoing"; //$NON-NLS-1$
	/** The "path" name constant. */
	String NAME_PATH = "path"; //$NON-NLS-1$
	/** The "primary-field" name constant. */
	String NAME_PRIMARY_FIELD = "primary-field"; //$NON-NLS-1$
	/** The "right-operand" name constant. */
	String NAME_RIGHT_OPERAND = "right-operand"; //$NON-NLS-1$
	/** The "script" name constant. */
	String NAME_SCRIPT = "script"; //$NON-NLS-1$
	/** The "scripting-language" name constant. */
	String NAME_SCRIPTING_LANGUGAGE = "scripting-language"; //$NON-NLS-1$
	/** The "type" name constant. */
	String NAME_TYPE = "type"; //$NON-NLS-1$
	/** The "uri" name constant. */
	String NAME_URI = "uri"; //$NON-NLS-1$
	/** The "value" name constant. */
	String NAME_VALUE = "value"; //$NON-NLS-1$
	/** The "variable-mapping" name constant. */
	String NAME_VARIABLE_MAPPING = "variable-mapping"; //$NON-NLS-1$
	String NAME_SECURED = "secured";

	/** The "none" mapping type constant. */
	String MAPPING_TYPE_NONE = "none"; //$NON-NLS-1$
	/** The "static" mapping type constant. */
	String MAPPING_TYPE_STATIC = "static"; //$NON-NLS-1$
	/** The "expression" mapping type constant. */
	String MAPPING_TYPE_EXPRESSION = "expression"; //$NON-NLS-1$
	/** The "variable" mapping type constant. */
	String MAPPING_TYPE_VARIABLE = "variable"; //$NON-NLS-1$
}
