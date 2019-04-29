package org.eclipse.vtp.modules.standard.ui.properties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.vtp.desktop.editors.core.configuration.DesignElementPropertiesPanel;
import org.eclipse.vtp.desktop.model.core.design.IDesignElement;
import org.eclipse.vtp.desktop.model.core.design.IExitBroadcastReceiver;
import org.eclipse.vtp.desktop.model.core.internal.design.ExitBroadcastReceiver;
import org.eclipse.vtp.desktop.model.elements.core.internal.PrimitiveElement;
import org.eclipse.vtp.modules.standard.ui.BroadcastReceiverInformationProvider;

public class BroadcastReceiverPropertiesPanel extends
		DesignElementPropertiesPanel
{
	BroadcastReceiverInformationProvider info;
	List<IExitBroadcastReceiver> receivers;
	/** The text field used to set name of this particular Decision module */
	Text nameField;
	TableViewer table;

	public BroadcastReceiverPropertiesPanel(String name, IDesignElement element)
	{
		super(name, element);
		info = (BroadcastReceiverInformationProvider)((PrimitiveElement)element).getInformationProvider();
		receivers = new ArrayList<IExitBroadcastReceiver>(info.getExitBroadcastReceivers());
	}

	@Override
	public void setConfigurationContext(Map<String, Object> values)
	{
	}

	@Override
	public void createControls(Composite parent)
	{
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setBackground(comp.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		comp.setBackgroundMode(SWT.INHERIT_DEFAULT);
		comp.setLayout(new GridLayout(2, false));

		Label nameLabel = new Label(comp, SWT.NONE);
		nameLabel.setText("Name: ");
		nameLabel.setBackground(comp.getBackground());
		nameLabel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));
		nameField = new Text(comp, SWT.SINGLE | SWT.BORDER);
		nameField.setText(getElement().getName());
		nameField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_CENTER));
		
		Composite bottomComp = new Composite(comp, SWT.NONE);
		bottomComp.setBackgroundMode(SWT.INHERIT_DEFAULT);
		GridLayout bottomLayout = new GridLayout(2, false);
		bottomLayout.marginWidth = 0;
		bottomLayout.marginHeight = 0;
		bottomComp.setLayout(bottomLayout);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		bottomComp.setLayoutData(gd);

		Composite tableComp = new Composite(bottomComp, SWT.NONE);
		tableComp.setBackgroundMode(SWT.INHERIT_DEFAULT);
		TableColumnLayout tableLayout = new TableColumnLayout();
		tableComp.setLayout(tableLayout);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.heightHint = 250;
		gd.widthHint = 300;
		tableComp.setLayoutData(gd);
		Table t = new Table(tableComp, SWT.SINGLE | SWT.BORDER);
		t.setHeaderVisible(true);
		t.setLinesVisible(true);
		TableColumn patternColumn = new TableColumn(t, SWT.LEFT);
		patternColumn.setText("Events to Receive");
		tableLayout.setColumnData(patternColumn, new ColumnWeightData(100, 100, false));
		table = new TableViewer(t);
		table.setContentProvider(new IStructuredContentProvider()
		{
			@Override
			public void dispose()
			{
			}

			@Override
			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput)
			{
			}

			@Override
			public Object[] getElements(Object inputElement)
			{
				return receivers.toArray();
			}
			
		});
		table.setLabelProvider(new ReceiverLabelProvider());
		table.setInput(this);
		Composite buttonComp = new Composite(bottomComp, SWT.NONE);
		buttonComp.setBackgroundMode(SWT.INHERIT_DEFAULT);
		buttonComp.setLayout(new GridLayout(1, true));
		buttonComp.setLayoutData(new GridData());
		Button addButton = new Button(buttonComp, SWT.PUSH);
		addButton.setText("Add Receiver");
		addButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		final Button editButton = new Button(buttonComp, SWT.PUSH);
		editButton.setText("Edit Receiver");
		editButton.setEnabled(false);
		editButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		final Button removeButton = new Button(buttonComp, SWT.PUSH);
		removeButton.setText("Remove Receiver");
		removeButton.setEnabled(false);
		removeButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		table.addSelectionChangedListener(new ISelectionChangedListener()
		{
			@Override
			public void selectionChanged(SelectionChangedEvent event)
			{
				editButton.setEnabled(!event.getSelection().isEmpty());
				removeButton.setEnabled(!event.getSelection().isEmpty());
			}
		});
		addButton.addSelectionListener(new SelectionListener()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				ReceiverNameDialog rnd = new ReceiverNameDialog(BroadcastReceiverPropertiesPanel.this.getContainer().getShell());
				if(Dialog.OK == rnd.open())
				{
					receivers.add(new ExitBroadcastReceiver(rnd.getName()));
					table.refresh();
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{
			}
		});
		editButton.addSelectionListener(new SelectionListener()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{

				IExitBroadcastReceiver receiver = (IExitBroadcastReceiver)((IStructuredSelection)table.getSelection()).getFirstElement();
				ReceiverNameDialog rnd = new ReceiverNameDialog(BroadcastReceiverPropertiesPanel.this.getContainer().getShell());
				rnd.setName(receiver.getExitPattern());
				if(Dialog.OK == rnd.open())
				{
					receivers.remove(receiver);
					receivers.add(new ExitBroadcastReceiver(rnd.getName()));
					table.refresh();
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{
			}
		});
		removeButton.addSelectionListener(new SelectionListener()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{

				IExitBroadcastReceiver receiver = (IExitBroadcastReceiver)((IStructuredSelection)table.getSelection()).getFirstElement();
				receivers.remove(receiver);
				table.refresh();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{
			}
		});
	}

	@Override
	public void save()
	{
		getElement().setName(nameField.getText());
		info.setExitBroadcastReceivers(receivers);
	}

	@Override
	public void cancel()
	{
	}

	@Override
	public List<String> getApplicableContexts()
	{
		return Collections.emptyList();
	}

	public class ReceiverLabelProvider extends BaseLabelProvider implements ITableLabelProvider
	{
		@Override
		public Image getColumnImage(Object element, int columnIndex)
		{
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex)
		{
			return ((IExitBroadcastReceiver)element).getExitPattern();
		}
	}
}
