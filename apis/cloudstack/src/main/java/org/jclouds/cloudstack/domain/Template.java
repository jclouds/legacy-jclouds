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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Date;

import javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.gson.annotations.SerializedName;

/**
 * @author Adrian Cole
 */
public class Template implements Comparable<Template> {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private String id;
      private String displayText;
      private String domain;
      private String domainId;
      private String account;
      private String accountId;
      private String zone;
      private String zoneId;
      private String OSType;
      private String OSTypeId;
      private String name;
      private Type type;
      private String status;
      private Format format;
      private String hypervisor;
      private Long size;
      private Date created;
      private Date removed;
      private boolean crossZones;
      private boolean bootable;
      private boolean extractable;
      private boolean featured;
      private boolean isPublic;
      private boolean ready;
      private boolean passwordEnabled;
      private String jobId;
      private String jobStatus;
      private String checksum;
      private String hostId;
      private String hostName;
      private String sourceTemplateId;
      private String templateTag;

      public Builder id(String id) {
         this.id = id;
         return this;
      }

      public Builder displayText(String displayText) {
         this.displayText = displayText;
         return this;
      }

      public Builder domain(String domain) {
         this.domain = domain;
         return this;
      }

      public Builder domainId(String domainId) {
         this.domainId = domainId;
         return this;
      }

      public Builder account(String account) {
         this.account = account;
         return this;
      }

      public Builder accountId(String accountId) {
         this.accountId = accountId;
         return this;
      }

      public Builder zone(String zone) {
         this.zone = zone;
         return this;
      }

      public Builder zoneId(String zoneId) {
         this.zoneId = zoneId;
         return this;
      }

      public Builder OSType(String OSType) {
         this.OSType = OSType;
         return this;
      }

      public Builder OSTypeId(String OSTypeId) {
         this.OSTypeId = OSTypeId;
         return this;
      }

      public Builder name(String name) {
         this.name = name;
         return this;
      }

      public Builder type(Type type) {
         this.type = type;
         return this;
      }

      public Builder status(String status) {
         this.status = status;
         return this;
      }

      public Builder format(Format format) {
         this.format = format;
         return this;
      }

      public Builder hypervisor(String hypervisor) {
         this.hypervisor = hypervisor;
         return this;
      }

      public Builder size(Long size) {
         this.size = size;
         return this;
      }

      public Builder created(Date created) {
         this.created = created;
         return this;
      }

      public Builder removed(Date removed) {
         this.removed = removed;
         return this;
      }

      public Builder crossZones(boolean crossZones) {
         this.crossZones = crossZones;
         return this;
      }

      public Builder bootable(boolean bootable) {
         this.bootable = bootable;
         return this;
      }

      public Builder extractable(boolean extractable) {
         this.extractable = extractable;
         return this;
      }

      public Builder featured(boolean featured) {
         this.featured = featured;
         return this;
      }

      public Builder isPublic(boolean isPublic) {
         this.isPublic = isPublic;
         return this;
      }

      public Builder ready(boolean ready) {
         this.ready = ready;
         return this;
      }

      public Builder passwordEnabled(boolean passwordEnabled) {
         this.passwordEnabled = passwordEnabled;
         return this;
      }

      public Builder jobId(String jobId) {
         this.jobId = jobId;
         return this;
      }

      public Builder jobStatus(String jobStatus) {
         this.jobStatus = jobStatus;
         return this;
      }

      public Builder checksum(String checksum) {
         this.checksum = checksum;
         return this;
      }

      public Builder hostid(String hostid) {
         this.hostId = hostid;
         return this;
      }

      public Builder hostName(String hostName) {
         this.hostName = hostName;
         return this;
      }

      public Builder sourceTemplateId(String sourceTemplateId) {
         this.sourceTemplateId = sourceTemplateId;
         return this;
      }

      public Builder templateTag(String templateTag) {
         this.templateTag = templateTag;
         return this;
      }


