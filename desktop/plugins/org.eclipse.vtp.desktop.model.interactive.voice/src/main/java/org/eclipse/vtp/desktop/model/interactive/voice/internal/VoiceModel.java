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
/**
 * 
 */
package org.eclipse.vtp.desktop.model.interactive.voice.internal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.vtp.desktop.model.interactive.core.content.ContentLoadingManager;
import org.eclipse.vtp.framework.interactions.core.media.Content;
import org.eclipse.vtp.framework.util.XMLWriter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * A model that represents the voice project XML format.
 * 
 * @author Lonnie Pryor
 */
public class VoiceModel {
	/** The namespace URI used for the top-level elemenets. */
	public static final String NS_URI_VOICE = "http://eclipse.org/vtp/xml/media/voice#1.0"; //$NON-NLS-1$
	/** The prefix used for the top-level elemenets. */
	public static final String PREFIX_VOICE = "voice"; //$NON-NLS-1$
	/** The document element name. */
	private static final String ELEMENT_DESCRIPTOR = "descriptor"; //$NON-NLS-1$
	/** The shared-content element name. */
	private static final String ELEMENT_SHARED_CONTENT = "shared-content"; //$NON-NLS-1$
	/** The item-name attribute name. */
	private static final String ATTRIBUTE_ITEM_NAME = "item-name"; //$NON-NLS-1$
	/** The item-name attribute name. */
	private static final String QUALIFIED_ELEMENT_DESCRIPTOR = PREFIX_VOICE
			+ ":" + ELEMENT_DESCRIPTOR; //$NON-NLS-1$
	/** The item-name attribute name. */
	private static final String QUALIFIED_ELEMENT_SHARED_CONTENT = PREFIX_VOICE
			+ ":" + ELEMENT_SHARED_CONTENT; //$NON-NLS-1$
	/** The item-name attribute name. */
	private static final String QUALIFIED_ATTRIBUTE_ITEM_NAME = PREFIX_VOICE
			+ ":" + ATTRIBUTE_ITEM_NAME; //$NON-NLS-1$

	/** The file this model maps to. */
	private final IFile descriptor;
	/** The mapping of gloabl shared content entries by name. */
	private final Map<String, Content> sharedContent = new TreeMap<String, Content>();
	/** The observers notified when the model is changed. */
	private final List<Runnable> changeObservers = new LinkedList<Runnable>();

	/**
	 * Creates a new VoiceModel.
	 */
	public VoiceModel(IFile descriptor) {
		this.descriptor = descriptor;
	}

	/**
	 * Creates a new VoiceModel.
	 */
	public VoiceModel(VoiceProject project) {
		this.descriptor = project.getPromptSet().getUnderlyingFile();
	}
	
	public IProject getProject()
	{
		return descriptor.getProject();
	}

	/**
	 * Returns the names of all the shared content items in this model.
	 * 
	 * @return The names of all the shared content items in this model.
	 */
	public String[] getSharedContentNames() {
		return sharedContent.keySet().toArray(
				new String[sharedContent.size()]);
	}

	/**
	 * Returns the shared content value under the specified name or
	 * <code>null</code> if no such content exists in this model.
	 * 
	 * @param name
	 *            The name of the content to return.
	 * @return The shared content value under the specified name or
	 *         <code>null</code> if no such content exists in this model.
	 */
	public Content getSharedContent(String name) {
		return sharedContent.get(name);
	}

	/**
	 * Sets the shared content item registered under the specified name.
	 * 
	 * @param name
	 *            The name to regster the shared content item under.
	 * @param content
	 *            The value of the shared content item or <code>null</code> to
	 *            remove the specified item.
	 */
	public void putSharedContent(String name, Content content) {
		if (content == null)
			removeSharedContent(name);
		else {
			sharedContent.put(name, content);
			fireChangeEvent();
		}
	}

	/**
	 * Removes the shared content item under the specified name.
	 * 
	 * @param name
	 *            The name of the shared content item to remove.
	 */
	public void removeSharedContent(String name) {
		if (sharedContent.remove(name) != null)
			fireChangeEvent();
	}

