package org.eclipse.vtp.modules.webservice.ui.automata;

public class RealizedContainerNode extends ContainerNode implements Realized
{
	private GraphFactory factory = null;

	public RealizedContainerNode(Acceptor acceptor, GraphFactory factory)
	{
		super(acceptor);
		this.factory = factory;
	}

	public boolean accept(GraphContext context, Object obj)
	{
		//the creation of the sub graph is delayed until this node is activated to avoid infinite loops
		boolean ret = getAcceptor().accept(context, obj);
		if(ret)
		{
			if(getSubGraph() == null)
			{
				setSubGraph(factory.createGraph());
				getSubGraph().getTerminalNode().setAcceptor(new ContainerBoundaryAcceptor());
			}
		}
		return ret;
	}
}
