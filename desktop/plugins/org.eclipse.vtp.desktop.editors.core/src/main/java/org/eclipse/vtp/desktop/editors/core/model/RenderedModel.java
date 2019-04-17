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
package org.eclipse.vtp.desktop.editors.core.model;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.vtp.desktop.editors.themes.core.ComponentFrame;
import org.eclipse.vtp.desktop.editors.themes.core.ComponentFrameListener;
import org.eclipse.vtp.desktop.editors.themes.core.ConnectorFrame;
import org.eclipse.vtp.desktop.editors.themes.core.ElementFrame;
import org.eclipse.vtp.desktop.editors.themes.core.Theme;
import org.eclipse.vtp.desktop.editors.themes.core.ThemeManager;
import org.eclipse.vtp.desktop.model.core.IDesignDocument;
import org.eclipse.vtp.desktop.model.core.design.IDesign;
import org.eclipse.vtp.desktop.model.core.design.IDesignComponent;
import org.eclipse.vtp.desktop.model.core.design.IDesignConnector;
import org.eclipse.vtp.desktop.model.core.design.IDesignElement;
import org.eclipse.vtp.desktop.model.core.design.ModelListener;
import org.eclipse.vtp.desktop.model.core.internal.DesignDocument;
import org.eclipse.vtp.desktop.model.core.internal.DesignWriter;
import org.eclipse.vtp.desktop.model.core.internal.IDesignFilter;
import org.eclipse.vtp.desktop.model.core.internal.PartialDesignDocument;
import org.eclipse.vtp.desktop.model.core.internal.design.Design;
import org.eclipse.vtp.desktop.model.core.internal.design.DesignElement;
import org.eclipse.vtp.framework.util.XMLWriter;
import org.w3c.dom.Document;

public class RenderedModel implements ComponentFrameListener, ModelListener
{
	private IDesign uiModel;
	private List<RenderedModelListener> listeners = new ArrayList<RenderedModelListener>();
	private List<ElementFrame> elementFrames;
	private List<ConnectorFrame> connectorFrames;
	private SelectionStructure selection = null;
	private Theme theme = ThemeManager.getDefault().getCurrentTheme();
	private UndoSystem undoSystem = null;
	private IUndoContext undoContext = IOperationHistory.GLOBAL_UNDO_CONTEXT;
	private boolean batchUpdate = false;

	public RenderedModel(IDesign uiModel)
	{
		super();
		this.uiModel = uiModel;
		uiModel.addListener(this);
		elementFrames = new LinkedList<ElementFrame>();
		List<IDesignElement> uiElements = uiModel.getDesignElements();
		for(IDesignElement uiElement : uiElements)
		{
			ElementFrame elementFrame = theme.createElementFrame(uiElement);
			elementFrame.addListener(this);
			elementFrames.add(elementFrame);
		}
		connectorFrames = new LinkedList<ConnectorFrame>();
		List<IDesignConnector> uiConnectors = uiModel.getDesignConnectors();
		for(IDesignConnector uiConnector : uiConnectors)
		{
			ElementFrame source = null;
			ElementFrame destination = null;
			for(ElementFrame ef : elementFrames)
			{
				if(ef.getDesignElement().getId().equals(uiConnector.getOrigin().getId()))
					source = ef;
				if(ef.getDesignElement().getId().equals(uiConnector.getDestination().getId()))
					destination = ef;
				if(source != null && destination != null)
					break;
			}
			ConnectorFrame connectorFrame = theme.createConnectorFrame(source, destination, uiConnector);
			connectorFrame.addListener(this);
			connectorFrames.add(connectorFrame);
		}
		selection = new SelectionStructure(this);
	}
	
	public void startBatchUpdate()
	{
		batchUpdate = true;
	}
	
	public void endBatchUpdate()
	{
		endBatchUpdate(true);
	}
	
	public void endBatchUpdate(boolean signal)
	{
		batchUpdate = false;
		if(signal)
			this.fireChange();
	}
	
	public void setUndoSystem(UndoSystem undoSystem)
	{
		this.undoSystem = undoSystem;
	}
	
	public IOperationHistory getOperationHistory()
	{
		return undoSystem.getOperationHistory();
	}
	
	public void setUndoContext(IUndoContext context)
	{
		if(context == null)
			undoContext = IOperationHistory.GLOBAL_UNDO_CONTEXT;
		else
			undoContext = context;
	}
	
	public IUndoContext getUndoContext()
	{
		return this.undoContext;
	}
	
	public void enableUndo()
	{
		undoSystem.enableUndo();
	}
	
	public void disableUndo()
	{
		undoSystem.disableUndo();
	}
	
