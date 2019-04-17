package org.eclipse.vtp.modules.webservice.ui.automata;

import java.util.ArrayList;
import java.util.List;

public class Node
{
	private Acceptor acceptor = null;
	private SuggestionCommand suggestion = null;
	private Transition terminalTransition = null;
	private List<Transition> transitions = new ArrayList<Transition>();

	public Node(Acceptor acceptor)
	{
		super();
		this.acceptor = acceptor;
	}
	
	public Acceptor getAcceptor()
	{
		return acceptor;
	}
	
	public void setAcceptor(Acceptor acceptor)
	{
		this.acceptor = acceptor;
	}
	
	public boolean accept(GraphContext context, Object obj)
	{
		return acceptor == null || acceptor.accept(context, obj);
	}

	public List<Transition> getTransitions()
	{
		return transitions;
	}
	
	public void addTransition(Transition transition)
	{
		if(transition.getDestination() instanceof TerminalNode)
			terminalTransition = transition;
		else transitions.add(transition);
	}
	
	public Transition getTerminalTransition()
	{
		return terminalTransition;
	}
	
	public void setSuggestion(SuggestionCommand suggestion)
	{
		this.suggestion = suggestion;
	}
	
	public void suggest(List<Node> visited, Suggestor suggestor)
	{
		if(!visited.contains(this))
		{
			visited.add(this);
			if(suggestion != null)
				suggestor.suggest(suggestion);
		}
	}
}
