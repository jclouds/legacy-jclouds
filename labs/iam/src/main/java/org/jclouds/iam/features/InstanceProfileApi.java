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

import java.util.concurrent.TimeUnit;

import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.PagedIterable;
import org.jclouds.concurrent.Timeout;
import org.jclouds.iam.domain.InstanceProfile;
import org.jclouds.javax.annotation.Nullable;

/**
 * @see InstanceProfileAsyncApi
 * @author Adrian Cole
 */
@Timeout(duration = 30, timeUnit = TimeUnit.SECONDS)
public interface InstanceProfileApi {

   /**
    * Creates a new instance profile for your AWS account
    * 
    * @param name
    *           Name of the instance profile to create.
    * @return the new instance profile
    */
   InstanceProfile create(String name);

   /**
    * like {@link #create(String)}, except you can specify a path.
    */
   InstanceProfile createWithPath(String name, String path);

   /**
    * returns all instance profiles in order.
    */
   PagedIterable<InstanceProfile> list();

   /**
    * retrieves up to 100 instance profiles in order.
    */
   IterableWithMarker<InstanceProfile> listFirstPage();

   /**
    * retrieves up to 100 instance profiles in order, starting at {@code marker}
    * 
    * @param marker
    *           starting point to resume the list
    */
   IterableWithMarker<InstanceProfile> listAt(String marker);

   /**
    * returns all instance profiles in order at the specified {@code pathPrefix}.
    * 
    * @param pathPrefix
    *           ex. {@code /division_abc/subdivision_xyz/}
    */
   PagedIterable<InstanceProfile> listPathPrefix(String pathPrefix);

   /**
    * retrieves up to 100 instance profiles in order at the specified {@code pathPrefix}.
    * 
    * @param pathPrefix
    *           ex. {@code /division_abc/subdivision_xyz/}
    */
   IterableWithMarker<InstanceProfile> listPathPrefixFirstPage(String pathPrefix);

   /**
    * retrieves up to 100 instance profiles in order at the specified {@code pathPrefix}, starting at {@code marker}.
    * 
    * @param pathPrefix
    *           ex. {@code /division_abc/subdivision_xyz/}
    * @param marker
    *           starting point to resume the list
    */
   IterableWithMarker<InstanceProfile> listPathPrefixAt(String pathPrefix, String marker);

   /**
    * Retrieves information about the specified instance profile, including the instance profile's path, GUID, and ARN.
    * 
    * @param name
    *           Name of the instance profile to get information about.
    * @return null if not found
    */
   @Nullable
   InstanceProfile get(String name);

   /**
    * Deletes the specified instanceProfile. The instance profile must not have any policies attached.
    * 
    * @param name
    *           Name of the instance profile to delete
    */
   void delete(String name);

   /**
    * Adds the specified role to the specified instance profile.
    * 
    * @param name
    *           Name of the instance profile to update.
    * @param roleName
    *           Name of the role to add
    */
   void addRole(String name, String roleName);

   /**
    * Removes the specified role from the specified instance profile.
    * 
    * @param name
    *           Name of the instance profile to update.
    * @param roleName
    *           Name of the role to remove
    */
   void removeRole(String name, String roleName);
}
