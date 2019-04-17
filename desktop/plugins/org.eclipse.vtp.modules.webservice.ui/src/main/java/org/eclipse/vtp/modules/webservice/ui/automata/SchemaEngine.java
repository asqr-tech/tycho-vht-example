package org.eclipse.vtp.modules.webservice.ui.automata;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.vtp.desktop.model.core.schema.AbstractElementObject;
import org.eclipse.vtp.desktop.model.core.schema.ComplexContentModel;
import org.eclipse.vtp.desktop.model.core.schema.ComplexType;
import org.eclipse.vtp.desktop.model.core.schema.ContentModel;
import org.eclipse.vtp.desktop.model.core.schema.ElementGroup;
import org.eclipse.vtp.desktop.model.core.schema.ElementItem;
import org.eclipse.vtp.desktop.model.core.schema.SimpleContentModel;
import org.eclipse.vtp.desktop.model.core.schema.SimpleType;
import org.eclipse.vtp.desktop.model.core.schema.Type;
import org.eclipse.vtp.modules.webservice.ui.configuration.InputDocumentStructure;
import org.eclipse.vtp.modules.webservice.ui.configuration.document.ConditionalContainerSet;
import org.eclipse.vtp.modules.webservice.ui.configuration.document.ConditionalDocumentItem;
import org.eclipse.vtp.modules.webservice.ui.configuration.document.DocumentItem;
import org.eclipse.vtp.modules.webservice.ui.configuration.document.DocumentItemContainer;
import org.eclipse.vtp.modules.webservice.ui.configuration.document.ElementDocumentItem;
import org.eclipse.vtp.modules.webservice.ui.configuration.document.ElseDocumentItem;
import org.eclipse.vtp.modules.webservice.ui.configuration.document.ForLoopDocumentItem;
import org.eclipse.vtp.modules.webservice.ui.configuration.document.TextDocumentItem;

public class SchemaEngine
{
	private Graph baseGraph = null;
	private Type baseType = null;
	private List<EngineContext> contexts = new LinkedList<EngineContext>();
	private List<CommandListener> listeners = new LinkedList<CommandListener>();

	public SchemaEngine(Type type)
	{
		super();
		baseType = type;
		baseGraph = new TypeGraphFactory(baseType).createGraph();
		contexts.add(new EngineContext());
	}
	
	public class EngineContext implements Suggestor
	{
		private Deque<GraphContext> graphContexts = new ArrayDeque<GraphContext>();
		private List<Command> commands = new LinkedList<Command>();
		
		public EngineContext()
		{
			graphContexts.offerLast(new GraphContext(baseGraph));
		}
		
		public GraphContext getCurrentGraphContext()
		{
			return graphContexts.peekLast();
		}
		
		public void pushGraphContext(GraphContext context)
		{
			graphContexts.offerLast(context);
		}
		
		public void popGraphContext()
		{
			graphContexts.removeLast();
		}
		
		public void suggest(SuggestionCommand suggestion)
		{
			commands.add(suggestion);
		}
		
		public void command(Command command)
		{
			commands.add(command);
		}
		
		public List<Command> getCommands()
		{
			return commands;
		}
		
		public EngineContext clone()
		{
			EngineContext copy = new EngineContext();
			copy.graphContexts.clear();
			for(GraphContext context : graphContexts)
			{
				copy.graphContexts.offerLast(context.clone());
			}
			copy.commands.addAll(commands);
			return copy;
		}
	}
	
