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
package org.eclipse.vtp.modules.standard.ui.properties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.vtp.desktop.editors.core.configuration.DesignElementPropertiesPanel;
import org.eclipse.vtp.desktop.model.core.design.IDesignElement;
import org.eclipse.vtp.desktop.model.core.design.ObjectDefinition;
import org.eclipse.vtp.desktop.model.core.design.ObjectField;
import org.eclipse.vtp.desktop.model.core.design.Variable;
import org.eclipse.vtp.desktop.model.elements.core.internal.PrimitiveElement;
import org.eclipse.vtp.modules.standard.ui.ComparisonInformationProvider;


/**
 * The graphical user interface used to configure the Decision module
 *
 * @author Lonnie Pryor
 */
public class ComparisonPropertiesPanel extends DesignElementPropertiesPanel
{
	static final String lastString = "LastResult";
	/** The InformationProvider for this particular Decision module */
	ComparisonInformationProvider info = null;
	/** The text field used to set name of this particular Decision module */
	Text nameField;
	Combo lType;
	TreeViewer lVarTree;
	Text lExprField;
	Combo cType;
	Combo rType;
	TreeViewer rVarTree;
	Text rExprField;
	Button secureLeftButton = null;
	Button secureRightButton = null;
	List<Object> variables = new ArrayList<Object>();

	/**
	 * Creates a new LanguageSelectionGeneralPropertiesPanel.
	 *
	 *
	 */
	public ComparisonPropertiesPanel(String name, IDesignElement ppe)
	{
		super(name, ppe);
		info = (ComparisonInformationProvider)((PrimitiveElement)ppe).getInformationProvider();
		List<Variable> vars = ppe.getDesign().getVariablesFor(ppe);
		outer:	for(Variable v : vars)
		{
			for(int i = 0; i < this.variables.size(); i++)
			{
				Variable cur = (Variable)this.variables.get(i);
				if(cur.getName().compareToIgnoreCase(v.getName()) > 0)
				{
					this.variables.add(i, v);
					continue outer;
				}
			}
			this.variables.add(v);
		}
		for(int i = 0; i < this.variables.size(); i++)
		{
			Variable cur = (Variable)this.variables.get(i);
			if(cur.getName().compareToIgnoreCase(lastString) > 0)
			{
				this.variables.add(i, lastString);
				break;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.vtp.desktop.ui.app.editor.model.ComponentPropertiesPanel#createControls(org.eclipse.swt.widgets.Composite)
	 */
	public void createControls(Composite parent)
	{
		parent.setLayout(new FillLayout());

		Composite comp = new Composite(parent, SWT.NONE);
		comp.setBackground(parent.getBackground());
		comp.setLayout(new GridLayout(2, false));

		Label nameLabel = new Label(comp, SWT.NONE);
		nameLabel.setText("Name: ");
		nameLabel.setBackground(comp.getBackground());
		nameLabel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));
		nameField = new Text(comp, SWT.SINGLE | SWT.BORDER);
		nameField.setText(getElement().getName());
		nameField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_CENTER));

		Composite builderComp = new Composite(comp, SWT.NONE);
		builderComp.setBackground(comp.getBackground());

		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		builderComp.setLayout(new GridLayout(3, false));
		builderComp.setLayoutData(gd);

		Group lGroup = new Group(builderComp, SWT.TITLE);
		lGroup.setBackground(builderComp.getBackground());
		lGroup.setText("Left-hand Value");
		lGroup.setLayout(new GridLayout(2, false));
		gd = new GridData(GridData.FILL_BOTH);
		lGroup.setLayoutData(gd);

