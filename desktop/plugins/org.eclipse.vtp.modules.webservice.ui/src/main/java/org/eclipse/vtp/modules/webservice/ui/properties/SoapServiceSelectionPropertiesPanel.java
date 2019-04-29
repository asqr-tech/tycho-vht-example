/**
 * 
 */
package org.eclipse.vtp.modules.webservice.ui.properties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.vtp.desktop.editors.core.configuration.DesignElementPropertiesPanel;
import org.eclipse.vtp.desktop.model.core.IWebserviceDescriptor;
import org.eclipse.vtp.desktop.model.core.design.IDesignElement;
import org.eclipse.vtp.desktop.model.core.wsdl.BindingOperation;
import org.eclipse.vtp.desktop.model.core.wsdl.Port;
import org.eclipse.vtp.desktop.model.core.wsdl.Service;
import org.eclipse.vtp.desktop.model.core.wsdl.WSDL;
import org.eclipse.vtp.desktop.model.core.wsdl.soap.SoapBinding;
import org.eclipse.vtp.desktop.model.core.wsdl.soap.SoapBindingOperation;
import org.eclipse.vtp.modules.webservice.ui.configuration.WebserviceBindingManager;
import org.eclipse.vtp.modules.webservice.ui.configuration.WebserviceServiceBinding;

/**
 * @author trip
 *
 */
