package org.eclipse.vtp.modules.standard.ui.properties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxViewerCellEditor;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.vtp.desktop.editors.core.configuration.DesignElementPropertiesPanel;
import org.eclipse.vtp.desktop.model.core.IDesignDocument;
import org.eclipse.vtp.desktop.model.core.IWorkflowEntry;
import org.eclipse.vtp.desktop.model.core.IWorkflowExit;
import org.eclipse.vtp.desktop.model.core.branding.IBrand;
import org.eclipse.vtp.desktop.model.core.design.IDesignElement;
import org.eclipse.vtp.desktop.model.core.design.Variable;
import org.eclipse.vtp.desktop.model.core.internal.WorkflowTraversalHelper;
import org.eclipse.vtp.desktop.model.core.internal.branding.BrandContext;
import org.eclipse.vtp.desktop.model.elements.core.configuration.ExitBinding;
import org.eclipse.vtp.desktop.model.elements.core.configuration.FragmentConfigurationListener;
import org.eclipse.vtp.desktop.model.elements.core.configuration.FragmentConfigurationManager;
import org.eclipse.vtp.desktop.model.elements.core.configuration.OutputBinding;
import org.eclipse.vtp.desktop.model.elements.core.configuration.OutputBrandBinding;
import org.eclipse.vtp.desktop.model.elements.core.configuration.OutputItem;
import org.eclipse.vtp.desktop.model.elements.core.internal.ApplicationFragmentElement;

public class FragmentOutputMappingPanel extends DesignElementPropertiesPanel implements FragmentConfigurationListener
{
	private static final String NOT_USED = "Not Used";
	private static final String INHERIT = "Inherit From Parent";
	
	private List<IWorkflowExit> returnElements = Collections.emptyList();
	private TableViewer mappingViewer = null;
	private ComboViewer exitCombo = null;
	private List<Variable> vars = null;
	private FragmentConfigurationManager manager = null;
	private IBrand currentBrand = null;
	private ComboBoxViewerCellEditor comboEditor = null;

	public FragmentOutputMappingPanel(String name, IDesignElement element)
	{
		super(name, element);
		manager = (FragmentConfigurationManager)element.getConfigurationManager(FragmentConfigurationManager.TYPE_ID);
		manager.addListener(this);
		List<Variable> inc = element.getDesign().getVariablesFor(element);
		vars = new ArrayList<Variable>();
outer:	for(Variable var : inc)
		{
			if(!(var.getType().isObject() || (var.getType().hasBaseType() && var.getType().isObjectBaseType())))
			{
				for(int i = 0; i < vars.size(); i++)
				{
					if(vars.get(i).getName().compareToIgnoreCase(var.getName()) > 0)
					{
						vars.add(i, var);
						continue outer;
					}
				}
				vars.add(var);
			}
		}
		entryChanged(manager);
	}

	@Override
	public void createControls(Composite parent)
	{
		Composite comp = new Composite(parent, SWT.NONE);
		this.setControl(comp);
		comp.setLayout(new GridLayout(2, false));
		Label exitLabel = new Label(comp, SWT.NONE);
		exitLabel.setText("Exit Point:");
		exitLabel.setLayoutData(new GridData());
		exitCombo = new ComboViewer(comp, SWT.DROP_DOWN | SWT.READ_ONLY | SWT.SINGLE);
		exitCombo.setContentProvider(new ExitContentProvider());
		exitCombo.setLabelProvider(new ExitLabelProvider());
		exitCombo.setInput(this);
		if(returnElements.size() > 0)
			exitCombo.setSelection(new StructuredSelection(returnElements.get(0)));
		exitCombo.getControl().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Table mappingTable = new Table(comp, SWT.SINGLE | SWT.BORDER);
		TableColumn outputColumn = new TableColumn(mappingTable, SWT.NONE);
		outputColumn.setText("Workflow Variable");
		outputColumn.setWidth(150);
		TableColumn targetColumn = new TableColumn(mappingTable, SWT.NONE);
		targetColumn.setText("Put into the variable");
		targetColumn.setWidth(200);
		mappingTable.setHeaderVisible(true);
		mappingViewer = new TableViewer(mappingTable);
		mappingViewer.setColumnProperties(new String[] {"Field", "Value"});
		mappingViewer.setCellModifier(new MappingCellModifier());
		comboEditor = new ComboBoxViewerCellEditor(mappingTable);
		comboEditor.setContentProvider(new VariableContentProvider());
		comboEditor.setLabelProvider(new VariableLabelProvider());
		comboEditor.setInput(this);
		mappingViewer.setCellEditors(new CellEditor[] {null, comboEditor});
		mappingViewer.setContentProvider(new MappingContentProvider());
		mappingViewer.setLabelProvider(new MappingLabelProvider());
		mappingViewer.setInput(this);
		GridData gd = new GridData(GridData.FILL_BOTH); 
		gd.horizontalSpan = 2;
		mappingViewer.getTable().setLayoutData(gd);
		exitCombo.addSelectionChangedListener(new ISelectionChangedListener()
		{
			public void selectionChanged(SelectionChangedEvent e)
            {
				mappingViewer.refresh();
            }
		});
	}

