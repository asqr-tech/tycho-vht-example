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
package org.eclipse.vtp.desktop.export.internal.main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.vtp.desktop.export.IConfigurationExporter;
import org.eclipse.vtp.desktop.export.IDefinitionBuilder;
import org.eclipse.vtp.desktop.export.IFlowElement;
import org.eclipse.vtp.desktop.export.IFlowModel;
import org.eclipse.vtp.desktop.export.internal.ExportCore;
import org.eclipse.vtp.desktop.media.core.FormatterRegistration;
import org.eclipse.vtp.desktop.media.core.FormatterRegistrationManager;
import org.eclipse.vtp.desktop.model.core.IWorkflowEntry;
import org.eclipse.vtp.desktop.model.core.IWorkflowProject;
import org.eclipse.vtp.desktop.model.core.branding.IBrand;
import org.eclipse.vtp.desktop.model.core.design.IDesign;
import org.eclipse.vtp.desktop.model.core.design.IDesignElement;
import org.eclipse.vtp.desktop.model.core.design.IDesignElementConnectionPoint;
import org.eclipse.vtp.desktop.model.core.design.IExitBroadcastReceiver;
import org.eclipse.vtp.desktop.model.interactive.core.content.ContentLoadingManager;
import org.eclipse.vtp.desktop.model.interactive.core.input.InputLoadingManager;
import org.eclipse.vtp.desktop.model.interactive.core.internal.mediadefaults.WorkspaceMediaDefaultSettings;
import org.eclipse.vtp.framework.common.IDateObject;
import org.eclipse.vtp.framework.common.configurations.BrandConfiguration;
import org.eclipse.vtp.framework.common.configurations.DataTypeConfiguration;
import org.eclipse.vtp.framework.common.configurations.FieldConfiguration;
import org.eclipse.vtp.framework.interactions.core.configurations.InputConfiguration;
import org.eclipse.vtp.framework.interactions.core.configurations.LanguageConfiguration;
import org.eclipse.vtp.framework.interactions.core.configurations.MediaConfiguration;
import org.eclipse.vtp.framework.interactions.core.configurations.MediaProviderBindingConfiguration;
import org.eclipse.vtp.framework.interactions.core.configurations.MediaProviderConfiguration;
import org.eclipse.vtp.framework.interactions.core.configurations.MetaDataConfiguration;
import org.eclipse.vtp.framework.interactions.core.configurations.MetaDataItemConfiguration;
import org.eclipse.vtp.framework.interactions.core.configurations.OutputCase;
import org.eclipse.vtp.framework.interactions.core.configurations.OutputConfiguration;
import org.eclipse.vtp.framework.interactions.core.configurations.OutputContent;
import org.eclipse.vtp.framework.interactions.core.configurations.OutputNode;
import org.eclipse.vtp.framework.interactions.core.configurations.OutputSwitch;
import org.eclipse.vtp.framework.interactions.core.configurations.PropertyConfiguration;
import org.eclipse.vtp.framework.interactions.core.configurations.PropertyConfiguration.Value;
import org.eclipse.vtp.framework.interactions.core.configurations.SharedContentConfiguration;
import org.eclipse.vtp.framework.interactions.core.media.Content;
import org.eclipse.vtp.framework.interactions.core.media.IFormatter;
import org.eclipse.vtp.framework.interactions.core.media.InputGrammar;
import org.eclipse.vtp.framework.util.Guid;
import org.eclipse.vtp.framework.util.XMLUtilities;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Constructor that builds process definitions from projects.
 * 
 * @author Lonnie Pryor
 */
public class DefinitionBuilder implements IDefinitionBuilder {

	/** The call design document. */
	private final DesignReference[] callDesigns;
	/** The document builder to use. */
	private final DocumentBuilder builder;
	/** The process definition document. */
	private final Document definition;
	/** The services element. */
	private final Element servicesElement;
	/** The actions element. */
	private final Element actionsElement;
	/** The observers element. */
	private final Element observersElement;
	/** The transitions element. */
	private final Element transitionsElement;

	private FlowModel mainModel = null;
	private Map<String, FlowModel> dialogModels = new HashMap<String, FlowModel>();
	private IWorkflowProject project;

	/**
	 * Creates a new DefinitionBuilder.
	 * 
	 * @param callDesigns
	 *            The call design document.
	 * @param builder
	 *            The document builder to use.
	 * @param project
	 *            The project being built.
	 * @param brands
	 *            The brand tree.
	 * @param formatterIDsByLanguage
	 *            The formatter IDs by language.
	 * @param resourceManagerIDsByLanguage
	 *            The resource manager IDs by language.
	 * @throws Exception
	 *             If the process cannot be built.
	 */
	public DefinitionBuilder(DesignReference[] callDesigns, DocumentBuilder builder,
			IWorkflowProject project,
			Map<String, String> formatterIDsByLanguage,
			Map<String, String> resourceManagerIDsByLanguage,
			Map<String, List<String>> languageMapping) throws Exception {
		this.project = project;
		this.callDesigns = callDesigns;
		this.builder = builder;
		this.definition = builder.newDocument();
		Element definitionElement = definition.createElementNS(
				NAMESPACE_URI_PROCESS_DEFINITION, "process:definition"); //$NON-NLS-1$
		definition.appendChild(definitionElement);
		servicesElement = definition.createElementNS(
				NAMESPACE_URI_PROCESS_DEFINITION, "process:services"); //$NON-NLS-1$
		actionsElement = definition.createElementNS(
				NAMESPACE_URI_PROCESS_DEFINITION, "process:actions"); //$NON-NLS-1$
		observersElement = definition.createElementNS(
				NAMESPACE_URI_PROCESS_DEFINITION, "process:observers"); //$NON-NLS-1$
		transitionsElement = definition.createElementNS(
				NAMESPACE_URI_PROCESS_DEFINITION, "process:transitions"); //$NON-NLS-1$
		definitionElement.appendChild(servicesElement);
		definitionElement.appendChild(actionsElement);
		definitionElement.appendChild(observersElement);
		definitionElement.appendChild(transitionsElement);
		buildServices(project.getUnderlyingProject(), project.getBrandManager()
				.getDefaultBrand(), formatterIDsByLanguage,
				resourceManagerIDsByLanguage, languageMapping);
		buildFlow(project.getWorkflowEntries());
	}

