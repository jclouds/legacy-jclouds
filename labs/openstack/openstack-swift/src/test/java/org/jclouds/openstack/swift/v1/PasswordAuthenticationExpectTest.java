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
package org.jclouds.openstack.swift.v1;

import static org.testng.Assert.assertEquals;

import java.util.Properties;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties;
import org.jclouds.openstack.swift.v1.internal.BaseSwiftApiExpectTest;
import org.jclouds.openstack.swift.v1.parse.ParseContainerListTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * 
 * @see KeystoneProperties#CREDENTIAL_TYPE
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "PasswordAuthenticationExpectTest")
public class PasswordAuthenticationExpectTest extends BaseSwiftApiExpectTest {

   /**
    * this reflects the properties that a user would pass to createContext
    */
   @Override
   protected Properties setupProperties() {
      Properties contextProperties = super.setupProperties();
      contextProperties.setProperty("jclouds.keystone.credential-type", "passwordCredentials");
      return contextProperties;
   }

   public void testListContainersWhenResponseIs2xx() throws Exception {
      HttpRequest list = HttpRequest
            .builder()
            .method("GET")
            .endpoint("https://objects.jclouds.org/v1.0/40806637803162/?format=json")
            .addHeader("Accept", "application/json")
            .addHeader("X-Auth-Token", authToken).build();

      HttpResponse listResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/container_list.json")).build();

      SwiftApi apiWhenContainersExist = requestsSendResponses(keystoneAuthWithUsernameAndPassword,
            responseWithKeystoneAccess, list, listResponse);

      assertEquals(apiWhenContainersExist.getConfiguredRegions(), ImmutableSet.of("region-a.geo-1"));

      assertEquals(apiWhenContainersExist.getContainerApiForRegion("region-a.geo-1").list().toString(),
            new ParseContainerListTest().expected().toString());
   }

}
