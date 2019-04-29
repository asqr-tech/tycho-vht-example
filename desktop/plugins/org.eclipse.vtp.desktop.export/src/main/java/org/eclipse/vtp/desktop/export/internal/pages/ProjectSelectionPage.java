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
package org.eclipse.vtp.desktop.export.internal.pages;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.vtp.desktop.export.internal.Exporter;
import org.eclipse.vtp.desktop.export.internal.MediaExporter;
import org.eclipse.vtp.desktop.export.internal.WorkflowExporter;

/**
 * Wizard page responsible for selecting application projects to export.
 * 
 * @author Lonnie Pryor
 */
@SuppressWarnings("deprecation")
public final class ProjectSelectionPage extends WizardPage
		implements IStructuredContentProvider, ICheckStateListener, SelectionListener {
	private final Exporter exporter;
	private final Collection<WorkflowExporter> projects;
	private final Set<WorkflowExporter> initialSelection;
	private CheckboxTableViewer viewer = null;
	private Button selectAll = null;
	private Button deselectAll = null;
	private boolean ignoreProjectSelectionChanged = false;

	public ProjectSelectionPage(Exporter exporter, Collection<WorkflowExporter> projects,
			Set<WorkflowExporter> initialSelection) {
		super(ProjectSelectionPage.class.getSimpleName(), "Select the Projects to Export", null);
		this.exporter = exporter;
		this.projects = projects;
		this.initialSelection = initialSelection;
	}

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		viewer = CheckboxTableViewer.newCheckList(composite, SWT.BORDER);
		viewer.addCheckStateListener(this);
		viewer.setContentProvider(this);
		viewer.setSorter(new ViewerSorter());
		viewer.setInput(projects);
		viewer.setCheckedElements(initialSelection.toArray());
		viewer.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 3));
		selectAll = new Button(composite, SWT.PUSH);
		selectAll.setText("Select All");
		selectAll.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));
		selectAll.addSelectionListener(this);
		deselectAll = new Button(composite, SWT.PUSH);
		deselectAll.setText("Deselect All");
		deselectAll.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));
		deselectAll.addSelectionListener(this);
		Label extra = new Label(composite, SWT.NONE);
		extra.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
		setControl(composite);
		projectSelectionChanged(false);
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	public Object[] getElements(Object inputElement) {
		return projects.toArray();
	}

	public void checkStateChanged(CheckStateChangedEvent event) {
		projectSelectionChanged(true);
	}

	private void projectSelectionChanged(boolean showErrorMessage) {
		if (viewer == null || ignoreProjectSelectionChanged)
			return;
		Collection<MediaExporter> mediaSelection = new HashSet<MediaExporter>();
		HashMap<WorkflowExporter, Boolean> workflowSelection = new HashMap<WorkflowExporter, Boolean>();
		boolean dirty = false;
		for (WorkflowExporter exporter : projects) {
			if (viewer.getChecked(exporter)) {
				dirty |= workflowSelected(mediaSelection, workflowSelection, exporter, true);
			}
		}
		if (dirty)
			MessageDialog.openInformation(viewer.getControl().getShell(), "Unsaved Projects",
					"One or more of the projects to be exported contains unsaved resources.");
		exporter.setProjectSelection(mediaSelection, workflowSelection.keySet());
		setPageComplete(!workflowSelection.isEmpty());
		if (workflowSelection.isEmpty() && showErrorMessage)
			setErrorMessage("Select at least one project to export.");
		else
			setErrorMessage(null);
		ignoreProjectSelectionChanged = true;
		try {
			for (WorkflowExporter exporter : projects) {
				Boolean state = workflowSelection.get(exporter);
				viewer.setChecked(exporter, state != null);
				viewer.setGrayed(exporter, state != null && !state.booleanValue());
			}
		} finally {
			ignoreProjectSelectionChanged = false;
		}
	}

	private boolean workflowSelected(Collection<MediaExporter> mediaSelection,
			Map<WorkflowExporter, Boolean> workflowSelection, WorkflowExporter exporter, boolean enabled) {
		if (workflowSelection.containsKey(exporter)) {
			workflowSelection.put(exporter, workflowSelection.get(exporter) && enabled);
			return false;
		}
		workflowSelection.put(exporter, enabled);
		boolean dirty = exporter.isDirty();
		for (MediaExporter media : exporter.getMediaDependencies())
			if (mediaSelection.add(media))
				dirty |= media.isDirty();
		for (WorkflowExporter other : exporter.getWorkflowDependencies())
			dirty |= workflowSelected(mediaSelection, workflowSelection, other, false);
		return dirty;
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		if (e.getSource() == selectAll)
			viewer.setCheckedElements(projects.toArray());
		else if (e.getSource() == deselectAll)
			viewer.setCheckedElements(new Object[0]);
		projectSelectionChanged(true);
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
	}

}