	/**
	 * Returns the process definition document.
	 * 
	 * @return The process definition document.
	 */
	public Document getDefinition() {
		return definition;
	}

	/**
	 * Builds the services in the process.
	 * 
	 * @param project
	 *            The project being built.
	 * @param brands
	 *            The brand tree.
	 * @param formatterIDsByLanguage
	 *            The formatter IDs by language.
	 * @param resourceManagerIDsByLanguage
	 *            The resource manager IDs by language.
	 * @throws Exception
	 *             If the services cannot be built.
	 */
	private void buildServices(IProject project, IBrand defaultBrand,
			Map<String, String> formatterIDsByLanguage,
			Map<String, String> resourceManagerIDsByLanguage, Map<String, List<String>> languageMapping) throws Exception {
		// Build the brand configurations.
		Element brandRegistryElement = newServiceElement(//
		"org.eclipse.vtp.framework.common.services.brand-registry"); //$NON-NLS-1$
		Element defaultBrandElement = definition.createElementNS(
				NAMESPACE_URI_COMMON, "common:brand"); //$NON-NLS-1$
		BrandConfiguration defaultBrandConfiguration = new BrandConfiguration();
		defaultBrandConfiguration.setId(defaultBrand.getId());
		defaultBrandConfiguration.setName(defaultBrand.getName());
		buildBrandConfigurations(defaultBrand, defaultBrandConfiguration);
		defaultBrandConfiguration.save(defaultBrandElement);
		brandRegistryElement.appendChild(defaultBrandElement);
		// Build the data type configurations.
		Element dataTypeRegistryElement = newServiceElement(//
		"org.eclipse.vtp.framework.common.services.data-type-registry"); //$NON-NLS-1$
		IResource[] dataTypeFiles = project.getFolder("Business Objects") //$NON-NLS-1$
				.members();
		for (int i = 0; i < dataTypeFiles.length; ++i) {
			Document dataTypeDoc = builder.parse(dataTypeFiles[i].getLocation()
					.toFile());
			Element dataTypeElement = definition.createElementNS(
					NAMESPACE_URI_COMMON, "common:data-type"); //$NON-NLS-1$
			DataTypeConfiguration dataType = new DataTypeConfiguration();
			dataType.setName(dataTypeDoc.getDocumentElement().getAttribute(
					"name")); //$NON-NLS-1$
			if (dataTypeDoc.getDocumentElement().hasAttribute("primary")) //$NON-NLS-1$
				dataType.setPrimaryField(dataTypeDoc.getDocumentElement()
						.getAttribute("primary")); //$NON-NLS-1$
			NodeList list = ((Element) dataTypeDoc.getDocumentElement()
					.getElementsByTagName("fields").item(0)). //$NON-NLS-1$
					getElementsByTagName("field"); //$NON-NLS-1$
			for (int j = 0; j < list.getLength(); ++j) {
				FieldConfiguration field = new FieldConfiguration();
				Element fieldElement = (Element) list.item(j);
				field.setName(fieldElement.getAttribute("name")); //$NON-NLS-1$
				Element fieldTypeElement = (Element) fieldElement
						.getElementsByTagName("data-type").item(0); //$NON-NLS-1$
				if (fieldTypeElement.getAttributeNode("type") == null) // legacy
																		// support
				{
					String style = ((Element) fieldTypeElement
							.getElementsByTagName("style").item(0)) //$NON-NLS-1$
							.getAttribute("value"); //$NON-NLS-1$
					int styleBits = 0;
					try
					{
						styleBits = Integer.parseInt(style);
					}
					catch(Exception ex)
					{
					}
					if ((styleBits & 1) == 1) //$NON-NLS-1$
						field.setType("Array"); //$NON-NLS-1$
					else {
						String typeStr = XMLUtilities.getElementTextData(
								(Element) fieldTypeElement
										.getElementsByTagName("type").item(0),
								true);
						if ("DateTime".equals(typeStr))
							typeStr = IDateObject.TYPE_NAME;
						field.setType(typeStr); //$NON-NLS-1$
					}
				} else {
					String typeStr = fieldTypeElement.getAttribute("type");
					if (typeStr.indexOf(':') != -1) {
						typeStr = typeStr.substring(typeStr.indexOf(':') + 1);
					}
					if ("DateTime".equals(typeStr))
						typeStr = IDateObject.TYPE_NAME;
					field.setType(typeStr);
				}
				if (fieldElement.getAttribute("initialValue").length() > 0) //$NON-NLS-1$
					field.setInitialValue(fieldElement
							.getAttribute("initialValue")); //$NON-NLS-1$
				field.setSecured(Boolean.parseBoolean(fieldElement
						.getAttribute("secured")));
				dataType.addField(field);
			}
			dataType.save(dataTypeElement);
			dataTypeRegistryElement.appendChild(dataTypeElement);
		}
		// Build the language and media provider registries.
		Element languageRegistryElement = newServiceElement(//
		"org.eclipse.vtp.framework.interactions.core.services.language-registry"); //$NON-NLS-1$
		for(Map.Entry<String, List<String>> entry : languageMapping.entrySet())
		{
			for(String lang : entry.getValue())
			{
				// Build the language entry.
				Element languageElement = definition.createElementNS(
						NAMESPACE_URI_INTERACTIONS_CORE, "interactions:language"); //$NON-NLS-1$
				LanguageConfiguration language = new LanguageConfiguration();
				language.setID(lang);
				language.setInteractionType(entry.getKey());
				language.save(languageElement);
				languageRegistryElement.appendChild(languageElement);
			}
		}
		Element mediaProviderRegistryElement = newServiceElement(//
		"org.eclipse.vtp.framework.interactions.core.services.media-provider-registry"); //$NON-NLS-1$
		for (Map.Entry<String, String> entry : formatterIDsByLanguage
				.entrySet()) {
			String languageID = entry.getKey();
			String formatterID = entry.getValue();
			String resourceManagerID = resourceManagerIDsByLanguage
					.get(languageID);
			IFormatter formatter = null;
			FormatterRegistration reg = FormatterRegistrationManager
					.getInstance().getFormatter(formatterID);
			if (reg != null) {
				formatter = reg.getFormatter();
			}
			// Build the media provider entry.
			Element mediaProviderElement = definition.createElementNS(
					NAMESPACE_URI_INTERACTIONS_CORE,
					"interactions:media-provider"); //$NON-NLS-1$
			MediaProviderConfiguration mediaProvider = new MediaProviderConfiguration(
					ContentLoadingManager.getInstance());
			mediaProvider.setID(resourceManagerID);
			mediaProvider.setFormatterID(formatterID);
			mediaProvider.setResourceManagerID(resourceManagerID);
			loadSharedContent(project, mediaProvider);
			mediaProvider.save(mediaProviderElement);
			mediaProviderRegistryElement.appendChild(mediaProviderElement);
			Element mediaProviderBindingElement = definition.createElementNS(
					NAMESPACE_URI_INTERACTIONS_CORE,
					"interactions:media-provider-binding"); //$NON-NLS-1$
			MediaProviderBindingConfiguration mediaProviderBinding = //
			new MediaProviderBindingConfiguration();
			mediaProviderBinding.setKey(languageID);
			mediaProviderBinding.setMediaProviderID(mediaProvider.getID());
			mediaProviderBinding.save(mediaProviderBindingElement);
			mediaProviderRegistryElement
					.appendChild(mediaProviderBindingElement);
		}
	}

