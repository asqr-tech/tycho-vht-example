package org.eclipse.vtp.desktop.media.voice.mediascreens;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
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
import org.eclipse.vtp.desktop.media.core.DefaultValueStack;
import org.eclipse.vtp.desktop.media.core.IMediaDefaultPanel;
import org.eclipse.vtp.desktop.media.core.ValueControl;
import org.eclipse.vtp.desktop.model.interactive.core.mediadefaults.IMediaDefaultSettings;

public class OptionSetMediaDefaultsPanel implements IMediaDefaultPanel
{
	private static final String interactionType = "org.eclipse.vtp.framework.interactions.voice.interaction";
	private static final String elementType = "org.eclipse.vtp.modules.interactive.optionSet";
	IMediaDefaultSettings settings = null;
	Combo inputModeCombo = null;
	Combo barginCombo = null;
	Spinner initialTimeoutSpinner = null;
	Spinner interdigitTimeoutSpinner = null;
	Spinner terminationTimeoutSpinner = null;
	Spinner speechCompleteTimeoutSpinner = null;
	Spinner speechIncompleteTimeoutSpinner = null;
	Spinner maxSpeechTimeoutSpinner = null;
	Spinner confidenceLevelSpinner = null;
	Spinner sensitivitySpinner = null;
	Spinner speedVsAccuracySpinner = null;
	List<DefaultValueStack> valueStacks = new LinkedList<DefaultValueStack>();

