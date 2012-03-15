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

import javax.xml.bind.annotation.XmlType;


/**
 * Represents vApp creation parameters.
 *
 * <pre>
 * &lt;complexType name="VAppCreationParams" /&gt;
 * </pre>
 *
 * @author grkvlt@apache.org
 */
@XmlType(name = "VAppCreationParams")
public class VAppCreationParams extends VAppCreationParamsType {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return builder().fromVAppCreationParams(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }
   
   public static abstract class Builder<B extends Builder<B>> extends VAppCreationParamsType.Builder<B> {

      @Override
      public VAppCreationParams build() {
         VAppCreationParams vAppCreationParams = new VAppCreationParams(this);
         return vAppCreationParams;
      }

      public B fromVAppCreationParams(VAppCreationParams in) {
         return fromVAppCreationParamsType(in);
      }
   }

   protected VAppCreationParams() {
      // For JAXB and builder use
   }

   protected VAppCreationParams(Builder<?> builder) {
      super(builder);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      VAppCreationParams that = VAppCreationParams.class.cast(o);
      return super.equals(that);
   }
}
