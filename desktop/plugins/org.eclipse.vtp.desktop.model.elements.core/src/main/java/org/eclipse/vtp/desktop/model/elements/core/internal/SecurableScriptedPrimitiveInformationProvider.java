package org.eclipse.vtp.desktop.model.elements.core.internal;

import org.eclipse.vtp.desktop.model.core.design.ISecurableElement;
import org.w3c.dom.Element;

public class SecurableScriptedPrimitiveInformationProvider extends
        ScriptedPrimitiveInformationProvider implements ISecurableElement
{
	boolean secured = false;

	public SecurableScriptedPrimitiveInformationProvider(PrimitiveElement element)
	{
		super(element);
	}

	public boolean isSecured()
	{
		return secured;
	}

	public void setSecured(boolean secured)
	{
		this.secured = secured;
	}

	@Override
    public void readConfiguration(Element configuration)
    {
		secured = Boolean.parseBoolean(configuration.getAttribute("secured"));
    }

	@Override
    public void writeConfiguration(Element configuration)
    {
		configuration.setAttribute("secured", Boolean.toString(secured));
    }

}
