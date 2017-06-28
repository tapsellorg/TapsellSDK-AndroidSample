//
//  MediaPicker.java
//
//  Created by Harsha Herur on 12/4/13.
//  Copyright (c) 2014 Nexage. All rights reserved.
//

package org.nexage.sourcekit.vast.processor;

import java.util.List;

import org.nexage.sourcekit.vast.model.VASTMediaFile;

public interface VASTMediaPicker {
	
	VASTMediaFile pickVideo(List<VASTMediaFile> list);

}
