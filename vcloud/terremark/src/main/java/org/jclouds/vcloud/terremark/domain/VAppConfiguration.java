/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.vcloud.terremark.domain;

import static com.google.common.base.Preconditions.checkArgument;

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

   /**
    * The vApp name has the following requirements: Name can use uppercase and/or lowercase letters.
    * Name can contain numbers or hyphens (-). Name may only begin with a letter. A maximum of 15
    * characters are allowed
    * 
    */
   public VAppConfiguration changeNameTo(String name) {
      checkArgument(
               name.matches("^[a-zA-Z][-a-zA-Z0-9]+"),
               "Name can use uppercase and/or lowercase letters, numbers or hyphens (-). Name may only begin with a letter.");
      checkArgument(name.length() <= 15, "A maximum of 15 characters are allowed.");
      this.name = name;
      return this;
   }

   /**
    * the number of virtual CPUs. You can set this to “1,” “2,” “4,” or “8.”
    */
   public VAppConfiguration changeProcessorCountTo(int cpus) {
      checkArgument(cpus == 1 || cpus == 2 || cpus == 4 || cpus == 8,
               "cpu count must be in 1,2,4,8");
      this.processorCount = cpus;
      return this;
   }

   /**
    * number of MB of memory. This should be either 512 or a multiple of 1024 (1 GB).
    */
   public VAppConfiguration changeMemoryTo(long megabytes) {
      checkArgument(megabytes == 512 || megabytes % 1024 == 0,
               "memory must be 512 or an interval of 1024");
      checkArgument(megabytes <= 16384, "memory must be no more than 16GB");
      this.memory = megabytes;
      return this;
   }

   /**
    * To define a new disk, all you need to define is the size of the disk. The allowed values are a
    * multiple of 1048576. <br/>
    * For example: <br/>
    * 1048576 (1 GB) <br/>
    * 2097152 (2 GB) <br/>
    * 3145728 (3 GB) <br/>
    * 4194304 (4 GB) <br/>
    * 5242880 (5 GB) <br/>
    * ... <br/>
    * 524288000 (500 GB) <br/>
    * You can have a total of 15 disks. Each disk can contain up to 500 GB of storage.
    */
   public VAppConfiguration addDisk(long kilobytes) {
      checkArgument(kilobytes % 1048576 == 0, "disk must be an interval of 1048576");
      checkArgument(kilobytes <= 524288000, "disk must be no more than 500GB");
      checkArgument(disks.size() < 14, "you can only add up to 14 disks for a total of 15");
      this.disks.add(kilobytes);
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
}
