package org.eclipse.vtp.modules.interactive.ui.properties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.vtp.desktop.model.core.branding.BrandManager;
import org.eclipse.vtp.desktop.model.core.branding.IBrand;
import org.eclipse.vtp.desktop.model.core.configuration.ConfigurationException;
import org.eclipse.vtp.desktop.model.core.configuration.ConfigurationManager;
import org.eclipse.vtp.desktop.model.core.design.IDesign;
import org.eclipse.vtp.desktop.model.interactive.core.internal.MenuChoice;
import org.eclipse.vtp.framework.util.XMLUtilities;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class MenuChoiceBindingManager implements ConfigurationManager
{
	/**	The unique identifier for this manager type */
	public static final String TYPE_ID = "org.eclipse.vtp.configuration.menuchoice";
	/**	The current XML structure version used by this manager */
	public static final String XML_VERSION = "1.0.1";
	
	/**	The brand manager to use when resolving the brand hierarchy */
	private BrandManager brandManager = null;
	private IDesign hostDesign = null;

	
	private List<MenuChoice> menuChoices = new ArrayList<MenuChoice>();
	private Map<String, List<MenuChoice>> brandOrders = new HashMap<String, List<MenuChoice>>();

	
	/**
	 * Creates a new instance of this manager that will use the given brand
	 * manager to resolve the brand structure and have the provided media
	 * default values.
	 * 
	 */
	public MenuChoiceBindingManager(IDesign design)
	{
		this.hostDesign = design;
		this.brandManager = design.getDocument().getProject().getBrandManager();
		System.out.println("constructing this: " + this.getClass());//TODO
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.configuration.ConfigurationManager#getType()
	 */
	public String getType()
	{
		return TYPE_ID;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.configuration.ConfigurationManager#getXMLVersion()
	 */
	public String getXMLVersion()
	{
		return XML_VERSION;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.configuration.ConfigurationManager#readConfiguration(org.w3c.dom.Element)
	 */
	public void readConfiguration(Element configuration) throws ConfigurationException
	{
		try
		{
			org.w3c.dom.Element[] choices =
				XMLUtilities.getElementsOfNodeList(XMLUtilities
					.getNamedNodeList(configuration, "choices"));

			for(int ch = 0; ch < choices.length; ch++)
			{
				MenuChoice mc =
					new MenuChoice(choices[ch].getAttribute("name"), choices[ch].getAttribute("script"));
				menuChoices.add(mc);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		NodeList brandOrdersList = configuration.getElementsByTagName("brand-order");
		if(brandOrdersList.getLength() == 0) //backwards compatibility
		{
			List<MenuChoice> defaultList = new ArrayList<MenuChoice>();
			defaultList.addAll(this.menuChoices);
			this.brandOrders.put("Default", defaultList);
		}
		for(int i = 0; i < brandOrdersList.getLength(); i++)
		{
			List<MenuChoice> brandList = new ArrayList<MenuChoice>();
			org.w3c.dom.Element brandOrderElement = (org.w3c.dom.Element)brandOrdersList.item(i);
			String brandName = brandOrderElement.getAttribute("brand");
			NodeList entryList = brandOrderElement.getElementsByTagName("entry");
			for(int e = 0; e < entryList.getLength(); e++)
			{
				org.w3c.dom.Element entryElement = (org.w3c.dom.Element)entryList.item(e);
				for(MenuChoice mc : this.menuChoices)
				{
					if(mc.getOptionName().equals(entryElement.getAttribute("name")))
					{
						brandList.add(mc);
						break;
					}
				}
			}
			this.brandOrders.put(brandName, brandList);
		}
		if(brandOrders.get("Default") == null) //sanity pass
		{
			List<MenuChoice> defaultList = new ArrayList<MenuChoice>();
			defaultList.addAll(menuChoices);
			this.brandOrders.put("Default", defaultList);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.configuration.ConfigurationManager#writeConfiguration(org.w3c.dom.Element)
	 */
	public void writeConfiguration(Element configuration)
	{
		org.w3c.dom.Element choicesElement =
			configuration.getOwnerDocument().createElement("choices");
		configuration.appendChild(choicesElement);

		for(MenuChoice mc : menuChoices)
		{
			org.w3c.dom.Element choiceElement =
				configuration.getOwnerDocument().createElement("choice");
			choiceElement.setAttribute("name", mc.getOptionName());
			if(mc.getScriptText() != null)
				choiceElement.setAttribute("script", mc.getScriptText());
			choicesElement.appendChild(choiceElement);
		}
		for(Map.Entry<String, List<MenuChoice>> entry : brandOrders.entrySet())
		{
			org.w3c.dom.Element orderElement = configuration.getOwnerDocument().createElement("brand-order");
			orderElement.setAttribute("brand", entry.getKey());
			configuration.appendChild(orderElement);
			List<MenuChoice> options = entry.getValue();
			for(int i = 0; i < options.size(); i++)
			{
				MenuChoice mc = options.get(i);
				org.w3c.dom.Element orderEntryElement = configuration.getOwnerDocument().createElement("entry");
				orderEntryElement.setAttribute("name", mc.getOptionName());
				orderEntryElement.setAttribute("spot", Integer.toString(i));
				orderElement.appendChild(orderEntryElement);
			}
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public Object clone()
	{
		MenuChoiceBindingManager copy = new MenuChoiceBindingManager(hostDesign);
		try
		{
			//build document contents
			DocumentBuilderFactory factory =
				DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.getDOMImplementation().createDocument(null, "temporary-document", null);
			org.w3c.dom.Element rootElement = document.getDocumentElement();
			rootElement.setAttribute("xml-version", XML_VERSION);
			writeConfiguration(rootElement);
			copy.readConfiguration(rootElement);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return copy;
	}

	/**
	 * @return the brand manager this manager uses to resolve brand structure
	 */
	public BrandManager getBrandManager()
    {
    	return brandManager;
    }

	public MenuChoice addChoice(String brand, MenuChoice choice)
	{
		List<MenuChoice> orderList = brandOrders.get(brand);
		if(orderList == null)
		{
			orderList = new ArrayList<MenuChoice>(brandOrders.get("Default"));
			brandOrders.put(brand, orderList);
		}
		for(MenuChoice mc : menuChoices)
		{
			if(mc.getOptionName().equals(choice.getOptionName()))
			{
				orderList.add(mc);
				return mc;
			}
		}
		menuChoices.add(choice);
		orderList.add(choice);
		return choice;
	}

	public boolean removeChoice(String brand, MenuChoice choice)
	{
		List<MenuChoice> orderList = brandOrders.get(brand);
		if(orderList == null)
		{
			orderList = new ArrayList<MenuChoice>(brandOrders.get("Default"));
			brandOrders.put(brand, orderList);
		}
		orderList.remove(choice);
		boolean found = false;
		for(Map.Entry<String, List<MenuChoice>> entry : brandOrders.entrySet())
		{
			List<MenuChoice> mcList = entry.getValue();
			if(mcList.contains(choice))
			{
				found = true;
				break;
			}
		}
		if(!found)
		{
			menuChoices.remove(choice);
		}
		return found;
	}
	
	public void moveChoiceUp(String brand, MenuChoice choice)
	{
		List<MenuChoice> orderList = brandOrders.get(brand);
		if(orderList == null)
		{
			orderList = new ArrayList<MenuChoice>(brandOrders.get("Default"));
			brandOrders.put(brand, orderList);
		}
		int index = orderList.indexOf(choice);
		if(index > 0)
		{
			orderList.remove(index);
			orderList.add(index - 1, choice);
		}
	}

	public void moveChoiceDown(String brand, MenuChoice choice)
	{
		List<MenuChoice> orderList = brandOrders.get(brand);
		if(orderList == null)
		{
			orderList = new ArrayList<MenuChoice>(brandOrders.get("Default"));
			brandOrders.put(brand, orderList);
		}
		int index = orderList.indexOf(choice);
		if(index < (orderList.size() - 1))
		{
			orderList.remove(index);
			orderList.add(index + 1, choice);
		}
	}
	
	public List<MenuChoice> getAllChoices()
	{
		return Collections.unmodifiableList(this.menuChoices);
	}
	
	public List<MenuChoice> getChoicesByBrand(IBrand brand)
	{
		List<MenuChoice> list = brandOrders.get(brand.getName());
		if(list == null)
		{
			if(!"Default".equals(brand.getName()))
			{
				return getChoicesByBrand(brand.getParent());
			}
			else
			{
				list = new ArrayList<MenuChoice>();
				brandOrders.put("Default", list);
			}
		}
		return Collections.unmodifiableList(list);
	}
}
