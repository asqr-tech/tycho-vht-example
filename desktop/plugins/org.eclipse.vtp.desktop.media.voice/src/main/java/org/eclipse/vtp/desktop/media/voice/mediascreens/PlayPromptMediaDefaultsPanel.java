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

public class PlayPromptMediaDefaultsPanel implements IMediaDefaultPanel
{
	private static final String interactionType = "org.eclipse.vtp.framework.interactions.voice.interaction";
	private static final String elementType = "org.eclipse.vtp.modules.interactive.playPrompt";
	IMediaDefaultSettings settings = null;
	Combo barginCombo = null;
	List<DefaultValueStack> valueStacks = new LinkedList<DefaultValueStack>();

	public PlayPromptMediaDefaultsPanel()
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
		panelLabel.setText("Play Prompt");
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
		
		return settingsComposite;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.configuration.IMediaDefaultPanel#getTitle()
	 */
	public String getTitle()
	{
		return "Play Prompt";
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

	public RowDivider createRjowDivider(Composite parent, Color dividerColor)
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
