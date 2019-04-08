/*--------------------------------------------------------------------------
 * Copyright (c) 2004, 2006 OpenMethods, LLC
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Trip Gilman (OpenMethods), Lonnie G. Pryor (OpenMethods),
 *    T.D. Barnes (OpenMethods) - initial API and implementation
 -------------------------------------------------------------------------*/
package org.eclipse.vtp.framework.interactions.voice.vxml;

import java.util.LinkedList;
import java.util.List;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * The <code>Recording</code> class represents the &lt;record&gt; VXML
 * element.
 * 
 * @author Trip Gilman
 * @author Lonnie Pryor
 * @version 2.0
 */
public class Recording extends FormElement
{
	/** The beep toggle setting. */
	private boolean beepEnabled = false;
	/** The DTMF toggle setting. */
	private boolean dtmfTermEnabled = true;
	/** The maximum recording time setting. */
	private String maxtime = null;
	/** The final silence setting. */
	private String finalSilence = null;
	/** The file type setting. */
	private String fileType = null;
	/** The time out of the opening prompt. */
	private String timeout = null;
	/** The opening prompt played to the caller. */
	private Prompt prompt = null;
	/** The properties of this recording. */
	private final PropertiesSupport properties = new PropertiesSupport();
	/** The grammar used to collect the field input. */
	private List<Grammar> grammars = new LinkedList<Grammar>();
	/** The filled handlers registered with this recording. */
	private final LinkedList<Filled> filledHandlers = new LinkedList<Filled>();
	/** The event handlers registered with this recording. */
	private final LinkedList<EventHandler> eventHandlers = new LinkedList<EventHandler>();

	/**
	 * Creates a new instance of a <code>Recording</code> element.
	 * 
	 * @param name The name of the recording element.
	 * @throws IllegalArgumentException If the specified name is empty.
	 * @throws NullPointerException If the specified name is <code>null</code>.
	 */
	public Recording(String name) throws IllegalArgumentException,
			NullPointerException
	{
		super(name);
	}

	/**
	 * Creates a new instance of a <code>Recording</code> element.
	 * 
	 * @param name The name of the recording element.
	 * @param expression An expression that will set the initial value of the
	 *          recording element.
	 * @throws IllegalArgumentException If the specified name is empty.
	 * @throws IllegalArgumentException If the specified expression is empty.
	 * @throws NullPointerException If the specified name is <code>null</code>.
	 */
	public Recording(String name, String expression)
			throws IllegalArgumentException, NullPointerException
	{
		super(name, expression);
	}

	/**
	 * Creates a new instance of a <code>Recording</code> element.
	 * 
	 * @param name The name of the recording element.
	 * @param expression An expression that will set the initial value of the
	 *          recording element.
	 * @param condition An expression that equates to a boolean value which
	 *          determines if the recording will be executed.
	 * @throws IllegalArgumentException If the specified name is empty.
	 * @throws IllegalArgumentException If the specified expression is empty.
	 * @throws IllegalArgumentException If the specified condition is empty.
	 * @throws NullPointerException If the specified name is <code>null</code>.
	 */
	public Recording(String name, String expression, String condition)
			throws IllegalArgumentException, NullPointerException
	{
		super(name, expression, condition);
	}

	/**
	 * Returns the DTMF grammar to apply to the caller's input.
	 * 
	 * @return the DTMF grammar to apply to the caller's input.
	 */
	public Grammar[] getGrammar()
	{
		return grammars.toArray(new Grammar[grammars.size()]);
	}

	/**
	 * Specifies the DTMF grammar to match the caller's entered digits against. If
	 * the input does not conform to the rules of the grammar, a "nomatch" event
	 * is generated.
	 * 
	 * @param grammar The DTMF grammar to apply to the caller's input.
	 */
	public void addGrammar(Grammar grammar)
	{
		this.grammars.add(grammar);
	}

	/**
	 * Returns the beep toggle setting.
	 * 
	 * @return The beep toggle setting.
	 */
	public boolean isBeepEnabled()
	{
		return beepEnabled;
	}

	/**
	 * Returns the DTMF toggle setting.
	 * 
	 * @return The DTMF toggle setting.
	 */
	public boolean isDtmfTermEnabled()
	{
		return dtmfTermEnabled;
	}

	/**
	 * Returns the maximum recording time setting.
	 * 
	 * @return The maximum recording time setting.
	 */
	public String getMaxtime()
	{
		return maxtime;
	}

	/**
	 * Returns the final silence setting.
	 * 
	 * @return The final silence setting.
	 */
	public String getFinalSilence()
	{
		return finalSilence;
	}

	/**
	 * Returns the file type setting.
	 * 
	 * @return The file type setting.
	 */
	public String getFileType()
	{
		return fileType;
	}

	/**
	 * Returns the time out of the opening prompt.
	 * 
	 * @return The time out of the opening prompt.
	 */
	public String getTimeout()
	{
		return timeout;
	}

	/**
	 * Returns the opening prompt played to the caller.
	 * 
	 * @return The opening prompt played to the caller.
	 */
	public Prompt getPrompt()
	{
		return prompt;
	}

