package com.openmethods.openvxml.platforms.cvp.services;

import org.eclipse.vtp.framework.core.IExecutionContext;
import org.eclipse.vtp.framework.interactions.core.media.IMediaProviderRegistry;
import org.eclipse.vtp.framework.interactions.core.platforms.ILinkFactory;
import org.eclipse.vtp.framework.interactions.voice.vxml.Dialog;
import org.eclipse.vtp.framework.interactions.voice.vxml.VXMLDocument;

public class UCCXVoicePlatform extends CvpPlatform
{
	private IExecutionContext context = null;
	
	public UCCXVoicePlatform(IExecutionContext context, IMediaProviderRegistry mediaProviderRegistry)
	{
		super(context, mediaProviderRegistry);
		this.context = context;
		context.info("Creating UCCX Voice Platform Extension");
	}

	@Override
	protected VXMLDocument createVXMLDocument(ILinkFactory links, Dialog dialog)
	{
		context.info("In createVXMLDocument()");
		VXMLDocument ret = super.createVXMLDocument(links, dialog);
		ret.setVersion("2.0");
		context.info("VXML version = " + ret.getVersion());
		return ret;
	}

}
