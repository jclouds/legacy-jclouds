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

package org.jclouds.googlecompute.domain;

import com.google.common.base.Objects;

import java.beans.ConstructorProperties;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;

/**
 * A disk attached to an Instance.
 *
 * @author David Alves
 * @see <a href="https://developers.google.com/compute/docs/reference/v1beta13/instances"/>
 */
public class InstanceAttachedDisk {

   public enum Type {
      EPHEMERAL,
      PERSISTENT
   }

   public enum Mode {
      READ_WRITE,
      READ_OLNY
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromInstanceAttachedDisk(this);
   }

   public static class Builder {

      private Type type;
      private Mode mode;
      private String source;
      private String deviceName;
      private int index;
      private boolean deleteOnTerminate;

      /**
       * @see InstanceAttachedDisk#getType()
       */
      public Builder type(Type type) {
         this.type = type;
         return this;
      }

      /**
       * @see InstanceAttachedDisk#getMode()
       */
      public Builder mode(Mode mode) {
         this.mode = mode;
         return this;
      }

      /**
       * @see InstanceAttachedDisk#getSource()
       */
      public Builder source(String source) {
         this.source = source;
         return this;
      }

      /**
       * @see InstanceAttachedDisk#getDeviceName()
       */
      public Builder deviceName(String deviceName) {
         this.deviceName = deviceName;
         return this;
      }

      /**
       * @see InstanceAttachedDisk#getIndex()
       */
      public Builder index(int index) {
         this.index = index;
         return this;
      }

      /**
       * @see InstanceAttachedDisk#isDeleteOnTerminate()
       */
      public Builder deleteOnTerminate(boolean deleteOnTerminate) {
         this.deleteOnTerminate = deleteOnTerminate;
         return this;
      }

      public InstanceAttachedDisk build() {
         return new InstanceAttachedDisk(this.type, this.mode, this.source, this.deviceName, this.index,
                 this.deleteOnTerminate);
      }

      public Builder fromInstanceAttachedDisk(InstanceAttachedDisk in) {
         return this.type(in.getType()).mode(in.getMode()).source(in.getSource()).deviceName(in.getDeviceName())
                 .index(in.getIndex()).deleteOnTerminate(in.isDeleteOnTerminate());
      }
   }

   private final Type type;
   private final Mode mode;
   private final String source;
   private final String deviceName;
   private final int index;
   private final boolean deleteOnTerminate;

   @ConstructorProperties({
           "type", "mode", "source", "deviceName", "index", "deleteOnTerminate"
   })
   public InstanceAttachedDisk(Type type, Mode mode, String source, String deviceName, int index,
                               boolean deleteOnTerminate) {
      this.type = type;
      this.mode = mode;
      this.source = source;
      this.deviceName = deviceName;
      this.index = index;
      this.deleteOnTerminate = deleteOnTerminate;
   }

   /**
    * @return type of the disk, either EPHEMERAL or PERSISTENT. Note that persistent disks must be created before you
    *         can specify them here.
    */
   public Type getType() {
      return type;
   }

   /**
    * @return the mode in which to attach this disk, either READ_WRITE or READ_ONLY.
    */
   public Mode getMode() {
      return mode;
   }

   /**
    * @return persistent disk only; the URL of the persistent disk resource.
    */
   public String getSource() {
      return source;
   }

   /**
    * @return persistent disk only; must be unique within the instance when specified. This represents a unique
    *         device name that is reflected into the /dev/ tree of a Linux operating system running within the
    *         instance. If not specified, a default will be chosen by the system.
    */
   public String getDeviceName() {
      return deviceName;
   }

   /**
    * @return a zero-based index to assign to this disk, where 0 is reserved for the boot disk. If not specified,
    *         the server will choose an appropriate value.
    */
   public int getIndex() {
      return index;
   }

   /**
    * @return persistent disk only; If true, delete the disk and all its data when the associated instance is deleted
    *         . This property defaults to false if not specified.
    */
   public boolean isDeleteOnTerminate() {
      return deleteOnTerminate;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(type, mode, source, deviceName);
   }


   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      InstanceAttachedDisk that = InstanceAttachedDisk.class.cast(obj);
      return equal(this.type, that.type)
              && equal(this.mode, that.mode)
              && equal(this.source, that.source)
              && equal(this.deviceName, that.deviceName)
              && equal(this.index, that.index)
              && equal(this.deleteOnTerminate, that.deleteOnTerminate);
   }

   /**
    * {@inheritDoc}
    */
   protected Objects.ToStringHelper string() {
      return toStringHelper(this)
              .add("type", type).add("mode", mode).add("source", source).add("deviceName", deviceName).add("index",
                      index).add("deleteOnTerminate", deleteOnTerminate);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return string().toString();
   }
}
