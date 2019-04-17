/**
 * 
 */
package com.openmethods.openvxml.platforms.cvp.vxml;

import org.eclipse.vtp.framework.interactions.voice.vxml.Grammar;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * @author trip
 *
 */
public class RegexGrammar extends Grammar
{
	private String contents;

	/**
	 * @param mode
	 * @throws IllegalArgumentException
	 * @throws NullPointerException
	 */
	public RegexGrammar(String mode) throws IllegalArgumentException,
			NullPointerException
	{
		super(mode);
	}
	
	public void setContents(String contents)
	{
		this.contents = contents;
	}

	@Override
	protected void writeAttributes(AttributesImpl attributes)
	{
		super.writeAttributes(attributes);
		writeAttribute(attributes, null, null, NAME_TYPE, TYPE_CDATA, "application/grammar+regex");
	}

	@Override
	protected void writeChildren(ContentHandler outputHandler)
			throws NullPointerException, SAXException
	{
		outputHandler.characters(contents.toCharArray(), 0, contents.length());
	}

}
