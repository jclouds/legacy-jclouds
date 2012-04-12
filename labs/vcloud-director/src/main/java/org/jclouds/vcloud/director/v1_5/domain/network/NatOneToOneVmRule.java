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

package org.jclouds.vcloud.director.v1_5.domain.network;

import static com.google.common.base.Objects.equal;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;


/**
 * Represents the NAT rule for one to one mapping of VM NIC and
 * external IP addresses from a network.
 * <p/>
 * <p/>
 * <p>Java class for NatOneToOneVmRule complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="NatOneToOneVmRule">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.vmware.com/vcloud/v1.5}VCloudExtensibleType">
 *       &lt;sequence>
 *         &lt;element name="MappingMode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ExternalIpAddress" type="{http://www.vmware.com/vcloud/v1.5}IpAddressType" minOccurs="0"/>
 *         &lt;element name="VAppScopedVmId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="VmNicId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlType(name = "NatOneToOneVmRule", propOrder = {
      "mappingMode",
      "externalIpAddress",
      "vAppScopedVmId",
      "vmNicId"
})
public class NatOneToOneVmRule {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromNatOneToOneVmRule(this);
   }

   public static class Builder {

      private String mappingMode;
      private String externalIpAddress;
      private String vAppScopedVmId;
      private int vmNicId;

      /**
       * @see NatOneToOneVmRule#getMappingMode()
       */
      public Builder mappingMode(String mappingMode) {
         this.mappingMode = mappingMode;
         return this;
      }

      /**
       * @see NatOneToOneVmRule#getExternalIpAddress()
       */
      public Builder externalIpAddress(String externalIpAddress) {
         this.externalIpAddress = externalIpAddress;
         return this;
      }

      /**
       * @see NatOneToOneVmRule#getVAppScopedVmId()
       */
      public Builder vAppScopedVmId(String vAppScopedVmId) {
         this.vAppScopedVmId = vAppScopedVmId;
         return this;
      }

      /**
       * @see NatOneToOneVmRule#getVmNicId()
       */
      public Builder vmNicId(int vmNicId) {
         this.vmNicId = vmNicId;
         return this;
      }

      public NatOneToOneVmRule build() {
         return new NatOneToOneVmRule(mappingMode, externalIpAddress, vAppScopedVmId, vmNicId);
      }

      public Builder fromNatOneToOneVmRule(NatOneToOneVmRule in) {
         return mappingMode(in.getMappingMode())
               .externalIpAddress(in.getExternalIpAddress())
               .vAppScopedVmId(in.getVAppScopedVmId())
               .vmNicId(in.getVmNicId());
      }
   }

   private NatOneToOneVmRule(String mappingMode, String externalIpAddress, String vAppScopedVmId, int vmNicId) {
      this.mappingMode = mappingMode;
      this.externalIpAddress = externalIpAddress;
      this.vAppScopedVmId = vAppScopedVmId;
      this.vmNicId = vmNicId;
   }

   private NatOneToOneVmRule() {
      // For JAXB
   }

   @XmlElement(name = "MappingMode", required = true)
   protected String mappingMode;
   @XmlElement(name = "ExternalIpAddress")
   protected String externalIpAddress;
   @XmlElement(name = "VAppScopedVmId", required = true)
   protected String vAppScopedVmId;
   @XmlElement(name = "VmNicId")
   protected int vmNicId;

   /**
    * Gets the value of the mappingMode property.
    *
    * @return possible object is
    *         {@link String }
    */
   public String getMappingMode() {
      return mappingMode;
   }

   /**
    * Gets the value of the externalIpAddress property.
    *
    * @return possible object is
    *         {@link String }
    */
   public String getExternalIpAddress() {
      return externalIpAddress;
   }

   /**
    * Gets the value of the vAppScopedVmId property.
    *
    * @return possible object is
    *         {@link String }
    */
   public String getVAppScopedVmId() {
      return vAppScopedVmId;
   }

   /**
    * Gets the value of the vmNicId property.
    */
   public int getVmNicId() {
      return vmNicId;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      NatOneToOneVmRule that = NatOneToOneVmRule.class.cast(o);
      return equal(mappingMode, that.mappingMode) &&
            equal(externalIpAddress, that.externalIpAddress) &&
            equal(vAppScopedVmId, that.vAppScopedVmId) &&
            equal(vmNicId, that.vmNicId);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(mappingMode,
            externalIpAddress,
            vAppScopedVmId,
            vmNicId);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("")
            .add("mappingMode", mappingMode)
            .add("externalIpAddress", externalIpAddress)
            .add("vAppScopedVmId", vAppScopedVmId)
            .add("vmNicId", vmNicId).toString();
   }

}
