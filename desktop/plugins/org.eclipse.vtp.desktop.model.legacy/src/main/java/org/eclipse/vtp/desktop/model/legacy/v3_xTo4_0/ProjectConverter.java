package org.eclipse.vtp.desktop.model.legacy.v3_xTo4_0;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.vtp.desktop.model.core.IDesignDocument;
import org.eclipse.vtp.desktop.model.core.IDesignFolder;
import org.eclipse.vtp.desktop.model.core.IDesignItemContainer;
import org.eclipse.vtp.desktop.model.core.IDesignRootFolder;
import org.eclipse.vtp.desktop.model.core.IWorkflowProject;
import org.eclipse.vtp.desktop.model.core.WorkflowCore;
import org.eclipse.vtp.desktop.model.interactive.core.InteractionType;
import org.eclipse.vtp.desktop.model.interactive.core.InteractionTypeManager;
import org.eclipse.vtp.desktop.model.legacy.v3_xTo3_X.DocumentConverter;
import org.eclipse.vtp.desktop.projects.core.builder.VoiceApplicationFragmentNature;
import org.eclipse.vtp.desktop.projects.core.builder.VoiceApplicationNature;
import org.eclipse.vtp.framework.util.Guid;
import org.eclipse.vtp.framework.util.XMLWriter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@SuppressWarnings("deprecation")
public class ProjectConverter
{

	public ProjectConverter()
	{
	}

