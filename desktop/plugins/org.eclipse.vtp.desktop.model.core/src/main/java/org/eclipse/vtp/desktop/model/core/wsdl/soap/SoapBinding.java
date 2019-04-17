package org.eclipse.vtp.desktop.model.core.wsdl.soap;

import org.eclipse.vtp.desktop.model.core.wsdl.Binding;


public class SoapBinding extends Binding implements SoapConstants
{
	private String style = DOCUMENT;
	private String transport = HTTP_TRANSPORT;

	public SoapBinding(String name)
	{
		super(name);
	}

	public String getStyle()
	{
		return style;
	}
	
	public void setStyle(String style)
	{
		this.style = style;
	}
	
	public String getTransport()
	{
		return transport;
	}
	
	public void setTransport(String transport)
	{
		this.transport = transport;
	}
}
