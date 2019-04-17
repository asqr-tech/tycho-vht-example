package org.eclipse.vtp.modules.standard.ui.properties;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.vtp.desktop.core.Activator;
import org.eclipse.vtp.desktop.editors.core.configuration.DesignElementPropertiesPanel;
import org.eclipse.vtp.desktop.model.core.design.IDesignElement;
import org.eclipse.vtp.desktop.model.elements.core.internal.PrimitiveElement;
import org.eclipse.vtp.modules.standard.ui.BranchInformationProvider;

public class BranchPropertiesPanel extends DesignElementPropertiesPanel
{
	BranchInformationProvider info = null;
	/** A list of all Branch objects configured in this Branch module*/
	List<Branch> branches = new ArrayList<Branch>();
	/** A Label used to label the Text field for the name of the Branch module*/
	Label nameLabel = null;
	/** A text field used to display/change the name of this particular Branch module*/
	Text nameField = null;
	/** A UI table of all Branch objects configured in this Branch module*/


	public BranchPropertiesPanel(String name, IDesignElement ppe)
	{
		super(name, ppe);
		info = (BranchInformationProvider)((PrimitiveElement)ppe).getInformationProvider();
		branches = info.getBranches();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.media.core.MediaConfigurationScreen#save()
	 */
	public void save()
	{
		getElement().setName(nameField.getText());
		info.setBranches(branches);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.media.core.MediaConfigurationScreen#cancel()
	 */
	public void cancel()
	{
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.media.core.MediaConfigurationScreen#createControls(org.eclipse.swt.widgets.Composite)
	 */
	public void createControls(Composite parent)
	{
		Composite container = new Composite(parent, SWT.NONE);
		container.setBackground(parent.getBackground());
		container.setLayout(new GridLayout(2, false));
		
		Composite nameComp = new Composite(container, SWT.NONE);
		nameComp.setBackground(container.getBackground());
		nameComp.setLayout(new GridLayout(2, false));
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		nameComp.setLayoutData(gd);
		
		nameLabel = new Label(nameComp, SWT.NONE);
		nameLabel.setText("Name");
		nameLabel.setBackground(nameComp.getBackground());
		nameLabel.setLayoutData(new GridData());
		nameField = new Text(nameComp, SWT.SINGLE | SWT.BORDER);
		nameField.setText(getElement().getName());
		nameField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Composite tableComp = new Composite(container, SWT.NONE);
		tableComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		TableColumnLayout layout = new TableColumnLayout();
		tableComp.setLayout(layout);
		
		final TableViewer branchTableViewer = new TableViewer(tableComp, SWT.FULL_SELECTION | SWT.SINGLE | SWT.V_SCROLL | SWT.BORDER);
		branchTableViewer.getTable().setHeaderVisible(true);
		branchTableViewer.getTable().setLinesVisible(true);
		
		TableViewerColumn secureViewerColumn = new TableViewerColumn(branchTableViewer, SWT.NONE);
		TableColumn secureColumn = secureViewerColumn.getColumn();
		secureColumn.setImage(Activator.getDefault().getImageRegistry().get("ICON_LOCK"));
		layout.setColumnData(secureColumn, new ColumnPixelData(23, false, false));
		
		TableViewerColumn branchNameViewerColumn = new TableViewerColumn(branchTableViewer, SWT.NONE);
		TableColumn branchNameColumn = branchNameViewerColumn.getColumn();
		branchNameColumn.setText("Exit Path Name");
		branchNameColumn.setToolTipText("This will be the name given to a \r\n" +
				"new connector exiting the \r\n" +
				"branch module.");
		layout.setColumnData(branchNameColumn, new ColumnWeightData(2, ColumnWeightData.MINIMUM_WIDTH, true));
		
		TableViewerColumn branchValueViewerColumn = new TableViewerColumn(branchTableViewer, SWT.NONE);
		TableColumn branchValueColumn = branchValueViewerColumn.getColumn();
		branchValueColumn.setText("Expression");
		branchValueColumn.setToolTipText("If this expression evaluates to true, \r\n" +
				"then the branch module will use \r\n" +
				"this exit path.");
		layout.setColumnData(branchValueColumn, new ColumnWeightData(7, ColumnWeightData.MINIMUM_WIDTH, true));
		
		branchTableViewer.setContentProvider(new BranchTableContentProvider());
		branchTableViewer.setLabelProvider(new BranchTableLabelProvider());
		branchTableViewer.setInput(this);
		branchTableViewer.addDoubleClickListener(new IDoubleClickListener(){

			public void doubleClick(DoubleClickEvent event)
			{
				if(!branchTableViewer.getSelection().isEmpty())
				{
					BranchConfigurationDialog bcd = new BranchConfigurationDialog(Display.getCurrent().getActiveShell(), branches);
					Branch br = (Branch)((IStructuredSelection)branchTableViewer.getSelection()).getFirstElement();
					bcd.setValue(br);
					if(bcd.open() == SWT.OK)
					{
						branchTableViewer.refresh();
					}
				}
			}

		});
		
		branchTableViewer.getControl().addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent e)
			{
				if(e.character == SWT.DEL && !branchTableViewer.getSelection().isEmpty())
				{
					Branch br = (Branch)((IStructuredSelection)branchTableViewer.getSelection()).getFirstElement();
					branches.remove(br);
					branchTableViewer.refresh();
				}
			}

			@Override
			public void keyReleased(KeyEvent e)
			{
			}
		});
		
		Composite buttonComp = new Composite(container, SWT.NONE);
		buttonComp.setBackground(container.getBackground());
		buttonComp.setLayout(new GridLayout(1, false));
		
		Button addButton = new Button(buttonComp, SWT.PUSH);
		addButton.setText("Add Branch");
		gd = new GridData();
		gd.horizontalAlignment = SWT.RIGHT;
		addButton.setLayoutData(gd);
		addButton.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e)
			{
			}

