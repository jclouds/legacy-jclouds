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

import java.util.Date;
import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Adrian Cole
 */
public class Template {

   public enum Type {

      USER, BUILTIN, UNRECOGNIZED;

      public static Type fromValue(String type) {
         try {
            return valueOf(checkNotNull(type, "type"));
         } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }
   }

   public enum Format {

      VHD, QCOW2, OVA, UNRECOGNIZED;

      public static Format fromValue(String format) {
         try {
            return valueOf(checkNotNull(format, "format"));
         } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }
   }

   private String id;
   @SerializedName("displaytext")
   private String displayText;
   private String domain;
   @SerializedName("domainid")
   private String domainId;
   private String account;
   @SerializedName("accountid")
   private String accountId;
   @SerializedName("zonename")
   private String zone;
   @SerializedName("zoneid")
   private String zoneId;
   @SerializedName("ostypename")
   private String OSType;
   @SerializedName("ostypeid")
   private String OSTypeId;
   private String name;
   @SerializedName("templatetype")
   private Type type;
   private String status;
   private Format format;
   private String hypervisor;
   private Long size;
   private Date created;
   private Date removed;
   @SerializedName("crossZones")
   private boolean crossZones;
   @SerializedName("bootable")
   private boolean bootable;
   @SerializedName("isextractable")
   private boolean extractable;
   @SerializedName("isfeatured")
   private boolean featured;
   @SerializedName("ispublic")
   private boolean ispublic;
   @SerializedName("isready")
   private boolean ready;
   @SerializedName("passwordenabled")
   private boolean passwordEnabled;
   @Nullable
   @SerializedName("jobid")
   private String jobId;
   @SerializedName("jobstatus")
   private String jobStatus;

   public Template(String id, String displayText, String domain, String domainId, String account, String accountId,
            String zone, String zoneId, String oSType, String oSTypeId, String name, Type type, String status,
            Format format, String hypervisor, Long size, Date created, Date removed, boolean crossZones,
            boolean bootable, boolean extractable, boolean featured, boolean ispublic, boolean ready,
            boolean passwordEnabled, String jobId, String jobStatus) {
      this.id = id;
      this.displayText = displayText;
      this.domain = domain;
      this.domainId = domainId;
      this.account = account;
      this.accountId = accountId;
      this.zone = zone;
      this.zoneId = zoneId;
      this.OSType = oSType;
      this.OSTypeId = oSTypeId;
      this.name = name;
      this.type = type;
      this.status = status;
      this.format = format;
      this.hypervisor = hypervisor;
      this.size = size;
      this.created = created;
      this.removed = removed;
      this.crossZones = crossZones;
      this.bootable = bootable;
      this.extractable = extractable;
      this.featured = featured;
      this.ispublic = ispublic;
      this.ready = ready;
      this.passwordEnabled = passwordEnabled;
      this.jobId = jobId;
      this.jobStatus = jobStatus;
   }

   /**
    * present only for serializer
    * 
    */
   Template() {

   }

   /**
    * 
    * @return Template id
    */
   public String getId() {
      return id;
   }

   /**
    * 
    * @return the display text of the template
    */
   public String getDisplayText() {
      return displayText;
   }

   /**
    * 
    * @return the name of the domain to which the template beLongs
    */
   public String getDomain() {
      return domain;
   }

   /**
    * 
    * @return the ID of the domain to which the template beLongs
    */
   public String getDomainId() {
      return domainId;
   }

   /**
    * 
    * @return the name of the account to which the template beLongs
    */
   public String getAccount() {
      return account;
   }

   /**
    * 
    * @return the ID of the account to which the template beLongs
    */
   public String getAccountId() {
      return accountId;
   }

   /**
    * 
    * @return the name of the zone to which the template beLongs
    */
   public String getZone() {
      return zone;
   }

   /**
    * 
    * @return the ID of the zone to which the template beLongs
    */
   public String getZoneId() {
      return zoneId;
   }

