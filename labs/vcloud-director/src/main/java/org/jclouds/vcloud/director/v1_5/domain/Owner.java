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
package org.jclouds.vcloud.director.v1_5.domain;

import static com.google.common.base.Objects.equal;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Represents the owner of this entity.
 *
 * <pre>
 * &lt;complexType name="Owner" /&;
 * </pre>
 *
 * @since 1.5
 */
@XmlRootElement(name = "Owner")
@XmlType(name = "OwnerType")
public class Owner extends Resource {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   @Override
   public Builder<?> toBuilder() {
      return builder().fromOwner(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }
   
   public abstract static class Builder<B extends Builder<B>> extends Resource.Builder<B> {

      private Reference user;

      /**
       * @see Owner#getUser()
       */
      public B user(Reference user) {
         this.user = user;
         return self();
      }

      @Override
      public Owner build() {
         return new Owner(this);
      }
      
      public B fromOwner(Owner in) {
         return fromResource(in)
               .user(in.getUser());
      }
   }

   protected Owner() {
      // for JAXB
   }

   public Owner(Builder<?> builder) {
      super(builder);
      this.user = builder.user;
   }

   @XmlElement(name = "User", required = true)
   private Reference user;

   /**
    * Gets the value of the user property.
    */
   public Reference getUser() {
      return user;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      Owner that = Owner.class.cast(o);
      return super.equals(that) && equal(this.user, that.user);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), user);
   }

   @Override
   public ToStringHelper string() {
      return super.string().add("user", user);
   }
}
