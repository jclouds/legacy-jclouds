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

import java.util.Set;

import com.google.common.collect.ImmutableSet;
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
      private Server.State state;
      private String description;
      private String templateName;
      private int cpuCores;
      private int memorySizeMB;
      private int diskSizeGB;
      private int transferGB;
      private Cost cost;
      private Set<Ip> ips = ImmutableSet.of();

      public Builder state(Server.State state) {
         this.state = state;
         return this;
      }

      public Builder description(String description) {
         this.description = description;
         return this;
      }

      public Builder templateName(String templateName) {
         this.templateName = templateName;
         return this;
      }

      public Builder cpuCores(int cpuCores) {
         this.cpuCores = cpuCores;
         return this;
      }

      public Builder memorySizeMB(int memorySizeMB) {
         this.memorySizeMB = memorySizeMB;
         return this;
      }

      public Builder diskSizeGB(int diskSizeGB) {
         this.diskSizeGB = diskSizeGB;
         return this;
      }

      public Builder transferGB(int transferGB) {
         this.transferGB = transferGB;
         return this;
      }

      public Builder cost(Cost cost) {
         this.cost = cost;
         return this;
      }

      public Builder ips(Ip... ips) {
         return ips(ImmutableSet.copyOf(ips));
      }

      public Builder ips(Iterable<Ip> ips) {
         this.ips = ImmutableSet.copyOf(ips);
         return this;
      }

      public ServerDetails build() {
         return new ServerDetails(id, hostname, datacenter, platform, state, templateName, description, cpuCores,
               memorySizeMB, diskSizeGB, transferGB, cost, ips);
      }

      public Builder fromServerDetails(ServerDetails in) {
         return fromServer(in).templateName(in.getTemplateName()).state(in.getState()).memorySizeMB(in.getMemorySizeMB())
               .diskSizeGB(in.getDiskSizeGB()).cpuCores(in.getCpuCores()).cost(in.getCost())
               .transferGB(in.getTransferGB()).description(in.getDescription()).ips(in.getIps());
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

   private final Server.State state;
   private final String description;
   @SerializedName("templatename")
   private final String templateName;
   @SerializedName("cpucores")
   private final int cpuCores;
   @SerializedName("memorysize")
   private final int memorySizeMB;
   @SerializedName("disksize")
   private final int diskSizeGB;
   @SerializedName("transfer")
   private final int transferGB;
   private final Cost cost;
   @SerializedName("iplist")
   private final Set<Ip> ips;

   public ServerDetails(String id, String hostname, String datacenter, String platform, Server.State state,
         String templateName, String description, int cpuCores, int memorySizeMB, int diskSizeGB, int transferGB,
         Cost cost, Set<Ip> ips) {
      super(id, hostname, datacenter, platform);
      this.state = state;
      this.templateName = checkNotNull(templateName, "template");
      this.description = description;
      this.cpuCores = cpuCores;
      this.memorySizeMB = memorySizeMB;
      this.diskSizeGB = diskSizeGB;
      this.transferGB = transferGB;
      this.cost = checkNotNull(cost, "cost");
      this.ips = ImmutableSet.<Ip> copyOf(ips);
   }

   /**
    * @return the state of the server (e.g. "running")
    */
   public Server.State getState() {
      return state;
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
   public int getDiskSizeGB() {
      return diskSizeGB;
   }

   /**
    * @return the memory of the server in MB
    */
   public int getMemorySizeMB() {
      return memorySizeMB;
   }

   /**
    * @return the transfer of the server
    */
   public int getTransferGB() {
      return transferGB;
   }

   /**
    * @return details of the cost of the server
    */
   public Cost getCost() {
      return cost;
   }

   /**
    * @return the ip addresses assigned to the server
    */
   public Set<Ip> getIps() {
      return ips;
   }

   /**
    * @return the name of the template used to create the server
    */
   public String getTemplateName() {
      return templateName;
   }

   @Override
   public String toString() {
      return String
            .format(
                  "[id=%s, hostname=%s, datacenter=%s, platform=%s, templateName=%s, state=%s, description=%s, cpuCores=%d, memorySizeMB=%d, diskSizeGB=%d, transferGB=%d, cost=%s, ips=%s]",
                  id, hostname, datacenter, platform, templateName, state, description, cpuCores, memorySizeMB,
                  diskSizeGB, transferGB, cost, ips);
   }

}
