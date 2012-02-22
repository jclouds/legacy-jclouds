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
package org.jclouds.vcloud.director.v1_5.domain.vapp;

/**
 * Represents a vApp.
 * 
 * <pre>
 * &lt;complexType name="VApp" /&gt;
 * </pre>
 * 
 * @author grkvlt@apache.org
 */
public class VApp extends AbstractVAppType {
//   
//   @SuppressWarnings("unchecked")
//   public static Builder builder() {
//      return new Builder();
//   }
//
//   public Builder toBuilder() {
//      return new Builder().fromVApp(this);
//   }
//
//   public static class Builder extends AbstractVAppType.Builder<VApp> {
//      
//      private Owner owner;
//      private Boolean inMaintenanceMode;
//      private VAppChildren children;
//      private Boolean ovfDescriptorUploaded;
//
//      /**
//       * @see VApp#getOwner()
//       */
//      public Builder owner(Owner owner) {
//         this.owner = owner;
//         return this;
//      }
//
//      /**
//       * @see VApp#getInMaintenanceMode()
//       */
//      public Builder inMaintenanceMode(Boolean inMaintenanceMode) {
//         this.inMaintenanceMode = inMaintenanceMode;
//         return this;
//      }
//
//      /**
//       * @see VApp#getChildren()
//       */
//      public Builder children(VAppChildren children) {
//         this.children = children;
//         return this;
//      }
//
//      /**
//       * @see VApp#getOvfDescriptorUploaded()
//       */
//      public Builder ovfDescriptorUploaded(Boolean ovfDescriptorUploaded) {
//         this.ovfDescriptorUploaded = ovfDescriptorUploaded;
//         return this;
//      }
//
//
//      public VApp build() {
//         VApp vApp = new VApp();
//         vApp.setOwner(owner);
//         vApp.setInMaintenanceMode(inMaintenanceMode);
//         vApp.setChildren(children);
//         vApp.setOvfDescriptorUploaded(ovfDescriptorUploaded);
//         return vApp;
//      }
//
//
//      @Override
//      public Builder fromAbstractVAppType(AbstractVAppType<VApp> in) {
//          return Builder.class.cast(super.fromAbstractVAppType(in));
//      }
//      public Builder fromVApp(VApp in) {
//         return fromAbstractVAppType(in)
//            .owner(in.getOwner())
//            .inMaintenanceMode(in.getInMaintenanceMode())
//            .children(in.getChildren())
//            .ovfDescriptorUploaded(in.getOvfDescriptorUploaded());
//      }
//   }
//
//   private VApp() {
//      // For JAXB and builder use
//   }
//
//
//
//    @XmlElement(name = "Owner")
//    protected Owner owner;
//    @XmlElement(name = "InMaintenanceMode")
//    protected Boolean inMaintenanceMode;
//    @XmlElement(name = "Children")
//    protected VAppChildren children;
//    @XmlAttribute
//    protected Boolean ovfDescriptorUploaded;
//
//    /**
//     * Gets the value of the owner property.
//     * 
//     * @return
//     *     possible object is
//     *     {@link Owner }
//     *     
//     */
//    public Owner getOwner() {
//        return owner;
//    }
//
//    /**
//     * Sets the value of the owner property.
//     * 
//     * @param value
//     *     allowed object is
//     *     {@link Owner }
//     *     
//     */
//    public void setOwner(Owner value) {
//        this.owner = value;
//    }
//
//    /**
//     * Gets the value of the inMaintenanceMode property.
//     * 
//     * @return
//     *     possible object is
//     *     {@link Boolean }
//     *     
//     */
//    public Boolean isInMaintenanceMode() {
//        return inMaintenanceMode;
//    }
//
//    /**
//     * Sets the value of the inMaintenanceMode property.
//     * 
//     * @param value
//     *     allowed object is
//     *     {@link Boolean }
//     *     
//     */
//    public void setInMaintenanceMode(Boolean value) {
//        this.inMaintenanceMode = value;
//    }
//
//    /**
//     * Gets the value of the children property.
//     * 
//     * @return
//     *     possible object is
//     *     {@link VAppChildren }
//     *     
//     */
//    public VAppChildren getChildren() {
//        return children;
//    }
//
//    /**
//     * Sets the value of the children property.
//     * 
//     * @param value
//     *     allowed object is
//     *     {@link VAppChildren }
//     *     
//     */
//    public void setChildren(VAppChildren value) {
//        this.children = value;
//    }
//
//    /**
//     * Gets the value of the ovfDescriptorUploaded property.
//     * 
//     * @return
//     *     possible object is
//     *     {@link Boolean }
//     *     
//     */
//    public Boolean isOvfDescriptorUploaded() {
//        return ovfDescriptorUploaded;
//    }
//
//    /**
//     * Sets the value of the ovfDescriptorUploaded property.
//     * 
//     * @param value
//     *     allowed object is
//     *     {@link Boolean }
//     *     
//     */
//    public void setOvfDescriptorUploaded(Boolean value) {
//        this.ovfDescriptorUploaded = value;
//    }
//
//   @Override
//   public boolean equals(Object o) {
//      if (this == o)
//          return true;
//      if (o == null || getClass() != o.getClass())
//         return false;
//      VApp that = VApp.class.cast(o);
//      return equal(owner, that.owner) && 
//           equal(inMaintenanceMode, that.inMaintenanceMode) && 
//           equal(children, that.children) && 
//           equal(ovfDescriptorUploaded, that.ovfDescriptorUploaded);
//   }
//
//   @Override
//   public int hashCode() {
//      return Objects.hashCode(owner, 
//           inMaintenanceMode, 
//           children, 
//           ovfDescriptorUploaded);
//   }
//
//   @Override
//   public String toString() {
//      return Objects.toStringHelper("")
//            .add("owner", owner)
//            .add("inMaintenanceMode", inMaintenanceMode)
//            .add("children", children)
//            .add("ovfDescriptorUploaded", ovfDescriptorUploaded).toString();
//   }

}
