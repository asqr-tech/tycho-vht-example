package org.eclipse.vtp.desktop.export.internal.main;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.vtp.desktop.export.internal.ExportAgent;
import org.eclipse.vtp.desktop.export.internal.ExportCore;
import org.eclipse.vtp.desktop.export.internal.MediaExporter;
import org.eclipse.vtp.desktop.export.internal.ProjectExporter;
import org.eclipse.vtp.desktop.export.internal.WorkflowExporter;
import org.eclipse.vtp.desktop.model.core.IDesignDocument;
import org.eclipse.vtp.framework.util.ConfigurationDictionary;
import org.eclipse.vtp.framework.util.Guid;
import org.eclipse.vtp.framework.util.StaticConfigurationAdmin;
import org.eclipse.vtp.framework.util.XMLWriter;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.Version;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class WebApplicationExporter {

	private static final List<String> REQUIRED_BUNDLES = Collections
			.unmodifiableList(Arrays.asList(new String[] { "org.eclipse.osgi",
					"org.eclipse.osgi.services",
					"org.eclipse.core.contenttype", "org.eclipse.core.jobs",
					"org.eclipse.core.runtime",
					"org.eclipse.core.runtime.compatibility.auth",
					"org.eclipse.equinox.app", "org.eclipse.equinox.common",
					"org.eclipse.equinox.http.servlet",
					"org.eclipse.equinox.http.servletbridge",
					"org.eclipse.equinox.preferences",
					"org.eclipse.equinox.registry",
					"org.eclipse.equinox.servletbridge",
					"org.eclipse.update.configurator", "javax.activation",
					"javax.mail", "javax.xml.soap", "javax.xml.rpc",
					"javax.wsdl", "org.apache.ant", "org.apache.axis",
					"org.apache.commons.discovery", "org.apache.commons.io",
					"org.apache.commons.fileupload",
					"org.apache.commons.logging", "org.apache.commons.pool",
					"org.apache.log4j", "org.apache.xerces",
					"org.apache.xml.resolver", "org.mozilla.javascript",
					"org.eclipse.vtp.framework.core",
					"org.eclipse.vtp.framework.engine",
					"org.eclipse.vtp.framework.spi",
					"org.eclipse.vtp.framework.util" }));

	private static final List<String> OPTIONAL_BUNDLES = Collections
			.unmodifiableList(Arrays.asList(new String[] { "javax.xml",
					"org.apache.xml.serializer" }));

	private static final List<String> EXTENSION_POINTS = Collections
			.unmodifiableList(Arrays.asList(new String[] {
					"org.eclipse.vtp.framework.core.actions",
					"org.eclipse.vtp.framework.core.configurations",
					"org.eclipse.vtp.framework.core.observers",
					"org.eclipse.vtp.framework.core.services" }));

	private static final Map<String, Integer> START_LEVELS;

	static {
		Map<String, Integer> startLevels = new HashMap<String, Integer>(4);
		startLevels.put("org.eclipse.equinox.servletbridge", -1);
		startLevels.put("org.eclipse.osgi", 0);
		startLevels.put("org.eclipse.osgi.services", 1);
		startLevels.put("org.eclipse.equinox.app", 2);
		startLevels.put("org.eclipse.equinox.common", 2);
		START_LEVELS = Collections.unmodifiableMap(startLevels);
	}

	private final Collection<ExportAgent> agents;
	private String uniqueToken = null;
	private ExportWriter output = null;
	private ExportWriter mediaOutput = null;
	private Collection<WorkflowExporter> workflowExporters = null;
	private Collection<MediaExporter> mediaExporters = null;
	private Map<String, BundleExporter> bundleExporters = null;
	private IProgressMonitor monitor = null;
	private DocumentBuilder documentBuilder = null;
	private Transformer transformer = null;
	private boolean separateMedia;

	public WebApplicationExporter(Collection<ExportAgent> agents) {
		this.agents = agents;
	}

	public final synchronized boolean export(File archive,
			Collection<WorkflowExporter> workflowProjects,
			Collection<MediaExporter> mediaProjects, boolean separateMedia, File mediaDestination, IProgressMonitor monitor)
			throws Exception {
		this.separateMedia = separateMedia;
		File canonical = archive.getCanonicalFile();
		boolean completed = false;
		try {
			this.uniqueToken = Guid.createGUID();
			if (canonical.isFile())
				delete(canonical);
			if (canonical.isDirectory())
				deleteChildren(canonical);
			this.output = ExportWriter.create(canonical);
			this.mediaOutput = output;
			if(separateMedia)
			{
				File mediaCanonical = mediaDestination.getCanonicalFile();
				if (mediaCanonical.isFile())
					delete(mediaCanonical);
				if (mediaCanonical.isDirectory())
					deleteChildren(mediaCanonical);
				this.mediaOutput = ExportWriter.create(mediaCanonical);
			}
			this.workflowExporters = workflowProjects;
			this.mediaExporters = mediaProjects;
			this.bundleExporters = new LinkedHashMap<String, BundleExporter>();
			this.monitor = monitor;
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			dbf.setValidating(false);
			this.documentBuilder = dbf.newDocumentBuilder();
			this.transformer = TransformerFactory.newInstance()
					.newTransformer();
			return completed = run();
		} finally {
			try {
				if (output != null)
					output.close();
			} finally {
				this.uniqueToken = null;
				this.output = null;
				this.workflowExporters = null;
				this.mediaExporters = null;
				this.bundleExporters = null;
				this.monitor = null;
				this.documentBuilder = null;
				this.transformer = null;
				if (!completed)
					delete(canonical);
			}
		}
	}

	private void delete(File file) throws IOException {
		File canonical = file.getCanonicalFile();
		if (canonical.isDirectory())
			deleteChildren(canonical);
		canonical.delete();
	}

	private void deleteChildren(File canonical) throws IOException {
		File[] children = canonical.listFiles();
		if (children != null)
			for (File child : children)
				delete(child);
	}

	private boolean run() throws Exception {
		if (monitor.isCanceled())
			return false;
		monitor.beginTask("Constructing runtime...", workflowExporters.size()
				+ mediaExporters.size() + 3);
		constructRuntime();
		monitor.worked(1);
		if (monitor.isCanceled())
			return false;
		monitor.setTaskName("Exporting framework...");
		exportFramework();
		monitor.worked(1);
		for (WorkflowExporter project : workflowExporters) {
			if (monitor.isCanceled())
				return false;
			monitor.setTaskName(String.format(
					"Exporting workflow project %s...", project.getProject()
							.getName()));
			exportWorkflow(project);
			monitor.worked(1);
		}
		for (MediaExporter project : mediaExporters) {
			if (monitor.isCanceled())
				return false;
			monitor.setTaskName(String.format("Exporting media project %s...",
					project.getProject().getName()));
			if(!separateMedia)
				exportMedia(project);
			else
				exportExternalMedia(project);
			monitor.worked(1);
		}
		if (monitor.isCanceled())
			return false;
		monitor.setTaskName("Finializing web application...");
		finalizeWebApplication();
		monitor.worked(1);
		monitor.done();
		return true;
	}

	private void constructRuntime() {
		// Locate all the bundles that will be included in the export.
		for (String name : REQUIRED_BUNDLES)
			bundleExporters.put(name, null);
		for (String name : OPTIONAL_BUNDLES)
			bundleExporters.put(name, null);
		IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
		for (String extensionPoint : EXTENSION_POINTS)
			for (IExtension extension : extensionRegistry.getExtensionPoint(
					extensionPoint).getExtensions())
				bundleExporters.put(extension.getContributor().getName(), null);
		for (Bundle bundle : ExportCore.getBundles()) {
			System.err.println("Bundle: " + bundle.getSymbolicName());
			if (bundle.getState() == Bundle.UNINSTALLED)
				continue;
			String symbolicName = bundle.getSymbolicName();
			if (Constants.SYSTEM_BUNDLE_SYMBOLICNAME.equals(symbolicName))
				symbolicName = REQUIRED_BUNDLES.get(0);
			if (!bundleExporters.containsKey(symbolicName)) {
				String hostSymbolicName = (String)bundle.getHeaders().get(
						"Fragment-Host");
				if (hostSymbolicName == null)
					continue;
				if (hostSymbolicName.indexOf(';') >= 0)
					hostSymbolicName = hostSymbolicName.substring(0,
							hostSymbolicName.indexOf(';'));
				hostSymbolicName = hostSymbolicName.trim();
				if (!bundleExporters.containsKey(hostSymbolicName))
					continue;
			}

			BundleExporter exporter = bundleExporters.get(symbolicName);
			if (exporter == null)
				bundleExporters.put(symbolicName, new BundleExporter(bundle));
			else {
				Version oldVersion = new Version((String) exporter.getHeaders()
						.get(Constants.BUNDLE_VERSION));
				Version newVersion = new Version((String)bundle.getHeaders()
						.get(Constants.BUNDLE_VERSION));
				if (newVersion.compareTo(oldVersion) > 0)
					bundleExporters.put(symbolicName, exporter);
			}
		}
		for (String name : bundleExporters.keySet().toArray(
				new String[bundleExporters.size()]))
			if (bundleExporters.get(name) == null) {
				System.err.println("Missing bundle import: " + name);
				bundleExporters.remove(name);
			}
	}

	private void exportFramework() throws Exception {
		// Copy the static launch configuration file.
		output.writeURL("WEB-INF/eclipse/launch.ini",
				getClass().getResource("/launch.ini"));
		// Create the startup configuration file.
		PrintStream frameworkConfig = new PrintStream(
				output.write("WEB-INF/eclipse/configuration/config.ini"));
		frameworkConfig.println("#Eclipse Runtime Configuration File");
		frameworkConfig.println("#Generated by the VoiceTools export wizard.");
		frameworkConfig.println("osgi.parentClassLoader=boot");
		frameworkConfig.print("osgi.bundles=");
		frameworkConfig
				.println("org.eclipse.equinox.servletbridge.extensionbundle,\\");
		frameworkConfig.print("javax.servlet");
		for (String symbolicName : bundleExporters.keySet()) {
			Integer startLevel = START_LEVELS.get(symbolicName);
			if (startLevel == null || startLevel.intValue() > 0) {
				frameworkConfig.println(",\\");
				frameworkConfig.print("  ");
				frameworkConfig.print(symbolicName);
				if (bundleExporters.get(symbolicName).isFragment())
					continue;
				if (startLevel == null)
					frameworkConfig.print("@start");
				else {
					frameworkConfig.print("@");
					frameworkConfig.print(startLevel);
					frameworkConfig.print(":start");
				}
			}
		}
		frameworkConfig.println();
		frameworkConfig.println("osgi.bundles.defaultStartLevel=4");
		frameworkConfig.close();
		// Export the bundles from the environment.
		for (BundleExporter exporter : bundleExporters.values()) {
			Integer startLevel = START_LEVELS.get(exporter.getSymbolicName());
			if (startLevel == null || startLevel.intValue() >= 0)
				exporter.export(output, "WEB-INF/eclipse/plugins/");
		}
		// Export the fake Java Servlet bundle.
		output.writeURL(
				"WEB-INF/eclipse/plugins/javax.servlet_2.4.0/META-INF/MANIFEST.MF",
				getClass().getResource("/servletManifest.mf"));
		// Export the static configuration bundle.
		output.writeURL(
				"WEB-INF/eclipse/plugins/static.configuration_0.0.0/META-INF/MANIFEST.MF",
				getClass().getResource("/staticConfigManifest.mf"));
		List<ConfigurationDictionary> dictionaries = new LinkedList<ConfigurationDictionary>();
		ConfigurationDictionary httpDictionary = new ConfigurationDictionary(
				"org.eclipse.vtp.framework.engine.http");
		httpDictionary.put("path", "/");
		httpDictionary.put("mime.type.grxml", "application/srgs+xml");
		httpDictionary.put("mime.type.vox", "audio/basic");
		httpDictionary.put("mime.type.wav", "audio/x-wav");
		dictionaries.add(httpDictionary);
		for (WorkflowExporter exporter : workflowExporters)
			dictionaries.addAll(exporter
					.getConfigurationDictionaries(uniqueToken));
		for (MediaExporter exporter : mediaExporters)
			dictionaries.addAll(exporter
					.getConfigurationDictionaries(uniqueToken));
		Document document = documentBuilder.newDocument();
		document.appendChild(ConfigurationDictionary.saveAll(document,
				dictionaries.toArray(new ConfigurationDictionary[dictionaries
						.size()])));
		OutputStream staticConfig = output
				.write("WEB-INF/eclipse/plugins/static.configuration_0.0.0/META-INF/services/"
						+ StaticConfigurationAdmin.class.getName());
		transformer.transform(new DOMSource(document), new XMLWriter(
				staticConfig).toXMLResult());
		staticConfig.close();

	}

	private String exportProject(ProjectExporter project) throws Exception
	{
		return exportProject(project, null);
	}
	
	private String exportProject(ProjectExporter project, FilenameFilter filter) throws Exception
	{
		String symbolicName = project.getProject().getName() + "."
				+ uniqueToken;
		String path = "WEB-INF/eclipse/plugins/" + symbolicName + "_0.0.0/";
		// Write the bundle manifest.
		PrintStream manifest = new PrintStream(output.write(path
				+ "META-INF/MANIFEST.MF"));
		manifest.println("Manifest-Version: 1.0");
		manifest.println("Bundle-ManifestVersion: 2");
		manifest.print("Bundle-Name: ");
		manifest.println(project.getProject().getName());
		manifest.print("Bundle-SymbolicName: ");
		manifest.print(symbolicName);
		manifest.println(";singleton:=true");
		manifest.println("Bundle-Version: 0.0.0");
		manifest.println("Bundle-Localization: plugin");
		manifest.print("Bundle-ClassPath: .,project/Dependencies");
		IFolder dependencyFolder = project.getProject().getFolder("Dependencies");
		if(dependencyFolder.exists())
		{
			IResource[] members = dependencyFolder.members();
			for(IResource dep : members)
			{
				if(dep.exists() && dep instanceof IFile)
				{
					IFile file = (IFile)dep;
					if(file.getFileExtension().toLowerCase().equals("jar"))
					{
						manifest.print(",project/Dependencies/" + file.getName());
					}
				}
			}
			manifest.println();
		}
		manifest.print("Require-Bundle:");
		boolean printed = false;
		for (String name : bundleExporters.keySet()) {
			if (printed)
				manifest.println(",");
			manifest.print(" ");
			manifest.print(name);
			manifest.print(";resolution:=optional");
			printed = true;
		}
		manifest.println();
		manifest.close();
		// Copy the project contents.
		output.writeFile(path + "project/", project.getProject().getLocation()
			.toFile(), filter);
		return path;
	}
	
	private void exportExternalMedia(MediaExporter project) throws Exception
	{
		FilenameFilter filter = new FilenameFilter(){

			@Override
			public boolean accept(File dir, String name)
			{
				if(name.endsWith(".wav") || name.endsWith(".au") || name.endsWith(".vox"))
					return false;
				return true;
			}
			
		};
		File file = project.getMediaProject().getMediaFilesFolder().getUnderlyingFolder().getLocation().toFile();
		mediaOutput.writeFile(project.getMediaProject().getName() + "/", file, null);
		String path = exportProject(project, filter);
		// Create the plugin.xml file.
		Document pluginXmlDoc = documentBuilder.newDocument();
		pluginXmlDoc.appendChild(pluginXmlDoc.createProcessingInstruction(
				"eclipse", "version=\"3.2\""));
		Element pluginElement = pluginXmlDoc.createElement("plugins");
		Element extensionElement = pluginXmlDoc.createElement("extension");
		extensionElement.setAttribute("point",
				"org.eclipse.vtp.framework.engine.resources");
		Element resourcesElement = pluginXmlDoc.createElement("resources");
		resourcesElement.setAttribute("id", project.getProject().getName());
		resourcesElement.setAttribute("name", project.getProject().getName());
		resourcesElement.setAttribute("path", "project/Media Files");
		extensionElement.appendChild(resourcesElement);
		pluginElement.appendChild(extensionElement);
		pluginXmlDoc.appendChild(pluginElement);
		OutputStream stream = output.write(path + "plugin.xml");
		transformer.transform(new DOMSource(pluginXmlDoc),
				new XMLWriter(stream).toXMLResult());
		stream.close();
		StringBuilder fileIndex = new StringBuilder();
		indexMedia(fileIndex, project.getMediaProject().getMediaFilesFolder().getUnderlyingFolder());
		stream = output.write(path + "files.index");
		stream.write(fileIndex.toString().getBytes());
		stream.close();
	}
	
	private void indexMedia(StringBuilder index, IFolder toIndex)
	{
		try
		{
			for(IResource r : toIndex.members())
			{
				if(r instanceof IFolder)
				{
					indexMedia(index, (IFolder)r);
				}
				else
				{
					index.append(r.getProjectRelativePath().toString());
					index.append("\r\n");
				}
			}
		}
		catch (CoreException e)
		{
			e.printStackTrace();
		}
	}

	private void exportWorkflow(WorkflowExporter project) throws Exception {
		String path = exportProject(project);
		// Create the plugin.xml file.
		Document pluginXmlDoc = documentBuilder.newDocument();
		pluginXmlDoc.appendChild(pluginXmlDoc.createProcessingInstruction(
				"eclipse", "version=\"3.2\""));
		Element pluginElement = pluginXmlDoc.createElement("plugins");
		Element extensionElement = pluginXmlDoc.createElement("extension");
		extensionElement.setAttribute("point",
				"org.eclipse.vtp.framework.engine.definitions");
		Element resourcesElement = pluginXmlDoc.createElement("definition");
		resourcesElement.setAttribute("id", project.getProject().getName()
				+ "." + uniqueToken);
		resourcesElement.setAttribute("name", project.getProject().getName());
		resourcesElement.setAttribute("path", "process.xml");
		extensionElement.appendChild(resourcesElement);
		pluginElement.appendChild(extensionElement);
		pluginXmlDoc.appendChild(pluginElement);
		OutputStream stream = output.write(path + "plugin.xml");
		transformer.transform(new DOMSource(pluginXmlDoc),
				new XMLWriter(stream).toXMLResult());
		stream.close();
		// Create the process.xml file.
		Map<String, MediaExporter> mediaProjects = project
				.getMediaDependencyMap();
		Map<String, String> formatterIDsByLanguage = new HashMap<String, String>(
				mediaProjects.size());
		Map<String, String> resourceManagerIDsByLanguage = new HashMap<String, String>(
				mediaProjects.size());
		for (Map.Entry<String, MediaExporter> entry : mediaProjects.entrySet()) {
			formatterIDsByLanguage.put(entry.getKey(), entry.getValue()
					.getFormatter());
			resourceManagerIDsByLanguage.put(entry.getKey(), entry.getValue()
					.getProject().getName());
		}
		Collection<DesignReference> callDesigns = new LinkedList<DesignReference>();
		for (IDesignDocument document : project.getDesignDocuments())
			callDesigns.add(new DesignReference(document, documentBuilder.parse(document.getUnderlyingFile()
					.getLocation().toFile().getCanonicalFile())));
		Document definition = new DefinitionBuilder(
				callDesigns.toArray(new DesignReference[callDesigns.size()]),
				documentBuilder, project.getWorkflowProject(),
				formatterIDsByLanguage, resourceManagerIDsByLanguage, project.getLanguageMapping())
				.getDefinition();
		Element servicesElement = (Element) definition
				.getDocumentElement()
				.getElementsByTagNameNS(
						"http://eclipse.org/vtp/xml/framework/engine/process-definition",
						"services").item(0);
		for (ExportAgent agent : agents)
			agent.getValue().configureServices(project, servicesElement);
		OutputStream fileEntryStream = output.write(path + "process.xml");
		transformer.transform(new DOMSource(definition), new XMLWriter(
				fileEntryStream).toXMLResult());
		fileEntryStream.close();
	}

	private void exportMedia(MediaExporter project) throws Exception {
		String path = exportProject(project);
		// Create the plugin.xml file.
		Document pluginXmlDoc = documentBuilder.newDocument();
		pluginXmlDoc.appendChild(pluginXmlDoc.createProcessingInstruction(
				"eclipse", "version=\"3.2\""));
		Element pluginElement = pluginXmlDoc.createElement("plugins");
		Element extensionElement = pluginXmlDoc.createElement("extension");
		extensionElement.setAttribute("point",
				"org.eclipse.vtp.framework.engine.resources");
		Element resourcesElement = pluginXmlDoc.createElement("resources");
		resourcesElement.setAttribute("id", project.getProject().getName());
		resourcesElement.setAttribute("name", project.getProject().getName());
		resourcesElement.setAttribute("path", "project/Media Files");
		extensionElement.appendChild(resourcesElement);
		pluginElement.appendChild(extensionElement);
		pluginXmlDoc.appendChild(pluginElement);
		OutputStream stream = output.write(path + "plugin.xml");
		transformer.transform(new DOMSource(pluginXmlDoc),
				new XMLWriter(stream).toXMLResult());
		stream.close();
	}

	private void finalizeWebApplication() throws Exception {
		// Copy the session listener bridge.
		output.writeURL(
				"WEB-INF/classes/org/eclipse/vtp/framework/webapp/HttpSessionListenerManager.class",
				getClass()
						.getResource(
								"/org/eclipse/vtp/framework/webapp/HttpSessionListenerManager.class"));
		StringBuilder builder = new StringBuilder();
		IConfigurationElement[] packageEntries = Platform.getExtensionRegistry().getConfigurationElementsFor("org.eclipse.vtp.framework.engine.externalPackageEntries");
		for(IConfigurationElement entry : packageEntries)
		{
			if(builder.length() != 0)
				builder.append(", ");
			builder.append(entry.getAttribute("package"));
			builder.append("; version=");
			builder.append(entry.getAttribute("version"));
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		InputStream webIn = getClass().getResourceAsStream("/web.xml");
		byte[] buf = new byte[1024];
		int len = webIn.read(buf);
		while(len != -1)
		{
			baos.write(buf, 0, len);
			len = webIn.read(buf);
		}
		baos.close();
		webIn.close();
		String webText = baos.toString();
		webText = webText.replace("[[extendedFrameworkExports]]", builder.toString());
		ByteArrayInputStream bais = new ByteArrayInputStream(webText.getBytes());
		output.writeStream("WEB-INF/web.xml", bais);
		bais.close();
		// Export the WAR libraries.
		output.writeURL("WEB-INF/lib/servletbridge.jar", getClass()
				.getResource("/servletbridge.jar"));
	}

}
