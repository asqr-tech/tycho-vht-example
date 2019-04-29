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
package org.eclipse.vtp.desktop.editors.core.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.vtp.desktop.editors.core.actions.DesignElementAction;
import org.eclipse.vtp.desktop.editors.core.actions.DesignElementActionManager;
import org.eclipse.vtp.desktop.editors.core.configuration.ComponentPropertiesDialog;
import org.eclipse.vtp.desktop.editors.core.configuration.ComponentPropertiesPanel;
import org.eclipse.vtp.desktop.editors.core.configuration.ComponentPropertiesPanelProviderRegistry;
import org.eclipse.vtp.desktop.editors.core.dialogs.CanvasPropertiesDialog;
import org.eclipse.vtp.desktop.editors.core.dialogs.CreateDialogTemplateWizard;
import org.eclipse.vtp.desktop.editors.core.model.RenderedModel;
import org.eclipse.vtp.desktop.editors.core.model.RenderedModelListener;
import org.eclipse.vtp.desktop.editors.core.model.SelectionResult;
import org.eclipse.vtp.desktop.editors.themes.core.ComponentFrame;
import org.eclipse.vtp.desktop.editors.themes.core.ConnectorFrame;
import org.eclipse.vtp.desktop.editors.themes.core.ElementFrame;
import org.eclipse.vtp.desktop.editors.themes.core.commands.BeginConnector;
import org.eclipse.vtp.desktop.editors.themes.core.commands.Command;
import org.eclipse.vtp.desktop.editors.themes.core.commands.CommandListener;
import org.eclipse.vtp.desktop.editors.themes.core.commands.LocateElement;
import org.eclipse.vtp.desktop.editors.themes.core.commands.ShowProperties;
import org.eclipse.vtp.desktop.editors.themes.core.commands.StartMove;
import org.eclipse.vtp.desktop.model.core.design.IDesignComponent;
import org.eclipse.vtp.desktop.model.core.design.IDesignConnector;
import org.eclipse.vtp.desktop.model.core.design.IDesignElement;
import org.eclipse.vtp.desktop.model.core.internal.design.Design;
import org.eclipse.vtp.desktop.model.core.internal.design.DesignConnector;
import org.eclipse.vtp.desktop.model.core.internal.design.DesignElement;
import org.eclipse.vtp.desktop.model.elements.core.internal.DialogElement;
import org.eclipse.vtp.desktop.model.elements.core.internal.PrimitiveElement;
import org.eclipse.vtp.desktop.views.pallet.PallateProviderManager;
import org.eclipse.vtp.desktop.views.pallet.PalletItem;
import org.eclipse.vtp.desktop.views.pallet.PalletItemProvider;
import org.eclipse.vtp.desktop.views.pallet.PalletItemTransfer;

public class BasicController implements CommandListener, MouseListener, MouseMoveListener, MouseTrackListener, PaintListener, DropTargetListener, DragSourceListener, KeyListener, RenderedModelListener
{
	RenderedModel currentCanvas;
	Point commandPosition = null;
	List<ControllerListener> listeners = new ArrayList<ControllerListener>();
	Map<String, Object> resourceMap = new HashMap<String, Object>();
	Control interactionSurface = null;
	boolean dragging = false;
	Point dragPoint = null;
	boolean isLine = false;
	boolean isArea = false;
	private DropTarget dropTarget = null;
	private DragSource dragSource = null;
	IAdaptable adaptableContainer = null;
	List<PalletItemProvider> providers = null;
	
	/**
	 * @param brandManager
	 * @param renderedCanvas
	 */
	public BasicController(RenderedModel renderedCanvas)
	{
		super();
		this.currentCanvas = renderedCanvas;
		currentCanvas.addListener(this);
		providers = PallateProviderManager.getPallateProviders();
//TODO		renderedCanvas.getUICanvas().validateCanvasStatus();
	}
	
	/**
	 * @param adaptableContainer
	 */
	public void setContainer(IAdaptable adaptableContainer)
	{
		this.adaptableContainer = adaptableContainer;
	}
	
	/**
	 * @param resourceMap
	 */
	public void setResourceMap(Map<String, Object> resourceMap)
	{
		this.resourceMap = resourceMap;
	}
	
