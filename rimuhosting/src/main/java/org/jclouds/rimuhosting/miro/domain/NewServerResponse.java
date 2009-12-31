/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.rimuhosting.miro.domain;

import com.google.gson.annotations.SerializedName;
import org.jclouds.rimuhosting.miro.data.NewServerData;

/**
 * Wrapper object to get back all data from a Instance create. The Password has been populated the NewInstance
 * object.
 *
 * @author Ivan Meredith
 */
public class NewServerResponse implements Comparable<NewServerResponse> {
   @SerializedName("about_order")
   private Server server;

   @SerializedName("new_order_request")
   private NewServerData newServerDataRequest;

   @SerializedName("running_vps_info")
   private ServerInfo serverInfo;

   public Server getServer() {
      return server;
   }

   public void setServer(Server server) {
      this.server = server;
   }

   public NewServerData getNewInstanceRequest() {
      return newServerDataRequest;
   }

   public void setNewInstanceRequest(NewServerData newServerDataRequest) {
      this.newServerDataRequest = newServerDataRequest;
   }

   public ServerInfo getServerInfo() {
      return serverInfo;
   }

   public void setServerInfo(ServerInfo serverInfo) {
      this.serverInfo = serverInfo;
   }

   @Override
   public int compareTo(NewServerResponse server) {
      return this.server.getId().compareTo(server.getServer().getId());
   }
}