	public void processDocumentStructure(InputDocumentStructure structure)
	{
		processDocumentItemContainer(structure);
		System.out.println(contexts.size());
		if(contexts.size() > 0)
		{
			List<Iterator<Command>> contextPointers2 = new ArrayList<Iterator<Command>>(contexts.size());
			for(EngineContext context : contexts)
			{
				contextPointers2.add(context.getCommands().iterator());
			}
			while(contextPointers2.get(0).hasNext())
			{
				for(int i = 0; i < contextPointers2.size(); i++)
				{
					Iterator<Command> cp = contextPointers2.get(i);
					Command cmd = null;
					while(cp.hasNext() && (cmd = cp.next()) instanceof SuggestionCommand)
					{
						postCommand(cmd);
					}
					if(i == contextPointers2.size() - 1)
					{
						//last one in the set so go ahead and post the non
						//suggestion as it should be identical to the current
						//items of the other iterators
						postCommand(cmd);
					}
				}
			}
		}
	}
	
	public class RealizedCommandIterator implements Iterator<RealizeDocumentItemCommand>
	{
		Iterator<Command> base = null;
		RealizeDocumentItemCommand next = null;
		
		public RealizedCommandIterator(Iterator<Command> base)
		{
			super();
			this.base = base;
			while(base.hasNext())
			{
				Command cmd = base.next();
				if(cmd instanceof RealizeDocumentItemCommand)
				{
					next = (RealizeDocumentItemCommand)cmd;
					break;
				}
			}
		}
		
		public boolean hasNext()
		{
			return next != null;
		}

		public RealizeDocumentItemCommand next()
		{
			RealizeDocumentItemCommand ret = next;
			next = null;
			while(base.hasNext())
			{
				Command cmd = base.next();
				if(cmd instanceof RealizeDocumentItemCommand)
				{
					next = (RealizeDocumentItemCommand)cmd;
					break;
				}
			}
			return ret;
		}

		public void remove()
		{
			throw new UnsupportedOperationException();
		}
		
	}
	
	private void processDocumentItemContainer(DocumentItemContainer container)
	{
		if(container instanceof ConditionalContainerSet)
		{
			processContainerContainerSet((ConditionalContainerSet)container);
			postContainerBoundary();
			return;
		}
		List<DocumentItem> items = container.getItems();
		for(DocumentItem item : items)
		{
			postDocumentItem(item);
			if(item instanceof DocumentItemContainer)
			{
				processDocumentItemContainer((DocumentItemContainer)item);
			}
		}
		postContainerBoundary();
	}
	
	private void processContainerContainerSet(ConditionalContainerSet container)
	{
		if(container.getIf() != null)
		{
			postDocumentItem(container.getIf());
			processDocumentItemContainer(container.getIf());
			List<ConditionalDocumentItem> elseIfs = container.getElseIfs();
			for(ConditionalDocumentItem elseIf : elseIfs)
			{
				postDocumentItem(elseIf);
				processDocumentItemContainer(elseIf);
			}
			if(container.getElse() != null)
			{
				postDocumentItem(container.getElse());
				processDocumentItemContainer(container.getElse());
			}
		}
	}
	
	public void postDocumentItem(DocumentItem item)
	{
		List<EngineContext> cs = new LinkedList<EngineContext>(contexts);
		contexts.clear();
		System.out.println("posting: " + item + " to " + cs.size() + " contexts");
		for(EngineContext context : cs)
		{
			internalPostDocumentItem(context, item);
		}
		if(contexts.size() > 0)
		{
			List<RealizedCommandIterator> contextPointers = new ArrayList<RealizedCommandIterator>(contexts.size());
			for(EngineContext context : contexts)
			{
				contextPointers.add(new RealizedCommandIterator(context.getCommands().iterator()));
			}
			while(contextPointers.get(0).hasNext())
			{
				boolean[] validity = new boolean[contextPointers.size()];
				for(int i = 0; i < contextPointers.size(); i++)
				{
					RealizeDocumentItemCommand cmd = contextPointers.get(i).next();
					validity[i] = cmd.isValid();
				}
				boolean allValid = true;
				boolean allInvalid = true;
				for(int i = 0; i < validity.length; i++)
				{
					allValid &= validity[i];
					allInvalid &= !validity[i];
				}
				if(!(allValid || allInvalid))
				{
					//inconsistent validity, remove all invalid contexts
					cs = new LinkedList<EngineContext>(contexts);
					List<RealizedCommandIterator> cps = new ArrayList<RealizedCommandIterator>(contextPointers);
					contexts.clear();
					contextPointers.clear();
					for(int i = 0; i < validity.length; i++)
					{
						if(validity[i])
						{
							contexts.add(cs.get(i));
							contextPointers.add(cps.get(i));
						}
					}
				}
			}
		}
	}
	
