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
package org.eclipse.vtp.desktop.views.pallet;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.events.IExpansionListener;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.vtp.desktop.core.Activator;
import org.eclipse.vtp.desktop.model.core.design.IDesign;

public class SimplePallet implements Pallet
{
	/** A comparator that sorts item providers. */
	private static final Comparator<PalletItemProvider> PALLATE_ITEM_PROVIDER_SORT = new Comparator<PalletItemProvider>()
	{
		public int compare(PalletItemProvider left, PalletItemProvider right)
		{
			PalletItemProvider leftProvider = left;
			PalletItemProvider rightProvider = right;
			int diff = leftProvider.getRanking() - rightProvider.getRanking();
			if (diff == 0)
				diff = leftProvider.getName().compareTo(rightProvider.getName());
			return diff;
		}
	};
	/** A comparator that sorts items. */
	private static final ViewerSorter PALLATE_ITEM_SORT = new ViewerSorter()
	{
		public int compare(Viewer viewer, Object left, Object right)
		{
			PalletItem leftItem = (PalletItem)left;
			PalletItem rightItem = (PalletItem)right;
			return leftItem.getName().compareTo(rightItem.getName());
		}
	};

	/** The container this pallet is mapped to. */
	private IDesign container = null;
	/** The UI sections indexed by the provider they observe. */
	private Map<PalletItemProvider, ProviderSection> sectionsByProvider = Collections.emptyMap();
	/** The form toolkit to use. */
	private FormToolkit toolkit = null;
	/** The control used for this pallet. */
	private ScrolledForm form = null;

	/**
	 * Creates a new SimplePallet.
	 */
	public SimplePallet()
	{
	}

	/**
	 * Sets the container this pallet is mapped to.
	 * 
	 * @param container The container this pallet is mapped to.
	 */
	public void setContainer(IDesign container)
	{
		this.container = container;
	}

	/**
	 * Creates the control used for this pallet.
	 * 
	 * @param parent The parent element that contains this pallet.
	 */
	public void createControl(Composite parent)
	{
		Map<PalletItemProvider, ProviderSection> sectionsByProvider = new TreeMap<PalletItemProvider, ProviderSection>(PALLATE_ITEM_PROVIDER_SORT);
		for (PalletItemProvider provider : PallateProviderManager.getPallateProviders())
		{
			int numItems = 0;
			List<PalletItem> items = provider.getPalletItems();
			for (PalletItem item : items)
			{
				if (item.canBeContainedBy(container))
					numItems++;
			}
			if(numItems > 0)
				sectionsByProvider.put(provider, new ProviderSection(provider));
		}
		this.sectionsByProvider = Collections.unmodifiableMap(sectionsByProvider);
		this.toolkit = new FormToolkit(parent.getDisplay());
		this.form = toolkit.createScrolledForm(parent);
		Composite formBody = form.getBody();
		formBody.setLayout(new GridLayout(1, false));
		int height = 0;
		int width = 0;
		for(ProviderSection ps : this.sectionsByProvider.values())
		{
			Control cps = ps.initialize();
			cps.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
			Point point = cps.computeSize(SWT.DEFAULT, SWT.DEFAULT);
			height = height + point.y;
			if(point.x > width)
				width = point.x;
		}
		this.form.setSize(width, height);
		this.form.layout(true, true);
	}

	/**
	 * Returns the control used for this pallet.
	 * 
	 * @return The control used for this pallet.
	 */
	public Control getControl()
	{
		return form;
	}

	/**
	 * Disposes this pallet.
	 */
	public void destroy()
	{
		for(ProviderSection ps : this.sectionsByProvider.values())
			ps.destroy();
		this.form.dispose();
		this.form = null;
		this.toolkit.dispose();
		this.toolkit = null;
		this.sectionsByProvider = Collections.emptyMap();
	}

