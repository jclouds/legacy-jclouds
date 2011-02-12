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

package org.jclouds.cloudstack.features;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jclouds.cloudstack.domain.VirtualMachine;
import org.jclouds.cloudstack.options.ListVirtualMachinesOptions;
import org.jclouds.concurrent.Timeout;

/**
 * Provides synchronous access to CloudStack VirtualMachine features.
 * <p/>
 * 
 * @see VirtualMachineAsyncClient
 * @see <a href="http://download.cloud.com/releases/2.2/api/TOC_User.html" />
 * @author Adrian Cole
 */
@Timeout(duration = 60, timeUnit = TimeUnit.SECONDS)
public interface VirtualMachineClient {
   /**
    * Lists VirtualMachines
    * 
    * @param options
    *           if present, how to constrain the list.
    * @return VirtualMachines matching query, or empty set, if no VirtualMachines are found
    */
   Set<VirtualMachine> listVirtualMachines(ListVirtualMachinesOptions... options);

   /**
    * get a specific VirtualMachine by id
    * 
    * @param id
    *           VirtualMachine to get
    * @return VirtualMachine or null if not found
    */
   VirtualMachine getVirtualMachine(String id);
}