	/**
	 * Load the descriptor represented by this model.
	 * 
	 * @throws IOException
	 *             If the descriptor cannot be loaded.
	 */
	public void load() throws IOException {
		sharedContent.clear();
		// Load and parse the XML document.
		Document doc = null;
		InputStream input = null;
		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
					.newInstance();
			docBuilderFactory.setNamespaceAware(true);
			docBuilderFactory.setValidating(false);
			doc = docBuilderFactory.newDocumentBuilder().parse(
					input = descriptor.getContents());
		} catch (CoreException e) {
			IOException ex = new IOException("Could not load XML document: "
					+ e.getMessage());
			ex.initCause(e);
			throw ex;
		} catch (ParserConfigurationException e) {
			IOException ex = new IOException(
					"Could not initialize XML parser: " + e.getMessage());
			ex.initCause(e);
			throw ex;
		} catch (SAXException e) {
			IOException ex = new IOException("Could not parse XML document: "
					+ e.getMessage());
			ex.initCause(e);
			throw ex;
		} finally {
			try {
				if (input != null)
					input.close();
			} catch (IOException e) {
				// Ignore errors thrown when the input stream is closed.
			}
		}
		// Extract the shared content entries.
		NodeList sharedContentList = doc.getDocumentElement()
				.getElementsByTagNameNS(NS_URI_VOICE, ELEMENT_SHARED_CONTENT);
		if (sharedContentList.getLength() > 0) {
			Element sharedContentElement = (Element) sharedContentList.item(0);
			NodeList contentList = sharedContentElement.getChildNodes();
			for (int i = 0; i < contentList.getLength(); i++) {
				if (!(contentList.item(i) instanceof Element))
					continue;
				Element contentElement = (Element) contentList.item(i);
				String name = contentElement.getAttributeNS(NS_URI_VOICE,
						ATTRIBUTE_ITEM_NAME);
				if (name == null)
					continue;
				Content content = ContentLoadingManager.getInstance()
						.loadContent(contentElement);
				if (content == null)
					continue;
				sharedContent.put(name, content);
			}
		}
	}

	/**
	 * Saves the descriptor represented by this model.
	 * 
	 * @param monitor
	 *            The progress monitor to notify or <code>null</code> if no
	 *            monitoring is desired.
	 * @throws IOException
	 *             If the descriptor cannot be loaded.
	 */
	public void save(IProgressMonitor monitor) throws IOException {
		// Create a new XML document.
		Document doc = null;
		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
					.newInstance();
			docBuilderFactory.setNamespaceAware(true);
			docBuilderFactory.setValidating(false);
			doc = docBuilderFactory.newDocumentBuilder().newDocument();
		} catch (ParserConfigurationException e) {
			IOException ex = new IOException(
					"Could not initialize XML encoder: " + e.getMessage());
			ex.initCause(e);
			throw ex;
		}
		Element desriptorElement = doc.createElementNS(NS_URI_VOICE,
				QUALIFIED_ELEMENT_DESCRIPTOR);
		doc.appendChild(desriptorElement);
		// Create the shared content entries.
		Element sharedContentElement = doc.createElementNS(NS_URI_VOICE,
				QUALIFIED_ELEMENT_SHARED_CONTENT);
		desriptorElement.appendChild(sharedContentElement);
		for (Map.Entry<String, Content> entry : sharedContent.entrySet())
		{
			Element contentElement = entry.getValue().store(sharedContentElement);
			if (contentElement == null)
				continue;
			contentElement.setAttributeNS(NS_URI_VOICE,
					QUALIFIED_ATTRIBUTE_ITEM_NAME, String.valueOf(entry
							.getKey()));
		}
		// Write the document to disk.
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			TransformerFactory.newInstance().newTransformer().transform(
					new DOMSource(doc), new XMLWriter(
							baos).toXMLResult());
		} catch (TransformerConfigurationException e) {
			IOException ex = new IOException(
					"Could not initialize XML transformer: " + e.getMessage());
			ex.initCause(e);
			throw ex;
		} catch (TransformerException e) {
			IOException ex = new IOException(
					"Could not transform XML document: " + e.getMessage());
			ex.initCause(e);
			throw ex;
		}
		try {
			descriptor.setContents(
					new ByteArrayInputStream(baos.toByteArray()), true, true,
					monitor);
		} catch (CoreException e) {
			IOException ex = new IOException("Could not save XML document: "
					+ e.getMessage());
			ex.initCause(e);
			throw ex;
		}
	}

	/**
	 * Adds a change observer to this model.
	 * 
	 * @param observer
	 *            The observer to add.
	 */
	public void addChangeObserver(Runnable observer) {
		changeObservers.add(observer);
	}

	/**
	 * Removes a change observer from this model.
	 * 
	 * @param observer
	 *            The observer to remove.
	 */
	public void removeChangeObserver(Runnable observer) {
		changeObservers.remove(observer);
	}

	/**
	 * Fires a change event to all the registered listeners.
	 */
	private void fireChangeEvent()
	{
		for (Runnable r : changeObservers)
			r.run();
	}
}
