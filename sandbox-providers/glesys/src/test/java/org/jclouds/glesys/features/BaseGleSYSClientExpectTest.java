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
package org.jclouds.glesys.features;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import org.jclouds.glesys.GleSYSClient;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.options.BaseHttpRequestOptions;
import org.jclouds.io.payloads.StringPayload;
import org.jclouds.rest.BaseRestClientExpectTest;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.jclouds.io.Payloads.newUrlEncodedFormPayload;

/**
 * Support for short-hand generation of ClientExpectTests for the GleSYS clients
 * 
 * @author Adam Lowe
 */
public abstract class BaseGleSYSClientExpectTest<T> extends BaseRestClientExpectTest<GleSYSClient> {
   protected String remoteServicePrefix;

   public BaseGleSYSClientExpectTest() {
      provider = "glesys";
   }

   protected abstract T getClient(GleSYSClient gleSYSClient);

   protected Map.Entry<String, String> entry(String key, Object value) {
      return Maps.immutableEntry(key, value.toString());
   }

   /**
    * Build a mock of a GleSYS client that responds as instructed
    *
    * @param remoteCall       the name of the expected call on the remote server
    * @param httpMethod       "GET" or "POST"
    * @param returnCode       the http status code expected (ordinarily 200)
    * @param expectedResponse ensure this is not-null for calls that expect a response - for OK responses this should be
    *                         the classpath location of a file with a valid server response, for errors any String
    * @param args             either Map.Entry or BaseHttpRequestOption objects that make up the arguments to the method
    * @return the appropriate client for test to invoke methods on (by calling getClient() in the appropriate subclass)
    */
   @SuppressWarnings("unchecked")
   protected T createMock(String remoteCall, String httpMethod, int returnCode, String expectedResponse, Object... args) throws Exception {
      List<Object> argValues = new ArrayList<Object>();

      Multimap<String, String> map = LinkedHashMultimap.create();

      for (Object arg : args) {
         if (arg instanceof BaseHttpRequestOptions) {
            for (Map.Entry<String, String> httpEntry : ((BaseHttpRequestOptions) arg).buildFormParameters().entries()) {
               map.put(httpEntry.getKey(), httpEntry.getValue());
            }
            argValues.add(arg);
         } else {
            Map.Entry<String, String> entry = (Map.Entry<String, String>) arg;
            map.put(entry.getKey(), entry.getValue());
            argValues.add(entry.getValue());
         }
      }

      HttpRequest.Builder httpRequestBuilder = HttpRequest.builder().method(httpMethod).endpoint(
            URI.create("https://api.glesys.com/" + remoteServicePrefix + "/" + remoteCall + "/format/json"));

      if (expectedResponse == null) {
         httpRequestBuilder.headers(ImmutableMultimap.<String, String>builder()
               .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build());
      } else {
         httpRequestBuilder.headers(ImmutableMultimap.<String, String>builder()
               .put("Accept", "application/json").put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build());
      }

      if (!map.isEmpty()) {
         httpRequestBuilder.payload(newUrlEncodedFormPayload(
               ImmutableMultimap.<String, String>builder().putAll(map).build()));
      }

      HttpResponse.Builder responseBuilder = HttpResponse.builder().statusCode(returnCode);

      if (expectedResponse != null) {
         if (returnCode < 300) {
            responseBuilder.payload(payloadFromResource(expectedResponse));
         } else {
            responseBuilder.payload(new StringPayload(expectedResponse));
         }
      }

      return getClient(requestSendsResponse(httpRequestBuilder.build(), responseBuilder.build()));
   }
}
