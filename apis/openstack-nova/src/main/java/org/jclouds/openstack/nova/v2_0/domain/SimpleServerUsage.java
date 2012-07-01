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

import javax.inject.Named;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Information the SimpleTenantUsage extension return data about each Server
 * 
 * @author Adam Lowe
*/
public class SimpleServerUsage {

   /**
    */
   public static enum Status {
      
      UNRECOGNIZED, ACTIVE;
      
      public String value() {
      return name();
      }
      
      public static Status fromValue(String v) {
      try {
      return valueOf(v.toUpperCase());
      } catch (IllegalArgumentException e) {
      return UNRECOGNIZED;
      }
      }
      
   }

   public static Builder<?> builder() { 
      return new ConcreteBuilder();
   }
   
   public Builder<?> toBuilder() { 
      return new ConcreteBuilder().fromSimpleServerUsage(this);
   }

   public static abstract class Builder<T extends Builder<T>>  {
      protected abstract T self();

      protected String instanceName;
      protected double hours;
      protected double flavorMemoryMb;
      protected double flavorLocalGb;
      protected double flavorVcpus;
      protected String tenantId;
      protected String flavorName;
      protected Date instanceCreated;
      protected Date instanceTerminiated;
      protected SimpleServerUsage.Status instanceStatus;
      protected long uptime;
   
      /** 
       * @see SimpleServerUsage#getInstanceName()
       */
      public T instanceName(String instanceName) {
         this.instanceName = instanceName;
         return self();
      }

      /** 
       * @see SimpleServerUsage#getHours()
       */
      public T hours(double hours) {
         this.hours = hours;
         return self();
      }

      /** 
       * @see SimpleServerUsage#getFlavorMemoryMb()
       */
      public T flavorMemoryMb(double flavorMemoryMb) {
         this.flavorMemoryMb = flavorMemoryMb;
         return self();
      }

      /** 
       * @see SimpleServerUsage#getFlavorLocalGb()
       */
      public T flavorLocalGb(double flavorLocalGb) {
         this.flavorLocalGb = flavorLocalGb;
         return self();
      }

      /** 
       * @see SimpleServerUsage#getFlavorVcpus()
       */
      public T flavorVcpus(double flavorVcpus) {
         this.flavorVcpus = flavorVcpus;
         return self();
      }

      /** 
       * @see SimpleServerUsage#getTenantId()
       */
      public T tenantId(String tenantId) {
         this.tenantId = tenantId;
         return self();
      }

      /** 
       * @see SimpleServerUsage#getFlavorName()
       */
      public T flavorName(String flavorName) {
         this.flavorName = flavorName;
         return self();
      }

      /** 
       * @see SimpleServerUsage#getInstanceCreated()
       */
      public T instanceCreated(Date instanceCreated) {
         this.instanceCreated = instanceCreated;
         return self();
      }

      /** 
       * @see SimpleServerUsage#getInstanceTerminiated()
       */
      public T instanceTerminiated(Date instanceTerminiated) {
         this.instanceTerminiated = instanceTerminiated;
         return self();
      }

      /** 
       * @see SimpleServerUsage#getInstanceStatus()
       */
      public T instanceStatus(SimpleServerUsage.Status instanceStatus) {
         this.instanceStatus = instanceStatus;
         return self();
      }

      /** 
       * @see SimpleServerUsage#getUptime()
       */
      public T uptime(long uptime) {
         this.uptime = uptime;
         return self();
      }

      public SimpleServerUsage build() {
         return new SimpleServerUsage(instanceName, hours, flavorMemoryMb, flavorLocalGb, flavorVcpus, tenantId, flavorName, instanceCreated, instanceTerminiated, instanceStatus, uptime);
      }
      
