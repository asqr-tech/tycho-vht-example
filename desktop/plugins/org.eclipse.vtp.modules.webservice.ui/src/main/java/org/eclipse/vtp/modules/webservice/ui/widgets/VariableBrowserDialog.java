package org.eclipse.vtp.modules.webservice.ui.widgets;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.vtp.desktop.model.core.FieldType.Primitive;
import org.eclipse.vtp.desktop.model.core.design.ObjectDefinition;
import org.eclipse.vtp.desktop.model.core.design.ObjectField;
import org.eclipse.vtp.desktop.model.core.design.Variable;

public class VariableBrowserDialog extends Dialog
{
	static
	{
		ColorRegistry cr = JFaceResources.getColorRegistry();
		cr.put("VTP_BLACK", new RGB(0, 0, 0));
		cr.put("VTP_WHITE", new RGB(255, 255, 255));
		cr.put("VTP_GRAYED", new RGB(140, 140, 140));
	};
	
	private Text searchText = null;
	private TreeViewer variableViewer = null;
	private ObjectDefinitionFilter openFilter = new ObjectDefinitionFilter()
	{
		public boolean isApplicable(ObjectDefinition definition)
		{
			return true;
		}
	};
	private ObjectDefinitionFilter filter = openFilter;
	private List<Variable> scope = null;
	private ObjectDefinition selection = null;

	public VariableBrowserDialog(Shell parentShell, List<Variable> scope)
	{
		super(parentShell);
		this.scope = scope;
	}
	
	public void setFilter(ObjectDefinitionFilter filter)
	{
		if(filter == null)
		{
			this.filter = openFilter;
		}
		else
		{
			this.filter = filter;
		}
		if(variableViewer != null)
		{
			variableViewer.refresh();
		}
	}
	
	public ObjectDefinition getSelectedDefinition()
	{
		return selection;
	}

	@Override
	protected Control createDialogArea(Composite parent)
	{
		Composite dialogComp = (Composite)super.createDialogArea(parent);
		GridData dialogData = (GridData)dialogComp.getLayoutData();
		dialogData.widthHint = 400;
		dialogData.heightHint = 300;
		searchText = new Text(dialogComp, SWT.SINGLE | SWT.SEARCH);
		searchText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Composite layoutComp = new Composite(dialogComp, SWT.NONE);
		layoutComp.setLayoutData(new GridData(GridData.FILL_BOTH));
		TreeColumnLayout columnLayout = new TreeColumnLayout();
		layoutComp.setLayout(columnLayout);
		Tree variableTree = new Tree(layoutComp, SWT.BORDER | SWT.FULL_SELECTION | SWT.SINGLE | SWT.V_SCROLL);
		variableTree.setHeaderVisible(true);
		variableTree.setLinesVisible(true);
		TreeColumn nameColumn = new TreeColumn(variableTree, SWT.NONE);
		nameColumn.setText("Name");
		columnLayout.setColumnData(nameColumn, new ColumnWeightData(65, 200));
		TreeColumn typeColumn = new TreeColumn(variableTree, SWT.NONE);
		typeColumn.setText("Type");
		columnLayout.setColumnData(typeColumn, new ColumnWeightData(35, 75));
		variableViewer = new TreeViewer(variableTree);
		variableViewer.setComparator(new ViewerComparator());
		variableViewer.setContentProvider(new VariableContentProvider());
		variableViewer.setLabelProvider(new VariableLabelProvider());
		variableViewer.setInput(this);
		variableViewer.addSelectionChangedListener(new ISelectionChangedListener()
		{
			public void selectionChanged(SelectionChangedEvent event)
			{
				Object sel = ((IStructuredSelection)event.getSelection()).getFirstElement();
				if(sel == null)
				{
					selection = null;
				}
				else
				{
					if(filter.isApplicable((ObjectDefinition)sel))
					{
						selection = (ObjectDefinition)sel;
					}
					else
						selection = null;
				}
			}
		});
		searchText.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{
				variableViewer.refresh();
			}
		});
		variableViewer.addFilter(new ViewerFilter()
		{
			public boolean select(Viewer viewer, Object parentElement, Object element)
			{
				String crit = searchText.getText();
				if(crit.length() == 0)
					return true;
				ObjectDefinition od = (ObjectDefinition)element;
				List<String> paths = new LinkedList<String>();
				paths.add(od.getPath());
				addFields(paths, od.getFields());
				for(String path : paths)
				{
					if(path.toLowerCase().contains(crit.toLowerCase()))
						return true;
				}
				return false;
			}
			
			private void addFields(List<String> paths, List<ObjectField> fields)
			{
				for(ObjectField field : fields)
				{
					paths.add(field.getPath());
					addFields(paths, field.getFields());
				}
			}
		});
		
		return dialogComp;
	}
	
	public class VariableContentProvider implements ITreeContentProvider
	{
		public Object[] getElements(Object inputElement)
		{
			return scope.toArray();
		}
		
		public Object getParent(Object element)
		{
			if(element instanceof ObjectField)
				return ((ObjectField)element).getParent();
			return null;
		}

		public boolean hasChildren(Object element)
		{
			return ((ObjectDefinition)element).getFields().size() > 0;
		}

		public Object[] getChildren(Object parentElement)
		{
			return ((ObjectDefinition)parentElement).getFields().toArray();
		}
		
		public void dispose()
		{
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
		{
		}
	}
	
	public class VariableLabelProvider extends BaseLabelProvider implements ITableLabelProvider, ITableColorProvider
	{
		public Image getColumnImage(Object element, int index)
		{
			return null;
		}
		
		public String getColumnText(Object element, int index)
		{
			ObjectDefinition od = (ObjectDefinition)element;
			if(index == 0)
			{
				return od.getName();
			}
			else
			{
				if(od.getType().isObject())
					return od.getType().getName();
				else
					if(od.getType().getPrimitiveType() == Primitive.ARRAY)
						return od.getType().getBaseTypeName() + "[]";
						
				return od.getType().getName();
			}
		}

		public Color getForeground(Object element, int index)
		{
			ColorRegistry cr = JFaceResources.getColorRegistry();
			ObjectDefinition od = (ObjectDefinition)element;
			if(filter.isApplicable(od))
				return cr.get("VTP_BLACK");
			return cr.get("VTP_GRAYED");
		}

		public Color getBackground(Object element, int index)
		{
			return null;
		}
	}
}
