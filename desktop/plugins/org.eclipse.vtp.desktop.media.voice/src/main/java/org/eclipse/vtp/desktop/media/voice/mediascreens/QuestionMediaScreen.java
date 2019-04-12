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
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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
import org.eclipse.swt.widgets.Control;
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
import org.eclipse.vtp.desktop.media.core.ValueStackListener;
import org.eclipse.vtp.desktop.model.core.branding.IBrand;
import org.eclipse.vtp.desktop.model.core.design.IDesignElement;
import org.eclipse.vtp.desktop.model.interactive.core.configuration.generic.BrandBinding;
import org.eclipse.vtp.desktop.model.interactive.core.configuration.generic.GenericBindingManager;
import org.eclipse.vtp.desktop.model.interactive.core.configuration.generic.InteractionBinding;
import org.eclipse.vtp.desktop.model.interactive.core.configuration.generic.LanguageBinding;
import org.eclipse.vtp.desktop.model.interactive.core.configuration.generic.NamedBinding;

public class QuestionMediaScreen extends MediaConfigurationScreen implements PromptBindingViewerListener
{
	private static final String elementType = "org.eclipse.vtp.modules.interactive.question";
	PromptBindingViewer promptViewer;
	GrammarBindingViewer dtmfGrammarViewer;
	GrammarBindingViewer voiceGrammarViewer;
	private FormToolkit toolkit;
	GenericBindingManager bindingManager;
	InteractionBinding interactionBinding = null;
	List<Control> dtmfWidgets = new ArrayList<Control>();
	List<Control> voiceWidgets = new ArrayList<Control>();
	Composite comp = null;
	ScrolledComposite sc = null;
	Map<String, ValueStack> valueStacks = new HashMap<String, ValueStack>();
	String currentLanguage = null;
	IBrand currentBrand = null;
	
	/**
	 * @param element
	 */
	public QuestionMediaScreen(MediaConfigurationScreenContainer container)
	{
		super(container);
		IDesignElement element = getElement();
		bindingManager = (GenericBindingManager)element.getConfigurationManager(GenericBindingManager.TYPE_ID);
		interactionBinding = bindingManager.getInteractionBinding(getInteractionType());
		NamedBinding promptBinding = interactionBinding.getNamedBinding("Prompt");
		promptViewer = new PromptBindingViewer(getElement(), promptBinding, getInteractionType(), getElement().getDesign().getVariablesFor(element));
		NamedBinding dtmfGrammarBinding = interactionBinding.getNamedBinding("Grammar");
		dtmfGrammarViewer = new GrammarBindingViewer(element, dtmfGrammarBinding, getInteractionType());
		NamedBinding voiceGrammarBinding = interactionBinding.getNamedBinding("Voice-Grammar");
		voiceGrammarViewer = new GrammarBindingViewer(element, voiceGrammarBinding, getInteractionType());
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

		Label grammarLabel = createPropertyLabel(mediaComposite, "DTMF Grammar:");
		containerComp = createWrapperComposite(mediaComposite, 100);
		dtmfGrammarViewer.createControls(containerComp);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_END);
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		dtmfGrammarViewer.getControl().setLayoutData(gridData);
		dtmfWidgets.add(grammarLabel);
		dtmfWidgets.add(containerComp);

