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
      private ServerState state;
      private Cpu cpu;
      private Memory memory;
      private Disk disk;
      private Bandwidth bandwidth;
      private long uptime;

      public Builder state(ServerState state) {
         this.state = state;
         return this;
      }

      public Builder cpu(Cpu cpu) {
         this.cpu = cpu;
         return this;
      }

      public Builder memory(Memory memory) {
         this.memory = memory;
         return this;
      }

      public Builder disk(Disk disk) {
         this.disk = disk;
         return this;
      }

      public Builder bandwidth(Bandwidth bandwidth) {
         this.bandwidth = bandwidth;
         return this;
      }

      public Builder uptime(long uptime) {
         this.uptime = uptime;
         return this;
      }

      public ServerStatus build() {
         return new ServerStatus(state, cpu, memory, disk, bandwidth, uptime);
      }

      public Builder fromServerStatus(ServerStatus in) {
         return state(in.getState()).cpu(in.getCpu()).memory(in.getMemory()).disk(in.getDisk()).bandwidth(in.getBandwidth()).uptime(in.getUptime());
      }
   }

   private final ServerState state;
   private final Cpu cpu;
   private final Memory memory;
   private final Disk disk;
   private final Bandwidth bandwidth;
   private final ServerUptime uptime;

   public ServerStatus(ServerState state, Cpu cpu, Memory memory, Disk disk, Bandwidth bandwidth, long uptime) {
      this.state = state;
      this.cpu = cpu;
      this.memory = memory;
      this.disk = disk;
      this.bandwidth = bandwidth;
      this.uptime = ServerUptime.fromValue(uptime);
   }

   /**
    * @return the state of the server (e.g. "running")
    */
   public ServerState getState() {
      return state;
   }

   /**
    * @return CPU usage information
    */
   public Cpu getCpu() {
      return cpu;
   }

   /**
    * @return details of memory usage and limits
    */
   public Memory getMemory() {
      return memory;
   }

   /**
    * @return details of disk usage and limits
    */
   public Disk getDisk() {
      return disk;
   }

   /**
    * @return details of bandwidth usage and limits
    */
   public Bandwidth getBandwidth() {
      return bandwidth;
   }

   /**
    * @return the uptime of the server
    */
   public long getUptime() {
      return uptime.getTime();
   }

   @Override
   public String toString() {
      return String.format("Status[state=%s, cpu=%s, memory=%s, disk=%s, bandwidth=%s, uptime=%s]",
            state, cpu, memory, disk, bandwidth, uptime);
   }

}
