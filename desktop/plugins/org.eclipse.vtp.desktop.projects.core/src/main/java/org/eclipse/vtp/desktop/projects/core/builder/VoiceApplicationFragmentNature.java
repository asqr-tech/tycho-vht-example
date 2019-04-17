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
package org.eclipse.vtp.desktop.projects.core.builder;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

/**
 * This eclipse project nature identifies a project as an OpenVXML
 * voice application project.  Only projects with this nature or
 * the <code>VoicePersonaNature</code> are visible in the OpenVXML
 * application designer projects view.
 * 
 * Note: This project nature and it's associated builder have been deprecated.
 * They remain in this plug-in for now to support triggering conversion actions
 * in the navigator context menu.  They will be removed in the next major update
 * to the system or earlier if another trigger mechanism is constructed.
 *
 * @author Trip Gilman
 * @deprecated
 */
public class VoiceApplicationFragmentNature implements IProjectNature
{
	/**
	 * Constant string containing the nature id
	 */
	public static final String NATURE_ID =
		"org.eclipse.vtp.desktop.projects.core.VoiceApplicationFragmentNature";

	/**
	 * The project to which this nature applies.
	 */
	private IProject project;

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.core.resources.IProjectNature#configure()
	 */
	public void configure() throws CoreException
	{
		IProjectDescription desc = project.getDescription();
		ICommand[] commands = desc.getBuildSpec();

		for(int i = 0; i < commands.length; ++i)
		{
			if(commands[i].getBuilderName()
							  .equals(VoiceApplicationFragmentBuilder.BUILDER_ID))
			{
				return;
			}
		}

		ICommand[] newCommands = new ICommand[commands.length + 1];
		System.arraycopy(commands, 0, newCommands, 0, commands.length);

		ICommand command = desc.newCommand();
		command.setBuilderName(VoiceApplicationFragmentBuilder.BUILDER_ID);
		newCommands[newCommands.length - 1] = command;
		desc.setBuildSpec(newCommands);
		project.setDescription(desc, null);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.core.resources.IProjectNature#deconfigure()
	 */
	public void deconfigure() throws CoreException
	{
		IProjectDescription description = getProject().getDescription();
		ICommand[] commands = description.getBuildSpec();

		for(int i = 0; i < commands.length; ++i)
		{
			if(commands[i].getBuilderName()
							  .equals(VoiceApplicationFragmentBuilder.BUILDER_ID))
			{
				ICommand[] newCommands = new ICommand[commands.length - 1];
				System.arraycopy(commands, 0, newCommands, 0, i);
				System.arraycopy(commands, i + 1, newCommands, i,
					commands.length - i - 1);
				description.setBuildSpec(newCommands);

				return;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.core.resources.IProjectNature#getProject()
	 */
	public IProject getProject()
	{
		return project;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.core.resources.IProjectNature#setProject(org.eclipse.core.resources.IProject)
	 */
	public void setProject(IProject project)
	{
		this.project = project;
	}
}
