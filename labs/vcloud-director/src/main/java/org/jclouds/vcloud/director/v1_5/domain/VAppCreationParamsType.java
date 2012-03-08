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

import static com.google.common.base.Objects.*;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;


import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Represents vApp creation parameters.
 *
 * <pre>
 * &lt;complexType name="VAppCreationParams" /&gt;
 * </pre>
 *
 * @author grkvlt@apache.org
 */
public class VAppCreationParamsType<T extends VAppCreationParamsType<T>> extends ParamsType<T> {

   public static class Builder<T extends VAppCreationParamsType<T>> extends ParamsType.Builder<T> {

      protected Reference vAppParent;
      protected InstantiationParams instantiationParams;
      protected Boolean deploy;
      protected Boolean powerOn;

      /**
       * @see VAppCreationParamsType#getVAppParent()
       */
      public Builder<T> vAppParent(Reference vAppParent) {
         this.vAppParent = vAppParent;
         return this;
      }

      /**
       * @see VAppCreationParamsType#getInstantiationParams()
       */
      public Builder<T> instantiationParams(InstantiationParams instantiationParams) {
         this.instantiationParams = instantiationParams;
         return this;
      }

      /**
       * @see VAppCreationParamsType#isDeploy()
       */
      public Builder<T> deploy(Boolean deploy) {
         this.deploy = deploy;
         return this;
      }

      /**
       * @see VAppCreationParamsType#isDeploy()
       */
      public Builder<T> deploy() {
         this.deploy = Boolean.TRUE;
         return this;
      }

      /**
       * @see VAppCreationParamsType#isDeploy()
       */
      public Builder<T> notDeploy() {
         this.deploy = Boolean.FALSE;
         return this;
      }

      /**
       * @see VAppCreationParamsType#isPowerOn()
       */
      public Builder<T> powerOn(Boolean powerOn) {
         this.powerOn = powerOn;
         return this;
      }

      /**
       * @see VAppCreationParamsType#isPowerOn()
       */
      public Builder<T> powerOn() {
         this.powerOn = Boolean.TRUE;
         return this;
      }

      /**
       * @see VAppCreationParamsType#isPowerOn()
       */
      public Builder<T> notPowerOn() {
         this.powerOn = Boolean.FALSE;
         return this;
      }

      @Override
      public VAppCreationParamsType<T> build() {
         VAppCreationParamsType<T> vAppCreationParams = new VAppCreationParamsType<T>(description, name, vAppParent, instantiationParams, deploy, powerOn);
         return vAppCreationParams;
      }

      /**
       * @see ParamsType#getDescription()
       */
      @Override
      public Builder<T> description(String description) {
         this.description = description;
         return this;
      }

      /**
       * @see ParamsType#getName()
       */
      @Override
      public Builder<T> name(String name) {
         this.name = name;
         return this;
      }

      @Override
      public Builder<T> fromParamsType(ParamsType<T> in) {
         return Builder.class.cast(super.fromParamsType(in));
      }

      public Builder<T> fromVAppCreationParamsType(VAppCreationParamsType<T> in) {
         return fromParamsType(in).vAppParent(in.getVAppParent()).instantiationParams(in.getInstantiationParams()).deploy(in.isDeploy()).powerOn(in.isPowerOn());
      }
   }

   protected VAppCreationParamsType() {
      // For JAXB and builder use
   }

   public VAppCreationParamsType(String description, String name, Reference vAppParent, InstantiationParams instantiationParams, Boolean deploy, Boolean powerOn) {
      super(description, name);
      this.vAppParent = vAppParent;
      this.instantiationParams = instantiationParams;
      this.deploy = deploy;
      this.powerOn = powerOn;
   }

   @XmlElement(name = "VAppParent")
   protected Reference vAppParent;
   @XmlElement(name = "InstantiationParams")
   protected InstantiationParams instantiationParams;
   @XmlAttribute
   protected Boolean deploy;
   @XmlAttribute
   protected Boolean powerOn;

   /**
    * Gets the value of the vAppParent property.
    */
   public Reference getVAppParent() {
      return vAppParent;
   }

   /**
    * Gets the value of the instantiationParams property.
    */
   public InstantiationParams getInstantiationParams() {
      return instantiationParams;
   }

   /**
    * Gets the value of the deploy property.
    */
   public Boolean isDeploy() {
      return deploy;
   }

   /**
    * Gets the value of the powerOn property.
    */
   public Boolean isPowerOn() {
      return powerOn;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      VAppCreationParamsType<?> that = VAppCreationParamsType.class.cast(o);
      return super.equals(that) &&
            equal(vAppParent, that.vAppParent) &&
            equal(instantiationParams, that.instantiationParams) &&
            equal(deploy, that.deploy) &&
            equal(powerOn, that.powerOn);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), vAppParent, instantiationParams, deploy, powerOn);
   }

   @Override
   public ToStringHelper string() {
      return Objects.toStringHelper("")
            .add("vAppParent", vAppParent)
            .add("instantiationParams", instantiationParams)
            .add("deploy", deploy)
            .add("powerOn", powerOn);
   }
}
