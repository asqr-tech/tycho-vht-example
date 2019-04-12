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
import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.graphics.Point;
import org.eclipse.vtp.desktop.model.core.design.IDesignConnector;
import org.eclipse.vtp.desktop.model.core.design.IDesignConnectorLabel;
import org.eclipse.vtp.desktop.model.core.design.IDesignConnectorMidpoint;
import org.eclipse.vtp.desktop.model.core.design.IDesignElementConnectionPoint;

public class DesignConnector extends DesignComponent implements IDesignConnector
{
	private DesignElement origin;
	private DesignElement destination;
	protected List<ConnectorRecord> exitCodes = new ArrayList<ConnectorRecord>();
	private List<ConnectorListener> listeners = new ArrayList<ConnectorListener>();
	private List<GraphPoint> points = new ArrayList<GraphPoint>();
	LineLabel label = null;

	/**
	 * @param origin
	 * @param destination
	 */
	public DesignConnector(DesignElement origin, DesignElement destination)
	{
		super();
		this.origin = origin;
		this.destination = destination;
		destination.addIncomingConnector(this);
		points.add(new ConnectorEndPoint(origin));
		points.add(new ConnectorEndPoint(destination));
		this.label = new LineLabel();
	}
	
	public DesignConnector(String id, DesignElement origin, DesignElement destination)
	{
		super(id);
		this.origin = origin;
		this.destination = destination;
		destination.addIncomingConnector(this);
		points.add(new ConnectorEndPoint(origin));
		points.add(new ConnectorEndPoint(destination));
		this.label = new LineLabel();
	}

	/**
	 * @param configuration
	 */
	public void writeConfiguration(org.w3c.dom.Element configuration)
	{
		org.w3c.dom.Element connectorElement = configuration.getOwnerDocument().createElement("connector");
		configuration.appendChild(connectorElement);
		connectorElement.setAttribute("id", getId());
		connectorElement.setAttribute("origin", origin.getId());
		connectorElement.setAttribute("destination", destination.getId());
		for(ConnectorRecord cr : exitCodes)
		{
			org.w3c.dom.Element recordElement = connectorElement.getOwnerDocument().createElement("record");
			connectorElement.appendChild(recordElement);
			recordElement.setAttribute("sourcename", cr.getName());
			recordElement.setAttribute("destinationname", "");
		}
	}
	
	/**
	 * @param destination
	 */
	public void setDestination(DesignElement destination)
	{
		this.destination = destination;
		this.fireChange();
	}

	/**
	 * @param origin The origin to set.
	 */
	public void setOrigin(DesignElement origin)
	{
		this.origin = origin;
		this.fireChange();
	}

	/**
	 * @param record
	 */
	public void addConnectionPoint(IDesignElementConnectionPoint record)
	{
		if (record != null)
		{
			ConnectorRecord record2 = (ConnectorRecord)record;
			record2.setConnector(this);
			exitCodes.add(record2);
			this.fireExitAdded(record2);
		}
		this.fireChange();
	}

	/**
	 * @param record
	 */
	public void removeConnectionPoint(IDesignElementConnectionPoint record)
	{
		ConnectorRecord record2 = (ConnectorRecord)record;
		record2.setConnector(null);
		exitCodes.remove(record);
		this.fireChange();
		this.fireExitRemoved(record2);
	}

	/**
	 * @return
	 */
	public List<IDesignElementConnectionPoint> getConnectionPoints()
	{
		return new LinkedList<IDesignElementConnectionPoint>(exitCodes);
	}
	
	public void clearConnectionPoints()
	{
		List<ConnectorRecord> oldExits = exitCodes;
		exitCodes = new ArrayList<ConnectorRecord>();
		for(ConnectorRecord cr : oldExits)
		{
			cr.setConnector(null);
			this.fireExitRemoved(cr);
		}
	}

	/**
	 * @return Returns the destination.
	 */
	public DesignElement getDestination()
	{
		return destination;
	}

