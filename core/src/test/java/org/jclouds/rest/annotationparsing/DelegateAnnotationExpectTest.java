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
package org.jclouds.rest.annotationparsing;

import static org.jclouds.providers.AnonymousProviderMetadata.forClientMappedToAsyncClientOnEndpoint;
import static org.testng.Assert.assertTrue;

import javax.ws.rs.HEAD;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.jclouds.Fallbacks.FalseOnNotFoundOr404;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.config.RestClientModule;
import org.jclouds.rest.internal.BaseRestClientExpectTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Module;

/**
 * Tests the ways that {@link Delegate}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "DelegateAnnotationExpectTest")
public class DelegateAnnotationExpectTest extends BaseRestClientExpectTest<DelegateAnnotationExpectTest.DelegatingApi> {

   static interface DelegatingApi {

      @Delegate
      @Path("/projects/{project}")
      DiskApi getDiskApiForProject(@PathParam("project") String projectName);
   }

   static interface DelegatingAsyncApi {

      @Delegate
      @Path("/projects/{project}")
      DiskAsyncApi getDiskApiForProject(@PathParam("project") String projectName);
   }

   static interface DiskApi {

      boolean exists(@PathParam("disk") String diskName);
   }

   static interface DiskAsyncApi {

      @HEAD
      @Path("/disks/{disk}")
      @Fallback(FalseOnNotFoundOr404.class)
      public ListenableFuture<Boolean> exists(@PathParam("disk") String diskName);
   }

   public void testDelegatingCallTakesIntoConsiderationCallerAndCalleePath() {

      DelegatingApi client = requestSendsResponse(
            HttpRequest.builder().method("HEAD").endpoint("http://mock/projects/prod/disks/disk1").build(),
            HttpResponse.builder().statusCode(200).build());

      assertTrue(client.getDiskApiForProject("prod").exists("disk1"));

   }

   // crufty junk until we inspect delegating api classes for all their client
   // mappings and make a test helper for random classes.

   @Override
   public ProviderMetadata createProviderMetadata() {
      return forClientMappedToAsyncClientOnEndpoint(DelegatingApi.class, DelegatingAsyncApi.class, "http://mock");
   }

   @Override
   protected Module createModule() {
      return new DelegatingRestClientModule();
   }

   @ConfiguresRestClient
   static class DelegatingRestClientModule extends RestClientModule<DelegatingApi, DelegatingAsyncApi> {

      public DelegatingRestClientModule() {
         // right now, we have to define the delegates by hand as opposed to
         // reflection looking for coordinated annotations
         super(ImmutableMap.<Class<?>, Class<?>> of(DiskApi.class, DiskAsyncApi.class));
      }
   }

}
