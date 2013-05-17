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

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Class ServerSpec
 *
 * @author Adrian Cole
 */
public class ServerSpec {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromServerSpec(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected String platform;
      protected String datacenter;
      protected int memorySizeMB;
      protected int diskSizeGB;
      protected String templateName;
      protected int cpuCores;
      protected int transferGB;

      /**
       * @see ServerSpec#getPlatform()
       */
      public T platform(String platform) {
         this.platform = checkNotNull(platform, "platform");
         return self();
      }

      /**
       * @see ServerSpec#getDatacenter()
       */
      public T datacenter(String datacenter) {
         this.datacenter = checkNotNull(datacenter, "datacenter");
         return self();
      }

      /**
       * @see ServerSpec#getMemorySizeMB()
       */
      public T memorySizeMB(int memorySizeMB) {
         this.memorySizeMB = memorySizeMB;
         return self();
      }

      /**
       * @see ServerSpec#getDiskSizeGB()
       */
      public T diskSizeGB(int diskSizeGB) {
         this.diskSizeGB = diskSizeGB;
         return self();
      }

      /**
       * @see ServerSpec#getTemplateName()
       */
      public T templateName(String templateName) {
         this.templateName = checkNotNull(templateName, "templateName");
         return self();
      }

      /**
       * @see ServerSpec#getCpuCores()
       */
      public T cpuCores(int cpuCores) {
         this.cpuCores = cpuCores;
         return self();
      }

      /**
       * @see ServerSpec#getTransferGB()
       */
      public T transferGB(int transferGB) {
         this.transferGB = transferGB;
         return self();
      }

      public ServerSpec build() {
         return new ServerSpec(platform, datacenter, memorySizeMB, diskSizeGB, templateName, cpuCores, transferGB);
      }

      public T fromServerSpec(ServerSpec in) {
         return this.platform(in.getPlatform())
               .datacenter(in.getDatacenter())
               .memorySizeMB(in.getMemorySizeMB())
               .diskSizeGB(in.getDiskSizeGB())
               .templateName(in.getTemplateName())
               .cpuCores(in.getCpuCores())
               .transferGB(in.getTransferGB());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String platform;
   private final String datacenter;
   private final int memorySizeMB;
   private final int diskSizeGB;
   private final String templateName;
   private final int cpuCores;
   private final int transferGB;

   @ConstructorProperties({
         "platform", "datacenter", "memorySizeMB", "diskSizeGB", "templateName", "cpuCores", "transferGB"
   })
   protected ServerSpec(String platform, String datacenter, int memorySizeMB, int diskSizeGB, String templateName, int cpuCores, int transferGB) {
      this.platform = checkNotNull(platform, "platform");
      this.datacenter = checkNotNull(datacenter, "datacenter");
      this.memorySizeMB = memorySizeMB;
      this.diskSizeGB = diskSizeGB;
      this.templateName = checkNotNull(templateName, "templateName");
      this.cpuCores = cpuCores;
      this.transferGB = transferGB;
   }

   /**
    * @return the data center to create the new server in
    */
   public String getPlatform() {
      return this.platform;
   }

   /**
    * @return the platform to use (i.e. "Xen" or "OpenVZ")
    */
   public String getDatacenter() {
      return this.datacenter;
   }

   /**
    * @return the os template to use to create the new server
    */
   public int getMemorySizeMB() {
      return this.memorySizeMB;
   }

   /**
    * @return the amount of disk space, in GB, to allocate
    */
   public int getDiskSizeGB() {
      return this.diskSizeGB;
   }

   /**
    * @return the memory, in MB, to allocate
    */
   public String getTemplateName() {
      return this.templateName;
   }

   /**
    * @return the number of CPU cores to allocate
    */
   public int getCpuCores() {
      return this.cpuCores;
   }

   /**
    * @return bandwidth of in GB
    */
   public int getTransferGB() {
      return this.transferGB;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(platform, datacenter, memorySizeMB, diskSizeGB, templateName, cpuCores, transferGB);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      ServerSpec that = ServerSpec.class.cast(obj);
      return Objects.equal(this.platform, that.platform)
            && Objects.equal(this.datacenter, that.datacenter)
            && Objects.equal(this.memorySizeMB, that.memorySizeMB)
            && Objects.equal(this.diskSizeGB, that.diskSizeGB)
            && Objects.equal(this.templateName, that.templateName)
            && Objects.equal(this.cpuCores, that.cpuCores)
            && Objects.equal(this.transferGB, that.transferGB);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper("").add("platform", platform).add("datacenter", datacenter)
            .add("memorySizeMB", memorySizeMB).add("diskSizeGB", diskSizeGB).add("templateName", templateName)
            .add("cpuCores", cpuCores).add("transferGB", transferGB);
   }

   @Override
   public String toString() {
      return string().toString();
   }
}
