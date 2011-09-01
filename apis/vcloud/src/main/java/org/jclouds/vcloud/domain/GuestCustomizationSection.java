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
package org.jclouds.vcloud.domain;

import java.net.URI;

import org.jclouds.vcloud.VCloudMediaType;

/**
 * The GuestCustomization of a Vm contains customization parameters for the guest operating system
 * of the virtual machine.
 */
public class GuestCustomizationSection {
   protected final String type;
   protected final URI href;
   protected String info;
   protected Boolean enabled;
   protected Boolean changeSid;
   protected String virtualMachineId;
   protected Boolean joinDomainEnabled;
   protected Boolean useOrgSettings;
   protected String domainName;
   protected String domainUserName;
   protected String domainUserPassword;
   protected Boolean adminPasswordEnabled;
   protected Boolean adminPasswordAuto;
   protected String adminPassword;
   protected Boolean resetPasswordRequired;
   protected String customizationScript;
   protected String computerName;
   protected final ReferenceType edit;

   public GuestCustomizationSection(URI href) {
      this.href = href;
      this.type = VCloudMediaType.GUESTCUSTOMIZATIONSECTION_XML;
      this.info = "Specifies Guest OS Customization Settings";
      this.edit = null;
   }

   public GuestCustomizationSection(String type, URI href, String info, Boolean enabled, Boolean changeSid,
            String virtualMachineId, Boolean joinDomainEnabled, Boolean useOrgSettings, String domainName,
            String domainUserName, String domainUserPassword, Boolean adminPasswordEnabled, Boolean adminPasswordAuto,
            String adminPassword, Boolean resetPasswordRequired, String customizationScript, String computerName,
            ReferenceType edit) {
      this.type = type;
      this.href = href;
      this.info = info;
      this.enabled = enabled;
      this.changeSid = changeSid;
      this.virtualMachineId = virtualMachineId;
      this.joinDomainEnabled = joinDomainEnabled;
      this.useOrgSettings = useOrgSettings;
      this.domainName = domainName;
      this.domainUserName = domainUserName;
      this.domainUserPassword = domainUserPassword;
      this.adminPasswordEnabled = adminPasswordEnabled;
      this.adminPasswordAuto = adminPasswordAuto;
      this.adminPassword = adminPassword;
      this.resetPasswordRequired = resetPasswordRequired;
      this.customizationScript = customizationScript;
      this.computerName = computerName;
      this.edit = edit;
   }

   /**
    * 
    * @return media type of this section
    */
   public String getType() {
      return type;
   }

   /**
    * 
    * @return URL to access this section
    */
   public URI getHref() {
      return href;
   }

   /**
    * 
    * @return
    */
   public String getInfo() {
      return info;
   }

   /**
    * 
    * @return if true, to enable guest customization at power on
    */
   public Boolean isEnabled() {
      return enabled;
   }

   /**
    * 
    * @return if true, customization will run sysprep to change the Windows SID for this virtual
    *         machine
    */
   public Boolean shouldChangeSid() {
      return changeSid;
   }

   /**
    * 
    * @return unique identifier for this virtual machine
    */
   public String getVirtualMachineId() {
      return virtualMachineId;
   }

   /**
    * 
    * @return if true, this virtual machine can join a Windows domain
    */
   public Boolean isJoinDomainEnabled() {
      return joinDomainEnabled;
   }

   /**
    * 
    * @return if true, this virtual machine uses the containing organization’s default values for
    *         Windows domain name, domain user name, and domain user password
    */
   public Boolean useOrgSettings() {
      return useOrgSettings;
   }

   /**
    * 
    * @return if UseOrgSettings is false, specifies the Windows domain to join
    */
   public String getDomainName() {
      return domainName;
   }

   /**
    * 
    * @return if UseOrgSettings is false, specifies the Windows domain user name
    */
   public String getDomainUserName() {
      return domainUserName;
   }

   /**
    * 
    * @return if UseOrgSettings is false, specifies the Windows domain user’s password
    */
   public String getDomainUserPassword() {
      return domainUserPassword;
   }

