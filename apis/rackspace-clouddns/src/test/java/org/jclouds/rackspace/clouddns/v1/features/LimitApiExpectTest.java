/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.rackspace.clouddns.v1.features;

import static javax.ws.rs.core.Response.Status.OK;
import static org.testng.Assert.assertEquals;

import java.net.URI;

import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.v2_0.domain.Limits;
import org.jclouds.openstack.v2_0.domain.RateLimit;
import org.jclouds.rackspace.clouddns.v1.CloudDNSApi;
import org.jclouds.rackspace.clouddns.v1.internal.BaseCloudDNSApiExpectTest;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/**
 * @author Everett Toews
 */
@Test(groups = "unit")
public class LimitApiExpectTest extends BaseCloudDNSApiExpectTest<CloudDNSApi> {
   public void testListLimits() {
      URI endpoint = URI.create("https://dns.api.rackspacecloud.com/v1.0/123123/limits");
      LimitApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey,
            responseWithAccess, 
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(OK.getStatusCode()).payload(payloadFromResource("/limit-list.json")).build()
      ).getLimitApi();

      Limits limits = api.list();
      assertEquals(limits.getAbsoluteLimits().get("records per domain"), Integer.valueOf(500));
      assertEquals(limits.getAbsoluteLimits().get("domains"), Integer.valueOf(500));
      assertEquals(Iterables.size(limits.getRateLimits()), 2);
      
      RateLimit statusRateLimit = Iterables.tryFind(limits.getRateLimits(), isStatusRateLimit()).orNull();
      assertEquals(statusRateLimit.getRegex(), ".*/v\\d+\\.\\d+/(\\d+/status).*");
      assertEquals(Iterables.get(statusRateLimit.getLimits(), 0).getVerb(), "GET");
      assertEquals(Iterables.get(statusRateLimit.getLimits(), 0).getValue(), 5);
      assertEquals(Iterables.get(statusRateLimit.getLimits(), 0).getRemaining().get(), Integer.valueOf(5));
      assertEquals(Iterables.get(statusRateLimit.getLimits(), 0).getUnit(), "SECOND");
   }
   
   public void testListLimitTypes() {
      URI endpoint = URI.create("https://dns.api.rackspacecloud.com/v1.0/123123/limits/types");
      LimitApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey,
            responseWithAccess, 
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(OK.getStatusCode()).payload(payloadFromResource("/limit-types-list.json")).build()
      ).getLimitApi();

      Iterable<String> limitTypes = api.listTypes();
      assertEquals(Iterables.size(limitTypes), 3);
   }
   
   private static Predicate<RateLimit> isStatusRateLimit() {
      return new Predicate<RateLimit>() {
         @Override
         public boolean apply(RateLimit rateLimit) {
            return rateLimit.getUri().contains("status");
         }
      };
   }
}