	@Override
	public void cancel()
	{
		getElement().rollbackConfigurationChanges(manager);
	}

	@Override
	public void save()
	{
		getElement().commitConfigurationChanges(manager);
	}

	public void setConfigurationContext(Map<String, Object> values)
	{
		Object obj = values.get(BrandContext.CONTEXT_ID);
		if(obj != null)
		{
			currentBrand = (IBrand)obj;
			comboEditor.getViewer().refresh();
			mappingViewer.refresh();
		}
	}
	
	public class ExitContentProvider implements IStructuredContentProvider
	{
		public Object[] getElements(Object inputElement)
		{
			return returnElements.toArray();
		}

		public void dispose()
		{
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
		{
		}
	}
	
	public class ExitLabelProvider extends BaseLabelProvider implements ILabelProvider
	{
		public Image getImage(Object element)
		{
			return null;
		}

		public String getText(Object element)
		{
			return ((IWorkflowExit)element).getName();
		}
	}

	public class MappingContentProvider implements IStructuredContentProvider
	{
		public Object[] getElements(Object inputElement)
		{
			if(returnElements.isEmpty() || exitCombo.getSelection().isEmpty())
				return new Object[0];
			IWorkflowExit retElement = (IWorkflowExit)((IStructuredSelection)exitCombo.getSelection()).getFirstElement();
			ExitBinding exitBinding = manager.getExitBinding(retElement.getName());
			List<OutputBinding> outputBindings = exitBinding.getOutputBindings();
			return outputBindings.toArray();
		}

		public void dispose()
		{
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
		{
		}
	}

	public class MappingLabelProvider implements ITableLabelProvider
	{
		public Image getColumnImage(Object element, int columnIndex)
		{
			return null;
		}

		public String getColumnText(Object element, int columnIndex)
		{
			OutputBinding outputBinding = (OutputBinding)element;
			if(columnIndex == 0)
			{
				return outputBinding.getOutput();
			}
			else if(columnIndex == 1)
			{
				if(currentBrand == null)
					return "Unknown";
				OutputBrandBinding brandBinding = outputBinding.getBrandBinding(currentBrand);
				if(brandBinding.isInherited() && brandBinding.getBrand().getParent() != null)
					return INHERIT;
				else if(brandBinding.getValue() == null || brandBinding.getValue().getValue() == null)
					return NOT_USED;
				return brandBinding.getValue().getValue();
			}
			return "Unknown";
		}

		public void addListener(ILabelProviderListener listener)
        {
        }

		public void dispose()
        {
        }

		public boolean isLabelProperty(Object element, String property)
        {
	        return false;
        }

		public void removeListener(ILabelProviderListener listener)
        {
        }
	}
	
	public class VariableContentProvider implements IStructuredContentProvider
	{
		public Object[] getElements(Object inputElement)
		{
			if(currentBrand == null)
				return new Object[0];
			if(currentBrand.getParent() == null) //default brand
			{
				Object[] ret = new Object[vars.size() + 1];
				ret[0] = NOT_USED;
				for(int i = 0; i < vars.size(); i++)
				{
					ret[i + 1] = vars.get(i);
				}
				return ret;
			}
			Object[] ret = new Object[vars.size() + 2];
			ret[0] = INHERIT;
			ret[1] = NOT_USED;
			for(int i = 0; i < vars.size(); i++)
			{
				ret[i + 2] = vars.get(i);
			}
			return ret;
		}

