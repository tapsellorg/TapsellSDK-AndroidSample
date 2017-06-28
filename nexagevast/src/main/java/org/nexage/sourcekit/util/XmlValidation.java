//
//  XmlValidation.java
//
//  Copyright (c) 2014 Nexage. All rights reserved.
//

package org.nexage.sourcekit.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import mf.javax.xml.transform.Source;
import mf.javax.xml.transform.stream.StreamSource;
import mf.javax.xml.validation.Schema;
import mf.javax.xml.validation.SchemaFactory;
import mf.javax.xml.validation.Validator;
import mf.org.apache.xerces.jaxp.validation.XMLSchemaFactory;

public class XmlValidation {

	private static String TAG = "XmlTools";

	public static boolean validate(InputStream schemaStream, String xml) {
		VASTLog.i(TAG, "Beginning XSD validation.");
		SchemaFactory factory = new XMLSchemaFactory();
		Source schemaSource = new StreamSource(schemaStream);
		Source xmlSource = new StreamSource(new ByteArrayInputStream(xml.getBytes()));
		Schema schema;
        try {
            schema = factory.newSchema(schemaSource);
            Validator validator = schema.newValidator();
            validator.validate(xmlSource);
        } catch (Exception e) {
        	VASTLog.e(TAG, e.getMessage(), e);
            return false;
        }
        VASTLog.i(TAG, "Completed XSD validation..");
		return true;
	}

}
