package com.openmethods.openvxml.platforms.vxmlb.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.eclipse.vtp.framework.common.IBooleanObject;
import org.eclipse.vtp.framework.common.IBrand;
import org.eclipse.vtp.framework.common.IBrandRegistry;
import org.eclipse.vtp.framework.common.IBrandSelection;
import org.eclipse.vtp.framework.common.IDataObject;
import org.eclipse.vtp.framework.common.IDataTypeRegistry;
import org.eclipse.vtp.framework.common.IDateObject;
import org.eclipse.vtp.framework.common.IDecimalObject;
import org.eclipse.vtp.framework.common.INumberObject;
import org.eclipse.vtp.framework.common.IStringObject;
import org.eclipse.vtp.framework.common.IVariableRegistry;
import org.eclipse.vtp.framework.common.configurations.AssignmentConfiguration;
import org.eclipse.vtp.framework.common.configurations.InitialConfiguration;
import org.eclipse.vtp.framework.common.support.CustomDataField;
import org.eclipse.vtp.framework.common.support.CustomDataType;
import org.eclipse.vtp.framework.core.IActionContext;
import org.eclipse.vtp.framework.core.IActionResult;
import org.eclipse.vtp.framework.core.IReporter;
import org.eclipse.vtp.framework.interactions.core.ILanguageRegistry;
import org.eclipse.vtp.framework.interactions.core.ILanguageSelection;
import org.eclipse.vtp.framework.interactions.core.actions.InitialAction;
import org.eclipse.vtp.framework.interactions.core.conversation.IConversation;
import org.eclipse.vtp.framework.interactions.core.platforms.IPlatformSelector;
import org.eclipse.vtp.framework.interactions.core.support.AbstractPlatform;
import org.eclipse.vtp.framework.util.Guid;

/* TODO
 * It looks like we can't use this mechanism to replace InitialAction
 * and modify the initial variable assignments to decode the UUI variable
 * prior to entering the callflow. As such, this class is being will not
 * registered as an extension of org.eclipse.vtp.framework.core.actions
 * 
 * Use the UUIDecoder project that is an OVXML 'dependency' jar instead.
 */

public class VXMLBInitialAction extends InitialAction {

	private static final Set<String> INITAL_TYPES = Collections.unmodifiableSet(
			new HashSet<String>(Arrays.asList(new String[] { IBooleanObject.TYPE_NAME, IDateObject.TYPE_NAME,
					IDecimalObject.TYPE_NAME, INumberObject.TYPE_NAME, IStringObject.TYPE_NAME })));

	/** The conversation to use. */
	private final IConversation conversation;
	/** The brand to use. */
	private final IBrandSelection brand;
	private final IDataTypeRegistry dataTypeRegistry;
	private final IPlatformSelector platformSelector;
	private final InitialConfiguration initialConfig;
	private final IBrandRegistry brandRegistry;
	private final ILanguageSelection languageSelection;
	@SuppressWarnings("unused")
	private final ILanguageRegistry languageRegistry;

	public VXMLBInitialAction(IActionContext context, IVariableRegistry variableRegistry,
			IDataTypeRegistry dataTypeRegistry, AssignmentConfiguration[] assignCongigs, IConversation conversation,
			IBrandSelection brand, IPlatformSelector platformSelector, InitialConfiguration initialConfig,
			IBrandRegistry brandRegistry, ILanguageSelection languageSelection, ILanguageRegistry languageRegistry) {
		super(context, variableRegistry, dataTypeRegistry, assignCongigs, conversation, brand, platformSelector,
				initialConfig, brandRegistry, languageSelection, languageRegistry);

		this.conversation = conversation;
		this.brand = brand;
		this.dataTypeRegistry = dataTypeRegistry;
		this.platformSelector = platformSelector;
		this.initialConfig = initialConfig;
		this.brandRegistry = brandRegistry;
		this.languageSelection = languageSelection;
		this.languageRegistry = languageRegistry;

		System.out.println("CONSTRUCTING VXMLB INITIAL ACTION"); // TODO remove this line
	}

