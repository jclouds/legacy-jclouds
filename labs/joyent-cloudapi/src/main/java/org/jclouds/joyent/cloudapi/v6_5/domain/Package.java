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
package org.jclouds.joyent.cloudapi.v6_5.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;

import javax.inject.Named;

import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;

/**
 * Packages are named collections of resources that are used to describe the ‘sizes’ of either a
 * smart machine or a virtual machine. These resources include (but are not limited to) RAM, CPUs,
 * CPU Caps, Lightweight Threads, Disk Space, Swap size, and Logical Networks.
 * 
 * @author Gerald Pereira
 * @see <a href= "http://apidocs.joyent.com/sdcapidoc/cloudapi/index.html#packages" >docs</a>
 */
public class Package implements Comparable<Package> {

   public static Builder builder() {
      return new Builder();
   }
   
   public Builder toBuilder() {
      return new Builder().fromPackage(this);
   }

   public static class Builder {
      private String name;
      private int memorySizeMb;
      private int diskSizeGb;
      private int swapSizeMb;
      private boolean isDefault;

      /**
       * @see Package#getName()
       */
      public Builder name(String name) {
         this.name = name;
         return this;
      }

      /**
       * @see Package#getMemorySizeMb()
       */
      public Builder memorySizeMb(int memorySizeMb) {
         this.memorySizeMb = memorySizeMb;
         return this;
      }

      /**
       * @see Package#getDiskSizeGb()
       */
      public Builder diskSizeGb(int diskSizeGb) {
         this.diskSizeGb = diskSizeGb;
         return this;
      }

      /**
       * @see Package#getSwapSizeMb()
       */
      public Builder swapSizeMb(int swapSizeMb) {
         this.swapSizeMb = swapSizeMb;
         return this;
      }

      /**
       * @see Package#isDefault()
       */
      public Builder isDefault(boolean isDefault) {
         this.isDefault = isDefault;
         return this;
      }

      public Package build() {
         return new Package(name, memorySizeMb, diskSizeGb, swapSizeMb, isDefault);
      }

      public Builder fromPackage(Package in) {
         return name(in.getName()).memorySizeMb(in.getMemorySizeMb()).diskSizeGb(in.getDiskSizeGb())
               .swapSizeMb(in.getSwapSizeMb()).isDefault(in.isDefault());
      }
   }

   protected final String name;
   @Named("memory")
   protected final int memorySizeMb;
   @Named("disk")
   protected final int diskSizeGb;
   @Named("swap")
   protected final int swapSizeMb;
   @Named("default")
   protected final boolean isDefault;

   @ConstructorProperties({ "name", "memory", "disk", "swap", "default" })
   public Package(String name, int memorySizeMb, int diskSizeGb, int swapSizeMb, boolean isDefault) {
      this.name = checkNotNull(name, "name");
      this.memorySizeMb = memorySizeMb;
      this.diskSizeGb = diskSizeGb;
      this.swapSizeMb = swapSizeMb;
      this.isDefault = isDefault;
   }

   /**
    * The "friendly name for this package
    */
   public String getName() {
      return name;
   }

   /**
    * How much memory will by available (in Mb)
    */
   public int getMemorySizeMb() {
      return memorySizeMb;
   }

   /**
    * How much disk space will be available (in Gb)
    */
   public int getDiskSizeGb() {
      return diskSizeGb;
   }

   /**
    * How much swap memory will be available (in Mb)
    */
   public int getSwapSizeMb() {
      return swapSizeMb;
   }

   /**
    * Whether this is the default package in this datacenter
    */
   public boolean isDefault() {
      return isDefault;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }
      if (object instanceof Package) {
         Package that = Package.class.cast(object);
         return Objects.equal(name, that.name);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(name);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("").omitNullValues()
                    .add("name", name)
                    .add("memorySizeMb", memorySizeMb)
                    .add("diskSizeGb", diskSizeGb)
                    .add("swapSizeMb", swapSizeMb)
                    .add("isDefault", isDefault).toString();
   }
   
   @Override
   public int compareTo(Package that) {
      return ComparisonChain.start()
                            .compare(this.name, that.name)
                            .compare(this.memorySizeMb, that.memorySizeMb)
                            .compare(this.diskSizeGb, that.diskSizeGb)
                            .compare(this.swapSizeMb, that.swapSizeMb).result();
   }
}
