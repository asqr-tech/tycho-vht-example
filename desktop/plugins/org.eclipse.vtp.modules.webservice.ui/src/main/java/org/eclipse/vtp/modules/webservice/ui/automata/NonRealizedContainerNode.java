package org.eclipse.vtp.modules.webservice.ui.automata;

import java.util.List;

public class NonRealizedContainerNode extends ContainerNode
{
	public NonRealizedContainerNode(Graph subGraph)
	{
		super(null, subGraph);
	}

	public void suggest(List<Node> visited, Suggestor suggestor)
	{
		super.suggest(visited, suggestor);
		if(!visited.contains(this))
		{
			Node initialNode = getSubGraph().getInitialNode();
			traverseSuggestions(visited, initialNode, suggestor);
		}
	}
	
	private void traverseSuggestions(List<Node> visited, Node node, Suggestor suggestor)
	{
		List<Transition> subTransitions = node.getTransitions();
		for(Transition t : subTransitions)
		{
			if(!visited.contains(t.getDestination()))
			{
				t.getDestination().suggest(visited, suggestor);
				if(!(t.getDestination() instanceof Realized))
					traverseSuggestions(visited, t.getDestination(), suggestor);
			}
		}
	}
}
