package org.eclipse.vtp.desktop.editors.core.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

public class UIHelper
{
	public static Composite createWrapperComposite(Composite parent)
	{
		return createWrapperComposite(parent, 20);
	}
	
	public static Composite createWrapperComposite(Composite parent, int indent)
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

	public static Label createPropertyLabel(Composite parent, String text)
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

	public static RowDivider createRowDivider(Composite parent, Color dividerColor)
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

	public static Combo createValueDropDown(Composite parent)
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

	public static Text createValueText(Composite parent)
	{
		return createValueText(parent, 50);
	}
	
	public static Text createValueText(Composite parent, int width)
	{
		Text ret =
			new Text(parent, SWT.BORDER | SWT.SINGLE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.verticalIndent = 2;
		gd.horizontalAlignment = SWT.RIGHT;
		gd.grabExcessHorizontalSpace = true;
		gd.widthHint = width;
		ret.setLayoutData(gd);
		return ret;
	}
	
	public static Text createValuePlaceHolder(Composite parent)
	{
		Text ret =
			new Text(parent, SWT.BORDER | SWT.SINGLE);
		GridData gd = new GridData();
		gd.verticalIndent = 2;
		gd.horizontalAlignment = SWT.RIGHT;
		gd.grabExcessHorizontalSpace = true;
		ret.setVisible(false);
		ret.setEnabled(false);
		ret.setLayoutData(gd);

		return ret;
	}
	
	public static Text createValueTextArea(Composite parent)
	{
		Text ret =
			new Text(parent, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		GridData gd = new GridData();
		gd.verticalIndent = 2;
		gd.heightHint = 75;
		gd.widthHint = 225;
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		ret.setLayoutData(gd);

		return ret;
	}

	public static Text createValuePassword(Composite parent)
	{
		Text ret =
			new Text(parent, SWT.BORDER | SWT.SINGLE | SWT.PASSWORD);
		GridData gd = new GridData();
		gd.verticalIndent = 2;
		gd.horizontalAlignment = SWT.RIGHT;
		gd.grabExcessHorizontalSpace = true;
		ret.setLayoutData(gd);

		return ret;
	}

	public static Spinner createValueSpinner(Composite parent, int min, int max,
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

	public static Slider createValueSlider(Composite parent, int min, int max,
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

}
