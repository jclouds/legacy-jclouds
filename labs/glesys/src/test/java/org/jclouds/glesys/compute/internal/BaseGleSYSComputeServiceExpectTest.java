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
package org.jclouds.glesys.compute.internal;

import java.net.URI;
import java.util.Map;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.glesys.GleSYSApiMetadata;
import org.jclouds.glesys.compute.config.GleSYSComputeServiceContextModule.PasswordProvider;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.internal.BaseRestClientExpectTest;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * Base class for writing GleSYS Expect tests for ComputeService operations
 * 
 * @author Adrian Cole
 */
public abstract class BaseGleSYSComputeServiceExpectTest extends BaseRestClientExpectTest<ComputeService> {

   public BaseGleSYSComputeServiceExpectTest() {
      provider = "glesys";
   }

   @Override
   protected ApiMetadata createApiMetadata() {
      return new GleSYSApiMetadata();
   }

   @Override
   public ComputeService createClient(Function<HttpRequest, HttpResponse> fn, Module module, Properties props) {
      return createInjector(fn, module, props).getInstance(ComputeService.class);
   }

   protected PasswordProvider passwordGenerator() {
      // make sure we can predict passwords generated for createServer requests
      return new PasswordProvider() {
         public String get() {
            return "foo";
         }
      };
   }

   protected Injector injectorForKnownArgumentsAndConstantPassword() {
      return injectorForKnownArgumentsAndConstantPassword(ImmutableMap.<HttpRequest, HttpResponse> of());
   }

   protected Injector injectorForKnownArgumentsAndConstantPassword(Map<HttpRequest, HttpResponse> requestsResponses) {
      return computeContextForKnownArgumentsAndConstantPassword(requestsResponses).utils().injector();
   }

   protected ComputeServiceContext computeContextForKnownArgumentsAndConstantPassword(
         Map<HttpRequest, HttpResponse> requestsResponses) {
      return requestsSendResponses(
            ImmutableMap
                  .<HttpRequest, HttpResponse> builder()
                  .put(HttpRequest
                        .builder()
                        .method("GET")
                        .endpoint(URI.create("https://api.glesys.com/server/templates/format/json"))
                        .headers(
                              ImmutableMultimap.<String, String> builder().put("Accept", "application/json")
                                    .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build()).build(),
                        HttpResponse.builder().statusCode(200).payload(payloadFromResource("/server_templates.json"))
                              .build())
                  .put(HttpRequest
                        .builder()
                        .method("GET")
                        .endpoint(URI.create("https://api.glesys.com/server/allowedarguments/format/json"))
                        .headers(
                              ImmutableMultimap.<String, String> builder().put("Accept", "application/json")
                                    .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build()).build(),
                        HttpResponse.builder().statusCode(204)
                              .payload(payloadFromResource("/server_allowed_arguments.json")).build())
                  .putAll(requestsResponses).build(), new AbstractModule() {

               @Override
               protected void configure() {
                  bind(PasswordProvider.class).toInstance(passwordGenerator());
               }

            }).getContext();
   }

   protected ComputeServiceContext computeContextForKnownArgumentsAndConstantPassword() {
      return computeContextForKnownArgumentsAndConstantPassword(ImmutableMap.<HttpRequest, HttpResponse> of());
   }
}
