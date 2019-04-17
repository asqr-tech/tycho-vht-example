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
package org.eclipse.vtp.desktop.export.internal.wizards;

import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.vtp.desktop.export.internal.ExportAgent;
import org.eclipse.vtp.desktop.export.internal.ExportCore;
import org.eclipse.vtp.desktop.export.internal.Exporter;
import org.eclipse.vtp.desktop.export.internal.MediaExporter;
import org.eclipse.vtp.desktop.export.internal.ProjectExporter;
import org.eclipse.vtp.desktop.export.internal.WorkflowExporter;
import org.eclipse.vtp.desktop.export.internal.main.WebApplicationExporter;
import org.eclipse.vtp.desktop.export.internal.pages.ArchiveSelectionPage;
import org.eclipse.vtp.desktop.export.internal.pages.ProjectSelectionPage;
import org.eclipse.vtp.desktop.model.core.IWorkflowProject;
import org.eclipse.vtp.desktop.model.core.WorkflowCore;
import org.eclipse.vtp.desktop.model.interactive.core.IMediaProject;
import org.eclipse.vtp.desktop.model.interactive.core.InteractiveWorkflowCore;

/**
 * The wizard that exports projects as web applications.
 * 
 * @author Lonnie Pryor
 */
public class ExportWizard extends Wizard implements IExportWizard {
	private final List<ExportAgent> agents = Collections
			.unmodifiableList(ExportCore.createExportAgents());
	private final Exporter exporter = new Exporter(agents);
	private List<WorkflowExporter> workflowProjects;
	private final Map<IWizardPage, ExportAgent> pages = new LinkedHashMap<IWizardPage, ExportAgent>();

