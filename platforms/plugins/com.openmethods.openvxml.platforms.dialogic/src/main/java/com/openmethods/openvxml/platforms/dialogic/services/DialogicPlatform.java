package com.openmethods.openvxml.platforms.dialogic.services;

import java.util.List;
import java.util.Map;

import org.eclipse.vtp.framework.core.IExecutionContext;
import org.eclipse.vtp.framework.interactions.core.platforms.ILinkFactory;
import org.eclipse.vtp.framework.interactions.voice.services.VoicePlatform;
import org.eclipse.vtp.framework.interactions.voice.vxml.Dialog;
import org.eclipse.vtp.framework.interactions.voice.vxml.VXMLDocument;

public class DialogicPlatform extends VoicePlatform
{
	
	public DialogicPlatform(IExecutionContext context)
	{
		super(context);
	}
	
	@Override
	public void generateInitialVariableRequests(Map<String, String> variables)
	{
		super.generateInitialVariableRequests(variables);
		if(variables.containsKey("PLATFORM_ANI"))
			variables.remove("PLATFORM_ANI");
		if(variables.containsKey("PLATFORM_DNIS"))
			variables.remove("PLATFORM_DNIS");
		variables.put("PLATFORM_ANI", "session.telephone.ani");
		variables.put("PLATFORM_DNIS", "session.telephone.dnis");
		
		//This variable seems to be causing an error. Accordingly, it has been disabled.
		//variables.put("criticaldigit_timer", "com.snowshore.criticaldigit_timer"); 
		
		//TODO maybe set up an object structure for these protocol.x, protocol.sip.y etc?
		variables.put("protocolName", "session.connection.protocol.name");
		variables.put("protocolVersion", "session.connection.protocol.version");
		
		variables.put("sipUri", "session.connection.protocol.sip.uri");
		variables.put("sipTo", "session.connection.protocol.sip.to");
		variables.put("sipFrom", "session.connection.protocol.sip.from");
		variables.put("sipCall_id", "session.connection.protocol.sip.call_id");
		
		//TODO Add support for the arrays
		//session.connection.protocol.sip.parameter[n].name
		//session.connection.protocol.sip.parameter[n].value
	}

	@Override
	public List<String> getPlatformVariableNames()
	{
		List<String> names = super.getPlatformVariableNames();
		//This variable seems to be causing an error. Accordingly, it has been disabled.
		//names.add("criticaldigit_timer");
		names.add("protocolName");
		names.add("protocolVersion");
		names.add("sipUri");
		names.add("sipTo");
		names.add("sipFrom");
		names.add("sipCall_id");
		return names;
	}

	@Override
    protected VXMLDocument createVXMLDocument(ILinkFactory links, Dialog dialog)
    {
		VXMLDocument document = super.createVXMLDocument(links, dialog);
		document.setProperty("documentmaxage", "0"); //$NON-NLS-1$ //$NON-NLS-2$
		document.setProperty("documentmaxstale", "0"); //$NON-NLS-1$ //$NON-NLS-2$
		document.setVersion(VERSION_2_0);
		return document;
    }

}