	/**
	 * @param connector
	 */
	public void showConnectorProperties(IDesignConnector connector)
	{
		try
        {
	        Shell workbenchShell = Display.getCurrent().getActiveShell();
	        boolean canDelete = connector.getConnectionPoints().isEmpty();
	        ComponentPropertiesDialog cpd = new ComponentPropertiesDialog(currentCanvas.getUIModel(), workbenchShell);
	        final Color dialogFrameColor = new Color(Display.getCurrent(), 77, 113, 179);
	        final Color dialogSideBarColor = new Color(Display.getCurrent(), 240, 243, 249);
	        if(dialogFrameColor != null)
	        {
	        	cpd.setFrameColor(dialogFrameColor);
	        }

	        if(dialogSideBarColor != null)
	        {
	        	cpd.setSideBarColor(dialogSideBarColor);
	        }

	        cpd.setTitle("DesignConnector Properties: " + connector.getOrigin().getName() + " -> " + connector.getDestination().getName());
	        List<ComponentPropertiesPanel> panels = ComponentPropertiesPanelProviderRegistry.getInstance().getPropertiesPanels(connector);
	        for(ComponentPropertiesPanel panel : panels)
	        {
	        	if(panel.getName().equals("General"))
	        		cpd.addPage(panel);
	        }
	        for(ComponentPropertiesPanel panel : panels)
	        {
	        	if(!panel.getName().equals("General"))
	        		cpd.addPage(panel);
	        }
			for(ComponentPropertiesPanel panel : panels)
			{
				panel.resolve();
			}
	        cpd.open();
	        if(connector.getConnectionPoints().isEmpty() && canDelete)
	        	connector.getDesign().removeDesignConnector(connector);
//TODO	        currentCanvas.getUICanvas().validateCanvasStatus();
	        fireGraphicUpdate(0, 0, currentCanvas.getUIModel().getWidth(), currentCanvas.getUIModel().getHeight(), false);
	        dialogFrameColor.dispose();
	        dialogSideBarColor.dispose();
        }
        catch(RuntimeException e)
        {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }
	}
	
	/**
	 * Creates a new CanvasPropertiesDialog for the current canvas
	 * @see org.eclipse.vtp.desktop.editors.core.dialogs.CanvasPropertiesDialog#CanvasPropertiesDialog(Shell)
	 */
	public void showCanvasProperties()
	{
		Shell workbenchShell = Display.getCurrent().getActiveShell();
		CanvasPropertiesDialog cpd = new CanvasPropertiesDialog(workbenchShell);
		cpd.setRenderedCanvas(currentCanvas);
		if(cpd.open() == ComponentPropertiesDialog.OK)
			fireGraphicUpdate(0, 0, currentCanvas.getUIModel().getWidth(), currentCanvas.getUIModel().getHeight(), false);
	}
	
	/**
	 * Creates a ComponentPropertiesDialog and displays the PropertiesPanel for the element
	 * @param element - The element the PropertiesPanels of which are to be displayed
	 * @see org.eclipse.vtp.desktop.editors.core.controller.BasicController#showElementProperties(DesignElement, boolean)
	 */
	public void showElementProperties(IDesignElement element)
	{
		showElementProperties(element, false);
	}
	
