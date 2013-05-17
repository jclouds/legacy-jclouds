/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.glesys.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Detailed information server status including hardware usage (cpu, memory and disk), bandwidth and up-time.
 *
 * @author Adam Lowe
 * @see <a href= "https://customer.glesys.com/api.php?a=doc#server_status" />
 */
public class ServerStatus {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromServerStatus(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected Server.State state;
      protected ResourceStatus cpu;
      protected ResourceStatus memory;
      protected ResourceStatus disk;
      protected ServerUptime uptime;

      /**
       * @see ServerStatus#getState()
       */
      public T state(Server.State state) {
         this.state = checkNotNull(state, "state");
         return self();
      }

      /**
       * @see ServerStatus#getCpu()
       */
      public T cpu(ResourceStatus cpu) {
         this.cpu = checkNotNull(cpu, "cpu");
         return self();
      }

      /**
       * @see ServerStatus#getMemory()
       */
      public T memory(ResourceStatus memory) {
         this.memory = checkNotNull(memory, "memory");
         return self();
      }

      /**
       * @see ServerStatus#getDisk()
       */
      public T disk(ResourceStatus disk) {
         this.disk = checkNotNull(disk, "disk");
         return self();
      }

      /**
       * @see ServerStatus#getUptime()
       */
      public T uptime(ServerUptime uptime) {
         this.uptime = checkNotNull(uptime, "uptime");
         return self();
      }

      public ServerStatus build() {
         return new ServerStatus(state, cpu, memory, disk, uptime);
      }

      public T fromServerStatus(ServerStatus in) {
         return this.state(in.getState()).cpu(in.getCpu()).memory(in.getMemory()).disk(in.getDisk()).uptime(in.getUptime());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final Server.State state;
   private final ResourceStatus cpu;
   private final ResourceStatus memory;
   private final ResourceStatus disk;
   private final ServerUptime uptime;

   @ConstructorProperties({
         "state", "cpu", "memory", "disk", "uptime"
   })
   protected ServerStatus(Server.State state, @Nullable ResourceStatus cpu, @Nullable ResourceStatus memory,
                          @Nullable ResourceStatus disk, @Nullable ServerUptime uptime) {
      this.state = checkNotNull(state, "state");
      this.cpu = cpu;
      this.memory = memory;
      this.disk = disk;
      this.uptime = uptime;
   }

   /**
    * @return the state of the server (e.g. "running")
    */
   @Nullable
   public Server.State getState() {
      return this.state;
   }

   /**
    * @return CPU usage information
    */
   @Nullable
   public ResourceStatus getCpu() {
      return this.cpu;
   }

   /**
    * @return details of memory usage and limits
    */
   @Nullable
   public ResourceStatus getMemory() {
      return this.memory;
   }

   /**
    * @return details of disk usage and limits
    */
   @Nullable
   public ResourceStatus getDisk() {
      return this.disk;
   }

   /**
    * @return the uptime of the server
    */
   @Nullable
   public ServerUptime getUptime() {
      return this.uptime;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(state, cpu, memory, disk, uptime);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      ServerStatus that = ServerStatus.class.cast(obj);
      return Objects.equal(this.state, that.state)
            && Objects.equal(this.cpu, that.cpu)
            && Objects.equal(this.memory, that.memory)
            && Objects.equal(this.disk, that.disk)
            && Objects.equal(this.uptime, that.uptime);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper("")
            .add("state", state).add("cpu", cpu).add("memory", memory).add("disk", disk).add("uptime", uptime);
   }

   @Override
   public String toString() {
      return string().toString();
   }

}
