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

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.glesys.compute.config.GleSYSComputeServiceContextModule.PasswordProvider;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.logging.config.NullLoggingModule;
import org.jclouds.rest.BaseRestClientExpectTest;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
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
   public ComputeService createClient(Function<HttpRequest, HttpResponse> fn, Module module, Properties props) {
      return new ComputeServiceContextFactory(setupRestProperties()).createContext(provider, identity, credential,
            ImmutableSet.<Module> of(new ExpectModule(fn), new NullLoggingModule(), module), props).getComputeService();
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
