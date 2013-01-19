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
package org.jclouds.vcloud.director.v1_5.internal;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.testng.Assert.assertNotNull;

import java.util.Properties;

import org.jclouds.Constants;
import org.jclouds.date.DateService;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.internal.BaseRestApiExpectTest;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;
import org.testng.annotations.BeforeGroups;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.net.HttpHeaders;
import com.google.inject.Guice;
import com.jamesmurty.utils.XMLBuilder;

/**
 * Base class for writing vCloud Director REST api expect tests.
 * 
 * @author Adrian Cole
 */
public abstract class BaseVCloudDirectorExpectTest<T> extends BaseRestApiExpectTest<T> {
   
   public BaseVCloudDirectorExpectTest() {
      provider = "vcloud-director";
      identity = String.format("%s@%s", user, org);
      credential = password;
   }
   
   public static final String user = "adrian@jclouds.org";
   public static final String org = "JClouds";
   public static final String password = "password";
   public static final String token = "mIaR3/6Lna8DWImd7/JPR5rK8FcUHabt+G/UCJV5pJQ=";
   public static final String endpoint = "https://vcloudbeta.bluelock.com/api";

   protected static DateService dateService;

   @BeforeGroups("unit")
   protected static void setupDateService() {
      dateService = Guice.createInjector().getInstance(DateService.class);
      assertNotNull(dateService);
   }

   @Override
   public Properties setupProperties() {
      Properties props = new Properties();
      props.put(Constants.PROPERTY_MAX_RETRIES, 1);
      props.put(Constants.PROPERTY_ENDPOINT, endpoint);
      return props;
   }
   
   @Override
   public HttpRequestComparisonType compareHttpRequestAsType(HttpRequest input) {
      if (input.getPayload() == null || input.getPayload().getContentMetadata().getContentLength() == 0) {
         return HttpRequestComparisonType.DEFAULT;
      }
      return HttpRequestComparisonType.XML;
   }

   protected HttpRequest loginRequest = HttpRequest.builder().method("POST")
         .endpoint(endpoint + "/sessions")
         .addHeader("Accept", "*/*")
         .addHeader("Authorization", "Basic YWRyaWFuQGpjbG91ZHMub3JnQEpDbG91ZHM6cGFzc3dvcmQ=")
         .build();

   protected HttpResponse sessionResponse = HttpResponse.builder()
         .statusCode(200)
         .headers(ImmutableMultimap.<String, String> builder()
               .put("x-vcloud-authorization", token)
               .put("Set-Cookie", String.format("vcloud-token=%s; Secure; Path=/", token))
               .build())
         .payload(payloadFromResourceWithContentType("/session.xml", VCloudDirectorMediaType.SESSION + ";version=1.5"))
         .build();
      
   /**
    * Implicitly adds x-vcloud-authorization header with token. 
    * Provides convenience methods for priming a HttpRequest.Builder for vCloud testing
    *
    * @author danikov
    */
   @Deprecated
   public class VcloudHttpRequestPrimer {
      private Multimap<String, String> headers = LinkedListMultimap.create();
      private HttpRequest.Builder<?> builder = HttpRequest.builder();
      
      public VcloudHttpRequestPrimer() {
      }

      public VcloudHttpRequestPrimer apiCommand(String method, String command) {
         builder.method(method).endpoint(endpoint + command);
         return this;
      }
      
      public VcloudHttpRequestPrimer xmlFilePayload(String relativeFilePath, String mediaType) {
         builder.payload(payloadFromResourceWithContentType(relativeFilePath, mediaType));
         return this;
      }
      
      public VcloudHttpRequestPrimer headers(Multimap<String, String> headers) {
         this.headers.putAll(ImmutableMultimap.copyOf(checkNotNull(headers, "headers")));
         return this;
      }
      
      public VcloudHttpRequestPrimer acceptAnyMedia() {
         return acceptMedia(VCloudDirectorMediaType.ANY);
      }
      
      public VcloudHttpRequestPrimer acceptMedia(String media) {
         return header("Accept", media);
      }
      
      public VcloudHttpRequestPrimer header(String name, String value) {
         headers.put(checkNotNull(name, "header.name"), checkNotNull(value, "header.value"));
         return this;
      }
      
      public HttpRequest.Builder<?> httpRequestBuilder() {
         header("x-vcloud-authorization", token);
         header(HttpHeaders.COOKIE, "vcloud-token=" + token);
         builder.headers(headers);
         return builder;
      }
   }
   
   @Deprecated
   protected class VcloudHttpResponsePrimer {
      private HttpResponse.Builder<?> builder = HttpResponse.builder().statusCode(200);

      public VcloudHttpResponsePrimer() {
      }

      public VcloudHttpResponsePrimer xmlFilePayload(String relativeFilePath, String mediaType) {
         builder.payload(payloadFromResourceWithContentType(relativeFilePath, mediaType));
         return this;
      }
      public HttpResponse.Builder<?> httpResponseBuilder() {
         return builder;
      }
   }
   
   protected static XMLBuilder createXMLBuilder(String root){
      try {
         return XMLBuilder.create(root);
      } catch (Exception e) {
         throw Throwables.propagate(e);
      }
   }
   
   protected static String asString(XMLBuilder in){
      try {
         return in.asString();
      } catch (Exception e) {
         throw Throwables.propagate(e);
      }
   }
}
