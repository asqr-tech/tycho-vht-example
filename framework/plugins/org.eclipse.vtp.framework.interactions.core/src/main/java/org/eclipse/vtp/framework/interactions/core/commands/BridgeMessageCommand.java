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

/**
 * A command that sends a transfer ending to the user.
 * 
 * @author Lonnie Pryor
 */
public final class BridgeMessageCommand extends ConversationCommand
{
	public static final String BLIND = "blind";
	public static final String BRIDGE = "bridge";
	public static final String CONSULTATION = "consultation";
	private String transferType = BLIND;
	/** The destination to transfer to. */
	private String destination = null;
	/** The name of the parameter to pass the result of the request as. */
	private String resultName = null;
	/** The value of the result parameter to pass if the input is valid. */
	private String transferredResultValue = null;
	/** The value of the result parameter to pass if the input is missing. */
	private String busyResultValue = null;
	/** The value of the result parameter to pass if the input is invalid. */
	private String unavailableResultValue = null;
	/** The value of the result parameter to pass if the input is invalid. */
	private String noAuthResultValue = null;
	/** The value of the result parameter to pass if the input is invalid. */
	private String badDestResultValue = null;
	/** The value of the result parameter to pass if the input is invalid. */
	private String noRouteResultValue = null;
	/** The value of the result parameter to pass if the input is invalid. */
	private String noResourceResultValue = null;
	/** The value of the result parameter to pass if the input is invalid. */
	private String protocolResultValue = null;
	/** The value of the result parameter to pass if the input is invalid. */
	private String badBridgeResultValue = null;
	/** The value of the result parameter to pass if the input is invalid. */
	private String badUriResultValue = null;
	/** The value of the result parameter to pass if the caller hungup. */
	private String hangupResultValue = null;

	/**
	 * Creates a new BridgeMessageCommand.
	 */
	public BridgeMessageCommand()
	{
	}
	
	/**
	 * @return
	 */
	public String getTransferType()
	{
		return this.transferType;
	}
	
	/**
	 * @param useBridged
	 */
	public void setTransferType(String transferType)
	{
		this.transferType = transferType;
	}

	/**
	 * Returns the destination to transfer to.
	 * 
	 * @return The destination to transfer to.
	 */
	public String getDestination()
	{
		return destination;
	}

	/**
	 * Sets the destination to transfer to.
	 * 
	 * @param destination The destination to transfer to.
	 */
	public void setDestination(String destination)
	{
		this.destination = destination;
	}

	/**
	 * Returns the resultName.
	 * 
	 * @return The resultName.
	 */
	public String getResultName()
	{
		return resultName;
	}

	/**
	 * Sets the resultName.
	 * 
	 * @param resultName The resultName to set.
	 */
	public void setResultName(String resultName)
	{
		this.resultName = resultName;
	}

	/**
	 * Returns the transferredResultValue.
	 * 
	 * @return The transferredResultValue.
	 */
	public String getTransferredResultValue()
	{
		return transferredResultValue;
	}

	/**
	 * Sets the transferredResultValue.
	 * 
	 * @param transferredResultValue The transferredResultValue to set.
	 */
	public void setTransferredResultValue(String transferredResultValue)
	{
		this.transferredResultValue = transferredResultValue;
	}

	/**
	 * Returns the busyResultValue.
	 * 
	 * @return The busyResultValue.
	 */
	public String getBusyResultValue()
	{
		return busyResultValue;
	}

	/**
	 * Sets the busyResultValue.
	 * 
	 * @param busyResultValue The busyResultValue to set.
	 */
	public void setBusyResultValue(String busyResultValue)
	{
		this.busyResultValue = busyResultValue;
	}

	/**
	 * Returns the unavailableResultValue.
	 * 
	 * @return The unavailableResultValue.
	 */
	public String getUnavailableResultValue()
	{
		return unavailableResultValue;
	}

	/**
	 * Sets the unavailableResultValue.
	 * 
	 * @param unavailableResultValue The unavailableResultValue to set.
	 */
	public void setUnavailableResultValue(String unavailableResultValue)
	{
		this.unavailableResultValue = unavailableResultValue;
	}

	/**
	 * Returns the noAuthResultValue.
	 * 
	 * @return The noAuthResultValue.
	 */
	public String getNoAuthResultValue()
	{
		return noAuthResultValue;
	}

	/**
	 * Sets the noAuthResultValue.
	 * 
	 * @param noAuthResultValue The noAuthResultValue to set.
	 */
	public void setNoAuthResultValue(String noAuthResultValue)
	{
		this.noAuthResultValue = noAuthResultValue;
	}

