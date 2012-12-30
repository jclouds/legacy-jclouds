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
import org.jclouds.iam.options.ListUsersOptions;
import org.jclouds.javax.annotation.Nullable;

/**
 * Provides access to Amazon IAM via the Query API
 * <p/>
 * 
 * @see <a href="http://docs.amazonwebservices.com/IAM/latest/APIReference" />
 * @author Adrian Cole
 */
public interface UserApi {
   /**
    * Retrieves information about the current user, including the user's path, GUID, and ARN.
    */
   User getCurrent();

   /**
    * Retrieves information about the specified user, including the user's path, GUID, and ARN.
    * 
    * @param name
    *           Name of the user to get information about.
    * @return null if not found
    */
   @Nullable
   User get(String name);

   /**
    * Lists the users that have the specified path prefix. If there are none, the action returns an
    * empty list.
    * 
    * <br/>
    * You can paginate the results using the {@link ListUsersOptions parameter}
    * 
    * @param options
    *           the options describing the users query
    * 
    * @return the response object
    */
   IterableWithMarker<User> list(ListUsersOptions options);

   /**
    * Lists the users that have the specified path prefix. If there are none, the action returns an
    * empty list.
    * 
    * @return the response object
    */
   PagedIterable<User> list();

}
