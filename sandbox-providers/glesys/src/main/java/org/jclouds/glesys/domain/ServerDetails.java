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

import com.google.common.collect.ImmutableList;
import com.google.gson.annotations.SerializedName;

import java.util.Arrays;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

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
      private String templateName;
      private int cpuCores;
      private int memorySize;
      private int diskSize;
      private int transfer;
      private Cost cost;
      private List<Ip> ips;

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

      public Builder memorySize(int memorySize) {
         this.memorySize = memorySize;
         return this;
      }

      public Builder diskSize(int diskSize) {
         this.diskSize = diskSize;
         return this;
      }

      public Builder transfer(int transfer) {
         this.transfer = transfer;
         return this;
      }

      public Builder cost(Cost cost) {
         this.cost = cost;
         return this;
      }

      public Builder ips(Ip... ips) {
         return ips(Arrays.asList(ips));
      }

      public Builder ips(List<Ip> ips) {
         this.ips = ips;
         return this;
      }

      public ServerDetails build() {
         return new ServerDetails(id, hostname, datacenter, platform, templateName, description, cpuCores, memorySize, diskSize, transfer, cost, ips);
      }

      public Builder fromServerDetails(ServerDetails in) {
         return fromServer(in).templateName(in.getTemplateName()).memorySize(in.getMemorySize()).diskSize(in.getDiskSize()).cpuCores(in.getCpuCores()).cost(in.getCost())
               .description(in.getDescription()).ips(in.getIps());
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
   @SerializedName("templatename")
   private final String templateName;
   @SerializedName("cpucores")
   private final int cpuCores;
   @SerializedName("memorysize")
   private final int memorySize;
   @SerializedName("disksize")
   private final int diskSize;
   private final int transfer;
   private final Cost cost;
   @SerializedName("iplist")
   private final List<Ip> ips;

   public ServerDetails(String id, String hostname, String datacenter, String platform, String templateName,
                        String description, int cpuCores, int memorySize, int diskSize, int transfer, Cost cost, List<Ip> ips) {
      super(id, hostname, datacenter, platform);
      this.templateName = checkNotNull(templateName, "template");
      this.description = description;
      this.cpuCores = cpuCores;
      this.memorySize = memorySize;
      this.diskSize = diskSize;
      this.transfer = transfer;
      this.cost = checkNotNull(cost, "cost");
      this.ips = ips == null ? ImmutableList.<Ip>of() : ips;
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
   public int getDiskSize() {
      return diskSize;
   }

   /**
    * @return the memory of the server in MB
    */
   public int getMemorySize() {
      return memorySize;
   }

   /**
    * @return the transfer of the server
    */
   public int getTransfer() {
      return transfer;
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
   public List<Ip> getIps() {
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
      return String.format(
            "[id=%s, hostname=%s, datacenter=%s, platform=%s, templateName=%s, description=%s, cpuCores=%d, memorySize=%d, diskSize=%d, transfer=%d, cost=%s, ips=%s]", id,
            hostname, datacenter, platform, templateName, description, cpuCores, memorySize, diskSize, transfer, cost, ips);
   }

}
