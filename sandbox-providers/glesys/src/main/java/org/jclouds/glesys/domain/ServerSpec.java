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

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;

/**
 * 
 * 
 * @author Adrian Cole
 */
public class ServerSpec {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return Builder.fromServerSpecification(this);
   }

   public static class Builder {
      protected String datacenter;
      protected String platform;
      protected String templateName;
      protected int diskSizeGB;
      protected int memorySizeMB;
      protected int cpuCores;
      protected int transfer;

      public Builder datacenter(String datacenter) {
         this.datacenter = datacenter;
         return this;
      }

      public Builder platform(String platform) {
         this.platform = platform;
         return this;
      }

      public Builder templateName(String templateName) {
         this.templateName = templateName;
         return this;
      }

      public Builder diskSizeGB(int diskSizeGB) {
         this.diskSizeGB = diskSizeGB;
         return this;
      }

      public Builder memorySizeMB(int memorySizeMB) {
         this.memorySizeMB = memorySizeMB;
         return this;
      }

      public Builder cpuCores(int cpuCores) {
         this.cpuCores = cpuCores;
         return this;
      }

      public Builder transfer(int transfer) {
         this.transfer = transfer;
         return this;
      }

      public ServerSpec build() {
         return new ServerSpec(platform, datacenter, memorySizeMB, diskSizeGB, templateName, cpuCores, transfer);
      }

      public static Builder fromServerSpecification(ServerSpec in) {
         return new Builder().platform(in.getPlatform()).datacenter(in.getDatacenter())
               .memorySizeMB(in.getMemorySizeMB()).diskSizeGB(in.getDiskSizeGB()).templateName(in.getTemplateName())
               .cpuCores(in.getCpuCores()).transfer(in.getTransfer());
      }
   }

   protected final String platform;
   protected final String datacenter;
   protected final int memorySizeMB;
   protected final int diskSizeGB;
   protected final String templateName;
   protected final int cpuCores;
   protected final int transfer;

   protected ServerSpec(String platform, String datacenter, int memorySizeMB, int diskSizeGB, String templateName,
         int cpuCores, int transfer) {
      this.platform = checkNotNull(platform, "platform");
      this.datacenter = checkNotNull(datacenter, "datacenter");
      this.memorySizeMB = memorySizeMB;
      this.diskSizeGB = diskSizeGB;
      this.templateName = checkNotNull(templateName, "templateName");
      this.cpuCores = cpuCores;
      this.transfer = transfer;
   }

   /**
    * @return the data center to create the new server in
    */
   public String getDatacenter() {
      return datacenter;
   }

   /**
    * @return the platform to use (i.e. "Xen" or "OpenVZ")
    */
   public String getPlatform() {
      return platform;
   }

   /**
    * @return the os template to use to create the new server
    */
   public String getTemplateName() {
      return templateName;
   }

   /**
    * @return the amount of disk space, in GB, to allocate
    */
   public int getDiskSizeGB() {
      return diskSizeGB;
   }

   /**
    * @return the memory, in MB, to allocate
    */
   public int getMemorySizeMB() {
      return memorySizeMB;
   }

   /**
    * @return the number of CPU cores to allocate
    */
   public int getCpuCores() {
      return cpuCores;
   }

   /**
    * @return number of transfer
    */
   public int getTransfer() {
      return transfer;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }
      if (object instanceof ServerSpec) {
         final ServerSpec that = ServerSpec.class.cast(object);
         return equal(platform, that.platform) && equal(datacenter, that.datacenter)
               && equal(memorySizeMB, that.memorySizeMB) && equal(diskSizeGB, that.diskSizeGB)
               && equal(templateName, that.templateName) && equal(cpuCores, that.cpuCores)
               && equal(transfer, that.transfer);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(platform, datacenter, memorySizeMB, diskSizeGB, templateName, cpuCores, transfer);
   }

   @Override
   public String toString() {
      return toStringHelper("").add("platform", platform).add("datacenter", datacenter)
            .add("templateName", templateName).add("cpuCores", cpuCores).add("cpuCores", cpuCores)
            .add("diskSizeGB", diskSizeGB).add("transfer", transfer).toString();
   }
}
