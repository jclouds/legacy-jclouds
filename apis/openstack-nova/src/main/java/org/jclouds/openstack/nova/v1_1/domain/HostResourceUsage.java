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

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.gson.annotations.SerializedName;

/**
 * Class HostResourceUsage
 */
public class HostResourceUsage {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromHostResourceUsage(this);
   }

   public static abstract class Builder<T extends Builder<T>> {
      protected abstract T self();

      private String host;
      private String memoryMb;
      private int cpu;
      private int diskGb;

      public T host(String host) {
         this.host = host;
         return self();
      }

      public T memoryMb(String memoryMb) {
         this.memoryMb = memoryMb;
         return self();
      }

      public T cpu(int cpu) {
         this.cpu = cpu;
         return self();
      }

      public T diskGb(int diskGb) {
         this.diskGb = diskGb;
         return self();
      }

      public HostResourceUsage build() {
         return new HostResourceUsage(this);
      }

      public T fromHostResourceUsage(HostResourceUsage in) {
         return this
               .host(in.getHost())
               .memoryMb(in.getMemoryMb())
               .cpu(in.getCpu())
               .diskGb(in.getDiskGb())
               ;
      }

   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String host;
   @SerializedName(value = "memory_mb")
   private final String memoryMb;
   private final int cpu;
   @SerializedName(value = "disk_gb")
   private final int diskGb;

   protected HostResourceUsage(Builder<?> builder) {
      this.host = builder.host;
      this.memoryMb = builder.memoryMb;
      this.cpu = builder.cpu;
      this.diskGb = builder.diskGb;
   }

   /**
    */
   @Nullable
   public String getHost() {
      return this.host;
   }

   /**
    */
   @Nullable
   public String getMemoryMb() {
      return this.memoryMb;
   }

   /**
    */
   @Nullable
   public int getCpu() {
      return this.cpu;
   }

   /**
    */
   @Nullable
   public int getDiskGb() {
      return this.diskGb;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(host, memoryMb, cpu, diskGb);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      HostResourceUsage that = HostResourceUsage.class.cast(obj);
      return Objects.equal(this.host, that.host)
            && Objects.equal(this.memoryMb, that.memoryMb)
            && Objects.equal(this.cpu, that.cpu)
            && Objects.equal(this.diskGb, that.diskGb)
            ;
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper("")
            .add("host", host)
            .add("memoryMb", memoryMb)
            .add("cpu", cpu)
            .add("diskGb", diskGb)
            ;
   }

   @Override
   public String toString() {
      return string().toString();
   }

}