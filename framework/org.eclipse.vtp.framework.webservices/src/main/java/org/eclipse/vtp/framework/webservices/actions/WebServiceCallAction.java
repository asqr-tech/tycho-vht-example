/*--------------------------------------------------------------------------
 * Copyright (c) 2004, 2006-2007 OpenMethods, LLC
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Trip Gilman (OpenMethods), Lonnie G. Pryor (OpenMethods), 
 *    Randy Childers (OpenMethods)
 *    - initial API and implementation
 -------------------------------------------------------------------------*/
package org.eclipse.vtp.framework.webservices.actions;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.vtp.framework.common.IArrayObject;
import org.eclipse.vtp.framework.common.IBrandSelection;
import org.eclipse.vtp.framework.common.IDataObject;
import org.eclipse.vtp.framework.common.IScriptingEngine;
import org.eclipse.vtp.framework.common.IScriptingService;
import org.eclipse.vtp.framework.common.IStringObject;
import org.eclipse.vtp.framework.common.IVariableRegistry;
import org.eclipse.vtp.framework.core.IAction;
import org.eclipse.vtp.framework.core.IActionContext;
import org.eclipse.vtp.framework.core.IActionResult;
import org.eclipse.vtp.framework.core.IReporter;
import org.eclipse.vtp.framework.util.XMLUtilities;
import org.eclipse.vtp.framework.webservices.configurations.WebServiceConfiguration;
import org.eclipse.vtp.framework.webservices.configurations.document.BindingValue;
import org.eclipse.vtp.framework.webservices.configurations.document.ConditionalContainerSet;
import org.eclipse.vtp.framework.webservices.configurations.document.ConditionalDocumentItem;
import org.eclipse.vtp.framework.webservices.configurations.document.DocumentItem;
import org.eclipse.vtp.framework.webservices.configurations.document.DocumentItemContainer;
import org.eclipse.vtp.framework.webservices.configurations.document.ElementAttributeDocumentItem;
import org.eclipse.vtp.framework.webservices.configurations.document.ElementDocumentItem;
import org.eclipse.vtp.framework.webservices.configurations.document.ElseDocumentItem;
import org.eclipse.vtp.framework.webservices.configurations.document.ForLoopDocumentItem;
import org.eclipse.vtp.framework.webservices.configurations.document.InputDocumentStructure;
import org.eclipse.vtp.framework.webservices.configurations.document.TextDocumentItem;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * WebServiceCallAction.
 * 
 * @author Lonnie Pryor
 */
public class WebServiceCallAction implements IAction
{
	public static final String SOAP_ENVELOPE_URI = "http://schemas.xmlsoap.org/soap/envelope/";
	/** The context to use. */
	private final IActionContext context;
	/** The variable registry to use. */
	private final IVariableRegistry variables;
	private final WebServiceConfiguration configuration;
	private final IScriptingService scriptingService;
	private final IBrandSelection brandSelection;

