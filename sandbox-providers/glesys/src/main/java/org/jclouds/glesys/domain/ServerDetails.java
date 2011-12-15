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

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;
import com.google.gson.annotations.SerializedName;

/**
 * Detailed information about a server such as cpuCores, hardware configuration
 * (cpu, memory and disk), ip adresses, cost, transfer, os and more.
 *
 * @author Adrian Cole
 * @see <a href= "https://customer.glesys.com/api.php?a=doc#server_details" />
 */
public class ServerDetails extends Server {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder extends Server.Builder {
      private String description;
      private int cpuCores;
      private int memory;
      private int disk;
      private Cost cost;

      public Builder description(String description) {
         this.description = description;
         return this;
      }

      public Builder cpuCores(int cpuCores) {
         this.cpuCores = cpuCores;
         return this;
      }

      public Builder memory(int memory) {
         this.memory = memory;
         return this;
      }

      public Builder disk(int disk) {
         this.disk = disk;
         return this;
      }

      public Builder cost(Cost cost) {
         this.cost = cost;
         return this;
      }

      public ServerDetails build() {
         return new ServerDetails(id, hostname, datacenter, platform, description, cpuCores, memory, disk, cost);
      }

      public Builder fromServerDetails(ServerDetails in) {
         return fromServer(in).memory(in.getMemory()).disk(in.getDisk()).cpuCores(in.getCpuCores()).cost(in.getCost())
               .description(in.getDescription());
      }

      @Override
      public Builder id(String id) {
         return Builder.class.cast(super.id(id));
      }

      @Override
      public Builder hostname(String hostname) {
         return Builder.class.cast(super.hostname(hostname));
      }

      @Override
      public Builder datacenter(String datacenter) {
         return Builder.class.cast(super.datacenter(datacenter));
      }

      @Override
      public Builder platform(String platform) {
         return Builder.class.cast(super.platform(platform));
      }

      @Override
      public Builder fromServer(Server in) {
         return Builder.class.cast(super.fromServer(in));
      }
   }

   private final String description;
   @SerializedName("cpucores")
   private final int cpuCores;
   private final int memory;
   private final int disk;
   private final Cost cost;

   public ServerDetails(String id, String hostname, String datacenter, String platform, String description,
                        int cpuCores, int memory, int disk, Cost cost) {
      super(id, hostname, datacenter, platform);
      this.description = checkNotNull(description, "description");
      this.cpuCores = cpuCores;
      this.memory = memory;
      this.disk = disk;
      this.cost = checkNotNull(cost, "cost");
   }

   /**
    * @return the user-specified description of the server
    */
   public String getDescription() {
      return description;
   }

   /**
    * @return number of cores on the server
    */
   public int getCpuCores() {
      return cpuCores;
   }

   /**
    * @return the disk of the server in GB
    */
   public int getDisk() {
      return disk;
   }

   /**
    * @return the memory of the server in MB
    */
   public int getMemory() {
      return memory;
   }

   /**
    * @return details of the cost of the server
    */
   public Cost getCost() {
      return cost;
   }

   @Override
   public String toString() {
      return String.format(
            "[id=%s, hostname=%s, datacenter=%s, platform=%s, description=%s, cpuCores=%s, memory=%s, disk=%s, cost=%s]", id,
            hostname, datacenter, platform, description, cpuCores, memory, disk, cost);
   }

   @Override
   public int hashCode() {
      return super.hashCode() + Objects.hashCode(description, cpuCores, disk, memory, cost);
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }
      if (object instanceof ServerDetails) {
         final ServerDetails other = (ServerDetails) object;
         return super.equals(other)
               && Objects.equal(description, other.description) 
               && Objects.equal(cpuCores, other.cpuCores)
               && Objects.equal(disk, other.disk)
               && Objects.equal(memory, other.memory)
               && Objects.equal(cost, other.cost);
      } else {
         return false;
      }
   }

}
