package org;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class ConfigurationProperty{
    String ConfigFileName;
    Document doc;

    public ConfigurationProperty(String configFileName)
    {
        try
        {
            this.ConfigFileName = configFileName;
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            this.doc = db.parse(new File(configFileName));
            this.doc.getDocumentElement().normalize();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public String GetRoot()
    {
        String rootPath = this.doc.getElementsByTagName("RootPath").item(0).getTextContent();
        return rootPath;
    }

    public String GetFilePath(String dataName, String tag)
    {
        NodeList data = this.doc.getElementsByTagName("Data");
        for (int dataInd = 0; dataInd < data.getLength(); dataInd ++)
        {
            Element dataElement = (Element) data.item(dataInd);

            if (dataElement.getAttribute("Name").equals(dataName)) 
            {
                continue;
            }

            String rootPath = this.GetRoot();
            String filePath = dataElement.getElementsByTagName(tag).item(0).getTextContent();
            return new File(rootPath, filePath).getPath();
        }
        return null;
    }

    public String GetOpenCVFilePath()
    {
        String rootPath = this.GetRoot();
        String opencvPath = this.doc.getElementsByTagName("OpenCV").item(0).getTextContent();
        return new File(rootPath, opencvPath).getPath();
    }

    public static void main(String[] args) 
    {
        ConfigurationProperty cp = new ConfigurationProperty("Config.xml");
        System.out.println(cp.GetOpenCVFilePath());
        System.out.println(cp.GetRoot());
        System.out.println(cp.GetFilePath("soccer", "Audio"));
    }
}