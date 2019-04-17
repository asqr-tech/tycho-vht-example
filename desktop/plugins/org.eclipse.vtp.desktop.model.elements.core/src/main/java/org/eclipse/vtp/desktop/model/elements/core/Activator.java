package org.eclipse.vtp.desktop.model.elements.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.vtp.desktop.model.core.internal.DesignWriter;
import org.eclipse.vtp.desktop.model.core.internal.design.Design;
import org.eclipse.vtp.framework.util.XMLWriter;
import org.osgi.framework.BundleContext;
import org.w3c.dom.Document;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipse.vtp.desktop.model.elements.core";

	// The shared instance
	private static Activator plugin;
	
	private Properties localDialogMap = null;
	private File dialogFolder = null;
	private List<LocalDialogRecord> localDialogs = new ArrayList<LocalDialogRecord>();

	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception
	{
		super.start(context);
		plugin = this;
		try
        {
	        File storageRoot = context.getDataFile("");
	        dialogFolder = new File(storageRoot, "local_dialogs/");
	        if(!dialogFolder.exists()) //first activation
	        {
	        	dialogFolder.mkdirs();
	        }
	        File propFile = new File(dialogFolder, "dialog_map.properties");
	        if(!propFile.exists())
	        	localDialogMap = new Properties();
	        else
	        {
	        	localDialogMap = new Properties();
	        	InputStream propIn = new FileInputStream(propFile);
	        	localDialogMap.load(propIn);
	        	propIn.close();
	        }
	        for(Object obj : localDialogMap.keySet())
	        {
	        	String dialogId = (String)obj;
	        	localDialogs.add(new LocalDialogRecord(dialogId, localDialogMap.getProperty(dialogId), new File(dialogFolder, dialogId + ".xml").toURI().toURL()));
	        }
	        String[] fnames = dialogFolder.list();
	        for(int i = 0; i < fnames.length; i++)
	        {
	        	System.err.println("local dialog: " + fnames[i]);
	        }
        }
        catch(RuntimeException e)
        {
	        e.printStackTrace();
        }
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
	{
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

	public LocalDialogRecord getLocalDialog(String id)
	{
		for(LocalDialogRecord record : localDialogs)
        {
	        if(record.getId().equals(id))
	        {
	        	return record;
	        }
        }
		return null;
	}
	
	public List<LocalDialogRecord> listLocalDialogs()
	{
		return Collections.unmodifiableList(localDialogs);
	}
	
	public void addLocalDialog(String name, Design model)
	{
		try
		{
			//build document contents
			DocumentBuilderFactory factory =
				DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.getDOMImplementation().createDocument(null, "dialog-definition", null);
			org.w3c.dom.Element rootElement = document.getDocumentElement();
			DesignWriter designWriter = new DesignWriter();
			designWriter.writeDesign(rootElement, model);
			String dialogId = model.getDesignId();
			File dialogTemplate = new File(dialogFolder, dialogId + ".xml");
			//write document to file
			OutputStream baos = new FileOutputStream(dialogTemplate);
			TransformerFactory transfactory = TransformerFactory.newInstance();
			Transformer t = transfactory.newTransformer();
			t.setOutputProperty(OutputKeys.METHOD, "xml");
			t.setOutputProperty(OutputKeys.INDENT, "yes");
			t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			t.transform(new DOMSource(document), new XMLWriter(baos).toXMLResult());
			localDialogMap.setProperty(dialogId, name);
			localDialogs.add(new LocalDialogRecord(dialogId, localDialogMap.getProperty(dialogId), dialogTemplate.toURI().toURL()));
			File propFile = new File(dialogFolder, "dialog_map.properties");
			OutputStream propOut = new FileOutputStream(propFile);
			localDialogMap.store(propOut, "");
			propOut.close();
			fireDialogChange();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public void removeLocalDialog(String id)
	{
		try
        {
	        localDialogMap.remove(id);
	        File propFile = new File(dialogFolder, "dialog_map.properties");
	        OutputStream propOut = new FileOutputStream(propFile);
	        localDialogMap.store(propOut, "");
	        File dialogTemplate = new File(dialogFolder, id + ".xml");
	        dialogTemplate.delete();
	        LocalDialogRecord toRemove = null;
	        for(LocalDialogRecord record : localDialogs)
            {
	            if(record.getId().equals(id))
	            {
	            	toRemove = record;
	            	break;
	            }
            }
	        if(toRemove != null)
	        	localDialogs.remove(toRemove);
	        fireDialogChange();
        }
        catch(Exception e)
        {
	        e.printStackTrace();
        }
	}

	public class LocalDialogRecord
	{
		private String id;
		private String name;
		private URL templateURL;
		
		public LocalDialogRecord(String id, String name, URL templateURL)
		{
			super();
			this.id = id;
			this.name = name;
			this.templateURL = templateURL;
		}

		public String getId()
        {
        	return id;
        }

		public String getName()
        {
        	return name;
        }

		public URL getTemplateURL()
        {
        	return templateURL;
        }
		
		
	}
	
	private List<LocalDialogListener> dialogListeners = new ArrayList<LocalDialogListener>();
	
	public void addListener(LocalDialogListener listener)
	{
		dialogListeners.remove(listener);
		dialogListeners.add(listener);
	}
	
	public void removeListener(LocalDialogListener listener)
	{
		dialogListeners.remove(listener);
	}
	
	public void fireDialogChange()
	{
		for(LocalDialogListener listener : dialogListeners)
        {
	        listener.localDialogsChanged();
        }
	}
	
	public interface LocalDialogListener
	{
		public void localDialogsChanged();
	}
}
