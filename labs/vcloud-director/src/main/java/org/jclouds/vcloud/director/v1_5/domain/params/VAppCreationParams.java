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
package org.jclouds.vcloud.director.v1_5.domain.params;

import static com.google.common.base.Objects.equal;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.jclouds.vcloud.director.v1_5.domain.Reference;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Represents vApp creation parameters.
 *
 * @author grkvlt@apache.org
 * @see <a href="http://www.vmware.com/support/vcd/doc/rest-api-doc-1.5-html/types/VAppCreationParamsType.html">
 *    vCloud REST API - VAppCreationParamsType</a>
 * @since 0.9
 */
@XmlRootElement(name = "VAppCreationParams")
@XmlType(name = "VAppCreationParamsType")
public class VAppCreationParams extends ParamsType {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   @Override
   public Builder<?> toBuilder() {
      return builder().fromVAppCreationParamsType(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }
   
   public abstract static class Builder<B extends Builder<B>> extends ParamsType.Builder<B> {

      private Reference vAppParent;
      private InstantiationParams instantiationParams;
      private Boolean deploy;
      private Boolean powerOn;

      /**
       * @see VAppCreationParamsType#getVAppParent()
       */
      public B vAppParent(Reference vAppParent) {
         this.vAppParent = vAppParent;
         return self();
      }

      /**
       * @see VAppCreationParamsType#getInstantiationParams()
       */
      public B instantiationParams(InstantiationParams instantiationParams) {
         this.instantiationParams = instantiationParams;
         return self();
      }

      /**
       * @see VAppCreationParamsType#isDeploy()
       */
      public B deploy(Boolean deploy) {
         this.deploy = deploy;
         return self();
      }

      /**
       * @see VAppCreationParamsType#isDeploy()
       */
      public B deploy() {
         this.deploy = Boolean.TRUE;
         return self();
      }

      /**
       * @see VAppCreationParamsType#isDeploy()
       */
      public B notDeploy() {
         this.deploy = Boolean.FALSE;
         return self();
      }

      /**
       * @see VAppCreationParamsType#isPowerOn()
       */
      public B powerOn(Boolean powerOn) {
         this.powerOn = powerOn;
         return self();
      }

      /**
       * @see VAppCreationParamsType#isPowerOn()
       */
      public B powerOn() {
         this.powerOn = Boolean.TRUE;
         return self();
      }

      /**
       * @see VAppCreationParamsType#isPowerOn()
       */
      public B notPowerOn() {
         this.powerOn = Boolean.FALSE;
         return self();
      }
      
      public B fromVAppCreationParamsType(VAppCreationParams in) {
         return fromParamsType(in).vAppParent(in.getVAppParent()).instantiationParams(in.getInstantiationParams()).deploy(in.isDeploy()).powerOn(in.isPowerOn());
      }
   }

   protected VAppCreationParams() {
      // For JAXB and B use
   }

   public VAppCreationParams(Builder<?> builder) {
      super(builder);
      this.vAppParent = builder.vAppParent;
      this.instantiationParams = builder.instantiationParams;
      this.deploy = builder.deploy;
      this.powerOn = builder.powerOn;
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
    * Reserved.
    *
    * Unimplemented.
    */
   public Reference getVAppParent() {
      return vAppParent;
   }

   /**
    * Instantiation parameters of a VApp.
    */
   public InstantiationParams getInstantiationParams() {
      return instantiationParams;
   }

   /**
    * Flag to deploy the VApp after successful creation.
    */
   public Boolean isDeploy() {
      return deploy;
   }

   /**
    * Flag to deploy and power on the VApp after successful creation.
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
      VAppCreationParams that = VAppCreationParams.class.cast(o);
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
