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

import com.google.common.base.Strings;
import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * @author Adrian Cole
 */
public class Template implements Comparable<Template> {
   public enum Status {

      /**
       * status of download is not known. Example - When the job for downloading doesn't exist
       * during progress check.
       */
      UNKNOWN,
      /**
       * the download has been cancelled/aborted.
       */
      ABANDONED,
      /**
       * the download has reached an error state. Example - there is not route to ssvm agent
       */
      DOWNLOAD_ERROR,
      /**
       * the download hasn't started.
       */
      NOT_DOWNLOADED,
      /**
       * the download is in progress
       */
      DOWNLOAD_IN_PROGRESS,
      /**
       * the resource has been downloaded on secondary storage.
       */
      DOWNLOADED,

      // These states are specifically used for extraction of resources out of CS(ironically shown
      // as download template in the UI, API - extractTemplate ). Some of the generic states (like
      // abandoned, unknown) above are used for the extraction tasks as well.

      /**
       * the resource has been uploaded
       */
      UPLOADED,
      /**
       * the resource upload work hasn't started yet
       */
      NOT_UPLOADED,
      /**
       * the resource upload has reached error.
       */
      UPLOAD_ERROR,
      /**
       * the resource upload is in progress.
       */
      UPLOAD_IN_PROGRESS, UNRECOGNIZED;

      public static Status fromValue(String state) {
         // Statuses are in free text form. These are the ones in CloudStack 3.0.4 source
         // https://github.com/CloudStack/CloudStack/blob/e2e76c70ec51bfb35d755371f6c33856cef8a277/server/src/com/cloud/api/ApiResponseHelper.java#L1968
         if (Strings.isNullOrEmpty(state)) { return UNKNOWN; }
         else if (state.equals("Processing")) { return DOWNLOAD_IN_PROGRESS; }
         else if (state.endsWith("% Downloaded")) { return DOWNLOAD_IN_PROGRESS; }
         else if (state.equals("Installing Template")) { return DOWNLOAD_IN_PROGRESS; }
         else if (state.equals("Installing ISO")) { return DOWNLOAD_IN_PROGRESS; }
         else if (state.equals("Download Complete")) { return DOWNLOADED; }
         try {
            return valueOf(checkNotNull(state, "state"));
         } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }

   }

