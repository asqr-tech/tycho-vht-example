/**
 * 
 */
package com.openmethods.openvxml.idriver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * @author trip
 *
 */
public class Activator implements BundleActivator
{
	public static Activator getInstance()
	{
		return INSTANCE;
	}
	
	private static Activator INSTANCE;
	private File dllLocation = null;

	/**
	 * 
	 */
	public Activator()
	{
		INSTANCE = this;
	}

	/* (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception
	{
		File dataRoot = context.getBundle().getDataFile("");
		dllLocation = new File(dataRoot, "ilib_SDK_MD.dll");
		if(!dllLocation.exists())
		{
			URL sourceDll = context.getBundle().getEntry("ilib_SDK_MD.dll");
			if(sourceDll == null) //don't have the original dll
			{
				throw new Exception("Could not locate dll to copy");
			}
			FileOutputStream fos = new FileOutputStream(dllLocation);
			InputStream in = sourceDll.openStream();
			byte[] buf = new byte[10240];
			int len = in.read(buf);
			while(len != -1)
			{
				fos.write(buf, 0, len);
				len = in.read(buf);
			}
			fos.close();
			in.close();
		}
	}

	/* (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception
	{
	}

}
