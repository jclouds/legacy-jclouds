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
package org.jclouds.rimuhosting.miro.functions;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.rimuhosting.miro.domain.Server;
import org.jclouds.rimuhosting.miro.domain.internal.RimuHostingResponse;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.Map;
/**
 * @author Ivan Meredith
 */
@Singleton
public class ParseResizeResponseFromJsonResponse extends ParseJson<ParseResizeResponseFromJsonResponse.ResizeResponse> {
   @Inject
   public ParseResizeResponseFromJsonResponse(Gson gson) {
      super(gson);
   }

   public static class ResizeResponse extends RimuHostingResponse {
      private Server about_order;
      private ResizeResponse resource_change_result;

      public ResizeResponse getResourceChangeResult() {
         return resource_change_result;
      }

      public void setResourceChangeResult(ResizeResponse resource_change_result) {
         this.resource_change_result = resource_change_result;
      }

      public Server getAboutOrder() {
         return about_order;
      }

      public void setAboutOrder(Server about_orders) {
         this.about_order = about_orders;
      }
   }
   @Override
   protected ResizeResponse apply(InputStream stream) {
      Type setType = new TypeToken<Map<String, ResizeResponse>>() {
      }.getType();
      try {
         Map<String, ResizeResponse> responseMap = gson.fromJson(new InputStreamReader(stream, "UTF-8"), setType);
         return responseMap.values().iterator().next();
      } catch (UnsupportedEncodingException e) {
         throw new RuntimeException("jclouds requires UTF-8 encoding", e);
      }
   }
}