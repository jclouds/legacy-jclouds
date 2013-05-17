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
package org.jclouds.trmk.vcloud_0_8.domain;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.emptyToNull;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * 
 * @author Adrian Cole
 * 
 */
public class VAppConfiguration {
   private String name = null;
   private Integer processorCount = null;
   private Long memory = null;
   private List<Long> disks = Lists.newArrayList();
   private List<Integer> disksToDelete = Lists.newArrayList();

   /**
    * The vApp name
    * 
    */
   public VAppConfiguration changeNameTo(String name) {
      this.name = checkNotNull(emptyToNull(name), "name must be defined");
      return this;
   }

   /**
    * the number of virtual CPUs.
    */
   public VAppConfiguration changeProcessorCountTo(int cpus) {
      checkArgument(cpus >= 1, "cpu count must be positive");
      this.processorCount = cpus;
      return this;
   }

   /**
    * number of MB of memory.
    */
   public VAppConfiguration changeMemoryTo(long megabytes) {
      checkArgument(megabytes >= 1, "megabytes must be positive");
      this.memory = megabytes;
      return this;
   }

   /**
    * To define a new disk, all you need to define is the size of the disk. The allowed values are a
    * multiple of 1048576.
    */
   public VAppConfiguration addDisk(long kilobytes) {
      checkArgument(kilobytes > 0, "kilobytes must be positive");
      checkArgument(kilobytes % 1048576 == 0, "disk must be an interval of 1048576");
      this.disks.add(kilobytes);
      return this;
   }

   /**
    * To remove a disk, you specify its addressOnParent.
    * 
    * Ex.
    * 
    * <pre>
    * Set&lt;ResourceAllocation&gt; disks = Sets.newLinkedHashSet(vApp.getResourceAllocationByType().get(
    *          ResourceType.DISK_DRIVE));
    * ResourceAllocation lastDisk = disks.last();
    * VAppConfiguration config = deleteDiskWithAddressOnParent(lastDisk.getAddressOnParent());
    * </pre>
    */
   public VAppConfiguration deleteDiskWithAddressOnParent(int addressOnParent) {
      checkArgument(addressOnParent > 0, "you cannot delete the system disk");
      disksToDelete.add(addressOnParent);
      return this;
   }

   public static class Builder {

      /**
       * @see VAppConfiguration#changeNameTo(String)
       */
      public static VAppConfiguration changeNameTo(String name) {
         VAppConfiguration options = new VAppConfiguration();
         return options.changeNameTo(name);
      }

      /**
       * @see VAppConfiguration#changeProcessorCountTo(int)
       */
      public static VAppConfiguration changeProcessorCountTo(int cpus) {
         VAppConfiguration options = new VAppConfiguration();
         return options.changeProcessorCountTo(cpus);
      }

      /**
       * @see VAppConfiguration#changeMemoryTo(long)
       */
      public static VAppConfiguration changeMemoryTo(long megabytes) {
         VAppConfiguration options = new VAppConfiguration();
         return options.changeMemoryTo(megabytes);
      }

      /**
       * @see VAppConfiguration#addDisk(long)
       */
      public static VAppConfiguration addDisk(long kilobytes) {
         VAppConfiguration options = new VAppConfiguration();
         return options.addDisk(kilobytes);
      }

      /**
       * @see VAppConfiguration#deleteDiskWithAddressOnParent(int)
       */
      public static VAppConfiguration deleteDiskWithAddressOnParent(int addressOnParent) {
         VAppConfiguration options = new VAppConfiguration();
         return options.deleteDiskWithAddressOnParent(addressOnParent);
      }

   }

   public Integer getProcessorCount() {
      return processorCount;
   }

   public Long getMemory() {
      return memory;
   }

   public List<Long> getDisks() {
      return disks;
   }

   public String getName() {
      return name;
   }

   public List<Integer> getDisksToDelete() {
      return disksToDelete;
   }
}
