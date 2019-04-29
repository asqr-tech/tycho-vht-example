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
package org.eclipse.vtp.desktop.media.voice.mediascreens;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.vtp.desktop.media.core.ContentPlaceholder;
import org.eclipse.vtp.desktop.media.core.MediaConfigurationScreen;
import org.eclipse.vtp.desktop.media.core.MediaConfigurationScreenContainer;
import org.eclipse.vtp.desktop.media.core.PromptBindingViewer;
import org.eclipse.vtp.desktop.media.core.PromptBindingViewerListener;
import org.eclipse.vtp.desktop.media.core.ValueControl;
import org.eclipse.vtp.desktop.media.core.ValueStack;
import org.eclipse.vtp.desktop.model.core.branding.IBrand;
import org.eclipse.vtp.desktop.model.interactive.core.configuration.generic.BrandBinding;
import org.eclipse.vtp.desktop.model.interactive.core.configuration.generic.GenericBindingManager;
import org.eclipse.vtp.desktop.model.interactive.core.configuration.generic.InteractionBinding;
import org.eclipse.vtp.desktop.model.interactive.core.configuration.generic.LanguageBinding;
import org.eclipse.vtp.desktop.model.interactive.core.configuration.generic.NamedBinding;

public class PlayPromptMediaScreen extends MediaConfigurationScreen implements PromptBindingViewerListener
{
	private static final String elementType = "org.eclipse.vtp.modules.interactive.playPrompt";
	PromptBindingViewer promptViewer;
	private FormToolkit toolkit;
	GenericBindingManager bindingManager;
	Combo barginCombo = null;
	Composite comp = null;
	ScrolledComposite sc = null;
	NamedBinding promptBinding = null;
	NamedBinding bargeinBinding = null;
	IBrand currentBrand = null;
	String currentLanguage = null;
	Map<String, ValueStack> valueStacks = new HashMap<String, ValueStack>();

