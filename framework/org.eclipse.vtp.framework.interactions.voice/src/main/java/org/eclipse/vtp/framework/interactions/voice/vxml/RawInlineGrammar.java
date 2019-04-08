package org.eclipse.vtp.framework.interactions.voice.vxml;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class RawInlineGrammar extends Grammar
{
	private String text = "";

	public RawInlineGrammar(String text) throws IllegalArgumentException,
			NullPointerException
	{
		super(GRAMMAR_MODE_DTMF);
		this.text = text;
	}

	@Override
	public void writeWidget(ContentHandler outputHandler)
			throws NullPointerException, SAXException
	{
		if(text == null || text.equals(""))
			return;
		try
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			System.err.println(text);
			ByteArrayInputStream bais = new ByteArrayInputStream(text.getBytes());
			Document document = builder.parse(bais);
			bais.close();
			Element docElement = document.getDocumentElement();
			writeElement(outputHandler, docElement);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	private void writeElement(ContentHandler outputHandler, Element element) throws Exception
	{
		AttributesImpl attributes = new AttributesImpl();
		NamedNodeMap attributeMap = element.getAttributes();
		for(int i = 0; i < attributeMap.getLength(); i++)
		{
			Attr attribute = (Attr)attributeMap.item(i);
			writeAttribute(attributes, attribute.getNamespaceURI(), attribute.getLocalName(), attribute.getName(), TYPE_CDATA, attribute.getValue());
		}
		outputHandler.startElement(element.getNamespaceURI(), element.getLocalName(), element.getNodeName(), attributes);
		NodeList nl = element.getChildNodes();
		for(int i = 0; i < nl.getLength(); i++)
		{
			Node node = nl.item(i);
			if(node.getNodeType() == Node.ELEMENT_NODE)
				writeElement(outputHandler, (Element)node);
			else if(node.getNodeType() == Node.CDATA_SECTION_NODE || node.getNodeType() == Node.TEXT_NODE || node.getNodeType() == Node.COMMENT_NODE)
			{
				char[] charArray = ((CharacterData)node).getData().toCharArray();
				outputHandler.characters(charArray, 0, charArray.length);
			}
		}
		outputHandler.endElement(element.getNamespaceURI(), element.getLocalName(), element.getNodeName());
	}
}
