package org.eclipse.vtp.desktop.model.core.internal;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.resources.IFile;
import org.eclipse.swt.graphics.Image;
import org.eclipse.vtp.desktop.model.core.IDesignDocument;
import org.eclipse.vtp.desktop.model.core.IDesignDocumentListener;
import org.eclipse.vtp.desktop.model.core.IDesignItemContainer;
import org.eclipse.vtp.desktop.model.core.IWorkflowEntry;
import org.eclipse.vtp.desktop.model.core.IWorkflowExit;
import org.eclipse.vtp.desktop.model.core.IWorkflowProject;
import org.eclipse.vtp.desktop.model.core.IWorkflowReference;
import org.eclipse.vtp.desktop.model.core.IWorkflowResource;
import org.eclipse.vtp.desktop.model.core.design.IDesign;
import org.eclipse.vtp.desktop.model.core.design.IDesignComponent;
import org.eclipse.vtp.desktop.model.core.design.IDesignEntryPoint;
import org.eclipse.vtp.desktop.model.core.design.IDesignExitPoint;
import org.eclipse.vtp.desktop.model.core.event.IRefreshListener;
import org.eclipse.vtp.desktop.model.core.internal.design.Design;
import org.eclipse.vtp.desktop.model.core.internal.design.DesignElement;
import org.eclipse.vtp.desktop.model.core.internal.design.ElementResolutionVisitor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


public class PartialDesignDocument implements IDesignDocument, ElementResolutionVisitor
{
	private boolean dialogOnly = false;
	private List<Design> dialogs = new LinkedList<Design>();
	private Design mainDesign = null;
	private IDesignDocument backingDocument = null;
	private Design baseDesign = null;

	public PartialDesignDocument(IDesignDocument baseDocument, Design baseDesign, Document document)
	{
		this.backingDocument = baseDocument;
		this.baseDesign = baseDesign;
		DesignParser parser = new DesignParser();
		Element rootElement = document.getDocumentElement();
		if(rootElement.getAttribute("dialog-only").equals("true"))
		{
			dialogOnly = true;
			NodeList mainList = rootElement.getElementsByTagName("workflow");
			if(mainList.getLength() > 0)
			{
				org.w3c.dom.Element mainElement = (org.w3c.dom.Element)mainList.item(0);
				mainDesign = parser.parseDesign(this, baseDesign, mainElement, this, true);
			}
		}
		else
		{
			NodeList dialogsList = rootElement.getElementsByTagName("dialogs");
			if(dialogsList.getLength() > 0)
			{
				NodeList dialogList = ((org.w3c.dom.Element)dialogsList.item(0)).getElementsByTagName("workflow");
				for(int i = 0; i < dialogList.getLength(); i++)
				{
					org.w3c.dom.Element dialogElement = (org.w3c.dom.Element)dialogList.item(i);
					dialogs.add(parser.parseDesign(this, dialogElement, null, true));
				}
			}
			NodeList mainList = rootElement.getElementsByTagName("workflow");
			if(mainList.getLength() > 0)
			{
				org.w3c.dom.Element mainElement = (org.w3c.dom.Element)mainList.item(0);
				mainDesign = parser.parseDesign(this, baseDesign, mainElement, this, true);
			}
		}
	}
	
