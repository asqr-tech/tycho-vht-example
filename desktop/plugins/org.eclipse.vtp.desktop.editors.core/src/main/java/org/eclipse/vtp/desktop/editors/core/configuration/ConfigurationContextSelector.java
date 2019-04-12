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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.vtp.desktop.model.core.IWorkflowProject;
import org.eclipse.vtp.desktop.model.core.configuration.ConfigurationContext;
import org.eclipse.vtp.desktop.model.core.configuration.ConfigurationContextRegistry;

public class ConfigurationContextSelector
{
	private List<ConfigurationContextHolder> holders = new LinkedList<ConfigurationContextHolder>();
	private List<ConfigurationContextSelectorListener> listeners = new LinkedList<ConfigurationContextSelectorListener>();

	/**
	 * @param brandManager
	 */
	public ConfigurationContextSelector(IWorkflowProject workflowProject)
	{
		super();
		List<ConfigurationContext> contexts = ConfigurationContextRegistry.getInstance().getConfigurationContextsFor(workflowProject);
		for(ConfigurationContext context : contexts)
		{
			ConfigurationContextHolder holder = new ConfigurationContextHolder(context);
			holders.add(holder);
		}
	}
	
	public void setContextFilter(List<String> interestingContexts)
	{
		for(ConfigurationContextHolder holder : holders)
		{
			if(interestingContexts.contains(holder.context.getId()))
				holder.activate();
			else
				holder.deactivate();
			
		}
	}

	/**
	 * @param parent
	 * @return
	 */
	public Control createControls(Composite parent)
	{
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setBackground(parent.getBackground());
		int columns = Math.min(holders.size() * 2, 6);
		comp.setLayout(new GridLayout(columns, false));

		for(ConfigurationContextHolder holder : holders)
		{
			holder.createControl(comp);
		}
		comp.layout(true, true);
		fireSelectionChanged(null);
		return comp;
	}

	/**
	 * @param listener
	 */
	public void addListener(ConfigurationContextSelectorListener listener)
	{
		listeners.remove(listener);
		listeners.add(listener);
		Map<String, Object> contextValues = new HashMap<String, Object>();
		for(ConfigurationContextHolder h : holders)
		{
			contextValues.put(h.context.getId(), h.getSelection());
		}
		listener.contextSelectionChanged(contextValues);
	}
	
	/**
	 * @param listener
	 */
	public void removeListener(ConfigurationContextSelectorListener listener)
	{
		listeners.remove(listener);
	}
		
	private void fireSelectionChanged(ConfigurationContextHolder holder)
	{
		Map<String, Object> contextValues = new HashMap<String, Object>();
		boolean updated = false;
		int cycle = 0;
		do
		{
			cycle++;
			updated = false;
			for(ConfigurationContextHolder h : holders)
			{
				contextValues.put(h.context.getId(), h.getSelection());
			}
			for(ConfigurationContextHolder h : holders)
			{
				boolean contextUpdated = h.context.setConfigurationContext(contextValues);
				h.refreshContext();
				updated = updated || contextUpdated;
			}
		}
		while (updated && cycle < 1000); //capping the iterations at 1000 in case of infinite loops
		for(ConfigurationContextSelectorListener l : listeners)
		{
			l.contextSelectionChanged(contextValues);
		}
	}
	
	public class ConfigurationContextHolder extends BaseLabelProvider implements IStructuredContentProvider, ILabelProvider
	{
		ConfigurationContext context = null;
		ComboViewer viewer = null;
		boolean active = true;
		
		ConfigurationContextHolder(ConfigurationContext context)
		{
			this.context = context;
		}
		
		void createControl(Composite parent)
		{
			Label label = new Label(parent, SWT.NONE);
			label.setText(context.getName());
			label.setBackground(parent.getBackground());
			label.setLayoutData(new GridData());
			
			Combo combo = new Combo(parent, SWT.SINGLE | SWT.READ_ONLY | SWT.DROP_DOWN);
			viewer = new ComboViewer(combo);
			viewer.getControl().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			viewer.setContentProvider(this);
			viewer.setLabelProvider(this);
			viewer.setInput(this);
			Object[] elements = getElements(this);
			if(elements.length > 0)
				viewer.setSelection(new StructuredSelection(elements[0]));
			viewer.addSelectionChangedListener(new ISelectionChangedListener()
			{
				public void selectionChanged(SelectionChangedEvent event)
				{
					if(!event.getSelection().isEmpty())
					{
						fireSelectionChanged(ConfigurationContextHolder.this);
					}
				}
			});
		}
		
		public void refreshContext()
		{
			viewer.refresh();
			if(viewer.getSelection().isEmpty())
			{
				Object[] elements = getElements(this);
				if(elements.length > 0)
					viewer.setSelection(new StructuredSelection(elements[0]));
			}
		}
		
		public Object getSelection()
		{
			return viewer == null ? null : ((IStructuredSelection)viewer.getSelection()).getFirstElement();
		}
		
		public void activate()
		{
			active = true;
			if(viewer != null)
			{
				viewer.getControl().setEnabled(true);
			}
		}

		public void deactivate()
		{
			active = false;
			if(viewer != null)
			{
				viewer.getControl().setEnabled(false);
			}
		}

		public Object[] getElements(Object inputElement)
		{
			return context.getValues().toArray();
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
		{
		}

		public Image getImage(Object element)
		{
			return null;
		}

		public String getText(Object element)
		{
			return context.getLabel(element);
		}
	}
}
