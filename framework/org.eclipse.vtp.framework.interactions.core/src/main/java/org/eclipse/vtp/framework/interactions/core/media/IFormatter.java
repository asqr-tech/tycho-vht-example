/*--------------------------------------------------------------------------
 * Copyright (c) 2004, 2006-2007 OpenMethods, LLC
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Trip Gilman (OpenMethods), Lonnie G. Pryor (OpenMethods)
 *    - initial API and implementation
 -------------------------------------------------------------------------*/
package org.eclipse.vtp.framework.interactions.core.media;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.List;

/**
 * This interface outlines the functions responsible for transforming dynamic
 * information into a language specific representation.  Implementations of this
 * interface will receive a piece of data, such as a date, through the
 * corresponding function and produce a set of
 * <code>org.eclipse.vtp.framework.interactions.core.media.Content</code>
 * objects that contain the transformed information, e.g. a TextContent with
 * the text "Monday, March 31st, 2008".
 * 
 * @author trip
 *
 */
public interface IFormatter
{
	/**
	 * @return The language code supported by this formatter, e.g. en-US.
	 */
	public String getLanguageCode();
	
	/**
	 * @return The readable name of the language supported by this formatter,
	 * e.g. English.
	 */
	public String getLanguageName();
	
	/**
	 * Transforms the supplied date information into a language specific
	 * sequence of elements following the given definition using the provided
	 * resource manager to locate file resources.<br>
	 * <br>
	 * If the definition is not recognized or invalid, the default definition
	 * should be used.  This event may be logged but should never produce an
	 * exception.
	 *
	 * @param date The date to translate to text.
	 * @param formatDefinition The provided specialized format to use during
	 * transformation.
	 * @param formatOptions TODO
	 * @param resourceManager Provides access to the available file resources.
	 * @return A list of <code>org.eclipse.vtp.framework.interactions.core.media.Content</code>
	 * objects that contain the formatted elements of the provided date.
	 */
	List<Content> formatDate(Date date, String formatDefinition, String formatOptions, IResourceManager resourceManager);

	/**
	 * Transforms the supplied date information into a language specific
	 * sequence of elements following the given definition using the provided
	 * resource manager to locate file resources.<br>
	 * <br>
	 * If the definition is not recognized or invalid, the default definition
	 * should be used.  This event may be logged but should never produce an
	 * exception.
	 *
	 * @param date The date to translate to text.
	 * @param formatDefinition The provided specialized format to use during
	 * transformation.
	 * @param formatOptions TODO
	 * @param resourceManager Provides access to the available file resources.
	 * @return A list of <code>org.eclipse.vtp.framework.interactions.core.media.Content</code>
	 * objects that contain the formatted elements of the provided date.
	 */
	List<Content> formatDate(Calendar date, String formatDefinition, String formatOptions, IResourceManager resourceManager);

	/**
	 * Transforms the supplied string of digits into a language specific
	 * sequence of elements following the given definition using the provided
	 * resource manager to locate file resources.<br>
	 * <br>
	 * If the definition is not recognized or invalid, the default definition
	 * should be used.  This event may be logged but should never produce an
	 * exception.
	 *
	 * @param digits The digits to translate to text.
	 * @param formatDefinition The provided specialized format to use during
	 * transformation.
	 * @param formatOptions TODO
	 * @param resourceManager Provides access to the available file resources.
	 * @return A list of <code>org.eclipse.vtp.framework.interactions.core.media.Content</code>
	 * objects that contain the formatted elements of the provided sequence of
	 * digits.
	 */
	List<Content> formatDigits(String digits, String formatDefinition, String formatOptions, IResourceManager resourceManager);

