package org.eclipse.vtp.modules.webservice.ui.properties;

import org.eclipse.vtp.desktop.model.core.wsdl.soap.SoapBindingOperation;

public interface IOperationListener
{
	public void operationChanged(SoapBindingOperation operation);
}
