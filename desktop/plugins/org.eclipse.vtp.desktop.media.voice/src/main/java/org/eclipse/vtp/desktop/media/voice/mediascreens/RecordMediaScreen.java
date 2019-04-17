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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.vtp.desktop.media.core.GrammarBindingViewer;
import org.eclipse.vtp.desktop.media.core.MediaConfigurationScreen;
import org.eclipse.vtp.desktop.media.core.MediaConfigurationScreenContainer;
import org.eclipse.vtp.desktop.media.core.PromptBindingViewer;
import org.eclipse.vtp.desktop.media.core.PromptBindingViewerListener;
import org.eclipse.vtp.desktop.media.core.ValueControl;
import org.eclipse.vtp.desktop.media.core.ValueStack;
import org.eclipse.vtp.desktop.model.core.branding.IBrand;
import org.eclipse.vtp.desktop.model.core.design.IDesignElement;
import org.eclipse.vtp.desktop.model.interactive.core.configuration.generic.BrandBinding;
import org.eclipse.vtp.desktop.model.interactive.core.configuration.generic.GenericBindingManager;
import org.eclipse.vtp.desktop.model.interactive.core.configuration.generic.InteractionBinding;
import org.eclipse.vtp.desktop.model.interactive.core.configuration.generic.LanguageBinding;
import org.eclipse.vtp.desktop.model.interactive.core.configuration.generic.NamedBinding;

public class RecordMediaScreen extends MediaConfigurationScreen implements PromptBindingViewerListener
{
	private static final String elementType = "org.eclipse.vtp.modules.interactive.record";
	PromptBindingViewer promptViewer;
	GrammarBindingViewer grammarViewer;
	private FormToolkit toolkit;
	GenericBindingManager bindingManager;
	InteractionBinding interactionBinding = null;
	Map<String, ValueStack> valueStacks = new HashMap<String, ValueStack>();
	Composite comp = null;
	ScrolledComposite sc = null;
	IBrand currentBrand = null;
	String currentLanguage = null;
	
	/**
	 * @param element
	 */
	public RecordMediaScreen(MediaConfigurationScreenContainer container)
	{
		super(container);
		IDesignElement element = getElement();
		bindingManager = (GenericBindingManager)element.getConfigurationManager(GenericBindingManager.TYPE_ID);
		interactionBinding = bindingManager.getInteractionBinding(getInteractionType());
		NamedBinding promptBinding = interactionBinding.getNamedBinding("Prompt");
		promptViewer = new PromptBindingViewer(element, promptBinding, getInteractionType(), getElement().getDesign().getVariablesFor(element));
		NamedBinding grammarBinding = interactionBinding.getNamedBinding("Grammar");
		grammarViewer = new GrammarBindingViewer(element, grammarBinding, getInteractionType());
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
		sc.getVerticalBar().setIncrement(30);
		sc.getVerticalBar().setPageIncrement(275);
		comp = new Composite(sc, SWT.NONE);
		sc.setContent(comp);
		comp.setBackground(parent.getBackground());
		comp.setLayout(new GridLayout(1, false));
		Composite mediaComposite = new Composite(comp, SWT.NONE);
		mediaComposite.setBackground(comp.getBackground());
		mediaComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		mediaComposite.setLayout(new GridLayout(2, false));
		final Section contentSection =
			toolkit.createSection(mediaComposite, Section.TITLE_BAR);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL
						| GridData.VERTICAL_ALIGN_BEGINNING);
		gridData.horizontalSpan = 2;
		contentSection.setLayoutData(gridData);
		contentSection.setText("Media");

		@SuppressWarnings("unused")
		Label promptLabel = createPropertyLabel(mediaComposite, "Prompt:");
		Composite containerComp = createWrapperComposite(mediaComposite, 100);
		promptViewer.createControls(containerComp);
		promptViewer.addListener(this);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_END);
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		promptViewer.getControl().setLayoutData(gridData);

		@SuppressWarnings("unused")
		Label grammarLabel = createPropertyLabel(mediaComposite, "Grammar:");
		Composite grammarComp = createWrapperComposite(mediaComposite, 100);
		grammarViewer.createControls(grammarComp);