			public void widgetSelected(SelectionEvent e)
			{
				BranchConfigurationDialog bcd = new BranchConfigurationDialog(Display.getCurrent().getActiveShell(), branches);
				Branch br = new Branch("", "", false, branches.size());
				bcd.setValue(br);
				if(bcd.open() == SWT.OK)
				{
					branches.add(br);
					branchTableViewer.refresh();
				}
			}

		});

		Button deleteButton = new Button(buttonComp, SWT.PUSH);
		deleteButton.setText("Delete Branch");
		gd = new GridData();
		gd.horizontalAlignment = SWT.RIGHT;
		deleteButton.setLayoutData(gd);
		deleteButton.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e)
			{
			}

			public void widgetSelected(SelectionEvent e)
			{
				if(!branchTableViewer.getSelection().isEmpty())
				{
					Branch br = (Branch)((IStructuredSelection)branchTableViewer.getSelection()).getFirstElement();
					branches.remove(br);
					branchTableViewer.refresh();
				}
			}

		});

		final Button upButton = new Button(buttonComp, SWT.PUSH);
		upButton.setText("Move Up");
		gd = new GridData();
		gd.horizontalAlignment = SWT.RIGHT;
		upButton.setLayoutData(gd);
		upButton.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e)
			{
			}

			public void widgetSelected(SelectionEvent e)
			{
				if(!branchTableViewer.getSelection().isEmpty())
				{
					Branch branch = (Branch)((IStructuredSelection)branchTableViewer.getSelection()).getFirstElement();
					BranchPropertiesPanel.moveBranchUp(branch, branches);
					branchTableViewer.refresh();
				}
			}

		});

		final Button downButton = new Button(buttonComp, SWT.PUSH);
		downButton.setText("Move Down");
		gd = new GridData();
		gd.horizontalAlignment = SWT.RIGHT;
		downButton.setLayoutData(gd);
		downButton.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e)
			{
			}

			public void widgetSelected(SelectionEvent e)
			{
				if(!branchTableViewer.getSelection().isEmpty())
				{
					Branch branch = (Branch)((IStructuredSelection)branchTableViewer.getSelection()).getFirstElement();
					BranchPropertiesPanel.moveBranchDown(branch, branches);
					branchTableViewer.refresh();
				}
			}

		});
		
		branchTableViewer.addSelectionChangedListener(new ISelectionChangedListener()
		{
			@Override
			public void selectionChanged(SelectionChangedEvent event)
			{
				upButton.setEnabled(true);
				downButton.setEnabled(true);
				ISelection selection = event.getSelection();
				if(selection == null || selection.isEmpty())
				{
					upButton.setEnabled(false);
					downButton.setEnabled(false);
				}
				else
				{
					if(branches.indexOf(((IStructuredSelection)selection).getFirstElement()) == 0)
					{
						upButton.setEnabled(false);
					}
					if(branches.indexOf(((IStructuredSelection)selection).getFirstElement()) == branches.size() - 1)
					{
						downButton.setEnabled(false);
					}
				}
			}
			
		});

		upButton.setEnabled(true);
		downButton.setEnabled(true);
		ISelection selection = branchTableViewer.getSelection();
		if(selection == null || selection.isEmpty())
		{
			upButton.setEnabled(false);
			downButton.setEnabled(false);
		}
		else
		{
			if(branches.indexOf(((IStructuredSelection)selection).getFirstElement()) == 0)
			{
				upButton.setEnabled(false);
			}
			if(branches.indexOf(((IStructuredSelection)selection).getFirstElement()) == branches.size() - 1)
			{
				downButton.setEnabled(false);
			}
		}

	}

	@Override
	public void setConfigurationContext(Map<String, Object> values)
	{
	}

	public static void moveBranchUp(Branch branch, List<Branch> branches)
	{
		moveBranch(branch, branches, -1);
	}
	
	public static void moveBranchDown(Branch branch, List<Branch> branches)
	{
		moveBranch(branch, branches, 1);
	}
	
	public static void moveBranch(Branch branch, List<Branch> branches, int distance)
	{
		int sourceIndex = branches.indexOf(branch);
		int destinationIndex = sourceIndex + distance;
		
		if((sourceIndex < branches.size() && sourceIndex >= 0)
				&& (destinationIndex < branches.size() && destinationIndex >= 0))
		{
			branches.set(sourceIndex, branches.set(destinationIndex, branch));
		}
		
	}
	
	public class BranchTableContentProvider implements IStructuredContentProvider
	{

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
		 */
		public Object[] getElements(Object inputElement)
		{
			return branches.toArray();
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
		 */
		public void dispose()
		{
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
		 */
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
		{
		}
	}
	
	public class BranchTableLabelProvider implements ITableLabelProvider
	{

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
		 */
		public Image getColumnImage(Object element, int columnIndex)
		{
			Branch branch = (Branch)element;
			if(columnIndex == 0 && branch.isSecure())
				return Activator.getDefault().getImageRegistry().get("ICON_LOCK");
			return null;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
		 */
		public String getColumnText(Object element, int columnIndex)
		{
			Branch br = (Branch)element;
			if(columnIndex == 0)
				return "";
			if(columnIndex == 1)
				return br.getName();
			return br.getExpression();
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
		 */
		public void addListener(ILabelProviderListener listener)
		{
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
		 */
		public void dispose()
		{
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
		 */
		public boolean isLabelProperty(Object element, String property)
		{
			return false;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
		 */
		public void removeListener(ILabelProviderListener listener)
		{
		}
		
	}
}
