/**
 * 
 */
package com.openmethods.openvxml.idriver;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.sun.jna.NativeLong;

/**
 * @author trip
 *
 */
public class GenesysIDriver implements Runnable
{
	private static GenesysIDriver INSTANCE;
	private static short DEFAULT_CONFIG_PORT = 2020;
	private static long DEFAULT_TIMEOUT = 4000L;
	private static String DEFAULT_DELIMITER = "|";

	static
	{
		System.out.println(System.getProperty("java.library.path"));
		INSTANCE = new GenesysIDriver();
	}
	
	public static GenesysIDriver getInstance()
	{
		return INSTANCE;
	}
	
	private boolean running = true;
	private String delimiter;
	private IDriver iDriver = IDriver.INSTANCE;
	private Map<String, PortRecord> activePorts = new HashMap<String, PortRecord>();

	/**
	 * 
	 */
	private GenesysIDriver()
	{
		try
		{
			String ivrName = System.getProperty("com.openmethods.idriver.ivrname");
			String primaryServerAddress = System.getProperty("com.openmethods.idriver.primaryserver");
			String primaryPortString = System.getProperty("com.openmethods.idriver.primaryport");
			String backupServerAddress = System.getProperty("com.openmethods.idriver.backupserver");
			String backupPortString = System.getProperty("com.openmethods.idriver.backupport");
			String appName = System.getProperty("com.openmethods.idriver.appname");
			String timeoutString = System.getProperty("com.openmethods.idriver.timeout");
			delimiter = System.getProperty("com.openmethods.idriver.delimiter", DEFAULT_DELIMITER);
			if(ivrName == null || primaryServerAddress == null || appName == null)
			{
				running = false;
				System.out.println("ERROR: Invalid I-Driver configuration provided:");
				System.out.println("\tIVR Name: " + ivrName);
				System.out.println("\tPrimary Address: " + primaryServerAddress);
				System.out.println("\tPrimary Port: " + primaryPortString);
				System.out.println("\tBackup Address: " + backupServerAddress);
				System.out.println("\tBackup Port: " + backupPortString);
				System.out.println("\tApplication Name: " + appName);
				return;
			}
			iDriver.ilInitiate(ivrName);
			iDriver.ilSetTimeout(new NativeLong(4000L));
			short primaryServerPort = DEFAULT_CONFIG_PORT;
			if(primaryPortString != null)
			{
				try
				{
					primaryServerPort = Short.parseShort(primaryPortString);
				}
				catch(Exception ex)
				{
					System.out.println("WARN: Primary configuration port invalid [" + primaryPortString + "] using default [" + DEFAULT_CONFIG_PORT + "]");
				}
			}
			else
				System.out.println("INFO: Primary configuration port not supplied, using default [" + DEFAULT_CONFIG_PORT + "]");
			short backupServerPort = DEFAULT_CONFIG_PORT;
			if(backupServerAddress != null)
			{
				if(backupPortString != null)
				{
					try
					{
						backupServerPort = Short.parseShort(backupPortString);
					}
					catch(Exception ex)
					{
						System.out.println("WARN: Backup configuration port invalid [" + backupPortString + "] using default [" + DEFAULT_CONFIG_PORT + "]");
					}
				}
				else
					System.out.println("INFO: Backup configuration port not supplied, using default [" + DEFAULT_CONFIG_PORT + "]");
			}
			long timeout = DEFAULT_TIMEOUT;
			if(timeoutString != null)
			{
				try
				{
					timeout = Long.parseLong(timeoutString);
				}
				catch(Exception ex)
				{
					System.out.println("WARN: Connection timeout invalid [" + timeoutString + "] using default [" + DEFAULT_TIMEOUT + "]");
				}
			}
			else
				System.out.println("INFO: Connection timeout not supplied, using default [" + DEFAULT_TIMEOUT + "]");
			if(!iDriver.ilConnectionOpenConfigServer80(primaryServerAddress, primaryServerPort, (short)0, backupServerAddress, backupServerPort, appName, new NativeLong(timeout)))
			{
				System.out.println("ERROR: Unable to connect to configuration or IVR server.");
				running = false;
				return;
			}
			if(running)
			{
				Thread t = new Thread(this);
				t.setName("IDriver Port Monitor");
				t.setDaemon(true);
				t.start();
			}
		}
		catch (Throwable e)
		{
			System.out.println("Big Time Error during GenesysIDriver setup");
			e.printStackTrace();
		}
	}

