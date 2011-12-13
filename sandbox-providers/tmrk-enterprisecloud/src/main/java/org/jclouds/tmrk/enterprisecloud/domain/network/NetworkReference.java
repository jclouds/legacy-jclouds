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
package org.jclouds.tmrk.enterprisecloud.domain.network;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.tmrk.enterprisecloud.domain.NamedResource;
import org.jclouds.tmrk.enterprisecloud.domain.internal.BaseNamedResource;
import org.jclouds.tmrk.enterprisecloud.domain.internal.BaseResource;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlRootElement;
import java.net.URI;
import java.util.Map;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_UNDERSCORE;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * <xs:complexType name="NetworkReference">
 * @author Jason King
 * 
 */
@XmlRootElement(name = "Network")
public class NetworkReference extends BaseNamedResource<NetworkReference> {
    @XmlEnum
    public static enum NetworkType {

      @XmlEnumValue("Dmz")
      DMZ,

      @XmlEnumValue("Internal")
      INTERNAL;

      public String value() {
         return UPPER_UNDERSCORE.to(LOWER_CAMEL, name());
      }

      @Override
      public String toString() {
         return value();
      }
   }

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Builder toBuilder() {
      return new Builder().fromNetworkReference(this);
   }

   public static class Builder extends BaseNamedResource.Builder<NetworkReference> {

      private String address;
      private NetworkType networkType;
      private String broadcastAddress;
      private String gatewayAddress;
      private NamedResource rnatAddress;
      private IpAddresses ipAddresses;
      
      /**
       * @see NetworkReference#getAddress
       */
      public Builder address(String address) {
         this.address = address;
         return this;
      }
      
      /**
       * @see NetworkReference#getNetworkType
       */
      public Builder networkType(NetworkType networkType) {
         this.networkType = networkType;
         return this;
      }

      /**
       * @see NetworkReference#getBroadcastAddress
       */
      public Builder broadcastAddress(String broadcastAddress) {
         this.broadcastAddress = broadcastAddress;
         return this;
      }

      /**
       * @see NetworkReference#getGatewayAddress
       */
      public Builder gatewayAddress(String gatewayAddress) {
         this.gatewayAddress = gatewayAddress;
         return this;
      }

      /**
       * @see NetworkReference#getRnatAddress
       */
      public Builder rnatAddress(NamedResource rnatAddress) {
         this.rnatAddress = rnatAddress;
         return this;
      }

      /**
       * @see NetworkReference#getIpAddresses
       */
      public Builder gatewayAddress(IpAddresses ipAddresses) {
         this.ipAddresses = ipAddresses;
         return this;
      }

      @Override
      public NetworkReference build() {
         return new NetworkReference(href, type, name, address, networkType, broadcastAddress, gatewayAddress, rnatAddress, ipAddresses);
      }

      public Builder fromNetworkReference(NetworkReference in) {
         return fromNamedResource(in).networkType(in.getNetworkType());
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromBaseResource(BaseResource<NetworkReference> in) {
         return Builder.class.cast(super.fromBaseResource(in));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromNamedResource(BaseNamedResource<NetworkReference> in) {
         return Builder.class.cast(super.fromNamedResource(in));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder name(String name) {
         return Builder.class.cast(super.name(name));
      }

       /**
       * {@inheritDoc}
       */
      @Override
      public Builder href(URI href) {
         return Builder.class.cast(super.href(href));
      }

       /**
       * {@inheritDoc}
       */
      @Override
      public Builder type(String type) {
         return Builder.class.cast(super.type(type));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromAttributes(Map<String, String> attributes) {
         return Builder.class.cast(super.fromAttributes(attributes));
      }
   }

   @XmlElement(name = "Address", required = false)
   private String address;
   
   @XmlElement(name = "NetworkType", required = true)
   private NetworkType networkType;

   @XmlElement(name = "BroadcastAddress", required = false)
   private String broadcastAddress;

   @XmlElement(name = "GatewayAddress", required = false)
   private String gatewayAddress;

   @XmlElement(name = "RnatAddress", required = false)
   private NamedResource rnatAddress;

   @XmlElement(name = "IpAddresses", required = false)
   private IpAddresses ipAddresses;

   private NetworkReference(URI href, String type, String name,@Nullable String address, NetworkType networkType,
                            @Nullable String broadcastAddress, @Nullable String gatewayAddress, @Nullable NamedResource rnatAddress, @Nullable IpAddresses ipAddresses) {
      super(href, type, name);
      this.address = address;
      this.networkType = checkNotNull(networkType,"networkType");
      this.broadcastAddress = broadcastAddress;
      this.gatewayAddress = gatewayAddress;
      this.rnatAddress = rnatAddress;
      this.ipAddresses = ipAddresses;
   }

   private NetworkReference() {
       //For JAXB
   }

   public String getAddress() {
      return address;
   }

   public NetworkType getNetworkType() {
      return networkType;
   }

   public String getBroadcastAddress() {
      return broadcastAddress;
   }

   public String getGatewayAddress() {
      return gatewayAddress;
   }

   public NamedResource getRnatAddress() {
      return rnatAddress;
   }

   public IpAddresses getIpAddresses() {
      return ipAddresses;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      if (!super.equals(o)) return false;

      NetworkReference that = (NetworkReference) o;

      if (address != null ? !address.equals(that.address) : that.address != null)
         return false;
      if (broadcastAddress != null ? !broadcastAddress.equals(that.broadcastAddress) : that.broadcastAddress != null)
         return false;
      if (gatewayAddress != null ? !gatewayAddress.equals(that.gatewayAddress) : that.gatewayAddress != null)
         return false;
      if (ipAddresses != null ? !ipAddresses.equals(that.ipAddresses) : that.ipAddresses != null)
         return false;
      if (networkType != that.networkType) return false;
      if (rnatAddress != null ? !rnatAddress.equals(that.rnatAddress) : that.rnatAddress != null)
         return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + (address != null ? address.hashCode() : 0);
      result = 31 * result + networkType.hashCode();
      result = 31 * result + (broadcastAddress != null ? broadcastAddress.hashCode() : 0);
      result = 31 * result + (gatewayAddress != null ? gatewayAddress.hashCode() : 0);
      result = 31 * result + (rnatAddress != null ? rnatAddress.hashCode() : 0);
      result = 31 * result + (ipAddresses != null ? ipAddresses.hashCode() : 0);
      return result;
   }

   @Override
   public String string() {
      return super.string()+", address="+address+", networkType="+networkType+", broadcastAddress="+broadcastAddress+", gatewayAddress="+gatewayAddress+", rnatAddress="+rnatAddress+", ipAddresses="+ipAddresses;
   }
}