/**
 * 
 */
package org.eclipse.vtp.desktop.model.core.internal.branding;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.vtp.desktop.model.core.IWorkflowProject;
import org.eclipse.vtp.desktop.model.core.branding.IBrand;
import org.eclipse.vtp.desktop.model.core.configuration.ConfigurationContext;

/**
 * @author trip
 *
 */
public class BrandContext implements ConfigurationContext
{
	public static final String CONTEXT_ID = "org.eclipse.vtp.desktop.model.core.brandcontext";
	private static final String CONTEXT_NAME = "Brands";
	private IWorkflowProject project = null;

	/**
	 * 
	 */
	public BrandContext()
	{
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.configuration.ConfigurationContext#getId()
	 */
	public String getId()
	{
		return CONTEXT_ID;
	}
	
	public String getName()
	{
		return CONTEXT_NAME;
	}
	
	public void setProject(IWorkflowProject project)
	{
		this.project = project;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.configuration.ConfigurationContext#getLabel(java.lang.Object)
	 */
	public String getLabel(Object obj)
	{
		if(obj instanceof IBrand)
		{
			IBrand brand = (IBrand)obj;
			IBrand parent = brand.getParent();
			int indent = 0;
			while(parent != null)
			{
				indent++;
				parent = parent.getParent();
			}
			StringBuilder builder = new StringBuilder();
			for(int i = 0; i < indent; i++)
			{
				builder.append("   ");
			}
			builder.append(brand.getName());
			return builder.toString();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.desktop.model.core.configuration.ConfigurationContext#getValues()
	 */
	public List<Object> getValues()
	{
		List<Object> ret = new ArrayList<Object>();
		IBrand defaultBrand = project.getBrandManager().getDefaultBrand();
		ret.add(defaultBrand);
		traverseBrands(defaultBrand, ret);
		return ret;
	}

	private void traverseBrands(IBrand parent, List<Object> brands)
	{
		for(IBrand child : parent.getChildBrands())
		{
			brands.add(child);
			traverseBrands(child, brands);
		}
	}

	public boolean setConfigurationContext(Map<String, Object> values)
	{
		return false;
	}
}