public class SoapServiceSelectionPropertiesPanel extends
	DesignElementPropertiesPanel
{
	private static final String NO_DESCRIPTORS = "No descriptors available";
	private static final String NO_SERVICES = "No services available";
	private static final String NO_PORTS = "No ports available";
	private static final String NO_OPERATIONS = "No operations available";
	
	private WebserviceBindingManager manager = null;
	private WebserviceServiceBinding serviceBinding = null;
	private IWebserviceDescriptor currentDescriptor = null;
	private List<IWebserviceDescriptor> descriptors = null;
	private Service currentService = null;
	private List<Service> services = new ArrayList<Service>();
	private Port currentPort = null;
	private List<Port> ports = new ArrayList<Port>();
	private SoapBindingOperation currentBindingOperation = null;
	private List<BindingOperation> bindingOperations = new ArrayList<BindingOperation>();
	private Text nameField = null;
	private Text urlField = null;
	private ComboViewer descriptorCombo = null;
	private ComboViewer serviceCombo = null;
	private ComboViewer portCombo = null;
	private ComboViewer operationCombo = null;
	private boolean updating = false;
	private List<IOperationListener> listeners = new LinkedList<IOperationListener>();
	private String currentKey = null;

	/**
	 * @param name
	 * @param element
	 */
	public SoapServiceSelectionPropertiesPanel(String name, IDesignElement element)
	{
		super(name, element);
		descriptors = element.getDesign().getDocument().getProject().getWebserviceSet().getWebserviceDescriptors(true);
		manager = (WebserviceBindingManager)element.getConfigurationManager(WebserviceBindingManager.TYPE_ID);
		serviceBinding = manager.getServiceBinding();
		currentKey = serviceBinding.getDescriptor() + ":" + 
					 serviceBinding.getService() + ":" + 
					 serviceBinding.getPort() + ":" + 
					 serviceBinding.getOperation();
		if(descriptors.size() > 0)
			updateSelections();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.editors.core.configuration.ComponentPropertiesPanel#createControls(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControls(Composite parent)
	{
		FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		Composite mainComp = new Composite(parent, SWT.NONE);
		mainComp.setBackground(parent.getBackground());
		mainComp.setLayout(new GridLayout(2, false));
		
		final Section contentSection =
			toolkit.createSection(mainComp, Section.TITLE_BAR);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL
						| GridData.VERTICAL_ALIGN_BEGINNING);
		gd.horizontalSpan = 2;
		contentSection.setLayoutData(gd);
		contentSection.setText("General");
		
		Label nameLabel = new Label(mainComp, SWT.NONE);
		nameLabel.setBackground(mainComp.getBackground());
		nameLabel.setText("Name");
		nameLabel.setLayoutData(new GridData());
		nameField = new Text(mainComp, SWT.BORDER | SWT.SINGLE);
		nameField.setText(getElement().getName());
		nameField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Label urlLabel = new Label(mainComp, SWT.NONE);
		urlLabel.setBackground(mainComp.getBackground());
		urlLabel.setText("URL");
		urlLabel.setLayoutData(new GridData());
		urlField = new Text(mainComp, SWT.BORDER | SWT.SINGLE);
		urlField.setText(serviceBinding.getURL());
		urlField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		final Section serviceSection =
			toolkit.createSection(mainComp, Section.TITLE_BAR);
		gd = new GridData(GridData.FILL_HORIZONTAL
						| GridData.VERTICAL_ALIGN_BEGINNING);
		gd.horizontalSpan = 2;
		serviceSection.setLayoutData(gd);
		serviceSection.setText("Service Information");
		
		Label descriptorLabel = new Label(mainComp, SWT.NONE);
		descriptorLabel.setBackground(mainComp.getBackground());
		descriptorLabel.setText("Descriptor");
		descriptorLabel.setLayoutData(new GridData());
		
		descriptorCombo = new ComboViewer(new Combo(mainComp, SWT.DROP_DOWN | SWT.READ_ONLY));
		DescriptorContentLabelProvider dclp = new DescriptorContentLabelProvider();
		descriptorCombo.setContentProvider(dclp);
		descriptorCombo.setLabelProvider(dclp);
		descriptorCombo.setInput(this);
		descriptorCombo.getCombo().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Label serviceLabel = new Label(mainComp, SWT.NONE);
		serviceLabel.setBackground(mainComp.getBackground());
		serviceLabel.setText("Service");
		serviceLabel.setLayoutData(new GridData());
		
		serviceCombo = new ComboViewer(new Combo(mainComp, SWT.DROP_DOWN | SWT.READ_ONLY));
		ServiceContentLabelProvider sclp = new ServiceContentLabelProvider();
		serviceCombo.setContentProvider(sclp);
		serviceCombo.setLabelProvider(sclp);
		serviceCombo.setInput(this);
		serviceCombo.getCombo().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Label portLabel = new Label(mainComp, SWT.NONE);
		portLabel.setBackground(mainComp.getBackground());
		portLabel.setText("Port");
		portLabel.setLayoutData(new GridData());
		
		portCombo = new ComboViewer(new Combo(mainComp, SWT.DROP_DOWN | SWT.READ_ONLY));
		PortContentLabelProvider pclp = new PortContentLabelProvider();
		portCombo.setContentProvider(pclp);
		portCombo.setLabelProvider(pclp);
		portCombo.setInput(this);
		portCombo.getCombo().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Label operationLabel = new Label(mainComp, SWT.NONE);
		operationLabel.setBackground(mainComp.getBackground());
		operationLabel.setText("Operation");
		operationLabel.setLayoutData(new GridData());
		
		operationCombo = new ComboViewer(new Combo(mainComp, SWT.DROP_DOWN | SWT.READ_ONLY));
		OperationContentLabelProvider oclp = new OperationContentLabelProvider();
		operationCombo.setContentProvider(oclp);
		operationCombo.setLabelProvider(oclp);
		operationCombo.setInput(this);
		operationCombo.getCombo().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		descriptorCombo.addSelectionChangedListener(new ISelectionChangedListener ()
		{
			public void selectionChanged(SelectionChangedEvent event)
			{
				System.out.println("descriptor changed");
				if(!updating && currentDescriptor != null)
				{
					updating = true;
					IWebserviceDescriptor descriptor = (IWebserviceDescriptor)((IStructuredSelection)descriptorCombo.getSelection()).getFirstElement();
					if(descriptor != currentDescriptor)
					{
						serviceBinding.setDescriptor(descriptor.getName());
						serviceBinding.setService(null);
						serviceBinding.setPort(null);
						serviceBinding.setOperation(null);
						updateSelections();
					}
					updating = false;
				}
			}
		});

		serviceCombo.addSelectionChangedListener(new ISelectionChangedListener ()
		{
			public void selectionChanged(SelectionChangedEvent event)
			{
				System.out.println("service changed");
				if(!updating && currentService != null)
				{
					updating = true;
					Service service = (Service)((IStructuredSelection)serviceCombo.getSelection()).getFirstElement();
					if(service != currentService)
					{
						serviceBinding.setService(service.getName());
						serviceBinding.setPort(null);
						serviceBinding.setOperation(null);
						updateSelections();
					}
					updating = false;
				}
			}
		});

		portCombo.addSelectionChangedListener(new ISelectionChangedListener ()
		{
			public void selectionChanged(SelectionChangedEvent event)
			{
				System.out.println("port changed");
				if(!updating && currentPort != null)
				{
					updating = true;
					Port port = (Port)((IStructuredSelection)portCombo.getSelection()).getFirstElement();
					if(port != currentPort)
					{
						serviceBinding.setPort(port.getName());
						serviceBinding.setOperation(null);
						updateSelections();
					}
					updating = false;
				}
			}
		});

		operationCombo.addSelectionChangedListener(new ISelectionChangedListener ()
		{
			public void selectionChanged(SelectionChangedEvent event)
			{
				System.out.println("operation changed");
				if(!updating && currentBindingOperation != null)
				{
					updating = true;
					SoapBindingOperation operation = (SoapBindingOperation)((IStructuredSelection)operationCombo.getSelection()).getFirstElement();
					if(operation != currentBindingOperation)
					{
						serviceBinding.setOperation(operation.getOperation().getName());
						serviceBinding.setSoapAction(operation.getSoapAction());
						updateSelections();
					}
					updating = false;
				}
			}
		});

		updateSelections();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.editors.core.configuration.ComponentPropertiesPanel#save()
	 */
	@Override
	public void save()
	{
		getElement().setName(nameField.getText());
		serviceBinding.setURL(urlField.getText());
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
	
	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.editors.core.configuration.ComponentPropertiesPanel#setConfigurationContext(java.util.Map)
	 */
	@Override
	public void setConfigurationContext(Map<String, Object> values)
	{
	}

	@Override
	public List<String> getApplicableContexts()
	{
		return Collections.emptyList();
	}
	
	private List<Service> getSoapServices(List<Service> genericServices)
	{
		List<Service> ret = new ArrayList<Service>();
		for(Service service : genericServices)
		{
			if(hasSoapBinding(service))
				ret.add(service);
		}
		return ret;
	}
	
	private boolean hasSoapBinding(Service service)
	{
		List<Port> searchPorts = service.getPorts();
		for(Port port : searchPorts)
		{
			if(port.getBinding() instanceof SoapBinding)
				return true;
		}
		return false;
	}
	
	private List<Port> getSoapPorts(List<Port> genericPorts)
	{
		List<Port> ret = new ArrayList<Port>();
		for(Port port : genericPorts)
		{
			if(port.getBinding() instanceof SoapBinding)
				ret.add(port);
		}
		return ret;
	}
	
	private void updateSelections()
	{
//		IWebserviceDescriptor oldDescriptor = currentDescriptor;
//		Service oldService = currentService;
//		Port oldPort = currentPort;
//		SoapBindingOperation oldOperation = currentBindingOperation;
		currentDescriptor = null;
		currentService = null;
		currentPort = null;
		currentBindingOperation = null;
		if(serviceBinding.getDescriptor() != null)
		{
			for(IWebserviceDescriptor descriptor : descriptors)
			{
				if(descriptor.getName().equals(serviceBinding.getDescriptor()))
				{
					currentDescriptor = descriptor;
					break;
				}
			}
		}
		if(currentDescriptor == null && descriptors.size() > 0)
		{
			currentDescriptor = descriptors.get(0);
			serviceBinding.setDescriptor(currentDescriptor.getName());
		}
		if(currentDescriptor != null)
		{
			WSDL wsdl = null;
			try
			{
				wsdl = currentDescriptor.getWSDL();
			}
			catch(Exception we)
			{
				return;
			}
			services = getSoapServices(wsdl.getServices());
			if(serviceCombo != null)
				serviceCombo.refresh();
			if(serviceBinding.getService() != null)
			{
				for(Service service : services)
				{
					if(service.getName().equals(serviceBinding.getService()))
					{
						currentService = service;
						break;
					}
				}
			}
			if(currentService == null && services.size() > 0)
			{
				currentService = services.get(0);
				serviceBinding.setService(currentService.getName());
			}
			if(currentService != null)
			{
				ports = getSoapPorts(currentService.getPorts());
				if(portCombo != null)
					portCombo.refresh();
				if(serviceBinding.getPort() != null)
				{
					for(Port port : ports)
					{
						if(port.getName().equals(serviceBinding.getPort()))
						{
							currentPort = port;
							break;
						}
					}
				}
				if(currentPort == null && ports.size() > 0)
				{
					currentPort = ports.get(0);
					serviceBinding.setPort(currentPort.getName());
				}
				if(currentPort != null)
				{
					bindingOperations = currentPort.getBinding().getOperations();
					if(operationCombo != null)
						operationCombo.refresh();
					if(serviceBinding.getOperation() != null)
					{
						for(BindingOperation bindingOperation : bindingOperations)
						{
							if(bindingOperation.getOperation().getName().equals(serviceBinding.getOperation()))
							{
								currentBindingOperation = (SoapBindingOperation)bindingOperation;
								break;
							}
						}
					}
					if(currentBindingOperation == null && bindingOperations.size() > 0)
					{
						currentBindingOperation = (SoapBindingOperation)bindingOperations.get(0);
						serviceBinding.setOperation(currentBindingOperation.getOperation().getName());
						serviceBinding.setSoapAction(currentBindingOperation.getSoapAction());
					}
				}
			}
		}
		String oldKey = currentKey;
		currentKey = serviceBinding.getDescriptor() + ":" + 
		 serviceBinding.getService() + ":" + 
		 serviceBinding.getPort() + ":" + 
		 serviceBinding.getOperation();
		if(!oldKey.equals(currentKey))
			manager.getInputDocumentStructure().clearStructure();
		for(IOperationListener l : listeners)
		{
			l.operationChanged(currentBindingOperation);
		}
		if(descriptorCombo != null)
		{
			if(currentDescriptor != null)
			{
				descriptorCombo.setSelection(new StructuredSelection(currentDescriptor));
			}
			else if(descriptors.size() < 1)
			{
				descriptorCombo.setSelection(new StructuredSelection(NO_DESCRIPTORS));
			}
			
			if(currentService != null)
			{
				serviceCombo.setSelection(new StructuredSelection(currentService));
			}
			else if(services.size() < 1)
			{
				serviceCombo.setSelection(new StructuredSelection(NO_SERVICES));
			}
			
			if(currentPort != null)
			{
				portCombo.setSelection(new StructuredSelection(currentPort));
			}
			else if(ports.size() < 1)
			{
				portCombo.setSelection(new StructuredSelection(NO_PORTS));
			}
			
			if(currentBindingOperation != null)
			{
				operationCombo.setSelection(new StructuredSelection(currentBindingOperation));
			}
			else if(ports.size() < 1)
			{
				operationCombo.setSelection(new StructuredSelection(NO_OPERATIONS));
			}
		}
	}
	
	public SoapBindingOperation getBindingOperation()
	{
		return currentBindingOperation;
	}
	
	public void addOperationListener(IOperationListener l)
	{
		listeners.remove(l);
		listeners.add(l);
		l.operationChanged(currentBindingOperation);
	}
	
	public void removeOperationListener(IOperationListener l)
	{
		listeners.remove(l);
	}
	
	public class DescriptorContentLabelProvider extends BaseLabelProvider implements ILabelProvider, IStructuredContentProvider
	{

		public Image getImage(Object element)
		{
			return null;
		}

		public String getText(Object element)
		{
			if(element instanceof String)
				return (String)element;
			return ((IWebserviceDescriptor)element).getName();
		}

		public Object[] getElements(Object inputElement)
		{
			if(descriptors.size() > 0)
				return descriptors.toArray();
			return new Object[]	{NO_DESCRIPTORS};
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
		{
		}
	}
	
	public class ServiceContentLabelProvider extends BaseLabelProvider implements ILabelProvider, IStructuredContentProvider
	{

		public Image getImage(Object element)
		{
			return null;
		}

		public String getText(Object element)
		{
			if(element instanceof String)
				return (String)element;
			return ((Service)element).getName();
		}

		public Object[] getElements(Object inputElement)
		{
			if(services.size() > 0)
				return services.toArray();
			return new Object[]	{NO_SERVICES};
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
		{
		}
	}

	public class PortContentLabelProvider extends BaseLabelProvider implements ILabelProvider, IStructuredContentProvider
	{

		public Image getImage(Object element)
		{
			return null;
		}

		public String getText(Object element)
		{
			if(element instanceof String)
				return (String)element;
			return ((Port)element).getName();
		}

		public Object[] getElements(Object inputElement)
		{
			if(ports.size() > 0)
				return ports.toArray();
			return new Object[]	{NO_PORTS};
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
		{
		}
	}

	public class OperationContentLabelProvider extends BaseLabelProvider implements ILabelProvider, IStructuredContentProvider
	{

		public Image getImage(Object element)
		{
			return null;
		}

		public String getText(Object element)
		{
			if(element instanceof String)
				return (String)element;
			return ((SoapBindingOperation)element).getOperation().getName();
		}

		public Object[] getElements(Object inputElement)
		{
			if(bindingOperations.size() > 0)
				return bindingOperations.toArray();
			return new Object[]	{NO_OPERATIONS};
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
		{
		}
	}
}
