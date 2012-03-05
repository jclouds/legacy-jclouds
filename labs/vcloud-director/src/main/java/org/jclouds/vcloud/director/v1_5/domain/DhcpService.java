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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;

/**
 * Represents a DHCP network service.
 *
 * <p>Java class for DhcpService complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="DhcpService">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.vmware.com/vcloud/v1.5}NetworkServiceType">
 *       &lt;sequence>
 *         &lt;element name="DefaultLeaseTime" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="MaxLeaseTime" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="IpRange" type="{http://www.vmware.com/vcloud/v1.5}IpRangeType" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 * @author Adam Lowe
 */
@XmlRootElement(name = "DhcpService")
@XmlType(propOrder = {
      "defaultLeaseTime",
      "maxLeaseTime",
      "ipRange"
})
public class DhcpService extends NetworkServiceType<DhcpService> {

   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return new Builder().fromDhcpService(this);
   }

   public static class Builder extends NetworkServiceType.Builder<DhcpService> {
      private int defaultLeaseTime;
      private int maxLeaseTime;
      private IpRange ipRange;

      public Builder defaultLeaseTime(int defaultLeaseTime) {
         this.defaultLeaseTime = defaultLeaseTime;
         return this;
      }

      public Builder maxLeaseTime(int maxLeaseTime) {
         this.maxLeaseTime = maxLeaseTime;
         return this;
      }

      public Builder ipRange(IpRange ipRange) {
         this.ipRange = ipRange;
         return this;
      }

      public DhcpService build() {
         return new DhcpService(isEnabled, defaultLeaseTime, maxLeaseTime, ipRange);
      }

      public Builder fromDhcpService(DhcpService in) {
         return fromNetworkService(in).defaultLeaseTime(in.getDefaultLeaseTime()).maxLeaseTime(in.getMaxLeaseTime())
               .ipRange(in.getIpRange());
      }

      public Builder fromNetworkService(NetworkServiceType<DhcpService> in) {
         return Builder.class.cast(super.fromNetworkServiceType(in));
      }

      @Override
      public Builder enabled(boolean enabled) {
         this.isEnabled = enabled;
         return this;
      }

   }

   @XmlElement(name = "DefaultLeaseTime")
   private int defaultLeaseTime;
   @XmlElement(name = "MaxLeaseTime")
   private int maxLeaseTime;
   @XmlElement(name = "IpRange")
   private IpRange ipRange;

   private DhcpService(boolean enabled, int defaultLeaseTime, int maxLeaseTime, IpRange ipRange) {
      super(enabled);
      this.defaultLeaseTime = defaultLeaseTime;
      this.maxLeaseTime = maxLeaseTime;
      this.ipRange = ipRange;
   }

   private DhcpService() {
      // for JAXB
   }

   /**
    * @return default lease in seconds for DHCP addresses.
    */
   public int getDefaultLeaseTime() {
      return defaultLeaseTime;
   }

   /**
    * @return Max lease in seconds for DHCP addresses.
    */
   public int getMaxLeaseTime() {
      return maxLeaseTime;
   }

   /**
    * @return IP range for DHCP addresses.
    */
   public IpRange getIpRange() {
      return ipRange;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      DhcpService that = DhcpService.class.cast(o);
      return super.equals(that)
            && equal(defaultLeaseTime, that.defaultLeaseTime)
            && equal(maxLeaseTime, that.maxLeaseTime)
            && equal(ipRange, that.ipRange);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), defaultLeaseTime, maxLeaseTime, ipRange);
   }

   @Override
   protected Objects.ToStringHelper string() {
      return super.string().add("defaultLeastTime", defaultLeaseTime).add("maxLeaseTime", maxLeaseTime).add("ipRange", ipRange);
   }
}
