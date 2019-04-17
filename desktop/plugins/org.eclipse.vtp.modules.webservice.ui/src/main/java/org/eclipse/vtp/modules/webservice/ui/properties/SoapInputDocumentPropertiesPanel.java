package org.eclipse.vtp.modules.webservice.ui.properties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.Twistie;
import org.eclipse.vtp.desktop.editors.core.configuration.ComponentPropertiesPanel;
import org.eclipse.vtp.desktop.editors.core.configuration.DesignElementPropertiesPanel;
import org.eclipse.vtp.desktop.editors.core.widgets.UIHelper;
import org.eclipse.vtp.desktop.editors.core.widgets.ValueControl;
import org.eclipse.vtp.desktop.model.core.FieldType;
import org.eclipse.vtp.desktop.model.core.FieldType.Primitive;
import org.eclipse.vtp.desktop.model.core.branding.IBrand;
import org.eclipse.vtp.desktop.model.core.design.IDesignElement;
import org.eclipse.vtp.desktop.model.core.design.ObjectDefinition;
import org.eclipse.vtp.desktop.model.core.design.Variable;
import org.eclipse.vtp.desktop.model.core.internal.VariableHelper;
import org.eclipse.vtp.desktop.model.core.internal.branding.BrandContext;
import org.eclipse.vtp.desktop.model.core.schema.AttributeItem;
import org.eclipse.vtp.desktop.model.core.schema.ComplexContentModel;
import org.eclipse.vtp.desktop.model.core.schema.ComplexType;
import org.eclipse.vtp.desktop.model.core.schema.ContentModel;
import org.eclipse.vtp.desktop.model.core.schema.ElementGroup;
import org.eclipse.vtp.desktop.model.core.schema.ElementItem;
import org.eclipse.vtp.desktop.model.core.schema.SimpleContentModel;
import org.eclipse.vtp.desktop.model.core.schema.SimpleType;
import org.eclipse.vtp.desktop.model.core.schema.Type;
import org.eclipse.vtp.desktop.model.core.wsdl.ElementPart;
import org.eclipse.vtp.desktop.model.core.wsdl.Part;
import org.eclipse.vtp.desktop.model.core.wsdl.TypedPart;
import org.eclipse.vtp.desktop.model.core.wsdl.soap.SoapBindingOperation;
import org.eclipse.vtp.modules.webservice.ui.Activator;
import org.eclipse.vtp.modules.webservice.ui.automata.CCSSuggestionCommand;
import org.eclipse.vtp.modules.webservice.ui.automata.Command;
import org.eclipse.vtp.modules.webservice.ui.automata.CommandListener;
import org.eclipse.vtp.modules.webservice.ui.automata.ContainerBoundaryCommand;
import org.eclipse.vtp.modules.webservice.ui.automata.ElementSuggestionCommand;
import org.eclipse.vtp.modules.webservice.ui.automata.ElseIfSuggestionCommand;
import org.eclipse.vtp.modules.webservice.ui.automata.ElseSuggestionCommand;
import org.eclipse.vtp.modules.webservice.ui.automata.ForLoopSuggestionCommand;
import org.eclipse.vtp.modules.webservice.ui.automata.RealizeDocumentItemCommand;
import org.eclipse.vtp.modules.webservice.ui.automata.RealizeElementItemCommand;
import org.eclipse.vtp.modules.webservice.ui.automata.SchemaEngine;
import org.eclipse.vtp.modules.webservice.ui.automata.SuggestionCommand;
import org.eclipse.vtp.modules.webservice.ui.automata.TextSuggestionCommand;
import org.eclipse.vtp.modules.webservice.ui.configuration.BrandBinding;
import org.eclipse.vtp.modules.webservice.ui.configuration.BrandedBinding;
import org.eclipse.vtp.modules.webservice.ui.configuration.InputDocumentStructure;
import org.eclipse.vtp.modules.webservice.ui.configuration.WebserviceBindingManager;
import org.eclipse.vtp.modules.webservice.ui.configuration.document.ConditionalContainerSet;
import org.eclipse.vtp.modules.webservice.ui.configuration.document.ConditionalDocumentItem;
import org.eclipse.vtp.modules.webservice.ui.configuration.document.DocumentItem;
import org.eclipse.vtp.modules.webservice.ui.configuration.document.DocumentItemContainer;
import org.eclipse.vtp.modules.webservice.ui.configuration.document.ElementAttributeDocumentItem;
import org.eclipse.vtp.modules.webservice.ui.configuration.document.ElementDocumentItem;
import org.eclipse.vtp.modules.webservice.ui.configuration.document.ElseDocumentItem;
import org.eclipse.vtp.modules.webservice.ui.configuration.document.ForLoopDocumentItem;
import org.eclipse.vtp.modules.webservice.ui.configuration.document.TextDocumentItem;
import org.eclipse.vtp.modules.webservice.ui.widgets.ObjectDefinitionFilter;
import org.eclipse.vtp.modules.webservice.ui.widgets.ValueStack;
import org.eclipse.vtp.modules.webservice.ui.widgets.VariableBrowserDialog;