   /**
    * 
    * @return true if the guest OS allows use of a local administrator password
    */
   public Boolean isAdminPasswordEnabled() {
      return adminPasswordEnabled;
   }

   /**
    * 
    * @return true if the local administrator password should be automatically generated
    */
   public Boolean isAdminPasswordAuto() {
      return adminPasswordAuto;
   }

   /**
    * 
    * @return local administrator password for this virtual machine
    */
   public String getAdminPassword() {
      return adminPassword;
   }

   /**
    * 
    * @return if true, the local administrator must reset his password on first use
    */
   public Boolean isResetPasswordRequired() {
      return resetPasswordRequired;
   }

   /**
    * 
    * @return the customization script to run
    */
   public String getCustomizationScript() {
      return customizationScript;
   }

   /**
    * 
    * @return name of this virtual machine in DNS or Windows domain
    */
   public String getComputerName() {
      return computerName;
   }

   /**
    * 
    * @return edit link
    */
   public ReferenceType getEdit() {
      return edit;
   }

   @Override
   public String toString() {
      return "[href=" + getHref() + ", type=" + getType() + ", info=" + getInfo() + ", enabled=" + isEnabled() + "]";
   }

   @Override
   public int hashCode() {
      int prime = 31;
      int result = 1;
      result = prime * result + ((adminPassword == null) ? 0 : adminPassword.hashCode());
      result = prime * result + ((adminPasswordAuto == null) ? 0 : adminPasswordAuto.hashCode());
      result = prime * result + ((adminPasswordEnabled == null) ? 0 : adminPasswordEnabled.hashCode());
      result = prime * result + ((changeSid == null) ? 0 : changeSid.hashCode());
      result = prime * result + ((computerName == null) ? 0 : computerName.hashCode());
      result = prime * result + ((customizationScript == null) ? 0 : customizationScript.hashCode());
      result = prime * result + ((domainName == null) ? 0 : domainName.hashCode());
      result = prime * result + ((domainUserName == null) ? 0 : domainUserName.hashCode());
      result = prime * result + ((domainUserPassword == null) ? 0 : domainUserPassword.hashCode());
      result = prime * result + ((edit == null) ? 0 : edit.hashCode());
      result = prime * result + ((enabled == null) ? 0 : enabled.hashCode());
      result = prime * result + ((href == null) ? 0 : href.hashCode());
      result = prime * result + ((info == null) ? 0 : info.hashCode());
      result = prime * result + ((joinDomainEnabled == null) ? 0 : joinDomainEnabled.hashCode());
      result = prime * result + ((resetPasswordRequired == null) ? 0 : resetPasswordRequired.hashCode());
      result = prime * result + ((type == null) ? 0 : type.hashCode());
      result = prime * result + ((useOrgSettings == null) ? 0 : useOrgSettings.hashCode());
      result = prime * result + ((virtualMachineId == null) ? 0 : virtualMachineId.hashCode());
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
      GuestCustomizationSection other = (GuestCustomizationSection) obj;
      if (adminPassword == null) {
         if (other.adminPassword != null)
            return false;
      } else if (!adminPassword.equals(other.adminPassword))
         return false;
      if (adminPasswordAuto == null) {
         if (other.adminPasswordAuto != null)
            return false;
      } else if (!adminPasswordAuto.equals(other.adminPasswordAuto))
         return false;
      if (adminPasswordEnabled == null) {
         if (other.adminPasswordEnabled != null)
            return false;
      } else if (!adminPasswordEnabled.equals(other.adminPasswordEnabled))
         return false;
      if (changeSid == null) {
         if (other.changeSid != null)
            return false;
      } else if (!changeSid.equals(other.changeSid))
         return false;
      if (computerName == null) {
         if (other.computerName != null)
            return false;
      } else if (!computerName.equals(other.computerName))
         return false;
      if (customizationScript == null) {
         if (other.customizationScript != null)
            return false;
      } else if (!customizationScript.equals(other.customizationScript))
         return false;
      if (domainName == null) {
         if (other.domainName != null)
            return false;
      } else if (!domainName.equals(other.domainName))
         return false;
      if (domainUserName == null) {
         if (other.domainUserName != null)
            return false;
      } else if (!domainUserName.equals(other.domainUserName))
         return false;
      if (domainUserPassword == null) {
         if (other.domainUserPassword != null)
            return false;
      } else if (!domainUserPassword.equals(other.domainUserPassword))
         return false;
      if (edit == null) {
         if (other.edit != null)
            return false;
      } else if (!edit.equals(other.edit))
         return false;
      if (enabled == null) {
         if (other.enabled != null)
            return false;
      } else if (!enabled.equals(other.enabled))
         return false;
      if (href == null) {
         if (other.href != null)
            return false;
      } else if (!href.equals(other.href))
         return false;
      if (info == null) {
         if (other.info != null)
            return false;
      } else if (!info.equals(other.info))
         return false;
      if (joinDomainEnabled == null) {
         if (other.joinDomainEnabled != null)
            return false;
      } else if (!joinDomainEnabled.equals(other.joinDomainEnabled))
         return false;
      if (resetPasswordRequired == null) {
         if (other.resetPasswordRequired != null)
            return false;
      } else if (!resetPasswordRequired.equals(other.resetPasswordRequired))
         return false;
      if (type == null) {
         if (other.type != null)
            return false;
      } else if (!type.equals(other.type))
         return false;
      if (useOrgSettings == null) {
         if (other.useOrgSettings != null)
            return false;
      } else if (!useOrgSettings.equals(other.useOrgSettings))
         return false;
      if (virtualMachineId == null) {
         if (other.virtualMachineId != null)
            return false;
      } else if (!virtualMachineId.equals(other.virtualMachineId))
         return false;
      return true;
   }

