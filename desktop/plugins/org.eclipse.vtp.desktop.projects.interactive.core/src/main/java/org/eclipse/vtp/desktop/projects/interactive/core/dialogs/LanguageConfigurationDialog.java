package org.eclipse.vtp.desktop.projects.interactive.core.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableLayout;
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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.vtp.desktop.model.interactive.core.internal.InteractionTypeSupport;
import org.eclipse.vtp.desktop.model.interactive.core.internal.LanguageSupport;

/**
 * @author trip
 *
 */
public class LanguageConfigurationDialog extends Dialog
{
	private Shell parentShell = null;
	private TableViewer languageViewer = null;
	private List<String> languages = new ArrayList<String>();
	private InteractionTypeSupport support = null;

	/**
	 * @param parentShell
	 */
	public LanguageConfigurationDialog(Shell parentShell)
	{
		super(parentShell);
		this.parentShell = parentShell;
	}
	
	public void setCurrentSupport(InteractionTypeSupport its)
	{
		support = its;
		for(LanguageSupport ls : its.getSupportedLanguages())
		{
			languages.add(ls.getLanguage());
		}
	}

	@Override
	protected Control createDialogArea(Composite parent)
	{
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new GridLayout(2, false));
		
		Table table = new Table(comp, SWT.BORDER | SWT.SINGLE | SWT.FULL_SELECTION);
		TableLayout tableLayout = new TableLayout();
		tableLayout.addColumnData(new ColumnWeightData(1));
		table.setLayout(tableLayout);
		@SuppressWarnings("unused")
		TableColumn col = new TableColumn(table, SWT.NONE);
		GridData gd = new GridData(GridData.FILL_BOTH);
		table.setLayoutData(gd);

		languageViewer = new TableViewer(table);
		languageViewer.setContentProvider(new LanguageContentProvider());
		languageViewer.setLabelProvider(new LanguageLabelProvider());
		languageViewer.setInput(this);
		
		Composite buttonComp = new Composite(comp, SWT.NONE);
		gd = new GridData(GridData.FILL_VERTICAL);
		buttonComp.setLayoutData(gd);
		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 10;
		buttonComp.setLayout(layout);
		
		final Button addButton = new Button(buttonComp, SWT.PUSH);
		addButton.setText("Add Language");
		addButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		addButton.addSelectionListener(new SelectionListener()
		{
			public void widgetDefaultSelected(SelectionEvent e)
			{
			}

			public void widgetSelected(SelectionEvent e)
			{
				LanguageDialog dialog = new LanguageDialog(parentShell);
				dialog.setReservedNames(languages);
				if(dialog.open() == Dialog.OK)
				{
					languages.add(dialog.getName());
					languageViewer.refresh();
				}
			}
		});
		
		final Button removeButton = new Button(buttonComp, SWT.PUSH);
		removeButton.setText("Remove Language");
		removeButton.setEnabled(false);
		removeButton.setLayoutData(new GridData());
		removeButton.addSelectionListener(new SelectionListener()
		{
			public void widgetDefaultSelected(SelectionEvent e)
			{
			}

			public void widgetSelected(SelectionEvent e)
			{
				languages.remove(((IStructuredSelection)languageViewer.getSelection()).getFirstElement());
				languageViewer.refresh();
			}
		});
		
		languageViewer.addSelectionChangedListener(new ISelectionChangedListener()
		{

			public void selectionChanged(SelectionChangedEvent event)
			{
				boolean enabled = !event.getSelection().isEmpty();
				removeButton.setEnabled(enabled);
			}
			
		});
		
		return comp;
	}

	public class LanguageContentProvider implements IStructuredContentProvider
	{

		public Object[] getElements(Object inputElement)
		{
			return languages.toArray();
		}

		public void dispose()
		{
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
		{
		}
		
	}

	public class LanguageLabelProvider extends BaseLabelProvider implements ILabelProvider
	{

		public Image getImage(Object element)
		{
			return null;
		}

		public String getText(Object element)
		{
			return element.toString();
		}
		
	}

	protected void okPressed()
	{
		List<LanguageSupport> originalSupport = support.getSupportedLanguages();
		for(LanguageSupport language : originalSupport)
		{
			if(!languages.contains(language.getLanguage()))
			{
				support.removeLanguageSupport(language.getLanguage());
			}
		}
outer:	for(String language : languages)
		{
			for(LanguageSupport ls : originalSupport)
			{
				if(ls.getLanguage().equals(language))
				{
					continue outer;
				}
			}
			support.addLanguageSupport(language);
		}
		super.okPressed();
	}
	
	
}