	@Override
	public IActionResult execute() {
		System.out.println("Starting execute()");// TODO remove this line
		String result = context.getParameter(context.getActionID());
		if (IConversation.RESULT_NAME_FILLED.equals(result)) {
			System.out.println("It's RESULT_NAME_FILLED");// TODO remove this line
			if (context.isReportingEnabled()) {
				Dictionary<String, Object> props = new Hashtable<String, Object>();
				props.put("event", "initial.after");
				context.report(IReporter.SEVERITY_INFO, "Processing initial variables.", props);
			}
			IDataObject platform = variableRegistry.createVariable("Platform");
			String anivalue = context.getParameter("PLATFORM_ANI");
			if (anivalue != null) {
				if (context.isReportingEnabled()) {
					Dictionary<String, Object> props2 = new Hashtable<String, Object>();
					props2.put("event", "assignment");
					props2.put("event.key", "Platform.ANI");
					props2.put("event.value", String.valueOf(anivalue));
					context.report(IReporter.SEVERITY_INFO,
							"Assigned variable \"Platform.ANI\" to \"" + anivalue + "\"", props2);
				}
				((IStringObject) platform.getField("ANI")).setValue(anivalue);
				((IStringObject) platform.getField("PLATFORM_ANI")).setValue(anivalue);
			}
			String dnisvalue = context.getParameter("PLATFORM_DNIS");
			if (dnisvalue != null) {
				if (context.isReportingEnabled()) {
					Dictionary<String, Object> props2 = new Hashtable<String, Object>();
					props2.put("event", "assignment");
					props2.put("event.key", "Platform.DNIS");
					props2.put("event.value", String.valueOf(dnisvalue));
					context.report(IReporter.SEVERITY_INFO,
							"Assigned variable \"Platform.DNIS\" to \"" + dnisvalue + "\"", props2);
				}
				((IStringObject) platform.getField("DNIS")).setValue(dnisvalue);
				((IStringObject) platform.getField("PLATFORM_DNIS")).setValue(dnisvalue);
			}
			IBrand b = brand.getSelectedBrand();
			if (b != null)
				((IStringObject) platform.getField("Brand")).setValue(b.getName());
			else {
				b = brandRegistry.getBrandById(initialConfig.getDefaultBrandId());
				if (b != null)
					((IStringObject) platform.getField("Brand")).setValue(b.getName());
				else
					((IStringObject) platform.getField("Brand")).setValue(brandRegistry.getDefaultBrand().getName());
			}
			languageSelection.setDefaultLanguage(initialConfig.getDefaultLanguageName());
			variableRegistry.setVariable("Platform", platform);
			Map<String, String> values = new HashMap<String, String>();
			for (int i = 0; i < configurations.length; ++i) {
				String value = context.getParameter(configurations[i].getName());
				if (value != null && value.length() > 0)
					values.put(configurations[i].getName(), value);
			}

			// platform extension variables
			AbstractPlatform abstractPlatform = (AbstractPlatform) platformSelector.getSelectedPlatform();
			List<String> incomingParametersNames = abstractPlatform.getPlatformVariableNames();
			if (incomingParametersNames.size() > 0) {
				for (int i = 0; i < incomingParametersNames.size(); i++) {
					incomingParametersNames.set(i, incomingParametersNames.get(i).replaceAll(Pattern.quote("."), "_"));
					incomingParametersNames.set(i, incomingParametersNames.get(i).replaceAll("-", "_"));
				}
				CustomDataField[] platformFields = new CustomDataField[incomingParametersNames.size()];
				for (int i = 0; i < incomingParametersNames.size(); i++) {
					platformFields[i] = new CustomDataField(incomingParametersNames.get(i),
							dataTypeRegistry.getDataType(IStringObject.TYPE_NAME), "");
				}
				CustomDataType cdt = new CustomDataType(Guid.createGUID(), incomingParametersNames.get(0),
						platformFields);
				IDataObject initialParameters = variableRegistry.createVariable(cdt);
				for (int i = 0; i < incomingParametersNames.size(); i++) {
					System.out
							.println("Looping on incoming parameter name: " + i + " " + incomingParametersNames.get(i));// TODO
																														// remove
																														// this
																														// line
					String parameter = context.getParameter(incomingParametersNames.get(i));
					IStringObject field = (IStringObject) initialParameters.getField(incomingParametersNames.get(i));
					if (field != null) {
						if (incomingParametersNames.get(i).equals("ctiUUI"))
							; // Is this the Avaya UUI parameter?
						field.setValue(parameter == null ? "" : decodeUUI(parameter)); // Then decode it
						field.setValue(parameter == null ? "" : parameter);
					}
				}
				variableRegistry.setVariable("PlatformVariables", initialParameters);
			}
			return execute(values, false);
		} else if (IConversation.RESULT_NAME_HANGUP.equals(result)) {
			if (context.isReportingEnabled()) {
				Dictionary<String, Object> props = new Hashtable<String, Object>();
				props.put("event", "error.disconnect.hangup");
				context.report(IReporter.SEVERITY_INFO, "Got disconnect during interaction.", props);
			}
			return context.createResult(IConversation.RESULT_NAME_HANGUP);
		} else if (result != null) {
			return context.createResult(result);
		} else {
			try {
				String[] incomingParametersNames = context.getParameterNames();
				for (int i = 0; i < incomingParametersNames.length; i++) {
					incomingParametersNames[i] = incomingParametersNames[i].replaceAll(Pattern.quote("."), "_");
					incomingParametersNames[i] = incomingParametersNames[i].replaceAll("-", "_");
				}
				CustomDataField[] fields = new CustomDataField[incomingParametersNames.length];
				for (int i = 0; i < incomingParametersNames.length; i++) {
					fields[i] = new CustomDataField(incomingParametersNames[i],
							dataTypeRegistry.getDataType(IStringObject.TYPE_NAME), "");
				}
				if (fields.length < 1) {
					fields = new CustomDataField[] {
							new CustomDataField("empty", dataTypeRegistry.getDataType(IStringObject.TYPE_NAME), "") };
				}
				CustomDataType cdt = new CustomDataType(Guid.createGUID(),
						incomingParametersNames.length > 1 ? incomingParametersNames[0] : "empty", fields);
				IDataObject initialParameters = variableRegistry.createVariable(cdt);
				for (int i = 0; i < incomingParametersNames.length; i++) {
					String parameter = context.getParameter(incomingParametersNames[i]);
					IStringObject field = (IStringObject) initialParameters.getField(incomingParametersNames[i]);
					if (field != null) {
						field.setValue(parameter == null ? "" : parameter);
					}
				}
				variableRegistry.setVariable("InitialParameters", initialParameters);
				if (context.isReportingEnabled()) {
					Dictionary<String, Object> props = new Hashtable<String, Object>();
					props.put("event", "initial.before");
					context.report(IReporter.SEVERITY_INFO, "Requesting initial variables.", props);
				}
				Map<String, String> vars = new LinkedHashMap<String, String>();
				if ("true".equals(context.getAttribute("subdialog"))) {
					for (int i = 0; i < configurations.length; ++i)
						if (INITAL_TYPES.contains(configurations[i].getType()))
							vars.put(configurations[i].getName(), configurations[i].getValue());
				}
				conversation.createInitial(context.getActionID(), vars).enqueue();
			} catch (NullPointerException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
			return context.createResult(IActionResult.RESULT_NAME_REPEAT);
		}
	}

	public static String decodeUUI(String undecodedString) {
		List<String> messageList = new ArrayList<String>();
		for (int b = 0; b < undecodedString.length() / 2; b = b + 2) {
			String currentByteString = undecodedString.substring(b, b + 2);
			if (currentByteString.equalsIgnoreCase("C8")) // Look for an Information Element
			{
				String lengthByte = undecodedString.substring(b + 2, b + 4);
				int length = Integer.valueOf(lengthByte, 16);

				int offset = 4;
				char[] messageBytes = new char[length];
				for (int c = 0; c < length; c++) {
					String byteString = undecodedString.substring(b + (c * 2) + offset, b + (c * 2) + offset + 2);
					messageBytes[c] = (char) Integer.parseInt(byteString, 16);
				}

				messageList.add(new String(messageBytes));

				// Add length byte and message to our counter
				b = b + 2 * (length + 1);
			}
		}

		return messageList.get(0); // TODO return the List for conversion to an OpenVXML string array
	}

}
