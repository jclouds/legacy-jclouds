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

import static com.google.common.base.Objects.equal;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.jclouds.dmtf.ovf.environment.EnvironmentType;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Represents a virtual machine.
 *
 * <pre>
 * &lt;complexType name="VmType" /&gt;
 * </pre>
 *
 * @author grkvlt@apache.org
 */
@XmlRootElement(name = "Vm")
@XmlType(name = "VmType")
public class Vm extends AbstractVAppType {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   @Override
   public Builder<?> toBuilder() {
      return builder().fromVm(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }
   
   public abstract static class Builder<B extends Builder<B>> extends AbstractVAppType.Builder<B> {

      private String vAppScopedLocalId;
      private EnvironmentType environment;
      private Boolean needsCustomization;

      /**
       * @see Vm#getVAppScopedLocalId()
       */
      public B vAppScopedLocalId(String vAppScopedLocalId) {
         this.vAppScopedLocalId = vAppScopedLocalId;
         return self();
      }

      /**
       * @see Vm#getEnvironment()
       */
      public B environment(EnvironmentType environment) {
         this.environment = environment;
         return self();
      }

      /**
       * @see Vm#getNeedsCustomization()
       */
      public B isNeedsCustomization(Boolean needsCustomization) {
         this.needsCustomization = needsCustomization;
         return self();
      }

      /**
       * @see Vm#getNeedsCustomization()
       */
      public B needsCustomization() {
         this.needsCustomization = Boolean.TRUE;
         return self();
      }

      /**
       * @see Vm#getNeedsCustomization()
       */
      public B notNeedsCustomization() {
         this.needsCustomization = Boolean.FALSE;
         return self();
      }

      @Override
      public Vm build() {
         Vm vm = new Vm(this);
         return vm;
      }

      public B fromVm(Vm in) {
         return fromAbstractVAppType(in).vAppScopedLocalId(in.getVAppScopedLocalId()).environment(in.getEnvironment()).isNeedsCustomization(in.isNeedsCustomization());
      }
   }

   protected Vm() {
      // for JAXB and Builders
   }

   protected Vm(Builder<?> builder) {
      super(builder);
      this.vAppScopedLocalId = builder.vAppScopedLocalId;
      this.environment = builder.environment;
      this.needsCustomization = builder.needsCustomization;
   }

   @XmlElement(name = "VAppScopedLocalId")
   protected String vAppScopedLocalId;
   @XmlElement(name = "Environment", namespace = "http://schemas.dmtf.org/ovf/environment/1")
   protected EnvironmentType environment;
   @XmlAttribute
   protected Boolean needsCustomization;

   /**
    * Gets the value of the vAppScopedLocalId property.
    *
    * @return possible object is {@link String }
    */
   public String getVAppScopedLocalId() {
      return vAppScopedLocalId;
   }

   /**
    * OVF environment section
    *
    * @return possible object is {@link Environment }
    */
   public EnvironmentType getEnvironment() {
      return environment;
   }

   /**
    * Gets the value of the needsCustomization property.
    *
    * @return possible object is {@link Boolean }
    */
   public Boolean isNeedsCustomization() {
      return needsCustomization;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      Vm that = Vm.class.cast(o);
      return super.equals(that) &&
            equal(this.vAppScopedLocalId, that.vAppScopedLocalId) && equal(this.environment, that.environment) && equal(this.needsCustomization, that.needsCustomization);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), vAppScopedLocalId, environment, needsCustomization);
   }

   @Override
   public ToStringHelper string() {
      return super.string().add("vAppScopedLocalId", vAppScopedLocalId).add("environment", environment).add("needsCustomization", needsCustomization);
   }
}
