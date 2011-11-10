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

import com.google.gson.annotations.SerializedName;

import java.util.Date;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Richard Downer
 */
public class ISO implements Comparable<ISO> {

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {

      private long id;
      private String account;
      private long accountId;
      private boolean bootable;
      private String checksum;
      private Date created;
      private boolean crossZones;
      private String displayText;
      private String domain;
      private long domainid;
      private String format;
      private long hostId;
      private String hostName;
      private String hypervisor;
      private boolean isExtractable;
      private boolean isFeatured;
      private boolean isPublic;
      private boolean isReady;
      private long jobId;
      private String jobStatus;
      private String name;
      private long osTypeId;
      private String osTypeName;
      private boolean passwordEnabled;
      private Date removed;
      private long size;
      private long sourceTemplateId;
      private String status;
      private String templateTag;
      private String templateType;
      private long zoneId;
      private String zoneName;

      /**
       * @param id the template ID
       */
      public Builder id(long id) {
         this.id = id;
         return this;
      }

      /**
       * @param account the account name to which the template belongs
       */
      public Builder account(String account) {
         this.account = account;
         return this;
      }

      /**
       * @param accountId the account id to which the template belongs
       */
      public Builder accountId(long accountId) {
         this.accountId = accountId;
         return this;
      }

      /**
       * @param bootable true if the ISO is bootable, false otherwise
       */
      public Builder bootable(boolean bootable) {
         this.bootable = bootable;
         return this;
      }

      /**
       * @param checksum checksum of the template
       */
      public Builder checksum(String checksum) {
         this.checksum = checksum;
         return this;
      }

      /**
       * @param created the date this template was created
       */
      public Builder created(Date created) {
         this.created = created;
         return this;
      }

      /**
       * @param crossZones true if the template is managed across all Zones, false otherwise
       */
      public Builder crossZones(boolean crossZones) {
         this.crossZones = crossZones;
         return this;
      }

      /**
       * @param displayText the template display text
       */
      public Builder displayText(String displayText) {
         this.displayText = displayText;
         return this;
      }

      /**
       * @param domain the name of the domain to which the template belongs
       */
      public Builder domain(String domain) {
         this.domain = domain;
         return this;
      }

      /**
       * @param domainid the ID of the domain to which the template belongs
       */
      public Builder domainid(long domainid) {
         this.domainid = domainid;
         return this;
      }

      /**
       * @param format the format of the template.
       */
      public Builder format(String format) {
         this.format = format;
         return this;
      }

      /**
       * @param hostId the ID of the secondary storage host for the template
       */
      public Builder hostId(long hostId) {
         this.hostId = hostId;
         return this;
      }

      /**
       * @param hostName the name of the secondary storage host for the template
       */
      public Builder hostName(String hostName) {
         this.hostName = hostName;
         return this;
      }

      /**
       * @param hypervisor the hypervisor on which the template runs
       */
      public Builder hypervisor(String hypervisor) {
         this.hypervisor = hypervisor;
         return this;
      }

      /**
       * @param isExtractable true if the template is extractable, false otherwise
       */
      public Builder isExtractable(boolean isExtractable) {
         this.isExtractable = isExtractable;
         return this;
      }

      /**
       * @param isFeatured true if this template is a featured template, false otherwise
       */
      public Builder isFeatured(boolean isFeatured) {
         this.isFeatured = isFeatured;
         return this;
      }

      /**
       * @param isPublic true if this template is a public template, false otherwise
       */
      public Builder isPublic(boolean isPublic) {
         this.isPublic = isPublic;
         return this;
      }

      /**
       * @param isReady true if the template is ready to be deployed from, false otherwise.
       */
      public Builder isReady(boolean isReady) {
         this.isReady = isReady;
         return this;
      }

      /**
       * @param jobId shows the current pending asynchronous job ID. This tag is not returned if no current pending jobs are acting on the template
       */
      public Builder jobId(long jobId) {
         this.jobId = jobId;
         return this;
      }

      /**
       * @param jobStatus shows the current pending asynchronous job status
       */
      public Builder jobStatus(String jobStatus) {
         this.jobStatus = jobStatus;
         return this;
      }

      /**
       * @param name the template name
       */
      public Builder name(String name) {
         this.name = name;
         return this;
      }

      /**
       * @param osTypeId the ID of the OS type for this template.
       */
      public Builder osTypeId(long osTypeId) {
         this.osTypeId = osTypeId;
         return this;
      }

      /**
       * @param osTypeName the name of the OS type for this template.
       */
      public Builder osTypeName(String osTypeName) {
         this.osTypeName = osTypeName;
         return this;
      }

      /**
       * @param passwordEnabled true if the reset password feature is enabled, false otherwise
       */
      public Builder passwordEnabled(boolean passwordEnabled) {
         this.passwordEnabled = passwordEnabled;
         return this;
      }

      /**
       * @param removed the date this template was removed
       */
      public Builder removed(Date removed) {
         this.removed = removed;
         return this;
      }

      /**
       * @param size the size of the template
       */
      public Builder size(long size) {
         this.size = size;
         return this;
      }

      /**
       * @param sourceTemplateId the template ID of the parent template if present
       */
      public Builder sourceTemplateId(long sourceTemplateId) {
         this.sourceTemplateId = sourceTemplateId;
         return this;
      }

      /**
       * @param status the status of the template
       */
      public Builder status(String status) {
         this.status = status;
         return this;
      }

      /**
       * @param templateTag the tag of this template
       */
      public Builder templateTag(String templateTag) {
         this.templateTag = templateTag;
         return this;
      }

      /**
       * @param templateType the type of the template
       */
      public Builder templateType(String templateType) {
         this.templateType = templateType;
         return this;
      }

      /**
       * @param zoneId the ID of the zone for this template
       */
      public Builder zoneId(long zoneId) {
         this.zoneId = zoneId;
         return this;
      }

      /**
       * @param zoneName the name of the zone for this template
       */
      public Builder zoneName(String zoneName) {
         this.zoneName = zoneName;
         return this;
      }

   }

