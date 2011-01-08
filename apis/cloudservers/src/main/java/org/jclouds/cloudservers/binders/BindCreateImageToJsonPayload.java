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

package org.jclouds.cloudservers.binders;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import javax.inject.Singleton;

import org.jclouds.http.HttpRequest;
import org.jclouds.rest.binders.BindToJsonPayload;

import com.google.common.collect.ImmutableMap;

/**
 * 
 * @author Adrian Cole
 * 
 */
@Singleton
public class BindCreateImageToJsonPayload extends BindToJsonPayload {

   @SuppressWarnings("unused")
   private class CreateImageRequest {
      final int serverId;
      final String name;

      private CreateImageRequest(int serverId, String name) {
         this.serverId = serverId;
         this.name = name;
      }

   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, String> postParams) {
      CreateImageRequest createRequest = new CreateImageRequest(Integer
               .parseInt(checkNotNull(postParams.get("serverId"))), checkNotNull(postParams
               .get("imageName")));
      return super.bindToRequest(request, ImmutableMap.of("image", createRequest));
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object toBind) {
      throw new IllegalArgumentException("image is needs parameters");
   }
}
