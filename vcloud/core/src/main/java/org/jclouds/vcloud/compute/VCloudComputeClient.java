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

package org.jclouds.vcloud.compute;

import java.net.URI;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import org.jclouds.vcloud.options.InstantiateVAppTemplateOptions;

import com.google.inject.ImplementedBy;

/**
 * 
 * @author Adrian Cole
 */
@ImplementedBy(BaseVCloudComputeClient.class)
public interface VCloudComputeClient {
   /**
    * Runs through all commands necessary to startup a vApp, opening at least
    * one ip address to the public network. These are the steps:
    * <p/>
    * instantiate -> deploy -> powerOn
    * <p/>
    * This command blocks until the vApp is in state {@code VAppStatus#ON}
    * 
    * @param orgName
    *           name of the organization
    * 
    * @param vDCName
    *           name of the virtual datacenter {@code
    *           VCloudClient#getDefaultVDC}
    * @param name
    *           name of the vApp
    * @param templateId
    *           id of the vAppTemplate you wish to instantiate
    * @param cores
    *           amount of virtual cpu cores
    * @param megs
    *           amount of ram in megabytes
    * @param options
    *           options for instantiating the vApp; null is ok
    * @param portsToOpen
    *           opens the following ports on the public ip address
    * @return map contains at least the following properties
    *         <ol>
    *         <li>id - vApp id</li> <li>username - console login user</li> <li>
    *         password - console login password</li>
    *         </ol>
    */
   Map<String, String> start(@Nullable URI VDC, URI templateId, String name, InstantiateVAppTemplateOptions options,
         int... portsToOpen);

   /**
    * returns a set of addresses that are only visible to the private network.
    */
   Set<String> getPrivateAddresses(String vAppId);

   /**
    * returns a set of addresses that are publically visible
    */
   Set<String> getPublicAddresses(String vAppId);

   /**
    * reboots the vApp, blocking until the following state transition is
    * complete:
    * <p/>
    * current -> {@code VAppStatus#OFF} -> {@code VAppStatus#ON}
    * 
    * @param vAppId
    *           vApp to reboot
    */
   void reboot(String vAppId);

   /**
    * Destroys dependent resources, powers off and deletes the vApp, blocking
    * until the following state transition is complete:
    * <p/>
    * current -> {@code VAppStatus#OFF} -> deleted
    * 
    * @param vAppId
    *           vApp to stop
    */
   void stop(String vAppId);

}
