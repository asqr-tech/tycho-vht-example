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
package org.eclipse.vtp.desktop.model.legacy.v3_xTo4_0.view;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.SelectionListenerAction;
import org.eclipse.vtp.desktop.model.legacy.v3_xTo4_0.ProjectConverter;
import org.eclipse.vtp.desktop.model.legacy.v3_xTo4_0.VoiceConverter;
import org.eclipse.vtp.desktop.model.legacy.v3_xTo4_0.dialogs.ConversionSelectionDialog;
import org.eclipse.vtp.desktop.projects.core.builder.VoiceApplicationFragmentNature;
import org.eclipse.vtp.desktop.projects.core.builder.VoiceApplicationNature;

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
@SuppressWarnings("deprecation")
public class ConvertWorkflowProjectAction extends SelectionListenerAction
{
	/**
	 * Constructs a new <code>CreateApplicationAction</code> instance with the
	 * default values.
	 */
	public ConvertWorkflowProjectAction()
	{
		super("Convert 3.x VTP Project");
	}

	@Override
	public void run()
	{
		ConversionSelectionDialog dialog = new ConversionSelectionDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		if(dialog.open() == Dialog.OK)
		{
			ProjectConverter pc = new ProjectConverter();
			VoiceConverter voiceConverter = new VoiceConverter();
			List<IProject> projects = dialog.getProjectsToConvert();
			for(IProject project : projects)
			{
				try
				{
					if(project.hasNature(VoiceApplicationNature.NATURE_ID) || project.hasNature(VoiceApplicationFragmentNature.NATURE_ID))
					{	
						pc.convertProject(project);
					}
					else
					{
						voiceConverter.convertVoice(project);
					}
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
			}
		}
	}
}
