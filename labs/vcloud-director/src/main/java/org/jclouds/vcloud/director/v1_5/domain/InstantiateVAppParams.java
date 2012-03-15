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




/**
 * Parameters for Instantiating a vApp
 *
 * @author danikov
 */
public class InstantiateVAppParams extends InstantiateVAppParamsType {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return builder().fromInstantiateVAppParams(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }
   
   public static abstract class Builder<B extends Builder<B>> extends InstantiateVAppParamsType.Builder<B> {

      @Override
      public InstantiateVAppParams build() {
         return new InstantiateVAppParams(this);
      }

      public B fromInstantiateVAppParams(InstantiateVAppParams in) {
         return fromInstantiateVAppParamsType(in);
      }
   }

   public InstantiateVAppParams(Builder<?> builder) {
      super(builder);
   }

   protected InstantiateVAppParams() {
      // for JAXB
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      InstantiateVAppParams that = InstantiateVAppParams.class.cast(o);
      return super.equals(that);
   }
}
