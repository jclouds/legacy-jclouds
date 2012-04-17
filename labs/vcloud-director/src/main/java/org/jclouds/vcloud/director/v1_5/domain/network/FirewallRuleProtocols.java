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
 * <p>Java class for anonymous complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;sequence>
 *           &lt;element name="Tcp" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *           &lt;element name="Udp" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;/sequence>
 *         &lt;element name="Icmp" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="Any" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlType(propOrder = {
      "tcp",
      "udp",
      "icmp",
      "any"
})
public class FirewallRuleProtocols {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromProtocols(this);
   }

   public static class Builder {
      private Boolean tcp;
      private Boolean udp;
      private Boolean icmp;
      private Boolean any;

      public Builder tcp(Boolean tcp) {
         this.tcp = tcp;
         return this;
      }

      public Builder udp(Boolean udp) {
         this.udp = udp;
         return this;
      }

      public Builder icmp(Boolean icmp) {
         this.icmp = icmp;
         return this;
      }

      public Builder any(Boolean any) {
         this.any = any;
         return this;
      }

      public Builder fromProtocols(FirewallRuleProtocols in) {
         return tcp(in.isTcp()).udp(in.isUdp()).icmp(in.isIcmp()).any(in.isAny());
      }

      public FirewallRuleProtocols build() {
         return new FirewallRuleProtocols(tcp, udp, icmp, any);
      }
   }

   @XmlElement(name = "Tcp")
   private Boolean tcp;
   @XmlElement(name = "Udp")
   private Boolean udp;
   @XmlElement(name = "Icmp")
   private Boolean icmp;
   @XmlElement(name = "Any")
   private Boolean any;

   private FirewallRuleProtocols(Boolean tcp, Boolean udp, Boolean icmp, Boolean any) {
      this.tcp = tcp;
      this.udp = udp;
      this.icmp = icmp;
      this.any = any;
   }

   private FirewallRuleProtocols() {
      // for JAXB
   }

   /**
    * Gets the value of the tcp property.
    *
    * @return possible object is
    *         {@link Boolean }
    */
   public Boolean isTcp() {
      return tcp;
   }

   /**
    * Gets the value of the udp property.
    *
    * @return possible object is
    *         {@link Boolean }
    */
   public Boolean isUdp() {
      return udp;
   }

   /**
    * Gets the value of the icmp property.
    *
    * @return possible object is
    *         {@link Boolean }
    */
   public Boolean isIcmp() {
      return icmp;
   }

   /**
    * Gets the value of the any property.
    *
    * @return possible object is
    *         {@link Boolean }
    */
   public Boolean isAny() {
      return any;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      FirewallRuleProtocols that = FirewallRuleProtocols.class.cast(o);
      return equal(tcp, that.tcp) &&
            equal(udp, that.udp) &&
            equal(icmp, that.icmp) &&
            equal(any, that.any);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(tcp, udp, icmp, any);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("")
            .add("tcp", tcp)
            .add("udp", udp)
            .add("icmp", icmp)
            .add("any", any).toString();
   }
}
