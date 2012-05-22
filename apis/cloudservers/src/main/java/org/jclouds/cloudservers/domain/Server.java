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
package org.jclouds.cloudservers.domain;

import java.util.Map;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;

/**
 * A server is a virtual machine instance in the Cloud Servers system. Flavor and image are
 * requisite elements when creating a server.
 * 
 * @author Adrian Cole
 */
public class Server {
   private int id;
   private String name;
   private Map<String, String> metadata = Maps.newHashMap();
   private Addresses addresses;
   private String adminPass;
   private Integer flavorId;
   private String hostId;
   private Integer imageId;
   private Integer sharedIpGroupId;
   private Integer progress;
   private ServerStatus status;

   public Server() {
   }

   public Server(int id, String name) {
      this.id = id;
      this.name = name;
   }

   public void setMetadata(Map<String, String> metadata) {
      this.metadata = metadata;
   }

   public Map<String, String> getMetadata() {
      return metadata;
   }

   public void setAddresses(Addresses addresses) {
      this.addresses = addresses;
   }

   public Addresses getAddresses() {
      return addresses;
   }

   public void setAdminPass(String adminPass) {
      this.adminPass = adminPass;
   }

   public String getAdminPass() {
      return adminPass;
   }

   public void setFlavorId(Integer flavorId) {
      this.flavorId = flavorId;
   }

   public Integer getFlavorId() {
      return flavorId;
   }

   public void setHostId(String hostId) {
      this.hostId = hostId;
   }

   /**
    * The Cloud Servers provisioning algorithm has an anti-affinity property that attempts to spread
    * out customer VMs across hosts. Under certain situations, VMs from the same customer may be
    * placed on the same host. hostId represents the host your cloud server runs on and can be used
    * to determine this scenario if it's relevant to your application.
    * <p/>
    * Note: hostId is unique PER ACCOUNT and is not globally unique.
    */
   public String getHostId() {
      return hostId;
   }

   public int getId() {
      return id;
   }

   public void setImageId(Integer imageId) {
      this.imageId = imageId;
   }

   public Integer getImageId() {
      return imageId;
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

   public void setSharedIpGroupId(Integer sharedIpGroupId) {
      this.sharedIpGroupId = sharedIpGroupId;
   }

   public Integer getSharedIpGroupId() {
      return sharedIpGroupId;
   }

   public void setStatus(ServerStatus status) {
      this.status = status;
   }

   /**
    * Servers contain a status attribute that can be used as an indication of the current server
    * state. Servers with an ACTIVE status are available for use.
    */
   public ServerStatus getStatus() {
      return status;
   }


   @Override
   public int hashCode() {
      return Objects.hashCode(id, name);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Server that = Server.class.cast(obj);
      return Objects.equal(this.getId(), that.getId())
            && Objects.equal(this.name, that.name);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("")
            .add("id", getId())
            .add("name", name)
            .add("addresses", addresses)
            .add("flavorId", flavorId)
            .add("imageId", imageId)
            .add("hostId", hostId)
            .add("metadata", metadata)
            .add("sharedIpGroupId", sharedIpGroupId).toString();
   }

}
