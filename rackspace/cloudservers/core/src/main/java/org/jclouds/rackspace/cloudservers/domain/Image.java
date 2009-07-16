/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
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

public class Image {

   private DateTime created;
   private Integer id;
   private String name;
   private Integer progress;
   private Integer serverId;
   private ImageStatus status;
   private DateTime updated;

   public Image(String name) {
      this.setName(name);
   }

   public void setCreated(DateTime created) {
      this.created = created;
   }

   public DateTime getCreated() {
      return created;
   }

   public void setId(Integer id) {
      this.id = id;
   }

   public Integer getId() {
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

}
