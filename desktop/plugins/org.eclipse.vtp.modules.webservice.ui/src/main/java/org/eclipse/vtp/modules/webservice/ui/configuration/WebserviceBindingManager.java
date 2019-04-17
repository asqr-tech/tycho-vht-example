package org.eclipse.vtp.modules.webservice.ui.configuration;

import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.vtp.desktop.model.core.branding.BrandManager;
import org.eclipse.vtp.desktop.model.core.configuration.ConfigurationException;
import org.eclipse.vtp.desktop.model.core.configuration.ConfigurationManager;
import org.eclipse.vtp.desktop.model.core.design.IDesign;
import org.eclipse.vtp.framework.util.XMLUtilities;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class WebserviceBindingManager implements ConfigurationManager
{
	/**	The unique identifier for this manager type */
	public static final String TYPE_ID = "org.eclipse.vtp.configuration.webservicecall";
	/**	The current XML structure version used by this manager */
	public static final String XML_VERSION = "1.0.0";

	private IDesign design = null;
	private WebserviceServiceBinding serviceBinding = null;
	private InputDocumentStructure documentStructure = null;
	private OutputBinding outputBinding = null;

	public WebserviceBindingManager(IDesign design)
	{
		super();
		this.design = design;
		serviceBinding = new WebserviceServiceBinding();
		documentStructure = new InputDocumentStructure(this);
		outputBinding = new OutputBinding();
	}

	public String getType()
	{
		return TYPE_ID;
	}

	public String getXMLVersion()
	{
		return XML_VERSION;
	}
	
	public BrandManager getBrandManager()
	{
		return design.getDocument().getProject().getBrandManager();
	}
	
	public WebserviceServiceBinding getServiceBinding()
	{
		return serviceBinding;
	}
	
	public InputDocumentStructure getInputDocumentStructure()
	{
		return documentStructure;
	}
	
	public OutputBinding getOutputBinding()
	{
		return outputBinding;
	}

	public void readConfiguration(Element configuration)
		throws ConfigurationException
	{
		List<Element> serviceBindingElementList = XMLUtilities.getElementsByTagName(configuration, "service-binding", true);
		if(serviceBindingElementList.size() > 0)
		{
			serviceBinding.readConfiguration(serviceBindingElementList.get(0));
		}
		List<Element> documentStructureElementList = XMLUtilities.getElementsByTagName(configuration, "input-document-structure", true);
		if(documentStructureElementList.size() > 0)
		{
			documentStructure.readConfiguration(documentStructureElementList.get(0));
		}
		List<Element> outputElementList = XMLUtilities.getElementsByTagName(configuration, "output-binding", true);
		if(outputElementList.size() > 0)
		{
			outputBinding.readConfiguration(outputElementList.get(0));
		}
	}

	public void writeConfiguration(Element configuration)
	{
		Element serviceBindingElement = configuration.getOwnerDocument().createElementNS(null, "service-binding");
		configuration.appendChild(serviceBindingElement);
		serviceBinding.writeConfiguration(serviceBindingElement);
		Element documentStructureElement = documentStructure.createConfigurationElement(configuration);
		documentStructure.writeConfiguration(documentStructureElement);
		Element outputBindingElement = configuration.getOwnerDocument().createElementNS(null, "output-binding");
		configuration.appendChild(outputBindingElement);
		outputBinding.writeConfiguration(outputBindingElement);
	}

	public Object clone()
	{
		WebserviceBindingManager copy = new WebserviceBindingManager(design);
		try
		{
			//build document contents
			DocumentBuilderFactory factory =
				DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			factory.setNamespaceAware(true);
			Document document = builder.getDOMImplementation().createDocument(null, "temporary-document", null); //$NON-NLS-1$
			org.w3c.dom.Element rootElement = document.getDocumentElement();
			rootElement.setAttribute("xml-version", XML_VERSION); //$NON-NLS-1$
			writeConfiguration(rootElement);
			copy.readConfiguration(rootElement);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return copy;
	}
}
