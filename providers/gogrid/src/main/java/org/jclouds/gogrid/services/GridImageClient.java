/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.gogrid.services;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.gogrid.domain.Option;
import org.jclouds.gogrid.domain.ServerImage;
import org.jclouds.gogrid.options.GetImageListOptions;

/**
 * Manages the server images
 * 
 * @see <a href="http://wiki.gogrid.com/wiki/index.php/API#Server_Image_Methods"/>
 * @author Oleksiy Yarmula
 */
@Timeout(duration = 30, timeUnit = TimeUnit.SECONDS)
public interface GridImageClient {

   /**
    * Returns all server images.
    * 
    * @param options
    *           options to narrow the search down
    * @return server images found
    */
   Set<ServerImage> getImageList(GetImageListOptions... options);

   /**
    * Returns images, found by specified ids
    * 
    * @param ids
    *           the ids that match existing images
    * @return images found
    */
   Set<ServerImage> getImagesById(Long... ids);

   /**
    * Returns images, found by specified names
    * 
    * @param names
    *           the names that march existing images
    * @return images found
    */
   Set<ServerImage> getImagesByName(String... names);

   /**
    * Edits an existing image
    * 
    * @param idOrName
    *           id or name of the existing image
    * @param newDescription
    *           description to replace the current one
    * @return edited server image
    */
   ServerImage editImageDescription(String idOrName, String newDescription);

   /**
    * Edits an existing image
    * 
    * @param idOrName
    *           id or name of the existing image
    * @param newFriendlyName
    *           friendly name to replace the current one
    * @return edited server image
    */
   ServerImage editImageFriendlyName(String idOrName, String newFriendlyName);

   /**
    * Retrieves the list of supported Datacenters to save images in. The objects will have
    * datacenter ID, name and description. In most cases, id or name will be used for
    * {@link #getImageList}.
    * 
    * @return supported datacenters
    */
   Set<Option> getDatacenters();
}
