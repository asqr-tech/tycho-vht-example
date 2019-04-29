package org.eclipse.vtp.desktop.model.core.schema;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.vtp.framework.util.XMLUtilities;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Schema
{
	public static final String SCHEMA_NAMESPACE = "http://www.w3.org/2001/XMLSchema";
	public static final Schema DEFAULT_SCHEMA = new Schema();
	
	private String targetNamespace = null;
	private boolean qualifyElements = false;
	private boolean qualifyAttributes = false;
	private Map<String, Schema> schemasByNamespace = new HashMap<String, Schema>();
	private Map<String, Type> localTypesByName = new HashMap<String, Type>();
	private Map<String, ElementItem> globalElementsByName = new HashMap<String, ElementItem>();
	private Map<String, AttributeItem> globalAttributesByName = new HashMap<String, AttributeItem>();
	private Map<String, AttributeGrouping> globalAttributeGroupsByName = new HashMap<String, AttributeGrouping>();
	private Map<String, ElementGrouping> globalElementGroupsByName = new HashMap<String, ElementGrouping>();
	private List<SchemaProblem> problems = new LinkedList<SchemaProblem>();
	
	protected Schema() //constructs the root schema
	{
		super();
		targetNamespace = SCHEMA_NAMESPACE;
		qualifyElements = false;
		qualifyAttributes = false;
		localTypesByName.put("anySimpleType", new SimpleType(this, "anySimpleType"));
		localTypesByName.put("duration", new SimpleType(this, "duration"));
		localTypesByName.put("dateTime", new SimpleType(this, "dateTime"));
		localTypesByName.put("time", new SimpleType(this, "time"));
		localTypesByName.put("date", new SimpleType(this, "date"));
		localTypesByName.put("gYearMonth", new SimpleType(this, "gYearMonth"));
		localTypesByName.put("gYear", new SimpleType(this, "gYear"));
		localTypesByName.put("gMonthDay", new SimpleType(this, "gMonthDay"));
		localTypesByName.put("gDay", new SimpleType(this, "gDay"));
		localTypesByName.put("gMonth", new SimpleType(this, "gMonth"));
		localTypesByName.put("boolean", new SimpleType(this, "boolean"));
		localTypesByName.put("base64Binary", new SimpleType(this, "base64Binary"));
		localTypesByName.put("hexBinary", new SimpleType(this, "hexBinary"));
		localTypesByName.put("float", new SimpleType(this, "float"));
		localTypesByName.put("double", new SimpleType(this, "double"));
		localTypesByName.put("anyURI", new SimpleType(this, "anyURI"));
		localTypesByName.put("QName", new SimpleType(this, "QName"));
		localTypesByName.put("NOTATION", new SimpleType(this, "NOTATION"));
		localTypesByName.put("string", new SimpleType(this, "string"));
		localTypesByName.put("decimal", new SimpleType(this, "decimal"));
		localTypesByName.put("normalizedString", new SimpleType(this, "normalizedString"));
		localTypesByName.put("token", new SimpleType(this, "token"));
		localTypesByName.put("language", new SimpleType(this, "language"));
		localTypesByName.put("Name", new SimpleType(this, "Name"));
		localTypesByName.put("NMTOKEN", new SimpleType(this, "NMTOKEN"));
		localTypesByName.put("NCName", new SimpleType(this, "NCName"));
		localTypesByName.put("ID", new SimpleType(this, "ID"));
		localTypesByName.put("IDREF", new SimpleType(this, "IDREF"));
		localTypesByName.put("ENTITY", new SimpleType(this, "ENTITY"));
		localTypesByName.put("integer", new SimpleType(this, "integer"));
		localTypesByName.put("nonPositiveInteger", new SimpleType(this, "nonPositiveInteger"));
		localTypesByName.put("long", new SimpleType(this, "long"));
		localTypesByName.put("nonNegativeInteger", new SimpleType(this, "nonNegativeInteger"));
		localTypesByName.put("negativeInteger", new SimpleType(this, "negativeInteger"));
		localTypesByName.put("int", new SimpleType(this, "int"));
		localTypesByName.put("unsignedLong", new SimpleType(this, "unsignedLong"));
		localTypesByName.put("positiveInteger", new SimpleType(this, "positiveInteger"));
		localTypesByName.put("short", new SimpleType(this, "short"));
		localTypesByName.put("unsignedInt", new SimpleType(this, "unsignedInt"));
		localTypesByName.put("byte", new SimpleType(this, "byte"));
		localTypesByName.put("unsignedShort", new SimpleType(this, "unsignedShort"));
		localTypesByName.put("unsignedByte", new SimpleType(this, "unsignedByte"));
		localTypesByName.put("NMTOKENS", new SimpleType(this, "NMTOKENS"));
		localTypesByName.put("IDREFS", new SimpleType(this, "IDREFS"));
		localTypesByName.put("ENTITIES", new SimpleType(this, "ENTITIES"));
		ComplexType anyType = new ComplexType(this, "anyType");
		anyType.setContentModel(new ComplexContentModel(true));
		localTypesByName.put("anyType", anyType);
	}
	
	public Schema(Element schemaElement)
	{
		super();
		addSchema(new Schema());
		LinkedList<SchemaProblem> problemCollector = new LinkedList<SchemaProblem>();
		targetNamespace = schemaElement.getAttribute("targetNamespace");
		qualifyElements = schemaElement.getAttribute("elementFormDefault").equals("qualified");
		qualifyAttributes = schemaElement.getAttribute("attributeFormDefault").equals("qualified");
		
		List<Element> redefines = XMLUtilities.getElementsByTagNameNS(schemaElement, SCHEMA_NAMESPACE, "redefine", true);
		if(redefines.size() > 0)
		{
			for(Element redefineElement : redefines)
			{
				problemCollector.add(createProblem("The redefine schema feature is not supported by this parser.", redefineElement));
			}
		}

		List<Element> imports = XMLUtilities.getElementsByTagNameNS(schemaElement, SCHEMA_NAMESPACE, "import", true);
		for(int i = 0; i < imports.size(); i++)
		{
			Attr namespaceAttr = imports.get(i).getAttributeNode("namespace");
			if(namespaceAttr != null && schemasByNamespace.get(namespaceAttr.getValue()) != null)
				continue; //already in the parent schema set
			String locationUri = null;
			Attr locationAttr = imports.get(i).getAttributeNode("schemaLocation");
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
					problemCollector.add(createProblem("Unable to import schema", imports.get(i)));
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
					Schema schema = new Schema(rootElement);
					if(schema.getProblems().size() > 0)
						problemCollector.add(createProblem("Unable to import schema location: " + locationUri, imports.get(i)));
					else
						schemasByNamespace.put(schema.targetNamespace, schema);
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
					problemCollector.add(createProblem("Unable to import schema location: " + locationUri, imports.get(i), ex));
				}
				in.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
				problemCollector.add(createProblem("Unable to import schema location: " + locationUri, imports.get(i), e));
			}
		}
		
		List<Element> processList = XMLUtilities.getElementsByTagNameNS(schemaElement, SCHEMA_NAMESPACE, "simpleType", true);
		int numResolved = 0;
		do
		{
			setProblems(new LinkedList<SchemaProblem>());
			numResolved = 0;
			Iterator<Element> iterator = processList.iterator();
			while(iterator.hasNext())
			{
				SimpleType simpleType = parseSimpleType(iterator.next());
				if(simpleType != null)
				{
					localTypesByName.put(simpleType.getName(), simpleType);
					iterator.remove();
					numResolved++;
				}
			}
		}
		while(processList.size() > 0 && numResolved > 0);
		if(processList.size() > 0)
			problemCollector.addAll(getProblems());
		setProblems(new LinkedList<SchemaProblem>());
		List<Element> attributeItemElementList = XMLUtilities.getElementsByTagNameNS(schemaElement, SCHEMA_NAMESPACE, "attribute", true);
		for(int i = 0; i < attributeItemElementList.size(); i++)
		{
			Element attributeItemElement = attributeItemElementList.get(i);
			System.out.println("parsing global attribute: " + attributeItemElement.getAttribute("name"));
			AttributeItem attributeItem = parseAttributeItem(attributeItemElement);
			globalAttributesByName.put(attributeItem.getName(), attributeItem);
		}
		problemCollector.addAll(getProblems());
		processList = XMLUtilities.getElementsByTagNameNS(schemaElement, SCHEMA_NAMESPACE, "attributeGroup", true);
		numResolved = 0;
		do
		{
			setProblems(new LinkedList<SchemaProblem>());
			numResolved = 0;
			Iterator<Element> iterator = processList.iterator();
			while(iterator.hasNext())
			{
				Element attributeGroupElement = iterator.next();
				AttributeGrouping attributeGrouping = new AttributeGrouping(this, attributeGroupElement.getAttribute("name"));
				if(parseAttributeObjects(attributeGrouping, attributeGroupElement))
				{
					globalAttributeGroupsByName.put(attributeGrouping.getName(), attributeGrouping);
					iterator.remove();
					numResolved++;
				}
			}
		}
		while(processList.size() > 0 && numResolved > 0);
		if(processList.size() > 0)
			problemCollector.addAll(getProblems());
		//pre-process all the global element declarations.
		List<Element> elementItemElementList = XMLUtilities.getElementsByTagNameNS(schemaElement, SCHEMA_NAMESPACE, "element", true);
		for(int i = 0; i < elementItemElementList.size(); i++)
		{
			Element elementItemElement = elementItemElementList.get(i);
			ElementItem elementItem = new ElementItem(this, elementItemElement.getAttribute("name"));
			globalElementsByName.put(elementItem.getName(), elementItem);
		}
		//pre-process all the global element groupings
		setProblems(new LinkedList<SchemaProblem>());
		List<Element> elementGroupElementList = XMLUtilities.getElementsByTagNameNS(schemaElement, SCHEMA_NAMESPACE, "group", true);
		for(int i = 0; i < elementGroupElementList.size(); i++)
		{
			Element elementGroupElement = elementGroupElementList.get(i);
			ElementGrouping elementGrouping = new ElementGrouping(this, elementGroupElement.getAttribute("name"));
			NodeList childList = elementGroupElement.getChildNodes();
			for(int c = 0; c < childList.getLength(); c++)
			{
				if(childList.item(c).getNodeType() == Node.ELEMENT_NODE)
				{
					Element element = (Element)childList.item(c);
					if(element.getLocalName().equals("annotation"))
						continue;
					ElementGroup eg = parseElementGroup(element);
					if(eg != null)
						elementGrouping.setElementGroup(eg);
				}
			}
			globalElementGroupsByName.put(elementGrouping.getName(), elementGrouping);
		}
		problemCollector.addAll(getProblems());
		processList = XMLUtilities.getElementsByTagNameNS(schemaElement, SCHEMA_NAMESPACE, "complexType", true);
		numResolved = 0;
		do
		{
			setProblems(new LinkedList<SchemaProblem>());
			numResolved = 0;
			Iterator<Element> iterator = processList.iterator();
			while(iterator.hasNext())
			{
				Element complexTypeElement = iterator.next();
				ComplexType complexType = new ComplexType(this, complexTypeElement.getAttribute("name"));
				//temporarily place this type into the index in case it is recursive
				localTypesByName.put(complexType.getName(), complexType);
				System.out.println("Attempting to resolve global complex type: " + complexType.getName());
				if(populateComplexType(complexType, complexTypeElement))
				{
					System.out.println("resolved complex type");
					iterator.remove();
					numResolved++;
				}
				else
				{
					System.out.println("failed to resolve complex type");
					localTypesByName.remove(complexType.getName());
				}
			}
		}
		while(processList.size() > 0 && numResolved > 0);
		if(processList.size() > 0)
		{
			System.out.println(processList.get(0).getAttribute("name"));
			problemCollector.addAll(getProblems());
		}
		//populate the global elements
		setProblems(new LinkedList<SchemaProblem>());
		for(int i = 0; i < elementItemElementList.size(); i++)
		{
			Element elementItemElement = elementItemElementList.get(i);
			ElementItem elementItem = globalElementsByName.get(elementItemElement.getAttribute("name"));
			if(elementItem == null)
				throw new RuntimeException("Could not locate global element to populate: " + elementItemElement);
			System.out.println("populating global element: " + elementItem.getName());
			if(!populateElementItem(elementItemElement, elementItem))
				;//throw new RuntimeException("Could not populate global element: " + elementItemElement);
		}
		problemCollector.addAll(getProblems());
		setProblems(problemCollector);
	}
	
	public String getTargetNamespace()
	{
		return targetNamespace;
	}
	
	private void addProblem(SchemaProblem problem)
	{
		problems.add(problem);
	}
	
	private void setProblems(List<SchemaProblem> problems)
	{
		this.problems = problems;
	}
	
	public List<SchemaProblem> getProblems()
	{
		return new LinkedList<SchemaProblem>(problems);
	}
	
	public SimpleType parseSimpleType(Element simpleTypeElement)
	{
		String name = simpleTypeElement.getAttribute("name");
		SimpleType simpleType = new SimpleType(this, name);
		NodeList restrictionElementList = simpleTypeElement.getElementsByTagNameNS(SCHEMA_NAMESPACE, "restriction");
		if(restrictionElementList.getLength() > 0)
		{
			Element restrictionElement = (Element)restrictionElementList.item(0);
			SimpleType baseType = null;
			String base = restrictionElement.getAttribute("base");
			System.out.println("base type: " + base);
			if(!base.equals("")) //has base type
			{
				baseType = (SimpleType)resolveType(restrictionElement, base);
			}
			else
			{
				NodeList recurseSimpleTypeElementList = restrictionElement.getElementsByTagNameNS(SCHEMA_NAMESPACE, "simpleType");
				if(recurseSimpleTypeElementList.getLength() > 0)
				{
					baseType = parseSimpleType((Element)recurseSimpleTypeElementList.item(0));
				}
			}
			if(baseType == null)
			{
				addProblem(createProblem("Unable to parse simple type '" + name + "': base type '" + base + "' not found or invalid", simpleTypeElement));
				return null;
			}
			return simpleType;
		}
		//we don't currently don't support list or union types
		addProblem(createProblem("Unsupported simple type mechanism.", simpleTypeElement));
		return null;
	}
	
	public ComplexType parseComplexType(Element complexTypeElement)
	{
		String name = complexTypeElement.getAttribute("name");
		ComplexType complexType = new ComplexType(this, name);
		if(!populateComplexType(complexType, complexTypeElement))
			return null;
		return complexType;
	}
	
	public boolean populateComplexType(ComplexType complexType, Element complexTypeElement)
	{
		List<Element> simpleContentElementList = XMLUtilities.getElementsByTagNameNS(complexTypeElement, SCHEMA_NAMESPACE, "simpleContent", true);
		if(simpleContentElementList.size() > 0)
		{
			Element simpleContentElement = simpleContentElementList.get(0);
			SimpleContentModel simpleContent = new SimpleContentModel();
			NodeList derivationElementList = simpleContentElement.getElementsByTagNameNS(SCHEMA_NAMESPACE, "restriction");
			if(derivationElementList.getLength() > 0)
			{
				Element restrictionElement = (Element)derivationElementList.item(0);
				SimpleType baseType = null;
				String base = restrictionElement.getAttribute("base");
				if(!base.equals("")) //has base type
				{
					baseType = (SimpleType)resolveType(restrictionElement, base);
				}
				if(baseType == null)
				{
					addProblem(createProblem("Base type '" + base + "' not found or invalid.", restrictionElement));
					return false;
				}
				if(!parseAttributeObjects(simpleContent, restrictionElement))
					return false;
				complexType.setContentModel(simpleContent);
			}
			else
			{
				derivationElementList = simpleContentElement.getElementsByTagNameNS(SCHEMA_NAMESPACE, "extension");
				if(derivationElementList.getLength() > 0)
				{
					Element extensionElement = (Element)derivationElementList.item(0);
					SimpleType baseType = null;
					String base = extensionElement.getAttribute("base");
					if(!base.equals("")) //has base type
					{
						baseType = (SimpleType)resolveType(extensionElement, base);
					}
					if(baseType == null)
					{
						addProblem(createProblem("Base type '" + base + "' not found or invalid.", extensionElement));
						return false;
					}
					if(!parseAttributeObjects(simpleContent, extensionElement))
						return false;
					complexType.setContentModel(simpleContent);
				}
				else
				{
					addProblem(createProblem("Type derivation method not supported.", simpleContentElement));
					return false;
				}
			}
		}
		else
		{
			List<Element> complexContentElementList = XMLUtilities.getElementsByTagNameNS(complexTypeElement, SCHEMA_NAMESPACE, "complexContent", true);
			if(complexContentElementList.size() > 0)
			{
				Element complexContentElement = complexContentElementList.get(0);
				String mixed = complexContentElement.getAttribute("mixed");
				NodeList derivationElementList = complexContentElement.getElementsByTagNameNS(SCHEMA_NAMESPACE, "restriction");
				if(derivationElementList.getLength() > 0)
				{
					Element restrictionElement = (Element)derivationElementList.item(0);
					String base = restrictionElement.getAttribute("base");
					ComplexType baseType = (ComplexType)resolveType(restrictionElement, base);
					if(baseType == null)
					{
						addProblem(createProblem("Base type '" + base + "' not found or invalid.", restrictionElement));
						return false;
					}
					RestrictedComplexContentModel restrictedModel = new RestrictedComplexContentModel(baseType);
					if(!mixed.equals(""))
						restrictedModel.setLocalMixedContent(Boolean.parseBoolean(mixed));
					NodeList particleList = restrictionElement.getChildNodes();
					for(int i = 0; i < particleList.getLength(); i++)
					{
						if(particleList.item(i).getNodeType() == Node.ELEMENT_NODE)
						{
							Element element = (Element)particleList.item(i);
							if(element.getLocalName().equals("annotation"))
								continue;
							if(element.getLocalName().equals("attributeGroup"))
								continue;
							if(element.getLocalName().equals("attribute"))
								continue;
							ElementGroup eg = parseElementGroup(element);
							if(eg == null)
								return false;
							restrictedModel.setElementGroup(eg);
						}
					}
					if(!parseAttributeObjects(restrictedModel, restrictionElement))
						return false;
					complexType.setContentModel(restrictedModel);
				}
				else
				{
					derivationElementList = complexContentElement.getElementsByTagNameNS(SCHEMA_NAMESPACE, "extension");
					if(derivationElementList.getLength() > 0)
					{
						Element extensionElement = (Element)derivationElementList.item(0);
						String base = extensionElement.getAttribute("base");
						ComplexType baseType = (ComplexType)resolveType(extensionElement, base);
						if(baseType == null)
						{
							addProblem(createProblem("Base type '" + base + "' not found or invalid.", extensionElement));
							return false;
						}
						ExtendedComplexContentModel extendedModel = new ExtendedComplexContentModel(baseType);
						if(!mixed.equals(""))
							extendedModel.setLocalMixedContent(Boolean.parseBoolean(mixed));
						NodeList particleList = extensionElement.getChildNodes();
						for(int i = 0; i < particleList.getLength(); i++)
						{
							if(particleList.item(i).getNodeType() == Node.ELEMENT_NODE)
							{
								Element element = (Element)particleList.item(i);
								if(element.getLocalName().equals("annotation"))
									continue;
								if(element.getLocalName().equals("attributeGroup"))
									continue;
								if(element.getLocalName().equals("attribute"))
									continue;
								ElementGroup eg = parseElementGroup(element);
								if(eg == null)
									return false;
								extendedModel.setElementGroup(eg);
							}
						}
						if(!parseAttributeObjects(extendedModel, extensionElement))
							return false;
						complexType.setContentModel(extendedModel);
					}
					else
					{
						addProblem(createProblem("Type derivation method not supported.", complexContentElement));
						return false;
					}
				}
			}
			else
			{
				String mixed = complexTypeElement.getAttribute("mixed");
				ComplexType baseType = (ComplexType)resolveType(SCHEMA_NAMESPACE, "anyType");
				if(baseType == null)
				{
					addProblem(createProblem("Base type 'anyType' not found or invalid.", complexTypeElement));
					return false;
				}
				RestrictedComplexContentModel restrictedModel = new RestrictedComplexContentModel(baseType);
				if(!mixed.equals(""))
					restrictedModel.setLocalMixedContent(Boolean.parseBoolean(mixed));
				NodeList particleList = complexTypeElement.getChildNodes();
				for(int i = 0; i < particleList.getLength(); i++)
				{
					if(particleList.item(i).getNodeType() == Node.ELEMENT_NODE)
					{
						Element element = (Element)particleList.item(i);
						if(element.getLocalName().equals("annotation"))
							continue;
						if(element.getLocalName().equals("attributeGroup"))
							continue;
						if(element.getLocalName().equals("attribute"))
							continue;
						ElementGroup eg = parseElementGroup(element);
						if(eg == null)
							return false;
						restrictedModel.setElementGroup(eg);
						if(!parseAttributeObjects(restrictedModel, complexTypeElement))
							return false;
						complexType.setContentModel(restrictedModel);
					}
				}
			}
		}
		return true;
	}
	
	public boolean populateElementItem(Element elementItemElement, ElementItem item)
	{
		System.out.println("populating element: " + item.getName());
		Attr typeAttr = elementItemElement.getAttributeNode("type");
		if(typeAttr != null)
		{
			Type type = resolveType(elementItemElement, typeAttr.getValue());
			if(type == null)
			{
				addProblem(createProblem("Element type '" + typeAttr.getValue() + "' not found or invalid.", elementItemElement));
				return false;
			}
			item.setType(type);
		}
		else
		{
			NodeList simpleTypeList = elementItemElement.getElementsByTagNameNS(SCHEMA_NAMESPACE, "simpleType");
			if(simpleTypeList.getLength() > 0)
			{
				SimpleType type = parseSimpleType((Element)simpleTypeList.item(0));
				if(type == null)
					return false;
				item.setType(type);
			}
			else
			{
				NodeList complexTypeList = elementItemElement.getElementsByTagNameNS(SCHEMA_NAMESPACE, "complexType");
				if(complexTypeList.getLength() > 0)
				{
					ComplexType type = parseComplexType((Element)complexTypeList.item(0));
					if(type == null)
						return false;
					item.setType(type);
				}
				else
					item.setType(resolveType(SCHEMA_NAMESPACE, "anyType"));
			}
		}
		return true;
	}
	
	public ElementGroup parseElementGroup(Element elementGroupElement)
	{
		System.out.println("parsing element group: " + elementGroupElement.getLocalName());
		ElementGroup eg = null;
		if(elementGroupElement.getLocalName().equals("sequence"))
		{
			eg = new ElementGroup(this, ElementGroup.SEQUENCE);
		}
		else if(elementGroupElement.getLocalName().equals("choice"))
		{
			eg = new ElementGroup(this, ElementGroup.CHOICE);
		}
		else if(elementGroupElement.getLocalName().equals("all"))
		{
			eg = new ElementGroup(this, ElementGroup.ALL);
		}
		else if(elementGroupElement.getLocalName().equals("any"))
		{
			eg = new ElementGroup(this, ElementGroup.ANY);
			return eg;
		}
		else if(elementGroupElement.getLocalName().equals("group"))
		{
			String ref = elementGroupElement.getAttribute("ref");
			eg = new ElementGroupReference(this);
			ElementGrouping elementGrouping = resolveElementGrouping(elementGroupElement, ref);
			if(elementGrouping == null)
			{
				addProblem(createProblem("Unable to resolve group reference: " + ref, elementGroupElement));
				return null;
			}
			((ElementGroupReference)eg).setReferencedElementGroup(elementGrouping);
		}
		else
		{
			addProblem(createProblem("Unsupported element group tag.", elementGroupElement));
			return null;
		}
		Attr minAttr = elementGroupElement.getAttributeNode("minOccurs");
		if(minAttr != null)
			eg.setMinOccurs(Integer.parseInt(minAttr.getValue()));
		Attr maxAttr = elementGroupElement.getAttributeNode("maxOccurs");
		if(maxAttr != null)
		{
			try
			{
				eg.setMaxOccurs(Integer.parseInt(maxAttr.getValue()));
			}
			catch (NumberFormatException e)
			{
				if(maxAttr.getValue().equals("unbounded"))
					eg.setMaxOccurs(CardinalItem.UNBOUNDED);
			}
		}
		NodeList children = elementGroupElement.getChildNodes();
		for(int i = 0; i < children.getLength(); i++)
		{
			if(children.item(i).getNodeType() != Node.ELEMENT_NODE)
				continue;
			Element childElement = (Element)children.item(i);
			System.out.println("processing child element of group: " + childElement.getLocalName());
			if(childElement.getLocalName().equals("annotation"))
				continue;
			if(childElement.getLocalName().equals("element"))
			{
				ElementItem item = null;
				Attr refAttr = childElement.getAttributeNode("ref");
				if(refAttr != null)
				{
					ElementItem ei = resolveElementItem(childElement, refAttr.getValue());
					if(ei == null)
					{
						addProblem(createProblem("Element reference could not be resolved.", childElement));
						return null;
					}
					ElementItemReference eir = new ElementItemReference(this);
					eir.setReferencedElementItem(ei);
					item = eir;
				}
				else
				{
					String name = childElement.getAttribute("name");
					ElementItem ei = new ElementItem(this, name);
					if(!populateElementItem(childElement, ei))
						return null;
					item = ei;
				}
				minAttr = childElement.getAttributeNode("minOccurs");
				if(minAttr != null)
					item.setMinOccurs(Integer.parseInt(minAttr.getValue()));
				maxAttr = childElement.getAttributeNode("maxOccurs");
				if(maxAttr != null)
				{
					try
					{
						item.setMaxOccurs(Integer.parseInt(maxAttr.getValue()));
					}
					catch (NumberFormatException e)
					{
						if(maxAttr.getValue().equals("unbounded"))
							item.setMaxOccurs(CardinalItem.UNBOUNDED);
					}
				}
				eg.addElementObject(item);
			}
			else
			{
				ElementGroup childGroup = parseElementGroup(childElement);
				if(childGroup == null)
					return null;
				eg.addElementObject(childGroup);
			}
		}
		return eg;
	}
	
	public boolean parseAttributeObjects(AttributeItemContainer attributeContainer, Element attributeContainerElement)
	{
		NodeList attributeObjectElementList = attributeContainerElement.getElementsByTagNameNS(SCHEMA_NAMESPACE, "*");
		for(int i = 0; i < attributeObjectElementList.getLength(); i++)
		{
			if(attributeObjectElementList.item(i).getNodeType() != Node.ELEMENT_NODE)
				continue;
			Element attributeObjectElement = (Element)attributeObjectElementList.item(i);
			if(attributeObjectElement.getLocalName().equals("attribute"))
			{
				AttributeItem attributeItem = parseAttributeItem(attributeObjectElement);
				attributeContainer.addAttribute(attributeItem);
			}
			else if(attributeObjectElement.getLocalName().equals("attributeGroup"))
			{
				String name = attributeObjectElement.getAttribute("ref");
				AttributeGrouping attributeGrouping = resolveAttributeGrouping(attributeObjectElement, name);
				if(attributeGrouping != null)
				{
					for(AttributeItem attributeItem : attributeGrouping.getAttributes())
						attributeContainer.addAttribute(attributeItem);
				}
				else
				{
					addProblem(createProblem("Attribute group could not be resolved.", attributeObjectElement));
					return false;
				}
			}
		}
		return true;
	}
	
	public AttributeItem parseAttributeItem(Element attributeItemElement)
	{
		if(!attributeItemElement.getAttribute("ref").equals(""))
			return globalAttributesByName.get(attributeItemElement.getAttribute("ref"));
		AttributeItem attributeItem = new AttributeItem(this, attributeItemElement.getAttribute("name"));
		Attr defaultAttributeNode = attributeItemElement.getAttributeNode("default");
		if(defaultAttributeNode != null)
			attributeItem.setDefaultValue(defaultAttributeNode.getValue());
		Attr fixedAttributeNode = attributeItemElement.getAttributeNode("fixed");
		if(fixedAttributeNode != null)
			attributeItem.setFixedValue(fixedAttributeNode.getValue());
		Attr formAttributeNode = attributeItemElement.getAttributeNode("form");
		if(formAttributeNode != null)
		{
			attributeItem.setQualifyOverride(true);
			attributeItem.setQualified(formAttributeNode.getValue().equals("qualified"));
		}
		attributeItem.setRequired(attributeItemElement.getAttribute("use").equals("required"));
		SimpleType type = null;
		String typeString = attributeItemElement.getAttribute("type");
		if(!typeString.equals(""))
		{
			type = (SimpleType)resolveType(attributeItemElement, typeString);
		}
		else
		{
			NodeList simpleTypeElementList = attributeItemElement.getElementsByTagNameNS(SCHEMA_NAMESPACE, "simpleType");
			if(simpleTypeElementList.getLength() > 0)
			{
				type = parseSimpleType((Element)simpleTypeElementList.item(0));
			}
		}
		if(type == null)
		{
			addProblem(createProblem("Base type '" + typeString + "' not found or invalid.", attributeItemElement));
		}
		attributeItem.setType(type);
		return attributeItem;
	}
	
	public Type resolveType(Element hostElement, String qName)
	{
		String[] baseParts = qName.split(":");
		String prefix = null;
		if(baseParts.length > 1)
			prefix = baseParts[0];
		String nameSpace = hostElement.lookupNamespaceURI(prefix);
		String partialName = baseParts[baseParts.length - 1];
		return resolveType(nameSpace, partialName);
	}
	
	public Type resolveType(String uri, String name)
	{
		if(uri == null || uri.equals(targetNamespace))
			return localTypesByName.get(name);
		else
		{
			Schema schema = schemasByNamespace.get(uri);
			if(schema == null)
				return null;
			return schema.getType(name);
		}
	}
	
	public AttributeGrouping resolveAttributeGrouping(Element hostElement, String qName)
	{
		String[] baseParts = qName.split(":");
		String prefix = null;
		if(baseParts.length > 1)
			prefix = baseParts[0];
		String nameSpace = hostElement.lookupNamespaceURI(prefix);
		String partialName = baseParts[baseParts.length - 1];
		if(nameSpace == null || nameSpace.equals(targetNamespace))
			return globalAttributeGroupsByName.get(partialName);
		else
		{
			Schema schema = schemasByNamespace.get(nameSpace);
			if(schema == null)
				return null;
			return schema.getAttributeGrouping(partialName);
		}
	}
	
	public ElementGrouping resolveElementGrouping(Element hostElement, String qName)
	{
		String[] baseParts = qName.split(":");
		String prefix = null;
		if(baseParts.length > 1)
			prefix = baseParts[0];
		String nameSpace = hostElement.lookupNamespaceURI(prefix);
		String partialName = baseParts[baseParts.length - 1];
		if(nameSpace == null || nameSpace.equals(targetNamespace))
			return globalElementGroupsByName.get(partialName);
		else
		{
			Schema schema = schemasByNamespace.get(nameSpace);
			if(schema == null)
				return null;
			return schema.getElementGrouping(partialName);
		}
	}
	
	public ElementItem resolveElementItem(Element hostElement, String qName)
	{
		String[] baseParts = qName.split(":");
		String prefix = null;
		if(baseParts.length > 1)
			prefix = baseParts[0];
		String nameSpace = hostElement.lookupNamespaceURI(prefix);
		String partialName = baseParts[baseParts.length - 1];
		return resolveElementItem(nameSpace, partialName);
	}
	
	public ElementItem resolveElementItem(String uri, String name)
	{
		if(uri == null || uri.equals(targetNamespace))
			return globalElementsByName.get(name);
		else
		{
			Schema schema = schemasByNamespace.get(uri);
			if(schema == null)
				return null;
			return schema.getElementItem(name);
		}
	}
	
	public Type getType(String localName)
	{
		return localTypesByName.get(localName);
	}
	
	public AttributeGrouping getAttributeGrouping(String localName)
	{
		return globalAttributeGroupsByName.get(localName);
	}
	
	public ElementGrouping getElementGrouping(String localName)
	{
		return globalElementGroupsByName.get(localName);
	}
	
	public ElementItem getElementItem(String localName)
	{
		return globalElementsByName.get(localName);
	}
	
	public void addSchema(Schema schema)
	{
		schemasByNamespace.put(schema.targetNamespace, schema);
	}
	
	private SchemaProblem createProblem(String message, Element source)
	{
		return createProblem(message, source, null);
	}
	
	private SchemaProblem createProblem(String message, Element source, Throwable t)
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
		return new SchemaProblem(message, lineNumber, t);
	}
	
	public boolean shouldQualifyElements()
	{
		return qualifyElements;
	}
	
	public boolean shouldQualifyAttributes()
	{
		return qualifyAttributes;
	}
	
	public static void main(String[] args)
	{
		if(args.length < 1)
		{
			System.out.println("Usage: java Schema file_path");
			System.exit(1);
		}
		File file = new File(args[0]);
		if(!file.exists())
		{
			System.out.println("Usage: java Schema file_path");
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
			Schema schema = new Schema(rootElement);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
}
