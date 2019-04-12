package org.eclipse.vtp.modules.webservice.ui.automata;

import java.util.HashMap;
import java.util.Map;

public class GraphContext
{
	private Graph graph = null;
	private Node currentNode = null;
	private boolean error = false;
	private boolean finalSuggestionsMade = false;
	private Map<Transition, Integer> transitionCounters = new HashMap<Transition, Integer>();
	private Map<String, Object> properties = new HashMap<String, Object>();
	
	public GraphContext(Graph graph)
	{
		super();
		this.graph = graph;
		currentNode = graph.getInitialNode();
	}
	
	public Node getCurrentNode()
	{
		return currentNode;
	}
	
	public int getTransitionCount(Transition transition)
	{
		Integer count = transitionCounters.get(transition);
		return count == null ? 0 : count;
	}
	
	public void performTransition(Transition transition)
	{
		Integer i = transitionCounters.get(transition);
		if(i == null)
			i = new Integer(0);
		transitionCounters.put(transition, ++i);
		currentNode = transition.getDestination();
	}
	
	public Object getProperty(String name)
	{
		return properties.get(name);
	}
	
	public void setProperty(String name, Object value)
	{
		properties.put(name, value);
	}
	
	public boolean hasError()
	{
		return error;
	}
	
	public void setError(boolean error)
	{
		this.error = error;
	}
	
	public boolean hasMadeFinalSuggestions()
	{
		return finalSuggestionsMade;
	}
	
	public void madeFinalSuggestions(boolean bool)
	{
		this.finalSuggestionsMade = bool;
	}

	public GraphContext clone()
	{
		GraphContext copy = new GraphContext(graph);
		copy.transitionCounters.putAll(transitionCounters);
		copy.currentNode = currentNode;
		copy.properties.putAll(properties);
		return copy;
	}
}
