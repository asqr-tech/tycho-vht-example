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

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.vtp.framework.interactions.core.media.Content;
import org.eclipse.vtp.framework.interactions.core.media.DateContent;

public class DateContentCreatorPanel extends DynamicContentCreatorPanel {
	Text text = null;
	Composite options = null;
	StackLayout optionsLayout = null;
	Label noOptions = null;
	Map<String, FormatOptions> optionMap = new HashMap<String, FormatOptions>();

	public DateContentCreatorPanel() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.vtp.desktop.media.core.ContentCreatorPanel#createContent()
	 */
	public Content createContent() {
		DateContent content = new DateContent();
		if (isDynamicSelected())
			content.setVariableValue(getDynamicSelection());
		else
			content.setStaticValue(text.getText());
		content.setFormatName(getFormat());
		FormatOptions formatOptions = optionMap.get(getFormat());
		if (formatOptions != null)
			content.setFormatOptions(formatOptions.getEncodedOptions());
		return content;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.desktop.media.core.DynamicContentCreatorPanel#
	 * createStaticControls(org.eclipse.swt.widgets.Composite)
	 */
	public Control createStaticControls(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new GridLayout());
		text = new Text(comp, SWT.BORDER | SWT.FLAT | SWT.SINGLE);
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		return comp;
	}

	@Override
	protected Control createFormatterControls(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, true));
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		super.createFormatterControls(composite);
		options = new Composite(composite, SWT.NONE);
		optionsLayout = new StackLayout();
		options.setLayout(optionsLayout);
		options.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		noOptions = new Label(options, SWT.NONE);
		optionMap.put("Day of Week", new DayOfWeekFormatOptions());
		optionMap.put("Hour of Day", new HourOfDayFormatOptions());
		for (FormatOptions formatOptions : optionMap.values())
			formatOptions.createOptionsControls(options);
		optionsLayout.topControl = noOptions;
		formatCombo.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateFormatOptions();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		return composite;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.desktop.media.core.DynamicContentCreatorPanel#
	 * setInitialContent
	 * (org.eclipse.vtp.framework.interactions.core.media.Content)
	 */
	public void setInitialContent(Content content) {
		if (content instanceof DateContent) {
			DateContent dateContent = (DateContent) content;
			if (dateContent.getValueType() == DateContent.STATIC_VALUE) {
				setDynamicSelected(false);
				text.setText(dateContent.getValue());
			} else {
				setDynamicSelected(true);
				setDynamicSelection(dateContent.getValue());
			}
		}
		super.setInitialContent(content);
		FormatOptions formatOptions = updateFormatOptions();
		if (formatOptions != null && content instanceof DateContent)
			formatOptions.setEncodedOptions(((DateContent) content)
					.getFormatOptions());
	}

	private FormatOptions updateFormatOptions() {
		FormatOptions formatOptions = optionMap.get(getFormat());
		optionsLayout.topControl = formatOptions == null ? noOptions
				: formatOptions.getControl();
		options.layout(true, true);
		return formatOptions;
	}

	private abstract class FormatOptions {

		abstract Control getControl();

		abstract String getEncodedOptions();

		abstract void setEncodedOptions(String options);

		abstract void createOptionsControls(Composite parent);

	}

	private final class DayOfWeekFormatOptions extends FormatOptions {

		Composite dayOfWeekOptions = null;
		Button dayOfWeekToday = null;
		Button dayOfWeekTomorrow = null;
		Button dayOfWeekThisXXX = null;
		Button dayOfWeekNextXXX = null;

		Control getControl() {
			return dayOfWeekOptions;
		}

		String getEncodedOptions() {
			StringBuilder builder = new StringBuilder();
			if (dayOfWeekToday.getSelection())
				builder.append("today,");
			if (dayOfWeekTomorrow.getSelection())
				builder.append("tomorrow,");
			if (dayOfWeekThisXXX.getSelection())
				builder.append("this,");
			if (dayOfWeekNextXXX.getSelection())
				builder.append("next,");
			if (builder.length() > 0)
				builder.setLength(builder.length() - 1);
			return builder.toString();
		}

		void setEncodedOptions(String options) {
			Set<String> set = new HashSet<String>(Arrays.asList(options
					.split(",")));
			dayOfWeekToday.setSelection(set.contains("today"));
			dayOfWeekTomorrow.setSelection(set.contains("tomorrow"));
			dayOfWeekThisXXX.setSelection(set.contains("this"));
			dayOfWeekNextXXX.setSelection(set.contains("next"));
		}

		void createOptionsControls(Composite parent) {
			dayOfWeekOptions = new Composite(parent, SWT.NONE);
			dayOfWeekOptions.setLayout(new GridLayout(2, true));
			dayOfWeekOptions.setLayoutData(new GridData(
					GridData.FILL_HORIZONTAL));
			dayOfWeekToday = new Button(dayOfWeekOptions, SWT.CHECK);
			dayOfWeekToday.setText("Today");
			dayOfWeekToday
					.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			dayOfWeekTomorrow = new Button(dayOfWeekOptions, SWT.CHECK);
			dayOfWeekTomorrow.setText("Tomorrow");
			dayOfWeekTomorrow.setLayoutData(new GridData(
					GridData.FILL_HORIZONTAL));
			dayOfWeekThisXXX = new Button(dayOfWeekOptions, SWT.CHECK);
			dayOfWeekThisXXX.setText("This ...");
			dayOfWeekThisXXX.setLayoutData(new GridData(
					GridData.FILL_HORIZONTAL));
			dayOfWeekNextXXX = new Button(dayOfWeekOptions, SWT.CHECK);
			dayOfWeekNextXXX.setText("Next ...");
			dayOfWeekNextXXX.setLayoutData(new GridData(
					GridData.FILL_HORIZONTAL));
		}

	}

	private final class HourOfDayFormatOptions extends FormatOptions {

		Composite hourOfDayOptions = null;
		Button hourOfDayMinutes = null;

		Control getControl() {
			return hourOfDayOptions;
		}

		String getEncodedOptions() {
			StringBuilder builder = new StringBuilder();
			if (hourOfDayMinutes != null && hourOfDayMinutes.getSelection())
				builder.append("minutes,");
			if (builder.length() > 0)
				builder.setLength(builder.length() - 1);
			return builder.toString();
		}

		void setEncodedOptions(String options) {
			Set<String> set = new HashSet<String>(Arrays.asList(options
					.split(",")));
			if (hourOfDayMinutes != null)
				hourOfDayMinutes.setSelection(set.contains("minutes"));
		}

		void createOptionsControls(Composite parent) {
			hourOfDayOptions = new Composite(options, SWT.NONE);
			hourOfDayOptions.setLayout(new GridLayout(1, true));
			hourOfDayOptions.setLayoutData(new GridData(
					GridData.FILL_HORIZONTAL));
			hourOfDayMinutes = new Button(hourOfDayOptions, SWT.CHECK);
			hourOfDayMinutes.setText("Minutes");
			hourOfDayMinutes.setLayoutData(new GridData(
					GridData.FILL_HORIZONTAL));
		}

	}

}