      public T fromSimpleServerUsage(SimpleServerUsage in) {
         return this
                  .instanceName(in.getInstanceName())
                  .hours(in.getHours())
                  .flavorMemoryMb(in.getFlavorMemoryMb())
                  .flavorLocalGb(in.getFlavorLocalGb())
                  .flavorVcpus(in.getFlavorVcpus())
                  .tenantId(in.getTenantId())
                  .flavorName(in.getFlavorName())
                  .instanceCreated(in.getInstanceCreated())
                  .instanceTerminiated(in.getInstanceTerminiated())
                  .instanceStatus(in.getInstanceStatus())
                  .uptime(in.getUptime());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   @Named("name")
   private final String instanceName;
   private final double hours;
   @Named("memory_mb")
   private final double flavorMemoryMb;
   @Named("local_gb")
   private final double flavorLocalGb;
   @Named("vcpus")
   private final double flavorVcpus;
   @Named("tenant_id")
   private final String tenantId;
   @Named("flavor")
   private final String flavorName;
   @Named("started_at")
   private final Date instanceCreated;
   @Named("ended_at")
   private final Date instanceTerminiated;
   @Named("state")
   private final SimpleServerUsage.Status instanceStatus;
   private final long uptime;

   @ConstructorProperties({
      "name", "hours", "memory_mb", "local_gb", "vcpus", "tenant_id", "flavor", "started_at", "ended_at", "state", "uptime"
   })
   protected SimpleServerUsage(String instanceName, double hours, double flavorMemoryMb, double flavorLocalGb, double flavorVcpus, String tenantId, String flavorName, Date instanceCreated, @Nullable Date instanceTerminiated, SimpleServerUsage.Status instanceStatus, long uptime) {
      this.instanceName = checkNotNull(instanceName, "instanceName");
      this.hours = hours;
      this.flavorMemoryMb = flavorMemoryMb;
      this.flavorLocalGb = flavorLocalGb;
      this.flavorVcpus = flavorVcpus;
      this.tenantId = checkNotNull(tenantId, "tenantId");
      this.flavorName = checkNotNull(flavorName, "flavorName");
      this.instanceCreated = checkNotNull(instanceCreated, "instanceCreated");
      this.instanceTerminiated = instanceTerminiated;
      this.instanceStatus = checkNotNull(instanceStatus, "instanceStatus");
      this.uptime = uptime;
   }

   public String getInstanceName() {
      return this.instanceName;
   }

   public double getHours() {
      return this.hours;
   }

   public double getFlavorMemoryMb() {
      return this.flavorMemoryMb;
   }

   public double getFlavorLocalGb() {
      return this.flavorLocalGb;
   }

   public double getFlavorVcpus() {
      return this.flavorVcpus;
   }

   public String getTenantId() {
      return this.tenantId;
   }

   public String getFlavorName() {
      return this.flavorName;
   }

   public Date getInstanceCreated() {
      return this.instanceCreated;
   }

   @Nullable
   public Date getInstanceTerminiated() {
      return this.instanceTerminiated;
   }

   public SimpleServerUsage.Status getInstanceStatus() {
      return this.instanceStatus;
   }

   public long getUptime() {
      return this.uptime;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(instanceName, hours, flavorMemoryMb, flavorLocalGb, flavorVcpus, tenantId, flavorName, instanceCreated, instanceTerminiated, instanceStatus, uptime);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      SimpleServerUsage that = SimpleServerUsage.class.cast(obj);
      return Objects.equal(this.instanceName, that.instanceName)
               && Objects.equal(this.hours, that.hours)
               && Objects.equal(this.flavorMemoryMb, that.flavorMemoryMb)
               && Objects.equal(this.flavorLocalGb, that.flavorLocalGb)
               && Objects.equal(this.flavorVcpus, that.flavorVcpus)
               && Objects.equal(this.tenantId, that.tenantId)
               && Objects.equal(this.flavorName, that.flavorName)
               && Objects.equal(this.instanceCreated, that.instanceCreated)
               && Objects.equal(this.instanceTerminiated, that.instanceTerminiated)
               && Objects.equal(this.instanceStatus, that.instanceStatus)
               && Objects.equal(this.uptime, that.uptime);
   }
   
   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("instanceName", instanceName).add("hours", hours).add("flavorMemoryMb", flavorMemoryMb).add("flavorLocalGb", flavorLocalGb).add("flavorVcpus", flavorVcpus).add("tenantId", tenantId).add("flavorName", flavorName).add("instanceCreated", instanceCreated).add("instanceTerminiated", instanceTerminiated).add("instanceStatus", instanceStatus).add("uptime", uptime);
   }
   
   @Override
   public String toString() {
      return string().toString();
   }

}
