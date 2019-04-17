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
package org.eclipse.vtp.desktop.model.core.design;

/**
 * Defines a pre-determined set of dimensions for canvases
 */
public class PaperSize
{
	/** The id that will be used internally to identify this paper size */
	private String id;
	/** The name that will be displayed to the user for this paper size */
	private String name;
	/** The width in pixels of this paper size in portrait orientation*/
	private int portraitPixelWidth = 0;
	/** The height in pixels of this paper size in portrait orientation*/
	private int portraitPixelHeight = 0;
	/** The width in pixels of this paper size in landscape orientation*/
	private int landscapePixelWidth = 0;
	/** The height in pixels of this paper size in landscape orientation*/
	private int landscapePixelHeight = 0;
	
	/**
	 * Creates a new PaperSize with the id, name, and dimensions specified by the parameters
	 * @param id - the id that will be used internally to identify this paper size
	 * @param name - the name that will be displayed to the user for this paper size
	 * @param ppw - the width in pixels of this paper size in portrait orientation
	 * @param pph - the height in pixels of this paper size in portrait orientation
	 * @param lpw - the width in pixels of this paper size in landscape orientation
	 * @param lph - the height in pixels of this paper size in landscape orientation
	 */
	public PaperSize(String id, String name, int ppw, int pph, int lpw, int lph)
	{
		super();
		this.id = id;
		this.name = name;
		this.portraitPixelWidth = ppw;
		this.portraitPixelHeight = pph;
		this.landscapePixelWidth = lpw;
		this.landscapePixelHeight = lph;
	}

	/**
	 * Returns the id used internally to identify this paper size
	 * @return the id of the paper size
	 */
	public String getId()
	{
		return id;
	}

	/**
	 * Returns the name that will be displayed to the user for this paper size
	 * @return the name of the paper size
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Returns the width in pixels of this paper size in portrait orientation
	 * @return portraitPixelWidth
	 */
	public int getPortraitPixelWidth()
	{
		return portraitPixelWidth;
	}

	/**
	 * Returns the height in pixels of this paper size in portrait orientation
	 * @return portraitPixelHeight
	 */
	public int getPortraitPixelHeight()
	{
		return portraitPixelHeight;
	}
	
	/**
	 * Returns the width in pixels of this paper size in landscape orientation
	 * @return landscapePixelWidth
	 */
	public int getLandscapePixelWidth()
	{
		return landscapePixelWidth;
	}
	
	/**
	 * Returns the height in pixels of this paper size in landscape orientation
	 * @return landscapePixelHeight
	 */
	public int getLandscapePixelHeight()
	{
		return landscapePixelHeight;
	}
}
