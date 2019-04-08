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
package org.eclipse.vtp.framework.interactions.core.support;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.vtp.framework.core.IActionContext;
import org.eclipse.vtp.framework.interactions.core.commands.BridgeMessageCommand;
import org.eclipse.vtp.framework.interactions.core.commands.ConversationCommand;
import org.eclipse.vtp.framework.interactions.core.commands.DataRequestCommand;
import org.eclipse.vtp.framework.interactions.core.commands.EndMessageCommand;
import org.eclipse.vtp.framework.interactions.core.commands.ExternalReferenceCommand;
import org.eclipse.vtp.framework.interactions.core.commands.FinalCommand;
import org.eclipse.vtp.framework.interactions.core.commands.IConversationCommandVisitor;
import org.eclipse.vtp.framework.interactions.core.commands.InitialCommand;
import org.eclipse.vtp.framework.interactions.core.commands.InputRequestCommand;
import org.eclipse.vtp.framework.interactions.core.commands.MetaDataMessageCommand;
import org.eclipse.vtp.framework.interactions.core.commands.MetaDataRequestCommand;
import org.eclipse.vtp.framework.interactions.core.commands.OutputMessageCommand;
import org.eclipse.vtp.framework.interactions.core.commands.SelectionRequestCommand;
import org.eclipse.vtp.framework.interactions.core.commands.TransferMessageCommand;
import org.eclipse.vtp.framework.interactions.core.configurations.MetaDataConfiguration;
import org.eclipse.vtp.framework.interactions.core.platforms.IDocument;
import org.eclipse.vtp.framework.interactions.core.platforms.ILinkFactory;
import org.eclipse.vtp.framework.interactions.core.platforms.IPlatform;
import org.eclipse.vtp.framework.interactions.core.platforms.IRenderingQueue;
import org.eclipse.vtp.framework.spi.ICommand;

/**
 * A base implementation of {@link IPlatform}.
 * 
 * @author Lonnie Pryor
 */
public abstract class AbstractPlatform implements IPlatform
{
	/**
	 * Creates a new AbstractPlatform.
	 */
	protected AbstractPlatform()
	{
	}

	/**
	 * Renders the initial document to the user.
	 * 
	 * @param links The link factory to use.
	 * @param initialCommand The command to render.
	 * @return The document that was rendered from the command or
	 *         <code>null</code> if no document could be rendered.
	 */
	protected IDocument renderInitialDocument(ILinkFactory links,
			InitialCommand initialCommand)
	{
		return null;
	}

	/**
	 * Renders an output message to the user.
	 * 
	 * @param links The link factory to use.
	 * @param outputMessageCommand The command to render.
	 * @return The document that was rendered from the command or
	 *         <code>null</code> if no document could be rendered.
	 */
	protected IDocument renderOutputMessage(ILinkFactory links,
			OutputMessageCommand outputMessageCommand)
	{
		return null;
	}

	/**
	 * Renders a meta-data message to the user.
	 * 
	 * @param links The link factory to use.
	 * @param metaDataMessageCommand The command to render.
	 * @return The document that was rendered from the command or
	 *         <code>null</code> if no document could be rendered.
	 */
	protected IDocument renderMetaDataMessage(ILinkFactory links,
			MetaDataMessageCommand metaDataMessageCommand)
	{
		return null;
	}
	
	public boolean processMetaDataMessageResults(IActionContext context)
	{
		return true;
	}

	/**
	 * Renders a meta-data request to the user.
	 * 
	 * @param links The link factory to use.
	 * @param metaDataMessageRequest The command to render.
	 * @return The document that was rendered from the command or
	 *         <code>null</code> if no document could be rendered.
	 */
	protected IDocument renderMetaDataRequest(ILinkFactory links,
			MetaDataRequestCommand metaDataMessageRequest)
	{
		return null;
	}
	
	public Map processMetaDataResponse(MetaDataConfiguration configuration, IActionContext context)
	{
		return Collections.EMPTY_MAP;
	}

	/**
	 * Renders an input request to the user.
	 * 
	 * @param links The link factory to use.
	 * @param inputRequestCommand The command to render.
	 * @return The document that was rendered from the command or
	 *         <code>null</code> if no document could be rendered.
	 */
	protected IDocument renderInputRequest(ILinkFactory links,
			InputRequestCommand inputRequestCommand)
	{
		return null;
	}

