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
package org.jclouds.compute.domain.internal;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.compute.domain.Volume;
import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * @author Adrian Cole
 */
public class VolumeImpl implements Volume {

   private final String id;
   private final Volume.Type type;
   @Nullable
   private final Float size;
   @Nullable
   private final String device;
   private final boolean bootDevice;
   private final boolean durable;

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      VolumeImpl that = VolumeImpl.class.cast(o);
      return equal(this.id, that.id) && equal(this.getType(), that.getType()) && equal(this.size, that.size)
               && equal(this.device, that.device) && equal(this.bootDevice, that.bootDevice)
               && equal(this.durable, that.durable);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, size, device, bootDevice, durable);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper("").omitNullValues().add("id", id).add("type", getType()).add("size", size)
               .add("device", device).add("bootDevice", bootDevice).add("durable", durable);
   }

   public VolumeImpl(@Nullable String id, Volume.Type type, @Nullable Float size, @Nullable String device,
            boolean bootDevice, boolean durable) {
      this.id = id;
      this.type = checkNotNull(type, "type");
      this.size = size;
      this.device = device;
      this.bootDevice = bootDevice;
      this.durable = durable;
   }

   public VolumeImpl(@Nullable Float size, boolean bootDevice, boolean durable) {
      this(null, Volume.Type.LOCAL, size, null, bootDevice, durable);
   }

   public VolumeImpl(@Nullable Float size, @Nullable String device, boolean bootDevice, boolean durable) {
      this(null, Volume.Type.LOCAL, size, device, bootDevice, durable);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getId() {
      return id;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Volume.Type getType() {
      return type;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Float getSize() {
      return size;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getDevice() {
      return device;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean isDurable() {
      return durable;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean isBootDevice() {
      return bootDevice;
   }


}
