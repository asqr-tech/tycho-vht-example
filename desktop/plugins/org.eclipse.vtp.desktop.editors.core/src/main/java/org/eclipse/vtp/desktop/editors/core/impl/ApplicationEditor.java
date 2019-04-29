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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.operations.RedoActionHandler;
import org.eclipse.ui.operations.UndoActionHandler;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.vtp.desktop.editors.core.Activator;
import org.eclipse.vtp.desktop.editors.core.controller.BasicController;
import org.eclipse.vtp.desktop.editors.core.controller.ControllerListener;
import org.eclipse.vtp.desktop.editors.core.controller.ModelNavigationListener;
import org.eclipse.vtp.desktop.editors.core.model.RenderedModel;
import org.eclipse.vtp.desktop.editors.core.model.RenderedModelListener;
import org.eclipse.vtp.desktop.editors.core.model.SelectionStructure;
import org.eclipse.vtp.desktop.editors.core.model.UndoSystem;
import org.eclipse.vtp.desktop.editors.themes.core.CanvasFrame;
import org.eclipse.vtp.desktop.editors.themes.core.ComponentFrame;
import org.eclipse.vtp.desktop.editors.themes.core.ConnectorFrame;
import org.eclipse.vtp.desktop.editors.themes.core.ElementFrame;
import org.eclipse.vtp.desktop.editors.themes.core.Theme;
import org.eclipse.vtp.desktop.editors.themes.core.ThemeManager;
import org.eclipse.vtp.desktop.model.core.IDesignDocument;
import org.eclipse.vtp.desktop.model.core.IDesignDocumentListener;
import org.eclipse.vtp.desktop.model.core.IWorkflowProject;
import org.eclipse.vtp.desktop.model.core.WorkflowCore;
import org.eclipse.vtp.desktop.model.core.design.IDesign;
import org.eclipse.vtp.desktop.model.core.design.IDesignComponent;
import org.eclipse.vtp.desktop.model.core.design.IDesignConstants;
import org.eclipse.vtp.desktop.model.core.design.IDesignViewer;
import org.eclipse.vtp.desktop.model.core.internal.DesignDocument;
import org.eclipse.vtp.desktop.model.core.internal.DesignWriter;
import org.eclipse.vtp.desktop.model.core.internal.IDesignFilter;
import org.eclipse.vtp.desktop.model.core.internal.PartialDesignDocument;
import org.eclipse.vtp.desktop.model.core.internal.design.Design;
import org.eclipse.vtp.desktop.views.pallet.PalletFocusListener;
import org.eclipse.vtp.desktop.views.pallet.PalletFocusProvider;
import org.eclipse.vtp.framework.util.XMLWriter;
import org.w3c.dom.Document;

