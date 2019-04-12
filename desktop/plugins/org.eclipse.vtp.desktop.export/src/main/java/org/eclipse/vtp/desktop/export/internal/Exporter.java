package org.eclipse.vtp.desktop.export.internal;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;

public class Exporter {
	
	private final Collection<ExportAgent> agents;
	private File archiveLocation = null;
	private boolean usingArchiveFile = false;
	private final Collection<MediaExporter> mediaSelection = new LinkedList<MediaExporter>();
	private final Collection<WorkflowExporter> workflowSelection = new LinkedList<WorkflowExporter>();
	private File mediaLocation = null;
	private boolean separateMedia = false;

	public Exporter(Collection<ExportAgent> agents) {
		this.agents = agents;
	}
	
	public File getArchiveLocation() {
		return archiveLocation;
	}

	public boolean isUsingArchiveFile() {
		return usingArchiveFile;
	}
	
	public boolean hasSeparateMedia()
	{
		return separateMedia;
	}
	
	public File getMediaLocation()
	{
		return mediaLocation;
	}

	public void setArchiveLocation(File archiveLocation, boolean usingArchiveFile) {
		this.archiveLocation = archiveLocation;
		this.usingArchiveFile = usingArchiveFile;
		reloadSettings();
	}
	
	public void setMediaLocation(File mediaLocation)
	{
		this.separateMedia = mediaLocation != null;
		this.mediaLocation = mediaLocation;
	}
	
	public Collection<MediaExporter> getMediaSelection() {
		return Collections.unmodifiableCollection(mediaSelection);
	}
	
	public Collection<WorkflowExporter> getWorkflowSelection() {
		return Collections.unmodifiableCollection(workflowSelection);
	}
	
	public void setProjectSelection (Collection<MediaExporter> mediaSelection, Collection<WorkflowExporter> workflowSelection) {
		this.mediaSelection.clear();
		this.workflowSelection.clear();
		this.mediaSelection.addAll(mediaSelection);
		this.workflowSelection.addAll(workflowSelection);
		reloadSettings();
		Collection<MediaExporter> ms = getMediaSelection();
		Collection<WorkflowExporter> ws = getWorkflowSelection();
		for (ExportAgent agent : agents)
			agent.getValue().setProjects(ws, ms);
	}
	
	public void reloadSettings() {
		for (WorkflowExporter e : workflowSelection)
			e.loadSettings(this);
		for (MediaExporter e : mediaSelection)
			e.loadSettings(this);
	}
	
	public Map<String, String> loadSettings(String projectName) {
		return ExportCore.loadSettings(preferencePrefix(), projectName);
	}
	
	public void saveSettings(String projectName, Map<String, String> settings) {
		ExportCore.saveSettings(preferencePrefix(), projectName, settings);
	}
	
	private String preferencePrefix () {
		return archiveLocation == null ? "" : archiveLocation.getAbsolutePath();
	}
}
