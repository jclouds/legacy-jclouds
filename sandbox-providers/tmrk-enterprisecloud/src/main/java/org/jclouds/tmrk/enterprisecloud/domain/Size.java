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
package org.jclouds.tmrk.enterprisecloud.domain;

import org.jclouds.tmrk.enterprisecloud.domain.internal.ResourceCapacity;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Jason King
 */
@XmlRootElement(name = "Size")
public class Size extends ResourceCapacity<Size> {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Builder toBuilder() {
      return new Builder().fromSize(this);
   }

   public static class Builder extends ResourceCapacity.Builder<Size> {

      @Override
      public Size build() {
         return new Size(value,unit);
      }

      public Builder fromSize(Size in) {
         return fromResource(in);
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromResource(ResourceCapacity<Size> in) {
         return Builder.class.cast(super.fromResource(in));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder value(double value) {
         return Builder.class.cast(super.value(value));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder unit(String unit) {
         return Builder.class.cast(super.unit(unit));
      }
   }

   public Size(double value, String unit) {
      super(value, unit);
   }

   protected Size() {
       //For JAXB
   }
}