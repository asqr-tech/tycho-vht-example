package org.eclipse.vtp.modules.standard.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.vtp.desktop.model.core.design.IDesignElement;
import org.eclipse.vtp.desktop.model.core.design.IDesignElementConnectionPoint;
import org.eclipse.vtp.desktop.model.core.internal.design.ConnectorRecord;
import org.eclipse.vtp.desktop.model.elements.core.PrimitiveInformationProvider;
import org.eclipse.vtp.desktop.model.elements.core.internal.PrimitiveElement;
import org.eclipse.vtp.modules.standard.ui.properties.Branch;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class BranchInformationProvider extends PrimitiveInformationProvider
{
	List<ConnectorRecord> connectorRecords = new ArrayList<ConnectorRecord>();
	List<Branch> branches = new ArrayList<Branch>();
	
	public BranchInformationProvider(PrimitiveElement element)
	{
		super(element);
		buildInitialConnectorRecords();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.editors.core.elements.PrimitiveInformationProvider#acceptConnector(org.eclipse.vtp.desktop.model.core.Element)
	 */
	public boolean acceptsConnector(IDesignElement origin)
	{
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.editors.core.elements.PrimitiveInformationProvider#getConnectorRecord(java.lang.String)
	 */
	public ConnectorRecord getConnectorRecord(String recordName)
	{
		for(ConnectorRecord cr : connectorRecords)
		{
			if(cr.getName().equals(recordName))
				return cr;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.editors.core.elements.PrimitiveInformationProvider#getConnectorRecords()
	 */
	public List<ConnectorRecord> getConnectorRecords()
	{
		return connectorRecords;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.editors.core.elements.PrimitiveInformationProvider#getConnectorRecords(int)
	 */
	public List<ConnectorRecord> getConnectorRecords(IDesignElementConnectionPoint.ConnectionPointType... types)
	{
		List<ConnectorRecord> ret = new ArrayList<ConnectorRecord>();
		for(ConnectorRecord cr : connectorRecords)
		{
			if(cr.getType().isSet(IDesignElementConnectionPoint.ConnectionPointType.getFlagSet(types)))
				ret.add(cr);
		}
		return ret;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.editors.core.elements.PrimitiveInformationProvider#readConfiguration(Element)
	 */
	public void readConfiguration(Element configuration)
	{
		NodeList nl = configuration.getElementsByTagName("branch");
		for(int b = 0; b < nl.getLength(); b++)
		{
			String name;
			String expression;
			boolean secured;
			int number;

			Element branchElement = (Element)nl.item(b);

			name = branchElement.getAttribute("name");
			expression = branchElement.getAttribute("expression");
			secured = Boolean.parseBoolean(branchElement.getAttribute("secure"));
			number = Integer.parseInt(branchElement.getAttribute("number"));
			
			branches.add(new Branch(name, expression, secured, number));
		}
		orderBranchesByNumber();
		buildInitialConnectorRecords();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.editors.core.elements.PrimitiveInformationProvider#writeConfiguration(Element)
	 */
	public void writeConfiguration(Element configuration)
	{
		numberBranchesByOrder();
		for(Branch branch:branches)
		{
			Element branchElement = configuration.getOwnerDocument().createElement("branch");
			
			branchElement.setAttribute("name", branch.getName());
			branchElement.setAttribute("expression", branch.getExpression());
			branchElement.setAttribute("secure", Boolean.toString(branch.isSecure()));
			branchElement.setAttribute("number", Integer.toString(branch.getNumber()));
			
			configuration.appendChild(branchElement);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.editors.core.elements.PrimitiveInformationProvider#hasConnectors()
	 */
	public boolean hasConnectors()
    {
	    return true;
    }
	
	public List<Branch> getBranches()
	{
		List<Branch> branchesCopy = new ArrayList<Branch>();
		for(Branch br : branches)
		{
			branchesCopy.add(br.copy());
		}
		return branchesCopy;
	}

	public void setBranches(List<Branch> branches)
	{
		this.branches = updateConnectorRecords(branches);
	}

	private void buildInitialConnectorRecords()
	{
		connectorRecords.clear();
		connectorRecords.add(new ConnectorRecord(getElement(), "error.script", IDesignElementConnectionPoint.ConnectionPointType.ERROR_POINT));
		connectorRecords.add(new ConnectorRecord(getElement(), "Default", IDesignElementConnectionPoint.ConnectionPointType.EXIT_POINT));
		for(Branch br : branches)
		{
			ConnectorRecord cr = new ConnectorRecord(getElement(), br.getName(), IDesignElementConnectionPoint.ConnectionPointType.EXIT_POINT);
			connectorRecords.add(cr);
			br.setConnector(cr);
		}
	}
	
	private void numberBranchesByOrder()
	{
		for(int b = 0; b < branches.size(); b++)
		{
			branches.get(b).setNumber(b);
		}
	}
	
	private void orderBranchesByNumber()
	{
		Map<Integer,Branch> branchMap = new TreeMap<Integer,Branch>();
		
		for(Branch br : branches)
		{
			branchMap.put(br.getNumber(), br);
		}
		
		Iterator<Integer> i = branchMap.keySet().iterator();
		while(i.hasNext())
		{
			int b = i.next();
			branches.set(b, branchMap.get(b));
		}
	}

	private List<Branch> updateConnectorRecords(List<Branch> updatedBranches)
	{
		Map<String, Branch> updatedBranchMap = new HashMap<String,Branch>();
		Map<String, Branch> branchMap = new HashMap<String,Branch>();
		for(Branch br : branches)
		{
			branchMap.put(br.getGuid(), br);
		}
		for(Branch ubr : updatedBranches)
		{
			updatedBranchMap.put(ubr.getGuid(), ubr);
		}
		
		//look for deletions (exists in branches, but not updatedBranches); delete from connectorRecords
		for(Branch br : branches)
		{
			if(!(updatedBranchMap.containsKey(br.getGuid())))
			{
				connectorRecords.remove(br.getConnector());
			}
		}
		
		for(Branch ubr : updatedBranches)
		{
			//look for new items (no connector record); create&assign a connectorRecord
			if(!(branchMap.containsKey(ubr.getGuid())))
			{
				ConnectorRecord cr = new ConnectorRecord(getElement(), ubr.getName(), IDesignElementConnectionPoint.ConnectionPointType.EXIT_POINT);
				connectorRecords.add(cr);
				ubr.setConnector(cr);
			}
			//look for renamed items (same guid, different names); rename the connectorRecord
			else if(!(branchMap.get(ubr.getGuid()).getName().equals(ubr.getName())))
			{
				ubr.getConnector().setName(ubr.getName());
			}
			
		}
		return updatedBranches;
	}
}
