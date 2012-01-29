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
package org.jclouds.glesys.domain;

import com.google.common.base.Objects;

/**
 * Detailed information server status including hardware usage (cpu, memory and disk), bandwidth and up-time.
 *
 * @author Adam Lowe
 * @see <a href= "https://customer.glesys.com/api.php?a=doc#server_status" />
 */

public class ServerStatus {

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private Server.State state;
      private ResourceUsage cpu;
      private ResourceUsage memory;
      private ResourceUsage disk;
      private ServerUptime uptime;

      public Builder state(Server.State state) {
         this.state = state;
         return this;
      }

      public Builder cpu(ResourceUsage cpu) {
         this.cpu = cpu;
         return this;
      }

      public Builder memory(ResourceUsage memory) {
         this.memory = memory;
         return this;
      }

      public Builder disk(ResourceUsage disk) {
         this.disk = disk;
         return this;
      }

      public Builder uptime(ServerUptime uptime) {
         this.uptime = uptime;
         return this;
      }

      public ServerStatus build() {
         return new ServerStatus(state, cpu, memory, disk, uptime);
      }

      public Builder fromServerStatus(ServerStatus in) {
         return state(in.getState()).cpu(in.getCpu()).memory(in.getMemory()).disk(in.getDisk()).uptime(in.getUptime());
      }
   }

   private final Server.State state;
   private final ResourceUsage cpu;
   private final ResourceUsage memory;
   private final ResourceUsage disk;
   private final ServerUptime uptime;

   public ServerStatus(Server.State state, ResourceUsage cpu, ResourceUsage memory, ResourceUsage disk,  ServerUptime uptime) {
      this.state = state;
      this.cpu = cpu;
      this.memory = memory;
      this.disk = disk;
      this.uptime = uptime;
   }

   /**
    * @return the state of the server (e.g. "running")
    */
   public Server.State getState() {
      return state;
   }

   /**
    * @return CPU usage information
    */
   public ResourceUsage getCpu() {
      return cpu;
   }

   /**
    * @return details of memory usage and limits
    */
   public ResourceUsage getMemory() {
      return memory;
   }

   /**
    * @return details of disk usage and limits
    */
   public ResourceUsage getDisk() {
      return disk;
   }

   /**
    * @return the uptime of the server
    */
   public ServerUptime getUptime() {
      return uptime;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }
      if (object instanceof ServerStatus) {
         final ServerStatus other = (ServerStatus) object;
         return Objects.equal(state, other.state)
               && Objects.equal(cpu, other.cpu)
               && Objects.equal(memory, other.memory)
               && Objects.equal(disk, other.disk)
               && Objects.equal(uptime, other.uptime);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(state, cpu, memory, disk, uptime);
   }
   
   @Override
   public String toString() {
      return String.format("[state=%s, cpu=%s, memory=%s, disk=%s, uptime=%s]",
            state, cpu, memory, disk, uptime);
   }

}
