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
package org.jclouds.openstack.nova.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Functions;
import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * @author Dmitri Babaev
 * @author Matt Stephenson
 */
public abstract class Resource {

   public static enum LinkType {
      BOOKMARK_JSON(new Predicate<Map<String, String>>() {
         @Override
         public boolean apply(@Nullable Map<String, String> linkMap) {
            return Functions.forMap(linkMap, "").apply("rel").equals("bookmark") &&
                  Functions.forMap(linkMap, "").apply("type").contains("json");
         }
      }),
      BOOKMARK_ANY(new Predicate<Map<String, String>>() {
         @Override
         public boolean apply(@Nullable Map<String, String> linkMap) {
            return Functions.forMap(linkMap, "").apply("rel").equals("bookmark");
         }
      }),
      SELF(new Predicate<Map<String, String>>() {
         @Override
         public boolean apply(@Nullable Map<String, String> linkMap) {
            return Functions.forMap(linkMap, "").apply("rel").equals("self");
         }
      });

      Predicate<Map<String, String>> linkPredicate;

      LinkType(Predicate<Map<String, String>> linkPredicate) {
         this.linkPredicate = linkPredicate;
      }
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected int id;
      protected List<Map<String, String>> links = ImmutableList.of();
      protected Map<Resource.LinkType, URI> orderedSelfReferences;

      /**
       * @see Resource#getId()
       */
      public T id(int id) {
         this.id = id;
         return self();
      }

      /**
       * @see Resource#getLinks()
       */
      public T links(List<Map<String, String>> links) {
         this.links = ImmutableList.copyOf(checkNotNull(links, "links"));
         return self();
      }

      public T links(Map<String, String>... in) {
         return links(ImmutableList.copyOf(in));
      }


      /**
       * @see Resource#getOrderedSelfReferences()
       */
      public T orderedSelfReferences(Map<Resource.LinkType, URI> orderedSelfReferences) {
         this.orderedSelfReferences = ImmutableMap.copyOf(orderedSelfReferences);
         return self();
      }

      public T fromResource(Resource in) {
         return this
               .links(in.getLinks())
               .orderedSelfReferences(in.getOrderedSelfReferences());
      }
   }

   private final int id;
   private final List<Map<String, String>> links;
   private final ConcurrentSkipListMap<Resource.LinkType, URI> orderedSelfReferences;

   protected Resource(int id, List<Map<String, String>> links, @Nullable Map<Resource.LinkType, URI> orderedSelfReferences) {
      this.id = id;
      this.links = links == null ? ImmutableList.<Map<String, String>>of() : ImmutableList.copyOf(checkNotNull(links, "links"));
      this.orderedSelfReferences = orderedSelfReferences == null ? new ConcurrentSkipListMap<LinkType, URI>() : new ConcurrentSkipListMap<LinkType, URI>(orderedSelfReferences);
   }

   public int getId() {
      return id;
   }

   public List<Map<String, String>> getLinks() {
      return this.links;
   }

   public Map<Resource.LinkType, URI> getOrderedSelfReferences() {
      return this.orderedSelfReferences;
   }

   private void populateOrderedSelfReferences() {
      for (Map<String, String> linkProperties : links) {
         for (LinkType type : LinkType.values()) {
            if (type.linkPredicate.apply(linkProperties)) {
               try {
                  orderedSelfReferences.put(type, new URI(linkProperties.get("href")));
               } catch (URISyntaxException e) {
                  throw new RuntimeException(e);
               }
            }
         }
      }
      if (orderedSelfReferences.isEmpty())
         throw new IllegalStateException("URI is not available");
   }

   public URI getURI() {
      if (orderedSelfReferences.isEmpty())
         populateOrderedSelfReferences();

      return orderedSelfReferences.firstEntry().getValue();
   }

   public URI getSelfURI() {
      if (orderedSelfReferences.isEmpty())
         populateOrderedSelfReferences();

      return orderedSelfReferences.get(LinkType.SELF);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, orderedSelfReferences);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Resource that = Resource.class.cast(obj);
      return Objects.equal(id, that.id) && Objects.equal(this.orderedSelfReferences, that.orderedSelfReferences);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("id", id).add("links", links).add("orderedSelfReferences", orderedSelfReferences);
   }

   @Override
   public String toString() {
      return string().toString();
   }

}
