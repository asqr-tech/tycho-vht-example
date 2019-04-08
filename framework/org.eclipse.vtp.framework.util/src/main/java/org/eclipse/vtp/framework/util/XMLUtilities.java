/*--------------------------------------------------------------------------
 * Copyright (c) 2004, 2006 OpenMethods, LLC
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Trip Gilman (OpenMethods), Lonnie G. Pryor (OpenMethods),
 *    Vincent Pruitt (OpenMethods)
 *    
 *    T.D. Barnes (OpenMethods) - initial API and implementation
 -------------------------------------------------------------------------*/
package org.eclipse.vtp.framework.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Static utility class that hosts functions that make dealing with xml formatted
 * content easier.
 * 
 * @author trip
 */
public class XMLUtilities
{
	/**
	 * Encodes the given string so that it conforms to the XML 1.0 specification
	 * for attribute values.
	 * 
	 * @param toEncode The string to encode
	 * 
	 * @return the encoded string
	 */
	public static String encodeAttribute(String toEncode)
	{
		StringBuffer buffer = new StringBuffer();
		char[] cs = toEncode.toCharArray();

		for (int i = 0; i < cs.length; i++)
		{
			switch (cs[i])
			{
			case '\'':
				buffer.append("&apos;");

				break;

			case '"':
				buffer.append("&quot;");

				break;

			case '&':
				buffer.append("&amp;");

				break;

			case '<':
				buffer.append("&lt;");

				break;

			case '>':
				buffer.append("&gt;");

				break;

			default:
				buffer.append(cs[i]);
			}
		}

		return buffer.toString();
	}

	/**
	 * Encodes the given string so that it conforms to the XML 1.0 specification
	 * for CDATA and TEXT nodes.
	 * 
	 * @param toEncode The string to encode
	 * 
	 * @return the encoded string
	 */
	public static String encodeText(String toEncode)
	{
		StringBuffer buffer = new StringBuffer();
		char[] cs = toEncode.toCharArray();

		for (int i = 0; i < cs.length; i++)
		{
			switch (cs[i])
			{
			case '&':
				buffer.append("&amp;");

				break;

			case '<':
				buffer.append("&lt;");

				break;

			case '>':
				buffer.append("&gt;");

				break;

			default:
				buffer.append(cs[i]);
			}
		}

		return buffer.toString();
	}

	/**
	 * Retrieves the DOM <code>NodeList</code> contained by the named element
	 * in the given parent element.<br>
	 * <br>
	 * <pre>
	 * <parent-element>
	 *     <named-list>
	 *         <list-item/>
	 *         <list-item/>
	 *     </named-list>
	 * </parent-element>
	 * </pre>
	 * 
	 * In the above example, a <code>NodeList</code> containing the two
	 * <pre><list-item/></pre> elements would be returned by passing the parent
	 * element and "named-list" into the function parameters.
	 * 
	 * @param parent The parent DOM element that contains the named list.
	 * @param containerTagName The name of the element that contains the list items.
	 * @return A <code>NodeList</code> containing the list items.
	 * @throws Exception If an exception occurs while traversing the DOM objects.
	 */
	public static NodeList getNamedNodeList(Element parent,
			String containerTagName) throws Exception
	{
		NodeList nl = parent.getElementsByTagName(containerTagName);

		if (nl.getLength() < 1)
		{
			throw new Exception("Missing named list: " + containerTagName);
		}

		return nl.item(0).getChildNodes();
	}

	/**
	 * Returns an array containing the element objects in the provided node list.
	 * 
	 * @param nodeList The DOM node objects to extract elements from.
	 * @return The array of DOM elements found in the node list.
	 */
	@SuppressWarnings("unchecked")
	public static Element[] getElementsOfNodeList(NodeList nodeList)
	{
		Element[] ret = null;
		@SuppressWarnings("rawtypes")
		Vector v = new Vector();

		for (int n = 0; n < nodeList.getLength(); n++)
		{
			Node item = nodeList.item(n);

			if (item.getNodeType() == Node.ELEMENT_NODE)
			{
				v.addElement(item);
			}
		}

		ret = new Element[v.size()];

		for (int n = 0; n < ret.length; n++)
		{
			ret[n] = (Element)v.elementAt(n);
		}

		return ret;
	}

