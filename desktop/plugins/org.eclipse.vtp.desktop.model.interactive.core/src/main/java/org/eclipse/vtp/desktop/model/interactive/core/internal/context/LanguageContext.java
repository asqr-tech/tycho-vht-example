/**
 * 
 */
package org.eclipse.vtp.desktop.model.interactive.core.internal.context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.vtp.desktop.model.core.IWorkflowProject;
import org.eclipse.vtp.desktop.model.core.configuration.ConfigurationContext;
import org.eclipse.vtp.desktop.model.interactive.core.IInteractiveWorkflowProject;
import org.eclipse.vtp.desktop.model.interactive.core.InteractionType;

/**
 * @author trip
 *
 */
public class LanguageContext implements ConfigurationContext
{
	public static final String CONTEXT_ID = "org.eclipse.vtp.desktop.model.interactive.core.languagecontext";
	private static final String CONTEXT_NAME = "Language";
	private IWorkflowProject project = null;
	private String interactionType = null;

	/**
	 * 
	 */
	public LanguageContext()
	{
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.configuration.ConfigurationContext#getId()
	 */
	public String getId()
	{
		return CONTEXT_ID;
	}
	
	public String getName()
	{
		return CONTEXT_NAME;
	}
	
	public void setProject(IWorkflowProject project)
	{
		this.project = project;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.configuration.ConfigurationContext#getLabel(java.lang.Object)
	 */
	public String getLabel(Object obj)
	{
		return obj.toString();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.configuration.ConfigurationContext#getValues()
	 */
	public List<Object> getValues()
	{
		List<Object> ret = new ArrayList<Object>();
		if(interactionType != null)
		{
			IInteractiveWorkflowProject interactiveProject = (IInteractiveWorkflowProject)project;
			ret.addAll(interactiveProject.getSupportedLanguages(interactionType));
		}
		return ret;
	}

	public boolean setConfigurationContext(Map<String, Object> values)
	{
		InteractionType newInteractionType = (InteractionType)values.get(InteractionTypeContext.CONTEXT_ID);
		if((newInteractionType == null && interactionType == null) || newInteractionType.getId().equals(interactionType))
			return false;
		interactionType = newInteractionType == null ? null : newInteractionType.getId();
		return true;
	}

}
