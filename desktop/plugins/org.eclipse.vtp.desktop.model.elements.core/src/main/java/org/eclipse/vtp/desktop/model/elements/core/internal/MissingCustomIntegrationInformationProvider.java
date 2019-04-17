package org.eclipse.vtp.desktop.model.elements.core.internal;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;

import org.eclipse.vtp.desktop.model.core.design.IDesignElement;
import org.eclipse.vtp.desktop.model.core.design.IDesignElementConnectionPoint;
import org.eclipse.vtp.desktop.model.core.internal.design.ConnectorRecord;
import org.eclipse.vtp.desktop.model.elements.core.PrimitiveInformationProvider;

public class MissingCustomIntegrationInformationProvider extends
        PrimitiveInformationProvider
{
	private org.w3c.dom.Element configuration = null;
	private List<ConnectorRecord> connectorRecords = new LinkedList<ConnectorRecord>();

	public MissingCustomIntegrationInformationProvider(
	        PrimitiveElement element)
	{
		super(element);
	}

	@Override
	public boolean acceptsConnector(IDesignElement origin)
	{
		return true;
	}

	@Override
	public ConnectorRecord getConnectorRecord(String recordName)
	{
		ConnectorRecord cr = null;
		for(int i = 0; i < connectorRecords.size(); i++)
		{
			ConnectorRecord temp = connectorRecords.get(i);
			if(temp.getName().equals(recordName))
			{
				cr = temp;
				break;
			}
		}
		if(cr == null)
		{
			cr = new ConnectorRecord(getElement(), recordName, IDesignElementConnectionPoint.ConnectionPointType.EXIT_POINT);
			connectorRecords.add(cr);
		}
		return cr;
	}

	@Override
	public List<ConnectorRecord> getConnectorRecords()
	{
		return connectorRecords;
	}

	@Override
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

	@Override
	public boolean hasConnectors()
	{
		return true;
	}

	@Override
	public void readConfiguration(org.w3c.dom.Element configuration)
	{
		this.configuration = configuration;
	}

	@Override
	public void writeConfiguration(org.w3c.dom.Element configuration)
	{
		try
		{
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.transform(new DOMSource(this.configuration), new DOMResult(configuration));
		}
		catch (TransformerException e)
		{
			e.printStackTrace();
		}
	}

}