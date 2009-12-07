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

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 
 * VirtualResource such as disks or CPU
 * 
 * @author Adrian Cole
 * 
 */
public enum ResourceType {

   VIRTUAL_CPU,

   MEMORY,

   SCSI_CONTROLLER,

   VIRTUAL_DISK;

   public String value() {
      switch (this) {
         case VIRTUAL_CPU:
            return "3";
         case MEMORY:
            return "4";
         case SCSI_CONTROLLER:
            return "6";
         case VIRTUAL_DISK:
            return "17";
         default:
            throw new IllegalArgumentException("invalid type:" + this);
      }
   }

   public static ResourceType fromValue(String type) {
      return fromValue(Integer.parseInt(checkNotNull(type, "type")));
   }

   public static ResourceType fromValue(int v) {
      switch (v) {
         case 3:
            return VIRTUAL_CPU;
         case 4:
            return MEMORY;
         case 6:
            return SCSI_CONTROLLER;
         case 17:
            return VIRTUAL_DISK;
         default:
            throw new IllegalArgumentException("invalid type:" + v);
      }
   }
}