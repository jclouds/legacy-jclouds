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
package org.jclouds.route53.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;

import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.internal.CallerArg0ToPagedIterable;
import org.jclouds.route53.Route53Api;
import org.jclouds.route53.domain.ResourceRecordSet;
import org.jclouds.route53.domain.ResourceRecordSetIterable.NextRecord;
import org.jclouds.route53.features.ResourceRecordSetApi;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;

/**
 * @author Adrian Cole
 */
@Beta
public class ResourceRecordSetsToPagedIterable extends
      CallerArg0ToPagedIterable<ResourceRecordSet, ResourceRecordSetsToPagedIterable> {

   private final Route53Api api;

   @Inject
   protected ResourceRecordSetsToPagedIterable(Route53Api api) {
      this.api = checkNotNull(api, "api");
   }

   @Override
   protected Function<Object, IterableWithMarker<ResourceRecordSet>> markerToNextForCallingArg0(String zoneId) {
      final ResourceRecordSetApi resourceRecordSetApi = api.getResourceRecordSetApiForZone(zoneId);
      return new Function<Object, IterableWithMarker<ResourceRecordSet>>() {

         @Override
         public IterableWithMarker<ResourceRecordSet> apply(Object input) {
            return resourceRecordSetApi.listAt(NextRecord.class.cast(input));
         }

         @Override
         public String toString() {
            return "listResourceRecordSets()";
         }
      };
   }
}
