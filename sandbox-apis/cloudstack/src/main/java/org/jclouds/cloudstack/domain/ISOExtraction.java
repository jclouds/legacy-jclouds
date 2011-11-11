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

/**
 * @author Richard Downer
 */
public class ISOExtraction implements Comparable<ISOExtraction> {

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {

      private long id;
      private long accountId;
      private Date created;
      private long extractId;
      private ExtractMode extractMode;
      private String name;
      private String state;
      private String status;
      private String storageType;
      private int uploadPercentage;
      private String url;
      private long zoneId;
      private String zoneName;

      /**
       * @param id the id of extracted object
       */
      public Builder id(long id) {
         this.id = id;
         return this;
      }

      /**
       * @param accountId the account id to which the extracted object belongs
       */
      public Builder accountId(long accountId) {
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
      public Builder extractId(long extractId) {
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
      public Builder zoneId(long zoneId) {
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

   private long id;
   @SerializedName("accountid")
   private long accountId;
   private Date created;
   private long extractId;
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
   private long zoneId;
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
   public long getId() {
      return id;
   }

   /**
    * @return the account id to which the extracted object belongs
    */
   public long getAccountId() {
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
   public long getExtractId() {
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
   public long getZoneId() {
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

      if (accountId != that.accountId) return false;
      if (extractId != that.extractId) return false;
      if (id != that.id) return false;
      if (uploadPercentage != that.uploadPercentage) return false;
      if (zoneId != that.zoneId) return false;
      if (created != null ? !created.equals(that.created) : that.created != null) return false;
      if (extractMode != that.extractMode) return false;
      if (name != null ? !name.equals(that.name) : that.name != null) return false;
      if (state != null ? !state.equals(that.state) : that.state != null) return false;
      if (status != null ? !status.equals(that.status) : that.status != null) return false;
      if (storageType != null ? !storageType.equals(that.storageType) : that.storageType != null) return false;
      if (url != null ? !url.equals(that.url) : that.url != null) return false;
      if (zoneName != null ? !zoneName.equals(that.zoneName) : that.zoneName != null) return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = (int) (id ^ (id >>> 32));
      result = 31 * result + (int) (accountId ^ (accountId >>> 32));
      result = 31 * result + (created != null ? created.hashCode() : 0);
      result = 31 * result + (int) (extractId ^ (extractId >>> 32));
      result = 31 * result + (extractMode != null ? extractMode.hashCode() : 0);
      result = 31 * result + (name != null ? name.hashCode() : 0);
      result = 31 * result + (state != null ? state.hashCode() : 0);
      result = 31 * result + (status != null ? status.hashCode() : 0);
      result = 31 * result + (storageType != null ? storageType.hashCode() : 0);
      result = 31 * result + uploadPercentage;
      result = 31 * result + (url != null ? url.hashCode() : 0);
      result = 31 * result + (int) (zoneId ^ (zoneId >>> 32));
      result = 31 * result + (zoneName != null ? zoneName.hashCode() : 0);
      return result;
   }

   @Override
   public String toString() {
      return "[" +
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
         ']';
   }

   @Override
   public int compareTo(ISOExtraction other) {
      return new Long(id).compareTo(other.getId());
   }

}
