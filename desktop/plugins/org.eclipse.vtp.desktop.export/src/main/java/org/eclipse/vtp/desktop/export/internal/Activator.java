package org.eclipse.vtp.desktop.export.internal;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class Activator extends AbstractUIPlugin {
	
	static volatile Activator instance = null;

	BundleContext context = null;
	
	public void start(BundleContext context) throws Exception {
		this.context = context;
		super.start(context);
		instance = this;
	}

	public void stop(BundleContext context) throws Exception {
		instance = null;
		super.stop(context);
		this.context = null;
	}

}
