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

package org.jclouds.openstack.nova.v1_1.compute.predicates;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import java.util.Map;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.Image;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.nova.v1_1.domain.zonescoped.ZoneAndId;
import org.jclouds.openstack.nova.v1_1.internal.BaseNovaComputeServiceContextExpectTest;
import org.jclouds.predicates.PredicateWithResult;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Injector;

/**
 * 
 * @author David Alves
 * 
 */
@Test(groups = "unit", testName = "GetImageWhenImageInZoneHasActiveStatucPredicateWithResultExpectTest")
public class GetImageWhenImageInZoneHasActiveStatusPredicateWithResultExpectTest extends
         BaseNovaComputeServiceContextExpectTest<Injector> {

   private final HttpResponse listImagesDetailImageExtensionResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/image_list_detail_imageextension.json")).build();

   private Map<HttpRequest, HttpResponse> requestResponseMap = ImmutableMap.<HttpRequest, HttpResponse> builder()
            .put(keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess)
            .put(listImagesDetail, listImagesDetailImageExtensionResponse).build();

   public void testReturnsFalseOnImageStatusSavingAndTrueOnActive() {
      Injector injector = requestsSendResponses(requestResponseMap);
      PredicateWithResult<ZoneAndId, Image> predicate = injector
               .getInstance(GetImageWhenImageInZoneHasActiveStatusPredicateWithResult.class);
      ZoneAndId zoneAdnId0 = ZoneAndId.fromZoneAndId("az-1.region-a.geo-1", "13");
      ZoneAndId zoneAdnId1 = ZoneAndId.fromZoneAndId("az-1.region-a.geo-1", "12");
      assertTrue(!predicate.apply(zoneAdnId1));
      assertTrue(predicate.apply(zoneAdnId0));
      assertEquals("natty-server-cloudimg-amd64", predicate.getResult().getName());
   }

   public void testFailsOnOtherStatuses() {
      Injector injector = requestsSendResponses(requestResponseMap);
      PredicateWithResult<ZoneAndId, Image> predicate = injector
               .getInstance(GetImageWhenImageInZoneHasActiveStatusPredicateWithResult.class);
      ZoneAndId zoneAdnId0 = ZoneAndId.fromZoneAndId("az-1.region-a.geo-1", "15");
      ZoneAndId zoneAdnId1 = ZoneAndId.fromZoneAndId("az-1.region-a.geo-1", "14");
      ZoneAndId zoneAdnId2 = ZoneAndId.fromZoneAndId("az-1.region-a.geo-1", "11");
      ZoneAndId zoneAdnId3 = ZoneAndId.fromZoneAndId("az-1.region-a.geo-1", "10");
      assertTrue(illegalStateExceptionThrown(predicate, zoneAdnId0));
      assertTrue(illegalStateExceptionThrown(predicate, zoneAdnId1));
      assertTrue(illegalStateExceptionThrown(predicate, zoneAdnId2));
      assertTrue(illegalStateExceptionThrown(predicate, zoneAdnId3));
   }

   private boolean illegalStateExceptionThrown(PredicateWithResult<ZoneAndId, Image> predicate, ZoneAndId zoneAndId) {
      try {
         predicate.apply(zoneAndId);
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
