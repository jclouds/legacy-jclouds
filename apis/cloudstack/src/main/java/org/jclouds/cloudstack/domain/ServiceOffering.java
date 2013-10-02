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
import java.util.Set;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableSet;

/**
 * Class ServiceOffering
 *
 * @author Adrian Cole
 */
public class ServiceOffering implements Comparable<ServiceOffering> {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromServiceOffering(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected String id;
      protected String name;
      protected String displayText;
      protected Date created;
      protected String domain;
      protected String domainId;
      protected int cpuNumber;
      protected int cpuSpeed;
      protected int memory;
      protected boolean haSupport;
      protected StorageType storageType;
      protected boolean defaultUse;
      protected boolean systemOffering;
      protected boolean cpuUseLimited;
      protected long networkRate;
      protected boolean systemVmType;
      protected ImmutableSet.Builder<String> tags = ImmutableSet.<String>builder();

      /**
       * @see ServiceOffering#getId()
       */
      public T id(String id) {
         this.id = id;
         return self();
      }

      /**
       * @see ServiceOffering#getName()
       */
      public T name(String name) {
         this.name = name;
         return self();
      }

      /**
       * @see ServiceOffering#getDisplayText()
       */
      public T displayText(String displayText) {
         this.displayText = displayText;
         return self();
      }

      /**
       * @see ServiceOffering#getCreated()
       */
      public T created(Date created) {
         this.created = created;
         return self();
      }

      /**
       * @see ServiceOffering#getDomain()
       */
      public T domain(String domain) {
         this.domain = domain;
         return self();
      }

      /**
       * @see ServiceOffering#getDomainId()
       */
      public T domainId(String domainId) {
         this.domainId = domainId;
         return self();
      }

      /**
       * @see ServiceOffering#getCpuNumber()
       */
      public T cpuNumber(int cpuNumber) {
         this.cpuNumber = cpuNumber;
         return self();
      }

      /**
       * @see ServiceOffering#getCpuSpeed()
       */
      public T cpuSpeed(int cpuSpeed) {
         this.cpuSpeed = cpuSpeed;
         return self();
      }

      /**
       * @see ServiceOffering#getMemory()
       */
      public T memory(int memory) {
         this.memory = memory;
         return self();
      }

      /**
       * @see ServiceOffering#supportsHA()
       */
      public T supportsHA(boolean haSupport) {
         this.haSupport = haSupport;
         return self();
      }

      /**
       * @see ServiceOffering#getStorageType()
       */
      public T storageType(StorageType storageType) {
         this.storageType = storageType;
         return self();
      }

      /**
       * @see ServiceOffering#getTags()
       */
      public T tags(Iterable<String> tags) {
         this.tags = ImmutableSet.<String>builder().addAll(tags);
         return self();
      }
      
      /**
       * @see ServiceOffering#getTags()
       */
      public T tag(String tag) {
         this.tags.add(tag);
         return self();
      }
      
      /**
       * @see ServiceOffering#isDefaultUse()
       */
      public T defaultUse(boolean defaultUse) {
         this.defaultUse = defaultUse;
         return self();
      }

      /**
       * @see ServiceOffering#isSystemOffering()
       */
      public T systemOffering(boolean systemOffering) {
         this.systemOffering = systemOffering;
         return self();
      }

      /**
       * @see ServiceOffering#isCpuUseLimited()
       */
      public T cpuUseLimited(boolean cpuUseLimited) {
         this.cpuUseLimited = cpuUseLimited;
         return self();
      }

      /**
       * @see ServiceOffering#getNetworkRate()
       */
      public T networkRate(long networkRate) {
         this.networkRate = networkRate;
         return self();
      }

      /**
       * @see ServiceOffering#isSystemVmType()
       */
      public T systemVmType(boolean systemVmType) {
         this.systemVmType = systemVmType;
         return self();
      }

      public ServiceOffering build() {
         return new ServiceOffering(id, name, displayText, created, domain, domainId, cpuNumber, cpuSpeed, memory, haSupport, storageType,
               tags.build(), defaultUse, systemOffering, cpuUseLimited, networkRate, systemVmType);
      }

      public T fromServiceOffering(ServiceOffering in) {
         return this
               .id(in.getId())
               .name(in.getName())
               .displayText(in.getDisplayText())
               .created(in.getCreated())
               .domain(in.getDomain())
               .domainId(in.getDomainId())
               .cpuNumber(in.getCpuNumber())
               .cpuSpeed(in.getCpuSpeed())
               .memory(in.getMemory())
               .supportsHA(in.supportsHA())
               .storageType(in.getStorageType())
               .tags(in.getTags())
               .defaultUse(in.isDefaultUse())
               .systemOffering(in.isSystemOffering())
               .cpuUseLimited(in.isCpuUseLimited())
               .networkRate(in.getNetworkRate())
               .systemVmType(in.isSystemVmType());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String id;
   private final String name;
   private final String displayText;
   private final Date created;
   private final String domain;
   private final String domainId;
   private final int cpuNumber;
   private final int cpuSpeed;
   private final int memory;
   private final boolean haSupport;
   private final StorageType storageType;
   private final Set<String> tags;
   private final boolean defaultUse;
   private final boolean systemOffering;
   private final boolean cpuUseLimited;
   private final long networkRate;
   private final boolean systemVmType;

   @ConstructorProperties({
         "id", "name", "displaytext", "created", "domain", "domainid", "cpunumber", "cpuspeed", "memory", "offerha", "storagetype", "tags", "defaultuse", "issystem", "limitcpuuse", "networkrate", "systemvmtype"
   })
   protected ServiceOffering(String id, @Nullable String name, @Nullable String displayText, @Nullable Date created,
                             @Nullable String domain, @Nullable String domainId, int cpuNumber, int cpuSpeed, int memory,
                             boolean haSupport, @Nullable StorageType storageType, @Nullable Iterable<String> tags, boolean defaultUse,
                             boolean systemOffering, boolean cpuUseLimited, long networkRate, boolean systemVmType) {
      this.id = checkNotNull(id, "id");
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
      this.tags = tags != null ? ImmutableSet.copyOf(tags) : ImmutableSet.<String> of();
      this.defaultUse = defaultUse;
      this.systemOffering = systemOffering;
      this.cpuUseLimited = cpuUseLimited;
      this.networkRate = networkRate;
      this.systemVmType = systemVmType;
   }

   /**
    * @return the id of the service offering
    */
   public String getId() {
      return this.id;
   }

   /**
    * @return the name of the service offering
    */
   public String getName() {
      return this.name;
   }

   /**
    * @return an alternate display text of the service offering.
    */
   @Nullable
   public String getDisplayText() {
      return this.displayText;
   }

   /**
    * @return the date this service offering was created
    */
   @Nullable
   public Date getCreated() {
      return this.created;
   }

   /**
    * @return Domain name for the offering
    */
   @Nullable
   public String getDomain() {
      return this.domain;
   }

   /**
    * @return the domain id of the service offering
    */
   @Nullable
   public String getDomainId() {
      return this.domainId;
   }

   /**
    * @return the number of CPU
    */
   public int getCpuNumber() {
      return this.cpuNumber;
   }

   /**
    * @return the clock rate CPU speed in Mhz
    */
   public int getCpuSpeed() {
      return this.cpuSpeed;
   }

   /**
    * @return the memory in MB
    */
   public int getMemory() {
      return this.memory;
   }

   public boolean supportsHA() {
      return this.haSupport;
   }

   /**
    * @return the storage type for this service offering
    */
   @Nullable
   public StorageType getStorageType() {
      return this.storageType;
   }

   /**
    * @return the tags for the service offering
    */
   public Set<String> getTags() {
      return tags;
   }

   /**
    * @return whether this is a default system vm offering
    */
   public boolean isDefaultUse() {
      return this.defaultUse;
   }

   /**
    * @return whether this is a system vm offering
    */
   public boolean isSystemOffering() {
      return this.systemOffering;
   }

   /**
    * @return whether restrict the CPU usage to committed service offering
    */
   public boolean isCpuUseLimited() {
      return this.cpuUseLimited;
   }

   /**
    * @return data transfer rate in megabits per second allowed.
    */
   public long getNetworkRate() {
      return this.networkRate;
   }

   /**
    * @return whether this is a the systemvm type for system vm offering
    */
   public boolean isSystemVmType() {
      return this.systemVmType;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, name, displayText, created, domain, domainId, cpuNumber, cpuSpeed, memory, haSupport, storageType, tags, defaultUse, systemOffering, cpuUseLimited, networkRate, systemVmType);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      ServiceOffering that = ServiceOffering.class.cast(obj);
      return Objects.equal(this.id, that.id)
            && Objects.equal(this.name, that.name)
            && Objects.equal(this.displayText, that.displayText)
            && Objects.equal(this.created, that.created)
            && Objects.equal(this.domain, that.domain)
            && Objects.equal(this.domainId, that.domainId)
            && Objects.equal(this.cpuNumber, that.cpuNumber)
            && Objects.equal(this.cpuSpeed, that.cpuSpeed)
            && Objects.equal(this.memory, that.memory)
            && Objects.equal(this.haSupport, that.haSupport)
            && Objects.equal(this.storageType, that.storageType)
            && Objects.equal(this.getTags(), that.getTags())
            && Objects.equal(this.defaultUse, that.defaultUse)
            && Objects.equal(this.systemOffering, that.systemOffering)
            && Objects.equal(this.cpuUseLimited, that.cpuUseLimited)
            && Objects.equal(this.networkRate, that.networkRate)
            && Objects.equal(this.systemVmType, that.systemVmType);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("id", id).add("name", name).add("displayText", displayText).add("created", created).add("domain", domain)
            .add("domainId", domainId).add("cpuNumber", cpuNumber).add("cpuSpeed", cpuSpeed).add("memory", memory)
            .add("haSupport", haSupport).add("storageType", storageType).add("tags", getTags()).add("defaultUse", defaultUse)
            .add("systemOffering", systemOffering).add("cpuUseLimited", cpuUseLimited)
            .add("networkRate", networkRate).add("systemVmType", systemVmType);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   @Override
   public int compareTo(ServiceOffering o) {
      return id.compareTo(o.getId());
   }

}
