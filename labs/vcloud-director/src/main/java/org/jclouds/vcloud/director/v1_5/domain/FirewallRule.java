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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;


/**
 * Represents a firewall rule.
 * <p/>
 * <p/>
 * <p>Java class for FirewallRule complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="FirewallRule">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.vmware.com/vcloud/v1.5}VCloudExtensibleType">
 *       &lt;sequence>
 *         &lt;element name="IsEnabled" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="Description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Policy" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Protocols" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;choice>
 *                   &lt;sequence>
 *                     &lt;element name="Tcp" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *                     &lt;element name="Udp" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *                   &lt;/sequence>
 *                   &lt;element name="Icmp" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *                   &lt;element name="Any" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *                 &lt;/choice>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Port" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="DestinationIp" type="{http://www.vmware.com/vcloud/v1.5}FirewallIpAddressType"/>
 *         &lt;element name="SourcePort" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="SourceIp" type="{http://www.vmware.com/vcloud/v1.5}FirewallIpAddressType"/>
 *         &lt;element name="Direction" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="EnableLogging" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlType(name = "FirewallRule", propOrder = {
      "isEnabled",
      "description",
      "policy",
      "protocols",
      "port",
      "destinationIp",
      "sourcePort",
      "sourceIp",
      "direction",
      "enableLogging"
})
public class FirewallRule {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromFirewallRule(this);
   }

   public static class Builder {

      private Boolean isEnabled;
      private String description;
      private String policy;
      private FirewallRuleProtocols protocols;
      private Integer port;
      private String destinationIp;
      private Integer sourcePort;
      private String sourceIp;
      private String direction;
      private Boolean enableLogging;

      /**
       * @see FirewallRule#isEnabled()
       */
      public Builder isEnabled(Boolean isEnabled) {
         this.isEnabled = isEnabled;
         return this;
      }

      /**
       * @see FirewallRule#getDescription()
       */
      public Builder description(String description) {
         this.description = description;
         return this;
      }

      /**
       * @see FirewallRule#getPolicy()
       */
      public Builder policy(String policy) {
         this.policy = policy;
         return this;
      }

      /**
       * @see FirewallRule#getProtocols()
       */
      public Builder protocols(FirewallRuleProtocols protocols) {
         this.protocols = protocols;
         return this;
      }

      /**
       * @see FirewallRule#getPort()
       */
      public Builder port(int port) {
         this.port = port;
         return this;
      }

      /**
       * @see FirewallRule#getDestinationIp()
       */
      public Builder destinationIp(String destinationIp) {
         this.destinationIp = destinationIp;
         return this;
      }

      /**
       * @see FirewallRule#getSourcePort()
       */
      public Builder sourcePort(int sourcePort) {
         this.sourcePort = sourcePort;
         return this;
      }

      /**
       * @see FirewallRule#getSourceIp()
       */
      public Builder sourceIp(String sourceIp) {
         this.sourceIp = sourceIp;
         return this;
      }

      /**
       * @see FirewallRule#getDirection()
       */
      public Builder direction(String direction) {
         this.direction = direction;
         return this;
      }

      /**
       * @see FirewallRule#isEnableLogging()
       */
      public Builder enableLogging(Boolean enableLogging) {
         this.enableLogging = enableLogging;
         return this;
      }

      public FirewallRule build() {
         return new FirewallRule(
               isEnabled, description, policy, protocols, port, destinationIp, sourcePort, sourceIp, direction, enableLogging);
      }

      public Builder fromFirewallRule(FirewallRule in) {
         return isEnabled(in.isEnabled())
               .description(in.getDescription())
               .policy(in.getPolicy())
               .protocols(in.getProtocols())
               .port(in.getPort())
               .destinationIp(in.getDestinationIp())
               .sourcePort(in.getSourcePort())
               .sourceIp(in.getSourceIp())
               .direction(in.getDirection())
               .enableLogging(in.isEnableLogging());
      }
   }

   private FirewallRule(Boolean enabled, String description, String policy, FirewallRuleProtocols protocols, Integer port,
                       String destinationIp, Integer sourcePort, String sourceIp, String direction, Boolean enableLogging) {
      isEnabled = enabled;
      this.description = description;
      this.policy = policy;
      this.protocols = protocols;
      this.port = port;
      this.destinationIp = destinationIp;
      this.sourcePort = sourcePort;
      this.sourceIp = sourceIp;
      this.direction = direction;
      this.enableLogging = enableLogging;
   }

   private FirewallRule() {
      // For JAXB
   }

   @XmlElement(name = "IsEnabled")
   protected Boolean isEnabled;
   @XmlElement(name = "Description")
   protected String description;
   @XmlElement(name = "Policy")
   protected String policy;
   @XmlElement(name = "Protocols")
   protected FirewallRuleProtocols protocols;
   @XmlElement(name = "Port")
   protected Integer port;
   @XmlElement(name = "DestinationIp", required = true)
   protected String destinationIp;
   @XmlElement(name = "SourcePort")
   protected Integer sourcePort;
   @XmlElement(name = "SourceIp", required = true)
   protected String sourceIp;
   @XmlElement(name = "Direction")
   protected String direction;
   @XmlElement(name = "EnableLogging")
   protected Boolean enableLogging;

   /**
    * Gets the value of the isEnabled property.
    *
    * @return possible object is
    *         {@link Boolean }
    */
   public Boolean isEnabled() {
      return isEnabled;
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
    * Gets the value of the policy property.
    *
    * @return possible object is
    *         {@link String }
    */
   public String getPolicy() {
      return policy;
   }

   /**
    * Gets the value of the protocols property.
    *
    * @return possible object is
    *         {@link FirewallRuleProtocols }
    */
   public FirewallRuleProtocols getProtocols() {
      return protocols;
   }

   /**
    * Gets the value of the port property.
    */
   public int getPort() {
      return port;
   }

   /**
    * Gets the value of the destinationIp property.
    *
    * @return possible object is
    *         {@link String }
    */
   public String getDestinationIp() {
      return destinationIp;
   }

   /**
    * Gets the value of the sourcePort property.
    */
   public int getSourcePort() {
      return sourcePort;
   }

   /**
    * Gets the value of the sourceIp property.
    *
    * @return possible object is
    *         {@link String }
    */
   public String getSourceIp() {
      return sourceIp;
   }

   /**
    * Gets the value of the direction property.
    *
    * @return possible object is
    *         {@link String }
    */
   public String getDirection() {
      return direction;
   }

   /**
    * Gets the value of the enableLogging property.
    *
    * @return possible object is
    *         {@link Boolean }
    */
   public Boolean isEnableLogging() {
      return enableLogging;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      FirewallRule that = FirewallRule.class.cast(o);
      return equal(isEnabled, that.isEnabled) &&
            equal(description, that.description) &&
            equal(policy, that.policy) &&
            equal(protocols, that.protocols) &&
            equal(port, that.port) &&
            equal(destinationIp, that.destinationIp) &&
            equal(sourcePort, that.sourcePort) &&
            equal(sourceIp, that.sourceIp) &&
            equal(direction, that.direction) &&
            equal(enableLogging, that.enableLogging);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(isEnabled,
            description,
            policy,
            protocols,
            port,
            destinationIp,
            sourcePort,
            sourceIp,
            direction,
            enableLogging);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("")
            .add("isEnabled", isEnabled)
            .add("description", description)
            .add("policy", policy)
            .add("protocols", protocols)
            .add("port", port)
            .add("destinationIp", destinationIp)
            .add("sourcePort", sourcePort)
            .add("sourceIp", sourceIp)
            .add("direction", direction)
            .add("enableLogging", enableLogging).toString();
   }

}
