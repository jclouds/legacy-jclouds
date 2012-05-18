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
 * 
 * @author Adrian Cole
 */
public class DiskOffering implements Comparable<DiskOffering> {

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
      private int diskSize;
      private boolean customized;
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

      public Builder diskSize(int diskSize) {
         this.diskSize = diskSize;
         return this;
      }

      public Builder customized(boolean customized) {
         this.customized = customized;
         return this;
      }

      public Builder tags(Set<String> tags) {
         this.tags = ImmutableSet.copyOf(checkNotNull(tags, "tags"));
         return this;
      }

      public DiskOffering build() {
         return new DiskOffering(id, name, displayText, created, domain, domainId, diskSize, customized, tags);
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
   @SerializedName("disksize")
   private int diskSize;
   @SerializedName("iscustomized")
   private boolean customized;
   private String tags;

   public DiskOffering(String id, String name, String displayText, Date created, String domain, String domainId,
         int diskSize, boolean customized, Set<String> tags) {
      this.id = id;
      this.name = name;
      this.displayText = displayText;
      this.created = created;
      this.domain = domain;
      this.domainId = domainId;
      this.diskSize = diskSize;
      this.customized = customized;
      this.tags = tags.size() == 0 ? null : Joiner.on(',').join(tags);
   }

   /**
    * present only for serializer
    * 
    */
   DiskOffering() {

   }

   /**
    * 
    * @return the id of the disk offering
    */
   public String getId() {
      return id;
   }

   /**
    * 
    * @return the name of the disk offering
    */

   public String getName() {
      return name;
   }

   /**
    * 
    * @return an alternate display text of the disk offering.
    */
   public String getDisplayText() {
      return displayText;
   }

   /**
    * 
    * @return the date this disk offering was created
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
    * @return the domain id of the disk offering
    */
   public String getDomainId() {
      return domainId;
   }

   /**
    * 
    * @return the size of the disk offering in GB
    */
   public int getDiskSize() {
      return diskSize;
   }

   /**
    * 
    * @return the ha support in the disk offering
    */
   public boolean isCustomized() {
      return customized;
   }

   /**
    * 
    * @return the tags for the disk offering
    */
   public Set<String> getTags() {
      return tags != null ? ImmutableSet.copyOf(Splitter.on(',').split(tags)) : ImmutableSet.<String> of();
   }

   @Override
   public int hashCode() {
       return Objects.hashCode(created, customized, diskSize, displayText, domain, domainId, id, name, tags);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      DiskOffering that = (DiskOffering) obj;

      if (!Objects.equal(created, that.created)) return false;
      if (!Objects.equal(customized, that.customized)) return false;
      if (!Objects.equal(diskSize, that.diskSize)) return false;
      if (!Objects.equal(displayText, that.displayText)) return false;
      if (!Objects.equal(domain, that.domain)) return false;
      if (!Objects.equal(domainId, that.domainId)) return false;
      if (!Objects.equal(id, that.id)) return false;
      if (!Objects.equal(name, that.name)) return false;
      if (!Objects.equal(tags, that.tags)) return false;

      return true;
   }

   @Override
   public String toString() {
      return "DiskOffering{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", displayText='" + displayText + '\'' +
            ", created=" + created +
            ", domain='" + domain + '\'' +
            ", domainId=" + domainId +
            ", diskSize=" + diskSize +
            ", customized=" + customized +
            ", tags='" + tags + '\'' +
            '}';
   }

   @Override
   public int compareTo(DiskOffering arg0) {
      return id.compareTo(arg0.getId());
   }

}
