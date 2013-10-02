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
package org.jclouds.rackspace.cloudidentity.v2_0.domain;

import static org.jclouds.http.utils.Queries.queryParser;

import java.util.Iterator;

import org.jclouds.collect.IterableWithMarker;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.openstack.v2_0.domain.Link;
import org.jclouds.rackspace.cloudidentity.v2_0.options.PaginationOptions;

import com.google.common.annotations.Beta;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;

/**
 * Base class for a paginated collection in Rackspace.
 * 
 * @author Everett Toews
 */
@Beta
public class PaginatedCollection<T> extends IterableWithMarker<T> {
   private Iterable<T> resources;
   private Iterable<Link> links;
   private int totalEntries;

   protected PaginatedCollection(@Nullable Iterable<T> resources, @Nullable Iterable<Link> links, int totalEntries) {
      this.resources = resources != null ? resources : ImmutableSet.<T> of();
      this.links = links != null ? links : ImmutableSet.<Link> of();
      this.totalEntries = totalEntries;
   }

   @Override
   public Iterator<T> iterator() {
      return resources.iterator();
   }

   /**
    * links that relate to this collection
    */
   public Iterable<Link> getLinks() {
      return links;
   }

   public int getTotalEntries() {
      return totalEntries;
   }

   public PaginationOptions nextPaginationOptions() {
      return PaginationOptions.class.cast(nextMarker().get());
   }

   @Override
   public Optional<Object> nextMarker() {
      for (Link link: getLinks()) {
         if (Link.Relation.NEXT == link.getRelation()) {
            return Optional.<Object> of(toPaginationOptions(link));
         }
      }

      return Optional.absent();
   }

   private Object toPaginationOptions(Link link) {
      Multimap<String, String> queryParams = queryParser().apply(link.getHref().getRawQuery());
      PaginationOptions paginationOptions = PaginationOptions.Builder.queryParameters(queryParams);

      return paginationOptions;
   }
}
