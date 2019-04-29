/**
 * 
 */
package org.eclipse.vtp.desktop.model.core.internal;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;

/**
 * @author trip
 *
 */
public final class WorkflowIndexService
{
	private static final WorkflowIndexService instance = new WorkflowIndexService();
	
	public static WorkflowIndexService getInstance()
	{
		return instance;
	}

	private Map<String, WorkflowIndex> activeIndexes = new HashMap<String, WorkflowIndex>();
	
	/**
	 * 
	 */
	private WorkflowIndexService()
	{
		try
		{
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
			Runtime.getRuntime().addShutdownHook(new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					try
					{
						DriverManager.getConnection("jdbc:derby:;shutdown=true");
					}
					catch (SQLException e)
					{
						if(e.getErrorCode() != 45000)
							e.printStackTrace();
					}
				}
			}));
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public synchronized WorkflowIndex getIndex(IProject project)
	{
//		System.err.println("Getting index for: " + project.getName());
		WorkflowIndex index = activeIndexes.get(project.getLocation().toPortableString());
		if(index != null)
			return index;
		index = new WorkflowIndex(project);
		activeIndexes.put(project.getLocation().toPortableString(), index);
		index.init();
		return index;
	}
	
	public synchronized void forgetIndex(IProject project)
	{
		activeIndexes.remove(project.getLocation().toPortableString());
	}
	
	public synchronized boolean isIndexed(IProject project)
	{
		WorkflowIndex index = activeIndexes.get(project.getLocation().toPortableString());
		if(index != null)
			return true;
		File dataDirectory = project.getWorkingLocation("org.eclipse.vtp.desktop.model.core").toFile();
		File indexDirectory = new File(dataDirectory, "index/");
		if(!indexDirectory.exists())
			return false;
		try
		{
			Connection con = DriverManager.getConnection("jdbc:derby:" + indexDirectory.getAbsolutePath());
			con.close();
			return true;
		}
		catch (SQLException e)
		{
		}
		return false;
	}
	
}
