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
package org.eclipse.vtp.desktop.core;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	/** The plug-in ID */
	public static final String PLUGIN_ID = "org.eclipse.vtp.desktop.core";

	/** The shared instance */
	private static Activator plugin;
	
	/**
	 * The constructor
	 */
	public Activator() {
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
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

	/* (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#initializeImageRegistry(org.eclipse.jface.resource.ImageRegistry)
	 */
	protected void initializeImageRegistry(ImageRegistry reg)
	{
		super.initializeImageRegistry(reg);

		ImageDescriptor todoDescriptor =
			ImageDescriptor.createFromURL(getBundle()
											  .getEntry("icons/tasks_tsk.gif"));
		ImageDescriptor warningDescriptor =
			ImageDescriptor.createFromURL(getBundle()
											  .getEntry("icons/warning_tsk.gif"));
		ImageDescriptor errorDescriptor =
			ImageDescriptor.createFromURL(getBundle()
											  .getEntry("icons/error_tsk.gif"));
		ImageDescriptor moduleDescriptor =
			ImageDescriptor.createFromURL(getBundle()
											  .getEntry("icons/module.gif"));
		ImageDescriptor pointerToolDescriptor =
			ImageDescriptor.createFromURL(getBundle().getEntry("icons/arrow.gif"));
		ImageDescriptor lineToolDescriptor =
			ImageDescriptor.createFromURL(getBundle()
											  .getEntry("icons/line_tool.gif"));
		ImageDescriptor applicationDescriptor =
			ImageDescriptor.createFromURL(getBundle()
											  .getEntry("icons/application.gif"));
		ImageDescriptor folderClosedDescriptor =
			ImageDescriptor.createFromURL(getBundle()
											  .getEntry("icons/folder_closed.gif"));
		ImageDescriptor folderOpenDescriptor =
			ImageDescriptor.createFromURL(getBundle()
											  .getEntry("icons/folder_open.gif"));
		ImageDescriptor provisioningServerDescriptor =
			ImageDescriptor.createFromURL(getBundle()
											  .getEntry("icons/database.gif"));
		ImageDescriptor wormholeEntryBigDescriptor =
			ImageDescriptor.createFromURL(getBundle()
											  .getEntry("icons/wormhole-entry.gif"));
		ImageDescriptor wormholeEntrySmallDescriptor =
			ImageDescriptor.createFromURL(getBundle()
											  .getEntry("icons/wormhole-entry-sm.gif"));
		ImageDescriptor personaDescriptor =
			ImageDescriptor.createFromURL(getBundle()
											  .getEntry("icons/persona.gif"));
		ImageDescriptor libraryDescriptor =
			ImageDescriptor.createFromURL(getBundle()
											  .getEntry("icons/library.gif"));
		ImageDescriptor checkboxTrueDescriptor =
			ImageDescriptor.createFromURL(getBundle()
											  .getEntry("icons/checkbox_true.gif"));
		ImageDescriptor checkboxFalseDescriptor =
			ImageDescriptor.createFromURL(getBundle()
											  .getEntry("icons/checkbox_false.gif"));
		ImageDescriptor tinySquareDescriptor =
			ImageDescriptor.createFromURL(getBundle()
											  .getEntry("icons/tiny_square.gif"));
		ImageDescriptor tableDescriptor =
			ImageDescriptor.createFromURL(getBundle().getEntry("icons/table.gif"));
		ImageDescriptor moveUpDescriptor =
			ImageDescriptor.createFromURL(getBundle()
											  .getEntry("icons/move_up.gif"));
		ImageDescriptor moveDownDescriptor =
			ImageDescriptor.createFromURL(getBundle()
											  .getEntry("icons/move_down.gif"));
		ImageDescriptor moveUpDisabledDescriptor =
			ImageDescriptor.createFromURL(getBundle()
											  .getEntry("icons/move_up_disabled.gif"));
		ImageDescriptor moveDownDisabledDescriptor =
			ImageDescriptor.createFromURL(getBundle()
											  .getEntry("icons/move_down_disabled.gif"));
		ImageDescriptor fileObjectDescriptor =
			ImageDescriptor.createFromURL(getBundle()
											  .getEntry("icons/file_obj.gif"));
		ImageDescriptor scriptObjectDescriptor =
			ImageDescriptor.createFromURL(getBundle()
											  .getEntry("icons/javascript_view.gif"));
		ImageDescriptor noscriptObjectDescriptor =
			ImageDescriptor.createFromURL(getBundle()
											  .getEntry("icons/nojavascript.gif"));
		ImageDescriptor lockDescriptor =
			ImageDescriptor.createFromURL(getBundle()
											  .getEntry("icons/lock.gif"));
		reg.put("ICON_TASK", todoDescriptor);
		reg.put("ICON_WARNING", warningDescriptor);
		reg.put("ICON_ERROR", errorDescriptor);
		reg.put("ICON_MODULE", moduleDescriptor);
		reg.put("ICON_POINTER_TOOL", pointerToolDescriptor);
		reg.put("ICON_LINE_TOOL", lineToolDescriptor);
		reg.put("ICON_APPLICATION", applicationDescriptor);
		reg.put("ICON_FOLDER_CLOSED", folderClosedDescriptor);
		reg.put("ICON_FOLDER_OPEN", folderOpenDescriptor);
		reg.put("ICON_DOMAIN", provisioningServerDescriptor);
		reg.put("ICON_WORMHOLE_ENTRY_BIG", wormholeEntryBigDescriptor);
		reg.put("ICON_WORMHOLE_ENTRY_SMALL", wormholeEntrySmallDescriptor);
		reg.put("ICON_PERSONA", personaDescriptor);
		reg.put("ICON_LIBRARY", libraryDescriptor);
		reg.put("ICON_CHECKBOX_TRUE", checkboxTrueDescriptor);
		reg.put("ICON_CHECKBOX_FALSE", checkboxFalseDescriptor);
		reg.put("ICON_TINY_SQUARE", tinySquareDescriptor);
		reg.put("ICON_TABLE", tableDescriptor);
		reg.put("ICON_MOVE_UP", moveUpDescriptor);
		reg.put("ICON_MOVE_DOWN", moveDownDescriptor);
		reg.put("ICON_MOVE_UP_DISABLED", moveUpDisabledDescriptor);
		reg.put("ICON_MOVE_DOWN_DISABLED", moveDownDisabledDescriptor);
		reg.put("ICON_FILE", fileObjectDescriptor);
		reg.put("ICON_SCRIPT", scriptObjectDescriptor);
		reg.put("ICON_NOSCRIPT", noscriptObjectDescriptor);
		reg.put("ICON_LOCK", lockDescriptor);
	}

}
