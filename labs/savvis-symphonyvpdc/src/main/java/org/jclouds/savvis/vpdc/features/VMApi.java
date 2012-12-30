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
package org.jclouds.savvis.vpdc.features;

import java.net.URI;
import java.util.Set;
import org.jclouds.savvis.vpdc.domain.Task;
import org.jclouds.savvis.vpdc.domain.VMSpec;

/**
 * Provides access to Symphony VPDC resources via their REST API.
 * <p/>
 * 
 * @see <a href="https://api.sandbox.savvis.net/doc/spec/api/" />
 * @author Adrian Cole
 */
public interface VMApi {

   /**
    * Add/Deploy new VM into VDC
    * 
    * @param billingSiteId
    *           billing site Id, or null for default
    * @param vpdcId
    *           vpdc Id
    * @param spec
    *           how to
    * 
    * @return VM in progress
    */
   Task addVMIntoVDC(String billingSiteId, String vpdcId, VMSpec spec);

   /**
    * 
    * @param vpdc
    *           href of the vpdc
    * @see #addVMIntoVDC
    */
   Task addVMIntoVDC(URI vpdc, VMSpec spec);

   /**
    * Add/Deploy new VMs into VDC
    * 
    * @param billingSiteId
    *           billing site Id, or null for default
    * @param vpdcId
    *           vpdc Id
    * @param vmSpecs
    *           vm configurations
    * @return VM's in progress
    */
   Set<Task> addMultipleVMsIntoVDC(String billingSiteId, String vpdcId, Iterable<VMSpec> vmSpecs);

   /**
    * Add/Deploy new VMs into VDC
    * 
    * @param vpdc
    *           href of the vpdc
    * @param vmSpecs
    *           vm configurations
    * @return VM's in progress
    */
   Set<Task> addMultipleVMsIntoVDC(URI vpdc, Iterable<VMSpec> vmSpecs);

   /**
    * 
    * @param billingSiteId
    *           billing site Id, or null for default
    * @param vpdcId
    *           vpdc Id
    * @param vAppUri
    *           href of the vApp
    * @return Task with vAppTemplate href
    */
   Task captureVApp(String billingSiteId, String vpdcId, URI vAppUri);

   /**
    * 
    * @param vAppUri
    *           href of the vApp
    * @param newVAppName
    *           name for the new vApp
    * @param networkTierName
    *           network tier name for vApp
    * @return
    */
   Task cloneVApp(URI vAppUri, String newVAppName, String networkTierName);

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
    *           billing site Id, or null for default
    * @param vpdcId
    *           vpdc Id
    * @param vmId
    *           vm you wish to remove
    * @return null, if the vm was not found
    */
   Task removeVMFromVDC(String billingSiteId, String vpdcId, String vmId);

   /**
    * 
    * Remove a VM
    * 
    * @param vm
    *           href of the vm
    * @see #removeVMFromVDC
    */
   Task removeVM(URI vm);

   /**
    * Power off a VM
    * 
    * @param vm
    *           href of the vm
    * @return
    */
   Task powerOffVM(URI vm);

   /**
    * Power on a VM
    * 
    * @param vm
    *           href of the vm
    * @return
    */
   Task powerOnVM(URI vm);
}
