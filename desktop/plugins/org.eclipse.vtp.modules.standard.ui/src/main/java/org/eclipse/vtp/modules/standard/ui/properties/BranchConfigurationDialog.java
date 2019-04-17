package org.eclipse.vtp.modules.standard.ui.properties;

import java.util.ArrayList;
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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.vtp.desktop.core.dialogs.FramedDialog;

/**
 * A dialog used to configure the properties of a SubdialogOutput object.
 */
public class BranchConfigurationDialog extends FramedDialog
{
	/** A text field used to display/modify the name of the variable */
	Text variableNameField;
	/** A text field used to display/modify a static value for the variable */
	Text staticValueField;
	/** The SubdialogOutput object this dialog will modify*/
	Branch branch;
	Color darkBlue;
	Color lightBlue;
	Button okButton;
	Label nameLabel;
	Label valueLabel;
	Button secureButton;
	List<Branch> branches = new ArrayList<Branch>();;

	/**
	 * Creates a new OutputValueDialog
	 * @param shellProvider
	 */
	public BranchConfigurationDialog(Shell shell, List<Branch> branches)
	{
		super(shell);
		this.setSideBarSize(40);
		this.setTitle("Select a value");
		this.branches = branches;
	}

	/**
	 * Specifies which SubdialogOutput object to modify
	 * @param branch - the SubdialogOutput object to modify
	 */
	public void setValue(Branch branch)
	{
		this.branch = branch;
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
	 * Saves any changes made to this object and exits with a return code of SWT.OK
	 */
	public void okPressed()
	{
		branch.setName(variableNameField.getText());
		branch.setExpression(staticValueField.getText());
		branch.setSecure(secureButton.getSelection());
		branch.setNumber(branches.size());
		this.setReturnCode(SWT.OK);
		close();
	}

	/**
	 * Cancels any changes made to this object and exits with a return code of SWT.CANCEL
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
		nameLabel.setText("Exit Path Name");
		nameLabel.setToolTipText("This will be the name given to a \r\n" +
				"new connector exiting the \r\n" +
				"branch module.");
		nameLabel.setBackground(parent.getBackground());
		GridData gd = new GridData();
		nameLabel.setLayoutData(gd);
		
		variableNameField = new Text(parent, SWT.SINGLE | SWT.BORDER);
		variableNameField.setText(branch.getName());
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		variableNameField.setLayoutData(gd);
		variableNameField.addVerifyListener(new VerifyListener()
		{
			public void verifyText(VerifyEvent e)
			{
				String currentName = variableNameField.getText().substring(0, e.start) + e.text + variableNameField.getText(e.end, (variableNameField.getText().length() - 1));
				nameLabel.setForeground(nameLabel.getDisplay().getSystemColor(SWT.COLOR_BLACK));
				variableNameField.setForeground(variableNameField.getDisplay().getSystemColor(SWT.COLOR_BLACK));
				okButton.setEnabled(true);
				for(Branch br : branches)
				{
					if((currentName.equals(br.getName()) && !(currentName.equals(branch.getName()))) || "".equals(currentName)) //Is this name taken?
					{
						nameLabel.setForeground(nameLabel.getDisplay().getSystemColor(SWT.COLOR_RED));
						variableNameField.setForeground(variableNameField.getDisplay().getSystemColor(SWT.COLOR_RED));
						okButton.setEnabled(false);	                		
					}
				}
			}
		});

		if("".equals(variableNameField.getText()))
		{
			nameLabel.setForeground(nameLabel.getDisplay().getSystemColor(SWT.COLOR_RED));
			variableNameField.setForeground(variableNameField.getDisplay().getSystemColor(SWT.COLOR_RED));
			okButton.setEnabled(false);
		}
			
		valueLabel = new Label(parent, SWT.NONE);
		valueLabel.setText("Expression");
		valueLabel.setToolTipText("If this expression evaluates to true, \r\n" +
				"then the branch module will use \r\n" +
				"this exit path.");
		valueLabel.setBackground(parent.getBackground());
		gd = new GridData();
		valueLabel.setLayoutData(gd);
		
		staticValueField = new Text(parent, SWT.SINGLE | SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		staticValueField.setLayoutData(gd);
		staticValueField.setText((branch.getExpression() == null) ? "" : branch.getExpression());

		secureButton = new Button(parent, SWT.CHECK);
		secureButton.setText("Secure Expression");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		secureButton.setLayoutData(gridData);
		secureButton.setSelection(branch.isSecure());

	}

}
