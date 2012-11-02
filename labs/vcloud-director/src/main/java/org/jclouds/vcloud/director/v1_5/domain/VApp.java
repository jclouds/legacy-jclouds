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

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Represents a vApp.
 *
 * <pre>
 * &lt;complexType name="VApp" /&gt;
 * </pre>
 *
 * @author grkvlt@apache.org
 */
@XmlRootElement(name = "VApp")
@XmlType(name = "VAppType")
public class VApp extends AbstractVAppType {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   @Override
   public Builder<?> toBuilder() {
      return builder().fromVApp(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }
   
   public abstract static class Builder<B extends Builder<B>> extends AbstractVAppType.Builder<B> {

      private Owner owner;
      private Boolean inMaintenanceMode;
      private VAppChildren children;
      private Boolean ovfDescriptorUploaded;

      /**
       * @see VApp#getOwner()
       */
      public B owner(Owner owner) {
         this.owner = owner;
         return self();
      }

      /**
       * @see VApp#isInMaintenanceMode()
       */
      public B isInMaintenanceMode(Boolean inMaintenanceMode) {
         this.inMaintenanceMode = inMaintenanceMode;
         return self();
      }

      /**
       * @see VApp#isInMaintenanceMode()
       */
      public B inMaintenanceMode() {
         this.inMaintenanceMode = Boolean.TRUE;
         return self();
      }

      /**
       * @see VApp#isInMaintenanceMode()
       */
      public B notInMaintenanceMode() {
         this.inMaintenanceMode = Boolean.FALSE;
         return self();
      }

      /**
       * @see VApp#getChildren()
       */
      public B children(VAppChildren children) {
         this.children = children;
         return self();
      }

      /**
       * @see VApp#isOvfDescriptorUploaded()
       */
      public B isOvfDescriptorUploaded(Boolean ovfDescriptorUploaded) {
         this.ovfDescriptorUploaded = ovfDescriptorUploaded;
         return self();
      }

      /**
       * @see VApp#isOvfDescriptorUploaded()
       */
      public B ovfDescriptorUploaded() {
         this.ovfDescriptorUploaded = Boolean.TRUE;
         return self();
      }

      /**
       * @see VApp#isOvfDescriptorUploaded()
       */
      public B ovfDescriptorNotUploaded() {
         this.ovfDescriptorUploaded = Boolean.FALSE;
         return self();
      }

      @Override
      public VApp build() {
         return new VApp(this);
      }

      public B fromVApp(VApp in) {
         return fromAbstractVAppType(in)
               .owner(in.getOwner()).isInMaintenanceMode(in.isInMaintenanceMode())
               .children(in.getChildren()).isOvfDescriptorUploaded(in.isOvfDescriptorUploaded());
      }
   }

   protected VApp() {
      // For JAXB and builder use
   }

   protected VApp(Builder<?> builder) {
      super(builder);
      this.owner = builder.owner;
      this.inMaintenanceMode = builder.inMaintenanceMode;
      this.children = builder.children;
      this.ovfDescriptorUploaded = builder.ovfDescriptorUploaded;
   }

   @XmlElement(name = "Owner")
   private Owner owner;
   @XmlElement(name = "InMaintenanceMode")
   private Boolean inMaintenanceMode;
   @XmlElement(name = "Children")
   private VAppChildren children;
   @XmlAttribute
   private Boolean ovfDescriptorUploaded;

   /**
    * Gets the value of the owner property.
    */
   public Owner getOwner() {
      return owner;
   }

   /**
    * Gets the value of the inMaintenanceMode property.
    */
   public Boolean isInMaintenanceMode() {
      return inMaintenanceMode;
   }

   /**
    * Gets the value of the children property.
    */
   public VAppChildren getChildren() {
      return children;
   }

   /**
    * Gets the value of the ovfDescriptorUploaded property.
    */
   public Boolean isOvfDescriptorUploaded() {
      return ovfDescriptorUploaded;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      VApp that = VApp.class.cast(o);
      return super.equals(that) &&
            equal(this.owner, that.owner) && equal(this.inMaintenanceMode, that.inMaintenanceMode) &&
            equal(this.children, that.children) && equal(this.ovfDescriptorUploaded, that.ovfDescriptorUploaded);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), owner, inMaintenanceMode, children, ovfDescriptorUploaded);
   }

   @Override
   public ToStringHelper string() {
      return super.string().add("owner", owner).add("inMaintenanceMode", inMaintenanceMode)
            .add("children", children).add("ovfDescriptorUploaded", ovfDescriptorUploaded);
   }
}