	/**
	 * Loads the voice.xml file into the media provider config.
	 * 
	 * @param processProject
	 *            The process referencing the voice.
	 * @param mediaProvider
	 *            The media provider to create.
	 * @throws Exception
	 *             If the voice cannot be loaded.
	 */
	private void loadSharedContent(IProject processProject,
			MediaProviderConfiguration mediaProvider) throws Exception {
		IProject voiceProject = processProject.getWorkspace().getRoot()
				.getProject(mediaProvider.getResourceManagerID());
		IFile xmlFile = voiceProject.getFile("Voice.xml");
		String uri = "http://eclipse.org/vtp/xml/media/voice#1.0"; //$NON-NLS-1$
		if (!xmlFile.exists()) {
			xmlFile = voiceProject.getFile("Author.xml");
			uri = "http://eclipse.org/vtp/xml/media/author#1.0"; //$NON-NLS-1$
		}
		Document voiceDocument = builder.parse(xmlFile.getLocation().toFile()); //$NON-NLS-1$
		NodeList list = ((Element) voiceDocument.getDocumentElement()
				.getElementsByTagNameNS(uri, "shared-content").item(0)) //$NON-NLS-1$
				.getChildNodes();
		for (int i = 0; i < list.getLength(); ++i) {
			if (!(list.item(i) instanceof Element))
				continue;
			Element element = (Element) list.item(i);
			ContentLoadingManager contentFactory = ContentLoadingManager
					.getInstance();
			SharedContentConfiguration sharedContent = new SharedContentConfiguration(
					contentFactory);
			sharedContent.setName(element.getAttributeNS(uri, "item-name")); //$NON-NLS-1$
			sharedContent.setContent(contentFactory.loadContent(element));
			mediaProvider.addSharedContent(sharedContent);
		}
	}

	/**
	 * Builds the configurations for the specified brand tree.
	 * 
	 * @param brands
	 *            The brand tree to build the configurations for.
	 * @param config
	 *            The configuration created for the brand tree.
	 */
	private void buildBrandConfigurations(IBrand brand,
			BrandConfiguration config) {
		for (IBrand child : brand.getChildBrands()) {
			BrandConfiguration childConfig = new BrandConfiguration();
			childConfig.setId(child.getId());
			childConfig.setName(child.getName());
			config.addChild(childConfig);
			buildBrandConfigurations(child, childConfig);
		}
	}

