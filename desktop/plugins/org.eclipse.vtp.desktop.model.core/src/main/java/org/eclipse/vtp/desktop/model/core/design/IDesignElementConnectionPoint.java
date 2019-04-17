package org.eclipse.vtp.desktop.model.core.design;

@SuppressWarnings("rawtypes")
public interface IDesignElementConnectionPoint extends Comparable
{
	public IDesignElement getSourceElement();
	
	public String getName();
	
	public ConnectionPointType getType();
	
	public IDesignConnector getDesignConnector();
	
	public enum ConnectionPointType
	{
		HOOK_POINT (1),
		EXIT_POINT (2),
		ERROR_POINT (4);
		
		private int bits;
		
		private ConnectionPointType(int bits)
		{
			this.bits = bits;
		}
		
		public boolean isSet(int flagSet)
		{
			return (flagSet & bits) == bits;
		}
		
		public static int getFlagSet(ConnectionPointType... types)
		{
			int flagSet = 0;
			for(ConnectionPointType type : types)
			{
				flagSet |= type.bits;
			}
			return flagSet;
		}
	}
}
