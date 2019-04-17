package org.eclipse.vtp.modules.webservice.ui.automata;

public interface Acceptor
{
	public boolean accept(GraphContext context, Object obj);
}