	/**
	 * Renders a selection request to the user.
	 * 
	 * @param links The link factory to use.
	 * @param selectionRequestCommand The command to render.
	 * @return The document that was rendered from the command or
	 *         <code>null</code> if no document could be rendered.
	 */
	protected IDocument renderSelectionRequest(ILinkFactory links,
			SelectionRequestCommand selectionRequestCommand)
	{
		return null;
	}

	/**
	 * Renders a data request to the user.
	 * 
	 * @param links The link factory to use.
	 * @param dataRequestCommand The command to render.
	 * @return The document that was rendered from the command or
	 *         <code>null</code> if no document could be rendered.
	 */
	protected IDocument renderDataRequest(ILinkFactory links,
			DataRequestCommand dataRequestCommand)
	{
		return null;
	}

	/**
	 * Renders an external reference to the user.
	 * 
	 * @param links The link factory to use.
	 * @param externalReferenceCommand The command to render.
	 * @return The document that was rendered from the command or
	 *         <code>null</code> if no document could be rendered.
	 */
	protected IDocument renderExternalReference(ILinkFactory links,
			ExternalReferenceCommand externalReferenceCommand)
	{
		return null;
	}

	/**
	 * Renders a transfer message to the user.
	 * 
	 * @param links The link factory to use.
	 * @param transferMessageCommand The command to render.
	 * @return The document that was rendered from the command or
	 *         <code>null</code> if no document could be rendered.
	 */
	protected IDocument renderTransferMessage(ILinkFactory links,
			TransferMessageCommand transferMessageCommand)
	{
		return null;
	}

	/**
	 * Renders a bridge message to the user.
	 * 
	 * @param links The link factory to use.
	 * @param bridgeMessageCommand The command to render.
	 * @return The document that was rendered from the command or
	 *         <code>null</code> if no document could be rendered.
	 */
	protected IDocument renderBridgeMessage(ILinkFactory links,
			BridgeMessageCommand bridgeMessageCommand)
	{
		return null;
	}

	/**
	 * Renders an end message to the user.
	 * 
	 * @param links The link factory to use.
	 * @param endMessageCommand The command to render.
	 * @return The document that was rendered from the command or
	 *         <code>null</code> if no document could be rendered.
	 */
	protected IDocument renderEndMessage(ILinkFactory links,
			EndMessageCommand endMessageCommand)
	{
		return null;
	}

	/**
	 * Renders the final document to the user.
	 * 
	 * @param links The link factory to use.
	 * @param finalCommand The command to render.
	 * @return The document that was rendered from the command or
	 *         <code>null</code> if no document could be rendered.
	 */
	protected IDocument renderFinalDocument(ILinkFactory links,
			FinalCommand finalCommand)
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.interactions.core.platforms.IPlatform#
	 *      createDocument(
	 *      org.eclipse.vtp.framework.interactions.core.platforms.ILinkFactory,
	 *      org.eclipse.vtp.framework.interactions.core.platforms.IRenderingQueue)
	 */
	public IDocument createDocument(ILinkFactory links,
			IRenderingQueue renderingQueue)
	{
		IDocument document = null;
		Renderer renderer = new Renderer(links);
		while (!renderingQueue.isEmpty() && document == null)
		{
			ConversationCommand next = renderingQueue.next();
			document = (IDocument)next.accept(renderer);
			if (document != null)
				document.setSecured(next.isSecured());
		}
		return document;
	}

	public void generateInitialVariableRequests(Map<String, String> variables)
	{
	}

	public List<String> getPlatformVariableNames()
	{
		return new LinkedList<String>();
	}

	/**
	 * The visitor implementation.
	 * 
	 * @author Lonnie Pryor
	 */
	private final class Renderer implements IConversationCommandVisitor
	{
		/** The link factory. */
		private final ILinkFactory links;

