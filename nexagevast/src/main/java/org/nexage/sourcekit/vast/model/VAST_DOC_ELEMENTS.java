//
//  VAST_DOC_ELEMENTS.java
//
//  Copyright (c) 2014 Nexage. All rights reserved.
//


package org.nexage.sourcekit.vast.model;

public enum VAST_DOC_ELEMENTS {

	vastVersion ("2.0"),
	vasts ("VASTS"),
	vastAdTagURI ("VASTAdTagURI"),
	vastVersionAttribute ("version");

	private String value;

	private VAST_DOC_ELEMENTS(String value) {
		this.value = value;

	}

	public String getValue() {
		return value;
	}

}
