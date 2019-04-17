/**
 * 
 */
package org.eclipse.vtp.desktop.model.core.internal;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.eclipse.core.resources.IProject;
import org.eclipse.vtp.desktop.model.core.FieldType;
import org.eclipse.vtp.desktop.model.core.FieldType.Primitive;
import org.eclipse.vtp.desktop.model.core.IDesignDocument;
import org.eclipse.vtp.desktop.model.core.IDesignFolder;
import org.eclipse.vtp.desktop.model.core.IDesignItemContainer;
import org.eclipse.vtp.desktop.model.core.IWorkflowEntry;
import org.eclipse.vtp.desktop.model.core.IWorkflowExit;
import org.eclipse.vtp.desktop.model.core.IWorkflowProject;
import org.eclipse.vtp.desktop.model.core.IWorkflowReference;
import org.eclipse.vtp.desktop.model.core.WorkflowCore;
import org.eclipse.vtp.desktop.model.core.design.IDesignElement;
import org.eclipse.vtp.desktop.model.core.design.IDesignEntryPoint;
import org.eclipse.vtp.desktop.model.core.design.IDesignExitPoint;
import org.eclipse.vtp.desktop.model.core.design.Variable;
import org.eclipse.vtp.desktop.model.core.internal.DesignDocument.IndexedDesignEntry;
import org.eclipse.vtp.desktop.model.core.internal.DesignDocument.IndexedDesignExit;
import org.eclipse.vtp.desktop.model.core.internal.DesignDocument.IndexedWorkflowEntry;
import org.eclipse.vtp.desktop.model.core.internal.DesignDocument.IndexedWorkflowExit;
import org.eclipse.vtp.desktop.model.core.internal.DesignDocument.IndexedWorkflowReference;
import org.eclipse.vtp.framework.util.Guid;

/**
 * @author trip
 *
 */