		/**
		 * Creates a new Renderer.
		 * 
		 * @param links The link factory.
		 */
		Renderer(ILinkFactory links)
		{
			this.links = links;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.interactions.core.commands.
		 *      IConversationCommandVisitor#visitInitial(
		 *      org.eclipse.vtp.framework.interactions.core.commands.InitialCommand)
		 */
		public Object visitInitial(InitialCommand initialCommand)
		{
			return renderInitialDocument(links, initialCommand);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.interactions.core.commands.
		 *      IConversationCommandVisitor#visitOutputMessage(
		 *      org.eclipse.vtp.framework.interactions.core.commands.
		 *      OutputMessageCommand)
		 */
		public Object visitOutputMessage(OutputMessageCommand outputMessageCommand)
		{
			return renderOutputMessage(links, outputMessageCommand);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.interactions.core.commands.
		 *      IConversationCommandVisitor#visitMetaDataMessage(
		 *      org.eclipse.vtp.framework.interactions.core.commands.
		 *      MetaDataMessageCommand)
		 */
		public Object visitMetaDataMessage(
				MetaDataMessageCommand metaDataMessageCommand)
		{
			return renderMetaDataMessage(links, metaDataMessageCommand);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.interactions.core.commands.
		 *      IConversationCommandVisitor#visitMetaDataRequest(
		 *      org.eclipse.vtp.framework.interactions.core.commands.
		 *      MetaDataRequestCommand)
		 */
		public Object visitMetaDataRequest(
				MetaDataRequestCommand metaDataRequestCommand)
		{
			return renderMetaDataRequest(links, metaDataRequestCommand);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.interactions.core.commands.
		 *      IConversationCommandVisitor#visitInputRequest(
		 *      org.eclipse.vtp.framework.interactions.core.commands.
		 *      InputRequestCommand)
		 */
		public Object visitInputRequest(InputRequestCommand inputRequestCommand)
		{
			return renderInputRequest(links, inputRequestCommand);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.interactions.core.commands.
		 *      IConversationCommandVisitor#visitSelectionRequest(
		 *      org.eclipse.vtp.framework.interactions.core.commands.
		 *      SelectionRequestCommand)
		 */
		public Object visitSelectionRequest(
				SelectionRequestCommand selectionRequestCommand)
		{
			return renderSelectionRequest(links, selectionRequestCommand);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.interactions.core.commands.
		 *      IConversationCommandVisitor#visitDataRequest(
		 *      org.eclipse.vtp.framework.interactions.core.commands.
		 *      DataRequestCommand)
		 */
		public Object visitDataRequest(DataRequestCommand dataRequestCommand)
		{
			return renderDataRequest(links, dataRequestCommand);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.interactions.core.commands.
		 *      IConversationCommandVisitor#visitExternalReference(
		 *      org.eclipse.vtp.framework.interactions.core.commands.
		 *      ExternalReferenceCommand)
		 */
		public Object visitExternalReference(
				ExternalReferenceCommand externalReferenceCommand)
		{
			return renderExternalReference(links, externalReferenceCommand);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.interactions.core.commands.
		 *      IConversationCommandVisitor#visitTransferMessage(
		 *      org.eclipse.vtp.framework.interactions.core.commands.
		 *      TransferMessageCommand)
		 */
		public Object visitTransferMessage(
				TransferMessageCommand transferMessageCommand)
		{
			return renderTransferMessage(links, transferMessageCommand);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.interactions.core.commands.
		 *      IConversationCommandVisitor#visitBridgeMessage(
		 *      org.eclipse.vtp.framework.interactions.core.commands.
		 *      BridgeMessageCommand)
		 */
		public Object visitBridgeMessage(BridgeMessageCommand bridgeMessageCommand)
		{
			return renderBridgeMessage(links, bridgeMessageCommand);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.interactions.core.commands.
		 *      IConversationCommandVisitor#visitEndMessage(
		 *      org.eclipse.vtp.framework.interactions.core.commands.
		 *      EndMessageCommand)
		 */
		public Object visitEndMessage(EndMessageCommand endMessageCommand)
		{
			return renderEndMessage(links, endMessageCommand);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.interactions.core.commands.
		 *      IConversationCommandVisitor#visitFinal(
		 *      org.eclipse.vtp.framework.interactions.core.commands.FinalCommand)
		 */
		public Object visitFinal(FinalCommand finalCommand)
		{
			return renderFinalDocument(links, finalCommand);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.vtp.framework.spi.ICommandVisitor#visitUnknown(
		 *      org.eclipse.vtp.framework.spi.ICommand)
		 */
		public Object visitUnknown(ICommand unknownCommand)
				throws NullPointerException
		{
			return null;
		}
	}
}
