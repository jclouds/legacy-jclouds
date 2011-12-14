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
package org.jclouds.openstack.nova.v1_1.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.gson.annotations.SerializedName;

/**
 * Listing of a server.
 * 
 * @author Adrian Cole
 * @see <a href= "http://docs.openstack.org/api/openstack-compute/1.1/content/?a=doc#server_list" />
 */
public class Server {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      protected String id;
      protected String hostname;
      protected String datacenter;
      protected String platform;

      public Builder id(String id) {
         this.id = id;
         return this;
      }

      public Builder hostname(String hostname) {
         this.hostname = hostname;
         return this;
      }

      public Builder datacenter(String datacenter) {
         this.datacenter = datacenter;
         return this;
      }

      public Builder platform(String platform) {
         this.platform = platform;
         return this;
      }

      public Server build() {
         return new Server(id, hostname, datacenter, platform);
      }

      public Builder fromServer(Server in) {
         return datacenter(in.getDatacenter()).platform(in.getPlatform()).hostname(in.getHostname()).id(in.getId());
      }
   }

   @SerializedName("serverid")
   protected final String id;
   protected final String hostname;
   protected final String datacenter;
   protected final String platform;

   public Server(String id, String hostname, String datacenter, String platform) {
      this.id = checkNotNull(id, "id");
      this.hostname = checkNotNull(hostname, "hostname");
      this.datacenter = checkNotNull(datacenter, "datacenter");
      this.platform = checkNotNull(platform, "platform");
   }

   /**
    * @return the generated id of the server
    */
   public String getId() {
      return id;
   }

   /**
    * @return the hostname of the server
    */
   public String getHostname() {
      return hostname;
   }

   /**
    * @return platform running the server (ex. {@code OpenVZ})
    */
   public String getPlatform() {
      return platform;
   }

   /**
    * @return the datacenter the server exists in (ex. {@code Falkenberg})
    */
   public String getDatacenter() {
      return datacenter;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((datacenter == null) ? 0 : datacenter.hashCode());
      result = prime * result + ((hostname == null) ? 0 : hostname.hashCode());
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      result = prime * result + ((platform == null) ? 0 : platform.hashCode());
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
      Server other = (Server) obj;
      if (datacenter == null) {
         if (other.datacenter != null)
            return false;
      } else if (!datacenter.equals(other.datacenter))
         return false;
      if (hostname == null) {
         if (other.hostname != null)
            return false;
      } else if (!hostname.equals(other.hostname))
         return false;
      if (id == null) {
         if (other.id != null)
            return false;
      } else if (!id.equals(other.id))
         return false;
      if (platform == null) {
         if (other.platform != null)
            return false;
      } else if (!platform.equals(other.platform))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return String.format("[id=%s, hostname=%s, datacenter=%s, platform=%s]", id, hostname, datacenter, platform);
   }

}
