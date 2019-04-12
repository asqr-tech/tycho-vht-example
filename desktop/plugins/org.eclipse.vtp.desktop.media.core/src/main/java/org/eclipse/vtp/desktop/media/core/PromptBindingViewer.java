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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.vtp.desktop.model.core.branding.IBrand;
import org.eclipse.vtp.desktop.model.core.design.IDesignElement;
import org.eclipse.vtp.desktop.model.core.design.Variable;
import org.eclipse.vtp.desktop.model.interactive.core.IInteractiveWorkflowProject;
import org.eclipse.vtp.desktop.model.interactive.core.IMediaProviderManager;
import org.eclipse.vtp.desktop.model.interactive.core.configuration.generic.BrandBinding;
import org.eclipse.vtp.desktop.model.interactive.core.configuration.generic.LanguageBinding;
import org.eclipse.vtp.desktop.model.interactive.core.configuration.generic.NamedBinding;
import org.eclipse.vtp.desktop.model.interactive.core.configuration.generic.PromptBindingCase;
import org.eclipse.vtp.desktop.model.interactive.core.configuration.generic.PromptBindingEntry;
import org.eclipse.vtp.desktop.model.interactive.core.configuration.generic.PromptBindingItem;
import org.eclipse.vtp.desktop.model.interactive.core.configuration.generic.PromptBindingNode;
import org.eclipse.vtp.desktop.model.interactive.core.configuration.generic.PromptBindingSwitch;
import org.eclipse.vtp.framework.interactions.core.media.Content;
import org.eclipse.vtp.framework.interactions.core.media.FileContent;
import org.eclipse.vtp.framework.interactions.core.media.FormattableContent;
import org.eclipse.vtp.framework.interactions.core.media.IMediaProvider;
import org.eclipse.vtp.framework.interactions.core.media.PlaceholderContent;
import org.eclipse.vtp.framework.interactions.core.media.ReferencedContent;
import org.eclipse.vtp.framework.interactions.core.media.TextContent;

public class PromptBindingViewer implements MouseListener {
	NamedBinding namedBinding;
	BrandBinding brandBinding = null;
	IBrand currentBrand;
	String currentLanguage;
	Label contents;
	String interactionType;
	List<Variable> variables;
	List<ContentPlaceholder> placeholders = Collections.emptyList();
	List<PromptBindingViewerListener> listeners = new ArrayList<PromptBindingViewerListener>();
	private IDesignElement designElement = null;

	/**
	 * @param namedBinding
	 * @param interactionType
	 * @param variables
	 */
	public PromptBindingViewer(IDesignElement designElement,
			NamedBinding namedBinding, String interactionType,
			List<Variable> variables) {
		super();
		this.namedBinding = namedBinding;
		this.interactionType = interactionType;
		this.variables = variables;
		this.designElement = designElement;
	}

	/**
	 * @param parent
	 */
	public void createControls(Composite parent) {
		contents = new Label(parent, SWT.WRAP);
		contents.setBackground(parent.getBackground());
		contents.setText("Not Configured");
		contents.addMouseListener(this);
	}

	/**
	 * @return
	 */
	public Control getControl() {
		return contents;
	}

	/**
	 * @param language
	 */
	public void setCurrentLanguage(String language) {
		this.currentLanguage = language;
		if (currentBrand != null) {
			setContents();
		}
	}

	/**
	 * @param brand
	 */
	public void setCurrentBrand(IBrand brand) {
		this.currentBrand = brand;
		if (currentLanguage != null) {
			setContents();
		}
	}
	
	public void setPlaceholders(List<ContentPlaceholder> placeholders)
	{
		this.placeholders = placeholders;
	}

	private void setContents() {
		StringBuffer buf = new StringBuffer();
		LanguageBinding languageBinding = namedBinding
				.getLanguageBinding(currentLanguage);
		brandBinding = languageBinding.getBrandBinding(currentBrand);
		PromptBindingItem pbi = (PromptBindingItem) brandBinding
				.getBindingItem();
		if (pbi != null) {
			List<PromptBindingNode> entries = pbi.getEntries();
			for (PromptBindingNode pbie : entries)
				calculateContents(buf, pbie);
		}
		contents.setText(buf.length() < 1 ? "Not Configured" : buf.toString());
		contents.getParent().layout();
	}