	public void run()
	{
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		while(running)
		{
			List<PortRecord> copy = null;
			synchronized(activePorts)
			{
				copy = new LinkedList<PortRecord>(activePorts.values());
			}
			for(PortRecord pr : copy)
			{
				synchronized(pr)
				{
					if(!pr.onCall)
						continue;
					long inactivity = System.currentTimeMillis() - pr.lastActivity;
					if(inactivity > 90000)
					{
						System.out.println("INFO: Timing out port [" + pr.port + "] after " + Long.toString(inactivity/1000) + "s of inactivity");
						pr.onCall = false;
						NativeLong result = iDriver.ilSRqNoteCallEnd(IDriver.ANY_RQ, new NativeLong(pr.port));
						if(result.longValue() >= 0) //no error
						{
							//clear response buffer
							iDriver.ilGetReply(result, buffer, buffer.capacity());
							buffer.clear();
						}
					}
				}
			}
			try
			{
				Thread.sleep(10000);
			}
			catch(InterruptedException ex)
			{
			}
		}
	}
	
	public boolean startCall(long port, String callId, String dnis, String ani)
	{
		System.out.println("INFO: Starting call on port [" + port + "]");
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		PortRecord pr = null;
		synchronized(activePorts)
		{
			pr = activePorts.get(Long.toString(port));
			if(pr == null)
				activePorts.put(Long.toString(port), pr = new PortRecord(port));
		}
		synchronized(pr)
		{
			if(pr.onCall) //already marked as on call, force close
			{
				System.out.println("WARN: Received start call on port showing active.  Killing call on port [" + port + "]");
				killCall(port, buffer);
				pr.onCall = false;
				buffer.clear();
			}
		}
		NativeLong result = iDriver.ilSRqNoteCallStart(IDriver.ANY_RQ, new NativeLong(port), callId, dnis, ani, "");
		if(result.longValue() < 0) //error occurred
			return false;
		result = iDriver.ilGetReply(result, buffer, buffer.capacity());
//		buffer.flip();
		if(result.longValue() < 0) //error occurred
		{
			System.out.println("ERROR: Problem starting new call:");
			System.out.println(getString(buffer));
		}
		boolean established = waitForEstablished(port, buffer);
		if(established)
		{
			synchronized(pr)
			{
				pr.onCall = true;
				pr.lastActivity = System.currentTimeMillis();
			}
		}
		return established;
	}
	
	public boolean updateCall(long port)
	{
		System.out.println("INFO: Updating call on port [" + port + "]");
		PortRecord pr = activePorts.get(Long.toString(port));
		if(pr == null)
		{
			System.out.println("ERROR: Received update call on inactive port [" + port + "]");
			return false;
		}
		synchronized(pr)
		{
			if(!pr.onCall)
			{
				System.out.println("ERROR: Received update call on inactive port [" + port + "]");
				return false;
			}
			pr.lastActivity = System.currentTimeMillis();
		}
		return true;
	}
	
	public String getConnId(long port)
	{
		System.out.println("INFO: Getting Conn ID for port [" + port + "]");
		PortRecord pr = activePorts.get(Long.toString(port));
		if(pr == null)
		{
			System.out.println("ERROR: Received get ConnId on inactive port [" + port + "]");
			return null;
		}
		synchronized(pr)
		{
			if(!pr.onCall)
			{
				System.out.println("ERROR: Received get ConnId on inactive port [" + port + "]");
				return null;
			}
			pr.lastActivity = System.currentTimeMillis();
		}
		//with last activity updated, no need to maintain lock
		NativeLong result = iDriver.ilSRqGetCallInfo(IDriver.ANY_RQ, new NativeLong(port), IDriver.INFO_CONN_ID);
		if(result.longValue() < 1) //error occurred
		{
			System.out.println("WARN: Could not get ConnId for port [" + port + "] code: " + result.longValue());
			iDriver.ilGetLastPortError(new NativeLong(port), 10);
			return null;
		}
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		result = iDriver.ilGetReply(result, buffer, buffer.capacity());
//		buffer.flip();
		String value = getString(buffer);
		if(result.longValue() < 0) //error occurred
		{
			System.out.println("ERROR: Problem retrieving Conn ID:");
			System.out.println(value);
			return null;
		}
		return value;
	}
	