		Label voiceGrammarLabel = createPropertyLabel(mediaComposite, "Voice Grammar:");
		containerComp = createWrapperComposite(mediaComposite, 100);
		voiceGrammarViewer.createControls(containerComp);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_END);
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		voiceGrammarViewer.getControl().setLayoutData(gridData);
		voiceWidgets.add(voiceGrammarLabel);
		voiceWidgets.add(containerComp);

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

		Label inputModeLabel = createPropertyLabel(settingsComposite, "User Input Style");
		inputModeLabel.setToolTipText("This property selects the valid ways\r\n" +
									  "a caller can provide input:\r\n" + 
									  "\t*DTMF - Touchtone keypad only" +
									  "\t*Voice - Speech recognition only" +
									  "\t*Hybrid - Touchtone or speech accepted");
		containerComp = createWrapperComposite(settingsComposite);
		ValueStack lastStack = new ValueStack("input-mode", getInteractionType(), elementType, "Dtmf Only", 0);
		lastStack.createControls(containerComp);
		containerComp = lastStack.getValueComposite();
		valueStacks.put("input-mode", lastStack);
		final Combo inputModeCombo = createValueDropDown(containerComp);
		inputModeCombo.add("Dtmf Only");
		inputModeCombo.add("Voice Only");
		inputModeCombo.add("Hybrid");
		inputModeCombo.select(0);
		lastStack.setValueControl(new ValueControl()
		{
			public String getValue()
            {
				return inputModeCombo.getItem(inputModeCombo.getSelectionIndex());
            }

			public void setValue(String value)
            {
				if(value == null)
					inputModeCombo.select(0);
				else if("Voice Only".equals(value))
					inputModeCombo.select(1);
				else if("Hybrid".equals(value))
					inputModeCombo.select(2);
				else
					inputModeCombo.select(0);
				updateInputMode();
            }
		});
		lastStack.addListener(new ValueStackListener(){

			public void valueTypeChanged(ValueStack valueStack)
            {
				updateInputMode();
            }
			
		});
		inputModeCombo.addSelectionListener(new SelectionListener()
		{
			public void widgetDefaultSelected(SelectionEvent e)
			{
			}

			public void widgetSelected(SelectionEvent e)
			{
				updateInputMode();
			}
		});
		
		Label bargeLabel = createPropertyLabel(settingsComposite, "Barge-in Enabled");
		bargeLabel.setToolTipText("Determines whether the caller can\r\ninterrupt the prompt to begin entry");
		containerComp = createWrapperComposite(settingsComposite);
		lastStack = new ValueStack("barge-in", getInteractionType(), elementType, "true", 0);
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
		
		Label interdigitTimeoutLabel = createPropertyLabel(settingsComposite, "Interdigit Timeout (Seconds)");
		interdigitTimeoutLabel.setToolTipText("The amount of time in seconds to wait\r\n" +
				   "for additional touchtone if the current\r\n" +
				   "input does not match the grammar before\r\n" +
				   "a NoMatch event.");
		dtmfWidgets.add(interdigitTimeoutLabel);
		containerComp = createWrapperComposite(settingsComposite);
		dtmfWidgets.add(containerComp);
		lastStack = new ValueStack("interdigit-timeout", getInteractionType(), elementType, "2", 0);
		lastStack.createControls(containerComp);
		containerComp = lastStack.getValueComposite();
		valueStacks.put("interdigit-timeout", lastStack);
		final Spinner interdigitTimeoutSpinner = createValueSpinner(containerComp, 0, 100, 0, 0);
		lastStack.setValueControl(new ValueControl()
		{
			public String getValue()
            {
				return Integer.toString(interdigitTimeoutSpinner.getSelection());
            }

			public void setValue(String value)
            {
				interdigitTimeoutSpinner.setSelection(Integer.parseInt(value));
            }
		});

		Label terminationTimeoutLabel = createPropertyLabel(settingsComposite, "Termination Timeout (Seconds)");
		terminationTimeoutLabel.setToolTipText("The amount of time in seconds to wait\r\n" +
											   "for additional input after a selection\r\n" +
											   "has been matched.");
		dtmfWidgets.add(terminationTimeoutLabel);
		containerComp = createWrapperComposite(settingsComposite);
		dtmfWidgets.add(containerComp);
		lastStack = new ValueStack("termination-timeout", getInteractionType(), elementType, "1", 0);
		lastStack.createControls(containerComp);
		containerComp = lastStack.getValueComposite();
		valueStacks.put("termination-timeout", lastStack);
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

		Label terminationCharacterLabel = createPropertyLabel(settingsComposite, "Termination Character");
		terminationCharacterLabel.setToolTipText("An option touchtone key that can be used\r\n" +
												 "to indicate the caller has reached the end\r\n" +
												 "of their input.");
		dtmfWidgets.add(terminationCharacterLabel);
		containerComp = createWrapperComposite(settingsComposite);
		dtmfWidgets.add(containerComp);
		lastStack = new ValueStack("termination-character", getInteractionType(), elementType, "#", 0);
		lastStack.createControls(containerComp);
		containerComp = lastStack.getValueComposite();
		valueStacks.put("termination-character", lastStack);
		final Combo terminationCharacterCombo = createValueDropDown(containerComp);
		terminationCharacterCombo.add("#");
		terminationCharacterCombo.add("0");
		terminationCharacterCombo.add("1");
		terminationCharacterCombo.add("2");
		terminationCharacterCombo.add("3");
		terminationCharacterCombo.add("4");
		terminationCharacterCombo.add("5");
		terminationCharacterCombo.add("6");
		terminationCharacterCombo.add("7");
		terminationCharacterCombo.add("8");
		terminationCharacterCombo.add("9");
		terminationCharacterCombo.add("*");
		terminationCharacterCombo.add("None");
		terminationCharacterCombo.select(0);
		lastStack.setValueControl(new ValueControl()
		{
			public String getValue()
            {
				return terminationCharacterCombo.getItem(terminationCharacterCombo.getSelectionIndex());
            }

			public void setValue(String value)
            {
				boolean found = false;
				String[] items = terminationCharacterCombo.getItems();
				for(int i = 0; i < items.length; i++)
				{
					if(items[i].equals(value))
					{
						terminationCharacterCombo.select(i);
						found = true;
						break;
					}
				}
				if(!found)
					terminationCharacterCombo.select(0);
            }
		});

		Label speechIncompleteTimeoutLabel = createPropertyLabel(settingsComposite, "Speech Incomplete Timeout (Seconds)");
		speechIncompleteTimeoutLabel.setToolTipText("The amount of time in seconds to wait\r\n" +
													"for additional input if the current entry\r\n" + 
													"does not match the provided grammar.");
		voiceWidgets.add(speechIncompleteTimeoutLabel);
		containerComp = createWrapperComposite(settingsComposite);
		voiceWidgets.add(containerComp);
		lastStack = new ValueStack("speech-incomplete-timeout", getInteractionType(), elementType, "1", 0);
		lastStack.createControls(containerComp);
		containerComp = lastStack.getValueComposite();
		valueStacks.put("speech-incomplete-timeout", lastStack);
		final Spinner speechIncompleteTimeoutSpinner = createValueSpinner(containerComp, 0, 100, 0, 0);
		lastStack.setValueControl(new ValueControl()
		{
			public String getValue()
            {
				return Integer.toString(speechIncompleteTimeoutSpinner.getSelection());
            }

			public void setValue(String value)
            {
				speechIncompleteTimeoutSpinner.setSelection(Integer.parseInt(value));
            }
		});

		Label speechCompleteTimeoutLabel = createPropertyLabel(settingsComposite, "Speech Completion Timeout (Seconds)");
		speechCompleteTimeoutLabel.setToolTipText("The amount of time in seconds to wait\r\n" +
												  "for additional input if the current entry\r\n" + 
												  "already matches the provided grammar.");
		voiceWidgets.add(speechCompleteTimeoutLabel);
		containerComp = createWrapperComposite(settingsComposite);
		voiceWidgets.add(containerComp);
		lastStack = new ValueStack("speech-complete-timeout", getInteractionType(), elementType, "1", 0);
		lastStack.createControls(containerComp);
		containerComp = lastStack.getValueComposite();
		valueStacks.put("speech-complete-timeout", lastStack);
		final Spinner speechCompleteTimeoutSpinner = createValueSpinner(containerComp, 0, 100, 0, 0);
		lastStack.setValueControl(new ValueControl()
		{
			public String getValue()
            {
				return Integer.toString(speechCompleteTimeoutSpinner.getSelection());
            }

			public void setValue(String value)
            {
				speechCompleteTimeoutSpinner.setSelection(Integer.parseInt(value));
            }
		});

		Label maxSpeechTimeoutLabel = createPropertyLabel(settingsComposite, "Maximum Speech Length (Seconds)");
		maxSpeechTimeoutLabel.setToolTipText("The maximum length of speech input\r\n" +
											 "in seconds that will be accepted.");
		voiceWidgets.add(maxSpeechTimeoutLabel);
		containerComp = createWrapperComposite(settingsComposite);
		voiceWidgets.add(containerComp);
		lastStack = new ValueStack("max-speech-timeout", getInteractionType(), elementType, "300", 0);
		lastStack.createControls(containerComp);
		containerComp = lastStack.getValueComposite();
		valueStacks.put("max-speech-timeout", lastStack);
		final Spinner maxSpeechTimeoutSpinner = createValueSpinner(containerComp, 10, 300, 0, 0);
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

		Label confidenceLevelLabel = createPropertyLabel(settingsComposite, "Minimum Confidence Level Accepted");
		confidenceLevelLabel.setToolTipText("The minimum level of confidence accepted\r\n" +
											"by the speech recognition provider.");
		voiceWidgets.add(confidenceLevelLabel);
		containerComp = createWrapperComposite(settingsComposite);
		voiceWidgets.add(containerComp);
		lastStack = new ValueStack("confidence-level", getInteractionType(), elementType, "50", 0);
		lastStack.createControls(containerComp);
		containerComp = lastStack.getValueComposite();
		valueStacks.put("confidence-level", lastStack);
		final Spinner confidenceLevelSpinner = createValueSpinner(containerComp, 0, 100, 0, 0);
		lastStack.setValueControl(new ValueControl()
		{
			public String getValue()
            {
				return Integer.toString(confidenceLevelSpinner.getSelection());
            }

			public void setValue(String value)
            {
				confidenceLevelSpinner.setSelection(Integer.parseInt(value));
            }
		});

		Label sensitivityLabel = createPropertyLabel(settingsComposite, "Typical Caller Environment");
		sensitivityLabel.setToolTipText("Determines how sensitive the speech recognition\r\n" +
				"will be to background noise.  The lower the number,\r\n" +
				"the less senitive the system will be");
		voiceWidgets.add(sensitivityLabel);
		containerComp = createWrapperComposite(settingsComposite);
		voiceWidgets.add(containerComp);
		lastStack = new ValueStack("sensitivity-level", getInteractionType(), elementType, "50", 0);
		lastStack.createControls(containerComp);
		containerComp = lastStack.getValueComposite();
		valueStacks.put("sensitivity-level", lastStack);
		final Spinner sensitivitySpinner = createValueSpinner(containerComp, 0, 100, 0, 0);
		lastStack.setValueControl(new ValueControl()
		{
			public String getValue()
            {
				return Integer.toString(sensitivitySpinner.getSelection());
            }

			public void setValue(String value)
            {
				sensitivitySpinner.setSelection(Integer.parseInt(value));
            }
		});

		Label speedVsAccuracyLabel = createPropertyLabel(settingsComposite, "Speed Vs Accuracy");
		speedVsAccuracyLabel.setToolTipText("A hint to the speech recognition platform indicating\r\n" +
				"relative focus between the speed in which the result\r\n" +
				"is returned and the accuracy of that result.  The\r\n" +
				"lower the number, the faster the entry is processed\r\n" +
				"but accuracy may be reduced.");
		voiceWidgets.add(speedVsAccuracyLabel);
		containerComp = createWrapperComposite(settingsComposite);
		voiceWidgets.add(containerComp);
		lastStack = new ValueStack("speed-vs-accuracy", getInteractionType(), elementType, "50", 0);
		lastStack.createControls(containerComp);
		containerComp = lastStack.getValueComposite();
		valueStacks.put("speed-vs-accuracy", lastStack);
		final Spinner speedVsAccuracySpinner = createValueSpinner(containerComp, 0, 100, 0, 0);
		lastStack.setValueControl(new ValueControl()
		{
			public String getValue()
            {
				return Integer.toString(speedVsAccuracySpinner.getSelection());
            }

			public void setValue(String value)
            {
				speedVsAccuracySpinner.setSelection(Integer.parseInt(value));
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
		for(Control control : dtmfWidgets)
		{
			GridData gd = (GridData)control.getLayoutData();
			control.setVisible(false);
			gd.exclude = true;
		}
		for(Control control : voiceWidgets)
		{
			GridData gd = (GridData)control.getLayoutData();
			control.setVisible(false);
			gd.exclude = true;
		}
		int sel = inputModeCombo.getSelectionIndex();
		if(sel == 2 || sel == 0) //dtmf or hybrid
		{
			for(Control control : dtmfWidgets)
			{
				GridData gd = (GridData)control.getLayoutData();
				control.setVisible(true);
				gd.exclude = false;
			}
		}
		if(sel == 2 || sel == 1) //voice or hybrid
		{
			for(Control control : voiceWidgets)
			{
				GridData gd = (GridData)control.getLayoutData();
				control.setVisible(true);
				gd.exclude = false;
			}
		}
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
			promptViewer.setCurrentLanguage(language);
		if(dtmfGrammarViewer != null)
			dtmfGrammarViewer.setCurrentLanguage(language);
		if(voiceGrammarViewer != null)
			voiceGrammarViewer.setCurrentLanguage(language);
		if(currentBrand != null)
			setContents();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.media.core.MediaConfigurationScreen#setBrand(org.eclipse.vtp.desktop.core.configuration.Brand)
	 */
	public void setBrand(IBrand brand)
	{
		currentBrand = brand;
		if(promptViewer != null)
			promptViewer.setCurrentBrand(brand);
		if(dtmfGrammarViewer != null)
			dtmfGrammarViewer.setCurrentBrand(brand);
		if(voiceGrammarViewer != null)
			voiceGrammarViewer.setCurrentBrand(brand);

		if(currentLanguage != null)
			setContents();
	}
	
	public void setContents()
	{
		NamedBinding namedBinding = interactionBinding.getNamedBinding("input-mode");
		LanguageBinding languageBinding = namedBinding.getLanguageBinding("");
		BrandBinding brandBinding = languageBinding.getBrandBinding(currentBrand);
		ValueStack valueStack = this.valueStacks.get(namedBinding.getName());
		valueStack.setSetting(bindingManager.getMediaDefaults(), brandBinding);

		namedBinding = interactionBinding.getNamedBinding("barge-in");
		languageBinding = namedBinding.getLanguageBinding("");
		brandBinding = languageBinding.getBrandBinding(currentBrand);
		valueStack = this.valueStacks.get(namedBinding.getName());
		valueStack.setSetting(bindingManager.getMediaDefaults(), brandBinding);

		namedBinding = interactionBinding.getNamedBinding("initial-timeout");
		languageBinding = namedBinding.getLanguageBinding("");
		brandBinding = languageBinding.getBrandBinding(currentBrand);
		valueStack = this.valueStacks.get(namedBinding.getName());
		valueStack.setSetting(bindingManager.getMediaDefaults(), brandBinding);

		namedBinding = interactionBinding.getNamedBinding("interdigit-timeout");
		languageBinding = namedBinding.getLanguageBinding("");
		brandBinding = languageBinding.getBrandBinding(currentBrand);
		valueStack = this.valueStacks.get(namedBinding.getName());
		valueStack.setSetting(bindingManager.getMediaDefaults(), brandBinding);

		namedBinding = interactionBinding.getNamedBinding("termination-timeout");
		languageBinding = namedBinding.getLanguageBinding("");
		brandBinding = languageBinding.getBrandBinding(currentBrand);
		valueStack = this.valueStacks.get(namedBinding.getName());
		valueStack.setSetting(bindingManager.getMediaDefaults(), brandBinding);

		namedBinding = interactionBinding.getNamedBinding("termination-character");
		languageBinding = namedBinding.getLanguageBinding("");
		brandBinding = languageBinding.getBrandBinding(currentBrand);
		valueStack = this.valueStacks.get(namedBinding.getName());
		valueStack.setSetting(bindingManager.getMediaDefaults(), brandBinding);
		
		namedBinding = interactionBinding.getNamedBinding("speech-incomplete-timeout");
		languageBinding = namedBinding.getLanguageBinding("");
		brandBinding = languageBinding.getBrandBinding(currentBrand);
		valueStack = this.valueStacks.get(namedBinding.getName());
		valueStack.setSetting(bindingManager.getMediaDefaults(), brandBinding);

		namedBinding = interactionBinding.getNamedBinding("speech-complete-timeout");
		languageBinding = namedBinding.getLanguageBinding("");
		brandBinding = languageBinding.getBrandBinding(currentBrand);
		valueStack = this.valueStacks.get(namedBinding.getName());
		valueStack.setSetting(bindingManager.getMediaDefaults(), brandBinding);

		namedBinding = interactionBinding.getNamedBinding("max-speech-timeout");
		languageBinding = namedBinding.getLanguageBinding("");
		brandBinding = languageBinding.getBrandBinding(currentBrand);
		valueStack = this.valueStacks.get(namedBinding.getName());
		valueStack.setSetting(bindingManager.getMediaDefaults(), brandBinding);

		namedBinding = interactionBinding.getNamedBinding("confidence-level");
		languageBinding = namedBinding.getLanguageBinding("");
		brandBinding = languageBinding.getBrandBinding(currentBrand);
		valueStack = this.valueStacks.get(namedBinding.getName());
		valueStack.setSetting(bindingManager.getMediaDefaults(), brandBinding);

		namedBinding = interactionBinding.getNamedBinding("sensitivity-level");
		languageBinding = namedBinding.getLanguageBinding("");
		brandBinding = languageBinding.getBrandBinding(currentBrand);
		valueStack = this.valueStacks.get(namedBinding.getName());
		valueStack.setSetting(bindingManager.getMediaDefaults(), brandBinding);

		namedBinding = interactionBinding.getNamedBinding("speed-vs-accuracy");
		languageBinding = namedBinding.getLanguageBinding("");
		brandBinding = languageBinding.getBrandBinding(currentBrand);
		valueStack = this.valueStacks.get(namedBinding.getName());
		valueStack.setSetting(bindingManager.getMediaDefaults(), brandBinding);
	}

	public void updateInputMode()
	{
		//hide all
		for(Control control : dtmfWidgets)
        {
            GridData gd = (GridData)control.getLayoutData();
            control.setVisible(false);
            gd.exclude = true;
        }
		for(Control control : voiceWidgets)
        {
            GridData gd = (GridData)control.getLayoutData();
            control.setVisible(false);
            gd.exclude = true;
        }
		String sel = valueStacks.get("input-mode").getValue();
		if(sel.equals("Hybrid") || sel.equals("Dtmf Only")) //dtmf or hybrid
		{
			for(Control control : dtmfWidgets)
            {
                GridData gd = (GridData)control.getLayoutData();
                control.setVisible(true);
                gd.exclude = false;
            }
		}
		if(sel.equals("Hybrid") || sel.equals("Voice Only")) //voice or hybrid
		{
			for(Control control : voiceWidgets)
	        {
	            GridData gd = (GridData)control.getLayoutData();
	            control.setVisible(true);
	            gd.exclude = false;
	        }
		}
		Point preferred = comp.computeSize(sc.getMinWidth(), SWT.DEFAULT, true);
		sc.setMinSize(preferred);
		comp.layout();
		if(preferred.y > sc.getClientArea().height) //need to re-adjust because the scroll bar appeared
		{
			preferred = comp.computeSize(sc.getClientArea().width, SWT.DEFAULT, true);
			sc.setMinSize(preferred);
			comp.layout();
		}
        comp.getDisplay().asyncExec(new Runnable(){
        	public void run()
        	{
        		comp.layout(true, true);
        	}
        });
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
