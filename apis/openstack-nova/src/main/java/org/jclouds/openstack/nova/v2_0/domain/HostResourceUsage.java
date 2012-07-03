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

import javax.inject.Named;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

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

      protected String host;
      protected String project;
      protected int memoryMb;
      protected int cpu;
      protected int diskGb;
   
      /** 
       * @see HostResourceUsage#getHost()
       */
      public T host(String host) {
         this.host = host;
         return self();
      }

      /** 
       * @see HostResourceUsage#getProject()
       */
      public T project(String project) {
         this.project = project;
         return self();
      }

      /** 
       * @see HostResourceUsage#getMemoryMb()
       */
      public T memoryMb(int memoryMb) {
         this.memoryMb = memoryMb;
         return self();
      }

      /** 
       * @see HostResourceUsage#getCpu()
       */
      public T cpu(int cpu) {
         this.cpu = cpu;
         return self();
      }

      /** 
       * @see HostResourceUsage#getDiskGb()
       */
      public T diskGb(int diskGb) {
         this.diskGb = diskGb;
         return self();
      }

      public HostResourceUsage build() {
         return new HostResourceUsage(host, project, memoryMb, cpu, diskGb);
      }
      
      public T fromHostResourceUsage(HostResourceUsage in) {
         return this
                  .host(in.getHost())
                  .project(in.getProject())
                  .memoryMb(in.getMemoryMb())
                  .cpu(in.getCpu())
                  .diskGb(in.getDiskGb());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String host;
   private final String project;
   @Named("memory_mb")
   private final int memoryMb;
   private final int cpu;
   @Named("disk_gb")
   private final int diskGb;

   @ConstructorProperties({
      "host", "project", "memory_mb", "cpu", "disk_gb"
   })
   protected HostResourceUsage(String host, @Nullable String project, int memoryMb, int cpu, int diskGb) {
      this.host = checkNotNull(host, "host");
      this.project = project;
      this.memoryMb = memoryMb;
      this.cpu = cpu;
      this.diskGb = diskGb;
   }

   public String getHost() {
      return this.host;
   }

   @Nullable
   public String getProject() {
      return this.project;
   }

   public int getMemoryMb() {
      return this.memoryMb;
   }

   public int getCpu() {
      return this.cpu;
   }

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
      return Objects.toStringHelper(this)
            .add("host", host).add("project", project).add("memoryMb", memoryMb).add("cpu", cpu).add("diskGb", diskGb);
   }
   
   @Override
   public String toString() {
      return string().toString();
   }

}