	public Document toDocument()
	{
		Document document = null;
		try
		{
			DesignWriter writer = new DesignWriter();
			//build document contents
			DocumentBuilderFactory factory =
				DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			document = builder.getDOMImplementation().createDocument(null, "design-fragment", null);
			org.w3c.dom.Element rootElement = document.getDocumentElement();
			rootElement.setAttribute("xml-version", "4.0.0");
			IDesignFilter filter = new IDesignFilter()
			{
				public boolean matches(IDesignComponent component)
				{
					return true;
				}
			};
			if(dialogOnly)
			{
				rootElement.setAttribute("dialog-only", "true");
				writer.writeDesign(rootElement, mainDesign, filter);
			}
			else
			{
				writer.writeDesign(rootElement, mainDesign, filter);

				org.w3c.dom.Element dialogsElement = rootElement.getOwnerDocument().createElement("dialogs");
				rootElement.appendChild(dialogsElement);
				for(IDesign dialogDesign : dialogs)
				{
					writer.writeDesign(dialogsElement, (Design)dialogDesign);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return document;
	}
	
	public PartialDesignDocument clone()
	{
		return new PartialDesignDocument(backingDocument, baseDesign, toDocument());
	}
	
	public void forceNewIds()
	{
		for(Design dialogDesign : dialogs)
		{
			dialogDesign.forceNewIds();
		}
		mainDesign.forceNewIds();
	}
	
	public IDesign getMainDesign()
	{
		return mainDesign;
	}

	public List<IDesign> getDialogDesigns()
	{
		return new LinkedList<IDesign>(dialogs);
	}
	
	public void removeAllDialogs()
	{
		dialogs.clear();
	}

	public IDesign getDialogDesign(String id)
	{
		for(Design dialog : dialogs)
		{
			if(dialog.getDesignId().equals(id))
				return dialog;
		}
		return null;
	}

	@Override
	public void resolveElement(DesignElement element)
	{
	}

	@Override
	public String getName()
	{
		return null;
	}

	@Override
	public IWorkflowResource getParent()
	{
		return null;
	}

	@Override
	public void addRefreshListener(IRefreshListener listener)
	{
	}

	@Override
	public void removeRefreshListener(IRefreshListener listener)
	{
	}

	@Override
	public void deferEvents()
	{
	}

	@Override
	public void resumeEvents()
	{
	}

	@Override
	public void refresh()
	{
	}

	@Override
	public IWorkflowProject getProject()
	{
		return backingDocument.getProject();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(Class adapter)
	{
		return null;
	}

	@Override
	public IDesignItemContainer getParentDesignContainer()
	{
		return null;
	}

	@Override
	public boolean hasWorkflowEntry()
	{
		return false;
	}

	@Override
	public List<IWorkflowEntry> getWorkflowEntries()
	{
		return null;
	}

	@Override
	public List<IWorkflowEntry> getUpStreamWorkflowEntries(
		IWorkflowExit workflowExit)
	{
		return null;
	}

	@Override
	public List<IWorkflowEntry> getUpStreamWorkflowEntries(
		IDesignExitPoint designExit)
	{
		return null;
	}

	@Override
	public List<IWorkflowExit> getWorkflowExits()
	{
		return null;
	}
	
	public List<IWorkflowReference> getWorkflowReferences()
	{
		return null;
	}

	@Override
	public List<IWorkflowExit> getDownStreamWorkflowExits(
		IWorkflowEntry workflowEntry)
	{
		return null;
	}

	@Override
	public List<IWorkflowExit> getDownStreamWorkflowExits(
		IDesignEntryPoint designEntry)
	{
		return null;
	}

	@Override
	public List<IDesignEntryPoint> getDesignEntryPoints()
	{
		return null;
	}

	@Override
	public List<IDesignEntryPoint> getUpStreamDesignEntries(
		IWorkflowExit workflowExit)
	{
		return null;
	}

	@Override
	public List<IDesignEntryPoint> getUpStreamDesignEntries(
		IDesignExitPoint designExit)
	{
		return null;
	}

	@Override
	public List<IDesignExitPoint> getDesignExitPoints()
	{
		return null;
	}

	@Override
	public List<IDesignExitPoint> getDownStreamDesignExits(
		IWorkflowEntry workflowEntry)
	{
		return null;
	}

	@Override
	public List<IDesignExitPoint> getDownStreamDesignExits(
		IDesignEntryPoint designEntry)
	{
		return null;
	}

	@Override
	public void becomeWorkingCopy()
	{
	}

	@Override
	public void becomeWorkingCopy(boolean followExternalReferences)
	{
	}

	@Override
	public void discardWorkingCopy()
	{
	}

	@Override
	public void restoreWorkingCopy()
	{
	}

	@Override
	public void commitWorkingCopy()
	{
	}

	@Override
	public boolean isWorkingCopy()
	{
		return false;
	}

	@Override
	public IDesign addDialogDesign(String id, URL templateURL)
	{
		return null;
	}

	@Override
	public IFile getUnderlyingFile()
	{
		return null;
	}

	@Override
	public Image getDesignThumbnail(String designId)
	{
		return null;
	}

	@Override
	public void addDocumentListener(IDesignDocumentListener listener)
	{
	}

	@Override
	public void removeDocumentListener(IDesignDocumentListener listener)
	{
	}
}