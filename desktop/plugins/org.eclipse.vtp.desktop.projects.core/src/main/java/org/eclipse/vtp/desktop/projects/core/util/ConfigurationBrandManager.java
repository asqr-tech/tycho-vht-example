/**
 * 
 */
package org.eclipse.vtp.desktop.projects.core.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.vtp.desktop.model.core.branding.BrandManager;
import org.eclipse.vtp.desktop.model.core.branding.IBrand;
import org.eclipse.vtp.desktop.model.core.internal.branding.Brand;
import org.eclipse.vtp.desktop.model.core.internal.branding.DefaultBrandManager;

/**
 * @author trip
 *
 */
public class ConfigurationBrandManager extends DefaultBrandManager
{

	/**
	 * 
	 */
	public ConfigurationBrandManager(BrandManager sourceManager)
	{
		super();
		IBrand sourceDefault = sourceManager.getDefaultBrand();
		Brand copyDefault = new Brand(sourceDefault.getId(), sourceDefault.getName());
		this.setDefaultBrand(copyDefault);
		copyBrands(sourceDefault, copyDefault);
	}

	private void copyBrands(IBrand sourceBrand, Brand copyBrand)
	{
		List<IBrand> sourceChildren = sourceBrand.getChildBrands();
		for(IBrand sourceChild : sourceChildren)
		{
			Brand copyChild = new Brand(sourceChild.getId(), sourceChild.getName());
			copyChild.setParent(copyBrand);
			copyBrands(sourceChild, copyChild);
		}
	}
	
	public void saveTo(BrandManager destinationManager)
	{
		saveTo(destinationManager, false);
	}
	
	public void saveTo(BrandManager destinationManager, boolean overwrite)
	{
		Brand destinationDefault = (Brand)destinationManager.getDefaultBrand();
		Brand copyDefault = (Brand)getDefaultBrand();
		if(overwrite)
			((DefaultBrandManager)destinationManager).setDefaultBrand(copyDefault);
		else
			mergeBrand(copyDefault, destinationDefault);
	}
	
	private void mergeBrand(Brand copyBrand, Brand destinationBrand)
	{
		Map<String, Brand> copyBrandIndex = new HashMap<String, Brand>();
		for(IBrand copyChild : copyBrand.getChildBrands())
		{
			copyBrandIndex.put(copyChild.getId(), (Brand)copyChild);
		}
		Map<String, Brand> destinationBrandIndex = new HashMap<String, Brand>();
		for(IBrand destinationChild : destinationBrand.getChildBrands())
		{
			destinationBrandIndex.put(destinationChild.getId(), (Brand)destinationChild);
		}
		for(IBrand copyChild : copyBrand.getChildBrands())
		{
			Brand destinationChild = destinationBrandIndex.get(copyChild.getId());
			if(destinationChild == null) //brand has been added
			{
				copyChild.setParent(destinationBrand);
			}
			else //no change at this level, recurse to children
			{
				mergeBrand((Brand)copyChild, destinationChild);
			}
		}
		for(IBrand destinationChild : destinationBrand.getChildBrands())
		{
			Brand copyChild = copyBrandIndex.get(destinationChild.getId());
			if(copyChild == null) //brand has been removed
			{
				destinationChild.delete();
			}
		}
	}
}
