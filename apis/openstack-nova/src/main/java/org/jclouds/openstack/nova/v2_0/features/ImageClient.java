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
package org.jclouds.openstack.nova.v2_0.features;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.openstack.nova.v2_0.domain.Image;
import org.jclouds.openstack.v2_0.domain.Resource;

/**
 * Provides synchronous access to Images.
 * <p/>
 * 
 * @see ImageAsyncClient
 * @see <a href=
 *      "http://docs.openstack.org/api/openstack-compute/1.1/content/Servers-d1e2073.html"
 *      />
 * @author Adrian Cole
 */
@Timeout(duration = 180, timeUnit = TimeUnit.SECONDS)
public interface ImageClient {

   /**
    * List all images (IDs, names, links)
    * 
    * @return all images (IDs, names, links)
    */
   Set<Resource> listImages();

   /**
    * List all images (all details)
    * 
    * @return all images (all details)
    */
   Set<Image> listImagesInDetail();

   /**
    * List details of the specified image
    * 
    * @param id
    *           id of the server
    * @return server or null if not found
    */
   Image getImage(String id);

   /**
    * Delete the specified image
    * 
    * @param id
    *           id of the image
    * @return server or null if not found
    */
   void deleteImage(String id);

}
