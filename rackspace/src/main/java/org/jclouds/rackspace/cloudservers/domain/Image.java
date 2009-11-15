/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.rackspace.cloudservers.domain;

import org.joda.time.DateTime;

/**
 * An image is a collection of files used to create or rebuild a server. Rackspace provides a number
 * of pre-built OS images by default. You may also create custom images from cloud servers you have
 * launched. These custom images are useful for backup purposes or for producing gold server
 * images if you plan to deploy a particular server configuration frequently.
 * 
 * @author Adrian Cole
 */
public class Image {

   public static final Image NOT_FOUND = new Image(-1, "NOT_FOUND");

   private DateTime created;
   private int id;
   private String name;
   private Integer progress;
   private Integer serverId;
   private ImageStatus status;
   private DateTime updated;

   public Image() {
   }

   public Image(int id, String name) {
      this.id = id;
      this.name = name;
   }

   public void setCreated(DateTime created) {
      this.created = created;
   }

   public DateTime getCreated() {
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

   public void setServerId(Integer serverId) {
      this.serverId = serverId;
   }

   public Integer getServerId() {
      return serverId;
   }

   public void setStatus(ImageStatus status) {
      this.status = status;
   }

   public ImageStatus getStatus() {
      return status;
   }

   public void setUpdated(DateTime updated) {
      this.updated = updated;
   }

   public DateTime getUpdated() {
      return updated;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + id;
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((serverId == null) ? 0 : serverId.hashCode());
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
      Image other = (Image) obj;
      if (id != other.id)
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (serverId == null) {
         if (other.serverId != null)
            return false;
      } else if (!serverId.equals(other.serverId))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "Image [created=" + created + ", id=" + id + ", name=" + name + ", serverId="
               + serverId + "]";
   }

}