//		grammarViewer.addListener(this);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_END);
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		grammarViewer.getControl().setLayoutData(gridData);

		//spacer
		Composite spacerComp = new Composite(mediaComposite, SWT.NONE);
		spacerComp.setBackground(mediaComposite.getBackground());
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		gridData.heightHint = 15;
		spacerComp.setLayoutData(gridData);
		
		Composite settingsComposite = new Composite(comp, SWT.NONE);
		settingsComposite.setBackground(comp.getBackground());
		settingsComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		settingsComposite.setLayout(new GridLayout(2, false));
		final Section settingsSection =
			toolkit.createSection(settingsComposite, Section.TITLE_BAR);
		gridData = new GridData(GridData.FILL_HORIZONTAL
						| GridData.VERTICAL_ALIGN_BEGINNING);
		gridData.horizontalSpan = 2;
		settingsSection.setLayoutData(gridData);
		settingsSection.setText("Settings");

		Label bargeLabel = createPropertyLabel(settingsComposite, "Barge-in Enabled");
		bargeLabel.setToolTipText("Determines whether the caller can\r\ninterrupt the prompt to begin entry.");
		containerComp = createWrapperComposite(settingsComposite);
		ValueStack lastStack = new ValueStack("barge-in", getInteractionType(), elementType, "true", 0);
		lastStack.createControls(containerComp);
		containerComp = lastStack.getValueComposite();
		valueStacks.put("barge-in", lastStack);
		final Combo barginCombo = createValueDropDown(containerComp);
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
		
		Label beepLabel = createPropertyLabel(settingsComposite, "Play Beep?");
		beepLabel.setToolTipText("Tells the voice platform whether to\r\nplay a beep before recording.");
		containerComp = createWrapperComposite(settingsComposite);
		lastStack = new ValueStack("play-beep", getInteractionType(), elementType, "true", 0);
		lastStack.createControls(containerComp);
		containerComp = lastStack.getValueComposite();
		valueStacks.put("play-beep", lastStack);
		final Combo beepCombo = createValueDropDown(containerComp);
		beepCombo.add("true");
		beepCombo.add("false");
		beepCombo.select(0);
		lastStack.setValueControl(new ValueControl()
		{
			public String getValue()
            {
				return beepCombo.getItem(beepCombo.getSelectionIndex());
            }

			public void setValue(String value)
            {
				if(value == null)
					beepCombo.select(0);
				else if("true".equals(value))
					beepCombo.select(0);
				else if("false".equals(value))
					beepCombo.select(1);
				else
					beepCombo.select(0);
            }
		});
		
		Label terminationCharacterLabel = createPropertyLabel(settingsComposite, "Allow DTMF Termination?");
		terminationCharacterLabel.setToolTipText("Determines whether recording is ended\r\nby DTMF input.");
		containerComp = createWrapperComposite(settingsComposite);
		lastStack = new ValueStack("dtmf-termination", getInteractionType(), elementType, "true", 0);
		lastStack.createControls(containerComp);
		containerComp = lastStack.getValueComposite();
		valueStacks.put("dtmf-termination", lastStack);
		final Combo terminationCharacterCombo = createValueDropDown(containerComp);
		terminationCharacterCombo.add("true");
		terminationCharacterCombo.add("false");
		terminationCharacterCombo.select(0);
		lastStack.setValueControl(new ValueControl()
		{
			public String getValue()
            {
				return terminationCharacterCombo.getItem(terminationCharacterCombo.getSelectionIndex());
            }

			public void setValue(String value)
            {
				if(value == null)
					terminationCharacterCombo.select(0);
				else if("true".equals(value))
					terminationCharacterCombo.select(0);
				else if("false".equals(value))
					terminationCharacterCombo.select(1);
				else
					terminationCharacterCombo.select(0);
            }
		});
		
		Label initialTimeoutLabel = createPropertyLabel(settingsComposite, "Initial Input Timeout (Seconds)");
		initialTimeoutLabel.setToolTipText("The amount of time in seconds to wait\r\n" +
										   "for the caller to begin input before\r\n" +
										   "a NoInput event.");
		containerComp = createWrapperComposite(settingsComposite);
		lastStack = new ValueStack("initial-timeout", getInteractionType(), elementType, "3", 0);
		lastStack.createControls(containerComp);
		containerComp = lastStack.getValueComposite();
		valueStacks.put("initial-timeout", lastStack);
		final Spinner initialTimeoutSpinner = createValueSpinner(containerComp, 0, 100, 0, 0);
		lastStack.setValueControl(new ValueControl()
		{
			public String getValue()
            {
				return Integer.toString(initialTimeoutSpinner.getSelection());
            }

			public void setValue(String value)
            {
				initialTimeoutSpinner.setSelection(Integer.parseInt(value));
            }
		});
		
		Label terminationTimeoutLabel = createPropertyLabel(settingsComposite, "Termination Timeout (Seconds)");
		terminationTimeoutLabel.setToolTipText("The amount of time in seconds to wait\r\n" +
											   "for additional input after a selection\r\n" +
											   "has been matched.");
		containerComp = createWrapperComposite(settingsComposite);
		lastStack = new ValueStack("final-silence-timeout", getInteractionType(), elementType, "1", 0);
		lastStack.createControls(containerComp);
		containerComp = lastStack.getValueComposite();
		valueStacks.put("final-silence-timeout", lastStack);
		final Spinner terminationTimeoutSpinner = createValueSpinner(containerComp, 0, 100, 0, 0);
		lastStack.setValueControl(new ValueControl()
		{
			public String getValue()
            {
				return Integer.toString(terminationTimeoutSpinner.getSelection());
            }

			public void setValue(String value)
            {
				terminationTimeoutSpinner.setSelection(Integer.parseInt(value));
            }
		});

		Label maxSpeechTimeoutLabel = createPropertyLabel(settingsComposite, "Maximum Recording Time (Seconds)");
		maxSpeechTimeoutLabel.setToolTipText("The maximum length of speech input\r\n" +
											 "in seconds that will be accepted.");
		containerComp = createWrapperComposite(settingsComposite);
		lastStack = new ValueStack("max-record-time", getInteractionType(), elementType, "300", 0);
		lastStack.createControls(containerComp);
		containerComp = lastStack.getValueComposite();
		valueStacks.put("max-record-time", lastStack);
		final Spinner maxSpeechTimeoutSpinner = createValueSpinner(containerComp, 10, 1740, 0, 0);
		lastStack.setValueControl(new ValueControl()
		{
			public String getValue()
            {
				return Integer.toString(maxSpeechTimeoutSpinner.getSelection());
            }

			public void setValue(String value)
            {
				maxSpeechTimeoutSpinner.setSelection(Integer.parseInt(value));
            }
		});

		comp.addControlListener(new ControlListener(){

			public void controlMoved(ControlEvent e)
            {
            }

			public void controlResized(ControlEvent e)
            {
	            comp.removeControlListener(this);
	            sc.setAlwaysShowScrollBars(true);
	            Rectangle clientArea = sc.getClientArea();
	            sc.setAlwaysShowScrollBars(false);
	            if(sc.getVerticalBar() != null)
	            	sc.getVerticalBar().setVisible(false);
				sc.setMinSize(new Point(clientArea.width, clientArea.height));
				sc.layout(true, true);
				sc.redraw();
            }
			
		});
		sc.setMinWidth(comp.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
		sc.setMinHeight(comp.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).y);
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);
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
	 * @see org.eclipse.vtp.desktop.media.core.MediaConfigurationScreen#setLanguage(java.lang.String)
	 */
	public void setLanguage(String language)
	{
		currentLanguage = language;
		if(promptViewer != null)
		{
			promptViewer.setCurrentLanguage(language);
			grammarViewer.setCurrentLanguage(language);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.media.core.MediaConfigurationScreen#setBrand(org.eclipse.vtp.desktop.core.configuration.Brand)
	 */
	public void setBrand(IBrand brand)
	{
		currentBrand = brand;
		if(promptViewer != null)
		{
			promptViewer.setCurrentBrand(brand);
			grammarViewer.setCurrentBrand(brand);
		}

		NamedBinding namedBinding = interactionBinding.getNamedBinding("barge-in");
		LanguageBinding languageBinding = namedBinding.getLanguageBinding("");
		BrandBinding brandBinding = languageBinding.getBrandBinding(currentBrand);
		ValueStack valueStack = this.valueStacks.get(namedBinding.getName());
		valueStack.setSetting(bindingManager.getMediaDefaults(), brandBinding);

		namedBinding = interactionBinding.getNamedBinding("play-beep");
		languageBinding = namedBinding.getLanguageBinding("");
		brandBinding = languageBinding.getBrandBinding(currentBrand);
		valueStack = this.valueStacks.get(namedBinding.getName());
		valueStack.setSetting(bindingManager.getMediaDefaults(), brandBinding);
		
		namedBinding = interactionBinding.getNamedBinding("initial-timeout");
		languageBinding = namedBinding.getLanguageBinding("");
		brandBinding = languageBinding.getBrandBinding(currentBrand);
		valueStack = this.valueStacks.get(namedBinding.getName());
		valueStack.setSetting(bindingManager.getMediaDefaults(), brandBinding);

		namedBinding = interactionBinding.getNamedBinding("final-silence-timeout");
		languageBinding = namedBinding.getLanguageBinding("");
		brandBinding = languageBinding.getBrandBinding(currentBrand);
		valueStack = this.valueStacks.get(namedBinding.getName());
		valueStack.setSetting(bindingManager.getMediaDefaults(), brandBinding);

		namedBinding = interactionBinding.getNamedBinding("dtmf-termination");
		languageBinding = namedBinding.getLanguageBinding("");
		brandBinding = languageBinding.getBrandBinding(currentBrand);
		valueStack = this.valueStacks.get(namedBinding.getName());
		valueStack.setSetting(bindingManager.getMediaDefaults(), brandBinding);
		
		namedBinding = interactionBinding.getNamedBinding("max-record-time");
		languageBinding = namedBinding.getLanguageBinding("");
		brandBinding = languageBinding.getBrandBinding(currentBrand);
		valueStack = this.valueStacks.get(namedBinding.getName());
		valueStack.setSetting(bindingManager.getMediaDefaults(), brandBinding);

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
		Spinner ret = new Spinner(parent, SWT.NONE);
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

	/**
	 * @param parent
	 * @param min
	 * @param max
	 * @param leftName
	 * @param rightName
	 * @return
	 */
	public Slider createValueSlider(Composite parent, int min, int max,
			String leftName, String rightName)
	{
		Composite sliderComp = new Composite(parent, SWT.NONE);
		sliderComp.setBackground(parent.getBackground());
		GridData gd = new GridData();
		gd.verticalIndent = 2;
		gd.horizontalAlignment = SWT.RIGHT;
		gd.grabExcessHorizontalSpace = true;
		sliderComp.setLayoutData(gd);
		sliderComp.setLayout(new FormLayout());
		
		Label leftLabel = new Label(sliderComp, SWT.NONE);
		leftLabel.setBackground(sliderComp.getBackground());
		leftLabel.setText(leftName);
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, 0);
		fd.bottom = new FormAttachment(100, 0);
		leftLabel.setLayoutData(fd);
		
		Label rightLabel = new Label(sliderComp, SWT.NONE);
		rightLabel.setBackground(sliderComp.getBackground());
		rightLabel.setText(rightName);
		fd = new FormData();
		fd.right = new FormAttachment(100, 0);
		fd.bottom = new FormAttachment(100, 0);
		rightLabel.setLayoutData(fd);
		
		Slider ret = new Slider(sliderComp, SWT.HORIZONTAL);
		ret.setMinimum(min);
		ret.setMaximum(max);
		fd = new FormData();
		fd.left = new FormAttachment(leftLabel, /*leftLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT).x / 2*/0, SWT.LEFT);
		fd.right = new FormAttachment(rightLabel, /*-1 * (rightLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT).x / 2)*/0, SWT.RIGHT);
		fd.top = new FormAttachment(0, 0);
		fd.bottom = new FormAttachment(leftLabel, -3);
		fd.width = 150;
		ret.setLayoutData(fd);

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