	private void internalPostDocumentItem(EngineContext context, Object item)
	{
		boolean transitioned = false;
		Node node = context.getCurrentGraphContext().getCurrentNode();
		List<Transition> transitions = node.getTransitions();
		for(Transition transition : transitions)
		{
			//create a clean copy in case this context branches
			EngineContext copy = context.clone();
			GraphContext gc = copy.getCurrentGraphContext();
			if(gc.getTransitionCount(transition) < transition.getMaxTraversals())
			{
				Node destination = transition.getDestination();
				if(destination.accept(gc, item))
				{
					transitioned = true;
					gc.performTransition(transition);
					if(transition.hasSuggestion())
					{
						//post the suggestion commands here
						performSuggestions(copy, transition);
					}
					if(destination instanceof Realized)
					{
						//post the item realization command here
						RealizeDocumentItemCommand command = null;
						if(item instanceof ElementDocumentItem)
						{
							RealizeElementItemCommand elementCommand = new RealizeElementItemCommand((DocumentItem)item, true);
							ElementAcceptor acceptor = (ElementAcceptor)destination.getAcceptor();
							elementCommand.setTextOnly(acceptor.isTextOnly());
							command = elementCommand;
						}
						else
							command = new RealizeDocumentItemCommand((DocumentItem)item, true);
						copy.command(command);
					}
					if(destination instanceof ContainerNode)
					{
						ContainerNode cn = (ContainerNode)destination;
						GraphContext subContext = new GraphContext(cn.getSubGraph());
						copy.pushGraphContext(subContext);
						if(!(destination instanceof Realized))
						{
							//immediately traverse the graph as the item has not been consumed yet.
							internalPostDocumentItem(copy, item);
						}
						else
						{
							contexts.add(copy);
						}
					}
					else
					{
						contexts.add(copy);
					}
				}
			}
		}
		if(!transitioned) //take the terminal path
		{
			Transition transition = node.getTerminalTransition();
			Node destination = transition.getDestination();
			if(destination.accept(context.getCurrentGraphContext(), item))
			{
				if(transition.hasSuggestion() && !context.getCurrentGraphContext().hasMadeFinalSuggestions())
				{
					//perform transition suggestions
					performSuggestions(context, transition);
				}
				context.popGraphContext();
				if(item instanceof ContainerBoundary && destination.getAcceptor() instanceof ContainerBoundaryAcceptor)
					context.command(new ContainerBoundaryCommand());
				if(context.getCurrentGraphContext() != null)
					if(!(destination.getAcceptor() instanceof ContainerBoundaryAcceptor))
						internalPostDocumentItem(context, item);
					else
						contexts.add(context);
				else
					contexts.add(context);
			}
			else
			{
				if(item instanceof ContainerBoundary)
				{
					context.command(new ContainerBoundaryCommand());
					contexts.add(context);
				}
				else
				{
					//the terminal node of realized containers and the top level
					//type container can deny advancement until the container
					//boundary has been reached
					
					if(!context.getCurrentGraphContext().hasMadeFinalSuggestions())
					{
						context.getCurrentGraphContext().madeFinalSuggestions(true);
						if(transition.hasSuggestion())
						{
							//perform transition suggestions prior to posting invalid items
							performSuggestions(context, transition);
						}
					}
					
					//generate invalid item command
					RealizeDocumentItemCommand rdic = null;
					if(item instanceof ElementDocumentItem)
					{
						RealizeElementItemCommand elementCommand = new RealizeElementItemCommand((DocumentItem)item, false);
						rdic = elementCommand;
					}
					else
						rdic = new RealizeDocumentItemCommand((DocumentItem)item, false);
					System.out.println("invalid realized command: " + rdic.getDocumentItem());
					if(item instanceof DocumentItemContainer)
						((ContainerBoundaryAcceptor)destination.getAcceptor()).newContainer(context.getCurrentGraphContext());
					context.command(rdic);
					
					contexts.add(context);
				}
			}
		}
	}
	