	public boolean endCall(long port)
	{
		System.out.println("INFO: Ending call on port [" + port + "]");
		PortRecord pr = activePorts.get(Long.toString(port));
		if(pr == null)
		{
			System.out.println("ERROR: Received end call on inactive port [" + port + "]");
			return false;
		}
		synchronized(pr)
		{
			if(!pr.onCall)
			{
				System.out.println("ERROR: Received end call on inactive port [" + port + "]");
				return false;
			}
			pr.lastActivity = System.currentTimeMillis();
			pr.onCall = false;
		}
		NativeLong result = iDriver.ilSRqNoteCallEnd(IDriver.ANY_RQ, new NativeLong(port));
		if(result.longValue() < 0) //error occurred
		{
			System.out.println("ERROR: Could not end call on port [" + port + "] code: " + result.longValue());
			return false;
		}
		iDriver.ilGetReply(result, ByteBuffer.allocate(100), 100);
		return true;
	}
	
	public String getUData(long port, String key)
	{
		System.out.println("INFO: Getting User data (" + key + ") for port [" + port + "]");
		PortRecord pr = activePorts.get(Long.toString(port));
		if(pr == null)
		{
			System.out.println("ERROR: Received get user data on inactive port [" + port + "]");
			return null;
		}
		synchronized(pr)
		{
			if(!pr.onCall)
			{
				System.out.println("ERROR: Received get user data on inactive port [" + port + "]");
				return null;
			}
			pr.lastActivity = System.currentTimeMillis();
		}
		//with last activity updated, no need to maintain lock
		NativeLong result = iDriver.ilSRqUDataGetKD(IDriver.ANY_RQ, new NativeLong(port), key);
		if(result.longValue() < 1) //error occurred
		{
			System.out.println("WARN: Could not get user data (" + key + ") for port [" + port + "] code: " + result.longValue());
			iDriver.ilGetLastPortError(new NativeLong(port), 10);
			return null;
		}
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		result = iDriver.ilGetReply(result, buffer, buffer.capacity());
//		buffer.flip();
		String value = getString(buffer);
		if(result.longValue() < 0) //error occurred
		{
			System.out.println("ERROR: Problem retrieving user data (" + key + "):");
			System.out.println(value);
			return null;
		}
		return value;
	}
	
	public Map<String, String> getAllUData(long port)
	{
		System.out.println("INFO: Getting User data (ALL) for port [" + port + "]");
		PortRecord pr = activePorts.get(Long.toString(port));
		if(pr == null)
		{
			System.out.println("ERROR: Received get user data on inactive port [" + port + "]");
			return null;
		}
		synchronized(pr)
		{
			if(!pr.onCall)
			{
				System.out.println("ERROR: Received get user data on inactive port [" + port + "]");
				return null;
			}
			pr.lastActivity = System.currentTimeMillis();
		}
		//with last activity updated, no need to maintain lock
		NativeLong result = iDriver.ilSRqUDataGetAll(IDriver.ANY_RQ, new NativeLong(port));
		if(result.longValue() < 0) //error occurred
		{
			System.out.println("WARN: Could not get user data (ALL) for port [" + port + "] code: " + result.longValue());
			return null;
		}
		ByteBuffer buffer = ByteBuffer.allocate(65535);
		result = iDriver.ilGetReply(result, buffer, buffer.capacity());
		Map<String, String> ret = new HashMap<String, String>();
//		buffer.flip();
		String value = getString(buffer);
		if(result.longValue() < 1) //error occurred
		{
			System.out.println("ERROR: Problem retrieving user data (ALL):");
			System.out.println(value);
			return ret;
		}
		if(value.equals("NoMatch"))
			return ret;
		System.out.println("Encoded Data String: " + value);
		String[] parts = value.split(value.substring(0, 1), -1);
		for(int i = 1; i < parts.length - 2; i += 2)
		{
			System.out.print("Inspecting Key [" + parts[i] + "...");
			if(parts[i].equals("ListName"))
			{
				System.out.println("skipping");
				for(i = i + 1; i < parts.length - 1; i++)
				{
					System.out.println("Skipping Segment: " + parts[i]);
					if(parts[i].equals("ListEnd"))
					{
						i = i - 1;
						break;
					}
				}
				continue;
			}
			System.out.println("added [" + parts[i + 1] + "]");
			ret.put(parts[i], parts[i + 1]);
		}
		return ret;
	}
	
