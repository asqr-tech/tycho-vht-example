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
package org.eclipse.vtp.modules.database.ui.properties;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.vtp.desktop.editors.core.configuration.DesignElementPropertiesPanel;
import org.eclipse.vtp.desktop.model.core.IDatabase;
import org.eclipse.vtp.desktop.model.core.IDatabaseTable;
import org.eclipse.vtp.desktop.model.elements.core.internal.PrimitiveElement;
import org.eclipse.vtp.modules.database.ui.DatabaseQueryInformationProvider;

/**
 * @author Trip
 *
 */
public class DatabaseQuerySourceDatabasePropertiesPanel
	extends DesignElementPropertiesPanel
{
	DatabaseQueryInformationProvider queryElement;
	DatabaseQuerySettingsStructure settings;
	Combo sourceDatabaseCombo;
	Combo sourceTableCombo;
	List<IDatabase> databases;
	Map<String, List<IDatabaseTable>> databaseTables = new HashMap<String, List<IDatabaseTable>>();

	/**
	 * @param name
	 */
	public DatabaseQuerySourceDatabasePropertiesPanel(
		PrimitiveElement dqe, DatabaseQuerySettingsStructure settings)
	{
		super("Source Database", dqe);
		this.queryElement = (DatabaseQueryInformationProvider)dqe.getInformationProvider();
		this.settings = settings;
		databases = dqe.getDesign().getDocument().getProject().getDatabaseSet()
				 .getDatabases();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.ui.app.editor.model.ComponentPropertiesPanel#createControls(org.eclipse.swt.widgets.Composite)
	 */
	public void createControls(Composite parent)
	{
		GridLayout gl = new GridLayout();
		parent.setLayout(gl);

		if(databases.size() > 0)
		{
			Composite sdbComp = new Composite(parent, SWT.NONE);
			Composite stabComp = new Composite(parent, SWT.NONE);
			
			gl = new GridLayout(2, false);
			gl.marginHeight = 10;
			gl.marginWidth = 20;
			sdbComp.setLayout(gl);
			sdbComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			gl = new GridLayout(2, false);
			gl.marginHeight = 10;
			gl.marginWidth = 20;
			stabComp.setLayout(gl);
			stabComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			Label sourceDatabaseLabel = new Label(sdbComp, SWT.NONE);
			sourceDatabaseLabel.setText("Source Database:");
			sourceDatabaseLabel.setBackground(sdbComp.getBackground());
			sourceDatabaseLabel.setLayoutData(new GridData());
			sourceDatabaseCombo = new Combo(sdbComp,
					SWT.DROP_DOWN | SWT.READ_ONLY);
			sourceDatabaseCombo.setLayoutData(new GridData(
					GridData.FILL_HORIZONTAL));

			int sel = -1;

			for(int i = 0; i < databases.size(); i++)
			{
				databaseTables.put(databases.get(i).getName(),
					databases.get(i).getTables());
				sourceDatabaseCombo.add(databases.get(i).getName());

				if(databases.get(i).getName()
						.equals(settings.sourceDatabase))
				{
					sel = i;
				}
			}

			sourceDatabaseCombo.addSelectionListener(new SelectionListener()
				{
					public void widgetSelected(SelectionEvent e)
					{
						if(!sourceDatabaseCombo.getItem(
							sourceDatabaseCombo.getSelectionIndex())
							.equals(settings.sourceDatabase))
						{
							sourceTableCombo.removeAll();
	
							List<IDatabaseTable> tables =
								databaseTables.get(sourceDatabaseCombo.getItem(
										sourceDatabaseCombo.getSelectionIndex()));
	
							for(int i = 0; i < tables.size(); i++)
							{
								sourceTableCombo.add(tables.get(i).getName());
							}
	
							sourceTableCombo.select(0);
							fireSourceChanged();
						}
					}

					public void widgetDefaultSelected(SelectionEvent e)
					{
					}
				});

			Label sourceTableLabel = new Label(stabComp, SWT.NONE);
			sourceTableLabel.setText("Source Table:");
			sourceTableLabel.setBackground(stabComp.getBackground());
			sourceTableLabel.setLayoutData(new GridData(GridData.FILL_VERTICAL));
			sourceTableCombo = new Combo(stabComp, SWT.DROP_DOWN | SWT.READ_ONLY);
			sourceTableCombo.setLayoutData(new GridData(GridData.FILL_BOTH));

			if(sel != -1)
			{
				sourceDatabaseCombo.select(sel);

				List<IDatabaseTable> tables =
					databaseTables.get(sourceDatabaseCombo.getItem(
							sourceDatabaseCombo.getSelectionIndex()));
				sel = -1;

				for(int i = 0; i < tables.size(); i++)
				{
					sourceTableCombo.add(tables.get(i).getName());

					if(tables.get(i).getName()
							.equals(settings.sourceDatabaseTable))
					{
						sel = i;
					}
				}

				if(sel != -1)
				{
					sourceTableCombo.select(sel);
				}
			}

			sourceTableCombo.addSelectionListener(new SelectionListener()
				{
					public void widgetSelected(SelectionEvent e)
					{
						fireSourceChanged();
					}

					public void widgetDefaultSelected(SelectionEvent e)
					{
					}
				});
		}
		else
		{
			parent.setLayout(new GridLayout());

			Label noDatabasesLabel = new Label(parent, SWT.NONE);
			noDatabasesLabel.setBackground(parent.getBackground());
			noDatabasesLabel.setText(
				"You must first create a database to query.");

			GridData gd = new GridData(GridData.FILL_BOTH);
			gd.horizontalAlignment = SWT.CENTER;
			gd.verticalAlignment = SWT.CENTER;
			noDatabasesLabel.setLayoutData(gd);
		}
	}

	private void fireSourceChanged()
	{
		settings.sourceDatabase = sourceDatabaseCombo.getItem(sourceDatabaseCombo
				.getSelectionIndex());
		settings.sourceDatabaseTable = sourceTableCombo.getItem(sourceTableCombo
				.getSelectionIndex());
		settings.fireSourceChanged();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.ui.app.editor.model.ComponentPropertiesPanel#save()
	 */
	public void save()
	{
	}
	
	public void cancel()
	{
		
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
