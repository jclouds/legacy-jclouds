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
import org.jclouds.googlecompute.domain.Disk;
import org.jclouds.googlecompute.domain.ListPage;
import org.jclouds.googlecompute.options.ListOptions;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.json.Json;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author David Alves
 */
@Singleton
public class ParseDisks extends ParseJson<ListPage<Disk>> {

   @Inject
   public ParseDisks(Json json) {
      super(json, new TypeLiteral<ListPage<Disk>>() {});
   }

   public static class ToPagedIterable extends BaseToPagedIterable<Disk, ToPagedIterable> {

      private final GoogleComputeApi api;

      @Inject
      protected ToPagedIterable(GoogleComputeApi api) {
         this.api = checkNotNull(api, "api");
      }

      @Override
      protected Function<Object, IterableWithMarker<Disk>> fetchNextPage(final String projectName,
                                                                         final ListOptions options) {
         return new Function<Object, IterableWithMarker<Disk>>() {

            @Override
            public IterableWithMarker<Disk> apply(Object input) {
               return api.getDiskApiForProject(projectName).listAtMarker(input.toString(), options);
            }
         };
      }
   }
}