      public Template build() {
         return new Template(id, displayText, domain, domainId, account, accountId, zone, zoneId, OSType, OSTypeId,
               name, type, status, format, hypervisor, size, created, removed, crossZones, bootable, extractable,
               featured, isPublic, ready, passwordEnabled, jobId, jobStatus, checksum, hostId, hostName, sourceTemplateId,
               templateTag);
      }

   }

   public enum Type {

      USER, BUILTIN, UNRECOGNIZED;

      //TODO do we need camel case routines (e.g. see enums in VirtualMachine) ?
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
   //TODO: this should be a type
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
   //TODO: this should be a type
   private String jobStatus;
   private String checksum;
   @SerializedName("hostId")
   private String hostId;
   @SerializedName("hostname")
   private String hostName;
   @SerializedName("sourcetemplateid")
   @Nullable
   private String sourceTemplateId;
   @SerializedName("templatetag")
   private String templateTag;


   public Template(String id, String displayText, String domain, String domainId, String account, String accountId,
                   String zone, String zoneId, String oSType, String oSTypeId, String name, Type type, String status, Format format,
                   String hypervisor, Long size, Date created, Date removed, boolean crossZones, boolean bootable,
                   boolean extractable, boolean featured, boolean ispublic, boolean ready, boolean passwordEnabled, String jobId,
                   String jobStatus, String checksum, String hostId, String hostName, String sourceTemplateId,
                   String templateTag) {
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
      this.checksum = checksum;
      this.hostId = hostId;
      this.hostName = hostName;
      this.sourceTemplateId = sourceTemplateId;
      this.templateTag = templateTag;

   }

   /**
    * present only for serializer
    */
   Template() {

   }

   /**
    * @return Template id
    */
   public String getId() {
      return id;
   }

   /**
    * @return the display text of the template
    */
   public String getDisplayText() {
      return displayText;
   }

   /**
    * @return the name of the domain to which the template beLongs
    */
   public String getDomain() {
      return domain;
   }

   /**
    * @return the ID of the domain to which the template beLongs
    */
   public String getDomainId() {
      return domainId;
   }

   /**
    * @return the name of the account to which the template beLongs
    */
   public String getAccount() {
      return account;
   }

   /**
    * @return the ID of the account to which the template beLongs
    */
   public String getAccountId() {
      return accountId;
   }

   /**
    * @return the name of the zone to which the template beLongs
    */
   public String getZone() {
      return zone;
   }

   /**
    * @return the ID of the zone to which the template beLongs
    */
   public String getZoneId() {
      return zoneId;
   }

   /**
    * @return the name of the OS type to which the template beLongs
    */
   public String getOSType() {
      return OSType;
   }

   /**
    * @return the ID of the OS type to which the template beLongs
    */
   public String getOSTypeId() {
      return OSTypeId;
   }

   /**
    * @return Template name
    */
   public String getName() {
      return name;
   }

   /**
    * @return
    */
   public String getStatus() {
      return status;
   }

   /**
    * @return the format of the template.
    */
   public Format getFormat() {
      return format;
   }

   /**
    * @return the hypervisor on which the template runs
    */
   public String getHypervisor() {
      return hypervisor;
   }

   /**
    * @return the size of the template in kilobytes
    */
   public Long getSize() {
      return size;
   }

   /**
    * @return the type of the template
    */
   public Type getType() {
      return type;
   }

   /**
    * @return the date this template was created
    */
   public Date getCreated() {
      return created;
   }

   /**
    * @return the date this template was removed
    */
   public Date getRemoved() {
      return removed;
   }

   /**
    * @return true if the template is managed across all Zones, false otherwise
    */
   public boolean isCrossZones() {
      return crossZones;
   }

   /**
    * @return true if the ISO is bootable, false otherwise
    */
   public boolean isBootable() {
      return bootable;
   }

   /**
    * @return true if the template is extractable, false otherwise
    */
   public boolean isExtractable() {
      return extractable;
   }

   /**
    * @return true if this template is a featured template, false otherwise
    */
   public boolean isFeatured() {
      return featured;
   }

   /**
    * @return true if this template is a public template, false otherwise
    */
   public boolean isPublic() {
      return ispublic;
   }

   /**
    * @return true if the template is ready to be deployed from, false otherwise
    */
   public boolean isReady() {
      return ready;
   }

   /**
    * @return true if the reset password feature is enabled, false otherwise
    */
   public boolean isPasswordEnabled() {
      return passwordEnabled;
   }

   /**
    * @return shows the current pending asynchronous job ID, or null if current
    *         pending jobs are acting on the template
    */
   @Nullable
   public String getJobId() {
      return jobId;
   }

   /**
    * @return shows the current pending asynchronous job status
    */
   public String getJobStatus() {
      return jobStatus;
   }

   /**
    * @return checksum of the template
    */
   public String getChecksum() {
      return checksum;
   }

   /**
    * @return the ID of the secondary storage host for the template
    */
   public String getHostId() {
      return hostId;
   }

   /**
    * @return the name of the secondary storage host for the template
    */
   public String getHostName() {
      return hostName;
   }

   /**
    * @return the template ID of the parent template if present
    */
   public String getSourceTemplateId() {
      return sourceTemplateId;
   }

   /**
    * @return the tag of this template
    */
   public String getTemplateTag() {
      return templateTag;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      Template that = (Template) o;

      if (!Objects.equal(id, that.id)) return false;
      if (!Objects.equal(displayText, that.displayText)) return false;
      if (!Objects.equal(domain, that.domain)) return false;
      if (!Objects.equal(domainId, that.domainId)) return false;
      if (!Objects.equal(account, that.account)) return false;
      if (!Objects.equal(accountId, that.accountId)) return false;
      if (!Objects.equal(zone, that.zone)) return false;
      if (!Objects.equal(zoneId, that.zoneId)) return false;
      if (!Objects.equal(OSType, that.OSType)) return false;
      if (!Objects.equal(OSTypeId, that.OSTypeId)) return false;
      if (!Objects.equal(name, that.name)) return false;
      if (!Objects.equal(type, that.type)) return false;
      if (!Objects.equal(status, that.status)) return false;
      if (!Objects.equal(format, that.format)) return false;
      if (!Objects.equal(hypervisor, that.hypervisor)) return false;
      if (!Objects.equal(size, that.size)) return false;
      if (!Objects.equal(created, that.created)) return false;
      if (!Objects.equal(removed, that.removed)) return false;
      if (!Objects.equal(crossZones, that.crossZones)) return false;
      if (!Objects.equal(bootable, that.bootable)) return false;
      if (!Objects.equal(extractable, that.extractable)) return false;
      if (!Objects.equal(featured, that.featured)) return false;
      if (!Objects.equal(ispublic, that.ispublic)) return false;
      if (!Objects.equal(ready, that.ready)) return false;
      if (!Objects.equal(passwordEnabled, that.passwordEnabled)) return false;
      if (!Objects.equal(jobId, that.jobId)) return false;
      if (!Objects.equal(jobStatus, that.jobStatus)) return false;
      if (!Objects.equal(checksum, that.checksum)) return false;
      if (!Objects.equal(hostId, that.hostId)) return false;
      if (!Objects.equal(hostName, that.hostName)) return false;
      if (!Objects.equal(sourceTemplateId, that.sourceTemplateId)) return false;
      if (!Objects.equal(templateTag, that.templateTag)) return false;

      return true;
   }

   @Override
   public int hashCode() {
       return Objects.hashCode(id, displayText, domain, domainId, account, accountId,
                               zone, zoneId, OSType, OSTypeId, name, type, status, format,
                               hypervisor, size, created, removed, crossZones, bootable,
                               extractable, featured, ispublic, ready, passwordEnabled,
                               jobId, jobStatus, checksum, hostId, hostName,
                               sourceTemplateId, templateTag);
   }

   @Override
   public String toString() {
      return "Template{" +
            "id=" + id +
            ", displayText='" + displayText + '\'' +
            ", domain='" + domain + '\'' +
            ", domainId=" + domainId +
            ", account='" + account + '\'' +
            ", accountId=" + accountId +
            ", zone='" + zone + '\'' +
            ", zoneId=" + zoneId +
            ", OSType='" + OSType + '\'' +
            ", OSTypeId=" + OSTypeId +
            ", name='" + name + '\'' +
            ", type=" + type +
            ", status='" + status + '\'' +
            ", format=" + format +
            ", hypervisor='" + hypervisor + '\'' +
            ", size=" + size +
            ", created=" + created +
            ", removed=" + removed +
            ", crossZones=" + crossZones +
            ", bootable=" + bootable +
            ", extractable=" + extractable +
            ", featured=" + featured +
            ", ispublic=" + ispublic +
            ", ready=" + ready +
            ", passwordEnabled=" + passwordEnabled +
            ", jobId=" + jobId +
            ", jobStatus='" + jobStatus + '\'' +
            ", checksum='" + checksum + '\'' +
            ", hostId=" + hostId +
            ", hostName='" + hostName + '\'' +
            ", sourceTemplateId=" + sourceTemplateId +
            ", templateTag='" + templateTag + '\'' +
            '}';
   }

   @Override
   public int compareTo(Template arg0) {
      return id.compareTo(arg0.getId());
   }
}
