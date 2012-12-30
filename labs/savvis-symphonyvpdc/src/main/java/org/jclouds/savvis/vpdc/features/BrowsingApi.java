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
import org.jclouds.javax.annotation.Nullable;

import org.jclouds.savvis.vpdc.domain.FirewallService;
import org.jclouds.savvis.vpdc.domain.Network;
import org.jclouds.savvis.vpdc.domain.Org;
import org.jclouds.savvis.vpdc.domain.Task;
import org.jclouds.savvis.vpdc.domain.VDC;
import org.jclouds.savvis.vpdc.domain.VM;
import org.jclouds.savvis.vpdc.options.GetVMOptions;

/**
 * Provides access to Symphony VPDC resources via their REST API.
 * <p/>
 * 
 * @see <a href="https://api.sandbox.savvis.net/doc/spec/api/" />
 * @author Adrian Cole
 */
public interface BrowsingApi {
   /**
    * Get an organization, which can contain list of vDC entities
    * 
    * @param billingSiteId
    *           billing site Id, or null for default
    * @return organization, or null if not present
    */
   Org getOrg(@Nullable String billingSiteId);

   /**
    * VDC is a virtual data center ,the API returns a list of VAPPs own by given bill site Id.
    * 
    * @param billingSiteId
    *           billing site Id, or null for default
    * @param vpdcId
    *           vpdc Id
    * @return a list of resource entity and VM configurations, or null if not present
    */
   VDC getVDCInOrg(@Nullable String billingSiteId, String vpdcId);

   /**
    * Get Network API returns network detail
    * 
    * @param billingSiteId
    *           billing site Id, or null for default
    * @param vpdcId
    *           vpdc Id
    * @param networkTierName
    *           network tier name
    * 
    * @return network detail if it used any one deployed VM and NetworkConfigSection defines various
    *         network features such NAT Public IP, Gateway and Netmask, or null if not present
    */
   Network getNetworkInVDC(String billingSiteId, String vpdcId, String networkTierName);

   /**
    * VAPP is a software solution, the API returns details of virtual machine configuration such as
    * CPU,RAM Memory and hard drive. The VM State is from the MW Database.
    * 
    * @param billingSiteId
    *           billing site Id, or null for default
    * @param vpdcId
    *           vpdc Id
    * @param vAppId
    *           vApp ID
    * @param options
    *           control whether or not to get real time state
    * 
    * @return A virtual application (vApp) is a software solution comprising one or more virtual
    *         machines, all of which are deployed, managed, and maintained as a unit, or null if not
    *         present
    */
   VM getVMInVDC(String billingSiteId, String vpdcId, String vAppId, GetVMOptions... options);

   VM getVM(URI vm, GetVMOptions... options);

   /**
    * Gets an existing task.
    * 
    * @param taskId
    *           task id
    * @return If the request is successful, caller could get the VM/VMDK details as specified in the
    *         result element and if the request is not successful, caller would get empty VAPP/VMDK
    *         URL and respective validation (error) message.
    */
   Task getTask(String taskId);

   /**
    * Gets Firewall Rules
    * 
    * @param billingSiteId
    *           billing site Id, or null for default
    * @param vpdcId
    *           vpdc Id
    * 
    * @return If the request is successful, caller could get the firewall rules as specified in the
    *         result element and if the request is not successful, caller would get empty rules list
    *         and respective validation (error) message.
    */
   FirewallService listFirewallRules(String billingSiteId, String vpdcId);

}
