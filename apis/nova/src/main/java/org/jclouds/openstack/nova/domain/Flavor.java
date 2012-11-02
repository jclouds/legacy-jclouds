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

import java.beans.ConstructorProperties;
import java.net.URI;
import java.util.List;
import java.util.Map;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * A flavor is an available hardware configuration for a server. Each flavor has a unique
 * combination of disk space and memory capacity.
 *
 * @author Adrian Cole
 */
public class Flavor extends Resource {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromFlavor(this);
   }

   public abstract static class Builder<T extends Builder<T>> extends Resource.Builder<T> {
      protected String name;
      protected Integer disk;
      protected Integer ram;
      protected Integer vcpus;

      /**
       * @see Flavor#getName()
       */
      public T name(String name) {
         this.name = name;
         return self();
      }

      /**
       * @see Flavor#getDisk()
       */
      public T disk(Integer disk) {
         this.disk = disk;
         return self();
      }

      /**
       * @see Flavor#getRam()
       */
      public T ram(Integer ram) {
         this.ram = ram;
         return self();
      }

      /**
       * @see Flavor#getVcpus()
       */
      public T vcpus(Integer vcpus) {
         this.vcpus = vcpus;
         return self();
      }

      public Flavor build() {
         return new Flavor(id, links, orderedSelfReferences, name, disk, ram, vcpus);
      }

      public T fromFlavor(Flavor in) {
         return super.fromResource(in)
               .name(in.getName())
               .disk(in.getDisk())
               .ram(in.getRam())
               .vcpus(in.getVcpus());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String name;
   private final Integer disk;
   private final Integer ram;
   private final Integer vcpus;

   @ConstructorProperties({
         "id", "links", "orderedSelfReferences", "name", "disk", "ram", "vcpus"
   })
   protected Flavor(int id, List<Map<String, String>> links, @Nullable Map<LinkType, URI> orderedSelfReferences,
                    @Nullable String name, @Nullable Integer disk, @Nullable Integer ram, @Nullable Integer vcpus) {
      super(id, links, orderedSelfReferences);
      this.name = name;
      this.disk = disk;
      this.ram = ram;
      this.vcpus = vcpus;
   }

   @Nullable
   public String getName() {
      return this.name;
   }

   @Nullable
   public Integer getDisk() {
      return this.disk;
   }

   @Nullable
   public Integer getRam() {
      return this.ram;
   }

   @Nullable
   public Integer getVcpus() {
      return this.vcpus;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), name, disk, ram, vcpus);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Flavor that = Flavor.class.cast(obj);
      return super.equals(that)
            && Objects.equal(this.name, that.name)
            && Objects.equal(this.disk, that.disk)
            && Objects.equal(this.ram, that.ram)
            && Objects.equal(this.vcpus, that.vcpus);
   }

   protected ToStringHelper string() {
      return super.string().add("name", name).add("disk", disk).add("ram", ram).add("vcpus", vcpus);
   }

}
