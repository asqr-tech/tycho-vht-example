/**  
 * 
 */
package org.eclipse.vtp.desktop.model.core.internal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.vtp.desktop.model.core.IDesignDocument;
import org.eclipse.vtp.desktop.model.core.IDesignDocumentListener;
import org.eclipse.vtp.desktop.model.core.IDesignItemContainer;
import org.eclipse.vtp.desktop.model.core.IWorkflowEntry;
import org.eclipse.vtp.desktop.model.core.IWorkflowExit;
import org.eclipse.vtp.desktop.model.core.IWorkflowReference;
import org.eclipse.vtp.desktop.model.core.IWorkflowResource;
import org.eclipse.vtp.desktop.model.core.design.IDesign;
import org.eclipse.vtp.desktop.model.core.design.IDesignConnector;
import org.eclipse.vtp.desktop.model.core.design.IDesignElement;
import org.eclipse.vtp.desktop.model.core.design.IDesignEntryPoint;
import org.eclipse.vtp.desktop.model.core.design.IDesignExitPoint;
import org.eclipse.vtp.desktop.model.core.design.Variable;
import org.eclipse.vtp.desktop.model.core.internal.design.Design;
import org.eclipse.vtp.desktop.model.core.internal.design.DesignConnector;
import org.eclipse.vtp.desktop.model.core.internal.design.DesignElement;
import org.eclipse.vtp.desktop.model.core.internal.design.DesignTraversalHelper;
import org.eclipse.vtp.desktop.model.core.internal.design.ElementResolutionVisitor;
import org.eclipse.vtp.framework.util.XMLWriter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author trip
 *
 */
public class DesignDocument extends WorkflowResource implements IDesignDocument, ElementResolutionVisitor
{
	private static final String HASHPREFIX = "DesignDocument";
	private IDesignItemContainer parent = null;
	private IFile file = null;
	private boolean isWorking = false;
	private boolean externalReferencesFollowed = false;
	private List<Design> dialogs = new LinkedList<Design>();
	private Design mainDesign = null;
	private List<IDesignDocumentListener> listeners = new LinkedList<IDesignDocumentListener>();

