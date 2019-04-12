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
package org.eclipse.vtp.desktop.model.elements.core.internal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.vtp.desktop.model.core.FieldType;
import org.eclipse.vtp.desktop.model.core.FieldType.Primitive;
import org.eclipse.vtp.desktop.model.core.IBusinessObject;
import org.eclipse.vtp.desktop.model.core.IBusinessObjectField;
import org.eclipse.vtp.desktop.model.core.IBusinessObjectSet;
import org.eclipse.vtp.desktop.model.core.design.IDesignElement;
import org.eclipse.vtp.desktop.model.core.design.IDesignElementConnectionPoint;
import org.eclipse.vtp.desktop.model.core.design.Variable;
import org.eclipse.vtp.desktop.model.core.internal.BusinessObject;
import org.eclipse.vtp.desktop.model.core.internal.BusinessObjectField;
import org.eclipse.vtp.desktop.model.core.internal.VariableHelper;
import org.eclipse.vtp.desktop.model.core.internal.design.ConnectorRecord;
import org.eclipse.vtp.desktop.model.elements.core.PrimitiveInformationProvider;
import org.eclipse.vtp.framework.util.XMLWriter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ScriptedPrimitiveInformationProvider extends PrimitiveInformationProvider
{
	String typeId;
	String typeName;
	boolean acceptsConnectors = false;
	List<ConnectorRecord> connectorRecords = new ArrayList<ConnectorRecord>();
	List<Variable> variables = new ArrayList<Variable>();
	IConfigurationElement configurationElement = null;
	boolean canDelete = true;
	boolean hasConnectors = false;
	List<VarRecord> varRecords = new ArrayList<VarRecord>();
	List<VarPropertySet> varPropertySets = new ArrayList<VarPropertySet>();
	
	public ScriptedPrimitiveInformationProvider(PrimitiveElement element)
	{
		super(element);
	}
	
	public void init(IConfigurationElement configurationElement)
	{
		acceptsConnectors = Boolean.parseBoolean(configurationElement.getAttribute("acceptsConnectors"));
		if(configurationElement.getAttribute("canDelete") != null)
		{
			canDelete = Boolean.parseBoolean(configurationElement.getAttribute("canDelete"));
		}
		typeId = configurationElement.getAttribute("typeid");
		typeName = configurationElement.getAttribute("typename");
		
		IConfigurationElement exitPointsElement = configurationElement.getChildren("exit_points")[0];
		IConfigurationElement[] exitPointElements = exitPointsElement.getChildren("exit_point");
		for(int i = 0; i < exitPointElements.length; i++)
		{
			ConnectorRecord cr = new ConnectorRecord(getElement(), exitPointElements[i].getAttribute("name"), IDesignElementConnectionPoint.ConnectionPointType.valueOf(exitPointElements[i].getAttribute("type")));
			connectorRecords.add(cr);
			hasConnectors = true;
		}
		IConfigurationElement actionsElement = configurationElement.getChildren("variables")[0];
		IConfigurationElement[] actionElements = actionsElement.getChildren("variable");
		for(int i = 0; i < actionElements.length; i++)
		{
			String variableName = actionElements[i].getAttribute("name");
			String variableType = actionElements[i].getAttribute("type");
			if(variableType != null) //legacy support
			{
				boolean array = false;
				if(actionElements[i].getAttribute("array") != null &&
					"true".equalsIgnoreCase(actionElements[i].getAttribute("array"))) //support for old format
				{
					array = true;
				}
				Primitive prim = Primitive.find(variableType);
				if(prim != null)
				{
					if(array)
						varPropertySets.add(new VarPropertySet(variableName, Primitive.ARRAY.getName(), variableType));
					else
						varPropertySets.add(new VarPropertySet(variableName,variableType, null));
				}
				else
				{
					if(array)
						varPropertySets.add(new VarPropertySet(variableName, Primitive.ARRAY.getName(), variableType));
					else
						varPropertySets.add(new VarPropertySet(variableName, variableType, null));
				}
			}
			else
			{
				String ftTypeName;
				String ftBaseTypeName = null;
				int precision = 1;
				
				IConfigurationElement[] typeElements = actionElements[i].getChildren("data-type");
				if(typeElements.length > 0)
				{
					IConfigurationElement element = typeElements[0];
					String typeName = element.getAttribute("type");
					ftTypeName = typeName;
					if(typeName.startsWith("primitive:"))
					{
						typeName = typeName.substring(10);
						ftTypeName = typeName;
						Primitive type = Primitive.find(typeName);
						if(type.hasBaseType())
						{
							String baseTypeName = element.getAttribute("base-type");
							if(baseTypeName.startsWith("primitive:"))
							{
								baseTypeName = baseTypeName.substring(10);
								ftBaseTypeName = baseTypeName;
								Primitive baseType = Primitive.find(baseTypeName);
								if(baseType.hasPrecision())
								{
									try
									{
										precision = Integer.parseInt(element.getAttribute("precision"));
									}
									catch(NumberFormatException nfe)
									{}
								}
							}
							else //business object type
							{
								baseTypeName = baseTypeName.substring(7);
								ftBaseTypeName = baseTypeName;
							}
						}

						if(type.hasPrecision())
						{
							try
							{
								precision = Integer.parseInt(element.getAttribute("precision"));
							}
							catch(NumberFormatException nfe)
							{}
						}
					}
					
					varPropertySets.add(new VarPropertySet(variableName, ftTypeName, ftBaseTypeName, precision));
				}
			}
		}
	}

	public ConnectorRecord getConnectorRecord(String recordName)
	{
		for(int i = 0; i < connectorRecords.size(); i++)
		{
			ConnectorRecord cr = connectorRecords.get(i);
			if(cr.getName().equals(recordName))
				return cr;
		}
		return null;
	}

	public List<ConnectorRecord> getConnectorRecords()
	{
		return connectorRecords;
	}
	
	public List<ConnectorRecord> getConnectorRecords(IDesignElementConnectionPoint.ConnectionPointType... types)
	{
		int flags = IDesignElementConnectionPoint.ConnectionPointType.getFlagSet(types);
		if((flags & 0xFFFFFFF8) != 0) //ensure the type passed is a valid combination of the available types
			throw new IllegalArgumentException("DesignConnector type must be one of those defined in the class ConnectorRecord.");
		List<ConnectorRecord> ret = new ArrayList<ConnectorRecord>();
		for(int i = 0; i < connectorRecords.size(); i++)
		{
			ConnectorRecord cr = connectorRecords.get(i);
			if(cr.getType().isSet(flags))
				ret.add(cr);
		}
		return ret;
	}

	public void readConfiguration(Element configuration)
	{
	}

	public void writeConfiguration(Element configuration)
	{
	}

	public boolean acceptsConnector(IDesignElement origin)
	{
		return acceptsConnectors;
	}

	public String getTypeId()
	{
		return typeId;
	}

	public String getTypeName()
	{
		return typeName;
	}
	
	public boolean hasConnectors()
    {
	    return hasConnectors;
    }

	public void addVariable(String name, FieldType type)
	{
		Variable v = new Variable(name, type);
		VariableHelper.buildObjectFields(v, getElement().getDesign().getDocument().getProject().getBusinessObjectSet());
		variables.add(v);
	}
	
	public List<Variable> getOutgoingVariables(String exitPoint, boolean localOnly)
    {
		varRecords.clear();
		for(VarPropertySet vps : varPropertySets)
		{
			varRecords.add(createVarRecord(vps));
		}
		
		if(variables.size() == 0 && varRecords.size() != 0)
		{
			for(VarRecord vr : varRecords)
			{
				addVariable(vr.name, vr.type);
			}
		}
	    return variables;
    }

	public void declareBusinessObjects(IBusinessObjectSet businessObjectSet)
	{
		if(configurationElement != null)
		{
			IConfigurationElement[] bosArray = configurationElement.getChildren("business-objects");
			if(bosArray != null && bosArray.length > 0 && bosArray[0] != null)
			{
				IConfigurationElement[] boElements = bosArray[0].getChildren("business-object");
				for(int i = 0; i < boElements.length; i++)
				{
					boolean safeToCreate = false;
					String boDefName = boElements[i].getAttribute("name");
					
					Map<String, BusinessObjectFieldDefinition> fieldDefs = new HashMap<String, BusinessObjectFieldDefinition>();
					IConfigurationElement[] fieldElements = boElements[i].getChildren("field");
					for(int c = 0; c < fieldElements.length; c++)
					{
						String name = fieldElements[c].getAttribute("name"); //Was already an attribute
						FieldType ft = null;
						String type = fieldElements[c].getAttribute("type"); //Was already an attribute
						if(fieldElements[c].getAttribute("object") != null) //legacy support
						{
							boolean	array = Boolean.parseBoolean(fieldElements[c].getAttribute("array")); //Compatible because parseBoolean(null) returns false
							Primitive prim = Primitive.find(type);
							if(prim != null)
							{
								if(array)
									ft = new FieldType(Primitive.ARRAY, prim);
								else
									ft = new FieldType(prim);
							}
							else
							{
								IBusinessObject bo = businessObjectSet.getBusinessObject(type);
								if(array)
									ft = new FieldType(Primitive.ARRAY, bo);
								else
									ft = new FieldType(bo);
							}
						}
						else
						{
							Primitive prim = Primitive.find(type);
							if(prim != null)
							{
								if(prim.hasBaseType())
								{
									String baseType = fieldElements[c].getAttribute("basetype");
									Primitive basePrim = Primitive.find(baseType);
									if(basePrim != null)
									{
										ft = new FieldType(prim, basePrim);
									}
									else
									{
										ft = new FieldType(prim, businessObjectSet.getBusinessObject(baseType));
									}
								}
								else
									ft = new FieldType(prim);
							}
							else
							{
								IBusinessObject bo = businessObjectSet.getBusinessObject(type);
								ft = new FieldType(bo);
							}
						}
						int precision; //Compatible because the null value loads old default
						String precisionText = fieldElements[c].getAttribute("precision");
						if("double".equals(precisionText))
						{
							precision = FieldType.DOUBLE;
						}
						else if ("absolute".equals(precisionText))
						{
							precision = FieldType.ABSOLUTE;
						}
						else
						{
							precision = FieldType.SINGLE;
						}
						ft.setPrecision(precision);
						String initialValue = fieldElements[c].getAttribute("initial_value"); //Compatible because null value load old default
						if(initialValue == null)
						{
							initialValue = "";
						}
						boolean secured = Boolean.parseBoolean(fieldElements[c].getAttribute("secured")); //Compatible because parseBoolean(null) returns false

						fieldDefs.put(fieldElements[c].getAttribute("name"), new BusinessObjectFieldDefinition(name, ft, initialValue, secured));
					}

					List<IBusinessObject> existingBOs = businessObjectSet.getBusinessObjects();
					if(existingBOs != null && existingBOs.size() > 0)
					{
						boolean noBoNameMatches = true;
						for(IBusinessObject existingBo : existingBOs)
						{
							//Is there a name collision?
							if(existingBo.getName().equalsIgnoreCase(boDefName))
							{
								noBoNameMatches = false;
								List<IBusinessObjectField> existingBoFields = existingBo.getFields();
								boolean fieldsMatch = true;
								//Why is there a name collision?
								//Right number of fields?
								if(fieldDefs.size() == existingBoFields.size())
								{
									//Compare each of the fields
									for(IBusinessObjectField existingBof : existingBoFields)
									{
										//Does the field name match?
										if(fieldDefs.containsKey(existingBof.getName()))
										{
											BusinessObjectFieldDefinition newBof = fieldDefs.get(existingBof.getName());
											if(
													!(
															existingBof.getDataType().getPrimitiveType() == newBof.getType().getPrimitiveType() &&
															(existingBof.getDataType().getPrimitiveBaseType() == newBof.getType().getPrimitiveType()) &&
															(existingBof.getDataType().getObjectType() == newBof.getType().getObjectType()) &&
															existingBof.getDataType().getObjectBaseType() == newBof.getType().getObjectBaseType() &&
															existingBof.getInitialValue().equals(newBof.getInitialValue()) &&
															(existingBof.isSecured() == newBof.isSecured())
													)
											)
											{
												//there is an attribute of the field that doesn't match
												fieldsMatch = false;
												break;
											}
										}
										else
										{
											//there is a field name that doesn't have a match
											fieldsMatch = false;
											break;
										}
									}
								}
								else
								{
									//Wrong number of fields
									fieldsMatch = false;
								}

								if(fieldsMatch)
								{
									//same name, same definition - do nothing
								}
								else
								{
									//same name, different definition - warn the user
									//TODO warn the user of the situation
									//TODO offer the user some options along with the notification
									System.out.println("There is already a Business Object by that name. Skipping declaration of the new one."); //TODO remove this up after the user prompt is added
								}
							}
						}
						if(noBoNameMatches)
						{
							//There is no BO definition with this name, so it's safe to create this one
							safeToCreate = true;
						}
					}
					else
					{
						//There are no existing BO definitions, so it's safe to create this one
						safeToCreate = true;
					}

					if(safeToCreate)
					{
						//The name is not taken - create the definition
						BusinessObject bo;
						try
						{
							bo = (BusinessObject) businessObjectSet.createBusinessObject(boDefName);

							Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
							Element rootElement = doc.createElement("business-object");
							doc.appendChild(rootElement);
							rootElement.setAttribute("id", bo.getId());
							rootElement.setAttribute("name", bo.getName());

							Element fields = rootElement.getOwnerDocument().createElement("fields");
							rootElement.appendChild(fields);

							for(BusinessObjectFieldDefinition bofd : fieldDefs.values())
							{
								BusinessObjectField bof = new BusinessObjectField(bo, bofd.getName(), bofd.getType(), bofd.getInitialValue(), bofd.isSecured());
								Element fieldElement = fields.getOwnerDocument().createElement("field");
								fields.appendChild(fieldElement);
								fieldElement.setAttribute("name", bof.getName());
								fieldElement.setAttribute("initialValue", bof.getInitialValue());
								fieldElement.setAttribute("secured", Boolean.toString(bof.isSecured()));
								bofd.getType().write(fieldElement);
							}

							DOMSource source = new DOMSource(doc);
							ByteArrayOutputStream baos = new ByteArrayOutputStream();
							Transformer trans = TransformerFactory.newInstance().newTransformer();
							trans.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
							trans.transform(source, new XMLWriter(baos).toXMLResult());
							bo.write(new ByteArrayInputStream(baos.toByteArray()));
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
					}
				}
			}
			for(VarRecord vr : varRecords)
			{
				addVariable(vr.name, vr.type);
			}
		}
	}
	
	
	private VarRecord createVarRecord(VarPropertySet vps)
	{
		FieldType ft;

		Primitive type = Primitive.find(vps.typeName);
		
		if(type.hasBaseType())
		{
			String baseTypeName = vps.baseTypeName;
			Primitive baseType = Primitive.find(baseTypeName);
			if(baseType != null) //Should return primitive type if it's not an object; Otherwise null
			{
				ft = new FieldType(type, baseType);
				if(baseType.hasPrecision())
				{
					ft.setPrecision(vps.precision);
				}
			}
			else //business object type
			{
				IBusinessObject bo = getElement().getDesign().getDocument().getProject().getBusinessObjectSet().getBusinessObject(baseTypeName);
				if(bo == null)
					throw new RuntimeException("Missing business object definition: " + baseTypeName);
				ft = new FieldType(type, bo);
			}
		}
		else
		{
			ft = new FieldType(type);
		}
		
		if(type.hasPrecision())
		{
			ft.setPrecision(vps.precision);
		}
		
		return new VarRecord(vps.variableName, ft);
	}
	
	
	
	
	private class BusinessObjectFieldDefinition
	{
		private String name;
		private FieldType type;
		private String initialValue;
		private boolean secured;
		
		BusinessObjectFieldDefinition(String name, FieldType type, String initialValue, boolean secured)
		{
			this.name = name;
			this.type = type;
			this.initialValue = initialValue;
			this.secured = secured;
		}

		String getName()
		{
			return name;
		}

		FieldType getType()
		{
			return type;
		}

		String getInitialValue()
		{
			return initialValue;
		}

		boolean isSecured()
		{
			return secured;
		}
	}
	
	private class VarRecord
	{
		String name;
		FieldType type;
		
		public VarRecord(String name, FieldType type)
		{
			super();
			this.name = name;
			this.type = type;
		}
	}
	
	private class VarPropertySet{
		
		String variableName; //name of the variable
		String typeName; //String,Date,Number,Array,Map, ObjectName, etc
		String baseTypeName; //if typeName is Array or Map, then this parameterizes with String,Date,Number,ObjectName, etc
		int precision = 1; //Set 1 as the default
		
		public VarPropertySet(String variableName, String typeName, String baseTypeName){
			this.variableName = variableName;
			this.typeName = typeName;
			this.baseTypeName = baseTypeName;
		}
		
		public VarPropertySet(String variableName, String typeName, String baseTypeName, int precision){
			this.variableName = variableName;
			this.typeName = typeName;
			this.baseTypeName = baseTypeName;
			this.precision = precision;
		}
	}
}
