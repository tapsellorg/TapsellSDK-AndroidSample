//
//  XmlTools.java
//
//  Copyright (c) 2014 Nexage. All rights reserved.
//

package org.nexage.sourcekit.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class XmlTools {
	
	private static String TAG = "XmlTools";
	
	public static void logXmlDocument(Document doc) {
		VASTLog.d(TAG, "logXmlDocument");
		try {
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer
					.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(
					"{http://xml.apache.org/xslt}indent-amount", "4");

			StringWriter sw = new StringWriter();
			transformer.transform(new DOMSource(doc), new StreamResult(sw));

			VASTLog.d(TAG, sw.toString());

		} catch (Exception e) {
			VASTLog.e(TAG, e.getMessage(), e);
;
		}
	}

	public static String xmlDocumentToString(Document doc) {
		String xml = null;
		VASTLog.d(TAG, "xmlDocumentToString");
		try {
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer
					.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(
					"{http://xml.apache.org/xslt}indent-amount", "4");

			StringWriter sw = new StringWriter();
			transformer.transform(new DOMSource(doc), new StreamResult(sw));

			xml = sw.toString();

		} catch (Exception e) {
			VASTLog.e(TAG, e.getMessage(), e);
		}

		return xml;
	}

	public static String xmlDocumentToString(Node node) {
		String xml = null;
		VASTLog.d(TAG, "xmlDocumentToString");
		try {
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer
					.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(
					"{http://xml.apache.org/xslt}indent-amount", "4");

			StringWriter sw = new StringWriter();
			transformer.transform(new DOMSource(node), new StreamResult(sw));

			xml = sw.toString();

		} catch (Exception e) {
			VASTLog.e(TAG, e.getMessage(), e);
		}

		return xml;
	}
	public static Document stringToDocument(String doc) {
		VASTLog.d(TAG, "stringToDocument");
	
		DocumentBuilder db;
		Document document = null;
		try {
			db = DocumentBuilderFactory.newInstance().newDocumentBuilder();

			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(doc));

			document = db.parse(is);

		} catch (Exception e) {
			VASTLog.e(TAG, e.getMessage(), e);
		}
		return document;

	}

	public static String stringFromStream(InputStream inputStream)
			throws IOException {
		VASTLog.d(TAG, "stringFromStream");
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int length = 0;

		while ((length = inputStream.read(buffer)) != -1) {
			baos.write(buffer, 0, length);
		}

		byte[] bytes = baos.toByteArray();

		return new String(bytes, "UTF-8");
	}

	public static String getElementValue(Node node) {
		
		NodeList childNodes = node.getChildNodes();
		Node child;
		String value = null;
		CharacterData cd;

		for (int childIndex = 0; childIndex < childNodes.getLength(); childIndex++) {
			child = childNodes.item(childIndex);
			// value = child.getNodeValue().trim();
			cd = (CharacterData) child;
			value = cd.getData().trim();

			if (value.length() == 0) {
				// this node was whitespace
				continue;
			}
		
			VASTLog.v(TAG, "getElementValue: " + value);
			return value;

		}
		return value;
	}

}