		Label lTypeLabel = new Label(lGroup, SWT.NONE);
		lTypeLabel.setText("Type:");
		lTypeLabel.setBackground(lGroup.getBackground());
		lType = new Combo(lGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
		lType.add("Variable");
		lType.add("Expression");
		lType.select(0);

		final Composite stackComp1 = new Composite(lGroup, SWT.NONE);
		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		stackComp1.setLayoutData(gd);

		final StackLayout sl1 = new StackLayout();
		stackComp1.setLayout(sl1);
		stackComp1.setBackground(lGroup.getBackground());
		lVarTree = new TreeViewer(stackComp1,
				SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.SINGLE);
		lVarTree.setContentProvider(new VariableContentProvider());
		lVarTree.setLabelProvider(new VariableLabelProvider());
		lVarTree.setInput(this);
		sl1.topControl = lVarTree.getControl();

		final Composite lEFComp = new Composite(stackComp1, SWT.NONE);
		lEFComp.setBackground(stackComp1.getBackground());
		lEFComp.setLayout(new GridLayout(1, false));
		lExprField = new Text(lEFComp, SWT.BORDER | SWT.SINGLE);
		lExprField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		secureLeftButton = new Button(lEFComp, SWT.CHECK);
		secureLeftButton.setText("Secure Expression");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		secureLeftButton.setLayoutData(gridData);
		secureLeftButton.setSelection(info.isLeftSecured());
		
		lType.addSelectionListener(new SelectionListener()
			{
				public void widgetSelected(SelectionEvent e)
				{
					if(lType.getSelectionIndex() == 0)
					{
						sl1.topControl = lVarTree.getControl();
					}
					else
					{
						sl1.topControl = lEFComp;
					}

					stackComp1.layout(true, true);
				}

				public void widgetDefaultSelected(SelectionEvent e)
				{
				}
			});

		Group cGroup = new Group(builderComp, SWT.NONE);
		cGroup.setBackground(builderComp.getBackground());
		cGroup.setLayout(new GridLayout(1, false));
		gd = new GridData();
		gd.verticalAlignment = SWT.TOP;
		cGroup.setLayoutData(gd);

		Label cTypeLabel = new Label(cGroup, SWT.NONE);
		cTypeLabel.setText("Comparison");
		cTypeLabel.setBackground(cGroup.getBackground());
		cType = new Combo(cGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
		cType.add("Equals (=)");
		cType.add("Less Than (<)");
		cType.add("Less Than or Equal (<=)");
		cType.add("Greater Than (>)");
		cType.add("Greater Than or Equal (>=)");
		cType.add("Not Equal (!=)");
		cType.select(0);

		Group rGroup = new Group(builderComp, SWT.TITLE);
		rGroup.setBackground(builderComp.getBackground());
		rGroup.setText("Right-hand Value");
		rGroup.setLayout(new GridLayout(2, false));
		gd = new GridData(GridData.FILL_BOTH);
		rGroup.setLayoutData(gd);

		Label rTypeLabel = new Label(rGroup, SWT.NONE);
		rTypeLabel.setText("Type:");
		rTypeLabel.setBackground(rGroup.getBackground());
		rType = new Combo(rGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
		rType.add("Variable");
		rType.add("Expression");
		rType.select(0);

		final Composite stackComp2 = new Composite(rGroup, SWT.NONE);
		stackComp2.setBackground(rGroup.getBackground());
		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		stackComp2.setLayoutData(gd);

		final StackLayout sl2 = new StackLayout();
		stackComp2.setLayout(sl2);
		rVarTree = new TreeViewer(stackComp2,
				SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.SINGLE);
		rVarTree.setContentProvider(new VariableContentProvider());
		rVarTree.setLabelProvider(new VariableLabelProvider());
		rVarTree.setInput(this);
		sl2.topControl = rVarTree.getControl();

		final Composite rEFComp = new Composite(stackComp2, SWT.NONE);
		rEFComp.setBackground(stackComp2.getBackground());
		rEFComp.setLayout(new GridLayout(1, false));
		rExprField = new Text(rEFComp, SWT.BORDER | SWT.SINGLE);
		rExprField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		secureRightButton = new Button(rEFComp, SWT.CHECK);
		secureRightButton.setText("Secure Expression");
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		secureRightButton.setLayoutData(gridData);
		secureRightButton.setSelection(info.isRightSecured());
		
		rType.addSelectionListener(new SelectionListener()
			{
				public void widgetSelected(SelectionEvent e)
				{
					if(rType.getSelectionIndex() == 0)
					{
						sl2.topControl = rVarTree.getControl();
					}
					else
					{
						sl2.topControl = rEFComp;
					}

					stackComp2.layout(true, true);
				}

				public void widgetDefaultSelected(SelectionEvent e)
				{
				}
			});
		lType.select(info.getLType());

		if(info.getLType() == 0)
		{
			//set the tree viewer selection
			if(info.getLValue().startsWith("LastResult"))
			{
				if(info.getLValue().equals("LastResult"))
				{
					lVarTree.setSelection(new StructuredSelection(info.getLValue()));
				}
				else
				{
					lVarTree.setSelection(new StructuredSelection(info.getLValue().substring(11)));
				}
			}
			else
			{
				ObjectDefinition od =
					getObjectDefinitionFromVariables(info.getLValue());
				StructuredSelection ss =
					(od == null) ? StructuredSelection.EMPTY
								 : new StructuredSelection(od);
				lVarTree.setSelection(ss);
			}
		}
		else
		{
			lExprField.setText(info.getLValue());
			sl1.topControl = lEFComp;
			stackComp1.layout(true, true);
		}

		cType.select(info.getCompType());
		rType.select(info.getRType());

		if(info.getRType() == 0)
		{
			//set the tree viewer selection
			if(info.getRValue().startsWith("LastResult"))
			{
				if(info.getRValue().equals("LastResult"))
				{
					rVarTree.setSelection(new StructuredSelection(info.getRValue()));
				}
				else
				{
					rVarTree.setSelection(new StructuredSelection(info.getRValue().substring(11)));
				}
			}
			else
			{
				ObjectDefinition od =
					getObjectDefinitionFromVariables(info.getRValue());
				StructuredSelection ss =
					(od == null) ? StructuredSelection.EMPTY
								 : new StructuredSelection(od);
				rVarTree.setSelection(ss);
			}
		}
		else
		{
			rExprField.setText(info.getRValue());
			sl2.topControl = rEFComp;
			stackComp2.layout(true, true);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.editors.core.elements.PrimitivePropertiesPanel#save()
	 */
	public void save()
	{
		getElement().setName(nameField.getText());
		info.setLType(lType.getSelectionIndex());

		if(info.getLType() == 0)
		{
			ISelection selection = lVarTree.getSelection();

			if((selection != null) && !selection.isEmpty()
					&& selection instanceof IStructuredSelection)
			{
				IStructuredSelection ss = (IStructuredSelection)selection;
				Object selObj = ss.getFirstElement();

				if(selObj instanceof ObjectDefinition)
				{
					ObjectDefinition objDef = (ObjectDefinition)selObj;
					info.setLValue(objDef.getPath());
				}
				else if(selObj instanceof String)
				{
					String selString = (String)selObj;
					if(!selString.equals("LastResult"))
					{
						info.setLValue("LastResult." + selString);
					}
					else
						info.setLValue(selString);
				}
				else
				{
					info.setLValue("");
				}
			}
			else
			{
				info.setLValue("");
			}
		}
		else
		{
			info.setLValue(lExprField.getText());
			info.setLeftSecured(secureLeftButton.getSelection());
		}

		info.setCompType(cType.getSelectionIndex());
		info.setRType(rType.getSelectionIndex());

		if(info.getRType() == 0)
		{
			ISelection selection = rVarTree.getSelection();

			if((selection != null) && !selection.isEmpty()
					&& selection instanceof IStructuredSelection)
			{
				IStructuredSelection ss = (IStructuredSelection)selection;
				Object selObj = ss.getFirstElement();

				if(selObj instanceof ObjectDefinition)
				{
					ObjectDefinition objDef = (ObjectDefinition)selObj;
					info.setRValue(objDef.getPath());
				}
				else if(selObj instanceof String)
				{
					String selString = (String)selObj;
					if(!selString.equals("LastResult"))
					{
						info.setRValue("LastResult." + selString);
					}
					else
						info.setRValue(selString);
				}
				else
				{
					info.setRValue("");
				}
			}
			else
			{
				info.setRValue("");
			}
		}
		else
		{
			info.setRValue(rExprField.getText());
			info.setRightSecured(secureRightButton.getSelection());
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.configuration.ComponentPropertiesPanel#cancel()
	 */
	public void cancel()
	{
		
	}

	/**
	 * @param name
	 * @return
	 */
	public ObjectDefinition getObjectDefinitionFromVariables(String name)
	{
		ObjectDefinition ret = null;
		List<Variable> vars = getElement().getDesign().getVariablesFor(getElement());

		for(int i = 0; i < vars.size(); i++)
		{
			String varName = name;
			boolean sub = false;

			if(name.indexOf(".") != -1)
			{
				varName = name.substring(0, name.indexOf("."));
				sub = true;
			}

			Variable v = vars.get(i);

			if(v.getName().equals(varName))
			{
				if(sub)
				{
					//dig deeper
					ret = getObjectDefinitionFromFields(name.substring(name
								.indexOf(".") + 1), v);
				}
				else
				{
					ret = v;

					break;
				}
			}
		}

		return ret;
	}

	/**
	 * @param name
	 * @param parent
	 * @return
	 */
	public ObjectDefinition getObjectDefinitionFromFields(String name,
		ObjectDefinition parent)
	{
		ObjectDefinition ret = null;
		List<ObjectField> fields = parent.getFields();

		for(int i = 0; i < fields.size(); i++)
		{
			String varName = name;
			boolean sub = false;

			if(name.indexOf('.') != -1)
			{
				varName = name.substring(0, name.indexOf("."));
				sub = true;
			}

			ObjectField of = fields.get(i);

			if(of.getName().equals(varName))
			{
				if(sub)
				{
					ret = getObjectDefinitionFromFields(name.substring(name
								.indexOf(".") + 1), of);
				}
				else
				{
					ret = of;

					break;
				}
			}
		}

		return ret;
	}

	public class VariableContentProvider implements ITreeContentProvider
	{
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
		 */
		public Object[] getChildren(Object parentElement)
		{
			if(parentElement instanceof ObjectDefinition)
			{
				ObjectDefinition v = (ObjectDefinition)parentElement;

				return v.getFields().toArray();
			}
			else if(parentElement instanceof String && ((String)parentElement).equals(lastString))
			{
				return new String [] {"markname", "marktime", "confidence", "inputmode", "interpretation", "utterance"};
			}
			return null;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
		 */
		public Object getParent(Object element)
		{
			if(element instanceof Variable)
			{
				return null;
			}
			else if(element instanceof String)
			{
				if(((String)element).equals(lastString))
					return null;
				return lastString;
			}
			else
			{
				return ((ObjectField)element).getParent();
			}
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
		 */
		public boolean hasChildren(Object element)
		{
			if(element instanceof String)
			{
				if(((String)element).equals(lastString))
					return true;
				return false;
			}
			return ((ObjectDefinition)element).getFields().size() > 0;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
		 */
		public Object[] getElements(Object inputElement)
		{
			return variables.toArray();
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

	public class VariableLabelProvider implements ILabelProvider
	{
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
		 */
		public Image getImage(Object element)
		{
			return null;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
		 */
		public String getText(Object element)
		{
			if(element instanceof String)
				return (String)element;
			return ((ObjectDefinition)element).getName();
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
			return true;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
		 */
		public void removeListener(ILabelProviderListener listener)
		{
		}
	}

	@Override
	public void setConfigurationContext(Map<String, Object> values)
	{
	}

	@Override
	public List<String> getApplicableContexts()
	{
		return Collections.emptyList();
	}

}
