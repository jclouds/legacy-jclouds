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

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromAllowedArgumentsForCreateServer(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected Set<Integer> diskSizes = ImmutableSet.of();
      protected Set<Integer> memorySizes = ImmutableSet.of();
      protected Set<Integer> cpuCores = ImmutableSet.of();
      protected Set<String> templates = ImmutableSet.of();
      protected Set<Integer> transfers = ImmutableSet.of();
      protected Set<String> dataCenters = ImmutableSet.of();

      /**
       * @see AllowedArgumentsForCreateServer#getDiskSizesInGB()
       */
      public T diskSizes(Set<Integer> diskSizes) {
         this.diskSizes = ImmutableSet.copyOf(checkNotNull(diskSizes, "diskSizesInGB"));
         return self();
      }

      public T diskSizes(Integer... in) {
         return diskSizes(ImmutableSet.copyOf(in));
      }

      /**
       * @see AllowedArgumentsForCreateServer#getMemorySizesInMB()
       */
      public T memorySizes(Set<Integer> memorySizes) {
         this.memorySizes = ImmutableSet.copyOf(checkNotNull(memorySizes, "memorySizesInMB"));
         return self();
      }

      public T memorySizes(Integer... in) {
         return memorySizes(ImmutableSet.copyOf(in));
      }

      /**
       * @see AllowedArgumentsForCreateServer#getCpuCoreOptions()
       */
      public T cpuCores(Set<Integer> cpuCores) {
         this.cpuCores = ImmutableSet.copyOf(checkNotNull(cpuCores, "cpuCoreOptions"));
         return self();
      }

      public T cpuCores(Integer... in) {
         return cpuCores(ImmutableSet.copyOf(in));
      }

      /**
       * @see AllowedArgumentsForCreateServer#getTemplateNames()
       */
      public T templates(Set<String> templates) {
         this.templates = ImmutableSet.copyOf(checkNotNull(templates, "templateNames"));
         return self();
      }

      public T templates(String... in) {
         return templates(ImmutableSet.copyOf(in));
      }

      /**
       * @see AllowedArgumentsForCreateServer#getTransfersInGB()
       */
      public T transfers(Set<Integer> transfers) {
         this.transfers = ImmutableSet.copyOf(checkNotNull(transfers, "transfersInGB"));
         return self();
      }

      public T transfers(Integer... in) {
         return transfers(ImmutableSet.copyOf(in));
      }

      /**
       * @see AllowedArgumentsForCreateServer#getDataCenters()
       */
      public T dataCenters(Set<String> dataCenters) {
         this.dataCenters = ImmutableSet.copyOf(checkNotNull(dataCenters, "dataCenters"));
         return self();
      }

      public T dataCenters(String... in) {
         return dataCenters(ImmutableSet.copyOf(in));
      }

      public AllowedArgumentsForCreateServer build() {
         return new AllowedArgumentsForCreateServer(diskSizes, memorySizes, cpuCores, templates, transfers, dataCenters);
      }

      public T fromAllowedArgumentsForCreateServer(AllowedArgumentsForCreateServer in) {
         return this
               .diskSizes(in.getDiskSizesInGB())
               .memorySizes(in.getMemorySizesInMB())
               .cpuCores(in.getCpuCoreOptions())
               .templates(in.getTemplateNames())
               .transfers(in.getTransfersInGB())
               .dataCenters(in.getDataCenters());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final Set<Integer> diskSizesInGB;
   private final Set<Integer> memorySizesInMB;
   private final Set<Integer> cpuCoreOptions;
   private final Set<String> templateNames;
   private final Set<Integer> transfersInGB;
   private final Set<String> dataCenters;

   @ConstructorProperties({
         "disksize", "memorysize", "cpucores", "template", "transfer", "datacenter"
   })
   protected AllowedArgumentsForCreateServer(Set<Integer> diskSizesInGB, Set<Integer> memorySizesInMB, Set<Integer> cpuCoreOptions, Set<String> templateNames, Set<Integer> transfersInGB, Set<String> dataCenters) {
      this.diskSizesInGB = ImmutableSet.copyOf(checkNotNull(diskSizesInGB, "diskSizesInGB"));
      this.memorySizesInMB = ImmutableSet.copyOf(checkNotNull(memorySizesInMB, "memorySizesInMB"));
      this.cpuCoreOptions = ImmutableSet.copyOf(checkNotNull(cpuCoreOptions, "cpuCoreOptions"));
      this.templateNames = ImmutableSet.copyOf(checkNotNull(templateNames, "templateNames"));
      this.transfersInGB = ImmutableSet.copyOf(checkNotNull(transfersInGB, "transfersInGB"));
      this.dataCenters = ImmutableSet.copyOf(checkNotNull(dataCenters, "dataCenters"));
   }

   /**
    * @return a list of disk sizes, in GB, that can be used for creating servers on this platform
    * @see org.jclouds.glesys.domain.OSTemplate#getMinDiskSize()
    */
   public Set<Integer> getDiskSizesInGB() {
      return this.diskSizesInGB;
   }

   /**
    * @return a list of memory sizes, in MB, that can be used for creating servers on this platform
    * @see org.jclouds.glesys.domain.OSTemplate#getMinMemSize()
    */
   public Set<Integer> getMemorySizesInMB() {
      return this.memorySizesInMB;
   }

   /**
    * @return a list of which core counts can be used for creating servers on this platform
    */
   public Set<Integer> getCpuCoreOptions() {
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
   public Set<Integer> getTransfersInGB() {
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
