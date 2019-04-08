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
package org.eclipse.vtp.framework.common.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.vtp.framework.common.ILastResult;
import org.eclipse.vtp.framework.common.ILastResultData;
import org.eclipse.vtp.framework.common.IScriptable;
import org.eclipse.vtp.framework.core.ISessionContext;

/**
 * 
 * @author Lonnie Pryor
 */
public class LastResult implements ILastResult, IScriptable
{
	/** The context to use. */
	private final ISessionContext context;
	
	/**
	 * Creates a new LastResult.
	 * 
	 * @param context The context to use.
	 * @param brandRegistry The brand registry to use.
	 */
	public LastResult(ISessionContext context)
	{
		this.context = context;
	}

	public ILastResultData addResult(int confidence, String utterance,
            String inputMode, String interpretation)
    {
		String lengthString = (String)context.getAttribute("lastresult.length");
		if(lengthString == null || "".equals(lengthString))
			lengthString = "0";
		int length = Integer.parseInt(lengthString);
		context.setAttribute("lastresult." + length + ".confidence", Integer.toString(confidence));
		context.setAttribute("lastresult." + length + ".utterance", utterance);
		context.setAttribute("lastresult." + length + ".inputmode", inputMode);
		context.setAttribute("lastresult." + length + ".interpretation", interpretation);
		context.setAttribute("lastresult.length", Integer.toString(length + 1));
	    return new LastResultData(confidence, utterance, inputMode, interpretation);
    }

	public void clear()
    {
		String lengthString = (String)context.getAttribute("lastresult.length");
		if(lengthString == null || "".equals(lengthString))
			return;
		int length = Integer.parseInt(lengthString);
		for(int i = 0; i < length; i++)
		{
			context.clearAttribute("lastresult." + i + ".confidence");
			context.clearAttribute("lastresult." + i + ".utterance");
			context.clearAttribute("lastresult." + i + ".inputmode");
			context.clearAttribute("lastresult." + i + ".interpretation");
		}
		context.clearAttribute("lastresult.length");
		context.clearAttribute("lastresult.markname");
		context.clearAttribute("lastresult.marktime");
    }

