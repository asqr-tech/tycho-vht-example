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
package org.eclipse.vtp.framework.interactions.core.commands;

import org.eclipse.vtp.framework.spi.ICommandVisitor;

/**
 * A visitor capable of handling conversation commands.
 * 
 * @author Lonnie Pryor
 */
public interface IConversationCommandVisitor extends ICommandVisitor
{
	/**
	 * Called when this visitor is passed to an initial command instance.
	 * 
	 * @param initialCommand The initial command this visitor was passed to.
	 * @return An implementation-specific result.
	 */
	Object visitInitial(InitialCommand initialCommand);

	/**
	 * Called when this visitor is passed to an output message command instance.
	 * 
	 * @param outputMessageCommand The output message command this visitor was
	 *          passed to.
	 * @return An implementation-specific result.
	 */
	Object visitOutputMessage(OutputMessageCommand outputMessageCommand);

	/**
	 * Called when this visitor is passed to an meta-data message command
	 * instance.
	 * 
	 * @param metaDataMessageCommand The meta-data message command this visitor
	 *          was passed to.
	 * @return An implementation-specific result.
	 */
	Object visitMetaDataMessage(MetaDataMessageCommand metaDataMessageCommand);

	/**
	 * Called when this visitor is passed to an meta-data request command
	 * instance.
	 * 
	 * @param metaDataRequestCommand The meta-data request command this visitor
	 *          was passed to.
	 * @return An implementation-specific result.
	 */
	Object visitMetaDataRequest(MetaDataRequestCommand metaDataRequestCommand);

	/**
	 * Called when this visitor is passed to an input request command instance.
	 * 
	 * @param inputRequestCommand The input request command this visitor was
	 *          passed to.
	 * @return An implementation-specific result.
	 */
	Object visitInputRequest(InputRequestCommand inputRequestCommand);

	/**
	 * Called when this visitor is passed to a selection request command instance.
	 * 
	 * @param selectionRequestCommand The selection request command this visitor
	 *          was passed to.
	 * @return An implementation-specific result.
	 */
	Object visitSelectionRequest(SelectionRequestCommand selectionRequestCommand);

	/**
	 * Called when this visitor is passed to a data request command instance.
	 * 
	 * @param dataRequestCommand The data request command this visitor was passed
	 *          to.
	 * @return An implementation-specific result.
	 */
	Object visitDataRequest(DataRequestCommand dataRequestCommand);

	/**
	 * Called when this visitor is passed to an external reference command
	 * instance.
	 * 
	 * @param externalReferenceCommand The external reference command this visitor
	 *          was passed to.
	 * @return An implementation-specific result.
	 */
	Object visitExternalReference(
			ExternalReferenceCommand externalReferenceCommand);

	/**
	 * Called when this visitor is passed to a transfer message command instance.
	 * 
	 * @param transferMessageCommand The transfer message command this visitor was
	 *          passed to.
	 * @return An implementation-specific result.
	 */
	Object visitTransferMessage(TransferMessageCommand transferMessageCommand);

	/**
	 * Called when this visitor is passed to a bridge message command instance.
	 * 
	 * @param transferMessageCommand The bridge message command this visitor was
	 *          passed to.
	 * @return An implementation-specific result.
	 */
	Object visitBridgeMessage(BridgeMessageCommand transferMessageCommand);

	/**
	 * Called when this visitor is passed to an end message command instance.
	 * 
	 * @param endMessageCommand The end message command this visitor was passed
	 *          to.
	 * @return An implementation-specific result.
	 */
	Object visitEndMessage(EndMessageCommand endMessageCommand);

	/**
	 * Called when this visitor is passed to a final command instance.
	 * 
	 * @param finalCommand The final command this visitor was passed to.
	 * @return An implementation-specific result.
	 */
	Object visitFinal(FinalCommand finalCommand);
}
