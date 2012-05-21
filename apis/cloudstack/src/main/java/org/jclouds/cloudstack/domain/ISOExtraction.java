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

import java.util.Date;

import com.google.common.base.Objects;
import com.google.gson.annotations.SerializedName;

/**
 * @author Richard Downer
 */
public class ISOExtraction implements Comparable<ISOExtraction> {

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {

      private String id;
      private String accountId;
      private Date created;
      private String extractId;
      private ExtractMode extractMode;
      private String name;
      private String state;
      private String status;
      private String storageType;
      private int uploadPercentage;
      private String url;
      private String zoneId;
      private String zoneName;

      /**
       * @param id the id of extracted object
       */
      public Builder id(String id) {
         this.id = id;
         return this;
      }

      /**
       * @param accountId the account id to which the extracted object belongs
       */
      public Builder accountId(String accountId) {
         this.accountId = accountId;
         return this;
      }

      /**
       * @param created the time and date the object was created
       */
      public Builder created(Date created) {
         this.created = created;
         return this;
      }

      /**
       * @param extractId the upload id of extracted object
       */
      public Builder extractId(String extractId) {
         this.extractId = extractId;
         return this;
      }

      /**
       * @param extractMode the mode of extraction - upload or download
       */
      public Builder extractMode(ExtractMode extractMode) {
         this.extractMode = extractMode;
         return this;
      }

      /**
       * @param name the name of the extracted object
       */
      public Builder name(String name) {
         this.name = name;
         return this;
      }

      /**
       * @param state the state of the extracted object
       */
      public Builder state(String state) {
         this.state = state;
         return this;
      }

      /**
       * @param status the status of the extraction
       */
      public Builder status(String status) {
         this.status = status;
         return this;
      }

      /**
       * @param storageType type of the storage
       */
      public Builder storageType(String storageType) {
         this.storageType = storageType;
         return this;
      }

      /**
       * @param uploadPercentage the percentage of the entity uploaded to the specified location
       */
      public Builder uploadPercentage(int uploadPercentage) {
         this.uploadPercentage = uploadPercentage;
         return this;
      }

      /**
       * @param url if mode = upload then url of the uploaded entity. if mode = download the url from which the entity can be downloaded
       */
      public Builder url(String url) {
         this.url = url;
         return this;
      }

      /**
       * @param zoneId zone ID the object was extracted from
       */
      public Builder zoneId(String zoneId) {
         this.zoneId = zoneId;
         return this;
      }

      /**
       * @param zoneName zone name the object was extracted from
       */
      public Builder zoneName(String zoneName) {
         this.zoneName = zoneName;
         return this;
      }

   }

   private String id;
   @SerializedName("accountid")
   private String accountId;
   private Date created;
   private String extractId;
   private ExtractMode extractMode;
   private String name;
   private String state;
   private String status;
   @SerializedName("storagetype")
   private String storageType;
   @SerializedName("uploadpercentage")
   private int uploadPercentage;
   private String url;
   @SerializedName("zoneid")
   private String zoneId;
   @SerializedName("zonename")
   private String zoneName;

   /**
    * present only for serializer
    */
   ISOExtraction() {
   }

   /**
    * @return the id of extracted object
    */
   public String getId() {
      return id;
   }

   /**
    * @return the account id to which the extracted object belongs
    */
   public String getAccountId() {
      return accountId;
   }

   /**
    * @return the time and date the object was created
    */
   public Date getCreated() {
      return created;
   }

   /**
    * @return the upload id of extracted object
    */
   public String getExtractId() {
      return extractId;
   }

   /**
    * @return the mode of extraction - upload or download
    */
   public ExtractMode getExtractMode() {
      return extractMode;
   }

   /**
    * @return the name of the extracted object
    */
   public String getName() {
      return name;
   }

   /**
    * @return the state of the extracted object
    */
   public String getState() {
      return state;
   }

   /**
    * @return the status of the extraction
    */
   public String getStatus() {
      return status;
   }

   /**
    * @return type of the storage
    */
   public String getStorageType() {
      return storageType;
   }

   /**
    * @return the percentage of the entity uploaded to the specified location
    */
   public int getUploadPercentage() {
      return uploadPercentage;
   }

   /**
    * @return if mode = upload then url of the uploaded entity. if mode = download the url from which the entity can be downloaded
    */
   public String getUrl() {
      return url;
   }

   /**
    * @return zone ID the object was extracted from
    */
   public String getZoneId() {
      return zoneId;
   }

   /**
    * @return zone name the object was extracted from
    */
   public String getZoneName() {
      return zoneName;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      ISOExtraction that = (ISOExtraction) o;

      if (!Objects.equal(accountId, that.accountId)) return false;
      if (!Objects.equal(extractId, that.extractId)) return false;
      if (!Objects.equal(id, that.id)) return false;
      if (!Objects.equal(uploadPercentage, that.uploadPercentage)) return false;
      if (!Objects.equal(zoneId, that.zoneId)) return false;
      if (!Objects.equal(created, that.created)) return false;
      if (!Objects.equal(extractMode, that.extractMode)) return false;
      if (!Objects.equal(name, that.name)) return false;
      if (!Objects.equal(state, that.state)) return false;
      if (!Objects.equal(status, that.status)) return false;
      if (!Objects.equal(storageType, that.storageType)) return false;
      if (!Objects.equal(url, that.url)) return false;
      if (!Objects.equal(zoneName, that.zoneName)) return false;

      return true;
   }

   @Override
   public int hashCode() {
       return Objects.hashCode(accountId, extractId, id, uploadPercentage, zoneId, created, extractMode, name, state, status, storageType, url, zoneName);
   }

   @Override
   public String toString() {
      return "ISOExtraction{" +
            "id=" + id +
            ", accountId=" + accountId +
            ", created=" + created +
            ", extractId=" + extractId +
            ", extractMode=" + extractMode +
            ", name='" + name + '\'' +
            ", state='" + state + '\'' +
            ", status='" + status + '\'' +
            ", storageType='" + storageType + '\'' +
            ", uploadPercentage=" + uploadPercentage +
            ", url='" + url + '\'' +
            ", zoneId=" + zoneId +
            ", zoneName='" + zoneName + '\'' +
            '}';
   }

   @Override
   public int compareTo(ISOExtraction other) {
      return id.compareTo(other.getId());
   }

}