	/**
	 * @param element
	 */
	public PlayPromptMediaScreen(MediaConfigurationScreenContainer container)
	{
		super(container);
		bindingManager = (GenericBindingManager)getElement().getConfigurationManager(GenericBindingManager.TYPE_ID);
		bindingManager.dumpContents(System.err);
		InteractionBinding interactionBinding = bindingManager.getInteractionBinding(getInteractionType());
		promptBinding = interactionBinding.getNamedBinding("Prompt");
		bargeinBinding = interactionBinding.getNamedBinding("barge-in");
		promptViewer = new PromptBindingViewer(getElement(), promptBinding, getInteractionType(), getElement().getDesign().getVariablesFor(getElement()));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.media.core.MediaConfigurationScreen#save()
	 */
	public void save()
	{
		for(ValueStack dvs : valueStacks.values())
		{
			dvs.save();
		}
		getElement().commitConfigurationChanges(bindingManager);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.media.core.MediaConfigurationScreen#cancel()
	 */
	public void cancel()
	{
		getElement().rollbackConfigurationChanges(bindingManager);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.media.core.MediaConfigurationScreen#createControls(org.eclipse.swt.widgets.Composite)
	 */
	public void createControls(Composite parent)
	{
		toolkit = new FormToolkit(parent.getDisplay());
		sc = new ScrolledComposite(parent, SWT.V_SCROLL);
		comp = new Composite(sc, SWT.NONE);
		sc.setContent(comp);
		comp.setBackground(parent.getBackground());
		comp.setLayout(new GridLayout(2, false));
		final Section contentSection =
			toolkit.createSection(comp, Section.TITLE_BAR);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL
						| GridData.VERTICAL_ALIGN_BEGINNING);
		gridData.horizontalSpan = 2;
		contentSection.setLayoutData(gridData);
		contentSection.setText("Media");

		@SuppressWarnings("unused")
		Label promptLabel = createPropertyLabel(comp, "Prompt:");
		Composite containerComp = createWrapperComposite(comp, 100);
		promptViewer.createControls(containerComp);
		List<ContentPlaceholder> placeholders = new ArrayList<ContentPlaceholder>();
		placeholders.add(new ContentPlaceholder("Test Placeholder", "This is the description kds jflj sfkl jlskfj lfkj sdlkfjsldkjf lksdj lfkjls"));
		promptViewer.setPlaceholders(placeholders);
		promptViewer.addListener(this);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_END);
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		promptViewer.getControl().setLayoutData(gridData);

		//spacer
		Composite spacerComp = new Composite(comp, SWT.NONE);
		spacerComp.setBackground(comp.getBackground());
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		gridData.heightHint = 15;
		spacerComp.setLayoutData(gridData);
		
		final Section settingsSection =
			toolkit.createSection(comp, Section.TITLE_BAR);
		gridData = new GridData(GridData.FILL_HORIZONTAL
						| GridData.VERTICAL_ALIGN_BEGINNING);
		gridData.horizontalSpan = 2;
		settingsSection.setLayoutData(gridData);
		settingsSection.setText("Settings");
		Label bargeLabel = createPropertyLabel(comp, "Barge-in Enabled");
		bargeLabel.setToolTipText("Determines whether the caller can\r\ninterrupt the prompt to begin entry");
		containerComp = createWrapperComposite(comp);
		ValueStack lastStack = new ValueStack("barge-in", getInteractionType(), elementType, "true", 0);//ValueStack.EXPRESSION | ValueStack.VARIABLE);
		lastStack.createControls(containerComp);
		containerComp = lastStack.getValueComposite();
		valueStacks.put("barge-in", lastStack);
		barginCombo = createValueDropDown(containerComp);
		barginCombo.add("true");
		barginCombo.add("false");
		barginCombo.select(0);
		lastStack.setValueControl(new ValueControl()
		{
			public String getValue()
            {
				return barginCombo.getItem(barginCombo.getSelectionIndex());
            }

			public void setValue(String value)
            {
				if(value == null)
					barginCombo.select(0);
				else if("true".equals(value))
					barginCombo.select(0);
				else if("false".equals(value))
					barginCombo.select(1);
				else
					barginCombo.select(0);
            }
		});
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);
		sc.setMinSize(comp.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		setControl(sc);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.media.core.MediaConfigurationScreen#getInteractionType()
	 */
	public String getInteractionType()
	{
		return "org.eclipse.vtp.framework.interactions.voice.interaction";
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.media.core.MediaConfigurationScreen#setBrand(org.eclipse.vtp.desktop.core.configuration.Brand)
	 */
	public void setBrand(IBrand brand)
	{
		currentBrand = brand;
		if(promptViewer != null)
			promptViewer.setCurrentBrand(brand);
		LanguageBinding languageBinding = bargeinBinding.getLanguageBinding("");
		BrandBinding brandBinding = languageBinding.getBrandBinding(currentBrand);
		valueStacks.get(bargeinBinding.getName()).setSetting(bindingManager.getMediaDefaults(), brandBinding);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.media.core.MediaConfigurationScreen#setLanguage(java.lang.String)
	 */
	public void setLanguage(String language)
	{
		currentLanguage = language;
		if(promptViewer != null)
			promptViewer.setCurrentLanguage(language);
	}
	
	/**
	 * @param parent
	 * @return
	 */
	public Composite createWrapperComposite(Composite parent)
	{
		return createWrapperComposite(parent, 20);
	}
	
	/**
	 * @param parent
	 * @param indent
	 * @return
	 */
	public Composite createWrapperComposite(Composite parent, int indent)
	{
		Composite containerComp = new Composite(parent, SWT.NONE);
		containerComp.setBackground(parent.getBackground());
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL | GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_BEGINNING);
		gridData.horizontalIndent = indent;
		gridData.widthHint = 150;
//		gridData.grabExcessVerticalSpace = true;
		GridLayout gl = new GridLayout(1, false);
		gl.horizontalSpacing = 0;
		gl.verticalSpacing = 0;
		gl.marginHeight = 0;
		gl.marginWidth = 0;
		containerComp.setLayout(gl);
		containerComp.setLayoutData(gridData);
		return containerComp;
	}

	/**
	 * @param parent
	 * @param text
	 * @return
	 */
	public Label createPropertyLabel(Composite parent, String text)
	{
		Label ret = new Label(parent, SWT.NONE);
		ret.setText(text);
		ret.setBackground(parent.getBackground());

		GridData gd = new GridData();
		gd.verticalAlignment = SWT.TOP;
		gd.verticalIndent = 4;
		gd.horizontalIndent = 30;
		ret.setLayoutData(gd);

		return ret;
	}

	/**
	 * @param parent
	 * @param dividerColor
	 * @return
	 */
	public RowDivider createRowDivider(Composite parent, Color dividerColor)
	{
		RowDivider rd1 = new RowDivider(parent, SWT.NONE);
		rd1.setBackground(parent.getBackground());
		rd1.setForeground(dividerColor);

		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.heightHint = 3;
		gd.horizontalIndent = 50;
		gd.horizontalSpan = 2;
		rd1.setLayoutData(gd);

		return rd1;
	}

	/**
	 * @param parent
	 * @return
	 */
	private Combo createValueDropDown(Composite parent)
	{
		Combo ret =
			new Combo(parent, SWT.BORDER | SWT.READ_ONLY | SWT.DROP_DOWN);
		GridData gd = new GridData();
		gd.verticalIndent = 2;
		gd.horizontalAlignment = SWT.RIGHT;
		gd.grabExcessHorizontalSpace = true;
		ret.setLayoutData(gd);

		return ret;
	}

	/**
	 * @param parent
	 * @param min
	 * @param max
	 * @param digits
	 * @param value
	 * @return
	 */
	public Spinner createValueSpinner(Composite parent, int min, int max,
		int digits, int value)
	{
		Spinner ret = new Spinner(parent, SWT.BORDER);
		ret.setMinimum(min);
		ret.setMaximum(max);
		ret.setDigits(digits);
		ret.setSelection(value);

		GridData gd = new GridData();
		gd.verticalIndent = 2;
		gd.horizontalAlignment = SWT.RIGHT;
		gd.grabExcessHorizontalSpace = true;
		ret.setLayoutData(gd);

		return ret;
	}

	public class RowDivider extends Canvas implements PaintListener
	{
		/**
		 * @param parent
		 * @param style
		 */
		public RowDivider(Composite parent, int style)
		{
			super(parent, style);
			this.addPaintListener(this);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.swt.events.PaintListener#paintControl(org.eclipse.swt.events.PaintEvent)
		 */
		public void paintControl(PaintEvent e)
		{
			Point size = getSize();
			e.gc.drawLine(30, 1, size.x - 30, 1);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.media.core.PromptBindingViewerListener#valueChanged(org.eclipse.vtp.desktop.media.core.PromptBindingViewer)
	 */
	public void valueChanged(PromptBindingViewer viewer)
    {
		Point preferred = comp.computeSize(sc.getMinWidth(), SWT.DEFAULT, true);
		sc.setMinSize(preferred);
		comp.layout();
		if(preferred.y > sc.getClientArea().height) //need to re-adjust because the scroll bar appeared
		{
			preferred = comp.computeSize(sc.getClientArea().width, SWT.DEFAULT, true);
			sc.setMinSize(preferred);
			comp.layout();
		}
    }

	public void invalidConfigurationAttempt(PromptBindingViewer viewer)
	{
		getContainer().cancelMediaConfiguration();
	}

}