	/**
	 * Builds the flow elements of the process.
	 * 
	 * @throws Exception
	 *             If the flow cannot be built.
	 */
	private void buildFlow(List<IWorkflowEntry> entries) throws Exception {
		mainModel = new FlowModel();
		for (DesignReference callDesign : callDesigns) {
			Element documentElement = callDesign.getXMLDocument().getDocumentElement();
			String xmlVersion = documentElement.getAttribute("xml-version");
			if (!xmlVersion.equals("4.0.0"))
				throw new Exception("Incorrect model xml version.");
			callDesign.getDesignDocument().becomeWorkingCopy();
			buildFlowModel(
					mainModel,
					(Element) ((Element) documentElement.getElementsByTagName(
							"workflow").item(0)) //$NON-NLS-1$
							.getElementsByTagName("model").item(0), CONTEXT_APPLICATION, null); //$NON-NLS-1$
			NodeList dialogs = ((Element) documentElement.getElementsByTagName(
					"dialogs").item(0)) //$NON-NLS-1$
					.getElementsByTagName("workflow"); //$NON-NLS-1$
			for (int i = 0; i < dialogs.getLength(); ++i) {
				Element dialog = (Element) dialogs.item(i);
				FlowModel dialogModel = new FlowModel();
				String dialogId = dialog.getAttribute("id");
				buildFlowModel(dialogModel, (Element) dialog
						.getElementsByTagName("model") //$NON-NLS-1$
						.item(0), CONTEXT_DIALOG, dialogId); //$NON-NLS-1$
				System.out.println("Gathering Receivers for Dialog: " + callDesign.getDesignDocument().getUnderlyingFile().getFullPath().toString() + ":" + dialogId);
				List<IDesignElement> receiverElements = gatherReceivers(callDesign.getDesignDocument().getDialogDesign(dialogId));
				bindDialogBroadcastReceivers(dialogModel, callDesign.getDesignDocument().getDialogDesign(dialogId), receiverElements, dialogId);
				dialogModels.put(dialogId, dialogModel);
			}
			List<IDesignElement> receiverElements = gatherReceivers(callDesign.getDesignDocument().getMainDesign());
			bindBroadcastReceivers(mainModel, callDesign.getDesignDocument().getMainDesign(), receiverElements);
		}
		StringBuilder entryIds = new StringBuilder();
		for (IWorkflowEntry entry : entries) {
			mainModel.addEntry((FlowElement) mainModel.getElementsById().get(
					entry.getId()));
			entryIds.append(entry.getId()).append(',');
		}
		if (entryIds.length() > 0)
			entryIds.setLength(entryIds.length() - 1);
		definition.getDocumentElement().setAttribute("start",
				entryIds.toString());
		LinkedList<FlowElement> toProcess = new LinkedList<FlowElement>(
				mainModel.getEntryList());
		while (!toProcess.isEmpty())
			for (FlowElement element : toProcess.removeFirst().process())
				toProcess.addLast(element);
	}

	/**
	 * Builds the flow elements of a model in the process.
	 * 
	 * @param modelElement
	 *            The model element to process.
	 * @param context
	 *            The context the parsing is occurring in.
	 * @param dialogID
	 *            The ID of the dialog that contains the model or
	 *            <code>null</code>.
	 * @return The element that starts the supplied flow.
	 * @throws Exception
	 *             If the flow cannot be built.
	 */
	private FlowModel buildFlowModel(FlowModel flowModel, Element modelElement,
			int context, String dialogID) throws Exception {
		NodeList elements = ((Element) modelElement.getElementsByTagName(
				"elements") //$NON-NLS-1$
				.item(0)).getChildNodes();
		for (int i = 0; i < elements.getLength(); ++i) {
			if (!(elements.item(i) instanceof Element))
				continue;
			Element element = (Element) elements.item(i);
			String id = element.getAttribute("id"); //$NON-NLS-1$
			String name = element.getAttribute("name");
			FlowElement flowElement = null;
			String elementType = element.getAttribute("type");
			Properties elementProperties = new Properties();
			if (context == CONTEXT_DIALOG) {
				id = dialogID + ":" + id; //$NON-NLS-1$
				elementProperties.setProperty("DIALOG_ID", dialogID);
			}
			NodeList propertiesElementList = element
					.getElementsByTagName("properties");
			if (propertiesElementList.getLength() > 0) {
				Element propertiesElement = (Element) propertiesElementList
						.item(0);
				NodeList propertyElementList = propertiesElement
						.getElementsByTagName("property");
				for (int pe = 0; pe < propertyElementList.getLength(); pe++) {
					Element propertyElement = (Element) propertyElementList
							.item(pe);
					elementProperties.setProperty(
							propertyElement.getAttribute("name"),
							propertyElement.getAttribute("value"));
				}
			}
			Element elementConfigurationElement = (Element) element
					.getElementsByTagName("configuration").item(0);
			System.err.println("Adding flow element: " + elementType);
			elementProperties.store(System.err, "");
			System.err.println();
			flowElement = new FlowElement(context, id, name, elementType,
					elementProperties, elementConfigurationElement);
			flowModel.addElement(flowElement);
		}
		NodeList connectors = ((Element) modelElement.getElementsByTagName(
				"connectors") //$NON-NLS-1$
				.item(0)).getElementsByTagName("connector"); //$NON-NLS-1$
		for (int i = 0; i < connectors.getLength(); ++i) {
			Element connector = (Element) connectors.item(i);
			String originID = connector.getAttribute("origin"); //$NON-NLS-1$
			if (context == CONTEXT_DIALOG)
				originID = dialogID + ":" + originID; //$NON-NLS-1$
			FlowElement origin = (FlowElement) flowModel.getElementsById().get(
					originID);
			if (origin == null)
				continue;
			String destinationID = connector.getAttribute("destination"); //$NON-NLS-1$
			if (context == CONTEXT_DIALOG)
				destinationID = dialogID + ":" + destinationID; //$NON-NLS-1$
			FlowElement destination = (FlowElement) flowModel.getElementsById()
					.get(destinationID);
			if (destination == null)
				continue;
			NodeList records = connector.getElementsByTagName("record"); //$NON-NLS-1$
			for (int j = 0; j < records.getLength(); ++j)
				origin.addResultPath(
						((Element) records.item(j)).getAttribute("sourcename"), destination); //$NON-NLS-1$
		}
		return flowModel;
	}
	
	private List<IDesignElement> gatherReceivers(IDesign design)
	{
		List<IDesignElement> list = new ArrayList<IDesignElement>();
		design.getDocument().becomeWorkingCopy();
		for(IDesignElement element : design.getDesignElements())
		{
			System.out.println("Inspecting: " + element.getId() + ":" + element.getName());
			if(element.getExitBroadcastReceivers().isEmpty())
				continue;
			list.add(element);
			System.err.println("has receivers: " + element.getId() + ":" + element.getName());
		}
		return list;
	}
	
