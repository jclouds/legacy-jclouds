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

import java.util.Date;

import org.jclouds.ultradns.ws.domain.Zone.Type;

import com.google.common.base.Objects;

/**
 * 
 * @author Adrian Cole
 */
public final class ZoneProperties {

   private final String name;
   private final Type type;
   private final int typeCode;
   private final Date modified;
   private final int resourceRecordCount;

   private ZoneProperties(String name, Type type, int typeCode, Date modified, int resourceRecordCount) {
      this.name = checkNotNull(name, "name");
      checkArgument(typeCode >= 0, "typeCode of %s must be >= 0", name);
      this.typeCode = typeCode;
      this.type = checkNotNull(type, "type for %s", name);
      this.modified = checkNotNull(modified, "modified for %s", name);
      this.resourceRecordCount = checkNotNull(resourceRecordCount, "resourceRecordCount for %s", name);
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
    * Last time the zone was modified
    */
   public Date getModified() {
      return modified;
   }

   /**
    * The count of records in this zone.
    */
   public int getResourceRecordCount() {
      return resourceRecordCount;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(name);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      ZoneProperties that = ZoneProperties.class.cast(obj);
      return Objects.equal(this.name, that.name);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this).add("name", name).add("type", type)
            .add("modified", modified).add("resourceRecordCount", resourceRecordCount).toString();
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().from(this);
   }

   public static final class Builder {
      private String name;
      private Type type;
      private int typeCode = -1;
      private Date modified;
      private int resourceRecordCount;

      /**
       * @see ZoneProperties#getName()
       */
      public Builder name(String name) {
         this.name = name;
         return this;
      }

      /**
       * @see ZoneProperties#getType()
       */
      public Builder type(Type type) {
         this.type = type;
         return this;
      }

      /**
       * @see ZoneProperties#getTypeCode()
       */
      public Builder typeCode(int typeCode) {
         this.typeCode = typeCode;
         this.type = Type.fromValue(typeCode);
         return this;
      }

      /**
       * @see ZoneProperties#getModified()
       */
      public Builder modified(Date modified) {
         this.modified = modified;
         return this;
      }

      /**
       * @see ZoneProperties#getResourceRecordCount()
       */
      public Builder resourceRecordCount(int resourceRecordCount) {
         this.resourceRecordCount = resourceRecordCount;
         return this;
      }

      public ZoneProperties build() {
         return new ZoneProperties(name, type, typeCode, modified, resourceRecordCount);
      }

      public Builder from(ZoneProperties in) {
         return this.name(in.name).typeCode(in.typeCode).type(in.type).modified(in.modified)
               .resourceRecordCount(in.resourceRecordCount);
      }
   }
}