	/**
	 * Returns the badDestResultValue.
	 * 
	 * @return The badDestResultValue.
	 */
	public String getBadDestResultValue()
	{
		return badDestResultValue;
	}

	/**
	 * Sets the badDestResultValue.
	 * 
	 * @param badDestResultValue The badDestResultValue to set.
	 */
	public void setBadDestResultValue(String badDestResultValue)
	{
		this.badDestResultValue = badDestResultValue;
	}

	/**
	 * Returns the noRouteResultValue.
	 * 
	 * @return The noRouteResultValue.
	 */
	public String getNoRouteResultValue()
	{
		return noRouteResultValue;
	}

	/**
	 * Sets the noRouteResultValue.
	 * 
	 * @param noRouteResultValue The noRouteResultValue to set.
	 */
	public void setNoRouteResultValue(String noRouteResultValue)
	{
		this.noRouteResultValue = noRouteResultValue;
	}

	/**
	 * Returns the noResourceResultValue.
	 * 
	 * @return The noResourceResultValue.
	 */
	public String getNoResourceResultValue()
	{
		return noResourceResultValue;
	}

	/**
	 * Sets the noResourceResultValue.
	 * 
	 * @param noResourceResultValue The noResourceResultValue to set.
	 */
	public void setNoResourceResultValue(String noResourceResultValue)
	{
		this.noResourceResultValue = noResourceResultValue;
	}

	/**
	 * Returns the protocolResultValue.
	 * 
	 * @return The protocolResultValue.
	 */
	public String getProtocolResultValue()
	{
		return protocolResultValue;
	}

	/**
	 * Sets the protocolResultValue.
	 * 
	 * @param protocolResultValue The protocolResultValue to set.
	 */
	public void setProtocolResultValue(String protocolResultValue)
	{
		this.protocolResultValue = protocolResultValue;
	}

	/**
	 * Returns the badBridgeResultValue.
	 * 
	 * @return The badBridgeResultValue.
	 */
	public String getBadBridgeResultValue()
	{
		return badBridgeResultValue;
	}

	/**
	 * Sets the badBridgeResultValue.
	 * 
	 * @param badBridgeResultValue The badBridgeResultValue to set.
	 */
	public void setBadBridgeResultValue(String badBridgeResultValue)
	{
		this.badBridgeResultValue = badBridgeResultValue;
	}

	/**
	 * Returns the badUriResultValue.
	 * 
	 * @return The badUriResultValue.
	 */
	public String getBadUriResultValue()
	{
		return badUriResultValue;
	}

	/**
	 * Sets the badUriResultValue.
	 * 
	 * @param badUriResultValue The badUriResultValue to set.
	 */
	public void setBadUriResultValue(String badUriResultValue)
	{
		this.badUriResultValue = badUriResultValue;
	}

	/**
	 * Returns the value of the result parameter to pass if the caller hungup.
	 * 
	 * @return The value of the result parameter to pass if the caller hungup.
	 */
	public String getHangupResultValue()
	{
		return hangupResultValue;
	}

	/**
	 * Sets the value of the result parameter to pass if the caller hungup.
	 * 
	 * @param hangupResultValue The value of the result parameter to pass if the
	 *          caller hungup.
	 */
	public void setHangupResultValue(String hangupResultValue)
	{
		this.hangupResultValue = hangupResultValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.interactions.core.commands.
	 *      ConversationCommand#accept(
	 *      org.eclipse.vtp.framework.interactions.core.commands.
	 *      IConversationCommandVisitor)
	 */
	Object accept(IConversationCommandVisitor visitor)
	{
		return visitor.visitBridgeMessage(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.ICommand#exportContents()
	 */
	public Object exportContents()
	{
		return new String[] { transferType, destination, resultName, transferredResultValue,
				busyResultValue, unavailableResultValue, noAuthResultValue,
				badDestResultValue, noRouteResultValue, noResourceResultValue,
				protocolResultValue, badBridgeResultValue, badUriResultValue };
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vtp.framework.spi.ICommand#importContents(
	 *      java.lang.Object)
	 */
	public void importContents(Object contents)
	{
		String[] values = (String[])contents;
		transferType = values[0];
		destination = values[1];
		resultName = values[2];
		transferredResultValue = values[3];
		busyResultValue = values[4];
		unavailableResultValue = values[5];
		noAuthResultValue = values[6];
		badDestResultValue = values[7];
		noRouteResultValue = values[8];
		noResourceResultValue = values[9];
		protocolResultValue = values[10];
		badBridgeResultValue = values[11];
		badUriResultValue = values[12];
	}
}
