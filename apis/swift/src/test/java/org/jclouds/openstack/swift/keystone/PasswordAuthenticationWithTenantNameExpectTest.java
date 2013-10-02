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
package org.jclouds.openstack.swift.keystone;

import static org.testng.Assert.assertEquals;

import java.util.Properties;

import javax.ws.rs.core.MediaType;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties;
import org.jclouds.openstack.swift.SwiftKeystoneClient;
import org.jclouds.openstack.swift.internal.BaseSwiftKeystoneExpectTest;
import org.testng.annotations.Test;

/**
 * 
 * @see KeystoneProperties#CREDENTIAL_TYPE
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "PasswordAuthenticationWithTenantNameExpectTest")
public class PasswordAuthenticationWithTenantNameExpectTest extends BaseSwiftKeystoneExpectTest<SwiftKeystoneClient> {

   /**
    * this reflects the properties that a user would pass to createContext
    */
   @Override
   protected Properties setupProperties() {
      Properties contextProperties = super.setupProperties();
      contextProperties.setProperty("jclouds.keystone.credential-type", "passwordCredentials");
      return contextProperties;
   }

   public void testContainerExistsWhenResponseIs2xx() throws Exception {
      HttpRequest containerExists = HttpRequest.builder()
                                               .method("HEAD")
                                               .endpoint("https://objects.jclouds.org/v1.0/40806637803162/container")
                                               .addHeader("Accept", MediaType.WILDCARD)
                                               .addHeader("X-Auth-Token", authToken).build();

      HttpResponse containerExistsResponse = HttpResponse.builder().statusCode(200).build();

      SwiftKeystoneClient clientWhenContainerExists = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, containerExists, containerExistsResponse);

      assertEquals(clientWhenContainerExists.containerExists("container"), true);
   }


}
