package org.eclipse.vtp.modules.standard.ui.properties;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.vtp.desktop.core.dialogs.FramedDialog;
import org.eclipse.vtp.desktop.model.core.FieldType;
import org.eclipse.vtp.desktop.model.core.FieldType.Primitive;
import org.eclipse.vtp.desktop.model.core.IBusinessObject;
import org.eclipse.vtp.desktop.model.core.IBusinessObjectSet;
import org.eclipse.vtp.framework.util.VariableNameValidator;

public class NewVariableDialog extends FramedDialog
{
	Color darkBlue;
	Color lightBlue;
	/** The text field used to set variable's identifier */
	Text nameField;
	/** A combo box used to select the type of the variable */
	Combo typeCombo;
	/** A combo box used to select the base type if the main type is an array or map */
	Combo baseTypeCombo;
	/** A checkbox used to denote whether the variable may contain sensitive data */
	Button secureButton;
	String name;
	FieldType type;
	/** The button used to dismiss the dialog and keep the changes */
	Button okButton;
	List<String> reservedNames;
	IBusinessObjectSet businessObjectSet;
	boolean secure = false;
	Label nameLabel;
	
	public NewVariableDialog(Shell shell, List<String> reservedNames, IBusinessObjectSet businessObjectSet)
	{
		super(shell);
		this.setSideBarSize(40);
		this.setTitle("New Variable");
		this.reservedNames = reservedNames;
		this.businessObjectSet = businessObjectSet;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.dialogs.FramedDialog#createButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	protected void createButtonBar(Composite parent)
	{
		parent.setLayout(new GridLayout(1, true));

		Composite buttons = new Composite(parent, SWT.NONE);
		buttons.setBackground(parent.getBackground());

		GridData buttonsData = new GridData(GridData.FILL_BOTH);
		buttonsData.horizontalAlignment = SWT.RIGHT;
		buttons.setLayoutData(buttonsData);

		RowLayout rl = new RowLayout();
		rl.pack = false;
		rl.spacing = 5;
		buttons.setLayout(rl);

		okButton = new Button(buttons, SWT.PUSH);
		okButton.setText("Ok");
		okButton.setEnabled(false);
		okButton.addSelectionListener(new SelectionListener()
			{
				public void widgetSelected(SelectionEvent e)
				{
					okPressed();
				}

				public void widgetDefaultSelected(SelectionEvent e)
				{
				}
			});

		final Button cancelButton = new Button(buttons, SWT.PUSH);
		cancelButton.setText("Cancel");
		cancelButton.addSelectionListener(new SelectionListener()
			{
				public void widgetSelected(SelectionEvent e)
				{
					cancelPressed();
				}

				public void widgetDefaultSelected(SelectionEvent e)
				{
				}
			});
		
		if(Display.getCurrent().getDismissalAlignment() == SWT.RIGHT)
		{
			cancelButton.moveAbove(okButton);
		}
		this.getShell().setDefaultButton(okButton);
		
	}

	/**
	 * Closes the dialog, saving all changes and setting the return code to ok
	 */
	public void okPressed()
	{
		this.name = nameField.getText();
		Primitive prim = Primitive.find(typeCombo.getItem(typeCombo.getSelectionIndex()));
		if(prim != null)
		{
			if(prim.hasBaseType())
			{
				Primitive basePrim = Primitive.find(baseTypeCombo.getItem(baseTypeCombo.getSelectionIndex()));
				if(basePrim != null)
				{
					this.type = new FieldType(prim, basePrim);
				}
				else
					this.type = new FieldType(prim, businessObjectSet.getBusinessObject(baseTypeCombo.getItem(baseTypeCombo.getSelectionIndex())));
			}
			else
				this.type = new FieldType(prim);
		}
		else
		{
			this.type = new FieldType(businessObjectSet.getBusinessObject(typeCombo.getItem(typeCombo.getSelectionIndex())));
		}
		this.secure = secureButton.getSelection();
		this.setReturnCode(SWT.OK);
		close();
	}

	/**
	 * Closes the dialog, discarding all changes and setting the return code to cancel
	 */
	public void cancelPressed()
	{
		this.setReturnCode(SWT.CANCEL);
		close();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.dialogs.FramedDialog#createDialogContents(org.eclipse.swt.widgets.Composite)
	 */
	protected void createDialogContents(Composite parent)
	{
		darkBlue = new Color(parent.getDisplay(), 77, 113, 179);
		lightBlue = new Color(parent.getDisplay(), 240, 243, 249);
		parent.addDisposeListener(new DisposeListener()
			{
				public void widgetDisposed(DisposeEvent e)
				{
					darkBlue.dispose();
					lightBlue.dispose();
				}
			});
		this.setFrameColor(darkBlue);
		this.setSideBarColor(lightBlue);
		parent.setLayout(new GridLayout(2, false));

		nameLabel = new Label(parent, SWT.NONE);
		nameLabel.setText("Name");
		nameLabel.setForeground(nameLabel.getDisplay().getSystemColor(SWT.COLOR_RED));
		nameLabel.setBackground(parent.getBackground());

		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		nameLabel.setLayoutData(gd);
		nameField = new Text(parent, SWT.SINGLE | SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		nameField.setLayoutData(gd);
		nameField.addVerifyListener(new VerifyListener()
		{
			public void verifyText(VerifyEvent e)
			{
				String currentName = nameField.getText().substring(0, e.start) + e.text + nameField.getText(e.end, (nameField.getText().length() - 1));
				if(VariableNameValidator.followsVtpNamingRules(currentName))
				{
					nameLabel.setForeground(nameLabel.getDisplay().getSystemColor(SWT.COLOR_BLACK));
					nameField.setForeground(nameField.getDisplay().getSystemColor(SWT.COLOR_BLACK));
					okButton.setEnabled(true);
					for(int b = 0; b < reservedNames.size(); b++)
					{
						if(currentName.equals(reservedNames.get(b))) //Is this name taken?
						{
							nameLabel.setForeground(nameLabel.getDisplay().getSystemColor(SWT.COLOR_RED));
							nameField.setForeground(nameField.getDisplay().getSystemColor(SWT.COLOR_RED));
							okButton.setEnabled(false);	                		
						}
					}
				}
				else
				{
					nameLabel.setForeground(nameLabel.getDisplay().getSystemColor(SWT.COLOR_RED));
					nameField.setForeground(nameField.getDisplay().getSystemColor(SWT.COLOR_RED));
					okButton.setEnabled(false);
				}
            }
		});

		Label typeLabel = new Label(parent, SWT.NONE);
		typeLabel.setText("Type");
		typeLabel.setBackground(parent.getBackground());
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		typeLabel.setLayoutData(gd);
		typeCombo = new Combo(parent,
				SWT.READ_ONLY | SWT.DROP_DOWN | SWT.BORDER);
		typeCombo.add("String");
		typeCombo.add("Number");
		typeCombo.add("Decimal");
		typeCombo.add("Boolean");
		typeCombo.add("DateTime");
		typeCombo.add("Array");
		typeCombo.add("Map");

		typeCombo.select(0);
		typeCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		typeCombo.addSelectionListener(new SelectionListener()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				Primitive prim = Primitive.find(typeCombo.getItem(typeCombo.getSelectionIndex()));
				baseTypeCombo.setEnabled(prim != null && prim.hasBaseType());
				baseTypeCombo.select(0);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{
			}
		});

		baseTypeCombo = new Combo(parent,
			SWT.READ_ONLY | SWT.DROP_DOWN | SWT.BORDER);
		baseTypeCombo.add("ANYTYPE");
		baseTypeCombo.add("String");
		baseTypeCombo.add("Number");
		baseTypeCombo.add("Decimal");
		baseTypeCombo.add("Boolean");
		baseTypeCombo.add("DateTime");
		baseTypeCombo.setEnabled(false);
		baseTypeCombo.select(0);
		baseTypeCombo.setLayoutData(new GridData());

		List<IBusinessObject> businessObjects = this.businessObjectSet.getBusinessObjects();

		for(int i = 0; i < businessObjects.size(); i++)
		{
			IBusinessObject ibo = businessObjects.get(i);
			typeCombo.add(ibo.getName());
			baseTypeCombo.add(ibo.getName());
		}

		secureButton = new Button(parent, SWT.CHECK);
		secureButton.setText("Make this variable secure");
		secureButton.setSelection(false);
		secureButton.setBackground(parent.getBackground());
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		secureButton.setLayoutData(gd);
		nameField.setFocus();
	}
}
