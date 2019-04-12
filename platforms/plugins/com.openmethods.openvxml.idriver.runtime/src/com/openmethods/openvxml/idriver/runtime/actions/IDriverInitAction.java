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
package com.openmethods.openvxml.idriver.runtime.actions;

import java.lang.reflect.Method;
import java.util.Map;

import org.eclipse.vtp.framework.common.IDataObject;
import org.eclipse.vtp.framework.common.IMapObject;
import org.eclipse.vtp.framework.common.IStringObject;
import org.eclipse.vtp.framework.common.IValueObject;
import org.eclipse.vtp.framework.common.IVariableRegistry;
import org.eclipse.vtp.framework.core.IAction;
import org.eclipse.vtp.framework.core.IActionContext;
import org.eclipse.vtp.framework.core.IActionResult;
import org.eclipse.vtp.framework.util.Guid;

import com.openmethods.openvxml.idriver.runtime.configuration.IDriverConfiguration;

public class IDriverInitAction implements IAction
{
	/** The context to use. */
	private final IActionContext context;
	/** The configuration to use. */
	private final IDriverConfiguration configuration;
	private final IVariableRegistry variables;

	/**
	 * Creates a new MetaDataMessageAction.
	 * 
	 * @param logger The logger to use.
	 * @param conversation The conversation to use.
	 * @param configuration The configuration to use.
	 */
	public IDriverInitAction(IActionContext context, IVariableRegistry variables,
			IDriverConfiguration configuration)
	{
		this.context = context;
		this.variables = variables;
		this.configuration = configuration;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public IActionResult execute()
	{
		context.debug(getClass().getName().substring(
				getClass().getName().lastIndexOf('.') + 1));
		IDataObject obj = variables.getVariable(configuration.getPortVariable());
		if(obj != null && obj instanceof IValueObject)
		{
			try
			{
				long port = Long.parseLong(String.valueOf(((IValueObject)obj).toValue()));
				String callId = Guid.createGUID();
				obj = variables.getVariable(configuration.getCallIdVariable());
				if(obj != null && obj instanceof IValueObject)
				{
					Object value = ((IValueObject)obj).toValue();
					if(value != null)
						callId = String.valueOf(value);
				}
				IStringObject dnisObject = (IStringObject)variables.getVariable("Platform.DNIS");
				String dnis = "";
				if(dnisObject != null && dnisObject.getValue() != null)
					dnis = dnisObject.getValue();
				IStringObject aniObject = (IStringObject)variables.getVariable("Platform.ANI");
				String ani = "";
				if(aniObject != null && aniObject.getValue() != null)
					ani = aniObject.getValue();
				context.info("About to load dummy class");
				Class dummyClass = Class.forName("com.openmethods.openvxml.idriver.Dummy");
				context.info("loaded dummy class");
				Class driverClass = Class.forName("com.openmethods.openvxml.idriver.GenesysIDriver");
				Method instanceMethod = driverClass.getMethod("getInstance", null);
				Object instanceObject = instanceMethod.invoke(null, new Object[0]);
				Method startCallMethod = driverClass.getMethod("startCall", new Class[] { long.class, String.class, String.class, String.class});
				startCallMethod.invoke(instanceObject, port, callId, dnis, ani);
				context.setRootAttribute("idriver.port", new Long(port));
				Method getConnIdMethod = driverClass.getMethod("getConnId", new Class[] {long.class});
				String connId = (String)getConnIdMethod.invoke(instanceObject, port);
				System.out.println("Got conn ID " + connId);
				IStringObject connIdVariable = (IStringObject)variables.createVariable(IStringObject.TYPE_NAME);
				connIdVariable.setValue(connId);
				variables.setVariable(configuration.getConnIdVariable(), connIdVariable);
				System.out.println("set conn id into " + configuration.getConnIdVariable());
				Method getAllUDataMethod = driverClass.getMethod("getAllUData", new Class[] {long.class});
				Map<String, String> data = (Map<String, String>)getAllUDataMethod.invoke(instanceObject, port);
				System.out.println("Retrieved " + data.size() + " kv pairs");
				IMapObject dataVariable = (IMapObject)variables.createVariable(IMapObject.TYPE_NAME);
				for(Map.Entry<String, String> entry : data.entrySet())
				{
					System.out.println("Key: " + entry.getKey() + " Value: " + entry.getValue());
					IStringObject valueObject = (IStringObject)variables.createVariable(IStringObject.TYPE_NAME);
					valueObject.setValue(entry.getValue());
					dataVariable.setField(entry.getKey(), valueObject);
				}
				variables.setVariable("userData", dataVariable);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
		return context.createResult(IActionResult.RESULT_NAME_DEFAULT);
	}
}
