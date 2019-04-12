/**
 * 
 */
package com.openmethods.openvxml.platforms.genesys.services;

import java.io.ByteArrayInputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.vtp.framework.core.IActionContext;
import org.eclipse.vtp.framework.core.IExecutionContext;
import org.eclipse.vtp.framework.interactions.core.commands.InitialCommand;
import org.eclipse.vtp.framework.interactions.core.commands.MetaDataMessageCommand;
import org.eclipse.vtp.framework.interactions.core.commands.MetaDataRequestCommand;
import org.eclipse.vtp.framework.interactions.core.configurations.MetaDataConfiguration;
import org.eclipse.vtp.framework.interactions.core.platforms.IDocument;
import org.eclipse.vtp.framework.interactions.core.platforms.ILink;
import org.eclipse.vtp.framework.interactions.core.platforms.ILinkFactory;
import org.eclipse.vtp.framework.interactions.core.services.ExtendedActionEventManager;
import org.eclipse.vtp.framework.interactions.voice.services.VoicePlatform;
import org.eclipse.vtp.framework.interactions.voice.vxml.Assignment;
import org.eclipse.vtp.framework.interactions.voice.vxml.Block;
import org.eclipse.vtp.framework.interactions.voice.vxml.Catch;
import org.eclipse.vtp.framework.interactions.voice.vxml.Dialog;
import org.eclipse.vtp.framework.interactions.voice.vxml.Filled;
import org.eclipse.vtp.framework.interactions.voice.vxml.Form;
import org.eclipse.vtp.framework.interactions.voice.vxml.Goto;
import org.eclipse.vtp.framework.interactions.voice.vxml.Parameter;
import org.eclipse.vtp.framework.interactions.voice.vxml.Script;
import org.eclipse.vtp.framework.interactions.voice.vxml.Submit;
import org.eclipse.vtp.framework.interactions.voice.vxml.VXMLConstants;
import org.eclipse.vtp.framework.interactions.voice.vxml.VXMLDocument;
import org.eclipse.vtp.framework.interactions.voice.vxml.Variable;
import org.eclipse.vtp.framework.util.XMLUtilities;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.openmethods.openvxml.platforms.genesys.vxml.Send;
import com.openmethods.openvxml.platforms.genesys.vxml.UserData;

/**
 * @author trip
 *
 */
public class GenesysVoicePlatform8 extends VoicePlatform
{

