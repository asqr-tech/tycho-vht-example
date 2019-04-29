package org.eclipse.vtp.desktop.model.core.schema;

public class DerivedComplexContentModel extends ComplexContentModel
{
	private ComplexType superType = null;
	boolean overridesMixedContent = false;


	public DerivedComplexContentModel(ComplexType superType)
	{
		super(false);
		this.superType = superType;
	}

	public ComplexType getSuperType()
	{
		return superType;
	}
	
	public ComplexContentModel getSuperTypeContentModel()
	{
		return (ComplexContentModel)superType.getContentModel();
	}

	public boolean overridesMixedContent()
	{
		return overridesMixedContent;
	}
	
	public boolean isLocalMixedContent()
	{
		return super.isMixedContent();
	}
	
	public void setLocalMixedContent(boolean mixedContent)
	{
		overridesMixedContent = true;
		setMixedContent(mixedContent);
	}

	@Override
	public boolean isMixedContent()
	{
		if(!overridesMixedContent)
			return getSuperTypeContentModel().isMixedContent();
		return super.isMixedContent();
	}

}
