package com.stayprime.webservice;

import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/*
 * http://www.java2s.com/Code/Java/XML/NewDocumentFromInputStream.htm
 */
public class XmlUtil {
    public static Document newDocumentFromInputStream(InputStream in) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document ret = builder.parse(new InputSource(in));
        return ret;
    }
}