public class WorkflowIndex
{
	IProject project = null;
	private boolean validated = false;
	private ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);

	/**
	 * 
	 */
	public WorkflowIndex(IProject project)
	{
		System.err.println("Creating index for: " + project.getName());
		this.project = project;
	}
	
	void init()
	{
		if(setupDB())
		{
			fullIndex();
		}
	}
	
	public Lock getReadLock()
	{
		return lock.readLock();
	}
	
	public Lock getWriteLock()
	{
		return lock.writeLock();
	}
	
	public boolean isValidated()
	{
		return validated;
	}
	
	public void setValidated(boolean validated)
	{
		this.validated = validated;
	}
	
	public List<IWorkflowEntry> getWorkflowEntries(IDesignDocument designDocument)
	{
		List<IWorkflowEntry> ret = new ArrayList<IWorkflowEntry>();
		lock.readLock().lock();
		String documentId = getDocumentId(designDocument);
		try
		{
			if(documentId != null)
			{
				Connection con = createConnection(true);
				Statement st = con.createStatement();
				ResultSet rs = st.executeQuery("select id, name from workflowentries where documentid = '" + documentId + "'");
				while(rs.next())
				{
					String id = rs.getString(1);
					String name = rs.getString(2);
					IndexedWorkflowEntry workflowEntry = ((DesignDocument)designDocument).new IndexedWorkflowEntry(id, name);
					ret.add(workflowEntry);
				}
				rs.close();
				for(IWorkflowEntry workflowEntry : ret)
				{
					rs = st.executeQuery("select name, type, basetype, precision from variables where documentid = '" + documentId + "' and elementid = '" + workflowEntry.getId() + "'");
					while(rs.next())
					{
						String name = rs.getString(1);
						String type = rs.getString(2);
						String baseType = rs.getString(3);
						int precision = rs.getInt(4);
						FieldType ft = null;
						Primitive prim = Primitive.find(type);
						if(prim != null)
						{
							if(prim.hasBaseType())
							{
								Primitive basePrim = Primitive.find(baseType);
								if(basePrim != null)
								{
									ft = new FieldType(prim, basePrim);
								}
								else
								{
									ft = new FieldType(prim, designDocument.getProject().getBusinessObjectSet().getBusinessObject(baseType));
								}
							}
							else
								ft = new FieldType(prim);
							ft.setPrecision(precision);
						}
						else
							ft = new FieldType(designDocument.getProject().getBusinessObjectSet().getBusinessObject(type));
						Variable v = new Variable(name, ft);
						VariableHelper.buildObjectFields(v, designDocument.getProject().getBusinessObjectSet());
						((IndexedWorkflowEntry)workflowEntry).addInputVariable(v);
					}
					rs.close();
				}
				st.close();
				con.close();
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			lock.readLock().unlock();
		}
		return ret;
	}
	
	public List<IWorkflowExit> getWorkflowExits(IDesignDocument designDocument)
	{
		List<IWorkflowExit> ret = new ArrayList<IWorkflowExit>();
		lock.readLock().lock();
		String documentId = getDocumentId(designDocument);
		try
		{
			if(documentId != null)
			{
				Connection con = createConnection(true);
				Statement st = con.createStatement();
				ResultSet rs = st.executeQuery("select id, name, type from workflowexits where documentid = '" + documentId + "'");
				while(rs.next())
				{
					String id = rs.getString(1);
					String name = rs.getString(2);
					String type = rs.getString(3);
					IndexedWorkflowExit workflowExit = ((DesignDocument)designDocument).new IndexedWorkflowExit(id, name, type);
					ret.add(workflowExit);
				}
				rs.close();
				for(IWorkflowExit workflowExit : ret)
				{
					rs = st.executeQuery("select name, type, basetype, precision from variables where documentid = '" + documentId + "' and elementid = '" + workflowExit.getId() + "'");
					while(rs.next())
					{
						String name = rs.getString(1);
						String type = rs.getString(2);
						String baseType = rs.getString(3);
						int precision = rs.getInt(4);
						FieldType ft = null;
						Primitive prim = Primitive.find(type);
						if(prim != null)
						{
							if(prim.hasBaseType())
							{
								Primitive basePrim = Primitive.find(baseType);
								if(basePrim != null)
								{
									ft = new FieldType(prim, basePrim);
								}
								else
								{
									ft = new FieldType(prim, designDocument.getProject().getBusinessObjectSet().getBusinessObject(baseType));
								}
							}
							else
								ft = new FieldType(prim);
							ft.setPrecision(precision);
						}
						else
							ft = new FieldType(designDocument.getProject().getBusinessObjectSet().getBusinessObject(type));
						Variable v = new Variable(name, ft);
						VariableHelper.buildObjectFields(v, designDocument.getProject().getBusinessObjectSet());
						((IndexedWorkflowExit)workflowExit).addExportedVariable(v);
					}
					rs.close();
				}
				st.close();
				con.close();
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			lock.readLock().unlock();
		}
		return ret;
	}
	
	public List<IWorkflowReference> getWorkflowReferences(IDesignDocument designDocument)
	{
		List<IWorkflowReference> ret = new ArrayList<IWorkflowReference>();
		lock.readLock().lock();
		String documentId = getDocumentId(designDocument);
		try
		{
			if(documentId != null)
			{
				Connection con = createConnection(true);
				Statement st = con.createStatement();
				ResultSet rs = st.executeQuery("select id, target, entry from workflowreferences where documentid = '" + documentId + "'");
				while(rs.next())
				{
					String id = rs.getString(1);
					String target = rs.getString(2);
					String entry = rs.getString(3);
					IndexedWorkflowReference workflowReference = new DesignDocument.IndexedWorkflowReference(id, target, entry);
					ret.add(workflowReference);
				}
				rs.close();
				st.close();
				con.close();
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			lock.readLock().unlock();
		}
		return ret;
	}
	
	public List<IWorkflowReference> getWorkflowReferences()
	{
		List<IWorkflowReference> ret = new ArrayList<IWorkflowReference>();
		lock.readLock().lock();
		try
		{
			Connection con = createConnection(true);
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("select id, target, entry from workflowreferences");
			while(rs.next())
			{
				String id = rs.getString(1);
				String target = rs.getString(2);
				String entry = rs.getString(3);
				IndexedWorkflowReference workflowReference = new DesignDocument.IndexedWorkflowReference(id, target, entry);
				ret.add(workflowReference);
			}
			rs.close();
			st.close();
			con.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			lock.readLock().unlock();
		}
		return ret;
	}
	
	public List<IDesignEntryPoint> getDesignEntries(IDesignDocument designDocument)
	{
		List<IDesignEntryPoint> ret = new ArrayList<IDesignEntryPoint>();
		lock.readLock().lock();
		String documentId = getDocumentId(designDocument);
		try
		{
			if(documentId != null)
			{
				Connection con = createConnection(true);
				Statement st = con.createStatement();
				ResultSet rs = st.executeQuery("select id, name from designentries where documentid = '" + documentId + "'");
				while(rs.next())
				{
					String id = rs.getString(1);
					String name = rs.getString(2);
					IndexedDesignEntry designEntry = ((DesignDocument)designDocument).new IndexedDesignEntry(id, name);
					ret.add(designEntry);
				}
				rs.close();
				st.close();
				con.close();
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			lock.readLock().unlock();
		}
		return ret;
	}
	
	public List<IDesignExitPoint> getDesignExits(IDesignDocument designDocument)
	{
		List<IDesignExitPoint> ret = new ArrayList<IDesignExitPoint>();
		lock.readLock().lock();
		String documentId = getDocumentId(designDocument);
		try
		{
			if(documentId != null)
			{
				Connection con = createConnection(true);
				Statement st = con.createStatement();
				ResultSet rs = st.executeQuery("select id, targetid, targetname from designexits where documentid = '" + documentId + "'");
				while(rs.next())
				{
					String id = rs.getString(1);
					String targetId = rs.getString(2);
					String targetName = rs.getString(3);
					IndexedDesignExit designExit = ((DesignDocument)designDocument).new IndexedDesignExit(id, targetId, targetName);
					ret.add(designExit);
				}
				rs.close();
				for(IDesignExitPoint workflowExit : ret)
				{
					rs = st.executeQuery("select name, type, basetype, precision from variables where documentid = '" + documentId + "' and elementid = '" + workflowExit.getId() + "'");
					while(rs.next())
					{
						String name = rs.getString(1);
						String type = rs.getString(2);
						String baseType = rs.getString(3);
						int precision = rs.getInt(4);
						FieldType ft = null;
						Primitive prim = Primitive.find(type);
						if(prim != null)
						{
							if(prim.hasBaseType())
							{
								Primitive basePrim = Primitive.find(baseType);
								if(basePrim != null)
								{
									ft = new FieldType(prim, basePrim);
								}
								else
								{
									ft = new FieldType(prim, designDocument.getProject().getBusinessObjectSet().getBusinessObject(baseType));
								}
							}
							else
								ft = new FieldType(prim);
							ft.setPrecision(precision);
						}
						else
							ft = new FieldType(designDocument.getProject().getBusinessObjectSet().getBusinessObject(type));
						Variable v = new Variable(name, ft);
						VariableHelper.buildObjectFields(v, designDocument.getProject().getBusinessObjectSet());
						((IndexedDesignExit)workflowExit).addExportedVariable(v);
					}
					rs.close();
				}
				st.close();
				con.close();
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			lock.readLock().unlock();
		}
		return ret;
	}
	
	public List<IWorkflowEntry> getUpstreamWorkflowEntries(IDesignDocument designDocument, IWorkflowExit workflowExit)
	{
		return getUpstreamWorkflowEntries(designDocument, workflowExit.getId());
	}
	
	public List<IWorkflowEntry> getUpstreamWorkflowEntries(IDesignDocument designDocument, IDesignExitPoint designExit)
	{
		return getUpstreamWorkflowEntries(designDocument, designExit.getId());
	}
	
	private List<IWorkflowEntry> getUpstreamWorkflowEntries(IDesignDocument designDocument, String sourceId)
	{
		List<IWorkflowEntry> ret = new ArrayList<IWorkflowEntry>();
		lock.readLock().lock();
		String documentId = getDocumentId(designDocument);
		try
		{
			if(documentId != null)
			{
				Connection con = createConnection(true);
				Statement st = con.createStatement();
				ResultSet rs = st.executeQuery("select id, name from workflowentries where documentid = '" + documentId + "' and id in (select upstreamid from streamindex where documentid = '" + documentId + "' and downstreamid = '" + sourceId + "')");
				while(rs.next())
				{
					String id = rs.getString(1);
					String name = rs.getString(2);
					IndexedWorkflowEntry workflowEntry = ((DesignDocument)designDocument).new IndexedWorkflowEntry(id, name);
					ret.add(workflowEntry);
				}
				rs.close();
				for(IWorkflowEntry workflowEntry : ret)
				{
					rs = st.executeQuery("select name, type, basetype, precision from variables where documentid = '" + documentId + "' and elementid = '" + workflowEntry.getId() + "'");
					while(rs.next())
					{
						String name = rs.getString(1);
						String type = rs.getString(2);
						String baseType = rs.getString(3);
						int precision = rs.getInt(4);
						FieldType ft = null;
						Primitive prim = Primitive.find(type);
						if(prim != null)
						{
							if(prim.hasBaseType())
							{
								Primitive basePrim = Primitive.find(baseType);
								if(basePrim != null)
								{
									ft = new FieldType(prim, basePrim);
								}
								else
								{
									ft = new FieldType(prim, designDocument.getProject().getBusinessObjectSet().getBusinessObject(baseType));
								}
							}
							else
								ft = new FieldType(prim);
							ft.setPrecision(precision);
						}
						else
							ft = new FieldType(designDocument.getProject().getBusinessObjectSet().getBusinessObject(type));
						Variable v = new Variable(name, ft);
						VariableHelper.buildObjectFields(v, designDocument.getProject().getBusinessObjectSet());
						((IndexedWorkflowEntry)workflowEntry).addInputVariable(v);
					}
					rs.close();
				}
				st.close();
				con.close();
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			lock.readLock().unlock();
		}
		return ret;
	}
	
	public List<IDesignEntryPoint> getUpstreamDesignEntries(IDesignDocument designDocument, IWorkflowExit workflowExit)
	{
		return getUpstreamDesignEntries(designDocument, workflowExit.getId());
	}
	
	public List<IDesignEntryPoint> getUpstreamDesignEntries(IDesignDocument designDocument, IDesignExitPoint designExit)
	{
		return getUpstreamDesignEntries(designDocument, designExit.getId());
	}
	
	public List<IDesignEntryPoint> getUpstreamDesignEntries(IDesignDocument designDocument, String sourceId)
	{
		List<IDesignEntryPoint> ret = new ArrayList<IDesignEntryPoint>();
		lock.readLock().lock();
		String documentId = getDocumentId(designDocument);
		try
		{
			if(documentId != null)
			{
				Connection con = createConnection(true);
				Statement st = con.createStatement();
				ResultSet rs = st.executeQuery("select id, name from designentries where documentid = '" + documentId + "' and id in (select upstreamid from streamindex where documentid = '" + documentId + "' and downstreamid = '" + sourceId + "')");
				while(rs.next())
				{
					String id = rs.getString(1);
					String name = rs.getString(2);
					IndexedDesignEntry designEntry = ((DesignDocument)designDocument).new IndexedDesignEntry(id, name);
					ret.add(designEntry);
				}
				rs.close();
				st.close();
				con.close();
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			lock.readLock().unlock();
		}
		return ret;
	}
	
	public List<IWorkflowExit> getDownstreamWorkflowExits(IDesignDocument designDocument, IWorkflowEntry workflowEntry)
	{
		return getDownstreamWorkflowExits(designDocument, workflowEntry.getId());
	}
	
	public List<IWorkflowExit> getDownstreamWorkflowExits(IDesignDocument designDocument, IDesignEntryPoint designEntry)
	{
		return getDownstreamWorkflowExits(designDocument, designEntry.getId());
	}
	
	public List<IWorkflowExit> getDownstreamWorkflowExits(IDesignDocument designDocument, String sourceId)
	{
		List<IWorkflowExit> ret = new ArrayList<IWorkflowExit>();
		lock.readLock().lock();
		String documentId = getDocumentId(designDocument);
		try
		{
			if(documentId != null)
			{
				Connection con = createConnection(true);
				Statement st = con.createStatement();
				ResultSet rs = st.executeQuery("select id, name, type from workflowexits where documentid = '" + documentId + "' and id in (select downstreamid from streamindex where documentid = '" + documentId + "' and upstreamid = '" + sourceId + "')");
				while(rs.next())
				{
					String id = rs.getString(1);
					String name = rs.getString(2);
					String type = rs.getString(3);
					IndexedWorkflowExit workflowExit = ((DesignDocument)designDocument).new IndexedWorkflowExit(id, name, type);
					ret.add(workflowExit);
				}
				rs.close();
				for(IWorkflowExit workflowExit : ret)
				{
					rs = st.executeQuery("select name, type, basetype, precision from variables where documentid = '" + documentId + "' and elementid = '" + workflowExit.getId() + "'");
					while(rs.next())
					{
						String name = rs.getString(1);
						String type = rs.getString(2);
						String baseType = rs.getString(3);
						int precision = rs.getInt(4);
						FieldType ft = null;
						Primitive prim = Primitive.find(type);
						if(prim != null)
						{
							if(prim.hasBaseType())
							{
								Primitive basePrim = Primitive.find(baseType);
								if(basePrim != null)
								{
									ft = new FieldType(prim, basePrim);
								}
								else
								{
									ft = new FieldType(prim, designDocument.getProject().getBusinessObjectSet().getBusinessObject(baseType));
								}
							}
							else
								ft = new FieldType(prim);
							ft.setPrecision(precision);
						}
						else
							ft = new FieldType(designDocument.getProject().getBusinessObjectSet().getBusinessObject(type));
						Variable v = new Variable(name, ft);
						VariableHelper.buildObjectFields(v, designDocument.getProject().getBusinessObjectSet());
						((IndexedWorkflowExit)workflowExit).addExportedVariable(v);
					}
					rs.close();
				}
				st.close();
				con.close();
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			lock.readLock().unlock();
		}
		return ret;
	}
	
	public List<IDesignExitPoint> getDownstreamDesignExits(IDesignDocument designDocument, IWorkflowEntry workflowEntry)
	{
		return getDownstreamDesignExits(designDocument, workflowEntry.getId());
	}
	
	public List<IDesignExitPoint> getDownstreamDesignExits(IDesignDocument designDocument, IDesignEntryPoint designEntry)
	{
		return getDownstreamDesignExits(designDocument, designEntry.getId());
	}
	
	public List<IDesignExitPoint> getDownstreamDesignExits(IDesignDocument designDocument, String sourceId)
	{
		List<IDesignExitPoint> ret = new ArrayList<IDesignExitPoint>();
		lock.readLock().lock();
		String documentId = getDocumentId(designDocument);
		try
		{
			if(documentId != null)
			{
				Connection con = createConnection(true);
				Statement st = con.createStatement();
				ResultSet rs = st.executeQuery("select id, targetid, targetname from designexits where documentid = '" + documentId + "' and id in (select downstreamid from streamindex where documentid = '" + documentId + "' and upstreamid = '" + sourceId + "')");
				while(rs.next())
				{
					String id = rs.getString(1);
					String targetId = rs.getString(2);
					String targetName = rs.getString(3);
					IndexedDesignExit designExit = ((DesignDocument)designDocument).new IndexedDesignExit(id, targetId, targetName);
					ret.add(designExit);
				}
				rs.close();
				for(IDesignExitPoint workflowExit : ret)
				{
					rs = st.executeQuery("select name, type, basetype, precision from variables where documentid = '" + documentId + "' and elementid = '" + workflowExit.getId() + "'");
					while(rs.next())
					{
						String name = rs.getString(1);
						String type = rs.getString(2);
						String baseType = rs.getString(3);
						int precision = rs.getInt(4);
						FieldType ft = null;
						Primitive prim = Primitive.find(type);
						if(prim != null)
						{
							if(prim.hasBaseType())
							{
								Primitive basePrim = Primitive.find(baseType);
								if(basePrim != null)
								{
									ft = new FieldType(prim, basePrim);
								}
								else
								{
									ft = new FieldType(prim, designDocument.getProject().getBusinessObjectSet().getBusinessObject(baseType));
								}
							}
							else
								ft = new FieldType(prim);
							ft.setPrecision(precision);
						}
						else
							ft = new FieldType(designDocument.getProject().getBusinessObjectSet().getBusinessObject(type));
						Variable v = new Variable(name, ft);
						VariableHelper.buildObjectFields(v, designDocument.getProject().getBusinessObjectSet());
						((IndexedDesignExit)workflowExit).addExportedVariable(v);
					}
					rs.close();
				}
				st.close();
				con.close();
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			lock.readLock().unlock();
		}
		return ret;
	}
	
	public boolean elementExists(IDesignDocument newDocument, String elementId)
	{
		lock.readLock().lock();
		try
		{
			Connection con = createConnection(true);
			Statement st = con.createStatement();
			String documentId = getDocumentId(newDocument);
			ResultSet rs = st.executeQuery("select * from elementindex where elementid = '" + elementId + "'" + (documentId == null ? "" : " and documentid != '" + documentId + "'"));
			boolean ret = rs.next();
			rs.close();
			st.close();
			con.close();
			return ret;
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			lock.readLock().unlock();
		}
		return false;
	}
	
	public String locateElement(String elementId)
	{
		String documentId = null;
		String documentPath = null;
		lock.readLock().lock();
		try
		{
			Connection con = createConnection(true);
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("select documentid from elementindex where elementid = '" + elementId + "'");
			if(rs.next())
				documentId = rs.getString(1);
			rs.close();
			if(documentId != null)
			{
				documentPath = getDocumentPath(documentId);
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			lock.readLock().unlock();
		}
		return documentPath;
	}
	
	public void fullIndex()
	{
		IWorkflowProject wproject = WorkflowCore.getDefault().getWorkflowModel().convertToWorkflowProject(project);
		indexStructure(wproject.getDesignRootFolder());
		indexExportedData(wproject.getDesignRootFolder());
		try
		{
			Connection con = createConnection(true);
			Statement st = con.createStatement();
			System.out.println("Table: designdocuments");
			ResultSet rs = st.executeQuery("select * from designdocuments");
			while(rs.next())
			{
				System.out.println("id: " + rs.getString(1) + " path: " + rs.getString(2));
			}
			rs.close();
			System.out.println("Table: workflowentries");
			rs = st.executeQuery("select * from workflowentries");
			while(rs.next())
			{
				System.out.println("id: " + rs.getString(1) + " name: " + rs.getString(2) + " doc: " + rs.getString(3));
			}
			rs.close();
			System.out.println("Table: variables");
			rs = st.executeQuery("select * from variables");
			while(rs.next())
			{
				System.out.println("name: " + rs.getString(1) + " type: " + rs.getString(2) + " basetype: " + rs.getString(3) + " elementid: " + rs.getString(5) + " doc: " + rs.getString(6));
			}
			rs.close();
			System.out.println("Table: workflowexits");
			rs = st.executeQuery("select * from workflowexits");
			while(rs.next())
			{
				System.out.println("id: " + rs.getString(1) + " name: " + rs.getString(2) + " type: " + rs.getString(3) + " doc: " + rs.getString(4));
			}
			rs.close();
			System.out.println("Table: workflowreferences");
			rs = st.executeQuery("select * from workflowreferences");
			while(rs.next())
			{
				System.out.println("id: " + rs.getString(1) + " target: " + rs.getString(2) + " entry: " + rs.getString(3) + " doc: " + rs.getString(4));
			}
			rs.close();
			System.out.println("Table: designentries");
			rs = st.executeQuery("select * from designentries");
			while(rs.next())
			{
				System.out.println("id: " + rs.getString(1) + " name: " + rs.getString(2) + " doc: " + rs.getString(3));
			}
			rs.close();
			System.out.println("Table: designexits");
			rs = st.executeQuery("select * from designexits");
			while(rs.next())
			{
				System.out.println("id: " + rs.getString(1) + " targetid: " + rs.getString(2) + " targetname: " + rs.getString(3) + " doc: " + rs.getString(4));
			}
			rs.close();
			System.out.println("Table: streamindex");
			rs = st.executeQuery("select * from streamindex");
			while(rs.next())
			{
				System.out.println("upstreamid: " + rs.getString(1) + " downstreamid: " + rs.getString(2) + " doc: " + rs.getString(3));
			}
			rs.close();
			System.out.println("Table: elementindex");
			rs = st.executeQuery("select * from elementindex");
			while(rs.next())
			{
				System.out.println("elementid: " + rs.getString(1) + " doc: " + rs.getString(2));
			}
			rs.close();
			st.close();
			con.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public void indexExportedData()
	{
		IWorkflowProject wproject = WorkflowCore.getDefault().getWorkflowModel().convertToWorkflowProject(project);
		indexExportedData(wproject.getDesignRootFolder());
	}
	
	public void indexStructure(IDesignItemContainer container)
	{
		List<IDesignDocument> docs = container.getDesignDocuments();
		for(IDesignDocument doc : docs)
		{
			indexStructure(doc);
		}
		List<IDesignFolder> folders = container.getDesignFolders();
		for(IDesignFolder folder : folders)
		{
			indexStructure(folder);
		}
	}
	
	public void indexExportedData(IDesignItemContainer container)
	{
		List<IDesignDocument> docs = container.getDesignDocuments();
		for(IDesignDocument doc : docs)
		{
			indexExportedData(doc);
		}
		List<IDesignFolder> folders = container.getDesignFolders();
		for(IDesignFolder folder : folders)
		{
			indexExportedData(folder);
		}
	}
	
	public void indexExportedData(IDesignDocument designDocument)
	{
		validated = false;
		System.out.println("indexing externals: " + designDocument.getUnderlyingFile().getProjectRelativePath());
		long t = System.currentTimeMillis();
		if(!designDocument.isWorkingCopy())
		{
			designDocument.becomeWorkingCopy(false);
			System.out.println("Working copy for " + designDocument.getName() + " in " + Long.toString(System.currentTimeMillis() - t));
		}
		t = System.currentTimeMillis();
		lock.writeLock().lock();
		try
		{
			Connection con = createConnection(true);
			Statement st = con.createStatement();
			String documentId = getDocumentId(designDocument);
			//create new statement object for batch isolation purposes
			if(documentId == null) //should always be true
			{
				documentId = Guid.createGUID();
				st.executeUpdate("insert into designdocuments values ('" + documentId + "', '" + designDocument.getUnderlyingFile().getProjectRelativePath().toString() + "')");
			}
			List<IWorkflowExit> workflowExits = designDocument.getWorkflowExits();
			for(IWorkflowExit workflowExit : workflowExits)
			{
				System.out.println("indexing exports for: " + workflowExit.getId() + " " + workflowExit.getName());
				st.executeUpdate("insert into workflowexits values ('" + workflowExit.getId() + "', '" + workflowExit.getName() + "', '" + workflowExit.getType() + "', '" + documentId + "')");
				st.executeUpdate("delete from variables where elementid = '" + workflowExit.getId() + "' and documentid = '" + documentId + "'");
				List<Variable> exportedVariables = workflowExit.getExportedVariables();
				for(Variable v : exportedVariables)
				{
					st.executeUpdate("insert into variables values ('" + v.getName() + "', '" + v.getType().getName() + "', '" + (v.getType().hasBaseType() ? v.getType().getBaseTypeName() : "") + "', " + v.getType().getPrecision() + ", '" + workflowExit.getId() + "', '" + documentId + "')");
				}
			}
			System.out.println("indexing exported data for " + designDocument.getName() + " in " + Long.toString(System.currentTimeMillis() - t));
			st.close();
			con.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			lock.writeLock().unlock();
		}
	}
	
	public void indexStructure(IDesignDocument designDocument)
	{
		validated = false;
		System.out.println("indexing: " + designDocument.getUnderlyingFile().getProjectRelativePath());
		long t = System.currentTimeMillis();
		if(!designDocument.isWorkingCopy())
		{
			designDocument.becomeWorkingCopy(false);
			System.out.println("Working copy for " + designDocument.getName() + " in " + Long.toString(System.currentTimeMillis() - t));
		}
		t = System.currentTimeMillis();
		lock.writeLock().lock();
		try
		{
			long t2 = System.currentTimeMillis();
			Connection con = createConnection(false);
			Statement st = con.createStatement();
			System.out.println("Time: connection: " + Long.toString(System.currentTimeMillis() - t2));
			t2 = System.currentTimeMillis();
			String documentId = getDocumentId(designDocument);
			if(documentId == null) //should always be true
			{
				documentId = Guid.createGUID();
				st.executeUpdate("insert into designdocuments values ('" + documentId + "', '" + designDocument.getUnderlyingFile().getProjectRelativePath().toString() + "')");
			}
			System.out.println("Time: document id: " + Long.toString(System.currentTimeMillis() - t2));
			t2 = System.currentTimeMillis();
			List<IWorkflowEntry> workflowEntries = designDocument.getWorkflowEntries();
			for(IWorkflowEntry workflowEntry : workflowEntries)
			{
				System.out.println("indexing workflow entry: " + workflowEntry.getId() + " " + workflowEntry.getName());
				st.executeUpdate("insert into workflowentries values ('" + workflowEntry.getId() + "', '" + workflowEntry.getName() + "', '" + documentId + "')");
				List<Variable> inputVariables = workflowEntry.getInputVariables();
				for(Variable v : inputVariables)
				{
					st.executeUpdate("insert into variables values ('" + v.getName() + "', '" + v.getType().getName() + "', '" + (v.getType().hasBaseType() ? v.getType().getBaseTypeName() : "") + "', " + v.getType().getPrecision() + ", '" + workflowEntry.getId() + "', '" + documentId + "')");
				}
			}
			System.out.println("Time: workflow entry: " + Long.toString(System.currentTimeMillis() - t2));
			t2 = System.currentTimeMillis();
			List<IWorkflowReference> workflowReferences = designDocument.getWorkflowReferences();
			for(IWorkflowReference workflowReference : workflowReferences)
			{
				System.out.println("indexing workflow reference: " + workflowReference.getId() + " " + workflowReference.getTargetId() + " " + workflowReference.getEntryId());
				st.executeUpdate("insert into workflowreferences values ('" + workflowReference.getId() + "', '" + workflowReference.getTargetId() + "', '" + workflowReference.getEntryId() + "', '" + documentId + "')");
			}
			System.out.println("Time: workflow reference: " + Long.toString(System.currentTimeMillis() - t2));
			t2 = System.currentTimeMillis();
			List<IDesignEntryPoint> designEntries = designDocument.getDesignEntryPoints();
			for(IDesignEntryPoint designEntry : designEntries)
			{
				System.out.println("indexing design entry: " + designEntry.getId() + " " + designEntry.getName());
				st.executeUpdate("insert into designentries values ('" + designEntry.getId() + "', '" + designEntry.getName() + "', '" + documentId + "')");
			}
			System.out.println("Time: design entry: " + Long.toString(System.currentTimeMillis() - t2));
			t2 = System.currentTimeMillis();
			List<IDesignExitPoint> designExits = designDocument.getDesignExitPoints();
			for(IDesignExitPoint designExit : designExits)
			{
				System.out.println("indexing design exit: " + designExit.getId() + " " + designExit.getTargetId() + " " + designExit.getTargetName());
				st.executeUpdate("insert into designexits values ('" + designExit.getId() + "', '" + designExit.getTargetId() + "', '" + designExit.getTargetName() + "', '" + documentId + "')");
				List<Variable> variables = designExit.getExportedDesignVariables();
				for(Variable v : variables)
				{
					st.executeUpdate("insert into variables values ('" + v.getName() + "', '" + v.getType().getName() + "', '" + (v.getType().hasBaseType() ? v.getType().getBaseTypeName() : "") + "', " + v.getType().getPrecision() + ", '" + designExit.getId() + "', '" + documentId + "')");
				}
			}
			System.out.println("Time: design exit: " + Long.toString(System.currentTimeMillis() - t2));
			t2 = System.currentTimeMillis();
			for(IWorkflowEntry workflowEntry : workflowEntries)
			{
				List<IWorkflowExit> downStreamWorkflowExits = designDocument.getDownStreamWorkflowExits(workflowEntry);
				for(IWorkflowExit workflowExit : downStreamWorkflowExits)
				{
					st.executeUpdate("insert into streamindex values ('" + workflowEntry.getId() + "', '" + workflowExit.getId() + "', '" + documentId + "')");
				}
				List<IDesignExitPoint> downStreamDesignExits = designDocument.getDownStreamDesignExits(workflowEntry);
				for(IDesignExitPoint designExit : downStreamDesignExits)
				{
					st.executeUpdate("insert into streamindex values ('" + workflowEntry.getId() + "', '" + designExit.getId() + "', '" + documentId + "')");
				}
			}
			System.out.println("Time: stream index: workflow entry: " + Long.toString(System.currentTimeMillis() - t2));
			t2 = System.currentTimeMillis();
			for(IDesignEntryPoint designEntry : designEntries)
			{
				List<IWorkflowExit> downStreamWorkflowExits = designDocument.getDownStreamWorkflowExits(designEntry);
				for(IWorkflowExit workflowExit : downStreamWorkflowExits)
				{
					st.executeUpdate("insert into streamindex values ('" + designEntry.getId() + "', '" + workflowExit.getId() + "', '" + documentId + "')");
				}
				List<IDesignExitPoint> downStreamDesignExits = designDocument.getDownStreamDesignExits(designEntry);
				for(IDesignExitPoint designExit : downStreamDesignExits)
				{
					st.executeUpdate("insert into streamindex values ('" + designEntry.getId() + "', '" + designExit.getId() + "', '" + documentId + "')");
				}
			}
			System.out.println("Time: stream index: design entry: " + Long.toString(System.currentTimeMillis() - t2));
			t2 = System.currentTimeMillis();
			for(IDesignElement designElement : designDocument.getMainDesign().getDesignElements())
			{
				st.executeUpdate("insert into elementindex values('" + designElement.getId() + "', '" + documentId + "')");
			}
			System.out.println("Time: element index: " + Long.toString(System.currentTimeMillis() - t2));
			t2 = System.currentTimeMillis();
			con.commit();
			System.out.println("Time: commit: " + Long.toString(System.currentTimeMillis() - t2));
			System.out.println(Long.toString(System.currentTimeMillis() - t));
			System.out.println("indexing structure of " + designDocument.getName() + " in " + Long.toString(System.currentTimeMillis() - t));
			st.close();
			con.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			lock.writeLock().unlock();
		}
	}

	public void clean(IDesignDocument designDocument)
	{
		lock.writeLock().lock();
		try
		{
			String documentId = getDocumentId(designDocument);
			if(documentId == null)
				return;
			Connection con = createConnection(false);
			Statement st = con.createStatement();
			st.executeUpdate("delete from workflowentries where documentid = '" + documentId + "'");
			st.executeUpdate("delete from variables where documentid = '" + documentId + "'");
			st.executeUpdate("delete from workflowexits where documentid = '" + documentId + "'");
			st.executeUpdate("delete from workflowreferences where documentid = '" + documentId + "'");
			st.executeUpdate("delete from designentries where documentid = '" + documentId + "'");
			st.executeUpdate("delete from designexits where documentid = '" + documentId + "'");
			st.executeUpdate("delete from streamindex where documentid = '" + documentId + "'");
			st.executeUpdate("delete from elementindex where documentid = '" + documentId + "'");
			con.commit();
			st.close();
			con.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			lock.writeLock().unlock();
		}
	}
	
	public void cleanProject()
	{
		lock.writeLock().lock();
		try
		{
			File dataDirectory = project.getWorkingLocation("org.eclipse.vtp.desktop.model.core").toFile();
			File indexDirectory = new File(dataDirectory, "index/");
			if(indexDirectory.exists())
			{
				try
				{
					DriverManager.getConnection("jdbc:derby:" + indexDirectory.getAbsolutePath() + ";shutdown=true");
				}
				catch(Exception ex) {}
				deleteDir(indexDirectory);
				indexDirectory.delete();
			}
			setupDB();
		}
		finally
		{
			lock.writeLock().unlock();
		}
	}
	
	private void deleteDir(File dir)
	{
		File[] children = dir.listFiles();
		if(children != null)
		{
			for(File child : children)
			{
				if(child.isDirectory())
				{
					deleteDir(child);
				}
				child.delete();
			}
		}
	}
	
	public void remove(String documentPath)
	{
		String documentId = null;
		lock.writeLock().lock();
		try
		{
			Connection con = createConnection(false);
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("select id from designdocuments where path = '" + documentPath + "'");
			if(rs.next())
				documentId = rs.getString(1);
			rs.close();
			if(documentId != null)
			{
				st.executeUpdate("delete from designdocuments where id = '" + documentId + "'");
				st.executeUpdate("delete from workflowentries where documentid = '" + documentId + "'");
				st.executeUpdate("delete from variables where documentid = '" + documentId + "'");
				st.executeUpdate("delete from workflowexits where documentid = '" + documentId + "'");
				st.executeUpdate("delete from workflowreferences where documentid = '" + documentId + "'");
				st.executeUpdate("delete from designentries where documentid = '" + documentId + "'");
				st.executeUpdate("delete from designexits where documentid = '" + documentId + "'");
				st.executeUpdate("delete from streamindex where documentid = '" + documentId + "'");
				st.executeUpdate("delete from elementindex where documentid = '" + documentId + "'");
			}
			con.commit();
			st.close();
			con.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			lock.writeLock().unlock();
		}
	}
	
	public void move(String originalPath, String destinationPath)
	{
		lock.writeLock().lock();
		try
		{
			Connection con = createConnection(true);
			Statement st = con.createStatement();
			st.executeUpdate("update designdocuments set path = '" + destinationPath + "' where path = '" + originalPath + "'");
			st.close();
			con.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			lock.writeLock().unlock();
		}
	}
	
	private String getDocumentId(IDesignDocument designDocument)
	{
		String documentId = null;
		lock.readLock().lock();
		try
		{
			Connection con = createConnection(true);
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("select id from designdocuments where path = '" + designDocument.getUnderlyingFile().getProjectRelativePath().toString() + "'");
			if(rs.next())
				documentId = rs.getString(1);
			rs.close();
			st.close();
			con.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			lock.readLock().unlock();
		}
		return documentId;
	}
	
	private String getDocumentPath(String documentId)
	{
		String documentPath = null;
		lock.readLock().lock();
		try
		{
			Connection con = createConnection(true);
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("select path from designdocuments where id = '" + documentId + "'");
			if(rs.next())
				documentPath = rs.getString(1);
			rs.close();
			st.close();
			con.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			lock.readLock().unlock();
		}
		return documentPath;
	}
	
	private boolean setupDB()
	{
		return setupDB(true);
	}
	
	private boolean setupDB(boolean retry)
	{
		lock.writeLock().lock();
		File dataDirectory = project.getWorkingLocation("org.eclipse.vtp.desktop.model.core").toFile();
		System.err.println("data directory: " + dataDirectory);
		File indexDirectory = new File(dataDirectory, "index/");
		boolean previousIndex = indexDirectory.exists();
		try
		{
			long t = System.currentTimeMillis();
			Connection con = DriverManager.getConnection("jdbc:derby:" + indexDirectory.getAbsolutePath() + ";create=true");
			System.err.println("Connection Creation: " + project.getName() + " in " + Long.toString(System.currentTimeMillis() - t));
			if(!previousIndex)
			{
				Statement st = con.createStatement();
				createStructure(con, st);
				st.close();
			}
			con.close();
			return !previousIndex;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			if(retry)
			{
				System.err.println("Deleting the database directory and trying again.");
				try
				{
					DriverManager.getConnection("jdbc:derby:" + indexDirectory.getAbsolutePath() + ";shutdown=true");
				}
				catch(Exception ex) {}
				deleteDir(indexDirectory);
				indexDirectory.delete();
				return setupDB(false);
			}
			return false;
		}
		finally
		{
			lock.writeLock().unlock();
		}
	}
	
	private Connection createConnection(boolean autoCommit) throws SQLException
	{
		File dataDirectory = project.getWorkingLocation("org.eclipse.vtp.desktop.model.core").toFile();
		File indexDirectory = new File(dataDirectory, "index/");
		Connection con = DriverManager.getConnection("jdbc:derby:" + indexDirectory.getAbsolutePath());
		con.setAutoCommit(autoCommit);
		return con;
	}
	
	private void createStructure(Connection con, Statement st)
	{
        try
		{
			System.out.println("creating tables for: " + project.getName());
			st.execute("create table designdocuments (id VARCHAR(32) NOT NULL, path VARCHAR(1000) NOT NULL)");
			st.execute("create table workflowentries (id VARCHAR(32) NOT NULL, name VARCHAR(255) NOT NULL, documentid VARCHAR(32) NOT NULL)");
			st.execute("create table variables (name VARCHAR(255) NOT NULL, type VARCHAR(255) NOT NULL, basetype VARCHAR(255) NOT NULL, precision int NOT NULL, elementid VARCHAR(32) NOT NULL, documentid VARCHAR(32) NOT NULL)");
			st.execute("create table workflowexits (id VARCHAR(32) NOT NULL, name VARCHAR(255) NOT NULL, type VARCHAR(8) NOT NULL, documentid VARCHAR(32) NOT NULL)");
			st.execute("create table workflowreferences (id VARCHAR(32) NOT NULL, target VARCHAR(32) NOT NULL, entry VARCHAR(32) NOT NULL, documentid VARCHAR(32) NOT NULL)");
			st.execute("create table designentries (id VARCHAR(32) NOT NULL, name VARCHAR(255) NOT NULL, documentid VARCHAR(32) NOT NULL)");
			st.execute("create table designexits (id VARCHAR(32) NOT NULL, targetid VARCHAR(32) NOT NULL, targetname VARCHAR(32) NOT NULL, documentid VARCHAR(32) NOT NULL)");
			st.execute("create table streamindex (upstreamid VARCHAR(32) NOT NULL, downstreamid VARCHAR(32) NOT NULL, documentid VARCHAR(32) NOT NULL)");
			st.execute("create table elementindex (elementid VARCHAR(32) NOT NULL, documentid VARCHAR(32) NOT NULL)");
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
}