	/**
	 * 
	 */
	public GenesysVoicePlatform8(IExecutionContext context)
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
		document.setProperty("com.genesyslab.externalevents.enable", "true");
//		document.setProperty("com.genesyslab.externalevents.queue", "false");
		return document;
    }
	
	/* (non-Javadoc)
	 * @see org.eclipse.vtp.framework.interactions.voice.services.VoicePlatform#generateInitialVariableRequests(java.util.Map)
	 */
	public void generateInitialVariableRequests(Map variables)
	{
		super.generateInitialVariableRequests(variables);
		variables.put("gvpUUID", "session.connection.uuid");
	}

	@Override
	public List<String> getPlatformVariableNames() {
		List<String> names = super.getPlatformVariableNames();
		names.add("gvpUserData");
		names.add("gvpUUID");
		return names;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.interactions.core.support.AbstractPlatform#
	 *      renderInitialDocument(
	 *      org.eclipse.vtp.framework.interactions.core.platforms.ILinkFactory,
	 *      org.eclipse.vtp.framework.interactions.core.commands.InitialCommand)
	 */
	protected IDocument renderInitialDocument(ILinkFactory links,
			InitialCommand initialCommand)
	{
		VXMLDocument document = new VXMLDocument();
		document.setProperty("documentmaxage", "0"); //$NON-NLS-1$ //$NON-NLS-2$
		document.setProperty("documentmaxstale", "0"); //$NON-NLS-1$ //$NON-NLS-2$
		Form form = new Form("InitialForm"); //$NON-NLS-1$
		Map<String, String> varMap = new LinkedHashMap<String, String>();
		generateInitialVariableRequests(varMap);
		for (String key : varMap.keySet())
		{
			form.addVariable(new Variable(key, "''")); //$NON-NLS-1$
		}
		form.addVariable(new Variable("gvpUserData", "''"));
		String[] variables = initialCommand.getVariableNames();
		for (int i = 0; i < variables.length; ++i)
		{
			String value = initialCommand.getVariableValue(variables[i]);
			if (value == null)
				value = ""; //$NON-NLS-1$
			form.addVariable(new Variable(variables[i], "'" + value + "'"));
		}
		Block block = new Block("InitialBlock"); //$NON-NLS-1$
		for (String key : varMap.keySet())
		{
			block.addAction(new Assignment(key, varMap.get(key)));
		}
		Script userDataScript = new Script();
		userDataScript.appendText("for(var key in session.com.genesyslab.userdata)\r\n");
		userDataScript.appendText("{\r\n");
		userDataScript.appendText("\tif(gvpUserData != '')\r\n");
		userDataScript.appendText("\t\tgvpUserData = gvpUserData + '&';\r\n");
		userDataScript.appendText("\tgvpUserData = gvpUserData + key + '=' + session.com.genesyslab.userdata[key];\r\n");
		userDataScript.appendText("}\r\n");
		block.addAction(userDataScript);
		ILink nextLink = links.createNextLink();
		String[] parameterNames = initialCommand.getParameterNames();
		for (int i = 0; i < parameterNames.length; ++i)
			nextLink.setParameters(parameterNames[i], initialCommand
					.getParameterValues(parameterNames[i]));
		nextLink.setParameter(initialCommand.getResultName(), initialCommand
				.getResultValue());
		String[] fields = new String[varMap.size() + variables.length + 1];
		int j = 0;
		for (String key : varMap.keySet())
		{
			fields[j] = key;
			++j;
		}
		System.arraycopy(variables, 0, fields, varMap.size(), variables.length);
		fields[fields.length - 1] = "gvpUserData";
		Submit submit = new Submit(nextLink.toString(), fields);
		submit.setMethod("post");
		block.addAction(submit);
		form.addFormElement(block);
		ILink hangupLink = links.createNextLink();
		for (int i = 0; i < parameterNames.length; ++i)
			hangupLink.setParameters(parameterNames[i], initialCommand
					.getParameterValues(parameterNames[i]));
		hangupLink.setParameter(initialCommand.getResultName(),
				initialCommand.getHangupResultValue());
		Catch disconnectCatch = new Catch("connection.disconnect.hangup");
		disconnectCatch.addAction(new Goto(hangupLink.toString()));
		form.addEventHandler(disconnectCatch);
		document.addDialog(form);
		List<String> events = ExtendedActionEventManager.getDefault().getExtendedEvents();
		for(String event : events)
		{
			ILink eventLink = links.createNextLink();
			for (int i = 0; i < parameterNames.length; ++i)
				eventLink.setParameters(parameterNames[i], initialCommand
						.getParameterValues(parameterNames[i]));
			eventLink.setParameter(initialCommand.getResultName(), event);
			Catch eventCatch = new Catch(event);
			eventCatch.addAction(new Goto(eventLink.toString()));
			form.addEventHandler(eventCatch);
		}
		return document;
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
		Send send = new Send();
		send.setAsync(false);
		StringBuilder nameList = new StringBuilder();
		String[] names = metaDataMessageCommand.getMetaDataNames();
		for(int i = 0; i < names.length; i++)
        {
			String encodedName = URLEncoder.encode(names[i]);
			encodedName = encodedName.replaceAll("\\+", "%20");
			nameList.append(encodedName);
			nameList.append('=');
			String encodedValue = URLEncoder.encode(metaDataMessageCommand.getMetaDataValue(names[i]));
			encodedValue = encodedValue.replaceAll("\\+", "%20");
			nameList.append(encodedValue);
			if(i < names.length -1)
				nameList.append('&');
//	        form.addVariable(new Variable(names[i], "'"+metaDataMessageCommand.getMetaDataValue(names[i])+"'"));
//	        if(i != 0)
//	        	nameList.append(' ');
//	        nameList.append(names[i]);
        }
//		send.setNameList(nameList.toString());
		send.setBody(nameList.toString());
		send.setContentType("application/x-www-form-urlencoded;charset=utf-8");
		Block block = new Block("RedirectBlock");
		ILink createNextLink = links.createNextLink();
		createNextLink.setParameter(metaDataMessageCommand.getResultName(), metaDataMessageCommand.getFilledResultValue());
		String[] params = metaDataMessageCommand.getParameterNames();
		for(int i = 0; i < params.length; i++)
        {
			createNextLink.setParameters(params[i], metaDataMessageCommand.getParameterValues(params[i]));
        }
		block.addAction(send);
		block.addAction(new Goto(createNextLink.toString()));
		form.addFormElement(block);
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
