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
package org.jclouds.openstack.nova.v2_0;

import static org.testng.Assert.assertEquals;

import java.util.Properties;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties;
import org.jclouds.openstack.keystone.v2_0.internal.KeystoneFixture;
import org.jclouds.openstack.nova.v2_0.internal.BaseNovaApiExpectTest;
import org.jclouds.openstack.nova.v2_0.parse.ParseServerListTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * 
 * @see KeystoneProperties#CREDENTIAL_TYPE
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "AccessKeyAndSecretKeyAndTenantIdAuthenticationExpectTest")
public class AccessKeyAndSecretKeyAndTenantIdAuthenticationExpectTest extends BaseNovaApiExpectTest {
   public AccessKeyAndSecretKeyAndTenantIdAuthenticationExpectTest() {
      identity = identityWithTenantId;
   }

   /**
    * this reflects the properties that a user would pass to createContext
    */
   @Override
   protected Properties setupProperties() {
      Properties contextProperties = super.setupProperties();
      contextProperties.setProperty("jclouds.keystone.credential-type", "apiAccessKeyCredentials");
      contextProperties.setProperty("jclouds.keystone.tenant-id", KeystoneFixture.INSTANCE.getTenantId());
      return contextProperties;
   }

   public void testListServersWhenResponseIs2xx() throws Exception {
      HttpRequest listServers = HttpRequest
            .builder()
            .method("GET")
            .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/servers")
            .addHeader("Accept", "application/json")
            .addHeader("X-Auth-Token", authToken).build();

      HttpResponse listServersResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/server_list.json")).build();

      NovaApi apiWhenServersExist = requestsSendResponses(keystoneAuthWithAccessKeyAndSecretKeyAndTenantId,
            responseWithKeystoneAccess, listServers, listServersResponse);

      assertEquals(apiWhenServersExist.getConfiguredZones(), ImmutableSet.of("az-1.region-a.geo-1", "az-2.region-a.geo-1", "az-3.region-a.geo-1"));

      assertEquals(apiWhenServersExist.getServerApiForZone("az-1.region-a.geo-1").list().concat().toString(),
            new ParseServerListTest().expected().toString());
   }

}

