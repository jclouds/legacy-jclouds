/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.deltacloud;

import java.net.URI;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.deltacloud.collections.DeltacloudCollection;
import org.jclouds.deltacloud.domain.Image;
import org.jclouds.deltacloud.options.CreateInstanceOptions;

/**
 * Provides synchronous access to deltacloud.
 * <p/>
 * 
 * @see DeltacloudAsyncClient
 * @see <a href="TODO: insert URL of deltacloud documentation" />
 * @author Adrian Cole
 */
@Timeout(duration = 60, timeUnit = TimeUnit.SECONDS)
public interface DeltacloudClient {
   /**
    * The result of this entry-point is a set of entry-points into other collections, such as
    * images, instances, hardware profiles and realms, among others.
    * 
    * @return named links to available collections, or empty map, if no resources are found
    */
   Map<DeltacloudCollection, URI> getCollections();

   /**
    * The images collection will return a set of all images available to the current user.
    * 
    * @return images viewable to the user or empty set
    */
   Set<? extends Image> listImages();

   /**
    * 
    * @param imageHref
    * @return image or null, if not found
    */
   Image getImage(URI imageHref);

   /**
    * Create a new Instance
    * 
    * <h4>Note</h4>
    * 
    * If options realmId or hardwareProfileName are not specified, the provider must select
    * reasonable defaults. The architecture of the selected hardware profile must match the
    * architecture of the specified image.
    * 
    * @param imageId
    *           The identifier (not URL) of the image from which to base the instance
    * @param options
    *           includes realm, hardware profile, etc.
    * @return newly-created instance including a URL to retrieve the instance in the future.
    */
   String createInstance(String imageId, CreateInstanceOptions... options);
}