	private void bindBroadcastReceivers(FlowModel model, IDesign design, List<IDesignElement> elements)
	{
		design.getDocument().becomeWorkingCopy();
		for(IDesignElement element : design.getDesignElements())
		{
			List<IDesignElementConnectionPoint> records = element.getConnectorRecords();
			for(IDesignElementConnectionPoint point : records)
			{
				if(point.getDesignConnector() == null)
				{
					System.err.println("Disconnected exit: " + element.getId() + ":" + element.getName() + ":" + point.getName());
					for(IDesignElement receiverElement : elements)
					{
						//must avoid looping of disconnected receivers
						if(element.getId().equals(receiverElement.getId()))
							continue;
						for(IExitBroadcastReceiver receiver : receiverElement.getExitBroadcastReceivers())
						{
							System.err.print("Matching: " + point.getName() + "->" + receiver.getExitPattern() + "...");
							if(receiver.getExitPattern().equals(point.getName()))
							{
								System.err.println("true");
								IFlowElement origin = model.flowElementsById.get(element.getId());
								IFlowElement target = model.flowElementsById.get(receiverElement.getId());
								if(origin != null && target != null)
								{
									((FlowElement)origin).addResultPath(point.getName(), (FlowElement)target);
								}
							}
							else
								System.err.println("false");
						}
					}
				}
			}
		}
	}

	private void bindDialogBroadcastReceivers(FlowModel model, IDesign design, List<IDesignElement> elements, String dialogId)
	{
		design.getDocument().becomeWorkingCopy();
		for(IDesignElement element : design.getDesignElements())
		{
			List<IDesignElementConnectionPoint> records = element.getConnectorRecords();
			for(IDesignElementConnectionPoint point : records)
			{
				if(point.getDesignConnector() == null)
				{
					System.err.println("Disconnected exit: " + dialogId + ":" + element.getId() + ":" + element.getName() + ":" + point.getName());
					for(IDesignElement receiverElement : elements)
					{
						//must avoid looping of disconnected receivers
						if(element.getId().equals(receiverElement.getId()))
							continue;
						for(IExitBroadcastReceiver receiver : receiverElement.getExitBroadcastReceivers())
						{
							System.err.print("Matching: " + point.getName() + "->" + receiver.getExitPattern() + "...");
							if(receiver.getExitPattern().equals(point.getName()))
							{
								System.err.println("true");
								IFlowElement origin = model.flowElementsById.get(dialogId + ":" + element.getId());
								IFlowElement target = model.flowElementsById.get(dialogId + ":" + receiverElement.getId());
								if(origin != null && target != null)
								{
									((FlowElement)origin).addResultPath(point.getName(), (FlowElement)target);
								}
							}
							else
								System.err.println("false");
						}
					}
				}
			}
		}
	}

	/**
	 * Creates a new service element with the specified ID.
	 * 
	 * @param serviceID
	 *            The ID of the service.
	 * @return A new service element with the specified ID.
	 */
	private Element newServiceElement(String serviceID) {
		Element element = definition.createElementNS(
				NAMESPACE_URI_PROCESS_DEFINITION, "process:service"); //$NON-NLS-1$
		element.setAttribute("id", serviceID); //$NON-NLS-1$
		servicesElement.appendChild(element);
		return element;
	}

	/**
	 * Creates a new action element with the specified IDs.
	 * 
	 * @param actionID
	 *            The ID of the action.
	 * @param actionName
	 *            The display name of the action.
	 * @param actionDescriptorID
	 *            The ID of the action descriptor.
	 * @return A new action element with the specified IDs.
	 */
	private Element newActionElement(String actionID, String actionName,
			String actionDescriptorID) {
		Element element = definition.createElementNS(
				NAMESPACE_URI_PROCESS_DEFINITION, "process:action"); //$NON-NLS-1$
		element.setAttribute("id", actionID); //$NON-NLS-1$
		element.setAttribute("name", actionName); //$NON-NLS-1$
		element.setAttribute("descriptor-id", actionDescriptorID); //$NON-NLS-1$
		actionsElement.appendChild(element);
		return element;
	}

	/**
	 * Creates a new observer element with the specified IDs.
	 * 
	 * @param observerID
	 *            The ID of the observer.
	 * @param observerDescriptorID
	 *            The ID of the observer descriptor.
	 * @return A new observer element with the specified IDs.
	 */
	private Element newObserverElement(String observerID,
			String observerDescriptorID) {
		Element element = definition.createElementNS(
				NAMESPACE_URI_PROCESS_DEFINITION, "process:observer"); //$NON-NLS-1$
		element.setAttribute("id", observerID); //$NON-NLS-1$
		element.setAttribute("descriptor-id", observerDescriptorID); //$NON-NLS-1$
		observersElement.appendChild(element);
		return element;
	}

	/**
	 * Creates a new transition element with the specified ID and path.
	 * 
	 * @param actionID
	 *            The ID of the action that the transition follows.
	 * @param path
	 *            The exit path this transition applies to.
	 * @return A new transition element with the specified ID and path.
	 */
	private Element newAfterTransitionElement(String actionID, String path) {
		Element element = definition.createElementNS(
				NAMESPACE_URI_PROCESS_DEFINITION, "process:after"); //$NON-NLS-1$
		element.setAttribute("action", actionID); //$NON-NLS-1$
		element.setAttribute("path", path); //$NON-NLS-1$
		transitionsElement.appendChild(element);
		return element;
	}

	/**
	 * Base representations of elements in the UI model.
	 * 
	 * @author Lonnie Pryor
	 */
	public class FlowElement implements IFlowElement {
		/** The ID of this element. */
		final String id;
		/** The name of this element. */
		final String name;
		/** The configuration for this element. */
		final Element configuration;
		/** The result paths for this element. */
		final Map<String, FlowElement> resultPaths = new HashMap<String, FlowElement>();
		/** True if this element has been processed. */
		boolean processed = false;
		Properties properties = null;
		private int context = 0;
		private FlowModel model = null;
		private String elementType = null;
		IConfigurationExporter exporter = null;

