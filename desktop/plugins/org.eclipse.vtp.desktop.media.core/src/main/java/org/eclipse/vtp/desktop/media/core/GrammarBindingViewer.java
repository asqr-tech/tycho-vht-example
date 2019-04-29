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
package org.eclipse.vtp.desktop.media.core;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.vtp.desktop.model.core.branding.IBrand;
import org.eclipse.vtp.desktop.model.core.design.IDesignElement;
import org.eclipse.vtp.desktop.model.interactive.core.IInteractiveWorkflowProject;
import org.eclipse.vtp.desktop.model.interactive.core.IMediaProviderManager;
import org.eclipse.vtp.desktop.model.interactive.core.configuration.generic.BrandBinding;
import org.eclipse.vtp.desktop.model.interactive.core.configuration.generic.GrammarBindingItem;
import org.eclipse.vtp.desktop.model.interactive.core.configuration.generic.LanguageBinding;
import org.eclipse.vtp.desktop.model.interactive.core.configuration.generic.NamedBinding;
import org.eclipse.vtp.framework.interactions.core.media.InputGrammar;

public class GrammarBindingViewer implements MouseListener
{
	NamedBinding grammarBinding;
	IBrand currentBrand;
	String currentLanguage;
	Label contents;
	String interactionType;
	private IDesignElement designElement = null;

	/**
	 * @param grammarBinding
	 * @param interactionType
	 */
	public GrammarBindingViewer(IDesignElement designElement, NamedBinding grammarBinding, String interactionType)
	{
		super();
		this.grammarBinding = grammarBinding;
		this.interactionType = interactionType;
		this.designElement = designElement;
	}

	/**
	 * @param parent
	 */
	public void createControls(Composite parent)
	{
		contents = new Label(parent, SWT.WRAP);
		contents.setBackground(parent.getBackground());
		contents.setText("Not Configured");
		contents.addMouseListener(this);
	}
	
	/**
	 * @return
	 */
	public Control getControl()
	{
		return contents;
	}
	
	/**
	 * @param brand
	 */
	public void setCurrentBrand(IBrand brand)
	{
		this.currentBrand = brand;
		if(currentLanguage != null)
		{
			setContents();
		}
	}

	private void setContents()
	{
		LanguageBinding languageBinding = grammarBinding.getLanguageBinding(currentLanguage);
		BrandBinding brandBinding = languageBinding.getBrandBinding(currentBrand);
		GrammarBindingItem pbi = (GrammarBindingItem)brandBinding.getBindingItem();
		if(pbi == null)
		{
			pbi = new GrammarBindingItem();
		}
		contents.setText(pbi.getGrammar() == null ? "Not Configured" : pbi.getGrammar().toString());
		contents.getParent().layout();
	}
	
	/**
	 * @param language
	 */
	public void setCurrentLanguage(String language)
	{
		this.currentLanguage = language;
		if(currentBrand != null)
		{
			setContents();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.MouseListener#mouseDoubleClick(org.eclipse.swt.events.MouseEvent)
	 */
	public void mouseDoubleClick(MouseEvent e)
	{
		try
		{
			Shell workbenchShell = Display.getCurrent().getActiveShell();
			GrammarEntryDialog pbd = new GrammarEntryDialog(workbenchShell);
			LanguageBinding languageBinding = grammarBinding.getLanguageBinding(currentLanguage);
			BrandBinding brandBinding = languageBinding.getBrandBinding(currentBrand);
			GrammarBindingItem pbi = (GrammarBindingItem)brandBinding.getBindingItem();
			if(pbi == null)
				pbi = new GrammarBindingItem();
			IMediaProviderManager mediaProviderManager = ((IInteractiveWorkflowProject)designElement.getDesign().getDocument().getProject()).getMediaProviderManager();
			pbd.setMediaProvider(mediaProviderManager.getMediaProvider(interactionType, currentBrand, currentLanguage));
			pbd.setContent(pbi.getGrammar());
			int result = pbd.open();
			if(result == Window.OK)
			{
				InputGrammar gram = pbd.getContent();
				if(gram != null)
				{
					pbi = (GrammarBindingItem)pbi.clone();
					pbi.setGrammar(gram);
					brandBinding.setBindingItem(pbi);
				}
				else
				{
					brandBinding.setBindingItem(null);
				}
				setContents();
				Composite comp = contents.getParent();
				while(comp.getParent() != null && comp.getParent().getLayout() != null && comp.getParent().getLayout() instanceof GridLayout)
					comp = comp.getParent();
				comp.layout(true, true);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.MouseListener#mouseDown(org.eclipse.swt.events.MouseEvent)
	 */
	public void mouseDown(MouseEvent e)
	{
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.MouseListener#mouseUp(org.eclipse.swt.events.MouseEvent)
	 */
	public void mouseUp(MouseEvent e)
	{
	}
}