	/**
	 * @return Returns the origin.
	 */
	public DesignElement getOrigin()
	{
		return origin;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.Component#delete()
	 */
	protected void delete()
	{
		clearConnectionPoints();
		destination.removeIncomingConnector(this);
		super.delete();
	}

	public IDesignConnectorLabel getConnectorLabel()
	{
		return label;
	}

	public Point[] getPoints()
	{
		Point[] ps = new Point[points.size()];
		for(int i = 0; i < points.size(); i++)
		{
			if(i == (points.size() - 1))
			{
				ps[i] = points.get(i).getPoint(points.get(i - 1).getRegistryPoint());
			}
			else
			{
				ps[i] = points.get(i).getPoint(points.get(i + 1).getRegistryPoint());
			}
			if(ps[i] == null)
			{
				ps[i] = points.get(i).getRegistryPoint();
			}
		}
		return ps;
	}
	
	@SuppressWarnings("unchecked")
	public List<IDesignConnectorMidpoint> getMidpoints()
	{
		@SuppressWarnings("rawtypes")
		List midpoints = points.subList(1, points.size() - 1);
		return new LinkedList<IDesignConnectorMidpoint>(midpoints);
	}

	public IDesignConnectorMidpoint addMidpoint(int x, int y)
	{
		MidPoint midPoint = new MidPoint(x, y);
		points.add(points.size() - 1, midPoint);
		this.fireChange();
		return midPoint;
	}

	public IDesignConnectorMidpoint insertMidpoint(int index, int x, int y)
	{
		MidPoint midPoint = new MidPoint(x, y);
		points.add(index, midPoint);
		this.fireChange();
		return midPoint;
	}

	public void removeMidpoint(int index)
	{
		points.remove(index);
		this.fireChange();
	}
	
	public void removeMidpoint(IDesignConnectorMidpoint midpoint)
	{
		points.remove(midpoint);
	}
	
	public interface GraphPoint
	{
		public Point getPoint(Point originPoint);
		
		public Point getRegistryPoint();

		public void adjustPosition(int xoff, int yoff);
	}

	public class MidPoint implements GraphPoint, IDesignConnectorMidpoint
	{
		int mx = 0;
		int my = 0;

		public MidPoint(int x, int y)
		{
			super();
			this.mx = x;
			this.my = y;
		}

		public Point getPoint(Point originPoint)
		{
			return new Point(mx, my);
		}

		public Point getRegistryPoint()
		{
			return new Point(mx, my);
		}
		
		public void adjustPosition(int xoff, int yoff)
		{
			mx += xoff;
			my += yoff;
		}
		
		public Point getPosition()
		{
			return getRegistryPoint();
		}
		
		public void setPosition(int x, int y)
		{
			mx = x;
			my = y;
		}
	}
	
	public class ConnectorEndPoint implements GraphPoint
	{
		DesignElement terminus;
		
		public ConnectorEndPoint(DesignElement terminus)
		{
			super();
			this.terminus = terminus;
		}
		
		public Point getPoint(Point originPoint)
		{
			//TODO fix me: need to calculate the incursion point of the line into the element frame
			return terminus.getCenterPoint();
		}

		public Point getRegistryPoint()
		{
			return terminus.getCenterPoint();
		}
		
		public void adjustPosition(int xoff, int yoff)
		{
		}
	}

	public class LineLabel implements IDesignConnectorLabel
	{
		private int lox = 0;
		private int loy = 0;
		private int linePointIndex = 0;

		public LineLabel()
		{
			super();
		}

		public void setPosition(int segment, int lox, int loy)
		{
			Point[] points = getPoints();

			if ((segment < 0) || (segment >= (points.length - 1)))
			{
				segment = 0;
				lox = 0;
				loy = 0;
			}

			this.linePointIndex = segment;
			this.lox = lox;
			this.loy = loy;
		}
		
		public void adjustPosition(int xoff, int yoff)
		{
			lox += xoff;
			loy += yoff;
		}

		public void resetPosition()
		{
			linePointIndex = 0;
			lox = 0;
			loy = 0;
		}

		public int getAnchorSegment()
		{
			return linePointIndex;
		}
		
		public Point getOffsetPosition()
		{
			return new Point(lox, loy);
		}

	}

	/**
	 * @param listener
	 */
	public void addConnectorListener(ConnectorListener listener)
	{
		this.listeners.remove(listener);
		this.listeners.add(listener);
	}
	
	/**
	 * @param listener
	 */
	public void removeConnectorListener(ConnectorListener listener)
	{
		this.listeners.remove(listener);
	}
	
	/**
	 * @param cr
	 */
	private void fireExitAdded(ConnectorRecord cr)
	{
		for(ConnectorListener listener : listeners)
        {
	        listener.exitPointAdded(cr);
        }
	}
	
	/**
	 * @param cr
	 */
	private void fireExitRemoved(ConnectorRecord cr)
	{
		for(ConnectorListener listener : listeners)
        {
	        listener.exitPointRemoved(cr);
        }
	}
}
