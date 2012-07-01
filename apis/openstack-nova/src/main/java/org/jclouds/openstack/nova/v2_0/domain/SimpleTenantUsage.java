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

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.util.Date;
import java.util.Set;

import javax.inject.Named;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableSet;

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

   public static abstract class Builder<T extends Builder<T>>  {
      protected abstract T self();

      protected String tenantId;
      protected double totalLocalGbUsage;
      protected double totalVcpusUsage;
      protected double totalMemoryMbUsage;
      protected double totalHours;
      protected Date start;
      protected Date stop;
      protected Set<SimpleServerUsage> serverUsages = ImmutableSet.of();
   
      /** 
       * @see SimpleTenantUsage#getTenantId()
       */
      public T tenantId(String tenantId) {
         this.tenantId = tenantId;
         return self();
      }

      /** 
       * @see SimpleTenantUsage#getTotalLocalGbUsage()
       */
      public T totalLocalGbUsage(double totalLocalGbUsage) {
         this.totalLocalGbUsage = totalLocalGbUsage;
         return self();
      }

      /** 
       * @see SimpleTenantUsage#getTotalVcpusUsage()
       */
      public T totalVcpusUsage(double totalVcpusUsage) {
         this.totalVcpusUsage = totalVcpusUsage;
         return self();
      }

      /** 
       * @see SimpleTenantUsage#getTotalMemoryMbUsage()
       */
      public T totalMemoryMbUsage(double totalMemoryMbUsage) {
         this.totalMemoryMbUsage = totalMemoryMbUsage;
         return self();
      }

      /** 
       * @see SimpleTenantUsage#getTotalHours()
       */
      public T totalHours(double totalHours) {
         this.totalHours = totalHours;
         return self();
      }

      /** 
       * @see SimpleTenantUsage#getStart()
       */
      public T start(Date start) {
         this.start = start;
         return self();
      }

      /** 
       * @see SimpleTenantUsage#getStop()
       */
      public T stop(Date stop) {
         this.stop = stop;
         return self();
      }

      /** 
       * @see SimpleTenantUsage#getServerUsages()
       */
      public T serverUsages(Set<SimpleServerUsage> serverUsages) {
         this.serverUsages = ImmutableSet.copyOf(checkNotNull(serverUsages, "serverUsages"));      
         return self();
      }

      public T serverUsages(SimpleServerUsage... in) {
         return serverUsages(ImmutableSet.copyOf(in));
      }

      public SimpleTenantUsage build() {
         return new SimpleTenantUsage(tenantId, totalLocalGbUsage, totalVcpusUsage, totalMemoryMbUsage, totalHours, start, stop, serverUsages);
      }
      
      public T fromSimpleTenantUsage(SimpleTenantUsage in) {
         return this
                  .tenantId(in.getTenantId())
                  .totalLocalGbUsage(in.getTotalLocalGbUsage())
                  .totalVcpusUsage(in.getTotalVcpusUsage())
                  .totalMemoryMbUsage(in.getTotalMemoryMbUsage())
                  .totalHours(in.getTotalHours())
                  .start(in.getStart())
                  .stop(in.getStop())
                  .serverUsages(in.getServerUsages());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   @Named("tenant_id")
   private final String tenantId;
   @Named("total_local_gb_usage")
   private final double totalLocalGbUsage;
   @Named("total_vcpus_usage")
   private final double totalVcpusUsage;
   @Named("total_memory_mb_usage")
   private final double totalMemoryMbUsage;
   @Named("total_hours")
   private final double totalHours;
   private final Date start;
   private final Date stop;
   @Named("server_usages")
   private final Set<SimpleServerUsage> serverUsages;

   @ConstructorProperties({
      "tenant_id", "total_local_gb_usage", "total_vcpus_usage", "total_memory_mb_usage", "total_hours", "start", "stop", "server_usages"
   })
   protected SimpleTenantUsage(String tenantId, double totalLocalGbUsage, double totalVcpusUsage, double totalMemoryMbUsage, double totalHours, @Nullable Date start, @Nullable Date stop, @Nullable Set<SimpleServerUsage> serverUsages) {
      this.tenantId = checkNotNull(tenantId, "tenantId");
      this.totalLocalGbUsage = totalLocalGbUsage;
      this.totalVcpusUsage = totalVcpusUsage;
      this.totalMemoryMbUsage = totalMemoryMbUsage;
      this.totalHours = totalHours;
      this.start = start;
      this.stop = stop;
      this.serverUsages = serverUsages == null ? ImmutableSet.<SimpleServerUsage>of() : ImmutableSet.copyOf(serverUsages);      
   }

   public String getTenantId() {
      return this.tenantId;
   }

   public double getTotalLocalGbUsage() {
      return this.totalLocalGbUsage;
   }

   public double getTotalVcpusUsage() {
      return this.totalVcpusUsage;
   }

   public double getTotalMemoryMbUsage() {
      return this.totalMemoryMbUsage;
   }

   public double getTotalHours() {
      return this.totalHours;
   }

   @Nullable
   public Date getStart() {
      return this.start;
   }

   @Nullable
   public Date getStop() {
      return this.stop;
   }

   public Set<SimpleServerUsage> getServerUsages() {
      return this.serverUsages;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(tenantId, totalLocalGbUsage, totalVcpusUsage, totalMemoryMbUsage, totalHours, start, stop, serverUsages);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      SimpleTenantUsage that = SimpleTenantUsage.class.cast(obj);
      return Objects.equal(this.tenantId, that.tenantId)
               && Objects.equal(this.totalLocalGbUsage, that.totalLocalGbUsage)
               && Objects.equal(this.totalVcpusUsage, that.totalVcpusUsage)
               && Objects.equal(this.totalMemoryMbUsage, that.totalMemoryMbUsage)
               && Objects.equal(this.totalHours, that.totalHours)
               && Objects.equal(this.start, that.start)
               && Objects.equal(this.stop, that.stop)
               && Objects.equal(this.serverUsages, that.serverUsages);
   }
   
   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("tenantId", tenantId).add("totalLocalGbUsage", totalLocalGbUsage).add("totalVcpusUsage", totalVcpusUsage).add("totalMemoryMbUsage", totalMemoryMbUsage).add("totalHours", totalHours).add("start", start).add("stop", stop).add("serverUsages", serverUsages);
   }
   
   @Override
   public String toString() {
      return string().toString();
   }

}
