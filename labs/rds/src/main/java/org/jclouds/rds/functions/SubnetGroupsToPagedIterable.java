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
package org.jclouds.rds.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;

import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.internal.CallerArg0ToPagedIterable;
import org.jclouds.rds.RDSApi;
import org.jclouds.rds.domain.SubnetGroup;
import org.jclouds.rds.features.SubnetGroupApi;
import org.jclouds.rds.options.ListSubnetGroupsOptions;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;

/**
 * @author Adrian Cole
 */
@Beta
public class SubnetGroupsToPagedIterable extends CallerArg0ToPagedIterable<SubnetGroup, SubnetGroupsToPagedIterable> {

   private final RDSApi api;

   @Inject
   protected SubnetGroupsToPagedIterable(RDSApi api) {
      this.api = checkNotNull(api, "api");
   }

   @Override
   protected Function<Object, IterableWithMarker<SubnetGroup>> markerToNextForCallingArg0(final String arg0) {
      final SubnetGroupApi subnetGroupApi = api.getSubnetGroupApiForRegion(arg0);
      return new Function<Object, IterableWithMarker<SubnetGroup>>() {

         @Override
         public IterableWithMarker<SubnetGroup> apply(Object input) {
            return subnetGroupApi.list(ListSubnetGroupsOptions.Builder.afterMarker(input));
         }

         @Override
         public String toString() {
            return "listSubnetGroupsInRegion(" + arg0 + ")";
         }
      };
   }

}