	public List<ILastResultData> getResults()
    {
		String lengthString = (String)context.getAttribute("lastresult.length");
		if(lengthString == null || "".equals(lengthString))
			return Collections.emptyList();
		int length = Integer.parseInt(lengthString);
		List<ILastResultData> ret = new ArrayList<ILastResultData>(length);
		for(int i = 0; i < length; i++)
		{
			String confidenceString = (String)context.getAttribute("lastresult." + i + ".confidence");
			int confidence = Integer.parseInt(confidenceString);
			String utterance = (String)context.getAttribute("lastresult." + i + ".utterance");
			String inputMode = (String)context.getAttribute("lastresult." + i + ".inputmode");
			String interpretation = (String)context.getAttribute("lastresult." + i + ".interpretation");
			ret.add(new LastResultData(confidence, utterance, inputMode, interpretation));
		}
	    return ret;
    }

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#getName()
	 */
	public final String getName()
	{
		return "LastResult"; //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#hasValue()
	 */
	public boolean hasValue()
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#toValue()
	 */
	public Object toValue()
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#getFunctionNames()
	 */
	public final String[] getFunctionNames()
	{
		return new String[] {};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#invokeFunction(
	 *      java.lang.String, java.lang.Object[])
	 */
	public final Object invokeFunction(String name, Object[] arguments)
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#hasItem(int)
	 */
	public final boolean hasItem(int index)
	{
		String lengthString = (String)context.getAttribute("lastresult.length");
		if(lengthString == null || "".equals(lengthString))
			lengthString = "0";
		int length = Integer.parseInt(lengthString);
		return index >= 0 && index < length;
	}

	public String[] getPropertyNames()
	{
		return new String[] {"length", "confidence", "utterance", "inputmode", "interpretation", "markname", "marktime"};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#hasEntry(
	 *      java.lang.String)
	 */
	public final boolean hasEntry(String name)
	{
		return "length".equals(name) || "confidence".equals(name) || "utterance".equals(name) || "inputmode".equals(name) || "interpretation".equals(name) || "markname".equals(name) || "marktime".equals(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#getItem(int)
	 */
	public final Object getItem(int index)
	{
		String lengthString = (String)context.getAttribute("lastresult.length");
		if(lengthString == null || "".equals(lengthString))
			return null;
		String confidenceString = (String)context.getAttribute("lastresult." + index + ".confidence");
		int confidence = Integer.parseInt(confidenceString);
		String utterance = (String)context.getAttribute("lastresult." + index + ".utterance");
		String inputMode = (String)context.getAttribute("lastresult." + index + ".inputmode");
		String interpretation = (String)context.getAttribute("lastresult." + index + ".interpretation");
		return new LastResultData(confidence, utterance, inputMode, interpretation);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#getEntry(
	 *      java.lang.String)
	 */
	public final Object getEntry(String name)
	{
		if("length".equals(name))
		{
			String lengthString = (String)context.getAttribute("lastresult.length");
			if(lengthString == null || "".equals(lengthString))
				lengthString = "0";
			int length = Integer.parseInt(lengthString);
			return new Integer(length);
		}
		ILastResultData lrd = null;
		List<ILastResultData> results = getResults();
		if(results.size() > 0)
		{
			lrd = results.get(0);
		}
		if("confidence".equals(name))
		{
			return lrd == null ? null : new Integer(lrd.getConfidence());
		}
		if("utterance".equals(name))
		{
			return lrd == null ? null : lrd.getUtterence();
		}
		if("inputmode".equals(name))
		{
			return lrd == null ? null : lrd.getInputMode();
		}
		if("interpretation".equals(name))
		{
			return lrd == null ? null : lrd.getInterpretation();
		}
		if("markname".equals(name))
		{
			String markName = (String)context.getAttribute("lastresult.markname");
			if(markName == null)
				markName = "";
			return markName;
		}
		if("marktime".equals(name))
		{
			String markTime = (String)context.getAttribute("lastresult.marktime");
			if(markTime == null)
				markTime = "0";
			return markTime;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#setItem(int,
	 *      java.lang.Object)
	 */
	public final boolean setItem(int index, Object value)
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#setEntry(
	 *      java.lang.String, java.lang.Object)
	 */
	public final boolean setEntry(String name, Object value)
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#clearItem(int)
	 */
	public final boolean clearItem(int index)
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.scripting.IScriptable#clearEntry(
	 *      java.lang.String)
	 */
	public final boolean clearEntry(String name)
	{
		return false;
	}

	private class LastResultData implements ILastResultData, IScriptable
	{
		private int confidence = 0;
		private String utterance = null;
		private String inputMode = null;
		private String interpretation = null;
		
		public LastResultData(int confidence, String utterance, String inputMode, String interpretation)
		{
			super();
			this.confidence = confidence;
			this.utterance = utterance;
			this.inputMode = inputMode;
			this.interpretation = interpretation;
		}

		public int getConfidence()
        {
	        return confidence;
        }

		public String getInputMode()
        {
	        return inputMode;
        }

		public String getInterpretation()
        {
	        return interpretation;
        }

		public String getUtterence()
        {
	        return utterance;
        }

		public boolean clearEntry(String name)
        {
	        return false;
        }

		public boolean clearItem(int index)
        {
	        return false;
        }

		public Object getEntry(String name)
        {
			if("confidence".equals(name))
			{
				return new Integer(confidence);
			}
			if("utterance".equals(name))
			{
				return utterance;
			}
			if("inputmode".equals(name))
			{
				return inputMode;
			}
			if("interpretation".equals(name))
			{
				return interpretation;
			}
			return null;
        }

		public String[] getFunctionNames()
        {
	        return new String[] {};
        }

		public Object getItem(int index)
        {
	        return null;
        }

		public String getName()
        {
	        return null;
        }

		public String[] getPropertyNames()
		{
			return new String[] {"confidence", "utterance", "inputmode", "interpretation"};
		}

		public boolean hasEntry(String name)
        {
			return "confidence".equals(name) || "utterance".equals(name) || "inputmode".equals(name) || "interpretation".equals(name);
        }

		public boolean hasItem(int index)
        {
	        return false;
        }

		public boolean hasValue()
        {
	        return false;
        }

		public Object invokeFunction(String name, Object[] arguments)
        {
	        return null;
        }

		public boolean setEntry(String name, Object value)
        {
	        return false;
        }

		public boolean setItem(int index, Object value)
        {
	        return false;
        }

		public Object toValue()
        {
	        return null;
        }

		@Override
		public boolean isMutable()
		{
			// TODO Auto-generated method stub
			return false;
		}
		
	}

	public String getMarkName()
    {
		String markName = (String)context.getAttribute("lastresult.markname");
		if(markName == null)
			markName = "";
		return markName;
    }

	public String getMarkTime()
    {
		String markTime = (String)context.getAttribute("lastresult.marktime");
		if(markTime == null)
			markTime = "0";
		return markTime;
    }

	public void setMarkName(String name)
    {
		context.setAttribute("lastresult.markname", name);
    }

	public void setMarkTime(String time)
    {
		context.setAttribute("lastresult.marktime", time);
    }

	@Override
	public boolean isMutable()
	{
		// TODO Auto-generated method stub
		return false;
	}
}