		/**
		 * Creates a new FlowElement.
		 * 
		 * @param context
		 * @param id
		 *            The ID of this element.
		 * @param name
		 * @param properties
		 * @param configuration
		 *            The configuration of this element.
		 */
		FlowElement(int context, String id, String name, String type,
				Properties properties, Element configuration) {
			this.context = context;
			this.id = id;
			this.name = name;
			this.properties = properties;
			this.configuration = configuration;
			this.elementType = type;
			exporter = ExportCore.getConfigurationExporter(elementType);
		}

		public String getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public int getContext() {
			return context;
		}

		public Properties getProperties() {
			return properties;
		}

		public Element getConfiguration() {
			return configuration;
		}

		public FlowModel getModel() {
			return model;
		}

		void setModel(FlowModel model) {
			this.model = model;
		}

		/**
		 * Configures a result path on this element.
		 * 
		 * @param path
		 *            The result path to configure.
		 * @param next
		 *            The element the path points at.
		 */
		void addResultPath(String path, FlowElement next) {
			resultPaths.put(path, next);
		}

		public FlowElement getResultPath(String path) {
			return resultPaths.get(path);
		}

		/**
		 * Returns the ID that should be used for targeting this element.
		 * 
		 * @param afterTransitionElement
		 *            The transition to configure with additional observer
		 *            notifications.
		 * @return The ID that should be used for targeting this element.
		 */
		public String getTargetID(Element afterTransitionElement) {
			if (exporter != null)
				return exporter.getTargetId(this, afterTransitionElement);
			return getDefaultTargetId(afterTransitionElement);
		}

		public String getDefaultTargetId(Element afterTansitionElement) {
			return id;
		}

		/**
		 * Writes this element to the process definition.
		 * 
		 * @return The next elements to be processed.
		 */
		final List<FlowElement> process() {
			if (processed)
				return Collections.emptyList();
			processed = true;
			buildAction();
			List<FlowElement> nextElements = new ArrayList<FlowElement>(
					resultPaths.size());
			for (Map.Entry<String, FlowElement> entry : resultPaths.entrySet()) {
				String designPath = entry.getKey();
				String processPath = mapResultPath(designPath);
				Element afterTrasition = newAfterTransitionElement(id,
						processPath);
				buildObservers(designPath, afterTrasition);
				String target = entry.getValue().getTargetID(afterTrasition);
				if (target == null)
					transitionsElement.removeChild(afterTrasition);
				else {
					afterTrasition.setAttribute("target", target); //$NON-NLS-1$
					Map<String, IFlowElement> flowElementsById = mainModel
							.getElementsById();
					if (target.indexOf(':') != -1) {
						flowElementsById = dialogModels.get(
								target.substring(0, target.indexOf(':')))
								.getElementsById();
					}
					nextElements
							.add((FlowElement) flowElementsById.get(target));
				}
			}
			return nextElements;
		}

		/**
		 * Writes the action this element represents.
		 */
		void buildAction() {
			if (exporter != null) {
				String actionId = exporter.getActionId(this);
				if (actionId != null) {
					Element actionElement = newActionElement(id, name, actionId);
					exporter.exportConfiguration(this, actionElement);
				}
			}
		}

		public boolean isEntryPoint() {
			if (exporter != null) {
				return exporter.isEntryPoint(this);
			}
			return false;
		}

		/**
		 * Writes the observers bound to the specified result path.
		 * 
		 * @param designPath
		 *            The path to write the observers for.
		 * @param afterTrasition
		 *            The element to write the notifications to.
		 */
		public void buildObservers(String designPath, Element afterTrasition) {
			String uri = "http://eclipse.org/vtp/xml/configuration/attacheddata"; //$NON-NLS-1$
			NodeList bindingsList = configuration
					.getElementsByTagName("managed-config"); //$NON-NLS-1$
			Element attachedData = null;
			for (int i = 0; attachedData == null
					&& i < bindingsList.getLength(); ++i) {
				Element bindings = (Element) bindingsList.item(i);
				if (bindings.getAttribute("type").equals(
						"org.eclipse.vtp.configuration.attacheddata")) {
					NodeList itemList = bindings.getElementsByTagNameNS(uri,
							"attached-data-binding"); //$NON-NLS-1$
					for (int j = 0; attachedData == null
							&& j < itemList.getLength(); ++j) {
						Element item = (Element) itemList.item(j);
						if (designPath.equals(item.getAttribute("name"))) //$NON-NLS-1$
							attachedData = item;
					}
				}
			}
			if (attachedData == null)
				return;
			String observerID = Guid.createGUID();
			MetaDataConfiguration config = new MetaDataConfiguration();
			NodeList itemList = attachedData
					.getElementsByTagNameNS(uri, "item"); //$NON-NLS-1$
			int count = 0;
			for (int i = 0; i < itemList.getLength(); ++i) {
				Element item = (Element) itemList.item(i);
				NodeList entryList = item.getElementsByTagNameNS(uri, "entry"); //$NON-NLS-1$
				if (entryList.getLength() > 0) {
					MetaDataItemConfiguration[] metaDataItems = new MetaDataItemConfiguration[entryList
							.getLength()];
					for (int j = 0; j < entryList.getLength(); ++j) {
						Element entry = (Element) entryList.item(j);
						MetaDataItemConfiguration metaDataItem = new MetaDataItemConfiguration();
						metaDataItem.setName(entry.getAttribute("name")); //$NON-NLS-1$
						if ("variable".equalsIgnoreCase(entry.getAttribute("type"))) //$NON-NLS-1$
							metaDataItem.setVariableValue(entry
									.getAttribute("value")); //$NON-NLS-1$
						else if ("expression".equalsIgnoreCase(entry.getAttribute("type"))) //$NON-NLS-1$
							metaDataItem.setExpressionValue(
									entry.getAttribute("value"), //$NON-NLS-1$
									"JavaScript"); //$NON-NLS-1$
						else if("map".equalsIgnoreCase(entry.getAttribute("type")))
							metaDataItem.setMapValue(entry.getAttribute("value"));
						else
							metaDataItem.setStaticValue(entry
									.getAttribute("value")); //$NON-NLS-1$
						metaDataItems[j] = metaDataItem;
					}
					config.setItem(item.getAttribute("key"), metaDataItems); //$NON-NLS-1$
					++count;
				}
			}
			if (count == 0)
				return;
			Element observerElement = newObserverElement(observerID,
					"org.eclipse.vtp.framework.interactions.core.observers.meta-data-message"); //$NON-NLS-1$
			Element configElement = definition.createElementNS(
					NAMESPACE_URI_INTERACTIONS_CORE, "interactions:meta-data"); //$NON-NLS-1$
			config.save(configElement);
			observerElement.appendChild(configElement);
			Element notify = afterTrasition.getOwnerDocument().createElementNS(
					NAMESPACE_URI_PROCESS_DEFINITION, "process:notify"); //$NON-NLS-1$
			notify.setAttribute("observer", observerID); //$NON-NLS-1$
			afterTrasition.appendChild(notify);
		}

