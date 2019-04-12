// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   VXMLBVoicePlatform.java

package com.openmethods.openvxml.platforms.vxmlb.services;

import java.util.List;
import java.util.Map;

import org.eclipse.vtp.framework.common.IStringObject;
import org.eclipse.vtp.framework.common.IVariableRegistry;
import org.eclipse.vtp.framework.core.IExecutionContext;
import org.eclipse.vtp.framework.interactions.core.commands.BridgeMessageCommand;
import org.eclipse.vtp.framework.interactions.core.platforms.IDocument;
import org.eclipse.vtp.framework.interactions.core.platforms.ILink;
import org.eclipse.vtp.framework.interactions.core.platforms.ILinkFactory;
import org.eclipse.vtp.framework.interactions.core.services.ExtendedActionEventManager;
import org.eclipse.vtp.framework.interactions.voice.services.VoicePlatform;
import org.eclipse.vtp.framework.interactions.voice.vxml.Catch;
import org.eclipse.vtp.framework.interactions.voice.vxml.Dialog;
import org.eclipse.vtp.framework.interactions.voice.vxml.ElseIf;
import org.eclipse.vtp.framework.interactions.voice.vxml.Filled;
import org.eclipse.vtp.framework.interactions.voice.vxml.Form;
import org.eclipse.vtp.framework.interactions.voice.vxml.Goto;
import org.eclipse.vtp.framework.interactions.voice.vxml.If;
import org.eclipse.vtp.framework.interactions.voice.vxml.Transfer;
import org.eclipse.vtp.framework.interactions.voice.vxml.VXMLDocument;

public class VXMLBVoicePlatform extends VoicePlatform
{
	private IVariableRegistry variableRegistry;

    public VXMLBVoicePlatform(IExecutionContext context, IVariableRegistry variableRegistry)
    {
    	super(context);
    	this.variableRegistry = variableRegistry;
    }

	@Override
    protected VXMLDocument createVXMLDocument(ILinkFactory links, Dialog dialog)
    {
		VXMLDocument document = super.createVXMLDocument(links, dialog);
		document.setProperty("documentmaxage", "0"); //$NON-NLS-1$ //$NON-NLS-2$
		document.setProperty("documentmaxstale", "0"); //$NON-NLS-1$ //$NON-NLS-2$
		return document;
    }

	public void generateInitialVariableRequests(Map variables)
    {
        super.generateInitialVariableRequests(variables);
        variables.put("ctiID", "session.connection.cti_uuid");
        variables.put("aai", "session.connection.aai");
    }

    public List getPlatformVariableNames()
    {
        List names = super.getPlatformVariableNames();
        names.add("ctiID");
        names.add("aai");
        return names;
    }

