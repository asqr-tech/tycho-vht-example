package org.eclipse.vtp.desktop.model.legacy.v3_xTo3_X.update;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.vtp.desktop.model.legacy.v3_xTo3_X.XMLConverter;
import org.eclipse.vtp.desktop.model.legacy.v3_xTo3_X.legacysupport.ConversionException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class OldMediaToGenericConverter implements XMLConverter
{
	private LegacyBrandManager brandManager = null;
	private List<LegacyBrand> brands = new ArrayList<LegacyBrand>();
	private Map<String, Element> interactionElements = new HashMap<String, Element>();
	private Map<String, Element> namedElements = new HashMap<String, Element>();
	private Map<String, Element> languageElements = new HashMap<String, Element>();

	/**
	 * @param brandManager
	 */
	public OldMediaToGenericConverter(LegacyBrandManager brandManager)
	{
		super();
		this.brandManager = brandManager;
		addBrand(brandManager.getDefaultBrand());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.update.XMLConverter#convert(org.w3c.dom.Element)
	 */
	public void convert(Element element) throws ConversionException
	{
		Element parentElement = (Element)element.getParentNode();
		Element newManagedConfigElement = parentElement.getOwnerDocument().createElement("managed-config");
		parentElement.appendChild(newManagedConfigElement);
		newManagedConfigElement.setAttribute("type", "org.eclipse.vtp.configuration.generic");
		newManagedConfigElement.setAttribute("xml-version", "1.0.0");
		NodeList promptBindingElementList = element.getElementsByTagNameNS("http://eclipse.org/vtp/xml/configuration/media", "prompt-binding");
		for(int i = 0; i < promptBindingElementList.getLength(); i++)
		{
			Element promptBindingElement = (Element)promptBindingElementList.item(i);
			String bindingName = promptBindingElement.getAttribute("name");
			NodeList promptItemElementList = promptBindingElement.getElementsByTagName("item");
			for(int pi = 0; pi < promptItemElementList.getLength(); pi++)
			{
				LegacyBrand itemBrand = null;
				String itemLanguage = null;
				String interactionId = null;
				Element promptItemElement = (Element)promptItemElementList.item(pi);
				String key = promptItemElement.getAttribute("key");
				for(LegacyBrand brand : brands)
				{
					if(key.startsWith(brand.getName()))
					{
						itemBrand = brand;
						key = key.substring(brand.getName().length());
						break;
					}
				}
				List<String> interactionTypes = brandManager.getSupportedInteractionTypes();
				for(String interactionType : interactionTypes)
				{
					if(key.startsWith(interactionType))
					{
						interactionId = interactionType;
						key = key.substring(interactionType.length());
						//assume the remainder is the language.
						itemLanguage = key;
						break;
					}
				}
				if(itemBrand != null && itemLanguage != null && interactionId != null)
				{
					Element newInteractionBindingElement = interactionElements.get(interactionId);
					if(newInteractionBindingElement == null)
					{
						newInteractionBindingElement = parentElement.getOwnerDocument().createElement("interaction-binding");
						newManagedConfigElement.appendChild(newInteractionBindingElement);
						newInteractionBindingElement.setAttribute("type", interactionId);
						interactionElements.put(interactionId, newInteractionBindingElement);
					}
					Element newNamedBindingElement = namedElements.get(interactionId + bindingName);
					if(newNamedBindingElement == null)
					{
						newNamedBindingElement = parentElement.getOwnerDocument().createElement("named-binding");
						newInteractionBindingElement.appendChild(newNamedBindingElement);
						newNamedBindingElement.setAttribute("name", bindingName);
						namedElements.put(interactionId + bindingName, newNamedBindingElement);
					}
					Element newLanguageBindingElement = languageElements.get(interactionId + bindingName + itemLanguage);
					if(newLanguageBindingElement == null)
					{
						newLanguageBindingElement = parentElement.getOwnerDocument().createElement("language-binding");
						newNamedBindingElement.appendChild(newLanguageBindingElement);
						newLanguageBindingElement.setAttribute("language", itemLanguage);
						languageElements.put(interactionId + bindingName + itemLanguage, newLanguageBindingElement);
					}
					Element newBrandBindingElement = parentElement.getOwnerDocument().createElement("brand-binding");
					newLanguageBindingElement.appendChild(newBrandBindingElement);
					newBrandBindingElement.setAttribute("id", itemBrand.getId());
					Element bindingItemElement = parentElement.getOwnerDocument().createElement("binding-item");
					newBrandBindingElement.appendChild(bindingItemElement);
					bindingItemElement.setAttribute("type", "org.eclipse.vtp.configuration.generic.items.prompt");
					NodeList promptBindingEntryList = promptItemElement.getChildNodes();
					for(int en = 0; en < promptBindingEntryList.getLength(); en++)
					{
						if(promptBindingEntryList.item(en).getNodeType() == Node.ELEMENT_NODE)
						{
							Element promptBindingEntryElement = (Element)promptBindingEntryList.item(en);
							bindingItemElement.appendChild(promptBindingEntryElement.cloneNode(true));
						}
					}
				}
			}
		}
		NodeList grammarBindingElementList = element.getElementsByTagNameNS("http://eclipse.org/vtp/xml/configuration/media", "grammar-binding");
		for(int i = 0; i < grammarBindingElementList.getLength(); i++)
		{
			Element grammarBindingElement = (Element)grammarBindingElementList.item(i);
			String bindingName = grammarBindingElement.getAttribute("name");
			NodeList grammarItemElementList = grammarBindingElement.getElementsByTagName("item");
			for(int pi = 0; pi < grammarItemElementList.getLength(); pi++)
			{
				LegacyBrand itemBrand = null;
				String itemLanguage = null;
				String interactionId = null;
				Element grammarItemElement = (Element)grammarItemElementList.item(pi);
				String key = grammarItemElement.getAttribute("key");
				for(LegacyBrand brand : brands)
				{
					if(key.startsWith(brand.getName()))
					{
						itemBrand = brand;
						key = key.substring(brand.getName().length());
						break;
					}
				}
				List<String> interactionTypes = brandManager.getSupportedInteractionTypes();
				for(String interactionType : interactionTypes)
				{
					if(key.startsWith(interactionType))
					{
						interactionId = interactionType;
						key = key.substring(interactionType.length());
						//assume the remainder is the language.
						itemLanguage = key;
						break;
					}
				}
				if(itemBrand != null && itemLanguage != null && interactionId != null)
				{
					Element newInteractionBindingElement = interactionElements.get(interactionId);
					if(newInteractionBindingElement == null)
					{
						newInteractionBindingElement = parentElement.getOwnerDocument().createElement("interaction-binding");
						newManagedConfigElement.appendChild(newInteractionBindingElement);
						newInteractionBindingElement.setAttribute("type", interactionId);
						interactionElements.put(interactionId, newInteractionBindingElement);
					}
					Element newNamedBindingElement = namedElements.get(interactionId + bindingName);
					if(newNamedBindingElement == null)
					{
						newNamedBindingElement = parentElement.getOwnerDocument().createElement("named-binding");
						newInteractionBindingElement.appendChild(newNamedBindingElement);
						newNamedBindingElement.setAttribute("name", bindingName);
						namedElements.put(interactionId + bindingName, newNamedBindingElement);
					}
					Element newLanguageBindingElement = languageElements.get(interactionId + bindingName + itemLanguage);
					if(newLanguageBindingElement == null)
					{
						newLanguageBindingElement = parentElement.getOwnerDocument().createElement("language-binding");
						newNamedBindingElement.appendChild(newLanguageBindingElement);
						newLanguageBindingElement.setAttribute("language", itemLanguage);
						languageElements.put(interactionId + bindingName + itemLanguage, newLanguageBindingElement);
					}
					Element newBrandBindingElement = parentElement.getOwnerDocument().createElement("brand-binding");
					newLanguageBindingElement.appendChild(newBrandBindingElement);
					newBrandBindingElement.setAttribute("id", itemBrand.getId());
					Element bindingItemElement = parentElement.getOwnerDocument().createElement("binding-item");
					newBrandBindingElement.appendChild(bindingItemElement);
					bindingItemElement.setAttribute("type", "org.eclipse.vtp.configuration.generic.items.grammar");
					NodeList grammarBindingEntryList = grammarItemElement.getChildNodes();
					for(int en = 0; en < grammarBindingEntryList.getLength(); en++)
					{
						if(grammarBindingEntryList.item(en).getNodeType() == Node.ELEMENT_NODE)
						{
							Element grammarBindingEntryElement = (Element)grammarBindingEntryList.item(en);
							bindingItemElement.appendChild(grammarBindingEntryElement.cloneNode(true));
						}
					}
				}
			}
		}
		NodeList propertyBindingElementList = element.getElementsByTagNameNS("http://eclipse.org/vtp/xml/configuration/media", "property-binding");
		for(int i = 0; i < propertyBindingElementList.getLength(); i++)
		{
			Element propertyBindingElement = (Element)propertyBindingElementList.item(i);
			String bindingName = propertyBindingElement.getAttribute("name");
			NodeList propertyItemElementList = propertyBindingElement.getElementsByTagName("item");
			for(int pi = 0; pi < propertyItemElementList.getLength(); pi++)
			{
				LegacyBrand itemBrand = null;
				String itemLanguage = null;
				String interactionId = null;
				Element propertyItemElement = (Element)propertyItemElementList.item(pi);
				String key = propertyItemElement.getAttribute("key");
				for(LegacyBrand brand : brands)
				{
					if(key.startsWith(brand.getName()))
					{
						itemBrand = brand;
						key = key.substring(brand.getName().length());
						break;
					}
				}
				List<String> interactionTypes = brandManager.getSupportedInteractionTypes();
				for(String interactionType : interactionTypes)
				{
					if(key.startsWith(interactionType))
					{
						interactionId = interactionType;
						key = key.substring(interactionType.length());
						//assume the remainder is the language.
						itemLanguage = key;
						break;
					}
				}
				if(itemBrand != null && itemLanguage != null && interactionId != null)
				{
					Element newInteractionBindingElement = interactionElements.get(interactionId);
					if(newInteractionBindingElement == null)
					{
						newInteractionBindingElement = parentElement.getOwnerDocument().createElement("interaction-binding");
						newManagedConfigElement.appendChild(newInteractionBindingElement);
						newInteractionBindingElement.setAttribute("type", interactionId);
						interactionElements.put(interactionId, newInteractionBindingElement);
					}
					Element newNamedBindingElement = namedElements.get(interactionId + bindingName);
					if(newNamedBindingElement == null)
					{
						newNamedBindingElement = parentElement.getOwnerDocument().createElement("named-binding");
						newInteractionBindingElement.appendChild(newNamedBindingElement);
						newNamedBindingElement.setAttribute("name", bindingName);
						namedElements.put(interactionId + bindingName, newNamedBindingElement);
					}
					Element newLanguageBindingElement = languageElements.get(interactionId + bindingName + itemLanguage);
					if(newLanguageBindingElement == null)
					{
						newLanguageBindingElement = parentElement.getOwnerDocument().createElement("language-binding");
						newNamedBindingElement.appendChild(newLanguageBindingElement);
						newLanguageBindingElement.setAttribute("language", itemLanguage);
						languageElements.put(interactionId + bindingName + itemLanguage, newLanguageBindingElement);
					}
					Element newBrandBindingElement = parentElement.getOwnerDocument().createElement("brand-binding");
					newLanguageBindingElement.appendChild(newBrandBindingElement);
					newBrandBindingElement.setAttribute("id", itemBrand.getId());
					Element bindingItemElement = parentElement.getOwnerDocument().createElement("binding-item");
					newBrandBindingElement.appendChild(bindingItemElement);
					bindingItemElement.setAttribute("type", "org.eclipse.vtp.configuration.generic.items.property");
					Element propertyEntryItemElement = parentElement.getOwnerDocument().createElement("property-value");
					bindingItemElement.appendChild(propertyEntryItemElement);
					propertyEntryItemElement.setAttribute("value-type", "static");
					propertyEntryItemElement.setTextContent(propertyItemElement.getTextContent());
				}
			}
		}
		parentElement.removeChild(element);
	}

	/**
	 * @param brand
	 */
	private void addBrand(LegacyBrand brand)
	{
		brands.add(brand);
		List<LegacyBrand> children = brand.getChildBrands();
		for(LegacyBrand child : children)
		{
			addBrand(child);
		}
	}
}
