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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Represents vApp instantiation from OVF parameters
 *
 * <pre>
 * &lt;complexType name="InstantiateOvfParams" /&gt;
 * </pre>
 *
 * @author grkvlt@apache.org
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InstantiateOvfParams")
public class InstantiateOvfParams extends VAppCreationParamsType<InstantiateOvfParams> {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromInstantiateOvfParams(this);
   }

   public static class Builder extends VAppCreationParamsType.Builder<InstantiateOvfParams> {

      private Boolean allEULAsAccepted;
      private String transferFormat;

      /**
       * @see InstantiateOvfParams#isAllEULAsAccepted()
       */
      public Builder isAllEULAsAccepted(Boolean allEULAsAccepted) {
         this.allEULAsAccepted = allEULAsAccepted;
         return this;
      }

      /**
       * @see InstantiateOvfParams#isAllEULAsAccepted()
       */
      public Builder allEULAsAccepted() {
         this.allEULAsAccepted = Boolean.TRUE;
         return this;
      }

      /**
       * @see InstantiateOvfParams#isAllEULAsAccepted()
       */
      public Builder allEULAsNotAccepted() {
         this.allEULAsAccepted = Boolean.FALSE;
         return this;
      }

      /**
       * @see InstantiateOvfParams#getTransferFormat()
       */
      public Builder transferFormat(String transferFormat) {
         this.transferFormat = transferFormat;
         return this;
      }

      @Override
      public InstantiateOvfParams build() {
         InstantiateOvfParams instantiateOvfParams = new InstantiateOvfParams(description, name, vAppParent, instantiationParams, deploy, powerOn, allEULAsAccepted, transferFormat);
         return instantiateOvfParams;
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
      public Builder fromVAppCreationParamsType(VAppCreationParamsType<InstantiateOvfParams> in) {
         return Builder.class.cast(super.fromVAppCreationParamsType(in));
      }

      public Builder fromInstantiateOvfParams(InstantiateOvfParams in) {
         return fromVAppCreationParamsType(in).isAllEULAsAccepted(in.isAllEULAsAccepted()).transferFormat(in.getTransferFormat());
      }
   }

   protected InstantiateOvfParams() {
      // For JAXB and builder use
   }

   public InstantiateOvfParams(String description, String name, Reference vAppParent, InstantiationParams instantiationParams, Boolean deploy, Boolean powerOn, Boolean allEULAsAccepted,
                               String transferFormat) {
      super(description, name, vAppParent, instantiationParams, deploy, powerOn);
      this.allEULAsAccepted = allEULAsAccepted;
      this.transferFormat = transferFormat;
   }

   @XmlElement(name = "AllEULAsAccepted")
   protected Boolean allEULAsAccepted;
   @XmlAttribute
   protected String transferFormat;

   /**
    * Gets the value of the allEULAsAccepted property.
    */
   public Boolean isAllEULAsAccepted() {
      return allEULAsAccepted;
   }

   /**
    * Gets the value of the transferFormat property.
    */
   public String getTransferFormat() {
      return transferFormat;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      InstantiateOvfParams that = InstantiateOvfParams.class.cast(o);
      return super.equals(that) && equal(this.allEULAsAccepted, that.allEULAsAccepted) && equal(this.transferFormat, that.transferFormat);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), allEULAsAccepted, transferFormat);
   }

   @Override
   public ToStringHelper string() {
      return super.string().add("allEULAsAccepted", allEULAsAccepted).add("transferFormat", transferFormat);
   }
}
