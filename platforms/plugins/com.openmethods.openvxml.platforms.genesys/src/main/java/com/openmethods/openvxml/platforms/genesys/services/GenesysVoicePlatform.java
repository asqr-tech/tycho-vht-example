/**
 * 
 */
package com.openmethods.openvxml.platforms.genesys.services;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.vtp.framework.core.IActionContext;
import org.eclipse.vtp.framework.core.IExecutionContext;
import org.eclipse.vtp.framework.interactions.core.commands.MetaDataMessageCommand;
import org.eclipse.vtp.framework.interactions.core.commands.MetaDataRequestCommand;
import org.eclipse.vtp.framework.interactions.core.configurations.MetaDataConfiguration;
import org.eclipse.vtp.framework.interactions.core.platforms.IDocument;
import org.eclipse.vtp.framework.interactions.core.platforms.ILink;
import org.eclipse.vtp.framework.interactions.core.platforms.ILinkFactory;
import org.eclipse.vtp.framework.interactions.voice.services.VoicePlatform;
import org.eclipse.vtp.framework.interactions.voice.vxml.Catch;
import org.eclipse.vtp.framework.interactions.voice.vxml.Dialog;
import org.eclipse.vtp.framework.interactions.voice.vxml.Filled;
import org.eclipse.vtp.framework.interactions.voice.vxml.Form;
import org.eclipse.vtp.framework.interactions.voice.vxml.Goto;
import org.eclipse.vtp.framework.interactions.voice.vxml.Parameter;
import org.eclipse.vtp.framework.interactions.voice.vxml.Submit;
import org.eclipse.vtp.framework.interactions.voice.vxml.VXMLConstants;
import org.eclipse.vtp.framework.interactions.voice.vxml.VXMLDocument;
import org.eclipse.vtp.framework.interactions.voice.vxml.Variable;
import org.eclipse.vtp.framework.util.XMLUtilities;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.openmethods.openvxml.platforms.genesys.vxml.UserData;

/**
 * @author trip
 *
 */
public class GenesysVoicePlatform extends VoicePlatform
{

	/**
	 * 
	 */
	public GenesysVoicePlatform(IExecutionContext context)
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
	
	/* (non-Javadoc)
	 * @see org.eclipse.vtp.framework.interactions.voice.services.VoicePlatform#generateInitialVariableRequests(java.util.Map)
	 */
	public void generateInitialVariableRequests(Map variables)
	{
		super.generateInitialVariableRequests(variables);
	}


    protected IDocument renderMetaDataRequest(ILinkFactory links,
            MetaDataRequestCommand metaDataMessageRequest)
    {
		Form form = new Form("SetAttachedDataForm");
		UserData userData = new UserData("GetAttachedData");
		userData.setDoGet(true);
		String[] names = metaDataMessageRequest.getMetaDataNames();
		for(int i = 0; i < names.length; i++)
        {
	        userData.addParameter(new Parameter(names[i], "''"));
        }
		String[] parameterNames = metaDataMessageRequest.getParameterNames();
		String[] submitVars = new String[parameterNames.length + 2];
		submitVars[0] = metaDataMessageRequest.getDataName();
		submitVars[1] = metaDataMessageRequest.getResultName();
		Filled filled = new Filled();
		filled.addVariable(new Variable(metaDataMessageRequest.getResultName(), "'" + metaDataMessageRequest.getFilledResultValue() + "'"));
		for (int i = 0; i < parameterNames.length; ++i)
		{
			submitVars[i + 2] = parameterNames[i];
			String[] values = metaDataMessageRequest.getParameterValues(parameterNames[i]);
			StringBuffer buf = new StringBuffer();
			for(int v = 0; v < values.length; v++)
			{
				buf.append(values[v]);
				if(v < values.length - 1)
					buf.append(',');
			}
			Variable paramVar = new Variable(parameterNames[i], "'" + buf.toString() + "'");
			filled.addVariable(paramVar);
		}
		ILink filledLink = links.createNextLink();
		Submit submit = new Submit(filledLink.toString(), submitVars);
		submit.setMethod(VXMLConstants.METHOD_POST);
		submit.setEncodingType("multipart/form-data");
		filled.addAction(submit);
		userData.addFilledHandler(filled);
		form.addFormElement(userData);
		ILink hangupLink = links.createNextLink();
		for (int i = 0; i < parameterNames.length; ++i)
			hangupLink.setParameters(parameterNames[i], metaDataMessageRequest
					.getParameterValues(parameterNames[i]));
		hangupLink.setParameter(metaDataMessageRequest.getResultName(),
				metaDataMessageRequest.getHangupResultValue());
		Catch disconnectCatch = new Catch("connection.disconnect.hangup");
		disconnectCatch.addAction(new Goto(hangupLink.toString()));
		form.addEventHandler(disconnectCatch);
	    return this.createVXMLDocument(links, form);
    }


