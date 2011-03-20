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

package org.jclouds.softlayer;

import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.softlayer.features.DatacenterClient;
import org.jclouds.softlayer.features.VirtualGuestClient;

/**
 * Provides synchronous access to SoftLayer.
 * <p/>
 * 
 * @see SoftLayerAsyncClient
 * @see <a href="http://sldn.softlayer.com/wiki/index.php/REST" />
 * @author Adrian Cole
 */
@Timeout(duration = 60, timeUnit = TimeUnit.SECONDS)
public interface SoftLayerClient {

   /**
    * Provides synchronous access to VirtualGuest features.
    */
   @Delegate
   VirtualGuestClient getVirtualGuestClient();

   /**
    * Provides synchronous access to Datacenter features.
    */
   @Delegate
   DatacenterClient getDatacenterClient();
}
