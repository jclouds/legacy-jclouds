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
 * @author Richard Downer
 */
public class TemplateExtraction implements Comparable<TemplateExtraction> {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromTemplateExtraction(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected String id;
      protected String accountId;
      protected Date created;
      protected String extractId;
      protected ExtractMode extractMode;
      protected String name;
      protected String state;
      protected String status;
      protected String storageType;
      protected int uploadPercentage;
      protected String url;
      protected String zoneId;
      protected String zoneName;

      /**
       * @see TemplateExtraction#getId()
       */
      public T id(String id) {
         this.id = id;
         return self();
      }

      /**
       * @see TemplateExtraction#getAccountId()
       */
      public T accountId(String accountId) {
         this.accountId = accountId;
         return self();
      }

      /**
       * @see TemplateExtraction#getCreated()
       */
      public T created(Date created) {
         this.created = created;
         return self();
      }

      /**
       * @see TemplateExtraction#getExtractId()
       */
      public T extractId(String extractId) {
         this.extractId = extractId;
         return self();
      }

      /**
       * @see TemplateExtraction#getExtractMode()
       */
      public T extractMode(ExtractMode extractMode) {
         this.extractMode = extractMode;
         return self();
      }

      /**
       * @see TemplateExtraction#getName()
       */
      public T name(String name) {
         this.name = name;
         return self();
      }

      /**
       * @see TemplateExtraction#getState()
       */
      public T state(String state) {
         this.state = state;
         return self();
      }

      /**
       * @see TemplateExtraction#getStatus()
       */
      public T status(String status) {
         this.status = status;
         return self();
      }

      /**
       * @see TemplateExtraction#getStorageType()
       */
      public T storageType(String storageType) {
         this.storageType = storageType;
         return self();
      }

      /**
       * @see TemplateExtraction#getUploadPercentage()
       */
      public T uploadPercentage(int uploadPercentage) {
         this.uploadPercentage = uploadPercentage;
         return self();
      }

      /**
       * @see TemplateExtraction#getUrl()
       */
      public T url(String url) {
         this.url = url;
         return self();
      }

      /**
       * @see TemplateExtraction#getZoneId()
       */
      public T zoneId(String zoneId) {
         this.zoneId = zoneId;
         return self();
      }

      /**
       * @see TemplateExtraction#getZoneName()
       */
      public T zoneName(String zoneName) {
         this.zoneName = zoneName;
         return self();
      }

      public TemplateExtraction build() {
         return new TemplateExtraction(id, accountId, created, extractId, extractMode, name, state, status, storageType, uploadPercentage, url, zoneId, zoneName);
      }

      public T fromTemplateExtraction(TemplateExtraction in) {
         return this
               .id(in.getId())
               .accountId(in.getAccountId())
               .created(in.getCreated())
               .extractId(in.getExtractId())
               .extractMode(in.getExtractMode())
               .name(in.getName())
               .state(in.getState())
               .status(in.getStatus())
               .storageType(in.getStorageType())
               .uploadPercentage(in.getUploadPercentage())
               .url(in.getUrl())
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
   private final String accountId;
   private final Date created;
   private final String extractId;
   private final ExtractMode extractMode;
   private final String name;
   private final String state;
   private final String status;
   private final String storageType;
   private final int uploadPercentage;
   private final String url;
   private final String zoneId;
   private final String zoneName;

   @ConstructorProperties({
         "id", "accountid", "created", "extractId", "extractMode", "name", "state", "status", "storagetype", "uploadpercentage", "url", "zoneid", "zonename"
   })
   protected TemplateExtraction(String id, @Nullable String accountId, @Nullable Date created, @Nullable String extractId,
                                @Nullable ExtractMode extractMode, @Nullable String name, @Nullable String state, @Nullable String status,
                                @Nullable String storageType, int uploadPercentage, @Nullable String url, @Nullable String zoneId,
                                @Nullable String zoneName) {
      this.id = checkNotNull(id, "id");
      this.accountId = accountId;
      this.created = created;
      this.extractId = extractId;
      this.extractMode = extractMode;
      this.name = name;
      this.state = state;
      this.status = status;
      this.storageType = storageType;
      this.uploadPercentage = uploadPercentage;
      this.url = url;
      this.zoneId = zoneId;
      this.zoneName = zoneName;
   }

   /**
    * @return the id of extracted object
    */
   public String getId() {
      return this.id;
   }

   /**
    * @return the account id to which the extracted object belongs
    */
   @Nullable
   public String getAccountId() {
      return this.accountId;
   }

   /**
    * @return the time and date the object was created
    */
   @Nullable
   public Date getCreated() {
      return this.created;
   }

   /**
    * @return the upload id of extracted object
    */
   @Nullable
   public String getExtractId() {
      return this.extractId;
   }

   /**
    * @return the mode of extraction - upload or download
    */
   @Nullable
   public ExtractMode getExtractMode() {
      return this.extractMode;
   }

   /**
    * @return the name of the extracted object
    */
   @Nullable
   public String getName() {
      return this.name;
   }

   /**
    * @return the state of the extracted object
    */
   @Nullable
   public String getState() {
      return this.state;
   }

   /**
    * @return the status of the extraction
    */
   @Nullable
   public String getStatus() {
      return this.status;
   }

   /**
    * @return type of the storage
    */
   @Nullable
   public String getStorageType() {
      return this.storageType;
   }

   /**
    * @return the percentage of the entity uploaded to the specified location
    */
   public int getUploadPercentage() {
      return this.uploadPercentage;
   }

   /**
    * @return if mode = upload then url of the uploaded entity. if mode = download the url from which the entity can be downloaded
    */
   @Nullable
   public String getUrl() {
      return this.url;
   }

   /**
    * @return zone ID the object was extracted from
    */
   @Nullable
   public String getZoneId() {
      return this.zoneId;
   }

   /**
    * @return zone name the object was extracted from
    */
   @Nullable
   public String getZoneName() {
      return this.zoneName;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, accountId, created, extractId, extractMode, name, state, status, storageType, uploadPercentage, url, zoneId, zoneName);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      TemplateExtraction that = TemplateExtraction.class.cast(obj);
      return Objects.equal(this.id, that.id)
            && Objects.equal(this.accountId, that.accountId)
            && Objects.equal(this.created, that.created)
            && Objects.equal(this.extractId, that.extractId)
            && Objects.equal(this.extractMode, that.extractMode)
            && Objects.equal(this.name, that.name)
            && Objects.equal(this.state, that.state)
            && Objects.equal(this.status, that.status)
            && Objects.equal(this.storageType, that.storageType)
            && Objects.equal(this.uploadPercentage, that.uploadPercentage)
            && Objects.equal(this.url, that.url)
            && Objects.equal(this.zoneId, that.zoneId)
            && Objects.equal(this.zoneName, that.zoneName);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("id", id).add("accountId", accountId).add("created", created).add("extractId", extractId).add("extractMode", extractMode).add("name", name).add("state", state).add("status", status).add("storageType", storageType).add("uploadPercentage", uploadPercentage).add("url", url).add("zoneId", zoneId).add("zoneName", zoneName);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   @Override
   public int compareTo(TemplateExtraction other) {
      return id.compareTo(other.id);
   }

}
