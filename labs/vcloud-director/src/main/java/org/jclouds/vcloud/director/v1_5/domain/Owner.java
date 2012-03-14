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

import java.net.URI;
import java.util.Set;

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
public class Owner extends ResourceType<Owner> {

   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return new Builder().fromOwner(this);
   }

   public static class Builder extends ResourceType.Builder<Owner> {

      private ReferenceType<?> user;

      /**
       * @see Owner#getUser()
       */
      public Builder user(ReferenceType<?> user) {
         this.user = user;
         return this;
      }

      @Override
      public Owner build() {
         return new Owner(href, type, links, user);
      }

      /**
       * @see ResourceType#getHref()
       */
      @Override
      public Builder href(URI href) {
         super.href(href);
         return this;
      }

      /**
       * @see ResourceType#getType()
       */
      @Override
      public Builder type(String type) {
         super.type(type);
         return this;
      }

      /**
       * @see ResourceType#getLinks()
       */
      @Override
      public Builder links(Set<Link> links) {
         return Builder.class.cast(super.links(links));
      }

      /**
       * @see ResourceType#getLinks()
       */
      @Override
      public Builder link(Link link) {
         return Builder.class.cast(super.link(link));
      }


      @Override
      public Builder fromResourceType(ResourceType<Owner> in) {
         return Builder.class.cast(super.fromResourceType(in));
      }

      public Builder fromOwner(Owner in) {
         return fromResourceType(in)
               .user(in.getUser());
      }
   }

   protected Owner() {
      // for JAXB
   }

   public Owner(URI href, String type, Set<Link> links, ReferenceType<?> user) {
      super(href, type, links);
      this.user = user;
   }

   @XmlElement(name = "User", required = true)
   protected ReferenceType<?> user;

   /**
    * Gets the value of the user property.
    */
   public ReferenceType<?> getUser() {
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
