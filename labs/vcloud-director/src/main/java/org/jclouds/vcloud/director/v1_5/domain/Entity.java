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
 * An entity.
 *
 * @author grkvlt@apache.org
 * @author Adam Lowe
 */
@XmlRootElement(name = "Entity")
public class Entity extends EntityType {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   @Override
   public Builder<?> toBuilder() {
      return builder().fromEntity(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }
   
   public static abstract class Builder<B extends Builder<B>> extends EntityType.Builder<B> {

      @Override
      public Entity build() {
         return new Entity(this);
      }

      public B fromEntity(Entity in) {
         return fromEntityType(in);
      }
   }

   private Entity(Builder<?> builder) {
      super(builder);
   }

   private Entity() {
      // For JAXB
   }
}