   /**
    * 
    * @return the name of the OS type to which the template beLongs
    */
   public String getOSType() {
      return OSType;
   }

   /**
    * 
    * @return the ID of the OS type to which the template beLongs
    */
   public String getOSTypeId() {
      return OSTypeId;
   }

   /**
    * 
    * @return Template name
    */
   public String getName() {
      return name;
   }

   /**
    * 
    * @return
    */
   public String getStatus() {
      return status;
   }

   /**
    * 
    * @return the format of the template.
    */
   public Format getFormat() {
      return format;
   }

   /**
    * 
    * @return the hypervisor on which the template runs
    */
   public String getHypervisor() {
      return hypervisor;
   }

   /**
    * 
    * @return the size of the template in kilobytes
    */
   public Long getSize() {
      return size;
   }

   /**
    * 
    * @return the type of the template
    */
   public Type getType() {
      return type;
   }

   /**
    * 
    * @return the date this template was created
    */
   public Date getCreated() {
      return created;
   }

   /**
    * 
    * @return the date this template was removed
    */
   public Date getRemoved() {
      return removed;
   }

   /**
    * 
    * @return true if the template is managed across all Zones, false otherwise
    */
   public boolean isCrossZones() {
      return crossZones;
   }

   /**
    * 
    * @return true if the ISO is bootable, false otherwise
    */
   public boolean isBootable() {
      return bootable;
   }

   /**
    * 
    * @return true if the template is extractable, false otherwise
    */
   public boolean isExtractable() {
      return extractable;
   }

   /**
    * 
    * @return true if this template is a featured template, false otherwise
    */
   public boolean isFeatured() {
      return featured;
   }

   /**
    * 
    * @return true if this template is a public template, false otherwise
    */
   public boolean isPublic() {
      return ispublic;
   }

   /**
    * 
    * @return true if the template is ready to be deployed from, false otherwise
    */
   public boolean isReady() {
      return ready;
   }

   /**
    * 
    * @return true if the reset password feature is enabled, false otherwise
    */
   public boolean isPasswordEnabled() {
      return passwordEnabled;
   }

   /**
    * 
    * @return shows the current pending asynchronous job ID, or null if current pending jobs are
    *         acting on the template
    */
   @Nullable
   public String getJobId() {
      return jobId;
   }

