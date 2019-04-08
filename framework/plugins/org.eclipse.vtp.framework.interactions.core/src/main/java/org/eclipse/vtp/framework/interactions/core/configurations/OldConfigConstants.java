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
package org.eclipse.vtp.framework.interactions.core.configurations;

/**
 * ConfigConstants.
 * 
 * @author Lonnie Pryor
 */
public interface OldConfigConstants
{
	/** The name space URI of the media configuration objects. */
	String NAMESPACE_URI_MEDIA = "http://eclipse.org/vtp/xml/configuration/media"; //$NON-NLS-1$
	/** The name space URI of the meta-data configuration objects. */
	String NAMESPACE_URI_META_DATA = "http://eclipse.org/vtp/xml/configuration/attacheddata"; //$NON-NLS-1$

	/** The "attached-data-binding" name constant. */
	String NAME_ATTACHED_DATA_BINDING = "attached-data-binding"; //$NON-NLS-1$
	/** The "bindings" name constant. */
	String NAME_BINDINGS = "bindings"; //$NON-NLS-1$
	/** The "grammar-binding" name constant. */
	String NAME_GRAMMAR_BINDING = "grammar-binding"; //$NON-NLS-1$
	/** The "item" name constant. */
	String NAME_ITEM = "item"; //$NON-NLS-1$
	/** The "key" name constant. */
	String NAME_KEY = "key"; //$NON-NLS-1$
	/** The "name" name constant. */
	String NAME_NAME = "name"; //$NON-NLS-1$
	/** The "prompt-binding" name constant. */
	String NAME_PROMPT_BINDING = "prompt-binding"; //$NON-NLS-1$
	/** The "property-binding" name constant. */
	String NAME_PROPERTY_BINDING = "property-binding"; //$NON-NLS-1$
}
