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
import java.util.Date;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Represents an alert issued by Cloudstack
 *
 * @author Richard Downer
 */
public class Alert {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromAlert(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected String id;
      protected String description;
      protected Date sent;
      protected String type;

      /**
       * @see Alert#getId()
       */
      public T id(String id) {
         this.id = id;
         return self();
      }

      /**
       * @see Alert#getDescription()
       */
      public T description(String description) {
         this.description = description;
         return self();
      }

      /**
       * @see Alert#getSent()
       */
      public T sent(Date sent) {
         this.sent = sent;
         return self();
      }

      /**
       * @see Alert#getType()
       */
      public T type(String type) {
         this.type = type;
         return self();
      }

      public Alert build() {
         return new Alert(id, description, sent, type);
      }

      public T fromAlert(Alert in) {
         return this
               .id(in.getId())
               .description(in.getDescription())
               .sent(in.getSent())
               .type(in.getType());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String id;
   private final String description;
   private final Date sent;
   private final String type;

   @ConstructorProperties({
         "id", "description", "sent", "type"
   })
   protected Alert(String id, @Nullable String description, @Nullable Date sent, @Nullable String type) {
      this.id = checkNotNull(id, "id");
      this.description = description;
      this.sent = sent;
      this.type = type;
   }

   public String getId() {
      return this.id;
   }

   @Nullable
   public String getDescription() {
      return this.description;
   }

   @Nullable
   public Date getSent() {
      return this.sent;
   }

   @Nullable
   public String getType() {
      return this.type;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, description, sent, type);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Alert that = Alert.class.cast(obj);
      return Objects.equal(this.id, that.id)
            && Objects.equal(this.description, that.description)
            && Objects.equal(this.sent, that.sent)
            && Objects.equal(this.type, that.type);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("id", id).add("description", description).add("sent", sent).add("type", type);
   }

   @Override
   public String toString() {
      return string().toString();
   }

}