	@Override
	protected IDocument renderBridgeMessage(ILinkFactory links,
			BridgeMessageCommand bridgeMessageCommand)
	{
		Form form = new Form("BridgeMessageForm"); //$NON-NLS-1$
		Transfer tx = new Transfer("BridgeMessageElement", //$NON-NLS-1$
				bridgeMessageCommand.getDestination());
		IStringObject aaiVariable = (IStringObject)variableRegistry.getVariable("ctiAAI");
		System.out.println("Looking for AAI information: " + aaiVariable);
		if(aaiVariable != null)
		{
			String aai = aaiVariable.getValue();
			System.out.println("AAI Value: " + aai);
			if(aai != null && !"".equals(aai))
			{
				aai = aai.replaceAll("\\\\\"", "\\\"");
				tx.setAAI(aai);
//				Variable aaiVXMLVariable = new Variable("newAAI", aai);
//				form.addVariable(aaiVXMLVariable);
			}
		}
		tx.setMaxTime("0s");
		tx.setTransferType(bridgeMessageCommand.getTransferType());
		Filled filled = new Filled();
		If ifBusy = new If("BridgeMessageElement == 'busy'");
		ILink link = links.createNextLink();
		link.setParameter(bridgeMessageCommand.getResultName(),
				bridgeMessageCommand.getBusyResultValue());
		ifBusy.addAction(new Goto(link.toString()));
		ElseIf ifNetworkBusy = new ElseIf("BridgeMessageElement == 'network_busy'");
		link.setParameter(bridgeMessageCommand.getResultName(),
				bridgeMessageCommand.getBusyResultValue());
		ifNetworkBusy.addAction(new Goto(link.toString()));
		ifBusy.addElseIf(ifNetworkBusy);
		ElseIf ifNoAnswer = new ElseIf("BridgeMessageElement == 'noanswer'");
		link.setParameter(bridgeMessageCommand.getResultName(),
				bridgeMessageCommand.getUnavailableResultValue());
		ifNoAnswer.addAction(new Goto(link.toString()));
		ifBusy.addElseIf(ifNoAnswer);
		ElseIf ifUnknown = new ElseIf("BridgeMessageElement == 'unknown'");
		link.setParameter(bridgeMessageCommand.getResultName(),
				bridgeMessageCommand.getUnavailableResultValue());
		ifUnknown.addAction(new Goto(link.toString()));
		ifBusy.addElseIf(ifUnknown);
		filled.addIfClause(ifBusy);
		tx.addFilledHandler(filled);
		
		//catch handlers
		Catch noAuthCatch = new Catch("error.connection.noauthorization");
		ILink noAuthLink = links.createNextLink();
		noAuthLink.setParameter(bridgeMessageCommand.getResultName(),
				bridgeMessageCommand.getNoAuthResultValue());
		noAuthCatch.addAction(new Goto(noAuthLink.toString()));
		tx.addEventHandler(noAuthCatch);
		
		Catch badDestCatch = new Catch("error.connection.baddestination");
		ILink badDestLink = links.createNextLink();
		badDestLink.setParameter(bridgeMessageCommand.getResultName(),
				bridgeMessageCommand.getBadDestResultValue());
		badDestCatch.addAction(new Goto(badDestLink.toString()));
		tx.addEventHandler(badDestCatch);
		
		Catch noRouteCatch = new Catch("error.connection.noroute");
		ILink noRouteLink = links.createNextLink();
		noRouteLink.setParameter(bridgeMessageCommand.getResultName(),
				bridgeMessageCommand.getNoRouteResultValue());
		noRouteCatch.addAction(new Goto(noRouteLink.toString()));
		tx.addEventHandler(noRouteCatch);
		
		Catch noResourceCatch = new Catch("error.connection.noresource");
		ILink noResourceLink = links.createNextLink();
		noResourceLink.setParameter(bridgeMessageCommand.getResultName(),
				bridgeMessageCommand.getNoResourceResultValue());
		noResourceCatch.addAction(new Goto(noResourceLink.toString()));
		tx.addEventHandler(noResourceCatch);
		
		Catch badProtocolCatch = new Catch("error.connection.protocol");
		ILink badProtocolLink = links.createNextLink();
		badProtocolLink.setParameter(bridgeMessageCommand.getResultName(),
				bridgeMessageCommand.getProtocolResultValue());
		badProtocolCatch.addAction(new Goto(badProtocolLink.toString()));
		tx.addEventHandler(badProtocolCatch);
		
		Catch bridgeUnsupportedCatch = new Catch("error.unsupported.transfer.bridge");
		ILink bridgeUnsupportedLink = links.createNextLink();
		bridgeUnsupportedLink.setParameter(bridgeMessageCommand.getResultName(),
				bridgeMessageCommand.getBadBridgeResultValue());
		bridgeUnsupportedCatch.addAction(new Goto(bridgeUnsupportedLink.toString()));
		tx.addEventHandler(bridgeUnsupportedCatch);
		
		Catch uriUnsupportedCatch = new Catch("error.unsupported.uri");
		ILink uriUnsupportedLink = links.createNextLink();
		uriUnsupportedLink.setParameter(bridgeMessageCommand.getResultName(),
				bridgeMessageCommand.getBadUriResultValue());
		uriUnsupportedCatch.addAction(new Goto(uriUnsupportedLink.toString()));
		tx.addEventHandler(uriUnsupportedCatch);
		
		Catch transferCatch = new Catch("connection.disconnect.transfer");
		ILink transferLink = links.createNextLink();
		transferLink.setParameter(bridgeMessageCommand.getResultName(),
				bridgeMessageCommand.getTransferredResultValue());
		transferCatch.addAction(new Goto(transferLink.toString()));
		tx.addEventHandler(transferCatch);

		Catch disconnectCatch = new Catch("connection.disconnect.hangup");
		ILink hangupLink = links.createNextLink();
		hangupLink.setParameter(bridgeMessageCommand.getResultName(),
				bridgeMessageCommand.getHangupResultValue());
		disconnectCatch.addAction(new Goto(hangupLink.toString()));
		tx.addEventHandler(disconnectCatch);
		
		form.addFormElement(tx);
		List<String> events = ExtendedActionEventManager.getDefault().getExtendedEvents();
		for(String event : events)
		{
			ILink eventLink = links.createNextLink();
			eventLink.setParameter(bridgeMessageCommand.getResultName(), event);
			Catch eventCatch = new Catch(event);
			eventCatch.addAction(new Goto(eventLink.toString()));
			form.addEventHandler(eventCatch);
		}
		return createVXMLDocument(links, form);
	}

}