	/**
	 * Returns the names of the properties of this recording.
	 * 
	 * @return The names of the properties of this recording.
	 */
	public String[] getPropertyNames()
	{
		return properties.getPropertyNames();
	}

	/**
	 * Returns the value of the specified property or <code>null</code> if no
	 * such property exists.
	 * 
	 * @param propertyName The name of the property to find the value of.
	 * @return The value of the specified property or <code>null</code> if no
	 *         such property exists.
	 * @throws NullPointerException If the supplied property name is
	 *           <code>null</code>.
	 */
	public String getPropertyValue(String propertyName)
			throws NullPointerException
	{
		return properties.getPropertyValue(propertyName);
	}

	/**
	 * Returns the list of filled handlers for this recording.
	 * 
	 * @return The list of filled handlers for this recording.
	 */
	Filled[] getFilledHandlers()
	{
		return filledHandlers.toArray(new Filled[filledHandlers.size()]);
	}

	/**
	 * Returns the list of event handlers for this recording.
	 * 
	 * @return The list of event handlers for this recording.
	 */
	EventHandler[] getEventHandlers()
	{
		return eventHandlers.toArray(new EventHandler[eventHandlers
				.size()]);
	}

	/**
	 * Sets the beep toggle setting.
	 * 
	 * @param beepEnabled The beep toggle setting.
	 */
	public void setBeepEnabled(boolean beepEnabled)
	{
		this.beepEnabled = beepEnabled;
	}

	/**
	 * Sets the DTMF toggle setting.
	 * 
	 * @param dtmfTermEnabled The DTMF toggle setting.
	 */
	public void setDtmfTermEnabled(boolean dtmfTermEnabled)
	{
		this.dtmfTermEnabled = dtmfTermEnabled;
	}

	/**
	 * Sets the maximum recording time setting.
	 * 
	 * @param maxtime The maximum recording time setting.
	 * @throws IllegalArgumentException If the specified setting is empty.
	 */
	public void setMaxtime(String maxtime) throws IllegalArgumentException
	{
		if (maxtime != null && maxtime.length() == 0)
			throw new IllegalArgumentException("maxtime"); //$NON-NLS-1$
		this.maxtime = maxtime;
	}

	/**
	 * Sets the final silence setting.
	 * 
	 * @param finalSilence The final silence setting.
	 * @throws IllegalArgumentException If the specified setting is empty.
	 */
	public void setFinalSilence(String finalSilence)
			throws IllegalArgumentException
	{
		if (finalSilence != null && finalSilence.length() == 0)
			throw new IllegalArgumentException("finalSilence"); //$NON-NLS-1$
		this.finalSilence = finalSilence;
	}

	/**
	 * Sets the file type setting.
	 * 
	 * @param fileType The file type setting.
	 * @throws IllegalArgumentException If the specified setting is empty.
	 */
	public void setFileType(String fileType) throws IllegalArgumentException
	{
		if (fileType != null && fileType.length() == 0)
			throw new IllegalArgumentException("fileType"); //$NON-NLS-1$
		this.fileType = fileType;
	}

	/**
	 * Sets the time out of the opening prompt.
	 * 
	 * @param timeout The time out of the opening prompt.
	 * @throws IllegalArgumentException If the specified setting is empty.
	 */
	public void setTimeout(String timeout) throws IllegalArgumentException
	{
		if (timeout != null && timeout.length() == 0)
			throw new IllegalArgumentException("timeout"); //$NON-NLS-1$
		this.timeout = timeout;
		if (this.prompt != null)
			this.prompt.setTimeout(timeout);
	}

	/**
	 * Sets the opening prompt played to the caller.
	 * 
	 * @param prompt The opening prompt played to the caller.
	 */
	public void setPrompt(Prompt prompt)
	{
		this.prompt = prompt;
		if (prompt != null)
			prompt.setTimeout(timeout);
	}

	/**
	 * Sets the value of a property in this recording.
	 * 
	 * @param propertyName The name of the property to set.
	 * @param propertyValue The value to set the property to.
	 * @throws NullPointerException If the supplied property name or value is
	 *           <code>null</code>.
	 */
	public void setProperty(String propertyName, String propertyValue)
			throws NullPointerException
	{
		properties.setProperty(propertyName, propertyValue);
	}

	/**
	 * Clears the value of a property in this recording.
	 * 
	 * @param propertyName The name of the property to clear.
	 * @throws NullPointerException If the supplied property name is
	 *           <code>null</code>.
	 */
	public void clearProperty(String propertyName) throws NullPointerException
	{
		properties.clearProperty(propertyName);
	}

	/**
	 * Adds the specified filled handler to this recording. The filled handlers
	 * will be executed in the order they were added.
	 * 
	 * @param filled The filled handler to be added.
	 * @throws NullPointerException If the supplied filled handler is
	 *           <code>null</code>.
	 */
	public void addFilledHandler(Filled filled) throws NullPointerException
	{
		if (filled == null)
			throw new NullPointerException("filled"); //$NON-NLS-1$
		filledHandlers.add(filled);
	}

