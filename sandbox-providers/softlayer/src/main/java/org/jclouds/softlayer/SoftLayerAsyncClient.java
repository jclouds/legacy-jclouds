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
package org.jclouds.softlayer;

import org.jclouds.rest.annotations.Delegate;
import org.jclouds.softlayer.features.DatacenterAsyncClient;
import org.jclouds.softlayer.features.VirtualGuestAsyncClient;

/**
 * Provides asynchronous access to SoftLayer via their REST API.
 * <p/>
 * 
 * @see SoftLayerClient
 * @see <a href="http://sldn.softlayer.com/wiki/index.php/REST" />
 * @author Adrian Cole
 */
public interface SoftLayerAsyncClient {

   /**
    * Provides asynchronous access to VirtualGuest features.
    */
   @Delegate
   VirtualGuestAsyncClient getVirtualGuestClient();

   /**
    * Provides asynchronous access to Datacenter features.
    */
   @Delegate
   DatacenterAsyncClient getDatacenterClient();
}
