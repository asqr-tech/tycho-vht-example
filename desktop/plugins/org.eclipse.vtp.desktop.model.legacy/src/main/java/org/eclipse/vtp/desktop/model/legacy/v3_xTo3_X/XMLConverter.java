package org.eclipse.vtp.desktop.model.legacy.v3_xTo3_X;

import org.eclipse.vtp.desktop.model.legacy.v3_xTo3_X.legacysupport.ConversionException;
import org.w3c.dom.Element;

public interface XMLConverter
{
	/**
	 * @param element
	 * @throws ConversionException
	 */
	public void convert(Element element) throws ConversionException;
}
