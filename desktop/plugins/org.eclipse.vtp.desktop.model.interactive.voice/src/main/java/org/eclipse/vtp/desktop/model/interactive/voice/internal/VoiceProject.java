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
package org.eclipse.vtp.desktop.model.interactive.voice.internal;

import org.eclipse.core.resources.IProject;
import org.eclipse.vtp.desktop.model.interactive.core.IPromptSet;
import org.eclipse.vtp.desktop.model.interactive.core.InteractionType;
import org.eclipse.vtp.desktop.model.interactive.core.InteractionTypeManager;
import org.eclipse.vtp.desktop.model.interactive.core.internal.MediaProject;
import org.eclipse.vtp.desktop.model.interactive.core.internal.PromptSet;
import org.eclipse.vtp.desktop.model.interactive.voice.IVoiceMediaProject;
import org.eclipse.vtp.framework.interactions.core.media.IMediaProvider;
import org.w3c.dom.Element;

/**
 * This is a concrete implementation of <code>IVoiceProject</code>
 * and provides the default behavior of that interface.
 *
 * @author Trip Gilman
 * @version 2.0
 */
public class VoiceProject extends MediaProject implements IVoiceMediaProject
{
	private VoiceMediaProvider mediaProvider;
	private PromptSet promptSet;
	private String languagePackId;

	/**
	 * Creates a new <code>Personaproject</code> with the given
	 * eclipse project resource.
	 *
	 * @param project The eclipse project resource this persona project
	 * represents
	 */
	public VoiceProject(IProject project)
	{
		super(project);
		promptSet = new PromptSet(project.getFile("Voice.xml"), this);
		mediaProvider = new VoiceMediaProvider(this);
	}
	
	protected void loadConfig(Element rootElement)
	{
		super.loadConfig(rootElement);
		languagePackId = rootElement.getAttribute("language-pack-id");
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.project.IPersonaProject#getLanguagePackId()
	 */
	public String getLanguagePackId()
	{
		return languagePackId;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.core.project.IPersonaProject#getPromptSet()
	 */
	public IPromptSet getPromptSet()
	{
		return promptSet;
	}

	public IMediaProvider getMediaProvider()
	{
		return mediaProvider;
	}

	public InteractionType getInteractionType()
	{
		return InteractionTypeManager.getInstance().getType("org.eclipse.vtp.framework.interactions.voice.interaction");
	}
}
