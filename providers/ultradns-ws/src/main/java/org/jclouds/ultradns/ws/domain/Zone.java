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
package org.jclouds.ultradns.ws.domain;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;
import com.google.common.base.Optional;

/**
 * 
 * @author Adrian Cole
 */
public final class Zone {

   private final String id;
   private final String name;
   private final Type type;
   private final int typeCode;
   private final String accountId;
   private final String ownerId;
   private final DNSSECStatus dnssecStatus;
   private final Optional<String> primarySrc;

   private Zone(String id, String name, Type type, int typeCode, String accountId, String ownerId,
         DNSSECStatus dnssecStatus, Optional<String> primarySrc) {
      this.id = checkNotNull(id, "id");
      this.name = checkNotNull(name, "name for %s", id);
      checkArgument(typeCode >= 0, "typeCode of %s must be >= 0", id);
      this.typeCode = typeCode;
      this.type = checkNotNull(type, "type for %s", name);
      this.accountId = checkNotNull(accountId, "accountId for %s", name);
      this.ownerId = checkNotNull(ownerId, "ownerId for %s", name);
      this.dnssecStatus = checkNotNull(dnssecStatus, "dnssecStatus for %s", name);
      this.primarySrc = checkNotNull(primarySrc, "primarySrc for %s", primarySrc);
   }

   /**
    * The ID of the zone.
    */
   public String getId() {
      return id;
   }

   /**
    * The name of the domain. ex. {@code jclouds.org.} or {@code 0.0.0.0.8.b.d.0.1.0.0.2.ip6.arpa.}
    */
   public String getName() {
      return name;
   }

   /**
    * The type of the zone
    */
   public Type getType() {
      return type;
   }

   /**
    * The type of the zone
    */
   public int getTypeCode() {
      return typeCode;
   }

   /**
    * The account which this domain is a part of
    */
   public String getAccountId() {
      return accountId;
   }

   /**
    * The user that created this zone.
    */
   public String getOwnerId() {
      return ownerId;
   }

   /**
    * signed status of the zone
    */
   public DNSSECStatus getDNSSECStatus() {
      return dnssecStatus;
   }

   /**
    * present when {@link #getType} is {@link Type#SECONDARY}.  ex. {@code 192.168.1.23}
    */
   public Optional<String> getPrimarySrc() {
      return primarySrc;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, name, accountId);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      Zone that = Zone.class.cast(obj);
      return Objects.equal(this.id, that.id) && Objects.equal(this.name, that.name)
            && Objects.equal(this.accountId, that.accountId);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this).omitNullValues().add("id", id).add("name", name).add("type", type)
            .add("accountId", accountId).add("ownerId", ownerId).add("dnssecStatus", dnssecStatus)
            .add("primarySrc", primarySrc.orNull()).toString();
   }

   public static enum Type {

      PRIMARY(1), SECONDARY(2), ALIAS(3), UNRECOGNIZED(-1);

      private final int code;

      Type(int code) {
         this.code = code;
      }

      public int getCode() {
         return code;
      }

      @Override
      public String toString(){
         return this.name().toLowerCase();
      }

      public static Type fromValue(String type) {
         return fromValue(Integer.parseInt(checkNotNull(type, "type")));
      }

      public static Type fromValue(int code) {
         switch (code) {
         case 1:
            return PRIMARY;
         case 2:
            return SECONDARY;
         case 3:
            return ALIAS;
         default:
            return UNRECOGNIZED;
         }
      }
   }

   public static enum DNSSECStatus {

      SIGNED, UNSIGNED, UNRECOGNIZED;

      public static DNSSECStatus fromValue(String status) {
         try {
            return valueOf(checkNotNull(status, "status"));
         } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().from(this);
   }

   public static final class Builder {
      private String id;
      private String name;
      private Type type;
      private int typeCode = -1;
      private String accountId;
      private String ownerId;
      private DNSSECStatus dnssecStatus;
      private Optional<String> primarySrc = Optional.absent();

      /**
       * @see Zone#getId()
       */
      public Builder id(String id) {
         this.id = id;
         return this;
      }

      /**
       * @see Zone#getName()
       */
      public Builder name(String name) {
         this.name = name;
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
       * @see Zone#getTypeCode()
       */
      public Builder typeCode(int typeCode) {
         this.typeCode = typeCode;
         this.type = Type.fromValue(typeCode);
         return this;
      }

      /**
       * @see Zone#getAccountId()
       */
      public Builder accountId(String accountId) {
         this.accountId = accountId;
         return this;
      }

      /**
       * @see Zone#getOwnerId()
       */
      public Builder ownerId(String ownerId) {
         this.ownerId = ownerId;
         return this;
      }

      /**
       * @see Zone#getDNSSECStatus()
       */
      public Builder dnssecStatus(DNSSECStatus dnssecStatus) {
         this.dnssecStatus = dnssecStatus;
         return this;
      }

      /**
       * @see Zone#getPrimarySrc()
       */
      public Builder primarySrc(String primarySrc) {
         this.primarySrc = Optional.fromNullable(primarySrc);
         return this;
      }

      public Zone build() {
         return new Zone(id, name, type, typeCode, accountId, ownerId, dnssecStatus, primarySrc);
      }

      public Builder from(Zone in) {
         return this.id(in.id).name(in.name).typeCode(in.typeCode).type(in.type).accountId(in.accountId)
               .ownerId(in.ownerId).dnssecStatus(in.dnssecStatus).primarySrc(in.primarySrc.orNull());
      }
   }
}
