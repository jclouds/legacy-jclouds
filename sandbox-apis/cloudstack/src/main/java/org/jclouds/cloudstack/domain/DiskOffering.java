/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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

import static com.google.common.base.Preconditions.checkNotNull;

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
public class DiskOffering implements Comparable<DiskOffering> {

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private long id;
      private String name;
      private String displayText;
      private Date created;
      private String domain;
      private long domainId;
      private int diskSize;
      private boolean customized;
      private Set<String> tags = ImmutableSet.of();

      public Builder id(long id) {
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

      public Builder domainId(long domainId) {
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

   private long id;
   private String name;
   @SerializedName("displaytext")
   private String displayText;
   private Date created;
   private String domain;
   @SerializedName("domainid")
   private long domainId;
   @SerializedName("disksize")
   private int diskSize;
   @SerializedName("iscustomized")
   private boolean customized;
   private String tags;

   public DiskOffering(long id, String name, String displayText, Date created, String domain, long domainId,
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
   public long getId() {
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
   public long getDomainId() {
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
      final int prime = 31;
      int result = 1;
      result = prime * result + ((created == null) ? 0 : created.hashCode());
      result = prime * result + (customized ? 1231 : 1237);
      result = prime * result + diskSize;
      result = prime * result + ((displayText == null) ? 0 : displayText.hashCode());
      result = prime * result + ((domain == null) ? 0 : domain.hashCode());
      result = prime * result + (int) (domainId ^ (domainId >>> 32));
      result = prime * result + (int) (id ^ (id >>> 32));
      result = prime * result + ((name == null) ? 0 : name.hashCode());
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
      DiskOffering other = (DiskOffering) obj;
      if (created == null) {
         if (other.created != null)
            return false;
      } else if (!created.equals(other.created))
         return false;
      if (customized != other.customized)
         return false;
      if (diskSize != other.diskSize)
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
      if (domainId != other.domainId)
         return false;
      if (id != other.id)
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
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
      return "[id=" + id + ", name=" + name + ", displayText=" + displayText + ", created=" + created + ", diskSize="
            + diskSize + ", iscustomized=" + customized + ", domain=" + domain + ", domainId=" + domainId + ", tags="
            + getTags() + "]";
   }

   @Override
   public int compareTo(DiskOffering arg0) {
      return new Long(id).compareTo(arg0.getId());
   }

}
