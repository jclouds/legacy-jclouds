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
package org.jclouds.openstack.swift.internal;

import java.util.Properties;

import javax.ws.rs.core.MediaType;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.keystone.v2_0.internal.KeystoneFixture;
import org.jclouds.rest.internal.BaseRestClientExpectTest;

/**
 * Base class for writing Swift Keystone Expect tests
 * 
 * @author Adrian Cole
 */
public class BaseSwiftKeystoneExpectTest<T> extends BaseRestClientExpectTest<T>  {
   protected HttpRequest keystoneAuthWithUsernameAndPassword;
   protected HttpRequest keystoneAuthWithUsernameAndPasswordAndTenantName;
   protected HttpRequest keystoneAuthWithAccessKeyAndSecretKeyAndTenantName;
   protected String authToken;
   protected HttpResponse responseWithKeystoneAccess;
   protected HttpRequest keystoneAuthWithAccessKeyAndSecretKeyAndTenantId;
   protected String identityWithTenantId;

   public BaseSwiftKeystoneExpectTest() {
      provider = "swift-keystone";
      keystoneAuthWithUsernameAndPassword = KeystoneFixture.INSTANCE.initialAuthWithUsernameAndPassword(identity,
            credential);
      keystoneAuthWithUsernameAndPasswordAndTenantName = KeystoneFixture.INSTANCE.initialAuthWithUsernameAndPasswordAndTenantName(identity,
            credential);
      keystoneAuthWithAccessKeyAndSecretKeyAndTenantName = KeystoneFixture.INSTANCE.initialAuthWithAccessKeyAndSecretKeyAndTenantName(identity,
            credential);
      keystoneAuthWithAccessKeyAndSecretKeyAndTenantId = KeystoneFixture.INSTANCE.initialAuthWithAccessKeyAndSecretKeyAndTenantId(identity,
              credential);
      
      authToken = KeystoneFixture.INSTANCE.getAuthToken();
      responseWithKeystoneAccess = KeystoneFixture.INSTANCE.responseWithAccess();
      // now, createContext arg will need tenant prefix
      identityWithTenantId = KeystoneFixture.INSTANCE.getTenantId() + ":" + identity;
      identity = KeystoneFixture.INSTANCE.getTenantName() + ":" + identity;
  }
   
   @Override
   protected Properties setupProperties() {
      Properties overrides = super.setupProperties();
      // hpcloud or openstack
      overrides.setProperty("jclouds.regions", "region-a.geo-1,RegionOne");
      return overrides;
   }
   
   protected HttpRequest.Builder<?> authenticatedGET() {
      return HttpRequest.builder()
                        .method("GET")
                        .addHeader("Accept", MediaType.APPLICATION_JSON)
                        .addHeader("X-Auth-Token", authToken);
   }
   
   @Override
   protected HttpRequestComparisonType compareHttpRequestAsType(HttpRequest input) {
      return HttpRequestComparisonType.JSON;
   }
}
