//
//  VASTModelPostValidator.java
//
//  Copyright (c) 2014 Nexage. All rights reserved.
//

package org.nexage.sourcekit.vast.processor;

import java.util.List;

import org.nexage.sourcekit.util.VASTLog;
import org.nexage.sourcekit.vast.model.VASTMediaFile;
import org.nexage.sourcekit.vast.model.VASTModel;

import android.text.TextUtils;

public class VASTModelPostValidator {
    
    private static final String TAG = "VASTModelPostValidator";
    


    // This method tries to make sure that there is at least 1 Media file to 
    // be used for VASTActivity. Also, if the boolean validateModel is true, it will
    // do additional validations which includes "at least 1 impression tracking url's is required'
    // If any of the above fails, it returns false. The false indicates that you can stop proceeding
    // further to display this on the MediaPlayer.
    
    public static boolean validate(VASTModel model, VASTMediaPicker mediaPicker) {
        VASTLog.d(TAG, "validate");

        if (!validateModel(model)) {
            VASTLog.d(TAG, "Validator returns: not valid (invalid model)");
            return false;
        }

        boolean isValid = false;
        
        // Must have a MediaPicker to choose one of the MediaFile element from XML
        if (mediaPicker != null) {
            List<VASTMediaFile> mediaFiles = model.getMediaFiles();
			VASTMediaFile mediaFile = mediaPicker.pickVideo(mediaFiles);
			
			if (mediaFile != null) {
				String url = mediaFile.getValue();
				if (!TextUtils.isEmpty(url)) {
					isValid = true;
					// Let's set this value inside VASTModel so that it can be
					// accessed from VASTPlayer
					model.setPickedMediaFileURL(url);
					VASTLog.d(TAG,
							"mediaPicker selected mediaFile with URL " + url);
				}
			}

        }
        else {
            VASTLog.w(TAG, "mediaPicker: We don't have a compatible media file to play.");
        }
        
        VASTLog.d(TAG, "Validator returns: " + (isValid?"valid":"not valid (no media file)"));
        
        return isValid;
    }
    

    private static boolean validateModel(VASTModel model) {
        VASTLog.d(TAG, "validateModel");
        boolean isValid = true;
        
        // There should be at least one impression.
        List<String> impressions = model.getImpressions();
        if (impressions == null || impressions.size() == 0) {
        	isValid = false;
        }
        
        // There must be at least one VASTMediaFile object       
        List<VASTMediaFile> mediaFiles = model.getMediaFiles();
        if (mediaFiles == null || mediaFiles.size() == 0) {
        	VASTLog.d(TAG, "Validator error: mediaFile list invalid");
        	isValid = false;
        }
              
		return isValid;
    }

}
