package org.eclipse.vtp.modules.webservice.ui.automata;

public class ContainerNode extends Node
{
	private Graph subGraph = null;

	public ContainerNode(Acceptor acceptor)
	{
		super(acceptor);
	}
	
	protected ContainerNode(Acceptor acceptor, Graph subGraph)
	{
		super(acceptor);
		this.subGraph = subGraph;
	}

	public Graph getSubGraph()
	{
		return subGraph;
	}
	
	protected void setSubGraph(Graph subGraph)
	{
		this.subGraph = subGraph;
	}
	
}
