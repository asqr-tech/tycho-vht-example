/*--------------------------------------------------------------------------
 * Copyright (c) 2004, 2006 OpenMethods, LLC
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Trip Gilman (OpenMethods), Lonnie G. Pryor (OpenMethods),
 *    T.D. Barnes (OpenMethods) - initial API and implementation
 -------------------------------------------------------------------------*/
package org.eclipse.vtp.framework.util;

import java.security.SecureRandom;

/**
 * The <code>Guid</code> class provides a facility for generating unique
 * identifiers.  The identifier is a 128 bit number packed into a 32
 * character hex string.
 * 
 * @author trip
 */
public class Guid
{
	/**
	 * Creates a unique identifier.
	 * 
	 * @return 128 bit number hex encoded into a 32 character string.
	 */
	public static String createGUID()
	{
		SecureRandom secureRandom = new SecureRandom();
		byte[] buffer = new byte[16];
		secureRandom.nextBytes(buffer);
		buffer[6] &= 0x0f;
		buffer[6] |= 0x40;
		buffer[8] &= 0x3f;
		buffer[8] |= 0x80;
		buffer[10] |= 0x80;

		long mostSignificant = 0;

		for(int i = 0; i < 8; i++)
		{
			mostSignificant = (mostSignificant << 8) | (buffer[i] & 0xff);
		}

		long leastSignificant = 0;

		for(int i = 8; i < 16; i++)
		{
			leastSignificant = (leastSignificant << 8) | (buffer[i] & 0xff);
		}

		return convertToString((mostSignificant >> 56) & 0xffL)
		+ convertToString((mostSignificant >> 48) & 0xffL)
		+ convertToString((mostSignificant >> 40) & 0xffL)
		+ convertToString((mostSignificant >> 32) & 0xffL)
		+ convertToString((mostSignificant >> 24) & 0xffL)
		+ convertToString((mostSignificant >> 16) & 0xffL)
		+ convertToString((mostSignificant >> 8) & 0xffL)
		+ convertToString(mostSignificant & 0xffL)
		+ convertToString((leastSignificant >> 56) & 0xffL)
		+ convertToString((leastSignificant >> 48) & 0xffL)
		+ convertToString((leastSignificant >> 40) & 0xffL)
		+ convertToString((leastSignificant >> 32) & 0xffL)
		+ convertToString((leastSignificant >> 24) & 0xffL)
		+ convertToString((leastSignificant >> 16) & 0xffL)
		+ convertToString((leastSignificant >> 8) & 0xffL)
		+ convertToString(leastSignificant & 0xffL);
	}

	/**
	 * Utility function that converts the given long into a hex encoded string.
	 * 
	 * @param l The long to encode.
	 * @return The hex encoded string representation of the given long value.
	 */
	private static String convertToString(long l)
	{
		String ret = Long.toString(l, 16);

		if(ret.length() < 2)
		{
			ret = "0" + ret;
		}

		return ret;
	}

	/**
	 * Utility function that allows the generation of a unique identifier
	 * directly from the command line.  The identifier is printed to standard
	 * out and the program terminates.
	 * 
	 * @param args The command line arguments provided.
	 */
	public static void main(String[] args)
	{
		System.out.println(Guid.createGUID());
	}
}
