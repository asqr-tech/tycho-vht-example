/*--------------------------------------------------------------------------
 * Copyright (c) 2004, 2006-2007 OpenMethods, LLC
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Trip Gilman (OpenMethods), Lonnie G. Pryor (OpenMethods), 
 *    Randy Childers (OpenMethods)
 *    - initial API and implementation
 -------------------------------------------------------------------------*/
package org.eclipse.vtp.framework.interactions.core.actions;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;

import org.eclipse.vtp.framework.common.IBooleanObject;
import org.eclipse.vtp.framework.common.IBrand;
import org.eclipse.vtp.framework.common.IBrandSelection;
import org.eclipse.vtp.framework.common.IDataObject;
import org.eclipse.vtp.framework.common.IDateObject;
import org.eclipse.vtp.framework.common.IDecimalObject;
import org.eclipse.vtp.framework.common.INumberObject;
import org.eclipse.vtp.framework.common.IStringObject;
import org.eclipse.vtp.framework.common.IVariableRegistry;
import org.eclipse.vtp.framework.core.IAction;
import org.eclipse.vtp.framework.core.IActionContext;
import org.eclipse.vtp.framework.core.IActionResult;
import org.eclipse.vtp.framework.core.IReporter;
import org.eclipse.vtp.framework.interactions.core.configurations.MetaDataConfiguration;
import org.eclipse.vtp.framework.interactions.core.configurations.MetaDataItemConfiguration;
import org.eclipse.vtp.framework.interactions.core.conversation.IConversation;
import org.eclipse.vtp.framework.interactions.core.platforms.IPlatformSelector;
import org.eclipse.vtp.framework.interactions.core.support.AbstractPlatform;

/**
 * An action that enqueues a meta-data request.
 * 
 * @author Lonnie Pryor
 */
/**
 * @author trip
 *
 */
/**
 * @author trip
 *
 */
public class MetaDataRequestAction implements IAction
{
	/** The context to use. */
	private final IActionContext context;
	/** The conversation to use. */
	private final IConversation conversation;
	/** The configuration to use. */
	private final MetaDataConfiguration configuration;
	/** The variable registry to use. */
	private final IVariableRegistry variables;
	/** The currently selected brand. */
	private final IBrandSelection brandSelection;
	private final IPlatformSelector platformSelector;

	/**
	 * Creates a new MetaDataMessageAction.
	 * 
	 * @param context The context to use.
	 * @param conversation The conversation to use.
	 * @param configuration The configuration to use.
	 * @param variables The variable registry to use.
	 */
	public MetaDataRequestAction(IActionContext context,
			IConversation conversation, MetaDataConfiguration configuration,
			IVariableRegistry variables, IBrandSelection brandSelection,
			IPlatformSelector platformSelector)
	{
		this.context = context;
		this.conversation = conversation;
		this.configuration = configuration;
		this.variables = variables;
		this.brandSelection = brandSelection;
		this.platformSelector = platformSelector;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.framework.core.IAction#execute()
	 */
	public IActionResult execute()
	{
		String resultParameterName = ACTION_PREFIX + context.getActionID().replace(':', '_');
		try
		{
			if(context.isDebugEnabled()) context.debug(getClass().getName().substring(
					getClass().getName().lastIndexOf('.') + 1));
			String value = context.getParameter(resultParameterName);
			context.clearParameter(resultParameterName);
			if ("success.filled".equals(value))
			{
				if(context.isReportingEnabled())
				{
					Dictionary<String, String> props = new Hashtable<String, String>();
					props.put("event", "metadata.request.filled");
					context.report(IReporter.SEVERITY_INFO, "Received meta-data.", props);
				}
				IBrand brand = brandSelection.getSelectedBrand();
				MetaDataItemConfiguration[] items = null;
				for (; brand != null && items == null; brand = brand.getParentBrand())
				{
					items = configuration.getItem(brand.getName() + ""
							+ "");
				}
				AbstractPlatform platform = (AbstractPlatform)platformSelector.getSelectedPlatform();
				Map<?, ?> dataMap = platform.processMetaDataResponse(configuration, context);
				if (items != null)
				{
					for (int i = 0; i < items.length; ++i)
					{
						IDataObject object = variables.getVariable(items[i].getValue());
						if (object == null)
							variables.setVariable(items[i].getValue(), object = variables
									.createVariable(IStringObject.TYPE_NAME));
						IDataObject toSet = object;
						String primaryField = object.getType().getPrimaryFieldName();
						if (primaryField != null)
							toSet = object.getField(primaryField);
						if (toSet instanceof IBooleanObject)
							((IBooleanObject)toSet).setValue(dataMap.get(items[i]
									.getName()));
						else if (toSet instanceof IDateObject)
							((IDateObject)toSet).setValue(dataMap.get(items[i]
									.getName()));
						else if (toSet instanceof IDecimalObject)
							((IDecimalObject)toSet).setValue(dataMap.get(items[i]
									.getName()));
						else if (toSet instanceof INumberObject)
							((INumberObject)toSet).setValue(dataMap.get(items[i]
									.getName()));
						else if (toSet instanceof IStringObject)
							((IStringObject)toSet).setValue(dataMap.get(items[i]
									.getName()));
					}
				}
				return context.createResult(IActionResult.RESULT_NAME_DEFAULT);
			}
			else if (IConversation.RESULT_NAME_HANGUP.equals(value))
			{
				if(context.isReportingEnabled())
				{
					Dictionary<String, String> props = new Hashtable<String, String>();
					props.put("event", "error.disconnect.hangup");
					context.report(IReporter.SEVERITY_INFO,
						"Got disconnect during interaction.", props);
				}
				return context.createResult(IConversation.RESULT_NAME_HANGUP);
			}
			else if(value != null)
			{
				return context.createResult(value);
			}
			else
			{
				if(context.isReportingEnabled())
				{
					Dictionary<String, String> props = new Hashtable<String, String>();
					props.put("event", "metadata.request.before");
					context.report(IReporter.SEVERITY_INFO, "Requesting meta-data.", props);
				}
				if (conversation.createMetaDataRequest(configuration, resultParameterName).enqueue())
					return context.createResult(IActionResult.RESULT_NAME_REPEAT);
			}
		}
		catch (Exception e)
		{
			return context.createResult("error.meta-data.request", e); //$NON-NLS-1$
		}
		return context.createResult("error.meta-data.request"); //$NON-NLS-1$
	}
}