	/**
	 * Creates a ComponentPropertiesDialog and displays the PropertiesPanels for the element
	 * @param element - The element the PropertiesPanels of which is to be displayed
	 * @param deleteOnCancel - If true, the module will be deleted if the user clicks the cancel button.
	 * @see org.eclipse.vtp.desktop.editors.core.controller.BasicController#showElementProperties(DesignElement)
	 */
	public boolean showElementProperties(IDesignElement element, boolean deleteOnCancel)
	{
		boolean persists = !deleteOnCancel;
		try
		{
			Shell workbenchShell = Display.getCurrent().getActiveShell();
			ComponentPropertiesDialog cpd = new ComponentPropertiesDialog(currentCanvas.getUIModel(), workbenchShell);
			final Color dialogFrameColor = new Color(Display.getCurrent(), 77, 113, 179);
			final Color dialogSideBarColor = new Color(Display.getCurrent(), 240, 243, 249);
			if(dialogFrameColor != null)
			{
				cpd.setFrameColor(dialogFrameColor);
			}

			if(dialogSideBarColor != null)
			{
				cpd.setSideBarColor(dialogSideBarColor);
			}
			cpd.setTitle(element.getTitle());
			List<ComponentPropertiesPanel> panels = ComponentPropertiesPanelProviderRegistry.getInstance().getPropertiesPanels(element);
			if(panels.size() > 0)
			{
				for(ComponentPropertiesPanel cpp : panels)
				{
					cpd.addPage(cpp);
				}
				for(ComponentPropertiesPanel panel : panels)
				{
					panel.resolve();
				}
				int open = cpd.open();
				if(open != ComponentPropertiesDialog.OK && deleteOnCancel)
				{
					persists = false;
					element.getDesign().removeDesignElement(element);
				}
				else if(deleteOnCancel)
				{
					persists = true;
				}
				fireGraphicUpdate(0, 0, currentCanvas.getUIModel().getWidth(), currentCanvas.getUIModel().getHeight(), false);
			}
			dialogFrameColor.dispose();
			dialogSideBarColor.dispose();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return persists;
	}

	/**
	 * @param interactiveSurface
	 */
	public void setControl(Control interactiveSurface)
	{
		this.interactionSurface = interactiveSurface;
		dropTarget = new DropTarget(interactionSurface, DND.DROP_COPY | DND.DROP_DEFAULT | DND.DROP_MOVE);
		dropTarget.setTransfer(new Transfer[] {TextTransfer.getInstance(), PalletItemTransfer.getInstance()});
		dropTarget.addDropListener(this);
		interactiveSurface.addDisposeListener(new DisposeListener()
		{
			public void widgetDisposed(DisposeEvent e)
			{
				dropTarget.dispose();
				if(dragSource != null)
					dragSource.dispose();
			}
		});
	}
	
	/**
	 * @param listener
	 */
	public void addListener(ControllerListener listener)
	{
		listeners.add(listener);
	}
	
	/**
	 * @param listener
	 */
	public void removeListener(ControllerListener listener)
	{
		listeners.remove(listener);
	}
	
	/**
	 * @param menuManager
	 */
	public void fillContextMenu(IMenuManager menuManager)
	{
		try
        {
	        final Point contextPoint = new Point(commandPosition.x, commandPosition.y);
	        MenuManager addMenu = new MenuManager("Add");
	        menuManager.add(addMenu);
	        
	        //comparator for sorting list, just uses element names
	        Comparator<PalletItem> comp = new Comparator<PalletItem>()
	        {
	        	public int compare(PalletItem o1, PalletItem o2)
	        	{
	        		return o1.getName().compareTo(o2.getName());
	        	}
	        };
	        
	        for(int i = 0; i < providers.size(); i++)
	        {
	        	//added seperators between each provider, akin to the palette view
	        	if (i > 0) {
	        		addMenu.add(new Separator());
	        	}
	        	PalletItemProvider provider = providers.get(i);
	        	List<PalletItem> items = provider.getPalletItems();
	        	//uses comparator to sort each section
	        	Collections.sort(items, comp);
	        	for(final PalletItem item : items)
	        	{
	        		if(item.canBeContainedBy(currentCanvas.getUIModel()))
	        			addMenu.add(new Action(item.getName())
	        			{
	        				public void run()
	        				{
	        					addElement(item, contextPoint);
	        				}
	        			});
	        	}
	        }
	        if(currentCanvas.getSelection() != null && currentCanvas.getSelection().getSelectedItems().size() > 1 && currentCanvas.getSelection().getPrimarySelection() instanceof ElementFrame)
	        {
	        	MenuManager alignEdgeMenu = new MenuManager("Align Edges");
	        	menuManager.add(alignEdgeMenu);
	        	alignEdgeMenu.add(new Action("Left")
	        	{
	        		public void run()
	        		{
	        			currentCanvas.getSelection().alignEdge(SWT.LEFT);
	        		}
	        	});
	        	alignEdgeMenu.add(new Action("Top")
	        	{
	        		public void run()
	        		{
	        			currentCanvas.getSelection().alignEdge(SWT.TOP);
	        		}
	        	});
	        	alignEdgeMenu.add(new Action("Right")
	        	{
	        		public void run()
	        		{
	        			currentCanvas.getSelection().alignEdge(SWT.RIGHT);
	        		}
	        	});
	        	alignEdgeMenu.add(new Action("Bottom")
	        	{
	        		public void run()
	        		{
	        			currentCanvas.getSelection().alignEdge(SWT.BOTTOM);
	        		}
	        	});
	        	MenuManager alignCenterMenu = new MenuManager("Align Centers");
	        	menuManager.add(alignCenterMenu);
	        	alignCenterMenu.add(new Action("Horizontal")
	        	{
	        		public void run()
	        		{
	        			currentCanvas.getSelection().alignCenter(SWT.HORIZONTAL);
	        		}
	        	});
	        	alignCenterMenu.add(new Action("Vertical")
	        	{
	        		public void run()
	        		{
	        			currentCanvas.getSelection().alignCenter(SWT.VERTICAL);
	        		}
	        	});
	        }
//TODO fix actions
	        if(currentCanvas.getSelection() != null && currentCanvas.getSelection().getSelectedItems().size() == 1 && currentCanvas.getSelection().getPrimarySelection() instanceof ElementFrame && ((ElementFrame)currentCanvas.getSelection().getPrimarySelection()).getDesignElement() instanceof PrimitiveElement)
	        {
	        	PrimitiveElement primitiveElement = (PrimitiveElement)((ElementFrame)currentCanvas.getSelection().getPrimarySelection()).getDesignElement();
	        	List<DesignElementAction> actionList = DesignElementActionManager.getDefault().getActions(primitiveElement, this);
	        	for(DesignElementAction action : actionList)
	        	{
	                menuManager.add(action);
	            }
	        }
	        if(currentCanvas.getSelection() != null && currentCanvas.getSelection().getSelectedItems().size() == 1 && currentCanvas.getSelection().getPrimarySelection() instanceof ElementFrame && ((ElementFrame)currentCanvas.getSelection().getPrimarySelection()).getDesignElement() instanceof DialogElement)
	        {
	        	final DialogElement primitiveElement = (DialogElement)((ElementFrame)currentCanvas.getSelection().getPrimarySelection()).getDesignElement();
	        	menuManager.add(new Action("Create Template")
	        	{
	        		public void run()
	        		{
	        			CreateDialogTemplateWizard cdtw = new CreateDialogTemplateWizard(primitiveElement);
	        			WizardDialog wd = new WizardDialog(Display.getCurrent().getActiveShell(), cdtw);
	        			wd.open();
	        		}
	        	});
	        }
	        if(currentCanvas.getSelection() != null && currentCanvas.getSelection().getPrimarySelection() instanceof ConnectorFrame)
	        {
	        	menuManager.add(new Action("Toggle Midpoint")
	        	{
	        		public void run()
	        		{
	        			ConnectorFrame cf = (ConnectorFrame)currentCanvas.getSelection().getPrimarySelection();
	        			cf.toggleMidPoint(contextPoint.x, contextPoint.y);
	        			fireGraphicUpdate(cf.getBounds(), false);
	        		}
	        	});
	        }
	        if(currentCanvas.getSelection().getPrimarySelection() != null && currentCanvas.getSelection().getPrimarySelection() instanceof ElementFrame)
	        {
				boolean canDelete = true;
				if(currentCanvas.getSelection().getPrimarySelection() instanceof ElementFrame)
				{
					List<ComponentFrame> items = currentCanvas.getSelection().getSelectedItems();
					for(int i = 0; i < items.size(); i++)
		            {
			            ElementFrame ef = (ElementFrame)items.get(i);
			            IDesignElement el = ef.getDesignElement();
		            	canDelete = canDelete & el.canDelete();
		            }
				}
				if(canDelete)
				{
	        		menuManager.add(new Action("Delete")
	        		{
	        			public void run()
	        			{
	        				MessageBox confirmationDialog =
	        					new MessageBox(Display.getCurrent().getActiveShell(),
	        						SWT.YES | SWT.NO | SWT.ICON_WARNING);
	        				confirmationDialog.setMessage(
	        					"Are you sure you want to delete the selected item(s)?");
	
	        				int result = confirmationDialog.open();
	
	        				if(result == SWT.YES)
	        				{
	        					currentCanvas.deleteSelectedItems();
	        					fireGraphicUpdate(0, 0, currentCanvas.getUIModel().getWidth(), currentCanvas.getUIModel().getHeight(), false);
	        				}
	        			}
	        		});
				}
	        }
	        if(currentCanvas.getSelection().getPrimarySelection() != null && currentCanvas.getSelection().getPrimarySelection() instanceof ConnectorFrame)
	        {
        		menuManager.add(new Action("Delete")
        		{
        			public void run()
        			{
        				MessageBox confirmationDialog =
        					new MessageBox(Display.getCurrent().getActiveShell(),
        						SWT.YES | SWT.NO | SWT.ICON_WARNING);
        				confirmationDialog.setMessage(
        					"Are you sure you want to delete the selected item(s)?");

        				int result = confirmationDialog.open();

        				if(result == SWT.YES)
        				{
        					currentCanvas.deleteSelectedItems();
        					fireGraphicUpdate(0, 0, currentCanvas.getUIModel().getWidth(), currentCanvas.getUIModel().getHeight(), false);
        				}
        			}
        		});
	        }
	        menuManager.add(new Separator());
	        if(currentCanvas.getSelection().getPrimarySelection() == null || currentCanvas.getSelection().getPrimarySelection() instanceof ConnectorFrame)
	        {
	        	menuManager.add(new Action("Properties")
	        	{
	        		public void run()
	        		{
	        			ComponentFrame cf = currentCanvas.getSelection().getSelectedItems().size() > 1 ? null : currentCanvas.getSelection().getPrimarySelection();
	        			if(cf != null)
	        			{
	        				if(cf instanceof ConnectorFrame)
	        					showConnectorProperties(((ConnectorFrame)cf).getDesignConnector());
	        				else
	        					showElementProperties(((ElementFrame)cf).getDesignElement());
	        			}
	        			else
	        			{
	        				showCanvasProperties();
	        			}
	        		}
	        	});
	        }
	        if(currentCanvas.getSelection().getPrimarySelection() != null && currentCanvas.getSelection().getSelectedItems().size() == 1 && currentCanvas.getSelection().getPrimarySelection() instanceof ElementFrame && ComponentPropertiesPanelProviderRegistry.getInstance().hasPropertiesPanels(((ElementFrame)currentCanvas.getSelection().getPrimarySelection()).getDesignElement()))
	        {
	        	menuManager.add(new Action("Properties")
	        	{
	        		public void run()
	        		{
	        			ComponentFrame cf = currentCanvas.getSelection().getSelectedItems().size() > 1 ? null : currentCanvas.getSelection().getPrimarySelection();
	        			if(cf != null)
	        			{
	        				if(cf instanceof ConnectorFrame)
	        					showConnectorProperties(((ConnectorFrame)cf).getDesignConnector());
	        				else
	        					showElementProperties(((ElementFrame)cf).getDesignElement());
	        			}
	        			else
	        			{
	        				showCanvasProperties();
	        			}
	        		}
	        	});
	        }
        }
        catch(RuntimeException e)
        {
	        e.printStackTrace();
        }
		
	}
	
	/**
	 * @param rec
	 * @param inProgress
	 */
	public void fireGraphicUpdate(Rectangle rec, boolean inProgress)
	{
		fireGraphicUpdate(rec.x, rec.y, rec.width, rec.height, inProgress);
	}
	
	/**
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param inProgress
	 */
	public void fireGraphicUpdate(int x, int y, int width, int height, boolean inProgress)
	{
		for(int i = 0; i < listeners.size(); i++)
		{
			listeners.get(i).graphicUpdate(x, y, width, height, inProgress);
		}
	}
	
	public void fireSelectionChanged()
	{
		for(int i = 0; i < listeners.size(); i++)
		{
			listeners.get(i).selectionChanged(currentCanvas.getSelection());
		}
	}
	
	/********************************************************
	 * Implementation of org.eclipse.swt.events.MouseListener
	 ********************************************************/
	public void mouseDown(MouseEvent event)
	{
		try
		{
			commandPosition = new Point(event.x, event.y);
			Rectangle initialSelectionRect = currentCanvas.getSelection().getTertiaryBounds();
			ComponentFrame initialPrimarySelection = currentCanvas.getSelection().getPrimarySelection();
			SelectionResult result = currentCanvas.selectAt(event.x, event.y, (event.stateMask & SWT.SHIFT) != 0);
			System.out.println("result: hit=" + result.wasHit() + " selectionChanged=" + result.wasSelectionChanged() + " primaryChanged=" + result.wasPrimaryChanged());
			if(result.wasSelectionChanged())
			{
				if(initialPrimarySelection == null)
				{
					fireGraphicUpdate(currentCanvas.getSelection().getTertiaryBounds(), false);
				}
				else
				{
					fireGraphicUpdate(currentCanvas.getSelection().getTertiaryBounds() == null ? initialSelectionRect : initialSelectionRect.union(currentCanvas.getSelection().getTertiaryBounds()), false);
				}
				fireSelectionChanged();
			}
			if(event.button == 1)
			{
				if(result.wasHit())
				{
					currentCanvas.getSelection().getPrimarySelection().mouseDown(this, event.x, event.y, event.stateMask);
				}
				else //same selection, start area selection
				{
					dragPoint = commandPosition;
					dragging = true;
					isArea = true;
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.MouseListener#mouseUp(org.eclipse.swt.events.MouseEvent)
	 */
	public void mouseUp(MouseEvent event)
	{
		try
		{
		interactionSurface.setCursor(Display.getCurrent().getSystemCursor(SWT.CURSOR_ARROW));
		commandPosition = new Point(event.x, event.y);
		if(dragging)
		{
			boolean wasLine = isLine;
			boolean wasArea = isArea;
			dragging = false;
			isLine = false;
			isArea = false;
			if(wasLine) //find destination element
			{
				ElementFrame ef = (ElementFrame)currentCanvas.getSelection().getPrimarySelection();
				ElementFrame dest = currentCanvas.findElementAt(event.x, event.y);
				if(dest != null)
				{
					if(dest.getDesignElement().acceptsConnector(ef.getDesignElement()))
					{
						ConnectorFrame connectorFrame = currentCanvas.connectElements(ef, dest);
						showConnectorProperties(connectorFrame.getDesignConnector());
						//TODO need to delete the connector is no exit points were selected
						currentCanvas.select(connectorFrame, false);
						currentCanvas.postAddedItem();
					}
					currentCanvas.getSelection().validateSelection();
					fireGraphicUpdate(ef.getBounds().union(dest.getBounds()), false);
				}
				else
				{
					fireGraphicUpdate(ef.getBounds().union(new Rectangle(Math.min(dragPoint.x, commandPosition.x), Math.min(dragPoint.y, commandPosition.y), Math.abs(commandPosition.x - dragPoint.x) + 2, Math.abs(commandPosition.y - dragPoint.y) + 2)), false);
				}
			}
			else if(wasArea)
			{
				Rectangle selectionArea = new Rectangle(Math.min(dragPoint.x, commandPosition.x) -2, Math.min(dragPoint.y, commandPosition.y) -2, Math.abs(dragPoint.x - commandPosition.x) + 4, Math.abs(dragPoint.y - commandPosition.y) + 4);
				Rectangle initialSelectionRect = currentCanvas.getSelection().getTertiaryBounds();
				currentCanvas.selectRegion(selectionArea, (event.stateMask & SWT.SHIFT) != 0);
				Rectangle currentSelectionRect = currentCanvas.getSelection().getTertiaryBounds();
				if(initialSelectionRect != null && !initialSelectionRect.isEmpty())
					selectionArea.add(initialSelectionRect);
				if(currentSelectionRect != null && !currentSelectionRect.isEmpty())
					selectionArea.add(currentSelectionRect);
				fireGraphicUpdate(selectionArea, false);
			}
			else
			{
				currentCanvas.getSelection().endMove();
				fireGraphicUpdate(currentCanvas.getSelection().getTertiaryBounds(), false);
			}
		}
		else
		{
			ComponentFrame cf = currentCanvas.findComponentAt(event.x, event.y);
			if(cf != null)
			{
				cf.mouseUp(this, event.x, event.y, event.stateMask);
			}
		}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.MouseListener#mouseDoubleClick(org.eclipse.swt.events.MouseEvent)
	 */
	public void mouseDoubleClick(MouseEvent event)
	{
		commandPosition = new Point(event.x, event.y);
		interactionSurface.setCursor(Display.getCurrent().getSystemCursor(SWT.CURSOR_ARROW));
		dragging = false;
		isLine = false;
		isArea = false;
		ComponentFrame cf = currentCanvas.findComponentAt(event.x, event.y);
		if(cf != null)
		{
			cf.mouseDoubleClick(this, event.x, event.y, event.stateMask);
		}
		else
		{
			showCanvasProperties();
		}
	}

//	boolean dndFlag = false;
//	boolean skipDown = false;
	/************************************************************
	 * Implementation of org.eclipse.swt.events.MouseMoveListener
	 ************************************************************/
	public void mouseMove(MouseEvent event)
	{
		Point oldPosition = commandPosition;
		commandPosition = new Point(event.x, event.y);
		if(dragging)
		{
			currentCanvas.startBatchUpdate();
			Rectangle redrawArea = null;
			if(!isLine && !isArea) //dragging a selection
			{
				redrawArea = currentCanvas.getSelection().getTertiaryBounds();
				currentCanvas.getSelection().adjustPosition(commandPosition.x - dragPoint.x, commandPosition.y - dragPoint.y);
				redrawArea.add(currentCanvas.getSelection().getTertiaryBounds());
				dragPoint = commandPosition;
			}
			else if(isArea)
			{
				if(oldPosition == null)
					oldPosition = new Point(0, 0);
				Rectangle previousSelectionArea = new Rectangle(Math.min(dragPoint.x, oldPosition.x) - 2, Math.min(dragPoint.y, oldPosition.y) - 2, Math.abs(dragPoint.x - oldPosition.x) + 4, Math.abs(dragPoint.y - oldPosition.y) + 4);
				Rectangle currentSelectionArea = new Rectangle(Math.min(dragPoint.x, commandPosition.x) - 2, Math.min(dragPoint.y, commandPosition.y) - 2, Math.abs(dragPoint.x - commandPosition.x) + 4, Math.abs(dragPoint.y - commandPosition.y) + 4);
				redrawArea = previousSelectionArea.union(currentSelectionArea);
			}
			else //creating connector
			{
				ElementFrame ef = (ElementFrame)currentCanvas.getSelection().getPrimarySelection();
				redrawArea = ef.getBounds().union(new Rectangle(Math.min(dragPoint.x, commandPosition.x), Math.min(dragPoint.y, commandPosition.y), Math.abs(commandPosition.x - dragPoint.x) + 2, Math.abs(commandPosition.y - dragPoint.y) + 2));
				redrawArea.add(new Rectangle(Math.min(dragPoint.x, oldPosition.x), Math.min(dragPoint.y, oldPosition.y), Math.abs(oldPosition.x - dragPoint.x) + 2, Math.abs(oldPosition.y - dragPoint.y) + 2));
				ElementFrame df = currentCanvas.findElementAt(event.x, event.y);
				if(df != null)
				{
					if(!df.getDesignElement().acceptsConnector(ef.getDesignElement()))
					{
						interactionSurface.setCursor(Display.getCurrent().getSystemCursor(SWT.CURSOR_NO));
					}
				}
				else
				{
					interactionSurface.setCursor(Display.getCurrent().getSystemCursor(SWT.CURSOR_CROSS));
				}
			}
//			if(redrawArea != null)
//				fireGraphicUpdate(redrawArea, true);
			currentCanvas.endBatchUpdate();
		}
	}

	/*************************************************************
	 * Implementation of org.eclipse.swt.events.MouseTrackListener
	 *************************************************************/
	public void mouseEnter(MouseEvent event)
	{
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.MouseTrackListener#mouseExit(org.eclipse.swt.events.MouseEvent)
	 */
	public void mouseExit(MouseEvent event)
	{
		if(dragging && !isLine && !isArea) //moving a selection
		{
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.MouseTrackListener#mouseHover(org.eclipse.swt.events.MouseEvent)
	 */
	public void mouseHover(MouseEvent event)
	{
		commandPosition = new Point(event.x, event.y);
		
	}
	
	/********************************************************
	 * Implementation of org.eclipse.swt.events.PaintListener
	 ********************************************************/
	public void paintControl(PaintEvent e)
	{
		paintCanvas(e.gc);
	}
	
	/**
	 * @param gc1
	 */
	public void paintCanvas(GC gc1)
	{
		int curPri = Thread.currentThread().getPriority();
		Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
		Image buffer = new Image(gc1.getDevice(), currentCanvas.getUIModel().getWidth(), currentCanvas.getUIModel().getHeight());
		GC gc = new GC(buffer);
		gc.setBackground(gc1.getBackground());
		gc.setForeground(gc1.getForeground());
		gc.fillRectangle(0, 0, currentCanvas.getUIModel().getWidth(), currentCanvas.getUIModel().getHeight());
		if(isLine && dragging)
		{
			gc.drawLine(dragPoint.x, dragPoint.y, commandPosition.x, commandPosition.y);
		}
		currentCanvas.paintCanvas(gc, resourceMap, 0);
		if(isArea && dragging)
		{
			gc.setLineStyle(SWT.LINE_DOT);
			gc.setLineWidth(2);
			Rectangle currentSelectionArea = new Rectangle(Math.min(dragPoint.x, commandPosition.x), Math.min(dragPoint.y, commandPosition.y), Math.abs(dragPoint.x - commandPosition.x), Math.abs(dragPoint.y - commandPosition.y));
			gc.drawRectangle(currentSelectionArea);
			gc.setLineWidth(1);
		}
		gc1.drawImage(buffer, 0, 0);
		gc.dispose();
		buffer.dispose();
		Thread.currentThread().setPriority(curPri);
	}
	
	/****************************************************************************
	 * Implementation of org.eclipse.vtp.desktop.editors.commands.CommandListener
	 ****************************************************************************/
	public void executeCommand(Command command)
	{
		if(command instanceof StartMove)
		{
			dragging = true;
			dragPoint = commandPosition;
			interactionSurface.setCursor(Display.getCurrent().getSystemCursor(SWT.CURSOR_SIZEALL));
			currentCanvas.getSelection().startMove();
		}
		else if(command instanceof BeginConnector)
		{
			dragging = true;
			ElementFrame ef = (ElementFrame)currentCanvas.getSelection().getPrimarySelection();
			dragPoint = ef.getDesignElement().getCenterPoint();
			isLine = true;
			interactionSurface.setCursor(Display.getCurrent().getSystemCursor(SWT.CURSOR_CROSS));
		}
		else if(command instanceof ShowProperties)
		{
			ComponentFrame cof = currentCanvas.getSelection().getPrimarySelection();
			IDesignComponent component = (cof instanceof ElementFrame) ? ((ElementFrame)cof).getDesignElement() : ((ConnectorFrame)cof).getDesignConnector();
			if(component instanceof DesignConnector)
			{
				showConnectorProperties((DesignConnector)component);
			}
			else
			{
				DesignElement element = (DesignElement)component;
				if(element instanceof DialogElement)
				{
					ModelNavigationListener navigationListener = (ModelNavigationListener)adaptableContainer.getAdapter(ModelNavigationListener.class);
					if(navigationListener != null)
					{
						navigationListener.showDesign(((DialogElement)element).getId());
					}
				}
				this.showElementProperties(element);
			}
		}
		else if(command instanceof LocateElement)
		{
			LocateElement le = (LocateElement)command;
			String elementId = le.getElementId();
			ModelNavigationListener navigationListener = (ModelNavigationListener)adaptableContainer.getAdapter(ModelNavigationListener.class);
			if(navigationListener != null)
			{
				navigationListener.navigateToElement(elementId);
			}
		}
	}

	/**********************************************************
	 * Implementation of org.eclipse.swt.dnd.DropTargetListener
	 **********************************************************/
	public void dragEnter(DropTargetEvent event)
	{
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.dnd.DropTargetListener#dragLeave(org.eclipse.swt.dnd.DropTargetEvent)
	 */
	public void dragLeave(DropTargetEvent event)
	{
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.dnd.DropTargetListener#dragOperationChanged(org.eclipse.swt.dnd.DropTargetEvent)
	 */
	public void dragOperationChanged(DropTargetEvent event)
	{
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.dnd.DropTargetListener#dragOver(org.eclipse.swt.dnd.DropTargetEvent)
	 */
	public void dragOver(DropTargetEvent event)
	{
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.dnd.DropTargetListener#drop(org.eclipse.swt.dnd.DropTargetEvent)
	 */
	public void drop(DropTargetEvent event)
	{
		interactionSurface.setFocus();
		try
        {
	        PalletItem pi = PalletItemTransfer.getInstance().getPalletItem();
	        Point dropPoint = interactionSurface.toControl(event.x, event.y);
	        addElement(pi, dropPoint);
        }
        catch(RuntimeException e)
        {
	        e.printStackTrace();
        }
	}

	/**
	 * @param pi
	 * @param dropPoint
	 */
	private void addElement(PalletItem pi, Point dropPoint)
	{
		DesignElement createdElement = pi.createElement((Design)currentCanvas.getUIModel());
		currentCanvas.addElement(createdElement, dropPoint.x, dropPoint.y);
		fireGraphicUpdate(0, 0, currentCanvas.getUIModel().getWidth(), currentCanvas.getUIModel().getHeight(), false);
		boolean persists = true;
		if(pi.isPopOnDrop())
			persists = showElementProperties(createdElement, true);
		if(persists)
		{
			currentCanvas.selectAt(dropPoint.x, dropPoint.y, false);
			currentCanvas.postAddedItem();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.dnd.DropTargetListener#dropAccept(org.eclipse.swt.dnd.DropTargetEvent)
	 */
	public void dropAccept(DropTargetEvent event)
	{
		if(!PalletItemTransfer.getInstance().isSupportedType(event.currentDataType))
			event.detail = DND.DROP_NONE;
	}

	/**********************************************************
	 * Implementation of org.eclipse.swt.dnd.DragSourceListener
	 **********************************************************/
	public void dragFinished(DragSourceEvent event)
	{
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.dnd.DragSourceListener#dragSetData(org.eclipse.swt.dnd.DragSourceEvent)
	 */
	public void dragSetData(DragSourceEvent event)
	{
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.dnd.DragSourceListener#dragStart(org.eclipse.swt.dnd.DragSourceEvent)
	 */
	public void dragStart(DragSourceEvent event)
	{
		event.doit = event.x == -1 && event.y == -1;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.KeyListener#keyPressed(org.eclipse.swt.events.KeyEvent)
	 */
	public void keyPressed(KeyEvent e)
	{
		if(e.character == SWT.DEL)
		{
			if(currentCanvas.getSelection().getPrimarySelection() != null)
			{
				boolean canDelete = true;
				if(currentCanvas.getSelection().getPrimarySelection() instanceof ElementFrame)
				{
					List<ComponentFrame> items = currentCanvas.getSelection().getSelectedItems();
					for(int i = 0; i < items.size(); i++)
		            {
			            ElementFrame ef = (ElementFrame)items.get(i);
			            IDesignElement el = ef.getDesignElement();
		            	canDelete = canDelete & el.canDelete();
		            }
				}
				if(!canDelete)
					return;
				MessageBox confirmationDialog =
					new MessageBox(Display.getCurrent().getActiveShell(),
						SWT.YES | SWT.NO | SWT.ICON_WARNING);
				confirmationDialog.setMessage(
					"Are you sure you want to delete the selected item(s)?");
	
				int result = confirmationDialog.open();
	
				if(result == SWT.YES)
				{
					currentCanvas.deleteSelectedItems();
					fireGraphicUpdate(0, 0, currentCanvas.getUIModel().getWidth(), currentCanvas.getUIModel().getHeight(), false);
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.KeyListener#keyReleased(org.eclipse.swt.events.KeyEvent)
	 */
	public void keyReleased(KeyEvent e)
	{
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.editors.core.model.RenderedCanvasListener#renderedCanvasChanged(org.eclipse.vtp.desktop.editors.core.model.RenderedCanvas)
	 */
	public void renderedModelChanged(RenderedModel renderedCanvas)
	{
		fireGraphicUpdate(0, 0, currentCanvas.getUIModel().getWidth(), currentCanvas.getUIModel().getHeight(), false);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.editors.core.model.RenderedCanvasListener#renderedCanvasFormatChanged(org.eclipse.vtp.desktop.editors.core.model.RenderedCanvas)
	 */
	public void renderedModelFormatChanged(RenderedModel renderedCanvas)
    {
		fireGraphicUpdate(0, 0, currentCanvas.getUIModel().getWidth(), currentCanvas.getUIModel().getHeight(), false);
    }
	
	/**
	 * @return
	 */
	public RenderedModel getRenderedCanvas()
	{
		return currentCanvas;
		
	}
}
