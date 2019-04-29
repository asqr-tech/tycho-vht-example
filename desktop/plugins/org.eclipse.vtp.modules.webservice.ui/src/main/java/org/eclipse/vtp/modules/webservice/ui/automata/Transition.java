package org.eclipse.vtp.modules.webservice.ui.automata;

public class Transition
{
	private Node origin = null;
	private Node destination = null;
	private int maxTraversals = 1;
	private boolean valid = true;
	private boolean suggestion = false;

	public Transition(Node origin, Node destination)
	{
		this(origin, destination, false);
	}
	
	public Transition(Node origin, Node destination, boolean suggestion)
	{
		super();
		this.origin = origin;
		this.destination = destination;
		this.suggestion = suggestion;
		origin.addTransition(this);
	}

	public Node getOrigin()
	{
		return origin;
	}
	
	public Node getDestination()
	{
		return destination;
	}
	
	public int getMaxTraversals()
	{
		return maxTraversals;
	}
	
	public void setMaxTraversals(int maxTraversals)
	{
		this.maxTraversals = maxTraversals;
	}
	
	public boolean isValid()
	{
		return valid;
	}
	
	public void setValid(boolean valid)
	{
		this.valid = valid;
	}
	
	public boolean hasSuggestion()
	{
		return suggestion;
	}
	
	public void setSuggestion(boolean suggestion)
	{
		this.suggestion = suggestion;
	}
}
