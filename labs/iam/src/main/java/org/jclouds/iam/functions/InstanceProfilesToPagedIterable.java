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
import org.jclouds.iam.domain.InstanceProfile;
import org.jclouds.iam.features.InstanceProfileApi;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.base.Optional;

/**
 * @author Adrian Cole
 */
@Beta
public class InstanceProfilesToPagedIterable extends Arg0ToPagedIterable<InstanceProfile, InstanceProfilesToPagedIterable> {

   private final InstanceProfileApi api;

   @Inject
   protected InstanceProfilesToPagedIterable(IAMApi api) {
      this.api = checkNotNull(api, "api").getInstanceProfileApi();
   }

   @Override
   protected Function<Object, IterableWithMarker<InstanceProfile>> markerToNextForArg0(Optional<Object> pathPrefix) {
      if (pathPrefix.isPresent())
         return new ListInstanceProfilesUnderPathPrefixAtMarker(api, pathPrefix.get().toString());
      return new ListInstanceProfilesAtMarker(api);
   }

   private static class ListInstanceProfilesUnderPathPrefixAtMarker implements Function<Object, IterableWithMarker<InstanceProfile>> {
      private final InstanceProfileApi api;
      private final String pathPrefix;

      @Inject
      protected ListInstanceProfilesUnderPathPrefixAtMarker(InstanceProfileApi api, String pathPrefix) {
         this.api = checkNotNull(api, "api");
         this.pathPrefix = checkNotNull(pathPrefix, "pathPrefix");
      }

      public IterableWithMarker<InstanceProfile> apply(Object input) {
         return api.listPathPrefixAt(pathPrefix, input.toString());
      }

      public String toString() {
         return "ListInstanceProfilesUnderPathPrefixAtMarker(" + pathPrefix + ")";
      }
   }

   private static class ListInstanceProfilesAtMarker implements Function<Object, IterableWithMarker<InstanceProfile>> {
      private final InstanceProfileApi api;

      @Inject
      protected ListInstanceProfilesAtMarker(InstanceProfileApi api) {
         this.api = checkNotNull(api, "api");
      }

      public IterableWithMarker<InstanceProfile> apply(Object input) {
         return api.listAt(input.toString());
      }

      public String toString() {
         return "listInstanceProfilesAtMarker()";
      }
   }
}
