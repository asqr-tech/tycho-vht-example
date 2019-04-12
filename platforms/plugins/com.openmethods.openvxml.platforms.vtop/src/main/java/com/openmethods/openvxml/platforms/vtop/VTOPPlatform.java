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
package com.openmethods.openvxml.platforms.vtop;

import java.util.Locale;

import org.eclipse.vtp.framework.core.IExecutionContext;
import org.eclipse.vtp.framework.interactions.core.commands.BridgeMessageCommand;
import org.eclipse.vtp.framework.interactions.core.platforms.IDocument;
import org.eclipse.vtp.framework.interactions.core.platforms.ILink;
import org.eclipse.vtp.framework.interactions.core.platforms.ILinkFactory;
import org.eclipse.vtp.framework.interactions.voice.services.TimeValue;
import org.eclipse.vtp.framework.interactions.voice.services.VoicePlatform;
import org.eclipse.vtp.framework.interactions.voice.vxml.Block;
import org.eclipse.vtp.framework.interactions.voice.vxml.Catch;
import org.eclipse.vtp.framework.interactions.voice.vxml.Dialog;
import org.eclipse.vtp.framework.interactions.voice.vxml.ElseIf;
import org.eclipse.vtp.framework.interactions.voice.vxml.Filled;
import org.eclipse.vtp.framework.interactions.voice.vxml.Form;
import org.eclipse.vtp.framework.interactions.voice.vxml.Goto;
import org.eclipse.vtp.framework.interactions.voice.vxml.If;
import org.eclipse.vtp.framework.interactions.voice.vxml.Script;
import org.eclipse.vtp.framework.interactions.voice.vxml.VXMLDocument;
import org.eclipse.vtp.framework.interactions.voice.vxml.Variable;

/**
 * A generic implementation of a AVP-specific VXML platform.
 * 
 * @author Lonnie Pryor
 */
public class VTOPPlatform extends VoicePlatform
{
	/**
	 * Creates a new AvpPlatform.
	 */
	public VTOPPlatform(IExecutionContext context)
	{
		super(context);
	}
 
	@Override
    protected VXMLDocument createVXMLDocument(ILinkFactory links, Dialog dialog)
    {
		VXMLDocument document = super.createVXMLDocument(links, dialog);
		document.setProperty("documentmaxage", "0"); //$NON-NLS-1$ //$NON-NLS-2$
		document.setProperty("documentmaxstale", "0"); //$NON-NLS-1$ //$NON-NLS-2$
		document.setProperty("fetchaudio", "");
		return document;
    }
	
	public TimeValue getMinimumTimeValue(String property)
	{
		return new TimeValue(100);
	}
	
	public Locale getCurrentLocale()
	{
		return new Locale("en", "US");
	}
	
	protected IDocument renderBridgeMessage(ILinkFactory links,
			BridgeMessageCommand bridgeMessageCommand)
	{
		Form form = new Form("BridgeMessageForm"); //$NON-NLS-1$
		form.addVariable(new Variable("callvars", "new Object()"));
		Block block = new Block("ScriptHolder");
		Script callvarScript = new Script();
		callvarScript.setText("callvars.ani = session.connection.remote.uri;");
		block.addAction(callvarScript);
		form.addFormElement(block);
		VTOPTransfer tx = new VTOPTransfer("BridgeMessageElement", //$NON-NLS-1$
				bridgeMessageCommand.getDestination());
		tx.setSignalVars("callvars");
		tx.setMaxTime("0s");
		tx.setTransferType(bridgeMessageCommand.getTransferType());
//		tx.setAAI("FF,FAFAFAFAFA");
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
		
		Catch noRoute2Catch = new Catch("error.telephone.noroute");
		ILink noRoute2Link = links.createNextLink();
		noRoute2Link.setParameter(bridgeMessageCommand.getResultName(),
				bridgeMessageCommand.getNoRouteResultValue());
		noRoute2Catch.addAction(new Goto(noRoute2Link.toString()));
		tx.addEventHandler(noRoute2Catch);
		
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
		return createVXMLDocument(links, form);
	}
	
	
}
