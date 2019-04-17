package org.eclipse.vtp.modules.standard.ui;

import java.util.Collections;
import java.util.List;

import org.eclipse.vtp.desktop.model.core.design.IDesignElement;
import org.eclipse.vtp.desktop.model.core.design.IDesignElementConnectionPoint;
import org.eclipse.vtp.desktop.model.core.design.IDesignElementConnectionPoint.ConnectionPointType;
import org.eclipse.vtp.desktop.model.core.internal.design.ConnectorRecord;
import org.eclipse.vtp.desktop.model.elements.core.IDialogExit;
import org.eclipse.vtp.desktop.model.elements.core.PrimitiveInformationProvider;
import org.eclipse.vtp.desktop.model.elements.core.internal.PrimitiveElement;

public class ExitInformationProvider extends PrimitiveInformationProvider implements IDialogExit
{
	String exitType = "Normal";
	
	public ExitInformationProvider(PrimitiveElement element)
	{
		super(element);
	}

	public boolean acceptsConnector(IDesignElement origin)
	{
		return true;
	}

	public ConnectorRecord getConnectorRecord(String recordName)
	{
		return null;
	}

	public List<ConnectorRecord> getConnectorRecords()
	{
		return Collections.emptyList();
	}

	public List<ConnectorRecord> getConnectorRecords(IDesignElementConnectionPoint.ConnectionPointType... types)
	{
		return Collections.emptyList();
	}

	public void readConfiguration(org.w3c.dom.Element configuration)
	{
		exitType = configuration.getAttribute("exit-type");
		if(!exitType.equals("Normal") && !exitType.equals("Error"))
			exitType = "Normal";
	}

	public void writeConfiguration(org.w3c.dom.Element configuration)
	{
		configuration.setAttribute("exit-type", exitType);
	}

//	public List getPropertiesPanels()
//	{
//		List ret = new ArrayList();
//		ret.add(new ExitGeneralPropertiesPanel(getElement()));
//		return ret;
//	}
	
	public String getExitType()
	{
		return exitType;
	}

	public void setExitType(String text)
	{
		String oldType = exitType;
		this.exitType = text;
		getElement().firePropertyChange(PROP_EXIT_TYPE, oldType, text);
	}

	public boolean hasConnectors()
    {
	    return false;
    }

	public ConnectionPointType getType()
	{
		return exitType.equals("Error") ? IDesignElementConnectionPoint.ConnectionPointType.ERROR_POINT : IDesignElementConnectionPoint.ConnectionPointType.EXIT_POINT;
	}
}