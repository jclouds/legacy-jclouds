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
package org.jclouds.cloudstack.domain;

import com.google.common.base.Objects;
import com.google.gson.annotations.SerializedName;

/**
 * @author Adrian Cole
 */
public class Capabilities {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private String cloudStackVersion;
      private boolean securityGroupsEnabled;
      private boolean canShareTemplates;
      private boolean firewallRuleUiEnabled;
      private boolean supportELB;

      public Builder cloudStackVersion(String cloudStackVersion) {
         this.cloudStackVersion = cloudStackVersion;
         return this;
      }

      public Builder securityGroupsEnabled(boolean securityGroupsEnabled) {
         this.securityGroupsEnabled = securityGroupsEnabled;
         return this;
      }

      public Builder sharedTemplatesEnabled(boolean canShareTemplates) {
         this.canShareTemplates = canShareTemplates;
         return this;
      }

      public Builder firewallRuleUiEnabled(boolean firewallRuleUiEnabled) {
         this.firewallRuleUiEnabled = firewallRuleUiEnabled;
         return this;
      }

      public Builder supportELB(boolean supportELB) {
         this.supportELB = supportELB;
         return this;
      }

      public Capabilities build() {
         return new Capabilities(cloudStackVersion, securityGroupsEnabled, canShareTemplates, firewallRuleUiEnabled, supportELB);
      }
   }

   @SerializedName("cloudstackversion")
   private String cloudStackVersion;
   @SerializedName("securitygroupsenabled")
   private boolean securityGroupsEnabled;
   @SerializedName("userpublictemplateenabled")
   private boolean canShareTemplates;
   private boolean firewallRuleUiEnabled;
   private boolean supportELB;


   /**
    * present only for serializer
    */
   Capabilities() {

   }

   public Capabilities(String cloudStackVersion, boolean securityGroupsEnabled, boolean canShareTemplates,
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
   public String getCloudStackVersion() {
      return cloudStackVersion;
   }

   /**
    * @return true if security groups support is enabled, false otherwise
    */
   public boolean isSecurityGroupsEnabled() {
      return securityGroupsEnabled;
   }

   /**
    * @return true if user and domain admins can set templates to be shared,
    *         false otherwise
    */
   public boolean isSharedTemplatesEnabled() {
      return canShareTemplates;
   }

   /**
    * @return true if the firewall rule UI is enabled
    */
   public boolean isFirewallRuleUiEnabled() {
      return firewallRuleUiEnabled;
   }

   /**
    * @return true if region supports elastic load balancer on basic zones
    */
   public boolean isSupportELB() {
      return supportELB;
   }

   @Override
   public int hashCode() {
       return Objects.hashCode(canShareTemplates, cloudStackVersion, securityGroupsEnabled, firewallRuleUiEnabled, supportELB);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      Capabilities that = (Capabilities) obj;

      if (!Objects.equal(canShareTemplates, that.canShareTemplates)) return false;
      if (!Objects.equal(cloudStackVersion, that.cloudStackVersion)) return false;
      if (!Objects.equal(securityGroupsEnabled, that.securityGroupsEnabled)) return false;
      if (!Objects.equal(firewallRuleUiEnabled, that.firewallRuleUiEnabled)) return false;
      if (!Objects.equal(supportELB, that.supportELB)) return false;

      return true;
   }

   @Override
   public String toString() {
      return "Capabilities{" +
            "cloudStackVersion='" + cloudStackVersion + '\'' +
            ", securityGroupsEnabled=" + securityGroupsEnabled +
            ", canShareTemplates=" + canShareTemplates +
            ", firewallRuleUiEnabled=" + firewallRuleUiEnabled +
            ", supportELB=" + supportELB +
            '}';
   }
}
