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

public class RecordMediaDefaultsPanel implements IMediaDefaultPanel
{
	private static final String interactionType = "org.eclipse.vtp.framework.interactions.voice.interaction";
	private static final String elementType = "org.eclipse.vtp.modules.interactive.record";
	IMediaDefaultSettings settings = null;
	Combo barginCombo = null;
	Combo beepCombo = null;
	Combo termCombo = null;
	Spinner initialTimeoutSpinner = null;
	Spinner terminationTimeoutSpinner = null;
	Spinner maxSpeechTimeoutSpinner = null;
	List<DefaultValueStack> valueStacks = new LinkedList<DefaultValueStack>();

	public RecordMediaDefaultsPanel()
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
		panelLabel.setText("Record");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		panelLabel.setLayoutData(gridData);

		Label bargeLabel = createPropertyLabel(settingsComposite, "Barge-in Enabled");
		bargeLabel.setBackground(settingsComposite.getBackground());
		bargeLabel.setToolTipText("Determines whether the caller can\r\ninterrupt the prompt to begin entry");
		Composite containerComp = createWrapperComposite(settingsComposite);
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
		
		Label beepComboLabel = createPropertyLabel(settingsComposite, "Play Beep?");
		beepComboLabel.setBackground(settingsComposite.getBackground());
		beepComboLabel.setToolTipText("This property determines whether the caller\r\n" +
									  "will here a beep to indicate recording has begun.");
		containerComp = createWrapperComposite(settingsComposite);
		containerComp.setBackground(settingsComposite.getBackground());
		if(supportDefaults)
		{
			lastStack = new DefaultValueStack(interactionType, elementType, "play-beep");
			lastStack.createControls(containerComp);
			containerComp = lastStack.getValueComposite();
			valueStacks.add(lastStack);
		}
		beepCombo = createValueDropDown(containerComp);
		beepCombo.add("true");
		beepCombo.add("false");
		beepCombo.select(0);
		if(supportDefaults)
		{
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
		}
		
		Label terminationCharacterLabel = createPropertyLabel(settingsComposite, "Allow DTMF Termination?");
		terminationCharacterLabel.setBackground(settingsComposite.getBackground());
		terminationCharacterLabel.setToolTipText("");
		containerComp = createWrapperComposite(settingsComposite);
		containerComp.setBackground(settingsComposite.getBackground());
		if(supportDefaults)
		{
			lastStack = new DefaultValueStack(interactionType, elementType, "dtmf-termination");
			lastStack.createControls(containerComp);
			containerComp = lastStack.getValueComposite();
			valueStacks.add(lastStack);
		}
		termCombo = createValueDropDown(containerComp);
		termCombo.add("true");
		termCombo.add("false");
		termCombo.select(0);
		if(supportDefaults)
		{
			lastStack.setValueControl(new ValueControl()
			{
				public String getValue()
                {
					return termCombo.getItem(termCombo.getSelectionIndex());
                }

				public void setValue(String value)
                {
					if(value == null)
						termCombo.select(0);
					else if("true".equals(value))
						termCombo.select(0);
					else if("false".equals(value))
						termCombo.select(1);
					else
						termCombo.select(0);
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
		
		Label terminationTimeoutLabel = createPropertyLabel(settingsComposite, "Termination Timeout (Seconds)");
		terminationTimeoutLabel.setBackground(settingsComposite.getBackground());
		terminationTimeoutLabel.setToolTipText("The amount of time in seconds to wait\r\n" +
											   "for additional input after a selection\r\n" +
											   "has been matched.");
		containerComp = createWrapperComposite(settingsComposite);
		containerComp.setBackground(settingsComposite.getBackground());
		if(supportDefaults)
		{
			lastStack = new DefaultValueStack(interactionType, elementType, "final-silence-timeout");
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

		Label maxSpeechTimeoutLabel = createPropertyLabel(settingsComposite, "Maximum Recording Length (Seconds)");
		maxSpeechTimeoutLabel.setBackground(settingsComposite.getBackground());
		maxSpeechTimeoutLabel.setToolTipText("The maximum length of recording input\r\n" +
											 "in seconds that will be accepted.");
		containerComp = createWrapperComposite(settingsComposite);
		containerComp.setBackground(settingsComposite.getBackground());
		if(supportDefaults)
		{
			lastStack = new DefaultValueStack(interactionType, elementType, "max-record-time");
			lastStack.createControls(containerComp);
			containerComp = lastStack.getValueComposite();
			valueStacks.add(lastStack);
		}
		maxSpeechTimeoutSpinner = createValueSpinner(containerComp, 1, 1740, 0, 0);
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
					maxSpeechTimeoutSpinner.setSelection((value == null || value.equals("")) ? 300 : Integer.parseInt(value));
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
		return "Record";
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
			settings.getDefaultSetting(interactionType, elementType, "barge-in").setValue(barginCombo.getItem(barginCombo.getSelectionIndex()));
			settings.getDefaultSetting(interactionType, elementType, "play-beep").setValue(beepCombo.getItem(beepCombo.getSelectionIndex()));
			settings.getDefaultSetting(interactionType, elementType, "dtmf-termination").setValue(termCombo.getItem(termCombo.getSelectionIndex()));
			settings.getDefaultSetting(interactionType, elementType, "initial-timeout").setValue(Integer.toString(initialTimeoutSpinner.getSelection()));
			settings.getDefaultSetting(interactionType, elementType, "final-silence-timeout").setValue(Integer.toString(terminationTimeoutSpinner.getSelection()));
			settings.getDefaultSetting(interactionType, elementType, "max-record-time").setValue(Integer.toString(maxSpeechTimeoutSpinner.getSelection()));
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
			if(beepCombo != null)
			{
				String inputMode = settings.getDefaultSetting(interactionType, elementType, "play-beep").getValue();
				if(inputMode == null)
					beepCombo.select(0);
				else if("true".equals(inputMode))
					beepCombo.select(0);
				else if("false".equals(inputMode))
					beepCombo.select(1);
				else
					beepCombo.select(0);
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
			if(termCombo != null)
			{
				String canTerm = settings.getDefaultSetting(interactionType, elementType, "dtmf-termination").getValue();
				if(canTerm == null)
					termCombo.select(0);
				else if("true".equals(canTerm))
					termCombo.select(0);
				else if("false".equals(canTerm))
					termCombo.select(1);
				else
					termCombo.select(0);
			}
			if(initialTimeoutSpinner != null)
			{
				String initialTimeout = settings.getDefaultSetting(interactionType, elementType, "initial-timeout").getValue();
				initialTimeoutSpinner.setSelection(initialTimeout == null || initialTimeout.equals("") ? 3 : Integer.parseInt(initialTimeout));
			}
			if(terminationTimeoutSpinner != null)
			{
				String terminationTimeout = settings.getDefaultSetting(interactionType, elementType, "final-silence-timeout").getValue();
				terminationTimeoutSpinner.setSelection(terminationTimeout == null || terminationTimeout.equals("") ? 3 : Integer.parseInt(terminationTimeout));
			}
			if(maxSpeechTimeoutSpinner != null)
			{
				String maxSpeechTimeout = settings.getDefaultSetting(interactionType, elementType, "max-record-time").getValue();
				maxSpeechTimeoutSpinner.setSelection(maxSpeechTimeout == null || maxSpeechTimeout.equals("") ? 300 : Integer.parseInt(maxSpeechTimeout));
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