		/**
		 * Maps a result path from the designer to a path in the process engine.
		 * 
		 * @param input
		 *            The path in the designer.
		 * @return The path in the process engine.
		 */
		String mapResultPath(String input) {
			if (exporter != null) {
				if (input.equals(exporter.getDefaultPath(this)))
					return "default";
				return exporter.translatePath(this, input);
			}
			return "Continue".equals(input) ? "default" //$NON-NLS-1$ //$NON-NLS-2$
					: input;
		}

		/**
		 * Loads the bindings from this element's configuration into a media
		 * configuration.
		 * 
		 * @return The loaded binding information.
		 */
		public MediaConfiguration loadMediaBindings(String elementTypeId) {
			NodeList managedConfigList = configuration
					.getElementsByTagName("managed-config");
			Element genericConfigElement = null;
			for (int i = 0; i < managedConfigList.getLength(); i++) {
				Element managedConfigElement = (Element) managedConfigList
						.item(i);
				if (managedConfigElement.getAttribute("type").equals(
						"org.eclipse.vtp.configuration.generic")) {
					genericConfigElement = managedConfigElement;
					break;
				}
			}
			if (genericConfigElement == null)
				return null;
			return loadMediaBindings(elementTypeId, genericConfigElement);
		}

		public MediaConfiguration loadMediaBindings(String elementTypeId,
				Element genericConfigElement) {
			MediaConfiguration result = new MediaConfiguration(
					ContentLoadingManager.getInstance(),
					InputLoadingManager.getInstance());
			Map<String, OutputConfiguration> outputConfigurations = new HashMap<String, OutputConfiguration>();
			Map<String, InputConfiguration> inputConfigurations = new HashMap<String, InputConfiguration>();
			Map<String, PropertyConfiguration> propertyConfigurations = new HashMap<String, PropertyConfiguration>();
			NodeList interactionBindingList = genericConfigElement
					.getElementsByTagName("interaction-binding");
			for (int i = 0; i < interactionBindingList.getLength(); i++) {
				Element interactionBinding = (Element) interactionBindingList
						.item(i);
				String interactionType = interactionBinding
						.getAttribute("type");
				NodeList namedBindingList = interactionBinding
						.getElementsByTagName("named-binding");
				for (int nb = 0; nb < namedBindingList.getLength(); nb++) {
					Element namedBinding = (Element) namedBindingList.item(nb);
					String bindingName = namedBinding.getAttribute("name");
					NodeList languageBindingList = namedBinding
							.getElementsByTagName("language-binding");
					for (int lb = 0; lb < languageBindingList.getLength(); lb++) {
						Element languageBinding = (Element) languageBindingList
								.item(lb);
						String languageName = languageBinding
								.getAttribute("language");
						NodeList brandBindingList = languageBinding
								.getElementsByTagName("brand-binding");
						for (int bb = 0; bb < brandBindingList.getLength(); bb++) {
							Element brandBinding = (Element) brandBindingList
									.item(bb);
							String brandName = brandBinding
									.getAttribute("name");
							NodeList bindingItemList = brandBinding
									.getElementsByTagName("binding-item");
							if (bindingItemList.getLength() > 0) {
								Element bindingItemElement = (Element) bindingItemList
										.item(0);
								String bindingItemType = bindingItemElement
										.getAttribute("type");
								if ("org.eclipse.vtp.configuration.generic.items.prompt"
										.equals(bindingItemType)) {
									OutputConfiguration outputConfig = outputConfigurations
											.get(bindingName);
									if (outputConfig == null) {
										outputConfig = new OutputConfiguration(
												ContentLoadingManager
														.getInstance());
										outputConfigurations.put(bindingName,
												outputConfig);
									}
									outputConfig
											.setItem(
													brandName + interactionType
															+ languageName,
													loadOutputNodesFrom(bindingItemElement));
								}
								if ("org.eclipse.vtp.configuration.generic.items.grammar"
										.equals(bindingItemType)) {
									InputConfiguration inputConfig = inputConfigurations
											.get(bindingName);
									if (inputConfig == null) {
										inputConfig = new InputConfiguration(
												InputLoadingManager
														.getInstance());
										inputConfigurations.put(bindingName,
												inputConfig);
									}
									InputGrammar grammar = null;
									NodeList contents = bindingItemElement
											.getChildNodes();
									for (int k = 0; grammar == null
											&& k < contents.getLength(); ++k)
										if (contents.item(k) instanceof Element)
											grammar = InputLoadingManager
													.getInstance().loadInput(
															(Element) contents
																	.item(k));
									if (grammar != null)
										inputConfig.setItem(brandName
												+ interactionType
												+ languageName, grammar);
								}
								if ("org.eclipse.vtp.configuration.generic.items.property"
										.equals(bindingItemType)) {
									PropertyConfiguration propertyConfig = propertyConfigurations
											.get(bindingName);
									if (propertyConfig == null) {
										propertyConfig = new PropertyConfiguration();
										propertyConfigurations.put(bindingName,
												propertyConfig);
									}
									NodeList propertyValueList = bindingItemElement
											.getElementsByTagName("property-value");
									if (propertyValueList.getLength() > 0) {
										Element propertyValueElement = (Element) propertyValueList
												.item(0);
										try {
											propertyConfig
													.setItem(
															brandName
																	+ interactionType
																	+ languageName,
															propertyValueElement
																	.getAttribute("value-type"),
															XMLUtilities
																	.getElementTextData(
																			propertyValueElement,
																			true));
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
								}
							}
						}
					}
				}
			}
			for (Map.Entry<String, OutputConfiguration> entry : outputConfigurations
					.entrySet()) {
				result.setOutputConfiguration(entry.getKey(), entry.getValue());
			}
			for (Map.Entry<String, InputConfiguration> entry : inputConfigurations
					.entrySet()) {
				result.setInputConfiguration(entry.getKey(), entry.getValue());
			}
			for (int i = 0; i < interactionBindingList.getLength(); i++) {
				Element interactionBinding = (Element) interactionBindingList
						.item(i);
				String interactionType = interactionBinding
						.getAttribute("type");
				List<String> names = WorkspaceMediaDefaultSettings
						.getInstance().getDefaultSettingNames(elementTypeId,
								interactionType);
				for (String name : names) {
					System.err.print("Checking default value: " + name + "...");
					String defaultSetting = WorkspaceMediaDefaultSettings
							.getInstance()
							.getDefaultSetting(interactionType, elementTypeId,
									name).getValue();
					System.err.println(defaultSetting);
					if (defaultSetting != null) {
						PropertyConfiguration propertyConfig = propertyConfigurations
								.get(name);
						System.err.println("Current property config: "
								+ propertyConfig);
						if (propertyConfig == null) {
							propertyConfig = new PropertyConfiguration();
							propertyConfigurations.put(name, propertyConfig);
						}
						Value item = propertyConfig.getItem("Default",
								interactionType);
						System.err.println("Current property value: "
								+ (item == null ? "null" : (item.getType()
										+ ":" + item.getValue())));
						if (item == null)
							propertyConfig.setItem("Default", interactionType,
									"static", defaultSetting);
					}
				}
			}
			for (Map.Entry<String, PropertyConfiguration> entry : propertyConfigurations
					.entrySet()) {
				result.setPropertyConfiguration(entry.getKey(),
						entry.getValue());
			}
			return result;
		}

		private OutputNode[] loadOutputNodesFrom(
				Element element) {
			NodeList childElements = element.getChildNodes();
			List<Content> content = new LinkedList<Content>();
			List<OutputNode> nodes = new LinkedList<OutputNode>();
			for (int i = 0; i < childElements.getLength(); ++i) {
				if (childElements.item(i) instanceof Element) {
					Element childElement = (Element) childElements.item(i);
					if ("binding-branch".equals(childElement.getTagName())) {
						if (!content.isEmpty()) {
							OutputContent node = new OutputContent();
							node.setContent(content.toArray(new Content[content
									.size()]));
							content.clear();
							nodes.add(node);
						}
						OutputSwitch switchNode = new OutputSwitch();
						NodeList caseElements = childElement.getChildNodes();
						List<OutputCase> cases = new ArrayList<OutputCase>(
								caseElements.getLength());
						for (int j = 0; j < caseElements.getLength(); ++j) {
							if (caseElements.item(j) instanceof Element) {
								Element caseElement = (Element) caseElements
										.item(j);
								OutputCase caseNode = new OutputCase();
								caseNode.setScript(caseElement
										.getAttribute("condition"));
								caseNode.setScriptingLanguage("JavaScript");
								caseNode.setNodes(loadOutputNodesFrom(caseElement));
								cases.add(caseNode);
							}
						}
						switchNode
								.setCases(cases
										.toArray(new OutputCase[cases
												.size()]));
						nodes.add(switchNode);
					} else
						content.add(ContentLoadingManager.getInstance()
								.loadContent(childElement));
				}
			}
			if (!content.isEmpty()) {
				OutputContent node = new OutputContent();
				node.setContent(content.toArray(new Content[content.size()]));
				content.clear();
				nodes.add(node);
			}
			return nodes.toArray(new OutputNode[nodes
					.size()]);
		}
	}

	public class FlowModel implements IFlowModel {
		private List<FlowElement> entries = new ArrayList<FlowElement>();
		private Map<String, IFlowElement> flowElementsById = new HashMap<String, IFlowElement>();
		private List<FlowElement> flowElements = new ArrayList<FlowElement>();

		public FlowModel() {
		}

		public List<IFlowElement> getEntries() {
			return new ArrayList<IFlowElement>(entries);
		}

		public List<FlowElement> getEntryList() {
			return entries;
		}

		public void addEntry(FlowElement entry) {
			this.entries.add(entry);
		}
		
		public Map<String, IFlowElement> getElementsById() {
			return flowElementsById;
		}

		public List<FlowElement> getElements() {
			return flowElements;
		}

		public void addElement(IFlowElement flowElement) {
			FlowElement old = null;
			if ((old = (FlowElement) flowElementsById.put(flowElement.getId(),
					flowElement)) != null) {
				flowElements.remove(old);
				old.setModel(null);
			}
			FlowElement theFlowElement = (FlowElement) flowElement;
			flowElements.add(theFlowElement);
			theFlowElement.setModel(this);
			if (theFlowElement.isEntryPoint())
				entries.add(theFlowElement);
		}

		public DefinitionBuilder getDefinitionBuilder() {
			return DefinitionBuilder.this;
		}
	}

	public FlowModel getDialogModel(String dialogId) {
		return dialogModels.get(dialogId);
	}

	public FlowModel getMainModel() {
		return mainModel;
	}
}
