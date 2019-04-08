package org.eclipse.vtp.framework.interactions.voice.services;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeValue
{
	public static final int MILLIS = 1;
	public static final int SECONDS = 2;
	public static final int MINUTES = 4;
	public static final int HOURS = 8;
	public static final int DAYS = 16;
	
	private long millis;
	private Pattern decimalPattern = Pattern.compile("\\d*(\\.\\d+)?");
	
	public TimeValue(String value)
	{
		super();
		Pattern timePattern = Pattern.compile("(\\d*(?:\\.\\d+)?[a-zA-Z]+)");
		Matcher matcher = timePattern.matcher(value);
		if(!matcher.find())
		{
			Pattern nakedNumberPattern = Pattern.compile("(\\d*(?:\\.\\d+))?");
			Matcher numberMatcher = nakedNumberPattern.matcher(value);
			if(numberMatcher.find())
			{
				addGroup(value + "s");
				return;
			}
			throw new IllegalArgumentException("Invalid time value: " + value);
		}
		addGroup(matcher.group());
		while(matcher.find())
		{
			addGroup(matcher.group());
		}
	}
	
	public TimeValue(long millis)
	{
		this.millis = millis;
	}

	private void addGroup(String group)
	{
		if(group.endsWith("ms"))
		{
			millis += Double.parseDouble(group.substring(0, group.length() - 2));
		}
		else if(group.endsWith("s"))
		{
			millis += Double.parseDouble(group.substring(0, group.length() - 1)) * 1000;
		}
		else if(group.endsWith("m"))
		{
			millis += Double.parseDouble(group.substring(0, group.length() - 1)) * 60000;
		}
		else if(group.endsWith("h"))
		{
			millis += Double.parseDouble(group.substring(0, group.length() - 1)) * 3600000;
		}
		else if(group.endsWith("d"))
		{
			millis += Double.parseDouble(group.substring(0, group.length() - 1)) * 86400000;
		}
	}
	
	public String toTimeString(int groups)
	{
		long local = millis;
		if(local == 0)
		{
			if((groups & DAYS) == DAYS)
				return "0d";
			if((groups & HOURS) == HOURS)
				return "0h";
			if((groups & MINUTES) == MINUTES)
				return "0m";
			if((groups & SECONDS) == SECONDS)
				return "0s";
			if((groups & MILLIS) == MILLIS)
				return "0ms";
		}
		StringBuilder buf = new StringBuilder();
		if((groups & DAYS) == DAYS)
		{
			double days = local / 86400000d;
			long wholeDays = (long)Math.floor(days);
			if(wholeDays > 0)
			{
				buf.append(wholeDays);
				local -= wholeDays * 86400000;
			}
			if(local > 0 && (groups & (DAYS - 1)) == 0)
			{
				double partialDays = local / 86400000d;
				Matcher decimalMatcher = decimalPattern.matcher(Double.toString(partialDays));
				if(decimalMatcher.find())
				{
					buf.append(decimalMatcher.group());
				}
			}
			buf.append('d');
		}
		if((groups & HOURS) == HOURS)
		{
			double hours = local / 3600000d;
			long wholeHours = (long)Math.floor(hours);
			if(wholeHours > 0)
			{
				buf.append(wholeHours);
				local -= wholeHours * 3600000;
			}
			if(local > 0 && (groups & (HOURS - 1)) == 0)
			{
				double partialHours = local / 3600000d;
				Matcher decimalMatcher = decimalPattern.matcher(Double.toString(partialHours));
				if(decimalMatcher.find())
				{
					buf.append(decimalMatcher.group());
				}
			}
			buf.append('h');
		}
		if((groups & MINUTES) == MINUTES)
		{
			double minutes = local / 60000d;
			long wholeMinutes = (long)Math.floor(minutes);
			if(wholeMinutes > 0)
			{
				buf.append(wholeMinutes);
				local -= wholeMinutes * 60000;
			}
			if(local > 0 && (groups & (MINUTES - 1)) == 0)
			{
				double partialMinutes = local / 60000d;
				Matcher decimalMatcher = decimalPattern.matcher(Double.toString(partialMinutes));
				if(decimalMatcher.find())
				{
					buf.append(decimalMatcher.group());
				}
			}
			buf.append('m');
		}
		if((groups & SECONDS) == SECONDS)
		{
			double seconds = local / 1000d;
			long wholeSeconds = (long)Math.floor(seconds);
			if(wholeSeconds > 0)
			{
				buf.append(wholeSeconds);
				local -= wholeSeconds * 1000;
			}
			if(local > 0 && (groups & (SECONDS - 1)) == 0)
			{
				double partialSeconds = local / 1000d;
				Matcher decimalMatcher = decimalPattern.matcher(Double.toString(partialSeconds));
				if(decimalMatcher.find())
				{
					buf.append(decimalMatcher.group());
				}
			}
			buf.append('s');
		}
		if((groups & MILLIS) == MILLIS)
		{
			if(local > 0)
			{
				buf.append(local);
				buf.append("ms");
			}
		}
		return buf.toString();
	}
	
	public boolean equals(Object obj)
	{
		if(super.equals(obj))
			return true;
		if(obj instanceof String)
		{
			try
			{
				obj = new TimeValue((String)obj);
			}
			catch (Exception e)
			{
				return false;
			}
		}
		if(obj instanceof TimeValue)
		{
			return millis == ((TimeValue)obj).millis;
		}
		return false;
	}
	
	public int compareTo(Object obj)
	{
		if(super.equals(obj))
			return 0;
		if(obj instanceof String)
		{
			try
			{
				obj = new TimeValue((String)obj);
			}
			catch (Exception e)
			{
				throw new ClassCastException("Could not compare to " + obj.getClass().getName());
			}
		}
		if(obj instanceof TimeValue)
		{
			long dif = millis - ((TimeValue)obj).millis;
			if(dif < 0)
				return -1;
			else if(dif > 0)
				return 1;
			return 0;
		}
		throw new ClassCastException("Could not compare to " + obj.getClass().getName());
	}
	
	public static void main(String[] args)
	{
		TimeValue t = new TimeValue(3);
		System.out.println(t.toTimeString(SECONDS));
		System.out.println(t.toTimeString(MILLIS));
		t = new TimeValue("0");
		System.out.println(t.toTimeString(SECONDS));
		System.out.println(t.toTimeString(MILLIS));
		t = new TimeValue("3.0");
		System.out.println(t.toTimeString(SECONDS));
		System.out.println(t.toTimeString(MILLIS));
		t = new TimeValue("3s");
		System.out.println(t.toTimeString(SECONDS));
		System.out.println(t.toTimeString(MILLIS));
		t = new TimeValue("3ms");
		System.out.println(t.toTimeString(SECONDS));
		System.out.println(t.toTimeString(MILLIS));
		t = new TimeValue("3m");
		System.out.println(t.toTimeString(SECONDS));
		System.out.println(t.toTimeString(MILLIS));
		t = new TimeValue("");
		System.out.println(t.toTimeString(SECONDS));
		System.out.println(t.toTimeString(MILLIS));
		TimeValue min = new TimeValue(0);
		TimeValue max = new TimeValue(Long.MAX_VALUE);
		System.out.println(min.compareTo(t));
		System.out.println(t.compareTo(max));
	}
}
