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

import org.jclouds.cloudstack.features.NetworkAsyncClient;
import org.jclouds.cloudstack.features.OfferingAsyncClient;
import org.jclouds.cloudstack.features.TemplateAsyncClient;
import org.jclouds.cloudstack.features.ZoneAsyncClient;
import org.jclouds.rest.annotations.Delegate;

/**
 * Provides asynchronous access to CloudStack via their REST API.
 * <p/>
 * 
 * @see CloudStackClient
 * @see <a href="http://download.cloud.com/releases/2.2/api/TOC_User.html" />
 * @author Adrian Cole
 */
public interface CloudStackAsyncClient {

   /**
    * Provides asynchronous access to Zone features.
    */
   @Delegate
   ZoneAsyncClient getZoneClient();

   /**
    * Provides asynchronous access to Template features.
    */
   @Delegate
   TemplateAsyncClient getTemplateClient();

   /**
    * Provides asynchronous access to Service, Disk, and Network Offering features.
    */
   @Delegate
   OfferingAsyncClient getOfferingClient();

   /**
    * Provides asynchronous access to Network features.
    */
   @Delegate
   NetworkAsyncClient getNetworkClient();
}
