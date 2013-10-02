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
import org.jclouds.rackspace.clouddns.v1.domain.Subdomain;
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
public class SubdomainsToPagedIterable extends Arg0ToPagedIterable<Subdomain, SubdomainsToPagedIterable> {

   private final DomainApi api;

   @Inject
   protected SubdomainsToPagedIterable(CloudDNSApi api) {
      this.api = checkNotNull(api, "api").getDomainApi();
   }

   @Override
   protected Function<Object, IterableWithMarker<Subdomain>> markerToNextForArg0(Optional<Object> domainId) {
      return new ListSubdomainsUnderDomainIdAtMarker(api, Integer.valueOf(domainId.get().toString()));
   }

   private static class ListSubdomainsUnderDomainIdAtMarker implements Function<Object, IterableWithMarker<Subdomain>> {
      private final DomainApi api;
      private final int domainId;

      @Inject
      protected ListSubdomainsUnderDomainIdAtMarker(DomainApi api, int domainId) {
         this.api = checkNotNull(api, "api");
         this.domainId = domainId;
      }

      public PaginatedCollection<Subdomain> apply(Object input) {
         PaginationOptions paginationOptions = (PaginationOptions) input;

         return api.listSubdomains(domainId, paginationOptions);
      }

      public String toString() {
         return "ListSubdomainsUnderDomainIdAtMarker(" + domainId + ")";
      }
   }
}
