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
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;


/**
 * Represents an IPSec-VPN network service.
 * <p/>
 * <p/>
 * <p>Java class for IpsecVpnService complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="IpsecVpnService">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.vmware.com/vcloud/v1.5}NetworkServiceType">
 *       &lt;sequence>
 *         &lt;element name="ExternalIpAddress" type="{http://www.vmware.com/vcloud/v1.5}IpAddressType" minOccurs="0"/>
 *         &lt;element name="PublicIpAddress" type="{http://www.vmware.com/vcloud/v1.5}IpAddressType" minOccurs="0"/>
 *         &lt;element name="IpsecVpnTunnel" type="{http://www.vmware.com/vcloud/v1.5}IpsecVpnTunnelType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlRootElement(name = "IpsecVpnService")
@XmlType(propOrder = {
      "externalIpAddress",
      "publicIpAddress",
      "ipsecVpnTunnels"
})
public class IpsecVpnService extends NetworkServiceType<IpsecVpnService> {
   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromIpsecVpnService(this);
   }

   public static class Builder extends NetworkServiceType.Builder<IpsecVpnService> {

      private String externalIpAddress;
      private String publicIpAddress;
      private List<IpsecVpnTunnel> ipsecVpnTunnels;

      /**
       * @see IpsecVpnService#getExternalIpAddress()
       */
      public Builder externalIpAddress(String externalIpAddress) {
         this.externalIpAddress = externalIpAddress;
         return this;
      }

      /**
       * @see IpsecVpnService#getPublicIpAddress()
       */
      public Builder publicIpAddress(String publicIpAddress) {
         this.publicIpAddress = publicIpAddress;
         return this;
      }

      /**
       * @see IpsecVpnService#getIpsecVpnTunnels()
       */
      public Builder ipsecVpnTunnels(List<IpsecVpnTunnel> ipsecVpnTunnels) {
         this.ipsecVpnTunnels = checkNotNull(ipsecVpnTunnels, "ipsecVpnTunnels");
         return this;
      }

      public IpsecVpnService build() {
         return new IpsecVpnService(isEnabled, externalIpAddress, publicIpAddress, ipsecVpnTunnels);
      }

      @Override
      public Builder fromNetworkServiceType(NetworkServiceType<IpsecVpnService> in) {
         return Builder.class.cast(super.fromNetworkServiceType(in));
      }

      public Builder fromIpsecVpnService(IpsecVpnService in) {
         return fromNetworkServiceType(in)
               .externalIpAddress(in.getExternalIpAddress())
               .publicIpAddress(in.getPublicIpAddress())
               .ipsecVpnTunnels(in.getIpsecVpnTunnels());
      }
   }

   private IpsecVpnService(boolean enabled, String externalIpAddress, String publicIpAddress, List<IpsecVpnTunnel> ipsecVpnTunnel) {
      super(enabled);
      this.externalIpAddress = externalIpAddress;
      this.publicIpAddress = publicIpAddress;
      this.ipsecVpnTunnels = ImmutableList.copyOf(ipsecVpnTunnel);
   }

   private IpsecVpnService() {
      // For JAXB and builder use
   }

   @XmlElement(name = "ExternalIpAddress")
   protected String externalIpAddress;
   @XmlElement(name = "PublicIpAddress")
   protected String publicIpAddress;
   @XmlElement(name = "IpsecVpnTunnel")
   protected List<IpsecVpnTunnel> ipsecVpnTunnels = Lists.newArrayList();

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
    * Gets the value of the publicIpAddress property.
    *
    * @return possible object is
    *         {@link String }
    */
   public String getPublicIpAddress() {
      return publicIpAddress;
   }

   /**
    * Gets the value of the ipsecVpnTunnel property.
    */
   public List<IpsecVpnTunnel> getIpsecVpnTunnels() {
      return Collections.unmodifiableList(this.ipsecVpnTunnels);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      IpsecVpnService that = IpsecVpnService.class.cast(o);
      return equal(externalIpAddress, that.externalIpAddress) &&
            equal(publicIpAddress, that.publicIpAddress) &&
            equal(ipsecVpnTunnels, that.ipsecVpnTunnels);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(externalIpAddress,
            publicIpAddress,
            ipsecVpnTunnels);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("")
            .add("externalIpAddress", externalIpAddress)
            .add("publicIpAddress", publicIpAddress)
            .add("ipsecVpnTunnels", ipsecVpnTunnels).toString();
   }

}
