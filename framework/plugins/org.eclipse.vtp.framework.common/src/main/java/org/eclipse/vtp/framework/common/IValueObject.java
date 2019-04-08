package org.eclipse.vtp.framework.common;

public interface IValueObject extends IDataObject
{
	/**
	 * Sets the value of this data object to the specified value.
	 * 
	 * <p>
	 * This method will attempt to coerce the supplied value into a Java
	 * {@link String} object.
	 * </p>
	 * 
	 * @param value The value to assign to this data object.
	 * @return False if the supplied value cannot be coerced into a Java
	 *         {@link String}.
	 * @throws IllegalStateException If this object is read-only.
	 */
	boolean setValue(Object value) throws IllegalStateException;
}