public class ApplicationEditor extends EditorPart implements ControllerListener, ModelNavigationListener,
		PalletFocusProvider, IDesignDocumentListener, IDesignViewer, UndoSystem {
	boolean dirty = false;
	List<CanvasRecord> designs = new ArrayList<CanvasRecord>();
	Map<String, Object> resourceMap = new HashMap<String, Object>();
	CanvasRecord currentCanvas = null;
	IWorkflowProject project = null;
	IDesignDocument designDocument = null;
	CTabFolder canvasTabs = null;
	private List<PalletFocusListener> palletListeners = new LinkedList<PalletFocusListener>();
	IOperationHistory operationHistory = null;
	private UndoActionHandler undoActionHandler;
	private RedoActionHandler redoActionHandler;
	private IPartListener partListener;

	public ApplicationEditor() {
		super();
	}

	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		IWorkbench workbench = site.getWorkbenchWindow().getWorkbench();
		operationHistory = workbench.getOperationSupport().getOperationHistory();
//		ResourcesPlugin.getWorkspace().addResourceChangeListener(this, IResourceChangeEvent.POST_CHANGE);
		// required callbacks for the init function. TG
		setSite(site);
		setInput(input);

		// load document contents
		FileEditorInput fileInput = (FileEditorInput) input;
		try {
			project = WorkflowCore.getDefault().getWorkflowModel()
					.convertToWorkflowProject(fileInput.getFile().getProject());
			this.setPartName(fileInput.getName());
			designDocument = (IDesignDocument) WorkflowCore.getDefault().getWorkflowModel()
					.convertToWorkflowResource(fileInput.getFile());
			designDocument.becomeWorkingCopy();

			designDocument.addDocumentListener(this);
			RenderedModel mainDesign = new RenderedModel(designDocument.getMainDesign());
			CanvasRecord mainRecord = new CanvasRecord(mainDesign);
			mainDesign.setUndoSystem(this);
			mainDesign.setUndoContext(mainRecord.undoContext);
			currentCanvas = mainRecord;
			designs.add(mainRecord);
			for (IDesign dialogDesign : designDocument.getDialogDesigns()) {
				RenderedModel dialogModel = new RenderedModel(dialogDesign);
				CanvasRecord dialogRecord = new CanvasRecord(dialogModel);
				dialogModel.setUndoSystem(this);
				dialogModel.setUndoContext(dialogRecord.undoContext);
				designs.add(dialogRecord);
			}
//			if(neededConversion)
//			{
//				if(!MessageDialog.openConfirm(site.getShell(), "Application Upgrade Needed", "The application you are opening needs to be upgraded to your OpenVXML version before it can be edited.  The upgrade will be performed the next time you save the application.  If you would like to continue, press OK.  Otherwise press Cancel."))
//				{
//					Display.getCurrent().asyncExec(new Runnable()
//					{
//						public void run()
//						{
//							ApplicationEditor.this.getEditorSite().getWorkbenchWindow().getActivePage().closeEditor(ApplicationEditor.this, false);
//						}
//					});
//					return;
//				}
//				else
//				{
//					this.setPartName(this.getPartName() + " [Will be upgraded]");
//				}
//			}
			undoActionHandler = new UndoActionHandler(site, currentCanvas.undoContext);
			undoActionHandler.setPruneHistory(true);

			// create the redo action handler
			redoActionHandler = new RedoActionHandler(site, currentCanvas.undoContext);
			redoActionHandler.setPruneHistory(true);
			partListener = new IPartListener() {

				@Override
				public void partActivated(IWorkbenchPart part) {
					System.out.println("part activated: " + part);
					if (part != ApplicationEditor.this) {
						System.out.println("part activated but not me: " + part);
						return;
					}
					IEditorSite site = (IEditorSite) getSite();
					site.getActionBars().setGlobalActionHandler(ActionFactory.UNDO.getId(), undoActionHandler);
					site.getActionBars().setGlobalActionHandler(ActionFactory.REDO.getId(), redoActionHandler);
					site.getActionBars().setGlobalActionHandler(ActionFactory.COPY.getId(), new Action("Copy") {
						public void run() {
							final SelectionStructure selection = currentCanvas.renderedCanvas.getSelection();
							if (selection.getPrimarySelection() instanceof ConnectorFrame)
								return;
							copySelectionToClipboard(selection);
						}
					});
					site.getActionBars().setGlobalActionHandler(ActionFactory.CUT.getId(), new Action("Cut") {
						public void run() {
							final SelectionStructure selection = currentCanvas.renderedCanvas.getSelection();
							if (selection.getPrimarySelection() instanceof ConnectorFrame)
								return;
							copySelectionToClipboard(selection);
							currentCanvas.renderedCanvas.deleteSelectedItems();
						}
					});
					site.getActionBars().setGlobalActionHandler(ActionFactory.PASTE.getId(), new Action("Paste") {
						public void run() {
							Clipboard clipboard = new Clipboard(canvasTabs.getDisplay());
							String text = (String) clipboard.getContents(TextTransfer.getInstance());
							if (text != null) {
								try {
									DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
									factory.setNamespaceAware(true);
									DocumentBuilder builder = factory.newDocumentBuilder();
									Document document = builder.parse(new ByteArrayInputStream(text.getBytes()));
									PartialDesignDocument pdd = new PartialDesignDocument(designDocument, null,
											document);
									PasteOperation po = new PasteOperation(pdd, currentCanvas);
									po.addContext(currentCanvas.undoContext);
									operationHistory.execute(po, null, null);
								} catch (Exception ex) {
									ex.printStackTrace();
								}
							}
						}
					});
					site.getActionBars().setGlobalActionHandler(ActionFactory.PRINT.getId(), new Action("Print") {

						@Override
						public void run() {
							Shell workbenchShell = Display.getCurrent().getActiveShell();
							PrintDialog pd = new PrintDialog(workbenchShell);
							pd.setStartPage(1);
							pd.setEndPage(designs.size());
							PrinterData printerData = pd.open();
							if (printerData != null) {
								Printer printer = new Printer(printerData);
								printer.startJob("Print Callflow");
								for (int i = printerData.startPage - 1; i < Math.min(printerData.endPage,
										designs.size()); i++) {
									CanvasRecord cr = designs.get(i);
									RenderedModel renderedModel = cr.renderedCanvas;
									printer.startPage();
									Point dpi = printer.getDPI();
									System.out.println("printer dpi: " + dpi.x + ", " + dpi.y);
									float scaleX = (float) dpi.x / 96f;
									float scaleY = (float) dpi.y / 96f;
									Rectangle clientArea = printer.getClientArea();
									System.out.println("Client Area: " + clientArea);
									int printerOrientation = clientArea.width > clientArea.height ? 2 : 1;
									GC gc = new GC(printer);
									Transform transform = new Transform(printer);
									if (scaleX != 1 || scaleY != 1) {
										transform.scale(scaleX, scaleY);
									}
									if (renderedModel.getUIModel().getOrientation() != printerOrientation) {
										transform.translate(renderedModel.getUIModel().getHeight() / 2,
												renderedModel.getUIModel().getHeight() / 2);
										transform.rotate(90f);
										transform.translate(-1f * renderedModel.getUIModel().getHeight() / 2,
												-1f * renderedModel.getUIModel().getHeight() / 2);
									}
									gc.setTransform(transform);
									gc.setLineWidth(1);
									renderedModel.paintCanvas(gc, resourceMap, Theme.RENDER_FLAG_PRINTING);
									transform.dispose();
									gc.dispose();
									printer.endPage();
								}
								printer.endJob();
								printer.dispose();
							}
						}

					});
					site.getActionBars().updateActionBars();
				}

				@Override
				public void partBroughtToTop(IWorkbenchPart part) {
					System.out.println("part brought to top: " + part);
				}

				@Override
				public void partClosed(IWorkbenchPart part) {
					System.out.println("part closed: " + part);
				}

				@Override
				public void partDeactivated(IWorkbenchPart part) {
					System.out.println("part deactivated: " + part);
				}

				@Override
				public void partOpened(IWorkbenchPart part) {
					System.out.println("part opened: " + part);
				}

			};
			site.getPage().addPartListener(partListener);
		} catch (Exception ex) {
			throw new PartInitException("Unable to read file", ex);
		}
	}

	public void dispose() {
		getSite().getPage().removePartListener(partListener);
		super.dispose();
	}

	private void copySelectionToClipboard(final SelectionStructure selection) {
		IDesignFilter selectionFilter = new IDesignFilter() {
			List<IDesignComponent> components = new ArrayList<IDesignComponent>();
			{
				for (ComponentFrame cf : selection.getSelectedItems()) {
					components.add(cf.getDesignComponent());
				}
				for (ComponentFrame cf : selection.getSecondarySelectedItems()) {
					components.add(cf.getDesignComponent());
				}
			}

			public boolean matches(IDesignComponent component) {
				for (IDesignComponent dc : components) {
					if (dc.getId().equals(component.getId()))
						return true;
				}
				return false;
			}
		};
		try {
			DesignWriter writer = new DesignWriter();
			// build document contents
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.getDOMImplementation().createDocument(null, "design-fragment", null);
			org.w3c.dom.Element rootElement = document.getDocumentElement();
			rootElement.setAttribute("xml-version", "4.0.0");
			if (!ApplicationEditor.this.currentCanvas.renderedCanvas.getUIModel()
					.equals(designDocument.getMainDesign())) {
				rootElement.setAttribute("dialog-only", "true");
				writer.writeDesign(rootElement, (Design) currentCanvas.renderedCanvas.getUIModel(), selectionFilter);
			} else {
				writer.writeDesign(rootElement, (Design) designDocument.getMainDesign(), selectionFilter);

				org.w3c.dom.Element dialogsElement = rootElement.getOwnerDocument().createElement("dialogs");
				rootElement.appendChild(dialogsElement);
				for (IDesign dialogDesign : designDocument.getDialogDesigns()) {
					List<ComponentFrame> items = selection.getSelectedItems();
					for (ComponentFrame cf : items) {
						if (cf.getDesignComponent().getId().equals(dialogDesign.getDesignId())) {
							writer.writeDesign(dialogsElement, (Design) dialogDesign);
							break;
						}
					}
				}
			}

			// write document to file
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			TransformerFactory transfactory = TransformerFactory.newInstance();
			Transformer t = transfactory.newTransformer();
			t.setOutputProperty(OutputKeys.METHOD, "xml");
			t.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			t.setOutputProperty(OutputKeys.INDENT, "yes");
			t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			t.transform(new DOMSource(document), new XMLWriter(baos).toXMLResult());
			Clipboard clipboard = new Clipboard(ApplicationEditor.this.canvasTabs.getDisplay());
			clipboard.setContents(new Object[] { baos.toString() }, new Transfer[] { TextTransfer.getInstance() });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void createPartControl(Composite parent) {
		parent.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				List<Object> values = new ArrayList<Object>();
				values.addAll(resourceMap.values());
				for (int i = 0; i < values.size(); i++) {
					if (values.get(i) instanceof Resource)
						((Resource) values.get(i)).dispose();
				}
				resourceMap.clear();
			}
		});

		FileEditorInput fileInput = (FileEditorInput) getEditorInput();
		File workingLocation = fileInput.getFile().getProject().getWorkingLocation("org.eclipse.vtp.desktop.model.core")
				.toFile();
		File iconPath = new File(workingLocation, fileInput.getFile().getProjectRelativePath().toString() + "/");
		System.out.println(iconPath);
		iconPath.mkdirs();
		GC gc = new GC(parent);
		canvasTabs = new CTabFolder(parent, SWT.BOTTOM);
		for (CanvasRecord cr : designs) {
			RenderedModel renderedModel = cr.renderedCanvas;
			renderedModel.initializeGraphics(gc, resourceMap);
			if (renderedModel.getUIModel().getDocument()
					.getDesignThumbnail(renderedModel.getUIModel().getDesignId()) == null) {
				try {
					float scale;
					if (cr.renderedCanvas.getUIModel().getOrientation() == IDesignConstants.LANDSCAPE)
						scale = 100f / cr.renderedCanvas.getUIModel().getWidth();
					else
						scale = 100f / cr.renderedCanvas.getUIModel().getHeight();
					Image icon = new Image(Display.getCurrent(), 100, 100);
					GC gc2 = new GC(icon);
					Transform transform = new Transform(Display.getCurrent());
					transform.scale(scale, scale);
//					if(renderedModel.getUIModel().getOrientation() != printerOrientation)
//					{
//						transform.translate(renderedModel.getUIModel().getHeight() / 2, renderedModel.getUIModel().getHeight() / 2);
//						transform.rotate(90f);
//						transform.translate(-1f * renderedModel.getUIModel().getHeight() / 2, -1f * renderedModel.getUIModel().getHeight() / 2);
//					}
					gc2.setTransform(transform);
					gc2.setLineWidth(1);
					cr.renderedCanvas.paintCanvas(gc2, resourceMap, Theme.RENDER_FLAG_NO_SELECTION);
					transform.dispose();
					gc2.dispose();
					ImageLoader imageLoader = new ImageLoader();
					imageLoader.data = new ImageData[] { icon.getImageData() };
					File iconFile = new File(iconPath, cr.renderedCanvas.getUIModel().getDesignId() + ".jpg");
					FileOutputStream fos = new FileOutputStream(iconFile);
					imageLoader.save(fos, SWT.IMAGE_JPEG);
					fos.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			CTabItem canvasTab = new CTabItem(canvasTabs, SWT.NONE);
			canvasTab.setText(renderedModel.getUIModel().getName());
			canvasTab.setControl(cr.createControls(canvasTabs));
			canvasTab.setData(cr);
		}
		canvasTabs.setSelection(0);
		gc.dispose();
		canvasTabs.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				CTabItem item = canvasTabs.getSelection();
				setCurrentCanvas((CanvasRecord) item.getData());
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		parent.setLayout(new FillLayout());
	}

	public boolean isDirty() {
		return dirty;
	}

	public void doSave(IProgressMonitor monitor) {
//		FileEditorInput fileInput = (FileEditorInput)getEditorInput();
//		File workingLocation = fileInput.getFile().getProject().getWorkingLocation("org.eclipse.vtp.desktop.model.core").toFile();
//		File iconPath = new File(workingLocation, fileInput.getFile().getProjectRelativePath().toString() + "/");
//		iconPath.mkdirs();
		try {
//			try
//			{
//				for(CanvasRecord cr : designs)
//				{
//					float scale;
//					if(cr.renderedCanvas.getUIModel().getOrientation() == IDesignConstants.LANDSCAPE)
//						scale = 40f / cr.renderedCanvas.getUIModel().getWidth();
//					else
//						scale = 40f / cr.renderedCanvas.getUIModel().getHeight();
//					Image icon = new Image(Display.getCurrent(), 40, 40);
//					GC gc = new GC(icon);
//					Transform transform = new Transform(Display.getCurrent());
//					transform.scale(scale, scale);
////				if(renderedModel.getUIModel().getOrientation() != printerOrientation)
////				{
////					transform.translate(renderedModel.getUIModel().getHeight() / 2, renderedModel.getUIModel().getHeight() / 2);
////					transform.rotate(90f);
////					transform.translate(-1f * renderedModel.getUIModel().getHeight() / 2, -1f * renderedModel.getUIModel().getHeight() / 2);
////				}
//					gc.setTransform(transform);
//					gc.setLineWidth(1);
//					cr.renderedCanvas.paintCanvas(gc, resourceMap, Theme.RENDER_FLAG_NO_SELECTION);
//					transform.dispose();
//					gc.dispose();
//					ImageLoader imageLoader = new ImageLoader();
//					imageLoader.data = new ImageData[] {icon.getImageData()};
//					File iconFile = new File(iconPath, cr.renderedCanvas.getUIModel().getDesignId() + ".jpg");
//					FileOutputStream fos = new FileOutputStream(iconFile);
//					imageLoader.save(fos, SWT.IMAGE_JPEG);
//					fos.close();
//				}
//			}
//			catch (Exception e)
//			{
//				e.printStackTrace();
//			}
			designDocument.commitWorkingCopy();
			this.dirty = false;
			this.firePropertyChange(PROP_DIRTY);
		} catch (Exception ex) {
			IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, 0,
					"Could not save file: " + designDocument.getUnderlyingFile().getFullPath(), ex);
			Activator.getDefault().getLog().log(status);
			MessageDialog.openError(Display.getDefault().getActiveShell(), "Error Saving",
					"An error occured while saving.\n\n" + ex.getMessage());
		}
	}

	public boolean isSaveAsAllowed() {
		return false;
	}

	public void doSaveAs() {
	}

	public void setFocus() {
	}

	public void graphicUpdate(int x, int y, int width, int height, boolean inProgress) {
		GC gc = new GC(currentCanvas.canvas);
		currentCanvas.controller.paintCanvas(gc);
		gc.dispose();
	}

	public void selectionChanged(SelectionStructure selection) {
	}

	public boolean supportsMultipleCanvases() {
		return true;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object getAdapter(Class adapter) {
		if (adapter.isAssignableFrom(this.getClass()))
			return this;
		if (adapter.equals(ModelNavigationListener.class))
			return this;
		if (adapter.equals(IResource.class))
			return ((FileEditorInput) getEditorInput()).getFile();
		if (adapter.equals(IDesign.class))
			return currentCanvas.renderedCanvas.getUIModel();
		return super.getAdapter(adapter);
	}

	private class CanvasRecord implements RenderedModelListener {
		RenderedModel renderedCanvas;
		CanvasFrame canvasFrame;
		Composite canvasFrameComp;
		org.eclipse.swt.widgets.Canvas canvas;
		BasicController controller;
		ScrolledComposite sc;
		String designName = null;
		IUndoContext undoContext = new CanvasUndoContext();

		public CanvasRecord(RenderedModel renderedCanvas) {
			super();
			this.renderedCanvas = renderedCanvas;
			designName = renderedCanvas.getUIModel().getName();
			renderedCanvas.addListener(this);
			controller = new BasicController(renderedCanvas);
			controller.setResourceMap(resourceMap);
			controller.setContainer(ApplicationEditor.this);
			canvasFrame = ThemeManager.getDefault().getDefaultTheme().createCanvasFrame(renderedCanvas.getUIModel());

		}

		public Control createControls(Composite parent) {
			sc = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
			sc.getVerticalBar().setIncrement(30);
			sc.getVerticalBar().setPageIncrement(275);
			sc.getHorizontalBar().setIncrement(30);
			sc.getHorizontalBar().setPageIncrement(275);
			canvasFrameComp = new Composite(sc, SWT.DOUBLE_BUFFERED | SWT.NO_BACKGROUND);
			sc.setContent(canvasFrameComp);
			canvasFrameComp.setLayout(new FormLayout());
			canvasFrameComp.addControlListener(new ControlListener() {
				public void controlMoved(ControlEvent e) {
				}

				public void controlResized(ControlEvent e) {
					canvasFrame.setBounds(canvasFrameComp.getBounds());
				}
			});
			canvasFrameComp.addPaintListener(new PaintListener() {
				public void paintControl(PaintEvent e) {
					Map<String, Object> resourceMap = new HashMap<String, Object>();
					canvasFrame.renderFrame(e.gc, 0, 0, resourceMap);
				}
			});
			int canvasWidth = renderedCanvas.getUIModel().getWidth();
			int canvasHeight = renderedCanvas.getUIModel().getHeight();
			sc.setMinSize(canvasWidth + canvasFrame.getInsets().x + canvasFrame.getInsets().width,
					canvasHeight + canvasFrame.getInsets().y + canvasFrame.getInsets().height);
			sc.setExpandVertical(true);
			sc.setExpandHorizontal(true);
			canvas = new org.eclipse.swt.widgets.Canvas(canvasFrameComp, SWT.DOUBLE_BUFFERED);
			Font nameFont = new Font(canvas.getDisplay(), "sans", 9, SWT.NORMAL);
			canvas.setFont(nameFont);
			canvas.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
			FormData fd = new FormData();
			fd.left = new FormAttachment(50, -1 * canvasWidth / 2);
			fd.top = new FormAttachment(50, -1 * canvasHeight / 2);
			fd.right = new FormAttachment(50, canvasWidth / 2);
			fd.bottom = new FormAttachment(50, canvasHeight / 2);
			canvas.setLayoutData(fd);
			controller.setControl(canvas);
			canvas.addPaintListener(controller);
			canvas.addMouseListener(controller);
			canvas.addMouseMoveListener(controller);
			canvas.addMouseTrackListener(controller);
			canvas.addKeyListener(controller);
			controller.addListener(ApplicationEditor.this);
			hookContextMenu();
			return sc;
		}

		private void hookContextMenu() {
			MenuManager menuMgr = new MenuManager("#PopupMenu");
			menuMgr.setRemoveAllWhenShown(true);
			menuMgr.addMenuListener(new IMenuListener() {
				public void menuAboutToShow(IMenuManager manager) {
					CanvasRecord.this.fillContextMenu(manager);
				}
			});
			Menu menu = menuMgr.createContextMenu(canvas);
			canvas.setMenu(menu);
		}

		private void fillContextMenu(IMenuManager manager) {
			controller.fillContextMenu(manager);
		}

//		public void dispose()
//		{
//			sc.dispose();
//		}

		public void renderedModelChanged(RenderedModel renderedCanvas) {
			if (!designName.equals(renderedCanvas.getUIModel().getName())) {
				this.designName = renderedCanvas.getUIModel().getName();
				updateTab(this);
			}
			dirty = true;
			firePropertyChange(PROP_DIRTY);
		}

		public void renderedModelFormatChanged(RenderedModel renderedCanvas) {
			int width = renderedCanvas.getUIModel().getWidth();
			int height = renderedCanvas.getUIModel().getHeight();
			sc.setMinSize(width + canvasFrame.getInsets().x + canvasFrame.getInsets().width,
					height + canvasFrame.getInsets().y + canvasFrame.getInsets().height);
			FormData fd = new FormData();
			fd.left = new FormAttachment(50, -1 * width / 2);
			fd.top = new FormAttachment(50, -1 * height / 2);
			fd.right = new FormAttachment(50, width / 2);
			fd.bottom = new FormAttachment(50, height / 2);
			canvas.setLayoutData(fd);
			sc.layout(true, true);
			canvasFrameComp.layout();
			dirty = true;
			firePropertyChange(PROP_DIRTY);
		}

		public void displayRegion(Rectangle rec) {
			int exc = (rec.width / 2) + rec.x;
			int eyc = rec.height / 2 + rec.y;
			Rectangle clientArea = sc.getClientArea();
			int caxc = (clientArea.width - clientArea.x) / 2;
			int cayc = (clientArea.height - clientArea.y) / 2;
			int x = exc - caxc;
			int y = eyc - cayc;
			if (x < 0)
				x = 0;
			if (y < 0)
				y = 0;
			sc.setOrigin(x, y);
		}

		public class CanvasUndoContext implements IUndoContext {
			public String getId() {
				return renderedCanvas.getUIModel().getDocument().getUnderlyingFile()
						+ renderedCanvas.getUIModel().getDesignId();
			}

			public String getLabel() {
				return renderedCanvas.getUIModel().getName();
			}

			public boolean matches(IUndoContext context) {
				if (context instanceof CanvasUndoContext) {
					return getId().equals(((CanvasUndoContext) context).getId());
				}
				return false;
			}
		}
	}

	public void navigateToElement(String elementId) {
		designDocument.getProject().navigateToElement(elementId);
	}

	public void displayElement(String elementId) {
		CanvasRecord cr = designs.get(0);
		for (ElementFrame elementFrame : cr.renderedCanvas.getElementFrames()) {
			if (elementFrame.getDesignElement().getId().equals(elementId)) {
				Rectangle rec = elementFrame.getBounds();
				cr.displayRegion(rec);
				break;
			}
		}
	}

	public void showDesign(String designId) {
		if (designDocument.getMainDesign().getDesignId().equals(designId)) {
			canvasTabs.setSelection(0);
		} else {
			List<IDesign> dialogDesigns = designDocument.getDialogDesigns();
			for (int i = 0; i < dialogDesigns.size(); i++) {
				IDesign dialogDesign = dialogDesigns.get(i);
				if (dialogDesign.getDesignId().equals(designId)) {
					canvasTabs.setSelection(i + 1);
					break;
				}
			}
		}
		CTabItem item = canvasTabs.getSelection();
		setCurrentCanvas((CanvasRecord) item.getData());
	}

	private void setCurrentCanvas(CanvasRecord cr) {
		currentCanvas = cr;
		undoActionHandler.setContext(currentCanvas.undoContext);
		redoActionHandler.setContext(currentCanvas.undoContext);
		currentCanvas.canvas.getDisplay().asyncExec(new Runnable() {
			public void run() {
				currentCanvas.canvas.redraw();
			}
		});
		for (PalletFocusListener focusListener : palletListeners) {
			focusListener.focusChanged(currentCanvas.renderedCanvas.getUIModel());
		}
	}

	public void addFocusListener(PalletFocusListener focusListener) {
		palletListeners.remove(focusListener);
		palletListeners.add(focusListener);
	}

	public void removeFocusListener(PalletFocusListener focusListener) {
		palletListeners.remove(focusListener);
	}

	private void updateTab(CanvasRecord cr) {
		for (CTabItem item : canvasTabs.getItems()) {
			if (item.getData().equals(cr)) {
				item.setText(cr.designName);
				return;
			}
		}
	}

	public void dialogDesignAdded(IDesignDocument designDocument, IDesign dialogDesign) {
		RenderedModel dialogModel = new RenderedModel(dialogDesign);
		dialogModel.setUndoSystem(this);
		CanvasRecord dialogRecord = new CanvasRecord(dialogModel);
		dialogModel.setUndoContext(dialogRecord.undoContext);
		designs.add(dialogRecord);
		CTabItem canvasTab = new CTabItem(canvasTabs, SWT.NONE);
		canvasTab.setText(dialogModel.getUIModel().getName());
		canvasTab.setControl(dialogRecord.createControls(canvasTabs));
		canvasTab.setData(dialogRecord);
		canvasTabs.layout(true, true);
	}

	public void dialogDesignRemoved(IDesignDocument designDocument, String dialogId) {
		for (CanvasRecord cr : designs) {
			if (cr.renderedCanvas.getUIModel().getDesignId().equals(dialogId)) {
				designs.remove(cr);
				for (CTabItem item : canvasTabs.getItems()) {
					if (item.getData() == cr) {
						item.dispose();
						break;
					}
				}
				break;
			}
		}
		canvasTabs.layout(true, true);
	}

	public class PasteOperation extends AbstractOperation {
		PartialDesignDocument pdd = null;
		CanvasRecord targetDesign = null;

		public PasteOperation(PartialDesignDocument pdd, CanvasRecord targetDesign) {
			super("Paste");
			this.pdd = pdd;
			this.targetDesign = targetDesign;
		}

		public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
			DesignDocument dd = (DesignDocument) designDocument;
			IDesign design = targetDesign.renderedCanvas.getUIModel();
			boolean doit = true;
			if (!dd.canMergeAll(design, pdd)) {
				MessageBox confirmationDialog = new MessageBox(Display.getCurrent().getActiveShell(),
						SWT.YES | SWT.NO | SWT.ICON_QUESTION);
				confirmationDialog.setMessage(
						"Not all elements can be used in the target canvas.  Would you like to paste the remaining elements?");

				int result = confirmationDialog.open();

				doit = (result == SWT.YES);
			}
			if (doit) {
				dd.merge(design, pdd);
				pdd = pdd.clone();
			}
			if (monitor != null)
				monitor.done();
			return Status.OK_STATUS;
		}

		@Override
		public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
			System.out.println("redoing paste operation");
			DesignDocument dd = (DesignDocument) designDocument;
			IDesign design = targetDesign.renderedCanvas.getUIModel();
			dd.merge(design, pdd.clone(), false);
			targetDesign.renderedCanvas.fireChange();
			if (monitor != null)
				monitor.done();
			return Status.OK_STATUS;
		}

		@Override
		public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
			System.out.println("undoing paste operation");
			targetDesign.renderedCanvas.getSelection().clear();
			DesignDocument dd = (DesignDocument) designDocument;
			IDesign design = targetDesign.renderedCanvas.getUIModel();
			dd.reverse(design, pdd.clone());
			targetDesign.renderedCanvas.fireChange();
			if (monitor != null)
				monitor.done();
			return Status.OK_STATUS;
		}

	}

	public class BitbucketUndoContext implements IUndoContext {
		public String getLabel() {
			return "BitBucket";
		}

		public boolean matches(IUndoContext context) {
			return false;
		}
	}

	@Override
	public IOperationHistory getOperationHistory() {
		return this.operationHistory;
	}

	@Override
	public void disableUndo() {
		BitbucketUndoContext context = new BitbucketUndoContext();
		undoActionHandler.setContext(context);
		redoActionHandler.setContext(context);
	}

	@Override
	public void enableUndo() {
		setCurrentCanvas(currentCanvas);
	}
}