	/**
	 * A convenience method that allows the extraction of text data from a named
	 * child element of the given parent element.<br>
	 * <br>
	 * <pre>
	 * <parent-element>
	 *     <child-element>[Text Data]</child-element>
	 * </parent-element>
	 * </pre>
	 * 
	 * @param parent The parent DOM element.
	 * @param childTagName The name of the child element.
	 * @return The text contained by the named child element.
	 * @throws Exception If an error occurs while traversing the DOM objects.
	 */
	public static String getWrappedTextData(Element parent, String childTagName)
			throws Exception
	{
		NodeList nl = parent.getElementsByTagName(childTagName);

		if (nl.getLength() < 1)
		{
			return null;
		}

		Element childElement = (Element)nl.item(0);

		return getElementTextData(childElement, true);
	}

	/**
	 * Returns the text content of the provided element as is.  If multiple text
	 * DOM nodes are present, they are concatenated together. 
	 * 
	 * @param element The element that contains the desired text content.
	 * @return The text content of the given element.
	 * @throws Exception If an exception occurs during the traversal of the DOM
	 * objects.
	 */
	public static String getElementTextData(Element element) throws Exception
	{
		if (!element.hasChildNodes())
		{
			throw new Exception("Element has no children.");
		}

		Node n = element.getFirstChild();

		if ((n.getNodeType() != Node.TEXT_NODE)
				&& (n.getNodeType() != Node.CDATA_SECTION_NODE)) 
		{
			// must be a textual node
			// for now throw an exception, but later need to just skip this module and
			// resume initialization
			throw new Exception("Element child node is not textual.");
		}

		CharacterData value = (CharacterData)n;
		return value.getData();
	}
	
	public static String getElementTextDataNoEx(Element element)
	{
		String ret = null;
		try
		{
			ret = getElementTextData(element);
		}
		catch(Exception ex)
		{	
		}
		return ret;
	}

	/**
	 * Returns the text content of the provided element.  If unindent is false,
	 * the contents are returned as-is.  Otherwise, indention performed for
	 * formatting sake will be removed.
	 * 
	 * @param element The element that contains the text content.
	 * @param unindent Whether or not to unindent the text content.
	 * @return The text content of the provided element.
	 * @throws Exception If an exception occurs during the traversal of the DOM
	 * elements.
	 */
	public static String getElementTextData(Element element, boolean unindent)
			throws Exception
	{
		String result = getElementTextData(element);
		if (unindent)
			result = unindentTextData(result);
		return result;
	}
	
	public static String getElementTextDataNoEx(Element element, boolean unindent)
	{
		String result = null;
		try
		{
			result = getElementTextData(element);
			if (unindent)
				result = unindentTextData(result);
		}
		catch (Exception e)
		{
		}
		return result;
	}
	
	public static List<Element> getElementsByTagName(Element parent, String name, boolean localOnly)
	{
		List<Element> ret = new ArrayList<Element>();
		if(!localOnly)
		{
			NodeList elementList = parent.getElementsByTagName(name);
			for(int i = 0; i < elementList.getLength(); i++)
			{
				ret.add((Element)elementList.item(i));
			}
		}
		else
		{
			NodeList childList = parent.getChildNodes();
			for(int i = 0; i < childList.getLength(); i++)
			{
				if(childList.item(i).getNodeType() != Node.ELEMENT_NODE)
					continue;
				Element child = (Element)childList.item(i);
				if(child.getTagName().equals(name))
					ret.add(child);
			}
		}
		return ret;
	}
	
	public static List<Element> getChildElements(Element parent)
	{
		List<Element> ret = new ArrayList<Element>();
		NodeList childList = parent.getChildNodes();
		for(int i = 0; i < childList.getLength(); i++)
		{
			if(childList.item(i).getNodeType() != Node.ELEMENT_NODE)
				continue;
			Element child = (Element)childList.item(i);
			ret.add(child);
		}
		return ret;
	}

