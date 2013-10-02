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

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableSet;

/**
 * Sets the allowed arguments for some of the functions in this module such as disksize, cpucores etc.
 *
 * @author Adam Lowe
 * @see <a href="https://customer.glesys.com/api.php?a=doc#server_allowedarguments" />
 */
public class AllowedArgumentsForCreateServer {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromAllowedArgumentsForCreateServer(this);
   }

   public static class Builder {
      protected AllowedArguments diskSizes;
      protected AllowedArguments memorySizes;
      protected AllowedArguments cpuCores;
      protected Set<String> templates = ImmutableSet.of();
      protected AllowedArguments transfers;
      protected Set<String> dataCenters = ImmutableSet.of();

      /**
       * @see AllowedArgumentsForCreateServer#getDiskSizesInGB()
       */
      public Builder diskSizes(AllowedArguments diskSizes) {
         this.diskSizes = checkNotNull(diskSizes, "diskSizesInGB");
         return this;
      }


      /**
       * @see AllowedArgumentsForCreateServer#getMemorySizesInMB()
       */
      public Builder memorySizes(AllowedArguments memorySizes) {
         this.memorySizes = checkNotNull(memorySizes, "memorySizesInMB");
         return this;
      }


      /**
       * @see AllowedArgumentsForCreateServer#getCpuCoreOptions()
       */
      public Builder cpuCores(AllowedArguments cpuCores) {
         this.cpuCores = checkNotNull(cpuCores, "cpuCoreOptions");
         return this;
      }


      /**
       * @see AllowedArgumentsForCreateServer#getTemplateNames()
       */
      public Builder templates(Set<String> templates) {
         this.templates = ImmutableSet.copyOf(checkNotNull(templates, "templateNames"));
         return this;
      }

      public Builder templates(String... in) {
         return templates(ImmutableSet.copyOf(in));
      }

      /**
       * @see AllowedArgumentsForCreateServer#getTransfersInGB()
       */
      public Builder transfers(AllowedArguments transfers) {
         this.transfers = checkNotNull(transfers, "transfersInGB");
         return this;
      }

      /**
       * @see AllowedArgumentsForCreateServer#getDataCenters()
       */
      public Builder dataCenters(Set<String> dataCenters) {
         this.dataCenters = ImmutableSet.copyOf(checkNotNull(dataCenters, "dataCenters"));
         return this;
      }

      public Builder dataCenters(String... in) {
         return dataCenters(ImmutableSet.copyOf(in));
      }

      public AllowedArgumentsForCreateServer build() {
         return new AllowedArgumentsForCreateServer(diskSizes, memorySizes, cpuCores, templates, transfers, dataCenters);
      }

      public Builder fromAllowedArgumentsForCreateServer(AllowedArgumentsForCreateServer in) {
         return this
               .diskSizes(in.getDiskSizesInGB())
               .memorySizes(in.getMemorySizesInMB())
               .cpuCores(in.getCpuCoreOptions())
               .templates(in.getTemplateNames())
               .transfers(in.getTransfersInGB())
               .dataCenters(in.getDataCenters());
      }
   }

   private final AllowedArguments diskSizesInGB;
   private final AllowedArguments memorySizesInMB;
   private final AllowedArguments cpuCoreOptions;
   private final Set<String> templateNames;
   private final AllowedArguments transfersInGB;
   private final Set<String> dataCenters;

   @ConstructorProperties({
         "disksize", "memorysize", "cpucores", "template", "transfer", "datacenter"
   })
   protected AllowedArgumentsForCreateServer(AllowedArguments diskSizesInGB, AllowedArguments memorySizesInMB, AllowedArguments cpuCoreOptions, Set<String> templateNames, AllowedArguments transfersInGB, Set<String> dataCenters) {
      this.diskSizesInGB = checkNotNull(diskSizesInGB, "diskSizesInGB");
      this.memorySizesInMB = checkNotNull(memorySizesInMB, "memorySizesInMB");
      this.cpuCoreOptions = checkNotNull(cpuCoreOptions, "cpuCoreOptions");
      this.templateNames = ImmutableSet.copyOf(checkNotNull(templateNames, "templateNames"));
      this.transfersInGB = checkNotNull(transfersInGB, "transfersInGB");
      this.dataCenters = ImmutableSet.copyOf(checkNotNull(dataCenters, "dataCenters"));
   }

   /**
    * @return a list of disk sizes, in GB, that can be used for creating servers on this platform
    * @see org.jclouds.glesys.domain.OSTemplate#getMinDiskSize()
    */
   public AllowedArguments getDiskSizesInGB() {
      return this.diskSizesInGB;
   }

   /**
    * @return a list of memory sizes, in MB, that can be used for creating servers on this platform
    * @see org.jclouds.glesys.domain.OSTemplate#getMinMemSize()
    */
   public AllowedArguments getMemorySizesInMB() {
      return this.memorySizesInMB;
   }

   /**
    * @return a list of which core counts can be used for creating servers on this platform
    */
   public AllowedArguments getCpuCoreOptions() {
      return this.cpuCoreOptions;
   }

   /**
    * @return a list of template names available for creating servers on this platform
    * @see org.jclouds.glesys.domain.OSTemplate#getName()
    */
   public Set<String> getTemplateNames() {
      return this.templateNames;
   }

   /**
    * @return the list of transfer settings available for creating servers on this platform
    */
   public AllowedArguments getTransfersInGB() {
      return this.transfersInGB;
   }

   /**
    * @return the list of datacenters available that support creating servers on this platform
    */
   public Set<String> getDataCenters() {
      return this.dataCenters;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(diskSizesInGB, memorySizesInMB, cpuCoreOptions, templateNames, transfersInGB, dataCenters);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      AllowedArgumentsForCreateServer that = AllowedArgumentsForCreateServer.class.cast(obj);
      return Objects.equal(this.diskSizesInGB, that.diskSizesInGB)
            && Objects.equal(this.memorySizesInMB, that.memorySizesInMB)
            && Objects.equal(this.cpuCoreOptions, that.cpuCoreOptions)
            && Objects.equal(this.templateNames, that.templateNames)
            && Objects.equal(this.transfersInGB, that.transfersInGB)
            && Objects.equal(this.dataCenters, that.dataCenters);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper("").add("diskSizesInGB", diskSizesInGB).add("memorySizesInMB", memorySizesInMB)
            .add("cpuCoreOptions", cpuCoreOptions).add("templateNames", templateNames)
            .add("transfersInGB", transfersInGB).add("dataCenters", dataCenters);
   }

   @Override
   public String toString() {
      return string().toString();
   }

}
