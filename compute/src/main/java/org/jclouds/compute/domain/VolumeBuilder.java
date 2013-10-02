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

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.compute.domain.internal.VolumeImpl;
import org.jclouds.javax.annotation.Nullable;

/**
 * 
 * @author Adrian Cole
 */
public class VolumeBuilder {

   private Volume.Type type;
   private String id;
   @Nullable
   private Float size;
   @Nullable
   private String device;
   private boolean bootDevice;
   private boolean durable;

   public VolumeBuilder type(Volume.Type type) {
      this.type = checkNotNull(type, "type");
      return this;
   }

   public VolumeBuilder id(String id) {
      this.id = checkNotNull(id, "id");
      return this;
   }

   public VolumeBuilder size(@Nullable Float size) {
      this.size = size;
      return this;
   }

   public VolumeBuilder device(@Nullable String device) {
      this.device = device;
      return this;
   }

   public VolumeBuilder bootDevice(boolean bootDevice) {
      this.bootDevice = bootDevice;
      return this;
   }

   public VolumeBuilder durable(boolean durable) {
      this.durable = durable;
      return this;
   }

   public Volume build() {
      return new VolumeImpl(id, type, size, device, bootDevice, durable);
   }

   public static Volume fromVolume(Volume in) {
      return new VolumeImpl(in.getId(), in.getType(), in.getSize(), in.getDevice(), in.isBootDevice(), in.isDurable());
   }
}