	public void postContainerBoundary()
	{
		List<EngineContext> cs = new LinkedList<EngineContext>(contexts);
		contexts.clear();
		System.out.println("posting container boundary to " + cs.size() + " contexts");
		for(EngineContext context : cs)
		{
			if(context.getCurrentGraphContext() != null)
				internalPostDocumentItem(context, new ContainerBoundary());
		}
	}
	
	private void performSuggestions(EngineContext context, Transition transition)
	{
		GraphContext currentContext = context.getCurrentGraphContext();
		Node origin = transition.getOrigin();
		List<Node> visited = new LinkedList<Node>();
		visited.add(transition.getDestination());
		List<Transition> subTransitions = origin.getTransitions();
		for(Transition t : subTransitions)
		{
			if(currentContext.getTransitionCount(t) < t.getMaxTraversals())
			{
				t.getDestination().suggest(visited, context);
				if(!(t.getDestination() instanceof Realized))
					traverseSuggestions(context, visited, t.getDestination());
			}
		}
	}
	
	private void traverseSuggestions(Suggestor suggestor, List<Node> visited, Node node)
	{
		List<Transition> subTransitions = node.getTransitions();
		for(Transition t : subTransitions)
		{
			t.getDestination().suggest(visited, suggestor);
			if(!(t.getDestination() instanceof Realized))
				traverseSuggestions(suggestor, visited, t.getDestination());
		}
	}
	
	public void addCommandListener(CommandListener l)
	{
		listeners.remove(l);
		listeners.add(l);
	}
	
	public void removeCommandListener(CommandListener l)
	{
		listeners.remove(l);
	}
	
	private void postCommand(Command command)
	{
		for(CommandListener l : listeners)
		{
			l.proccess(command);
		}
	}
	
	public class ForContainerAcceptor implements Acceptor
	{
		public boolean accept(GraphContext context, Object obj)
		{
			return obj instanceof ForLoopDocumentItem;
		}
	}
	
	public class CCSContainerAcceptor implements Acceptor
	{
		public boolean accept(GraphContext context, Object obj)
		{
			return obj instanceof ConditionalContainerSet;
		}
	}
	
	public class ConditionalContainerAcceptor implements Acceptor
	{
		public boolean accept(GraphContext context, Object obj)
		{
			return obj instanceof ConditionalDocumentItem;
		}
	}
	
	public class ElseContainerAcceptor implements Acceptor
	{
		public boolean accept(GraphContext context, Object obj)
		{
			return obj instanceof ElseDocumentItem;
		}
	}
	
	public class TextContentAcceptor implements Acceptor
	{
		public boolean accept(GraphContext context, Object obj)
		{
			return obj instanceof TextDocumentItem;
		}
	}
	
	public class ElementAcceptor implements Acceptor
	{
		private ElementItem element = null;
		
		public ElementAcceptor(ElementItem element)
		{
			super();
			this.element = element;
		}
		
		public boolean accept(GraphContext context, Object obj)
		{
			if(obj instanceof ElementDocumentItem)
			{
				return ((ElementDocumentItem)obj).getName().equals(element.getName());
			}
			return false;
		}
		
		public boolean isTextOnly()
		{
			Type type = element.getType();
			if(type instanceof SimpleType)
				return true;
			ComplexType complexType = (ComplexType)type;
			if(complexType.getContentModel() instanceof SimpleContentModel)
				return true;
			return false;
		}
	}
	
