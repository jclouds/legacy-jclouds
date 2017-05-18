/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.openstack.nova.functions;

import javax.annotation.Resource;

import org.jclouds.http.HttpResponse;
import org.jclouds.logging.Logger;
import org.jclouds.openstack.nova.domain.Image;
import org.jclouds.openstack.nova.domain.ImageStatus;

import com.google.common.base.Function;

/**
 * @author Marek Kasztelnik
 * @see http
 *      ://docs.openstack.org/api/openstack-compute/1.1/content/Create_Image-
 *      d1e4655.html
 */
public class ParseImageCreationResponseFromHeaders implements
		Function<HttpResponse, Image> {

	@Resource
	protected Logger logger = Logger.NULL;

	/*
	 * (non-Javadoc)
	 * @see com.google.common.base.Function#apply(java.lang.Object)
	 */
	@Override
	public Image apply(HttpResponse input) {
		String newImageLocation = input.getFirstHeaderOrNull("Location");

		logger.debug("new image location ", newImageLocation);

		Image img = new Image();
		img.setStatus(ImageStatus.FAILED);

		if (newImageLocation != null) {
			String[] parts = newImageLocation.split("/");
			try {
				img.setId(Integer.parseInt(parts[parts.length - 1]));
				img.setStatus(ImageStatus.UNKNOWN);
			} catch (NumberFormatException e) {
				// wrong location format
			}
		}

		return img;
	}

}
