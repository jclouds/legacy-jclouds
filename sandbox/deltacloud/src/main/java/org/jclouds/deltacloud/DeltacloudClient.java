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
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.deltacloud.domain.DeltacloudCollection;
import org.jclouds.deltacloud.domain.HardwareProfile;
import org.jclouds.deltacloud.domain.Image;
import org.jclouds.deltacloud.domain.Instance;
import org.jclouds.deltacloud.domain.InstanceState;
import org.jclouds.deltacloud.domain.Realm;
import org.jclouds.deltacloud.domain.Transition;
import org.jclouds.deltacloud.options.CreateInstanceOptions;
import org.jclouds.http.HttpRequest;

import com.google.common.collect.Multimap;

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
    * @return named links to available collections, or empty set, if no collections are found
    */
   Set<? extends DeltacloudCollection> getCollections();

   /**
    * 
    * @return The possible states of an instance, and how to traverse between them
    */
   Multimap<InstanceState, ? extends Transition> getInstanceStates();

   /**
    * The realms collection will return a set of all realms available to the current user.
    * 
    * @return realms viewable to the user or empty set
    */
   Set<? extends Realm> listRealms();

   /**
    * 
    * @param realmHref
    * @return realm or null, if not found
    */
   Realm getRealm(URI realmHref);

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
    * The hardware profiles collection will return a set of all hardware profiles available to the
    * current user.
    * 
    * @return hardware profiles viewable to the user or empty set
    */
   Set<? extends HardwareProfile> listHardwareProfiles();

   /**
    * 
    * @param profileHref
    * @return hardware profile or null, if not found
    */
   HardwareProfile getHardwareProfile(URI profileHref);

   /**
    * The instances collection will return a set of all instances available to the current user.
    * 
    * @return instances viewable to the user or empty set
    */
   Set<? extends Instance> listInstances();

   /**
    * 
    * @param instanceHref
    * @return instance or null, if not found
    */
   Instance getInstance(URI instanceHref);

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
   Instance createInstance(String imageId, CreateInstanceOptions... options);

   /**
    * perform a specific action.
    * 
    * @param action
    *           reference from {@link Instance#getActions()}
    */
   void performAction(HttpRequest action);

}
