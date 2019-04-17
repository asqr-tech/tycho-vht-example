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

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
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
import org.eclipse.vtp.desktop.model.elements.core.internal.PrimitiveElement;
import org.eclipse.vtp.desktop.model.interactive.core.configuration.generic.BrandBinding;
import org.eclipse.vtp.desktop.model.interactive.core.configuration.generic.GenericBindingManager;
import org.eclipse.vtp.desktop.model.interactive.core.configuration.generic.InteractionBinding;
import org.eclipse.vtp.desktop.model.interactive.core.configuration.generic.LanguageBinding;
import org.eclipse.vtp.desktop.model.interactive.core.configuration.generic.NamedBinding;
import org.eclipse.vtp.desktop.model.interactive.core.configuration.generic.PropertyBindingItem;
import org.eclipse.vtp.desktop.model.interactive.core.internal.MenuChoice;
import org.eclipse.vtp.modules.interactive.ui.OptionSetInformationProvider;
import org.eclipse.vtp.modules.interactive.ui.dialogs.MenuOptionScriptDialog;
import org.eclipse.vtp.modules.interactive.ui.properties.MenuChoiceBindingManager;

public class OptionSetMediaScreen extends MediaConfigurationScreen implements PromptBindingViewerListener
{
	private static final String elementType = "org.eclipse.vtp.modules.interactive.optionSet";
	private FormToolkit toolkit;
	MenuChoiceBindingManager mcBindingManager;
	GenericBindingManager genericManager;
	InteractionBinding interactionBinding = null;
	OptionSetInformationProvider info = null;
	Section contentSection = null;
	Composite comp = null;
	Composite mediaComposite = null;
	ScrolledComposite sc = null;
	Control addOptionComposite;
	Font boldFont = null;
	Color dividerColor;
	List<Control> dtmfWidgets = new ArrayList<Control>();
	List<Control> voiceWidgets = new ArrayList<Control>();
	Map<MenuChoice, ToolItem> moveUpButtons = new HashMap<MenuChoice, ToolItem>();
	Map<MenuChoice, ToolItem> moveDownButtons = new HashMap<MenuChoice, ToolItem>();
	Map<MenuChoice, List<Control>> optionControlMap = new HashMap<MenuChoice, List<Control>>();
	Map<MenuChoice, PromptBindingViewer> optionPromptMap = new HashMap<MenuChoice, PromptBindingViewer>();
	Map<MenuChoice, GrammarBindingViewer> optionGrammarMap = new HashMap<MenuChoice, GrammarBindingViewer>();
	MouseListener focusMaster = new MouseListener()
	{
		public void mouseDoubleClick(MouseEvent e)
		{
		}

		public void mouseDown(MouseEvent e)
		{
			comp.forceFocus();
		}

		public void mouseUp(MouseEvent e)
		{
		}
	};
	Map<String, ValueStack> valueStacks = new HashMap<String, ValueStack>();
	IBrand currentBrand = null;
	String currentLanguage = null;
	
