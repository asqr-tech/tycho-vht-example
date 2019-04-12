package org.eclipse.vtp.desktop.model.core.schema;

public class ExtendedComplexContentModel extends DerivedComplexContentModel
{
	public ExtendedComplexContentModel(ComplexType superType)
	{
		super(superType);
	}
	
	public ElementGroup getLocalElementGroup()
	{
		return super.getElementGroup();
	}
	
	@Override
	public ElementGroup getElementGroup()
	{
		ElementGroup sequence = new ElementGroup(getSuperType().getOwnerSchema(), ElementGroup.SEQUENCE);
		sequence.addElementObject(getSuperTypeContentModel().getElementGroup());
		sequence.addElementObject(super.getElementGroup());
		return sequence;
	}

	@Override
	public void setLocalMixedContent(boolean mixedContent)
	{
		if(mixedContent)
			super.setLocalMixedContent(mixedContent);
		else
			if(getSuperTypeContentModel().isMixedContent())
				throw new IllegalArgumentException("Cannot reduce mixed content using extension");
	}
}
