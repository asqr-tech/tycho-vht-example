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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.vtp.desktop.model.core.FieldType;
import org.eclipse.vtp.desktop.model.core.FieldType.Primitive;
import org.eclipse.vtp.desktop.model.core.design.IDesignElement;
import org.eclipse.vtp.desktop.model.core.design.IDesignElementConnectionPoint;
import org.eclipse.vtp.desktop.model.core.design.Variable;
import org.eclipse.vtp.desktop.model.core.internal.VariableHelper;
import org.eclipse.vtp.desktop.model.core.internal.design.ConnectorRecord;
import org.eclipse.vtp.desktop.model.elements.core.PrimitiveInformationProvider;
import org.eclipse.vtp.desktop.model.elements.core.internal.PrimitiveElement;
import org.eclipse.vtp.framework.util.XMLUtilities;
import org.w3c.dom.NodeList;

public class VariableAssignmentInformationProvider extends PrimitiveInformationProvider
{
	List<ConnectorRecord> connectorRecords = new ArrayList<ConnectorRecord>();
	String scriptText = "";
	private List<VariableDeclaration> variableDeclarations;
	
	public VariableAssignmentInformationProvider(PrimitiveElement element)
	{
		super(element);
		connectorRecords.add(new ConnectorRecord(element, "Continue", IDesignElementConnectionPoint.ConnectionPointType.EXIT_POINT));
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
	}

	public boolean acceptsConnector(IDesignElement origin)
	{
		return true;
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
//		ret.add(new AssignmentVariablesPropertyPanel("Variables", getElement()));
//		return ret;
//	}
	
	public String getScriptText()
	{
		return scriptText;
	}

	public void setScriptText(String text)
	{
		this.scriptText = text;
	}

	@Override
	public List<Variable> getOutgoingVariables(String exitPoint, boolean localOnly)
    {
		List<Variable> variables = new ArrayList<Variable>();
		for(int i = 0; i < variableDeclarations.size(); i++)
		{
			VariableDeclaration vd = variableDeclarations.get(i);

			Variable v = VariableHelper.constructVariable(vd.name, getElement().getDesign().getDocument().getProject().getBusinessObjectSet(), vd.getType());
			if(v != null)
			{
				v.setSecure(vd.isSecure());
				variables.add(v);
			}
		}
	    return variables;
    }

	public boolean hasConnectors()
    {
	    return true;
    }
}
