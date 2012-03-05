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
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;


/**
 * Represents details of an IPSec-VPN tunnel.
 * <p/>
 * <p/>
 * <p>Java class for IpsecVpnTunnel complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="IpsecVpnTunnel">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.vmware.com/vcloud/v1.5}VCloudExtensibleType">
 *       &lt;sequence>
 *         &lt;element name="Name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element ref="{http://www.vmware.com/vcloud/v1.5}IpsecVpnPeer"/>
 *         &lt;element name="PeerIpAddress" type="{http://www.vmware.com/vcloud/v1.5}IpAddressType"/>
 *         &lt;element name="PeerNetworkAddress" type="{http://www.vmware.com/vcloud/v1.5}IpAddressType"/>
 *         &lt;element name="PeerNetworkMask" type="{http://www.vmware.com/vcloud/v1.5}IpAddressType"/>
 *         &lt;element name="SharedSecret" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="EncryptionProtocol" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Mtu" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="IsEnabled" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="IsOperational" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="ErrorDetails" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlType(name = "IpsecVpnTunnel", propOrder = {
      "name",
      "description",
      "ipsecVpnPeer",
      "peerIpAddress",
      "peerNetworkAddress",
      "peerNetworkMask",
      "sharedSecret",
      "encryptionProtocol",
      "mtu",
      "isEnabled",
      "isOperational",
      "errorDetails"
})
public class IpsecVpnTunnel


{
   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromIpsecVpnTunnel(this);
   }

   public static class Builder {

      private String name;
      private String description;
      private IpsecVpnPeerType ipsecVpnPeer;
      private String peerIpAddress;
      private String peerNetworkAddress;
      private String peerNetworkMask;
      private String sharedSecret;
      private String encryptionProtocol;
      private int mtu;
      private boolean isEnabled;
      private Boolean isOperational;
      private String errorDetails;

      /**
       * @see IpsecVpnTunnel#getName()
       */
      public Builder name(String name) {
         this.name = name;
         return this;
      }

      /**
       * @see IpsecVpnTunnel#getDescription()
       */
      public Builder description(String description) {
         this.description = description;
         return this;
      }

      /**
       * @see IpsecVpnTunnel#getIpsecVpnPeer()
       */
      public Builder ipsecVpnPeer(IpsecVpnPeerType ipsecVpnPeer) {
         this.ipsecVpnPeer = ipsecVpnPeer;
         return this;
      }

      /**
       * @see IpsecVpnTunnel#getPeerIpAddress()
       */
      public Builder peerIpAddress(String peerIpAddress) {
         this.peerIpAddress = peerIpAddress;
         return this;
      }

      /**
       * @see IpsecVpnTunnel#getPeerNetworkAddress()
       */
      public Builder peerNetworkAddress(String peerNetworkAddress) {
         this.peerNetworkAddress = peerNetworkAddress;
         return this;
      }

      /**
       * @see IpsecVpnTunnel#getPeerNetworkMask()
       */
      public Builder peerNetworkMask(String peerNetworkMask) {
         this.peerNetworkMask = peerNetworkMask;
         return this;
      }

      /**
       * @see IpsecVpnTunnel#getSharedSecret()
       */
      public Builder sharedSecret(String sharedSecret) {
         this.sharedSecret = sharedSecret;
         return this;
      }

      /**
       * @see IpsecVpnTunnel#getEncryptionProtocol()
       */
      public Builder encryptionProtocol(String encryptionProtocol) {
         this.encryptionProtocol = encryptionProtocol;
         return this;
      }

      /**
       * @see IpsecVpnTunnel#getMtu()
       */
      public Builder mtu(int mtu) {
         this.mtu = mtu;
         return this;
      }

      /**
       * @see IpsecVpnTunnel#isEnabled()
       */
      public Builder isEnabled(boolean isEnabled) {
         this.isEnabled = isEnabled;
         return this;
      }

      /**
       * @see IpsecVpnTunnel#isOperational()
       */
      public Builder isOperational(Boolean isOperational) {
         this.isOperational = isOperational;
         return this;
      }

      /**
       * @see IpsecVpnTunnel#getErrorDetails()
       */
      public Builder errorDetails(String errorDetails) {
         this.errorDetails = errorDetails;
         return this;
      }


      public IpsecVpnTunnel build() {
         return new IpsecVpnTunnel
               (name, description, ipsecVpnPeer, peerIpAddress, peerNetworkAddress, peerNetworkMask, sharedSecret,
                     encryptionProtocol, mtu, isEnabled, isOperational, errorDetails);
      }


      public Builder fromIpsecVpnTunnel(IpsecVpnTunnel in) {
         return name(in.getName())
               .description(in.getDescription())
               .peerIpAddress(in.getPeerIpAddress())
               .peerNetworkAddress(in.getPeerNetworkAddress())
               .peerNetworkMask(in.getPeerNetworkMask())
               .sharedSecret(in.getSharedSecret())
               .encryptionProtocol(in.getEncryptionProtocol())
               .mtu(in.getMtu())
               .isEnabled(in.isEnabled())
               .isOperational(in.isOperational())
               .errorDetails(in.getErrorDetails());
      }
   }

   private IpsecVpnTunnel(String name, String description, IpsecVpnPeerType ipsecVpnPeer, String peerIpAddress,
                          String peerNetworkAddress, String peerNetworkMask, String sharedSecret, String encryptionProtocol, int mtu, boolean enabled, Boolean operational, String errorDetails) {
      this.name = name;
      this.description = description;
      this.ipsecVpnPeer = ipsecVpnPeer;
      this.peerIpAddress = peerIpAddress;
      this.peerNetworkAddress = peerNetworkAddress;
      this.peerNetworkMask = peerNetworkMask;
      this.sharedSecret = sharedSecret;
      this.encryptionProtocol = encryptionProtocol;
      this.mtu = mtu;
      isEnabled = enabled;
      isOperational = operational;
      this.errorDetails = errorDetails;
   }

   private IpsecVpnTunnel() {
      // For JAXB and builder use
   }

   @XmlElement(name = "Name", required = true)
   protected String name;
   @XmlElement(name = "Description")
   protected String description;
   @XmlElementRef
   protected IpsecVpnPeerType ipsecVpnPeer;
   @XmlElement(name = "PeerIpAddress", required = true)
   protected String peerIpAddress;
   @XmlElement(name = "PeerNetworkAddress", required = true)
   protected String peerNetworkAddress;
   @XmlElement(name = "PeerNetworkMask", required = true)
   protected String peerNetworkMask;
   @XmlElement(name = "SharedSecret", required = true)
   protected String sharedSecret;
   @XmlElement(name = "EncryptionProtocol", required = true)
   protected String encryptionProtocol;
   @XmlElement(name = "Mtu")
   protected int mtu;
   @XmlElement(name = "IsEnabled")
   protected boolean isEnabled;
   @XmlElement(name = "IsOperational")
   protected Boolean isOperational;
   @XmlElement(name = "ErrorDetails")
   protected String errorDetails;

   /**
    * Gets the value of the name property.
    *
    * @return possible object is
    *         {@link String }
    */
   public String getName() {
      return name;
   }

   /**
    * Gets the value of the description property.
    *
    * @return possible object is
    *         {@link String }
    */
   public String getDescription() {
      return description;
   }

   /**
    * Details about the peer network.
    */
   public IpsecVpnPeerType getIpsecVpnPeer() {
      return ipsecVpnPeer;
   }

   /**
    * Gets the value of the peerIpAddress property.
    *
    * @return possible object is
    *         {@link String }
    */
   public String getPeerIpAddress() {
      return peerIpAddress;
   }

   /**
    * Gets the value of the peerNetworkAddress property.
    *
    * @return possible object is
    *         {@link String }
    */
   public String getPeerNetworkAddress() {
      return peerNetworkAddress;
   }

   /**
    * Gets the value of the peerNetworkMask property.
    *
    * @return possible object is
    *         {@link String }
    */
   public String getPeerNetworkMask() {
      return peerNetworkMask;
   }

   /**
    * Gets the value of the sharedSecret property.
    *
    * @return possible object is
    *         {@link String }
    */
   public String getSharedSecret() {
      return sharedSecret;
   }

   /**
    * Gets the value of the encryptionProtocol property.
    *
    * @return possible object is
    *         {@link String }
    */
   public String getEncryptionProtocol() {
      return encryptionProtocol;
   }

   /**
    * Gets the value of the mtu property.
    */
   public int getMtu() {
      return mtu;
   }

   /**
    * Gets the value of the isEnabled property.
    */
   public boolean isEnabled() {
      return isEnabled;
   }

   /**
    * Gets the value of the isOperational property.
    *
    * @return possible object is
    *         {@link Boolean }
    */
   public Boolean isOperational() {
      return isOperational;
   }

   /**
    * Gets the value of the errorDetails property.
    *
    * @return possible object is
    *         {@link String }
    */
   public String getErrorDetails() {
      return errorDetails;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      IpsecVpnTunnel that = IpsecVpnTunnel.class.cast(o);
      return equal(name, that.name) &&
            equal(description, that.description) &&
            equal(peerIpAddress, that.peerIpAddress) &&
            equal(peerNetworkAddress, that.peerNetworkAddress) &&
            equal(peerNetworkMask, that.peerNetworkMask) &&
            equal(sharedSecret, that.sharedSecret) &&
            equal(encryptionProtocol, that.encryptionProtocol) &&
            equal(mtu, that.mtu) &&
            equal(isEnabled, that.isEnabled) &&
            equal(isOperational, that.isOperational) &&
            equal(errorDetails, that.errorDetails);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(name,
            description,
            peerIpAddress,
            peerNetworkAddress,
            peerNetworkMask,
            sharedSecret,
            encryptionProtocol,
            mtu,
            isEnabled,
            isOperational,
            errorDetails);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("")
            .add("name", name)
            .add("description", description)
            .add("peerIpAddress", peerIpAddress)
            .add("peerNetworkAddress", peerNetworkAddress)
            .add("peerNetworkMask", peerNetworkMask)
            .add("sharedSecret", sharedSecret)
            .add("encryptionProtocol", encryptionProtocol)
            .add("mtu", mtu)
            .add("isEnabled", isEnabled)
            .add("isOperational", isOperational)
            .add("errorDetails", errorDetails).toString();
   }

}
