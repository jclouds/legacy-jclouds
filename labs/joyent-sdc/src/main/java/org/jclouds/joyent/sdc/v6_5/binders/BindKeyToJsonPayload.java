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
package org.jclouds.joyent.sdc.v6_5.binders;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.http.HttpRequest;
import org.jclouds.joyent.sdc.v6_5.domain.Key;
import org.jclouds.json.Json;
import org.jclouds.rest.binders.BindToJsonPayload;

/**
 * 
 * @author Adrian Cole
 * 
 */
@Singleton
public class BindKeyToJsonPayload extends BindToJsonPayload {
   @Inject
   public BindKeyToJsonPayload(Json jsonBinder) {
      super(jsonBinder);
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object toBind) {
      // don't include created in the http request
      return super.bindToRequest(request, Key.class.cast(toBind).toBuilder().created(null).build());
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
      throw new IllegalStateException("This should be assigned only a single arg");
   }

}
