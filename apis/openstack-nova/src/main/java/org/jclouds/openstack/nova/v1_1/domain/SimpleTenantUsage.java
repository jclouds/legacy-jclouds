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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collections;
import java.util.Date;
import java.util.Set;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.gson.annotations.SerializedName;

/**
 * Information the SimpleTenantUsage extension returns data about each tenant
 *
 * @author Adam Lowe
 */
public class SimpleTenantUsage {
   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromSimpleTenantUsage(this);
   }

   public static abstract class Builder<T extends Builder<T>> {
      private String tenantId;
      private double totalLocalGbUsage;
      private double totalVcpusUsage;
      private double totalMemoryMbUsage;
      private double totalHours;
      private Date start;
      private Date stop;
      private Set<SimpleServerUsage> serverUsages = Sets.newLinkedHashSet();

      protected abstract T self();

      public T tenantId(String tenantId) {
         this.tenantId = tenantId;
         return self();
      }
      
      public T totalLocalGbUsage(double total_local_gb_usage) {
         this.totalLocalGbUsage = total_local_gb_usage;
         return self();
      }

      public T totalVcpusUsage(double total_vcpus_usage) {
         this.totalVcpusUsage = total_vcpus_usage;
         return self();
      }

      public T totalMemoryMbUsage(double total_memory_mb_usage) {
         this.totalMemoryMbUsage = total_memory_mb_usage;
         return self();
      }

      public T totalHours(double total_hours) {
         this.totalHours = total_hours;
         return self();
      }

      public T start(Date start) {
         this.start = start;
         return self();
      }

      public T stop(Date stop) {
         this.stop = stop;
         return self();
      }

      public T serverUsages(Set<SimpleServerUsage> serverUsages) {
         this.serverUsages = serverUsages;
         return self();
      }

      public SimpleTenantUsage build() {
         return new SimpleTenantUsage(this);
      }


      public T fromSimpleTenantUsage(SimpleTenantUsage in) {
         return this
               .totalLocalGbUsage(in.getTotalLocalGbUsage())
               .totalVcpusUsage(in.getTotalVcpusUsage())
               .totalMemoryMbUsage(in.getTotalMemoryMbUsage())
               .totalHours(in.getTotalHours())
               .start(in.getStart())
               .stop(in.getStop())
               .serverUsages(in.getServerUsages())
               ;
      }

   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }
   
   protected SimpleTenantUsage() {
      // we want serializers like Gson to work w/o using sun.misc.Unsafe,
      // prohibited in GAE. This also implies fields are not final.
      // see http://code.google.com/p/jclouds/issues/detail?id=925
   }
  
   @SerializedName("tenant_id")
   private String tenantId;
   @SerializedName("total_local_gb_usage")
   private double totalLocalGbUsage;
   @SerializedName("total_vcpus_usage")
   private double totalVcpusUsage;
   @SerializedName("total_memory_mb_usage")
   private double totalMemoryMbUsage;
   @SerializedName("total_hours")
   private double totalHours;
   private Date start;
   private Date stop;
   @SerializedName("server_usages")
   private Set<SimpleServerUsage> serverUsages = ImmutableSet.of();

   private SimpleTenantUsage(Builder<?> builder) {
      this.tenantId = builder.tenantId;
      this.totalLocalGbUsage = builder.totalLocalGbUsage;
      this.totalVcpusUsage = builder.totalVcpusUsage;
      this.totalMemoryMbUsage = builder.totalMemoryMbUsage;
      this.totalHours = builder.totalHours;
      this.start = builder.start;
      this.stop = builder.stop;
      this.serverUsages = ImmutableSet.copyOf(checkNotNull(builder.serverUsages, "serverUsages"));
   }

   public String getTenantId() {
      return tenantId;
   }

   /**
    */
   public double getTotalLocalGbUsage() {
      return this.totalLocalGbUsage;
   }

   /**
    */
   public double getTotalVcpusUsage() {
      return this.totalVcpusUsage;
   }

   /**
    */
   public double getTotalMemoryMbUsage() {
      return this.totalMemoryMbUsage;
   }

   /**
    */
   public double getTotalHours() {
      return this.totalHours;
   }

   /**
    */
   @Nullable
   public Date getStart() {
      return this.start;
   }

   /**
    */
   @Nullable
   public Date getStop() {
      return this.stop;
   }

   /**
    */
   @Nullable
   public Set<SimpleServerUsage> getServerUsages() {
      return serverUsages == null ? ImmutableSet.<SimpleServerUsage>of() : Collections.unmodifiableSet(this.serverUsages);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(totalLocalGbUsage, totalVcpusUsage, totalMemoryMbUsage, totalHours, start, stop, serverUsages);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      SimpleTenantUsage that = SimpleTenantUsage.class.cast(obj);
      return Objects.equal(this.totalLocalGbUsage, that.totalLocalGbUsage)
            && Objects.equal(this.totalVcpusUsage, that.totalVcpusUsage)
            && Objects.equal(this.totalMemoryMbUsage, that.totalMemoryMbUsage)
            && Objects.equal(this.totalHours, that.totalHours)
            && Objects.equal(this.start, that.start)
            && Objects.equal(this.stop, that.stop)
            && Objects.equal(this.serverUsages, that.serverUsages)
            ;
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper("")
            .add("totalLocalGbUsage", totalLocalGbUsage)
            .add("totalVcpusUsage", totalVcpusUsage)
            .add("totalMemoryMbUsage", totalMemoryMbUsage)
            .add("totalHours", totalHours)
            .add("start", start)
            .add("stop", stop)
            .add("serverUsages", serverUsages)
            ;
   }

   @Override
   public String toString() {
      return string().toString();
   }

}
