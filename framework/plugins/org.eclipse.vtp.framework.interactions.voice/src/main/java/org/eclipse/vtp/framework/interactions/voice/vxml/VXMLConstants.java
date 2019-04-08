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
package org.eclipse.vtp.framework.interactions.voice.vxml;

/**
 * Constants from the VXML format.
 * 
 * @author Lonnie Pryor
 * @version 1.0
 */
public interface VXMLConstants
{
	/** The empty string. */
	String EMPTY = ""; //$NON-NLS-1$

	/** The ".gsl" file extension. */
	String FILE_EXT_GSL = ".gsl"; //$NON-NLS-1$

	/** The "all" filled mode constant. */
	String FILLED_MODE_ALL = "all"; //$NON-NLS-1$
	/** The "any" filled mode constant. */
	String FILLED_MODE_ANY = "any"; //$NON-NLS-1$

	/** The "dtmf" grammar mode constant. */
	String GRAMMAR_MODE_DTMF = "dtmf"; //$NON-NLS-1$
	/** The "voice" grammar mode constant. */
	String GRAMMAR_MODE_VOICE = "voice"; //$NON-NLS-1$

	/** The "get" method constant. */
	String METHOD_GET = "get"; //$NON-NLS-1$
	/** The "post" method constant. */
	String METHOD_POST = "post"; //$NON-NLS-1$

	/** The "application/x-gsl" MIME type constant. */
	String MIME_TYPE_GSL = "application/x-gsl"; //$NON-NLS-1$
	/** The "application/srgs+xml" MIME type constant. */
	String MIME_TYPE_SRGS = "application/srgs+xml"; //$NON-NLS-1$

