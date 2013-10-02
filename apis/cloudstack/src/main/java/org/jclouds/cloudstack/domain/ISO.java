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

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.util.Date;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Class ISO
 *
 * @author Richard Downer
 */
public class ISO {

   /**
    */
   public static enum ISOFilter {

      featured, self, self_executable, executable, community, UNRECOGNIZED;

      public static ISOFilter fromValue(String format) {
         try {
            return valueOf(checkNotNull(format, "format"));
         } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }
   }

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromISO(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected String id;
      protected String account;
      protected String accountId;
      protected boolean bootable;
      protected String checksum;
      protected Date created;
      protected boolean crossZones;
      protected String displayText;
      protected String domain;
      protected String domainid;
      protected String format;
      protected String hostId;
      protected String hostName;
      protected String hypervisor;
      protected boolean isExtractable;
      protected boolean isFeatured;
      protected boolean isPublic;
      protected boolean isReady;
      protected String jobId;
      protected String jobStatus;
      protected String name;
      protected String osTypeId;
      protected String osTypeName;
      protected boolean passwordEnabled;
      protected Date removed;
      protected long size;
      protected String sourceTemplateId;
      protected String status;
      protected String templateTag;
      protected String templateType;
      protected String zoneId;
      protected String zoneName;

      /**
       * @see ISO#getId()
       */
      public T id(String id) {
         this.id = id;
         return self();
      }

      /**
       * @see ISO#getAccount()
       */
      public T account(String account) {
         this.account = account;
         return self();
      }

      /**
       * @see ISO#getAccountId()
       */
      public T accountId(String accountId) {
         this.accountId = accountId;
         return self();
      }

      /**
       * @see ISO#isBootable()
       */
      public T bootable(boolean bootable) {
         this.bootable = bootable;
         return self();
      }

      /**
       * @see ISO#getChecksum()
       */
      public T checksum(String checksum) {
         this.checksum = checksum;
         return self();
      }

      /**
       * @see ISO#getCreated()
       */
      public T created(Date created) {
         this.created = created;
         return self();
      }

      /**
       * @see ISO#isCrossZones()
       */
      public T crossZones(boolean crossZones) {
         this.crossZones = crossZones;
         return self();
      }

      /**
       * @see ISO#getDisplayText()
       */
      public T displayText(String displayText) {
         this.displayText = displayText;
         return self();
      }

      /**
       * @see ISO#getDomain()
       */
      public T domain(String domain) {
         this.domain = domain;
         return self();
      }

      /**
       * @see ISO#getDomainid()
       */
      public T domainid(String domainid) {
         this.domainid = domainid;
         return self();
      }

      /**
       * @see ISO#getFormat()
       */
      public T format(String format) {
         this.format = format;
         return self();
      }

      /**
       * @see ISO#getHostId()
       */
      public T hostId(String hostId) {
         this.hostId = hostId;
         return self();
      }

      /**
       * @see ISO#getHostName()
       */
      public T hostName(String hostName) {
         this.hostName = hostName;
         return self();
      }

      /**
       * @see ISO#getHypervisor()
       */
      public T hypervisor(String hypervisor) {
         this.hypervisor = hypervisor;
         return self();
      }

      /**
       * @see ISO#isExtractable()
       */
      public T isExtractable(boolean isExtractable) {
         this.isExtractable = isExtractable;
         return self();
      }

      /**
       * @see ISO#isFeatured()
       */
      public T isFeatured(boolean isFeatured) {
         this.isFeatured = isFeatured;
         return self();
      }

      /**
       * @see ISO#isPublic()
       */
      public T isPublic(boolean isPublic) {
         this.isPublic = isPublic;
         return self();
      }

      /**
       * @see ISO#isReady()
       */
      public T isReady(boolean isReady) {
         this.isReady = isReady;
         return self();
      }

      /**
       * @see ISO#getJobId()
       */
      public T jobId(String jobId) {
         this.jobId = jobId;
         return self();
      }

      /**
       * @see ISO#getJobStatus()
       */
      public T jobStatus(String jobStatus) {
         this.jobStatus = jobStatus;
         return self();
      }

      /**
       * @see ISO#getName()
       */
      public T name(String name) {
         this.name = name;
         return self();
      }

      /**
       * @see ISO#getOsTypeId()
       */
      public T osTypeId(String osTypeId) {
         this.osTypeId = osTypeId;
         return self();
      }

      /**
       * @see ISO#getOsTypeName()
       */
      public T osTypeName(String osTypeName) {
         this.osTypeName = osTypeName;
         return self();
      }

      /**
       * @see ISO#isPasswordEnabled()
       */
      public T passwordEnabled(boolean passwordEnabled) {
         this.passwordEnabled = passwordEnabled;
         return self();
      }

      /**
       * @see ISO#getRemoved()
       */
      public T removed(Date removed) {
         this.removed = removed;
         return self();
      }

      /**
       * @see ISO#getSize()
       */
      public T size(long size) {
         this.size = size;
         return self();
      }

      /**
       * @see ISO#getSourceTemplateId()
       */
      public T sourceTemplateId(String sourceTemplateId) {
         this.sourceTemplateId = sourceTemplateId;
         return self();
      }

      /**
       * @see ISO#getStatus()
       */
      public T status(String status) {
         this.status = status;
         return self();
      }

      /**
       * @see ISO#getTemplateTag()
       */
      public T templateTag(String templateTag) {
         this.templateTag = templateTag;
         return self();
      }

      /**
       * @see ISO#getTemplateType()
       */
      public T templateType(String templateType) {
         this.templateType = templateType;
         return self();
      }

      /**
       * @see ISO#getZoneId()
       */
      public T zoneId(String zoneId) {
         this.zoneId = zoneId;
         return self();
      }

      /**
       * @see ISO#getZoneName()
       */
      public T zoneName(String zoneName) {
         this.zoneName = zoneName;
         return self();
      }

      public ISO build() {
         return new ISO(id, account, accountId, bootable, checksum, created, crossZones, displayText, domain, domainid,
               format, hostId, hostName, hypervisor, isExtractable, isFeatured, isPublic, isReady, jobId, jobStatus, name,
               osTypeId, osTypeName, passwordEnabled, removed, size, sourceTemplateId, status, templateTag, templateType,
               zoneId, zoneName);
      }

      public T fromISO(ISO in) {
         return this
               .id(in.getId())
               .account(in.getAccount())
               .accountId(in.getAccountId())
               .bootable(in.isBootable())
               .checksum(in.getChecksum())
               .created(in.getCreated())
               .crossZones(in.isCrossZones())
               .displayText(in.getDisplayText())
               .domain(in.getDomain())
               .domainid(in.getDomainid())
               .format(in.getFormat())
               .hostId(in.getHostId())
               .hostName(in.getHostName())
               .hypervisor(in.getHypervisor())
               .isExtractable(in.isExtractable())
               .isFeatured(in.isFeatured())
               .isPublic(in.isPublic())
               .isReady(in.isReady())
               .jobId(in.getJobId())
               .jobStatus(in.getJobStatus())
               .name(in.getName())
               .osTypeId(in.getOsTypeId())
               .osTypeName(in.getOsTypeName())
               .passwordEnabled(in.isPasswordEnabled())
               .removed(in.getRemoved())
               .size(in.getSize())
               .sourceTemplateId(in.getSourceTemplateId())
               .status(in.getStatus())
               .templateTag(in.getTemplateTag())
               .templateType(in.getTemplateType())
               .zoneId(in.getZoneId())
               .zoneName(in.getZoneName());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String id;
   private final String account;
   private final String accountId;
   private final boolean bootable;
   private final String checksum;
   private final Date created;
   private final boolean crossZones;
   private final String displayText;
   private final String domain;
   private final String domainid;
   private final String format;
   private final String hostId;
   private final String hostName;
   private final String hypervisor;
   private final boolean isExtractable;
   private final boolean isFeatured;
   private final boolean isPublic;
   private final boolean isReady;
   private final String jobId;
   private final String jobStatus;
   private final String name;
   private final String osTypeId;
   private final String osTypeName;
   private final boolean passwordEnabled;
   private final Date removed;
   private final long size;
   private final String sourceTemplateId;
   private final String status;
   private final String templateTag;
   private final String templateType;
   private final String zoneId;
   private final String zoneName;

   @ConstructorProperties({
         "id", "account", "accountid", "bootable", "checksum", "created", "crossZones", "displaytext", "domain", "domainid", "format", "hostid", "hostname", "hypervisor", "isextractable", "isfeatured", "ispublic", "isready", "jobid", "jobstatus", "name", "ostypeid", "ostypename", "passwordenabled", "removed", "size", "sourcetemplateid", "status", "templatetag", "templatetype", "zoneid", "zonename"
   })
   protected ISO(String id, @Nullable String account, @Nullable String accountId, boolean bootable, @Nullable String checksum,
                 @Nullable Date created, boolean crossZones, @Nullable String displayText, @Nullable String domain,
                 @Nullable String domainid, @Nullable String format, @Nullable String hostId, @Nullable String hostName,
                 @Nullable String hypervisor, boolean isExtractable, boolean isFeatured, boolean isPublic, boolean isReady,
                 @Nullable String jobId, @Nullable String jobStatus, @Nullable String name, @Nullable String osTypeId,
                 @Nullable String osTypeName, boolean passwordEnabled, @Nullable Date removed, long size, @Nullable String sourceTemplateId,
                 @Nullable String status, @Nullable String templateTag, @Nullable String templateType, @Nullable String zoneId, @Nullable String zoneName) {
      this.id = checkNotNull(id, "id");
      this.account = account;
      this.accountId = accountId;
      this.bootable = bootable;
      this.checksum = checksum;
      this.created = created;
      this.crossZones = crossZones;
      this.displayText = displayText;
      this.domain = domain;
      this.domainid = domainid;
      this.format = format;
      this.hostId = hostId;
      this.hostName = hostName;
      this.hypervisor = hypervisor;
      this.isExtractable = isExtractable;
      this.isFeatured = isFeatured;
      this.isPublic = isPublic;
      this.isReady = isReady;
      this.jobId = jobId;
      this.jobStatus = jobStatus;
      this.name = name;
      this.osTypeId = osTypeId;
      this.osTypeName = osTypeName;
      this.passwordEnabled = passwordEnabled;
      this.removed = removed;
      this.size = size;
      this.sourceTemplateId = sourceTemplateId;
      this.status = status;
      this.templateTag = templateTag;
      this.templateType = templateType;
      this.zoneId = zoneId;
      this.zoneName = zoneName;
   }

   /**
    * @return the template ID
    */
   public String getId() {
      return this.id;
   }

   /**
    * @return the account name to which the template belongs
    */
   @Nullable
   public String getAccount() {
      return this.account;
   }

   /**
    * @return the account id to which the template belongs
    */
   @Nullable
   public String getAccountId() {
      return this.accountId;
   }

   public boolean isBootable() {
      return this.bootable;
   }

   /**
    * @return checksum of the template
    */
   @Nullable
   public String getChecksum() {
      return this.checksum;
   }

   /**
    * @return the date this template was created
    */
   @Nullable
   public Date getCreated() {
      return this.created;
   }

   public boolean isCrossZones() {
      return this.crossZones;
   }

   /**
    * @return the template display text
    */
   @Nullable
   public String getDisplayText() {
      return this.displayText;
   }

   /**
    * @return the name of the domain to which the template belongs
    */
   @Nullable
   public String getDomain() {
      return this.domain;
   }

   /**
    * @return the ID of the domain to which the template belongs
    */
   @Nullable
   public String getDomainid() {
      return this.domainid;
   }

   /**
    * @return the format of the template.
    */
   @Nullable
   public String getFormat() {
      return this.format;
   }

   /**
    * @return the ID of the secondary storage host for the template
    */
   @Nullable
   public String getHostId() {
      return this.hostId;
   }

   /**
    * @return the name of the secondary storage host for the template
    */
   @Nullable
   public String getHostName() {
      return this.hostName;
   }

   /**
    * @return the hypervisor on which the template runs
    */
   @Nullable
   public String getHypervisor() {
      return this.hypervisor;
   }

   public boolean isExtractable() {
      return this.isExtractable;
   }

   public boolean isFeatured() {
      return this.isFeatured;
   }

   public boolean isPublic() {
      return this.isPublic;
   }

   public boolean isReady() {
      return this.isReady;
   }

   /**
    * @return shows the current pending asynchronous job ID. This tag is not returned if no current pending jobs are acting on the template
    */
   @Nullable
   public String getJobId() {
      return this.jobId;
   }

   /**
    * @return shows the current pending asynchronous job status
    */
   @Nullable
   public String getJobStatus() {
      return this.jobStatus;
   }

   /**
    * @return the template name
    */
   @Nullable
   public String getName() {
      return this.name;
   }

   /**
    * @return the ID of the OS type for this template.
    */
   @Nullable
   public String getOsTypeId() {
      return this.osTypeId;
   }

   /**
    * @return the name of the OS type for this template.
    */
   @Nullable
   public String getOsTypeName() {
      return this.osTypeName;
   }

   public boolean isPasswordEnabled() {
      return this.passwordEnabled;
   }

   /**
    * @return the date this template was removed
    */
   @Nullable
   public Date getRemoved() {
      return this.removed;
   }

   /**
    * @return the size of the template
    */
   public long getSize() {
      return this.size;
   }

   /**
    * @return the template ID of the parent template if present
    */
   @Nullable
   public String getSourceTemplateId() {
      return this.sourceTemplateId;
   }

   /**
    * @return the status of the template
    */
   @Nullable
   public String getStatus() {
      return this.status;
   }

   /**
    * @return the tag of this template
    */
   @Nullable
   public String getTemplateTag() {
      return this.templateTag;
   }

   /**
    * @return the type of the template
    */
   @Nullable
   public String getTemplateType() {
      return this.templateType;
   }

   /**
    * @return the ID of the zone for this template
    */
   @Nullable
   public String getZoneId() {
      return this.zoneId;
   }

   /**
    * @return the name of the zone for this template
    */
   @Nullable
   public String getZoneName() {
      return this.zoneName;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, account, accountId, bootable, checksum, created, crossZones, displayText, domain,
            domainid, format, hostId, hostName, hypervisor, isExtractable, isFeatured, isPublic, isReady, jobId, jobStatus,
            name, osTypeId, osTypeName, passwordEnabled, removed, size, sourceTemplateId, status, templateTag, templateType, zoneId, zoneName);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      ISO that = ISO.class.cast(obj);
      return Objects.equal(this.id, that.id)
            && Objects.equal(this.account, that.account)
            && Objects.equal(this.accountId, that.accountId)
            && Objects.equal(this.bootable, that.bootable)
            && Objects.equal(this.checksum, that.checksum)
            && Objects.equal(this.created, that.created)
            && Objects.equal(this.crossZones, that.crossZones)
            && Objects.equal(this.displayText, that.displayText)
            && Objects.equal(this.domain, that.domain)
            && Objects.equal(this.domainid, that.domainid)
            && Objects.equal(this.format, that.format)
            && Objects.equal(this.hostId, that.hostId)
            && Objects.equal(this.hostName, that.hostName)
            && Objects.equal(this.hypervisor, that.hypervisor)
            && Objects.equal(this.isExtractable, that.isExtractable)
            && Objects.equal(this.isFeatured, that.isFeatured)
            && Objects.equal(this.isPublic, that.isPublic)
            && Objects.equal(this.isReady, that.isReady)
            && Objects.equal(this.jobId, that.jobId)
            && Objects.equal(this.jobStatus, that.jobStatus)
            && Objects.equal(this.name, that.name)
            && Objects.equal(this.osTypeId, that.osTypeId)
            && Objects.equal(this.osTypeName, that.osTypeName)
            && Objects.equal(this.passwordEnabled, that.passwordEnabled)
            && Objects.equal(this.removed, that.removed)
            && Objects.equal(this.size, that.size)
            && Objects.equal(this.sourceTemplateId, that.sourceTemplateId)
            && Objects.equal(this.status, that.status)
            && Objects.equal(this.templateTag, that.templateTag)
            && Objects.equal(this.templateType, that.templateType)
            && Objects.equal(this.zoneId, that.zoneId)
            && Objects.equal(this.zoneName, that.zoneName);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("id", id).add("account", account).add("accountId", accountId).add("bootable", bootable)
            .add("checksum", checksum).add("created", created).add("crossZones", crossZones).add("displayText", displayText)
            .add("domain", domain).add("domainid", domainid).add("format", format).add("hostId", hostId).add("hostName", hostName)
            .add("hypervisor", hypervisor).add("isExtractable", isExtractable).add("isFeatured", isFeatured).add("isPublic", isPublic)
            .add("isReady", isReady).add("jobId", jobId).add("jobStatus", jobStatus).add("name", name).add("osTypeId", osTypeId)
            .add("osTypeName", osTypeName).add("passwordEnabled", passwordEnabled).add("removed", removed).add("size", size)
            .add("sourceTemplateId", sourceTemplateId).add("status", status).add("templateTag", templateTag).add("templateType", templateType)
            .add("zoneId", zoneId).add("zoneName", zoneName);
   }

   @Override
   public String toString() {
      return string().toString();
   }

}