	/**
	 * Removes the specified filled handler from this recording.
	 * 
	 * @param filled The filled handler to be removed.
	 * @throws NullPointerException If the supplied filled handler is
	 *           <code>null</code>.
	 */
	public void removeFilledHandler(Filled filled) throws NullPointerException
	{
		if (filled == null)
			throw new NullPointerException("filled"); //$NON-NLS-1$
		filledHandlers.remove(filled);
	}

	/**
	 * Adds the specified event handler to this recording. The event handlers are
	 * evaluated in the order they were added.
	 * 
	 * @param eventHandler The event handler to add.
	 * @throws NullPointerException If the supplied event handler is
	 *           <code>null</code>.
	 */
	public void addEventHandler(EventHandler eventHandler)
			throws NullPointerException
	{
		if (eventHandler == null)
			throw new NullPointerException("eventHandler"); //$NON-NLS-1$
		eventHandlers.add(eventHandler);
	}

	/**
	 * Removes the specified event handler from this recording.
	 * 
	 * @param eventHandler The event handler to remove.
	 * @throws NullPointerException If the supplied event handler is
	 *           <code>null</code>.
	 */
	public void removeEventHandler(EventHandler eventHandler)
			throws NullPointerException
	{
		if (eventHandler == null)
			throw new NullPointerException("eventHandler"); //$NON-NLS-1$
		eventHandlers.remove(eventHandler);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.core.output.Widget#writeWidget(
	 *      org.xml.sax.ContentHandler)
	 */
	public void writeWidget(ContentHandler outputHandler)
			throws NullPointerException, SAXException
	{
		if (outputHandler == null)
			throw new NullPointerException("outputHandler"); //$NON-NLS-1$
		// Start the element.
		AttributesImpl attributes = new AttributesImpl();
		writeAttributes(attributes);
		outputHandler.startElement(NAMESPACE_URI_VXML, NAME_RECORD, NAME_RECORD,
				attributes);
		// Write the children.
		writeProperties(outputHandler);
		writePrompt(outputHandler);
		writeFilledHandlers(outputHandler);
		writeEventHandlers(outputHandler);
		// End the element.
		outputHandler.endElement(NAMESPACE_URI_VXML, NAME_RECORD, NAME_RECORD);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.voice.output.FormElement#
	 *      writeAttributes(org.xml.sax.helpers.AttributesImpl)
	 */
	protected void writeAttributes(AttributesImpl attributes)
	{
		super.writeAttributes(attributes);
		writeAttribute(attributes, null, null, NAME_BEEP, TYPE_CDATA, String
				.valueOf(beepEnabled));
		writeAttribute(attributes, null, null, NAME_DTMFTERM, TYPE_CDATA, String
				.valueOf(dtmfTermEnabled));
		if (maxtime != null)
			writeAttribute(attributes, null, null, NAME_MAXTIME, TYPE_CDATA, maxtime);
		if (finalSilence != null)
			writeAttribute(attributes, null, null, NAME_FINALSILENCE, TYPE_CDATA,
					finalSilence);
		if (fileType != null)
			writeAttribute(attributes, null, null, NAME_TYPE, TYPE_CDATA, fileType);
	}

	/**
	 * Writes this field's prompt to the specified content handler.
	 * 
	 * @param outputHandler The handler to write the properties to.
	 * @throws SAXException If the writing of the prompt fails.
	 * @throws NullPointerException If the supplied content handler is
	 *           <code>null</code>.
	 */
	protected void writePrompt(ContentHandler outputHandler)
			throws NullPointerException, SAXException
	{
		if (prompt != null)
			prompt.writeWidget(outputHandler);
	}

	/**
	 * Writes the properties of this field to the specified content handler.
	 * 
	 * @param outputHandler The handler to write the properties to.
	 * @throws SAXException If the writing of the properties fails.
	 * @throws NullPointerException If the supplied content handler is
	 *           <code>null</code>.
	 */
	protected void writeProperties(ContentHandler outputHandler)
			throws NullPointerException, SAXException
	{
		properties.writeWidget(outputHandler);
	}

	/**
	 * Write the filled handlers of this field to the specified content handler.
	 * 
	 * @param outputHandler The content handler to write to.
	 * @throws NullPointerException If the supplied content handler is
	 *           <code>null</code>.
	 * @throws SAXException If the writing of one of the filled handlers fails.
	 */
	protected void writeFilledHandlers(ContentHandler outputHandler)
			throws NullPointerException, SAXException
	{
		writeChildren(outputHandler, filledHandlers);
	}

	/**
	 * Write the event handlers of this field to the specified content handler.
	 * 
	 * @param outputHandler The content handler to write to.
	 * @throws NullPointerException If the supplied content handler is
	 *           <code>null</code>.
	 * @throws SAXException If the writing of one of the event handlers fails.
	 */
	protected void writeEventHandlers(ContentHandler outputHandler)
			throws NullPointerException, SAXException
	{
		writeChildren(outputHandler, eventHandlers);
	}
}
