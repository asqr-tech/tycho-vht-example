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
package org.eclipse.vtp.desktop.model.interactive.voice.internal.helpers;

import org.eclipse.vtp.desktop.model.interactive.voice.internal.AudioFile;

/**
 * This utility class offers a center mechanism for storeing
 * and retrieving the alternate text associated with media
 * files.
 *
 * @author Trip Gilman
 * @version 2.0
 */
//TODO review this class for removal or reimplementation
public class AlternateTextManager
{
	/**
	 * The shared instance of this class.
	 */
	private static AlternateTextManager instance = new AlternateTextManager();

	/**
	 * Provides a cache of already loaded alternate text mapping files.
	 */
//	private WeakHashMap descriptorMap = new WeakHashMap();

	/**
	 * Creates a new <code>AlternateTextManager</code>.
	 */
	public AlternateTextManager()
	{
		super();
	}

	/**
	 * @return The singleton instance of this class
	 */
	public static AlternateTextManager getInstance()
	{
		return instance;
	}

	/**
	 * Retrieves the alternate text associated with the given
	 * audio file.  The value will be pulled from the cache
	 * if possible, otherwise the corresponding mapping file
	 * will be loaded and cached.
	 *
	 * @param audioFile The audio file with the alternate text
	 * @return The alternate text associated with the file or
	 * the empty string if no alternate text is assigned
	 */
	public String getAlternateText(AudioFile audioFile)
	{
/*		IVoiceToolsResource r = audioFile.getParent();

		while(!(r instanceof MediaFilesFolder))
		{
			r = r.getParent();
		}

		MediaFilesFolder mff = (MediaFilesFolder)r;
		IFile f = mff.getAlternateTextFile();
		String id = mff.getObjectId().intern();

		synchronized(id)
		{
			Object obj = null;

			synchronized(descriptorMap)
			{
				obj = descriptorMap.get(id);
			}

			if(obj == null)
			{
				try
				{
					obj = MediaXML.loadFiles(new StreamSource(f.getContents()));

					synchronized(descriptorMap)
					{
						descriptorMap.put(id, obj);
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}

			if(obj != null)
			{
				MediaFiles mediaFiles = (MediaFiles)obj;
				MediaFile mediaFile =
					(MediaFile)mediaFiles.find(audioFile.getMediaPath());

				return (mediaFile == null) ? "" : mediaFile.getAlternateText();
			}
		}
*/
		return null;
	}

	/**
	 * Assigns the given alternate text to the given audio file.  The
	 * value is stored in the mapping file and the cache is also
	 * updated.
	 *
	 * @param audioFile The audio file that will be associated with the text
	 * @param text The alternate text to associate with the file
	 */
	public void setAlternateText(AudioFile audioFile, String text)
	{
/*		IVoiceToolsResource r = audioFile.getParent();

		while(!(r instanceof MediaFilesFolder))
		{
			r = r.getParent();
		}

		MediaFilesFolder mff = (MediaFilesFolder)r;
		IFile f = mff.getAlternateTextFile();
		String id = mff.getObjectId().intern();

		synchronized(id)
		{
			Object obj = null;

			synchronized(descriptorMap)
			{
				descriptorMap.get(id);
			}

			if(obj == null)
			{
				try
				{
					obj = MediaXML.loadFiles(new StreamSource(f.getContents()));

					synchronized(descriptorMap)
					{
						descriptorMap.put(id, obj);
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}

			if(obj != null)
			{
				MediaFiles mediaFiles = (MediaFiles)obj;
				String mediaPath = audioFile.getMediaPath();
				MediaFile mediaFile = (MediaFile)mediaFiles.find(mediaPath);

				if(mediaFile == null)
				{
					mediaFile = new MediaFile(audioFile.getName());

					MediaContainer container = mediaFiles;

					for(StringTokenizer st =
							new StringTokenizer(mediaPath, "/\\");
							st.hasMoreTokens();)
					{
						String token = st.nextToken();

						if(st.hasMoreElements())
						{
							MediaDirectory dir =
								(MediaDirectory)container.get(token);

							if(dir == null)
							{
								dir = new MediaDirectory(token);
								container.add(dir);
							}

							container = dir;
						}
					}

					container.add(mediaFile);
				}

				mediaFile.setAlternateText(text);

				try
				{
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					MediaXML.saveFiles(mediaFiles, new StreamResult(baos));
					f.setContents(new ByteArrayInputStream(baos.toByteArray()),
						false, false, null);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
*/	}
}
