/**
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

import java.net.URI;

/**
 * A reference to a resource.
 *
 * @author grkvlt@apache.org
 */
public class Reference extends ReferenceType<Reference> {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return new Builder().fromReference(this);
   }

   public static class Builder extends ReferenceType.Builder<Reference> {

      @Override
      public Reference build() {
         return new Reference(href, id, name, type);
      }

      /**
       * @see ReferenceType#getHref()
       */
      @Override
      public Builder href(URI href) {
         this.href = href;
         return this;
      }

      /**
       * @see ReferenceType#getId()
       */
      @Override
      public Builder id(String id) {
         this.id = id;
         return this;
      }

      /**
       * @see ReferenceType#getType()
       */
      @Override
      public Builder type(String type) {
         this.type = type;
         return this;
      }

      /**
       * @see ReferenceType#getName()
       */
      @Override
      public Builder name(String name) {
         this.name = name;
         return this;
      }

      @Override
      public Builder fromReferenceType(ReferenceType<Reference> in) {
         return Builder.class.cast(super.fromReferenceType(in));
      }

      public Builder fromReference(Reference in) {
         return fromReferenceType(in);
      }
      
      public Builder fromEntity(EntityType<?> in) {
         return href(in.getHref()).id(in.getId()).name(in.getName()).type(in.getType());
      }
   }

   public Reference(URI href, String id, String name, String type) {
      super(href, id, name, type);
   }

   protected Reference() {
      // For JAXB
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      Reference that = Reference.class.cast(o);
      return super.equals(that);
   }
}