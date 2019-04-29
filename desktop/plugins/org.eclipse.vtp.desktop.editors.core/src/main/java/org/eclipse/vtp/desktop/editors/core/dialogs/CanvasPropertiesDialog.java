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
package org.eclipse.vtp.desktop.editors.core.dialogs;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.vtp.desktop.editors.core.model.RenderedModel;
import org.eclipse.vtp.desktop.model.core.design.IDesignConstants;
import org.eclipse.vtp.desktop.model.core.design.PaperSize;
import org.eclipse.vtp.desktop.model.core.design.PaperSizeManager;

public class CanvasPropertiesDialog extends Dialog
{
	/** The canvas the properties of which will be displayed */
 	RenderedModel canvas = null;
 	/** A text field used for displaying and changing the name of the canvas */
 	Text canvasName = null;
 	/** A combo box used to select the paper size of the canvas 
 	 * @see org.eclipse.vtp.desktop.editors.core.model.PaperSize
 	 */
	Combo paperSizeCombo = null;
	/** The list of available paper sizes
	 * @see org.eclipse.vtp.desktop.editors.core.model.PaperSize
	 */
	List<PaperSize> paperSizes = PaperSizeManager.getDefault().getPaperSizes();
	/** A radio button used to select a portrait orientation of the canvas */
	Button portraitButton = null;
	/** A radio button used to select a landscape orientation of the canvas */
	Button landscapeButton = null;
	
	/**
	 * Creates a new dialog on which to display the properties of a canvas
	 * @param parentShell
	 */
	public CanvasPropertiesDialog(Shell parentShell)
	{
		super(parentShell);
	}

	/**
	 * Create a new dialog on which to display the properties of a canvas
	 * @param parentShell 
	 */
	public CanvasPropertiesDialog(IShellProvider parentShell)
	{
		super(parentShell);
	}
	
	/**
	 * Sets the canvas
	 * @param canvas - the canvas the properties of which will be displayed
	 */
	public void setRenderedCanvas(RenderedModel canvas)
	{
		this.canvas = canvas;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createDialogArea(Composite parent)
	{
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayoutData(new GridData(GridData.FILL_BOTH));
		comp.setLayout(new GridLayout(2, false));
		Label canvasNameLabel = new Label(comp, SWT.NONE);
		canvasNameLabel.setText("Canvas Name:");
		GridData gd = new GridData();
		canvasNameLabel.setLayoutData(gd);
		canvasName = new Text(comp, SWT.SINGLE | SWT.BORDER | SWT.FLAT);
		canvasName.setText(canvas.getUIModel().getName());
		gd = new GridData(GridData.FILL_HORIZONTAL);
		canvasName.setLayoutData(gd);
		Group paperSizeGroup = new Group(comp, SWT.NONE);
		paperSizeGroup.setText("Canvas Size");
		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		paperSizeGroup.setLayoutData(gd);
		paperSizeGroup.setLayout(new FormLayout());
		Label paperSizeLabel = new Label(paperSizeGroup, SWT.NONE);
		paperSizeLabel.setText("Paper Size:");
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, 0);
		fd.top = new FormAttachment(0, 0);
		paperSizeLabel.setLayoutData(fd);
		paperSizeCombo = new Combo(paperSizeGroup, SWT.READ_ONLY | SWT.DROP_DOWN);
		for(int i = 0; i < paperSizes.size(); i++)
		{
			PaperSize ps = paperSizes.get(i);
			paperSizeCombo.add(ps.getName());
			if(ps.equals(canvas.getUIModel().getPaperSize()))
				paperSizeCombo.select(i);
		}
		fd = new FormData();
		fd.left = new FormAttachment(paperSizeLabel, 0);
		fd.top = new FormAttachment(0, 0);
		paperSizeCombo.setLayoutData(fd);
		portraitButton = new Button(paperSizeGroup, SWT.RADIO | SWT.FLAT);
		portraitButton.setText("Portrait");
		if(canvas.getUIModel().getOrientation() == IDesignConstants.PORTRAIT)
			portraitButton.setSelection(true);
		fd = new FormData();
		fd.left = new FormAttachment(0, 0);
		fd.top = new FormAttachment(paperSizeLabel, 15);
		portraitButton.setLayoutData(fd);
		landscapeButton = new Button(paperSizeGroup, SWT.RADIO | SWT.FLAT);
		landscapeButton.setText("Landscape");
		if(canvas.getUIModel().getOrientation() == IDesignConstants.LANDSCAPE)
			landscapeButton.setSelection(true);
		fd = new FormData();
		fd.left = new FormAttachment(portraitButton, 0);
		fd.top = new FormAttachment(paperSizeLabel, 15);
		landscapeButton.setLayoutData(fd);
		return comp;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	protected void okPressed()
	{
		try
        {
			canvas.getUIModel().setName(canvasName.getText());
	        canvas.getUIModel().setOrientation(portraitButton.getSelection() ? IDesignConstants.PORTRAIT : IDesignConstants.LANDSCAPE);
	        canvas.getUIModel().setPaperSize(paperSizes.get(paperSizeCombo.getSelectionIndex()));
        }
        catch(RuntimeException e)
        {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }
		super.okPressed();
	}

}
