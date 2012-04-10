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
 * Represents a NAT rule.
 * <p/>
 * <p/>
 * <p>Java class for NatRule complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="NatRule">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.vmware.com/vcloud/v1.5}VCloudExtensibleType">
 *       &lt;sequence>
 *         &lt;element name="Description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;choice>
 *           &lt;element name="OneToOneBasicRule" type="{http://www.vmware.com/vcloud/v1.5}NatOneToOneBasicRuleType"/>
 *           &lt;element name="OneToOneVmRule" type="{http://www.vmware.com/vcloud/v1.5}NatOneToOneVmRuleType"/>
 *           &lt;element name="PortForwardingRule" type="{http://www.vmware.com/vcloud/v1.5}NatPortForwardingRuleType"/>
 *           &lt;element name="VmRule" type="{http://www.vmware.com/vcloud/v1.5}NatVmRuleType"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlType(name = "NatRule", propOrder = {
      "description",
      "oneToOneBasicRule",
      "oneToOneVmRule",
      "portForwardingRule",
      "vmRule"
})
public class NatRule {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromNatRule(this);
   }

   public static class Builder {
      private String description;
      private NatOneToOneBasicRule oneToOneBasicRule;
      private NatOneToOneVmRule oneToOneVmRule;
      private NatPortForwardingRule portForwardingRule;
      private NatVmRule vmRule;

      /**
       * @see NatRule#getDescription()
       */
      public Builder description(String description) {
         this.description = description;
         return this;
      }

      /**
       * @see NatRule#getOneToOneBasicRule()
       */
      public Builder oneToOneBasicRule(NatOneToOneBasicRule oneToOneBasicRule) {
         this.oneToOneBasicRule = oneToOneBasicRule;
         return this;
      }

      /**
       * @see NatRule#getOneToOneVmRule()
       */
      public Builder oneToOneVmRule(NatOneToOneVmRule oneToOneVmRule) {
         this.oneToOneVmRule = oneToOneVmRule;
         return this;
      }

      /**
       * @see NatRule#getPortForwardingRule()
       */
      public Builder portForwardingRule(NatPortForwardingRule portForwardingRule) {
         this.portForwardingRule = portForwardingRule;
         return this;
      }

      /**
       * @see NatRule#getVmRule()
       */
      public Builder vmRule(NatVmRule vmRule) {
         this.vmRule = vmRule;
         return this;
      }

      public NatRule build() {
         return new NatRule(description, oneToOneBasicRule, oneToOneVmRule, portForwardingRule, vmRule);
      }

      public Builder fromNatRule(NatRule in) {
         return description(in.getDescription())
               .oneToOneBasicRule(in.getOneToOneBasicRule())
               .oneToOneVmRule(in.getOneToOneVmRule())
               .portForwardingRule(in.getPortForwardingRule())
               .vmRule(in.getVmRule());
      }
   }

   public NatRule(String description, NatOneToOneBasicRule oneToOneBasicRule, NatOneToOneVmRule oneToOneVmRule,
                  NatPortForwardingRule portForwardingRule, NatVmRule vmRule) {
      this.description = description;
      this.oneToOneBasicRule = oneToOneBasicRule;
      this.oneToOneVmRule = oneToOneVmRule;
      this.portForwardingRule = portForwardingRule;
      this.vmRule = vmRule;
   }

   private NatRule() {
      // for JAXB
   }

   @XmlElement(name = "Description")
   protected String description;
   @XmlElement(name = "OneToOneBasicRule")
   protected NatOneToOneBasicRule oneToOneBasicRule;
   @XmlElement(name = "OneToOneVmRule")
   protected NatOneToOneVmRule oneToOneVmRule;
   @XmlElement(name = "PortForwardingRule")
   protected NatPortForwardingRule portForwardingRule;
   @XmlElement(name = "VmRule")
   protected NatVmRule vmRule;

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
    * Gets the value of the oneToOneBasicRule property.
    *
    * @return possible object is
    *         {@link NatOneToOneBasicRule }
    */
   public NatOneToOneBasicRule getOneToOneBasicRule() {
      return oneToOneBasicRule;
   }


   /**
    * Gets the value of the oneToOneVmRule property.
    *
    * @return possible object is
    *         {@link NatOneToOneVmRule }
    */
   public NatOneToOneVmRule getOneToOneVmRule() {
      return oneToOneVmRule;
   }

   /**
    * Gets the value of the portForwardingRule property.
    *
    * @return possible object is
    *         {@link NatPortForwardingRule }
    */
   public NatPortForwardingRule getPortForwardingRule() {
      return portForwardingRule;
   }

   /**
    * Gets the value of the vmRule property.
    *
    * @return possible object is
    *         {@link NatVmRule }
    */
   public NatVmRule getVmRule() {
      return vmRule;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      NatRule that = NatRule.class.cast(o);
      return equal(description, that.description) &&
            equal(oneToOneBasicRule, that.oneToOneBasicRule) &&
            equal(oneToOneVmRule, that.oneToOneVmRule) &&
            equal(portForwardingRule, that.portForwardingRule) &&
            equal(vmRule, that.vmRule);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(description,
            oneToOneBasicRule,
            oneToOneVmRule,
            portForwardingRule,
            vmRule);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("")
            .add("description", description)
            .add("oneToOneBasicRule", oneToOneBasicRule)
            .add("oneToOneVmRule", oneToOneVmRule)
            .add("portForwardingRule", portForwardingRule)
            .add("vmRule", vmRule).toString();
   }

}