	public class CCSGraphFactory implements GraphFactory
	{
		private GraphFactory contentFactory = null;
		
		public CCSGraphFactory(GraphFactory contentFactory)
		{
			super();
			this.contentFactory = contentFactory;
		}
		
		public Graph createGraph()
		{
			Graph ret = new Graph();
			Transition terminalTransition = new Transition(ret.getInitialNode(), ret.getTerminalNode(), true);
			terminalTransition.setValid(false);
			RealizedContainerNode ifContainer = new RealizedContainerNode(new ConditionalContainerAcceptor(), contentFactory);
			new Transition(ret.getInitialNode(), ifContainer);
			new Transition(ifContainer, ret.getTerminalNode(), true);
			RealizedContainerNode elseIfContainer = new RealizedContainerNode(new ConditionalContainerAcceptor(), contentFactory);
			elseIfContainer.setSuggestion(new ElseIfSuggestionCommand());
			new Transition(ifContainer, elseIfContainer);
			Transition elseIfReentryTransition = new Transition(elseIfContainer, elseIfContainer);
			elseIfReentryTransition.setMaxTraversals(Integer.MAX_VALUE);
			new Transition(elseIfContainer, ret.getTerminalNode(), true);
			RealizedContainerNode elseContainer = new RealizedContainerNode(new ElseContainerAcceptor(), contentFactory);
			elseContainer.setSuggestion(new ElseSuggestionCommand());
			new Transition(ifContainer, elseContainer, true);
			new Transition(elseIfContainer, elseContainer, true);
			new Transition(elseContainer, ret.getTerminalNode());
			return ret;
		}
	}
	
	public class ElementGraphFactory implements GraphFactory
	{
		private ElementItem element = null;
		private int minOccurs = 1;
		private int maxOccurs = 1;
		
		public ElementGraphFactory(ElementItem element)
		{
			super();
			this.element = element;
			this.minOccurs = element.getMinOccurs();
			this.maxOccurs = element.getMaxOccurs();
		}
		
		public ElementGraphFactory(ElementItem element, int overrideMinOccurs, int overrideMaxOccurs)
		{
			super();
			this.element = element;
			this.minOccurs = overrideMinOccurs;
			this.maxOccurs = overrideMaxOccurs;
		}
		
		public Graph createGraph()
		{
			Graph ret = new Graph();
			Transition terminalTransition = new Transition(ret.getInitialNode(), ret.getTerminalNode(), true);
			if(minOccurs > 0)
				terminalTransition.setValid(false);	
			if(maxOccurs > 1) //eligible for looping
			{
				RealizedContainerNode forContainer = new RealizedContainerNode(new ForContainerAcceptor(), new ElementGraphFactory(element, 1, 1));
				forContainer.setSuggestion(new ForLoopSuggestionCommand());
				new Transition(ret.getInitialNode(), forContainer);
				new Transition(forContainer, ret.getTerminalNode());
			}
			RealizedContainerNode ccsContainer = new RealizedContainerNode(new CCSContainerAcceptor(), new CCSGraphFactory(this));
			ccsContainer.setSuggestion(new CCSSuggestionCommand());
			new Transition(ret.getInitialNode(), ccsContainer);
			new Transition(ccsContainer, ret.getTerminalNode());
			Node originNode = ret.getInitialNode();
			for(int i = 0; i < minOccurs - 1; i++)
			{
				RealizedContainerNode elementNode = new RealizedContainerNode(new ElementAcceptor(element), new TypeGraphFactory(element.getType()));
				ElementSuggestionCommand suggestion = new ElementSuggestionCommand(element);
				suggestion.setRequired(true);
				elementNode.setSuggestion(suggestion);
				new Transition(originNode, elementNode);
				terminalTransition = new Transition(elementNode, ret.getTerminalNode(), true);
				terminalTransition.setValid(false);
				originNode = elementNode;
			}
			RealizedContainerNode finalElementNode = new RealizedContainerNode(new ElementAcceptor(element), new TypeGraphFactory(element.getType()));
			ElementSuggestionCommand suggestion = new ElementSuggestionCommand(element);
			suggestion.setRequired(minOccurs > 0);
			finalElementNode.setSuggestion(suggestion);
			new Transition(originNode, finalElementNode);
			if(maxOccurs - minOccurs > 1)
			{
				Transition loopTransition = new Transition(finalElementNode, finalElementNode);
				loopTransition.setMaxTraversals(maxOccurs - Math.max(minOccurs, 1));
			}
			new Transition(finalElementNode, ret.getTerminalNode(), true);
			return ret;
		}
	}
	
