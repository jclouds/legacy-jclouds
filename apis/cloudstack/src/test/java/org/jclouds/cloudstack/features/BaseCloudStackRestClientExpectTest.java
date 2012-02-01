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
package org.jclouds.cloudstack.features;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;
import org.jclouds.cloudstack.CloudStackContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.logging.config.NullLoggingModule;
import org.jclouds.rest.BaseRestClientExpectTest;

import java.net.URI;
import java.net.URLEncoder;
import java.util.Properties;

import static org.jclouds.crypto.CryptoStreams.md5Hex;

/**
 * Base class for writing CloudStack Rest Client Expect tests
 * 
 * @author Andrei Savu
 */
public abstract class BaseCloudStackRestClientExpectTest<S> extends BaseRestClientExpectTest<S> {

   public BaseCloudStackRestClientExpectTest() {
      provider = "cloudstack";
   }

   @Override
   public S createClient(Function<HttpRequest, HttpResponse> fn, Module module, Properties props) {
      return clientFrom(CloudStackContext.class.cast(new ComputeServiceContextFactory(setupRestProperties())
               .createContext(provider, "identity", "credential", ImmutableSet.<Module> of(new ExpectModule(fn),
                        new NullLoggingModule(), module), props)));
   }

   protected abstract S clientFrom(CloudStackContext context);

   protected final HttpRequest loginRequest = HttpRequest.builder()
     .method("GET")
     .endpoint(
        URI.create("http://localhost:8080/client/api?response=json&command=login&" +
           "username=identity&password=" + md5Hex("credential")+ "&domain="))
     .headers(
        ImmutableMultimap.<String, String>builder()
           .put("Accept", "application/json")
           .build())
     .build();

   protected final String jSessionId = "90DD65D13AEAA590ECCA312D150B9F6D";
   protected final String sessionKey = "uYT4/MNiglgAKiZRQkvV8QP8gn0=";
   
   protected final HttpResponse loginResponse = HttpResponse.builder()
      .statusCode(200)
      .headers(
        ImmutableMultimap.<String, String>builder()
           .put("Set-Cookie", "JSESSIONID="+jSessionId+"; Path=/client")
           .build())
      .payload(payloadFromResource("/loginresponse.json"))
      .build();

   @SuppressWarnings("deprecation")
   protected final HttpRequest logoutRequest = HttpRequest.builder()
     .method("GET")
     .endpoint(
        URI.create("http://localhost:8080/client/api?response=json&command=logout&" +
           "sessionkey=" + URLEncoder.encode(sessionKey)))
     .build();
   
   protected final HttpResponse logoutResponse = HttpResponse.builder().statusCode(200).build();

}
