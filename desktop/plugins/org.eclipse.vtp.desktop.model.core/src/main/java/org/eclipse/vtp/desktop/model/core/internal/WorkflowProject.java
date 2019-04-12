/*--------------------------------------------------------------------------
 * Copyright (c) 2009 OpenMethods, LLC
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Trip Gilman (OpenMethods)
 *    - initial API and implementation
 -------------------------------------------------------------------------*/
package org.eclipse.vtp.desktop.model.core.internal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.vtp.desktop.model.core.IBusinessObjectSet;
import org.eclipse.vtp.desktop.model.core.IDatabaseSet;
import org.eclipse.vtp.desktop.model.core.IDependencySet;
import org.eclipse.vtp.desktop.model.core.IDesignDocument;
import org.eclipse.vtp.desktop.model.core.IDesignRootFolder;
import org.eclipse.vtp.desktop.model.core.IWebserviceSet;
import org.eclipse.vtp.desktop.model.core.IWorkflowEntry;
import org.eclipse.vtp.desktop.model.core.IWorkflowExit;
import org.eclipse.vtp.desktop.model.core.IWorkflowProject;
import org.eclipse.vtp.desktop.model.core.IWorkflowResource;
import org.eclipse.vtp.desktop.model.core.WorkflowCore;
import org.eclipse.vtp.desktop.model.core.branding.BrandManager;
import org.eclipse.vtp.desktop.model.core.branding.IBrand;
import org.eclipse.vtp.desktop.model.core.design.IDesignViewer;
import org.eclipse.vtp.desktop.model.core.event.ReloadObjectDataEvent;
import org.eclipse.vtp.desktop.model.core.internal.branding.Brand;
import org.eclipse.vtp.desktop.model.core.internal.branding.DefaultBrandManager;
import org.eclipse.vtp.framework.util.Guid;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This is a concrete implementation of <code>IWorkflowProject</code>
 * and provides the default behavior of that interface.
 *
 * @author Trip Gilman
 * @version 1.0
 * @since 4.0
 */
