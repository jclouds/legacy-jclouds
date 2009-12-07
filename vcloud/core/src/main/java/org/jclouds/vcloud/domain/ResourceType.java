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
package org.jclouds.vcloud.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.vcloud.VCloudAsyncClient;

/**
 * 
 * VirtualResource such as disks or CPU
 * 
 * @author Adrian Cole
 * @see VCloudAsyncClient#getVApp
 * @see <a href="http://blogs.vmware.com/vapp/2009/11/index.html"/>
 * 
 */
public enum ResourceType {
   OTHER, PROCESSOR, MEMORY, IDE_CONTROLLER, SCSI_CONTROLLER, ETHERNET_ADAPTER, FLOPPY_DRIVE, CD_DRIVE, DVD_DRIVE, DISK_DRIVE, USB_CONTROLLER;

   public String value() {
      switch (this) {
         case OTHER:
            return "1";
         case PROCESSOR:
            return "3";
         case MEMORY:
            return "4";
         case IDE_CONTROLLER:
            return "5";
         case SCSI_CONTROLLER:
            return "6";
         case ETHERNET_ADAPTER:
            return "10";
         case FLOPPY_DRIVE:
            return "14";
         case CD_DRIVE:
            return "15";
         case DVD_DRIVE:
            return "16";
         case DISK_DRIVE:
            return "17";
         case USB_CONTROLLER:
            return "23";
         default:
            throw new IllegalArgumentException("invalid type:" + this);
      }
   }

   public static ResourceType fromValue(String type) {
      return fromValue(Integer.parseInt(checkNotNull(type, "type")));
   }

   public static ResourceType fromValue(int v) {
      switch (v) {
         case 1:
            return OTHER;
         case 3:
            return PROCESSOR;
         case 4:
            return MEMORY;
         case 5:
            return IDE_CONTROLLER;
         case 6:
            return SCSI_CONTROLLER;
         case 10:
            return ETHERNET_ADAPTER;
         case 14:
            return FLOPPY_DRIVE;
         case 15:
            return CD_DRIVE;
         case 16:
            return DVD_DRIVE;
         case 17:
            return DISK_DRIVE;
         case 23:
            return USB_CONTROLLER;
         default:
            throw new IllegalArgumentException("invalid type:" + v);
      }
   }
}