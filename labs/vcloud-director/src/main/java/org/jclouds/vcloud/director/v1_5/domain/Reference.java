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
public class Reference extends ReferenceType {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return builder().fromReference(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }
   
   public static class Builder<B extends Builder<B>> extends ReferenceType.Builder<B> {

      @Override
      public Reference build() {
         return new Reference(this);
      }

      public B fromReference(Reference in) {
         return fromReferenceType(in);
      }
      
      public B fromEntity(EntityType in) {
         return href(in.getHref()).id(in.getId()).name(in.getName()).type(in.getType());
      }
   }

   protected Reference(Builder<?> builder) {
      super(builder);
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

   public ReferenceType toAdminReference(String endpoint) {
      return toBuilder()
        .type(null)
        .href(URI.create(getHref().toASCIIString().replace(endpoint, endpoint+"/admin")))
        .build();
   }
}