	public boolean addUData(long port, String key, String value, boolean delete)
	{
		System.out.println("INFO: Setting User data (" + key + ":" + value + ") for port [" + port + "]");
		if(key == null || key.equals(""))
		{
			System.out.println("ERROR: Key cannot be NULL or empty string.");
			return false;
		}
		PortRecord pr = activePorts.get(Long.toString(port));
		if(pr == null)
		{
			System.out.println("ERROR: Received set user data (" + key + ":" + value + ") on inactive port [" + port + "]");
			return false;
		}
		synchronized(pr)
		{
			if(!pr.onCall)
			{
				System.out.println("ERROR: Received set user data (" + key + ":" + value + ") on inactive port [" + port + "]");
				return false;
			}
			pr.lastActivity = System.currentTimeMillis();
		}
		//with last activity updated, no need to maintain lock
		NativeLong result = iDriver.ilSRqUDataAddKD(IDriver.ANY_RQ, new NativeLong(port), key, value);
		if(result.longValue() < 0) //error occurred
		{
			System.out.println("WARN: Could not set user data (" + key + ":" + value + ") for port [" + port + "] code: " + result.longValue());
			return false;
		}
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		result = iDriver.ilGetReply(result, buffer, buffer.capacity());
//		buffer.flip();
		if(result.longValue() < 0) //error occurred
		{
			System.out.println("ERROR: Problem setting user data (" + key + ":" + value + "):");
			System.out.println(getString(buffer));
			return false;
		}
		return true;
	}
	
	public boolean addUDataList(long port, Map<String, String> pairs, boolean delete)
	{
		System.out.println("INFO: Setting User data (" + pairs + ") for port [" + port + "]");
		PortRecord pr = activePorts.get(Long.toString(port));
		if(pr == null)
		{
			System.out.println("ERROR: Received set user data (" + pairs + ") on inactive port [" + port + "]");
			return false;
		}
		synchronized(pr)
		{
			if(!pr.onCall)
			{
				System.out.println("ERROR: Received set user data (" + pairs + ") on inactive port [" + port + "]");
				return false;
			}
			pr.lastActivity = System.currentTimeMillis();
		}
		//with last activity updated, no need to maintain lock
		StringBuilder builder = new StringBuilder();
		for(Map.Entry<String, String> entry : pairs.entrySet())
		{
			String key = entry.getKey();
			if(key == null || key.equals(""))
			{
				System.out.println("WARN: Bad key in user data list.  Keys cannot be NULL or empty string.  Skipping.");
				continue;
			}
			builder.append(delimiter);
			builder.append(key);
			builder.append(delimiter);
			builder.append(entry.getValue());
		}
		String listString = builder.toString();
		System.out.println("Setting user data list: " + listString);
		NativeLong result = iDriver.ilSRqUDataAddList(IDriver.ANY_RQ, new NativeLong(port), listString);
		if(result.longValue() < 1) //error occurred
		{
			System.out.println("WARN: Could not set user data (" + listString + ") for port [" + port + "] code: " + result.longValue());
			return false;
		}
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		result = iDriver.ilGetReply(result, buffer, buffer.capacity());
//		buffer.flip();
		if(result.longValue() < 0) //error occurred
		{
			System.out.println("ERROR: Problem setting user data (" + listString + "):");
			System.out.println(getString(buffer));
			return false;
		}
		return true;
	}
	
	private String getString(ByteBuffer buffer)
	{
		Charset charset = Charset.defaultCharset();
		CharsetDecoder decoder = charset.newDecoder();
		try
		{
			String ret = decoder.decode(buffer).toString();
			StringBuilder builder = new StringBuilder();
			for(int i = 0; i < ret.length(); i++)
			{
				if(ret.charAt(i) == (char)0)
					break;
				builder.append(ret.charAt(i));
			}
			return builder.toString();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return null;
	}
	
	private boolean waitForEstablished(long port, ByteBuffer buffer)
	{
		for(int i = 0; i < 25; i++)
		{
			int status = iDriver.ilGetCallStatus(new NativeLong(port));
			if(status < 0) //error occurred
			{
				return false;
			}
			if(status == IDriver.CALL_STATUS_ESTABLISHED)
				return true;
			try
			{
				Thread.sleep(100);
			}
			catch (InterruptedException e)
			{
				break;
			}
		}
		//forcefully kill the new call to ensure proper state inside IDriver
		killCall(port, buffer);
		return false;
	}
	private void killCall(long port, ByteBuffer buffer)
	{
		NativeLong result = iDriver.ilSRqNoteCallEnd(IDriver.ANY_RQ, new NativeLong(port));
		if(result.longValue() >= 0)
		{
			buffer.clear();
			iDriver.ilGetReply(result, buffer, buffer.capacity());
		}
	}
	
	private class PortRecord
	{
		boolean onCall = false;
		long port = 0L;
		long lastActivity = System.currentTimeMillis();
		
		public PortRecord(long port)
		{
			this.port = port;
		}
	}
	
	public static void main(String[] args)
	{
		System.out.println("Starting");
//		getInstance();
	}
}