public class SoapInputDocumentPropertiesPanel extends
	DesignElementPropertiesPanel implements IOperationListener
{
	private WebserviceBindingManager manager = null;
	private InputDocumentStructure structure = null;
	private SoapBindingOperation currentOperation = null;
	private ScrolledComposite scrollComp = null;
	private Composite rootComp = null;
	boolean composing = true;
	private SchemaEngine schemaEngine = null;
	/**	Holds a sorted list of the variables available to this element */
	private List<Variable> variables = new ArrayList<Variable>();
	List<BrandedUIItem> valueStacks = new LinkedList<BrandedUIItem>();
	Map<UIContentComposite, List<Variable>> variableScopes = new HashMap<UIContentComposite, List<Variable>>();
	private Map<DocumentItem, Map<String, Boolean>> expansionStates = 
		new HashMap<DocumentItem, Map<String, Boolean>>();

	public SoapInputDocumentPropertiesPanel(String name, IDesignElement element)
	{
		super(name, element);
		manager = (WebserviceBindingManager)element.getConfigurationManager(WebserviceBindingManager.TYPE_ID);
		structure = manager.getInputDocumentStructure();
		variables = element.getDesign().getVariablesFor(element);
	}
	
	@Override
	public void createControls(Composite parent)
	{
		FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		Composite mainComp = new Composite(parent, SWT.NONE);
		mainComp.setBackground(parent.getBackground());
		mainComp.setLayout(new GridLayout(1, false));
		
		final Section contentSection =
			toolkit.createSection(mainComp, Section.TITLE_BAR);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL
						| GridData.VERTICAL_ALIGN_BEGINNING);
		gd.horizontalSpan = 1;
		contentSection.setLayoutData(gd);
		contentSection.setText("Input Document Contents");
		
		scrollComp = new ScrolledComposite(mainComp, SWT.V_SCROLL | SWT.BORDER);
		scrollComp.setAlwaysShowScrollBars(true);
		scrollComp.setExpandHorizontal(true);
		scrollComp.setExpandVertical(false);
		scrollComp.getVerticalBar().setIncrement(10);
		scrollComp.setBackgroundMode(SWT.INHERIT_DEFAULT);
		scrollComp.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		GridData layoutData = new GridData(GridData.FILL_BOTH);
		layoutData.heightHint = 400;
		layoutData.widthHint = 600;
		scrollComp.setLayoutData(layoutData);
		
		buildDocumentDisplay();
	}
	
	Point origin = null;
	
	public void markOrigin()
	{
		origin = scrollComp.getOrigin();
	}
	
	public void restoreOrigin()
	{
		scrollComp.setOrigin(origin);
	}
	
	public void buildDocumentDisplay()
	{
		scrollComp.setLayoutDeferred(true);
		if(rootComp != null)
		{
			for(BrandedUIItem brandedItem : valueStacks)
			{
				brandedItem.getValueStack().save();
			}
			rootComp.dispose();
		}
		valueStacks.clear();
		rootComp = new Composite(scrollComp, SWT.NONE);
		scrollComp.setContent(rootComp);
		GridLayout layout = new GridLayout(1, false);
		layout.verticalSpacing = 0;
		layout.marginWidth = 0;
		layout.marginLeft = 5;
		rootComp.setLayout(layout);
		if(currentOperation == null)
		{
			Label messageText = new Label(rootComp, SWT.NONE);
			messageText.setText("No soap operation has been selected.");
			messageText.setSize(10, 200);
			scrollComp.setMinSize(1, 200);
		}
		else
		{
			try
			{
				List<Part> bodyParts = currentOperation.getInput().getBody().getParts();
				for(Part part : bodyParts)
				{
					if(part instanceof TypedPart)
					{
						TypedPart tp = (TypedPart)part;
						Type type = tp.getType();
						schemaEngine = new SchemaEngine(type);
					}
					else
					{
						ElementPart ep = (ElementPart)part;
						ElementItem ei= ep.getElementItem();
						ComplexType rootType = new ComplexType(ei.getOwnerSchema(), "roottype");
						ComplexContentModel ccm = new ComplexContentModel(false);
						ElementGroup eg = new ElementGroup(ei.getOwnerSchema(), ElementGroup.SEQUENCE);
						eg.addElementObject(ei);
						ccm.setElementGroup(eg);
						rootType.setContentModel(ccm);
						schemaEngine = new SchemaEngine(rootType);
					}
				}
				processStructure();
				if(lastContext != null)
					setConfigurationContext(lastContext);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			rootComp.setSize(rootComp.computeSize(scrollComp.getClientArea().width - 1, SWT.DEFAULT));
		}
		scrollComp.setLayoutDeferred(false);
//		scrollComp.layout(true, true);
	}
	
	LinkedList<Composite> comps = new LinkedList<Composite>();
	LinkedList<DocumentItemContainer> containers = new LinkedList<DocumentItemContainer>();
	LinkedList<DocumentItem> lastItems = new LinkedList<DocumentItem>();
	LinkedList<Integer> siblingCounters = new LinkedList<Integer>();
	LinkedList<Boolean> hasIf = new LinkedList<Boolean>();
	int depth = 0;
	UISuggestionComposite suggestionComp = null;
	List<SuggestionCommand> suggestions = new LinkedList<SuggestionCommand>();
	LinkedList<UIContentComposite> uiScopes = new LinkedList<UIContentComposite>();
	
	private void postNewContainer(UIContentComposite content, DocumentItemContainer container)
	{
		Composite comp = content;
		if(content instanceof UIContentContainer)
			comp = ((UIContentContainer)content).getContainerComposite();
		if(comp != null)
		{
			comps.offerLast(comp);
			depth++;
			siblingCounters.offerLast(0);
		}
		containers.offerLast(container);
		lastItems.offerLast(null);
		hasIf.offerLast(false);
		uiScopes.offerLast(content);
	}
	
	private void processStructure()
	{
		comps.clear();
		containers.clear();
		lastItems.clear();
		siblingCounters.clear();
		hasIf.clear();
		comps.offerLast(rootComp);
		containers.offerLast(structure);
		lastItems.offerLast(null);
		hasIf.offerLast(false);
		siblingCounters.offerLast(0);
		depth = 0;
		suggestionComp = null;
		suggestions.clear();
		uiScopes.clear();
		uiScopes.offerLast(null);
		CommandListener cl = new CommandListener()
		{
			public void proccess(Command command)
			{
				try
				{
//					System.out.println("got command: " + command);
					if(command instanceof ContainerBoundaryCommand)
					{
						if(!(containers.peekLast() instanceof ConditionalContainerSet))
						{
							comps.removeLast();
							depth--;
							siblingCounters.removeLast();
						}
						containers.removeLast();
						lastItems.removeLast();
						hasIf.removeLast();
						suggestionComp = null;
						suggestions.clear();
						uiScopes.removeLast();
					}
					else if(command instanceof RealizeDocumentItemCommand)
					{
						RealizeDocumentItemCommand itemCommand = (RealizeDocumentItemCommand)command;
						DocumentItem item = itemCommand.getDocumentItem();
						if(item instanceof ForLoopDocumentItem)
						{
							ForLoopDocumentItem forContainer = (ForLoopDocumentItem)item;
							UIForContent forContent = new UIForContent(uiScopes.peekLast(), comps.peekLast(), true);
							forContent.setContent(forContainer);
							forContent.setValidated(itemCommand.isValid());
//							forContent.setDepth(depth);
							Integer order = siblingCounters.pollLast();
//							forContent.setOrder(order);
							siblingCounters.offerLast(++order);
							lastItems.removeLast();
							lastItems.offerLast(item);
							
							postNewContainer(forContent, forContainer);
						}
						else if(item instanceof ConditionalContainerSet)
						{
							lastItems.offerLast(item);
							postNewContainer(null, (DocumentItemContainer)item);
						}
						else if(item instanceof ConditionalDocumentItem)
						{
							ConditionalDocumentItem conditionalContainer = (ConditionalDocumentItem)item;
							UIConditionalContent conditionalContent = new UIConditionalContent(uiScopes.peekLast(), comps.peekLast(), true);
							conditionalContent.setContent(conditionalContainer);
							conditionalContent.setElseIf(hasIf.pollLast());
							conditionalContent.setValidated(itemCommand.isValid());
//							conditionalContent.setDepth(depth);
							Integer order = siblingCounters.pollLast();
//							conditionalContent.setOrder(order);
							siblingCounters.offerLast(++order);
							hasIf.offerLast(true);
							
							postNewContainer(conditionalContent, conditionalContainer);
						}
						else if(item instanceof ElseDocumentItem)
						{
							ElseDocumentItem elseContainer = (ElseDocumentItem)item;
							UIElseContent elseContent = new UIElseContent(uiScopes.peekLast(), comps.peekLast(), true);
							elseContent.setContent(elseContainer);
							elseContent.setValidated(itemCommand.isValid());
//							elseContent.setDepth(depth);
							Integer order = siblingCounters.pollLast();
//							elseContent.setOrder(order);
							siblingCounters.offerLast(++order);
							postNewContainer(elseContent, elseContainer);
						}
						else if(item instanceof TextDocumentItem)
						{
							Composite comp = comps.peekLast();
							if(comp instanceof UIElementContent && ((UIElementContent)comp).isTextOnly())
							{
								UIElementContent edi = (UIElementContent)comp;
								edi.setText((TextDocumentItem)item);
							}
							else
							{
								UITextContent textContent = new UITextContent(uiScopes.peekLast(), comps.peekLast(), true);
								textContent.setContent((TextDocumentItem)item);
								textContent.setValidated(itemCommand.isValid());
//								textContent.setDepth(depth);
								Integer order = siblingCounters.pollLast();
//								textContent.setOrder(order);
								siblingCounters.offerLast(++order);
								lastItems.removeLast();
								lastItems.offerLast(item);
							}
						}
						else if(item instanceof ElementDocumentItem)
						{
							ElementDocumentItem elementContainer = (ElementDocumentItem)item;
							UIElementContent elementContent = new UIElementContent(uiScopes.peekLast(), comps.peekLast(), true, ((RealizeElementItemCommand)command).isTextOnly());
							elementContent.setContent(elementContainer);
							elementContent.setValidated(itemCommand.isValid());
//							elementContent.setDepth(depth);
							Integer order = siblingCounters.pollLast();
//							elementContent.setOrder(order);
							siblingCounters.offerLast(++order);
							lastItems.removeLast();
							lastItems.offerLast(item);
							postNewContainer(elementContent, elementContainer);
						}
						suggestions.clear();
						suggestionComp = null;
					}
					else if(command instanceof SuggestionCommand)
					{
						boolean matched = false;
						Composite currentComp = comps.peekLast();
						DocumentItemContainer currentContainer = containers.peekLast();
						DocumentItem lastItem = lastItems.peekLast();
//						System.out.println("Last Item: " + lastItem);
//						if(lastItem instanceof ElementDocumentItem)
//						{
//							System.out.println(((ElementDocumentItem)lastItem).getName());
//						}
						for(SuggestionCommand sc : suggestions)
						{
							if(sc.getClass().equals(command.getClass()))
							{
								if(!(command instanceof ElementSuggestionCommand))
								{
									matched = true;
									break;
								}
								else
								{
									ElementSuggestionCommand esc = (ElementSuggestionCommand)command;
									ElementSuggestionCommand esc2 = (ElementSuggestionCommand)sc;
									if(esc2.getElement().getName() == esc.getElement().getName())
									{
										matched = true;
										break;
									}
								}
							}
						}
						if(!matched)
						{
							SuggestionCommand sc = (SuggestionCommand)command;
							suggestions.add(sc);
							if(suggestionComp == null)
							{
								suggestionComp = new UISuggestionComposite(currentComp, SWT.NONE);
							}
//							System.err.println(suggestionComp + "@" + suggestionComp.hashCode());
							if(command instanceof ForLoopSuggestionCommand)
							{
								ForLoopSuggestion fls = new ForLoopSuggestion(currentContainer, sc.isRequired());
								fls.setInsertionPoint(lastItem);
								suggestionComp.addSuggestion(fls);
							}
							else if(command instanceof CCSSuggestionCommand)
							{
								IfSuggestion ifSuggestion = new IfSuggestion(currentContainer, sc.isRequired());
								ifSuggestion.setInsertionPoint(lastItem);
								suggestionComp.addSuggestion(ifSuggestion);
							}
							else if(command instanceof ElseIfSuggestionCommand)
							{
								ElseIfSuggestion elseIfSuggestion = new ElseIfSuggestion(currentContainer, sc.isRequired());
								elseIfSuggestion.setInsertionPoint(lastItem);
								suggestionComp.addSuggestion(elseIfSuggestion);
							}
							else if(command instanceof ElseSuggestionCommand)
							{
								ElseSuggestion elseSuggestion = new ElseSuggestion(currentContainer, sc.isRequired());
								elseSuggestion.setInsertionPoint(lastItem);
								suggestionComp.addSuggestion(elseSuggestion);
							}
							else if(command instanceof TextSuggestionCommand)
							{
								TextSuggestion textSuggestion = new TextSuggestion(currentContainer, sc.isRequired());
								textSuggestion.setInsertionPoint(lastItem);
								suggestionComp.addSuggestion(textSuggestion);
							}
							else if(command instanceof ElementSuggestionCommand)
							{
								ElementSuggestion elementSuggestion = new ElementSuggestion(((ElementSuggestionCommand)command).getElement(), currentContainer, sc.isRequired());
								elementSuggestion.setInsertionPoint(lastItem);
								suggestionComp.addSuggestion(elementSuggestion);
							}
						}
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}	
		};
		
		schemaEngine.addCommandListener(cl);
		
		schemaEngine.processDocumentStructure(structure);
		
		schemaEngine.removeCommandListener(cl);
	}
	
	@Override
	public void save()
	{
		for(BrandedUIItem brandedItem : valueStacks)
		{
			brandedItem.getValueStack().save();
		}
		getElement().commitConfigurationChanges(manager);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.editors.core.configuration.ComponentPropertiesPanel#cancel()
	 */
	@Override
	public void cancel()
	{
		getElement().rollbackConfigurationChanges(manager);
	}
	
	private Map<String, Object> lastContext = null;

	@Override
	public void setConfigurationContext(Map<String, Object> values)
	{
		lastContext = values;
		IBrand brand = (IBrand)values.get(BrandContext.CONTEXT_ID);
		for(BrandedUIItem brandedItem : valueStacks)
		{
			BrandBinding brandBinding = brandedItem.getBinding().getBrandBinding(brand);
			brandedItem.getValueStack().setSetting(brandBinding);
		}
	}

	public void resolve()
	{
		List<ComponentPropertiesPanel> panels = getContainer().getPanels();
		for(ComponentPropertiesPanel panel : panels)
		{
			if(panel instanceof SoapServiceSelectionPropertiesPanel)
			{
				((SoapServiceSelectionPropertiesPanel)panel).addOperationListener(this);
				break;
			}
		}
	}

	public void operationChanged(SoapBindingOperation operation)
	{
		if(operation != currentOperation)
		{
			currentOperation = operation;
			if(scrollComp != null)
			{
				valueStacks.clear();
				buildDocumentDisplay();
				variableScopes.clear();
			}
		}
	}
	
	public interface BrandedUIItem
	{
		public BrandedBinding getBinding();
		public ValueStack getValueStack();
	}

	public class UIContentComposite extends Composite
	{
		public static final int SINGLE_DELETE = 1;
		public static final int COMPLEX_DELETE = 2;
		public static final int MOVE_UP = 4;
		public static final int MOVE_DOWN = 8;
		public static final int MOVE_OUT = 16;
		public static final int MOVE_IN = 32;
		
		@SuppressWarnings("unused")
		private int depth = 0;
		@SuppressWarnings("unused")
		private int order = 0;
		private int buttonFlags = 0;
		private boolean validated = true;
		private boolean composing = true;
		private Color borderColor = null;
		protected Color accentColor = null;
		@SuppressWarnings("unused")
		private Image icon = null;
		@SuppressWarnings("unused")
		private String title = null;
		
		private Composite titleComp = null;
		private Label iconLabel = null;
		private Label titleLabel = null;
		private CoolBar coolBar = null;
		private ToolBar buttonBar = null;
		private ToolItem deleteItem = null;
		private ToolItem moveDownItem = null;
		private ToolItem moveUpItem = null;
		private ToolItem moveInItem = null;
		private ToolItem moveOutItem = null;
		private UIContentComposite scoping = null;
		
		public UIContentComposite(UIContentComposite scoping, Composite parent, int style, boolean composing)
		{
			super(parent, SWT.NONE);
			this.scoping = scoping;
			this.addPaintListener(new PaintListener()
			{
				public void paintControl(PaintEvent e)
				{
					GC g = e.gc;
					Color oldForground = g.getForeground();
					g.setForeground(borderColor);
					g.drawLine(0, 0, UIContentComposite.this.getSize().x - 10, 0);
					g.drawLine(0, 0, 0, UIContentComposite.this.getSize().y - 10);
					g.setForeground(oldForground);
				}
			});
			buttonFlags = style & 3;
			this.composing = composing;
			this.setBackgroundMode(SWT.INHERIT_DEFAULT);
			borderColor = new Color(getDisplay(), 231, 233, 238);
			accentColor = new Color(getDisplay(), 246, 249, 254);
			setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			GridLayout layout = new GridLayout(1, false);
			layout.marginWidth = 0;
			layout.marginLeft = 0;
			layout.verticalSpacing = 0;
			setLayout(layout);
			titleComp = new Composite(this, SWT.NONE);
			layout = new GridLayout(3, false);
			layout.marginWidth = 0;
			layout.marginHeight = 0;
			titleComp.setLayout(layout);
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalIndent = 7;
			titleComp.setLayoutData(gd);
			iconLabel = new Label(titleComp, SWT.NONE);
			iconLabel.setText("");
			gd = new GridData();
//			gd.exclude = true;
			iconLabel.setLayoutData(gd);
			titleLabel = new Label(titleComp, SWT.NONE);
			titleLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			coolBar = new CoolBar(titleComp, SWT.HORIZONTAL | SWT.FLAT);
			coolBar.setLocked(true);
			buttonBar = new ToolBar(coolBar, SWT.FLAT);
			CoolItem buttonBarItem = new CoolItem(coolBar, SWT.NONE);
			buttonBarItem.setControl(buttonBar);
			if((style & MOVE_OUT) > 0)
			{
				moveOutItem = new ToolItem(buttonBar, SWT.PUSH);
//				moveOutItem.setText("mo");
				moveOutItem.addSelectionListener(new SelectionListener()
				{
					public void widgetDefaultSelected(SelectionEvent e)
					{
					}
	
					public void widgetSelected(SelectionEvent e)
					{
						moveOut();
					}
				});
				moveOutItem.setEnabled(false);
			}
			if((style & MOVE_IN) > 0)
			{
				moveInItem = new ToolItem(buttonBar, SWT.PUSH);
//				moveInItem.setText("mi");
				moveInItem.addSelectionListener(new SelectionListener()
				{
					public void widgetDefaultSelected(SelectionEvent e)
					{
					}
	
					public void widgetSelected(SelectionEvent e)
					{
						moveIn();
					}
				});
				moveInItem.setEnabled(false);
			}
			if((style & MOVE_UP) > 0)
			{
				moveUpItem = new ToolItem(buttonBar, SWT.PUSH);
//				moveUpItem.setText("mu");
				moveUpItem.setImage(Activator.getDefault().getImageRegistry().get("MOVE_UP"));
				moveUpItem.setDisabledImage(Activator.getDefault().getImageRegistry().get("MOVE_UP_DISABLED"));
				moveUpItem.addSelectionListener(new SelectionListener()
				{
					public void widgetDefaultSelected(SelectionEvent e)
					{
					}
	
					public void widgetSelected(SelectionEvent e)
					{
						moveUp();
					}
				});
				moveUpItem.setEnabled(false);
			}
			if((style & MOVE_DOWN) > 0)
			{
				moveDownItem = new ToolItem(buttonBar, SWT.PUSH);
//				moveDownItem.setText("md");
				moveDownItem.setImage(Activator.getDefault().getImageRegistry().get("MOVE_DOWN"));
				moveDownItem.setDisabledImage(Activator.getDefault().getImageRegistry().get("MOVE_DOWN_DISABLED"));
				moveDownItem.addSelectionListener(new SelectionListener()
				{
					public void widgetDefaultSelected(SelectionEvent e)
					{
					}
	
					public void widgetSelected(SelectionEvent e)
					{
						moveDown();
					}
				});
				moveDownItem.setEnabled(false);
			}
			if((style & SINGLE_DELETE) > 0)
				deleteItem = new ToolItem (buttonBar, SWT.PUSH);
			else
			{
				deleteItem = new ToolItem (buttonBar, SWT.DROP_DOWN);
				deleteItem.addListener (SWT.Selection, new Listener () {
					public void handleEvent (Event event) {
						if (event.detail == SWT.ARROW) {
							final Menu menu = new Menu (buttonBar.getShell(), SWT.POP_UP);
							populateDeleteMenu(menu);
							Rectangle rect = deleteItem.getBounds ();
							Point pt = new Point (rect.x, rect.y + rect.height);
							pt = buttonBar.toDisplay (pt);
							menu.setLocation (pt.x, pt.y);
							menu.setVisible (true);
							menu.addMenuListener(new MenuListener()
							{
								public void menuHidden(MenuEvent e)
								{
									menu.getDisplay().asyncExec(new Runnable() { public void run() {menu.dispose();}});
								}

								public void menuShown(MenuEvent e)
								{
								}
							});
						}
					}
				});
			}
			deleteItem.addSelectionListener(new SelectionListener()
			{
				public void widgetDefaultSelected(SelectionEvent e)
				{
				}

				public void widgetSelected(SelectionEvent e)
				{
					if (e.detail != SWT.ARROW)
					{
						delete();
					}
				}
			});
			deleteItem.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_TOOL_DELETE));
			buttonBar.pack();
			buttonBarItem.setSize(buttonBar.computeSize(SWT.DEFAULT, SWT.DEFAULT));
			coolBar.pack();
			gd = new GridData();
			Point size = coolBar.computeSize(SWT.DEFAULT, SWT.DEFAULT);
			gd.widthHint = size.x + 20;
			gd.heightHint = size.y;
			coolBar.setLayoutData(gd);
		}
		
		public List<Variable> getVariableScope()
		{
			if(scoping != null)
			{
				return scoping.getVariableScope();
			}
			return variables;
		}
		
		protected void postScopeChange()
		{
			for(Control child : this.getChildren())
			{
				if(child instanceof UIContentComposite)
				{
					((UIContentComposite)child).scopeChanged();
				}
			}
		}
		
		protected void scopeChanged()
		{
			postScopeChange();
		}
		
		protected int getButtonFlags()
		{
			return buttonFlags;
		}
		
		protected void updateButtons(int buttonFlags)
		{
			this.buttonFlags = buttonFlags;
			boolean moveOut = (buttonFlags & MOVE_OUT) > 0;
			boolean moveIn = (buttonFlags & MOVE_IN) > 0;
			boolean moveUp = (buttonFlags & MOVE_UP) > 0;
			boolean moveDown = (buttonFlags & MOVE_DOWN) > 0;
			if(moveOutItem != null) moveOutItem.setEnabled(moveOut);
			if(moveInItem != null) moveInItem.setEnabled(moveIn);
			if(moveUpItem != null) moveUpItem.setEnabled(moveUp);
			if(moveDownItem != null) moveDownItem.setEnabled(moveDown);
		}
		
		public void dispose()
		{
			borderColor.dispose();
			accentColor.dispose();
			super.dispose();
		}
		
		public boolean isValidated()
		{
			return validated;
		}
		
		public void setValidated(boolean validated)
		{
			this.validated = validated;
			if(!validated)
				titleLabel.setForeground(this.getDisplay().getSystemColor(SWT.COLOR_RED));
		}
		
		public boolean isComposing()
		{
			return composing;
		}
		
		public void updateContext(IBrand brand)
		{
			for(Control c : this.getChildren())
			{
				if(c instanceof UIContentComposite)
				{
					((UIContentComposite)c).updateContext(brand);
				}
			}
		}
		
		public void setTitle(String title)
		{
			this.titleLabel.setText(title);
		}
		
		public void setIcon(Image icon)
		{
			this.iconLabel.setImage(icon);
			((GridData)this.iconLabel.getLayoutData()).exclude = (icon == null);
			titleComp.layout(true, true);
		}
		
		protected void delete()
		{
		}
		
		protected void populateDeleteMenu(Menu menu)
		{
		}
		
		protected void moveUp()
		{
		}
		
		protected void moveDown()
		{
		}
		
		protected void moveOut()
		{
		}
		
		protected void moveIn()
		{
		}
	}
	
	public abstract class UIContentContainer extends UIContentComposite
	{
		public UIContentContainer(UIContentComposite scoping, Composite parent, int style, boolean composing)
		{
			super(scoping, parent, style, composing);
		}
		
		protected void postScopeChange()
		{
			for(Control child : getContainerComposite().getChildren())
			{
				if(child instanceof UIContentComposite)
				{
					((UIContentComposite)child).scopeChanged();
				}
			}
		}
		
		public abstract Composite getContainerComposite();
	}

	public class UITextContent extends UIContentComposite implements BrandedUIItem
	{
		private TextDocumentItem textItem = null;
		private ValueStack valueStack = null;
		private Text contentArea = null;
		
		public UITextContent(UIContentComposite scoping, final Composite parent, boolean composing)
		{
			super(scoping, parent, SINGLE_DELETE, composing);
			setTitle("Text Content");
			Composite bodyComp = UIHelper.createWrapperComposite(this, 0);
			bodyComp.setBackground(null);
			bodyComp.setBackgroundMode(SWT.INHERIT_DEFAULT);
			bodyComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			valueStack = new ValueStack("", "", getVariableScope());
			valueStack.createControls(bodyComp);
			contentArea = new Text(valueStack.getValueComposite(), SWT.BORDER | SWT.H_SCROLL);
			valueStack.setValueControl(new ValueControl()
			{
				public String getValue()
	            {
					return contentArea.getText();
	            }

				public void setValue(String value)
	            {
					contentArea.setText(value == null ? "" : value);
	            }
			});
			contentArea.addModifyListener(new ModifyListener()
			{
				public void modifyText(ModifyEvent e)
				{
					Point size = contentArea.computeSize(contentArea.getSize().x, SWT.DEFAULT, true);
					GridData gd = (GridData)contentArea.getLayoutData();
					gd.minimumHeight = Math.max(18, size.y);
					rootComp.setSize(rootComp.computeSize(scrollComp.getClientArea().width - 1, SWT.DEFAULT));
					scrollComp.layout(true, true);
				}
			});
			contentArea.addListener(SWT.MouseWheel, new Listener()
			{
				public void handleEvent(Event event)
				{
					Point origin = scrollComp.getOrigin();
					origin.y -= event.count;
					event.doit = false;
					scrollComp.setOrigin(origin);
				}
			});
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.minimumHeight = 18;
			gd.widthHint = 300;
			contentArea.setLayoutData(gd);
			valueStacks.add(this);
		}
		
		public void setContent(TextDocumentItem textItem)
		{
			this.textItem = textItem;
		}

		public BrandedBinding getBinding()
		{
			return textItem;
		}

		public ValueStack getValueStack()
		{
			return valueStack;
		}
	}
	
	public class UIConditionalContent extends UIContentContainer
	{
		private ConditionalDocumentItem ifItem = null;
		private Text conditionText = null;
		private boolean elseif = false;
		private Composite propertiesComp = null;
		private Composite container = null;
		private Twistie propertiesExpander = null;
		private Twistie contentsExpander = null;
		
		public UIConditionalContent(UIContentComposite scoping, Composite parent, boolean composing)
		{
			super(scoping, parent, COMPLEX_DELETE | MOVE_UP | MOVE_DOWN, composing);
			setIcon(Activator.getDefault().getImageRegistry().get("SCRIPT"));
			final Composite bodyComp = new Composite(this, SWT.NONE);
			GridLayout layout = new GridLayout(3, false);
			layout.marginWidth = 0;
			layout.marginLeft = 5;
			bodyComp.setLayout(layout);
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalIndent = 15;
			bodyComp.setLayoutData(gd);
			
			propertiesExpander = new Twistie(bodyComp, SWT.NO_FOCUS);
			propertiesExpander.setExpanded(true);
			Label propertiesIconLabel = new Label(bodyComp, SWT.NONE);
			propertiesIconLabel.setImage(Activator.getDefault().getImageRegistry().get("ATTRIBUTE_GROUP"));
			propertiesIconLabel.setLayoutData(new GridData());
			Label propertiesLabel = new Label(bodyComp, SWT.NONE);
			propertiesLabel.setText("Properties");
			propertiesLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			propertiesComp = new Composite(bodyComp, SWT.NONE);
			propertiesExpander.addHyperlinkListener(new IHyperlinkListener()
			{
				public void linkExited(HyperlinkEvent e)
				{
				}
				
				public void linkEntered(HyperlinkEvent e)
				{
				}
				
				public void linkActivated(HyperlinkEvent e)
				{
					GridData gd = (GridData)propertiesComp.getLayoutData();
					gd.exclude = !propertiesExpander.isExpanded();
					propertiesComp.setVisible(propertiesExpander.isExpanded());
					rootComp.layout(true, true);
					Map<String, Boolean> states = expansionStates.get(ifItem);
					if(states == null)
					{
						states = new HashMap<String, Boolean>();
						expansionStates.put(ifItem, states);
					}
					states.put("properties", propertiesExpander.isExpanded());
				}
			});
			propertiesComp.setLayout(new GridLayout(2, false));
			GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
			layoutData.horizontalSpan = 3;
			layoutData.horizontalIndent = 17;
			propertiesComp.setLayoutData(layoutData);
			Label conditionLabel = new Label(propertiesComp, SWT.NONE);
			conditionLabel.setText("Condition");
			conditionLabel.setLayoutData(new GridData());
			conditionText = new Text(propertiesComp, SWT.BORDER | SWT.SINGLE);
			conditionText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			conditionText.addModifyListener(new ModifyListener()
			{
				public void modifyText(ModifyEvent e)
				{
					ifItem.setCondition(conditionText.getText());
				}
			});
			
			contentsExpander = new Twistie(bodyComp, SWT.NO_FOCUS);
			contentsExpander.setExpanded(true);
			Label contentsIconLabel = new Label(bodyComp, SWT.NONE);
			contentsIconLabel.setImage(Activator.getDefault().getImageRegistry().get("COMPLEX_TYPE"));
			contentsIconLabel.setLayoutData(new GridData());
			Label contentsLabel = new Label(bodyComp, SWT.NONE);
			contentsLabel.setText("Contents");
			contentsLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			
			container = new Composite(bodyComp, SWT.NONE);
			contentsExpander.addHyperlinkListener(new IHyperlinkListener()
			{
				public void linkExited(HyperlinkEvent e)
				{
				}
				
				public void linkEntered(HyperlinkEvent e)
				{
				}
				
				public void linkActivated(HyperlinkEvent e)
				{
					GridData gd = (GridData)container.getLayoutData();
					gd.exclude = !contentsExpander.isExpanded();
					container.setVisible(contentsExpander.isExpanded());
					Map<String, Boolean> states = expansionStates.get(ifItem);
					if(states == null)
					{
						states = new HashMap<String, Boolean>();
						expansionStates.put(ifItem, states);
					}
					states.put("contents", contentsExpander.isExpanded());
					rootComp.layout(true, true);
				}
			});
			layout = new GridLayout(1, false);
			layout.marginWidth = 0;
			layout.marginLeft = 5;
			container.setLayout(layout);
			gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan = 3;
			gd.horizontalIndent = 17;
			container.setLayoutData(gd);
		}
		
		public Composite getContainerComposite()
		{
			return container;
		}
		
		public void setElseIf(boolean elseif)
		{
			this.elseif = elseif;
			int flags = getButtonFlags();
			List<ConditionalDocumentItem> elseifs = ((ConditionalContainerSet)ifItem.getParent()).getElseIfs();
			if(elseif)
			{
				setTitle("ELSE IF");
				flags |= MOVE_UP;
				for(int i = 0; i < elseifs.size() - 1; i++)
				{
					if(elseifs.get(i).equals(ifItem))
					{
						flags |= MOVE_DOWN;
						break;
					}
				}
			}
			else
			{
				setTitle("IF");
				if(elseifs.size() > 0)
					flags |= MOVE_DOWN;
			}
			updateButtons(flags);
		}
		
		public void setContent(ConditionalDocumentItem ifItem)
		{
			this.ifItem = ifItem;
			Map<String, Boolean> states = expansionStates.get(ifItem);
			if(states != null)
			{
				Boolean state = states.get("properties");
				if(state == null)
					state = true;
				propertiesExpander.setExpanded(state);
				GridData gd = (GridData)propertiesComp.getLayoutData();
				gd.exclude = !propertiesExpander.isExpanded();
				propertiesComp.setVisible(propertiesExpander.isExpanded());
				state = states.get("contents");
				if(state == null)
					state = true;
				contentsExpander.setExpanded(state);
				gd = (GridData)container.getLayoutData();
				gd.exclude = !contentsExpander.isExpanded();
				container.setVisible(contentsExpander.isExpanded());
				rootComp.layout(true, true);
			}
			conditionText.setText(ifItem.getCondition() == null ? "" : ifItem.getCondition());
		}

		@Override
		protected void delete()
		{
			ConditionalContainerSet parent = (ConditionalContainerSet)ifItem.getParent();
			if(elseif)
			{
				parent.removeItem(ifItem);
				expansionStates.remove(ifItem);
			}
			else
			{
				List<ConditionalDocumentItem> elseIfs = parent.getElseIfs();
				if(elseIfs.size() > 0)
				{
					ConditionalDocumentItem firstElseIf = elseIfs.get(0);
					parent.removeItem(firstElseIf);
					expansionStates.remove(ifItem);
					parent.setIf(firstElseIf);
				}
				else
				{
					expansionStates.remove(parent.getIf());
					for(ConditionalDocumentItem elseIf : parent.getElseIfs())
					{
						expansionStates.remove(elseIf);
					}
					if(parent.getElse() != null)
					{
						expansionStates.remove(parent.getElse());
					}
					parent.getParent().removeItem(parent);
				}
			}
			markOrigin();
			buildDocumentDisplay();
			restoreOrigin();
		}
		
		protected void moveUp()
		{
			ConditionalContainerSet parent = (ConditionalContainerSet)ifItem.getParent();
			List<ConditionalDocumentItem> elseIfs = parent.getElseIfs();
			int i = 0;
			for(;i < elseIfs.size(); i++)
			{
				if(elseIfs.get(i).equals(ifItem))
				{
					break;
				}
			}
			if(i == 0)
			{
				ConditionalDocumentItem oi = parent.getIf();
				parent.removeItem(ifItem);
				parent.setIf(ifItem);
				parent.insertItem(oi, 0);
			}
			else
			{
				parent.removeItem(ifItem);
				parent.insertItem(ifItem, i - 1);
			}
			markOrigin();
			buildDocumentDisplay();
			restoreOrigin();
		}
		
		protected void moveDown()
		{
			ConditionalContainerSet parent = (ConditionalContainerSet)ifItem.getParent();
			if(!elseif)
			{
				ConditionalDocumentItem oi = parent.getElseIfs().get(0);
				parent.removeItem(oi);
				parent.setIf(oi);
				parent.insertItem(ifItem, 0);
			}
			else
			{
				List<ConditionalDocumentItem> elseIfs = parent.getElseIfs();
				int i = 0;
				for(; i < elseIfs.size() - 1; i++)
				{
					if(elseIfs.get(i).equals(ifItem))
					{
						break;
					}
				}
				parent.removeItem(ifItem);
				parent.insertItem(ifItem, i + 1);
			}
			markOrigin();
			buildDocumentDisplay();
			restoreOrigin();
		}

		@Override
		protected void populateDeleteMenu(Menu menu)
		{
			final ConditionalContainerSet parent = (ConditionalContainerSet)ifItem.getParent();
			if(parent != null)
			{
				if(!elseif)
				{
					MenuItem removeEntireSet = new MenuItem(menu, SWT.NONE);
					removeEntireSet.setText("Remove Entire Conditional Set");
					removeEntireSet.addSelectionListener(new SelectionListener()
					{
						public void widgetDefaultSelected(SelectionEvent e)
						{
						}
	
						public void widgetSelected(SelectionEvent e)
						{
							parent.getParent().removeItem(parent);
							markOrigin();
							buildDocumentDisplay();
							restoreOrigin();
						}
					}); 
				}
				if(parent.getElseIfs().size() > 0)
				{
					MenuItem removeConditional = new MenuItem(menu, SWT.NONE);
					removeConditional.setText("Remove This Condition");
					removeConditional.addSelectionListener(new SelectionListener()
					{
						public void widgetDefaultSelected(SelectionEvent e)
						{
						}
	
						public void widgetSelected(SelectionEvent e)
						{
							delete();
						}
					}); 
				}
			}
		}
		
		
	}

	public class UIElseContent extends UIContentContainer
	{
		private ElseDocumentItem elseItem = null;
		private Composite container = null;
		private Twistie contentsExpander = null;
		
		public UIElseContent(UIContentComposite scoping, Composite parent, boolean composing)
		{
			super(scoping, parent, SINGLE_DELETE, composing);
			setTitle("Else");
		
			Composite bodyComp = new Composite(this, SWT.NONE);
			GridLayout layout = new GridLayout(3, false);
			layout.marginWidth = 0;
			layout.marginLeft = 5;
			bodyComp.setLayout(layout);
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalIndent = 15;
			bodyComp.setLayoutData(gd);

			contentsExpander = new Twistie(bodyComp, SWT.NO_FOCUS);
			contentsExpander.setExpanded(true);
			Label contentsIconLabel = new Label(bodyComp, SWT.NONE);
			contentsIconLabel.setImage(Activator.getDefault().getImageRegistry().get("COMPLEX_TYPE"));
			contentsIconLabel.setLayoutData(new GridData());
			Label contentsLabel = new Label(bodyComp, SWT.NONE);
			contentsLabel.setText("Contents");
			contentsLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			
			container = new Composite(bodyComp, SWT.NONE);
			contentsExpander.addHyperlinkListener(new IHyperlinkListener()
			{
				public void linkExited(HyperlinkEvent e)
				{
				}
				
				public void linkEntered(HyperlinkEvent e)
				{
				}
				
				public void linkActivated(HyperlinkEvent e)
				{
					GridData gd = (GridData)container.getLayoutData();
					gd.exclude = !contentsExpander.isExpanded();
					container.setVisible(contentsExpander.isExpanded());
					Map<String, Boolean> states = expansionStates.get(elseItem);
					if(states == null)
					{
						states = new HashMap<String, Boolean>();
						expansionStates.put(elseItem, states);
					}
					states.put("contents", contentsExpander.isExpanded());
					rootComp.layout(true, true);
				}
			});
			container.setLayout(new GridLayout(1, false));
			gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan = 3;
			gd.horizontalIndent = 17;
			container.setLayoutData(gd);
		}
		
		public Composite getContainerComposite()
		{
			return container;
		}
		
		public void setContent(ElseDocumentItem elseItem)
		{
			this.elseItem = elseItem;
			Map<String, Boolean> states = expansionStates.get(elseItem);
			if(states != null)
			{
				Boolean state = states.get("contents");
				if(state == null)
					state = true;
				contentsExpander.setExpanded(state);
				GridData gd = (GridData)container.getLayoutData();
				gd.exclude = !contentsExpander.isExpanded();
				container.setVisible(contentsExpander.isExpanded());
				rootComp.layout(true, true);
			}
		}
		
		protected void delete()
		{
			((ConditionalContainerSet)elseItem.getParent()).setElse(null);
			expansionStates.remove(elseItem);
			markOrigin();
			buildDocumentDisplay();
			restoreOrigin();
		}
	}

	public class UIForContent extends UIContentContainer
	{
		private ForLoopDocumentItem forItem = null;
		private Text arrayNameText = null;
		private Button browseButton = null;
		private Text varNameText = null;
		@SuppressWarnings("unused")
		private List<Variable> parentScope = null;
		private ObjectDefinition arraySelection = null;
		private Variable cursorVariable = null;
		private boolean updating = false;
		private Twistie propertiesExpander = null;
		private Composite propertiesComp = null;
		private Twistie contentsExpander = null;
		private Composite container = null;
		
		public UIForContent(UIContentComposite scoping, Composite parent, boolean composing)
		{
			super(scoping, parent, SINGLE_DELETE, composing);
			cursorVariable = new Variable("forLoopCursor", FieldType.STRING);
			VariableHelper.buildObjectFields(cursorVariable, getElement().getDesign().getDocument().getProject().getBusinessObjectSet());
			parentScope = super.getVariableScope();
			setTitle("For Each");

			Composite bodyComp = new Composite(this, SWT.NONE);
			GridLayout layout = new GridLayout(3, false);
			layout.marginWidth = 0;
			layout.marginLeft = 5;
			bodyComp.setLayout(layout);
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalIndent = 15;
			bodyComp.setLayoutData(gd);

			propertiesExpander = new Twistie(bodyComp, SWT.NO_FOCUS);
			propertiesExpander.setExpanded(true);
			Label propertiesIconLabel = new Label(bodyComp, SWT.NONE);
			propertiesIconLabel.setImage(Activator.getDefault().getImageRegistry().get("ATTRIBUTE_GROUP"));
			propertiesIconLabel.setLayoutData(new GridData());
			Label propertiesLabel = new Label(bodyComp, SWT.NONE);
			propertiesLabel.setText("Properties");
			propertiesLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			propertiesComp = new Composite(bodyComp, SWT.NONE);
			propertiesExpander.addHyperlinkListener(new IHyperlinkListener()
			{
				public void linkExited(HyperlinkEvent e)
				{
				}
				
				public void linkEntered(HyperlinkEvent e)
				{
				}
				
				public void linkActivated(HyperlinkEvent e)
				{
					GridData gd = (GridData)propertiesComp.getLayoutData();
					gd.exclude = !propertiesExpander.isExpanded();
					propertiesComp.setVisible(propertiesExpander.isExpanded());
					rootComp.layout(true, true);
					Map<String, Boolean> states = expansionStates.get(forItem);
					if(states == null)
					{
						states = new HashMap<String, Boolean>();
						expansionStates.put(forItem, states);
					}
					states.put("properties", propertiesExpander.isExpanded());
				}
			});
			propertiesComp.setLayout(new GridLayout(2, false));
			GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
			layoutData.horizontalSpan = 3;
			layoutData.horizontalIndent = 17;
			propertiesComp.setLayoutData(layoutData);

			Label sourceNameLabel = new Label(propertiesComp, SWT.NONE);
			sourceNameLabel.setText("Object In");
			sourceNameLabel.setLayoutData(new GridData());
			Composite browseComp = new Composite(propertiesComp, SWT.NONE);
			browseComp.setLayout(new GridLayout(2, false));
			browseComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			arrayNameText = new Text(browseComp, SWT.SINGLE | SWT.BORDER);
			arrayNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			arrayNameText.addModifyListener(new ModifyListener()
			{
				public void modifyText(ModifyEvent e)
				{
					if(!updating) //avoid cascading updates
					{
						arraySelection = null;
						String[] parts = arrayNameText.getText().split("\\.");
						List<ObjectDefinition> defs = new LinkedList<ObjectDefinition>(getVariableScope());
outer:					for(int i = 0; i < parts.length; i++)
						{
							for(ObjectDefinition od : defs)
							{
								if(od.getName().equals(parts[i]))
								{
									if(i == parts.length - 1)
									{
										arrayNameText.setBackground(null);
										forItem.setForEach(cursorVariable.getName(), od.getPath());
										arraySelection = od;
										break outer;
									}
								}
							}
						}
						if(arraySelection == null)
						{
							arrayNameText.setBackground(getDisplay().getSystemColor(SWT.COLOR_RED));
							forItem.setForEach(cursorVariable.getName(), null);
						}
						updateCursor();
					}
				}
			});
			browseButton = new Button(browseComp, SWT.PUSH);
			browseButton.setText("pick");
			browseButton.setLayoutData(new GridData());
			browseButton.addSelectionListener(new SelectionListener()
			{
				public void widgetDefaultSelected(SelectionEvent e)
				{
				}

				public void widgetSelected(SelectionEvent e)
				{
					VariableBrowserDialog browserDialog = new VariableBrowserDialog(getDisplay().getActiveShell(), UIForContent.super.getVariableScope());
					browserDialog.setFilter(new ObjectDefinitionFilter()
					{
						public boolean isApplicable(ObjectDefinition definition)
						{
							return !definition.getType().isObject() && definition.getType().getPrimitiveType() == Primitive.ARRAY;
						}
					});
					if(browserDialog.open() == Dialog.OK)
					{
						updating = true;
						arraySelection = browserDialog.getSelectedDefinition();
						if(arraySelection != null)
						{
							arrayNameText.setText(arraySelection.getPath());
							arrayNameText.setBackground(null);
							forItem.setForEach(cursorVariable.getName(), arraySelection.getPath());
						}
						else
						{
							arrayNameText.setText("");
							arrayNameText.setBackground(getDisplay().getSystemColor(SWT.COLOR_RED));
							forItem.setForEach(cursorVariable.getName(), null);
						}
						updateCursor();
						updating = false;
					}
				}
			});
			
			Label varNameLabel = new Label(propertiesComp, SWT.NONE);
			varNameLabel.setText("Called");
			varNameLabel.setLayoutData(new GridData());
			varNameText = new Text(propertiesComp, SWT.BORDER | SWT.SINGLE);
			varNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			varNameText.addModifyListener(new ModifyListener()
			{
				public void modifyText(ModifyEvent e)
				{
					forItem.setForEach(varNameText.getText(), arraySelection == null ? null : arraySelection.getPath());
					updateCursor();
				}
			});

			contentsExpander = new Twistie(bodyComp, SWT.NO_FOCUS);
			contentsExpander.setExpanded(true);
			Label contentsIconLabel = new Label(bodyComp, SWT.NONE);
			contentsIconLabel.setImage(Activator.getDefault().getImageRegistry().get("COMPLEX_TYPE"));
			contentsIconLabel.setLayoutData(new GridData());
			Label contentsLabel = new Label(bodyComp, SWT.NONE);
			contentsLabel.setText("Contents");
			contentsLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			
			container = new Composite(bodyComp, SWT.NONE);
			contentsExpander.addHyperlinkListener(new IHyperlinkListener()
			{
				public void linkExited(HyperlinkEvent e)
				{
				}
				
				public void linkEntered(HyperlinkEvent e)
				{
				}
				
				public void linkActivated(HyperlinkEvent e)
				{
					GridData gd = (GridData)container.getLayoutData();
					gd.exclude = !contentsExpander.isExpanded();
					container.setVisible(contentsExpander.isExpanded());
					Map<String, Boolean> states = expansionStates.get(forItem);
					if(states == null)
					{
						states = new HashMap<String, Boolean>();
						expansionStates.put(forItem, states);
					}
					states.put("contents", contentsExpander.isExpanded());
					rootComp.layout(true, true);
				}
			});
			layout = new GridLayout(1, false);
			layout.marginWidth = 0;
			layout.marginLeft = 5;
			container.setLayout(layout);
			gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan = 3;
			gd.horizontalIndent = 17;
			container.setLayoutData(gd);
		}
		
		public Composite getContainerComposite()
		{
			return container;
		}
		
		private void updateCursor()
		{
			System.out.println("updating variable: " + cursorVariable);
			@SuppressWarnings("unused")
			FieldType oldType = cursorVariable.getType();
			String oldName = cursorVariable.getName();
			cursorVariable.setName(varNameText.getText());
			if(arraySelection == null)
			{
				cursorVariable.setType(FieldType.STRING);
				cursorVariable.clearFields();
				VariableHelper.buildObjectFields(cursorVariable, getElement().getDesign().getDocument().getProject().getBusinessObjectSet());
			}
			else
			{
				FieldType arrayType = arraySelection.getType();
				if(arrayType.isObjectBaseType())
					cursorVariable.setType(new FieldType(arrayType.getObjectBaseType()));
				else
					cursorVariable.setType(new FieldType(arrayType.getPrimitiveBaseType()));
				cursorVariable.clearFields();
				VariableHelper.buildObjectFields(cursorVariable, getElement().getDesign().getDocument().getProject().getBusinessObjectSet());
			}
			if(!cursorVariable.getName().equals(oldName))
			{
				variableScopes.remove(this);
			}
			postScopeChange();
		}
		
		protected void scopeChanged()
		{
			variableScopes.remove(this);
			arrayNameText.setText(arraySelection.getPath());
			varNameText.setText(varNameText.getText());
		}
		
		public synchronized List<Variable> getVariableScope()
		{
			List<Variable> scope = variableScopes.get(this);
			if(scope == null)
			{
				scope = new ArrayList<Variable>();
				scope.add(cursorVariable);
				List<Variable> parentScope = super.getVariableScope();
				for(Variable var : parentScope)
				{
					if(!var.getName().equals(cursorVariable.getName()))
					{
						scope.add(var);
					}
				}
				variableScopes.put(this, scope);
			}
			return scope;
		}
		
		public void setContent(ForLoopDocumentItem ifItem)
		{
			this.forItem = ifItem;
			Map<String, Boolean> states = expansionStates.get(ifItem);
			if(states != null)
			{
				Boolean state = states.get("properties");
				if(state == null)
					state = true;
				propertiesExpander.setExpanded(state);
				GridData gd = (GridData)propertiesComp.getLayoutData();
				gd.exclude = !propertiesExpander.isExpanded();
				propertiesComp.setVisible(propertiesExpander.isExpanded());
				state = states.get("contents");
				if(state == null)
					state = true;
				contentsExpander.setExpanded(state);
				gd = (GridData)container.getLayoutData();
				gd.exclude = !contentsExpander.isExpanded();
				container.setVisible(contentsExpander.isExpanded());
				rootComp.layout(true, true);
			}
			arrayNameText.setText(forItem.getTransform() == null ? "" : forItem.getTransform());
			varNameText.setText(forItem.getVariableName() == null ? "" : forItem.getVariableName());
		}

		protected void delete()
		{
			forItem.getParent().removeItem(forItem);
			expansionStates.remove(forItem);
			markOrigin();
			buildDocumentDisplay();
			restoreOrigin();
		}
		
	}

	public class UIElementContent extends UIContentContainer implements BrandedUIItem
	{
		private boolean textOnly = false;
		private ElementDocumentItem elementItem = null;
		private TextDocumentItem textItem = null;
		private ValueStack valueStack = null;
		private Text contentArea = null;
		private boolean updating = false;
		private Composite container = null;
		private Composite contentsComp = null;
		private Label attributesIconLabel = null;
		private Label attributesLabel = null;
		private Composite attributesComp = null;	
		private Twistie propertiesExpander = null;
		private Twistie contentsExpander = null;
		
		public UIElementContent(UIContentComposite scoping, Composite parent, boolean composing, boolean textOnly)
		{
			super(scoping, parent, SINGLE_DELETE, composing);
			setIcon(Activator.getDefault().getImageRegistry().get("XML_ELEMENT"));
			this.textOnly = textOnly;

			Composite bodyComp = new Composite(this, SWT.NONE);
			GridLayout layout = new GridLayout(3, false);
			layout.marginWidth = 0;
			layout.marginLeft = 5;
			bodyComp.setLayout(layout);
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalIndent = 15;
			bodyComp.setLayoutData(gd);
			
			if(textOnly)
			{
				container = this;
				propertiesExpander = new Twistie(bodyComp, SWT.NO_FOCUS);
				propertiesExpander.setExpanded(true);
				propertiesExpander.setLayoutData(new GridData());
				attributesIconLabel = new Label(bodyComp, SWT.NONE);
				attributesIconLabel.setImage(Activator.getDefault().getImageRegistry().get("ATTRIBUTE_GROUP"));
				attributesIconLabel.setLayoutData(new GridData());
				attributesLabel = new Label(bodyComp, SWT.NONE);
				attributesLabel.setText("Attributes");
				attributesLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

				attributesComp = new Composite(bodyComp, SWT.NONE);
				propertiesExpander.addHyperlinkListener(new IHyperlinkListener()
				{
					public void linkExited(HyperlinkEvent e)
					{
					}
					
					public void linkEntered(HyperlinkEvent e)
					{
					}
					
					public void linkActivated(HyperlinkEvent e)
					{
						GridData gd = (GridData)attributesComp.getLayoutData();
						gd.exclude = !propertiesExpander.isExpanded();
						attributesComp.setVisible(propertiesExpander.isExpanded());
						rootComp.layout(true, true);
						Map<String, Boolean> states = expansionStates.get(elementItem);
						if(states == null)
						{
							states = new HashMap<String, Boolean>();
							expansionStates.put(elementItem, states);
						}
						states.put("attributes", propertiesExpander.isExpanded());
					}
				});
				attributesComp.setLayout(new GridLayout(3, false));
				GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
				layoutData.horizontalSpan = 3;
				layoutData.horizontalIndent = 17;
				attributesComp.setLayoutData(layoutData);

				contentsExpander = new Twistie(bodyComp, SWT.NO_FOCUS);
				contentsExpander.setExpanded(true);
				contentsExpander.setLayoutData(new GridData());
				Label contentsIconLabel = new Label(bodyComp, SWT.NONE);
				contentsIconLabel.setImage(Activator.getDefault().getImageRegistry().get("COMPLEX_TYPE"));
				contentsIconLabel.setLayoutData(new GridData());
				Label contentsLabel = new Label(bodyComp, SWT.NONE);
				contentsLabel.setText("Contents");
				contentsLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
				
				contentsComp = UIHelper.createWrapperComposite(bodyComp, 17);
				contentsExpander.addHyperlinkListener(new IHyperlinkListener()
				{
					public void linkExited(HyperlinkEvent e)
					{
					}
					
					public void linkEntered(HyperlinkEvent e)
					{
					}
					
					public void linkActivated(HyperlinkEvent e)
					{
						GridData gd = (GridData)contentsComp.getLayoutData();
						gd.exclude = !contentsExpander.isExpanded();
						contentsComp.setVisible(contentsExpander.isExpanded());
						Map<String, Boolean> states = expansionStates.get(elementItem);
						if(states == null)
						{
							states = new HashMap<String, Boolean>();
							expansionStates.put(elementItem, states);
						}
						states.put("contents", contentsExpander.isExpanded());
						rootComp.layout(true, true);
					}
				});
				layout = (GridLayout)contentsComp.getLayout();
				layout.marginWidth = 5;
				contentsComp.setBackground(null);
				contentsComp.setBackgroundMode(SWT.INHERIT_DEFAULT);
				contentsComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
				valueStack = new ValueStack("", "", getVariableScope());
				valueStack.createControls(contentsComp);
				contentArea = new Text(valueStack.getValueComposite(), SWT.BORDER | SWT.H_SCROLL);
				valueStack.setValueControl(new ValueControl()
				{
					public String getValue()
		            {
						return contentArea.getText();
		            }
	
					public void setValue(String value)
		            {
						updating = true;
						contentArea.setText(value == null ? "" : value);
						updating = false;
		            }
				});
				contentArea.addModifyListener(new ModifyListener()
				{
					public void modifyText(ModifyEvent e)
					{
						Point size = contentArea.computeSize(contentArea.getSize().x, SWT.DEFAULT, true);
						GridData gd = (GridData)contentArea.getLayoutData();
						gd.minimumHeight = Math.max(18, size.y);
						if(!updating)
						{
							rootComp.setSize(rootComp.computeSize(scrollComp.getClientArea().width - 1, SWT.DEFAULT));
							scrollComp.layout(true, true);
						}
					}
				});
				contentArea.addListener(SWT.MouseWheel, new Listener()
				{
					public void handleEvent(Event event)
					{
						Point origin = scrollComp.getOrigin();
						origin.y -= event.count;
						event.doit = false;
						scrollComp.setOrigin(origin);
					}
				});
				gd = new GridData(GridData.FILL_HORIZONTAL);
				gd.minimumHeight = 18;
				gd.widthHint = 5000;
				contentArea.setLayoutData(gd);
				valueStacks.add(this);
				((GridData)contentsComp.getLayoutData()).horizontalSpan = 3;
			}
			else
			{
				propertiesExpander = new Twistie(bodyComp, SWT.NO_FOCUS);
				propertiesExpander.setExpanded(true);
				propertiesExpander.setLayoutData(new GridData());
				Label attributesIconLabel = new Label(bodyComp, SWT.NONE);
				attributesIconLabel.setImage(Activator.getDefault().getImageRegistry().get("ATTRIBUTE_GROUP"));
				attributesIconLabel.setLayoutData(new GridData());
				Label attributesLabel = new Label(bodyComp, SWT.NONE);
				attributesLabel.setText("Attributes");
				attributesLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

				attributesComp = new Composite(bodyComp, SWT.NONE);
				propertiesExpander.addHyperlinkListener(new IHyperlinkListener()
				{
					public void linkExited(HyperlinkEvent e)
					{
					}
					
					public void linkEntered(HyperlinkEvent e)
					{
					}
					
					public void linkActivated(HyperlinkEvent e)
					{
						GridData gd = (GridData)attributesComp.getLayoutData();
						gd.exclude = !propertiesExpander.isExpanded();
						attributesComp.setVisible(propertiesExpander.isExpanded());
						rootComp.layout(true, true);
						Map<String, Boolean> states = expansionStates.get(elementItem);
						if(states == null)
						{
							states = new HashMap<String, Boolean>();
							expansionStates.put(elementItem, states);
						}
						states.put("attributes", propertiesExpander.isExpanded());
					}
				});
				attributesComp.setLayout(new GridLayout(3, false));
				GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
				layoutData.horizontalSpan = 3;
				layoutData.horizontalIndent = 17;
				attributesComp.setLayoutData(layoutData);

				contentsExpander = new Twistie(bodyComp, SWT.NO_FOCUS);
				contentsExpander.setExpanded(true);
				contentsExpander.setLayoutData(new GridData());
				Label contentsIconLabel = new Label(bodyComp, SWT.NONE);
				contentsIconLabel.setImage(Activator.getDefault().getImageRegistry().get("COMPLEX_TYPE"));
				contentsIconLabel.setLayoutData(new GridData());
				Label contentsLabel = new Label(bodyComp, SWT.NONE);
				contentsLabel.setText("Contents");
				contentsLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
				
				container = contentsComp = new Composite(bodyComp, SWT.NONE);
				contentsExpander.addHyperlinkListener(new IHyperlinkListener()
				{
					public void linkExited(HyperlinkEvent e)
					{
					}
					
					public void linkEntered(HyperlinkEvent e)
					{
					}
					
					public void linkActivated(HyperlinkEvent e)
					{
						GridData gd = (GridData)contentsComp.getLayoutData();
						gd.exclude = !contentsExpander.isExpanded();
						contentsComp.setVisible(contentsExpander.isExpanded());
						Map<String, Boolean> states = expansionStates.get(elementItem);
						if(states == null)
						{
							states = new HashMap<String, Boolean>();
							expansionStates.put(elementItem, states);
						}
						states.put("contents", contentsExpander.isExpanded());
						rootComp.layout(true, true);
					}
				});
				layout = new GridLayout(1, false);
				layout.marginWidth = 0;
				layout.marginLeft = 5;
				contentsComp.setLayout(layout);
				gd = new GridData(GridData.FILL_HORIZONTAL);
				gd.horizontalIndent = 17;
				gd.horizontalSpan = 3;
				contentsComp.setLayoutData(gd);
			}
		}
		
		public Composite getContainerComposite()
		{
			return container;
		}
		
		
		public BrandedBinding getBinding()
		{
			return textItem;
		}
		
		protected void scopeChanged()
		{
			if(textOnly)
				valueStack.setVariables(getVariableScope());
			else
				super.scopeChanged();
		}

		public ValueStack getValueStack()
		{
			return valueStack;
		}
		
		public void setContent(ElementDocumentItem elementItem)
		{
			this.elementItem = elementItem;
			setTitle(elementItem.getName());
			Map<String, Boolean> states = expansionStates.get(elementItem);
			if(states != null)
			{
				Boolean state = states.get("attributes");
				if(state == null)
					state = true;
				propertiesExpander.setExpanded(state);
				GridData gd = (GridData)attributesComp.getLayoutData();
				gd.exclude = !propertiesExpander.isExpanded();
				attributesComp.setVisible(propertiesExpander.isExpanded());
				state = states.get("contents");
				if(state == null)
					state = true;
				contentsExpander.setExpanded(state);
				gd = (GridData)contentsComp.getLayoutData();
				gd.exclude = !contentsExpander.isExpanded();
				contentsComp.setVisible(contentsExpander.isExpanded());
				rootComp.layout(true, true);
			}
			if(elementItem.isSimple())
			{
				((GridData)propertiesExpander.getLayoutData()).exclude = true;
				propertiesExpander.setVisible(false);
				((GridData)attributesIconLabel.getLayoutData()).exclude = true;
				attributesIconLabel.setVisible(false);
				((GridData)attributesLabel.getLayoutData()).exclude = true;
				attributesLabel.setVisible(false);
				((GridData)attributesComp.getLayoutData()).exclude = true;
				attributesComp.setVisible(false);
				layout();
				return;
			}
			List<ElementAttributeDocumentItem> attributes = elementItem.getAttributes();
			for(ElementAttributeDocumentItem attribute : attributes)
			{
				Button attributePresent = new Button(attributesComp, SWT.CHECK);
				attributePresent.setLayoutData(new GridData());
				Label attributeName = new Label(attributesComp, SWT.NONE);
				attributeName.setText(attribute.getName());
				attributeName.setLayoutData(new GridData());
				attributePresent.setSelection(!attribute.isOptional() || attribute.isPresent());
				final ValueStack attributeValueStack = new ValueStack("", "", getVariableScope());
				attributeValueStack.createControls(attributesComp);
				final Text valueText = new Text(attributeValueStack.getValueComposite(), SWT.BORDER | SWT.SINGLE);
				attributeValueStack.setValueControl(new ValueControl()
				{
					public String getValue()
		            {
						return valueText.getText();
		            }
	
					public void setValue(String value)
		            {
						valueText.setText(value == null ? "" : value);
		            }
				});
				GridData gd = new GridData(GridData.FILL_HORIZONTAL);
				gd.minimumHeight = 18;
				gd.widthHint = 5000;
				valueText.setLayoutData(gd);
				final ElementAttributeDocumentItem attributeRef = attribute;
				valueStacks.add(new BrandedUIItem()
				{
					public BrandedBinding getBinding()
					{
						return attributeRef;
					}

					public ValueStack getValueStack()
					{
						return attributeValueStack;
					}
				});
			}
			if(attributes.size() == 0)
			{
				if(states == null)
				{
					states = new HashMap<String, Boolean>();
					expansionStates.put(elementItem, states);
				}
				states.put("attributes", false);
				Label noAttributesLabel = new Label(attributesComp, SWT.NONE);
				noAttributesLabel.setText("None");
				GridData gd = new GridData(GridData.FILL_HORIZONTAL);
				gd.horizontalSpan = 3;
				noAttributesLabel.setLayoutData(gd);
				propertiesExpander.setExpanded(false);
				gd = (GridData)attributesComp.getLayoutData();
				gd.exclude = !propertiesExpander.isExpanded();
				attributesComp.setVisible(propertiesExpander.isExpanded());
				rootComp.layout(true, true);
			}
		}
		
		public void setText(TextDocumentItem textItem)
		{
			if(textOnly)
			{
				this.textItem = textItem;
			}
		}
		
		protected void delete()
		{
			elementItem.getParent().removeItem(elementItem);
			expansionStates.remove(elementItem);
			markOrigin();
			buildDocumentDisplay();
			restoreOrigin();
		}
		
		public boolean isTextOnly()
		{
			return textOnly;
		}
	}

	public class UISuggestionComposite extends Composite
	{
		private List<Suggestion> suggestions = new ArrayList<Suggestion>();
		boolean hasRequired = false;
		private Color borderColor = null;
		
		public UISuggestionComposite(Composite parent, int style)
		{
			super(parent, style);
			borderColor = new Color(getDisplay(), 231, 233, 238);
			this.addPaintListener(new PaintListener()
			{
				public void paintControl(PaintEvent e)
				{
					GC g = e.gc;
					Color oldForground = g.getForeground();
					g.setForeground(borderColor);
					g.drawLine(0, 0, UISuggestionComposite.this.getSize().x - 10, 0);
					g.drawLine(0, 0, 0, UISuggestionComposite.this.getSize().y - 10);
					g.setForeground(oldForground);
				}
			});
			GridLayout layout = new GridLayout(1, false);
			layout.marginWidth = 0;
			layout.marginLeft = 15;
			layout.marginBottom = 6;
			setLayout(layout);
			setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		}

		public void addSuggestion(Suggestion suggestion)
		{
			suggestions.add(suggestion);
			if(hasRequired)
				suggestion.setActive(false);
			hasRequired |= suggestion.isRequired();
			suggestion.createControls(this);
			rootComp.layout(true, true);
		}

		public void dispose()
		{
			borderColor.dispose();
			super.dispose();
		}
		
	}
	
	public abstract class Suggestion
	{
		private DocumentItemContainer container = null;
		private boolean active = false;
		private boolean required = false;
		private DocumentItem insertionPoint = null;
		
		
		public Suggestion(DocumentItemContainer container, boolean required)
		{
			super();
			this.container = container;
			this.required = required;
		}
		
		public DocumentItemContainer getContainer()
		{
			return container;
		}
		
		public DocumentItem getInsertionPoint()
		{
			return insertionPoint;
		}
		
		public void setInsertionPoint(DocumentItem insertionPoint)
		{
			this.insertionPoint = insertionPoint;
		}
		
		public boolean isActive()
		{
			return active;
		}
		
		public void setActive(boolean active)
		{
			this.active = active;
		}
		
		public boolean isRequired()
		{
			return required;
		}
		
		public abstract void createControls(Composite parent);
	}
	
	public class TextSuggestion extends Suggestion
	{
		public TextSuggestion(DocumentItemContainer container, boolean required)
		{
			super(container, required);
		}
		
		public void createControls(Composite parent)
		{
			Label nameLabel = new Label(parent, SWT.NONE);
			nameLabel.setText("Text Content (" + (isRequired() ? "Required" : "Optional") + ")");
			nameLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			nameLabel.addMouseListener(new MouseListener()
			{
				public void mouseDown(MouseEvent e)
				{
				}

				public void mouseUp(MouseEvent e)
				{
					markOrigin();
					try
					{
						getContainer().insertItem(new TextDocumentItem(manager), getInsertionPoint());
						buildDocumentDisplay();
					}
					catch (Exception e1)
					{
						e1.printStackTrace();
					}
					restoreOrigin();
				}
				
				public void mouseDoubleClick(MouseEvent e)
				{
				}
				
			});
		}
	}

	public class IfSuggestion extends Suggestion
	{
		public IfSuggestion(DocumentItemContainer container, boolean required)
		{
			super(container, required);
		}
		
		public void createControls(Composite parent)
		{
			final Hyperlink nameLink = new Hyperlink(parent, SWT.NONE);
			nameLink.setText("IF");
			nameLink.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			nameLink.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_BLUE));
			nameLink.addHyperlinkListener(new IHyperlinkListener()
			{
				public void linkEntered(HyperlinkEvent e)
				{
					nameLink.setUnderlined(true);
				}

				public void linkExited(HyperlinkEvent e)
				{
					nameLink.setUnderlined(false);
				}

				public void linkActivated(HyperlinkEvent e)
				{
					markOrigin();
					ConditionalContainerSet conditionalSet = new ConditionalContainerSet(manager);
					getContainer().insertItem(conditionalSet, getInsertionPoint());
					ConditionalDocumentItem conditional = new ConditionalDocumentItem(manager);
					conditionalSet.setIf(conditional);
					buildDocumentDisplay();
					restoreOrigin();
				}
			});
		}
	}

	public class ElseIfSuggestion extends Suggestion
	{
		public ElseIfSuggestion(DocumentItemContainer container, boolean required)
		{
			super(container, required);
		}
		
		public void createControls(Composite parent)
		{
			final Hyperlink nameLink = new Hyperlink(parent, SWT.NONE);
			nameLink.setText("ELSE IF");
			nameLink.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			nameLink.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_BLUE));
			nameLink.addHyperlinkListener(new IHyperlinkListener()
			{
				public void linkEntered(HyperlinkEvent e)
				{
					nameLink.setUnderlined(true);
				}

				public void linkExited(HyperlinkEvent e)
				{
					nameLink.setUnderlined(false);
				}

				public void linkActivated(HyperlinkEvent e)
				{
					markOrigin();
					ConditionalDocumentItem conditional = new ConditionalDocumentItem(manager);
					((ConditionalContainerSet)getContainer()).addItem(conditional);
					buildDocumentDisplay();
					restoreOrigin();
				}
			});
		}
	}

	public class ElseSuggestion extends Suggestion
	{
		public ElseSuggestion(DocumentItemContainer container, boolean required)
		{
			super(container, required);
		}
		
		public void createControls(Composite parent)
		{
			final Hyperlink nameLink = new Hyperlink(parent, SWT.NONE);
			nameLink.setText("ELSE" + (isRequired() ? " (Required)" : ""));
			nameLink.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			nameLink.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_BLUE));
			nameLink.addHyperlinkListener(new IHyperlinkListener()
			{
				public void linkEntered(HyperlinkEvent e)
				{
					nameLink.setUnderlined(true);
				}

				public void linkExited(HyperlinkEvent e)
				{
					nameLink.setUnderlined(false);
				}

				public void linkActivated(HyperlinkEvent e)
				{
					markOrigin();
					try
					{
						ElseDocumentItem conditional = new ElseDocumentItem(manager);
						((ConditionalContainerSet)getContainer()).setElse(conditional);
						buildDocumentDisplay();
					}
					catch(Exception ex)
					{
						ex.printStackTrace();
					}
					restoreOrigin();
				}
			});
		}
	}
	
	public class ForLoopSuggestion extends Suggestion
	{
		public ForLoopSuggestion(DocumentItemContainer container, boolean required)
		{
			super(container, required);
		}
		
		public void createControls(Composite parent)
		{
			final Hyperlink nameLink = new Hyperlink(parent, SWT.NONE);
			nameLink.setText("FOR" + (isRequired() ? " (Required)" : ""));
			nameLink.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			nameLink.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_BLUE));
			nameLink.addHyperlinkListener(new IHyperlinkListener()
			{
				public void linkEntered(HyperlinkEvent e)
				{
					nameLink.setUnderlined(true);
				}

				public void linkExited(HyperlinkEvent e)
				{
					nameLink.setUnderlined(false);
				}

				public void linkActivated(HyperlinkEvent e)
				{
					markOrigin();
					ForLoopDocumentItem forItem = new ForLoopDocumentItem(manager);
					getContainer().insertItem(forItem, getInsertionPoint());
					buildDocumentDisplay();
					restoreOrigin();
				}
			});
		}
	}

	public class ElementSuggestion extends Suggestion
	{
		private ElementItem element = null;
		
		public ElementSuggestion(ElementItem element, DocumentItemContainer container, boolean required)
		{
			super(container, required);
			this.element = element;
		}
		
		public boolean isTextOnly()
		{
			Type type = element.getType();
			if(type instanceof SimpleType)
				return true;
			ComplexType complexType = (ComplexType)type;
			ContentModel contentModel = complexType.getContentModel();
			return contentModel instanceof SimpleContentModel;
		}
		
		public void createControls(Composite parent)
		{
			final Hyperlink nameLink = new Hyperlink(parent, SWT.NONE);
			nameLink.setText("Element: " + element.getName() + " " + (isRequired() ? " (Required)" : ""));
			nameLink.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			nameLink.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_BLUE));
			nameLink.addControlListener(new ControlListener()
			{
				public void controlMoved(ControlEvent e)
				{
				}

				public void controlResized(ControlEvent e)
				{
					System.out.println(nameLink.getSize());
				}
			});
			nameLink.addHyperlinkListener(new IHyperlinkListener()
			{
				public void linkEntered(HyperlinkEvent e)
				{
					nameLink.setUnderlined(true);
				}

				public void linkExited(HyperlinkEvent e)
				{
					nameLink.setUnderlined(false);
				}

				public void linkActivated(HyperlinkEvent e)
				{
					markOrigin();
					ElementDocumentItem elementItem = new ElementDocumentItem(manager);
					elementItem.setName(element.getName());
					elementItem.setNamespace(element.getOwnerSchema().getTargetNamespace());
					Type type = element.getType();
					if(type instanceof ComplexType)
					{
						ComplexType complexType = (ComplexType)type;
						ContentModel contentModel = complexType.getContentModel();
						List<AttributeItem> attributes = contentModel.getAttributes();
						for(AttributeItem attributeItem : attributes)
						{
							ElementAttributeDocumentItem attributeDocumentItem = new ElementAttributeDocumentItem(manager);
							attributeDocumentItem.setName(attributeItem.getName());
							attributeDocumentItem.setOptional(!attributeItem.isRequired());
							elementItem.addAttribute(attributeDocumentItem);
						}
					}
					else
						elementItem.setSimple(true);
					getContainer().insertItem(elementItem, getInsertionPoint());
					if(isTextOnly())
						elementItem.insertItem(new TextDocumentItem(manager), null);
					buildDocumentDisplay();
					restoreOrigin();
				}
			});
		}
	}
}