		public void dispose()
		{
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
		{
		}
	}
	
	public class VariableLabelProvider extends BaseLabelProvider implements ILabelProvider
	{
		public Image getImage(Object element)
		{
			return null;
		}

		public String getText(Object element)
		{
			if(element instanceof Variable)
				return ((Variable)element).getName();
			return element.toString();
		}
	}
	
	public class MappingCellModifier implements ICellModifier
	{

		public boolean canModify(Object element, String property)
        {
	        return true;
        }

		public Object getValue(Object element, String property)
        {
			OutputBinding outputBinding = (OutputBinding)element;
			OutputBrandBinding brandBinding = outputBinding.getBrandBinding(currentBrand);
			if(brandBinding.isInherited())
				if(brandBinding.getBrand().getParent() != null)
					return INHERIT;
				else
					return NOT_USED;
			if(brandBinding.getValue().getValue() == null)
				return NOT_USED;
			for(int i = 0; i < vars.size(); i++)
			{
				if(vars.get(i).getName().equals(brandBinding.getValue().getValue()))
					return vars.get(i);
			}
			return 0;
        }

		public void modify(Object element, String property, Object value)
        {
			OutputBinding outputBinding = (OutputBinding)((TableItem)element).getData();
			OutputBrandBinding brandBinding = outputBinding.getBrandBinding(currentBrand);
			OutputItem outputItem = brandBinding.getValue();
			if(outputItem == null)
				outputItem = new OutputItem(null);
			if(value instanceof String)
			{
				if(value.equals(NOT_USED))
				{
					outputItem.setValue(null);
				}
				else if(value.equals(INHERIT))
				{
					outputItem = null;
				}
			}
			else
				outputItem.setValue(((Variable)value).getName());
			brandBinding.setValue(outputItem);
			mappingViewer.refresh();
        }
		
	}

	public void entryChanged(FragmentConfigurationManager manager)
	{
		ApplicationFragmentElement applicationFragmentElement = (ApplicationFragmentElement)getElement();
		if(applicationFragmentElement.isModelPresent())
		{
			String entryId = manager.getEntryId();
			if(entryId != null && !entryId.equals(""))
			{
				IWorkflowEntry entry = applicationFragmentElement.getReferencedModel().getWorkflowEntry(entryId);
				if(entry != null)
				{
					List<IDesignDocument> workingCopies = new ArrayList<IDesignDocument>();
					WorkflowTraversalHelper wth = new WorkflowTraversalHelper(applicationFragmentElement.getReferencedModel(), workingCopies);
					returnElements = wth.getDownStreamWorkflowExits(entry);
				}
			}
		}
		List<ExitBinding> oldBindings = new LinkedList<ExitBinding>(manager.getExitBindings());
		for(IWorkflowExit exit : returnElements)
		{
			ExitBinding binding = manager.addExitBinding(exit.getName());
			List<OutputBinding> oldOutputBindings = binding.getOutputBindings();
			List<Variable> evs = exit.getExportedVariables();
			for(Variable v : evs)
			{
				binding.addOutputBinding(v.getName());
			}
outputouter:for(OutputBinding outputBinding : oldOutputBindings)
			{
				for(Variable v : evs)
				{
					if(v.getName().equals(outputBinding.getOutput()))
						continue outputouter;
				}
				binding.removeOutputBinding(outputBinding);
			}
		}
outer:	for(ExitBinding binding : oldBindings)
		{
			for(IWorkflowExit exit : returnElements)
			{
				if(exit.getName().equals(binding.getName()))
					continue outer;
			}
			manager.removeExitBinding(binding.getName());
		}
		if(exitCombo != null)
		{
			exitCombo.refresh();
			exitCombo.setSelection(returnElements.isEmpty() ? null : new StructuredSelection(returnElements.get(0)));
//			mappingViewer.refresh();
		}
	}
}
