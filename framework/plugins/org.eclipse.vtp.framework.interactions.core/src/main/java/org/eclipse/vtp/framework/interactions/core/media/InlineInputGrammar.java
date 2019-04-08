/**
 * 
 */
package org.eclipse.vtp.framework.interactions.core.media;

import org.w3c.dom.Element;

/**
 * @author trip
 *
 */
public abstract class InlineInputGrammar extends InputGrammar
{

	public InlineInputGrammar()
	{
		super();
	}

	public InlineInputGrammar(Element element)
	{
		super(element);
	}

	public abstract String getGrammarText();
}
