package org.eclipse.vtp.modules.standard.ui.properties;

import org.eclipse.vtp.desktop.model.core.internal.design.ConnectorRecord;
import org.eclipse.vtp.framework.util.Guid;


public class Branch
{
	
	private String name = "";
	private String expression = "";
	private boolean secure = false;
	private int number = -1;
	private String guid = "";
	private ConnectorRecord connector = null;
	
	public Branch(String name, String expression, boolean secure, int number)
	{
		this.name = name;
		this.expression = expression;
		this.secure = secure;
		this.number = number;
		this.guid = Guid.createGUID();
	}
	
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getExpression()
	{
		return expression;
	}

	public void setExpression(String expression)
	{
		this.expression = expression;
	}

	public boolean isSecure()
	{
		return secure;
	}

	public void setSecure(boolean secure)
	{
		this.secure = secure;
	}

	public int getNumber()
	{
		return number;
	}

	public void setNumber(int number)
	{
		this.number = number;
	}
	
	public void setGuid(String guid)
	{
		this.guid = guid;
	}

	public String getGuid()
	{
		return guid;
	}

	public void setConnector(ConnectorRecord connector)
	{
		this.connector = connector;
	}

	public ConnectorRecord getConnector()
	{
		return connector;
	}

	public Branch copy()
	{
		Branch copy = new Branch(name, expression, secure, number);
		copy.setGuid(guid);
		copy.setConnector(connector);
		return copy;
	}

	public void printBranchContents()
	{
		System.out.println("*****BEGIN BRANCH PRINTOUT*****");
		System.out.println("name is: " + name);
		System.out.println("expression is: " + expression);
		System.out.println("secure is: " + Boolean.toString(secure));
		System.out.println("number is: " + number);
		System.out.println("******END BRANCH PRINTOUT******");

	}
}

