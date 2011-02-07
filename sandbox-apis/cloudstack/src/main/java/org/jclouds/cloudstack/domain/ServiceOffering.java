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
import java.util.Set;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Adrian Cole
 */
public class ServiceOffering {
   private String id;
   private String name;
   @SerializedName("displaytext")
   private String displayText;
   private Date created;
   private String domain;
   @SerializedName("domainid")
   private String domainId;
   @SerializedName("cpunumber")
   private int cpuNumber;
   @SerializedName("cpuspeed")
   private int cpuSpeed;
   private int memory;
   @SerializedName("offerha")
   private boolean haSupport;
   @SerializedName("storagetype")
   private StorageType storageType;
   private String tags;

   public ServiceOffering(String id, String name, String displayText, Date created, String domain, String domainId,
            int cpuNumber, int cpuSpeed, int memory, boolean haSupport, StorageType storageType, Set<String> tags) {
      this.id = id;
      this.name = name;
      this.displayText = displayText;
      this.created = created;
      this.domain = domain;
      this.domainId = domainId;
      this.cpuNumber = cpuNumber;
      this.cpuSpeed = cpuSpeed;
      this.memory = memory;
      this.haSupport = haSupport;
      this.storageType = storageType;
      this.tags = Joiner.on(',').join(tags);
   }

   /**
    * present only for serializer
    * 
    */
   ServiceOffering() {

   }

   /**
    * 
    * @return the id of the service offering
    */
   public String getId() {
      return id;
   }

   /**
    * 
    * @return the name of the service offering
    */

   public String getName() {
      return name;
   }

   /**
    * 
    * @return an alternate display text of the service offering.
    */
   public String getDisplayText() {
      return displayText;
   }

   /**
    * 
    * @return the date this service offering was created
    */
   public Date getCreated() {
      return created;
   }

   /**
    * 
    * @return Domain name for the offering
    */
   public String getDomain() {
      return domain;
   }

   /**
    * 
    * @return the domain id of the service offering
    */
   public String getDomainId() {
      return domainId;
   }

   /**
    * 
    * @return the number of CPU
    */
   public int getCpuNumber() {
      return cpuNumber;
   }

   /**
    * 
    * @return the clock rate CPU speed in Mhz
    */
   public int getCpuSpeed() {
      return cpuSpeed;
   }

   /**
    * 
    * @return the memory in MB
    */
   public int getMemory() {
      return memory;
   }

   /**
    * 
    * @return the ha support in the service offering
    */
   public boolean supportsHA() {
      return haSupport;
   }

   /**
    * 
    * @return the storage type for this service offering
    */
   public StorageType getStorageType() {
      return storageType;
   }

   /**
    * 
    * @return the tags for the service offering
    */
   public Set<String> getTags() {
      return tags != null ? ImmutableSet.copyOf(Splitter.on(',').split(tags)) : ImmutableSet.<String> of();
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + cpuNumber;
      result = prime * result + cpuSpeed;
      result = prime * result + ((created == null) ? 0 : created.hashCode());
      result = prime * result + ((displayText == null) ? 0 : displayText.hashCode());
      result = prime * result + ((domain == null) ? 0 : domain.hashCode());
      result = prime * result + ((domainId == null) ? 0 : domainId.hashCode());
      result = prime * result + (haSupport ? 1231 : 1237);
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      result = prime * result + memory;
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((storageType == null) ? 0 : storageType.hashCode());
      result = prime * result + ((tags == null) ? 0 : tags.hashCode());
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
      ServiceOffering other = (ServiceOffering) obj;
      if (cpuNumber != other.cpuNumber)
         return false;
      if (cpuSpeed != other.cpuSpeed)
         return false;
      if (created == null) {
         if (other.created != null)
            return false;
      } else if (!created.equals(other.created))
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
      if (haSupport != other.haSupport)
         return false;
      if (id == null) {
         if (other.id != null)
            return false;
      } else if (!id.equals(other.id))
         return false;
      if (memory != other.memory)
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (storageType == null) {
         if (other.storageType != null)
            return false;
      } else if (!storageType.equals(other.storageType))
         return false;
      if (tags == null) {
         if (other.tags != null)
            return false;
      } else if (!tags.equals(other.tags))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[id=" + id + ", name=" + name + ", displayText=" + displayText + ", created=" + created + ", cpuNumber="
               + cpuNumber + ", cpuSpeed=" + cpuSpeed + ", memory=" + memory + ", storageType=" + storageType
               + ", haSupport=" + haSupport + ", domain=" + domain + ", domainId=" + domainId + ", tags=" + tags + "]";
   }

}
