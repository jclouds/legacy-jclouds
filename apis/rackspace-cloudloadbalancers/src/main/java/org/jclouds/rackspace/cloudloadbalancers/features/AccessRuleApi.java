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
package org.jclouds.rackspace.cloudloadbalancers.features;

import org.jclouds.rackspace.cloudloadbalancers.domain.AccessRule;
import org.jclouds.rackspace.cloudloadbalancers.domain.AccessRuleWithId;

/**
 * The access list management feature allows fine-grained network access controls to be applied to the load balancer's
 * virtual IP address.
 * <p/>
 * 
 * @see AccessRuleAsyncApi
 * @author Everett Toews
 */
public interface AccessRuleApi {
   /**
    * Create a new access list or append to an existing access list.
    * 
    * When creating an access list, one or more AccessRules are required. If a populated access list already exists 
    * for the load balancer, it will be appended to with subsequent creates. One access list may include up to 100 
    * AccessRules. A single address or subnet definition is considered unique and cannot be duplicated between rules
    * in an access list.
    */
   void create(Iterable<AccessRule> accessList);

   /**
    * List the AccessRules
    */
   Iterable<AccessRuleWithId> list();
   
   /**
    * Remove an access rule from the access list.
    */
   void remove(int id);
   
   /**
    * Batch delete the access rules given the specified ids.
    */
   void remove(Iterable<Integer> ids);
   
   /**
    * Remove the entire access list.
    */
   void removeAll();
}