package org.eclipse.vtp.desktop.model.core.wsdl;

import java.util.ArrayList;
import java.util.List;

public class Service
{
	private String name = null;
	private List<Port> ports = new ArrayList<Port>();

	public Service(String name)
	{
		super();
		this.name = name;
	}

	public String getName()
	{
		return name;
	}
	
	public List<Port> getPorts()
	{
		return ports;
	}
	
	public void addPort(Port port)
	{
		ports.add(port);
	}
	
	public Port getPort(String portName)
	{
		for(Port port : ports)
		{
			if(port.getName().equals(portName))
				return port;
		}
		return null;
	}
}
