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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * Represents a NAT network service.
 * <p/>
 * <p/>
 * <p>Java class for NatService complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="NatService">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.vmware.com/vcloud/v1.5}NetworkServiceType">
 *       &lt;sequence>
 *         &lt;element name="NatType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Policy" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="NatRule" type="{http://www.vmware.com/vcloud/v1.5}NatRuleType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlRootElement(name = "NatService")
@XmlType(propOrder = {
      "natType",
      "policy",
      "natRules"
})
public class NatService extends NetworkServiceType<NatService> {

   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return new Builder().fromNatService(this);
   }

   public static class Builder extends NetworkServiceType.Builder<NatService> {
      private String natType;
      private String policy;
      private Set<NatRule> natRules = Sets.newLinkedHashSet();

      /**
       * @see NatService#getNatType()
       */
      public Builder natType(String natType) {
         this.natType = natType;
         return this;
      }

      /**
       * @see NatService#getPolicy()
       */
      public Builder policy(String policy) {
         this.policy = policy;
         return this;
      }

      /**
       * @see NatService#getNatRules()
       */
      public Builder natRules(Set<NatRule> natRules) {
         this.natRules = checkNotNull(natRules, "natRules");
         return this;
      }

      public NatService build() {
         return new NatService(isEnabled, natType, policy, natRules);
      }

      public Builder fromNatService(NatService in) {
         return fromNetworkService(in).natType(in.getNatType()).policy(in.getPolicy())
               .natRules(in.getNatRules());
      }

      public Builder fromNetworkService(NetworkServiceType<NatService> in) {
         return Builder.class.cast(super.fromNetworkServiceType(in));
      }

      @Override
      public Builder enabled(boolean enabled) {
         this.isEnabled = enabled;
         return this;
      }
   }

   @XmlElement(name = "NatType")
   private String natType;
   @XmlElement(name = "Policy")
   private String policy;
   @XmlElement(name = "NatRule")
   private Set<NatRule> natRules = Sets.newLinkedHashSet();

   private NatService(boolean enabled, String natType, String policy, Set<NatRule> natRules) {
      super(enabled);
      this.natType = natType;
      this.policy = policy;
      this.natRules = ImmutableSet.copyOf(natRules);
   }

   private NatService() {
      // for JAXB
   }

   public String getNatType() {
      return natType;
   }

   public String getPolicy() {
      return policy;
   }

   public Set<NatRule> getNatRules() {
      return Collections.unmodifiableSet(natRules);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      NatService that = NatService.class.cast(o);
      return super.equals(that)
            && equal(natType, that.natType)
            && equal(natRules, that.natRules)
            && equal(policy, that.policy);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), natType, natRules, policy);
   }

   @Override
   protected Objects.ToStringHelper string() {
      return super.string().add("natType", natType).add("natRules", natRules).add("policy", policy);
   }
}
