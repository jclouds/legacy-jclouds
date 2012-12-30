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

import java.util.Map;
import java.util.Set;
import org.jclouds.cloudstack.domain.OSType;
import org.jclouds.cloudstack.options.ListOSTypesOptions;

/**
 * Provides synchronous access to CloudStack Operating System features.
 * <p/>
 * 
 * @see GuestOSAsyncClient
 * @see <a href="http://download.cloud.com/releases/2.2.0/api_2.2.12/TOC_User.html" />
 * @author Adrian Cole
 */
public interface GuestOSClient {
   /**
    * Lists all supported OS types for this cloud.
    * 
    * @param options
    *           if present, how to constrain the list
    * @return os types matching query, or empty set, if no types are found
    */
   Set<OSType> listOSTypes(ListOSTypesOptions... options);

   /**
    * get a specific os type by id
    * 
    * @param id
    *           os type to get
    * @return os type or null if not found
    */
   OSType getOSType(String id);

   /**
    * Lists all supported OS categories for this cloud.
    * 
    * @return os categories matching query, or empty set, if no categories are
    *         found
    */
   Map<String, String> listOSCategories();

   /**
    * get a specific os category by id
    * 
    * @param id
    *           os category to get
    * @return os category or null if not found
    */
   Map.Entry<String, String> getOSCategory(String id);
}