	public void convertProject(IProject project)
	{
		try
		{
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = builderFactory.newDocumentBuilder();
			if(project.hasNature(VoiceApplicationNature.NATURE_ID) ||
			   project.hasNature(VoiceApplicationFragmentNature.NATURE_ID))
			{
				//update the build path document
				IFile buildPath = project.getFile(".buildPath");
				Document buildPathDocument = builder.parse(buildPath.getContents());
				Document newBuildPathDocument = builder.getDOMImplementation().createDocument(null, "workflow-settings", null);
				Element bpRootElement = buildPathDocument.getDocumentElement();
				Element newBPRootElement = newBuildPathDocument.getDocumentElement();
				newBPRootElement.setAttribute("id", bpRootElement.getAttribute("id"));
				
				//copy brands over
				Element brandsElement = (Element)bpRootElement.getElementsByTagName("brands").item(0);
				Node newBrandsElement = newBuildPathDocument.importNode(brandsElement, true);
				newBPRootElement.appendChild(newBrandsElement);
				String defaultBrandId = ((Element)brandsElement.getElementsByTagName("brand").item(0)).getAttribute("id");
				
				//convert language support
				Element languageSupportElement = (Element)bpRootElement.getElementsByTagName("language-support").item(0);
				NodeList languageElementList = languageSupportElement.getElementsByTagName("language");
				Element interactionTypeSupportElement = newBuildPathDocument.createElement("interaction-type-support");
				newBPRootElement.appendChild(interactionTypeSupportElement);
				Element interactionTypeElement = newBuildPathDocument.createElement("interaction-type");
				interactionTypeElement.setAttribute("id", languageSupportElement.getAttribute("interaction-type"));
				InteractionType interactionType = InteractionTypeManager.getInstance().getType(languageSupportElement.getAttribute("interaction-type"));
				interactionTypeElement.setAttribute("name", interactionType == null ? "Unknown Interaction Type" : interactionType.getName());
				interactionTypeSupportElement.appendChild(interactionTypeElement);
				for(int le = 0; le < languageElementList.getLength(); le++)
				{
					Element languageElement = (Element)languageElementList.item(le);
					Element languageMappingElement = newBuildPathDocument.createElement("language-mapping");
					interactionTypeElement.appendChild(languageMappingElement);
					languageMappingElement.setAttribute("language", languageElement.getAttribute("name"));
					Element brandMappingElement = newBuildPathDocument.createElement("brand-mapping");
					languageMappingElement.appendChild(brandMappingElement);
					brandMappingElement.setAttribute("brand-id", defaultBrandId);
					brandMappingElement.setAttribute("media-project-id", languageElement.getAttribute("project"));
				}
				
				//copy media defaults over
				Element mediaDefaultsElement = (Element)bpRootElement.getElementsByTagName("media-defaults").item(0);
				Node newMediaDefaults = newBuildPathDocument.importNode(mediaDefaultsElement, true);
				newBPRootElement.appendChild(newMediaDefaults);
				
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				TransformerFactory transfactory = TransformerFactory.newInstance();
				Transformer t = transfactory.newTransformer();
				t.setOutputProperty(OutputKeys.METHOD, "xml");
				t.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
				t.setOutputProperty(OutputKeys.INDENT, "yes");
				t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
				t.transform(new DOMSource(newBuildPathDocument), new XMLWriter(baos).toXMLResult());
				buildPath.setContents(new ByteArrayInputStream(baos.toByteArray()), true, true, null);

				//This is a 3.x project and needs to be converted.
				//Step 1. Convert CallDesign.xml to 3.X format from any previous 3.x version
				IFile callDesignFile = project.getFile("CallDesign.xml");
				if(callDesignFile == null || !callDesignFile.exists())
				{
					callDesignFile = project.getFile("CallDesign.frag");
				}
				//make a backup of the original file
				InputStream in = callDesignFile.getContents();
				Document document = builder.parse(in);
				in.close();
				DocumentConverter documentConverter = new DocumentConverter();
				documentConverter.convertDocument(document);
				baos = new ByteArrayOutputStream();
				t.transform(new DOMSource(document), new XMLWriter(baos).toXMLResult());
				callDesignFile.setContents(new ByteArrayInputStream(baos.toByteArray()), true, true, null);

				//Step 2.  Restructure folders and move files around.
				IFolder designFolder = project.getFolder("Workflow Design");
				if(!designFolder.exists()) // shouldn't be there but just in case
				{
					designFolder.create(true, true, null);
				}
				IFolder webservicesFolder = project.getFolder("Web Services");
				if(webservicesFolder.exists())
					webservicesFolder.delete(true, null);
				webservicesFolder = project.getFolder("Webservices");
				webservicesFolder.create(true, true, null);
				
				//Step 3.  Group all the elements and canvases. We reload
				//the document to ensure no orphaned DOM elements are still
				//around.
				in = callDesignFile.getContents();
				document = builder.parse(in);
				in.close();
				List<CanvasRecord> canvasRecords = new LinkedList<CanvasRecord>();
				Element rootElement = document.getDocumentElement();
				NodeList nl = rootElement.getChildNodes();
				for(int i = 0; i < nl.getLength(); i++)
				{
					if(nl.item(i).getNodeType() != Node.ELEMENT_NODE)
						continue;
					Element element = (Element)nl.item(i);
					System.out.println("element: " + element.getTagName());
					if(element.getTagName().equals("dialogs"))
					{
						NodeList dialogList = element.getElementsByTagName("dialog");
						for(int d = 0; d < dialogList.getLength(); d++)
						{
							DialogRecord dr = new DialogRecord();
							dr.dialogElement = (Element)dialogList.item(d);
							String id = dr.dialogElement.getAttribute("id");
							System.out.println("processing dialog: " + id);
							NodeList childList = dr.dialogElement.getElementsByTagName("model");
							dr.modelElement = (Element)childList.item(0);
							childList = dr.dialogElement.getElementsByTagName("design");
							dr.designElement = (Element)childList.item(0);
							NodeList canvasList = dr.designElement.getElementsByTagName("canvas");
							dr.canvasElement = (Element)canvasList.item(0);
outer:						for(CanvasRecord cr : canvasRecords)
							{
								System.out.println("looking for dialog in canvas: " + cr.canvasElement.getAttribute("name"));
								for(Element moduleElement : cr.baseModuleElements)
								{
									System.out.println("comparing to module: " + moduleElement.getAttribute("id"));
									if(id.equals(moduleElement.getAttribute("id")))
									{
										dr.mainDesignElement = moduleElement;
										cr.dialogs.add(dr);
										break outer;
									}
								}
							}
						}
					}
					else //application or fragment element
					{
						NodeList childList = element.getElementsByTagName("model");
						Element modelElement = (Element)childList.item(0);
						NodeList moduleList = ((Element)modelElement.getElementsByTagName("elements").item(0)).getElementsByTagName("element");
						NodeList connectorList = ((Element)modelElement.getElementsByTagName("connectors").item(0)).getElementsByTagName("connector");
						childList = element.getElementsByTagName("design");
						Element designElement = (Element)childList.item(0);
						NodeList canvasList = designElement.getElementsByTagName("canvas");
						for(int c = 0; c < canvasList.getLength(); c++)
						{
							Element canvasElement = (Element)canvasList.item(c);
							CanvasRecord cr = new CanvasRecord();
							cr.designElement = designElement;
							cr.canvasElement = canvasElement;
							NodeList uiModuleList = canvasElement.getElementsByTagName("ui-element");
uiOuter:					for(int um = 0; um < uiModuleList.getLength(); um++)
							{
								Element uiModuleElement = (Element)uiModuleList.item(um);
								for(int mod = 0; mod < moduleList.getLength(); mod++)
								{
									Element moduleElement = (Element)moduleList.item(mod);
									if(moduleElement.getAttribute("id").equals(uiModuleElement.getAttribute("id")))
									{
										cr.baseModuleElements.add(moduleElement);
										continue uiOuter;
									}
								}
								System.out.println("missing base module: " + uiModuleElement.getAttribute("id"));
								//canvasElement.removeChild(uiModuleElement);
							}
							NodeList uiConnectorList = canvasElement.getElementsByTagName("ui-connector");
uicOuter:					for(int uc = 0; uc < uiConnectorList.getLength(); uc++)
							{
								Element uiConnectorElement = (Element)uiConnectorList.item(uc);
								for(int con = 0; con < connectorList.getLength(); con++)
								{
									Element connectorElement = (Element)connectorList.item(con);
									if(connectorElement.getAttribute("id").equals(uiConnectorElement.getAttribute("id")))
									{
										cr.baseConnectorElements.add(connectorElement);
										continue uicOuter;
									}
								}
								System.out.println("missing base connector: " + uiConnectorElement.getAttribute("id"));
								//canvasElement.removeChild(uiConnectorElement);
							}
							canvasRecords.add(cr);
						}
					}
				}
				
				//Step 4.  Write design into multiple files.
				for(CanvasRecord cr : canvasRecords)
				{
					Document newDocument = builder.getDOMImplementation().createDocument(null, "design-document", null);
					Element newRootElement = newDocument.getDocumentElement();
					newRootElement.setAttribute("xml-version", "4.0.0");
					Element workflowElement = newDocument.createElement("workflow");
					workflowElement.setAttribute("id", Guid.createGUID());
					workflowElement.setAttribute("name", cr.canvasElement.getAttribute("name"));
					newRootElement.appendChild(workflowElement);
					Element modelElement = newDocument.createElement("model");
					workflowElement.appendChild(modelElement);
					Element elementsElement = newDocument.createElement("elements");
					modelElement.appendChild(elementsElement);
					for(Element originalModuleElement : cr.baseModuleElements)
					{
						Element newModuleElement = (Element)newDocument.importNode(originalModuleElement, true);
						elementsElement.appendChild(newModuleElement);
						if(newModuleElement.getAttribute("type").equals("org.eclipse.vtp.desktop.editors.core.elements.fragment"))
						{
							//this is a fragment element and needs its configuration converted
							Element configurationElement = (Element)newModuleElement.getElementsByTagName("configuration").item(0);
							Element customConfigElement = (Element)configurationElement.getElementsByTagName("custom-config").item(0);
							Element managedConfigElement = newDocument.createElement("managed-config");
							managedConfigElement.setAttribute("type", "org.eclipse.vtp.configuration.include");
							managedConfigElement.setAttribute("xml-version", "1.0.0");
							configurationElement.appendChild(managedConfigElement);
							
							//convert input variable mappings
							NodeList inputMappingList = customConfigElement.getElementsByTagName("mapping");
							for(int im = 0; im < inputMappingList.getLength(); im++)
							{
								Element inputMappingElement = (Element)inputMappingList.item(im);
								Element inputBindingElement = newDocument.createElement("input-binding");
								managedConfigElement.appendChild(inputBindingElement);
								inputBindingElement.setAttribute("name", inputMappingElement.getAttribute("fragment-variable-name"));
								Element brandBindingElement = newDocument.createElement("brand-binding");
								inputBindingElement.appendChild(brandBindingElement);
								brandBindingElement.setAttribute("id", defaultBrandId);
								brandBindingElement.setAttribute("name", "Default");
								Element inputItemElement = newDocument.createElement("input-item");
								brandBindingElement.appendChild(inputItemElement);
								inputItemElement.setAttribute("type", inputMappingElement.getAttribute("mapping-type").toUpperCase());
								if(!inputMappingElement.getAttribute("mapping-value").equals(""))
								{
									inputItemElement.setTextContent(inputMappingElement.getAttribute("mapping-value"));
								}
							}
							
							//convert output mappings
							NodeList outputMappingList = customConfigElement.getElementsByTagName("outgoing-data");
							for(int om = 0; om < outputMappingList.getLength(); om++)
							{
								Element outputMappingElement = (Element)outputMappingList.item(om);
								Element exitBindingElement = newDocument.createElement("exit-binding");
								managedConfigElement.appendChild(exitBindingElement);
								exitBindingElement.setAttribute("name", outputMappingElement.getAttribute("exit-path"));
								NodeList outputDataMappingList = outputMappingElement.getElementsByTagName("data-mapping");
								for(int odm = 0; odm < outputDataMappingList.getLength(); odm++)
								{
									Element outputDataMappingElement = (Element)outputDataMappingList.item(odm);
									Element outputBindingElement = newDocument.createElement("output-binding");
									exitBindingElement.appendChild(outputBindingElement);
									outputBindingElement.setAttribute("name", outputDataMappingElement.getAttribute("output"));
									Element brandBindingElement = newDocument.createElement("brand-binding");
									outputBindingElement.appendChild(brandBindingElement);
									brandBindingElement.setAttribute("id", defaultBrandId);
									brandBindingElement.setAttribute("name", "Default");
									Element outputItemElement = newDocument.createElement("output-item");
									outputItemElement.setTextContent(outputDataMappingElement.getAttribute("target"));
									brandBindingElement.appendChild(outputItemElement);
								}
							}
						}
					}
					Element connectorsElement = newDocument.createElement("connectors");
					modelElement.appendChild(connectorsElement);
					for(Element originalConnectorElement : cr.baseConnectorElements)
					{
						Element newConnectorElement = (Element)newDocument.importNode(originalConnectorElement, true);
						connectorsElement.appendChild(newConnectorElement);
					}
					Element designElement = newDocument.createElement("design");
					designElement.setAttribute("orientation", cr.canvasElement.getAttribute("orientation"));
					designElement.setAttribute("paper-size", cr.canvasElement.getAttribute("paper-size"));
					workflowElement.appendChild(designElement);
					NodeList uiElementList = cr.canvasElement.getChildNodes();
					for(int ui = 0; ui < uiElementList.getLength(); ui++)
					{
						if(uiElementList.item(ui).getNodeType() != Node.ELEMENT_NODE)
							continue;
						Node newUIElement = newDocument.importNode(uiElementList.item(ui), true);
						designElement.appendChild(newUIElement);
					}
					Element dialogsElement = newDocument.createElement("dialogs");
					newRootElement.appendChild(dialogsElement);
					for(DialogRecord dr : cr.dialogs)
					{
						Element dialogElement = newDocument.createElement("workflow");
						dialogsElement.appendChild(dialogElement);
						dialogElement.setAttribute("id", dr.dialogElement.getAttribute("id"));
						dialogElement.setAttribute("name", dr.mainDesignElement.getAttribute("name"));
						Node dialogModelElement = newDocument.importNode(dr.modelElement, true);
						dialogElement.appendChild(dialogModelElement);
						Element dialogDesignElement = newDocument.createElement("design");
						dialogElement.appendChild(dialogDesignElement);
						dialogDesignElement.setAttribute("orientation", dr.canvasElement.getAttribute("orientation"));
						dialogDesignElement.setAttribute("paper-size", dr.canvasElement.getAttribute("paper-size"));
						NodeList dialogUIElementsList = dr.canvasElement.getChildNodes();
						for(int dui = 0; dui < dialogUIElementsList.getLength(); dui++)
						{
							if(dialogUIElementsList.item(dui).getNodeType() != Node.ELEMENT_NODE)
								continue;
							Node newUIElement = newDocument.importNode(dialogUIElementsList.item(dui), true);
							dialogDesignElement.appendChild(newUIElement);
						}
					}
					IFile designDocument = designFolder.getFile(safeName(designFolder, cr.canvasElement.getAttribute("name")));
					baos = new ByteArrayOutputStream();
					t.transform(new DOMSource(newDocument), new XMLWriter(baos).toXMLResult());
					String rawDocument = baos.toString();
					//Update paper size ids
					rawDocument = rawDocument.replaceAll("org.eclipse.vtp.desktop.editors.core.Letter", "org.eclipse.vtp.desktop.model.core.Letter");
					
					//Update element type ids
					rawDocument = rawDocument.replaceAll("org.eclipse.vtp.desktop.editors.core.elements.primitive", "org.eclipse.vtp.desktop.model.elements.core.basic");
					rawDocument = rawDocument.replaceAll("org.eclipse.vtp.desktop.editors.core.elements.dialog", "org.eclipse.vtp.desktop.model.elements.core.dialog");
					rawDocument = rawDocument.replaceAll("org.eclipse.vtp.desktop.editors.core.elements.fragment", "org.eclipse.vtp.desktop.model.elements.core.include");
					rawDocument = rawDocument.replaceAll("org.eclipse.vtp.desktop.editors.core.elements.customintegration", "org.eclipse.vtp.desktop.model.elements.core.basic");
					
					//update basic element sub type ids
					rawDocument = rawDocument.replaceAll("org.eclipse.vtp.desktop.editors.core.script", "org.eclipse.vtp.modules.standard.ui.script");
					rawDocument = rawDocument.replaceAll("org.eclipse.vtp.desktop.editors.core.modelEntry", "org.eclipse.vtp.modules.standard.ui.modelEntry");
					rawDocument = rawDocument.replaceAll("org.eclipse.vtp.desktop.editors.core.decision", "org.eclipse.vtp.modules.standard.ui.decision");
					rawDocument = rawDocument.replaceAll("org.eclipse.vtp.desktop.editors.core.portalEntry", "org.eclipse.vtp.modules.standard.ui.portalEntry");
					rawDocument = rawDocument.replaceAll("org.eclipse.vtp.desktop.editors.core.portalExit", "org.eclipse.vtp.modules.standard.ui.portalExit");
					rawDocument = rawDocument.replaceAll("org.eclipse.vtp.desktop.editors.core.exit", "org.eclipse.vtp.modules.standard.ui.exit");
					rawDocument = rawDocument.replaceAll("org.eclipse.vtp.desktop.editors.core.return", "org.eclipse.vtp.modules.standard.ui.return");
					rawDocument = rawDocument.replaceAll("org.eclipse.vtp.desktop.editors.core.appbegin", "org.eclipse.vtp.modules.standard.ui.appbegin");
					rawDocument = rawDocument.replaceAll("org.eclipse.vtp.desktop.editors.core.beginFragment", "org.eclipse.vtp.modules.standard.ui.appbegin");
					rawDocument = rawDocument.replaceAll("org.eclipse.vtp.desktop.editors.core.assignment", "org.eclipse.vtp.modules.standard.ui.assignment");
					rawDocument = rawDocument.replaceAll("org.eclipse.vtp.desktop.editors.core.optionSet", "org.eclipse.vtp.modules.interactive.optionSet");
					rawDocument = rawDocument.replaceAll("org.eclipse.vtp.desktop.editors.core.question", "org.eclipse.vtp.modules.interactive.question");
					rawDocument = rawDocument.replaceAll("org.eclipse.vtp.desktop.editors.core.playPrompt", "org.eclipse.vtp.modules.interactive.playPrompt");
					rawDocument = rawDocument.replaceAll("org.eclipse.vtp.desktop.editors.core.transfer", "org.eclipse.vtp.modules.interactive.advancedtransfer");
					rawDocument = rawDocument.replaceAll("org.eclipse.vtp.desktop.editors.core.advancedtransfer", "org.eclipse.vtp.modules.interactive.advancedtransfer");
					rawDocument = rawDocument.replaceAll("org.eclipse.vtp.desktop.editors.core.subdialog", "org.eclipse.vtp.modules.interactive.subdialog");
					rawDocument = rawDocument.replaceAll("org.eclipse.vtp.desktop.editors.core.record", "org.eclipse.vtp.modules.interactive.record");
					rawDocument = rawDocument.replaceAll("org.eclipse.vtp.desktop.editors.core.databaseQuery", "org.eclipse.vtp.modules.database.simplequery");
					rawDocument = rawDocument.replaceAll("org.eclipse.vtp.desktop.editors.core.adrequest", "org.eclipse.vtp.modules.attacheddata.adrequest");
					rawDocument = rawDocument.replaceAll("org.eclipse.vtp.desktop.editors.core.endCall", "org.eclipse.vtp.modules.standard.ui.return");
					designDocument.create(new ByteArrayInputStream(rawDocument.getBytes()), true, null);
					
				}
				IProjectDescription description = project.getDescription();
				description.setNatureIds(new String[] {"org.eclipse.vtp.desktop.model.interactive.core.InteractiveWorkflowProjectNature"});
				project.setDescription(description, null);
				callDesignFile.move(project.getFile(callDesignFile.getName() + ".bak").getFullPath(), true, null);
				IWorkflowProject wp = WorkflowCore.getDefault().getWorkflowModel().convertToWorkflowProject(project);
				IDesignRootFolder drf = wp.getDesignRootFolder();
				touchAll(drf);
			}
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private void touchAll(IDesignItemContainer container) throws Exception
	{
		for(IDesignDocument doc : container.getDesignDocuments())
		{
			doc.becomeWorkingCopy();
			doc.commitWorkingCopy();
		}
		for(IDesignFolder folder : container.getDesignFolders())
		{
			touchAll(folder);
		}
	}
	
	private String safeName(IFolder parent, String suggestion)
	{
		System.out.println("suggested name: " + suggestion);
		String ret = suggestion.replaceAll("'", "") + ".canvas";
		ret = ret.replaceAll("/", "_");
		ret = ret.replaceAll("\\\\", "_");
		System.out.println("adjusted suggested name: " + ret);
		int i = 1;
		while(parent.getWorkspace().validateName(ret, IResource.FILE).getCode() != IStatus.OK || parent.getFile(ret).exists())
		{
			ret = "Canvas" + i + ".canvas";
			i++;
			System.out.println("fail-safe try: " + ret);
		}
		return ret;
	}
	
	public class CanvasRecord
	{
		Element designElement = null;
		Element canvasElement = null;
		List<Element> baseModuleElements = new LinkedList<Element>();
		List<Element> baseConnectorElements = new LinkedList<Element>();
		List<DialogRecord> dialogs = new LinkedList<DialogRecord>();
	}
	
	public class DialogRecord
	{
		Element dialogElement = null;
		Element modelElement = null;
		Element designElement = null;
		Element canvasElement = null;
		Element mainDesignElement = null;
	}
}
