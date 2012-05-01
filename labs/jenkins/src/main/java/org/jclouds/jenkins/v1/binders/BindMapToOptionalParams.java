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
package org.jclouds.jenkins.v1.binders;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.http.utils.ModifyRequest.addQueryParam;

import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.core.UriBuilder;

import org.jclouds.http.HttpRequest;
import org.jclouds.rest.Binder;

/**
 * Binds the map to parameters.
 * 
 * @author Andrea Turli
 */
public class BindMapToOptionalParams implements Binder {
   private final Provider<UriBuilder> builder;

   @Inject
   BindMapToOptionalParams(Provider<UriBuilder> builder) {
      this.builder = checkNotNull(builder, "builder");
   }

   @SuppressWarnings("unchecked")
   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      checkArgument(checkNotNull(input, "input") instanceof Map, "this binder is only valid for Maps!");
      Map<String, String> map = (Map<String, String>) input;
      for (Entry<String, String> entry : map.entrySet()) {
         request = addQueryParam(request, entry.getKey(), entry.getValue(), builder.get());
      }
      return request;
   }

}