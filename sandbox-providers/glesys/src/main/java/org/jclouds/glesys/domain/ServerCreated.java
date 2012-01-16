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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;
import java.util.List;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.gson.annotations.SerializedName;

/**
 * Information about a new server
 *
 * @author Adam Lowe
 * @see <a href="https://customer.glesys.com/api.php?a=doc#server_create" />
 */
public class ServerCreated {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private String id;
      private String hostname;
      private List<ServerIp> ips;

      public Builder id(String id) {
         this.id = id;
         return this;
      }

      public Builder ips(List<ServerIp> ips) {
         this.ips = ips;
         return this;
      }

      public Builder ips(ServerIp... ips) {
         return ips(Arrays.asList(ips));
      }

      public Builder hostname(String hostname) {
         this.hostname = hostname;
         return this;
      }

      public ServerCreated build() {
         return new ServerCreated(id, hostname, ips);
      }

      public Builder fromServerCreated(ServerCreated in) {
         return id(in.getId()).hostname(in.getHostname()).ips(in.getIps());
      }
   }

   @SerializedName("serverid")
   private final String id;
   private final String hostname;
   @SerializedName("iplist")
   private final List<ServerIp> ips;

   public ServerCreated(String id, @Nullable String hostname, List<ServerIp> ips) {
      checkNotNull(id, "id");
      this.id = id;
      this.hostname = hostname;
      this.ips = ips;
   }

   /**
    * @return the id of the server (used for other calls to identify the server.
    * @see org.jclouds.glesys.features.ServerClient
    */
   public String getId() {
      return id;
   }

   /** @return the hostname of the server */
   public String getHostname() {
      return hostname;
   }

   /** @return the IP addresses assigned to the server */
   public List<ServerIp> getIps() {
      return ips == null ? ImmutableList.<ServerIp>of() : ips;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }
      return object instanceof ServerCreated
            && Objects.equal(id, ((ServerCreated) object).id);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id);
   }
   
   @Override
   public String toString() {
      return String.format("[id=%s, hostname=%s, ips=%s]", id, hostname, ips);
   }

}
