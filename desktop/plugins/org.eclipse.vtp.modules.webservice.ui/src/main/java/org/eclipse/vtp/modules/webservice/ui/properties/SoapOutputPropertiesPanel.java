/**
 * 
 */
package org.eclipse.vtp.modules.webservice.ui.properties;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.vtp.desktop.editors.core.configuration.DesignElementPropertiesPanel;
import org.eclipse.vtp.desktop.model.core.design.IDesignElement;
import org.eclipse.vtp.modules.webservice.ui.configuration.OutputBinding;
import org.eclipse.vtp.modules.webservice.ui.configuration.WebserviceBindingManager;

/**
 * @author trip
 *
 */
public class SoapOutputPropertiesPanel extends
	DesignElementPropertiesPanel
{
	private WebserviceBindingManager manager = null;
	private OutputBinding outputBinding = null;
	private Text variableField = null;
	private Button processButton = null;
	private Text scriptArea = null;

	/**
	 * @param name
	 * @param element
	 */
	public SoapOutputPropertiesPanel(String name, IDesignElement element)
	{
		super(name, element);
		manager = (WebserviceBindingManager)element.getConfigurationManager(WebserviceBindingManager.TYPE_ID);
		outputBinding = manager.getOutputBinding();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.editors.core.configuration.ComponentPropertiesPanel#createControls(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControls(Composite parent)
	{
		FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		Composite mainComp = new Composite(parent, SWT.NONE);
		mainComp.setBackground(parent.getBackground());
		mainComp.setLayout(new GridLayout(2, false));
		
		final Section contentSection =
			toolkit.createSection(mainComp, Section.TITLE_BAR);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL
						| GridData.VERTICAL_ALIGN_BEGINNING);
		gd.horizontalSpan = 2;
		contentSection.setLayoutData(gd);
		contentSection.setText("Output");
		
		Label variableLabel = new Label(mainComp, SWT.NONE);
		variableLabel.setBackground(mainComp.getBackground());
		variableLabel.setText("Output Variable");
		variableLabel.setLayoutData(new GridData());
		variableField = new Text(mainComp, SWT.BORDER | SWT.SINGLE);
		variableField.setText(outputBinding.getVariableName());
		variableField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		processButton = new Button(mainComp, SWT.CHECK);
		processButton.setText("Process the result immediately");
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		processButton.setLayoutData(gd);
		processButton.setSelection(outputBinding.shouldProcess());
		processButton.addSelectionListener(new SelectionListener()
		{
			public void widgetDefaultSelected(SelectionEvent e)
			{
			}

			public void widgetSelected(SelectionEvent e)
			{
				scriptArea.setEnabled(processButton.getSelection());
			}
		});
		
		scriptArea = new Text(mainComp, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
		scriptArea.setText(outputBinding.getScriptText());
		scriptArea.setEnabled(outputBinding.shouldProcess());
		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		scriptArea.setLayoutData(gd);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.editors.core.configuration.ComponentPropertiesPanel#save()
	 */
	@Override
	public void save()
	{
		outputBinding.setShouldProcess(processButton.getSelection());
		outputBinding.setVariableName(variableField.getText());
		outputBinding.setScriptText(scriptArea.getText());
		getElement().commitConfigurationChanges(manager);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.editors.core.configuration.ComponentPropertiesPanel#cancel()
	 */
	@Override
	public void cancel()
	{
		getElement().rollbackConfigurationChanges(manager);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.editors.core.configuration.ComponentPropertiesPanel#setConfigurationContext(java.util.Map)
	 */
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
