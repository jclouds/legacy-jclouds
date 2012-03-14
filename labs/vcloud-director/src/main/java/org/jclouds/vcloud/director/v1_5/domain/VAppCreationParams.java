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
public class VAppCreationParams extends VAppCreationParamsType<VAppCreationParams> {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromVAppCreationParams(this);
   }

   public static class Builder extends VAppCreationParamsType.Builder<VAppCreationParams> {

      @Override
      public VAppCreationParams build() {
         VAppCreationParams vAppCreationParams = new VAppCreationParams(description, name, vAppParent, instantiationParams, deploy, powerOn);
         return vAppCreationParams;
      }

      /**
       * @see VAppCreationParamsType#getVAppParent()
       */
      @Override
      public Builder vAppParent(Reference vAppParent) {
         this.vAppParent = vAppParent;
         return this;
      }

      /**
       * @see VAppCreationParamsType#getInstantiationParams()
       */
      @Override
      public Builder instantiationParams(InstantiationParams instantiationParams) {
         this.instantiationParams = instantiationParams;
         return this;
      }

      /**
       * @see VAppCreationParamsType#isDeploy()
       */
      @Override
      public Builder deploy(Boolean deploy) {
         this.deploy = deploy;
         return this;
      }

      /**
       * @see VAppCreationParamsType#isDeploy()
       */
      @Override
      public Builder deploy() {
         this.deploy = Boolean.TRUE;
         return this;
      }

      /**
       * @see VAppCreationParamsType#isDeploy()
       */
      @Override
      public Builder notDeploy() {
         this.deploy = Boolean.FALSE;
         return this;
      }

      /**
       * @see VAppCreationParamsType#isPowerOn()
       */
      @Override
      public Builder powerOn(Boolean powerOn) {
         this.powerOn = powerOn;
         return this;
      }

      /**
       * @see VAppCreationParamsType#isPowerOn()
       */
      @Override
      public Builder powerOn() {
         this.powerOn = Boolean.TRUE;
         return this;
      }

      /**
       * @see VAppCreationParamsType#isPowerOn()
       */
      @Override
      public Builder notPowerOn() {
         this.powerOn = Boolean.FALSE;
         return this;
      }

      /**
       * @see ParamsType#getDescription()
       */
      @Override
      public Builder description(String description) {
         this.description = description;
         return this;
      }

      /**
       * @see ParamsType#getName()
       */
      @Override
      public Builder name(String name) {
         this.name = name;
         return this;
      }

      @Override
      public Builder fromVAppCreationParamsType(VAppCreationParamsType<VAppCreationParams> in) {
         return Builder.class.cast(super.fromVAppCreationParamsType(in));
      }

      public Builder fromVAppCreationParams(VAppCreationParams in) {
         return fromVAppCreationParamsType(in);
      }
   }

   protected VAppCreationParams() {
      // For JAXB and builder use
   }

   public VAppCreationParams(String description, String name, Reference vAppParent, InstantiationParams instantiationParams, Boolean deploy, Boolean powerOn) {
      super(description, name, vAppParent, instantiationParams, deploy, powerOn);
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
