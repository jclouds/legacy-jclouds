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
package org.jclouds.cloudstack.features;

import java.util.Set;
import org.jclouds.cloudstack.domain.VMGroup;
import org.jclouds.cloudstack.options.CreateVMGroupOptions;
import org.jclouds.cloudstack.options.ListVMGroupsOptions;
import org.jclouds.cloudstack.options.UpdateVMGroupOptions;

/**
 * Provides synchronous access to CloudStack VM group features.
 * <p/>
 *
 * @author Richard Downer
 * @see VMGroupAsyncClient
 * @see <a href="http://download.cloud.com/releases/2.2.0/api_2.2.12/TOC_User.html" />
 */
public interface VMGroupClient {
   /**
    * Lists VM groups
    *
    * @param options if present, how to constrain the list.
    * @return VM groups matching query, or empty set, if no groups are found
    */
   Set<VMGroup> listInstanceGroups(ListVMGroupsOptions... options);

   /**
    * Retrieve a VM group by its id
    *
    * @param id the id of the required VM group.
    * @return the VM group with the requested id, or null if not found
    */
   VMGroup getInstanceGroup(String id);

   /**
    * Creates a VM group
    *
    * @param name    the name of the VM group
    * @param options optional parameters
    * @return the new VMGroup
    */
   VMGroup createInstanceGroup(String name, CreateVMGroupOptions... options);

   /**
    * Modify a VM group
    *
    * @param name the new name of the group
    * @return the modified VMGroup
    */
   VMGroup updateInstanceGroup(String id, UpdateVMGroupOptions... options);

   /**
    * Delete a VM group
    *
    * @param id the ID of the VM group
    */
   void deleteInstanceGroup(String id);
}
