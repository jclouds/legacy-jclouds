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

package org.jclouds.vcloud.director.v1_5.domain.network;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

/**
 * Represents a network connection.
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
 */
@XmlType(name = "NetworkConnection", propOrder = {
      "networkConnectionIndex",
      "ipAddress",
      "externalIpAddress",
      "isConnected",
      "macAddress",
      "ipAddressAllocationMode"
})
public class NetworkConnection {
   
   @XmlType
   @XmlEnum(String.class)
   public static enum IpAddressAllocationMode {
      @XmlEnumValue("POOL") POOL("pool"),
      @XmlEnumValue("DHCP") DHCP("dhcp"),
      @XmlEnumValue("MANUAL") MANUAL("manual"),
      @XmlEnumValue("NONE") NONE("none"),
      @XmlEnumValue("") UNRECOGNIZED("unrecognized");
      
      public static final List<IpAddressAllocationMode> ALL = ImmutableList.of(POOL, DHCP, MANUAL, NONE);

      protected final String label;

      IpAddressAllocationMode(String stringValue) {
         this.label = stringValue;
      }

      public String label() {
         return label;
      }

      protected static final Map<String, IpAddressAllocationMode> IP_ADDRESS_ALLOCATION_MODE_BY_ID = Maps.uniqueIndex(
            ImmutableSet.copyOf(IpAddressAllocationMode.values()), new Function<IpAddressAllocationMode, String>() {
               @Override
               public String apply(IpAddressAllocationMode input) {
                  return input.label;
               }
            });

      public static IpAddressAllocationMode fromValue(String value) {
         IpAddressAllocationMode mode = IP_ADDRESS_ALLOCATION_MODE_BY_ID.get(checkNotNull(value, "stringValue"));
         return mode == null ? UNRECOGNIZED : mode;
      }
   }

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
      private IpAddressAllocationMode ipAddressAllocationMode;
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
       * @see NetworkConnection#isConnected()
       */
      public Builder isConnected(boolean isConnected) {
         this.isConnected = isConnected;
         return this;
      }

      /**
       * @see NetworkConnection#getMACAddress()
       */
      public Builder macAddress(String macAddress) {
         this.macAddress = macAddress;
         return this;
      }

      /**
       * @see NetworkConnection#getIpAddressAllocationMode()
       */
      public Builder ipAddressAllocationMode(IpAddressAllocationMode ipAddressAllocationMode) {
         this.ipAddressAllocationMode = ipAddressAllocationMode;
         return this;
      }

      /**
       * @see NetworkConnection#getIpAddressAllocationMode()
       */
      public Builder ipAddressAllocationMode(String ipAddressAllocationMode) {
         this.ipAddressAllocationMode = IpAddressAllocationMode.valueOf(ipAddressAllocationMode);
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
       * @see NetworkConnection#needsCustomization()
       */
      public Builder needsCustomization(Boolean needsCustomization) {
         this.needsCustomization = needsCustomization;
         return this;
      }


      public NetworkConnection build() {
         return new NetworkConnection(networkConnectionIndex, ipAddress, externalIpAddress, isConnected,
               macAddress, ipAddressAllocationMode, network, needsCustomization);
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

   public NetworkConnection(int networkConnectionIndex, String ipAddress, String externalIpAddress, boolean connected,
                            String macAddress, IpAddressAllocationMode ipAddressAllocationMode, String network, Boolean needsCustomization) {
      this.networkConnectionIndex = networkConnectionIndex;
      this.ipAddress = ipAddress;
      this.externalIpAddress = externalIpAddress;
      this.isConnected = connected;
      this.macAddress = macAddress;
      this.ipAddressAllocationMode = ipAddressAllocationMode;
      this.network = network;
      this.needsCustomization = needsCustomization;
   }

   NetworkConnection() {
      // for JAXB
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
   protected IpAddressAllocationMode ipAddressAllocationMode;
   @XmlAttribute(required = true)
   protected String network;
   @XmlAttribute
   protected Boolean needsCustomization;

   /**
    * Gets the value of the networkConnectionIndex property.
    */
   public int getNetworkConnectionIndex() {
      return networkConnectionIndex;
   }

   /**
    * Gets the value of the ipAddress property.
    *
    * @return possible object is
    *         {@link String }
    */
   public String getIpAddress() {
      return ipAddress;
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
    * Gets the value of the isConnected property.
    */
   public boolean isConnected() {
      return isConnected;
   }

   /**
    * Gets the value of the macAddress property.
    *
    * @return possible object is
    *         {@link String }
    */
   public String getMACAddress() {
      return macAddress;
   }

   /**
    * Gets the value of the ipAddressAllocationMode property.
    *
    * @return possible object is
    *         {@link String }
    */
   public IpAddressAllocationMode getIpAddressAllocationMode() {
      return ipAddressAllocationMode;
   }

   /**
    * Gets the value of the network property.
    *
    * @return possible object is
    *         {@link String }
    */
   public String getNetwork() {
      return network;
   }

   /**
    * Gets the value of the needsCustomization property.
    *
    * @return possible object is
    *         {@link Boolean }
    */
   public Boolean needsCustomization() {
      return needsCustomization;
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
