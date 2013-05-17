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
package org.jclouds.openstack.keystone.v2_0.internal;

import static org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties.SERVICE_TYPE;

import java.util.Properties;

import javax.ws.rs.core.MediaType;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.keystone.v2_0.KeystoneApiMetadata;
import org.jclouds.openstack.v2_0.ServiceType;
import org.jclouds.rest.internal.BaseRestApiExpectTest;

import com.google.common.base.Objects;

/**
 * Base class for writing KeyStone 2.0 Rest Api Expect tests
 * 
 * @author Adam Lowe
 */
public class BaseKeystoneRestApiExpectTest<S> extends BaseRestApiExpectTest<S> {
   protected HttpRequest keystoneAuthWithUsernameAndPasswordAndTenantName;
   protected HttpRequest keystoneAuthWithUsernameAndPassword;
   protected HttpRequest keystoneAuthWithAccessKeyAndSecretKey;
   protected String authToken;
   protected HttpResponse responseWithKeystoneAccess;
   protected String endpoint = "http://localhost:5000";

   public BaseKeystoneRestApiExpectTest() {
      provider = "openstack-keystone";
      keystoneAuthWithUsernameAndPasswordAndTenantName = KeystoneFixture.INSTANCE
            .initialAuthWithUsernameAndPasswordAndTenantName(identity, credential);
      keystoneAuthWithUsernameAndPassword = KeystoneFixture.INSTANCE
            .initialAuthWithUsernameAndPassword(identity, credential);
      keystoneAuthWithAccessKeyAndSecretKey = KeystoneFixture.INSTANCE
            .initialAuthWithAccessKeyAndSecretKeyAndTenantName(identity, credential);

      authToken = KeystoneFixture.INSTANCE.getAuthToken();
      responseWithKeystoneAccess = KeystoneFixture.INSTANCE.responseWithAccess();
      // now, createContext arg will need tenant prefix
      identity = KeystoneFixture.INSTANCE.getTenantName() + ":" + identity;
   }

   protected HttpRequest.Builder<?> authenticatedGET() {
      return HttpRequest.builder().method("GET").addHeader("Accept", MediaType.APPLICATION_JSON)
            .addHeader("X-Auth-Token", authToken);
   }

   @Override
   protected Properties setupProperties() {
      Properties props = super.setupProperties();
      props.put(SERVICE_TYPE, ServiceType.IDENTITY);
      return props;
   }

   @Override
   protected HttpRequestComparisonType compareHttpRequestAsType(HttpRequest input) {
      return Objects.equal("HEAD", input.getMethod()) ? HttpRequestComparisonType.DEFAULT
            : HttpRequestComparisonType.JSON;
   }

   @Override
   protected ApiMetadata createApiMetadata() {
      return new KeystoneApiMetadata();
   }

}
