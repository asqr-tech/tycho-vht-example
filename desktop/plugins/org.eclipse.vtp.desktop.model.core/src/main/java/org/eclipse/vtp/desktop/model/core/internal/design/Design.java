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
package org.eclipse.vtp.desktop.model.core.internal.design;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.vtp.desktop.model.core.IDesignDocument;
import org.eclipse.vtp.desktop.model.core.design.IDesign;
import org.eclipse.vtp.desktop.model.core.design.IDesignComponent;
import org.eclipse.vtp.desktop.model.core.design.IDesignComponentListener;
import org.eclipse.vtp.desktop.model.core.design.IDesignConnector;
import org.eclipse.vtp.desktop.model.core.design.IDesignConstants;
import org.eclipse.vtp.desktop.model.core.design.IDesignElement;
import org.eclipse.vtp.desktop.model.core.design.IDesignElementConnectionPoint;
import org.eclipse.vtp.desktop.model.core.design.IDesignEntryPoint;
import org.eclipse.vtp.desktop.model.core.design.IDesignExitPoint;
import org.eclipse.vtp.desktop.model.core.design.IExitBroadcastReceiver;
import org.eclipse.vtp.desktop.model.core.design.ModelListener;
import org.eclipse.vtp.desktop.model.core.design.PaperSize;
import org.eclipse.vtp.desktop.model.core.design.Variable;
import org.eclipse.vtp.desktop.model.core.internal.WorkflowTraversalHelper;
import org.eclipse.vtp.framework.util.Guid;

public class Design implements IDesign, IDesignComponentListener
{
	public static final String XML_VERSION = "1.0.0";
	private IDesignDocument document = null;
	private String id = null;
	private String name = null;
	private int orientation = IDesignConstants.LANDSCAPE;
	private PaperSize paperSize = null;
	private List<DesignElement> elements = new ArrayList<DesignElement>();
	private List<DesignConnector> connectors = new ArrayList<DesignConnector>();
	private List<ModelListener> listeners = new LinkedList<ModelListener>();
	
	public Design(String name)
	{
		super();
		this.id = Guid.createGUID();
		this.name = name;
	}

	public Design(String id, String name)
	{
		super();
		this.id = id;
		this.name = name;
	}
	
	public String getDesignId()
	{
		return id;
	}
	
	public String getName()
	{
		return name;
	}
	
	public PaperSize getPaperSize()
	{
		return paperSize;
	}

	public int getOrientation()
	{
		return this.orientation;
	}
	
	public void setDesignId(String id)
	{
		this.id = id;
	}
	
	public void setName(String name)
	{
		this.name = name;
		fireNameChanged();
	}
	
	public void setOrientation(int orientation)
	{
		if(orientation != IDesignConstants.PORTRAIT && orientation != IDesignConstants.LANDSCAPE)
			throw new IllegalArgumentException("Canvas orientation must be one of IDesignConstants.PORTRAIT | IDesignConstants.LANDSCAPE");
		this.orientation = orientation;
		fireOrientationChanged();
	}
	
	public void setPaperSize(PaperSize paperSize)
	{
		this.paperSize = paperSize;
		firePaperSizeChanged();
	}

	public int getWidth()
	{
		return orientation == IDesignConstants.PORTRAIT ? this.paperSize.getPortraitPixelWidth() : this.paperSize.getLandscapePixelWidth();
	}

	public int getHeight()
	{
		return orientation == IDesignConstants.PORTRAIT ? this.paperSize.getPortraitPixelHeight() : this.paperSize.getLandscapePixelHeight();
	}
	
	public IDesignDocument getDocument()
	{
		return document;
	}
	
	public void setDocument(IDesignDocument document)
	{
		this.document = document;
	}
	
	/**
	 * @return
	 */
	public List<IDesignElement> getDesignElements()
	{
		List<IDesignElement> ret = new LinkedList<IDesignElement>();
		ret.addAll(elements);
		return ret;
	}
	
	/**
	 * @return
	 */
	public List<IDesignConnector> getDesignConnectors()
	{
		List<IDesignConnector> ret = new LinkedList<IDesignConnector>();
		ret.addAll(connectors);
		return ret;
	}
	
	/**
	 * @param id
	 * @return
	 */
	public IDesignElement getDesignElement(String id)
	{
		for(int i = 0; i < elements.size(); i++)
		{
			DesignElement e = elements.get(i);
			if(e.getId().equals(id))
				return e;
		}
		return null;
	}
	
	public void forceNewIds()
	{
		this.id = Guid.createGUID();
		for(DesignElement element : elements)
		{
			element.setId(Guid.createGUID());
		}
		for(DesignConnector connector : connectors)
		{
			connector.setId(Guid.createGUID());
		}
	}
	
