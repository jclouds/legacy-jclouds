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

package org.jclouds.rackspace.cloudservers.options;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import javax.annotation.Nullable;

import org.jclouds.http.HttpRequest;
import org.jclouds.rest.binders.BindToJsonPayload;

import com.google.common.collect.ImmutableMap;

/**
 * 
 * 
 * @author Adrian Cole
 * 
 */
public class CreateSharedIpGroupOptions extends BindToJsonPayload {
   Integer serverId;

   @SuppressWarnings("unused")
   private static class SharedIpGroupRequest {
      final String name;
      Integer server;

      private SharedIpGroupRequest(String name, @Nullable Integer serverId) {
         this.name = name;
         this.server = serverId;
      }

   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, String> postParams) {
      SharedIpGroupRequest createRequest = new SharedIpGroupRequest(checkNotNull(postParams
               .get("name")), serverId);
      return super.bindToRequest(request, ImmutableMap.of("sharedIpGroup", createRequest));
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object toBind) {
      throw new IllegalStateException("CreateSharedIpGroup is a POST operation");
   }

   /**
    * 
    * @param id
    *           of the server to include with this request.
    */
   public CreateSharedIpGroupOptions withServer(int id) {
      checkArgument(id > 0, "server id must be a positive number");
      this.serverId = id;
      return this;
   }

   public static class Builder {

      /**
       * @see CreateSharedIpGroupOptions#withServer(int)
       */
      public static CreateSharedIpGroupOptions withServer(int id) {
         CreateSharedIpGroupOptions options = new CreateSharedIpGroupOptions();
         return options.withServer(id);
      }
   }
}
