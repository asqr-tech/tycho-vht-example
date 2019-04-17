package org.eclipse.vtp.desktop.model.core.design;

import java.util.List;

import org.eclipse.vtp.desktop.model.core.IDesignDocument;

public interface IDesign
{
	public String getDesignId();
	
	public void setName(String name);
	
	public String getName();
	
	public void addDesignElement(IDesignElement designElement);
	
	public IDesignElement getDesignElement(String id);
	
	public List<IDesignElement> getDesignElements();
	
	public void removeDesignElement(IDesignElement designElement);
	
	public IDesignConnector createDesignConnector(IDesignElement source, IDesignElement destination);
	
	public IDesignConnector getDesignConnector(String id);
	
	public List<IDesignConnector> getDesignConnectors();
	
	public void removeDesignConnector(IDesignConnector desingConnector);
	
	public PaperSize getPaperSize();

	public int getOrientation();

	public int getWidth();
	
	public int getHeight();
	
	public void setOrientation(int orientation);
	
	public void setPaperSize(PaperSize paperSize);
	
	public IDesignDocument getDocument();
	
	public void addListener(ModelListener listener);

	public void removeListener(ModelListener listener);
	
	public List<Variable> getVariablesFor(IDesignElement designElement);
	
	public List<Variable> getVariablesFor(IDesignElement designElement, boolean localOnly);
	
	public List<Variable> getVariablesFrom(IDesignElement designElement, String exit);
	
	public List<Variable> getVariablesFrom(IDesignElement designElement, String exit, boolean localOnly);

}
