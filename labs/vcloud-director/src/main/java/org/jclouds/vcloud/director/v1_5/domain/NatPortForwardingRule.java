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
 * Represents the NAT rule for port forwarding between internal
 * IP/port and external IP/port.
 * <p/>
 * <p/>
 * <p>Java class for NatPortForwardingRule complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="NatPortForwardingRule">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.vmware.com/vcloud/v1.5}VCloudExtensibleType">
 *       &lt;sequence>
 *         &lt;element name="ExternalIpAddress" type="{http://www.vmware.com/vcloud/v1.5}IpAddressType"/>
 *         &lt;element name="ExternalPort" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="InternalIpAddress" type="{http://www.vmware.com/vcloud/v1.5}IpAddressType"/>
 *         &lt;element name="InternalPort" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Protocol" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlType(name = "NatPortForwardingRule", propOrder = {
      "externalIpAddress",
      "externalPort",
      "internalIpAddress",
      "internalPort",
      "protocol"
})
public class NatPortForwardingRule {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromNatPortForwardingRule(this);
   }

   public static class Builder {

      private String externalIpAddress;
      private int externalPort;
      private String internalIpAddress;
      private int internalPort;
      private String protocol;

      /**
       * @see NatPortForwardingRule#getExternalIpAddress()
       */
      public Builder externalIpAddress(String externalIpAddress) {
         this.externalIpAddress = externalIpAddress;
         return this;
      }

      /**
       * @see NatPortForwardingRule#getExternalPort()
       */
      public Builder externalPort(int externalPort) {
         this.externalPort = externalPort;
         return this;
      }

      /**
       * @see NatPortForwardingRule#getInternalIpAddress()
       */
      public Builder internalIpAddress(String internalIpAddress) {
         this.internalIpAddress = internalIpAddress;
         return this;
      }

      /**
       * @see NatPortForwardingRule#getInternalPort()
       */
      public Builder internalPort(int internalPort) {
         this.internalPort = internalPort;
         return this;
      }

      /**
       * @see NatPortForwardingRule#getProtocol()
       */
      public Builder protocol(String protocol) {
         this.protocol = protocol;
         return this;
      }

      public NatPortForwardingRule build() {
         return new NatPortForwardingRule(externalIpAddress, externalPort, internalIpAddress, internalPort, protocol);
      }

      public Builder fromNatPortForwardingRule(NatPortForwardingRule in) {
         return externalIpAddress(in.getExternalIpAddress())
               .externalPort(in.getExternalPort())
               .internalIpAddress(in.getInternalIpAddress())
               .internalPort(in.getInternalPort())
               .protocol(in.getProtocol());
      }
   }

   private NatPortForwardingRule(String externalIpAddress, int externalPort, String internalIpAddress, int internalPort, String protocol) {
      this.externalIpAddress = externalIpAddress;
      this.externalPort = externalPort;
      this.internalIpAddress = internalIpAddress;
      this.internalPort = internalPort;
      this.protocol = protocol;
   }

   private NatPortForwardingRule() {
      // For JAXB
   }

   @XmlElement(name = "ExternalIpAddress", required = true)
   protected String externalIpAddress;
   @XmlElement(name = "ExternalPort")
   protected int externalPort;
   @XmlElement(name = "InternalIpAddress", required = true)
   protected String internalIpAddress;
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
    * Gets the value of the internalIpAddress property.
    *
    * @return possible object is
    *         {@link String }
    */
   public String getInternalIpAddress() {
      return internalIpAddress;
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
      NatPortForwardingRule that = NatPortForwardingRule.class.cast(o);
      return equal(externalIpAddress, that.externalIpAddress) &&
            equal(externalPort, that.externalPort) &&
            equal(internalIpAddress, that.internalIpAddress) &&
            equal(internalPort, that.internalPort) &&
            equal(protocol, that.protocol);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(externalIpAddress,
            externalPort,
            internalIpAddress,
            internalPort,
            protocol);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("")
            .add("externalIpAddress", externalIpAddress)
            .add("externalPort", externalPort)
            .add("internalIpAddress", internalIpAddress)
            .add("internalPort", internalPort)
            .add("protocol", protocol).toString();
   }

}
