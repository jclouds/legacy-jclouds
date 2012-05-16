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

import org.jclouds.openstack.domain.Resource;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.gson.annotations.SerializedName;

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
   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromFlavor(this);
   }

   public static abstract class Builder<T extends Builder<T>> extends Resource.Builder<T>  {
      private int ram;
      private int disk;
      private int vcpus;
      private String swap;
      private Double rxtxFactor;
      private Integer ephemeral;


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
       * @see Flavor#getVcpus()
       */
      public T swap(String swap) {
         this.swap = swap;
         return self();
      }

      /**
       * @see Flavor#getVcpus()
       */
      public T rxtxFactor(Double rxtxFactor) {
         this.rxtxFactor = rxtxFactor;
         return self();
      }

      /**
       * @see Flavor#getVcpus()
       */
      public T ephemeral(Integer ephemeral) {
         this.ephemeral = ephemeral;
         return self();
      }


      public Flavor build() {
         return new Flavor(this);
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
   
   protected Flavor() {
      // we want serializers like Gson to work w/o using sun.misc.Unsafe,
      // prohibited in GAE. This also implies fields are not final.
      // see http://code.google.com/p/jclouds/issues/detail?id=925
   }
   
   private int ram;
   private int disk;
   private int vcpus;
   private Optional<String> swap = Optional.absent();
   @SerializedName("rxtx_factor")
   private Optional<Double> rxtxFactor = Optional.absent();
   @SerializedName("OS-FLV-EXT-DATA:ephemeral")
   private Optional<Integer> ephemeral = Optional.absent();

   protected Flavor(Builder<?> builder) {
      super(builder);
      this.ram = builder.ram;
      this.disk = builder.disk;
      this.vcpus = builder.vcpus;
      this.swap = Optional.fromNullable(builder.swap);
      this.rxtxFactor = Optional.fromNullable(builder.rxtxFactor);
      this.ephemeral = Optional.fromNullable(builder.ephemeral);
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
      return swap;
   }

   public Optional<Double> getRxtxFactor() {
      return rxtxFactor;
   }

   /**
    * Retrieves ephemeral disk space in GB
    * <p/>
    * NOTE: This field is only present if the Flavor Extra Data extension is installed (alias "OS-FLV-EXT-DATA").
    *
    * @see org.jclouds.openstack.nova.v1_1.features.ExtensionClient#getExtensionByAlias
    * @see org.jclouds.openstack.nova.v1_1.extensions.ExtensionNamespaces#FLAVOR_EXTRA_DATA
    */
   public Optional<Integer> getEphemeral() {
      return ephemeral;
   }

   @Override
   protected Objects.ToStringHelper string() {
      return super.string()
            .add("ram", ram)
            .add("disk", disk)
            .add("vcpus", vcpus)
            .add("swap", swap)
            .add("rxtxFactor", rxtxFactor)
            .add("ephemeral", ephemeral);
   }
}