   public static enum Type {

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

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromTemplate(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected String id;
      protected String displayText;
      protected String domain;
      protected String domainId;
      protected String account;
      protected String accountId;
      protected String zone;
      protected String zoneId;
      protected String OSType;
      protected String OSTypeId;
      protected String name;
      protected Template.Type type;
      protected Template.Status status;
      protected Template.Format format;
      protected String hypervisor;
      protected Long size;
      protected Date created;
      protected Date removed;
      protected boolean crossZones;
      protected boolean bootable;
      protected boolean extractable;
      protected boolean featured;
      protected boolean isPublic;
      protected boolean ready;
      protected boolean passwordEnabled;
      protected String jobId;
      protected String jobStatus;
      protected String checksum;
      protected String hostId;
      protected String hostName;
      protected String sourceTemplateId;
      protected String templateTag;

      /**
       * @see Template#getId()
       */
      public T id(String id) {
         this.id = id;
         return self();
      }

      /**
       * @see Template#getDisplayText()
       */
      public T displayText(String displayText) {
         this.displayText = displayText;
         return self();
      }

      /**
       * @see Template#getDomain()
       */
      public T domain(String domain) {
         this.domain = domain;
         return self();
      }

      /**
       * @see Template#getDomainId()
       */
      public T domainId(String domainId) {
         this.domainId = domainId;
         return self();
      }

      /**
       * @see Template#getAccount()
       */
      public T account(String account) {
         this.account = account;
         return self();
      }

      /**
       * @see Template#getAccountId()
       */
      public T accountId(String accountId) {
         this.accountId = accountId;
         return self();
      }

      /**
       * @see Template#getZone()
       */
      public T zone(String zone) {
         this.zone = zone;
         return self();
      }

      /**
       * @see Template#getZoneId()
       */
      public T zoneId(String zoneId) {
         this.zoneId = zoneId;
         return self();
      }

      /**
       * @see Template#getOSType()
       */
      public T OSType(String OSType) {
         this.OSType = OSType;
         return self();
      }

      /**
       * @see Template#getOSTypeId()
       */
      public T OSTypeId(String OSTypeId) {
         this.OSTypeId = OSTypeId;
         return self();
      }

      /**
       * @see Template#getName()
       */
      public T name(String name) {
         this.name = name;
         return self();
      }

      /**
       * @see Template#getType()
       */
      public T type(Template.Type type) {
         this.type = type;
         return self();
      }

      /**
       * @see Template#getStatus()
       */
      public T status(Template.Status status) {
         this.status = status;
         return self();
      }

      /**
       * @see Template#getFormat()
       */
      public T format(Template.Format format) {
         this.format = format;
         return self();
      }

      /**
       * @see Template#getHypervisor()
       */
      public T hypervisor(String hypervisor) {
         this.hypervisor = hypervisor;
         return self();
      }

      /**
       * @see Template#getSize()
       */
      public T size(Long size) {
         this.size = size;
         return self();
      }

      /**
       * @see Template#getCreated()
       */
      public T created(Date created) {
         this.created = created;
         return self();
      }

      /**
       * @see Template#getRemoved()
       */
      public T removed(Date removed) {
         this.removed = removed;
         return self();
      }

      /**
       * @see Template#isCrossZones()
       */
      public T crossZones(boolean crossZones) {
         this.crossZones = crossZones;
         return self();
      }

      /**
       * @see Template#isBootable()
       */
      public T bootable(boolean bootable) {
         this.bootable = bootable;
         return self();
      }

      /**
       * @see Template#isExtractable()
       */
      public T extractable(boolean extractable) {
         this.extractable = extractable;
         return self();
      }

      /**
       * @see Template#isFeatured()
       */
      public T featured(boolean featured) {
         this.featured = featured;
         return self();
      }

      /**
       * @see Template#ispublic()
       */
      public T isPublic(boolean isPublic) {
         this.isPublic = isPublic;
         return self();
      }

      /**
       * @see Template#isReady()
       */
      public T ready(boolean ready) {
         this.ready = ready;
         return self();
      }

      /**
       * @see Template#isPasswordEnabled()
       */
      public T passwordEnabled(boolean passwordEnabled) {
         this.passwordEnabled = passwordEnabled;
         return self();
      }

      /**
       * @see Template#getJobId()
       */
      public T jobId(String jobId) {
         this.jobId = jobId;
         return self();
      }

      /**
       * @see Template#getJobStatus()
       */
      public T jobStatus(String jobStatus) {
         this.jobStatus = jobStatus;
         return self();
      }

      /**
       * @see Template#getChecksum()
       */
      public T checksum(String checksum) {
         this.checksum = checksum;
         return self();
      }

      /**
       * @see Template#getHostId()
       */
      public T hostId(String hostId) {
         this.hostId = hostId;
         return self();
      }

      /**
       * @see Template#getHostName()
       */
      public T hostName(String hostName) {
         this.hostName = hostName;
         return self();
      }

      /**
       * @see Template#getSourceTemplateId()
       */
      public T sourceTemplateId(String sourceTemplateId) {
         this.sourceTemplateId = sourceTemplateId;
         return self();
      }

      /**
       * @see Template#getTemplateTag()
       */
      public T templateTag(String templateTag) {
         this.templateTag = templateTag;
         return self();
      }

      public Template build() {
         return new Template(id, displayText, domain, domainId, account, accountId, zone, zoneId, OSType, OSTypeId, name, type, status, format, hypervisor, size, created, removed, crossZones, bootable, extractable, featured, isPublic, ready, passwordEnabled, jobId, jobStatus, checksum, hostId, hostName, sourceTemplateId, templateTag);
      }

      public T fromTemplate(Template in) {
         return this
               .id(in.getId())
               .displayText(in.getDisplayText())
               .domain(in.getDomain())
               .domainId(in.getDomainId())
               .account(in.getAccount())
               .accountId(in.getAccountId())
               .zone(in.getZone())
               .zoneId(in.getZoneId())
               .OSType(in.getOSType())
               .OSTypeId(in.getOSTypeId())
               .name(in.getName())
               .type(in.getType())
               .status(in.getStatus())
               .format(in.getFormat())
               .hypervisor(in.getHypervisor())
               .size(in.getSize())
               .created(in.getCreated())
               .removed(in.getRemoved())
               .crossZones(in.isCrossZones())
               .bootable(in.isBootable())
               .extractable(in.isExtractable())
               .featured(in.isFeatured())
               .isPublic(in.ispublic())
               .ready(in.isReady())
               .passwordEnabled(in.isPasswordEnabled())
               .jobId(in.getJobId())
               .jobStatus(in.getJobStatus())
               .checksum(in.getChecksum())
               .hostId(in.getHostId())
               .hostName(in.getHostName())
               .sourceTemplateId(in.getSourceTemplateId())
               .templateTag(in.getTemplateTag());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String id;
   private final String displayText;
   private final String domain;
   private final String domainId;
   private final String account;
   private final String accountId;
   private final String zone;
   private final String zoneId;
   private final String OSType;
   private final String OSTypeId;
   private final String name;
   private final Template.Type type;
   private final Template.Status status;
   private final Template.Format format;
   private final String hypervisor;
   private final Long size;
   private final Date created;
   private final Date removed;
   private final boolean crossZones;
   private final boolean bootable;
   private final boolean extractable;
   private final boolean featured;
   private final boolean ispublic;
   private final boolean ready;
   private final boolean passwordEnabled;
   private final String jobId;
   private final String jobStatus;
   private final String checksum;
   private final String hostId;
   private final String hostName;
   private final String sourceTemplateId;
   private final String templateTag;

   @ConstructorProperties({
         "id", "displaytext", "domain", "domainid", "account", "accountid", "zonename", "zoneid", "ostypename", "ostypeid",
         "name", "templatetype", "status", "format", "hypervisor", "size", "created", "removed", "crossZones", "bootable",
         "isextractable", "isfeatured", "ispublic", "isready", "passwordenabled", "jobid", "jobstatus", "checksum", "hostId",
         "hostname", "sourcetemplateid", "templatetag"
   })
   protected Template(String id, @Nullable String displayText, @Nullable String domain, @Nullable String domainId,
                      @Nullable String account, @Nullable String accountId, @Nullable String zone, @Nullable String zoneId,
                      @Nullable String OSType, @Nullable String OSTypeId, @Nullable String name, @Nullable Template.Type type,
                      @Nullable Template.Status status, @Nullable Template.Format format, @Nullable String hypervisor,
                      @Nullable Long size, @Nullable Date created, @Nullable Date removed, boolean crossZones,
                      boolean bootable, boolean extractable, boolean featured, boolean ispublic, boolean ready, boolean passwordEnabled,
                      @Nullable String jobId, @Nullable String jobStatus, @Nullable String checksum, @Nullable String hostId,
                      @Nullable String hostName, @Nullable String sourceTemplateId, @Nullable String templateTag) {
      this.id = checkNotNull(id, "id");
      this.displayText = displayText;
      this.domain = domain;
      this.domainId = domainId;
      this.account = account;
      this.accountId = accountId;
      this.zone = zone;
      this.zoneId = zoneId;
      this.OSType = OSType;
      this.OSTypeId = OSTypeId;
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
    * @return Template id
    */
   public String getId() {
      return this.id;
   }

   /**
    * @return the display text of the template
    */
   @Nullable
   public String getDisplayText() {
      return this.displayText;
   }

   /**
    * @return the name of the domain to which the template beLongs
    */
   @Nullable
   public String getDomain() {
      return this.domain;
   }

   /**
    * @return the ID of the domain to which the template beLongs
    */
   @Nullable
   public String getDomainId() {
      return this.domainId;
   }

   /**
    * @return the name of the account to which the template beLongs
    */
   @Nullable
   public String getAccount() {
      return this.account;
   }

   /**
    * @return the ID of the account to which the template beLongs
    */
   @Nullable
   public String getAccountId() {
      return this.accountId;
   }

   /**
    * @return the name of the zone to which the template beLongs
    */
   @Nullable
   public String getZone() {
      return this.zone;
   }

   /**
    * @return the ID of the zone to which the template beLongs
    */
   @Nullable
   public String getZoneId() {
      return this.zoneId;
   }

   /**
    * @return the name of the OS type to which the template beLongs
    */
   @Nullable
   public String getOSType() {
      return this.OSType;
   }

   /**
    * @return the ID of the OS type to which the template beLongs
    */
   @Nullable
   public String getOSTypeId() {
      return this.OSTypeId;
   }

   /**
    * @return Template name
    */
   @Nullable
   public String getName() {
      return this.name;
   }

   /**
    * @return the type of the template
    */
   @Nullable
   public Template.Type getType() {
      return this.type;
   }

   /**
    * Retrieve the status of the template.
    *
    * <p>Note that in CloudStack 2.2.x through to at least 3.0.4, the possible status values are
    * not well defined by CloudStack. CloudStack returns a plain-text English string for UI
    * display, which jclouds attempts to parse into an enumeration, but the mapping is incomplete.
    * This method should be reliable for the common cases, but it is possible (particularly for
    * error statuses) that this method will return UNRECOGNIZED.</p>
    *
    * @return status of the template
    */
   @Nullable
   public Template.Status getStatus() {
      return this.status;
   }

   /**
    * @return the format of the template.
    */
   @Nullable
   public Template.Format getFormat() {
      return this.format;
   }

   /**
    * @return the hypervisor on which the template runs
    */
   @Nullable
   public String getHypervisor() {
      return this.hypervisor;
   }

   /**
    * @return the size of the template in kilobytes
    */
   @Nullable
   public Long getSize() {
      return this.size;
   }

   /**
    * @return the date this template was created
    */
   @Nullable
   public Date getCreated() {
      return this.created;
   }

   /**
    * @return the date this template was removed
    */
   @Nullable
   public Date getRemoved() {
      return this.removed;
   }

   /**
    * @return true if the template is managed across all Zones, false otherwise
    */
   public boolean isCrossZones() {
      return this.crossZones;
   }

   /**
    * @return true if the ISO is bootable, false otherwise
    */
   public boolean isBootable() {
      return this.bootable;
   }

   /**
    * @return true if the template is extractable, false otherwise
    */
   public boolean isExtractable() {
      return this.extractable;
   }

   /**
    * @return true if this template is a featured template, false otherwise
    */
   public boolean isFeatured() {
      return this.featured;
   }

   public boolean ispublic() {
      return this.ispublic;
   }

   /**
    * @return true if the template is ready to be deployed from, false otherwise
    */
   public boolean isReady() {
      return this.ready;
   }

   /**
    * @return true if the reset password feature is enabled, false otherwise
    */
   public boolean isPasswordEnabled() {
      return this.passwordEnabled;
   }

   /**
    * @return shows the current pending asynchronous job ID, or null if current
    *         pending jobs are acting on the template
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
    * @return checksum of the template
    */
   @Nullable
   public String getChecksum() {
      return this.checksum;
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
    * @return the template ID of the parent template if present
    */
   @Nullable
   public String getSourceTemplateId() {
      return this.sourceTemplateId;
   }

   /**
    * @return the tag of this template
    */
   @Nullable
   public String getTemplateTag() {
      return this.templateTag;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, displayText, domain, domainId, account, accountId, zone, zoneId, OSType, OSTypeId, name, type, status, format, hypervisor, size, created, removed, crossZones, bootable, extractable, featured, ispublic, ready, passwordEnabled, jobId, jobStatus, checksum, hostId, hostName, sourceTemplateId, templateTag);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Template that = Template.class.cast(obj);
      return Objects.equal(this.id, that.id)
            && Objects.equal(this.displayText, that.displayText)
            && Objects.equal(this.domain, that.domain)
            && Objects.equal(this.domainId, that.domainId)
            && Objects.equal(this.account, that.account)
            && Objects.equal(this.accountId, that.accountId)
            && Objects.equal(this.zone, that.zone)
            && Objects.equal(this.zoneId, that.zoneId)
            && Objects.equal(this.OSType, that.OSType)
            && Objects.equal(this.OSTypeId, that.OSTypeId)
            && Objects.equal(this.name, that.name)
            && Objects.equal(this.type, that.type)
            && Objects.equal(this.status, that.status)
            && Objects.equal(this.format, that.format)
            && Objects.equal(this.hypervisor, that.hypervisor)
            && Objects.equal(this.size, that.size)
            && Objects.equal(this.created, that.created)
            && Objects.equal(this.removed, that.removed)
            && Objects.equal(this.crossZones, that.crossZones)
            && Objects.equal(this.bootable, that.bootable)
            && Objects.equal(this.extractable, that.extractable)
            && Objects.equal(this.featured, that.featured)
            && Objects.equal(this.ispublic, that.ispublic)
            && Objects.equal(this.ready, that.ready)
            && Objects.equal(this.passwordEnabled, that.passwordEnabled)
            && Objects.equal(this.jobId, that.jobId)
            && Objects.equal(this.jobStatus, that.jobStatus)
            && Objects.equal(this.checksum, that.checksum)
            && Objects.equal(this.hostId, that.hostId)
            && Objects.equal(this.hostName, that.hostName)
            && Objects.equal(this.sourceTemplateId, that.sourceTemplateId)
            && Objects.equal(this.templateTag, that.templateTag);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("id", id).add("displayText", displayText).add("domain", domain).add("domainId", domainId).add("account", account).add("accountId", accountId).add("zone", zone).add("zoneId", zoneId).add("OSType", OSType).add("OSTypeId", OSTypeId).add("name", name).add("type", type).add("status", status).add("format", format).add("hypervisor", hypervisor).add("size", size).add("created", created).add("removed", removed).add("crossZones", crossZones).add("bootable", bootable).add("extractable", extractable).add("featured", featured).add("ispublic", ispublic).add("ready", ready).add("passwordEnabled", passwordEnabled).add("jobId", jobId).add("jobStatus", jobStatus).add("checksum", checksum).add("hostId", hostId).add("hostName", hostName).add("sourceTemplateId", sourceTemplateId).add("templateTag", templateTag);
   }

   @Override
   public String toString() {
      return string().toString();
   }


   @Override
   public int compareTo(Template o) {
      return id.compareTo(o.getId());
   }

}
