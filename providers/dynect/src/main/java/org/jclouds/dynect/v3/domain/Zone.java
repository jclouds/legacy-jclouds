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
package org.jclouds.dynect.v3.domain;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;

import javax.inject.Named;

import com.google.common.base.CaseFormat;
import com.google.common.base.Objects;

/**
 * @author Adrian Cole
 */
public final class Zone {

   private final String fqdn;
   @Named("zone_type")
   private final Type type;
   private final int serial;
   @Named("serial_style")
   private final SerialStyle serialStyle;

   public static enum Type {
      PRIMARY, SECONDARY;

      @Override
      public String toString() {
         return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name());
      }

      public static Type fromValue(String type) {
         return valueOf(CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, checkNotNull(type, "type")));
      }

   }

   public static enum SerialStyle {
      /**
       * Serials are incremented by 1 on every change
       */
      INCREMENT,
      /**
       * Serials will be the UNIX timestamp at the time of the publish
       */
      EPOCH,
      /**
       * Serials will be in the form of YYYYMMDDxx where xx is incremented by
       * one for each change during that particular day
       */
      DAY,
      /**
       * Serials will be in the form of YYMMDDHHMM
       */
      MINUTE,

      UNRECOGNIZED;

      @Override
      public String toString() {
         return name().toLowerCase();
      }

      public static SerialStyle fromValue(String serialStyle) {
         try {
            return valueOf(checkNotNull(serialStyle, "serialStyle").toUpperCase());
         } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }

   }

   @ConstructorProperties({ "zone", "zone_type", "serial", "serial_style" })
   private Zone(String fqdn, Type type, int serial, SerialStyle serialStyle) {
      this.fqdn = checkNotNull(fqdn, "fqdn");
      this.type = checkNotNull(type, "type for %s", fqdn);
      this.serial = checkNotNull(serial, "serial for %s", fqdn);
      this.serialStyle = checkNotNull(serialStyle, "serialStyle for %s", serialStyle);
   }

   /**
    * The fqdn of the requested zone
    */
   public String getFQDN() {
      return fqdn;
   }

   /**
    * A unique string that identifies the request to create the hosted zone.
    */
   public Type getType() {
      return type;
   }

   /**
    * The current serial number of the zone
    */
   public int getSerial() {
      return serial;
   }

   /**
    * The style of the zone's serial
    */
   public SerialStyle getSerialStyle() {
      return serialStyle;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(fqdn, type);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      Zone that = Zone.class.cast(obj);
      return equal(this.fqdn, that.fqdn) && equal(this.type, that.type);
   }

   @Override
   public String toString() {
      return toStringHelper(this).omitNullValues().add("fqdn", fqdn).add("type", type).add("serial", serial)
            .add("serialStyle", serialStyle).toString();
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().from(this);
   }

   public static final class Builder {
      private String fqdn;
      private Type type;
      private int serial;
      private SerialStyle serialStyle;

      /**
       * @see Zone#getFQDN()
       */
      public Builder fqdn(String fqdn) {
         this.fqdn = fqdn;
         return this;
      }

      /**
       * @see Zone#getType()
       */
      public Builder type(Type type) {
         this.type = type;
         return this;
      }

      /**
       * @see Zone#getSerial()
       */
      public Builder serial(int serial) {
         this.serial = serial;
         return this;
      }

      /**
       * @see Zone#getSerialStyle()
       */
      public Builder serialStyle(SerialStyle serialStyle) {
         this.serialStyle = serialStyle;
         return this;
      }

      public Zone build() {
         return new Zone(fqdn, type, serial, serialStyle);
      }

      public Builder from(Zone in) {
         return this.fqdn(in.fqdn).type(in.type).serial(in.serial).serialStyle(in.serialStyle);
      }
   }
}
