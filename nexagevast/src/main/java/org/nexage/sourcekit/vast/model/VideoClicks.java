//
//  VideoClicks.java
//
//  Copyright (c) 2014 Nexage. All rights reserved.
//

package org.nexage.sourcekit.vast.model;

import java.util.ArrayList;
import java.util.List;

public class VideoClicks {

	private String clickThrough;
	private List<String> clickTracking;
	private List<String> customClick;

	public String getClickThrough() {
		return clickThrough;
	}

	public void setClickThrough(String clickThrough) {
		this.clickThrough = clickThrough;
	}

	public List<String> getClickTracking() {
		if (clickTracking == null) {
			clickTracking = new ArrayList<String>();
		}
		return this.clickTracking;
	}

	public List<String> getCustomClick() {
		if (customClick == null) {
			customClick = new ArrayList<String>();
		}
		return this.customClick;
	}

	@Override
	public String toString() {
		return "VideoClicks [clickThrough=" + clickThrough
				+ ", clickTracking=[" + listToString(clickTracking)
				+ "], customClick=[" + listToString(customClick) + "] ]";
	}

	private String listToString(List<String> list) {
		StringBuffer sb = new StringBuffer();

		if (list == null) {
			return "";
		}
		for (int x = 0; x < list.size(); x++) {
			sb.append(list.get(x).toString());
		}
		return sb.toString();
	}
}
