package org.eclipse.vtp.modules.webservice.ui.properties;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class RowColorPainter implements PaintListener
{
	private Color accentColor = null;

	public RowColorPainter(Color accentColor)
	{
		super();
		this.accentColor = accentColor;
	}

	@Override
	public void paintControl(PaintEvent e)
	{
		Object obj = e.getSource();
		if(obj instanceof Composite) //only composite is supported
		{
			Composite container = (Composite)obj;
			if(container.getLayout() instanceof GridLayout) //only GridLayout is supported
			{
				GridLayout layout = (GridLayout)container.getLayout();
				Control[] children = container.getChildren();
				for(Control control : children)
				{
					if(control.getLayoutData() instanceof GridData)
					{
						if(((GridData)control.getLayoutData()).verticalSpan > 1) //vertical spans not supported
						{
							System.err.println("Vertical spans not supported");
							return;
						}
					}
				}
				List<Integer> rowHeights = new ArrayList<Integer>();
				int curX = 0;
				int rowTop = Integer.MAX_VALUE;
				int rowBottom = 0;
				for(Control control : children)
				{
					Point location = control.getLocation();
					Point size = control.getSize();
					if(location.x < curX) //hit the next row
					{
						curX = location.x;
						rowHeights.add(rowBottom - rowTop);
						rowTop = Integer.MAX_VALUE;
						rowBottom = 0;
					}
					if(location.y < rowTop) //new top
					{
						rowTop = location.y;
					}
					if(location.y + size.y > rowBottom) //new bottom
					{
						rowBottom = location.y + size.y;
					}
				}
				if(rowBottom - rowTop > 0)
					rowHeights.add(rowBottom - rowTop);
				int curY = layout.marginHeight + layout.marginTop - (layout.verticalSpacing / 2);
				GC g = e.gc;
				Color oldBackground = g.getBackground();
				g.setBackground(accentColor);
				int remainder = rowHeights.size() % 2;
				for(int i = 0; i < rowHeights.size(); i++)
				{
					int totalHeight = rowHeights.get(i) + layout.verticalSpacing;
					if((i + 1) % 2 == remainder)
					{
						g.fillRectangle(layout.marginWidth + layout.marginLeft, curY, container.getSize().x - layout.marginWidth - layout.marginLeft - layout.marginWidth - layout.marginRight, totalHeight);
					}
					curY += totalHeight;
				}
				g.setBackground(oldBackground);
			}
		}
	}

}
