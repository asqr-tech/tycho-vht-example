package org.eclipse.vtp.modules.database.ui.export;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.vtp.desktop.export.IDefinitionBuilder;
import org.eclipse.vtp.desktop.export.IExportAgent;
import org.eclipse.vtp.desktop.export.IMediaExporter;
import org.eclipse.vtp.desktop.export.IWorkflowExporter;
import org.eclipse.vtp.framework.databases.configurations.DatabaseColumnConfiguration;
import org.eclipse.vtp.framework.databases.configurations.DatabaseConfiguration;
import org.eclipse.vtp.framework.databases.configurations.DatabaseTableConfiguration;
import org.eclipse.vtp.framework.databases.configurations.JdbcDatabaseConfiguration;
import org.eclipse.vtp.framework.databases.configurations.JndiDatabaseConfiguration;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class DatabaseExportAgent implements IExportAgent {

	private static final Comparator<IWorkflowExporter> WORKFLOW_COMPARE = new Comparator<IWorkflowExporter>() {
		public int compare(IWorkflowExporter left, IWorkflowExporter right) {
			return left.getProject().getFullPath().toString().compareTo(right.getProject().getFullPath().toString());
		}
	};

	private static final Comparator<IFolder> RESOURCE_COMPARE = new Comparator<IFolder>() {
		public int compare(IFolder left, IFolder right) {
			return left.getFullPath().toString().compareTo(right.getFullPath().toString());
		}
	};

	/** The settings structure. */
	private final Map<IWorkflowExporter, Map<IFolder, Map<String, String>>> settings = new TreeMap<IWorkflowExporter, Map<IFolder, Map<String, String>>>(
			WORKFLOW_COMPARE);

	private ConfigureDatabasesPage page = new ConfigureDatabasesPage();

	@Override
	public Collection<IWizardPage> init() {
		return Collections.singleton((IWizardPage) page);
	}

	@Override
	public void setProjects(Collection<? extends IWorkflowExporter> workflowProjects,
			Collection<? extends IMediaExporter> mediaProjects) {
		settings.clear();
		for (IWorkflowExporter workflow : workflowProjects) {
			try {
				IProject project = workflow.getProject();
				IFolder databases = project.getFolder("Databases"); //$NON-NLS-1$
				if (databases == null || !databases.exists())
					continue;
				IResource[] children = null;
				try {
					children = databases.members();
				} catch (CoreException e) {
					e.printStackTrace();
				}
				if (children == null || children.length == 0)
					continue;
				Map<IFolder, Map<String, String>> folders = new TreeMap<IFolder, Map<String, String>>(RESOURCE_COMPARE);
				for (int j = 0; j < children.length; ++j)
					if (IResource.FOLDER == children[j].getType())
						folders.put((IFolder) children[j], null);
				if (!folders.isEmpty())
					settings.put(workflow, folders);
			} catch (RuntimeException e) {
				e.printStackTrace();
			}

		}
		page.projectSelectionChanged();
	}

	@Override
	public boolean shouldBeShown(IWizardPage page) {
		return settings.size() > 0;
	}

	@Override
	public boolean canFinish() {
		return true;
	}

	@Override
	public void configureServices(IWorkflowExporter exporter, Element servicesElement) {
		page.createServiceConfigurations(exporter, servicesElement);
		page.savePrefrences();
	}

	public class ConfigureDatabasesPage extends WizardPage implements ISelectionChangedListener, ModifyListener,
			SelectionListener, ITreeContentProvider, ILabelProvider {

		/** The settings structure. */
		private Map<String, String> selectedDatabase = null;
		/** The application/database viewer. */
		private TreeViewer viewer = null;
		private Combo typeCombo = null;
		private Composite stackComposite = null;
		private StackLayout stackLayout = null;
		private Composite jndiComposite = null;
		private Text jndiUriText = null;
		private Text jndiUsrText = null;
		private Text jndiPwdText = null;
		private Composite jdbcComposite = null;
		private Text jdbcDvrText = null;
		private Text jdbcUrlText = null;
		private Text jdbcUsrText = null;
		private Text jdbcPwdText = null;
		private Composite emptyComposite = null;

		/**
		 * Creates a new ConfigureDatabasesPage.
		 * 
		 * @param exporter The exporter to use.
		 */
		public ConfigureDatabasesPage() {
			super("ConfigureDatabasesPage", //$NON-NLS-1$
					"Configure the Exported Database Connections", null);
		}

		/**
		 * loadDefaults.
		 * 
		 * @param projectName
		 * @param folderName
		 * @param properties
		 */
		private void loadDefaults(IWorkflowExporter exporter, String folderName, Map<String, String> properties) {
			String prefix = "db." + folderName + ".";
			String type = exporter.getSetting(prefix + "type");
			if ("jdbc".equalsIgnoreCase(type))
				type = "jdbc";
			else
				type = "jndi";
			properties.put("type", type);
			properties.put("dvr", exporter.getSetting(prefix + "dvr", ""));
			properties.put("url", exporter.getSetting(prefix + "url", ""));
			String uri = exporter.getSetting(prefix + "uri", "");
			if (uri.length() == 0)
				uri = "java:comp/env/jdbc/" + folderName;
			properties.put("uri", uri);
			properties.put("usr", exporter.getSetting(prefix + "usr", ""));
			properties.put("pwd", exporter.getSetting(prefix + "pwd", ""));
		}

		/**
		 * resetStack.
		 */
		private void resetStack() {
			String type = selectedDatabase.get("type");
			if ("jndi".equalsIgnoreCase(type)) {
				typeCombo.select(0);
				stackLayout.topControl = jndiComposite;
				stackComposite.layout();
				jndiUriText.setText(selectedDatabase.get("uri"));
				jndiUsrText.setText(selectedDatabase.get("usr"));
				jndiPwdText.setText(selectedDatabase.get("pwd"));
			} else if ("jdbc".equalsIgnoreCase(type)) {
				typeCombo.select(1);
				stackLayout.topControl = jdbcComposite;
				stackComposite.layout();
				jdbcDvrText.setText(selectedDatabase.get("dvr"));
				jdbcUrlText.setText(selectedDatabase.get("url"));
				jdbcUsrText.setText(selectedDatabase.get("usr"));
				jdbcPwdText.setText(selectedDatabase.get("pwd"));
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(
		 * org.eclipse.swt.widgets.Composite)
		 */
		public void createControl(Composite parent) {
			SashForm root = new SashForm(parent, SWT.HORIZONTAL);
			// Database selector.
			viewer = new TreeViewer(root, SWT.BORDER);
			viewer.setContentProvider(this);
			viewer.setLabelProvider(this);
			viewer.setComparator(new ViewerComparator());
			viewer.setInput(settings);
			viewer.expandAll();
			viewer.addSelectionChangedListener(this);
			// Database configuration form.
			Composite composite = new Composite(root, SWT.NONE);
			composite.setLayout(new GridLayout(2, false));
			// Lookup type selection.
			Label typeLabel = new Label(composite, SWT.NONE);
			typeLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
			typeLabel.setText("Database lookup method:");
			typeCombo = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
			typeCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
			typeCombo.add("JNDI Lookup");
			typeCombo.add("JDBC Driver");
			typeCombo.addSelectionListener(this);
			new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL)
					.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
			// Configuration form stack.
			stackComposite = new Composite(composite, SWT.NONE);
			stackComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
			stackLayout = new StackLayout();
			stackComposite.setLayout(stackLayout);
			// JNDI Lookup form.
			jndiComposite = new Composite(stackComposite, SWT.NONE);
			jndiComposite.setLayout(new GridLayout(2, false));
			Label jndiUriLabel = new Label(jndiComposite, SWT.NONE);
			jndiUriLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
			jndiUriLabel.setText("JNDI URI:");
			jndiUriText = new Text(jndiComposite, SWT.BORDER);
			jndiUriText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
			jndiUriText.addModifyListener(this);
			Label jndiUsrLabel = new Label(jndiComposite, SWT.NONE);
			jndiUsrLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
			jndiUsrLabel.setText("User Name:");
			jndiUsrText = new Text(jndiComposite, SWT.BORDER);
			jndiUsrText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
			jndiUsrText.addModifyListener(this);
			Label jndiPwdLabel = new Label(jndiComposite, SWT.NONE);
			jndiPwdLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
			jndiPwdLabel.setText("Password:");
			jndiPwdText = new Text(jndiComposite, SWT.BORDER);
			jndiPwdText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
			jndiPwdText.addModifyListener(this);
			// JDBC Driver form.
			jdbcComposite = new Composite(stackComposite, SWT.NONE);
			jdbcComposite.setLayout(new GridLayout(2, false));
			Label jdbcDvrLabel = new Label(jdbcComposite, SWT.NONE);
			jdbcDvrLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
			jdbcDvrLabel.setText("JDBC Driver:");
			jdbcDvrText = new Text(jdbcComposite, SWT.BORDER);
			jdbcDvrText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
			jdbcDvrText.addModifyListener(this);
			Label jdbcUrlLabel = new Label(jdbcComposite, SWT.NONE);
			jdbcUrlLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
			jdbcUrlLabel.setText("JDBC URL:");
			jdbcUrlText = new Text(jdbcComposite, SWT.BORDER);
			jdbcUrlText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
			jdbcUrlText.addModifyListener(this);
			Label jdbcUsrLabel = new Label(jdbcComposite, SWT.NONE);
			jdbcUsrLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
			jdbcUsrLabel.setText("User Name:");
			jdbcUsrText = new Text(jdbcComposite, SWT.BORDER);
			jdbcUsrText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
			jdbcUsrText.addModifyListener(this);
			Label jdbcPwdLabel = new Label(jdbcComposite, SWT.NONE);
			jdbcPwdLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
			jdbcPwdLabel.setText("Password:");
			jdbcPwdText = new Text(jdbcComposite, SWT.BORDER);
			jdbcPwdText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
			jdbcPwdText.addModifyListener(this);
			// Empty form.
			emptyComposite = new Composite(stackComposite, SWT.NONE);
			stackLayout.topControl = emptyComposite;
			// Done.
			root.setWeights(new int[] { 40, 60 });
			setControl(root);
			projectSelectionChanged();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.desktop.projects.core.export.ExportWebappPage#
		 * projectSelectionChanged(
		 * org.eclipse.vtp.desktop.projects.core.export.Exporter)
		 */
		public void projectSelectionChanged() {
			if (viewer == null)
				return;
			viewer.refresh();
			viewer.expandAll();
			if (viewer.getSelection().isEmpty() && !settings.isEmpty()) {
				Map<IFolder, Map<String, String>> folders = settings.values().iterator().next();
				if (!folders.isEmpty())
					viewer.setSelection(new StructuredSelection(folders.keySet().iterator().next()));
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.desktop.projects.core.export.ExportWebappPage#
		 * savePrefrences()
		 */
		public void savePrefrences() {
			for (Iterator<IWorkflowExporter> i = settings.keySet().iterator(); i.hasNext();) {
				IWorkflowExporter project = i.next();
				Map<IFolder, Map<String, String>> folders = settings.get(project);
				for (Iterator<IFolder> j = folders.keySet().iterator(); j.hasNext();) {
					IFolder folder = j.next();
					Map<String, String> config = folders.get(folder);
					if (config == null)
						continue;
					String prefix = "db." + folder.getName() + ".";
					for (Map.Entry<String, String> entry : config.entrySet())
						project.putSetting(prefix + entry.getKey(), entry.getValue());
				}
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.desktop.projects.core.export.ExportWebappPage#
		 * createServiceConfigurations(java.lang.String, org.w3c.dom.Element)
		 */
		public void createServiceConfigurations(IWorkflowExporter exporter, Element servicesElement) {
			Map<IFolder, Map<String, String>> folders = settings.get(exporter);
			if (folders == null)
				return;
			Element serviceElement = servicesElement.getOwnerDocument()
					.createElementNS(IDefinitionBuilder.NAMESPACE_URI_PROCESS_DEFINITION, "process:service"); //$NON-NLS-1$
			serviceElement.setAttribute("id", //$NON-NLS-1$
					"org.eclipse.vtp.framework.databases.services.database-registry"); //$NON-NLS-1$
			for (Map.Entry<IFolder, Map<String, String>> folderEntry : folders.entrySet()) {
				IFolder folder = folderEntry.getKey();
				Map<String, String> properties = folderEntry.getValue();
				if (properties == null)
					loadDefaults(exporter, folder.getName(), properties = new HashMap<String, String>());
				DatabaseConfiguration database = null;
				Element databaseElement = null;
				if ("jdbc".equals(properties.get("type"))) {
					JdbcDatabaseConfiguration jdbc = new JdbcDatabaseConfiguration();
					jdbc.setDriver(properties.get("dvr"));
					jdbc.setUrl(properties.get("url"));
					database = jdbc;
					databaseElement = servicesElement.getOwnerDocument()
							.createElementNS(IDefinitionBuilder.NAMESPACE_URI_DATABASES, "database:jdbc-database"); //$NON-NLS-1$
				} else {
					JndiDatabaseConfiguration jndi = new JndiDatabaseConfiguration();
					jndi.setUri(properties.get("uri"));
					database = jndi;
					databaseElement = servicesElement.getOwnerDocument()
							.createElementNS(IDefinitionBuilder.NAMESPACE_URI_DATABASES, "database:jndi-database"); //$NON-NLS-1$
				}
				database.setName(folder.getName());
				String usr = properties.get("usr");
				if (usr.length() > 0)
					database.setUsername(usr);
				String pwd = properties.get("pwd");
				if (pwd.length() > 0)
					database.setPassword(pwd);
				try {
					DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
					factory.setValidating(false);
					factory.setNamespaceAware(true);
					DocumentBuilder builder = factory.newDocumentBuilder();
					IResource[] files = folder.members();
					for (int k = 0; k < files.length; ++k) {
						if (IResource.FILE != files[k].getType()
								|| !((IFile) files[k]).getFileExtension().equals(".dot"))
							continue;
						Element root = builder.parse(((IFile) files[k]).getLocation().toFile()).getDocumentElement();
						DatabaseTableConfiguration table = new DatabaseTableConfiguration();
						table.setName(root.getAttribute("name"));
						NodeList list = ((Element) root.getElementsByTagName("columns").item(0))
								.getElementsByTagName("column");
						for (int m = 0; m < list.getLength(); ++m) {
							Element element = (Element) list.item(m);
							DatabaseColumnConfiguration column = new DatabaseColumnConfiguration();
							column.setName(element.getAttribute("name"));
							String type = ((Element) element.getElementsByTagName("column-type").item(0))
									.getAttribute("type");
							if ("Varchar".equalsIgnoreCase(type))
								column.setType(DatabaseColumnConfiguration.TYPE_VARCHAR);
							else if ("Number".equalsIgnoreCase(type))
								column.setType(DatabaseColumnConfiguration.TYPE_NUMBER);
							else if ("Big Number".equalsIgnoreCase(type))
								column.setType(DatabaseColumnConfiguration.TYPE_BIG_NUMBER);
							else if ("Decimal".equalsIgnoreCase(type))
								column.setType(DatabaseColumnConfiguration.TYPE_DECIMAL);
							else if ("Big Decimal".equalsIgnoreCase(type))
								column.setType(DatabaseColumnConfiguration.TYPE_BIG_DECIMAL);
							else if ("Boolean".equalsIgnoreCase(type))
								column.setType(DatabaseColumnConfiguration.TYPE_BOOLEAN);
							else if ("DateTime".equalsIgnoreCase(type))
								column.setType(DatabaseColumnConfiguration.TYPE_DATETIME);
							else
								column.setType(DatabaseColumnConfiguration.TYPE_TEXT);
							table.addColumn(column);
						}
						database.addTable(table);
					}
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
				database.save(databaseElement);
				serviceElement.appendChild(databaseElement);
			}
			servicesElement.appendChild(serviceElement);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(
		 * org.eclipse.jface.viewers.SelectionChangedEvent)
		 */
		public void selectionChanged(SelectionChangedEvent event) {
			Object value = null;
			IStructuredSelection selection = (IStructuredSelection) event.getSelection();
			if (!selection.isEmpty())
				value = selection.getFirstElement();
			if (value instanceof IProject)
				value = null;
			if (value == null) {
				selectedDatabase = null;
				typeCombo.setEnabled(false);
				stackLayout.topControl = emptyComposite;
				stackComposite.layout();
				return;
			}
			IFolder folder = (IFolder) value;
			IWorkflowExporter exporter = getExporter(folder);
			Map<IFolder, Map<String, String>> folders = settings.get(exporter);
			selectedDatabase = folders.get(folder);
			if (selectedDatabase == null) {
				folders.put(folder, selectedDatabase = new HashMap<String, String>());
				loadDefaults(exporter, folder.getName(), selectedDatabase);
			}
			typeCombo.setEnabled(true);
			resetStack();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.swt.events.ModifyListener#modifyText(
		 * org.eclipse.swt.events.ModifyEvent)
		 */
		public void modifyText(ModifyEvent e) {
			Object source = e.getSource();
			if (jndiUriText == source)
				selectedDatabase.put("uri", jndiUriText.getText());
			else if (jndiUsrText == source)
				selectedDatabase.put("usr", jndiUsrText.getText());
			else if (jndiPwdText == source)
				selectedDatabase.put("pwd", jndiPwdText.getText());
			else if (jdbcDvrText == source)
				selectedDatabase.put("dvr", jdbcDvrText.getText());
			else if (jdbcUrlText == source)
				selectedDatabase.put("url", jdbcUrlText.getText());
			else if (jdbcUsrText == source)
				selectedDatabase.put("usr", jdbcUsrText.getText());
			else if (jdbcPwdText == source)
				selectedDatabase.put("pwd", jdbcPwdText.getText());
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(
		 * org.eclipse.swt.events.SelectionEvent)
		 */
		public void widgetSelected(SelectionEvent e) {
			Object source = e.getSource();
			if (typeCombo == source) {
				switch (typeCombo.getSelectionIndex()) {
				case 0:
					selectedDatabase.put("type", "jndi");
					break;
				case 1:
					selectedDatabase.put("type", "jdbc");
					break;
				}
				resetStack();
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(
		 * org.eclipse.swt.events.SelectionEvent)
		 */
		public void widgetDefaultSelected(SelectionEvent e) {
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(
		 * org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
		 */
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(
		 * java.lang.Object)
		 */
		public Object[] getElements(Object inputElement) {
			return settings.keySet().toArray();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(
		 * java.lang.Object)
		 */
		public Object getParent(Object element) {
			if (element instanceof IWorkflowExporter)
				return null;
			else if (element instanceof IResource) {
				IWorkflowExporter exporter = getExporter((IResource) element);
				if (exporter != null)
					return exporter;
			}
			return null;
		}

		private IWorkflowExporter getExporter(IResource resource) {
			IProject project = resource.getProject();
			for (IWorkflowExporter exporter : settings.keySet()) {
				if (exporter.getProject().getName().equals(project.getName()))
					return exporter;
			}
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(
		 * java.lang.Object)
		 */
		public boolean hasChildren(Object element) {
			return element instanceof IWorkflowExporter && settings.containsKey(element);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(
		 * java.lang.Object)
		 */
		public Object[] getChildren(Object parentElement) {
			Map<IFolder, Map<String, String>> folders = settings.get(parentElement);
			if (folders == null)
				return new Object[0];
			return folders.keySet().toArray();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(
		 * org.eclipse.jface.viewers.ILabelProviderListener)
		 */
		public void addListener(ILabelProviderListener listener) {
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(
		 * org.eclipse.jface.viewers.ILabelProviderListener)
		 */
		public void removeListener(ILabelProviderListener listener) {
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(
		 * java.lang.Object, java.lang.String)
		 */
		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
		 */
		public String getText(Object element) {
			if (element instanceof IWorkflowExporter)
				return ((IWorkflowExporter) element).getProject().getName();
			else if (element instanceof IResource)
				return ((IResource) element).getName();
			return element.toString();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
		 */
		public Image getImage(Object element) {
			return null;
		}
	}

}
