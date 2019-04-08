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
package org.eclipse.vtp.framework.interactions.voice.media;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.vtp.framework.interactions.core.media.Content;
import org.eclipse.vtp.framework.interactions.core.media.DateContent;
import org.eclipse.vtp.framework.interactions.core.media.DigitsContent;
import org.eclipse.vtp.framework.interactions.core.media.FormattableContent;
import org.eclipse.vtp.framework.interactions.core.media.IResourceManager;
import org.eclipse.vtp.framework.interactions.core.media.LettersContent;
import org.eclipse.vtp.framework.interactions.core.media.MoneyContent;
import org.eclipse.vtp.framework.interactions.core.media.TextContent;

/**
 * This implementation of IFormatter provides basic support for the English
 * language.  Audio files are used when available; otherwise text content is
 * used.  This formatter does not perform advanced audio selection, such as
 * using specific audio files for numbers in the middle of a grouping.<br>
 * <br>
 * This class can be used as a guide to construct other formatter
 * implementations, but is not meant to be directly subclassed.
 * 
 * @author Trip Gilman
 * @since 3.0
 */
public class SimpleEnglishVoiceFormatter extends VoiceFormatter
{
	/** Convenience member for US currency */
	private static final Currency USD_CURRENCY = Currency.getInstance("USD");

	/**	Array of month names.  The index is 0 based. */
	private static final String[] months = new String[] { "january",
														  "february",
														  "march",
														  "april",
														  "may",
														  "june",
														  "july",
														  "august",
														  "september",
														  "october",
														  "november",
														  "december"};
	/**	Array of day names.  The index is 0 based. */
	private static final String[] days = new String[] { "sunday",
														  "monday",
														  "tuesday",
														  "wednesday",
														  "thursday",
														  "friday",
														  "saturday",
														  "sunday"};
	
	private static final Map<Character, String[]> characterReplacements = new HashMap<Character, String[]>();
	
	static
	{
		characterReplacements.put('#', new String[] {"pound", "Pound"});
		characterReplacements.put('!', new String[] {"exclamation", "Exclamation Point"});
		characterReplacements.put('_', new String[] {"underscore", "Under score"});
		characterReplacements.put('-', new String[] {"dash", "Dash"});
		characterReplacements.put('[', new String[] {"rightsquarebracket", "Right Square Bracket"});
		characterReplacements.put(']', new String[] {"leftsquarebracket", "Left Square Bracket"});
		characterReplacements.put('{', new String[] {"rightcurlybrace", "Right Curly Brace"});
		characterReplacements.put('}', new String[] {"leftcurlybrace", "Left Curly Brace"});
		characterReplacements.put('*', new String[] {"asterisk", "Asterisk"});
		characterReplacements.put('@', new String[] {"atsign", "At Sign"});
		characterReplacements.put('%', new String[] {"percentsign", "Percent Sign"});
		characterReplacements.put('&', new String[] {"ampersand", "Ampersand"});
		characterReplacements.put('+', new String[] {"plussign", "Plus Sign"});
		characterReplacements.put('=', new String[] {"equalssign", "Equals Sign"});
		characterReplacements.put('$', new String[] {"dollarsign", "Dollar Sign"});
		characterReplacements.put('?', new String[] {"questionmark", "Question Mark"});
		characterReplacements.put('/', new String[] {"forwardslash", "Forward Slash"});
		characterReplacements.put('\\', new String[] {"backslash", "Back Slash"});
		characterReplacements.put('|', new String[] {"bar", "Bar"});
		characterReplacements.put(',', new String[] {"comma", "Comma"});
		characterReplacements.put('.', new String[] {"period", "Period"});
		characterReplacements.put('<', new String[] {"lessthansign", "Less Than Sign"});
		characterReplacements.put('>', new String[] {"greaterthansign", "Greater Than Sign"});
		characterReplacements.put('\'', new String[] {"singlequote", "Single Quote"});
		characterReplacements.put('"', new String[] {"doublequote", "Double Quote"});
		characterReplacements.put('(', new String[] {"rightparentheses", "Right Parentheses"});
		characterReplacements.put(')', new String[] {"leftparentheses", "Left Parentheses"});
	}

