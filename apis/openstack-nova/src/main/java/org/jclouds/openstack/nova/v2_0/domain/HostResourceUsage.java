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
package org.jclouds.openstack.nova.v2_0.domain;

import static com.google.common.base.Preconditions.checkNotNull;

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

   public static abstract class Builder<T extends Builder<T>>  {
      protected abstract T self();

      private String host;
      private String project;
      private int memoryMb;
      private int cpu;
      private int diskGb;

      public T host(String host) {
         this.host = host;
         return self();
      }

      public T project(String project) {
         this.project = project;
         return self();
      }

      public T memoryMb(int memoryMb) {
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
               .project(in.getProject())
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
   
   protected HostResourceUsage() {
      // we want serializers like Gson to work w/o using sun.misc.Unsafe,
      // prohibited in GAE. This also implies fields are not final.
      // see http://code.google.com/p/jclouds/issues/detail?id=925
   }
  
   private String host;
   private String project;
   @SerializedName(value="memory_mb")
   private int memoryMb;
   private int cpu;
   @SerializedName(value="disk_gb")
   private int diskGb;

   protected HostResourceUsage(Builder<?> builder) {
      this.host = checkNotNull(builder.host, "host");
      this.project = builder.project;
      this.memoryMb = checkNotNull(builder.memoryMb, "memoryMb");
      this.cpu = checkNotNull(builder.cpu, "cpu");
      this.diskGb = checkNotNull(builder.diskGb, "diskGb");
   }

   /**
    */
   public String getHost() {
      return this.host;
   }

   /**
    */
   @Nullable
   public String getProject() {
      return this.project;
   }

   /**
    */
   public int getMemoryMb() {
      return this.memoryMb;
   }

   /**
    */
   public int getCpu() {
      return this.cpu;
   }

   /**
    */
   public int getDiskGb() {
      return this.diskGb;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(host, project, memoryMb, cpu, diskGb);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      HostResourceUsage that = HostResourceUsage.class.cast(obj);
      return Objects.equal(this.host, that.host)
            && Objects.equal(this.project, that.project)
            && Objects.equal(this.memoryMb, that.memoryMb)
            && Objects.equal(this.cpu, that.cpu)
            && Objects.equal(this.diskGb, that.diskGb);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper("")
            .add("host", host)
            .add("project", project)
            .add("memoryMb", memoryMb)
            .add("cpu", cpu)
            .add("diskGb", diskGb);
   }

   @Override
   public String toString() {
      return string().toString();
   }

}