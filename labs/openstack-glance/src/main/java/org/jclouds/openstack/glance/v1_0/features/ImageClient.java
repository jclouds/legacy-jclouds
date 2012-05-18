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
package org.jclouds.openstack.glance.v1_0.features;

import java.io.InputStream;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.openstack.glance.v1_0.domain.Image;
import org.jclouds.openstack.glance.v1_0.domain.ImageDetails;

/**
 * Image Services
 * 
 * @see ImageAsyncClient
 * @author Adrian Cole
 * @see <a href="http://glance.openstack.org/glanceapi.html">api doc</a>
 */
@Timeout(duration = 180, timeUnit = TimeUnit.SECONDS)
public interface ImageClient {
   /**
    * Returns a set of brief metadata about images
    */
   Set<Image> list();
   
   /**
    * Returns a set of detailed metadata about images
    */
   Set<ImageDetails> listInDetail();
   
   /**
    * Return metadata about an image with id
    */
   @Nullable
   ImageDetails show(String id);

   /**
    * Return image data for image with id 
    */
   @Nullable
   InputStream getAsStream(String id);
   
// POST /images -- Store image data and return metadata about the
// newly-stored image
// PUT /images/<ID> -- Update image metadata and/or upload image
// data for a previously-reserved image
// DELETE /images/<ID> -- Delete the image with id <ID>
}
