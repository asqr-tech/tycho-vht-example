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
package org.eclipse.vtp.framework.engine.http;

import org.eclipse.vtp.framework.interactions.core.platforms.IDocument;

/**
 * Result generated for a single HTTP request.
 * 
 * @author Lonnie Pryor
 */
public class ResultDocument {
	/** The document to return over HTTP. */
	private IDocument document = null;
	/** True if the process has no more steps. */
	private boolean terminated = false;
	
	public ResultDocument() {}
	
	public ResultDocument(IDocument document, boolean terminated) {
		this.document = document;
		this.terminated = terminated;
	}
	
	public IDocument getDocument() {
		return document;
	}
	public void setDocument(IDocument document) {
		this.document = document;
	}
	public boolean isTerminated() {
		return terminated;
	}
	public void setTerminated(boolean terminated) {
		this.terminated = terminated;
	}
}
