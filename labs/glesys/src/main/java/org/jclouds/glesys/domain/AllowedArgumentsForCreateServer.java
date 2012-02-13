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

import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.google.gson.annotations.SerializedName;

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

   public static class Builder {
      private Set<Integer> diskSizes;
      private Set<Integer> memorySizes;
      private Set<Integer> cpuCores;
      private Set<String> templates;
      private Set<Integer> transfers;
      private Set<String> dataCenters;

      public Builder diskSizes(Integer... sizes) {
         return diskSizes(ImmutableSet.<Integer>copyOf(sizes));
      }

      public Builder diskSizes(Set<Integer> sizes) {
         this.diskSizes = sizes;
         return this;
      }

      public Builder memorySizes(Integer... sizes) {
         return memorySizes(ImmutableSet.<Integer>copyOf(sizes));
      }

      public Builder memorySizes(Set<Integer> sizes) {
         this.memorySizes = sizes;
         return this;
      }

      public Builder cpuCores(Integer... cpuCores) {
         this.cpuCores = ImmutableSet.<Integer>copyOf(cpuCores);
         return this;
      }

      public Builder cpuCores(Set<Integer> cpuCores) {
         this.cpuCores = cpuCores;
         return this;
      }

      public Builder templates(String... templates) {
         return templates(ImmutableSet.<String>copyOf(templates));
      }

      public Builder templates(Set<String> templates) {
         this.templates = templates;
         return this;
      }

      public Builder transfers(Integer... transfers) {
         return transfers(ImmutableSet.<Integer>copyOf(transfers));
      }

      public Builder transfers(Set<Integer> transfers) {
         this.transfers = transfers;
         return this;
      }

      public Builder dataCenters(String... dataCenters) {
         return dataCenters(ImmutableSet.<String>copyOf(dataCenters));
      }

      public Builder dataCenters(Set<String> dataCenters) {
         this.dataCenters = dataCenters;
         return this;
      }

      public AllowedArgumentsForCreateServer build() {
         return new AllowedArgumentsForCreateServer(diskSizes, memorySizes, cpuCores, templates, transfers, dataCenters);
      }

      public Builder fromAllowedArguments(AllowedArgumentsForCreateServer in) {
         return diskSizes(in.getDiskSizesInGB())
               .memorySizes(in.getMemorySizesInMB())
               .cpuCores(in.getCpuCoreOptions())
               .templates(in.getTemplateNames())
               .transfers(in.getTransfersInGB())
               .dataCenters(in.getDataCenters());
      }
   }

   @SerializedName("disksize")
   private final Set<Integer> diskSizes;
   @SerializedName("memorysize")
   private final Set<Integer> memorySizes;
   @SerializedName("cpucores")
   private final Set<Integer> cpuCores;
   @SerializedName("template")
   private final Set<String> templates;
   @SerializedName("transfer")
   private final Set<Integer> transfers;
   @SerializedName("datacenter")
   private final Set<String> dataCenters;

   public AllowedArgumentsForCreateServer(Set<Integer> diskSizes, Set<Integer> memorySizes, Set<Integer> cpuCores,
                                 Set<String> templates, Set<Integer> transfers, Set<String> dataCenters) {
      checkNotNull(diskSizes, "diskSizes");
      checkNotNull(memorySizes, "memorySizes");
      checkNotNull(cpuCores, "cpuCores");
      checkNotNull(templates, "templates");
      checkNotNull(transfers, "transfers");
      checkNotNull(dataCenters, "dataCenters");

      this.diskSizes = diskSizes;
      this.memorySizes = memorySizes;
      this.cpuCores = cpuCores;
      this.templates = templates;
      this.transfers = transfers;
      this.dataCenters = dataCenters;
   }

   /**
    * @return a list of disk sizes, in GB, that can be used for creating servers on this platform
    * @see org.jclouds.glesys.domain.OSTemplate#getMinDiskSize()
    */
   public Set<Integer> getDiskSizesInGB() {
      return diskSizes;
   }

   /**
    * @return a list of memory sizes, in MB, that can be used for creating servers on this platform
    * @see  org.jclouds.glesys.domain.OSTemplate#getMinMemSize()
    */
   public Set<Integer> getMemorySizesInMB() {
      return memorySizes;
   }

   /**
    * @return a list of which core counts can be used for creating servers on this platform
    */
   public Set<Integer> getCpuCoreOptions() {
      return cpuCores;
   }

   /**
    * @return a list of template names available for creating servers on this platform
    * @see org.jclouds.glesys.domain.OSTemplate#getName() 
    */
   public Set<String> getTemplateNames() {
      return templates;
   }

   /**
    * @return the list of transfer settings available for creating servers on this platform
    */
   public Set<Integer> getTransfersInGB() {
      return transfers;
   }

   /**
    * @return the list of datacenters available that support creating servers on this platform
    */
   public Set<String> getDataCenters() {
      return dataCenters;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }
      if (object instanceof AllowedArgumentsForCreateServer) {
         final AllowedArgumentsForCreateServer other = (AllowedArgumentsForCreateServer) object;
         return Objects.equal(diskSizes, other.diskSizes)
               && Objects.equal(memorySizes, other.memorySizes)
               && Objects.equal(cpuCores, other.cpuCores)
               && Objects.equal(templates, other.templates)
               && Objects.equal(transfers, other.transfers)
               && Objects.equal(dataCenters, other.dataCenters);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(diskSizes, memorySizes, cpuCores, templates, transfers, dataCenters);
   }

   @Override
   public String toString() {
      checkNotNull(diskSizes, "diskSizes");
      checkNotNull(memorySizes, "memorySizes");
      checkNotNull(cpuCores, "cpuCores");
      checkNotNull(templates, "templates");
      checkNotNull(transfers, "transfers");
      checkNotNull(dataCenters, "dataCenters");

      Joiner commaJoiner = Joiner.on(", ");
      return String.format("[disksize=[%s], memorysize=[%s], cpuCores=[%s], templates=[%s], transfers=[%s], datacenters=[%s]]",
            commaJoiner.join(diskSizes), commaJoiner.join(memorySizes), commaJoiner.join(cpuCores), commaJoiner.join(templates),
            commaJoiner.join(transfers), commaJoiner.join(dataCenters));
   }

}
