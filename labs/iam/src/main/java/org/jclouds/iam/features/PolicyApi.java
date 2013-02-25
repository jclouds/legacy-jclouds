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
package org.jclouds.iam.features;

import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.PagedIterable;
import org.jclouds.iam.domain.Policy;
import org.jclouds.javax.annotation.Nullable;

/**
 * @author Adrian Cole
 */
public interface PolicyApi {

   /**
    * Adds (or updates) a policy document.
    * 
    * @param name
    *           Name of the policy document.
    * @param document
    *           The policy document.
    */
   void create(String name, String document);

   /**
    * returns all policy names in order.
    */
   PagedIterable<String> list();

   /**
    * retrieves up to 100 policy names in order.
    */
   IterableWithMarker<String> listFirstPage();

   /**
    * retrieves up to 100 policy names in order, starting at {@code marker}
    * 
    * @param marker
    *           starting point to resume the list
    */
   IterableWithMarker<String> listAt(String marker);

   /**
    * Retrieves the specified policy document.
    * 
    * @param name
    *           Name of the policy to get information about.
    * @return null if not found
    */
   @Nullable
   Policy get(String name);

   /**
    * Deletes the specified policy.
    * 
    * @param name
    *           Name of the policy to delete
    */
   void delete(String name);
}
