package com.openmethods.openvxml.platforms.genesys.vxml;


import java.util.LinkedList;
import java.util.Vector;

import org.eclipse.vtp.framework.interactions.voice.vxml.EventHandler;
import org.eclipse.vtp.framework.interactions.voice.vxml.Filled;
import org.eclipse.vtp.framework.interactions.voice.vxml.FormElement;
import org.eclipse.vtp.framework.interactions.voice.vxml.Parameter;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * @author Trip
 * @version
 */
public class UserData extends FormElement
{
	protected Vector filledHandlers;
	protected Vector parameters;
	protected boolean doGet = true;
	/** The event handlers registered with this user data object. */
	private final LinkedList eventHandlers = new LinkedList();

	/**
	 * @param name
	 */
	public UserData(String name)
	{
		super(name);
		init();
	}

	/**
	 * @param name
	 * @param expression
	 */
	public UserData(String name, String expression)
	{
		super(name, expression);
		init();
	}

	/**
	 * @param name
	 * @param expression
	 * @param condition
	 */
	public UserData(String name, String expression, String condition)
	{
		super(name, expression, condition);
		init();
	}

	public void init()
	{
		filledHandlers = new Vector();
		parameters = new Vector();
	}

	public void setDoGet(boolean b)
	{
		doGet = b;
	}

	/**
	 * Returns the list of filled handlers for this field.
	 * 
	 * @return The list of filled handlers for this field.
	 */
	public Filled[] getFilledHandlers()
	{
		return (Filled[])filledHandlers.toArray(new Filled[filledHandlers.size()]);
	}

	public void addFilledHandler(Filled filledHandler)
	{
		if(filledHandler == null)
		{
			throw new IllegalArgumentException("Filled handler cannot be null.");
		}

		filledHandlers.addElement(filledHandler);
	}

	/**
	 * Removes the specified filled handler from this field.<br>
	 * <br>
	 * NOTE: A filled handler element that is added to a field must NOT specify a
	 * mode. Although this caveat is not mentioned in the w3c spec, many
	 * implementations do not allow mode to be declared in this situation.
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

	public void addParameter(Parameter parameter)
	{
		if(parameter == null)
		{
			throw new IllegalArgumentException("Parameter cannot be null.");
		}

		parameters.addElement(parameter);
	}

	/**
	 * Returns the list of event handlers for this field.
	 * 
	 * @return The list of event handlers for this field.
	 */
	public EventHandler[] getEventHandlers()
	{
		return (EventHandler[])eventHandlers.toArray(new EventHandler[eventHandlers
				.size()]);
	}

	/**
	 * Adds the specified event handler to this field. The event handlers are
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
	 * Removes the specified event handler from this field.
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

	public void writeWidget(ContentHandler outputHandler)
	throws NullPointerException, SAXException
	{
		if (outputHandler == null)
			throw new NullPointerException("outputHandler"); //$NON-NLS-1$
		// Start the element.
		AttributesImpl attributes = new AttributesImpl();
		writeAttributes(attributes);
		outputHandler.startElement(NAMESPACE_URI_VXML, "object", "object",
				attributes);
		// Write the children.
		writeParameters(outputHandler);
		writeFilledHandlers(outputHandler);
		writeEventHandlers(outputHandler);
		// End the element.
		outputHandler.endElement(NAMESPACE_URI_VXML, "object", "object");
	}

	/**
	 * @param out
	 * @throws IOException
	 */
	protected void writeAttributes(AttributesImpl attributes)
	{
		super.writeAttributes(attributes);
		writeAttribute(attributes, null, null, "classid", TYPE_CDATA, "CRData:" + (doGet ? "get" : "put"));
	}

	/**
	 * Writes the properties of this field to the specified content handler.
	 * 
	 * @param outputHandler The handler to write the properties to.
	 * @throws SAXException If the writing of the properties fails.
	 * @throws NullPointerException If the supplied content handler is
	 *           <code>null</code>.
	 */
	protected void writeParameters(ContentHandler outputHandler)
			throws NullPointerException, SAXException
	{
		writeChildren(outputHandler, parameters);
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
