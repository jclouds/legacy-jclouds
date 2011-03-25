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

package org.jclouds.savvis.vpdc.features;

import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.savvis.vpdc.domain.Task;
import org.jclouds.savvis.vpdc.domain.VMSpec;

/**
 * Provides access to Symphony VPDC resources via their REST API.
 * <p/>
 * 
 * @see <a href="https://api.sandbox.symphonyvpdc.savvis.net/doc/spec/api/" />
 * @author Adrian Cole
 */
@Timeout(duration = 300, timeUnit = TimeUnit.SECONDS)
public interface VMClient {

   /**
    * Add/Deploy new VM into VDC
    * 
    * @param billingSiteId
    *           billing site Id, or null for default
    * @param vpdcId
    *           vpdc Id
    * @param networkTierName
    *           network tier name
    * @param spec
    *           how to
    * 
    * @return VM in progress
    */
   Task addVMIntoVDC(String billingSiteId, String vpdcId, String networkTierName, String name, VMSpec spec);

   /**
    * Remove a VM
    * <p/>
    * <h4>Pre-conditions:</h4>
    * 
    * <ul>
    * <li>No snapshot has been created for the VM.</li>
    * <li>For Balanced profile, the VM must not be associated with any firewall rule and/or included
    * in a load balancing pool.</li>
    * </ul>
    * 
    * @param billingSiteId
    * @param vpdcId
    * @param vAppId
    * @return
    */
   Task removeVMFromVDC(String billingSiteId, String vpdcId, String vAppId);

}