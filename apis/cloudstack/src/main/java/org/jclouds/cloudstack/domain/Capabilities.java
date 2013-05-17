/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.cloudstack.domain;

import java.beans.ConstructorProperties;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Class Capabilities
 *
 * @author Adrian Cole
 */
public class Capabilities {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromCapabilities(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected String cloudStackVersion;
      protected boolean securityGroupsEnabled;
      protected boolean canShareTemplates;
      protected boolean firewallRuleUiEnabled;
      protected boolean supportELB;

      /**
       * @see Capabilities#getCloudStackVersion()
       */
      public T cloudStackVersion(String cloudStackVersion) {
         this.cloudStackVersion = cloudStackVersion;
         return self();
      }

      /**
       * @see Capabilities#isSecurityGroupsEnabled()
       */
      public T securityGroupsEnabled(boolean securityGroupsEnabled) {
         this.securityGroupsEnabled = securityGroupsEnabled;
         return self();
      }

      /**
       * @see Capabilities#canShareTemplates()
       */
      public T canShareTemplates(boolean canShareTemplates) {
         this.canShareTemplates = canShareTemplates;
         return self();
      }

      /**
       * @see Capabilities#isFirewallRuleUiEnabled()
       */
      public T firewallRuleUiEnabled(boolean firewallRuleUiEnabled) {
         this.firewallRuleUiEnabled = firewallRuleUiEnabled;
         return self();
      }

      /**
       * @see Capabilities#isSupportELB()
       */
      public T supportELB(boolean supportELB) {
         this.supportELB = supportELB;
         return self();
      }

      public Capabilities build() {
         return new Capabilities(cloudStackVersion, securityGroupsEnabled, canShareTemplates, firewallRuleUiEnabled, supportELB);
      }

      public T fromCapabilities(Capabilities in) {
         return this
               .cloudStackVersion(in.getCloudStackVersion())
               .securityGroupsEnabled(in.isSecurityGroupsEnabled())
               .canShareTemplates(in.canShareTemplates())
               .firewallRuleUiEnabled(in.isFirewallRuleUiEnabled())
               .supportELB(in.isSupportELB());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String cloudStackVersion;
   private final boolean securityGroupsEnabled;
   private final boolean canShareTemplates;
   private final boolean firewallRuleUiEnabled;
   private final boolean supportELB;

   @ConstructorProperties({
         "cloudstackversion", "securitygroupsenabled", "userpublictemplateenabled", "firewallRuleUiEnabled", "supportELB"
   })
   protected Capabilities(@Nullable String cloudStackVersion, boolean securityGroupsEnabled, boolean canShareTemplates,
                          boolean firewallRuleUiEnabled, boolean supportELB) {
      this.cloudStackVersion = cloudStackVersion;
      this.securityGroupsEnabled = securityGroupsEnabled;
      this.canShareTemplates = canShareTemplates;
      this.firewallRuleUiEnabled = firewallRuleUiEnabled;
      this.supportELB = supportELB;
   }

   /**
    * @return version of the cloud stack
    */
   @Nullable
   public String getCloudStackVersion() {
      return this.cloudStackVersion;
   }

   /**
    * @return true if security groups support is enabled, false otherwise
    */
   public boolean isSecurityGroupsEnabled() {
      return this.securityGroupsEnabled;
   }

   public boolean canShareTemplates() {
      return this.canShareTemplates;
   }

   /**
    * @return true if the firewall rule UI is enabled
    */
   public boolean isFirewallRuleUiEnabled() {
      return this.firewallRuleUiEnabled;
   }

   /**
    * @return true if region supports elastic load balancer on basic zones
    */
   public boolean isSupportELB() {
      return this.supportELB;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(cloudStackVersion, securityGroupsEnabled, canShareTemplates, firewallRuleUiEnabled, supportELB);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Capabilities that = Capabilities.class.cast(obj);
      return Objects.equal(this.cloudStackVersion, that.cloudStackVersion)
            && Objects.equal(this.securityGroupsEnabled, that.securityGroupsEnabled)
            && Objects.equal(this.canShareTemplates, that.canShareTemplates)
            && Objects.equal(this.firewallRuleUiEnabled, that.firewallRuleUiEnabled)
            && Objects.equal(this.supportELB, that.supportELB);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("cloudStackVersion", cloudStackVersion).add("securityGroupsEnabled", securityGroupsEnabled).add("canShareTemplates", canShareTemplates).add("firewallRuleUiEnabled", firewallRuleUiEnabled).add("supportELB", supportELB);
   }

   @Override
   public String toString() {
      return string().toString();
   }

}
