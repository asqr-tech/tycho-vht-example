package org.eclipse.vtp.desktop.model.core.schema;

public class RestrictedComplexContentModel extends DerivedComplexContentModel
{

	public RestrictedComplexContentModel(ComplexType superType)
	{
		super(superType);
	}
	
	@Override
	public void setLocalMixedContent(boolean mixedContent)
	{
		if(!mixedContent)
			super.setLocalMixedContent(mixedContent);
		else
			if(!getSuperTypeContentModel().isMixedContent())
				throw new IllegalArgumentException("Cannot add mixed content using restriction");
	}

}
