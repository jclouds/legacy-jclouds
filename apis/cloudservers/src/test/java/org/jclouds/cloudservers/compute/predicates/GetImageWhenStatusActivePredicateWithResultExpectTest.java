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
package org.jclouds.cloudservers.compute.predicates;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import java.net.URI;
import java.util.Map;

import org.jclouds.cloudservers.internal.BaseCloudServersComputeServiceExpectTest;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.Image;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.predicates.PredicateWithResult;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.net.HttpHeaders;
import com.google.inject.Injector;

/**
 * 
 * @author David Alves
 * 
 */
@Test(groups = "unit", testName = "GetImageWhenStatusActivePredicateWithResultExpectTest")
public class GetImageWhenStatusActivePredicateWithResultExpectTest extends
         BaseCloudServersComputeServiceExpectTest<Injector> {

   private final HttpRequest listImagesDetail = HttpRequest
            .builder()
            .method("GET")
            .endpoint(
                     URI.create("https://lon.servers.api.rackspacecloud.com/v1.0/10001786/images/detail?format=json&now=1257695648897"))
            .headers(ImmutableMultimap.<String, String> builder()
                     .put(HttpHeaders.ACCEPT, "application/json")
                     .put("X-Auth-Token", authToken).build()).build();

   private final HttpResponse listImagesResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/test_list_images_detail_imageextension.json")).build();

   private final Map<HttpRequest, HttpResponse> requestResponseMap = ImmutableMap.<HttpRequest, HttpResponse> builder()
            .put(listImagesDetail, listImagesResponse).put(initialAuth, responseWithAuth).build();

   public void testReturnsFalseOnQueuedAndSavingAndTrueOnActive() {
      Injector injector = requestsSendResponses(requestResponseMap);
      PredicateWithResult<Integer, Image> predicate = injector
               .getInstance(GetImageWhenStatusActivePredicateWithResult.class);
      assertTrue(predicate.apply(2));// ACTIVE
      assertFalse(predicate.apply(743));// SAVING
      assertFalse(predicate.apply(744));// QUEUED
      assertFalse(predicate.apply(747));// PREPARING
   }

   public void testFailsOnOtherStatuses() {
      Injector injector = requestsSendResponses(requestResponseMap);
      PredicateWithResult<Integer, Image> predicate = injector
               .getInstance(GetImageWhenStatusActivePredicateWithResult.class);
      assertTrue(illegalStateExceptionThrown(predicate, 745));// UNRECOGNIZED
      assertTrue(illegalStateExceptionThrown(predicate, 746));// UNKNOWN
      assertTrue(illegalStateExceptionThrown(predicate, 748));// FAILED
   }

   private boolean illegalStateExceptionThrown(PredicateWithResult<Integer, Image> predicate, Integer id) {
      try {
         predicate.apply(id);
      } catch (IllegalStateException e) {
         return true;
      }
      return false;
   }

   @Override
   public Injector apply(ComputeServiceContext input) {
      return input.utils().injector();
   }

}
