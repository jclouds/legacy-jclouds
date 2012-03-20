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
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.vcloud.director.v1_5.domain.AdminVdc;
import org.jclouds.vcloud.director.v1_5.domain.Task;
import org.jclouds.vcloud.director.v1_5.features.MetadataAsyncClient.Writable;

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

   /**
    * Modifies a Virtual Data Center. Virtual Data Center could be enabled or disabled. 
    * Additionally it could have one of these states FAILED_CREATION(-1), NOT_READY(0), 
    * READY(1), UNKNOWN(1) and UNRECOGNIZED(3).
    */
   Task editVdc(URI vdcRef, AdminVdc vdc);
   
   /**
    * Deletes a Virtual Data Center. The Virtual Data Center should be disabled when delete is issued. 
    * Otherwise error code 400 Bad Request is returned.
    */
   // TODO Saw what exception, instead of 400 
   Task deleteVdc(URI vdcRef);
   
   /**
    * Enables a Virtual Data Center. This operation enables disabled Virtual Data Center. 
    * If it is already enabled this operation has no effect.
    */
   void enableVdc(@EndpointParam URI vdcRef);
   
   /**
    * Disables a Virtual Data Center. If the Virtual Data Center is disabled this operation does not 
    * have an effect.
    */
   void disableVdc(URI vdcRef);
   
   /**
    * @return synchronous access to {@link Writable} features
    */
   @Delegate
   MetadataClient.Writeable getMetadataClient();
}
