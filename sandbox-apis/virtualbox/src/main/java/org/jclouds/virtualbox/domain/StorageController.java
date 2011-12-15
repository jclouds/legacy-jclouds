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

package org.jclouds.virtualbox.domain;

import org.virtualbox_4_1.DeviceType;
import org.virtualbox_4_1.StorageBus;

import java.util.HashSet;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.virtualbox.domain.HardDisk.DEFAULT_DISK_FORMAT;

/**
 * Represents a storage controller in a VirtualBox VM.
 *
 * name is the unique name of the controller.
 * bus is the type of connection bus for the controller
 * hardDisks contains the hard disks that are attached (or should be attached) to this controller
 * isoImages contains the ISOs that are attached (or should be attached) to this controller
 *
 * @see StorageBus
 */
public class StorageController {
   private final String name;
   private final StorageBus bus;
   private Set<HardDisk> hardDisks;
   private Set<IsoImage> isoImages;

   public StorageController(String name, StorageBus bus, Set<HardDisk> hardDisks, Set<IsoImage> isoImages) {
      this.name = name;
      this.bus = bus;
      this.hardDisks = hardDisks;
      this.isoImages = isoImages;
   }

   public String getName() {
      return name;
   }

   public StorageBus getBus() {
      return bus;
   }

   public Set<HardDisk> getHardDisks() {
      return hardDisks;
   }

   public Set<IsoImage> getIsoImages() {
      return isoImages;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      StorageController that = (StorageController) o;

      if (bus != that.bus) return false;
      if (isoImages != null ? !isoImages.equals(that.isoImages) : that.isoImages != null) return false;
      if (hardDisks != null ? !hardDisks.equals(that.hardDisks) : that.hardDisks != null) return false;
      if (name != null ? !name.equals(that.name) : that.name != null) return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = name != null ? name.hashCode() : 0;
      result = 31 * result + (bus != null ? bus.hashCode() : 0);
      result = 31 * result + (hardDisks != null ? hardDisks.hashCode() : 0);
      result = 31 * result + (isoImages != null ? isoImages.hashCode() : 0);
      return result;
   }

   @Override
   public String toString() {
      return "StorageController{" +
              "name='" + name + '\'' +
              ", bus=" + bus +
              ", hardDisks=" + hardDisks +
              ", isoImages=" + isoImages +
              '}';
   }

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {

      private String name;
      private StorageBus bus;
      private Set<HardDisk> hardDisks = new HashSet<HardDisk>();
      private Set<IsoImage> dvds = new HashSet<IsoImage>();

      public Builder name(String name) {
         this.name = name;
         return this;
      }

      public Builder bus(StorageBus bus) {
         this.bus = bus;
         return this;
      }

      public Builder attachISO(int controllerPort, int deviceSlot, String sourcePath) {
         dvds.add(new IsoImage(new DeviceDetails(controllerPort, deviceSlot, DeviceType.DVD), sourcePath));
         return this;
      }

      public Builder attachHardDisk(int controllerPort, int deviceSlot, String diskPath) {
         hardDisks.add(new HardDisk(new DeviceDetails(controllerPort, deviceSlot, DeviceType.HardDisk), diskPath, DEFAULT_DISK_FORMAT));
         return this;
      }

      public Builder attachHardDisk(int controllerPort, int deviceSlot, String diskPath, String diskFormat) {
         hardDisks.add(new HardDisk(new DeviceDetails(controllerPort, deviceSlot, DeviceType.HardDisk), diskPath, diskFormat));
         return this;
      }

      public StorageController build() {
         checkNotNull(name);
         checkNotNull(bus);
         return new StorageController(name, bus, hardDisks, dvds);
      }

   }
}
