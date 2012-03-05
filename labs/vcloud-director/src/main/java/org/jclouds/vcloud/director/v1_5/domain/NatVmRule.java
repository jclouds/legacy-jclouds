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

package org.jclouds.vcloud.director.v1_5.domain;

import static com.google.common.base.Objects.equal;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;


/**
 * Represents the NAT rule for port forwarding between VM NIC/port
 * and external IP/port.
 * <p/>
 * <p/>
 * <p>Java class for NatVmRule complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="NatVmRule">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.vmware.com/vcloud/v1.5}VCloudExtensibleType">
 *       &lt;sequence>
 *         &lt;element name="ExternalIpAddress" type="{http://www.vmware.com/vcloud/v1.5}IpAddressType" minOccurs="0"/>
 *         &lt;element name="ExternalPort" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="VAppScopedVmId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="VmNicId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="InternalPort" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Protocol" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlType(name = "NatVmRule", propOrder = {
      "externalIpAddress",
      "externalPort",
      "vAppScopedVmId",
      "vmNicId",
      "internalPort",
      "protocol"
})
public class NatVmRule {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromNatVmRule(this);
   }

   public static class Builder {

      private String externalIpAddress;
      private int externalPort;
      private String vAppScopedVmId;
      private int vmNicId;
      private int internalPort;
      private String protocol;

      /**
       * @see NatVmRule#getExternalIpAddress()
       */
      public Builder externalIpAddress(String externalIpAddress) {
         this.externalIpAddress = externalIpAddress;
         return this;
      }

      /**
       * @see NatVmRule#getExternalPort()
       */
      public Builder externalPort(int externalPort) {
         this.externalPort = externalPort;
         return this;
      }

      /**
       * @see NatVmRule#getVAppScopedVmId()
       */
      public Builder vAppScopedVmId(String vAppScopedVmId) {
         this.vAppScopedVmId = vAppScopedVmId;
         return this;
      }

      /**
       * @see NatVmRule#getVmNicId()
       */
      public Builder vmNicId(int vmNicId) {
         this.vmNicId = vmNicId;
         return this;
      }

      /**
       * @see NatVmRule#getInternalPort()
       */
      public Builder internalPort(int internalPort) {
         this.internalPort = internalPort;
         return this;
      }

      /**
       * @see NatVmRule#getProtocol()
       */
      public Builder protocol(String protocol) {
         this.protocol = protocol;
         return this;
      }

      public NatVmRule build() {
         return new NatVmRule(externalIpAddress, externalPort, vAppScopedVmId, vmNicId, internalPort, protocol);
      }

      public Builder fromNatVmRule(NatVmRule in) {
         return externalIpAddress(in.getExternalIpAddress())
               .externalPort(in.getExternalPort())
               .vAppScopedVmId(in.getVAppScopedVmId())
               .vmNicId(in.getVmNicId())
               .internalPort(in.getInternalPort())
               .protocol(in.getProtocol());
      }
   }

   private NatVmRule(String externalIpAddress, int externalPort, String vAppScopedVmId, int vmNicId,
                     int internalPort, String protocol) {
      this.externalIpAddress = externalIpAddress;
      this.externalPort = externalPort;
      this.vAppScopedVmId = vAppScopedVmId;
      this.vmNicId = vmNicId;
      this.internalPort = internalPort;
      this.protocol = protocol;
   }

   private NatVmRule() {
      // For JAXB and builder use
   }


   @XmlElement(name = "ExternalIpAddress")
   protected String externalIpAddress;
   @XmlElement(name = "ExternalPort")
   protected int externalPort;
   @XmlElement(name = "VAppScopedVmId", required = true)
   protected String vAppScopedVmId;
   @XmlElement(name = "VmNicId")
   protected int vmNicId;
   @XmlElement(name = "InternalPort")
   protected int internalPort;
   @XmlElement(name = "Protocol", required = true)
   protected String protocol;

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
    * Gets the value of the externalPort property.
    */
   public int getExternalPort() {
      return externalPort;
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

   /**
    * Gets the value of the internalPort property.
    */
   public int getInternalPort() {
      return internalPort;
   }


   /**
    * Gets the value of the protocol property.
    *
    * @return possible object is
    *         {@link String }
    */
   public String getProtocol() {
      return protocol;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      NatVmRule that = NatVmRule.class.cast(o);
      return equal(externalIpAddress, that.externalIpAddress) &&
            equal(externalPort, that.externalPort) &&
            equal(vAppScopedVmId, that.vAppScopedVmId) &&
            equal(vmNicId, that.vmNicId) &&
            equal(internalPort, that.internalPort) &&
            equal(protocol, that.protocol);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(externalIpAddress,
            externalPort,
            vAppScopedVmId,
            vmNicId,
            internalPort,
            protocol);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("")
            .add("externalIpAddress", externalIpAddress)
            .add("externalPort", externalPort)
            .add("vAppScopedVmId", vAppScopedVmId)
            .add("vmNicId", vmNicId)
            .add("internalPort", internalPort)
            .add("protocol", protocol).toString();
   }

}
