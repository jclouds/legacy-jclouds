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
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorConstants.VCLOUD_1_5_NS;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Objects;

/**
 * Specify network settings like gateway, network mask, DNS servers, IP ranges, etc.
 * 
 * @author danikov
 */
@XmlRootElement(namespace = VCLOUD_1_5_NS, name = "IpScope")
@XmlAccessorType(XmlAccessType.FIELD)
public class IpScope {
   
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromIpScope(this);
   }

   public static class Builder {
      
      private boolean isInherited;
      private String gateway;
      private String netmask;
      private String dns1;
      private String dns2;
      private String dnsSuffix;
      private IpRanges ipRanges;
      private IpAddresses allocatedIpAddresses;

      /**
       * @see IpScope#isInherited()
       */
      public Builder isInherited(boolean isInherited) {
         this.isInherited = isInherited;
         return this;
      }

      /**
       * @see IpScope#getGateway()
       */
      public Builder gateway(String gateway) {
         this.gateway = gateway;
         return this;
      }

      /**
       * @see IpScope#getNetmask()
       */
      public Builder netmask(String netmask) {
         this.netmask = netmask;
         return this;
      }

      /**
       * @see IpScope#getDns1()
       */
      public Builder dns1(String dns1) {
         this.dns1 = dns1;
         return this;
      }

      /**
       * @see IpScope#getDns2()
       */
      public Builder dns2(String dns2) {
         this.dns2 = dns2;
         return this;
      }

      /**
       * @see IpScope#getDnsSuffix()
       */
      public Builder dnsSuffix(String dnsSuffix) {
         this.dnsSuffix = dnsSuffix;
         return this;
      }

      /**
       * @see IpScope#getIpRanges()
       */
      public Builder ipRanges(IpRanges ipRanges) {
         this.ipRanges = ipRanges;
         return this;
      }
      
      /**
       * @see IpScope#getAllocatedIpAddresses()
       */
      public Builder allocatedIpAddresses(IpAddresses allocatedIpAddresses) {
         this.allocatedIpAddresses = allocatedIpAddresses;
         return this;
      }

      public IpScope build() {
         IpScope ipScope = new IpScope(isInherited);
         ipScope.setGateway(gateway);
         ipScope.setNetmask(netmask);
         ipScope.setDns1(dns1);
         ipScope.setDns2(dns2);
         ipScope.setDnsSuffix(dnsSuffix);
         ipScope.setIpRanges(ipRanges);
         ipScope.setAllocatedIpAddresses(allocatedIpAddresses);
         return ipScope;
      }

      public Builder fromIpScope(IpScope in) {
         return isInherited(in.isInherited()).gateway(in.getGateway())
               .netmask(in.getNetmask())
               .dns1(in.getDns1())
               .dns2(in.getDns2())
               .dnsSuffix(in.getDnsSuffix())
               .ipRanges(in.getIpRanges())
               .allocatedIpAddresses(in.getAllocatedIpAddresses());
      }
   }
   
   private IpScope() {
      // For JAXB and builder use
   }

   private IpScope(boolean isInherited) {
      this.isInherited = isInherited;
   }

   @XmlElement(namespace = VCLOUD_1_5_NS, name = "IsInherited")
   private boolean isInherited;
   @XmlElement(namespace = VCLOUD_1_5_NS, name = "Gateway")
   private String gateway;
   @XmlElement(namespace = VCLOUD_1_5_NS, name = "Netmask")
   private String netmask;
   @XmlElement(namespace = VCLOUD_1_5_NS, name = "Dns1")
   private String dns1;
   @XmlElement(namespace = VCLOUD_1_5_NS, name = "Dns2")
   private String dns2;
   @XmlElement(namespace = VCLOUD_1_5_NS, name = "DnsSuffix")
   private String dnsSuffix;
   @XmlElement(namespace = VCLOUD_1_5_NS, name = "IpRanges")
   private IpRanges ipRanges;
   @XmlElement(namespace = VCLOUD_1_5_NS, name = "AllocatedIpAddresses")
   private IpAddresses allocatedIpAddresses;

   /**
    * @return True if the IP scope is inherit from parent network.
    */
   public boolean isInherited() {
      return isInherited;
   }
   
   /**
    * @return Gateway of the network..
    */
   public String getGateway() {
      return gateway;
   }
   
   public void setGateway(String gateway) {
      this.gateway = gateway;
   }

   /**
    * @return Network mask.
    */
   public String getNetmask() {
      return netmask;
   }
   
   public void setNetmask(String netmask) {
      this.netmask = netmask;
   }

   /**
    * @return Primary DNS server.
    */
   public String getDns1() {
      return dns1;
   }
   
   public void setDns1(String dns1) {
      this.dns1 = dns1;
   }
   
   /**
    * @return Secondary DNS server.
    */
   public String getDns2() {
      return dns2;
   }
   
   public void setDns2(String dns2) {
      this.dns2 = dns2;
   }

   /**
    * @return DNS suffix.
    */
   public String getDnsSuffix() {
      return dnsSuffix;
   }
   
   public void setDnsSuffix(String dnsSuffix) {
      this.dnsSuffix = dnsSuffix;
   }
   
   /**
    * @return IP ranges used for static pool allocation in the network.
    */
   public IpRanges getIpRanges() {
      return ipRanges;
   }
   
   public void setIpRanges(IpRanges ipRanges) {
      this.ipRanges = ipRanges;
   }
   
   /**
    * @return Read-only list of allocated IP addresses in the network.
    */
   public IpAddresses getAllocatedIpAddresses() {
      return allocatedIpAddresses;
   }
   
   public void setAllocatedIpAddresses(IpAddresses allocatedIpAddresses) {
      this.allocatedIpAddresses = allocatedIpAddresses;
   }
   
   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      IpScope that = IpScope.class.cast(o);
      return equal(isInherited, that.isInherited) && equal(gateway, that.gateway) &&
            equal(netmask, that.netmask) &&
            equal(dns1, that.dns1) &&
            equal(dns2, that.dns2) &&
            equal(dnsSuffix, that.dnsSuffix) &&
            equal(ipRanges, that.ipRanges) &&
            equal(allocatedIpAddresses, that.allocatedIpAddresses);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(isInherited, gateway, netmask, dns1, dns2, dnsSuffix,
            ipRanges, allocatedIpAddresses);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("").add("isInherited", isInherited)
            .add("gateway", gateway)
            .add("netmask", netmask)
            .add("dns1", dns1)
            .add("dns2", dns2)
            .add("dnsSuffix", dnsSuffix)
            .add("ipRanges", ipRanges)
            .add("allocatedIpAddresses", allocatedIpAddresses).toString();
   }
   
}
