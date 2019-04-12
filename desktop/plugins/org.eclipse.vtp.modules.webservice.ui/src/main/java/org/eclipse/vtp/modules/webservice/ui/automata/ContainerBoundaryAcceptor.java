package org.eclipse.vtp.modules.webservice.ui.automata;

public class ContainerBoundaryAcceptor implements Acceptor
{
	public ContainerBoundaryAcceptor()
	{
		super();
	}

	public boolean accept(GraphContext context, Object obj)
	{
		boolean boundary = obj instanceof ContainerBoundary;
		if(boundary)
		{
			int invalidCount = 0;
			Integer temp = (Integer)context.getProperty("Invalid_Count");
			if(temp != null)
				invalidCount = temp;
			boundary &= invalidCount < 1;
			invalidCount--;
			context.setProperty("Invalid_Count", invalidCount);
		}
		return boundary;
	}

	public void newContainer(GraphContext context)
	{
		int invalidCount = 0;
		Integer temp = (Integer)context.getProperty("Invalid_Count");
		if(temp != null)
			invalidCount = temp;
		invalidCount++;
		context.setProperty("Invalid_Count", invalidCount);
	}
}
