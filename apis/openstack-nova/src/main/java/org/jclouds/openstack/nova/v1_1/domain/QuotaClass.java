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
package org.jclouds.openstack.nova.v1_1.domain;

/**
 * Represents the set of limits (quota class) returned by the Quota Class Extension
 *
 * @see org.jclouds.openstack.nova.v1_1.extensions.QuotaClassClient
 */
public class QuotaClass extends Quotas {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromQuotas(this);
   }

   public static abstract class Builder<T extends Builder<T>> extends Quotas.Builder<T> {
      /**
       * @see QuotaClass#getId()
       */
      @Override
      public T id(String id) {
         return super.id(id);
      }
      public QuotaClass build() {
         return new QuotaClass(this);
      }

   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   protected QuotaClass(Builder<?> builder) {
      super(builder);
   }

   /**
    * The id of this Quota Class.
    */
   @Override
   public String getId() {
      return super.getId();
   }
}