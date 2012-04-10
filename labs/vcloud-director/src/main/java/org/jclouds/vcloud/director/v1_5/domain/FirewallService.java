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
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;


/**
 * Represents a network firewall service.
 * <p/>
 * <p/>
 * <p>Java class for FirewallService complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="FirewallService">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.vmware.com/vcloud/v1.5}NetworkServiceType">
 *       &lt;sequence>
 *         &lt;element name="DefaultAction" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="LogDefaultAction" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="FirewallRule" type="{http://www.vmware.com/vcloud/v1.5}FirewallRuleType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlRootElement(name = "FirewallService")
@XmlType(propOrder = {
      "defaultAction",
      "logDefaultAction",
      "firewallRules"
})
public class FirewallService extends NetworkServiceType<FirewallService> {

   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return new Builder().fromFirewallService(this);
   }

   public static class Builder extends NetworkServiceType.Builder<FirewallService> {
      private String defaultAction;
      private Boolean logDefaultAction;
      private Set<FirewallRule> firewallRules = Sets.newLinkedHashSet();

      /**
       * @see FirewallService#getDefaultAction()
       */
      public Builder defaultAction(String defaultAction) {
         this.defaultAction = defaultAction;
         return this;
      }

      /**
       * @see FirewallService#isLogDefaultAction()
       */
      public Builder logDefaultAction(Boolean logDefaultAction) {
         this.logDefaultAction = logDefaultAction;
         return this;
      }

      /**
       * @see FirewallService#getFirewallRules()
       */
      public Builder firewallRules(Set<FirewallRule> firewallRules) {
         this.firewallRules = checkNotNull(firewallRules, "firewallRules");
         return this;
      }

      @Override
      public FirewallService build() {
         return new FirewallService(isEnabled, defaultAction, logDefaultAction, firewallRules);

      }

      @Override
      public Builder fromNetworkServiceType(NetworkServiceType<FirewallService> in) {
         return Builder.class.cast(super.fromNetworkServiceType(in));
      }

      public Builder fromFirewallService(FirewallService in) {
         return fromNetworkServiceType(in)
               .defaultAction(in.getDefaultAction())
               .logDefaultAction(in.isLogDefaultAction())
               .firewallRules(in.getFirewallRules());
      }
      
      @Override
      public Builder enabled(boolean isEnabled) {
         this.isEnabled = isEnabled;
         return this;
      }
   }

   private FirewallService(boolean enabled, String defaultAction, Boolean logDefaultAction, Set<FirewallRule> firewallRules) {
      super(enabled);
      this.defaultAction = defaultAction;
      this.logDefaultAction = logDefaultAction;
      this.firewallRules = ImmutableSet.copyOf(firewallRules);
   }

   private FirewallService() {
      // for JAXB
   }

   @XmlElement(name = "DefaultAction")
   protected String defaultAction;
   @XmlElement(name = "LogDefaultAction")
   protected Boolean logDefaultAction;
   @XmlElement(name = "FirewallRule")
   protected Set<FirewallRule> firewallRules = Sets.newLinkedHashSet();

   /**
    * Gets the value of the defaultAction property.
    *
    * @return possible object is
    *         {@link String }
    */
   public String getDefaultAction() {
      return defaultAction;
   }

   /**
    * Gets the value of the logDefaultAction property.
    *
    * @return possible object is
    *         {@link Boolean }
    */
   public Boolean isLogDefaultAction() {
      return logDefaultAction;
   }

   /**
    * Gets the value of the firewallRule property.
    */
   public Set<FirewallRule> getFirewallRules() {
      return Collections.unmodifiableSet(this.firewallRules);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      FirewallService that = FirewallService.class.cast(o);
      return equal(defaultAction, that.defaultAction) &&
            equal(logDefaultAction, that.logDefaultAction) &&
            equal(firewallRules, that.firewallRules);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(defaultAction,
            logDefaultAction,
            firewallRules);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("")
            .add("defaultAction", defaultAction)
            .add("logDefaultAction", logDefaultAction)
            .add("firewallRules", firewallRules).toString();
   }

}
