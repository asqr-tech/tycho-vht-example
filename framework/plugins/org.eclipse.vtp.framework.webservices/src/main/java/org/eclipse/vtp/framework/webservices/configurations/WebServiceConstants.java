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
package org.eclipse.vtp.framework.webservices.configurations;

/**
 * Configuration constants for the web service extensions.
 * 
 * @author Lonnie Pryor
 */
public interface WebServiceConstants
{
	/** The name space URI of the web service configuration objects. */
	String NAMESPACE_URI = //
	"http://eclipse.org/vtp/xml/framework/webservices/configurations"; //$NON-NLS-1$

	String STATIC = "static";
	String VARIABLE = "variable";
	String EXPRESSION = "expression";
	
	String SOAP = "soap";
	String REST = "rest";

	String NAME_SERVICE_INFO = "service-info";
	String NAME_SERVICE_TYPE = "service-type";
	String NAME_SOAP_ACTION_TYPE = "soap-action-type";
	String NAME_SOAP_ACTION = "soap-action";
	String NAME_URL_TYPE = "url-type";
	String NAME_URL = "url";
	String NAME_OUTPUT = "output";
	String NAME_PROCESS = "process";
	String NAME_INPUT_STRUCTURE = "input-structure";
	String NAME_VARIABLE = "variable";
	/** The "endpoint" name constant. */
	String NAME_ENDPOINT = "endpoint"; //$NON-NLS-1$
	/** The "input" name constant. */
	String NAME_INPUT = "input"; //$NON-NLS-1$
	/** The "locator" name constant. */
	String NAME_LOCATOR = "locator"; //$NON-NLS-1$
	/** The "mapping" name constant. */
	String NAME_MAPPING = "mapping"; //$NON-NLS-1$
	/** The "name" name constant. */
	String NAME_NAME = "name"; //$NON-NLS-1$
	/** The "operation" name constant. */
	String NAME_OPERATION = "operation"; //$NON-NLS-1$
	/** The "port" name constant. */
	String NAME_PORT = "port"; //$NON-NLS-1$
	/** The "result-cardinality" name constant. */
	String NAME_RESULT_CARDINALITY = "result-cardinality"; //$NON-NLS-1$
	/** The "result-name" name constant. */
	String NAME_RESULT_NAME = "result-name"; //$NON-NLS-1$
	/** The "result-type" name constant. */
	String NAME_RESULT_TYPE = "result-type"; //$NON-NLS-1$
	/** The "type" name constant. */
	String NAME_TYPE = "type"; //$NON-NLS-1$
	/** The "value" name constant. */
	String NAME_VALUE = "value"; //$NON-NLS-1$
	/** The "web-service" name constant. */
	String NAME_WEB_SERVICE = "web-service"; //$NON-NLS-1$
	String NAME_SECURED = "secured";
}
