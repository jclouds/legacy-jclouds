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
package org.jclouds.compute.domain;

import org.jclouds.compute.domain.internal.VolumeImpl;
import org.jclouds.javax.annotation.Nullable;

import com.google.inject.ImplementedBy;

/**
 * Describes what appears as a disk to an {@link OperatingSystem}
 * 
 * @author Adrian Cole
 */
@ImplementedBy(VolumeImpl.class)
public interface Volume {

   /**
    * Describes the cardinal type of a volume; used to determine scope and exclusivity.
    */
   public enum Type {
      /**
       * scoped to a node cannot be remounted onto another node.
       */
      LOCAL,

      /**
       * shared mount, non-exclusive by nature, although may happen to be exclusive.
       */
      NAS,
      /**
       * network storage; exclusive by nature, remountable onto other machines.
       */
      SAN;
   }

   /**
    * Unique identifier. If set, can be used to reference the volume for reorganizing within
    * {@link Hardware} or external commands such as backups.
    * 
    */
   @Nullable
   String getId();

   /**
    * Describes the cardinal type of a volume; used to determine scope and exclusivity.
    */
   Type getType();

   /**
    * @return capacity in gigabytes, if available
    * 
    */
   @Nullable
   Float getSize();

   /**
    * 
    * @return device this volume relates to on an operating system, if available
    */
   @Nullable
   String getDevice();

   /**
    * 
    * @return true if this survives restarts
    */
   boolean isDurable();

   /**
    * 
    * @return true if this is the boot device
    */
   boolean isBootDevice();

}
