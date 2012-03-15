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
package org.jclouds.vcloud.director.v1_5.features;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.vcloud.director.v1_5.domain.AdminVdc;

/**
 * Provides synchronous access to Network.
 * <p/>
 * 
 * @see NetworkAsyncClient
 * @see <a href= "http://support.theenterprisecloud.com/kb/default.asp?id=984&Lang=1&SID=" />
 * @author danikov
 */
@Timeout(duration = 180, timeUnit = TimeUnit.SECONDS)
public interface AdminVdcClient extends VdcClient {

   /**
    * Retrieves an admin view of virtual data center. The redwood admin can disable an 
    * organization vDC. This will prevent any further allocation to be used by the organization. 
    * Changing the state will not affect allocations already used. For example, if an organization 
    * vDC is disabled, an organization user cannot deploy or create a new virtual machine in the 
    * vDC (deploy uses memory and cpu allocations, and create uses storage allocation).
    * 
    * @return the admin vDC or null if not found
    */
   @Override
   AdminVdc getVdc(URI vdcRef);
}