	/**
	 * Creates a new WebServiceCallAction.
	 * 
	 * @param context The context to use.
	 * @param types The data type registry to use.
	 * @param variables The variable registry to use.
	 * @param webServices The web service registry to use.
	 * @param configuration The configuration to use.
	 */
	public WebServiceCallAction(IActionContext context,
								IVariableRegistry variables,
								IScriptingService scriptingService,
								IBrandSelection brandSelection,
								WebServiceConfiguration configuration)
	{
		this.context = context;
		this.variables = variables;
		this.scriptingService = scriptingService;
		this.brandSelection = brandSelection;
		this.configuration = configuration;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.core.IAction#execute()
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public IActionResult execute()
	{
		if(context.isReportingEnabled())
		{
			Dictionary props = new Hashtable();
			props.put("event", "wscall.before");
			context.report(IReporter.SEVERITY_INFO, "Calling web service...", props);
		}
		try
		{
			StringBuilder payload = new StringBuilder();
			payload.append("<SOAP-ENV:Envelope");
			payload.append(" xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">");//\"\r\n");
//			payload.append("\tSOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">");
			payload.append("<SOAP-ENV:Body>");
			InputDocumentStructure structure = configuration.getInputStructure();
			processDocumentItemContainer(payload, structure);
			payload.append("</SOAP-ENV:Body>");
			payload.append("</SOAP-ENV:Envelope>");
			URL url = null;
			String urlType = configuration.getURLType();
			if(urlType.equals(WebServiceConfiguration.STATIC))
				url = new URL(configuration.getURL());
			else if(urlType.equals(WebServiceConfiguration.VARIABLE))
				url = new URL(String.valueOf(variables.getVariable(configuration.getURL())));
			else
			{
				IScriptingEngine engine = scriptingService.createScriptingEngine("JavaScript");
				url = new URL(String.valueOf(engine.execute(configuration.getURL())));
			}
			if(url != null)
			{
				HttpURLConnection con = (HttpURLConnection)url.openConnection();
				con.setDoOutput(true);
				con.setRequestMethod("POST");
				con.setRequestProperty("Content-Type", "text/xml; charset=\"utf-8\"");
				con.setRequestProperty("SOAPAction", configuration.getSoapAction() == null ? "" : "\"" + configuration.getSoapAction() + "\"");
				OutputStream out = con.getOutputStream();
				Writer ps = new OutputStreamWriter(out);
				ps.write(payload.toString());
				context.debug(payload.toString());
				ps.flush();
				ps.close();
				int rc = con.getResponseCode();
				if(rc == 200)
				{
					DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
					factory.setNamespaceAware(true);
					DocumentBuilder builder = factory.newDocumentBuilder();
					Document document = builder.parse(con.getInputStream());
					Element rootElement = document.getDocumentElement();
					if(!rootElement.getLocalName().equals("Envelope"))
					{
						if(context.isErrorEnabled())
						{
							context.error("Webservice returned invalid document");
							ByteArrayOutputStream baos = new ByteArrayOutputStream();
							Transformer transformer = TransformerFactory.newInstance().newTransformer();
							transformer.transform(new DOMSource(document), new StreamResult(baos));
							context.error(baos.toString());
							if(context.isReportingEnabled())
							{
								Dictionary props = new Hashtable();
								props.put("event", "wscall.after");
								context.report(IReporter.SEVERITY_INFO,
										"Called web service but received invalid response", props);
							}
							return context.createResult("error.webservice");
						}
					}
					IDataObject resultObject = variables.createVariable("WSResponse");
					variables.setVariable(configuration.getVariableName(), resultObject);
					IArrayObject headerArrayObject = (IArrayObject)variables.createVariable(IArrayObject.TYPE_NAME);
					resultObject.setField("headers", headerArrayObject);
					List<Element> headerElementList = XMLUtilities.getElementsByTagNameNS(rootElement, SOAP_ENVELOPE_URI, "Header", true);
					if(headerElementList.size() > 0)
					{
						Element headerElement = headerElementList.get(0);
						List<Element> entryList = XMLUtilities.getChildElements(headerElement);
						for(Element entryElement : entryList)
						{
							ByteArrayOutputStream baos = new ByteArrayOutputStream();
							Transformer transformer = TransformerFactory.newInstance().newTransformer();
							transformer.transform(new DOMSource(entryElement), new StreamResult(baos));
							IDataObject headerObject = variables.createVariable("WSHeader");
							IStringObject nameObject = (IStringObject)variables.createVariable(IStringObject.TYPE_NAME);
							nameObject.setValue(entryElement.getLocalName());
							IStringObject rawContent = (IStringObject)variables.createVariable(IStringObject.TYPE_NAME);
							rawContent.setValue(baos.toString());
							headerObject.setField("name", nameObject);
							headerObject.setField("rawContent", rawContent);
						}
					}
					List<Element> bodyElementList = XMLUtilities.getElementsByTagNameNS(rootElement, SOAP_ENVELOPE_URI, "Body", true);
					if(bodyElementList.size() > 0)
					{
						Element bodyElement = bodyElementList.get(0);
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						Transformer transformer = TransformerFactory.newInstance().newTransformer();
						transformer.transform(new DOMSource(bodyElement), new StreamResult(baos));
						IStringObject rawContent = (IStringObject)variables.createVariable(IStringObject.TYPE_NAME);
						rawContent.setValue(baos.toString());
						resultObject.setField("rawContent", rawContent);
					}
				}
				else
				{
					if(context.isErrorEnabled())
					{
						context.error("Web service call failed: " + Integer.toString(rc));
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						InputStream in = con.getErrorStream();
						byte[] buf = new byte[1024];
						int len = in.read(buf);
						while(len != -1)
						{
							baos.write(buf, 0, len);
							len = in.read(buf);
						}
						in.close();
						con.disconnect();
						context.debug(baos.toString());
					}
					if(context.isReportingEnabled())
					{
						Dictionary props = new Hashtable();
						props.put("event", "wscall.after");
						props.put("http.response", Integer.toString(rc));
						context.report(IReporter.SEVERITY_INFO,
								"Call web service failed: " + Integer.toString(rc), props);
					}
					return context.createResult("error.webservice");
				}
			}
		}
		catch (Exception e)
		{
			if(context.isErrorEnabled())
			{
				context.error("Web service call failed: ");
				e.printStackTrace();
			}
			if(context.isReportingEnabled())
			{
				Dictionary props = new Hashtable();
				props.put("event", "wscall.after");
				context.report(IReporter.SEVERITY_INFO,
						"Call web service failed: " + e, props);
			}
			return context.createResult("error.webservice", e);
		}
		if(context.isReportingEnabled())
		{
			Dictionary props = new Hashtable();
			props.put("event", "wscall.after");
			context.report(IReporter.SEVERITY_INFO,
					"Called web service", props);
		}
		return context.createResult(IActionResult.RESULT_NAME_DEFAULT);
	}

	private void processDocumentItemContainer(StringBuilder payload, DocumentItemContainer container)
	{
		List<DocumentItem> children = container.getItems();
		for(DocumentItem child : children)
		{
			if(child instanceof ConditionalContainerSet)
			{
				processConditionalContainerSet(payload, (ConditionalContainerSet)child);
			}
			else if(child instanceof ForLoopDocumentItem)
			{
				ForLoopDocumentItem fldi = (ForLoopDocumentItem)child;
				String sourceName = fldi.getTransform();
				String targetVariable = fldi.getVariableName();
				IDataObject source = variables.getVariable(sourceName);
				if(source != null)
				{
					if(source instanceof IArrayObject)
					{
						IArrayObject sourceArray = (IArrayObject)source;
						for(int i = 0; i < sourceArray.getLength().getValue(); i++)
						{
							IDataObject currentObject = sourceArray.getElement(i);
							variables.setVariable(targetVariable, currentObject);
							processDocumentItemContainer(payload, fldi);
						}
					}
					else
					{
						variables.setVariable(targetVariable, source);
						processDocumentItemContainer(payload, fldi);
					}
				}
			}
			else if(child instanceof ElementDocumentItem)
			{
				ElementDocumentItem elementItem = (ElementDocumentItem)child;
				payload.append("<").append(elementItem.getName());
				if(elementItem.getNamespace() != null && !elementItem.getNamespace().equals(""))
				{
					payload.append(" xmlns=\"").append(elementItem.getNamespace()).append("\"");
				}
//				else
//				{
//					payload.append(" xmlns=\"http://example1.org/example1\"");
//				}
				List<ElementAttributeDocumentItem> attributes = elementItem.getAttributes();
				for(ElementAttributeDocumentItem attribute : attributes)
				{
					payload.append(" ").append(attribute.getName()).append("=\"");
					payload.append(resolveValue(attribute.getBrandBinding(brandSelection.getSelectedBrand())));
					payload.append("\"");
				}
				payload.append(">");
				processDocumentItemContainer(payload, elementItem);
				payload.append("</").append(elementItem.getName()).append(">");
			}
			else //text document item
			{
				payload.append(resolveValue(((TextDocumentItem)child).getBrandBinding(brandSelection.getSelectedBrand())));
			}
		}
	}
	
	private void processConditionalContainerSet(StringBuilder payload, ConditionalContainerSet container)
	{
		IScriptingEngine engine = scriptingService.createScriptingEngine("JavaScript");
		ConditionalDocumentItem ifItem = container.getIf();
		if(Boolean.valueOf(String.valueOf(engine.execute(ifItem.getCondition()))))
		{
			processDocumentItemContainer(payload, ifItem);
			return;
		}
		for(ConditionalDocumentItem elseIfItem : container.getElseIfs())
		{
			if(Boolean.valueOf(String.valueOf(engine.execute(elseIfItem.getCondition()))))
			{
				processDocumentItemContainer(payload, elseIfItem);
				return;
			}
		}
		ElseDocumentItem elseItem = container.getElse();
		if(elseItem != null)
		{
			processDocumentItemContainer(payload, elseItem);
		}
	}
	
	private String resolveValue(BindingValue value)
	{
		String type = value.getValueType();
		if(type.equals(BindingValue.STATIC))
			return value.getValue();
		else if(type.equals(BindingValue.VARIABLE))
			return String.valueOf(variables.getVariable(value.getValue()));
		else //expression
		{
			IScriptingEngine engine = scriptingService.createScriptingEngine("JavaScript");
			return String.valueOf(engine.execute(value.getValue()));
		}
	}
	
//	private boolean matchesElement(String localname)
}
