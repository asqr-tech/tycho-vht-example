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

import java.util.StringTokenizer;

/**
 * Utilities for the HTTP connector system.
 * 
 * @author Lonnie Pryor
 */
public class HttpUtils
{
	/**
	 * Normalizes the supplied path so that it always starts with '/', never
	 * contains consecutive instances of '/', and never ends with '/' except for
	 * the case where the normalized path is "/".
	 * 
	 * @param path The path to normalize. If <code>null</code> or empty, "/"
	 *          will be returned.
	 * @return A normalized version of the supplied path.
	 */
	public static String normalizePath(String path)
	{
		StringBuffer sb = null;
		if (path != null && path.length() > 0)
		{
			StringTokenizer st = new StringTokenizer(path, "/"); //$NON-NLS-1$
			while (st.hasMoreTokens())
			{
				String t = st.nextToken();
				if (t == null || t.length() == 0 || ".".equals(t)) //$NON-NLS-1$
					continue;
				if ("..".equals(t)) //$NON-NLS-1$
				{
					if (sb != null)
					{
						int lastSlash = sb.lastIndexOf("/"); //$NON-NLS-1$
						if (lastSlash >= 0)
							sb.setLength(lastSlash);
					}
				}
				else
				{
					if (sb == null)
						sb = new StringBuffer();
					sb.append('/').append(t);
				}
			}
		}
		return sb == null ? "/" : sb.toString(); //$NON-NLS-1$
	}
}
