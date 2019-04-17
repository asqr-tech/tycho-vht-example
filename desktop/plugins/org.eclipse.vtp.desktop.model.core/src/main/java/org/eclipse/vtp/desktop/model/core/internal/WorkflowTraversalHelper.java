package org.eclipse.vtp.desktop.model.core.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.vtp.desktop.model.core.IDesignDocument;
import org.eclipse.vtp.desktop.model.core.IDesignFolder;
import org.eclipse.vtp.desktop.model.core.IDesignItemContainer;
import org.eclipse.vtp.desktop.model.core.IDesignRootFolder;
import org.eclipse.vtp.desktop.model.core.IWorkflowEntry;
import org.eclipse.vtp.desktop.model.core.IWorkflowExit;
import org.eclipse.vtp.desktop.model.core.IWorkflowProject;
import org.eclipse.vtp.desktop.model.core.design.IDesignEntryPoint;
import org.eclipse.vtp.desktop.model.core.design.IDesignExitPoint;

public class WorkflowTraversalHelper
{
	private IWorkflowProject workflow = null;
	private List<IDesignDocument> workingCopies = null;
	private Map<IDesignDocument, List<IDesignEntryPoint>> designEntriesByDocument = new HashMap<IDesignDocument, List<IDesignEntryPoint>>();
	private Map<IDesignDocument, List<IDesignExitPoint>> designExitsByDocument = new HashMap<IDesignDocument, List<IDesignExitPoint>>();
	private Map<String, IDesignDocument> documentsByDesignEntry = new HashMap<String, IDesignDocument>();
	private Map<String, IDesignDocument> documentsByDesignExit = new HashMap<String, IDesignDocument>();
	private Map<String, List<IDesignExitPoint>> designExitsByDesignEntry = new HashMap<String, List<IDesignExitPoint>>();
	private Map<String, IDesignEntryPoint> designEntriesByDesignExit = new HashMap<String, IDesignEntryPoint>();
	private Map<IDesignDocument, List<IWorkflowEntry>> workflowEntriesByDocument = new HashMap<IDesignDocument, List<IWorkflowEntry>>();
	private Map<IDesignDocument, List<IWorkflowExit>> workflowExitsByDocument = new HashMap<IDesignDocument, List<IWorkflowExit>>();
	private Map<String, IDesignDocument> documentsByWorkflowEntry = new HashMap<String, IDesignDocument>();
	private Map<String, IDesignDocument> documentsByWorkflowExit = new HashMap<String, IDesignDocument>();
	private Map<String, IDesignEntryPoint> designEntriesById = new HashMap<String, IDesignEntryPoint>();
	private Map<String, IDesignExitPoint> designExitsById = new HashMap<String, IDesignExitPoint>();
	private Map<String, IWorkflowEntry> workflowEntriesById = new HashMap<String, IWorkflowEntry>();
	private Map<String, IWorkflowExit> workflowExitsById = new HashMap<String, IWorkflowExit>();

	public WorkflowTraversalHelper(IWorkflowProject workflow, List<IDesignDocument> workingCopies)
	{
		super();
		this.workflow = workflow;
		this.workingCopies = workingCopies;
		IDesignRootFolder rootFolder = this.workflow.getDesignRootFolder();
		index(rootFolder);
		for(IDesignEntryPoint entryPoint : designEntriesById.values())
		{
			designExitsByDesignEntry.put(entryPoint.getId(), new ArrayList<IDesignExitPoint>());
		}
		for(IDesignExitPoint exitPoint : designExitsById.values())
		{
			IDesignEntryPoint entryPoint = designEntriesById.get(exitPoint.getTargetId());
			if(entryPoint != null)
			{
				designExitsByDesignEntry.get(entryPoint.getId()).add(exitPoint);
				designEntriesByDesignExit.put(exitPoint.getId(), entryPoint);
			}
		}
	}

	private void index(IDesignItemContainer container)
	{
		List<IDesignDocument> documents = container.getDesignDocuments();
		for(IDesignDocument document : documents)
		{
			for(IDesignDocument workingCopy : workingCopies)
			{
				if(workingCopy.equals(document))
				{
					document = workingCopy;
					break;
				}
			}
			List<IDesignEntryPoint> entryPoints = document.getDesignEntryPoints();
			designEntriesByDocument.put(document, entryPoints);
			for(IDesignEntryPoint entryPoint : entryPoints)
			{
				documentsByDesignEntry.put(entryPoint.getId(), document);
				designEntriesById.put(entryPoint.getId(), entryPoint);
			}
			List<IDesignExitPoint> exitPoints = document.getDesignExitPoints();
			designExitsByDocument.put(document, exitPoints);
			for(IDesignExitPoint exitPoint : exitPoints)
			{
				documentsByDesignExit.put(exitPoint.getId(), document);
				designExitsById.put(exitPoint.getId(), exitPoint);
			}
			List<IWorkflowEntry> workflowEntryPoints = document.getWorkflowEntries();
			workflowEntriesByDocument.put(document, workflowEntryPoints);
			for(IWorkflowEntry entryPoint : workflowEntryPoints)
			{
				documentsByWorkflowEntry.put(entryPoint.getId(), document);
				workflowEntriesById.put(entryPoint.getId(), entryPoint);
			}
			List<IWorkflowExit> workflowExitPoints = document.getWorkflowExits();
			workflowExitsByDocument.put(document, workflowExitPoints);
			for(IWorkflowExit exitPoint : workflowExitPoints)
			{
				documentsByWorkflowExit.put(exitPoint.getId(), document);
				workflowExitsById.put(exitPoint.getId(), exitPoint);
			}
		}
		List<IDesignFolder> folders = container.getDesignFolders();
		for(IDesignFolder folder : folders)
		{
			index(folder);
		}
	}
	
