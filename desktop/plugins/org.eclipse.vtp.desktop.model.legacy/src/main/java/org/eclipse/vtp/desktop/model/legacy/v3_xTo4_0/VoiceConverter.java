package org.eclipse.vtp.desktop.model.legacy.v3_xTo4_0;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.vtp.desktop.model.interactive.voice.natures.VoiceProjectNature;
import org.eclipse.vtp.desktop.projects.voice.builder.VoicePersonaNature;
import org.eclipse.vtp.framework.util.XMLWriter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class VoiceConverter
{

	public VoiceConverter()
	{
	}

	public void convertVoice(IProject project)
	{
		try
		{
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = builderFactory.newDocumentBuilder();
			if(project.hasNature(VoicePersonaNature.NATURE_ID))
			{
				//Step 1. Update .config with new attribute names.
				IFile configFile = project.getFile(".config");
				InputStream in = configFile.getContents();
				Document document = builder.parse(in);
				in.close();
				Element rootElement = document.getDocumentElement();
				rootElement.removeAttribute("name");
				rootElement.removeAttribute("domain");
				rootElement.removeAttribute("language");
				rootElement.setAttribute("language-pack-id", rootElement.getAttribute("language-pack"));
				rootElement.removeAttribute("language-pack");
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				TransformerFactory transfactory = TransformerFactory.newInstance();
				Transformer t = transfactory.newTransformer();
				t.setOutputProperty(OutputKeys.METHOD, "xml");
				t.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
				t.setOutputProperty(OutputKeys.INDENT, "yes");
				t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
				t.transform(new DOMSource(document), new XMLWriter(baos).toXMLResult());
				configFile.setContents(new ByteArrayInputStream(baos.toByteArray()), true, true, null);
				IProjectDescription desc = project.getDescription();
				desc.setNatureIds(new String[] {VoiceProjectNature.NATURE_ID});
				project.setDescription(desc, null);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
}