	public void validateModelStatus()
	{
		for(IDesignElement element : elements)
        {
	        ((DesignElement)element).validateStatus();
        }
	}
	
	/**
	 * @param id
	 * @return
	 */
	public IDesignConnector getDesignConnector(String id)
	{
		for(int i = 0; i < connectors.size(); i++)
		{
			DesignConnector c = connectors.get(i);
			if(c.getId().equals(id))
				return c;
		}
		return null;
	}
	
	/**
	 * @param element
	 */
	public void removeDesignElement(IDesignElement element)
	{
		List<IDesignElementConnectionPoint> conrecs = element.getConnectorRecords();
		for(IDesignElementConnectionPoint cr : conrecs)
		{
			if(cr.getDesignConnector() != null)
			{
				deleteComponent(cr.getDesignConnector());
			}
		}
		List<IDesignConnector> inc = element.getIncomingConnectors();
		for(IDesignConnector con : inc)
		{
			deleteComponent(con);
		}
		deleteComponent(element);
	}
	
	/**
	 * @param connector
	 */
	public void removeDesignConnector(IDesignConnector connector)
	{
		deleteComponent(connector);
	}
	
	/**
	 * @param component
	 */
	private void deleteComponent(IDesignComponent component)
	{
		((DesignComponent)component).delete();
	}
	
	/**
	 * @param element
	 */
	public void addDesignElement(IDesignElement element)
	{
		addDesignElement(element, true);
	}
	
	public void addDesignElement(IDesignElement element, boolean resolve)
	{
		DesignElement de = (DesignElement)element;
		elements.remove(de);
		elements.add(de);
		de.setDesign(this);
		de.addListener(this);
		if(resolve)
			de.resolve();
		fireComponentAdded(de);
	}
	
	/**
	 * @param connector
	 */
	public void addDesignConnector(DesignConnector connector)
	{
		connectors.remove(connector);
		connectors.add(connector);
		connector.setDesign(this);
		connector.addListener(this);
		fireComponentAdded(connector);
	}
	
