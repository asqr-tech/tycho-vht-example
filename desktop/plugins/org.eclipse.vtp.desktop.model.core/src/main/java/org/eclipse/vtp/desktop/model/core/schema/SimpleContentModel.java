package org.eclipse.vtp.desktop.model.core.schema;

public class SimpleContentModel extends ContentModel
{
	private SimpleType contentType = null;

	public SimpleContentModel()
	{
		super();
	}

	public SimpleType getContentType()
	{
		return contentType;
	}
	
	public void setContentType(SimpleType contentType)
	{
		this.contentType = contentType;
	}
}
