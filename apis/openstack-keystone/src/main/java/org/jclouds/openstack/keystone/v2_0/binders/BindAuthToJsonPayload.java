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
package org.jclouds.openstack.keystone.v2_0.binders;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.http.HttpRequest;
import org.jclouds.json.Json;
import org.jclouds.openstack.keystone.v2_0.config.CredentialType;
import org.jclouds.rest.MapBinder;
import org.jclouds.rest.binders.BindToJsonPayload;
import org.jclouds.rest.internal.GeneratedHttpRequest;

import com.google.common.base.Predicates;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.ImmutableMap.Builder;

/**
 * 
 * @author Adrian Cole
 * 
 */
@Singleton
public class BindAuthToJsonPayload extends BindToJsonPayload implements MapBinder {
   @Inject
   public BindAuthToJsonPayload(Json jsonBinder) {
      super(jsonBinder);
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object toBind) {
      throw new IllegalStateException("BindAuthToJsonPayload needs parameters");
   }

   protected void addCredentialsInArgsOrNull(GeneratedHttpRequest<?> gRequest, Builder<String, Object> builder) {
      for (Object arg : Iterables.filter(gRequest.getArgs(), Predicates.notNull())) {
         if (arg.getClass().isAnnotationPresent(CredentialType.class)) {
            builder.put(arg.getClass().getAnnotation(CredentialType.class).value(), arg);
         }
      }
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
      checkArgument(checkNotNull(request, "request") instanceof GeneratedHttpRequest<?>,
               "this binder is only valid for GeneratedHttpRequests!");
      GeneratedHttpRequest<?> gRequest = (GeneratedHttpRequest<?>) request;
      checkState(gRequest.getArgs() != null, "args should be initialized at this point");

      Builder<String, Object> builder = ImmutableMap.builder();
      addCredentialsInArgsOrNull(gRequest, builder);
      // TODO: is tenantName permanent? or should we switch to tenantId at some point. seems most tools
      // still use tenantName
      if (!Strings.isNullOrEmpty((String) postParams.get("tenantName")))
         builder.put("tenantName", postParams.get("tenantName"));
      else if (!Strings.isNullOrEmpty((String) postParams.get("tenantId")))
          builder.put("tenantId", postParams.get("tenantId"));
      return super.bindToRequest(request, ImmutableMap.of("auth", builder.build()));
   }

}