	public class SequenceGraphFactory implements GraphFactory
	{
		private ElementGroup group = null;
		private int minOccurs = 1;
		private int maxOccurs = 1;
		
		public SequenceGraphFactory(ElementGroup group)
		{
			super();
			this.group = group;
			this.minOccurs = group.getMinOccurs();
			this.maxOccurs = group.getMaxOccurs();
		}
		
		public Graph createGraph()
		{
			Graph ret = new Graph();
			Transition terminalTransition = new Transition(ret.getInitialNode(), ret.getTerminalNode(), true);
			if(minOccurs > 0)
				terminalTransition.setValid(false);	
			if(maxOccurs > 1) //eligible for looping
			{
				RealizedContainerNode forContainer = new RealizedContainerNode(new ForContainerAcceptor(), new SequenceContentsGraphFactory(group));
				forContainer.setSuggestion(new ForLoopSuggestionCommand());
				new Transition(ret.getInitialNode(), forContainer);
				new Transition(forContainer, ret.getTerminalNode());
			}
			RealizedContainerNode ccsContainer = new RealizedContainerNode(new CCSContainerAcceptor(), new CCSGraphFactory(this));
			ccsContainer.setSuggestion(new CCSSuggestionCommand());
			new Transition(ret.getInitialNode(), ccsContainer);
			new Transition(ccsContainer, ret.getTerminalNode());
			Node originNode = ret.getInitialNode();
			for(int i = 0; i < minOccurs - 1; i++)
			{
				NonRealizedContainerNode elementNode = new NonRealizedContainerNode(new SequenceContentsGraphFactory(group).createGraph());
				new Transition(originNode, elementNode);
				terminalTransition = new Transition(elementNode, ret.getTerminalNode(), true);
				terminalTransition.setValid(false);
				originNode = elementNode;
			}
			NonRealizedContainerNode finalElementNode = new NonRealizedContainerNode(new SequenceContentsGraphFactory(group).createGraph());
			new Transition(originNode, finalElementNode);
			if(maxOccurs - minOccurs > 1)
			{
				Transition loopTransition = new Transition(finalElementNode, finalElementNode, true);
				loopTransition.setMaxTraversals(maxOccurs - Math.max(minOccurs, 1));
			}
			new Transition(finalElementNode, ret.getTerminalNode());
			return ret;
		}
	}
	
	public class SequenceContentsGraphFactory implements GraphFactory
	{
		private ElementGroup group = null;
		
		public SequenceContentsGraphFactory(ElementGroup group)
		{
			super();
			this.group = group;
		}
		