   private long id;
   private String account;
   @SerializedName("accountid")
   private long accountId;
   private boolean bootable;
   private String checksum;
   private Date created;
   private boolean crossZones;
   @SerializedName("displaytext")
   private String displayText;
   private String domain;
   @SerializedName("domainId")
   private long domainid;
   private String format;
   @SerializedName("hostid")
   private long hostId;
   @SerializedName("hostname")
   private String hostName;
   @SerializedName("")
   private String hypervisor;
   @SerializedName("isextractable")
   private boolean isExtractable;
   @SerializedName("isfeatured")
   private boolean isFeatured;
   @SerializedName("ispublic")
   private boolean isPublic;
   @SerializedName("isready")
   private boolean isReady;
   @SerializedName("jobid")
   private long jobId;
   @SerializedName("jobstatus")
   private String jobStatus;
   private String name;
   @SerializedName("ostypeid")
   private long osTypeId;
   @SerializedName("ostypename")
   private String osTypeName;
   @SerializedName("passwordenabled")
   private boolean passwordEnabled;
   private Date removed;
   private long size;
   @SerializedName("sourcetemplateid")
   private long sourceTemplateId;
   private String status;
   @SerializedName("templatetag")
   private String templateTag;
   @SerializedName("templatetype")
   private String templateType;
   @SerializedName("zoneid")
   private long zoneId;
   @SerializedName("zonename")
   private String zoneName;

   /**
    * present only for serializer
    */
   ISO() {
   }

   /**
    * @return the template ID
    */
   public long getId() {
      return id;
   }

   /**
    * @return the account name to which the template belongs
    */
   public String getAccount() {
      return account;
   }

   /**
    * @return the account id to which the template belongs
    */
   public long getAccountId() {
      return accountId;
   }

   /**
    * @return true if the ISO is bootable, false otherwise
    */
   public boolean getBootable() {
      return bootable;
   }

