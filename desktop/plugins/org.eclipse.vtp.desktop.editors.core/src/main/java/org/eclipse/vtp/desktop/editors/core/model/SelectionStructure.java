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
/**
 * 
 */
package org.eclipse.vtp.desktop.editors.core.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.vtp.desktop.editors.themes.core.ComponentFrame;
import org.eclipse.vtp.desktop.editors.themes.core.ConnectorFrame;
import org.eclipse.vtp.desktop.editors.themes.core.ElementFrame;
import org.eclipse.vtp.desktop.model.core.IDesignDocument;
import org.eclipse.vtp.desktop.model.core.design.IDesignElement;
import org.eclipse.vtp.desktop.model.core.internal.PartialDesignDocument;
import org.eclipse.vtp.desktop.model.core.internal.design.Design;
import org.w3c.dom.Document;

public class SelectionStructure
{
	private	List<ComponentFrame> selectedItems;
	private	List<ComponentFrame> secondarySelections;
	private	List<ComponentFrame> tertiarySelections;
	private	ComponentFrame primarySelection = null;
	private	Rectangle selectionBounds = null;
	private RenderedModel renderedCanvas = null;
	private int moveOffsetX = 0;
	private int moveOffsetY = 0;
	private boolean moving = false;
	
	public SelectionStructure(RenderedModel renderedCanvas)
	{
		super();
		selectedItems = new LinkedList<ComponentFrame>();
		secondarySelections = new LinkedList<ComponentFrame>();
		tertiarySelections = new LinkedList<ComponentFrame>();
		this.renderedCanvas = renderedCanvas;
	}
	
	public void clear()
	{
		secondarySelections.clear();
		tertiarySelections.clear();
		for(ComponentFrame componentFrame : selectedItems)
		{
			componentFrame.setSelected(false);
		}
		selectedItems.clear();
		primarySelection = null;
		selectionBounds = null;
	}
	
	public void startMove()
	{
		renderedCanvas.disableUndo();
		moving = true;
		moveOffsetX = 0;
		moveOffsetY = 0;
	}
	