	public static List<Element> getChildElementsNS(Element parent, String uri)
	{
		List<Element> ret = new ArrayList<Element>();
		NodeList childList = parent.getChildNodes();
		for(int i = 0; i < childList.getLength(); i++)
		{
			if(childList.item(i).getNodeType() != Node.ELEMENT_NODE)
				continue;
			Element child = (Element)childList.item(i);
			if(child.getNamespaceURI().equals(uri))
				ret.add(child);
		}
		return ret;
	}

	public static List<Element> getElementsByTagNameNS(Element parent, String uri, String name, boolean localOnly)
	{
		List<Element> ret = new ArrayList<Element>();
		if(!localOnly)
		{
			NodeList elementList = parent.getElementsByTagNameNS(uri, name);
			for(int i = 0; i < elementList.getLength(); i++)
			{
				ret.add((Element)elementList.item(i));
			}
		}
		else
		{
			NodeList childList = parent.getChildNodes();
			for(int i = 0; i < childList.getLength(); i++)
			{
				if(childList.item(i).getNodeType() != Node.ELEMENT_NODE)
					continue;
				Element child = (Element)childList.item(i);
				if((uri.equals("*") || child.getNamespaceURI().equals(uri)) && ((child.getLocalName() == null && uri.equals("*") && child.getTagName().equals(name)) || child.getLocalName().equals(name)))
					ret.add(child);
			}
		}
		return ret;
	}

	/**
	 * Removes excess indention from the provided text.  Also trims blank lines
	 * from the beginning and end of the text.
	 * 
	 * @param text The text to unindent.
	 * @return The reformatted text.
	 */
	public static String unindentTextData(String text)
	{
		// Exclude blank lines at the beginning.
		int start = 0;
		while (start < text.length())
		{
			if (!Character.isWhitespace(text.charAt(start)))
				break;
			else
				++start;
		}
		if (start == text.length())
			return ""; //$NON-NLS-1$
		while (true)
		{
			if (text.charAt(start) == '\n')
			{
				++start;
				break;
			}
			else if (start == 0)
				break;
			else
				--start;
		}
		// Exclude blank lines at the end.
		int end = text.length();
		while (Character.isWhitespace(text.charAt(end - 1)))
				--end;
		while (true)
		{
			if (text.charAt(end - 1) == '\n')
			{
				--end;
				if (text.charAt(end - 1) == '\r')
					--end;
				break;
			}
			else if (end == text.length())
				break;
			else
				++end;
		}
		// Determine the indent that was used for the first line.
		int indentLength = 0;
		while (Character.isWhitespace(text.charAt(start + indentLength)))
			++indentLength;
		// Validate the indent against all other lines.
		int index = start + indentLength, lineCount = 1;
		while (true)
		{
			// Check to see if any indent exists.
			if (indentLength == 0)
				return text.substring(start, end);
			// Move to the next line.
			while (index < end && text.charAt(index) != '\n')
				++index;
			++index;
			if (index >= end)
				break;
			++lineCount;
			// Validate the indent.
			for (int i = 0; i < indentLength; ++i)
				if (text.charAt(start + i) != text.charAt(index + i))
					indentLength = i;
			index += indentLength;
		}
		// Remove the indent and return the results.
		StringBuffer result = new StringBuffer((end - start)
				- (indentLength * lineCount));
		for (int i = start, j = 0; i < end; ++i)
		{
			while (j < indentLength)
			{
				++i;
				++j;
			}
			char c = text.charAt(i);
			result.append(c);
			if (c == '\n')
				j = 0;
		}
		return result.toString();
	}

	/**
	 * Convenience function to get a DOM document builder object.
	 * 
	 * @return A new <code>DocumentBuilder</code> instance.
	 * @throws ParserConfigurationException
	 */
	public static DocumentBuilder getDocumentBuilder()
			throws ParserConfigurationException
	{
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
				.newInstance();

		return documentBuilderFactory.newDocumentBuilder();
	}

	/**
	 * Loads the given file using the default XML DOM implementation.
	 * 
	 * @param documentFile The file to load.
	 * @return The DOM document contained in the file.
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static Document loadDocument(File documentFile)
			throws ParserConfigurationException, SAXException, IOException
	{
		DocumentBuilder documentBuilder = getDocumentBuilder();

		return documentBuilder.parse(documentFile);
	}
}