	public OptionSetMediaDefaultsPanel()
	{
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.configuration.IMediaDefaultPanel#createControls(org.eclipse.swt.widgets.Composite, boolean)
	 */
	public Control createControls(Composite parent, boolean supportDefaults)
	{
		DefaultValueStack lastStack = null;
		Composite settingsComposite = new Composite(parent, SWT.NONE);
		settingsComposite.setBackground(parent.getBackground());
		settingsComposite.setLayout(new GridLayout(2, false));
		
		Label panelLabel = new Label(settingsComposite, SWT.NONE);
		panelLabel.setBackground(settingsComposite.getBackground());
		panelLabel.setText("Option Set");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		panelLabel.setLayoutData(gridData);
		Label inputModeLabel = createPropertyLabel(settingsComposite, "User Input Style");
		inputModeLabel.setBackground(settingsComposite.getBackground());
		inputModeLabel.setToolTipText("This property selects the valid ways\r\n" +
									  "a caller can provide input:\r\n" + 
									  "\t*DTMF - Touchtone keypad only" +
									  "\t*Voice - Speech recognition only" +
									  "\t*Hybrid - Touchtone or speech accepted");
		Composite containerComp = createWrapperComposite(settingsComposite);
		containerComp.setBackground(settingsComposite.getBackground());
		if(supportDefaults)
		{
			lastStack = new DefaultValueStack(interactionType, elementType, "input-mode");
			lastStack.createControls(containerComp);
			containerComp = lastStack.getValueComposite();
			valueStacks.add(lastStack);
		}
		inputModeCombo = createValueDropDown(containerComp);
		inputModeCombo.add("Dtmf Only");
		inputModeCombo.add("Voice Only");
		inputModeCombo.add("Hybrid");
		inputModeCombo.select(0);
		if(supportDefaults)
		{
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
					else if("Dtmf Only".equals(value))
						inputModeCombo.select(0);
					else if("Voice Only".equals(value))
						inputModeCombo.select(1);
					else if("Hybrid".equals(value))
						inputModeCombo.select(2);
					else
						inputModeCombo.select(0);
                }
			});
		}
		
		Label bargeLabel = createPropertyLabel(settingsComposite, "Barge-in Enabled");
		bargeLabel.setBackground(settingsComposite.getBackground());
		bargeLabel.setToolTipText("Determines whether the caller can\r\ninterrupt the prompt to begin entry");
		containerComp = createWrapperComposite(settingsComposite);
		containerComp.setBackground(settingsComposite.getBackground());
		if(supportDefaults)
		{
			lastStack = new DefaultValueStack(interactionType, elementType, "barge-in");
			lastStack.createControls(containerComp);
			containerComp = lastStack.getValueComposite();
			valueStacks.add(lastStack);
		}
		barginCombo = createValueDropDown(containerComp);
		barginCombo.add("true");
		barginCombo.add("false");
		barginCombo.select(0);
		if(supportDefaults)
		{
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
		}
		
		Label initialTimeoutLabel = createPropertyLabel(settingsComposite, "Initial Input Timeout (Seconds)");
		initialTimeoutLabel.setBackground(settingsComposite.getBackground());
		initialTimeoutLabel.setToolTipText("The amount of time in seconds to wait\r\n" +
										   "for the caller to begin input before\r\n" +
										   "a NoInput event.");
		containerComp = createWrapperComposite(settingsComposite);
		containerComp.setBackground(settingsComposite.getBackground());
		if(supportDefaults)
		{
			lastStack = new DefaultValueStack(interactionType, elementType, "initial-timeout");
			lastStack.createControls(containerComp);
			containerComp = lastStack.getValueComposite();
			valueStacks.add(lastStack);
		}
		initialTimeoutSpinner = createValueSpinner(containerComp, 0, 100, 0, 0);
		if(supportDefaults)
		{
			lastStack.setValueControl(new ValueControl()
			{
				public String getValue()
                {
					return Integer.toString(initialTimeoutSpinner.getSelection());
                }

				public void setValue(String value)
                {
					initialTimeoutSpinner.setSelection((value == null || value.equals("")) ? 3 : Integer.parseInt(value));
                }
			});
		}
		
		Label interdigitTimeoutLabel = createPropertyLabel(settingsComposite, "Interdigit Timeout (Seconds)");
		interdigitTimeoutLabel.setBackground(settingsComposite.getBackground());
		interdigitTimeoutLabel.setToolTipText("The amount of time in seconds to wait\r\n" +
				   "for additional touchtone if the current\r\n" +
				   "input does not match the grammar before\r\n" +
				   "a NoMatch event.");
		containerComp = createWrapperComposite(settingsComposite);
		containerComp.setBackground(settingsComposite.getBackground());
		if(supportDefaults)
		{
			lastStack = new DefaultValueStack(interactionType, elementType, "interdigit-timeout");
			lastStack.createControls(containerComp);
			containerComp = lastStack.getValueComposite();
			valueStacks.add(lastStack);
		}
		interdigitTimeoutSpinner = createValueSpinner(containerComp, 0, 100, 0, 0);
		if(supportDefaults)
		{
			lastStack.setValueControl(new ValueControl()
			{
				public String getValue()
                {
					return Integer.toString(interdigitTimeoutSpinner.getSelection());
                }

				public void setValue(String value)
                {
					interdigitTimeoutSpinner.setSelection((value == null || value.equals("")) ? 2 : Integer.parseInt(value));
                }
			});
		}

		Label terminationTimeoutLabel = createPropertyLabel(settingsComposite, "Termination Timeout (Seconds)");
		terminationTimeoutLabel.setBackground(settingsComposite.getBackground());
		terminationTimeoutLabel.setToolTipText("The amount of time in seconds to wait\r\n" +
											   "for additional input after a selection\r\n" +
											   "has been matched.");
		containerComp = createWrapperComposite(settingsComposite);
		containerComp.setBackground(settingsComposite.getBackground());
		if(supportDefaults)
		{
			lastStack = new DefaultValueStack(interactionType, elementType, "termination-timeout");
			lastStack.createControls(containerComp);
			containerComp = lastStack.getValueComposite();
			valueStacks.add(lastStack);
		}
		terminationTimeoutSpinner = createValueSpinner(containerComp, 0, 100, 0, 0);
		if(supportDefaults)
		{
			lastStack.setValueControl(new ValueControl()
			{
				public String getValue()
                {
					return Integer.toString(terminationTimeoutSpinner.getSelection());
                }

				public void setValue(String value)
                {
					terminationTimeoutSpinner.setSelection((value == null || value.equals("")) ? 3 : Integer.parseInt(value));
                }
			});
		}

		Label speechIncompleteTimeoutLabel = createPropertyLabel(settingsComposite, "Speech Incomplete Timeout (Seconds)");
		speechIncompleteTimeoutLabel.setBackground(settingsComposite.getBackground());
		speechIncompleteTimeoutLabel.setToolTipText("The amount of time in seconds to wait\r\n" +
													"for additional input if the current entry\r\n" + 
													"does not match the provided grammar.");
		containerComp = createWrapperComposite(settingsComposite);
		containerComp.setBackground(settingsComposite.getBackground());
		if(supportDefaults)
		{
			lastStack = new DefaultValueStack(interactionType, elementType, "speech-incomplete-timeout");
			lastStack.createControls(containerComp);
			containerComp = lastStack.getValueComposite();
			valueStacks.add(lastStack);
		}
		speechIncompleteTimeoutSpinner = createValueSpinner(containerComp, 0, 100, 0, 0);
		if(supportDefaults)
		{
			lastStack.setValueControl(new ValueControl()
			{
				public String getValue()
                {
					return Integer.toString(speechIncompleteTimeoutSpinner.getSelection());
                }

				public void setValue(String value)
                {
					speechIncompleteTimeoutSpinner.setSelection((value == null || value.equals("")) ? 3 : Integer.parseInt(value));
                }
			});
		}

		Label speechCompleteTimeoutLabel = createPropertyLabel(settingsComposite, "Speech Completion Timeout (Seconds)");
		speechCompleteTimeoutLabel.setBackground(settingsComposite.getBackground());
		speechCompleteTimeoutLabel.setToolTipText("The amount of time in seconds to wait\r\n" +
												  "for additional input if the current entry\r\n" + 
												  "already matches the provided grammar.");
		containerComp = createWrapperComposite(settingsComposite);
		containerComp.setBackground(settingsComposite.getBackground());
		if(supportDefaults)
		{
			lastStack = new DefaultValueStack(interactionType, elementType, "speech-complete-timeout");
			lastStack.createControls(containerComp);
			containerComp = lastStack.getValueComposite();
			valueStacks.add(lastStack);
		}
		speechCompleteTimeoutSpinner = createValueSpinner(containerComp, 0, 100, 0, 0);
		if(supportDefaults)
		{
			lastStack.setValueControl(new ValueControl()
			{
				public String getValue()
                {
					return Integer.toString(speechCompleteTimeoutSpinner.getSelection());
                }

				public void setValue(String value)
                {
					speechCompleteTimeoutSpinner.setSelection((value == null || value.equals("")) ? 3 : Integer.parseInt(value));
                }
			});
		}

		Label maxSpeechTimeoutLabel = createPropertyLabel(settingsComposite, "Maximum Speech Length (Seconds)");
		maxSpeechTimeoutLabel.setBackground(settingsComposite.getBackground());
		maxSpeechTimeoutLabel.setToolTipText("The maximum length of speech input\r\n" +
											 "in seconds that will be accepted.");
		containerComp = createWrapperComposite(settingsComposite);
		containerComp.setBackground(settingsComposite.getBackground());
		if(supportDefaults)
		{
			lastStack = new DefaultValueStack(interactionType, elementType, "max-speech-timeout");
			lastStack.createControls(containerComp);
			containerComp = lastStack.getValueComposite();
			valueStacks.add(lastStack);
		}
		maxSpeechTimeoutSpinner = createValueSpinner(containerComp, 1, 300, 0, 0);
		if(supportDefaults)
		{
			lastStack.setValueControl(new ValueControl()
			{
				public String getValue()
                {
					return Integer.toString(maxSpeechTimeoutSpinner.getSelection());
                }

				public void setValue(String value)
                {
					maxSpeechTimeoutSpinner.setSelection((value == null || value.equals("")) ? 10 : Integer.parseInt(value));
                }
			});
		}

		Label confidenceLevelLabel = createPropertyLabel(settingsComposite, "Minimum Confidence Level Accepted");
		confidenceLevelLabel.setBackground(settingsComposite.getBackground());
		confidenceLevelLabel.setToolTipText("The minimum level of confidence accepted\r\n" +
											"by the speech recognition provider.");
		containerComp = createWrapperComposite(settingsComposite);
		containerComp.setBackground(settingsComposite.getBackground());
		if(supportDefaults)
		{
			lastStack = new DefaultValueStack(interactionType, elementType, "confidence-level");
			lastStack.createControls(containerComp);
			containerComp = lastStack.getValueComposite();
			valueStacks.add(lastStack);
		}
		confidenceLevelSpinner = createValueSpinner(containerComp, 0, 100, 0, 0);
		if(supportDefaults)
		{
			lastStack.setValueControl(new ValueControl()
			{
				public String getValue()
                {
					return Integer.toString(confidenceLevelSpinner.getSelection());
                }

				public void setValue(String value)
                {
					confidenceLevelSpinner.setSelection((value == null || value.equals("")) ? 50 : Integer.parseInt(value));
                }
			});
		}

		Label sensitivityLabel = createPropertyLabel(settingsComposite, "Typical Caller Environment");
		sensitivityLabel.setBackground(settingsComposite.getBackground());
		sensitivityLabel.setToolTipText("Determines how sensitive the speech recognition\r\n" +
				"will be to background noise.  The lower the number,\r\n" +
				"the less senitive the system will be");
		containerComp = createWrapperComposite(settingsComposite);
		containerComp.setBackground(settingsComposite.getBackground());
		if(supportDefaults)
		{
			lastStack = new DefaultValueStack(interactionType, elementType, "sensitivity-level");
			lastStack.createControls(containerComp);
			containerComp = lastStack.getValueComposite();
			valueStacks.add(lastStack);
		}
		sensitivitySpinner = createValueSpinner(containerComp, 0, 100, 0, 0);
		if(supportDefaults)
		{
			lastStack.setValueControl(new ValueControl()
			{
				public String getValue()
                {
					return Integer.toString(sensitivitySpinner.getSelection());
                }

				public void setValue(String value)
                {
					sensitivitySpinner.setSelection((value == null || value.equals("")) ? 50 : Integer.parseInt(value));
                }
			});
		}

		Label speedVsAccuracyLabel = createPropertyLabel(settingsComposite, "Speed Vs Accuracy");
		speedVsAccuracyLabel.setBackground(settingsComposite.getBackground());
		speedVsAccuracyLabel.setToolTipText("A hint to the speech recognition platform indicating\r\n" +
				"relative focus between the speed in which the result\r\n" +
				"is returned and the accuracy of that result.  The\r\n" +
				"lower the number, the faster the entry is processed\r\n" +
				"but accuracy may be reduced.");
		containerComp = createWrapperComposite(settingsComposite);
		containerComp.setBackground(settingsComposite.getBackground());
		if(supportDefaults)
		{
			lastStack = new DefaultValueStack(interactionType, elementType, "speed-vs-accuracy");
			lastStack.createControls(containerComp);
			containerComp = lastStack.getValueComposite();
			valueStacks.add(lastStack);
		}
		speedVsAccuracySpinner = createValueSpinner(containerComp, 0, 100, 0, 0);
		if(supportDefaults)
		{
			lastStack.setValueControl(new ValueControl()
			{
				public String getValue()
                {
					return Integer.toString(speedVsAccuracySpinner.getSelection());
                }

				public void setValue(String value)
                {
					speedVsAccuracySpinner.setSelection((value == null || value.equals("")) ? 50 : Integer.parseInt(value));
                }
			});
		}

		return settingsComposite;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.configuration.IMediaDefaultPanel#getTitle()
	 */
	public String getTitle()
	{
		return "Option Set";
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.configuration.IMediaDefaultPanel#save()
	 */
	public void save()
	{
		if(settings.inheritanceSupported())
		{
			for(DefaultValueStack dvs : valueStacks)
			{
				dvs.save();
			}
		}
		else
		{
			settings.getDefaultSetting(interactionType, elementType, "input-mode").setValue(inputModeCombo.getItem(inputModeCombo.getSelectionIndex()));
			settings.getDefaultSetting(interactionType, elementType, "barge-in").setValue(barginCombo.getItem(barginCombo.getSelectionIndex()));
			settings.getDefaultSetting(interactionType, elementType, "initial-timeout").setValue(Integer.toString(initialTimeoutSpinner.getSelection()));
			settings.getDefaultSetting(interactionType, elementType, "interdigit-timeout").setValue(Integer.toString(interdigitTimeoutSpinner.getSelection()));
			settings.getDefaultSetting(interactionType, elementType, "termination-timeout").setValue(Integer.toString(terminationTimeoutSpinner.getSelection()));
			settings.getDefaultSetting(interactionType, elementType, "speech-incomplete-timeout").setValue(Integer.toString(speechIncompleteTimeoutSpinner.getSelection()));
			settings.getDefaultSetting(interactionType, elementType, "speech-complete-timeout").setValue(Integer.toString(speechCompleteTimeoutSpinner.getSelection()));
			settings.getDefaultSetting(interactionType, elementType, "max-speech-timeout").setValue(Integer.toString(maxSpeechTimeoutSpinner.getSelection()));
			settings.getDefaultSetting(interactionType, elementType, "confidence-level").setValue(Integer.toString(confidenceLevelSpinner.getSelection()));
			settings.getDefaultSetting(interactionType, elementType, "sensitivity-level").setValue(Integer.toString(sensitivitySpinner.getSelection()));
			settings.getDefaultSetting(interactionType, elementType, "speed-vs-accuracy").setValue(Integer.toString(speedVsAccuracySpinner.getSelection()));
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.configuration.IMediaDefaultPanel#setDefaultSettings(org.eclipse.vtp.desktop.core.configuration.IMediaDefaultSettings)
	 */
	public void setDefaultSettings(IMediaDefaultSettings defaultSettings)
	{
		this.settings = defaultSettings;
		if(settings.inheritanceSupported())
		{
			for(DefaultValueStack dvs : valueStacks)
			{
				dvs.setSetting(settings);
			}
		}
		else
		{
			if(inputModeCombo != null)
			{
				String inputMode = settings.getDefaultSetting(interactionType, elementType, "input-mode").getValue();
				if(inputMode == null)
					inputModeCombo.select(0);
				else if("Dtmf Only".equals(inputMode))
					inputModeCombo.select(0);
				else if("Voice Only".equals(inputMode))
					inputModeCombo.select(1);
				else if("Hybrid".equals(inputMode))
					inputModeCombo.select(2);
				else
					inputModeCombo.select(0);
			}
			if(barginCombo != null)
			{
				String bargein = settings.getDefaultSetting(interactionType, elementType, "barge-in").getValue();
				if(bargein == null)
					barginCombo.select(0);
				else if("true".equals(bargein))
					barginCombo.select(0);
				else if("false".equals(bargein))
					barginCombo.select(1);
				else
					barginCombo.select(0);
			}
			if(initialTimeoutSpinner != null)
			{
				String initialTimeout = settings.getDefaultSetting(interactionType, elementType, "initial-timeout").getValue();
				initialTimeoutSpinner.setSelection(initialTimeout == null || initialTimeout.equals("") ? 3 : Integer.parseInt(initialTimeout));
			}
			if(interdigitTimeoutSpinner != null)
			{
				String interdigitTimeout = settings.getDefaultSetting(interactionType, elementType, "interdigit-timeout").getValue();
				interdigitTimeoutSpinner.setSelection(interdigitTimeout == null || interdigitTimeout.equals("") ? 2 : Integer.parseInt(interdigitTimeout));
			}
			if(terminationTimeoutSpinner != null)
			{
				String terminationTimeout = settings.getDefaultSetting(interactionType, elementType, "termination-timeout").getValue();
				terminationTimeoutSpinner.setSelection(terminationTimeout == null || terminationTimeout.equals("") ? 3 : Integer.parseInt(terminationTimeout));
			}
			if(speechIncompleteTimeoutSpinner != null)
			{
				String speechIncompleteTimeout = settings.getDefaultSetting(interactionType, elementType, "speech-incomplete-timeout").getValue();
				speechIncompleteTimeoutSpinner.setSelection(speechIncompleteTimeout == null || speechIncompleteTimeout.equals("") ? 3 : Integer.parseInt(speechIncompleteTimeout));
			}
			if(speechCompleteTimeoutSpinner != null)
			{
				String speechCompleteTimeout = settings.getDefaultSetting(interactionType, elementType, "speech-complete-timeout").getValue();
				speechCompleteTimeoutSpinner.setSelection(speechCompleteTimeout == null || speechCompleteTimeout.equals("") ? 3 : Integer.parseInt(speechCompleteTimeout));
			}
			if(maxSpeechTimeoutSpinner != null)
			{
				String maxSpeechTimeout = settings.getDefaultSetting(interactionType, elementType, "max-speech-timeout").getValue();
				maxSpeechTimeoutSpinner.setSelection(maxSpeechTimeout == null || maxSpeechTimeout.equals("") ? 10 : Integer.parseInt(maxSpeechTimeout));
			}
			if(confidenceLevelSpinner != null)
			{
				String confidenceLevel = settings.getDefaultSetting(interactionType, elementType, "confidence-level").getValue();
				confidenceLevelSpinner.setSelection(confidenceLevel == null || confidenceLevel.equals("") ? 50 : Integer.parseInt(confidenceLevel));
			}
			if(sensitivitySpinner != null)
			{
				String sensitivity = settings.getDefaultSetting(interactionType, elementType, "sensitivity-level").getValue();
				sensitivitySpinner.setSelection(sensitivity == null || sensitivity.equals("") ? 50 : Integer.parseInt(sensitivity));
			}
			if(speedVsAccuracySpinner != null)
			{
				String speedVsAccuracy = settings.getDefaultSetting(interactionType, elementType, "speed-vs-accuracy").getValue();
				speedVsAccuracySpinner.setSelection(speedVsAccuracy == null || speedVsAccuracy.equals("") ? 50 : Integer.parseInt(speedVsAccuracy));
			}
		}
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
	
}
