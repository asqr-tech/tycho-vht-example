package org.eclipse.vtp.desktop.export.internal.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Dictionary;
import java.util.Properties;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.FileLocator;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;

public class BundleExporter {
	
	private final Bundle bundle;

	public BundleExporter(Bundle bundle) {
		this.bundle = bundle;
	}
	
	public String getSymbolicName() {
		return bundle.getSymbolicName();
	}
	
	public boolean isFragment() {
		return getHeaders().get("Fragment-Host") != null;
	}

	public Dictionary<?, ?> getHeaders() {
		return bundle.getHeaders();
	}
	
	public void export (ExportWriter output, String basePath) throws Exception {
		File bundleFile = FileLocator.getBundleFile(bundle);
		if (bundleFile == null)
			return;
		bundleFile = bundleFile.getCanonicalFile();
		if (!bundleFile.exists())
			return;
		String jarName = basePath + getSymbolicName() + "_" + bundle.getHeaders().get(Constants.BUNDLE_VERSION);
		if (bundleFile.isFile())
			output.writeFile(jarName + ".jar", bundleFile, null);
		else {
			jarName += "/";
			File buildProperties = new File(bundleFile, "build.properties");
			if (buildProperties.exists()) {
				Properties props = new Properties();
				InputStream input = null;
				try {
					props.load(input = new FileInputStream(buildProperties));
				} finally {
					if (input != null)
						input.close();
				}
				for (StringTokenizer i = new StringTokenizer(props.getProperty("bin.includes"), ","); i.hasMoreTokens();) {
					String binToken = i.nextToken().trim();
					if (binToken.length() == 0)
						continue;
					String target = props.getProperty("output." + binToken);
					if (target == null)
						output.writeFile(jarName + binToken, new File(bundleFile, binToken).getCanonicalFile(), null);
					else
						for (StringTokenizer j = new StringTokenizer(target, ","); j.hasMoreTokens();) {
							String outputToken = j.nextToken().trim();
							if (outputToken.length() == 0)
								continue;
							String entryName = ".".equals(binToken) ? jarName : jarName + binToken;
							output.writeFile(entryName, new File(bundleFile, outputToken).getCanonicalFile(), null);
						}
				}
			} else
				output.writeFile(jarName, bundleFile.getCanonicalFile(), null);
		}
	}
}
