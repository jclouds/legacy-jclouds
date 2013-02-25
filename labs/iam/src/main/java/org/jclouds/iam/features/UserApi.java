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
import org.jclouds.iam.domain.User;
import org.jclouds.javax.annotation.Nullable;

/**
 * 
 * @see UserAsyncApi
 * @author Adrian Cole
 */
public interface UserApi {
   /**
    * Retrieves information about the current user, including the user's path, GUID, and ARN.
    */
   User getCurrent();

   /**
    * returns all users in order.
    */
   PagedIterable<User> list();

   /**
    * retrieves up to 100 users in order.
    */
   IterableWithMarker<User> listFirstPage();

   /**
    * retrieves up to 100 users in order, starting at {@code marker}
    * 
    * @param marker
    *           starting point to resume the list
    */
   IterableWithMarker<User> listAt(String marker);

   /**
    * returns all users in order at the specified {@code pathPrefix}.
    * 
    * @param pathPrefix
    *           ex. {@code /division_abc/subdivision_xyz/}
    */
   PagedIterable<User> listPathPrefix(String pathPrefix);

   /**
    * retrieves up to 100 users in order at the specified {@code pathPrefix}.
    * 
    * @param pathPrefix
    *           ex. {@code /division_abc/subdivision_xyz/}
    */
   IterableWithMarker<User> listPathPrefixFirstPage(String pathPrefix);

   /**
    * retrieves up to 100 users in order at the specified {@code pathPrefix}, starting at {@code marker}.
    * 
    * @param pathPrefix
    *           ex. {@code /division_abc/subdivision_xyz/}
    * @param marker
    *           starting point to resume the list
    */
   IterableWithMarker<User> listPathPrefixAt(String pathPrefix, String marker);

   /**
    * Retrieves information about the specified user, including the user's path, GUID, and ARN.
    * 
    * @param name
    *           Name of the user to get information about.
    * @return null if not found
    */
   @Nullable
   User get(String name);
}