	/**
	 * The UI control generated for a single provider.
	 * 
	 * @author Lonnie Pryor
	 */
	private final class ProviderSection extends LabelProvider implements
			IStructuredContentProvider, ITableLabelProvider, IMenuListener,
			DragSourceListener, IExpansionListener, PalletItemObserver
	{
		/** The provider in question. */
		final PalletItemProvider provider;
		/** The UI section. */
		Section section = null;
		/** The table viewer. */
		TableViewer viewer = null;

		/**
		 * Creates a new ProviderSection.
		 * 
		 * @param provider The provider in question.
		 */
		ProviderSection(PalletItemProvider provider)
		{
			this.provider = provider;
		}

		/**
		 * Initializes this section.
		 * 
		 * @return The control created for this section.
		 */
		Control initialize()
		{
			section = toolkit.createSection(form.getBody(),
					Section.TITLE_BAR);
			section.setText(provider.getName());
			viewer = new TableViewer(toolkit.createTable(section, SWT.NONE));
			viewer.setContentProvider(this);
			viewer.setLabelProvider(this);
			viewer.setSorter(PALLATE_ITEM_SORT);
			viewer.setInput(provider);
			viewer.addDragSupport(DND.DROP_COPY | DND.DROP_MOVE,
					new Transfer[] { PalletItemTransfer.getInstance() }, this);
			MenuManager menuMgr = new MenuManager("#PopupMenu");
			menuMgr.setRemoveAllWhenShown(true);
			menuMgr.addMenuListener(this);
			viewer.getControl().setMenu(
					menuMgr.createContextMenu(viewer.getControl()));
			section.setClient(viewer.getControl());
			if (equals(sectionsByProvider.values().iterator().next()))
				section.setExpanded(true);
			section.addExpansionListener(this);
			provider.addPalletItemObserver(this);
			return section;
		}

		/**
		 * Disposes this section.
		 */
		void destroy()
		{
			provider.removePalletItemObserver(this);
			section.removeExpansionListener(this);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(
		 *      org.eclipse.jface.viewers.Viewer, java.lang.Object,
		 *      java.lang.Object)
		 */
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
		{
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(
		 *      java.lang.Object)
		 */
		public Object[] getElements(Object inputElement)
		{
			List<PalletItem> palletItems = new LinkedList<PalletItem>();
			PalletItemProvider provider = (PalletItemProvider)inputElement;
			List<PalletItem> items = provider.getPalletItems();
			for(PalletItem item : items)
			{
				if (item.canBeContainedBy(container))
					palletItems.add(item);
			}
			return palletItems.toArray();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.BaseLabelProvider#dispose()
		 */
		public void dispose()
		{
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(
		 *      java.lang.Object, int)
		 */
		public String getColumnText(Object element, int columnIndex)
		{
			return ((PalletItem)element).getName();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(
		 *      java.lang.Object, int)
		 */
		public Image getColumnImage(Object element, int columnIndex)
		{
			final Image icon = ((PalletItem)element).getIcon();
			if (icon != null)
				return icon;
			return Activator.getDefault().getImageRegistry().get("ICON_MODULE");
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.action.IMenuListener#menuAboutToShow(
		 *      org.eclipse.jface.action.IMenuManager)
		 */
		@SuppressWarnings("unchecked")
		public void menuAboutToShow(IMenuManager manager)
		{
			provider.createMenu(null, manager,
					(PalletItem[])((IStructuredSelection)viewer.getSelection()).toList()
							.toArray(new PalletItem[0]));
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.swt.dnd.DragSourceListener#dragStart(
		 *      org.eclipse.swt.dnd.DragSourceEvent)
		 */
		public void dragStart(DragSourceEvent event)
		{
			event.doit = true;
			PalletItemTransfer.getInstance().setPalletItem(
					((PalletItem)((IStructuredSelection)viewer.getSelection())
							.getFirstElement()));
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.swt.dnd.DragSourceListener#dragSetData(
		 *      org.eclipse.swt.dnd.DragSourceEvent)
		 */
		public void dragSetData(DragSourceEvent event)
		{
			if (PalletItemTransfer.getInstance().isSupportedType(event.dataType))
				event.data = PalletItemTransfer.getInstance().getPalletItem();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.swt.dnd.DragSourceListener#dragFinished(
		 *      org.eclipse.swt.dnd.DragSourceEvent)
		 */
		public void dragFinished(DragSourceEvent event)
		{
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.ui.forms.events.IExpansionListener#
		 *      expansionStateChanging(org.eclipse.ui.forms.events.ExpansionEvent)
		 */
		public void expansionStateChanging(ExpansionEvent e)
		{
			if (!e.getState())
				return;
			for(ProviderSection providerSection : sectionsByProvider.values())
			{
				if (providerSection == this)
					continue;
				if (providerSection.section != null)
					providerSection.section.setExpanded(false);
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.ui.forms.events.IExpansionListener#
		 *      expansionStateChanged(org.eclipse.ui.forms.events.ExpansionEvent)
		 */
		public void expansionStateChanged(ExpansionEvent e)
		{
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.desktop.views.pallet.PalletItemObserver#
		 *      palletItemsChanged()
		 */
		public void palletItemsChanged()
		{
			try
            {
	            System.err.println("Viewer null? " + Boolean.toString(viewer == null));
	            if (viewer != null)
	            {
	            	viewer.getTable().getDisplay().asyncExec(new Runnable()
	            	{
	            		public void run()
	            		{
	            		viewer.refresh();
	            		form.getBody().layout(true, true);
	            		form.reflow(true);
	            		}
	            	});
	            }
            }
            catch(RuntimeException e)
            {
	            e.printStackTrace();
            }
		}
	}
}
