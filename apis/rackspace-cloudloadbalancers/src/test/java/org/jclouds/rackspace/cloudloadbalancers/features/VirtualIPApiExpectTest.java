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

import java.net.URI;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.jclouds.http.HttpResponse;
import org.jclouds.rackspace.cloudloadbalancers.CloudLoadBalancersApi;
import org.jclouds.rackspace.cloudloadbalancers.domain.VirtualIP;
import org.jclouds.rackspace.cloudloadbalancers.domain.VirtualIPWithId;
import org.jclouds.rackspace.cloudloadbalancers.internal.BaseCloudLoadBalancerApiExpectTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

/**
 * @author Everett Toews
 */
@Test(groups = "unit")
public class VirtualIPApiExpectTest extends BaseCloudLoadBalancerApiExpectTest<CloudLoadBalancersApi> {
   public void testListVirtualIPs() {
      URI endpoint = URI.create("https://dfw.loadbalancers.api.rackspacecloud.com/v1.0/123123/loadbalancers/2000/virtualips");
      VirtualIPApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey,
            responseWithAccess, 
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/virtualips-list.json")).build()
      ).getVirtualIPApiForZoneAndLoadBalancer("DFW", 2000);

      Iterable<VirtualIPWithId> virtualIPs = api.list();
      assertEquals(virtualIPs, getVirtualIPs());
   }

   public void testCreateVirtualIPs() {
      URI endpoint = URI.create("https://dfw.loadbalancers.api.rackspacecloud.com/v1.0/123123/loadbalancers/2000/virtualips");
      VirtualIPApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey,
            responseWithAccess, 
            authenticatedGET().method("POST").endpoint(endpoint).payload(payloadFromResource("/virtualips-create.json")).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/virtualips-create-response.json")).build()
      ).getVirtualIPApiForZoneAndLoadBalancer("DFW", 2000);
         
      api.create(VirtualIP.publicIPv6());
   }

   public void testRemoveSingleVirtualIP() {
      URI endpoint = URI.create("https://dfw.loadbalancers.api.rackspacecloud.com/v1.0/123123/loadbalancers/2000/virtualips/23");
      VirtualIPApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey,
            responseWithAccess, 
            authenticatedGET().method("DELETE").endpoint(endpoint).replaceHeader("Accept", MediaType.WILDCARD).build(),
            HttpResponse.builder().statusCode(200).build()
      ).getVirtualIPApiForZoneAndLoadBalancer("DFW", 2000);

      api.remove(23);
   }

   public void testRemoveManyVirtualIPs() {
      URI endpoint = URI.create("https://dfw.loadbalancers.api.rackspacecloud.com/v1.0/123123/loadbalancers/2000/virtualips?id=23&id=24");
      VirtualIPApi api = requestsSendResponses(
            rackspaceAuthWithUsernameAndApiKey,
            responseWithAccess, 
            authenticatedGET().method("DELETE").endpoint(endpoint).replaceHeader("Accept", MediaType.WILDCARD).build(),
            HttpResponse.builder().statusCode(200).build()
      ).getVirtualIPApiForZoneAndLoadBalancer("DFW", 2000);

      List<Integer> virtualIPIds = ImmutableList.<Integer> of(23, 24);
      api.remove(virtualIPIds);
   }

   private Iterable<VirtualIPWithId> getVirtualIPs() {
      VirtualIPWithId virtualIP1 = new VirtualIPWithId(VirtualIP.Type.PUBLIC, VirtualIP.IPVersion.IPV4, 5557, "166.78.34.87");
      VirtualIPWithId virtualIP2 = new VirtualIPWithId(VirtualIP.Type.PUBLIC, VirtualIP.IPVersion.IPV6, 9076419, "2001:4800:7901:0000:9a32:3c2a:0000:0001");
      VirtualIPWithId virtualIP3 = new VirtualIPWithId(VirtualIP.Type.PUBLIC, VirtualIP.IPVersion.IPV6, 9079727, "2001:4800:7901:0000:9a32:3c2a:0000:0002");

      return ImmutableList.<VirtualIPWithId> of(virtualIP1, virtualIP2, virtualIP3);
   }
}