	public IDesign getUIModel()
	{
		return uiModel;
	}
	
	public void initializeGraphics(GC gc, Map<String, Object> resourceMap)
	{
		for(ElementFrame ef : elementFrames)
		{
			ef.initializeGraphics(gc, resourceMap);
		}
		for(ConnectorFrame cf : connectorFrames)
		{
			cf.initializeGraphics(gc, resourceMap);
		}
	}
	
	public ElementFrame findElementAt(int x, int y)
	{
		for(int i = elementFrames.size() -1; i > -1; i--)
		{
			ElementFrame df = elementFrames.get(i);
			if(df.touchesComponent(x, y))
			{
				return df;
			}
		}
		return null;
	}

	public ConnectorFrame findConnectorAt(int x, int y)
	{
		for(ConnectorFrame cf : connectorFrames)
		{
			if(cf.touchesComponent(x, y))
			{
				return cf;
			}
		}
		return null;
	}
	
	public ComponentFrame findComponentAt(int x, int y)
	{
		ComponentFrame cf = findElementAt(x, y);
		if(cf == null)
			cf = findConnectorAt(x, y);
		return cf;
	}
	
	private boolean adding = false;
	
	public ElementFrame addElement(DesignElement element, int x, int y)
	{
		adding = true;
		uiModel.addDesignElement(element);
		adding = false;
		element.setCenterPoint(x, y);
		ElementFrame ret = theme.createElementFrame(element);
		ret.addListener(this);
		elementFrames.add(ret);
		this.fireChange();
		return ret;
	}
	
	public ConnectorFrame connectElements(ElementFrame source, ElementFrame destination)
	{
		adding = true;
		IDesignConnector connector = uiModel.createDesignConnector(source.getDesignElement(), destination.getDesignElement());
		adding = false;
		ConnectorFrame ret = theme.createConnectorFrame(source, destination, connector);
		ret.addListener(this);
		connectorFrames.add(ret);
		this.fireChange();
		return ret;
	}
	
	public void paintCanvas(GC gc, Map<String, Object> resourceMap, int flags)
	{
		for(ConnectorFrame cf : connectorFrames)
		{
			cf.renderFrame(gc, 0, flags, resourceMap);
		}
		for(ElementFrame ef : elementFrames)
		{
			ef.renderFrame(gc, 0, flags, resourceMap);
		}
	}

	public SelectionResult selectAt(int x, int y, boolean additive)
	{
		ComponentFrame cof = findComponentAt(x, y);
		return selection.select(cof, additive);
	}

	public void selectRegion(Rectangle rect, boolean additive)
	{
		List<ElementFrame> selectList = new LinkedList<ElementFrame>();
		for(ElementFrame ef : elementFrames)
		{
			if(ef.touchesElement(rect))
			{
				selectList.add(ef);
			}
		}
		selection.select(selectList, additive);
	}
	
	public void selectPartialDocument(PartialDesignDocument pdd)
	{
		selection.clear();
outer:	for(IDesignElement de : pdd.getMainDesign().getDesignElements())
		{
			for(ElementFrame ef : elementFrames)
			{
				if(ef.getDesignComponent().getId().equals(de.getId()))
				{
					selection.select(ef, true);
					continue outer;
				}
			}
		}
		if(pdd.getMainDesign().getDesignElements().size() == 0 && pdd.getMainDesign().getDesignConnectors().size() > 0) //single connector selection
		{
			for(ConnectorFrame ef : connectorFrames)
			{
				if(ef.getDesignComponent().getId().equals(pdd.getMainDesign().getDesignConnectors().get(0).getId()))
				{
					selection.select(ef, true);
					return;
				}
			}
		}
	}
	
	public void select(ComponentFrame componentFrame, boolean additive)
	{
		selection.select(componentFrame, additive);
	}

	public SelectionStructure getSelection()
	{
		return selection;
	}
	
