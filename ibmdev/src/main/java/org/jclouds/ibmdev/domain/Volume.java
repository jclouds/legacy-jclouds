/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the ;License;);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an ;AS IS; BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.ibmdev.domain;

import java.util.Date;
import java.util.Set;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * 
 * The current state of a Volume
 * 
 * @author Adrian Cole
 */
public class Volume {

   private Long instanceId;
   private int state;
   private int size;
   private String owner;
   private Date createdTime;
   private int location;
   private Set<String> productCodes = Sets.newLinkedHashSet();
   private String format;
   private String name;
   private long id;

   public Volume(Long instanceId, int state, int size, String owner, Date createdTime,
            int location, Iterable<String> productCodes, String format, String name, long id) {
      this.instanceId = instanceId;
      this.state = state;
      this.size = size;
      this.owner = owner;
      this.createdTime = createdTime;
      this.location = location;
      Iterables.addAll(this.productCodes, productCodes);
      this.format = format;
      this.name = name;
      this.id = id;
   }

   public Volume() {

   }

   public Long getInstanceId() {
      return instanceId;
   }

   public void setInstanceId(Long instanceId) {
      this.instanceId = instanceId;
   }

   public int getState() {
      return state;
   }

   public void setState(int state) {
      this.state = state;
   }

   public int getSize() {
      return size;
   }

   public void setSize(int size) {
      this.size = size;
   }

   public String getOwner() {
      return owner;
   }

   public void setOwner(String owner) {
      this.owner = owner;
   }

   public Date getCreatedTime() {
      return createdTime;
   }

   public void setCreatedTime(Date createdTime) {
      this.createdTime = createdTime;
   }

   public int getLocation() {
      return location;
   }

   public void setLocation(int location) {
      this.location = location;
   }

   public Set<String> getProductCodes() {
      return productCodes;
   }

   public void setProductCodes(Set<String> productCodes) {
      this.productCodes = productCodes;
   }

   public String getFormat() {
      return format;
   }

   public void setFormat(String format) {
      this.format = format;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public long getId() {
      return id;
   }

   public void setId(long id) {
      this.id = id;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((createdTime == null) ? 0 : createdTime.hashCode());
      result = prime * result + ((format == null) ? 0 : format.hashCode());
      result = prime * result + (int) (id ^ (id >>> 32));
      result = prime * result + ((instanceId == null) ? 0 : instanceId.hashCode());
      result = prime * result + location;
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((owner == null) ? 0 : owner.hashCode());
      result = prime * result + ((productCodes == null) ? 0 : productCodes.hashCode());
      result = prime * result + size;
      result = prime * result + state;
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
      Volume other = (Volume) obj;
      if (createdTime == null) {
         if (other.createdTime != null)
            return false;
      } else if (!createdTime.equals(other.createdTime))
         return false;
      if (format == null) {
         if (other.format != null)
            return false;
      } else if (!format.equals(other.format))
         return false;
      if (id != other.id)
         return false;
      if (instanceId == null) {
         if (other.instanceId != null)
            return false;
      } else if (!instanceId.equals(other.instanceId))
         return false;
      if (location != other.location)
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (owner == null) {
         if (other.owner != null)
            return false;
      } else if (!owner.equals(other.owner))
         return false;
      if (productCodes == null) {
         if (other.productCodes != null)
            return false;
      } else if (!productCodes.equals(other.productCodes))
         return false;
      if (size != other.size)
         return false;
      if (state != other.state)
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[id=" + id + ", name=" + name + ", size=" + size + ", state=" + state
               + ", instanceId=" + instanceId + ", location=" + location + ", format=" + format
               + ", owner=" + owner + ", createdTime=" + createdTime + ", productCodes="
               + productCodes + "]";
   }

}