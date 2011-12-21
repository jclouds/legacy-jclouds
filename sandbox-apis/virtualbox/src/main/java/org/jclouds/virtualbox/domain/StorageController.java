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

import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.Image;
import org.jclouds.javax.annotation.Nullable;
import org.virtualbox_4_1.DeviceType;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.StorageBus;

import java.util.HashSet;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_IMAGE_PREFIX;
import static org.jclouds.virtualbox.domain.HardDisk.DEFAULT_DISK_FORMAT;

/**
 * Represents a storage controller in a VirtualBox VM.
 * <p/>
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
      checkNotNull(name, "name");
      checkNotNull(bus, "bus");
      checkNotNull(hardDisks, "hardDisks");
      checkNotNull(isoImages, "isoImages");
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

   public HardDisk getHardDisk(String diskName) {

      final Iterable<HardDisk> hardDisks = filter(getHardDisks(), new HardDiskPredicate(diskName));
      return Iterables.getOnlyElement(hardDisks);
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
      if (o instanceof StorageController) {
         StorageController other = (StorageController) o;
         return Objects.equal(name, other.name) &&
                 Objects.equal(bus, other.bus) &&
                 Objects.equal(hardDisks, other.hardDisks) &&
                 Objects.equal(isoImages, other.isoImages);
      }
      return false;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(name, bus, hardDisks, isoImages);
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

      public Builder attachHardDisk(int controllerPort, int deviceSlot, String diskPath, String name) {
         hardDisks.add(new HardDisk(new DeviceDetails(controllerPort, deviceSlot, DeviceType.HardDisk), diskPath, DEFAULT_DISK_FORMAT, name));
         return this;
      }

      public Builder attachHardDisk(int controllerPort, int deviceSlot, String diskPath, String diskFormat, String name) {
         hardDisks.add(new HardDisk(new DeviceDetails(controllerPort, deviceSlot, DeviceType.HardDisk), diskPath, diskFormat, name));
         return this;
      }

      public StorageController build() {
         checkNotNull(name);
         checkNotNull(bus);
         return new StorageController(name, bus, hardDisks, dvds);
      }
   }
   
   public class HardDiskPredicate implements Predicate<HardDisk>  {
   	
   	private String diskName;
   	
      public HardDiskPredicate(String diskName) {
         this.diskName = diskName;
      }
   	
      @Override
      public boolean apply(@Nullable HardDisk hardDisk) {
         return hardDisk.getName().equals(diskName);
      }
   };
}
