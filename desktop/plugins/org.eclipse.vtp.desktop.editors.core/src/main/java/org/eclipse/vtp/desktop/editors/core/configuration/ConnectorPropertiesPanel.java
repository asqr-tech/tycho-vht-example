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
package org.eclipse.vtp.desktop.editors.core.configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.vtp.desktop.model.core.design.IDesignConnector;
import org.eclipse.vtp.desktop.model.core.design.IDesignElementConnectionPoint;

public class ConnectorPropertiesPanel extends ComponentPropertiesPanel
{
	private IDesignConnector connector;
	Label instructionLabel;
	Combo hookSelection;
	TableViewer pathViewer = null;
	List<Wrapper> exits = new ArrayList<Wrapper>();
	List<ConnectorPropertiesListener> epListeners = new ArrayList<ConnectorPropertiesListener>();
	
	public ConnectorPropertiesPanel(IDesignConnector connector)
	{
		super("General");
		this.connector = connector;
	}
	
	public void createControls(Composite parent)
	{
		parent.setLayout(new FillLayout());
	
		Composite comp = new Composite(parent, SWT.NONE);
		setControl(comp);
		comp.setBackground(parent.getBackground());

		List<IDesignElementConnectionPoint> freeExits =
			connector.getOrigin().getConnectorRecords(
				IDesignElementConnectionPoint.ConnectionPointType.EXIT_POINT,
				IDesignElementConnectionPoint.ConnectionPointType.ERROR_POINT);
		List<IDesignElementConnectionPoint> currentPoints = connector.getConnectionPoints();
		List<Wrapper> curWraps = new LinkedList<Wrapper>();
		for(int i = 0; i < currentPoints.size(); i++)
		{
			Wrapper wrapper =
				new Wrapper(currentPoints.get(i));
			exits.add(wrapper);
			curWraps.add(wrapper);
		}
		
		for(IDesignElementConnectionPoint cr : freeExits)
		{
			if(cr.getDesignConnector() == null)
				exits.add(new Wrapper(cr));
		}

		if(exits.size() > 0)
		{
			comp.setLayout(new FormLayout());
			instructionLabel = new Label(comp, SWT.NONE);
			instructionLabel.setBackground(comp.getBackground());
			instructionLabel.setText(
				"Please select the event(s) that will initiate this call flow.");
		
			FormData instructionLabelData = new FormData();
			instructionLabelData.left = new FormAttachment(0, 10);
			instructionLabelData.right = new FormAttachment(100, -10);
			instructionLabelData.top = new FormAttachment(0, 10);
			instructionLabel.setLayoutData(instructionLabelData);

			Table pathTable = new Table(comp, SWT.MULTI | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
			final TableColumn pathNameColumn = new TableColumn(pathTable, SWT.NONE);
			pathNameColumn.setText("Path Name");
			pathNameColumn.setWidth(300);
			pathTable.setHeaderVisible(false);
			pathTable.addControlListener(new ControlListener()
			{
				public void controlMoved(ControlEvent arg0)
                {
                }

				public void controlResized(ControlEvent arg0)
                {
					pathNameColumn.setWidth(pathViewer.getTable().getClientArea().width);
                }
			});
			pathViewer = new TableViewer(pathTable);
			pathViewer.setContentProvider(new PathContentProvider());
			pathViewer.setLabelProvider(new PathLabelProvider());
			pathViewer.setInput(this);
			pathViewer.addSelectionChangedListener(new ISelectionChangedListener()
			{
				public void selectionChanged(SelectionChangedEvent event)
                {
					fireEndPointUpdate();
                }
			});
			
			FormData resultSelectionData = new FormData();
			resultSelectionData.left = new FormAttachment(0, 10);
			resultSelectionData.top = new FormAttachment(instructionLabel, 10);
			resultSelectionData.right = new FormAttachment(100, -10);
			resultSelectionData.bottom = new FormAttachment(100, -10);
			resultSelectionData.height = 150;
			pathTable.setLayoutData(resultSelectionData);
		
			pathViewer.setSelection(new StructuredSelection(curWraps));
			fireEndPointUpdate();
		}
		else
		{
			comp.setLayout(new GridLayout(1, false));
			Label noneAvailableLabel = new Label(comp, SWT.NONE);
			noneAvailableLabel.setText("There are no available exits from the source element");
			noneAvailableLabel.setBackground(comp.getBackground());
			noneAvailableLabel.setForeground(comp.getDisplay().getSystemColor(SWT.COLOR_RED));
			GridData gd = new GridData();
			gd.horizontalAlignment = SWT.CENTER;
			gd.verticalAlignment = SWT.CENTER;
			noneAvailableLabel.setLayoutData(gd);
			
		}
			
	}
	
	/* (non-Javadoc)
	
	 */
	public void save()
	{
		connector.clearConnectionPoints();
//		fireUpdateStatusRequest();
		if(exits.size() > 0)
		{
			IStructuredSelection selection = (IStructuredSelection)pathViewer.getSelection();
			@SuppressWarnings("unchecked")
			Iterator<Wrapper> i = selection.iterator();
			while(i.hasNext())
			{
				IDesignElementConnectionPoint record = i.next().getRecord();
				connector.addConnectionPoint(record);
			}
		}
		if(getEndPoints().size() == 0)
		{
			connector.getDesign().removeDesignConnector(connector);
		}
//		fireUpdateStatusRequest();
//		fireModifiedRequest();
//		getModelPage().getModel().fireInternalModified();
	}
	
	public void cancel()
	{
		
	}
	
	public void addEndPointListener(ConnectorPropertiesListener cepc)
	{
		this.epListeners.remove(cepc);
		this.epListeners.add(cepc);
	}
	
	public void removeEndPointListener(ConnectorPropertiesListener cepc)
	{
		this.epListeners.remove(cepc);
	}
	
	private void fireEndPointUpdate()
	{
		List<IDesignElementConnectionPoint> endPoints = getEndPoints();
		for(ConnectorPropertiesListener listener : epListeners)
		{
			listener.exitPointSelectionChanged(endPoints);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.ui.app.editor.model.UIConnector.ConnectorEndPointProvider#getEndPoints()
	 */
	public List<IDesignElementConnectionPoint> getEndPoints()
	{
		List<IDesignElementConnectionPoint> ret = new ArrayList<IDesignElementConnectionPoint>();
	
		IStructuredSelection selection = (IStructuredSelection)pathViewer.getSelection();
		@SuppressWarnings("unchecked")
		Iterator<Wrapper> i = selection.iterator();
		while(i.hasNext())
		{
			IDesignElementConnectionPoint record = i.next().getRecord();
			ret.add(record);
		}
	
		return ret;
	}
	
	public class Wrapper
	{
		private IDesignElementConnectionPoint record;
	
		public Wrapper(IDesignElementConnectionPoint record)
		{
			this.record = record;
		}
	
		public IDesignElementConnectionPoint getRecord()
		{
			return record;
		}
	
		public String toString()
		{
			String ret = record.getName();
	
			if((record.getDesignConnector() != null)
					&& (record.getDesignConnector() == connector))
			{
				ret += " (Current)";
			}
	
			return ret;
		}
	}

	public void setConfigurationContext(Map<String, Object> values)
	{
	}
	
	public class PathContentProvider implements IStructuredContentProvider
	{

		public Object[] getElements(Object inputElement)
        {
	        return exits.toArray();
        }

		public void dispose()
        {
        }

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
        {
        }
		
	}
	
	public class PathLabelProvider implements ITableLabelProvider
	{

		public Image getColumnImage(Object element, int columnIndex)
        {
	        return null;
        }

		public String getColumnText(Object element, int columnIndex)
        {
	        return element.toString();
        }

		public void addListener(ILabelProviderListener listener)
        {
        }

		public void dispose()
        {
        }

		public boolean isLabelProperty(Object element, String property)
        {
	        return true;
        }

		public void removeListener(ILabelProviderListener listener)
        {
        }
		
	}
	
	public List<String> getApplicableContexts()
	{
		return Collections.emptyList();
	}
}
