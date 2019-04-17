package org.eclipse.vtp.modules.webservice.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipse.vtp.modules.webservice.ui";

	// The shared instance
	private static Activator plugin;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	protected void initializeImageRegistry(ImageRegistry reg)
	{
		super.initializeImageRegistry(reg);
		System.err.println("initializing the image registry");
		
		try
		{
			ImageDescriptor xmlElementDescriptor =
				ImageDescriptor.createFromURL(getBundle()
												  .getEntry("icons/XSDElement.gif"));
			ImageDescriptor complexTypeDescriptor =
				ImageDescriptor.createFromURL(getBundle()
												  .getEntry("icons/XSDComplexType.gif"));
			ImageDescriptor attributeGroupDescriptor =
				ImageDescriptor.createFromURL(getBundle()
												  .getEntry("icons/XSDAttributeGroup.gif"));
			ImageDescriptor choiceSuggestionDescriptor =
				ImageDescriptor.createFromURL(getBundle()
												  .getEntry("icons/XSDChoice.gif"));
			ImageDescriptor conditionalElementDescriptor =
				ImageDescriptor.createFromURL(getBundle()
												  .getEntry("icons/conditional.gif"));
			ImageDescriptor moveUpDescriptor =
				ImageDescriptor.createFromURL(getBundle()
												  .getEntry("icons/move_up.gif"));
			ImageDescriptor moveUpDisabledDescriptor =
				ImageDescriptor.createFromURL(getBundle()
												  .getEntry("icons/move_up_disabled.gif"));
			ImageDescriptor moveDownDescriptor =
				ImageDescriptor.createFromURL(getBundle()
												  .getEntry("icons/move_down.gif"));
			ImageDescriptor moveDownDisabledDescriptor =
				ImageDescriptor.createFromURL(getBundle()
												  .getEntry("icons/move_down_disabled.gif"));
			ImageDescriptor scriptDescriptor =
				ImageDescriptor.createFromURL(getBundle()
												  .getEntry("icons/javascript_view.gif"));
			reg.put("XML_ELEMENT", xmlElementDescriptor);
			reg.put("COMPLEX_TYPE", complexTypeDescriptor);
			reg.put("ATTRIBUTE_GROUP", attributeGroupDescriptor);
			reg.put("CHOICE_SUGGESTION", choiceSuggestionDescriptor);
			reg.put("CONDITIONAL_ELEMENT", conditionalElementDescriptor);
			reg.put("MOVE_UP", moveUpDescriptor);
			reg.put("MOVE_UP_DISABLED", moveUpDisabledDescriptor);
			reg.put("MOVE_DOWN", moveDownDescriptor);
			reg.put("MOVE_DOWN_DISABLED", moveDownDisabledDescriptor);
			reg.put("SCRIPT", scriptDescriptor);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