		public Graph createGraph()
		{
			Graph ret = new Graph();
			Transition terminalTransition = new Transition(ret.getInitialNode(), ret.getTerminalNode(), true);
			Node originNode = ret.getInitialNode();
			List<AbstractElementObject> children = group.getElementObjects();
			for(AbstractElementObject child : children)
			{
				if(child instanceof ElementItem)
				{
					NonRealizedContainerNode elementNode = new NonRealizedContainerNode(new ElementGraphFactory((ElementItem)child).createGraph());
					new Transition(originNode, elementNode);
					terminalTransition = new Transition(elementNode, ret.getTerminalNode(), true);
					terminalTransition.setValid(false);
					originNode = elementNode;
				}
				else if(child instanceof ElementGroup)
				{
					NonRealizedContainerNode elementGroupNode = null;
					ElementGroup childGroup = (ElementGroup)child;
					if(childGroup.getType().equals(ElementGroup.SEQUENCE))
					{
						elementGroupNode = new NonRealizedContainerNode(new SequenceGraphFactory(childGroup).createGraph());
					}
					else if(childGroup.getType().equals(ElementGroup.CHOICE))
					{
						elementGroupNode = new NonRealizedContainerNode(new ChoiceGraphFactory(childGroup).createGraph());
					}
					new Transition(originNode, elementGroupNode);
					terminalTransition = new Transition(elementGroupNode, ret.getTerminalNode(), true);
					terminalTransition.setValid(false);
					originNode = elementGroupNode;
				}
			}
			return ret;
		}
	}
	
	public class ChoiceGraphFactory implements GraphFactory
	{
		private ElementGroup group = null;
		private int minOccurs = 1;
		private int maxOccurs = 1;
		
		public ChoiceGraphFactory(ElementGroup group)
		{
			super();
			this.group = group;
			this.minOccurs = group.getMinOccurs();
			this.maxOccurs = group.getMaxOccurs();
		}
		
		public Graph createGraph()
		{
			Graph ret = new Graph();
			Transition terminalTransition = new Transition(ret.getInitialNode(), ret.getTerminalNode(), true);
			if(minOccurs > 0)
				terminalTransition.setValid(false);	
			if(maxOccurs > 1) //eligible for looping
			{
				RealizedContainerNode forContainer = new RealizedContainerNode(new ForContainerAcceptor(), new ChoiceContentsGraphFactory(group));
				forContainer.setSuggestion(new ForLoopSuggestionCommand());
				new Transition(ret.getInitialNode(), forContainer);
				new Transition(forContainer, ret.getTerminalNode());
			}
			RealizedContainerNode ccsContainer = new RealizedContainerNode(new CCSContainerAcceptor(), new CCSGraphFactory(this));
			ccsContainer.setSuggestion(new CCSSuggestionCommand());
			new Transition(ret.getInitialNode(), ccsContainer);
			new Transition(ccsContainer, ret.getTerminalNode());
			Node originNode = ret.getInitialNode();
			for(int i = 0; i < minOccurs - 1; i++)
			{
				NonRealizedContainerNode elementNode = new NonRealizedContainerNode(new ChoiceContentsGraphFactory(group).createGraph());
				new Transition(originNode, elementNode);
				terminalTransition = new Transition(elementNode, ret.getTerminalNode(), true);
				terminalTransition.setValid(false);
				originNode = elementNode;
			}
			NonRealizedContainerNode finalElementNode = new NonRealizedContainerNode(new ChoiceContentsGraphFactory(group).createGraph());
			new Transition(originNode, finalElementNode);
			if(maxOccurs - minOccurs > 1)
			{
				Transition loopTransition = new Transition(finalElementNode, finalElementNode);
				loopTransition.setMaxTraversals(maxOccurs - Math.max(minOccurs, 1));
			}
			new Transition(finalElementNode, ret.getTerminalNode(), true);
			return ret;
		}
	}
	
	public class ChoiceContentsGraphFactory implements GraphFactory
	{
		private ElementGroup group = null;
		
		public ChoiceContentsGraphFactory(ElementGroup group)
		{
			super();
			this.group = group;
		}
		