   /**
    * @return checksum of the template
    */
   public String getChecksum() {
      return checksum;
   }

   /**
    * @return the date this template was created
    */
   public Date getCreated() {
      return created;
   }

   /**
    * @return true if the template is managed across all Zones, false otherwise
    */
   public boolean getCrossZones() {
      return crossZones;
   }

   /**
    * @return the template display text
    */
   public String getDisplayText() {
      return displayText;
   }

   /**
    * @return the name of the domain to which the template belongs
    */
   public String getDomain() {
      return domain;
   }

   /**
    * @return the ID of the domain to which the template belongs
    */
   public long getDomainid() {
      return domainid;
   }

   /**
    * @return the format of the template.
    */
   public String getFormat() {
      return format;
   }

   /**
    * @return the ID of the secondary storage host for the template
    */
   public long getHostId() {
      return hostId;
   }

   /**
    * @return the name of the secondary storage host for the template
    */
   public String getHostName() {
      return hostName;
   }

   /**
    * @return the hypervisor on which the template runs
    */
   public String getHypervisor() {
      return hypervisor;
   }

   /**
    * @return true if the template is extractable, false otherwise
    */
   public boolean getIsExtractable() {
      return isExtractable;
   }

   /**
    * @return true if this template is a featured template, false otherwise
    */
   public boolean getIsFeatured() {
      return isFeatured;
   }

   /**
    * @return true if this template is a public template, false otherwise
    */
   public boolean getIsPublic() {
      return isPublic;
   }

   /**
    * @return true if the template is ready to be deployed from, false otherwise.
    */
   public boolean getIsReady() {
      return isReady;
   }

   /**
    * @return shows the current pending asynchronous job ID. This tag is not returned if no current pending jobs are acting on the template
    */
   public long getJobId() {
      return jobId;
   }

   /**
    * @return shows the current pending asynchronous job status
    */
   public String getJobStatus() {
      return jobStatus;
   }

   /**
    * @return the template name
    */
   public String getName() {
      return name;
   }

   /**
    * @return the ID of the OS type for this template.
    */
   public long getOsTypeId() {
      return osTypeId;
   }

   /**
    * @return the name of the OS type for this template.
    */
   public String getOsTypeName() {
      return osTypeName;
   }

   /**
    * @return true if the reset password feature is enabled, false otherwise
    */
   public boolean getPasswordEnabled() {
      return passwordEnabled;
   }

   /**
    * @return the date this template was removed
    */
   public Date getRemoved() {
      return removed;
   }

   /**
    * @return the size of the template
    */
   public long getSize() {
      return size;
   }

   /**
    * @return the template ID of the parent template if present
    */
   public long getSourceTemplateId() {
      return sourceTemplateId;
   }

   /**
    * @return the status of the template
    */
   public String getStatus() {
      return status;
   }

   /**
    * @return the tag of this template
    */
   public String getTemplateTag() {
      return templateTag;
   }

   /**
    * @return the type of the template
    */
   public String getTemplateType() {
      return templateType;
   }

   /**
    * @return the ID of the zone for this template
    */
   public long getZoneId() {
      return zoneId;
   }

   /**
    * @return the name of the zone for this template
    */
   public String getZoneName() {
      return zoneName;
   }

   @Override
   public boolean equals(Object o) {
      throw new RuntimeException("FIXME: Implement me");
   }

   @Override
   public int hashCode() {
      throw new RuntimeException("FIXME: Implement me");
   }

   @Override
   public String toString() {
      throw new RuntimeException("FIXME: Implement me");
   }

   @Override
   public int compareTo(ISO other) {
      throw new RuntimeException("FIXME: Implement me");
   }

   public enum ISOFilter {

      featured, self, self_executable, executable, community, UNRECOGNIZED;

      public static ISOFilter fromValue(String format) {
         try {
            return valueOf(checkNotNull(format, "format"));
         } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }
   }
}