	public IWorkflowEntry getWorkflowEntry(String id)
	{
		return workflowEntriesById.get(id);
	}
	
	public List<IWorkflowEntry> getAllWorkflowEntries()
	{
		return new LinkedList<IWorkflowEntry>(workflowEntriesById.values());
	}
	
	public List<IDesignExitPoint> getUpStreamExitPoints(IDesignEntryPoint entryPoint)
	{
		List<IDesignExitPoint> ret = new ArrayList<IDesignExitPoint>();
		Map<String, IDesignEntryPoint> visited = new HashMap<String, IDesignEntryPoint>();
		visited.put(entryPoint.getId(), entryPoint);
		getUpStreamExitPoints(entryPoint, ret, visited);
		return ret;
	}
	
	public List<IDesignExitPoint> getUpStreamExitPoints(List<IDesignEntryPoint> entryPoints)
	{
		List<IDesignExitPoint> ret = new ArrayList<IDesignExitPoint>();
		Map<String, IDesignEntryPoint> visited = new HashMap<String, IDesignEntryPoint>();
		for(IDesignEntryPoint entryPoint : entryPoints)
		{
			visited.put(entryPoint.getId(), entryPoint);
			getUpStreamExitPoints(entryPoint, ret, visited);
		}
		return ret;
	}
	
	private void getUpStreamExitPoints(IDesignEntryPoint entryPoint, List<IDesignExitPoint> exits, Map<String, IDesignEntryPoint> visited)
	{
		List<IDesignExitPoint> connectedExits = designExitsByDesignEntry.get(entryPoint.getId());
		for(IDesignExitPoint connectedExit : connectedExits)
		{
			exits.add(connectedExit);
			IDesignDocument document = documentsByDesignExit.get(connectedExit.getId());
			List<IDesignEntryPoint> upStreamEntries = document.getUpStreamDesignEntries(connectedExit);
			for(IDesignEntryPoint upStreamEntry : upStreamEntries)
			{
				if(visited.get(upStreamEntry.getId()) == null)
				{
					visited.put(upStreamEntry.getId(), upStreamEntry);
					getUpStreamExitPoints(upStreamEntry, exits, visited);
				}
			}
		}
	}

	public List<IDesignEntryPoint> getDownStreamEntryPoints(IDesignExitPoint exitPoint)
	{
		List<IDesignEntryPoint> ret = new ArrayList<IDesignEntryPoint>();
		Map<String, IDesignExitPoint> visited = new HashMap<String, IDesignExitPoint>();
		visited.put(exitPoint.getId(), exitPoint);
		getDownStreamEntryPoints(exitPoint, ret, visited);
		return ret;
	}
	
	public List<IDesignEntryPoint> getDownStreamEntryPoints(List<IDesignExitPoint> exitPoints)
	{
		List<IDesignEntryPoint> ret = new ArrayList<IDesignEntryPoint>();
		Map<String, IDesignExitPoint> visited = new HashMap<String, IDesignExitPoint>();
		for(IDesignExitPoint exitPoint : exitPoints)
		{
			visited.put(exitPoint.getId(), exitPoint);
			getDownStreamEntryPoints(exitPoint, ret, visited);
		}
		return ret;
	}
	
	private void getDownStreamEntryPoints(IDesignExitPoint exitPoint, List<IDesignEntryPoint> entries, Map<String, IDesignExitPoint> visited)
	{
		IDesignEntryPoint targetEntry = designEntriesByDesignExit.get(exitPoint.getId());
		if(targetEntry != null)
		{
			entries.add(targetEntry);
			IDesignDocument document = documentsByDesignEntry.get(targetEntry.getId());
			List<IDesignExitPoint> exits = document.getDownStreamDesignExits(targetEntry);
			for(IDesignExitPoint exit : exits)
			{
				if(visited.get(exit.getId()) == null)
				{
					visited.put(exit.getId(), exit);
					getDownStreamEntryPoints(exit, entries, visited);
				}
			}
		}
	}
	
	public List<IWorkflowExit> getDownStreamWorkflowExits(IWorkflowEntry workflowEntry)
	{
		Map<String, IWorkflowExit> exits = new HashMap<String, IWorkflowExit>();
		IDesignDocument document = documentsByWorkflowEntry.get(workflowEntry.getId());
		List<IDesignExitPoint> designExits = document.getDownStreamDesignExits(workflowEntry);
		List<IDesignEntryPoint> downStreamDesignEntries = getDownStreamEntryPoints(designExits);
		for(IDesignEntryPoint downStreamDesignEntry : downStreamDesignEntries)
		{
			IDesignDocument downStreamDocument = documentsByDesignEntry.get(downStreamDesignEntry.getId());
			List<IWorkflowExit> downStreamExits = downStreamDocument.getDownStreamWorkflowExits(downStreamDesignEntry);
			for(IWorkflowExit downStreamExit : downStreamExits)
			{
				exits.put(downStreamExit.getId(), downStreamExit);
			}
		}
		List<IWorkflowExit> documentExits = document.getDownStreamWorkflowExits(workflowEntry);
		for(IWorkflowExit documentExit : documentExits)
		{
			exits.put(documentExit.getId(), documentExit);
		}
		return new ArrayList<IWorkflowExit>(exits.values());
	}
}