		public Graph createGraph()
		{
			Graph ret = new Graph();
			Transition terminalTransition = new Transition(ret.getInitialNode(), ret.getTerminalNode(), true);
			Node originNode = ret.getInitialNode();
			List<AbstractElementObject> children = group.getElementObjects();
			for(AbstractElementObject child : children)
			{
				if(child instanceof ElementItem)
				{
					NonRealizedContainerNode elementNode = new NonRealizedContainerNode(new ElementGraphFactory((ElementItem)child).createGraph());
					new Transition(originNode, elementNode);
					terminalTransition = new Transition(elementNode, ret.getTerminalNode(), true);
					terminalTransition.setValid(true);
				}
				else if(child instanceof ElementGroup)
				{
					NonRealizedContainerNode elementGroupNode = null;
					ElementGroup childGroup = (ElementGroup)child;
					if(childGroup.getType().equals(ElementGroup.SEQUENCE))
					{
						elementGroupNode = new NonRealizedContainerNode(new SequenceGraphFactory(childGroup).createGraph());
					}
					else if(childGroup.getType().equals(ElementGroup.CHOICE))
					{
						elementGroupNode = new NonRealizedContainerNode(new ChoiceGraphFactory(childGroup).createGraph());
					}
					new Transition(originNode, elementGroupNode);
					terminalTransition = new Transition(elementGroupNode, ret.getTerminalNode(), true);
					terminalTransition.setValid(true);
				}
			}
			return ret;
		}
	}
	
	public class TypeGraphFactory implements GraphFactory
	{
		private Type type = null;
		
		public TypeGraphFactory(Type type)
		{
			super();
			this.type = type;
		}
		
		public Graph createGraph()
		{
			TextSuggestionCommand suggestion = new TextSuggestionCommand();
			suggestion.setRequired(true);
			if(type instanceof SimpleType)
			{
				Graph ret = new Graph();
				@SuppressWarnings("unused")
				Transition terminalTransition = new Transition(ret.getInitialNode(), ret.getTerminalNode(), true);
				ret.getTerminalNode().setAcceptor(new ContainerBoundaryAcceptor());
//				RealizedContainerNode ccsContainer = new RealizedContainerNode(new CCSContainerAcceptor(), new CCSGraphFactory(this));
//				ccsContainer.setSuggestion(new CCSSuggestionCommand());
//				new Transition(ret.getInitialNode(), ccsContainer);
//				new Transition(ccsContainer, ret.getTerminalNode());
				RealizedNode textNode = new RealizedNode(new TextContentAcceptor());
				textNode.setSuggestion(suggestion);
				new Transition(ret.getInitialNode(), textNode);
				new Transition(textNode, ret.getTerminalNode());
				return ret;
			}
			ComplexType complexType = (ComplexType)type;
			ContentModel contentModel = complexType.getContentModel();
			if(contentModel instanceof SimpleContentModel)
			{
				Graph ret = new Graph();
				@SuppressWarnings("unused")
				Transition terminalTransition = new Transition(ret.getInitialNode(), ret.getTerminalNode(), true);
				ret.getTerminalNode().setAcceptor(new ContainerBoundaryAcceptor());
//				RealizedContainerNode ccsContainer = new RealizedContainerNode(new CCSContainerAcceptor(), new CCSGraphFactory(this));
//				ccsContainer.setSuggestion(new CCSSuggestionCommand());
//				new Transition(ret.getInitialNode(), ccsContainer);
//				new Transition(ccsContainer, ret.getTerminalNode());
				RealizedNode textNode = new RealizedNode(new TextContentAcceptor());
				textNode.setSuggestion(suggestion);
				new Transition(ret.getInitialNode(), textNode);
				new Transition(textNode, ret.getTerminalNode());
				return ret;
			}
			Graph ret = null;
			ComplexContentModel ccm = (ComplexContentModel)contentModel;
			ElementGroup group = ccm.getElementGroup();
			if(group.getType().equals(ElementGroup.SEQUENCE))
			{
				ret = new SequenceGraphFactory(group).createGraph();
			}
			else if(group.getType().equals(ElementGroup.CHOICE))
			{
				ret = new ChoiceGraphFactory(group).createGraph();
			}
			ret.getTerminalNode().setAcceptor(new ContainerBoundaryAcceptor());
			return ret;
		}
	}
}