	/**
	 * Transforms the supplied monetary amount into a language specific
	 * sequence of elements following the given definition using the provided
	 * resource manager to locate file resources.<br>
	 * <br>
	 * If the definition is not recognized or invalid, the default definition
	 * should be used.  This event may be logged but should never produce an
	 * exception.
	 *
	 * @param money The amount to translate to text.
	 * @param currency The currency the amount is in.
	 * @param formatDefinition The provided specialized format to use during
	 * transformation.
	 * @param formatOptions TODO
	 * @param resourceManager Provides access to the available file resources.
	 * @return A list of <code>org.eclipse.vtp.framework.interactions.core.media.Content</code>
	 * objects that contain the formatted elements of the provided monetary
	 * value.
	 */
	List<Content> formatMoney(BigDecimal money, Currency currency, String formatDefinition, String formatOptions, IResourceManager resourceManager);

	/**
	 * Transforms the supplied number into a language specific sequence of
	 * elements following the given definition using the provided resource
	 * manager to locate file resources.<br>
	 * <br>
	 * If the definition is not recognized or invalid, the default definition
	 * should be used.  This event may be logged but should never produce an
	 * exception.
	 *
	 * @param number The number to translate to text.
	 * @param formatDefinition The provided specialized format to use during
	 * transformation.
	 * @param resourceManager Provides access to the available file resources.
	 * @return A list of <code>org.eclipse.vtp.framework.interactions.core.media.Content</code>
	 * objects that contain the formatted elements of the provided number.
	 */
	List<Content> formatNumber(int number, String formatDefinition, String formatOptions, IResourceManager resourceManager);

	/**
	 * Transforms the supplied ordinal value into a language specific
	 * sequence of elements following the given definition using the provided
	 * resource manager to locate file resources.<br>
	 * <br>
	 * If the definition is not recognized or invalid, the default definition
	 * should be used.  This event may be logged but should never produce an
	 * exception.
	 *
	 * @param ordinal The ordinal to translate to text.
	 * @param formatDefinition The provided specialized format to use during
	 * transformation.
	 * @param resourceManager Provides access to the available file resources.
	 * @return A list of <code>org.eclipse.vtp.framework.interactions.core.media.Content</code>
	 * objects that contain the formatted elements of the provided ordinal
	 * value.
	 */
	List<Content> formatOrdinal(int ordinal, String formatDefinition, String formatOptions, IResourceManager resourceManager);

	/**
	 * Transforms the supplied string of letters into a language specific
	 * sequence of elements following the given definition using the provided
	 * resource manager to locate file resources.<br>
	 * <br>
	 * If the definition is not recognized or invalid, the default definition
	 * should be used.  This event may be logged but should never produce an
	 * exception.
	 *
	 * @param letter The letter to translate.
	 * @param formatDefinition The provided specialized format to use during
	 * transformation.
	 * @param resourceManager Provides access to the available file resources.
	 * @return A list of <code>org.eclipse.vtp.framework.interactions.core.media.Content</code>
	 * objects that contain the formatted elements of the provided string of
	 * letters.
	 */
	List<Content> formatLetters(String text, String formatDefinition, String formatOptions, IResourceManager resourceManager);
	
	/**
	 * Returns the <code>List</code> of <code>String</code>s containing the
	 * readable names of any built-in specializations available for given
	 * formattable content.  Not all formattable content will have built-in
	 * specializations.<br>
	 * <br>
	 * A phone number would be a good example of a specialization that could be
	 * created for the <code>DigitsContent</code> type of content.  There are
	 * some common rules around how a phone number is read back in contrast to
	 * just a string of numbers.
	 * 
	 * @param formattable The type of content to find specializations for.
	 * @return A list of strings or an empty list if no specializations are
	 * available.
	 */
	List<String> getDefaultFormats(FormattableContent formattable);
	
	/**
	 * Returns the string representation of the specialization of the given
	 * name and for the given formattable content type.  This name/definition
	 * mechanism exists to provide facilities for formatters that wish to allow
	 * user defined formats.<br>
	 * <br>
	 * For example, a formatter may allow the user to specify how they want the
	 * content to be formatted using a mark up language similar to the printf
	 * family of functions in C and Java.
	 * 
	 * @param formattable The type of content to retrieve the definition for.
	 * @param formatName The name of the definition.
	 * @return The format definition.
	 */
	String getDefaultFormatDefintion(FormattableContent formattable, String formatName);
}