	protected IDocument renderMetaDataMessage(ILinkFactory links, MetaDataMessageCommand metaDataMessageCommand)
    {
		Form form = new Form("SetAttachedDataForm");
		UserData userData = new UserData("SetAttachedData");
		userData.setDoGet(false);
		String[] names = metaDataMessageCommand.getMetaDataNames();
		for(int i = 0; i < names.length; i++)
        {
	        userData.addParameter(new Parameter(names[i], "'"+metaDataMessageCommand.getMetaDataValue(names[i])+"'"));
        }
		Filled filled = new Filled();
		ILink createNextLink = links.createNextLink();
		createNextLink.setParameter(metaDataMessageCommand.getResultName(), metaDataMessageCommand.getFilledResultValue());
		String[] params = metaDataMessageCommand.getParameterNames();
		for(int i = 0; i < params.length; i++)
        {
			createNextLink.setParameters(params[i], metaDataMessageCommand.getParameterValues(params[i]));
        }
		filled.addAction(new Goto(createNextLink.toString()));
		userData.addFilledHandler(filled);
		Catch catchHandler = new Catch("");
		if(metaDataMessageCommand.isIgnoreErrors())
		{
			catchHandler.addAction(new Goto(createNextLink.toString()));
		}
		else
		{
			ILink errorLink = links.createNextLink();
			errorLink.setParameter(metaDataMessageCommand.getResultName(), "error");
			for(int i = 0; i < params.length; i++)
	        {
				errorLink.setParameters(params[i], metaDataMessageCommand.getParameterValues(params[i]));
	        }
			catchHandler.addAction(new Goto(errorLink.toString()));
		}
		userData.addEventHandler(catchHandler);
		form.addFormElement(userData);
		ILink hangupLink = links.createNextLink();
		hangupLink.setParameter(metaDataMessageCommand.getResultName(),
				metaDataMessageCommand.getHangupResultValue());
		Catch disconnectCatch = new Catch("connection.disconnect.hangup");
		disconnectCatch.addAction(new Goto(hangupLink.toString()));
		form.addEventHandler(disconnectCatch);
	    return this.createVXMLDocument(links, form);
    }


	@Override
    public Map processMetaDataResponse(MetaDataConfiguration configuration,
            IActionContext context)
    {
		Map dataMap = new HashMap();
		String attachedDataContent = context.getParameter("GetAttachedData");
		System.out.println(attachedDataContent);
		try
        {
	        ByteArrayInputStream bais = new ByteArrayInputStream(attachedDataContent.getBytes());
	        Document attachedDataDocument = XMLUtilities.getDocumentBuilder().parse(bais);
	        NodeList dataList = attachedDataDocument.getDocumentElement().getElementsByTagName("key");
	        for(int i = 0; i < dataList.getLength(); i++)
	        {
	        	Element dataElement = (Element)dataList.item(i);
	        	dataMap.put(dataElement.getAttribute("name"), dataElement.getAttribute("value"));
	        }
        }
        catch(Exception e)
        {
	        e.printStackTrace();
        }
		return dataMap;
    }

}
