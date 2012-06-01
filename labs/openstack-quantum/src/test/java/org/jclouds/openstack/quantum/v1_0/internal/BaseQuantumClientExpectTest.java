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
package org.jclouds.openstack.quantum.v1_0.internal;

import java.net.URI;

import javax.ws.rs.core.MediaType;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.quantum.v1_0.QuantumClient;

import com.google.common.collect.ImmutableMultimap;

/**
 * Base class for writing Quantum Rest Client Expect tests
 * 
 * @author Adam Lowe
 */
public class BaseQuantumClientExpectTest extends BaseQuantumExpectTest<QuantumClient> {
   protected String endpoint = "https://csnode.jclouds.org:9696/v1.0";

   protected HttpRequest.Builder standardRequestBuilder(String endpoint) {
      return HttpRequest.builder().method("GET")
            .headers(ImmutableMultimap.of("Accept", MediaType.APPLICATION_JSON, "X-Auth-Token", authToken))
            .endpoint(URI.create(endpoint));
   }

   protected HttpResponse.Builder standardResponseBuilder(int status) {
      return HttpResponse.builder().statusCode(status);
   }

}
