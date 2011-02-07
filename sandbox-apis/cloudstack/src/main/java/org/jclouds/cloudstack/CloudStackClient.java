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

package org.jclouds.cloudstack;

import java.util.concurrent.TimeUnit;

import org.jclouds.cloudstack.features.NetworkClient;
import org.jclouds.cloudstack.features.OfferingClient;
import org.jclouds.cloudstack.features.TemplateClient;
import org.jclouds.cloudstack.features.ZoneClient;
import org.jclouds.concurrent.Timeout;
import org.jclouds.rest.annotations.Delegate;

/**
 * Provides synchronous access to CloudStack.
 * <p/>
 * 
 * @see CloudStackAsyncClient
 * @see <a href="http://download.cloud.com/releases/2.2/api/TOC_User.html" />
 * @author Adrian Cole
 */
@Timeout(duration = 60, timeUnit = TimeUnit.SECONDS)
public interface CloudStackClient {
   /**
    * Provides synchronous access to Zone features.
    */
   @Delegate
   ZoneClient getZoneClient();

   /**
    * Provides synchronous access to Template features.
    */
   @Delegate
   TemplateClient getTemplateClient();

   /**
    * Provides synchronous access to Service, Disk, and Network Offering features.
    */
   @Delegate
   OfferingClient getOfferingClient();

   /**
    * Provides synchronous access to Network features.
    */
   @Delegate
   NetworkClient getNetworkClient();

}
