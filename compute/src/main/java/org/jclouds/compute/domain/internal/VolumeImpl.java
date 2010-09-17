/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.compute.domain.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Nullable;

import org.jclouds.compute.domain.Volume;

/**
 * @author Adrian Cole
 */
public class VolumeImpl implements Volume {
   /** The serialVersionUID */
   private static final long serialVersionUID = -3306004212804159093L;

   private final String id;
   private final Volume.Type type;
   private final @Nullable
   Float size;
   private final @Nullable
   String device;
   private final boolean bootDevice;
   private final boolean durable;

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

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return "[id=" + getId() + ", type=" + type + ", size=" + size + ", device=" + device + ", durable=" + durable
               + ", isBootDevice=" + bootDevice + "]";
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (bootDevice ? 1231 : 1237);
      result = prime * result + ((device == null) ? 0 : device.hashCode());
      result = prime * result + (durable ? 1231 : 1237);
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      result = prime * result + ((size == null) ? 0 : size.hashCode());
      result = prime * result + ((type == null) ? 0 : type.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      VolumeImpl other = (VolumeImpl) obj;
      if (bootDevice != other.bootDevice)
         return false;
      if (device == null) {
         if (other.device != null)
            return false;
      } else if (!device.equals(other.device))
         return false;
      if (durable != other.durable)
         return false;
      if (id == null) {
         if (other.id != null)
            return false;
      } else if (!id.equals(other.id))
         return false;
      if (size == null) {
         if (other.size != null)
            return false;
      } else if (!size.equals(other.size))
         return false;
      if (type == null) {
         if (other.type != null)
            return false;
      } else if (!type.equals(other.type))
         return false;
      return true;
   }

}
