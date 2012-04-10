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

import javax.xml.bind.annotation.XmlRootElement;

/**
 * A resource.
 *
 * @author grkvlt@apache.org
 */
@XmlRootElement(name = "Resource")
public class Resource extends ResourceType {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   @Override
   public Builder<?> toBuilder() {
      return builder().fromResource(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }
   
   public static abstract class Builder<B extends Builder<B>> extends ResourceType.Builder<B> {

      @Override
      public Resource build() {
         return new Resource(this);
      }

      protected B fromResource(Resource in) {
         return fromResourceType(in);
      }
   }

   protected Resource(Builder<?> builder) {
      super(builder);
   }

   protected Resource() {
      // For JAXB
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      Resource that = Resource.class.cast(o);
      return super.equals(that);
   }
}