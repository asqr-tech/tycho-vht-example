/*--------------------------------------------------------------------------
 * Copyright (c) 2004, 2006-2007 OpenMethods, LLC
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Trip Gilman (OpenMethods), Lonnie G. Pryor (OpenMethods)
 *    - initial API and implementation
 -------------------------------------------------------------------------*/
package org.eclipse.vtp.desktop.editors.core.impl;

/*import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Resource;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.vtp.desktop.core.configuration.DefaultBrandManager;
import org.eclipse.vtp.desktop.core.configuration.WorkspaceMediaDefaultSettings;
import org.eclipse.vtp.desktop.editors.core.controller.BasicController;
import org.eclipse.vtp.desktop.editors.core.controller.ControllerListener;
import org.eclipse.vtp.desktop.editors.core.model.RenderedCanvas;
import org.eclipse.vtp.desktop.editors.core.model.RenderedModel;
import org.eclipse.vtp.desktop.editors.core.model.RenderedModelListener;
import org.eclipse.vtp.desktop.editors.core.model.SelectionStructure;
import org.eclipse.vtp.desktop.editors.core.model.UICanvas;
import org.eclipse.vtp.desktop.editors.core.model.UIModel;
import org.eclipse.vtp.desktop.editors.core.theme.CanvasFrame;
import org.eclipse.vtp.desktop.editors.core.theme.Theme;
import org.eclipse.vtp.desktop.editors.core.theme.ThemeManager;
import org.eclipse.vtp.desktop.model.core.Model;
import org.eclipse.vtp.framework.util.XMLWriter;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
*/
public class DialogEditor{}
/*extends EditorPart implements ControllerListener, RenderedModelListener
{
	RenderedModel renderedModel = null;
	BasicController controller = null;
	org.eclipse.swt.widgets.Canvas canvas = null;
	CanvasFrame canvasFrame = null;
	boolean dirty = false;
	DefaultBrandManager brandManager = new DefaultBrandManager();
	Map resourceMap = new HashMap();
	
	public DialogEditor()
	{
		super();
//		brandManager.addInteractionSupport("org.eclipse.vtp.framework.interactions.voice.interaction");
//		brandManager.addSupportedLanguage("org.eclipse.vtp.framework.interactions.voice.interaction", "Default");
//		IBrand defaultBrand = new IBrand("Default");
//		defaultBrand.registerMediaProvider("org.eclipse.vtp.framework.interactions.voice.interaction", "Default", new StubMediaProvider());
//		brandManager.setDefaultBrand(defaultBrand);
	}

	public void init(IEditorSite site, IEditorInput input) throws PartInitException
	{
		//required callbacks for the init function.  TG
		setSite(site);
		setInput(input);
		
		//load document contents
		FileEditorInput fileInput = (FileEditorInput)input;
		try
		{
			boolean neededConversion = false;
			DocumentBuilderFactory factory =
				DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			System.out.println(builder.getDOMImplementation().toString());
			System.out.println("supports namespaces: " + builder.isNamespaceAware());
			Document document = builder.parse(fileInput.getFile().getContents());
			org.w3c.dom.Element rootElement = document.getDocumentElement();
			if(!rootElement.getTagName().equals("dialog-definition"))
				throw new IllegalArgumentException("The file provided is not a valid dialog definition file");
			NodeList dialogList = rootElement.getElementsByTagName("model");
			if(dialogList.getLength() != 1) //a dialog definition can only contain a single dialog model
				throw new IllegalArgumentException("The file provided is not a valid dialog definition file");
			org.w3c.dom.Element modelElement = (org.w3c.dom.Element)dialogList.item(0);
			Design dialogModel = new Design();
			dialogModel.putDataService("BrandManager", brandManager);
			dialogModel.putDataService("IMediaDefaultSettings", WorkspaceMediaDefaultSettings.getInstance());
			neededConversion |= dialogModel.loadModel(modelElement, null);
			NodeList designList = rootElement.getElementsByTagName("design");
			if(designList.getLength() != 1) //a dialog definition can only contain a single dialog model
				throw new IllegalArgumentException("The file provided is not a valid dialog definition file");
			org.w3c.dom.Element designElement = (org.w3c.dom.Element)designList.item(0);
			UIModel uiModel = new UIModel(dialogModel, designElement);
			renderedModel = new RenderedModel(uiModel);
			renderedModel.addListener(this);
			controller = new BasicController(brandManager, (RenderedCanvas)renderedModel.listRenderedCanvases().get(0));
			controller.setContainer(this);
			controller.setResourceMap(resourceMap);
			canvasFrame = DesignElementActionManager.getDefault().getDefaultTheme().createCanvasFrame(((UICanvas)renderedModel.getUIModel().listUICanvases().get(0)));
			if(neededConversion)
			{
				if(!MessageDialog.openConfirm(site.getShell(), "Application Upgrade Needed", "The application you are opening needs to be upgraded to your OpenVXML version before it can be edited.  The upgrade will be performed the next time you save the application.  If you would like to continue, press OK.  Otherwise press Cancel."))
				{
					Display.getCurrent().asyncExec(new Runnable()
					{
						public void run()
						{
							DialogEditor.this.getEditorSite().getWorkbenchWindow().getActivePage().closeEditor(DialogEditor.this, false);
						}
					});
					return;
				}
				else
				{
					this.setPartName(this.getPartName() + " [Will be upgraded]");
				}
			}
			site.getActionBars().setGlobalActionHandler(ActionFactory.PRINT.getId(), new Action("Print")
			{

				@Override
                public void run()
                {
					Shell workbenchShell = Display.getCurrent().getActiveShell();
					PrintDialog pd = new PrintDialog(workbenchShell);
					pd.setStartPage(1);
					pd.setEndPage(renderedModel.getUIModel().listUICanvases().size());
					PrinterData printerData = pd.open();
					if(printerData != null)
					{
						Printer printer = new Printer(printerData);
						printer.startJob("Print Callflow");
						List canvases = renderedModel.listRenderedCanvases();
						for(int i = printerData.startPage - 1; i < Math.min(printerData.endPage, canvases.size()); i++)
						{
							RenderedCanvas canvas = (RenderedCanvas)canvases.get(i);
							printer.startPage();
							Point dpi = printer.getDPI();
							float scaleX = (float)dpi.x / 96f;
							float scaleY = (float)dpi.y / 96f;
							Rectangle clientArea = printer.getClientArea();
							int printerOrientation = clientArea.width > clientArea.height ? 2 : 1;
							GC gc = new GC(printer);
							Transform transform = new Transform(printer);
							if(scaleX != 1 || scaleY != 1)
							{
								transform.scale(scaleX, scaleY);
							}
							if(canvas.getUICanvas().getOrientation() != printerOrientation)
							{
								transform.translate(canvas.getUICanvas().getHeight() / 2, canvas.getUICanvas().getHeight() / 2);
								transform.rotate(90f);
								transform.translate(-1f * canvas.getUICanvas().getHeight() / 2, -1f * canvas.getUICanvas().getHeight() / 2);
							}
							gc.setTransform(transform);
							gc.setLineWidth(1);
							canvas.paintCanvas(gc, resourceMap, Theme.RENDER_FLAG_PRINTING);
							transform.dispose();
							gc.dispose();
							printer.endPage();
						}
						printer.endJob();
						printer.dispose();
					}
                }
				
			});
		}
		catch(Exception ex)
		{
			throw new PartInitException("Unable to read file", ex);
		}
	}
	public Object getAdapter(Class adapter)
	{
		if(adapter.equals(IResource.class))
			return  ((FileEditorInput)getEditorInput()).getFile();
		return super.getAdapter(adapter);
	}
	
	public void createPartControl(Composite parent)
	{
		parent.addDisposeListener(new DisposeListener()
		{
			public void widgetDisposed(DisposeEvent e)
			{
				List values = new ArrayList();
				values.addAll(resourceMap.values());
				for(int i = 0; i < values.size(); i++)
				{
					if(values.get(i) instanceof Resource)
						((Resource)values.get(i)).dispose();
				}
				resourceMap.clear();
			}
		});

		GC gc = new GC(parent);
		renderedModel.initializeGraphics(gc, resourceMap);
		gc.dispose();
		parent.setLayout(new FillLayout());
		ScrolledComposite sc = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		sc.getVerticalBar().setIncrement(30);
		sc.getVerticalBar().setPageIncrement(275);
		sc.getHorizontalBar().setIncrement(30);
		sc.getHorizontalBar().setPageIncrement(275);
		final Composite canvasFrameComp = new Composite(sc, SWT.DOUBLE_BUFFERED | SWT.NO_BACKGROUND);
		sc.setContent(canvasFrameComp);
		canvasFrameComp.setLayout(new FormLayout());
		canvasFrameComp.addControlListener(new ControlListener()
		{
			public void controlMoved(ControlEvent e)
			{
			}

			public void controlResized(ControlEvent e)
			{
				canvasFrame.setBounds(canvasFrameComp.getBounds());
			}
		});
		canvasFrameComp.addPaintListener(new PaintListener()
		{
			public void paintControl(PaintEvent e)
			{
				Map resourceMap = new HashMap();
				canvasFrame.renderFrame(e.gc, 0, 0, resourceMap);
			}
		});
		RenderedCanvas rc = (RenderedCanvas)renderedModel.listRenderedCanvases().get(0);
		sc.setMinSize(rc.getUICanvas().getWidth() + canvasFrame.getInsets().x + canvasFrame.getInsets().width, rc.getUICanvas().getHeight() + canvasFrame.getInsets().y + canvasFrame.getInsets().height);
		sc.setExpandVertical(true);
		sc.setExpandHorizontal(true);
		canvas = new org.eclipse.swt.widgets.Canvas(canvasFrameComp, SWT.DOUBLE_BUFFERED);
		canvas.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		FormData fd = new FormData();
		fd.left = new FormAttachment(50, -1 * (rc.getUICanvas().getWidth()) / 2);
		fd.top = new FormAttachment(50, -1 * (rc.getUICanvas().getHeight()) / 2);
		fd.right = new FormAttachment(50, (rc.getUICanvas().getWidth()) / 2);
		fd.bottom = new FormAttachment(50, (rc.getUICanvas().getHeight()) / 2);
		canvas.setLayoutData(fd);
//		canvas.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_DARK_BLUE));
		controller.setControl(canvas);
		canvas.addPaintListener(controller);
		canvas.addMouseListener(controller);
		canvas.addMouseMoveListener(controller);
		canvas.addMouseTrackListener(controller);
		canvas.addKeyListener(controller);
		controller.addListener(this);
		hookContextMenu();
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				DialogEditor.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(canvas);
		canvas.setMenu(menu);
//		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void fillContextMenu(IMenuManager manager)
	{
		controller.fillContextMenu(manager);
	}

	public boolean isDirty()
	{
		return dirty;
	}

	public void doSave(IProgressMonitor monitor)
	{
		FileEditorInput fileInput = (FileEditorInput)getEditorInput();
		try
		{
			//build document contents
			DocumentBuilderFactory factory =
				DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.getDOMImplementation().createDocument(null, "dialog-definition", null);
			org.w3c.dom.Element rootElement = document.getDocumentElement();
			renderedModel.getUIModel().getModel().storeModel(rootElement);
			renderedModel.getUIModel().storeUIModel(rootElement);
			
			//write document to file
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			TransformerFactory transfactory = TransformerFactory.newInstance();
			Transformer t = transfactory.newTransformer();
			t.setOutputProperty(OutputKeys.METHOD, "xml");
			t.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			t.setOutputProperty(OutputKeys.INDENT, "yes");
			t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			t.transform(new DOMSource(document), new XMLWriter(baos).toXMLResult());
			fileInput.getFile().setContents(new ByteArrayInputStream(baos.toByteArray()), true, true, null);
			this.dirty = false;
			this.firePropertyChange(PROP_DIRTY);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			MessageDialog.openError(Display.getDefault().getActiveShell(), "Error Saving", "An error occured while saving.\n\n" + ex.getMessage());
		}
	}
	
	public boolean isSaveAsAllowed()
	{
		return false;
	}

	public void doSaveAs()
	{
	}
	
	public void setFocus()
	{
	}

	public void graphicUpdate(int x, int y, int width, int height, boolean inProgress)
	{
		GC gc = new GC(canvas);
		controller.paintCanvas(gc);
		gc.dispose();
		//canvas.redraw(x, y, width, height, false);
	}

	public void renderedModelChanged(RenderedModel renderedModel)
	{
		dirty = true;
		this.firePropertyChange(PROP_DIRTY);
	}

	public void selectionChanged(SelectionStructure selection)
	{
		// TODO Auto-generated method stub
		
	}
}
*/