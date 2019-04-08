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
package org.eclipse.vtp.framework.interactions.core.conversation;

import java.util.List;
import java.util.Map;

import org.eclipse.vtp.framework.common.configurations.AssignmentConfiguration;
import org.eclipse.vtp.framework.interactions.core.configurations.BridgeMessageConfiguration;
import org.eclipse.vtp.framework.interactions.core.configurations.DataRequestConfiguration;
import org.eclipse.vtp.framework.interactions.core.configurations.ExternalReferenceConfiguration;
import org.eclipse.vtp.framework.interactions.core.configurations.InputConfiguration;
import org.eclipse.vtp.framework.interactions.core.configurations.InputRequestConfiguration;
import org.eclipse.vtp.framework.interactions.core.configurations.MetaDataConfiguration;
import org.eclipse.vtp.framework.interactions.core.configurations.OutputConfiguration;
import org.eclipse.vtp.framework.interactions.core.configurations.OutputMessageConfiguration;
import org.eclipse.vtp.framework.interactions.core.configurations.PropertyConfiguration;
import org.eclipse.vtp.framework.interactions.core.configurations.SelectionRequestConfiguration;
import org.eclipse.vtp.framework.interactions.core.configurations.TransferMessageConfiguration;
import org.eclipse.vtp.framework.interactions.core.media.Content;
import org.eclipse.vtp.framework.interactions.core.media.InputGrammar;

/**
 * Represents a conversation between the process engine and an external entity.
 * 
 * @author Lonnie Pryor
 */
public interface IConversation
{
	String RESULT_NAME_FILLED = "success.filled"; //$NON-NLS-1$
	String RESULT_NAME_NO_INPUT = "error.input.noinput"; //$NON-NLS-1$
	String RESULT_NAME_NO_MATCH = "error.input.nomatch"; //$NON-NLS-1$
	String RESULT_NAME_HANGUP = "error.disconnect.hangup"; //$NON-NLS-1$

	IInitial createInitial(String resultParameterName, Map variableNames);

	IOutputMessage createOutputMessage(OutputMessageConfiguration configuration, String resultParameterName);

	IMetaDataMessage createMetaDataMessage(MetaDataConfiguration configuration, String resultParameterName);

	IMetaDataRequest createMetaDataRequest(MetaDataConfiguration configuration, String resultParameterName);

	IInputRequest createInputRequest(InputRequestConfiguration configuration,
			String resultParameterName);

	ISelectionRequest createSelectionRequest(
			SelectionRequestConfiguration configuration, String resultParameterName);

	IDataRequest createDataRequest(DataRequestConfiguration configuration,
			String resultParameterName);

	IExternalReference createExternalReference(
			ExternalReferenceConfiguration configuration,
			String resultParameterName);

	ITransferMessage createTransferMessage(
			TransferMessageConfiguration configuration);

	IBridgeMessage createBridgeMessage(
			BridgeMessageConfiguration configuration, String resultParameterName);

	IFinal createFinal();

	IEndMessage createEndMessage(AssignmentConfiguration[] configurations);

	public List<Content> resolveOutput(OutputConfiguration configuration);
	
	public InputGrammar resolveInput(InputConfiguration configuration);
	
	public String resolveProperty(PropertyConfiguration configuration, boolean useInteractionType);
	
	public String resolveProperty(PropertyConfiguration configuration, boolean useInteractionType, boolean useLanguage);
	
}