   /**
    * 
    * @return shows the current pending asynchronous job status
    */
   public String getJobStatus() {
      return jobStatus;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((OSType == null) ? 0 : OSType.hashCode());
      result = prime * result + ((OSTypeId == null) ? 0 : OSTypeId.hashCode());
      result = prime * result + ((account == null) ? 0 : account.hashCode());
      result = prime * result + ((accountId == null) ? 0 : accountId.hashCode());
      result = prime * result + (bootable ? 1231 : 1237);
      result = prime * result + ((created == null) ? 0 : created.hashCode());
      result = prime * result + (crossZones ? 1231 : 1237);
      result = prime * result + ((displayText == null) ? 0 : displayText.hashCode());
      result = prime * result + ((domain == null) ? 0 : domain.hashCode());
      result = prime * result + ((domainId == null) ? 0 : domainId.hashCode());
      result = prime * result + (extractable ? 1231 : 1237);
      result = prime * result + (featured ? 1231 : 1237);
      result = prime * result + ((format == null) ? 0 : format.hashCode());
      result = prime * result + ((hypervisor == null) ? 0 : hypervisor.hashCode());
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      result = prime * result + (ispublic ? 1231 : 1237);
      result = prime * result + ((jobId == null) ? 0 : jobId.hashCode());
      result = prime * result + ((jobStatus == null) ? 0 : jobStatus.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + (passwordEnabled ? 1231 : 1237);
      result = prime * result + (ready ? 1231 : 1237);
      result = prime * result + ((removed == null) ? 0 : removed.hashCode());
      result = prime * result + ((size == null) ? 0 : size.hashCode());
      result = prime * result + ((status == null) ? 0 : status.hashCode());
      result = prime * result + ((type == null) ? 0 : type.hashCode());
      result = prime * result + ((zone == null) ? 0 : zone.hashCode());
      result = prime * result + ((zoneId == null) ? 0 : zoneId.hashCode());
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
      Template other = (Template) obj;
      if (OSType == null) {
         if (other.OSType != null)
            return false;
      } else if (!OSType.equals(other.OSType))
         return false;
      if (OSTypeId == null) {
         if (other.OSTypeId != null)
            return false;
      } else if (!OSTypeId.equals(other.OSTypeId))
         return false;
      if (account == null) {
         if (other.account != null)
            return false;
      } else if (!account.equals(other.account))
         return false;
      if (accountId == null) {
         if (other.accountId != null)
            return false;
      } else if (!accountId.equals(other.accountId))
         return false;
      if (bootable != other.bootable)
         return false;
      if (created == null) {
         if (other.created != null)
            return false;
      } else if (!created.equals(other.created))
         return false;
      if (crossZones != other.crossZones)
         return false;
      if (displayText == null) {
         if (other.displayText != null)
            return false;
      } else if (!displayText.equals(other.displayText))
         return false;
      if (domain == null) {
         if (other.domain != null)
            return false;
      } else if (!domain.equals(other.domain))
         return false;
      if (domainId == null) {
         if (other.domainId != null)
            return false;
      } else if (!domainId.equals(other.domainId))
         return false;
      if (extractable != other.extractable)
         return false;
      if (featured != other.featured)
         return false;
      if (format == null) {
         if (other.format != null)
            return false;
      } else if (!format.equals(other.format))
         return false;
      if (hypervisor == null) {
         if (other.hypervisor != null)
            return false;
      } else if (!hypervisor.equals(other.hypervisor))
         return false;
      if (id == null) {
         if (other.id != null)
            return false;
      } else if (!id.equals(other.id))
         return false;
      if (ispublic != other.ispublic)
         return false;
      if (jobId == null) {
         if (other.jobId != null)
            return false;
      } else if (!jobId.equals(other.jobId))
         return false;
      if (jobStatus == null) {
         if (other.jobStatus != null)
            return false;
      } else if (!jobStatus.equals(other.jobStatus))
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (passwordEnabled != other.passwordEnabled)
         return false;
      if (ready != other.ready)
         return false;
      if (removed == null) {
         if (other.removed != null)
            return false;
      } else if (!removed.equals(other.removed))
         return false;
      if (size == null) {
         if (other.size != null)
            return false;
      } else if (!size.equals(other.size))
         return false;
      if (status == null) {
         if (other.status != null)
            return false;
      } else if (!status.equals(other.status))
         return false;
      if (type == null) {
         if (other.type != null)
            return false;
      } else if (!type.equals(other.type))
         return false;
      if (zone == null) {
         if (other.zone != null)
            return false;
      } else if (!zone.equals(other.zone))
         return false;
      if (zoneId == null) {
         if (other.zoneId != null)
            return false;
      } else if (!zoneId.equals(other.zoneId))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[id=" + id + ", name=" + name + ", displayText=" + displayText + ", format=" + format + ", type=" + type
               + ", hypervisor=" + hypervisor + ", size=" + size + ", status=" + status + ", created=" + created
               + ", removed=" + removed + ", OSType=" + OSType + ", OSTypeId=" + OSTypeId + ", account=" + account
               + ", accountId=" + accountId + ", domain=" + domain + ", domainId=" + domainId + ", zone=" + zone
               + ", zoneId=" + zoneId + ", ready=" + ready + ", bootable=" + bootable + ", crossZones=" + crossZones
               + ", extractable=" + extractable + ", featured=" + featured + ", ispublic=" + ispublic
               + ", passwordEnabled=" + passwordEnabled + ", jobId=" + jobId + ", jobStatus=" + jobStatus + "]";
   }

}
