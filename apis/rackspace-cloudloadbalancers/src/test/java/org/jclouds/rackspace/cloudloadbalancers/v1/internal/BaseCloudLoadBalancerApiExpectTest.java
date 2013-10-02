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
package org.jclouds.rackspace.cloudloadbalancers.v1.internal;
import static com.google.common.net.HttpHeaders.ACCEPT;
import static javax.ws.rs.HttpMethod.GET;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.rackspace.cloudidentity.v2_0.internal.RackspaceFixture;
import org.jclouds.rest.internal.BaseRestApiExpectTest;

/**
 * Base class for writing Cloud Load Balancer Expect tests
 * 
 * @author Everett Toews
 */
public class BaseCloudLoadBalancerApiExpectTest<T> extends BaseRestApiExpectTest<T> {
   protected HttpRequest rackspaceAuthWithUsernameAndApiKey;

   protected String authToken;
   protected HttpResponse responseWithAccess;

   public BaseCloudLoadBalancerApiExpectTest() {
      provider = "rackspace-cloudloadbalancers";

      rackspaceAuthWithUsernameAndApiKey = RackspaceFixture.INSTANCE
            .initialAuthWithUsernameAndApiKey(identity, credential);
      
      authToken = RackspaceFixture.INSTANCE.getAuthToken();
      responseWithAccess = RackspaceFixture.INSTANCE.responseWithAccess();
   }

   @Override
   protected HttpRequestComparisonType compareHttpRequestAsType(HttpRequest input) {
      return HttpRequestComparisonType.JSON;
   }

   protected HttpRequest.Builder<?> authenticatedGET() {
      return HttpRequest.builder()
            .method(GET)
            .addHeader(ACCEPT, APPLICATION_JSON)
            .addHeader("X-Auth-Token", authToken);
   }
}
