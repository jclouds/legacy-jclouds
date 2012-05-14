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
package org.jclouds.rimuhosting.miro.binder;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.jclouds.http.HttpRequest;
import org.jclouds.json.Json;
import org.jclouds.rest.binders.BindToJsonPayload;

import com.google.common.collect.ImmutableMap;

/**
 * Generic binder for RimuHosting POSTS/PUTS. In the form of
 * 
 * {"request":{...}}
 * 
 * @author Ivan Meredith
 */
public class RimuHostingJsonBinder extends BindToJsonPayload {
   @Inject
   public RimuHostingJsonBinder(Json jsonBinder) {
      super(jsonBinder);
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
      return bindToRequest(request, (Object) postParams);
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object toBind) {
      return super.bindToRequest(request, (Object) ImmutableMap.of("request", toBind));
   }
}
