package org.eclipse.vtp.desktop.model.core.wsdl;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.vtp.desktop.model.core.schema.ElementItem;
import org.eclipse.vtp.desktop.model.core.schema.Schema;
import org.eclipse.vtp.desktop.model.core.schema.SchemaProblem;
import org.eclipse.vtp.desktop.model.core.schema.Type;
import org.eclipse.vtp.desktop.model.core.wsdl.soap.SoapBinding;
import org.eclipse.vtp.desktop.model.core.wsdl.soap.SoapBindingOperation;
import org.eclipse.vtp.desktop.model.core.wsdl.soap.SoapBindingOperationElement;
import org.eclipse.vtp.desktop.model.core.wsdl.soap.SoapBody;
import org.eclipse.vtp.desktop.model.core.wsdl.soap.SoapConstants;
import org.eclipse.vtp.desktop.model.core.wsdl.soap.SoapFault;
import org.eclipse.vtp.desktop.model.core.wsdl.soap.SoapHeader;
import org.eclipse.vtp.desktop.model.core.wsdl.soap.SoapHeaderFault;
import org.eclipse.vtp.framework.util.XMLUtilities;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class WSDL
{
	public static final String WSDL_NAMESPACE = "http://schemas.xmlsoap.org/wsdl/";
	private String targetNamespace = null;
	private Map<String, WSDL> wsdlsByNamespace = new HashMap<String, WSDL>();
	private List<Schema> schemas = new LinkedList<Schema>();
	private Map<String, Message> messagesByName = new HashMap<String, Message>();
	private Map<String, PortType> portTypesByName = new HashMap<String, PortType>();
	private Map<String, Binding> bindingsByName = new HashMap<String, Binding>();
	private Map<String, Service> servicesByName = new HashMap<String, Service>();
	private List<WSDLProblem> problems = new LinkedList<WSDLProblem>();

	public WSDL(Element wsdlElement)
	{
		targetNamespace = wsdlElement.getAttribute("targetNamespace");

		//add the default schema
		schemas.add(Schema.DEFAULT_SCHEMA);
		//load any imported wsdls
		List<Element> imports = XMLUtilities.getElementsByTagNameNS(wsdlElement, WSDL_NAMESPACE, "import", true);
		for(int i = 0; i < imports.size(); i++)
		{
			Attr namespaceAttr = imports.get(i).getAttributeNode("namespace");
			if(namespaceAttr != null && wsdlsByNamespace.get(namespaceAttr.getValue()) != null)
				continue; //already in the parent wsdl set
			String locationUri = null;
			Attr locationAttr = imports.get(i).getAttributeNode("location");
			if(locationAttr != null)
			{
				locationUri = locationAttr.getValue();
			}
			else
			{
				if(namespaceAttr != null)
					locationUri = namespaceAttr.getValue();
				else
				{
					addProblem(createProblem("No URI source for imported WSDL.", imports.get(i)));
					continue;
				}
			}
			try
			{
				URL url = new URL(locationUri);
				URLConnection con = url.openConnection();
				InputStream in = con.getInputStream();
				try
				{
					DocumentBuilderFactory factory =
						DocumentBuilderFactory.newInstance();
					factory.setNamespaceAware(true);
					DocumentBuilder builder = factory.newDocumentBuilder();
					Document document = builder.parse(in);
					org.w3c.dom.Element rootElement = document.getDocumentElement();
					WSDL wsdl = new WSDL(rootElement);
					if(wsdl.getSchemaProblems().size() > 0 || wsdl.getWSDLProblems().size() > 0)
					{
						addProblem(createProblem("Problems importing WSDL: " + locationUri, imports.get(i)));
						continue;
					}
					wsdlsByNamespace.put(wsdl.targetNamespace, wsdl);
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
					addProblem(createProblem("Error while importing WSDL.", imports.get(i), ex));
				}
				in.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
				addProblem(createProblem("Error while importing WSDL.", imports.get(i), e));
			}
		}
		
		List<Element> typesElementList = XMLUtilities.getElementsByTagNameNS(wsdlElement, WSDL_NAMESPACE, "types", true);
		if(typesElementList.size() > 0)
		{
			Element typesElement = typesElementList.get(0);
			List<Element> schemaElementList = XMLUtilities.getElementsByTagNameNS(typesElement, Schema.SCHEMA_NAMESPACE, "schema", true);
			for(Element schemaElement : schemaElementList)
			{
				Schema schema = new Schema(schemaElement);
				schemas.add(schema);
			}
		}
		
		List<Element> messageElementList = XMLUtilities.getElementsByTagNameNS(wsdlElement, WSDL_NAMESPACE, "message", true);
		for(Element messageElement : messageElementList)
		{
			String name = messageElement.getAttribute("name");
			Message message = new Message(name);
			List<Element> partElementList = XMLUtilities.getElementsByTagNameNS(messageElement, WSDL_NAMESPACE, "part", true);
			for(Element partElement : partElementList)
			{
				String partName = partElement.getAttribute("name");
				Attr elementAttr = partElement.getAttributeNode("element");
				if(elementAttr != null)
				{
					ElementItem elementItem = resolveElementItem(partElement, elementAttr.getValue());
					if(elementItem == null)
					{
						addProblem(createProblem("Could not resolve part element.", partElement));
						continue;
					}
					ElementPart part = new ElementPart(partName);
					part.setElementItem(elementItem);
					message.addPart(part);
				}
				else
				{
					Attr typeAttr = partElement.getAttributeNode("type");
					if(typeAttr == null)
					{
						addProblem(createProblem("Missing type or element attribute.", partElement));
						continue;
					}
					Type type = resolveType(partElement, typeAttr.getValue());
					if(type == null)
					{
						addProblem(createProblem("Could not resolve part type.", partElement));
						continue;
					}
					TypedPart part = new TypedPart(partName);
					part.setType(type);
					message.addPart(part);
				}
			}
			messagesByName.put(name, message);
		}
		
		List<Element> portTypeElementList = XMLUtilities.getElementsByTagNameNS(wsdlElement, WSDL_NAMESPACE, "portType", true);
		for(Element portTypeElement : portTypeElementList)
		{
			String portTypeName = portTypeElement.getAttribute("name");
			PortType portType = new PortType(portTypeName);
			List<Element> operationElementList = XMLUtilities.getElementsByTagNameNS(portTypeElement, WSDL_NAMESPACE, "operation", true);
			for(Element operationElement : operationElementList)
			{
				String operationName = operationElement.getAttribute("name");
				Operation operation = null;
				List<Element> children = XMLUtilities.getChildElementsNS(operationElement, WSDL_NAMESPACE);
				if(children.size() < 1)
				{
					addProblem(createProblem("No operation format specified: " + portTypeName + "(" + operationName + ")", operationElement));
					continue;
				}
				if(children.size() == 1) //must be one-way or notification
				{
					UnidirectionalOperation uo = new UnidirectionalOperation(operationName);
					String operationElementName = operationName;
					Element child = children.get(0);
					Attr operationElementNameAttr = child.getAttributeNode("name");
					if(operationElementNameAttr != null)
						operationElementName = operationElementNameAttr.getValue();
					OperationElement oe = null;
					if(child.getLocalName().equals("input")) //one-way
					{
						oe = new OperationElement(OperationElement.INPUT);
					}
					else if(child.getLocalName().equals("output"))
					{
						oe = new OperationElement(OperationElement.OUTPUT);
					}
					else
					{
						addProblem(createProblem("Unexpected operation element type: " + child.getLocalName(), child));
						continue;
					}
					oe.setName(operationElementName);
					Message message = resolveMessage(child, child.getAttribute("message"));
					if(message == null)
					{
						addProblem(createProblem("Could not resolve message type for operation element: " + child.getAttribute("message"), child));
						continue;
					}
					oe.setMessage(message);
					uo.setOperationElement(oe);
					operation = uo;
				}
				else
				{
					BidirectionalOperation bo = new BidirectionalOperation(operationName);
					String inputElementName = operationName;
					String outputElementName = operationName + "Response";
					OperationElement oe = null;
					Element firstElement = children.get(0);
					int offset = 2;
					if(firstElement.getLocalName().equals("documentation"))
					{
						//skip element
						firstElement = children.get(1);
						offset++;
					}
					if(firstElement.getLocalName().equals("input")) //request-response
					{
						inputElementName += "Request";
						oe = new OperationElement(OperationElement.INPUT);
						oe.setName(inputElementName);
						bo.setType(BidirectionalOperation.REQUEST);
						bo.setInputElement(oe);
					}
					else if(firstElement.getLocalName().equals("output")) //solicit-response
					{
						inputElementName += "Solicit";
						oe = new OperationElement(OperationElement.OUTPUT);
						oe.setName(outputElementName);
						bo.setType(BidirectionalOperation.SOLICIT);
						bo.setOutputElement(oe);
					}
					else
					{
						addProblem(createProblem("Unexpected operation element type: " + firstElement.getLocalName(), firstElement));
						continue;
					}
					Message message = resolveMessage(firstElement, firstElement.getAttribute("message"));
					if(message == null)
					{
						addProblem(createProblem("Could not resolve message type for operation element: " + firstElement.getAttribute("message"), firstElement));
						continue;
					}
					oe.setMessage(message);
					Element secondElement = children.get(offset - 1);
					if(secondElement.getLocalName().equals("input"))
					{
						oe = new OperationElement(OperationElement.INPUT);
						oe.setName(inputElementName);
						bo.setInputElement(oe);
					}
					else if(secondElement.getLocalName().equals("output"))
					{
						oe = new OperationElement(OperationElement.OUTPUT);
						oe.setName(outputElementName);
						bo.setOutputElement(oe);
					}
					else
					{
						addProblem(createProblem("Unexpected operation element type: " + secondElement.getLocalName(), secondElement));
						continue;
					}
					message = resolveMessage(secondElement, secondElement.getAttribute("message"));
					if(message == null)
					{
						addProblem(createProblem("Could not resolve message type for operation element: " + secondElement.getAttribute("message"), secondElement));
						continue;
					}
					oe.setMessage(message);
					for(int i = offset; i < children.size(); i++)
					{
						Element faultElement = children.get(i);
						if(!faultElement.getLocalName().equals("fault"))
						{
							addProblem(createProblem("Unexpected operation element type: " + faultElement.getLocalName(), faultElement));
							continue;
						}
						oe = new OperationElement(OperationElement.FAULT);
						message = resolveMessage(faultElement, faultElement.getAttribute("message"));
						if(message == null)
						{
							addProblem(createProblem("Could not resolve message type for fault element: " + faultElement.getAttribute("message"), faultElement));
							continue;
						}
						oe.setMessage(message);
						oe.setName(faultElement.getAttribute("name"));
						bo.addFault(oe);
					}
					operation = bo;
				}
				portType.addOperation(operation);
			}
			portTypesByName.put(portTypeName, portType);
		}
		
		List<Element> bindingElementList = XMLUtilities.getElementsByTagNameNS(wsdlElement, WSDL_NAMESPACE, "binding", true);
		for(Element bindingElement : bindingElementList)
		{
			String bindingName = bindingElement.getAttribute("name");
			PortType portType = resolvePortType(bindingElement, bindingElement.getAttribute("type"));
			Binding binding = null;
			List<Element> bindingExtensionElementList = XMLUtilities.getElementsByTagNameNS(bindingElement, "*", "binding", true);
			List<Element> operationElementList = XMLUtilities.getElementsByTagNameNS(bindingElement, WSDL_NAMESPACE, "operation", true);
			Element bindingExtensionElement = bindingExtensionElementList.get(0);
			if(bindingExtensionElement.getNamespaceURI().equals(SoapConstants.SOAP_NAMESPACE))
			{
				binding = parseSoapBinding(bindingName, portType, bindingExtensionElement, operationElementList);
			}
			if(binding == null)
				continue;
			bindingsByName.put(bindingName, binding);
		}
		
		List<Element> serviceElementList = XMLUtilities.getElementsByTagNameNS(wsdlElement, WSDL_NAMESPACE, "service", true);
		for(Element serviceElement : serviceElementList)
		{
			String serviceName = serviceElement.getAttribute("name");
			Service service = new Service(serviceName);
			List<Element> portElementList = XMLUtilities.getElementsByTagNameNS(serviceElement, WSDL_NAMESPACE, "port", true);
			for(Element portElement : portElementList)
			{
				String portName = portElement.getAttribute("name");
				String bindingName = portElement.getAttribute("binding");
				Binding binding = resolveBinding(portElement, bindingName);
				Port port = new Port(portName);
				port.setBinding(binding);
				service.addPort(port);
			}
			servicesByName.put(serviceName, service);
		}
	}
	
	public List<WSDLProblem> getWSDLProblems()
	{
		return problems;
	}
	
	private void addProblem(WSDLProblem problem)
	{
		problems.add(problem);
	}
	
	public Binding parseSoapBinding(String bindingName, PortType portType, Element bindingExtensionElement, List<Element> operationElementList)
	{
		SoapBinding binding = new SoapBinding(bindingName);
		Attr transportAttr = bindingExtensionElement.getAttributeNode("transport");
		if(transportAttr != null)
		{
			if(!SoapConstants.HTTP_TRANSPORT.equals(transportAttr.getValue()))
			{
				addProblem(createProblem("Unsupported soap transport encountered: " + transportAttr.getValue(), bindingExtensionElement));
				return null;
			}
		}
		Attr styleAttr = bindingExtensionElement.getAttributeNode("style");
		if(styleAttr != null)
			binding.setStyle(styleAttr.getValue());
		for(Element operationElement : operationElementList)
		{
			Operation operation = portType.getOperation(operationElement.getAttribute("name"));
			SoapBindingOperation bindingOperation = new SoapBindingOperation(operation);
			bindingOperation.setStyle(binding.getStyle());
			List<Element> operationExtensionElementList = XMLUtilities.getElementsByTagNameNS(operationElement, SoapConstants.SOAP_NAMESPACE, "operation", true);
			if(operationExtensionElementList.size() > 0) //this element is optional
			{
				Element operationExtensionElement = operationExtensionElementList.get(0);
				Attr soapActionAttr = operationExtensionElement.getAttributeNode("soapAction");
				if(soapActionAttr != null)
					bindingOperation.setSoapAction(soapActionAttr.getValue());
				styleAttr = bindingExtensionElement.getAttributeNode("style");
				if(styleAttr != null)
					bindingOperation.setStyle(styleAttr.getValue());
			}
			List<Element> operationItemElementList = XMLUtilities.getChildElementsNS(operationElement, WSDL_NAMESPACE);
			for(Element operationItemElement : operationItemElementList)
			{
				if(operationItemElement.getLocalName().equals("input"))
				{
					SoapBindingOperationElement bindingOperationElement = parseSoapBindingOperationItem(operationItemElement, operation);
					bindingOperation.setInput(bindingOperationElement);
				}
				else if(operationItemElement.getLocalName().equals("output"))
				{
					SoapBindingOperationElement bindingOperationElement = parseSoapBindingOperationItem(operationItemElement, operation);
					bindingOperation.setOutput(bindingOperationElement);
				}
				else //fault
				{
					String faultName = operationItemElement.getAttribute("name");
					String use = operationItemElement.getAttribute("use");
					SoapFault soapFault = new SoapFault(faultName, use);
					bindingOperation.addFault(soapFault);
				}
			}
			binding.addOperation(bindingOperation);
		}
		return binding;
	}
	
	public SoapBindingOperationElement parseSoapBindingOperationItem(Element operationItemElement, Operation operation)
	{
		SoapBindingOperationElement bindingOperationItem = new SoapBindingOperationElement();
		List<Element> headerElementList = XMLUtilities.getElementsByTagNameNS(operationItemElement, SoapConstants.SOAP_NAMESPACE, "header", true);
		for(Element headerElement : headerElementList)
		{
			String messageName = headerElement.getAttribute("message");
			Message headerMessage = resolveMessage(headerElement, messageName);
			if(headerMessage == null)
			{
				addProblem(createProblem("Could not resolve header message: " + messageName, headerElement));
				return null;
			}
			String partName = headerElement.getAttribute("part");
			Part headerPart = headerMessage.getPart(partName);
			if(headerPart == null)
			{
				addProblem(createProblem("Could not locate part: " + partName + " in message: " + messageName, headerElement));
				return null;
			}
			String headerUse = headerElement.getAttribute("use");
			SoapHeader header = new SoapHeader(headerMessage, headerPart, headerUse);
			List<Element> headerFaultElementList = XMLUtilities.getElementsByTagNameNS(headerElement, SoapConstants.SOAP_NAMESPACE, "headerfault", true);
			for(Element headerFaultElement : headerFaultElementList)
			{
				messageName = headerFaultElement.getAttribute("message");
				Message headerFaultMessage = resolveMessage(headerFaultElement, messageName);
				if(headerFaultMessage == null)
				{
					addProblem(createProblem("Could not resolve header fault message: " + messageName, headerFaultElement));
					return null;
				}
				partName = headerFaultElement.getAttribute("part");
				Part headerFaultPart = headerFaultMessage.getPart(partName);
				if(headerFaultPart == null)
				{
					addProblem(createProblem("Could not locate part: " + partName + " in message: " + messageName, headerFaultElement));
					return null;
				}
				String headerFaultUse = headerFaultElement.getAttribute("use");
				SoapHeaderFault headerFault = new SoapHeaderFault(headerFaultMessage, headerFaultPart, headerFaultUse);
				header.addHeaderFault(headerFault);
			}
			bindingOperationItem.addHeader(header);
		}
		List<Element> bodyElementList = XMLUtilities.getElementsByTagNameNS(operationItemElement, SoapConstants.SOAP_NAMESPACE, "body", true);
		if(bodyElementList.size() != 1)
		{
			addProblem(createProblem("Wrong number of soap body elements in " + operationItemElement.getLocalName() + " for operation: " + operation.getName(), operationItemElement));
			return null;
		}
		Element bodyElement = bodyElementList.get(0);
		SoapBody body = new SoapBody();
		body.setUsage(bodyElement.getAttribute("use"));
		OperationElement oe = null;
		if(operation instanceof UnidirectionalOperation)
		{
			oe = ((UnidirectionalOperation)operation).getOperationElement();
		}
		else
		{
			if(operationItemElement.getLocalName().equals(OperationElement.INPUT))
				oe = ((BidirectionalOperation)operation).getInputElement();
			else
				oe = ((BidirectionalOperation)operation).getOutputElement();
		}
		System.out.println(oe);
		System.out.println(oe.getMessage());
		List<Part> parts = oe.getMessage().getParts();
		Attr partsAttr = bodyElement.getAttributeNode("parts");
		if(partsAttr != null)
		{
			String[] partNames = partsAttr.getValue().split(" ");
			for(String partName : partNames)
			{
				Part part = oe.getMessage().getPart(partName);
				if(part == null)
				{
					addProblem(createProblem("Could not locate part for soap body: " + partName, bodyElement));
					return null;
				}
				body.addPart(part);
			}
		}
		else
		{
			for(Part part : parts)
			{
				body.addPart(part);
			}
		}
		bindingOperationItem.setBody(body);
		return bindingOperationItem;
	}

	public ElementItem resolveElementItem(Element hostElement, String qName)
	{
		String[] baseParts = qName.split(":");
		String prefix = null;
		if(baseParts.length > 1)
			prefix = baseParts[0];
		String nameSpace = hostElement.lookupNamespaceURI(prefix);
		String partialName = baseParts[baseParts.length - 1];
		for(Schema schema : schemas)
		{
			ElementItem item = schema.resolveElementItem(nameSpace, partialName);
			if(item != null)
				return item;
		}
		return null;
	}
	
	public Type resolveType(Element hostElement, String qName)
	{
		String[] baseParts = qName.split(":");
		String prefix = null;
		if(baseParts.length > 1)
			prefix = baseParts[0];
		String nameSpace = hostElement.lookupNamespaceURI(prefix);
		String partialName = baseParts[baseParts.length - 1];
		for(Schema schema : schemas)
		{
			Type type = schema.resolveType(nameSpace, partialName);
			if(type != null)
				return type;
		}
		return null;
	}

	public Message resolveMessage(Element hostElement, String qName)
	{
		String[] baseParts = qName.split(":");
		String prefix = null;
		if(baseParts.length > 1)
			prefix = baseParts[0];
		String nameSpace = hostElement.lookupNamespaceURI(prefix);
		String partialName = baseParts[baseParts.length - 1];
		return resolveMessage(nameSpace, partialName);
	}
	
	public Message resolveMessage(String uri, String name)
	{
		if(uri == null || uri.equals(targetNamespace))
			return messagesByName.get(name);
		else
		{
			WSDL wsdl = wsdlsByNamespace.get(uri);
			if(wsdl == null)
				return null;
			return wsdl.getMessage(name);
		}
	}
	
	public Message getMessage(String name)
	{
		return messagesByName.get(name);
	}

	public PortType resolvePortType(Element hostElement, String qName)
	{
		String[] baseParts = qName.split(":");
		String prefix = null;
		if(baseParts.length > 1)
			prefix = baseParts[0];
		String nameSpace = hostElement.lookupNamespaceURI(prefix);
		String partialName = baseParts[baseParts.length - 1];
		return resolvePortType(nameSpace, partialName);
	}
	
	public PortType resolvePortType(String uri, String name)
	{
		if(uri == null || uri.equals(targetNamespace))
			return portTypesByName.get(name);
		else
		{
			WSDL wsdl = wsdlsByNamespace.get(uri);
			if(wsdl == null)
				return null;
			return wsdl.getPortType(name);
		}
	}
	
	public PortType getPortType(String name)
	{
		return portTypesByName.get(name);
	}

	public Binding resolveBinding(Element hostElement, String qName)
	{
		String[] baseParts = qName.split(":");
		String prefix = null;
		if(baseParts.length > 1)
			prefix = baseParts[0];
		String nameSpace = hostElement.lookupNamespaceURI(prefix);
		String partialName = baseParts[baseParts.length - 1];
		return resolveBinding(nameSpace, partialName);
	}
	
	public Binding resolveBinding(String uri, String name)
	{
		if(uri == null || uri.equals(targetNamespace))
			return bindingsByName.get(name);
		else
		{
			WSDL wsdl = wsdlsByNamespace.get(uri);
			if(wsdl == null)
				return null;
			return wsdl.getBinding(name);
		}
	}
	
	public Binding getBinding(String name)
	{
		return bindingsByName.get(name);
	}
	
	public Service getService(String name)
	{
		return servicesByName.get(name);
	}
	
	public List<Service> getServices()
	{
		return new LinkedList<Service>(servicesByName.values());
	}
	
	public List<SchemaProblem> getSchemaProblems()
	{
		List<SchemaProblem> problems = new LinkedList<SchemaProblem>();
		for(Schema schema : schemas)
		{
			problems.addAll(schema.getProblems());
		}
		return problems;
	}
	
	private WSDLProblem createProblem(String message, Element source)
	{
		return createProblem(message, source, null);
	}
	
	private WSDLProblem createProblem(String message, Element source, Throwable t)
	{
		Object userData = source.getUserData("line_Number");
		int lineNumber = -1;
		if(userData != null && userData instanceof String)
		{
			try
			{
				lineNumber = Integer.parseInt((String)userData);
			}
			catch(NumberFormatException nfe)
			{
			}
		}
		return new WSDLProblem(message, lineNumber, t);
	}
	
	public static void main(String[] args)
	{
		if(args.length < 1)
		{
			System.out.println("Usage: java WSDL file_path");
			System.exit(1);
		}
		File file = new File(args[0]);
		if(!file.exists())
		{
			System.out.println("Usage: java WSDL file_path");
			System.exit(1);
		}
		try
		{
			DocumentBuilderFactory factory =
				DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(file);
			org.w3c.dom.Element rootElement = document.getDocumentElement();
			@SuppressWarnings("unused")
			WSDL wsdl = new WSDL(rootElement);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
}
