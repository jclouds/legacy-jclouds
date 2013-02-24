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
import org.jclouds.collect.internal.Arg0ToPagedIterable;
import org.jclouds.iam.IAMApi;
import org.jclouds.iam.domain.User;
import org.jclouds.iam.features.UserApi;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.base.Optional;

/**
 * @author Adrian Cole
 */
@Beta
public class UsersToPagedIterable extends Arg0ToPagedIterable<User, UsersToPagedIterable> {

   private final UserApi api;

   @Inject
   protected UsersToPagedIterable(IAMApi api) {
      this.api = checkNotNull(api, "api").getUserApi();
   }

   @Override
   protected Function<Object, IterableWithMarker<User>> markerToNextForArg0(Optional<Object> pathPrefix) {
      if (pathPrefix.isPresent())
         return new ListUsersUnderPathPrefixAtMarker(api, pathPrefix.get().toString());
      return new ListUsersAtMarker(api);
   }

   private static class ListUsersUnderPathPrefixAtMarker implements Function<Object, IterableWithMarker<User>> {
      private final UserApi api;
      private final String pathPrefix;

      @Inject
      protected ListUsersUnderPathPrefixAtMarker(UserApi api, String pathPrefix) {
         this.api = checkNotNull(api, "api");
         this.pathPrefix = checkNotNull(pathPrefix, "pathPrefix");
      }

      public IterableWithMarker<User> apply(Object input) {
         return api.listPathPrefixAt(pathPrefix, input.toString());
      }

      public String toString() {
         return "ListUsersUnderPathPrefixAtMarker(" + pathPrefix + ")";
      }
   }

   private static class ListUsersAtMarker implements Function<Object, IterableWithMarker<User>> {
      private final UserApi api;

      @Inject
      protected ListUsersAtMarker(UserApi api) {
         this.api = checkNotNull(api, "api");
      }

      public IterableWithMarker<User> apply(Object input) {
         return api.listAt(input.toString());
      }

      public String toString() {
         return "listUsersAtMarker()";
      }
   }
}