	/** The "application" name constant. */
	String NAME_APPLICATION = "application"; //$NON-NLS-1$
	/** The "assign" name constant. */
	String NAME_ASSIGN = "assign"; //$NON-NLS-1$
	/** The "audio" name constant. */
	String NAME_AUDIO = "audio"; //$NON-NLS-1$
	/** The "bargein" name constant. */
	String NAME_BARGEIN = "bargein"; //$NON-NLS-1$
	/** The "beep" name constant. */
	String NAME_BEEP = "beep"; //$NON-NLS-1$
	/** The "bridge" name constant. */
	String NAME_BRIDGE = "bridge"; //$NON-NLS-1$
	/** The "block" name constant. */
	String NAME_BLOCK = "block"; //$NON-NLS-1$
	/** The "catch" name constant. */
	String NAME_CATCH = "catch"; //$NON-NLS-1$
	/** The "choice" name constant. */
	String NAME_CHOICE = "choice"; //$NON-NLS-1$
	/** The "completetimeout" name constant. */
	String NAME_COMPLETETIMEOUT = "completetimeout"; //$NON-NLS-1$
	/** The "cond" name constant. */
	String NAME_COND = "cond"; //$NON-NLS-1$
	/** The "confidencelevel" name constant. */
	String NAME_CONFIDENCELEVEL = "confidencelevel"; //$NON-NLS-1$
	/** The "count" name constant. */
	String NAME_COUNT = "count"; //$NON-NLS-1$
	/** The "dest" name constant. */
	String NAME_DEST = "dest"; //$NON-NLS-1$
	/** The "disconnect" name constant. */
	String NAME_DISCONNECT = "disconnect"; //$NON-NLS-1$
	/** The "dtmf" name constant. */
	String NAME_DTMF = "dtmf"; //$NON-NLS-1$
	/** The "dtmfterm" name constant. */
	String NAME_DTMFTERM = "dtmfterm"; //$NON-NLS-1$
	/** The "else" name constant. */
	String NAME_ELSE = "else"; //$NON-NLS-1$
	/** The "elseif" name constant. */
	String NAME_ELSEIF = "elseif"; //$NON-NLS-1$
	/** The "enctype" name constant. */
	String NAME_ENCTYPE = "enctype"; //$NON-NLS-1$
	/** The "enumerate" name constant. */
	String NAME_ENUMERATE = "enumerate"; //$NON-NLS-1$
	/** The "error" name constant. */
	String NAME_ERROR = "error"; //$NON-NLS-1$
	/** The "event" name constant. */
	String NAME_EVENT = "event"; //$NON-NLS-1$
	/** The "eventexpr" name constant. */
	String NAME_EVENTEXPR = "eventexpr"; //$NON-NLS-1$
	/** The "exit" name constant. */
	String NAME_EXIT = "exit"; //$NON-NLS-1$
	/** The "expr" name constant. */
	String NAME_EXPR = "expr"; //$NON-NLS-1$
	/** The "field" name constant. */
	String NAME_FIELD = "field"; //$NON-NLS-1$
	/** The "filled" name constant. */
	String NAME_FILLED = "filled"; //$NON-NLS-1$
	/** The "finalsilence" name constant. */
	String NAME_FINALSILENCE = "finalsilence"; //$NON-NLS-1$
	/** The "form" name constant. */
	String NAME_FORM = "form"; //$NON-NLS-1$
	/** The "goto" name constant. */
	String NAME_GOTO = "goto"; //$NON-NLS-1$
	/** The "grammar" name constant. */
	String NAME_GRAMMAR = "grammar"; //$NON-NLS-1$
	/** The "id" name constant. */
	String NAME_ID = "id"; //$NON-NLS-1$
	/** The "if" name constant. */
	String NAME_IF = "if"; //$NON-NLS-1$
	/** The "incompletetimeout" name constant. */
	String NAME_INCOMPLETETIMEOUT = "incompletetimeout"; //$NON-NLS-1$
	/** The "inputmodes" name constant. */
	String NAME_INPUTMODES = "inputmodes"; //$NON-NLS-1$
	/** The "interdigittimeout" name constant. */
	String NAME_INTERDIGITTIMEOUT = "interdigittimeout"; //$NON-NLS-1$
	/** The "item" name constant. */
	String NAME_ITEM = "item"; //$NON-NLS-1$
	/** The "mark" name constant. */
	String NAME_MARK = "mark"; //$NON-NLS-1$
	/** The "maxnbest" name constant. */
	String NAME_MAXNBEST = "maxnbest"; //$NON-NLS-1$
	/** The "maxspeechtimeout" name constant. */
	String NAME_MAXSPEECHTIMEOUT = "maxspeechtimeout"; //$NON-NLS-1$
	/** The "maxtime" name constant. */
	String NAME_MAXTIME = "maxtime"; //$NON-NLS-1$
	/** The "menu" name constant. */
	String NAME_MENU = "menu"; //$NON-NLS-1$
	/** The "method" name constant. */
	String NAME_METHOD = "method"; //$NON-NLS-1$
	/** The "mode" name constant. */
	String NAME_MODE = "mode"; //$NON-NLS-1$
	/** The "name" name constant. */
	String NAME_NAME = "name"; //$NON-NLS-1$
	/** The "namelist" name constant. */
	String NAME_NAMELIST = "namelist"; //$NON-NLS-1$
	/** The "next" name constant. */
	String NAME_NEXT = "next"; //$NON-NLS-1$
	/** The "noinput" name constant. */
	String NAME_NOINPUT = "noinput"; //$NON-NLS-1$
	/** The "nomatch" name constant. */
	String NAME_NOMATCH = "nomatch"; //$NON-NLS-1$
	/** The "one-of" name constant. */
	String NAME_ONE_OF = "one-of"; //$NON-NLS-1$
	/** The "option" name constant. */
	String NAME_OPTION = "option"; //$NON-NLS-1$
	/** The "param" name constant. */
	String NAME_PARAM = "param"; //$NON-NLS-1$
	/** The "prompt" name constant. */
	String NAME_PROMPT = "prompt"; //$NON-NLS-1$
	/** The "property" name constant. */
	String NAME_PROPERTY = "property"; //$NON-NLS-1$
	/** The "record" name constant. */
	String NAME_RECORD = "record"; //$NON-NLS-1$
	/** The "repeat" name constant. */
	String NAME_REPEAT = "repeat"; //$NON-NLS-1$
	/** The "reprompt" name constant. */
	String NAME_REPROMPT = "reprompt"; //$NON-NLS-1$
	/** The "return" name constant. */
	String NAME_RETURN = "return"; //$NON-NLS-1$
	/** The "root" name constant. */
	String NAME_ROOT = "root"; //$NON-NLS-1$
	/** The "rule" name constant. */
	String NAME_RULE = "rule"; //$NON-NLS-1$
	/** The "scope" name constant. */
	String NAME_SCOPE = "scope"; //$NON-NLS-1$
	/** The "script" name constant. */
	String NAME_SCRIPT = "script"; //$NON-NLS-1$
	/** The "sensitivity" name constant. */
	String NAME_SENSITIVITY = "sensitivity"; //$NON-NLS-1$
	/** The "speedvsaccuracy" name constant. */
	String NAME_SPEEDVSACCURACY = "speedvsaccuracy"; //$NON-NLS-1$
	/** The "src" name constant. */
	String NAME_SRC = "src"; //$NON-NLS-1$
	/** The "subdialog" name constant. */
	String NAME_SUBDIALOG = "subdialog"; //$NON-NLS-1$
	/** The "submit" name constant. */
	String NAME_SUBMIT = "submit"; //$NON-NLS-1$termchar
	/** The "termchar" name constant. */
	String NAME_TERMCHAR = "termchar"; //$NON-NLS-1$
	/** The "termtimeout" name constant. */
	String NAME_TERMTIMEOUT = "termtimeout"; //$NON-NLS-1$
	/** The "throw" name constant. */
	String NAME_THROW = "throw"; //$NON-NLS-1$
	/** The "timeout" name constant. */
	String NAME_TIMEOUT = "timeout"; //$NON-NLS-1$
	/** The "transfer" name constant. */
	String NAME_TRANSFER = "transfer"; //$NON-NLS-1$
	/** The "type" name constant. */
	String NAME_TYPE = "type"; //$NON-NLS-1$
	/** The "value" name constant. */
	String NAME_VALUE = "value"; //$NON-NLS-1$
	/** The "var" name constant. */
	String NAME_VAR = "var"; //$NON-NLS-1$
	/** The "version" name constant. */
	String NAME_VERSION = "version"; //$NON-NLS-1$
	/** The "vxml" name constant. */
	String NAME_VXML = "vxml"; //$NON-NLS-1$

	/** The name space of the VXML document type. */
	String NAMESPACE_URI_VXML = "http://www.w3.org/2001/vxml"; //$NON-NLS-1$

	/** The "xml:lang" qualified name constant. */
	String QNAME_XML_LANG = "xml:lang"; //$NON-NLS-1$

	/** The "document" scope constant. */
	String SCOPE_DOCUMENT = "document"; //$NON-NLS-1$
	/** The "dialog" scope constant. */
	String SCOPE_DIALOG = "dialog"; //$NON-NLS-1$

	/** The "CDATA" type constant. */
	String TYPE_CDATA = "CDATA"; //$NON-NLS-1$

	/** The "2.0" version constant. */
	String VERSION_2_0 = "2.0"; //$NON-NLS-1$
	/** The "2.1" version constant. */
	String VERSION_2_1 = "2.1"; //$NON-NLS-1$
}
