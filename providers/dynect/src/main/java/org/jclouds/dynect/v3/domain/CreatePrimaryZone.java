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

import javax.inject.Named;

import org.jclouds.dynect.v3.domain.Zone.SerialStyle;

import com.google.common.base.Function;
import com.google.common.base.Objects;

/**
 * @author Adrian Cole
 */
public final class CreatePrimaryZone {
   // persisted via path param
   private final transient String fqdn;
   @Named("rname")
   private final String contact;
   @Named("serial_style")
   private final SerialStyle serialStyle;
   @Named("ttl")
   private final int defaultTTL;

   private CreatePrimaryZone(String fqdn, String contact, SerialStyle serialStyle, int defaultTTL) {
      this.fqdn = checkNotNull(fqdn, "fqdn");
      this.contact = checkNotNull(contact, "contact for %s", fqdn);
      this.defaultTTL = checkNotNull(defaultTTL, "defaultTTL for %s", fqdn);
      this.serialStyle = checkNotNull(serialStyle, "serialStyle for %s", serialStyle);
   }

   /**
    * The fqdn of the zone to create
    */
   public String getFQDN() {
      return fqdn;
   }

   /**
    * The administrative contact or {@code rname} for the {@code SOA} record.
    */
   public String getContact() {
      return contact;
   }

   /**
    * Default TTL (in seconds) for records in the zone.  Defaults to 3600.
    */
   public int getDefaultTTL() {
      return defaultTTL;
   }

   /**
    * The style of the zone's serial. Defaults to {@link SerialStyle#INCREMENT}.
    */
   public SerialStyle getSerialStyle() {
      return serialStyle;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(fqdn, contact);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      CreatePrimaryZone that = CreatePrimaryZone.class.cast(obj);
      return equal(this.fqdn, that.fqdn) && equal(this.contact, that.contact);
   }

   @Override
   public String toString() {
      return toStringHelper(this).omitNullValues().add("fqdn", fqdn).add("contact", contact)
            .add("defaultTTL", defaultTTL).add("serialStyle", serialStyle).toString();
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().from(this);
   }

   public static final class Builder {
      private String fqdn;
      private String contact;
      private int defaultTTL = 3600;
      private SerialStyle serialStyle = SerialStyle.INCREMENT;

      /**
       * @see CreatePrimaryZone#getFQDN()
       */
      public Builder fqdn(String fqdn) {
         this.fqdn = fqdn;
         return this;
      }

      /**
       * @see CreatePrimaryZone#getContact()
       */
      public Builder contact(String contact) {
         this.contact = contact;
         return this;
      }

      /**
       * @see CreatePrimaryZone#getDefaultTTL()
       */
      public Builder defaultTTL(int defaultTTL) {
         this.defaultTTL = defaultTTL;
         return this;
      }

      /**
       * @see CreatePrimaryZone#getSerialStyle()
       */
      public Builder serialStyle(SerialStyle serialStyle) {
         this.serialStyle = serialStyle;
         return this;
      }

      public CreatePrimaryZone build() {
         return new CreatePrimaryZone(fqdn, contact, serialStyle, defaultTTL);
      }

      public Builder from(CreatePrimaryZone in) {
         return this.fqdn(in.fqdn).contact(in.contact).serialStyle(in.serialStyle).defaultTTL(in.defaultTTL);
      }
   }

   public static final class ToFQDN implements Function<Object, String> {
      public String apply(Object in) {
         return CreatePrimaryZone.class.cast(in).getFQDN();
      }
   }
}
