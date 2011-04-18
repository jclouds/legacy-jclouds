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

package org.jclouds.openstack.nova.domain;

import java.util.Collections;
import java.util.Date;
import java.util.Map;

import com.google.common.collect.Maps;

/**
 * An image is a collection of files used to create or rebuild a server.
 * 
 * @author Adrian Cole
 */
public class Image extends Resource {

   private Date created;
   private int id;
   private String name;
   private Integer progress;
   private String serverRef;
   private ImageStatus status;
   private Date updated;
   private Map<String, String> metadata = Maps.newHashMap();

   public Image() {
   }

   public Image(int id, String name) {
      this.id = id;
      this.name = name;
   }

   public void setCreated(Date created) {
      this.created = created;
   }

   public Date getCreated() {
      return created;
   }

   public void setId(int id) {
      this.id = id;
   }

   public int getId() {
      return id;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getName() {
      return name;
   }

   public void setProgress(Integer progress) {
      this.progress = progress;
   }

   public Integer getProgress() {
      return progress;
   }

   public void setServerRef(String serverRef) {
      this.serverRef = serverRef;
   }

   public String getServerRef() {
      return serverRef;
   }

   public void setStatus(ImageStatus status) {
      this.status = status;
   }

   public ImageStatus getStatus() {
      return status;
   }

   public void setUpdated(Date updated) {
      this.updated = updated;
   }

   public Date getUpdated() {
      return updated;
   }

   public Map<String, String> getMetadata() {
      return Collections.unmodifiableMap(metadata);
   }

   public void setMetadata(Map<String, String> metadata) {
      this.metadata = Maps.newHashMap(metadata);
   }

   /**
    * note that this ignores some fields
    */
   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + id;
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((serverRef == null) ? 0 : serverRef.hashCode());
      return result;
   }

   /**
    * note that this ignores some fields
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      Image other = (Image) obj;
      if (id != other.id)
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "Image [created=" + created + ", id=" + id + ", name=" + name + ", serverId="
               + serverRef + "]";
   }

}
