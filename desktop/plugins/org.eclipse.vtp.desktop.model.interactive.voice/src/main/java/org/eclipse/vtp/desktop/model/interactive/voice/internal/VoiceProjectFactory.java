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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.vtp.desktop.model.interactive.core.IMediaProject;
import org.eclipse.vtp.desktop.model.interactive.core.IMediaProjectFactory;
import org.eclipse.vtp.desktop.model.interactive.voice.natures.VoiceProjectNature;
import org.eclipse.vtp.framework.util.Guid;

public class VoiceProjectFactory implements IMediaProjectFactory
{

	public IMediaProject convertToMediaProject(IProject project)
	{
		return new VoiceProject(project);
	}

	public IMediaProject createMediaProject(String name, String languagePackId)
	{
		IProject newProject = ResourcesPlugin.getWorkspace().getRoot()
	    .getProject(name);
		try
		{
			newProject.create(null);
			newProject.open(null);
			
			IFolder mfFolder = newProject.getFolder("Media Files");
			mfFolder.create(true, true, null);
			
			IFile configDocument = newProject.getFile(".config");
			InputStream templateIn = this.getClass().getClassLoader().getResourceAsStream("config_template.xml");
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buf = new byte[10240];
			int len = templateIn.read(buf);
			while(len != -1)
			{
				baos.write(buf, 0, len);
				len = templateIn.read(buf);
			}
			templateIn.close();
			String template = baos.toString();
			template = template.replaceAll("\\[\\[id\\]\\]", Guid.createGUID());
			template = template.replaceAll("\\[\\[language-pack-id\\]\\]", languagePackId);
			configDocument.create(new ByteArrayInputStream(template.getBytes()), true, null);

			IFile mainDesignDocument = newProject.getFile("Voice.xml");
			templateIn = this.getClass().getClassLoader().getResourceAsStream("voice_template.xml");
			baos = new ByteArrayOutputStream();
			len = templateIn.read(buf);
			while(len != -1)
			{
				baos.write(buf, 0, len);
				len = templateIn.read(buf);
			}
			templateIn.close();
			template = baos.toString();
			mainDesignDocument.create(new ByteArrayInputStream(template.getBytes()), true, null);
	
			IProjectDescription desc = newProject.getDescription();
			desc.setNatureIds(new String[] {VoiceProjectNature.NATURE_ID});
			newProject.setDescription(desc, null);
			return convertToMediaProject(newProject);
		}
		catch(Exception ce)
		{
			ce.printStackTrace();
		}
		return null;
	}

}
