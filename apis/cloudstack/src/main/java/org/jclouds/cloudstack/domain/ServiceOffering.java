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
import java.util.Set;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.gson.annotations.SerializedName;

/**
 * @author Adrian Cole
 */
public class ServiceOffering implements Comparable<ServiceOffering> {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {

      private String id;
      private String name;
      private String displayText;
      private Date created;
      private String domain;
      private String domainId;
      private int cpuNumber;
      private int cpuSpeed;
      private int memory;
      private boolean haSupport;
      private StorageType storageType;
      private boolean defaultUse;
      private String hostTags;
      private boolean systemOffering;
      private boolean cpuUseLimited;
      private long networkRate;
      private boolean systemVmType;

      private Set<String> tags = ImmutableSet.of();

      public Builder id(String id) {
         this.id = id;
         return this;
      }

      public Builder name(String name) {
         this.name = name;
         return this;
      }

      public Builder displayText(String displayText) {
         this.displayText = displayText;
         return this;
      }

      public Builder created(Date created) {
         this.created = created;
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

      public Builder cpuNumber(int cpuNumber) {
         this.cpuNumber = cpuNumber;
         return this;
      }

      public Builder cpuSpeed(int cpuSpeed) {
         this.cpuSpeed = cpuSpeed;
         return this;
      }

      public Builder memory(int memory) {
         this.memory = memory;
         return this;
      }

      public Builder haSupport(boolean haSupport) {
         this.haSupport = haSupport;
         return this;
      }

      public Builder storageType(StorageType storageType) {
         this.storageType = storageType;
         return this;
      }

      public Builder tags(Set<String> tags) {
         this.tags = ImmutableSet.copyOf(checkNotNull(tags, "tags"));
         return this;
      }

      public Builder defaultUse(boolean defaultUse) {
         this.defaultUse = defaultUse;
         return this;
      }

      public Builder hostTags(String hostTags) {
         this.hostTags = hostTags;
         return this;
      }

      public Builder systemOffering(boolean systemOffering) {
         this.systemOffering = systemOffering;
         return this;
      }

      public Builder cpuUseLimited(boolean cpuUseLimited) {
         this.cpuUseLimited = cpuUseLimited;
         return this;
      }

      public Builder networkRate(long networkRate) {
         this.networkRate = networkRate;
         return this;
      }

      public Builder systemVmType(boolean systemVmType) {
         this.systemVmType = systemVmType;
         return this;
      }


      public ServiceOffering build() {
         return new ServiceOffering(id, name, displayText, created, domain, domainId, cpuNumber, cpuSpeed, memory,
               haSupport, storageType, tags, defaultUse, hostTags, systemOffering, cpuUseLimited, networkRate,
               systemVmType);
      }
   }

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
   @SerializedName("defaultuse")
   private boolean defaultUse;
   @SerializedName("hosttags")
   private String hostTags;
   @SerializedName("issystem")
   private boolean systemOffering;
   @SerializedName("limitcpuuse")
   private boolean cpuUseLimited;
   @SerializedName("networkrate")
   private long networkRate;
   @SerializedName("systemvmtype")
   private boolean systemVmType;


   public ServiceOffering(String id, String name, String displayText, Date created, String domain, String domainId,
                          int cpuNumber, int cpuSpeed, int memory, boolean haSupport, StorageType storageType, Set<String> tags,
                          boolean defaultUse, String hostTags, boolean systemOffering, boolean cpuUseLimited, long networkRate,
                          boolean systemVmType) {
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
      this.tags = tags.size() == 0 ? null : Joiner.on(',').join(tags);
   }

   /**
    * present only for serializer
    */
   ServiceOffering() {

   }

   /**
    * @return the id of the service offering
    */
   public String getId() {
      return id;
   }

   /**
    * @return the name of the service offering
    */

   public String getName() {
      return name;
   }

