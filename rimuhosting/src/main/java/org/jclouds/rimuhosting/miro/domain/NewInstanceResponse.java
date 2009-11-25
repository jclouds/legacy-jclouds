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
package org.jclouds.rimuhosting.miro.domain;

import com.google.gson.annotations.SerializedName;
import org.jclouds.rimuhosting.miro.data.NewInstance;

/**
 * Wrapper object to get back all data from a Instance create. The Password has been populated the NewInstance
 * object.
 */
public class NewInstanceResponse implements Comparable<NewInstanceResponse> {
   @SerializedName("about_order")
   private Instance instance;

   @SerializedName("new_order_request")
   private NewInstance newInstanceRequest;

   @SerializedName("running_vps_info")
   private InstanceInfo instanceInfo;

   public Instance getInstance() {
      return instance;
   }

   public void setInstance(Instance instaince) {
      this.instance = instaince;
   }

   public NewInstance getNewInstanceRequest() {
      return newInstanceRequest;
   }

   public void setNewInstanceRequest(NewInstance newInstanceRequest) {
      this.newInstanceRequest = newInstanceRequest;
   }

   public InstanceInfo getInstanceInfo() {
      return instanceInfo;
   }

   public void setInstanceInfo(InstanceInfo instanceInfo) {
      this.instanceInfo = instanceInfo;
   }

   @Override
   public int compareTo(NewInstanceResponse instance) {
      return this.instance.getId().compareTo(instance.getInstance().getId());     
   }
}