	/**
	 * Constructs a new SimpleEnglishVoiceFormatter.
	 */
	public SimpleEnglishVoiceFormatter()
	{
		super();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.framework.interactions.core.media.IFormatter#formatDate(java.util.Date, java.lang.String, org.eclipse.vtp.framework.interactions.core.media.IResourceManager)
	 */
	public List<Content> formatDate(Date date, String formatDefinition, String formatOptions, IResourceManager resourceManager)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return formatDate(cal, formatDefinition, formatOptions, resourceManager);
	}
	
	public List<Content> formatDate(Calendar cal, String formatDefinition, String formatOptions, IResourceManager resourceManager)
	{
		List<Content> ret = new ArrayList<Content>();
		if(formatDefinition == null || formatDefinition.equals(""))
		{
			TextContent tc = new TextContent();
			DateFormat df = DateFormat.getDateTimeInstance();
			df.setTimeZone(cal.getTimeZone());
			tc.setStaticText(df.format(cal.getTime()) + " ");
			ret.add(tc);
		}
		else if(formatDefinition.equals("Short Date") || formatDefinition.equals("Default"))
		{
        	ret.addAll(formatNumber(cal.get(Calendar.MONTH) + 1, "Default", "", resourceManager));
        	ret.addAll(formatOrdinal(cal.get(Calendar.DATE), "Default", "", resourceManager));
        	ret.addAll(formatYear(Integer.toString(cal.get(Calendar.YEAR)), resourceManager));
		}
		else if(formatDefinition.equals("Long Date"))
		{
        	ret.add(getAudioContent(resourceManager, "/Months/", months[cal.get(Calendar.MONTH)], months[cal.get(Calendar.MONTH)]));
        	ret.addAll(formatOrdinal(cal.get(Calendar.DATE), "Default", "", resourceManager));
        	ret.addAll(formatYear(Integer.toString(cal.get(Calendar.YEAR)), resourceManager));
		}
		else if(formatDefinition.equals("Short Time"))
		{
			int hour = cal.get(Calendar.HOUR);
			ret.addAll(formatNumber(hour == 0 ? 12 : hour, "Default", "", resourceManager));

			int minute = cal.get(Calendar.MINUTE);
			if(minute > 0 && minute < 10)
			{
				ret.addAll(formatLetters("O", "Default", "", resourceManager));
				ret.addAll(formatNumber(minute, "Default", "", resourceManager));
			}
			else if(minute >= 10)
			{
				ret.addAll(formatNumber(minute, "Default", "", resourceManager));
			}
			
			if(cal.get(Calendar.AM_PM) == 0)
			{
				ret.addAll(formatLetters("AM", "Default", "", resourceManager));
			}
			else
			{
				ret.addAll(formatLetters("PM", "Default", "", resourceManager));
			}
		}
		else if(formatDefinition.equals("Long Time"))
		{
			int hour = cal.get(Calendar.HOUR);
			ret.addAll(formatNumber(hour == 0 ? 12 : hour, "Default", "", resourceManager));

			int minute = cal.get(Calendar.MINUTE);
			if(minute > 0 && minute < 10)
			{
				ret.addAll(formatLetters("O", "Default", "", resourceManager));
				ret.addAll(formatNumber(minute, "Default", "", resourceManager));
			}
			else if(minute >= 10)
			{
				ret.addAll(formatNumber(minute, "Default", "", resourceManager));
			}
			
			if(cal.get(Calendar.AM_PM) == 0)
			{
				ret.addAll(formatLetters("AM", "Default", "", resourceManager));
			}
			else
			{
				ret.addAll(formatLetters("PM", "Default", "", resourceManager));
			}
		}
		else if(formatDefinition.equals("Short Date Time"))
		{
        	ret.addAll(formatNumber(cal.get(Calendar.MONTH) + 1, "Default", "", resourceManager));
        	ret.addAll(formatOrdinal(cal.get(Calendar.DATE), "Default", "", resourceManager));
        	ret.addAll(formatYear(Integer.toString(cal.get(Calendar.YEAR)), resourceManager));

        	int hour = cal.get(Calendar.HOUR);
			ret.addAll(formatNumber(hour == 0 ? 12 : hour, "Default", "", resourceManager));

			int minute = cal.get(Calendar.MINUTE);
			if(minute > 0 && minute < 10)
			{
				ret.addAll(formatLetters("O", "Default", "", resourceManager));
				ret.addAll(formatNumber(minute, "Default", "", resourceManager));
			}
			else if(minute >= 10)
			{
				ret.addAll(formatNumber(minute, "Default", "", resourceManager));
			}
			
			if(cal.get(Calendar.AM_PM) == 0)
			{
				ret.addAll(formatLetters("AM", "Default", "", resourceManager));
			}
			else
			{
				ret.addAll(formatLetters("PM", "Default", "", resourceManager));
			}
		}
		else if(formatDefinition.equals("Long Date Time"))
		{
        	ret.add(getAudioContent(resourceManager, "/Months/", months[cal.get(Calendar.MONTH)], months[cal.get(Calendar.MONTH)]));
        	ret.addAll(formatOrdinal(cal.get(Calendar.DATE), "Default", "", resourceManager));
        	ret.addAll(formatYear(Integer.toString(cal.get(Calendar.YEAR)), resourceManager));

        	int hour = cal.get(Calendar.HOUR);
			ret.addAll(formatNumber(hour == 0 ? 12 : hour, "Default", "", resourceManager));

			int minute = cal.get(Calendar.MINUTE);
			if(minute > 0 && minute < 10)
			{
				ret.addAll(formatLetters("O", "Default", "", resourceManager));
				ret.addAll(formatNumber(minute, "Default", "", resourceManager));
			}
			else if(minute >= 10)
			{
				ret.addAll(formatNumber(minute, "Default", "", resourceManager));
			}
			
			if(cal.get(Calendar.AM_PM) == 0)
			{
				ret.addAll(formatLetters("AM", "Default", "", resourceManager));
			}
			else
			{
				ret.addAll(formatLetters("PM", "Default", "", resourceManager));
			}
		}
		else if (formatDefinition.equals("Day of Week"))
		{
			int calYear = cal.get(Calendar.YEAR);
			int calDay = cal.get(Calendar.DAY_OF_YEAR);
			int calHour = cal.get(Calendar.HOUR_OF_DAY);
			Calendar now = Calendar.getInstance(cal.getTimeZone());
			int nowYear = now.get(Calendar.YEAR);
			int nowDay = now.get(Calendar.DAY_OF_YEAR);
			Set<String> options = new HashSet<String>(
					Arrays.asList(formatOptions.split(",")));
			if (calYear == nowYear && calDay == nowDay) {
				// Process dates that equal today.
				if (options.contains("this")) {
					if (calHour < 12)
						ret.add(getAudioContent(resourceManager, "/DayOfWeek/",
								"this_morning", "this morning"));
					else if (calHour < 17)
						ret.add(getAudioContent(resourceManager, "/DayOfWeek/",
								"this_afternoon", "this afternoon"));
					else
						ret.add(getAudioContent(resourceManager, "/DayOfWeek/",
								"this_evening", "this evening"));
					// Format this (morning, etc...)
				}
				else
				{
					if(options.contains("today"))
					{
						ret.add(getAudioContent(resourceManager, "/DayOfWeek/", "today",
							"today"));
					}
					else
					{
						ret.add(getAudioContent(resourceManager, "/DayOfWeek/",
								days[cal.get(Calendar.DAY_OF_WEEK) - 1],
								days[cal.get(Calendar.DAY_OF_WEEK) - 1]));
					}
				}
			} else {
				int distance = 1;
				Calendar search = (Calendar)now.clone();
				while (distance < 14) {
					search.add(Calendar.DAY_OF_MONTH, 1);
					if (calYear == search.get(Calendar.YEAR) && calDay == search.get(Calendar.DAY_OF_YEAR))
						break;
					++distance;
				}
				if (distance == 1 && options.contains("tomorrow")) {
					ret.add(getAudioContent(resourceManager, "/DayOfWeek/",
							"tomorrow", "tomorrow"));
				} else if (distance < 7) {
					ret.add(getAudioContent(resourceManager, "/DayOfWeek/",
							days[cal.get(Calendar.DAY_OF_WEEK) - 1],
							days[cal.get(Calendar.DAY_OF_WEEK) - 1]));
				} else if (distance < 14 && options.contains("next")) {
					ret.add(getAudioContent(resourceManager, "/DayOfWeek/", "next",
							"next"));
					ret.add(getAudioContent(resourceManager, "/DayOfWeek/",
							days[cal.get(Calendar.DAY_OF_WEEK) - 1],
							days[cal.get(Calendar.DAY_OF_WEEK) - 1]));
				} else {
					formatDate(cal, "Short Date", "", resourceManager);
				}
			}
		}
		else if (formatDefinition.equals("Hour of Day"))
		{
			Set<String> options = new HashSet<String>(
					Arrays.asList(formatOptions.split(",")));
			int hour = cal.get(Calendar.HOUR);
			ret.addAll(formatNumber(hour == 0 ? 12 : hour, "Default", "", resourceManager));
			if (options.contains("minutes"))
			{
				int minute = cal.get(Calendar.MINUTE);
				if(minute > 0 && minute < 10)
				{
					ret.addAll(formatLetters("O", "Default", "", resourceManager));
					ret.addAll(formatNumber(minute, "Default", "", resourceManager));
				}
				else if(minute >= 10)
				{
					ret.addAll(formatNumber(minute, "Default", "", resourceManager));
				}
			}
			if(cal.get(Calendar.AM_PM) == 0)
			{
				ret.addAll(formatLetters("AM", "Default", "", resourceManager));
			}
			else
			{
				ret.addAll(formatLetters("PM", "Default", "", resourceManager));
			}
		}
		return ret;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.framework.interactions.core.media.IFormatter#formatDigits(java.lang.String, java.lang.String, org.eclipse.vtp.framework.interactions.core.media.IResourceManager)
	 */
	public List<Content> formatDigits(String digits, String formatDefinition, String formatOptions, IResourceManager resourceManager)
	{
		char[] chars = digits.toCharArray();
		List<Content> ret = new ArrayList<Content>();
		if(formatDefinition.equals("DTMF"))
		{
			for(int i = 0; i < chars.length; i++)
			{
				String s = new String(chars, i, 1);
				if(s.equals("*"))
					s = "star";
				else if(s.equals("#"))
					s = "pound";
				if(resourceManager.isFileResource("/DTMF/Dtmf-" + s + ".vox"))
				{
					AudioContent ac = new AudioContent();
					ac.setStaticPath("/DTMF/Dtmf-" + s + ".vox");
					ret.add(ac);
				}
				else if(resourceManager.isFileResource("/DTMF/Dtmf-" + s + ".wav"))
				{
					AudioContent ac = new AudioContent();
					ac.setStaticPath("/DTMF/Dtmf-" + s + ".wav");
					ret.add(ac);
				}
				else if(resourceManager.isFileResource("/DTMF/Dtmf-" + s + ".au"))
				{
					AudioContent ac = new AudioContent();
					ac.setStaticPath("/DTMF/Dtmf-" + s + ".au");
					ret.add(ac);
				}
			}
		}
		else
		{
			for(int i = 0; i < chars.length; i++)
			{
				if(!Character.isDigit(chars[i]))
					continue;
				if(resourceManager.isFileResource("/Digits/" + new String(chars, i, 1) + ".vox"))
				{
					AudioContent ac = new AudioContent();
					ac.setStaticPath("/Digits/" + new String(chars, i, 1) + ".vox");
					ret.add(ac);
				}
				else if(resourceManager.isFileResource("/Digits/" + new String(chars, i, 1) + ".wav"))
				{
					AudioContent ac = new AudioContent();
					ac.setStaticPath("/Digits/" + new String(chars, i, 1) + ".wav");
					ret.add(ac);
				}
				else if(resourceManager.isFileResource("/Digits/" + new String(chars, i, 1) + ".au"))
				{
					AudioContent ac = new AudioContent();
					ac.setStaticPath("/Digits/" + new String(chars, i, 1) + ".au");
					ret.add(ac);
				}
				else
				{
					TextContent tc = new TextContent();
					tc.setStaticText(new String(chars, i, 1) + " ");
					ret.add(tc);
				}
			}
		}
		return ret;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.framework.interactions.core.media.IFormatter#formatLetters(java.lang.String, java.lang.String, org.eclipse.vtp.framework.interactions.core.media.IResourceManager)
	 */
	public List<Content> formatLetters(String text, String formatDefinition, String formatOptions, IResourceManager resourceManager)
	{
		char[] chars = text.toCharArray();
		List<Content> ret = new ArrayList<Content>();
		for(int i = 0; i < chars.length; i++)
		{
			String[] replacement = characterReplacements.get(chars[i]);
			if(!Character.isLetterOrDigit(chars[i]) && replacement == null)
				continue;
			String fileName = new String(chars, i, 1).toUpperCase();
			String altText = fileName;
			String prefixPath = "/Letters/";
			if(Character.isDigit(chars[i]))
			{
				prefixPath = "/Digits/";
				if(formatDefinition.equals("Preserve Case"))
				{
					ret.add(getAudioContent(resourceManager, "/Letters/", "number", "Number "));
				}
			}
			else if(Character.isLetter(chars[i]))
			{
				if(formatDefinition.equals("Preserve Case"))
				{
					if(Character.isUpperCase(chars[i]))
					{
						ret.add(getAudioContent(resourceManager, "/Letters/", "uppercase", "Capital "));
					}
					else if(Character.isLowerCase(chars[i]))
					{
						ret.add(getAudioContent(resourceManager, "/Letters/", "lowercase", "Lower Case "));
					}
				}
			}
			else if(replacement != null)
			{
				fileName = replacement[0];
				altText = replacement[1];
			}
			ret.add(getAudioContent(resourceManager, prefixPath, fileName, altText));
		}
		return ret;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.framework.interactions.core.media.IFormatter#formatMoney(java.math.BigDecimal, java.util.Currency, java.lang.String, org.eclipse.vtp.framework.interactions.core.media.IResourceManager)
	 */
	public List<Content> formatMoney(BigDecimal money, Currency currency, String formatDefinition, String formatOptions, IResourceManager resourceManager)
	{
		List<Content> ret = new ArrayList<Content>();
		money = money.setScale(2, BigDecimal.ROUND_DOWN);
		if(USD_CURRENCY.equals(currency))
		{
			if(money.compareTo(ZERO) < 0)
			{
	        	ret.add(getAudioContent(resourceManager, "/Common/", "negative", "negative"));
				money = money.multiply(NEGATIVE_ONE);
			}

			List<Content> dollarList = formatNumber(money.intValue(), "Default", "", resourceManager);
			ret.addAll(dollarList);
			if(money.intValue() == 1 || formatDefinition.equals("Force Singular"))
	        	ret.add(getAudioContent(resourceManager, "/Common/", "dollar", "dollar"));
			else
	        	ret.add(getAudioContent(resourceManager, "/Common/", "dollars", "dollars"));

			String str = money.toString();

			if(str.indexOf('.') >= 0)
			{
				int cents =
					Integer.parseInt(str.substring(str.indexOf('.') + 1));

				if(cents > 0)
				{
		        	ret.add(getAudioContent(resourceManager, "/Common/", "and", "and"));
					List<Content> centsList = formatNumber(cents, "Default", "", resourceManager);
					ret.addAll(centsList);
					if(cents == 1 || formatDefinition.equals("Force Singular"))
			        	ret.add(getAudioContent(resourceManager, "/Common/", "cent", "cent"));
					else
			        	ret.add(getAudioContent(resourceManager, "/Common/", "cents", "cents"));
				}
			}
		}
		return ret;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.framework.interactions.core.media.IFormatter#formatNumber(int, java.lang.String, org.eclipse.vtp.framework.interactions.core.media.IResourceManager)
	 */
	public List<Content> formatNumber(int number, String formatDefinition, String formatOptions, IResourceManager resourceManager)
	{
		List<Content> ret = new ArrayList<Content>();
        if(number == 0)
        {
        	ret.add(getAudioContent(resourceManager, "/Digits/", "0", "0"));
        	return ret;
        }

        //check negative
        if(number < 0)
        {
			ret.add(getAudioContent(resourceManager, "/Common/", "negative", "negative"));
            number *= -1;
        }
		//billions
		int billions = number / 1000000000;
		if(billions > 0)
		{
			translateNumberInHundreds(resourceManager, billions, ret);
			ret.add(getAudioContent(resourceManager, "/Common/", "billion", "billion"));
            number -= (billions * 1000000000);
		}
		//millions
		int millions = number / 1000000;
		if(millions > 0)
		{
			translateNumberInHundreds(resourceManager, millions, ret);
			ret.add(getAudioContent(resourceManager, "/Common/", "million", "million"));
            number -= (millions * 1000000);
		}
		//thousands
		int thousands = number / 1000;
		if(thousands > 0)
		{
			translateNumberInHundreds(resourceManager, thousands, ret);
			ret.add(getAudioContent(resourceManager, "/Common/", "thousand", "thousand"));
            number -= (thousands * 1000);
		}
		translateNumberInHundreds(resourceManager, number % 1000, ret);
		return ret;
	}

    /**
     * Internal function to produce the transformation of a number in the
     * hundreds range.  The results of this function are appended to the end
     * of the given list of content.
     * 
	 * @param resourceManager Provides access to the available file resources.
     * @param number The number to transform.
     * @param content The current list of elements.
     */
    private void translateNumberInHundreds(IResourceManager resourceManager, int number, List<Content> content)
    {
        int hundreds = number / 100;

        if(hundreds > 0)
        {
        	content.add(getAudioContent(resourceManager, "/Digits/", String.valueOf(hundreds), String.valueOf(hundreds)));
        	content.add(getAudioContent(resourceManager, "/Common/", "hundred", "hundred"));
            number -= (hundreds * 100);
        }

        if(number > 19)
        {
        	content.add(getAudioContent(resourceManager, "/Digits/", String.valueOf(number - (number % 10)), 
        			String.valueOf(number - (number % 10))));
            number %= 10;
        }

        if(number > 0)
        {
        	content.add(getAudioContent(resourceManager, "/Digits/", String.valueOf(number), String.valueOf(number)));
        }
    }

    /* (non-Javadoc)
	 * @see org.eclipse.vtp.framework.interactions.core.media.IFormatter#formatOrdinal(int, java.lang.String, org.eclipse.vtp.framework.interactions.core.media.IResourceManager)
	 */
	public List<Content> formatOrdinal(int ordinal, String formatDefinition, String formatOptions, IResourceManager resourceManager)
	{
		List<Content> ret = new ArrayList<Content>();
		if(ordinal == 0)
		{
			ret.add(getAudioContent(resourceManager, "/Ordinals/", "0th", "zeroth"));
			return ret;
		}

		if(ordinal < 0)
		{
			ret.add(getAudioContent(resourceManager, "/Common/", "negative", "negative"));
			ordinal *= -1;
		}

		int billions = ordinal / 1000000000;

		if(billions > 0)
		{
			translateNumberInHundreds(resourceManager, billions, ret);
			ordinal -= (billions * 1000000000);

			if(ordinal == 0)
			{
				ret.add(getAudioContent(resourceManager, "/Common/", "billionth", "billionth"));
			}
			else
			{
				ret.add(getAudioContent(resourceManager, "/Common/", "billion", "billion"));
			}
		}

		int millions = ordinal / 1000000;

		if(millions > 0)
		{
			translateNumberInHundreds(resourceManager, millions, ret);
			ordinal -= (millions * 1000000);

			if(ordinal == 0)
			{
				ret.add(getAudioContent(resourceManager, "/Common/", "millionth", "millionth"));
			}
			else
			{
				ret.add(getAudioContent(resourceManager, "/Common/", "million", "million"));
			}
		}

		int thousands = ordinal / 1000;

		if(thousands > 0)
		{
			translateNumberInHundreds(resourceManager, thousands, ret);
			ordinal -= (thousands * 1000);

			if(ordinal == 0)
			{
				ret.add(getAudioContent(resourceManager, "/Common/", "thousandth", "thousandth"));
			}
			else
			{
				ret.add(getAudioContent(resourceManager, "/Common/", "thousand", "thousand"));
			}
		}

		int hundreds = ordinal / 100;

		if(hundreds > 0)
		{
			translateNumberInHundreds(resourceManager, hundreds, ret);
			ordinal -= (hundreds * 100);

			if(ordinal == 0)
			{
				ret.add(getAudioContent(resourceManager, "/Common/", "hundredth", "hundredth"));
			}
			else
			{
				ret.add(getAudioContent(resourceManager, "/Common/", "hundred", "hundred"));
			}
		}

		if((ordinal % 10) == 0)
		{
			ret.add(getAudioContent(resourceManager, "/Ordinals/", String.valueOf(ordinal) + "th", String.valueOf(ordinal) + "th"));
		}
		else
		{
			int tens = ordinal / 10;

			if(tens > 1)
			{
				translateNumberInHundreds(resourceManager, tens * 10, ret);
				ordinal -= (tens * 10);
			}

			switch(ordinal)
			{
				case 1:
					ret.add(getAudioContent(resourceManager, "/Ordinals/", "1st", "1st"));
					break;

				case 2:
					ret.add(getAudioContent(resourceManager, "/Ordinals/", "2nd", "2nd"));
					break;

				case 3:
					ret.add(getAudioContent(resourceManager, "/Ordinals/", "3rd", "3rd"));
					break;

				default:
					ret.add(getAudioContent(resourceManager, "/Ordinals/", String.valueOf(ordinal) + "th", String.valueOf(ordinal) + "th"));
			}
		}
		return ret;
	}
	
	/**
	 * Internal function to transform the given string into the elements used in
	 * the English language to describe a year.
	 * 
	 * @param year The year value to transform.
	 * @param resourceManager Provides access to the available file resources.
	 * @return A List of Content objects that represent a year described in the
	 * English language.
	 */
	private List<Content> formatYear(String year, IResourceManager resourceManager)
	{
		List<Content> ret = new ArrayList<Content>();
		if(year.length() != 4 && year.length() != 3)
		{
			TextContent tc = new TextContent();
			tc.setStaticText(year);
			ret.add(tc);
		}
		else
		{
			if(year.length() == 3)
			{
				year = "0" + year;
			}
			int century;
			int tens;
			try
			{
				century = Integer.parseInt(year.substring(0, 2));
				tens = Integer.parseInt(year.substring(2));
			}
			catch(Exception e)
			{
				TextContent tc = new TextContent();
				tc.setStaticText(year);
				ret.add(tc);
				return ret;
			}
			if(century == 0)
			{
				ret.addAll(formatNumber(tens, "default", "", resourceManager));
			}
			else if(century % 10 == 0)
			{
				ret.addAll(formatNumber(century * 100, "default", "", resourceManager));
				if(tens > 0)
				{
					ret.addAll(formatNumber(tens, "default", "", resourceManager));
				}
			}
			else if(century < 13)
			{
				ret.addAll(formatNumber(century, "default", "", resourceManager));
				ret.add(getAudioContent(resourceManager, "/Common/", "hundred", "hundred"));
				if(tens > 0)
				{
					if(tens < 10)
					{
						ret.addAll(formatNumber(tens, "default", "", resourceManager));
					}
					else
					{
						ret.addAll(formatNumber(tens, "default", "", resourceManager));
					}
				}
			}
			else
			{
				ret.addAll(formatNumber(century, "default", "", resourceManager));
				if(tens == 0)
				{
					ret.add(getAudioContent(resourceManager, "/Common/", "hundred", "hundred"));
				}
				else if(tens < 10)
				{
					ret.add(getAudioContent(resourceManager, "/Common/", "oh", "oh"));
					ret.addAll(formatNumber(tens, "default", "", resourceManager));
				}
				else
				{
					ret.addAll(formatNumber(tens, "default", "", resourceManager));
				}
			}
		}
		return ret;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.framework.interactions.core.media.IFormatter#getLanguageCode()
	 */
	public String getLanguageCode()
	{
		return "en-US";
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.framework.interactions.core.media.IFormatter#getLanguageName()
	 */
	public String getLanguageName()
	{
		return "English";
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.framework.interactions.core.media.IFormatter#getDefaultFormats(org.eclipse.vtp.framework.interactions.core.media.FormattableContent)
	 */
	public List<String> getDefaultFormats(FormattableContent formattable)
    {
		List<String> ret = new ArrayList<String>();
		if(formattable instanceof DateContent)
		{
			ret.add("Short Date");
			ret.add("Long Date");
			ret.add("Short Time");
			ret.add("Long Time");
			ret.add("Short Date Time");
			ret.add("Long Date Time");
			ret.add("Day of Week");
			ret.add("Hour of Day");
		}
		else if(formattable instanceof MoneyContent)
		{
			ret.add("Force Singular");
		}
		else if(formattable instanceof DigitsContent)
		{
			ret.add("DTMF");
		}
		else if(formattable instanceof LettersContent)
		{
			ret.add("Preserve Case");
		}
	    return ret;
    }

}