	/**
	 * 
	 */
	public DesignDocument(IDesignItemContainer container, IFile file)
	{
		super();
		this.parent = container;
		this.file = file;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.IWorkflowResource#getName()
	 */
	public String getName()
	{
		return file.getName();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.IWorkflowResource#getParent()
	 */
	public IWorkflowResource getParent()
	{
		return parent;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.internal.WorkflowResource#getObjectId()
	 */
	@Override
	protected String getObjectId()
	{
		return file.getFullPath().toPortableString();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.IDesignDocument#getParentDesignContainer()
	 */
	public IDesignItemContainer getParentDesignContainer()
	{
		return parent;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.IDesignDocument#getUnderlyingFile()
	 */
	public IFile getUnderlyingFile()
	{
		return file;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.IDesignDocument#hasWorkingCopy()
	 */
//	@Override
//	public boolean hasWorkingCopy()
//	{
//		return false;
//	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.IDesignDocument#isWorkingCopy()
	 */
	public synchronized boolean isWorkingCopy()
	{
		return isWorking;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.IDesignDocument#becomeWorkingCopy()
	 */
	public void becomeWorkingCopy()
	{
		becomeWorkingCopy(true);
	}
	
	public synchronized void becomeWorkingCopy(boolean followExternalReferences)
	{
		if(!isWorking || followExternalReferences != externalReferencesFollowed)
		{
			externalReferencesFollowed = followExternalReferences;
			isWorking = true;
			mainDesign = null;
			dialogs.clear();
			DesignParser designParser = new DesignParser();
			try
			{
				DocumentBuilderFactory factory =
					DocumentBuilderFactory.newInstance();
				factory.setNamespaceAware(true);
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document document = builder.parse(file.getContents());
				org.w3c.dom.Element rootElement = document.getDocumentElement();
//				boolean neededConversion = false;
				//load dialogs
				NodeList dialogsList = rootElement.getElementsByTagName("dialogs");
				if(dialogsList.getLength() > 0)
				{
					NodeList dialogList = ((org.w3c.dom.Element)dialogsList.item(0)).getElementsByTagName("workflow");
					for(int i = 0; i < dialogList.getLength(); i++)
					{
						org.w3c.dom.Element dialogElement = (org.w3c.dom.Element)dialogList.item(i);
						dialogs.add(designParser.parseDesign(this, dialogElement, null, followExternalReferences));
					}
				}
				NodeList mainList = rootElement.getElementsByTagName("workflow");
				if(mainList.getLength() > 0)
				{
					org.w3c.dom.Element mainElement = (org.w3c.dom.Element)mainList.item(0);
					mainDesign = designParser.parseDesign(this, mainElement, this, followExternalReferences);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.IDesignDocument#commitWorkingCopy()
	 */
	public synchronized void commitWorkingCopy() throws Exception
	{
		DesignWriter writer = new DesignWriter();
		//build document contents
		DocumentBuilderFactory factory =
			DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.getDOMImplementation().createDocument(null, "design-document", null);
		org.w3c.dom.Element rootElement = document.getDocumentElement();
		rootElement.setAttribute("xml-version", "4.0.0");
		writer.writeDesign(rootElement, mainDesign);

		org.w3c.dom.Element dialogsElement = rootElement.getOwnerDocument().createElement("dialogs");
		rootElement.appendChild(dialogsElement);
		for(IDesign dialogDesign : getDialogDesigns())
		{
			writer.writeDesign(dialogsElement, (Design)dialogDesign);
		}
		
		//write document to file
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		TransformerFactory transfactory = TransformerFactory.newInstance();
		Transformer t = transfactory.newTransformer();
		t.setOutputProperty(OutputKeys.METHOD, "xml");
		t.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		t.setOutputProperty(OutputKeys.INDENT, "yes");
		t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		t.transform(new DOMSource(document), new XMLWriter(baos).toXMLResult());
		file.setContents(new ByteArrayInputStream(baos.toByteArray()), true, true, null);
	}
	
	public synchronized boolean canMergeAll(IDesign target, PartialDesignDocument partialDocument)
	{
		if(!mainDesign.equals(target) && !dialogs.contains(target))
		{
			System.err.println("trying to merge into design not in this document");
			return false;
		}
		IDesign mergeMain = partialDocument.getMainDesign();
		List<IDesignElement> mergeElements = mergeMain.getDesignElements();
		for(IDesignElement mergeElement : mergeElements)
		{
			if(!mergeElement.canBeContainedBy(target))
				return false;
		}
		return true;
	}
	
	public synchronized void merge(IDesign target, PartialDesignDocument partialDocument)
	{
		merge(target, partialDocument, true);
	}
	
	public synchronized void merge(IDesign target, PartialDesignDocument partialDocument, boolean overrideIds)
	{
		try
		{
			System.out.println("merging partial document: " + partialDocument);
			if(!mainDesign.equals(target) && !dialogs.contains(target))
			{
				System.err.println("trying to merge into design not in this document");
				return;
			}
			if(overrideIds)
				partialDocument.forceNewIds();
			if(mainDesign.equals(target))
			{
				List<IDesign> mergeDialogs = partialDocument.getDialogDesigns();
				for(IDesign mergeDialog : mergeDialogs)
				{
					Design md = (Design)mergeDialog;
					dialogs.add(md);
					md.setDocument(this);
					for(IDesignDocumentListener listener : listeners)
					{
						listener.dialogDesignAdded(this, md);
					}
				}
			}
			else
			{
				partialDocument.removeAllDialogs();
			}
			IDesign mergeMain = partialDocument.getMainDesign();
			List<IDesignElement> mergeElements = mergeMain.getDesignElements();
			System.out.println("merging " + mergeElements.size() + " elements");
			List<IDesignElement> droppedElements = new LinkedList<IDesignElement>();
			for(IDesignElement mergeElement : mergeElements)
			{
				System.out.println("processing element: [" + mergeElement.getId() + "] " + mergeElement.getName());
				if(mergeElement.canBeContainedBy(target))
				{
					System.out.println("adding to target design");
					target.addDesignElement(mergeElement);
				}
				else
				{
					System.out.println("skipping element as it cannot be contained by target design");
					droppedElements.add(mergeElement);
				}
			}
			List<IDesignConnector> droppedConnectors = new LinkedList<IDesignConnector>();
			for(IDesignConnector mergeConnector : mergeMain.getDesignConnectors())
			{
				IDesignElement source = null;
				IDesignElement dest = null;
				for(IDesignElement element : target.getDesignElements())
				{
					if(element.getId().equals(mergeConnector.getOrigin().getId()))
						source = element;
					if(element.getId().equals(mergeConnector.getDestination().getId()))
						dest = element;
					if(source != null && dest != null)
						break;
				}
				if(source != null && dest != null)
					((Design)target).addDesignConnector((DesignConnector)mergeConnector);
				else
					droppedConnectors.add(mergeConnector);
			}
			for(IDesignElement droppedElement : droppedElements)
			{
				partialDocument.getMainDesign().removeDesignElement(droppedElement);
			}
			for(IDesignConnector droppedConnector : droppedConnectors)
			{
				partialDocument.getMainDesign().removeDesignConnector(droppedConnector);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void reverse(IDesign target, PartialDesignDocument partialDocument)
	{
		if(!mainDesign.equals(target) && !dialogs.contains(target))
		{
			System.err.println("trying to merge into design not in this document");
			return;
		}
		for(IDesignConnector remConnector : partialDocument.getMainDesign().getDesignConnectors())
		{
			System.out.println("processing connector: [" + remConnector.getId() + "]");
			IDesignConnector rc = target.getDesignConnector(remConnector.getId());
			target.removeDesignConnector(rc);
		}
		for(IDesignElement targetElement : target.getDesignElements())
		{
			System.out.println("target element: [" + targetElement.getId() + "] " + targetElement.getName());
		}
		for(IDesignElement remElement : partialDocument.getMainDesign().getDesignElements())
		{
			System.out.println("processing element: [" + remElement.getId() + "] " + remElement.getName());
			IDesignElement re = target.getDesignElement(remElement.getId());
			System.out.println("found element: [" + re.getId() + "] " + re.getName());
			target.removeDesignElement(re);
		}
		for(IDesign dialogDesign : partialDocument.getDialogDesigns())
		{
			this.removeDialogDesign(dialogDesign.getDesignId());
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.IDesignDocument#restoreWorkingCopy()
	 */
	public synchronized void restoreWorkingCopy()
	{
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.IDesignDocument#discardWorkingCopy()
	 */
	
	public synchronized void discardWorkingCopy()
	{
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.IDesignDocument#getDesignThumbnail()
	 */
	
	public Image getDesignThumbnail(String designId)
	{
		File workingPath = file.getProject().getWorkingLocation("org.eclipse.vtp.model.core").toFile();
		File iconPath = new File(workingPath, file.getProjectRelativePath().toString());
		File iconFile = new File(iconPath, designId + ".jpg");
		if(iconFile.exists())
		{
			try
			{
				FileInputStream fis = new FileInputStream(iconFile);
				Image icon = new Image(Display.getCurrent(), fis);
				fis.close();
				return icon;
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.IDesignDocument#getMainDesign()
	 */
	
	public IDesign getMainDesign()
	{
		return mainDesign;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.IDesignDocument#getDialogDesigns()
	 */
	
	public List<IDesign> getDialogDesigns()
	{
		return new LinkedList<IDesign>(dialogs);
	}
	
	public IDesign addDialogDesign(String id, URL templateURL)
	{
		DesignParser designParser = new DesignParser();
		Design dialogDesign = null;
		try
		{
			DocumentBuilderFactory factory =
				DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(templateURL.openStream());
			Element rootElement = document.getDocumentElement();
			NodeList mainList = rootElement.getElementsByTagName("workflow");
			if(mainList.getLength() > 0)
			{
				Element mainElement = (Element)mainList.item(0);
				dialogDesign = designParser.parseDesign(this, mainElement, this, true);
				dialogDesign.setDesignId(id);
				dialogs.add(dialogDesign);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		for(IDesignDocumentListener listener : listeners)
		{
			listener.dialogDesignAdded(this, dialogDesign);
		}
		return dialogDesign;
	}
	
	public void removeDialogDesign(String id)
	{
		for(int i = 0; i < dialogs.size(); i++)
		{
			if(dialogs.get(i).getDesignId().equals(id))
			{
				dialogs.remove(i);
				break;
			}
		}
		for(IDesignDocumentListener listener : listeners)
		{
			listener.dialogDesignRemoved(this, id);
		}
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
	
	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.internal.WorkflowResource#getAdapter(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapterClass)
	{
		if(IResource.class.isAssignableFrom(adapterClass) && adapterClass.isAssignableFrom(file.getClass()))
			return file;
		return super.getAdapter(adapterClass);
	}
	
	public boolean equals(Object obj)
	{
		if(obj instanceof DesignDocument)
		{
			return file.equals(((DesignDocument)obj).getUnderlyingFile());
		}
		return false;
	}

	public int hashCode()
	{
		return (HASHPREFIX + file.toString()).hashCode();
	}

	
	public void resolveElement(DesignElement element)
	{
		
	}

	
	public List<IDesignEntryPoint> getDesignEntryPoints()
	{
		if(mainDesign != null)
			return DesignTraversalHelper.getDesignElements(mainDesign, IDesignEntryPoint.class);
		WorkflowIndex index = WorkflowIndexService.getInstance().getIndex(getProject().getUnderlyingProject());
		return index.getDesignEntries(this);
	}

	
	public List<IDesignEntryPoint> getUpStreamDesignEntries(
		IWorkflowExit workflowExit)
	{
		if(mainDesign != null)
			return DesignTraversalHelper.getUpStreamDesignElements((IDesignElement)workflowExit.getAdapter(IDesignElement.class), IDesignEntryPoint.class);
		WorkflowIndex index = WorkflowIndexService.getInstance().getIndex(getProject().getUnderlyingProject());
		return index.getUpstreamDesignEntries(this, workflowExit);
	}

	
	public List<IDesignEntryPoint> getUpStreamDesignEntries(
		IDesignExitPoint designExit)
	{
		if(mainDesign != null)
			return DesignTraversalHelper.getUpStreamDesignElements((IDesignElement)designExit.getAdapter(IDesignElement.class), IDesignEntryPoint.class);
		WorkflowIndex index = WorkflowIndexService.getInstance().getIndex(getProject().getUnderlyingProject());
		return index.getUpstreamDesignEntries(this, designExit);
	}

	
	public List<IDesignExitPoint> getDesignExitPoints()
	{
		if(mainDesign != null)
			return DesignTraversalHelper.getDesignElements(mainDesign, IDesignExitPoint.class);
		WorkflowIndex index = WorkflowIndexService.getInstance().getIndex(getProject().getUnderlyingProject());
		return index.getDesignExits(this);
	}

	
	public List<IDesignExitPoint> getDownStreamDesignExits(
		IWorkflowEntry workflowEntry)
	{
		if(mainDesign != null)
			return DesignTraversalHelper.getDownStreamDesignElements((IDesignElement)workflowEntry.getAdapter(IDesignElement.class), IDesignExitPoint.class);
		WorkflowIndex index = WorkflowIndexService.getInstance().getIndex(getProject().getUnderlyingProject());
		return index.getDownstreamDesignExits(this, workflowEntry);
	}

	
	public List<IDesignExitPoint> getDownStreamDesignExits(
		IDesignEntryPoint designEntry)
	{
		if(mainDesign != null)
			return DesignTraversalHelper.getDownStreamDesignElements((IDesignElement)designEntry.getAdapter(IDesignElement.class), IDesignExitPoint.class);
		WorkflowIndex index = WorkflowIndexService.getInstance().getIndex(getProject().getUnderlyingProject());
		return index.getDownstreamDesignExits(this, designEntry);
	}

	
	public boolean hasWorkflowEntry()
	{
		return getWorkflowEntries().size() > 0;
	}
	
	
	public List<IWorkflowEntry> getWorkflowEntries()
	{
		if(mainDesign != null)
			return DesignTraversalHelper.getDesignElements(mainDesign, IWorkflowEntry.class);
		WorkflowIndex index = WorkflowIndexService.getInstance().getIndex(getProject().getUnderlyingProject());
		return index.getWorkflowEntries(this);
	}
	
	
	public List<IWorkflowEntry> getUpStreamWorkflowEntries(
		IDesignExitPoint designExit)
		{
		if(mainDesign != null)
			return DesignTraversalHelper.getUpStreamDesignElements((IDesignElement)designExit.getAdapter(IDesignElement.class), IWorkflowEntry.class);
		WorkflowIndex index = WorkflowIndexService.getInstance().getIndex(getProject().getUnderlyingProject());
		return index.getUpstreamWorkflowEntries(this, designExit);
		}
	
	
	public List<IWorkflowEntry> getUpStreamWorkflowEntries(
		IWorkflowExit workflowExit)
	{
		if(mainDesign != null)
			return DesignTraversalHelper.getUpStreamDesignElements((IDesignElement)workflowExit.getAdapter(IDesignElement.class), IWorkflowEntry.class);
		WorkflowIndex index = WorkflowIndexService.getInstance().getIndex(getProject().getUnderlyingProject());
		return index.getUpstreamWorkflowEntries(this, workflowExit);
	}
	
	public List<IWorkflowExit> getWorkflowExits()
	{
		if(mainDesign != null)
			return DesignTraversalHelper.getDesignElements(mainDesign, IWorkflowExit.class);
		WorkflowIndex index = WorkflowIndexService.getInstance().getIndex(getProject().getUnderlyingProject());
		return index.getWorkflowExits(this);
	}

	public List<IWorkflowReference> getWorkflowReferences()
	{
		List<IWorkflowReference> ret = new ArrayList<IWorkflowReference>();
		if(mainDesign != null)
		{
			ret.addAll(DesignTraversalHelper.getDesignElements(mainDesign, IWorkflowReference.class));
			for(Design dialog : dialogs)
			{
				ret.addAll(DesignTraversalHelper.getDesignElements(dialog, IWorkflowReference.class));
			}
		}
		return ret;
	}
	
	public List<IWorkflowExit> getDownStreamWorkflowExits(
		IDesignEntryPoint designEntry)
		{
		if(mainDesign != null)
			return DesignTraversalHelper.getDownStreamDesignElements((IDesignElement)designEntry.getAdapter(IDesignElement.class), IWorkflowExit.class);
		WorkflowIndex index = WorkflowIndexService.getInstance().getIndex(getProject().getUnderlyingProject());
		return index.getDownstreamWorkflowExits(this, designEntry);
		}
	
	
	public List<IWorkflowExit> getDownStreamWorkflowExits(
		IWorkflowEntry workflowEntry)
	{
		if(mainDesign != null)
			return DesignTraversalHelper.getDownStreamDesignElements((IDesignElement)workflowEntry.getAdapter(IDesignElement.class), IWorkflowExit.class);
		WorkflowIndex index = WorkflowIndexService.getInstance().getIndex(getProject().getUnderlyingProject());
		return index.getDownstreamWorkflowExits(this, workflowEntry);
	}

	public class IndexedWorkflowEntry implements IWorkflowEntry
	{
		private String id = null;
		private String name = null;
		private List<Variable> inputVariables = new ArrayList<Variable>();
		
		IndexedWorkflowEntry(String id, String name)
		{
			super();
			this.id = id;
			this.name = name;
		}
		
		void addInputVariable(Variable inputVariable)
		{
			inputVariables.add(inputVariable);
		}

		
		public String getId()
		{
			return id;
		}

		
		public String getName()
		{
			return name;
		}
		
		
		public List<Variable> getInputVariables()
		{
			return Collections.unmodifiableList(inputVariables);
		}

		
		public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter)
		{
			return null;
		}
		
	}

	public class IndexedWorkflowExit implements IWorkflowExit
	{
		private String id = null;
		private String name = null;
		private String type = null;
		private List<Variable> exportedVariables = new ArrayList<Variable>();
		
		IndexedWorkflowExit(String id, String name, String type)
		{
			super();
			this.id = id;
			this.name = name;
			this.type = type;
		}
		
		void addExportedVariable(Variable exportedVariable)
		{
			exportedVariables.add(exportedVariable);
		}

		
		public String getId()
		{
			return id;
		}

		
		public String getName()
		{
			return name;
		}
		
		public String getType()
		{
			return type;
		}
		
		
		public List<Variable> getExportedVariables()
		{
			return Collections.unmodifiableList(exportedVariables);
		}

		
		public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter)
		{
			return null;
		}
		
	}
	
	public static class IndexedWorkflowReference implements IWorkflowReference
	{
		private String id;
		private String targetId;
		private String entryId;
		
		IndexedWorkflowReference(String id, String targetId, String entryId)
		{
			super();
			this.id = id;
			this.targetId = targetId;
			this.entryId = entryId;
		}

		@Override
		public String getId()
		{
			return id;
		}

		@Override
		public String getTargetId()
		{
			return targetId;
		}

		@Override
		public String getEntryId()
		{
			return entryId;
		}
	}

	public class IndexedDesignEntry implements IDesignEntryPoint
	{
		private String id = null;
		private String name = null;
		
		IndexedDesignEntry(String id, String name)
		{
			super();
			this.id = id;
			this.name = name;
		}
		
		
		public String getId()
		{
			return id;
		}

		
		public String getName()
		{
			return name;
		}
		
		
		public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter)
		{
			return null;
		}
		
	}

	public class IndexedDesignExit implements IDesignExitPoint
	{
		private String id = null;
		private String targetId = null;
		private String targetName = null;
		private List<Variable> exportedVariables = new ArrayList<Variable>();
		
		IndexedDesignExit(String id, String targetId, String targetName)
		{
			super();
			this.id = id;
			this.targetId = targetId;
			this.targetName = targetName;
		}
		
		void addExportedVariable(Variable exportedVariable)
		{
			exportedVariables.add(exportedVariable);
		}

		
		public String getId()
		{
			return id;
		}

		public String getTargetId()
		{
			return targetId;
		}
		
		public String getTargetName()
		{
			return targetName;
		}
		
		
		public List<Variable> getExportedDesignVariables()
		{
			return Collections.unmodifiableList(exportedVariables);
		}

		
		public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter)
		{
			return null;
		}
		
	}

	public void addDocumentListener(IDesignDocumentListener listener)
	{
		listeners.remove(listener);
		listeners.add(listener);
	}
	
	public void removeDocumentListener(IDesignDocumentListener listener)
	{
		listeners.remove(listener);
	}

}
