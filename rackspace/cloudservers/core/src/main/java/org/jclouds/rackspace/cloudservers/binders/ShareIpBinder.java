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
package org.jclouds.rackspace.cloudservers.binders;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import org.jclouds.http.HttpRequest;
import org.jclouds.rest.binders.JsonBinder;

import com.google.common.collect.ImmutableMap;

/**
 * 
 * @author Adrian Cole
 * 
 */
public class ShareIpBinder extends JsonBinder {

   @SuppressWarnings("unused")
   private class ShareIpRequest {
      final int sharedIpGroupId;
      Boolean configureServer;

      private ShareIpRequest(int sharedIpGroupId) {
         this.sharedIpGroupId = sharedIpGroupId;
      }

   }

   @Override
   public void addEntityToRequest(Map<String, String> postParams, HttpRequest request) {
      ShareIpRequest createRequest = new ShareIpRequest(Integer.parseInt(checkNotNull(postParams
               .get("sharedIpGroupId"))));
      if (Boolean.parseBoolean(checkNotNull(postParams.get("configureServer")))) {
         createRequest.configureServer = new Boolean(true);
      }
      super.addEntityToRequest(ImmutableMap.of("shareIp", createRequest), request);
   }

   @Override
   public void addEntityToRequest(Object toBind, HttpRequest request) {
      throw new IllegalStateException("shareIp is needs parameters");
   }
}