	private void calculateContents(StringBuffer buf, PromptBindingNode node) {
		if (node instanceof PromptBindingSwitch) {
			PromptBindingSwitch branch = (PromptBindingSwitch) node;
			boolean first = true;
			for (PromptBindingCase child : branch.getChildren()) {
				if (child.getCondition() == null) {
					if (!first)
						buf.append("ELSE { ");
					for (PromptBindingNode c : child.getChildren())
						calculateContents(buf, c);
					if (!first)
						buf.append(" } ");
				} else if (first) {
					buf.append("IF (");
					buf.append(child.getCondition());
					buf.append(") { ");
					for (PromptBindingNode c : child.getChildren())
						calculateContents(buf, c);
					buf.append(" } ");
				} else {
					buf.append("ELSE IF (");
					buf.append(child.getCondition());
					buf.append(") { ");
					for (PromptBindingNode c : child.getChildren())
						calculateContents(buf, c);
					buf.append(" } ");
				}
				first = false;
			}
		} else if (node instanceof PromptBindingCase) {
			for (PromptBindingNode c : ((PromptBindingCase) node).getChildren())
				calculateContents(buf, c);
		} else if (node instanceof PromptBindingEntry) {
			PromptBindingEntry pbie = (PromptBindingEntry) node;
			Content content = pbie.getContent();
			if (content instanceof FormattableContent) {
				FormattableContent fc = (FormattableContent) content;
				buf.append(fc.getContentTypeName() + "(" + fc.getFormatName()
						+ ", " + fc.getValue() + ")");
			} else if (content instanceof TextContent) {
				String tc = ((TextContent) content).getText();
				boolean allWhite = true;
				char[] chars = tc.toCharArray();
				for (int ic = 0; ic < chars.length; ic++) {
					if (!Character.isWhitespace(chars[ic]))
						allWhite = false;
				}
				buf.append(allWhite ? "TEXT(" + tc + ")" : tc);
			} else if (content instanceof ReferencedContent) {
				buf.append("REFERENCE("
						+ ((ReferencedContent) content).getReferencedName()
						+ ")");
			} else if (content instanceof FileContent) {
				FileContent fc = (FileContent) content;
				buf.append(fc.getFileTypeName() + "(" + fc.getPath() + ")");
			}
			else if (content instanceof PlaceholderContent)
			{
				buf.append("Placeholder(");
				buf.append(((PlaceholderContent)content).getPlaceholder());
				buf.append(")");
			}
			buf.append(' ');
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.swt.events.MouseListener#mouseDoubleClick(org.eclipse.swt
	 * .events.MouseEvent)
	 */
	public void mouseDoubleClick(MouseEvent e) {
		try {
			IMediaProviderManager mediaProviderManager = ((IInteractiveWorkflowProject) designElement
					.getDesign().getDocument().getProject())
					.getMediaProviderManager();
			IMediaProvider mediaProvider = mediaProviderManager
					.getMediaProvider(interactionType, currentBrand,
							currentLanguage);
			if (mediaProvider == null) // incomplete configuration
			{
				fireInvalidAttempt();
				return;
			}
			Shell workbenchShell = Display.getCurrent().getActiveShell();
			PromptBindingDialog pbd = new PromptBindingDialog(workbenchShell);
			pbd.setPlaceholders(placeholders);
			PromptBindingItem pbi = (PromptBindingItem) brandBinding
					.getBindingItem();
			if (pbi == null)
				pbi = new PromptBindingItem();
			else
				pbi = (PromptBindingItem) pbi.clone();
			pbd.setPromptBinding(pbi, mediaProviderManager.getMediaProvider(
					interactionType, currentBrand, currentLanguage));
			pbd.setMediaProvider(mediaProvider);
			pbd.setVariables(variables);
			int result = pbd.open();
			if (result == Window.OK) {
				pbi = pbd.getPromptBindingItem();
				brandBinding.setBindingItem(pbi.getEntries().size() < 1 ? null
						: pbi);
				setContents();
				fireChange();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.swt.events.MouseListener#mouseDown(org.eclipse.swt.events
	 * .MouseEvent)
	 */
	public void mouseDown(MouseEvent e) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.swt.events.MouseListener#mouseUp(org.eclipse.swt.events.
	 * MouseEvent)
	 */
	public void mouseUp(MouseEvent e) {
	}

	/**
	 * @param listener
	 */
	public void addListener(PromptBindingViewerListener listener) {
		listeners.remove(listener);
		listeners.add(listener);
	}

	/**
	 * @param listener
	 */
	public void removeListener(PromptBindingViewerListener listener) {
		listeners.remove(listener);
	}

	private void fireChange() {
		for (PromptBindingViewerListener listener : listeners) {
			listener.valueChanged(this);
		}
	}

	private void fireInvalidAttempt() {
		for (PromptBindingViewerListener listener : listeners) {
			listener.invalidConfigurationAttempt(this);
		}
	}

}
