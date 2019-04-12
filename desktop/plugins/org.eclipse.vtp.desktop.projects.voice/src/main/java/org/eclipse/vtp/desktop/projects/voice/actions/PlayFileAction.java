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
package org.eclipse.vtp.desktop.projects.voice.actions;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;

import org.eclipse.ui.actions.SelectionListenerAction;
import org.eclipse.vtp.desktop.model.interactive.core.IMediaFile;

/**
 * Used in context menus to initiate the creation of a new application.
 * This action is not view or perspective specific.  The resulting
 * wizard will be centered on the current shell associated with the
 * current UI thread.  This action will terminate upon opening of the
 * wizard dialog, and does not block until its completion or cancellation.
 *
 * @author Trip
 * @version 1.0
 */
public class PlayFileAction extends SelectionListenerAction
{
	/**
	 * Constructs a new <code>CreateApplicationAction</code> instance with the
	 * default values.
	 */
	public PlayFileAction()
	{
		super("Play File");
	}

	@Override
	public void run()
	{
		IMediaFile mediaFile = (IMediaFile)this.getStructuredSelection().getFirstElement();
	AudioFormat uLawformat =
		new AudioFormat(AudioFormat.Encoding.ULAW, 8000, 8, 1, 1, 8000,
			true);

	try
	{
		AudioInputStream pcmais = null;
		if(mediaFile.getName().toLowerCase().endsWith(".vox"))
		{
			AudioInputStream ais =
				new AudioInputStream(mediaFile.open(), uLawformat,
					mediaFile.length());
			pcmais = AudioSystem.getAudioInputStream(AudioFormat.Encoding.PCM_SIGNED,
					ais);
		}
		else
		{
			AudioInputStream ais =
				AudioSystem.getAudioInputStream(mediaFile.open());
			pcmais = AudioSystem.getAudioInputStream(AudioFormat.Encoding.PCM_SIGNED,
					ais);
		}

		DataLine.Info dinfo =
			new DataLine.Info(Clip.class, pcmais.getFormat());
		Clip currentClip = (Clip)AudioSystem.getLine(dinfo);
		currentClip.open(pcmais);
		currentClip.start();
	}
	catch(Exception e)
	{
		e.printStackTrace();
	}
	}
}
