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
package org.jclouds.iam.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;

import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.internal.CallerArg0ToPagedIterable;
import org.jclouds.iam.IAMApi;
import org.jclouds.iam.domain.User;
import org.jclouds.iam.features.UserApi;
import org.jclouds.iam.options.ListUsersOptions;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;

/**
 * @author Adrian Cole
 */
@Beta
public class UsersToPagedIterable extends CallerArg0ToPagedIterable<User, UsersToPagedIterable> {

   private final IAMApi api;

   @Inject
   protected UsersToPagedIterable(IAMApi api) {
      this.api = checkNotNull(api, "api");
   }

   @Override
   protected Function<Object, IterableWithMarker<User>> markerToNextForCallingArg0(String ignored) {
      final UserApi userApi = api.getUserApi();
      return new Function<Object, IterableWithMarker<User>>() {

         @Override
         public IterableWithMarker<User> apply(Object input) {
            return userApi.list(ListUsersOptions.Builder.afterMarker(input.toString()));
         }

         @Override
         public String toString() {
            return "listUsers()";
         }
      };
   }

}