	public IDesignConnector createDesignConnector(IDesignElement originElement, IDesignElement destinationElement)
	{
		DesignConnector dc = new DesignConnector((DesignElement)originElement, (DesignElement)destinationElement);
		if(originElement == destinationElement)
		{
			dc.addMidpoint(originElement.getCenterPoint().x + 80, originElement.getCenterPoint().y - 40);
			dc.addMidpoint(originElement.getCenterPoint().x + 80, originElement.getCenterPoint().y + 40);
			dc.getConnectorLabel().setPosition(1, 0, 0);
		}
		connectors.add(dc);
		dc.setDesign(this);
		dc.addListener(this);
		fireComponentAdded(dc);
		return dc;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.ComponentListener#componentChanged(org.eclipse.vtp.desktop.model.core.Component)
	 */
	public void componentChanged(IDesignComponent component)
	{
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.ComponentListener#componentDeleted(org.eclipse.vtp.desktop.model.core.Component)
	 */
	public void componentDeleted(IDesignComponent component)
	{
		if(component instanceof DesignConnector)
			connectors.remove(component);
		else
			elements.remove(component);
		fireComponentRemoved((DesignComponent)component);
	}
	
	/**
	 * @param listener
	 */
	public void addListener(ModelListener listener)
	{
		listeners.remove(listener);
		listeners.add(listener);
	}
	
	/**
	 * @param listener
	 */
	public void removeListener(ModelListener listener)
	{
		listeners.remove(listener);
	}
	
	/**
	 * @param component
	 */
	private void fireComponentAdded(DesignComponent component)
	{
		for(ModelListener listener : listeners)
        {
	        listener.componentAdded(this, component);
        }
	}
	
	/**
	 * @param component
	 */
	private void fireComponentRemoved(DesignComponent component)
	{
		for(ModelListener listener : listeners)
        {
	        listener.componentRemoved(this, component);
        }
	}

	private void fireOrientationChanged()
	{
		for(ModelListener listener : listeners)
		{
			listener.orientationChanged(this);
		}
	}
	
	private void firePaperSizeChanged()
	{
		for(ModelListener listener : listeners)
		{
			listener.paperSizeChanged(this);
		}
	}
	
	private void fireNameChanged()
	{
		for(ModelListener listener : listeners)
		{
			listener.nameChanged(this);
		}
	}
	
	public List<Variable> getVariablesFor(IDesignElement designElement)
	{
		return getVariablesFor(designElement, false);
	}
	
	public List<Variable> getVariablesFor(IDesignElement designElement, boolean localOnly)
	{
		Map<String, DesignElement> visited = new HashMap<String, DesignElement>();
		String key = designElement.getDesign().getDesignId() + designElement.getId();
		visited.put(key, (DesignElement)designElement);
		Map<String, Variable> variables = new HashMap<String, Variable>();
		getVariablesFor0((DesignElement)designElement, visited, variables, localOnly);
		if(!localOnly)
		{
			System.err.println("not local only");
			List<IDesignDocument> workingCopies = new ArrayList<IDesignDocument>();
			workingCopies.add(getDocument());
			WorkflowTraversalHelper wth = new WorkflowTraversalHelper(getDocument().getProject(), workingCopies);
			List<IDesignEntryPoint> entryPoints = DesignTraversalHelper.getUpStreamDesignElements(designElement, IDesignEntryPoint.class);
			for(IDesignEntryPoint entryPoint : entryPoints)
			{
				System.out.println("found entry point: " + entryPoint.getId() + " " + entryPoint.getName());
			}
			List<IDesignExitPoint> exitPoints = wth.getUpStreamExitPoints(entryPoints);
			for(IDesignExitPoint exitPoint : exitPoints)
			{
				System.out.println("processing up stream exit points: " + exitPoint.getId() + " " + exitPoint.getTargetId() + " " + exitPoint.getTargetName());
				List<Variable> exitVars = exitPoint.getExportedDesignVariables();
				for(Variable v : exitVars)
				{
					variables.put(v.getName(), v);
				}
			}
		}
		return new LinkedList<Variable>(variables.values());
	}
	
	private void getVariablesFor0(DesignElement designElement, Map<String, DesignElement> visited, Map<String, Variable> variables, boolean localOnly)
	{
		System.err.println("getting variables for: " + designElement.getName() + "[" + designElement.getId() + "]");
		List<IDesignConnector> incomingConnectors = designElement.getIncomingConnectors();
		for(IDesignConnector connector : incomingConnectors)
		{
			DesignElement sourceElement = (DesignElement)connector.getOrigin();
			String key = sourceElement.getDesign().getDesignId() + sourceElement.getId();
			if(visited.get(key) == null) //has not been visited
			{
				visited.put(key, sourceElement);
				getVariablesFor0(sourceElement, visited, variables, localOnly);
			}
			List<IDesignElementConnectionPoint> connectionPoints = connector.getConnectionPoints();
			for(IDesignElementConnectionPoint connectionPoint : connectionPoints)
			{
				List<Variable> exportedVariables = sourceElement.getOutgoingVariables(connectionPoint.getName(), localOnly);
				for(Variable var : exportedVariables)
				{
					//since this is a depth first traversal, just overwrite what's there already
					variables.put(var.getName(), var);
				}
			}
		}
		List<IExitBroadcastReceiver> receivers = designElement.getExitBroadcastReceivers();
		if(!receivers.isEmpty())
		{
			System.err.println("has receivers");
			for(IDesignElement de : designElement.getDesign().getDesignElements())
			{
				if(de == designElement)
					continue;
				for(IDesignElementConnectionPoint point : de.getConnectorRecords())
				{
					if(point.getDesignConnector() == null)
					{
						System.err.println("empty connector: " + point.getName());
						for(IExitBroadcastReceiver receiver : receivers)
						{
							if(point.getName().equals(receiver.getExitPattern()))
							{
								System.err.println("found matching receiver");
								System.err.println("design element: " + de.getName() + " [" + de.getId() + "]");
								String key = de.getDesign().getDesignId() + de.getId();
								if(visited.get(key) == null) //has not been visited
								{
									visited.put(key, (DesignElement)de);
									getVariablesFor0((DesignElement)de, visited, variables, localOnly);
								}
								List<Variable> exportedVariables = de.getOutgoingVariables(point.getName(), localOnly);
								for(Variable var : exportedVariables)
								{
									//since this is a depth first traversal, just overwrite what's there already
									variables.put(var.getName(), var);
								}
							}
						}
					}
				}
			}
		}
	}
	
	public List<Variable> getVariablesFrom(IDesignElement designElement, String exit)
	{
		return getVariablesFrom(designElement, exit, false);
	}
	
	public List<Variable> getVariablesFrom(IDesignElement designElement, String exit, boolean localOnly)
	{
		List<Variable> variables = getVariablesFor(designElement, localOnly);
		List<Variable> elementVariables = designElement.getOutgoingVariables(exit);
		for(Variable v : elementVariables)
		{
			for(Variable v1 : variables)
			{
				if(v1.getName().equals(v.getName()))
					break;
			}
			variables.add(v);
		}
		return variables;
	}
}
