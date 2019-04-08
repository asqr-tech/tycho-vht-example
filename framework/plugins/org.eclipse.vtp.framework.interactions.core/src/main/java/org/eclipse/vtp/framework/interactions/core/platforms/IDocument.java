/*--------------------------------------------------------------------------
 * Copyright (c) 2004, 2006-2007 OpenMethods, LLC
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Trip Gilman (OpenMethods), Lonnie G. Pryor (OpenMethods),
 *    T.D. Barnes (OpenMethods) - initial API and implementation
 -------------------------------------------------------------------------*/
package org.eclipse.vtp.framework.interactions.core.platforms;

import javax.xml.transform.Source;

/**
 * This interface represents an abstract document that can be represented in an
 * XML structure.
 * 
 * @author Lonnie Pryor
 * @version 1.0
 */
public interface IDocument
{
	/** The daefualt content type (text/xml). */
	String DEFAULT_CONTENT_TYPE = "text/xml"; //$NON-NLS-1$
	
	/**
	 * Returns a string representing the dialect of XML document this instance
	 * produces.
	 * 
	 * @return A string representing the dialect of XML document this instance
	 *         produces.
	 */
	String getDocumentType();

	/**
	 * Returns a string representing the MIME type of XML document this instance
	 * produces.
	 * 
	 * @return A string representing the MIME type of XML document this instance
	 *         produces.
	 */
	String getContentType();

	/**
	 * Creates and returns an XML representation of this document.
	 * 
	 * @return An XML transformation source object that can be used with the
	 *         {@link javax.xml.transform} API.
	 * @throws IllegalStateException If any aspect of this document's state
	 *           prevents it from being transformed into XML.
	 */
	Source toXMLSource() throws IllegalStateException;
	
	boolean isSecured();
	
	boolean isCachable();
	
	public void setSecured(boolean secured);
}
