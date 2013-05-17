/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.rackspace.clouddns.v1.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;

import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.internal.Arg0ToPagedIterable;
import org.jclouds.rackspace.clouddns.v1.CloudDNSApi;
import org.jclouds.rackspace.clouddns.v1.domain.Domain;
import org.jclouds.rackspace.clouddns.v1.features.DomainApi;
import org.jclouds.rackspace.cloudidentity.v2_0.domain.PaginatedCollection;
import org.jclouds.rackspace.cloudidentity.v2_0.options.PaginationOptions;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.base.Optional;

/**
 * @author Everett Toews
 */
@Beta
public class DomainsToPagedIterable extends Arg0ToPagedIterable<Domain, DomainsToPagedIterable> {

   private final DomainApi api;

   @Inject
   protected DomainsToPagedIterable(CloudDNSApi api) {
      this.api = checkNotNull(api, "api").getDomainApi();
   }

   @Override
   protected Function<Object, IterableWithMarker<Domain>> markerToNextForArg0(Optional<Object> arg0) {
      return new ListDomainsAtMarker(api);
   }

   private static class ListDomainsAtMarker implements Function<Object, IterableWithMarker<Domain>> {
      private final DomainApi api;

      @Inject
      protected ListDomainsAtMarker(DomainApi api) {
         this.api = checkNotNull(api, "api");
      }

      public PaginatedCollection<Domain> apply(Object input) {
         PaginationOptions paginationOptions = (PaginationOptions) input;

         return api.list(paginationOptions);
      }

      public String toString() {
         return "ListDomainsAtMarker";
      }
   }
}
