/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.cloudstack.domain;

import com.google.gson.annotations.SerializedName;

/**
 * 
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

      public Capabilities build() {
         return new Capabilities(cloudStackVersion, securityGroupsEnabled, canShareTemplates);
      }
   }

   @SerializedName("cloudstackversion")
   private String cloudStackVersion;
   @SerializedName("securitygroupsenabled")
   private boolean securityGroupsEnabled;
   @SerializedName("userpublictemplateenabled")
   private boolean canShareTemplates;

   /**
    * present only for serializer
    * 
    */
   Capabilities() {

   }

   public Capabilities(String cloudStackVersion, boolean securityGroupsEnabled, boolean canShareTemplates) {
      this.cloudStackVersion = cloudStackVersion;
      this.securityGroupsEnabled = securityGroupsEnabled;
      this.canShareTemplates = canShareTemplates;

   }

   /**
    * 
    * @return version of the cloud stack
    */
   public String getCloudStackVersion() {
      return cloudStackVersion;
   }

   /**
    * 
    * @return true if security groups support is enabled, false otherwise
    */
   public boolean isSecurityGroupsEnabled() {
      return securityGroupsEnabled;
   }

   /**
    * 
    * @return true if user and domain admins can set templates to be shared, false otherwise
    */
   public boolean isSharedTemplatesEnabled() {
      return canShareTemplates;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (canShareTemplates ? 1231 : 1237);
      result = prime * result + ((cloudStackVersion == null) ? 0 : cloudStackVersion.hashCode());
      result = prime * result + (securityGroupsEnabled ? 1231 : 1237);
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      Capabilities other = (Capabilities) obj;
      if (canShareTemplates != other.canShareTemplates)
         return false;
      if (cloudStackVersion == null) {
         if (other.cloudStackVersion != null)
            return false;
      } else if (!cloudStackVersion.equals(other.cloudStackVersion))
         return false;
      if (securityGroupsEnabled != other.securityGroupsEnabled)
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[cloudStackVersion=" + cloudStackVersion + ", canShareTemplates=" + canShareTemplates
               + ", securityGroupsEnabled=" + securityGroupsEnabled + "]";
   }
}
