package org.eclipse.vtp.modules.webservice.ui.automata;

public class Graph
{
	private InitialNode initialNode = null;
	private TerminalNode terminalNode = null;

	public Graph()
	{
		super();
		initialNode = new InitialNode();
		terminalNode = new TerminalNode();
	}

	public InitialNode getInitialNode()
	{
		return initialNode;
	}
	
	public TerminalNode getTerminalNode()
	{
		return terminalNode;
	}
}
