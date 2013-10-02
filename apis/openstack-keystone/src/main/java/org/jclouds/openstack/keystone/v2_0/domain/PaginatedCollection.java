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
package org.jclouds.openstack.keystone.v2_0.domain;

import static org.jclouds.http.utils.Queries.queryParser;

import java.util.Collection;
import java.util.Iterator;

import org.jclouds.collect.IterableWithMarker;
import org.jclouds.openstack.v2_0.domain.Link;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * base class for a paginated collection in openstack
 * 
 * @see <a
 *      href="http://docs.openstack.org/api/openstack-identity-service/2.0/content/Paginated_Collections-d1e325.html">
 *      docs</a>
 * @author Adrian Cole
 */
@Beta
public class PaginatedCollection<T> extends IterableWithMarker<T> {
   private Iterable<T> resources;
   private Iterable<Link> links;

   protected PaginatedCollection(Iterable<T> resources, Iterable<Link> links) {
      this.resources = resources != null ? resources : ImmutableSet.<T> of();
      this.links = links != null ? links : ImmutableSet.<Link> of();
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

   @Override
   public Optional<Object> nextMarker() {
      return FluentIterable.from(getLinks()).filter(new Predicate<Link>() {
         @Override
         public boolean apply(Link link) {
            return Link.Relation.NEXT == link.getRelation();
         }
      }).transform(new Function<Link, Optional<Object>>() {
         @Override
         public Optional<Object> apply(Link link) {
            Collection<String> markers = queryParser().apply(link.getHref().getRawQuery()).get("marker");
            return Optional.<Object> fromNullable(markers == null ? null : Iterables.get(markers, 0));
         }
      }).first().or(Optional.absent());
   }

}
