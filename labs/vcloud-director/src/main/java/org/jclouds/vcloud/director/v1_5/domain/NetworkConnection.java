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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;


/**
 * 
 *                 Represents a network connection.
 *             
 * 
 * <p>Java class for NetworkConnection complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="NetworkConnection">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.vmware.com/vcloud/v1.5}VCloudExtensibleType">
 *       &lt;sequence>
 *         &lt;element name="NetworkConnectionIndex" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="IpAddress" type="{http://www.vmware.com/vcloud/v1.5}IpAddressType" minOccurs="0"/>
 *         &lt;element name="ExternalIpAddress" type="{http://www.vmware.com/vcloud/v1.5}IpAddressType" minOccurs="0"/>
 *         &lt;element name="IsConnected" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="MACAddress" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="IpAddressAllocationMode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *       &lt;attribute name="network" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="needsCustomization" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NetworkConnection", propOrder = {
    "networkConnectionIndex",
    "ipAddress",
    "externalIpAddress",
    "isConnected",
    "macAddress",
    "ipAddressAllocationMode"
})
public class NetworkConnection {
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromNetworkConnection(this);
   }

   public static class Builder {
      
      private int networkConnectionIndex;
      private String ipAddress;
      private String externalIpAddress;
      private boolean isConnected;
      private String macAddress;
      private String ipAddressAllocationMode;
      private String network;
      private Boolean needsCustomization;

      /**
       * @see NetworkConnection#getNetworkConnectionIndex()
       */
      public Builder networkConnectionIndex(int networkConnectionIndex) {
         this.networkConnectionIndex = networkConnectionIndex;
         return this;
      }

      /**
       * @see NetworkConnection#getIpAddress()
       */
      public Builder ipAddress(String ipAddress) {
         this.ipAddress = ipAddress;
         return this;
      }

      /**
       * @see NetworkConnection#getExternalIpAddress()
       */
      public Builder externalIpAddress(String externalIpAddress) {
         this.externalIpAddress = externalIpAddress;
         return this;
      }

      /**
       * @see NetworkConnection#getIsConnected()
       */
      public Builder isConnected(boolean isConnected) {
         this.isConnected = isConnected;
         return this;
      }

      /**
       * @see NetworkConnection#getMacAddress()
       */
      public Builder macAddress(String macAddress) {
         this.macAddress = macAddress;
         return this;
      }

      /**
       * @see NetworkConnection#getIpAddressAllocationMode()
       */
      public Builder ipAddressAllocationMode(String ipAddressAllocationMode) {
         this.ipAddressAllocationMode = ipAddressAllocationMode;
         return this;
      }

      /**
       * @see NetworkConnection#getNetwork()
       */
      public Builder network(String network) {
         this.network = network;
         return this;
      }

      /**
       * @see NetworkConnection#getNeedsCustomization()
       */
      public Builder needsCustomization(Boolean needsCustomization) {
         this.needsCustomization = needsCustomization;
         return this;
      }


      public NetworkConnection build() {
         NetworkConnection networkConnection = new NetworkConnection(macAddress);
         networkConnection.setNetworkConnectionIndex(networkConnectionIndex);
         networkConnection.setIpAddress(ipAddress);
         networkConnection.setExternalIpAddress(externalIpAddress);
         networkConnection.setIsConnected(isConnected);
         networkConnection.setIpAddressAllocationMode(ipAddressAllocationMode);
         networkConnection.setNetwork(network);
         networkConnection.setNeedsCustomization(needsCustomization);
         return networkConnection;
      }


      public Builder fromNetworkConnection(NetworkConnection in) {
         return networkConnectionIndex(in.getNetworkConnectionIndex())
            .ipAddress(in.getIpAddress())
            .externalIpAddress(in.getExternalIpAddress())
            .isConnected(in.isConnected())
            .macAddress(in.getMACAddress())
            .ipAddressAllocationMode(in.getIpAddressAllocationMode())
            .network(in.getNetwork())
            .needsCustomization(in.needsCustomization());
      }
   }

   private NetworkConnection() {
      // For JAXB and builder use
   }

   private NetworkConnection(String macAddress) {
      this.macAddress = macAddress;
   }


    @XmlElement(name = "NetworkConnectionIndex")
    protected int networkConnectionIndex;
    @XmlElement(name = "IpAddress")
    protected String ipAddress;
    @XmlElement(name = "ExternalIpAddress")
    protected String externalIpAddress;
    @XmlElement(name = "IsConnected")
    protected boolean isConnected;
    @XmlElement(name = "MACAddress")
    protected String macAddress;
    @XmlElement(name = "IpAddressAllocationMode", required = true)
    protected String ipAddressAllocationMode;
    @XmlAttribute(required = true)
    protected String network;
    @XmlAttribute
    protected Boolean needsCustomization;

    /**
     * Gets the value of the networkConnectionIndex property.
     * 
     */
    public int getNetworkConnectionIndex() {
        return networkConnectionIndex;
    }

    /**
     * Sets the value of the networkConnectionIndex property.
     * 
     */
    public void setNetworkConnectionIndex(int value) {
        this.networkConnectionIndex = value;
    }

    /**
     * Gets the value of the ipAddress property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIpAddress() {
        return ipAddress;
    }

    /**
     * Sets the value of the ipAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIpAddress(String value) {
        this.ipAddress = value;
    }

    /**
     * Gets the value of the externalIpAddress property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExternalIpAddress() {
        return externalIpAddress;
    }

    /**
     * Sets the value of the externalIpAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExternalIpAddress(String value) {
        this.externalIpAddress = value;
    }

    /**
     * Gets the value of the isConnected property.
     * 
     */
    public boolean isConnected() {
        return isConnected;
    }

    /**
     * Sets the value of the isConnected property.
     * 
     */
    public void setIsConnected(boolean value) {
        this.isConnected = value;
    }

    /**
     * Gets the value of the macAddress property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMACAddress() {
        return macAddress;
    }

    /**
     * Sets the value of the macAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMACAddress(String value) {
        this.macAddress = value;
    }

    /**
     * Gets the value of the ipAddressAllocationMode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIpAddressAllocationMode() {
        return ipAddressAllocationMode;
    }

    /**
     * Sets the value of the ipAddressAllocationMode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIpAddressAllocationMode(String value) {
        this.ipAddressAllocationMode = value;
    }

    /**
     * Gets the value of the network property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNetwork() {
        return network;
    }

    /**
     * Sets the value of the network property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNetwork(String value) {
        this.network = value;
    }

    /**
     * Gets the value of the needsCustomization property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean needsCustomization() {
        return needsCustomization;
    }

    /**
     * Sets the value of the needsCustomization property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setNeedsCustomization(Boolean value) {
        this.needsCustomization = value;
    }

   @Override
   public boolean equals(Object o) {
      if (this == o)
          return true;
      if (o == null || getClass() != o.getClass())
         return false;
      NetworkConnection that = NetworkConnection.class.cast(o);
      return equal(networkConnectionIndex, that.networkConnectionIndex) && 
           equal(ipAddress, that.ipAddress) && 
           equal(externalIpAddress, that.externalIpAddress) && 
           equal(isConnected, that.isConnected) && 
           equal(macAddress, that.macAddress) && 
           equal(ipAddressAllocationMode, that.ipAddressAllocationMode) && 
           equal(network, that.network) && 
           equal(needsCustomization, that.needsCustomization);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(networkConnectionIndex, 
           ipAddress, 
           externalIpAddress, 
           isConnected, 
           macAddress, 
           ipAddressAllocationMode, 
           network, 
           needsCustomization);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("")
            .add("networkConnectionIndex", networkConnectionIndex)
            .add("ipAddress", ipAddress)
            .add("externalIpAddress", externalIpAddress)
            .add("isConnected", isConnected)
            .add("macAddress", macAddress)
            .add("ipAddressAllocationMode", ipAddressAllocationMode)
            .add("network", network)
            .add("needsCustomization", needsCustomization).toString();
   }

}
