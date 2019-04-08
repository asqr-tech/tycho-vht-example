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

import org.eclipse.vtp.framework.common.IScriptingEngine;
import org.eclipse.vtp.framework.common.IScriptingService;
import org.eclipse.vtp.framework.interactions.core.media.IDataSet;
import org.eclipse.vtp.framework.interactions.core.media.InlineInputGrammar;
import org.eclipse.vtp.framework.interactions.core.media.InputGrammar;
import org.eclipse.vtp.framework.util.XMLUtilities;
import org.w3c.dom.Element;

/**
 * @author lonnie
 * 
 */
public class DynamicInputGrammar extends InlineInputGrammar
{
	public static final String ELEMENT_NAME = "dynamic-input-grammar"; //$NON-NLS-1$
	
	private String script = "var grammar = '<grammar mode=\"DTMF\" version=\"1.0\" root=\"ROOT\">';\r\ngrammar += '<rule id=\"ROOT\" scope=\"public\">';\r\n\r\n/*Insert your grammar text here*/\r\n\r\ngrammar += '</rule></grammar>';";

	public DynamicInputGrammar()
	{
		super();
	}
	
	public DynamicInputGrammar(Element element)
	{
		super();
		script = XMLUtilities.getElementTextDataNoEx(element, true);
		if(script == null)
			script = "";
	}

	public boolean isDataAware()
	{
		return true;
	}
	
	public InputGrammar captureData(IScriptingService scriptingService, IDataSet dataSet)
	{
		try
		{
			IScriptingEngine engine = scriptingService.createScriptingEngine("JavaScript");
			Object obj = engine.execute(script);
			if(obj != null && obj instanceof String)
			{
				TextInputGrammar ret = new TextInputGrammar();
				ret.setText((String)obj);
				return ret;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return this;
	}
	
	public Element store(Element element)
	{
		Element thisElement = element.getOwnerDocument().createElementNS(
				"http://www.eclipse.org/vtp/media/voice/input", ELEMENT_NAME); //$NON-NLS-1$
		element.appendChild(thisElement);
		thisElement.setTextContent(script);
		return thisElement;
	}

	public String toString()
	{
		return "DYNAMIC INLINE GRAMMAR";
	}

	public String getInputGrammarType()
    {
	    return "org.eclipse.vtp.framework.interactions.voice.media.input.dynamic";
    }
	
	public String getScript()
	{
		return script;
	}
	
	public void setScript(String script)
	{
		this.script = script;
	}

	@Override
	public String getGrammarText()
	{
		return "Dynamic grammar was not processed properly";
	}
}
