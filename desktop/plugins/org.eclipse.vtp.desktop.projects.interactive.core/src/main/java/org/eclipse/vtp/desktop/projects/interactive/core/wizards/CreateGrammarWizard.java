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
package org.eclipse.vtp.desktop.projects.interactive.core.wizards;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.vtp.desktop.model.interactive.core.IMediaContainer;
import org.eclipse.vtp.desktop.model.interactive.core.IMediaFile;
import org.eclipse.vtp.desktop.model.interactive.core.IMediaResource;
import org.eclipse.vtp.desktop.model.interactive.core.internal.MediaFile;
import org.eclipse.vtp.desktop.projects.interactive.core.Activator;

/**
 * This wizard walks the user through the steps required to create a new
 * grammar file for an application. The user is prompted to enter a name for the new
 * file. This name must be unique among the current files in the
 * folder. The file is automatically created by this wizard and so
 * requires no actions from the caller of the wizard.
 *
 * @author Trip
 */
public class CreateGrammarWizard extends Wizard implements INewWizard,
IExecutableExtension
{
 	/**
	 * The media container that will contain the new grammar file
	 */
	private IMediaContainer mediaContainer = null;

	/**
	 * The page that collects the file name and grammar type.
	 */
	private GrammarWizardPage bwp = null;
	
	private MediaFile createdGrammar = null;

	IConfigurationElement configElement = null;
	/**
	 * Creates a new <code>CreateDatabaseWizard</code> instance in the
	 * given database set.
	 *
	 * @param mediaContainer The database set that will contain the new database descriptor
	 */
	public CreateGrammarWizard()
	{
		super();
		bwp = new GrammarWizardPage();
		addPage(bwp);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IExecutableExtension#setInitializationData(org.eclipse.core.runtime.IConfigurationElement, java.lang.String, java.lang.Object)
	 */
	public void setInitializationData(IConfigurationElement cfig,
		String propertyName, Object data)
	{
		configElement = cfig;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection)
	{
		Object sel = selection.getFirstElement();
		if(sel instanceof IMediaContainer)
			mediaContainer = (IMediaContainer)sel;
		else if(sel instanceof IMediaFile)
			mediaContainer = ((IMediaFile)sel).getParentMediaContainer();
	}
	
	/**
	 * @return
	 */
	public MediaFile getGrammar()
	{
		return createdGrammar;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.jface.wizard.IWizard#performFinish()
	 */
	public boolean performFinish()
	{
		try
		{
			MediaFile mediaFile = (MediaFile)mediaContainer.create(bwp.grammarNameField.getText() + (bwp.grammarNameField.getText().endsWith(".grxml") ? "" : ".grxml"));
			
			if(bwp.voiceGrammarButton.getSelection())
			{
				mediaFile.getUnderlyingFile().setContents(getClass().getClassLoader().getResourceAsStream("voice_grammar_template.grxml"), true, false, null);
			}
			else
			{
				if(bwp.dtmfTemplateButton.getSelection())
				{
					InputStream tempin = Activator.class.getClassLoader().getResourceAsStream("dtmf_grammar_gen_template.grxml");
					StringBuffer text = new StringBuffer();
					byte[] buf = new byte[10240];
					int len = tempin.read(buf);
					while(len != -1)
					{
						text.append(new String(buf, 0, len));
						len = tempin.read(buf);
					}
					String contents = text.toString();
					contents = contents.replaceAll("\\[min\\]", bwp.minDigitsField.getText());
					contents = contents.replaceAll("\\[max\\]", bwp.maxDigitsField.getText());
					mediaFile.getUnderlyingFile().setContents(new ByteArrayInputStream(contents.getBytes()), true, false, null);
				}
				else
					mediaFile.getUnderlyingFile().setContents(getClass().getClassLoader().getResourceAsStream("dtmf_grammar_template.grxml"), true, false, null);
				
			}
			createdGrammar = mediaFile;
			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return false;
	}

	private class GrammarWizardPage extends WizardPage
	{
		Text grammarNameField = null;
		Button voiceGrammarButton = null;
		Button dtmfGrammarButton = null;
		Button dtmfTemplateButton = null;
		Text minDigitsField = null;
		Text maxDigitsField = null;
		
		public GrammarWizardPage()
		{
			super("GrammarPage", "Enter a name for the grammar file", null);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
		 */
		public void createControl(Composite parent)
		{
			setPageComplete(false);

			Composite comp = new Composite(parent, SWT.NONE);
			comp.setLayout(new FormLayout());

			Label grammarNameLabel = new Label(comp, SWT.NONE);
			grammarNameLabel.setText("Grammar File Name:");
			grammarNameLabel.setSize(grammarNameLabel.computeSize(SWT.DEFAULT,
					SWT.DEFAULT));
			FormData brandNameLabelData = new FormData();
			brandNameLabelData.left = new FormAttachment(0, 10);
			brandNameLabelData.top = new FormAttachment(0, 30);
			brandNameLabelData.right = new FormAttachment(0,
					10 + grammarNameLabel.getSize().x);
			brandNameLabelData.bottom = new FormAttachment(0,
					30 + grammarNameLabel.getSize().y);
			grammarNameLabel.setLayoutData(brandNameLabelData);
			
			grammarNameField = new Text(comp, SWT.SINGLE | SWT.BORDER);
			grammarNameField.addModifyListener(new ModifyListener()
				{
					public void modifyText(ModifyEvent e)
					{
						String n = grammarNameField.getText();
						try
                        {
	                        for(IMediaResource mr : mediaContainer.listMediaResources())
	                        {
	                        	if(mr.getName().equals(n))
	                        	{
	                        		setErrorMessage(
	                        			"A grammar file already exists with that name.");
	                        		setPageComplete(false);

	                        		return;
	                        	}
	                        }

	                        setErrorMessage(null);
	                        setPageComplete(true);
                        }
                        catch(CoreException e1)
                        {
	                        e1.printStackTrace();
                        }
					}
				});
			grammarNameField.addVerifyListener(new VerifyListener()
			{

				public void verifyText(VerifyEvent e)
                {
					String currentName = grammarNameField.getText().substring(0, e.start) + e.text + grammarNameField.getText(e.end, (grammarNameField.getText().length() - 1));
	                if(currentName.length() > 255)
	                {
	                	e.doit = false;
	                	return;
	                }
                }
				
			});
			FormData brandNameFieldData = new FormData();
			brandNameFieldData.left = new FormAttachment(grammarNameLabel, 10);
			brandNameFieldData.top = new FormAttachment(0, 29);
			brandNameFieldData.right = new FormAttachment(100, -10);
			grammarNameField.setLayoutData(brandNameFieldData);
			
			voiceGrammarButton = new Button(comp, SWT.RADIO);
			voiceGrammarButton.setText("Voice Grammar");
			voiceGrammarButton.setSelection(true);
			FormData voiceButtonData = new FormData();
			voiceButtonData.left = new FormAttachment(0, 10);
			voiceButtonData.top = new FormAttachment(grammarNameLabel, 20);
			voiceButtonData.right = new FormAttachment(100, -10);
			voiceGrammarButton.setLayoutData(voiceButtonData);
			
			dtmfGrammarButton = new Button(comp, SWT.RADIO);
			dtmfGrammarButton.setText("DTMF Grammar");
			FormData dtmfButtonData = new FormData();
			dtmfButtonData.left = new FormAttachment(0, 10);
			dtmfButtonData.top = new FormAttachment(voiceGrammarButton, 20);
			dtmfButtonData.right = new FormAttachment(100, -10);
			dtmfGrammarButton.setLayoutData(dtmfButtonData);

			dtmfTemplateButton = new Button(comp, SWT.CHECK);
			dtmfTemplateButton.setText("Use this template");
			FormData dtmfTemplateData = new FormData();
			dtmfTemplateData.left = new FormAttachment(0, 30);
			dtmfTemplateData.top = new FormAttachment(dtmfGrammarButton, 15);
			dtmfTemplateData.right = new FormAttachment(100, -10);
			dtmfTemplateButton.setLayoutData(dtmfTemplateData);
			
			Label minDigitsLabel = new Label(comp, SWT.NONE);
			minDigitsLabel.setText("Minimum Digits");
			FormData minDigitsLabelData = new FormData();
			minDigitsLabelData.left = new FormAttachment(0, 50);
			minDigitsLabelData.top = new FormAttachment(dtmfTemplateButton, 15);
			minDigitsLabel.setLayoutData(minDigitsLabelData);
			
			minDigitsField = new Text(comp, SWT.BORDER);
			FormData minDigitsFieldData = new FormData();
			minDigitsFieldData.left = new FormAttachment(minDigitsLabel, 20);
			minDigitsFieldData.top = new FormAttachment(dtmfTemplateButton, 15);
			minDigitsFieldData.right = new FormAttachment(minDigitsLabel, 50, SWT.RIGHT);
			minDigitsField.setLayoutData(minDigitsFieldData);
			minDigitsField.setEnabled(false);
			
			Label maxDigitsLabel = new Label(comp, SWT.NONE);
			maxDigitsLabel.setText("Maximum Digits");
			FormData maxDigitsLabelData = new FormData();
			maxDigitsLabelData.left = new FormAttachment(0, 50);
			maxDigitsLabelData.top = new FormAttachment(minDigitsLabel, 15);
			maxDigitsLabel.setLayoutData(maxDigitsLabelData);
			
			maxDigitsField = new Text(comp, SWT.BORDER);
			FormData maxDigitsFieldData = new FormData();
			maxDigitsFieldData.left = new FormAttachment(maxDigitsLabel, 20);
			maxDigitsFieldData.top = new FormAttachment(minDigitsLabel, 15);
			maxDigitsFieldData.right = new FormAttachment(maxDigitsLabel, 50, SWT.RIGHT);
			maxDigitsField.setLayoutData(maxDigitsFieldData);
			maxDigitsField.setEnabled(false);
			
			dtmfTemplateButton.addSelectionListener(new SelectionListener()
			{
				public void widgetDefaultSelected(SelectionEvent e)
                {
                }

				public void widgetSelected(SelectionEvent e)
                {
					minDigitsField.setEnabled(dtmfTemplateButton.getSelection());
					maxDigitsField.setEnabled(dtmfTemplateButton.getSelection());
                }
			});
			
			setControl(comp);
		}
	}
}
