package org.eclipse.vtp.framework.interactions.voice.media;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import org.eclipse.vtp.framework.interactions.core.media.Content;
import org.eclipse.vtp.framework.interactions.core.media.FormattableContent;
import org.eclipse.vtp.framework.interactions.core.media.IFormatter;
import org.eclipse.vtp.framework.interactions.core.media.IResourceManager;
import org.eclipse.vtp.framework.interactions.core.media.TextContent;

public abstract class VoiceFormatter implements IFormatter
{

	/** Convenience member for 0 as a BigDecimal */
    protected static final BigDecimal ZERO = new BigDecimal("0");
	/** Convenience member for -1 as a BigDecimal */
    protected static final BigDecimal NEGATIVE_ONE = new BigDecimal("-1");

	public VoiceFormatter()
	{
		super();
	}

	/**
     * Convenience function to locate a requested audio resource given the
     * expected path and filename.  This function attempts the locate the file
     * using a set of file extensions.  If the file is not located, the provided
     * alternate text is wrapped in a TextContent object and returned.<br>
     * <br>
     * The file resource is located using the following audio file extension in
     * descending order:<br>
     * <table>
     *     <tr>
     *         <td>Extension</td><!--<td>Audio Type</td><td>Audio Specification</td>-->
     *     </tr>
     *     <tr>
     *         <td>.vox</td><!--<td>audio/basic</td><td>u-law, a-law<br>
     *                                                  8bit, 8khz</td>-->
     *     </tr>
     *     <tr>
     *         <td>.wav</td>
     *     </tr>
     *     <tr>
     *         <td>.au</td>
     *     </tr>
     * </table>
     * 
     * @param resourceManager Provides access to the available file resources.
     * @param path The path to the file.  This path is rooted at the voice
     * project utilizing this language formatter.
     * @param filename The name of the file minus the extension.
     * @param defaultText The default text to be used if the file is not located.
     * @return Either a AudioContent with the requested audio resource, or the
     * provided alternate text wrapped in a TextContent object.
     */
    protected Content getAudioContent(IResourceManager resourceManager, String path, String filename,
            String defaultText)
    {
    	Content ret = null;
    	if(resourceManager.isFileResource(path + filename + ".vox"))
    	{
    		AudioContent ac = new AudioContent();
    		ac.setStaticPath(path + filename + ".vox");
    		ret = ac;
    	}
    	else if(resourceManager.isFileResource(path + filename + ".wav"))
    	{
    		AudioContent ac = new AudioContent();
    		ac.setStaticPath(path + filename + ".wav");
    		ret = ac;
    	}
    	else if(resourceManager.isFileResource(path + filename + ".au"))
    	{
    		AudioContent ac = new AudioContent();
    		ac.setStaticPath(path + filename + ".au");
    		ret = ac;
    	}
    	else
    	{
    		TextContent tc = new TextContent();
    		tc.setStaticText(defaultText + " ");
    		ret = tc;
    	}
    	return ret;
    }
    
	public List<Content> formatDate(Calendar cal, String formatDefinition, String formatOptions, IResourceManager resourceManager)
	{
		return formatDate(cal.getTime(), formatDefinition, formatOptions, resourceManager);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.framework.interactions.core.media.IFormatter#getDefaultFormatDefintion(org.eclipse.vtp.framework.interactions.core.media.FormattableContent, java.lang.String)
	 */
	public String getDefaultFormatDefintion(FormattableContent formattable,
            String formatName)
    {
	    return formatName;
    }

	/* (non-Javadoc)
	 * @see org.eclipse.vtp.framework.interactions.core.media.IFormatter#getDefaultFormats(org.eclipse.vtp.framework.interactions.core.media.FormattableContent)
	 */
	public List<String> getDefaultFormats(FormattableContent formattable)
    {
	    return Collections.emptyList();
    }

    
}