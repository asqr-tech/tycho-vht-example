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
package org.eclipse.vtp.framework.engine.http;

/**
 * HTTPCleanup.
 * 
 * @author Lonnie Pryor
 */
public class HttpThread implements Runnable
{
	private static final int STATE_INACTIVE = 0;
	private static final int STATE_STARTING = 1;
	private static final int STATE_STARTED = 2;
	private static final int STATE_RUNNING = 3;
	private static final int STATE_STOPPING = 4;
	private static final int STATE_STOPPED = 5;

	/** The state of this helper. */
	private int state = STATE_INACTIVE;

	/**
	 * Creates a new HTTPCleanup.
	 * 
	 */
	public HttpThread()
	{
	}

	public synchronized void start() throws IllegalStateException
	{
		if (state != STATE_INACTIVE)
			throw new IllegalStateException();
		state = STATE_STARTING;
		new Thread(this, getClass().getName()).start();
		while (state == STATE_STARTING)
		{
			try
			{
				wait();
			}
			catch (InterruptedException e)
			{
			}
		}
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() throws IllegalStateException
	{
		synchronized (this)
		{
			if (state != STATE_STARTING)
				throw new IllegalStateException();
			state = STATE_STARTED;
			notify();
		}
		try
		{
			while (true)
			{
				synchronized (this)
				{
					if (state != STATE_RUNNING)
						return;
					try
					{
						wait(cleanup());
					}
					catch (InterruptedException e)
					{
					}
				}
			}
		}
		finally
		{
			synchronized (this)
			{
				state = STATE_STOPPED;
				notify();
			}
		}
	}

	public synchronized void stop()
	{
		if (state != STATE_RUNNING)
			return;
		state = STATE_STOPPING;
		notify();
		while (state == STATE_STOPPING)
		{
			try
			{
				wait();
			}
			catch (InterruptedException e)
			{
			}
		}
		state = STATE_INACTIVE;
	}

	private long cleanup()
	{
		return 0;
	}

}
