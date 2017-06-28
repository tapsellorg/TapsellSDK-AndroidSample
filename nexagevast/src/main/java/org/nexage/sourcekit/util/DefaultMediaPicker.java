//
//  DefaultMediaPicker.java
//
//  Created by Harsha Herur on 12/4/13.
//  Copyright (c) 2014 Nexage. All rights reserved.
//

package org.nexage.sourcekit.util;

import java.math.BigInteger;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.nexage.sourcekit.vast.model.VASTMediaFile;
import org.nexage.sourcekit.vast.processor.VASTMediaPicker;

import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;

public class DefaultMediaPicker implements VASTMediaPicker {
	
	private static final String TAG = "DefaultMediaPicker";
    private static final int maxPixels = 5000;

	// These are the Android supported MIME types, see http://developer.android.com/guide/appendix/media-formats.html#core (as of API 18)
	String SUPPORTED_VIDEO_TYPE_REGEX = "video/.*(?i)(mp4|3gpp|mp2t|webm|matroska)";
	
	private int deviceWidth;
	private int deviceHeight;
	private int deviceArea;
	private Context context;

	public DefaultMediaPicker(Context context)
	{
		this.context = context;
		setDeviceWidthHeight();
	}
	
	public DefaultMediaPicker(int width, int height)
	{
		setDeviceWidthHeight(width, height);
	}


	@Override
	// given a list of MediaFiles, select the most appropriate one.
	public VASTMediaFile pickVideo(List<VASTMediaFile> mediaFiles) {
		//make sure that the list of media files contains the correct attributes
		if  (mediaFiles == null || prefilterMediaFiles(mediaFiles) == 0) {
			return null;
		}			 
		Collections.sort(mediaFiles, new AreaComparator());
		VASTMediaFile mediaFile = getBestMatch(mediaFiles);
		return mediaFile;
	}
	
	/*
	 * This method filters the list of mediafiles and return the count.
	 * Validate that the media file objects contain the required attributes for the Default Media Picker processing.
	 * 
	 * 		Required attributes:
	 * 			1. type
	 * 			2. height
	 * 			3. width 
	 * 			4. url
	 */

	private int prefilterMediaFiles(List<VASTMediaFile> mediaFiles) {
		
		Iterator<VASTMediaFile> iter = mediaFiles.iterator();
		
		while (iter.hasNext()) {

			VASTMediaFile mediaFile = iter.next();

			// type attribute
			String type = mediaFile.getType();
			if (TextUtils.isEmpty(type)) {
				VASTLog.d(TAG, "Validator error: mediaFile type empty");
				iter.remove();
				continue;
			}

			// Height attribute
			BigInteger height = mediaFile.getHeight();

			if (null == height) {
				VASTLog
						.d(TAG, "Validator error: mediaFile height null");
				iter.remove();
				continue;
			} else {
				int videoHeight = height.intValue();
				if (!(0 < videoHeight && videoHeight < maxPixels)) {
					VASTLog.d(TAG,
							"Validator error: mediaFile height invalid: "
									+ videoHeight);
					iter.remove();
					continue;
				}
			}

			// width attribute
			BigInteger width = mediaFile.getWidth();
			if (null == width) {
				VASTLog.d(TAG, "Validator error: mediaFile width null");
				iter.remove();
				continue;
			} else {
				int videoWidth = width.intValue();
				if (!(0 < videoWidth && videoWidth < maxPixels)) {
					VASTLog.d(TAG,
							"Validator error: mediaFile width invalid: "
									+ videoWidth);
					iter.remove();
					continue;
				}
			}

			// mediaFile url
			String url = mediaFile.getValue();
			if (TextUtils.isEmpty(url)) {
				VASTLog.d(TAG, "Validator error: mediaFile url empty");
				iter.remove();
				continue;
			}
		}
		
		return mediaFiles.size();
	}

	
	private void setDeviceWidthHeight() {

		// get the device width and height of the device using the context
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		deviceWidth = metrics.widthPixels;
		deviceHeight = metrics.heightPixels;
		deviceArea = deviceWidth * deviceHeight;
	}
	
	private void setDeviceWidthHeight(int width, int height) {

		this.deviceWidth = width;
		this.deviceHeight = height;
		deviceArea = deviceWidth * deviceHeight;
		
	}

	private class AreaComparator implements Comparator<VASTMediaFile> {

		@Override
		public int compare(VASTMediaFile obj1, VASTMediaFile obj2) {
		   // get area of the video of the two MediaFiles
			int obj1Area = obj1.getWidth().intValue() * obj1.getHeight().intValue();
			int obj2Area = obj2.getWidth().intValue() * obj2.getHeight().intValue();
			
			// get the difference between the area of the MediaFile and the area of the screen
			int obj1Diff = Math.abs(obj1Area - deviceArea);
			int obj2Diff = Math.abs(obj2Area - deviceArea);
			 VASTLog.v(TAG, "AreaComparator: obj1:" + obj1Diff +" obj2:" + obj2Diff);
				
			// choose the MediaFile which has the lower difference in area
			if(obj1Diff < obj2Diff) {
				return -1;
			} else if(obj1Diff > obj2Diff) {
				return 1;
			} else {
				return 0;
			}
		}
		
	}
	
	private boolean isMediaFileCompatible(VASTMediaFile media) {

		// check if the MediaFile is compatible with the device.
		// further checks can be added here
		return media.getType().matches(SUPPORTED_VIDEO_TYPE_REGEX);
	}

	private VASTMediaFile getBestMatch(List<VASTMediaFile> list) {
	     VASTLog.d(TAG, "getBestMatch");
	     
		// Iterate through the sorted list and return the first compatible media.
		// If none of the media file is compatible, return null
		Iterator<VASTMediaFile> iterator = list.iterator();

		while (iterator.hasNext()) {
			VASTMediaFile media = iterator.next();
			if (isMediaFileCompatible(media)) {
				return media;
			}
		}
		return null;
	}


}
