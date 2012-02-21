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
package org.jclouds.openstack.nova.v1_1.domain;

import static com.google.common.base.Objects.toStringHelper;

import java.util.Set;

import org.jclouds.openstack.domain.Link;
import org.jclouds.openstack.domain.Resource;

/**
 * A flavor is an available hardware configuration for a server. Each flavor has
 * a unique combination of disk space and memory capacity.
 * 
 * @author Jeremy Daggett
 * @see <a href=
 *      "http://docs.openstack.org/api/openstack-compute/1.1/content/Flavors-d1e4180.html"
 *      />
 */
public class Flavor extends Resource {
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromFlavor(this);
   }

   public static class Builder extends Resource.Builder {

      private int ram;
      private int disk;
      private int vcpus;

      public Builder ram(int ram) {
         this.ram = ram;
         return this;
      }

      public Builder disk(int disk) {
         this.disk = disk;
         return this;
      }
      
      public Builder vcpus(int vcpus) {
         this.vcpus = vcpus;
         return this;
      }
      
      public Flavor build() {
         return new Flavor(id, name, links, ram, disk, vcpus);
      }

      public Builder fromFlavor(Flavor in) {
         return fromResource(in).ram(in.getRam()).disk(in.getDisk()).vcpus(in.getVcpus());
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder id(String id) {
         return Builder.class.cast(super.id(id));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder name(String name) {
         return Builder.class.cast(super.name(name));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder links(Set<Link> links) {
         return Builder.class.cast(super.links(links));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromResource(Resource in) {
         return Builder.class.cast(super.fromResource(in));
      }
   }

   private int ram;
   private int disk;
   private int vcpus;

   protected Flavor(String id, String name, Set<Link> links, int ram, int disk,
         int vcpus) {
      super(id, name, links);
      this.ram = ram;
      this.disk = disk;
      this.vcpus = vcpus;
   }

   public int getRam() {
      return this.ram;
   }

   public int getDisk() {
      return this.disk;
   }

   public int getVcpus() {
      return this.vcpus;
   }

   @Override
   public String toString() {
      return toStringHelper("").add("id", id).add("name", name)
            .add("links", links).add("ram", ram).add("disk", disk)
            .add("vcpus", vcpus).toString();
   }

}
