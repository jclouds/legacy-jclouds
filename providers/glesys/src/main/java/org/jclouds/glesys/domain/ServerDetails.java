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
import java.util.Set;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableSet;

/**
 * Detailed information about a server such as cpuCores, hardware configuration
 * (cpu, memory and disk), ip addresses, cost, transfer, os and more.
 *
 * @author Adrian Cole
 * @see <a href= "https://customer.glesys.com/api.php?a=doc#server_details" />
 */
public class ServerDetails extends Server {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromServerDetails(this);
   }

   public abstract static class Builder<T extends Builder<T>> extends Server.Builder<T> {
      protected Server.State state;
      protected String description;
      protected String templateName;
      protected int cpuCores;
      protected int memorySizeMB;
      protected int diskSizeGB;
      protected int transferGB;
      protected Cost cost;
      protected Set<Ip> ips = ImmutableSet.of();

      /**
       * @see ServerDetails#getState()
       */
      public T state(Server.State state) {
         this.state = checkNotNull(state, "state");
         return self();
      }

      /**
       * @see ServerDetails#getDescription()
       */
      public T description(String description) {
         this.description = checkNotNull(description, "description");
         return self();
      }

      /**
       * @see ServerDetails#getTemplateName()
       */
      public T templateName(String templateName) {
         this.templateName = checkNotNull(templateName, "templateName");
         return self();
      }

      /**
       * @see ServerDetails#getCpuCores()
       */
      public T cpuCores(int cpuCores) {
         this.cpuCores = cpuCores;
         return self();
      }

      /**
       * @see ServerDetails#getMemorySizeMB()
       */
      public T memorySizeMB(int memorySizeMB) {
         this.memorySizeMB = memorySizeMB;
         return self();
      }

      /**
       * @see ServerDetails#getDiskSizeGB()
       */
      public T diskSizeGB(int diskSizeGB) {
         this.diskSizeGB = diskSizeGB;
         return self();
      }

      /**
       * @see ServerDetails#getTransferGB()
       */
      public T transferGB(int transferGB) {
         this.transferGB = transferGB;
         return self();
      }

      /**
       * @see ServerDetails#getCost()
       */
      public T cost(Cost cost) {
         this.cost = checkNotNull(cost, "cost");
         return self();
      }

      /**
       * @see ServerDetails#getIps()
       */
      public T ips(Set<Ip> ips) {
         this.ips = ImmutableSet.copyOf(checkNotNull(ips, "ips"));
         return self();
      }

      public T ips(Ip... in) {
         return ips(ImmutableSet.copyOf(in));
      }

      public ServerDetails build() {
         return new ServerDetails(id, hostname, datacenter, platform, state, description, templateName, cpuCores, memorySizeMB, diskSizeGB, transferGB, cost, ips);
      }

      public T fromServerDetails(ServerDetails in) {
         return super.fromServer(in)
               .state(in.getState())
               .description(in.getDescription())
               .templateName(in.getTemplateName())
               .cpuCores(in.getCpuCores())
               .memorySizeMB(in.getMemorySizeMB())
               .diskSizeGB(in.getDiskSizeGB())
               .transferGB(in.getTransferGB())
               .cost(in.getCost())
               .ips(in.getIps());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final Server.State state;
   private final String description;
   private final String templateName;
   private final int cpuCores;
   private final int memorySizeMB;
   private final int diskSizeGB;
   private final int transferGB;
   private final Cost cost;
   private final Set<Ip> ips;

   @ConstructorProperties({
         "serverid", "hostname", "datacenter", "platform", "state", "description", "templatename", "cpucores",
         "memorysize", "disksize", "transfer", "cost", "iplist"
   })
   protected ServerDetails(String id, String hostname, String datacenter, String platform, @Nullable Server.State state,
                           @Nullable String description, String templateName, int cpuCores, int memorySizeMB,
                           int diskSizeGB, int transferGB, Cost cost, @Nullable Set<Ip> ips) {
      super(id, hostname, datacenter, platform);
      this.state = state;
      this.description = description;
      this.templateName = checkNotNull(templateName, "templateName");
      this.cpuCores = cpuCores;
      this.memorySizeMB = memorySizeMB;
      this.diskSizeGB = diskSizeGB;
      this.transferGB = transferGB;
      this.cost = checkNotNull(cost, "cost");
      this.ips = ips == null ? ImmutableSet.<Ip>of() : ImmutableSet.copyOf(checkNotNull(ips, "ips"));
   }

   /**
    * @return the state of the server (e.g. "running")
    */
   public Server.State getState() {
      return this.state;
   }

   /**
    * @return the user-specified description of the server
    */
   public String getDescription() {
      return this.description;
   }

   /**
    * @return the name of the template used to create the server
    */
   public String getTemplateName() {
      return this.templateName;
   }

   /**
    * @return number of cores on the server
    */
   public int getCpuCores() {
      return this.cpuCores;
   }

   /**
    * @return the memory of the server in MB
    */
   public int getMemorySizeMB() {
      return this.memorySizeMB;
   }

   /**
    * @return the disk of the server in GB
    */
   public int getDiskSizeGB() {
      return this.diskSizeGB;
   }

   /**
    * @return the transfer of the server
    */
   public int getTransferGB() {
      return this.transferGB;
   }

   /**
    * @return details of the cost of the server
    */
   public Cost getCost() {
      return this.cost;
   }

   /**
    * @return the ip addresses assigned to the server
    */
   public Set<Ip> getIps() {
      return this.ips;
   }

   protected ToStringHelper string() {
      return super.string().add("state", state).add("description", description).add("templateName", templateName)
            .add("cpuCores", cpuCores).add("memorySizeMB", memorySizeMB).add("diskSizeGB", diskSizeGB)
            .add("transferGB", transferGB).add("cost", cost).add("ips", ips);
   }

}
