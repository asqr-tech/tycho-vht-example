package org.eclipse.vtp.desktop.export.internal.pages;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.vtp.desktop.export.internal.ExportCore;
import org.eclipse.vtp.desktop.export.internal.Exporter;

/**
 * The archive selection page.
 * 
 * @author Lonnie Pryor
 */
public final class ArchiveSelectionPage extends WizardPage implements
		ModifyListener, SelectionListener {

	private static String getLastArchivePath() {
		return ExportCore.getPreference("lastArchivePath");
	}

	private static String[] getAllArchivePaths() {
		String names = ExportCore.getPreference("allArchivePaths");
		if (names == null || names.length() == 0)
			return new String[0];
		return names.split(File.pathSeparator);
	}

	private final Exporter exporter;
	private Button archiveRadio = null;
	private Combo archiveCombo = null;
	private Button archiveBrowse = null;
	private Button directoryRadio = null;
	private Combo directoryCombo = null;
	private Button directoryBrowse = null;
	private Button mediaCheckbox = null;
	private Combo mediaCombo = null;
	private Button mediaBrowse = null;
	private boolean hasNonMediaError = false;
	private boolean hasMediaError = false;

	public ArchiveSelectionPage(Exporter exporter) {
		super(ArchiveSelectionPage.class.getSimpleName(),
				"Select an Archive File or a Directory", null);
		this.exporter = exporter;
	}

	public void saveArchivePath() {
		String path = exporter.getArchiveLocation().getAbsolutePath();
		if (!exporter.isUsingArchiveFile() && !path.endsWith(File.separator))
			path = path + File.separator;
		Set<String> paths = new TreeSet<String>(
				Arrays.asList(getAllArchivePaths()));
		paths.add(path);
		StringBuilder joined = new StringBuilder();
		Iterator<String> iter = paths.iterator();
		joined.append(iter.next());
		while (iter.hasNext())
			joined.append(File.pathSeparator).append(iter.next());
		ExportCore.setPreference("lastArchivePath", path);
		ExportCore.setPreference("allArchivePaths", joined.toString());
	}

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		archiveRadio = new Button(composite, SWT.RADIO);
		archiveRadio.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 2, 1));
		archiveRadio.setText("Archive file:");
		archiveRadio.addSelectionListener(this);
		archiveCombo = new Combo(composite, SWT.DROP_DOWN);
		archiveCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false));
		archiveCombo.addModifyListener(this);
		archiveCombo.addSelectionListener(this);
		archiveBrowse = new Button(composite, SWT.PUSH);
		archiveBrowse.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false));
		archiveBrowse.setText("Browse...");
		archiveBrowse.addSelectionListener(this);
		directoryRadio = new Button(composite, SWT.RADIO);
		directoryRadio.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 2, 1));
		directoryRadio.setText("Directory:");
		directoryRadio.addSelectionListener(this);
		directoryCombo = new Combo(composite, SWT.DROP_DOWN);
		directoryCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false));
		directoryCombo.addModifyListener(this);
		directoryCombo.addSelectionListener(this);
		directoryBrowse = new Button(composite, SWT.PUSH);
		directoryBrowse.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false));
		directoryBrowse.setText("Browse...");
		directoryBrowse.addSelectionListener(this);
		mediaCheckbox = new Button(composite, SWT.CHECK);
		mediaCheckbox.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 2, 1));
		mediaCheckbox.setText("Export media to separate directory");
		mediaCheckbox.addSelectionListener(this);
		mediaCombo = new Combo(composite, SWT.DROP_DOWN);
		mediaCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false));
		mediaCombo.addModifyListener(this);
		mediaCombo.addSelectionListener(this);
		mediaBrowse = new Button(composite, SWT.PUSH);
		mediaBrowse.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false));
		mediaBrowse.setText("Browse...");
		mediaBrowse.addSelectionListener(this);
		String lastPath = getLastArchivePath();
		String[] paths = getAllArchivePaths();
		Arrays.sort(paths);
		for (int i = 0; i < paths.length; ++i) {
			if (paths[i].endsWith(File.separator)) {
				directoryCombo.add(paths[i]);
				if (paths[i].equals(lastPath)) {
					directoryRadio.setSelection(true);
					directoryCombo.select(directoryCombo.getItemCount() - 1);
				}
			} else {
				archiveCombo.add(paths[i]);
				if (paths[i].equals(lastPath)) {
					archiveRadio.setSelection(true);
					archiveCombo.select(archiveCombo.getItemCount() - 1);
				}
			}
		}
		setControl(composite);
		if (isArchive())
			archiveCombo.setFocus();
		else
			directoryCombo.setFocus();
		evaluate(lastPath, false);
	}

	public void modifyText(ModifyEvent e)
	{
		if(e.getSource() == mediaCombo)
			evaluateMedia(((Combo) e.getSource()).getText(), true);
		else
			evaluate(((Combo) e.getSource()).getText(), true);
	}

	public void widgetSelected(SelectionEvent e) {
		Object source = e.getSource();
		if (source instanceof Button) {
			Button button = (Button) source;
			if (button == archiveRadio)
				evaluate(archiveCombo.getText(), true);
			else if (button == directoryRadio)
				evaluate(directoryCombo.getText(), true);
			else if (button == archiveBrowse) {
				String path = new FileDialog(getShell()).open();
				if (path == null)
					return;
				archiveCombo.setText(path);
				evaluate(path, true);
			}
			else if(button == mediaCheckbox)
			{
				evaluateMedia(mediaCombo.getText(), true);
			}
			else if(button == mediaBrowse)
			{
				String path = new DirectoryDialog(getShell()).open();
				if (path == null)
					return;
				mediaCombo.setText(path);
				evaluateMedia(path, true);
			}
			else {
				String path = new DirectoryDialog(getShell()).open();
				if (path == null)
					return;
				directoryCombo.setText(path);
				evaluate(path, true);
			}
		}
		else
		{
			if(source == mediaCombo)
				evaluateMedia(((Combo) source).getText(), true);
			else
				evaluate(((Combo) source).getText(), true);
		}
	}

	public void widgetDefaultSelected(SelectionEvent e) {
	}

	private boolean isArchive() {
		return archiveRadio.getSelection();
	}

	private void evaluate(String path, boolean showErrorMessage) {
		File file = null;
		String msg = null;
		if (path == null || path.length() == 0) {
			if (isArchive())
				msg = "Select an archive file to export to.";
			else
				msg = "Select a directory to export to.";
		} else {
			try {
				file = new File(path).getCanonicalFile();
				if (isArchive()) {
					if (file.isDirectory())
						msg = "Select a file, not a directory.";
					else if (!(file.getName().endsWith(".war") || file
							.getName().endsWith(".zip")))
						msg = "Archive file name must end with \".war\" or \".zip\".";
				} else {
					if (file.isFile())
						msg = "Select a directory, not a file.";
				}
			} catch (IOException e) {
				msg = "Error resolving archive: " + e.getMessage();
			}
		}
		if (msg != null)
			file = null;
		if (showErrorMessage)
			setErrorMessage(msg);
		hasNonMediaError = msg != null;
		System.err.println("media error: " + hasMediaError);
		System.err.println("non media error: " + hasNonMediaError);
		setPageComplete(!hasMediaError && !hasNonMediaError);
		exporter.setArchiveLocation(file, isArchive());
	}

	private void evaluateMedia(String path, boolean showErrorMessage) {
		File file = null;
		String msg = null;
		if(mediaCheckbox.getSelection())
		{
			if (path == null || path.length() == 0) {
				msg = "Select a directory to export the media to.";
			} else {
				try {
					file = new File(path).getCanonicalFile();
					if (file.isFile())
						msg = "Select a directory, not a file.";
				} catch (IOException e) {
					msg = "Error resolving directory: " + e.getMessage();
				}
			}
			if (msg != null)
			{
				file = null;
			}
		}
		if (showErrorMessage)
			setErrorMessage(msg);
		hasMediaError = msg != null;
		System.err.println("media error: " + hasMediaError);
		System.err.println("non media error: " + hasNonMediaError);
		setPageComplete(!hasMediaError && !hasNonMediaError);
		exporter.setMediaLocation(file);
	}
}