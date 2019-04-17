package org.eclipse.vtp.desktop.export.internal;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.SAXParser;

import org.eclipse.core.resources.IProject;
import org.eclipse.vtp.desktop.export.IWorkflowExporter;
import org.eclipse.vtp.desktop.model.core.IDesignDocument;
import org.eclipse.vtp.desktop.model.core.IDesignFolder;
import org.eclipse.vtp.desktop.model.core.IDesignItemContainer;
import org.eclipse.vtp.desktop.model.core.IWorkflowProject;
import org.eclipse.vtp.desktop.model.core.branding.IBrand;
import org.eclipse.vtp.desktop.model.interactive.core.IInteractiveWorkflowProject;
import org.eclipse.vtp.desktop.model.interactive.core.IMediaProject;
import org.eclipse.vtp.framework.util.ConfigurationDictionary;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public final class WorkflowExporter extends ProjectExporter implements
		IWorkflowExporter {
	
	private final String id;
	private final IWorkflowProject project;
	private final Map<String, MediaExporter> mediaDependencies = new HashMap<String, MediaExporter>();
	private final Set<WorkflowExporter> workflowDependencies = new HashSet<WorkflowExporter>();
	private final Map<String, List<String>> languageMapping = new HashMap<String, List<String>>();

	public WorkflowExporter(Exporter exporter, DocumentBuilder db, IWorkflowProject project) {
		this.project = project;
		String id = "";
		try {
			InputStream input = project.getUnderlyingProject().getFile(".buildPath").getContents();
			try {
				id = db.parse(input).getDocumentElement().getAttribute("id");
			} finally {
				input.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.id = id;
		loadSettings(exporter);
	}
	
	public String getId () {
		return id;
	}

	public IWorkflowProject getWorkflowProject() {
		return project;
	}

	public IProject getProject() {
		return project.getUnderlyingProject();
	}
	
	@Override
	public Collection<ConfigurationDictionary> getConfigurationDictionaries(String uniqueToken) {
		ConfigurationDictionary config = new ConfigurationDictionary(
				"org.eclipse.vtp.framework.engine.http.deployments." + getProject().getName() + "."	 + uniqueToken,
				"org.eclipse.vtp.framework.engine.http.deployments");
		config.put("deployment.id", project.getName());
		config.put("definition.id", project.getName() + "." + uniqueToken);
		config.put("resources", mediaDependencies.keySet().toArray(
				new String[mediaDependencies.size()]));
		config.put("path", "/" + project.getName());
		config.put("fragment", "false");
		return Collections.singleton(config);
	}

	public Collection<MediaExporter> getMediaDependencies () {
		return Collections.unmodifiableCollection(mediaDependencies.values());
	}
	
	public Map<String, List<String>> getLanguageMapping()
	{
		return Collections.unmodifiableMap(languageMapping);
	}

	public Map<String, MediaExporter> getMediaDependencyMap () {
		return Collections.unmodifiableMap(mediaDependencies);
	}
	
	public Collection<WorkflowExporter> getWorkflowDependencies () {
		return new ArrayList<WorkflowExporter>(workflowDependencies);
	}
	
	public void resolveDependencies (
			SAXParser sp,
			Map<String, WorkflowExporter> workflowExporters,
			Map<String, MediaExporter> mediaExporters) {
		final Set<String> linkedIds = new HashSet<String>();
		DefaultHandler handler = new DefaultHandler() {
			boolean active = false;
			@Override
			public void startElement(String uri, String localName,
					String qName, Attributes attributes) throws SAXException {
				super.startElement(uri, localName, qName, attributes);
				if ("element".equals(qName) &&
						"org.eclipse.vtp.desktop.model.elements.core.include".equals(attributes.getValue("type")))
					active = true;
				else if (active && "property".equals(qName) && "instanceId".equals(attributes.getValue("name"))) {
					linkedIds.add(attributes.getValue("value"));
				}
			}
			@Override
			public void endElement(String uri, String localName, String qName)
					throws SAXException {
				super.endElement(uri, localName, qName);
				if (active && "element".equals(qName))
					active = false;
			}
		};
		for (IDesignDocument doc : getDesignDocuments()) {
			try {
				InputStream input = doc.getUnderlyingFile().getContents();
				try {
					sp.parse(input, handler);
				} finally {
					input.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		for (WorkflowExporter exporter : workflowExporters.values()) {
			if (linkedIds.contains(exporter.getId()))
				workflowDependencies.add(exporter);
		}
		if (project instanceof IInteractiveWorkflowProject) {
			IInteractiveWorkflowProject iProject = (IInteractiveWorkflowProject)project;
			for (String interactionType : iProject.getSupportedInteractionTypes())
			{
				languageMapping.put(interactionType, iProject.getSupportedLanguages(interactionType));
				for (String language : iProject.getSupportedLanguages(interactionType))
				{
					resolveMediaDependencies(mediaExporters, iProject, interactionType, language, iProject.getBrandManager().getDefaultBrand());
				}
			}
		}
	}
	
	private void resolveMediaDependencies (Map<String, MediaExporter> mediaExporters, 
			IInteractiveWorkflowProject iProject, String interactionType, String language, IBrand brand) {
		IMediaProject mediaProject = iProject.getMediaProviderManager().getMediaProject(interactionType, brand, language);
		if (mediaProject != null) {
			MediaExporter exporter = mediaExporters.get(mediaProject.getName());
			if (exporter != null)
				mediaDependencies.put(brand.getId() + ":" + interactionType + ":" + language, exporter);
		} 
		for (IBrand child : brand.getChildBrands())
			resolveMediaDependencies(mediaExporters, iProject, interactionType, language, child);
	}
	
	public Collection<IDesignDocument> getDesignDocuments () {
		Collection<IDesignDocument> documents = new LinkedList<IDesignDocument>();
		findDesignDocuments(project.getDesignRootFolder(), documents);
		return documents;
	}
	
	private void findDesignDocuments (IDesignItemContainer container, Collection<IDesignDocument> documents) {
		documents.addAll(container.getDesignDocuments());
		for (IDesignFolder folder : container.getDesignFolders())
			findDesignDocuments(folder, documents);
	}

}