public class WorkflowProject extends WorkflowResource
	implements IWorkflowProject, IResourceChangeListener
{
	/** The underlying eclipse project for this project resource. */
	protected IProject project;
	private DefaultBrandManager brandManager = null;
	private String projectId = null;
	private BusinessObjectSet businessObjectSet = null;
	private DependencySet dependencySet = null;
	private DesignRootFolder designRootFolder = null;
	private DatabaseSet databaseSet = null;
	private WebserviceSet webserviceSet = null;

	public WorkflowProject(IProject project)
	{
		this(project, true);
	}
	
	/**
	 * Creates a new <code>WorkflowProject</code> for the given eclipse
	 * project resource.
	 *
	 * @param project The underlying eclipse project resource
	 */
	protected WorkflowProject(IProject project, boolean initBuildPath)
	{
		super();
		this.project = project;
		activateEvents();
		IFolder f = getOrCreateFolder(project, "Dependencies");
		dependencySet = new DependencySet(this, f);
		f = getOrCreateFolder(project, "Business Objects");
		businessObjectSet = new BusinessObjectSet(this, f);
		f = getOrCreateFolder(project, "Databases");
		databaseSet = new DatabaseSet(this, f);
		f = getOrCreateFolder(project, "Webservices");
		webserviceSet = new WebserviceSet(this, f);
		f = getOrCreateFolder(project, "Workflow Design");
		designRootFolder = new DesignRootFolder(this, f);
		if(initBuildPath)
			loadBuildPath();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.project.IApplicationProject#getApplicationId()
	 */
	public String getId()
	{
		return projectId;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.project.IVoiceResource#getName()
	 */
	public String getName()
	{
		return project.getName();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.project.IVoiceResource#getParent()
	 */
	public IWorkflowResource getParent()
	{
		return null;
	}

	public IWorkflowProject getProject()
	{
		return this;
	}

	public BrandManager getBrandManager()
	{
		return brandManager;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.project.IApplicationProject#getBusinessObjectSet()
	 */
	public IBusinessObjectSet getBusinessObjectSet()
	{
		return businessObjectSet;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.desktop.core.project.IApplicationProject#
	 *      getDependencySet()
	 */
	public IDependencySet getDependencySet()
	{
		return dependencySet;
	}
	
	public IDatabaseSet getDatabaseSet()
	{
		return databaseSet;
	}
	
	public IWebserviceSet getWebserviceSet()
	{
		return webserviceSet;
	}

	public IDesignRootFolder getDesignRootFolder()
	{
		return designRootFolder;
	}
	
	public IWorkflowEntry getWorkflowEntry(String id)
	{
		WorkflowTraversalHelper wth = new WorkflowTraversalHelper(this, new LinkedList<IDesignDocument>());
		return wth.getWorkflowEntry(id);
	}
	
	public IWorkflowEntry getWorkflowEntryByName(String name)
	{
		WorkflowTraversalHelper wth = new WorkflowTraversalHelper(this, new LinkedList<IDesignDocument>());
		for(IWorkflowEntry entry : wth.getAllWorkflowEntries())
			if(entry.getName().equals(name))
				return entry;
		return null;
	}

	public List<IWorkflowEntry> getWorkflowEntries()
	{
		WorkflowTraversalHelper wth = new WorkflowTraversalHelper(this, new LinkedList<IDesignDocument>());
		return wth.getAllWorkflowEntries();
	}

	public List<IWorkflowExit> getWorkflowExits(IWorkflowEntry entryPoint)
	{
		return null;
	}

	public List<IWorkflowResource> getChildren()
	{
		List<IWorkflowResource> children = new LinkedList<IWorkflowResource>();
		children.add(designRootFolder);
		children.add(businessObjectSet);
		children.add(databaseSet);
		children.add(webserviceSet);
		children.add(dependencySet);
		return children;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.project.IVoiceProject#getUnderlyingProject()
	 */
	public IProject getUnderlyingProject()
	{
		return project;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.project.internals.VoiceResource#getObjectId()
	 */
	protected String getObjectId()
	{
		return project.getFullPath().toPortableString();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter)
	{
		if(adapter.equals(IResource.class) && adapter.isAssignableFrom(project.getClass()))
		{
			return project;
		}
		if(adapter.equals(IProject.class))
		{
			return project;
		}

		return super.getAdapter(adapter);
	}

	/**
	 * Pre-loads the information from the auxiliary "." files to initialize
	 * this object's internal member variables.
	 */
	protected final synchronized void loadBuildPath()
	{
		try
		{
			IFile buildPath = project.getFile(".buildPath");
			if(!buildPath.exists())
			{
				initializeBuildPath();
				storeBuildPath();
			}
			DocumentBuilderFactory buildFactory =
				DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = buildFactory.newDocumentBuilder();
			Document doc = builder.parse(buildPath.getContents());
			Element root = doc.getDocumentElement();
			projectId = root.getAttribute("id");
			
			NodeList sectionList = root.getChildNodes();
			for(int i = 0; i < sectionList.getLength(); i++)
			{
				if(sectionList.item(i).getNodeType() == Node.ELEMENT_NODE)
				{
					loadBuildPathSection((Element)sectionList.item(i));
				}
			}
			project.getWorkspace().addResourceChangeListener(this, IResourceChangeEvent.POST_CHANGE);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	protected void loadBuildPathSection(Element sectionElement)
	{
		if(sectionElement.getNodeName().equals("brands"))
		{
//			if(brandManager == null)
//			{
				brandManager = new DefaultBrandManager();
				NodeList nl = sectionElement.getChildNodes();
				
				for(int i = 0; i < nl.getLength(); i++)
				{
					if(nl.item(i).getNodeType() != Node.ELEMENT_NODE)
						continue;
					Element brandElement = (Element)nl.item(i);
					String brandId = brandElement.getAttribute("id");
					Brand defaultBrand = new Brand(brandId, brandElement.getAttribute("name"));
					brandManager.setDefaultBrand(defaultBrand);
					NodeList subBrandList = brandElement.getChildNodes();
					addBrands(defaultBrand, subBrandList);
				}
//			}
		}
	}
	
	private void addBrands(Brand parent, NodeList brandList)
	{
		for(int i = 0; i < brandList.getLength(); i++)
		{
			if(brandList.item(i).getNodeType() != Node.ELEMENT_NODE)
				continue;
			Element brandElement = (Element)brandList.item(i);
			String brandId = brandElement.getAttribute("id");
			Brand brand = new Brand(brandId, brandElement.getAttribute("name"));
			brand.setParent(parent);
			addBrands(brand, brandElement.getChildNodes());
		}
	}
	
	public final void storeBuildPath()
	{
		try
		{
			Document document =
				DocumentBuilderFactory.newInstance().newDocumentBuilder()
									  .newDocument();
			Element rootElement =
				document.createElement("workflow-settings");
			document.appendChild(rootElement);
			rootElement.setAttribute("id", projectId);
			writeBuildPathSections(rootElement);
			
			Transformer trans =
				TransformerFactory.newInstance().newTransformer();
			DOMSource source = new DOMSource(document);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			StreamResult result = new StreamResult(baos);
			trans.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			trans.transform(source, result);

			IFile buildPath = project.getFile(".buildPath");
			if(!buildPath.exists())
				buildPath.create(new ByteArrayInputStream(baos.toByteArray()), true, null);
			else
				buildPath.setContents(new ByteArrayInputStream(baos.toByteArray()),
					true, false, null);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		catch(FactoryConfigurationError e)
		{
			e.printStackTrace();
		}

		WorkflowCore.getDefault()
						 .postObjectEvent(new ReloadObjectDataEvent(
				getObjectId()));
	}
	
	protected void writeBuildPathSections(Element rootElement)
	{
		Document document = rootElement.getOwnerDocument();
		Element brandsElement = document.createElement("brands");
		rootElement.appendChild(brandsElement);
		IBrand defaultBrand = brandManager.getDefaultBrand();
		writeBrand(brandsElement, defaultBrand);
	}
	
	/**
	 * @param parentElement
	 * @param brand
	 */
	private void writeBrand(Element parentElement, IBrand brand)
	{
		Element brandElement = parentElement.getOwnerDocument().createElement("brand");
		parentElement.appendChild(brandElement);
		brandElement.setAttribute("id", brand.getId());
		brandElement.setAttribute("name", brand.getName());
		List<IBrand> children = brand.getChildBrands();
		for(IBrand child : children)
		{
			writeBrand(brandElement, child);
		}
	}

	protected void initializeBuildPath()
	{
		this.projectId = Guid.createGUID();
		brandManager = new DefaultBrandManager();
		Brand defaultBrand = new Brand(Guid.createGUID(), "Default");
		brandManager.setDefaultBrand(defaultBrand);
	}
	
	public void finalize()
	{
		project.getWorkspace().removeResourceChangeListener(this);
	}

	public void resourceChanged(IResourceChangeEvent event)
	{
		IResourceDelta rootDelta = event.getDelta();
		try
		{
			rootDelta.accept(new IResourceDeltaVisitor()
			{
				public boolean visit(IResourceDelta delta) throws CoreException
				{
					if(delta.getResource().equals(project.getFile(".buildPath")))
					{
						if(delta.getKind() == IResourceDelta.CHANGED)
						{
							loadBuildPath();
							return false;
						}
					}
					return true;
				}
				
			});
		}
		catch (CoreException e)
		{
			e.printStackTrace();
		}
	}

	public void navigateToElement(String elementId)
    {
		WorkflowIndex index = WorkflowIndexService.getInstance().getIndex(getUnderlyingProject());
		String documentPath = index.locateElement(elementId);
		if(documentPath != null)
		{
			IFile file = getUnderlyingProject().getFile(documentPath);
			if(file.exists())
			{
				IWorkflowResource workflowResource = WorkflowCore.getDefault().getWorkflowModel().convertToWorkflowResource(file);
				if(workflowResource instanceof IDesignDocument)
				{
					IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
					if(workbenchWindow == null)
					{
						if(PlatformUI.getWorkbench().getWorkbenchWindowCount() > 0)
							workbenchWindow = PlatformUI.getWorkbench().getWorkbenchWindows()[0];
					}
					if(workbenchWindow != null)
					{
						try
						{
							IEditorPart editor = IDE.openEditor(workbenchWindow.getActivePage(), file, true);
							if(editor instanceof IDesignViewer)
							{
								((IDesignViewer)editor).displayElement(elementId);
								return;
							}
						}
						catch (PartInitException e)
						{
							e.printStackTrace();
						}
					}
				}
			}
			
		}
		//should show an error box here or something
		System.err.println("could not locate element: " + elementId);
    }
	
	private IFolder getOrCreateFolder(IProject parent, String name)
	{
		IFolder f = parent.getFolder(name);
		if(!f.exists())
		{
			try
			{
				f.create(true, true, null);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
		return f;
	}
}
