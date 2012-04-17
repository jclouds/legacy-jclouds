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
 * Represents the NAT basic rule for one to one mapping of internal
 * and external IP addresses from a network.
 * <p/>
 * <p/>
 * <p>Java class for NatOneToOneBasicRule complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="NatOneToOneBasicRule">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.vmware.com/vcloud/v1.5}VCloudExtensibleType">
 *       &lt;sequence>
 *         &lt;element name="MappingMode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ExternalIpAddress" type="{http://www.vmware.com/vcloud/v1.5}IpAddressType"/>
 *         &lt;element name="InternalIpAddress" type="{http://www.vmware.com/vcloud/v1.5}IpAddressType"/>
 *       &lt;/sequence>
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlType(name = "NatOneToOneBasicRule", propOrder = {
      "mappingMode",
      "externalIpAddress",
      "internalIpAddress"
})
public class NatOneToOneBasicRule {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromNatOneToOneBasicRule(this);
   }

   public static class Builder {

      private String mappingMode;
      private String externalIpAddress;
      private String internalIpAddress;

      /**
       * @see NatOneToOneBasicRule#getMappingMode()
       */
      public Builder mappingMode(String mappingMode) {
         this.mappingMode = mappingMode;
         return this;
      }

      /**
       * @see NatOneToOneBasicRule#getExternalIpAddress()
       */
      public Builder externalIpAddress(String externalIpAddress) {
         this.externalIpAddress = externalIpAddress;
         return this;
      }

      /**
       * @see NatOneToOneBasicRule#getInternalIpAddress()
       */
      public Builder internalIpAddress(String internalIpAddress) {
         this.internalIpAddress = internalIpAddress;
         return this;
      }

      public NatOneToOneBasicRule build() {
         return new NatOneToOneBasicRule(mappingMode, externalIpAddress, internalIpAddress);
      }

      public Builder fromNatOneToOneBasicRule(NatOneToOneBasicRule in) {
         return mappingMode(in.getMappingMode())
               .externalIpAddress(in.getExternalIpAddress())
               .internalIpAddress(in.getInternalIpAddress());
      }
   }

   private NatOneToOneBasicRule(String mappingMode, String externalIpAddress, String internalIpAddress) {
      this.mappingMode = mappingMode;
      this.externalIpAddress = externalIpAddress;
      this.internalIpAddress = internalIpAddress;
   }

   private NatOneToOneBasicRule() {
      // For JAXB
   }


   @XmlElement(name = "MappingMode", required = true)
   protected String mappingMode;
   @XmlElement(name = "ExternalIpAddress", required = true)
   protected String externalIpAddress;
   @XmlElement(name = "InternalIpAddress", required = true)
   protected String internalIpAddress;

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
    * Gets the value of the internalIpAddress property.
    *
    * @return possible object is
    *         {@link String }
    */
   public String getInternalIpAddress() {
      return internalIpAddress;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      NatOneToOneBasicRule that = NatOneToOneBasicRule.class.cast(o);
      return equal(mappingMode, that.mappingMode) &&
            equal(externalIpAddress, that.externalIpAddress) &&
            equal(internalIpAddress, that.internalIpAddress);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(mappingMode,
            externalIpAddress,
            internalIpAddress);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("")
            .add("mappingMode", mappingMode)
            .add("externalIpAddress", externalIpAddress)
            .add("internalIpAddress", internalIpAddress).toString();
   }

}