   /**
    * @return an alternate display text of the service offering.
    */
   public String getDisplayText() {
      return displayText;
   }

   /**
    * @return the date this service offering was created
    */
   public Date getCreated() {
      return created;
   }

   /**
    * @return Domain name for the offering
    */
   public String getDomain() {
      return domain;
   }

   /**
    * @return the domain id of the service offering
    */
   public String getDomainId() {
      return domainId;
   }

   /**
    * @return the number of CPU
    */
   public int getCpuNumber() {
      return cpuNumber;
   }

   /**
    * @return the clock rate CPU speed in Mhz
    */
   public int getCpuSpeed() {
      return cpuSpeed;
   }

   /**
    * @return the memory in MB
    */
   public int getMemory() {
      return memory;
   }

   /**
    * @return the ha support in the service offering
    */
   public boolean supportsHA() {
      return haSupport;
   }

   /**
    * @return the storage type for this service offering
    */
   public StorageType getStorageType() {
      return storageType;
   }

   /**
    * @return whether this is a default system vm offering
    */
   public boolean isDefaultUse() {
      return defaultUse;
   }

   /**
    * @return the host tag for the service offering
    */
   public String getHostTags() {
      return hostTags;
   }

   /**
    * @return whether this is a system vm offering
    */
   public boolean isSystemOffering() {
      return systemOffering;
   }

   /**
    * @return whether restrict the CPU usage to committed service offering
    */
   public boolean isCpuUseLimited() {
      return cpuUseLimited;
   }

   /**
    * @return data transfer rate in megabits per second allowed.
    */
   public long getNetworkRate() {
      return networkRate;
   }

   /**
    * @return whether this is a the systemvm type for system vm offering
    */
   public boolean isSystemVmType() {
      return systemVmType;
   }

   /**
    * @return the tags for the service offering
    */
   public Set<String> getTags() {
      return tags != null ? ImmutableSet.copyOf(Splitter.on(',').split(tags)) : ImmutableSet.<String>of();
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      ServiceOffering that = (ServiceOffering) o;

      if (!Objects.equal(cpuNumber, that.cpuNumber)) return false;
      if (!Objects.equal(cpuSpeed, that.cpuSpeed)) return false;
      if (!Objects.equal(created, that.created)) return false;
      if (!Objects.equal(displayText, that.displayText)) return false;
      if (!Objects.equal(domain, that.domain)) return false;
      if (!Objects.equal(domainId, that.domainId)) return false;
      if (!Objects.equal(haSupport, that.haSupport)) return false;
      if (!Objects.equal(id, that.id)) return false;
      if (!Objects.equal(memory, that.memory)) return false;
      if (!Objects.equal(name, that.name)) return false;
      if (!Objects.equal(storageType, that.storageType)) return false;
      if (!Objects.equal(tags, that.tags)) return false;

      return true;
   }

   @Override
   public int hashCode() {
       return Objects.hashCode(cpuNumber, cpuSpeed, created, displayText, domain, domainId, haSupport, id, memory, name, storageType, tags);
   }

   @Override
   public String toString() {
      return "ServiceOffering{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", displayText='" + displayText + '\'' +
            ", created=" + created +
            ", domain='" + domain + '\'' +
            ", domainId=" + domainId +
            ", cpuNumber=" + cpuNumber +
            ", cpuSpeed=" + cpuSpeed +
            ", memory=" + memory +
            ", haSupport=" + haSupport +
            ", storageType=" + storageType +
            ", tags='" + tags + '\'' +
            ", defaultUse=" + defaultUse +
            ", hostTags='" + hostTags + '\'' +
            ", systemOffering=" + systemOffering +
            ", cpuUseLimited=" + cpuUseLimited +
            ", networkRate=" + networkRate +
            ", systemVmType=" + systemVmType +
            '}';
   }

   @Override
   public int compareTo(ServiceOffering arg0) {
      return id.compareTo(arg0.getId());
   }

}
