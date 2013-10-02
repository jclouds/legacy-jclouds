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
package org.jclouds.dynect.v3.domain;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;

/**
 * @author Adrian Cole
 */
public class GeoService {

   private final String name;
   private final boolean active;
   private final int ttl;
   private final List<Node> nodes;
   private final List<GeoRegionGroup> groups;

   private GeoService(String name, boolean active, int ttl, List<Node> nodes, List<GeoRegionGroup> groups) {
      this.name = checkNotNull(name, "name");
      this.active = checkNotNull(active, "active");
      this.ttl = checkNotNull(ttl, "ttl");
      this.nodes = checkNotNull(nodes, "nodes of %s", name);
      this.groups = checkNotNull(groups, "groups of %s", name);
   }

   public String getName() {
      return name;
   }

   public boolean isActive() {
      return active;
   }

   public int getTTL() {
      return ttl;
   }

   public List<Node> getNodes() {
      return nodes;
   }

   public List<GeoRegionGroup> getGroups() {
      return groups;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(active, name, nodes, groups);
   }

   /**
    * permits equals comparisons with subtypes
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || !(obj instanceof GeoService))
         return false;
      GeoService that = GeoService.class.cast(obj);
      return equal(this.active, that.active) && equal(this.name, that.name) && equal(this.ttl, that.ttl)
            && equal(this.nodes, that.nodes) && equal(this.groups, that.groups);
   }

   @Override
   public String toString() {
      return toStringHelper(this).add("active", active).add("name", name).add("ttl", ttl).add("nodes", nodes).add("groups", groups)
            .toString();
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().from(this);
   }

   public static class Builder {

      private String name;
      private boolean active;
      protected int ttl = -1;
      private ImmutableList.Builder<Node> nodes = ImmutableList.builder();
      private ImmutableList.Builder<GeoRegionGroup> groups = ImmutableList.builder();

      /**
       * @see GeoService#getName()
       */
      public Builder name(String name) {
         this.name = name;
         return this;
      }

      /**
       * @see GeoService#isActive()
       */
      public Builder active(boolean active) {
         this.active = active;
         return this;
      }

      /**
       * @see Builder#getTTL()
       */
      public Builder ttl(int ttl) {
         this.ttl = ttl;
         return this;
      }

      /**
       * @see GeoService#getNodes()
       */
      public Builder addNode(Node node) {
         this.nodes.add(node);
         return this;
      }

      /**
       * replaces current region groups
       * 
       * @see GeoService#getNodes()
       */
      public Builder nodes(Iterable<Node> nodes) {
         this.nodes = ImmutableList.<Node> builder().addAll(nodes);
         return this;
      }

      /**
       * @see GeoService#getNodes()
       */
      public Builder addAllNodes(Iterable<Node> nodes) {
         this.nodes.addAll(nodes);
         return this;
      }

      /**
       * @see GeoService#getGroups()
       */
      public Builder addGroup(GeoRegionGroup group) {
         this.groups.add(group);
         return this;
      }

      /**
       * replaces current region groups
       * 
       * @see GeoService#getGroups()
       */
      public Builder groups(Iterable<GeoRegionGroup> groups) {
         this.groups = ImmutableList.<GeoRegionGroup> builder().addAll(groups);
         return this;
      }

      /**
       * @see GeoService#getGroups()
       */
      public Builder addAllGroups(Iterable<GeoRegionGroup> groups) {
         this.groups.addAll(groups);
         return this;
      }

      public GeoService build() {
         return new GeoService(name, active, ttl, nodes.build(), groups.build());
      }

      public Builder from(GeoService in) {
         return name(in.name).active(in.active).ttl(in.ttl).nodes(in.nodes).groups(in.groups);
      }
   }

}
