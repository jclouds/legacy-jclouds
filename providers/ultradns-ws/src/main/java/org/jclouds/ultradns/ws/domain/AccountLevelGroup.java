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

import org.jclouds.ultradns.ws.domain.DirectionalPool.Type;

import com.google.common.base.Objects;

/**
 * 
 * @author Adrian Cole
 */
public final class AccountLevelGroup {

   private final String id;
   private final String name;
   private final Type type;
   private final int recordCount;

   private AccountLevelGroup(String id, String name, Type type, int recordCount) {
      this.id = checkNotNull(id, "id");
      this.name = checkNotNull(name, "name of %s", id);
      this.type = checkNotNull(type, "type of %s", id);
      this.recordCount = recordCount;
      checkArgument(recordCount >= 0, "recordCount of %s must be >= 0", id);
   }

   public String getId() {
      return id;
   }

   public String getName() {
      return name;
   }

   public Type getType() {
      return type;
   }

   public int getRecordCount() {
      return recordCount;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, name, type);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      AccountLevelGroup that = AccountLevelGroup.class.cast(obj);
      return Objects.equal(this.id, that.id) && Objects.equal(this.name, that.name)
            && Objects.equal(this.type, that.type);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this).omitNullValues().add("id", id).add("name", name).add("type", type)
            .add("recordCount", recordCount).toString();
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
      private int recordCount = -1;

      /**
       * @see AccountLevelGroup#getId()
       */
      public Builder id(String id) {
         this.id = id;
         return this;
      }

      /**
       * @see AccountLevelGroup#getName()
       */
      public Builder name(String name) {
         this.name = name;
         return this;
      }

      /**
       * @see AccountLevelGroup#getType()
       */
      public Builder type(Type type) {
         this.type = type;
         return this;
      }

      /**
       * @see AccountLevelGroup#getRecordCount()
       */
      public Builder recordCount(int recordCount) {
         this.recordCount = recordCount;
         return this;
      }

      public AccountLevelGroup build() {
         return new AccountLevelGroup(id, name, type, recordCount);
      }

      public Builder from(AccountLevelGroup in) {
         return this.id(in.id).name(in.name).type(in.type).recordCount(in.recordCount);
      }
   }
}
