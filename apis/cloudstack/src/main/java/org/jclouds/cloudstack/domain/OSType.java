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
package org.jclouds.cloudstack.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Class OSType
 *
 * @author Adrian Cole
 */
public class OSType implements Comparable<OSType> {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromOSType(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected String id;
      protected String OSCategoryId;
      protected String description;

      /**
       * @see OSType#getId()
       */
      public T id(String id) {
         this.id = id;
         return self();
      }

      /**
       * @see OSType#getOSCategoryId()
       */
      public T OSCategoryId(String OSCategoryId) {
         this.OSCategoryId = OSCategoryId;
         return self();
      }

      /**
       * @see OSType#getDescription()
       */
      public T description(String description) {
         this.description = description;
         return self();
      }

      public OSType build() {
         return new OSType(id, OSCategoryId, description);
      }

      public T fromOSType(OSType in) {
         return this
               .id(in.getId())
               .OSCategoryId(in.getOSCategoryId())
               .description(in.getDescription());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String id;
   private final String OSCategoryId;
   private final String description;

   @ConstructorProperties({
         "id", "oscategoryid", "description"
   })
   protected OSType(String id, @Nullable String OSCategoryId, @Nullable String description) {
      this.id = checkNotNull(id, "id");
      this.OSCategoryId = OSCategoryId;
      this.description = description;
   }

   /**
    * @return the ID of the OS type
    */
   public String getId() {
      return this.id;
   }

   /**
    * @return the ID of the OS category
    */
   @Nullable
   public String getOSCategoryId() {
      return this.OSCategoryId;
   }

   /**
    * @return the name/description of the OS type
    */
   @Nullable
   public String getDescription() {
      return this.description;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, OSCategoryId, description);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      OSType that = OSType.class.cast(obj);
      return Objects.equal(this.id, that.id)
            && Objects.equal(this.OSCategoryId, that.OSCategoryId)
            && Objects.equal(this.description, that.description);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this).add("id", id).add("OSCategoryId", OSCategoryId).add("description", description);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   @Override
   public int compareTo(OSType o) {
      return id.compareTo(o.getId());
   }
}
