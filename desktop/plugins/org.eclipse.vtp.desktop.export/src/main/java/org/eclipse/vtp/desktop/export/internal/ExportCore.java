package org.eclipse.vtp.desktop.export.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.vtp.desktop.export.IConfigurationExporter;
import org.eclipse.vtp.desktop.export.IExportAgent;
import org.osgi.framework.Bundle;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

/**
 * The activator class controls the plug-in life cycle
 */
public final class ExportCore {

	private static final IPreferencesService preferences = Platform.getPreferencesService();
	private static Map<String, IConfigurationExporter> exporters = new HashMap<String, IConfigurationExporter>();
	
	static
	{
		try
		{
			IConfigurationElement[] extensions = Platform.getExtensionRegistry().getConfigurationElementsFor("org.eclipse.vtp.desktop.export.configurationExporters");
			for(int i = 0; i < extensions.length; i++)
			{
				String id = extensions[i].getAttribute("id");
				Bundle contributor = Platform.getBundle(extensions[i].getContributor().getName());
				String className = extensions[i].getAttribute("class");
				try
				{
					Class<?> exporterClass = contributor.loadClass(className);
					exporters.put(id, (IConfigurationExporter)exporterClass.newInstance());
				}
				catch (Exception e)
				{
					e.printStackTrace();
					continue;
				}
			}
		}
		catch (InvalidRegistryObjectException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static String getSymbolicName()
	{
		return Activator.instance.getBundle().getSymbolicName();
	}
	
	public static Bundle[] getBundles()
	{
		return Activator.instance.context.getBundles();
	}
	
	public static void logError(String message, Throwable thrown)
	{
		System.err.println(message);
		thrown.printStackTrace(System.err);
	}
	
	public static void displayError(Shell shell, String message, Throwable thrown)
	{
		new ErrorDialog(shell,
				"Error Exporting Projects",
				message,
				new Status(Status.ERROR, getSymbolicName(), 1, message, thrown),
				Status.ERROR
			).open();
	}

	public static Map<String, String> loadSettings(String archive, String project) {
		Map<String, String> settings = new HashMap<String, String>();
		Preferences rootNode = preferences.getRootNode().node(InstanceScope.SCOPE).node(getSymbolicName());
		Preferences archiveNode = rootNode.node(escapeKey(archive));
		Preferences projectNode = archiveNode.node(escapeKey(project));
		try {
			for (String key : projectNode.keys())
				settings.put(key, projectNode.get(key, ""));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return settings;
	}
	
	public static void saveSettings(String archive, String project, Map<String, String> settings) {
		Preferences rootNode = preferences.getRootNode().node(InstanceScope.SCOPE).node(getSymbolicName());
		Preferences archiveNode = rootNode.node(escapeKey(archive));
		Preferences projectNode = archiveNode.node(escapeKey(project));
		try {
			projectNode.clear();
			for (Map.Entry<String, String> entry : settings.entrySet())
				projectNode.put(entry.getKey(), entry.getValue());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String getPreference (String key) {
		return preferences.getRootNode().node(InstanceScope.SCOPE).node(getSymbolicName()).get(escapeKey(key), null);
	}
	
	public static void setPreference (String key, String value) {
		preferences.getRootNode().node(InstanceScope.SCOPE).node(getSymbolicName()).put(escapeKey(key), value);
	}
	
	public static void flushPreferences () throws BackingStoreException {
		preferences.getRootNode().node(InstanceScope.SCOPE).node(getSymbolicName()).flush();
	}

	private static String escapeKey (String key) {
		StringBuilder sb = new StringBuilder(key);
		for (int i = sb.indexOf("\\"); i >= 0; i = sb.indexOf("\\", i + 2))
			sb.replace(i, i + 1, "\\\\");
		for (int i = sb.indexOf("/"); i >= 0; i = sb.indexOf("/", i + 1))
			sb.replace(i, i + 1, "\\");
		return sb.toString();
	}
	
	public static List<ExportAgent> createExportAgents()
	{
		IExtension[] agents = Platform.getExtensionRegistry().getExtensionPoint(
				getSymbolicName() + ".exportAgents").getExtensions();
		int count = 0;
		Integer ZERO = 0;
		Map<Integer, List<ExportAgent>> map =
			new TreeMap<Integer, List<ExportAgent>>();
		for (IExtension agent : agents)
		{
			Bundle contributor = Platform.getBundle(agent.getContributor().getName());
			for (IConfigurationElement element : agent.getConfigurationElements())
			{
				String id = element.getAttribute("id");
				try
				{
					Integer ranking = ZERO;
					String value = element.getAttribute("ranking");
					if (value != null && value.length() > 0)
						ranking = Integer.parseInt(value);
					String name = element.getAttribute("class");
					IExportAgent object = (IExportAgent)contributor.loadClass(name).newInstance();
					List<ExportAgent> list = map.get(ranking);
					if (list == null)
						map.put(ranking, list = new LinkedList<ExportAgent>());
					list.add(new ExportAgent(id, object));
					++count;
				}
				catch (Exception e)
				{
					logError("Error loading export agent \"" + id + "\".", e);
				}
			}
		}
		List<ExportAgent> result = new ArrayList<ExportAgent>(count);
		for (List<ExportAgent> list : map.values())
			result.addAll(list);
		return result;
	}
	
	public static IConfigurationExporter getConfigurationExporter(String elementTypeId)
	{
		return exporters.get(elementTypeId);
	}

}