	/**
	 * @param container
	 */
	public OptionSetMediaScreen(MediaConfigurationScreenContainer container)
	{
		super(container);
		mcBindingManager = (MenuChoiceBindingManager)getElement().getConfigurationManager(MenuChoiceBindingManager.TYPE_ID);
		genericManager = (GenericBindingManager)getElement().getConfigurationManager(GenericBindingManager.TYPE_ID);
		interactionBinding = genericManager.getInteractionBinding(getInteractionType());
		info = (OptionSetInformationProvider)((PrimitiveElement)container.getDesignElement()).getInformationProvider();
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
		getElement().commitConfigurationChanges(genericManager);
		getElement().commitConfigurationChanges(mcBindingManager);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.media.core.MediaConfigurationScreen#cancel()
	 */
	public void cancel()
	{
		getElement().rollbackConfigurationChanges(genericManager);
		getElement().rollbackConfigurationChanges(mcBindingManager);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.media.core.MediaConfigurationScreen#createControls(org.eclipse.swt.widgets.Composite)
	 */
	public void createControls(Composite parent)
	{
		boldFont = new Font(parent.getDisplay(),
				parent.getFont().getFontData()[0].getName(),
				parent.getFont().getFontData()[0].getHeight(), SWT.BOLD);
		toolkit = new FormToolkit(parent.getDisplay());
		sc = new ScrolledComposite(parent, SWT.V_SCROLL);
		sc.getVerticalBar().setIncrement(30);
		sc.getVerticalBar().setPageIncrement(275);
		comp = new Composite(sc, SWT.NONE);
		sc.setContent(comp);
		comp.addMouseListener(focusMaster);
		comp.setBackground(parent.getBackground());
		comp.setLayout(new GridLayout(1, false));
		mediaComposite = new Composite(comp, SWT.NONE);
		mediaComposite.setBackground(comp.getBackground());
		mediaComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.verticalSpacing = 1;
		mediaComposite.setLayout(gridLayout);
		contentSection = toolkit.createSection(mediaComposite, Section.TITLE_BAR);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL
						| GridData.VERTICAL_ALIGN_BEGINNING);
		gridData.horizontalSpan = 2;
		contentSection.setLayoutData(gridData);
		contentSection.setText("Options");
		
		addOptionComposite = createPropertyLabel(mediaComposite, "");
		Composite containerComp = createWrapperComposite(mediaComposite);
		final Button addOptionButton = new Button(containerComp, SWT.PUSH);
		addOptionButton.setText("Add Option");
		addOptionButton.setLayoutData(new GridData());
		addOptionButton.addSelectionListener(new SelectionListener()
		{
			public void widgetDefaultSelected(SelectionEvent e)
			{
			}

			public void widgetSelected(SelectionEvent e)
			{
				NewMenuChoiceWizard nmo =
					new NewMenuChoiceWizard(currentBrand, info, mcBindingManager);
				WizardDialog wd =
					new WizardDialog(Display.getCurrent()
											.getActiveShell(), nmo);

				if(wd.open() == WizardDialog.OK)
				{
					setBrand(currentBrand);
					setLanguage(currentLanguage);
				}
			}
		});
		
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
		bargeLabel.setToolTipText("Determines whether the caller can\r\n" +
								  "interrupt the prompt to begin entry");
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
		lastStack = new ValueStack("termination-timeout", getInteractionType(), elementType, "3", 0);
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

		Label confidenceLevelLabel = createPropertyLabel(settingsComposite, "Confidence Level Accepted");
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

		Label sensitivityLabel = createPropertyLabel(settingsComposite, "Caller Environment");
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
		sc.getDisplay().asyncExec(new Runnable(){
			public void run()
			{
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
		});
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
		if(currentBrand != null)
			updateValues();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.media.core.MediaConfigurationScreen#setBrand(org.eclipse.vtp.desktop.core.configuration.Brand)
	 */
	public void setBrand(IBrand brand)
	{
		currentBrand = brand;
		if(currentLanguage != null)
			updateValues();
	}

	private void updateValues()
    {
		try
        {
	        for(List<Control> controls : optionControlMap.values())
	        {
	        	for(Control control : controls)
	        	{
	        		control.dispose();
	        		dtmfWidgets.remove(control);
	        		voiceWidgets.remove(control);
	        	}
	        }
	        moveUpButtons = new HashMap<MenuChoice, ToolItem>();
	        moveDownButtons = new HashMap<MenuChoice, ToolItem>();
	        optionControlMap = new HashMap<MenuChoice, List<Control>>();
	        optionPromptMap = new HashMap<MenuChoice, PromptBindingViewer>();
	        optionGrammarMap = new HashMap<MenuChoice, GrammarBindingViewer>();
	        List<MenuChoice> options = mcBindingManager.getChoicesByBrand(currentBrand);
	        for(int i = 0; i < options.size(); i++)
	        {
	        	MenuChoice mc = options.get(i);
	        	List<Control> createOptionControls = createOptionControls(mediaComposite, mc);
	        	for(Control control : createOptionControls)
	        	{
	        		control.moveAbove(addOptionComposite);
	        	}
	        	optionControlMap.put(mc, createOptionControls);
	        }
	        for(PromptBindingViewer viewer : optionPromptMap.values())
	        {
	        	viewer.setCurrentBrand(currentBrand);
	        	viewer.setCurrentLanguage(currentLanguage);
	        }
	        for(GrammarBindingViewer viewer : optionGrammarMap.values())
	        {
	        	viewer.setCurrentBrand(currentBrand);
	        	viewer.setCurrentLanguage(currentLanguage);
	        }
			NamedBinding namedBinding = interactionBinding.getNamedBinding("input-mode");
			LanguageBinding languageBinding = namedBinding.getLanguageBinding("");
			BrandBinding brandBinding = languageBinding.getBrandBinding(currentBrand);
			ValueStack valueStack = this.valueStacks.get(namedBinding.getName());
			valueStack.setSetting(genericManager.getMediaDefaults(), brandBinding);

			namedBinding = interactionBinding.getNamedBinding("barge-in");
			languageBinding = namedBinding.getLanguageBinding("");
			brandBinding = languageBinding.getBrandBinding(currentBrand);
			valueStack = this.valueStacks.get(namedBinding.getName());
			valueStack.setSetting(genericManager.getMediaDefaults(), brandBinding);

			namedBinding = interactionBinding.getNamedBinding("initial-timeout");
			languageBinding = namedBinding.getLanguageBinding("");
			brandBinding = languageBinding.getBrandBinding(currentBrand);
			valueStack = this.valueStacks.get(namedBinding.getName());
			valueStack.setSetting(genericManager.getMediaDefaults(), brandBinding);

			namedBinding = interactionBinding.getNamedBinding("interdigit-timeout");
			languageBinding = namedBinding.getLanguageBinding("");
			brandBinding = languageBinding.getBrandBinding(currentBrand);
			valueStack = this.valueStacks.get(namedBinding.getName());
			valueStack.setSetting(genericManager.getMediaDefaults(), brandBinding);

			namedBinding = interactionBinding.getNamedBinding("termination-timeout");
			languageBinding = namedBinding.getLanguageBinding("");
			brandBinding = languageBinding.getBrandBinding(currentBrand);
			valueStack = this.valueStacks.get(namedBinding.getName());
			valueStack.setSetting(genericManager.getMediaDefaults(), brandBinding);

			namedBinding = interactionBinding.getNamedBinding("speech-incomplete-timeout");
			languageBinding = namedBinding.getLanguageBinding("");
			brandBinding = languageBinding.getBrandBinding(currentBrand);
			valueStack = this.valueStacks.get(namedBinding.getName());
			valueStack.setSetting(genericManager.getMediaDefaults(), brandBinding);

			namedBinding = interactionBinding.getNamedBinding("speech-complete-timeout");
			languageBinding = namedBinding.getLanguageBinding("");
			brandBinding = languageBinding.getBrandBinding(currentBrand);
			valueStack = this.valueStacks.get(namedBinding.getName());
			valueStack.setSetting(genericManager.getMediaDefaults(), brandBinding);

			namedBinding = interactionBinding.getNamedBinding("max-speech-timeout");
			languageBinding = namedBinding.getLanguageBinding("");
			brandBinding = languageBinding.getBrandBinding(currentBrand);
			valueStack = this.valueStacks.get(namedBinding.getName());
			valueStack.setSetting(genericManager.getMediaDefaults(), brandBinding);

			namedBinding = interactionBinding.getNamedBinding("confidence-level");
			languageBinding = namedBinding.getLanguageBinding("");
			brandBinding = languageBinding.getBrandBinding(currentBrand);
			valueStack = this.valueStacks.get(namedBinding.getName());
			valueStack.setSetting(genericManager.getMediaDefaults(), brandBinding);

			namedBinding = interactionBinding.getNamedBinding("sensitivity-level");
			languageBinding = namedBinding.getLanguageBinding("");
			brandBinding = languageBinding.getBrandBinding(currentBrand);
			valueStack = this.valueStacks.get(namedBinding.getName());
			valueStack.setSetting(genericManager.getMediaDefaults(), brandBinding);

			namedBinding = interactionBinding.getNamedBinding("speed-vs-accuracy");
			languageBinding = namedBinding.getLanguageBinding("");
			brandBinding = languageBinding.getBrandBinding(currentBrand);
			valueStack = this.valueStacks.get(namedBinding.getName());
			valueStack.setSetting(genericManager.getMediaDefaults(), brandBinding);
	        updateButtons();
        }
        catch(Exception e)
        {
	        e.printStackTrace();
        }
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
		containerComp.addMouseListener(focusMaster);
		containerComp.setBackground(parent.getBackground());
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL | GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_BEGINNING);
		gridData.horizontalIndent = indent;
		gridData.widthHint = 175;
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
	public Label createOptionLabel(Composite parent, final MenuChoice option)
	{
		Label ret = new Label(parent, SWT.NONE);
		
		ret.addMouseListener(new MouseListener()
		{
			public void mouseDoubleClick(MouseEvent e)
			{
				String oldName = option.getOptionName();
				RenameMenuChoiceWizard rmo = new RenameMenuChoiceWizard(currentBrand, option, mcBindingManager);
				WizardDialog wd = new WizardDialog(Display.getCurrent().getActiveShell(), rmo);
				if(wd.open() == WizardDialog.OK)
				{
					setBrand(currentBrand);
					setLanguage(currentLanguage);
				}

				if(!oldName.equals(option.getOptionName()))
				{
					info.updateChoice(option, oldName);
					String bindingTypes[] = {"-dtmf","-grammar","-prompt","-silent"};
					for(int b=0; b < bindingTypes.length; b++)
					{
						genericManager.renameNamedBinding(getInteractionType(), oldName + bindingTypes[b], option.getOptionName() + bindingTypes[b]);
						updateValues();
					}
				}
			}

			public void mouseDown(MouseEvent e)
			{
				comp.forceFocus();
			}

			public void mouseUp(MouseEvent e)
			{
			}
		});
		
		ret.setText(option.getOptionName());
		ret.setBackground(parent.getBackground());

		GridData gd = new GridData();
		gd.verticalAlignment = SWT.BOTTOM;
		gd.verticalIndent = 4;
		gd.horizontalIndent = 10;
		gd.minimumHeight = 21;
		gd.heightHint = 21;
		ret.setLayoutData(gd);

		return ret;
	}

	/**
	 * @param parent
	 * @param text
	 * @return
	 */
	public Label createPropertyLabel(Composite parent, String text)
	{
		Label ret = new Label(parent, SWT.NONE);
		ret.addMouseListener(focusMaster);
		ret.setText(text);
		ret.setBackground(parent.getBackground());

		GridData gd = new GridData();
		gd.verticalAlignment = SWT.BOTTOM;
		gd.verticalIndent = 4;
		gd.horizontalIndent = 30;
		gd.minimumHeight = 21;
		gd.heightHint = 21;
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
		gd.horizontalIndent = 20;
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
		gd.verticalIndent = 1;
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
		gd.verticalIndent = 1;
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
			this.addMouseListener(focusMaster);
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
	
	/**
	 * @param canvas
	 * @param option
	 * @return
	 */
	public List<Control> createOptionControls(final Composite canvas, final MenuChoice option)
	{
		List<Control> controlList = new ArrayList<Control>();
		Label option1Label = createOptionLabel(canvas, option);
		option1Label.setFont(boldFont);
		controlList.add(option1Label);

		Composite optHeaderComp = new Composite(canvas, SWT.NONE);
		FillLayout fl = new FillLayout();
		optHeaderComp.setLayout(fl);
		optHeaderComp.setBackground(canvas.getBackground());

		if(moveUpButtons == null)
		{
			moveUpButtons = new HashMap<MenuChoice, ToolItem>();
			moveDownButtons = new HashMap<MenuChoice, ToolItem>();
		}

		ToolBar tb = new ToolBar(optHeaderComp, SWT.FLAT);
		tb.setBackground(optHeaderComp.getBackground());

		ToolItem guardScriptButton = new ToolItem(tb, SWT.PUSH);
		if(!("".equals(option.getScriptText()) && option.getScriptText() != null))
		{
			guardScriptButton.setImage(org.eclipse.vtp.desktop.core.Activator.getDefault()
					.getImageRegistry()
					.get("ICON_SCRIPT"));
		}
		else
		{
			guardScriptButton.setImage(org.eclipse.vtp.desktop.core.Activator.getDefault()
					.getImageRegistry()
					.get("ICON_NOSCRIPT"));
		}

		guardScriptButton.setToolTipText("Edit Guard Condition");
		guardScriptButton.addSelectionListener(new SelectionListener()
			{
				public void widgetSelected(SelectionEvent e)
				{
					try
                    {
	                    MenuOptionScriptDialog mosd = new MenuOptionScriptDialog(Display.getCurrent()
	                    						.getActiveShell());
	                    mosd.setMenuChoice(option);
	                    mosd.open();
	                    updateValues();
                    }
                    catch(RuntimeException e1)
                    {
	                    e1.printStackTrace();
                    }
				}

				public void widgetDefaultSelected(SelectionEvent e)
				{
				}
			});

		ToolItem moveUpButton = new ToolItem(tb, SWT.PUSH);
		moveUpButton.setImage(org.eclipse.vtp.desktop.core.Activator.getDefault()
												 .getImageRegistry()
												 .get("ICON_MOVE_UP"));
		moveUpButton.setToolTipText("Move Option Up");
		moveUpButtons.put(option, moveUpButton);
		moveUpButton.addSelectionListener(new SelectionListener()
			{
				public void widgetSelected(SelectionEvent e)
				{
					List<MenuChoice> currentOptions = mcBindingManager.getChoicesByBrand(currentBrand);
					MenuChoice targetOption = null;

					for(int i = 0; i < currentOptions.size(); i++)
					{
						if(currentOptions.get(i) == option)
						{
							if(i != 0)
							{
								targetOption = currentOptions.get(i - 1);
							}
						}
					}

					if(targetOption != null)
					{
						List<Control> targetControlList =
							optionControlMap.get(targetOption);
						List<Control> mobileControlList =
							optionControlMap.get(option);
						for(int i = 0; i < mobileControlList.size(); i++)
						{
							mobileControlList.get(i).moveAbove(targetControlList
								.get(0));
						}

						mcBindingManager.moveChoiceUp(currentBrand.getName(), option);
						updateButtons();
						canvas.layout(true, true);
					}
				}

				public void widgetDefaultSelected(SelectionEvent e)
				{
				}
			});

		ToolItem moveDownButton = new ToolItem(tb, SWT.PUSH);
		moveDownButton.setImage(org.eclipse.vtp.desktop.core.Activator.getDefault()
												   .getImageRegistry()
												   .get("ICON_MOVE_DOWN"));
		moveDownButton.setToolTipText("Move Option Down");
		moveDownButtons.put(option, moveDownButton);
		moveDownButton.addSelectionListener(new SelectionListener()
			{
				public void widgetSelected(SelectionEvent e)
				{
					List<MenuChoice> currentOptions = mcBindingManager.getChoicesByBrand(currentBrand);
					MenuChoice targetOption = option;
					MenuChoice mobileOption = null;

					for(int i = 0; i < currentOptions.size(); i++)
					{
						if(currentOptions.get(i) == option)
						{
							if(i < (currentOptions.size() - 1))
							{
								mobileOption = currentOptions.get(i + 1);
							}
						}
					}

					if(mobileOption != null)
					{
						List<Control> targetControlList =
							optionControlMap.get(targetOption);
						List<Control> mobileControlList =
							optionControlMap.get(mobileOption);
						for(int i = 0; i < mobileControlList.size(); i++)
						{
							mobileControlList.get(i).moveAbove(targetControlList
								.get(0));
						}

						mcBindingManager.moveChoiceDown(currentBrand.getName(), option);
						updateButtons();
						canvas.layout(true, true);
					}
				}

				public void widgetDefaultSelected(SelectionEvent e)
				{
				}
			});

		ToolItem deleteButton = new ToolItem(tb, SWT.PUSH);
		deleteButton.setImage(PlatformUI.getWorkbench().getSharedImages()
										.getImage(ISharedImages.IMG_TOOL_DELETE));
		deleteButton.setToolTipText("Remove Option");
		deleteButton.addSelectionListener(new SelectionListener()
			{
				public void widgetSelected(SelectionEvent e)
				{
					List<Control> controlList = optionControlMap.get(option);
					moveUpButtons.remove(option);
					moveDownButtons.remove(option);
					optionPromptMap.remove(option);
					optionGrammarMap.remove(option);

					for(Control control : controlList)
					{
						control.dispose();
						dtmfWidgets.remove(control);
						voiceWidgets.remove(control);
					}

					optionControlMap.remove(option);
					info.removeChoice(currentBrand.getName(), option, mcBindingManager.removeChoice(currentBrand.getName(), option));
					updateButtons();
					canvas.layout(true, true);
					comp.layout();
				}

				public void widgetDefaultSelected(SelectionEvent e)
				{
				}
			});

		GridData gd = new GridData();
		gd.horizontalAlignment = SWT.RIGHT;
		optHeaderComp.setLayoutData(gd);
		controlList.add(optHeaderComp);

		dividerColor = new Color(Display.getCurrent(), 231, 233, 240);
		comp.addDisposeListener(new DisposeListener()
			{
				public void widgetDisposed(DisposeEvent e)
				{
					dividerColor.dispose();
					boldFont.dispose();
				}
			});
		RowDivider ord1 = createRowDivider(canvas, dividerColor);
		controlList.add(ord1);

		Label optionPromptLabel = createPropertyLabel(canvas, "Prompt");
		controlList.add(optionPromptLabel);

		NamedBinding promptBinding = interactionBinding.getNamedBinding(option.getOptionName() + "-prompt");
		final PromptBindingViewer optionPromptText = new PromptBindingViewer(getElement(), promptBinding, this.getInteractionType(), getElement().getDesign().getVariablesFor(getElement()));
		Composite containerComp = createWrapperComposite(canvas, 100);
		optionPromptText.createControls(containerComp);
		optionPromptText.addListener(this);
		gd = new GridData(GridData.HORIZONTAL_ALIGN_END);
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		optionPromptText.getControl().setLayoutData(gd);
		optionPromptMap.put(option, optionPromptText);
		controlList.add(containerComp);

		ord1 = createRowDivider(canvas, dividerColor);
		controlList.add(ord1);
		voiceWidgets.add(ord1);

		Label optionGrammarLabel =
			createPropertyLabel(canvas, "Grammar");
		controlList.add(optionGrammarLabel);
		voiceWidgets.add(optionGrammarLabel);

		NamedBinding grammarBinding = interactionBinding.getNamedBinding(option.getOptionName() + "-grammar");
		final GrammarBindingViewer grammarPromptText = new GrammarBindingViewer(getElement(), grammarBinding, this.getInteractionType());
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalAlignment = SWT.RIGHT;
		grammarPromptText.createControls(canvas);
		grammarPromptText.getControl().setLayoutData(gd);
		optionGrammarMap.put(option, grammarPromptText);
		controlList.add(grammarPromptText.getControl());
		voiceWidgets.add(grammarPromptText.getControl());

		ord1 = createRowDivider(canvas, dividerColor);
		controlList.add(ord1);
		dtmfWidgets.add(ord1);
		
		Label optionDTMFLabel =
			createPropertyLabel(canvas, "DTMF Value");
		controlList.add(optionDTMFLabel);
		dtmfWidgets.add(optionDTMFLabel);

		final Combo dtmfCombo = createValueDropDown(canvas);
		dtmfCombo.add("0");
		dtmfCombo.add("1");
		dtmfCombo.add("2");
		dtmfCombo.add("3");
		dtmfCombo.add("4");
		dtmfCombo.add("5");
		dtmfCombo.add("6");
		dtmfCombo.add("7");
		dtmfCombo.add("8");
		dtmfCombo.add("9");
		dtmfCombo.add("*");
		dtmfCombo.add("#");
		NamedBinding dtmfNamedBinding = interactionBinding.getNamedBinding(option.getOptionName() + "-dtmf");
		LanguageBinding dtmfLanguageBinding = dtmfNamedBinding.getLanguageBinding("");
		BrandBinding dtmfBrandBinding = dtmfLanguageBinding.getBrandBinding(currentBrand);
		PropertyBindingItem pbi = (PropertyBindingItem)dtmfBrandBinding.getBindingItem();
		if(pbi == null)
			pbi = new PropertyBindingItem();
		if(pbi.getValue() == null)
			dtmfCombo.select(0);
		else
		{
			if(pbi.getValue().equals("#"))
				dtmfCombo.select(11);
			else if(pbi.getValue().equals("*"))
				dtmfCombo.select(10);
			else
				dtmfCombo.select(Integer.parseInt(pbi.getValue()));
		}
		dtmfCombo.addSelectionListener(new SelectionListener()
			{
				public void widgetSelected(SelectionEvent e)
				{
					NamedBinding dtmfNamedBinding = interactionBinding.getNamedBinding(option.getOptionName() + "-dtmf");
					LanguageBinding dtmfLanguageBinding = dtmfNamedBinding.getLanguageBinding("");
					BrandBinding dtmfBrandBinding = dtmfLanguageBinding.getBrandBinding(currentBrand);
					PropertyBindingItem pbi = (PropertyBindingItem)dtmfBrandBinding.getBindingItem();
					if(pbi == null)
						pbi = new PropertyBindingItem();
					else
						pbi = (PropertyBindingItem)pbi.clone();
					pbi.setValue(dtmfCombo.getItem(dtmfCombo.getSelectionIndex()));
					dtmfBrandBinding.setBindingItem(pbi);
				}

				public void widgetDefaultSelected(SelectionEvent e)
				{
				}
			});
		controlList.add(dtmfCombo);
		dtmfWidgets.add(dtmfCombo);

		ord1 = createRowDivider(canvas, dividerColor);
		controlList.add(ord1);

		Label optionSilentLabel =
			createPropertyLabel(canvas, "Option is Silent");
		controlList.add(optionSilentLabel);

		final Combo silentCombo = createValueDropDown(canvas);
		silentCombo.add("true");
		silentCombo.add("false");
		NamedBinding silentNamedBinding = interactionBinding.getNamedBinding(option.getOptionName() + "-silent");
		LanguageBinding silentLanguageBinding = silentNamedBinding.getLanguageBinding("");
		BrandBinding silentBrandBinding = silentLanguageBinding.getBrandBinding(currentBrand);
		pbi = (PropertyBindingItem)silentBrandBinding.getBindingItem();
		if(pbi == null)
			pbi = new PropertyBindingItem();
		if(pbi.getValue() == null)
			silentCombo.select(1);
		else
		{
			if(pbi.getValue().equals("true"))
				silentCombo.select(0);
			else
				silentCombo.select(1);
		}
		silentCombo.addSelectionListener(new SelectionListener()
			{
				public void widgetSelected(SelectionEvent e)
				{
					NamedBinding silentNamedBinding = interactionBinding.getNamedBinding(option.getOptionName() + "-silent");
					LanguageBinding silentLanguageBinding = silentNamedBinding.getLanguageBinding("");
					BrandBinding silentBrandBinding = silentLanguageBinding.getBrandBinding(currentBrand);
					PropertyBindingItem pbi = (PropertyBindingItem)silentBrandBinding.getBindingItem();
					if(pbi == null)
						pbi = new PropertyBindingItem();
					else
						pbi = (PropertyBindingItem)pbi.clone();
					pbi.setValue(silentCombo.getItem(silentCombo.getSelectionIndex()));
					silentBrandBinding.setBindingItem(pbi);
				}

				public void widgetDefaultSelected(SelectionEvent e)
				{
				}
			});
//		silentControls.put(option, silentCombo);
		controlList.add(silentCombo);
		ord1 = createRowDivider(canvas, dividerColor);
		controlList.add(ord1);

		return controlList;
	}
	
	public void updateButtons()
	{
		List<MenuChoice> currentOptions = mcBindingManager.getChoicesByBrand(currentBrand);

		for(int i = 0; i < currentOptions.size(); i++)
		{
			MenuChoice lastOption = currentOptions.get(i);
			moveUpButtons.get(lastOption).setEnabled(i != 0);
			moveDownButtons.get(lastOption).setEnabled(i < (
					currentOptions.size() - 1
				));
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.media.core.PromptBindingViewerListener#valueChanged(org.eclipse.vtp.desktop.media.core.PromptBindingViewer)
	 */
	public void valueChanged(PromptBindingViewer viewer)
    {
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
        updateButtons();
    }

	public void invalidConfigurationAttempt(PromptBindingViewer viewer)
	{
		getContainer().cancelMediaConfiguration();
	}

}
