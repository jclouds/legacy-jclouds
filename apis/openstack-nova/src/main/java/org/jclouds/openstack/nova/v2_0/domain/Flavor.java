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
package org.jclouds.openstack.nova.v2_0.domain;

import java.beans.ConstructorProperties;

import javax.inject.Named;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.openstack.v2_0.domain.Link;
import org.jclouds.openstack.v2_0.domain.Resource;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.base.Optional;

/**
 * A flavor is an available hardware configuration for a server. Each flavor has
 * a unique combination of disk space and memory capacity.
 * 
 * @author Jeremy Daggett
 * @see <a href=
      "http://docs.openstack.org/api/openstack-compute/1.1/content/Flavors-d1e4180.html"
      />
*/
public class Flavor extends Resource {

   public static Builder<?> builder() { 
      return new ConcreteBuilder();
   }
   
   public Builder<?> toBuilder() { 
      return new ConcreteBuilder().fromFlavor(this);
   }

   public static abstract class Builder<T extends Builder<T>> extends Resource.Builder<T>  {
      protected int ram;
      protected int disk;
      protected int vcpus;
      protected String swap;
      protected Double rxtxFactor;
      protected Integer ephemeral;
   
      /** 
       * @see Flavor#getRam()
       */
      public T ram(int ram) {
         this.ram = ram;
         return self();
      }

      /** 
       * @see Flavor#getDisk()
       */
      public T disk(int disk) {
         this.disk = disk;
         return self();
      }

      /** 
       * @see Flavor#getVcpus()
       */
      public T vcpus(int vcpus) {
         this.vcpus = vcpus;
         return self();
      }

      /** 
       * @see Flavor#getSwap()
       */
      public T swap(String swap) {
         this.swap = swap;
         return self();
      }

      /** 
       * @see Flavor#getRxtxFactor()
       */
      public T rxtxFactor(Double rxtxFactor) {
         this.rxtxFactor = rxtxFactor;
         return self();
      }

      /** 
       * @see Flavor#getEphemeral()
       */
      public T ephemeral(Integer ephemeral) {
         this.ephemeral = ephemeral;
         return self();
      }

      public Flavor build() {
         return new Flavor(id, name, links, ram, disk, vcpus, swap, rxtxFactor, ephemeral);
      }
      
      public T fromFlavor(Flavor in) {
         return super.fromResource(in)
                  .ram(in.getRam())
                  .disk(in.getDisk())
                  .vcpus(in.getVcpus())
                  .swap(in.getSwap().orNull())
                  .rxtxFactor(in.getRxtxFactor().orNull())
                  .ephemeral(in.getEphemeral().orNull());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final int ram;
   private final int disk;
   private final int vcpus;
   private final Optional<String> swap;
   @Named("rxtx_factor")
   private final Optional<Double> rxtxFactor;
   @Named("OS-FLV-EXT-DATA:ephemeral")
   private final Optional<Integer> ephemeral;

   @ConstructorProperties({
      "id", "name", "links", "ram", "disk", "vcpus", "swap", "rxtx_factor", "OS-FLV-EXT-DATA:ephemeral"
   })
   protected Flavor(String id, @Nullable String name, java.util.Set<Link> links, int ram, int disk, int vcpus,
                    @Nullable String swap, @Nullable Double rxtxFactor, @Nullable Integer ephemeral) {
      super(id, name, links);
      this.ram = ram;
      this.disk = disk;
      this.vcpus = vcpus;
      this.swap = Optional.fromNullable(swap);
      this.rxtxFactor = Optional.fromNullable(rxtxFactor);
      this.ephemeral = Optional.fromNullable(ephemeral);
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

   public Optional<String> getSwap() {
      return this.swap;
   }

   public Optional<Double> getRxtxFactor() {
      return this.rxtxFactor;
   }

   /**
    * Retrieves ephemeral disk space in GB
    * <p/>
    * NOTE: This field is only present if the Flavor Extra Data extension is installed (alias "OS-FLV-EXT-DATA").
    * 
    * @see org.jclouds.openstack.nova.v2_0.features.ExtensionClient#getExtensionByAlias
    * @see org.jclouds.openstack.nova.v2_0.extensions.ExtensionNamespaces#FLAVOR_EXTRA_DATA
    */
   public Optional<Integer> getEphemeral() {
      return this.ephemeral;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(ram, disk, vcpus, swap, rxtxFactor, ephemeral);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Flavor that = Flavor.class.cast(obj);
      return super.equals(that) && Objects.equal(this.ram, that.ram)
               && Objects.equal(this.disk, that.disk)
               && Objects.equal(this.vcpus, that.vcpus)
               && Objects.equal(this.swap, that.swap)
               && Objects.equal(this.rxtxFactor, that.rxtxFactor)
               && Objects.equal(this.ephemeral, that.ephemeral);
   }
   
   protected ToStringHelper string() {
      return super.string()
            .add("ram", ram).add("disk", disk).add("vcpus", vcpus).add("swap", swap).add("rxtxFactor", rxtxFactor).add("ephemeral", ephemeral);
   }
   
}
