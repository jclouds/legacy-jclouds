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
package org.jclouds.glesys.domain;

import com.google.common.base.Objects;
import com.google.gson.annotations.SerializedName;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Listing of a server.
 *
 * @author Adrian Cole
 * @see <a href= "https://customer.glesys.com/api.php?a=doc#server_list" />
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
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }
      if (object instanceof Server) {
         final Server other = (Server) object;
         return Objects.equal(datacenter, other.datacenter)
               && Objects.equal(hostname, other.hostname)
               && Objects.equal(id, other.id)
               && Objects.equal(platform, other.platform);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(datacenter, hostname, id, platform);
   }

   @Override
   public String toString() {
      return String.format("[id=%s, hostname=%s, datacenter=%s, platform=%s]", id, hostname, datacenter, platform);
   }

}
