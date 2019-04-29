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
package com.openmethods.openvxml.platforms.vxmlbwithidriver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.vtp.framework.common.IVariableRegistry;
import org.eclipse.vtp.framework.core.IExecutionContext;
import org.eclipse.vtp.framework.interactions.core.commands.EndMessageCommand;
import org.eclipse.vtp.framework.interactions.core.commands.MetaDataMessageCommand;
import org.eclipse.vtp.framework.interactions.core.platforms.IDocument;
import org.eclipse.vtp.framework.interactions.core.platforms.ILinkFactory;
import org.eclipse.vtp.framework.interactions.core.platforms.IRenderingQueue;
import org.eclipse.vtp.framework.interactions.voice.services.TimeValue;
import org.eclipse.vtp.framework.interactions.voice.services.VoicePlatform;
import org.eclipse.vtp.framework.interactions.voice.vxml.Dialog;
import org.eclipse.vtp.framework.interactions.voice.vxml.VXMLDocument;

import com.openmethods.openvxml.idriver.GenesysIDriver;

/**
 * A generic implementation of a AVP-specific VXML platform.
 * 
 * @author Trip Gilman
 */
public class VXMLBWithIDriverPlatform extends VoicePlatform
{
	@SuppressWarnings("unused")
	private final IVariableRegistry variables;
	private IExecutionContext context;
	
	/**
	 * Creates a new AvpPlatform.
	 */
	public VXMLBWithIDriverPlatform(IExecutionContext context, IVariableRegistry variables)
	{
		super(context);
		this.context = context;
		this.variables = variables;
	}
 
	public TimeValue getMinimumTimeValue(String property)
	{
		return new TimeValue(100);
	}
	
	@Override
	public void generateInitialVariableRequests(Map<String, String> variables) {
		// TODO Auto-generated method stub
		super.generateInitialVariableRequests(variables);
		variables.put("vxmlbInviteURI", "session.connection.cti.vars['sip_req_uri']");
		variables.put("vxmlbFromUser", "session.connection.cti.vars['sip_from_user']");
	}

	@Override
	public List<String> getPlatformVariableNames() {
		// TODO Auto-generated method stub
		List<String> names = super.getPlatformVariableNames();
		names.add("vxmlbInviteURI");
		names.add("vxmlbFromUser");
		return names;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.interactions.core.support.AbstractPlatform#
	 *      createDocument(
	 *      org.eclipse.vtp.framework.interactions.core.platforms.ILinkFactory,
	 *      org.eclipse.vtp.framework.interactions.core.platforms.IRenderingQueue)
	 */
	public IDocument createDocument(ILinkFactory links,
			IRenderingQueue renderingQueue)
	{
		links.setUrlEncoded(false);
		return super.createDocument(links, renderingQueue);
	}

	@Override
    protected VXMLDocument createVXMLDocument(ILinkFactory links, Dialog dialog)
    {
		Long port = (Long)this.context.getRootAttribute("idriver.port");
		if(port != null)
		{
			GenesysIDriver.getInstance().updateCall(port);
		}
		VXMLDocument document = super.createVXMLDocument(links, dialog);
		document.setProperty("documentmaxage", "0"); //$NON-NLS-1$ //$NON-NLS-2$
		document.setProperty("documentmaxstale", "0"); //$NON-NLS-1$ //$NON-NLS-2$
		return document;
    }
	
	@Override
	protected IDocument renderEndMessage(ILinkFactory links,
			EndMessageCommand endMessageCommand)
	{
		new Exception().printStackTrace();
		IDocument ret = super.renderEndMessage(links, endMessageCommand);
		Long port = (Long)this.context.getRootAttribute("idriver.port");
		if(port != null)
		{
			GenesysIDriver.getInstance().endCall(port);
		}
		return ret;
	}

	@Override
	protected IDocument renderMetaDataMessage(ILinkFactory links,
			MetaDataMessageCommand metaDataMessageCommand)
	{
		Long port = (Long)this.context.getRootAttribute("idriver.port");
		if(port != null)
		{
			String[] names = metaDataMessageCommand.getMetaDataNames();
			Map<String, String> data = new HashMap<String, String>();
			for(String name : names)
			{
				data.put(name, metaDataMessageCommand.getMetaDataValue(name));
			}
			GenesysIDriver.getInstance().addUDataList(port, data, false);
		}
		return null; //no document needed as it's all done through API
	}

}
