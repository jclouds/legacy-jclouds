/*
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

package org.jclouds.googlecompute.functions.internal;

import com.google.common.base.Function;
import com.google.inject.TypeLiteral;
import org.jclouds.collect.IterableWithMarker;
import org.jclouds.googlecompute.GoogleComputeApi;
import org.jclouds.googlecompute.domain.ListPage;
import org.jclouds.googlecompute.domain.MachineType;
import org.jclouds.googlecompute.options.ListOptions;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.json.Json;

import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author David Alves
 */
public class ParseMachineTypes extends ParseJson<ListPage<MachineType>> {

   @Inject
   public ParseMachineTypes(Json json) {
      super(json, new TypeLiteral<ListPage<MachineType>>() {});
   }

   public static class ToPagedIterable extends BaseToPagedIterable<MachineType, ToPagedIterable> {

      private final GoogleComputeApi api;

      @Inject
      protected ToPagedIterable(GoogleComputeApi api) {
         this.api = checkNotNull(api, "api");
      }

      @Override
      protected Function<Object, IterableWithMarker<MachineType>> fetchNextPage(final String projectName,
                                                                                final ListOptions options) {
         return new Function<Object, IterableWithMarker<MachineType>>() {

            @Override
            public IterableWithMarker<MachineType> apply(Object input) {
               return api.getMachineTypeApiForProject(projectName).listAtMarker(input.toString(), options);
            }
         };
      }
   }
}
