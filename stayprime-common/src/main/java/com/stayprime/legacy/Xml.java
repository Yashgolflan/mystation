/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.stayprime.legacy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.apache.xerces.dom.DocumentImpl;
import org.apache.xml.serialize.*;

public class Xml
{
	public static Element rootElement(File file, String rootName)
	{
            Element rootElement = rootElement(file);
            if(!rootElement.getNodeName().equals(rootName))
                return null;
            return rootElement;
        }

	public static void writeRootElement(File f, Element rootElement) throws IOException {
	    writeRootElement(new FileOutputStream(f), rootElement);
	}

	public static void writeRootElement(OutputStream os, Element rootElement) throws IOException {
	    Element e = null;
	    Node n = null;
	    // Document (Xerces implementation only).
	    Document xmldoc= new DocumentImpl();
	    // Root element.
	    xmldoc.appendChild(xmldoc.importNode(rootElement, true));
	    // XERCES 1 or 2 additionnal classes.
	    OutputFormat of = new OutputFormat("XML","ISO-8859-1",true);
	    of.setIndent(1);
	    of.setIndenting(true);
	    of.setDoctype(null,null);
	    XMLSerializer serializer = new XMLSerializer(os,of);
	    // As a DOM Serializer
	    serializer.asDOMSerializer();
	    serializer.serialize( xmldoc.getDocumentElement() );
	    os.close();
	}

	public static Element rootElement(File file) {
	    try {
		return rootElement(new FileInputStream(file));
	    }
	    catch (FileNotFoundException ex) {
		throw new RuntimeException(ex);
	    }
	}

	public static Element rootElement(InputStream inputStream) {
		try
		{
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = builderFactory.newDocumentBuilder();
			Document document = builder.parse(inputStream);
			//Document document = builder.parse(url);
			Element rootElement = document.getDocumentElement();
                            //throw new RuntimeException("Could not find root node: "+rootName);
			return rootElement;
		}
		catch(Exception exception)
		{
			throw new RuntimeException(exception);
		}
		finally
		{
			if(inputStream!=null)
			{
				try
				{
					inputStream.close();
				}
				catch(Exception exception)
				{
					throw new RuntimeException(exception);
				}
			}
		}
	}

	public Xml(File file, String rootName)
	{
		this(rootElement(file,rootName));
	}

	public Xml(Element element)
	{
		this.element = element;
		this.name = element.getNodeName();
		this.content = element.getTextContent();
			//element.getNodeValue();
			//element.getChildNodes().getLength() > 0? element.getChildNodes().item(element.getChildNodes().getLength()-1).getNodeValue() : null;
		NamedNodeMap namedNodeMap = element.getAttributes();
		int n = namedNodeMap.getLength();
		for(int i=0;i<n;i++)
		{
			Node node = namedNodeMap.item(i);
			String name = node.getNodeName();
			addAttribute(name, node.getNodeValue());
		}
		NodeList nodes = element.getChildNodes();
		n = nodes.getLength();
	    for(int i=0;i<n;i++)
	    {
	    	Node node = nodes.item(i);
	    	int type = node.getNodeType();
	    	if(type==Node.ELEMENT_NODE) addChild(node.getNodeName(),new Xml((Element)node));
	    }
	}

	private void addAttribute(String name, String value)
	{
		nameAttributes.put(name,value);
	}

	private void addChild(String name, Xml child)
	{
		List<Xml> children = nameChildren.get(name);
		if(children==null)
		{
			children = new ArrayList<Xml>();
			nameChildren.put(name,children);
		}
		children.add(child);
	}

	public String name()
	{
		return name;
	}

	public String content()
	{
		return content;
	}

	public Element element()
	{
		return element;
	}

	public Xml child(String name)
	{
		List<Xml> children = children(name);
		if(children.size()!=1) //throw new RuntimeException("Could not find individual child node: "+name);
                    return null;
		return children.get(0);
	}

	public List<Xml> children(String name)
	{
		List<Xml> children = nameChildren.get(name);
		return children==null ? new ArrayList<Xml>() : children;
	}

	public String string(String name)
	{
		String value = nameAttributes.get(name);
		//if(value==null) throw new RuntimeException("Could not find attribute: "+name+", in node: "+this.name);
		return value;
	}

	public int integer(String name)
	{
		return Integer.parseInt(string(name));
	}

	private String name;
	private String content;
	private Map<String,String> nameAttributes = new HashMap<String,String>();
	private Map<String,List<Xml>> nameChildren = new HashMap<String,List<Xml>>();
	private Element element;
}