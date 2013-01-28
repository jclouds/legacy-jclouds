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
package org.jclouds.rackspace.cloudloadbalancers.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.net.URI;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.jclouds.http.HttpResponse;
import org.jclouds.rackspace.cloudloadbalancers.CloudLoadBalancersApi;
import org.jclouds.rackspace.cloudloadbalancers.domain.AccessRule;
import org.jclouds.rackspace.cloudloadbalancers.domain.AccessRuleWithId;
import org.jclouds.rackspace.cloudloadbalancers.internal.BaseCloudLoadBalancerApiExpectTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

/**
 * @author Everett Toews
 */
@Test(groups = "unit")
public class AccessRuleApiExpectTest extends BaseCloudLoadBalancerApiExpectTest<CloudLoadBalancersApi> {
   public void testListAccessRules() {
      URI endpoint = URI.create("https://dfw.loadbalancers.api.rackspacecloud.com/v1.0/123123/loadbalancers/2000/accesslist");
      AccessRuleApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey,
            responseWithAccess, 
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/accesslist-list.json")).build()
      ).getAccessRuleApiForZoneAndLoadBalancer("DFW", 2000);

      Iterable<AccessRuleWithId> accessList = api.list();
      assertEquals(accessList, getAccessRules());
   }

   public void testCreateAccessRules() {
      URI endpoint = URI.create("https://dfw.loadbalancers.api.rackspacecloud.com/v1.0/123123/loadbalancers/2000/accesslist");
      AccessRuleApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey,
            responseWithAccess, 
            authenticatedGET().method("POST").endpoint(endpoint).payload(payloadFromResource("/accesslist-create.json")).build(),
            HttpResponse.builder().statusCode(200).build()
      ).getAccessRuleApiForZoneAndLoadBalancer("DFW", 2000);
   
      AccessRule accessRule1 = AccessRule.deny("206.160.163.21");
      AccessRule accessRule2 = AccessRule.deny("206.160.165.11");
      AccessRule accessRule3 = AccessRule.deny("206.160.163.22");
      
      List<AccessRule> accessList = ImmutableList.<AccessRule> of(accessRule1, accessRule2, accessRule3);
      api.create(accessList);
   }

   public void testRemoveSingleAccessRule() {
      URI endpoint = URI.create("https://dfw.loadbalancers.api.rackspacecloud.com/v1.0/123123/loadbalancers/2000/accesslist/23");
      AccessRuleApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey,
            responseWithAccess, 
            authenticatedGET().method("DELETE").endpoint(endpoint).replaceHeader("Accept", MediaType.WILDCARD).build(),
            HttpResponse.builder().statusCode(200).build()
      ).getAccessRuleApiForZoneAndLoadBalancer("DFW", 2000);

      assertTrue(api.delete(23));
   }

   public void testRemoveManyAccessRules() {
      URI endpoint = URI.create("https://dfw.loadbalancers.api.rackspacecloud.com/v1.0/123123/loadbalancers/2000/accesslist?id=23&id=24");
      AccessRuleApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey,
            responseWithAccess, 
            authenticatedGET().method("DELETE").endpoint(endpoint).replaceHeader("Accept", MediaType.WILDCARD).build(),
            HttpResponse.builder().statusCode(200).build()
      ).getAccessRuleApiForZoneAndLoadBalancer("DFW", 2000);

      List<Integer> accessRuleIds = ImmutableList.<Integer> of(23, 24);
      assertTrue(api.delete(accessRuleIds));
   }

   public void testRemoveAllAccessRules() {
      URI endpoint = URI.create("https://dfw.loadbalancers.api.rackspacecloud.com/v1.0/123123/loadbalancers/2000/accesslist");
      AccessRuleApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey,
            responseWithAccess, 
            authenticatedGET().method("DELETE").endpoint(endpoint).replaceHeader("Accept", MediaType.WILDCARD).build(),
            HttpResponse.builder().statusCode(200).build()
      ).getAccessRuleApiForZoneAndLoadBalancer("DFW", 2000);

      assertTrue(api.deleteAll());
   }

   private Iterable<AccessRuleWithId> getAccessRules() {
      AccessRuleWithId accessRule1 = new AccessRuleWithId(23, "206.160.163.21", AccessRule.Type.DENY);
      AccessRuleWithId accessRule2 = new AccessRuleWithId(24, "206.160.165.11", AccessRule.Type.DENY);
      AccessRuleWithId accessRule3 = new AccessRuleWithId(25, "206.160.163.22", AccessRule.Type.DENY);

      return ImmutableList.<AccessRuleWithId> of(accessRule1, accessRule2, accessRule3);
   }
}
