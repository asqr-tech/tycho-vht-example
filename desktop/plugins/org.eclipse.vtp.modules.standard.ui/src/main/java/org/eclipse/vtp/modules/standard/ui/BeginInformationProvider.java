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
package org.eclipse.vtp.modules.standard.ui;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.vtp.desktop.model.core.FieldType;
import org.eclipse.vtp.desktop.model.core.FieldType.Primitive;
import org.eclipse.vtp.desktop.model.core.IBusinessObject;
import org.eclipse.vtp.desktop.model.core.IBusinessObjectField;
import org.eclipse.vtp.desktop.model.core.IBusinessObjectSet;
import org.eclipse.vtp.desktop.model.core.IWorkflowEntry;
import org.eclipse.vtp.desktop.model.core.design.IDesignElement;
import org.eclipse.vtp.desktop.model.core.design.IDesignElementConnectionPoint;
import org.eclipse.vtp.desktop.model.core.design.Variable;
import org.eclipse.vtp.desktop.model.core.internal.BusinessObject;
import org.eclipse.vtp.desktop.model.core.internal.VariableHelper;
import org.eclipse.vtp.desktop.model.core.internal.design.ConnectorRecord;
import org.eclipse.vtp.desktop.model.elements.core.PrimitiveInformationProvider;
import org.eclipse.vtp.desktop.model.elements.core.internal.PrimitiveElement;
import org.eclipse.vtp.framework.util.XMLUtilities;
import org.eclipse.vtp.framework.util.XMLWriter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class BeginInformationProvider extends PrimitiveInformationProvider implements IWorkflowEntry
{
	List<ConnectorRecord> connectorRecords = new ArrayList<ConnectorRecord>();
	private List<VariableDeclaration> variableDeclarations;
	private String defaultBrandId;
	private String defaultLanguage;
	
	public BeginInformationProvider(PrimitiveElement element)
	{
		super(element);
		connectorRecords.add(new ConnectorRecord(element, "Continue", IDesignElementConnectionPoint.ConnectionPointType.EXIT_POINT));
		connectorRecords.add(new ConnectorRecord(element, "error.disconnect.hangup", IDesignElementConnectionPoint.ConnectionPointType.ERROR_POINT));
		variableDeclarations = new ArrayList<VariableDeclaration>();
	}
	
	public List<VariableDeclaration> getDeclarations()
	{
		List<VariableDeclaration> copy = new ArrayList<VariableDeclaration>();
		copy.addAll(variableDeclarations);
		return copy;
	}
	
	public void setDeclarations(List<VariableDeclaration> declarations)
	{
		this.variableDeclarations = declarations;
//TODO		getElement().fireChange();
	}
	
	

	public String getDefaultBrand()
	{
		return defaultBrandId;
	}

	public void setDefaultBrand(String defaultBrandId)
	{
		this.defaultBrandId = defaultBrandId;
	}

	public String getDefaultLanguage()
	{
		return defaultLanguage;
	}

	public void setDefaultLanguage(String defaultLanguage)
	{
		this.defaultLanguage = defaultLanguage;
	}

	public boolean acceptsConnector(IDesignElement origin)
	{
		return false;
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
		List<ConnectorRecord> ret = new ArrayList<ConnectorRecord>();
		for(int i = 0; i < connectorRecords.size(); i++)
		{
			ConnectorRecord cr = connectorRecords.get(i);
			if(cr.getType().isSet(IDesignElementConnectionPoint.ConnectionPointType.getFlagSet(types)))
				ret.add(cr);
		}
		return ret;
	}

	public void readConfiguration(org.w3c.dom.Element configuration)
	{
		defaultBrandId = configuration.getAttribute("default-brand");
		defaultLanguage = configuration.getAttribute("default-language");
		NodeList decGroupList = configuration.getElementsByTagName("declarations");
		if(decGroupList.getLength() != 1)
			return;
		org.w3c.dom.Element decGroupElement = (org.w3c.dom.Element)decGroupList.item(0);
		NodeList varList =
			decGroupElement.getElementsByTagName("variable");

		for(int v = 0; v < varList.getLength(); v++)
		{
			org.w3c.dom.Element varElement =
				(org.w3c.dom.Element)varList.item(v);
			String vname = varElement.getAttribute("name");
			FieldType type = null;
			if(varElement.getAttributeNode("type") != null) //legacy support
			{
				String vtype = varElement.getAttribute("type");
				System.err.println("Legacy Type: " + vtype);
				int vmulti =
					varElement.getAttribute("multiplicity").equals("") ? 0
																	   : Integer
					.parseInt(varElement.getAttribute("multiplicity"));
				if(vmulti == 1)
				{
					Primitive prim = Primitive.find(vtype);
					if(prim != null)
						type = new FieldType(Primitive.ARRAY, prim);
					else
						type = new FieldType(Primitive.ARRAY, getElement().getDesign().getDocument().getProject().getBusinessObjectSet().getBusinessObject(vtype));
				}
				else
				{
					Primitive prim = Primitive.find(vtype);
					if(prim != null)
						type = new FieldType(prim);
					else
						type = new FieldType(getElement().getDesign().getDocument().getProject().getBusinessObjectSet().getBusinessObject(vtype));
				}
			}
			else
			{
				List<org.w3c.dom.Element> typeElements = XMLUtilities.getElementsByTagName(varElement, "data-type", true);
				if(typeElements.size() > 0)
				{
					type = FieldType.load(getElement().getDesign().getDocument().getProject().getBusinessObjectSet(), typeElements.get(0));
				}
			}
			int vvaluetype =
				varElement.getAttribute("value-type").equals("") ? 0
																 : Integer
				.parseInt(varElement.getAttribute("value-type"));
			String vvalue = varElement.getAttribute("value");
			String secureAtt = varElement.getAttribute("secured");
			boolean secure = false;
			if(secureAtt != null && !secureAtt.equals(""))
				secure = Boolean.parseBoolean(secureAtt);
			VariableDeclaration vd =
				new VariableDeclaration(vname, type, vvaluetype, vvalue, secure);
			variableDeclarations.add(vd);
		}

	}

	public void writeConfiguration(org.w3c.dom.Element configuration)
	{
		configuration.setAttribute("default-brand", defaultBrandId);
		configuration.setAttribute("default-language", defaultLanguage);
		org.w3c.dom.Element declarationsElement =
			configuration.getOwnerDocument().createElement("declarations");
		configuration.appendChild(declarationsElement);

		for(int i = 0; i < variableDeclarations.size(); i++)
		{
			VariableDeclaration vd = variableDeclarations.get(i);
			org.w3c.dom.Element declarationElement =
				declarationsElement.getOwnerDocument().createElement("variable");
			declarationsElement.appendChild(declarationElement);
			declarationElement.setAttribute("name",
				(vd.name == null) ? "" : vd.name);
			vd.type.write(declarationElement);
			declarationElement.setAttribute("value-type",
				Integer.toString(vd.valueType));
			declarationElement.setAttribute("value",
				(vd.value == null) ? "" : vd.value);
			declarationElement.setAttribute("secured", vd.isSecure() ? "true" : "false");
		}
	}

//	public List getPropertiesPanels()
//	{
//		List ret = new ArrayList();
//		ret.add(new ApplicationStartVariablesPropertyPanel("Variables", getElement()));
//		return ret;
//	}
	
	public List<Variable> getOutgoingVariables(String exitPoint, boolean localOnly)
    {
		List<Variable> variables = new ArrayList<Variable>();
		Variable platform = VariableHelper.constructVariable("Platform", getElement().getDesign().getDocument().getProject().getBusinessObjectSet(), new FieldType(getElement().getDesign().getDocument().getProject().getBusinessObjectSet().getBusinessObject("Platform")));
		variables.add(platform);
		HashMap<String, Variable> map = new HashMap<String, Variable>();

		for(int i = 0; i < variables.size(); i++)
		{
			map.put((variables.get(i)).getName(), variables.get(i));
		}
		for(int i = 0; i < variableDeclarations.size(); i++)
		{
			VariableDeclaration vd = variableDeclarations.get(i);
			if(map.get(vd.name) == null)
			{
				Variable v = VariableHelper.constructVariable(vd.name, getElement().getDesign().getDocument().getProject().getBusinessObjectSet(), vd.getType());
				if(v != null)
				{
					v.setSecure(vd.isSecure());
					variables.add(v);
				}
			}
		}
	    return variables;
    }

	@Override
	public void declareBusinessObjects()
	{
		boolean foundPlatform = false;
		IBusinessObjectSet businessObjectSet = this.getElement().getDesign().getDocument().getProject().getBusinessObjectSet();
		List<IBusinessObject> currentObjects = businessObjectSet.getBusinessObjects();
ex:		for(IBusinessObject businessObject : currentObjects)
		{
			if(businessObject.getName().equals("Platform"))
			{
				for(IBusinessObjectField field : businessObject.getFields())
				{
					if(field.getName().equals("PLATFORM_ANI"))
					{
						foundPlatform = true;
						break ex;
					}
				}
				try
				{
					businessObjectSet.deleteBusinessObject(businessObject);
				}
				catch (CoreException e)
				{
					e.printStackTrace();
				}
				break;
			}
		}
		if(!foundPlatform)
		{
			try
			{
				BusinessObject platform = (BusinessObject)businessObjectSet.createBusinessObject("Platform");
				try
				{
					Document doc =
						DocumentBuilderFactory.newInstance().newDocumentBuilder()
											  .newDocument();
					Element rootElement = doc.createElement("business-object");
					doc.appendChild(rootElement);
					rootElement.setAttribute("id", platform.getId());
					rootElement.setAttribute("name", platform.getName());

					Element fields =
						rootElement.getOwnerDocument().createElement("fields");
					rootElement.appendChild(fields);

					Element aniElement = fields.getOwnerDocument().createElement("field");
					fields.appendChild(aniElement);
					aniElement.setAttribute("name", "ANI");
					aniElement.setAttribute("initialValue", "");
					aniElement.setAttribute("secured", "false");
					FieldType.STRING.write(aniElement);

					Element ani2Element = fields.getOwnerDocument().createElement("field");
					fields.appendChild(ani2Element);
					ani2Element.setAttribute("name", "PLATFORM_ANI");
					ani2Element.setAttribute("initialValue", "");
					ani2Element.setAttribute("secured", "false");
					FieldType.STRING.write(ani2Element);

					Element dnisElement = fields.getOwnerDocument().createElement("field");
					fields.appendChild(dnisElement);
					dnisElement.setAttribute("name", "DNIS");
					dnisElement.setAttribute("initialValue", "");
					dnisElement.setAttribute("secured", "false");
					FieldType.STRING.write(dnisElement);
					
					Element dnis2Element = fields.getOwnerDocument().createElement("field");
					fields.appendChild(dnis2Element);
					dnis2Element.setAttribute("name", "PLATFORM_DNIS");
					dnis2Element.setAttribute("initialValue", "");
					dnis2Element.setAttribute("secured", "false");
					FieldType.STRING.write(dnis2Element);
					
					Element brandElement = fields.getOwnerDocument().createElement("field");
					fields.appendChild(brandElement);
					brandElement.setAttribute("name", "Brand");
					brandElement.setAttribute("initialValue", "");
					brandElement.setAttribute("secured", "false");
					FieldType.STRING.write(brandElement);
					
					DOMSource source = new DOMSource(doc);
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					Transformer trans =
						TransformerFactory.newInstance().newTransformer();
					trans.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
					trans.transform(source, new XMLWriter(baos).toXMLResult());
					platform.write(new ByteArrayInputStream(baos.toByteArray()));
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				catch(FactoryConfigurationError e)
				{
					e.printStackTrace();
				}
				catch(TransformerFactoryConfigurationError e)
				{
					e.printStackTrace();
				}
			}
			catch (CoreException e)
			{
				e.printStackTrace();
			}
		}
	}

	public boolean canDelete()
	{
		return true;
	}
	
	public boolean hasPathToStart(Map<String, IDesignElement> path)
	{
		return true;
	}
	public boolean hasConnectors()
    {
	    return true;
    }

	public String getId()
	{
		return getElement().getId();
	}

	public List<Variable> getInputVariables()
	{
		return getOutgoingVariables(null, true);
	}

	public String getName()
	{
		return getElement().getName();
	}
}
