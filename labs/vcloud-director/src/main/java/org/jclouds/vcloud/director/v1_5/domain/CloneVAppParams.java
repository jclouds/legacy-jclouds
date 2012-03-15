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
 * Represents parameters for copying a vApp and optionally deleting the source.
 * 
 * <pre>
 * &lt;complexType name="CloneVAppParams" /&gt;
 * </pre>
 * 
 * @author grkvlt@apache.org
 */
@XmlType(name = "CloneVAppParams")
public class CloneVAppParams extends InstantiateVAppParamsType {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return builder().fromCloneVAppParams(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }
   
   public static abstract class Builder<B extends Builder<B>> extends InstantiateVAppParamsType.Builder<B> {

      @Override
      public CloneVAppParams build() {
         return new CloneVAppParams(this);
      }

      public B fromCloneVAppParams(CloneVAppParams in) {
         return fromInstantiateVAppParamsType(in);
      }
   }

   protected CloneVAppParams() {
      // For JAXB and B use
   }

   public CloneVAppParams(Builder<?> builder) {
      super(builder);
   }
}