	public void endMove()
	{
		renderedCanvas.enableUndo();
		if(moving)
		{
			moving = false;
			try
			{
				IDesignDocument designDocument = renderedCanvas.getUIModel().getDocument();
				Document document = renderedCanvas.getSelectionDocument(true);
				PartialDesignDocument pdd = new PartialDesignDocument(designDocument, (Design)renderedCanvas.getUIModel(), document);
				MoveOperation delo = new MoveOperation(pdd, moveOffsetX, moveOffsetY);
				delo.addContext(renderedCanvas.getUndoContext());
				renderedCanvas.getOperationHistory().execute(delo, null, null);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	SelectionResult select(ComponentFrame cof, boolean additive)
	{
		bufferedXoffset = 0;
		bufferedYoffset = 0;
		
		SelectionResult result = new SelectionResult();
		if(cof != null && cof instanceof ElementFrame)
		{
			result.setHit(true);
			if(!additive) //single selection
			{
				if(!selectedItems.contains(cof))
				{
					secondarySelections.clear();
					tertiarySelections.clear();
					for(ComponentFrame componentFrame : selectedItems)
					{
						componentFrame.setSelected(false);
					}
					selectedItems.clear();
					selectedItems.add(cof);
					cof.setSelected(true);
					primarySelection = cof;
					addSecondaryAndTertiarySelections();
					selectionBounds = null;
					result.setSelectionChanged(true);
					result.setPrimaryChanged(true);
				}
				else if(primarySelection != cof)
				{
					result.setSelectionChanged(false);
					result.setPrimaryChanged(true);
					selectedItems.remove(cof);
					selectedItems.add(0, cof);
					primarySelection = cof;
					selectionBounds = null;
				}
				return result;
			}
			//adding element to the selection list
			secondarySelections.clear();
			tertiarySelections.clear();
			if(selectedItems.contains(cof)) //single deselection
			{
				selectedItems.remove(cof);
				cof.setSelected(false);
				if(selectedItems.size() > 0)
				{
					primarySelection = selectedItems.get(0);
					addSecondaryAndTertiarySelections();
				}
				else
					primarySelection = null;
				selectionBounds = null;
				result.setSelectionChanged(true);
				result.setPrimaryChanged(true);
				return result;
			}
			if(primarySelection != null && primarySelection instanceof ConnectorFrame) //existing selection is a connector
			{
				primarySelection.setSelected(false);
				selectedItems.clear();
			}
			selectedItems.add(0, cof);
			primarySelection = cof;
			primarySelection.setSelected(true);
			addSecondaryAndTertiarySelections();
			selectionBounds = null;
			result.setSelectionChanged(true);
			result.setPrimaryChanged(true);
			return result;
		}
		if(cof != null && cof instanceof ConnectorFrame)
		{
			result.setHit(true);
			if(!additive)
			{
				if(!(primarySelection == cof))
				{
					secondarySelections.clear();
					tertiarySelections.clear();
					for(ComponentFrame componentFrame : selectedItems)
					{
						componentFrame.setSelected(false);
					}
					selectedItems.clear();
					selectedItems.add(cof);
					cof.setSelected(true);
					primarySelection = cof;
					selectionBounds = null;
					result.setSelectionChanged(true);
					result.setPrimaryChanged(true);
				}
				else
				{
					result.setSelectionChanged(false);
					result.setPrimaryChanged(false);
				}
			}
			else
			{
				secondarySelections.clear();
				tertiarySelections.clear();
				selectedItems.clear();
				if(primarySelection == cof)
				{
					cof.setSelected(false);
					primarySelection = null;
				}
				else
				{
					selectedItems.add(cof);
					cof.setSelected(true);
					primarySelection = cof;
				}
				selectionBounds = null;
				result.setSelectionChanged(true);
				result.setPrimaryChanged(true);
			}
			return result;
		}
		//no items were hit
		if(cof == null && !additive) //clear the selection state
		{
			if(primarySelection != null)
			{
				secondarySelections.clear();
				tertiarySelections.clear();
				for(ComponentFrame componentFrame : selectedItems)
				{
					componentFrame.setSelected(false);
				}
				selectedItems.clear();
				primarySelection = null;
				selectionBounds = null;
				result.setSelectionChanged(true);
				result.setPrimaryChanged(true);
			}
		}
		else
		{
			result.setSelectionChanged(false);
			result.setPrimaryChanged(false);
		}
		return result;
	}
	
	void select(List<ElementFrame> selectionList, boolean additive)
	{
		bufferedXoffset = 0;
		bufferedYoffset = 0;
		
		secondarySelections.clear();
		tertiarySelections.clear();
		if(!additive)
		{
			for(ComponentFrame componentFrame : selectedItems)
			{
				componentFrame.setSelected(false);
			}
			selectedItems.clear();
		}
		for(ElementFrame ef : selectionList)
		{
			selectedItems.remove(ef);
			selectedItems.add(0, ef);
			ef.setSelected(true);
			primarySelection = ef;
			selectionBounds = null;
		}
		if(primarySelection != null)
			addSecondaryAndTertiarySelections();
	}
	
	public void validateSelection()
	{
		selectionBounds = null;
		if(primarySelection instanceof ElementFrame)
		{
			secondarySelections.clear();
			tertiarySelections.clear();
			addSecondaryAndTertiarySelections();
		}
	}
	
	private void addSecondaryAndTertiarySelections()
	{
		//it is assumed that this cannot be called unless there is a
		//selection and the selection only contains elements
		for(ConnectorFrame cf : renderedCanvas.getConnectorFrames())
		{
			ElementFrame originFrame = null;
			ElementFrame destinationFrame = null;
			for(ComponentFrame componentFrame : selectedItems)
			{
				ElementFrame ef = (ElementFrame)componentFrame;
				if(cf.getDesignConnector().getOrigin() == ef.getDesignElement())
					originFrame = ef;
				if(cf.getDesignConnector().getDestination() == ef.getDesignElement())
					destinationFrame = ef;
				if(originFrame != null && destinationFrame != null)
					break;
			}
			if(originFrame != null && destinationFrame != null)
				secondarySelections.add(cf);
			else if(originFrame != null || destinationFrame != null)
				tertiarySelections.add(cf);
		}
	}
	
	public Rectangle getBounds()
	{
		if(selectionBounds == null)
		{
			for(ComponentFrame componentFrame : selectedItems)
			{
				if(selectionBounds == null)
					selectionBounds = componentFrame.getBounds();
				else
					selectionBounds.add(componentFrame.getBounds());
			}
			for(ComponentFrame componentFrame : secondarySelections)
			{
				selectionBounds.add(componentFrame.getBounds());
			}
		}
		return selectionBounds == null ? (Rectangle)null : new Rectangle(selectionBounds.x, selectionBounds.y, selectionBounds.width, selectionBounds.height);
	}
	
	public Rectangle getTertiaryBounds()
	{
		Rectangle sb = getBounds();
		if(sb == null)
			return null;
		Rectangle rect = new Rectangle(sb.x, sb.y, sb.width, sb.height);
		for(ComponentFrame componentFrame : tertiarySelections)
		{
			rect.add(componentFrame.getBounds());
		}
		return rect;
	}
	
	public void alignEdge(int edge)
	{
		if(primarySelection instanceof ElementFrame)
		{
			Rectangle r = primarySelection.getBounds();
			int xy = 0;
			if(edge == SWT.LEFT)
				xy = r.x;
			else if(edge == SWT.TOP)
				xy = r.y;
			else if(edge == SWT.RIGHT)
				xy = r.x + r.width;
			else if(edge == SWT.BOTTOM)
				xy = r.y + r.height;
			else
				return;
			try
			{
				AlignEdgeOperation delo = new AlignEdgeOperation(edge, xy);
				delo.addContext(renderedCanvas.getUndoContext());
				renderedCanvas.getOperationHistory().execute(delo, null, null);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void alignCenter(int orientation)
	{
		if(primarySelection instanceof ElementFrame)
		{
			Point p = ((ElementFrame)primarySelection).getDesignElement().getCenterPoint();
			try
			{
				AlignCenterOperation delo = new AlignCenterOperation(orientation, p);
				delo.addContext(renderedCanvas.getUndoContext());
				renderedCanvas.getOperationHistory().execute(delo, null, null);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	int bufferedXoffset = 0;
	int bufferedYoffset = 0;
	
	public void adjustPosition(int xoffset, int yoffset)
	{
		Rectangle rect = getBounds();
		if(xoffset < 0) //moving to the left
		{
			if(rect.x + xoffset < 0) //offset would move something off the screen, need to buffer
			{
				bufferedXoffset += xoffset + rect.x; //add additional distance to the buffer
				xoffset = rect.x * -1;
			}
			else //the selection can move the full xoffset
			{
				//reduce the current buffer size
				bufferedXoffset += xoffset;
				if(bufferedXoffset < 0) //we've depleted our buffer, time to actually move the selection
				{
					xoffset = bufferedXoffset;
					bufferedXoffset = 0;
				}
				else //still have some distance buffered, no change in x coordinate of selection
					xoffset = 0;
			}
		}
		else if(xoffset > 0) //moving to the right
		{
			if(rect.x + rect.width + xoffset > renderedCanvas.getUIModel().getWidth()) //offset would move something off the screen, need to buffer
			{
				bufferedXoffset += xoffset - (renderedCanvas.getUIModel().getWidth() - (rect.x + rect.width));
				xoffset = (renderedCanvas.getUIModel().getWidth() - (rect.x + rect.width));
			}
			else //the selection can move the full xoffset
			{
				bufferedXoffset += xoffset;
				if(bufferedXoffset > 0) //we've depleted our buffer, time to move selection
				{
					xoffset = bufferedXoffset;
					bufferedXoffset = 0;
				}
				else //still have some distance buffered
					xoffset = 0;
			}
		}
		if(yoffset < 0) //moving up
		{
			if(rect.y + yoffset < 0) //offset would move something off the screen, need to buffer
			{
				bufferedYoffset += yoffset + rect.y; //add additional distance to the buffer
				yoffset = rect.y * -1;
			}
			else //the selection can move the full xoffset
			{
				//reduce the current buffer size
				bufferedYoffset += yoffset;
				if(bufferedYoffset < 0) //we've depleted our buffer, time to actually move the selection
				{
					yoffset = bufferedYoffset;
					bufferedYoffset = 0;
				}
				else //still have some distance buffered, no change in x coordinate of selection
					yoffset = 0;
			}
		}
		else if(yoffset > 0)//moving down
		{
			if(rect.y + rect.height + yoffset > renderedCanvas.getUIModel().getHeight()) //offset would move something off the screen, need to buffer
			{
				bufferedYoffset += yoffset - (renderedCanvas.getUIModel().getHeight() - (rect.y + rect.height));
				yoffset = (renderedCanvas.getUIModel().getHeight() - (rect.y + rect.height));
			}
			else //the selection can move the full xoffset
			{
				bufferedYoffset += yoffset;
				if(bufferedYoffset > 0) //we've depleted our buffer, time to move selection
				{
					yoffset = bufferedYoffset;
					bufferedYoffset = 0;
				}
				else //still have some distance buffered
					yoffset = 0;
			}
		}
		moveOffsetX += xoffset;
		moveOffsetY += yoffset;
		for(ComponentFrame componentFrame : selectedItems)
		{
			componentFrame.adjustPosition(xoffset, yoffset);
		}
		for(ComponentFrame componentFrame : secondarySelections)
		{
			componentFrame.adjustPosition(xoffset, yoffset);
		}
		if(primarySelection instanceof ElementFrame)
		{
			selectionBounds.x += xoffset;
			selectionBounds.y += yoffset;
		}
		else //there is no way to transform the rectangle of a connector as you could be adjusting just a midpoint
		{
			selectionBounds = null;
		}
	}

	public ComponentFrame getPrimarySelection()
	{
		return primarySelection;
	}

	public List<ComponentFrame> getSelectedItems()
	{
		return Collections.unmodifiableList(selectedItems);
	}
	
	public List<ComponentFrame> getSecondarySelectedItems()
	{
		return Collections.unmodifiableList(secondarySelections);
	}
	
	public List<ComponentFrame> getTertiarySelectedItems()
	{
		return Collections.unmodifiableList(tertiarySelections);
	}
	
	public class MoveOperation extends AbstractOperation
	{
		PartialDesignDocument pdd = null;
		int xOffset = 0;
		int yOffset = 0;
		
		public MoveOperation(PartialDesignDocument pdd, int xOffset, int yOffset)
		{
			super("Move");
			this.pdd = pdd;
			this.xOffset = xOffset;
			this.yOffset = yOffset;
		}

		@Override
		public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException
		{
			//this is a no-op for this operation.  the move already happened piecemeal.
			if(monitor != null)
				monitor.done();
			return Status.OK_STATUS;
		}

		@Override
		public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException
		{
			renderedCanvas.startBatchUpdate();
			renderedCanvas.selectPartialDocument(pdd);
			adjustPosition(xOffset, yOffset);
			renderedCanvas.endBatchUpdate();
			if(monitor != null)
				monitor.done();
			return Status.OK_STATUS;
		}

		@Override
		public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException
		{
			renderedCanvas.startBatchUpdate();
			renderedCanvas.selectPartialDocument(pdd);
			adjustPosition(xOffset * -1, yOffset * -1);
			renderedCanvas.endBatchUpdate();
			if(monitor != null)
				monitor.done();
			return Status.OK_STATUS;
		}
	}
	
	public class AlignCenterOperation extends AbstractOperation
	{
		int orientation = SWT.VERTICAL;
		Point p = null;
		PartialDesignDocument pdd = null;

		public AlignCenterOperation(int orientation, Point p)
		{
			super("Align Centers");
			this.orientation = orientation;
			this.p = p;
		}

		@Override
		public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException
		{
			renderedCanvas.startBatchUpdate();
			int xy = 0;
			if(orientation == SWT.HORIZONTAL)
				xy = p.y;
			else if(orientation == SWT.VERTICAL)
				xy = p.x;
			else
				return Status.CANCEL_STATUS;
			IDesignDocument designDocument = renderedCanvas.getUIModel().getDocument();
			Document document = renderedCanvas.getSelectionDocument(true);
			pdd = new PartialDesignDocument(designDocument, (Design)renderedCanvas.getUIModel(), document);
			for(ComponentFrame componentFrame : selectedItems)
			{
				((ElementFrame)componentFrame).alignCenter(orientation, xy);
			}
			renderedCanvas.endBatchUpdate();
			if(monitor != null)
				monitor.done();
			return Status.OK_STATUS;
		}

		@Override
		public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException
		{
			renderedCanvas.startBatchUpdate();
			int xy = 0;
			if(orientation == SWT.HORIZONTAL)
				xy = p.y;
			else if(orientation == SWT.VERTICAL)
				xy = p.x;
outer:		for(IDesignElement designElement : pdd.getMainDesign().getDesignElements())
			{
				for(ComponentFrame componentFrame : renderedCanvas.getElementFrames())
				{
					if(componentFrame.getDesignComponent().getId().equals(designElement.getId()))
					{
						((ElementFrame)componentFrame).alignCenter(orientation, xy);
						continue outer;
					}
				}
			}
			renderedCanvas.endBatchUpdate();
			if(monitor != null)
				monitor.done();
			return Status.OK_STATUS;
		}

		@Override
		public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException
		{
			renderedCanvas.startBatchUpdate();
outer:		for(IDesignElement designElement : pdd.getMainDesign().getDesignElements())
			{
				int xy = 0;
				if(orientation == SWT.HORIZONTAL)
					xy = designElement.getCenterPoint().y;
				else if(orientation == SWT.VERTICAL)
					xy = designElement.getCenterPoint().x;
				for(ComponentFrame componentFrame : renderedCanvas.getElementFrames())
				{
					if(componentFrame.getDesignComponent().getId().equals(designElement.getId()))
					{
						((ElementFrame)componentFrame).alignCenter(orientation, xy);
						continue outer;
					}
				}
			}
			renderedCanvas.endBatchUpdate();
			if(monitor != null)
				monitor.done();
			return Status.OK_STATUS;
		}
		
	}
	
	public class AlignEdgeOperation extends AbstractOperation
	{
		int edge;
		int xy;
		PartialDesignDocument pdd = null;
		Map<String, Point> locationOffsets = new HashMap<String, Point>();

		public AlignEdgeOperation(int edge, int xy)
		{
			super("Align Edge");
			this.edge = edge;
			this.xy = xy;
		}

		@Override
		public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException
		{
			IDesignDocument designDocument = renderedCanvas.getUIModel().getDocument();
			Document document = renderedCanvas.getSelectionDocument(true);
			pdd = new PartialDesignDocument(designDocument, (Design)renderedCanvas.getUIModel(), document);
			renderedCanvas.startBatchUpdate();
			for(ComponentFrame componentFrame : selectedItems)
			{
				ElementFrame ef = (ElementFrame)componentFrame;
				Point p = new Point(ef.getDesignElement().getCenterPoint().x, ef.getDesignElement().getCenterPoint().y);
				ef.alignEdge(edge, xy);
				Point newLocation = ef.getDesignElement().getCenterPoint();
				p.x = newLocation.x - p.x;
				p.y = newLocation.y - p.y;
				locationOffsets.put(ef.getDesignElement().getId(), p);
			}
			renderedCanvas.endBatchUpdate();
			if(monitor != null)
				monitor.done();
			return Status.OK_STATUS;
		}

		@Override
		public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException
		{
			renderedCanvas.startBatchUpdate();
			renderedCanvas.selectPartialDocument(pdd);
			for(ComponentFrame componentFrame : selectedItems)
			{
				ElementFrame ef = (ElementFrame)componentFrame;
				ef.alignEdge(edge, xy);
			}
			renderedCanvas.endBatchUpdate();
			if(monitor != null)
				monitor.done();
			return Status.OK_STATUS;
		}

		@Override
		public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException
		{
			renderedCanvas.startBatchUpdate();
			renderedCanvas.selectPartialDocument(pdd);
			for(ComponentFrame componentFrame : selectedItems)
			{
				ElementFrame ef = (ElementFrame)componentFrame;
				Point offsets = locationOffsets.get(ef.getDesignElement().getId());
				if(offsets != null)
					ef.adjustPosition(offsets.x * -1, offsets.y * -1);
			}
			renderedCanvas.endBatchUpdate();
			if(monitor != null)
				monitor.done();
			return Status.OK_STATUS;
		}
		
	}
}