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
import org.jclouds.iam.features.PolicyApi;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.base.Optional;

/**
 * @author Adrian Cole
 */
@Beta
public class PoliciesToPagedIterable {
   public static class RolePoliciesToPagedIterable extends Arg0ToPagedIterable.FromCaller<String, RolePoliciesToPagedIterable> {

      private final IAMApi api;

      @Inject
      protected RolePoliciesToPagedIterable(IAMApi api) {
         this.api = checkNotNull(api, "api");
      }

      @Override
      protected Function<Object, IterableWithMarker<String>> markerToNextForArg0(Optional<Object> roleName) {
         return new ListPoliciesAtMarker(api.getPolicyApiForRole(roleName.get().toString()));
      }
   }

   private static class ListPoliciesAtMarker implements Function<Object, IterableWithMarker<String>> {
      private final PolicyApi api;

      @Inject
      protected ListPoliciesAtMarker(PolicyApi api) {
         this.api = checkNotNull(api, "api");
      }

      public IterableWithMarker<String> apply(Object input) {
         return api.listAt(input.toString());
      }

      public String toString() {
         return "listPoliciesAtMarker()";
      }
   }
}
