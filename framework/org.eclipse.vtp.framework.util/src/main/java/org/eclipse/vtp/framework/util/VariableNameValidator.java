package org.eclipse.vtp.framework.util;

import java.util.regex.Pattern;

/**
 * This utility class provides methods for validating variable
 * names, allowing for more standardized, centralized validation.
 * 
 * @author Sam Hopkins
 */
public class VariableNameValidator
{
	/**
	 * Determines whether the variable name adheres to VTP identifier
	 * naming conventions, including a check against a list of
	 * reserved words. Currently, there are no provisions for escaped
	 * unicode characters.
	 * 
	 * @param name The variable name to validate
	 * @return Boolean 
	 */
	public static Boolean followsVtpNamingRules(String name)
	{
		if(isVtpReservedWord(name))
		{
			return false;
		}
		return(Pattern.matches("[\\p{L}_\\$][\\p{L}_\\$\\p{Digit}]*", name));
	}
	
	/**
	 * Determines whether the variable name adheres to ECMAScript
	 * identifier naming conventions, including a check against a
	 * list of reserved words. Currently, there are no provisions
	 * for escaped unicode characters.
	 * 
	 * @param name The variable name to validate
	 * @return Boolean 
	 */
	public static Boolean followsEcmaNamingRules(String name)
	{
		if(isEcmaReservedWord(name))
		{
			return false;
		}
		return(Pattern.matches("[\\p{L}_\\$][\\p{L}_\\$\\p{Digit}]*", name));
	}
	
	/**
	 * Determines whether the variable name is a VTP reserved word.
	 * 
	 * @param name The variable name to evaluate
	 * @return Boolean
	 */
	public static Boolean isVtpReservedWord(String name) 
	{
		//Include anything that is found under the Variables object.
		//LastResult isn't, but it'd look confusing in Decision modules.
		String[] keywords = {"Platform", "LastResult"};
		
		for(int b = 0; b < keywords.length; b++)
		{
			if(name.equals(keywords[b]))
			{
				return true;
			}
		}

		return false;
	}
	
	/**
	 * Determines whether the variable name is an ECMAScript reserved word.
	 * 
	 * @param name The variable name to evaluate
	 * @return Boolean
	 */
	public static Boolean isEcmaReservedWord(String name)
	{
		String[] nullLiterals = {"null"};
		
		String[] booleanLiterals ={"true", "false"};
		
		String[] keywords = {"break", "else", "new", "var",
		"case", "finally", "return", "void",
		"catch", "for", "switch", "while",
		"continue", "function", "this", "with",
		"default", "if", "throw",
		"delete", "in", "try",
		"do", "instanceof", "typeof"};
		
		String[] futureReservedWords = {"abstract", "enum", "int", "short",
		"boolean", "export", "interface", "static",
		"byte", "extends", "long", "super",
		"char", "final", "native", "synchronized",
		"class", "float", "package", "throws",
		"const", "goto", "private", "transient",
		"debugger", "implements", "protected", "volatile",
		"double", "import", "public"};
		
		for(int b = 0; b < nullLiterals.length; b++)
		{
			if(name.equals(nullLiterals[b]))
			{
				return true;
			}
		}
		for(int b = 0; b < booleanLiterals.length; b++)
		{
			if(name.equals(booleanLiterals[b]))
			{
				return true;
			}
		}
		for(int b = 0; b < keywords.length; b++)
		{
			if(name.equals(keywords[b]))
			{
				return true;
			}
		}
		for(int b = 0; b < futureReservedWords.length; b++)
		{
			if(name.equals(futureReservedWords[b]))
			{
				return true;
			}
		}
		return false;
	}
}
