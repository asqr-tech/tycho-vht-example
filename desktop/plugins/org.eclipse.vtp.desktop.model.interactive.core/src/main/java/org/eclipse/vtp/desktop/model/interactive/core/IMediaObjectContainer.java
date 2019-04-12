/**
 * 
 */
package org.eclipse.vtp.desktop.model.interactive.core;

import java.util.List;

/**
 * @author trip
 *
 */
public interface IMediaObjectContainer extends IMediaObject
{
	/**
	 * @return A list containing this container's children
	 */
	public List<IMediaObject> getChildren();

}
