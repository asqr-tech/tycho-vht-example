package org.eclipse.vtp.modules.webservice.ui.widgets;

import org.eclipse.vtp.desktop.model.core.design.ObjectDefinition;

public interface ObjectDefinitionFilter
{
	public boolean isApplicable(ObjectDefinition definition);
}