   public void setEnabled(Boolean enabled) {
      this.enabled = enabled;
   }

   public Boolean getChangeSid() {
      return changeSid;
   }

   public void setChangeSid(Boolean changeSid) {
      this.changeSid = changeSid;
   }

   public Boolean getJoinDomainEnabled() {
      return joinDomainEnabled;
   }

   public void setJoinDomainEnabled(Boolean joinDomainEnabled) {
      this.joinDomainEnabled = joinDomainEnabled;
   }

   public Boolean shouldUseOrgSettings() {
      return useOrgSettings;
   }

   public void setUseOrgSettings(Boolean useOrgSettings) {
      this.useOrgSettings = useOrgSettings;
   }

   public Boolean getAdminPasswordEnabled() {
      return adminPasswordEnabled;
   }

   public void setAdminPasswordEnabled(Boolean adminPasswordEnabled) {
      this.adminPasswordEnabled = adminPasswordEnabled;
   }

   public Boolean getAdminPasswordAuto() {
      return adminPasswordAuto;
   }

   public void setAdminPasswordAuto(Boolean adminPasswordAuto) {
      this.adminPasswordAuto = adminPasswordAuto;
   }

   public Boolean getResetPasswordRequired() {
      return resetPasswordRequired;
   }

   public void setResetPasswordRequired(Boolean resetPasswordRequired) {
      this.resetPasswordRequired = resetPasswordRequired;
   }

   public void setInfo(String info) {
      this.info = info;
   }

   public void setVirtualMachineId(String virtualMachineId) {
      this.virtualMachineId = virtualMachineId;
   }

   public void setDomainName(String domainName) {
      this.domainName = domainName;
   }

   public void setDomainUserName(String domainUserName) {
      this.domainUserName = domainUserName;
   }

   public void setDomainUserPassword(String domainUserPassword) {
      this.domainUserPassword = domainUserPassword;
   }

   public void setAdminPassword(String adminPassword) {
      this.adminPassword = adminPassword;
   }

   public void setCustomizationScript(String customizationScript) {
      this.customizationScript = customizationScript;
   }

   public void setComputerName(String computerName) {
      this.computerName = computerName;
   }
}