	/**
	 * Creates a new ExportWizard.
	 */
	public ExportWizard() {
		try {
			// Load the workflow projects.
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			dbf.setValidating(false);
			DocumentBuilder db = dbf.newDocumentBuilder();
			List<IWorkflowProject> rawProjects = WorkflowCore.getDefault()
					.getWorkflowModel().listWorkflowProjects();
			ArrayList<WorkflowExporter> workflowList = new ArrayList<WorkflowExporter>(
					rawProjects.size());
			for (IWorkflowProject project : rawProjects)
				workflowList.add(new WorkflowExporter(exporter, db, project));
			Collections.sort(workflowList);
			workflowProjects = Collections.unmodifiableList(workflowList);
			// Done.
			setNeedsProgressMonitor(true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public final void init(IWorkbench workbench, IStructuredSelection selection) {
		try {
			// Index the workflow projects.
			Map<String, WorkflowExporter> workflowMap = new HashMap<String, WorkflowExporter>();
			for (WorkflowExporter project : workflowProjects)
				workflowMap.put(project.getProject().getName(), project);
			// Load the media projects.
			Map<String, MediaExporter> mediaMap = new HashMap<String, MediaExporter>();
			for (IMediaProject project : InteractiveWorkflowCore.getDefault()
					.getInteractiveWorkflowModel().listMediaProjects())
				mediaMap.put(project.getName(), new MediaExporter(exporter,
						project));
			ArrayList<MediaExporter> mediaList = new ArrayList<MediaExporter>(
					mediaMap.values());
			Collections.sort(mediaList);
			// Resolve dependencies between projects.
			SAXParserFactory spf = SAXParserFactory.newInstance();
			spf.setNamespaceAware(true);
			spf.setValidating(false);
			SAXParser sp = spf.newSAXParser();
			for (WorkflowExporter exporter : workflowProjects)
				exporter.resolveDependencies(sp, workflowMap, mediaMap);
			// Find any projects that have open, unsaved resources.
			IWorkbenchPage[] workbenchPages = workbench
					.getActiveWorkbenchWindow().getPages();
			for (int i = 0; i < workbenchPages.length; ++i) {
				IEditorReference[] editorReferences = workbenchPages[i]
						.getEditorReferences();
				for (int j = 0; j < editorReferences.length; ++j) {
					if (!editorReferences[j].isDirty())
						continue;
					IEditorInput editorInput = null;
					try {
						editorInput = editorReferences[j].getEditorInput();
					} catch (PartInitException e) {
						e.printStackTrace();
					}
					if (editorInput instanceof IFileEditorInput) {
						String name = ((IFileEditorInput) editorInput)
								.getFile().getProject().getName();
						ProjectExporter project = workflowMap.get(name);
						if (project == null) {
							project = mediaMap.get(name);
							if (project == null)
								continue;
						}
						project.setDirty(true);
					}
				}
			}
			// Set the initial selection.
			Set<WorkflowExporter> selectedProjects = new HashSet<WorkflowExporter>();
			ISelection currentSelection = workbench.getActiveWorkbenchWindow()
					.getSelectionService().getSelection();
			if (currentSelection instanceof IStructuredSelection) {
				for (Object selected : ((IStructuredSelection) currentSelection)
						.toList()) {
					if (selected instanceof IProject) {
						WorkflowExporter project = workflowMap
								.get(((IProject) selected).getName());
						if (project != null)
							selectedProjects.add(project);
					}
				}
			}
			// Create the default pages.
			ArchiveSelectionPage archiveSelectionPage = new ArchiveSelectionPage(
					exporter);
			ProjectSelectionPage projectSelectionPage = new ProjectSelectionPage(
					exporter, workflowProjects, selectedProjects);
			pages.put(archiveSelectionPage, null);
			pages.put(projectSelectionPage, null);
			for (ExportAgent agent : agents)
				for (IWizardPage page : agent.getValue().init())
					pages.put(page, agent);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public final void addPages() {
		for (IWizardPage page : pages.keySet())
			addPage(page);
	}

	public final IWizardPage getNextPage(IWizardPage page) {
		IWizardPage nextPage = super.getNextPage(page);
		ExportAgent agent = pages.get(nextPage);
		while (agent != null && !agent.getValue().shouldBeShown(nextPage)) {
			nextPage = super.getNextPage(nextPage);
			agent = pages.get(nextPage);
		}
		return nextPage;
	}

	public final IWizardPage getPreviousPage(IWizardPage page) {
		IWizardPage previousPage = super.getPreviousPage(page);
		ExportAgent agent = pages.get(previousPage);
		while (agent != null && !agent.getValue().shouldBeShown(previousPage)) {
			previousPage = super.getPreviousPage(previousPage);
			agent = pages.get(previousPage);
		}
		return previousPage;
	}

	public final boolean canFinish() {
		for (ExportAgent agent : agents)
			if (!agent.getValue().canFinish())
				return false;
		return super.canFinish();
	}

	public final boolean performFinish() {
		try {
			getContainer().run(true, true, new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor)
						throws InvocationTargetException, InterruptedException {
					try {
						if (!exporter.isUsingArchiveFile()
								&& !exporter.getArchiveLocation().isDirectory()
								&& !exporter.getArchiveLocation().mkdirs())
							throw new FileNotFoundException(exporter
									.getArchiveLocation().getAbsolutePath());
						new WebApplicationExporter(agents).export(
								exporter.getArchiveLocation(),
								exporter.getWorkflowSelection(),
								exporter.getMediaSelection(), exporter.hasSeparateMedia(), exporter.getMediaLocation(), monitor);
						((ArchiveSelectionPage) pages.keySet().iterator()
								.next()).saveArchivePath();
						for (WorkflowExporter e : exporter
								.getWorkflowSelection())
							e.saveSettings(exporter);
						for (MediaExporter e : exporter.getMediaSelection())
							e.saveSettings(exporter);
						ExportCore.flushPreferences();
					} catch (Exception e) {
						e.printStackTrace();
						throw new InvocationTargetException(e);
					}
				}
			});
			return true;
		} catch (InvocationTargetException e) {
			ExportCore.displayError(getShell(),
					"Error Exporting Web Application", e.getTargetException());
		} catch (Exception e) {
			ExportCore.displayError(getShell(),
					"Error Exporting Web Application", e);
		}
		return false;
	}
}