	public void deleteSelectedItems()
	{
		try
		{
			IDesignDocument designDocument = uiModel.getDocument();
			Document document = getSelectionDocument(true);
			PartialDesignDocument pdd = new PartialDesignDocument(designDocument, (Design)uiModel, document);
			DeleteOperation delo = new DeleteOperation(pdd);
			delo.addContext(undoContext);
			getOperationHistory().execute(delo, null, null);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void postAddedItem()
	{
		try
		{
			IDesignDocument designDocument = uiModel.getDocument();
			Document document = getSelectionDocument(true);
			PartialDesignDocument pdd = new PartialDesignDocument(designDocument, (Design)uiModel, document);
			AddComponentOperation delo = new AddComponentOperation(pdd);
			delo.addContext(undoContext);
			getOperationHistory().execute(delo, null, null);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	Document getSelectionDocument(final boolean includeTertiaryConnectors)
	{
		IDesignFilter selectionFilter = new IDesignFilter()
		{
			List<IDesignComponent> components = new ArrayList<IDesignComponent>();
			{
				for(ComponentFrame cf : selection.getSelectedItems())
				{
					components.add(cf.getDesignComponent());
				}
				for(ComponentFrame cf : selection.getSecondarySelectedItems())
				{
					components.add(cf.getDesignComponent());
				}
				if(includeTertiaryConnectors)
				{
					for(ComponentFrame cf : selection.getTertiarySelectedItems())
					{
						components.add(cf.getDesignComponent());
					}
				}
			}

			public boolean matches(IDesignComponent component)
			{
				for(IDesignComponent dc : components)
				{
					if(dc.getId().equals(component.getId()))
						return true;
				}
				return false;
			}
		};
		try
		{
			DesignWriter writer = new DesignWriter();
			//build document contents
			DocumentBuilderFactory factory =
				DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.getDOMImplementation().createDocument(null, "design-fragment", null);
			org.w3c.dom.Element rootElement = document.getDocumentElement();
			rootElement.setAttribute("xml-version", "4.0.0");
			IDesignDocument designDocument = getUIModel().getDocument();
			if(!uiModel.equals(designDocument.getMainDesign()))
			{
				rootElement.setAttribute("dialog-only", "true");
				writer.writeDesign(rootElement, (Design)uiModel, selectionFilter);
			}
			else
			{
				writer.writeDesign(rootElement, (Design)designDocument.getMainDesign(), selectionFilter);
				org.w3c.dom.Element dialogsElement = rootElement.getOwnerDocument().createElement("dialogs");
				rootElement.appendChild(dialogsElement);
				for(IDesign dialogDesign : designDocument.getDialogDesigns())
				{
					List<ComponentFrame> items = selection.getSelectedItems();
					for(ComponentFrame cf : items)
					{
						if(cf.getDesignComponent().getId().equals(dialogDesign.getDesignId()))
						{
							writer.writeDesign(dialogsElement, (Design)dialogDesign);
							break;
						}
					}
				}
			}
			return document;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public void copySelectedItems(Clipboard clipboard)
	{
		try
		{
			Document document = getSelectionDocument(false);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			TransformerFactory transfactory = TransformerFactory.newInstance();
			Transformer t = transfactory.newTransformer();
			t.setOutputProperty(OutputKeys.METHOD, "xml");
			t.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			t.setOutputProperty(OutputKeys.INDENT, "yes");
			t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			t.transform(new DOMSource(document), new XMLWriter(baos).toXMLResult());
			clipboard.setContents(new Object[] {baos.toString()}, new Transfer[]{TextTransfer.getInstance()});
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public List<ConnectorFrame> getConnectorFrames()
	{
		return Collections.unmodifiableList(connectorFrames);
	}

	public List<ElementFrame> getElementFrames()
	{
		return Collections.unmodifiableList(elementFrames);
	}

	public void componentFrameChanged(ComponentFrame componentFrame)
	{
		if(!batchUpdate)
			fireChange();
	}
	
	public void addListener(RenderedModelListener listener)
	{
		listeners.remove(listener);
		listeners.add(listener);
	}
	
	public void removeListener(RenderedModelListener listener)
	{
		listeners.remove(listener);
	}
	
	public void fireChange()
	{
		for(int i = 0; i < listeners.size(); i++)
		{
			listeners.get(i).renderedModelChanged(this);
		}
	}

	protected void fireFormatChange()
	{
		for(int i = 0; i < listeners.size(); i++)
		{
			listeners.get(i).renderedModelFormatChanged(this);
		}
	}
	
	public void componentFrameDeleted(ComponentFrame componentFrame)
	{
		System.out.println("component frame deleted: " + componentFrame.getDesignComponent().getId());
		if(componentFrame instanceof ElementFrame)
		{
			this.elementFrames.remove(componentFrame);
		}
		else
			this.connectorFrames.remove(componentFrame);
	}

	public void componentFrameRepaintRequested(ComponentFrame componentFrame)
	{
		selection.validateSelection();
		fireChange();
	}

	public void orientationChanged(IDesign canvas)
    {
		for(ElementFrame elementFrame : elementFrames)
        {
	        Rectangle bounds = elementFrame.getBounds();
	        int xoff = 0;
	        int yoff = 0;
	        if(bounds.x + bounds.width > canvas.getWidth())
	        	xoff = canvas.getWidth() - (bounds.x + bounds.width + 5);
	        if(bounds.y + bounds.height > canvas.getHeight())
	        	yoff = canvas.getHeight() - (bounds.y + bounds.height + 5);
	        if(xoff != 0 || yoff != 0)
	        	elementFrame.adjustPosition(xoff, yoff);
        }
		selection.validateSelection();
		fireFormatChange();
    }

	public void paperSizeChanged(IDesign canvas)
    {
		for(ElementFrame elementFrame : elementFrames)
        {
	        Rectangle bounds = elementFrame.getBounds();
	        int xoff = 0;
	        int yoff = 0;
	        if(bounds.x + bounds.width > canvas.getWidth())
	        	xoff = canvas.getWidth() - (bounds.x + bounds.width + 5);
	        if(bounds.y + bounds.height > canvas.getHeight())
	        	yoff = canvas.getHeight() - (bounds.y + bounds.height + 5);
	        if(xoff != 0 || yoff != 0)
	        	elementFrame.adjustPosition(xoff, yoff);
        }
		selection.validateSelection();
		fireFormatChange();
    }
	
	public void nameChanged(IDesign design)
	{
		fireChange();
	}
	
	public class ThemeListener implements IPropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent event)
		{
			if(event.getProperty().equals("CurrentTheme"))
			{
				
			}
		}
	}

	public void componentAdded(IDesign model, IDesignComponent component)
	{
		if(!adding)
		{
			if(component instanceof IDesignElement)
			{
				ElementFrame elementFrame = theme.createElementFrame((IDesignElement)component);
				elementFrame.addListener(this);
				elementFrames.add(elementFrame);
			}
			else
			{
				IDesignConnector uiConnector = (IDesignConnector)component;
				ElementFrame source = null;
				ElementFrame destination = null;
				for(ElementFrame ef : elementFrames)
				{
					if(ef.getDesignElement().getId().equals(uiConnector.getOrigin().getId()))
						source = ef;
					if(ef.getDesignElement().getId().equals(uiConnector.getDestination().getId()))
						destination = ef;
					if(source != null && destination != null)
						break;
				}
				ConnectorFrame connectorFrame = theme.createConnectorFrame(source, destination, uiConnector);
				connectorFrame.addListener(this);
				connectorFrames.add(connectorFrame);
			}
			this.fireChange();
		}
	}

	public void componentRemoved(IDesign model, IDesignComponent component)
	{
		System.out.println("component removed: " + component.getId());
	}
	
	public class DeleteOperation extends AbstractOperation
	{
		PartialDesignDocument pdd = null;

		public DeleteOperation(PartialDesignDocument pdd)
		{
			super("Delete");
			this.pdd = pdd;
		}

		@Override
		public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException
		{
			((DesignDocument)uiModel.getDocument()).reverse(uiModel, pdd.clone());
			selection.clear();
			fireChange();
			if(monitor != null)
				monitor.done();
			return Status.OK_STATUS;
		}

		@Override
		public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException
		{
			System.out.println("redoing delete operation");
			((DesignDocument)uiModel.getDocument()).reverse(uiModel, pdd.clone());
			selection.clear();
			fireChange();
			if(monitor != null)
				monitor.done();
			return Status.OK_STATUS;
		}

		@Override
		public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException
		{
			System.out.println("undoing delete operation");
			PartialDesignDocument clone = pdd.clone();
			((DesignDocument)uiModel.getDocument()).merge(uiModel, clone, false);
			selectPartialDocument(clone);
			fireChange();
			if(monitor != null)
				monitor.done();
			return Status.OK_STATUS;
		}
	}
	
	public class AddComponentOperation extends AbstractOperation
	{
		PartialDesignDocument pdd = null;

		public AddComponentOperation(PartialDesignDocument pdd)
		{
			super("Add");
			this.pdd = pdd;
		}

		@Override
		public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException
		{
			//the initial work is already done elsewhere.  also pop-on-drop items
			//are supported because the snapshot is after that process has been completed
			if(monitor != null)
				monitor.done();
			return Status.OK_STATUS;
		}

		@Override
		public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException
		{
			System.out.println("undoing add operation");
			PartialDesignDocument clone = pdd.clone();
			((DesignDocument)uiModel.getDocument()).merge(uiModel, clone, false);
			selectPartialDocument(clone);
			fireChange();
			if(monitor != null)
				monitor.done();
			return Status.OK_STATUS;
		}

		@Override
		public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException
		{
			System.out.println("redoing add operation");
			((DesignDocument)uiModel.getDocument()).reverse(uiModel, pdd.clone());
			selection.clear();
			fireChange();
			if(monitor != null)
				monitor.done();
			return Status.OK_STATUS;
		}
		
	}
}
