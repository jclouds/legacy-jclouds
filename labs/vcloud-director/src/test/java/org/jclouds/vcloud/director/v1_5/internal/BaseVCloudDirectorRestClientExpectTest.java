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

import java.net.URI;
import java.util.Properties;

import org.jclouds.Constants;
import org.jclouds.date.DateService;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.BaseRestClientExpectTest;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorClient;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;
import org.jclouds.vcloud.director.v1_5.domain.Reference;
import org.testng.annotations.BeforeGroups;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Guice;

/**
 * Base class for writing KeyStone Rest Client Expect tests
 * 
 * @author Adrian Cole
 */
public class BaseVCloudDirectorRestClientExpectTest extends BaseRestClientExpectTest<VCloudDirectorClient> {

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
      return props;
   }
   
   @Override
   public HttpRequestComparisonType compareHttpRequestAsType(HttpRequest input) {
      if (input.getPayload() == null || input.getPayload().getContentMetadata().getContentLength() == 0) {
         return HttpRequestComparisonType.DEFAULT;
      }
      return HttpRequestComparisonType.XML;
   }

   protected HttpRequest loginRequest = HttpRequest.builder()
         .method("POST")
         .endpoint(URI.create(endpoint + "/sessions"))
         .headers(ImmutableMultimap.<String, String>builder()
               .put("Accept", "*/*")
               .put("Authorization", "Basic YWRyaWFuQGpjbG91ZHMub3JnQEpDbG91ZHM6cGFzc3dvcmQ=")
               .build())
         .build();

   protected HttpResponse sessionResponse = HttpResponse.builder()
         .statusCode(200)
         .headers(ImmutableMultimap.<String, String> builder()
               .put("x-vcloud-authorization", token)
               .put("Set-Cookie", String.format("vcloud-token=%s; Secure; Path=/", token))
               .build())
         .payload(payloadFromResourceWithContentType("/session.xml", VCloudDirectorMediaType.SESSION + ";version=1.5"))
         .build();

   public BaseVCloudDirectorRestClientExpectTest() {
      provider = "vcloud-director";
      identity = String.format("%s@%s", user, org);
      credential = password;
   }
   
   @Deprecated
   protected HttpRequest getStandardRequest(String method, String path) {
      return getStandardRequest(method, path, VCloudDirectorMediaType.ANY);
   }

   @Deprecated
   protected HttpRequest getStandardRequest(String method, URI uri) {
      return getStandardRequest(method, uri, VCloudDirectorMediaType.ANY);
   }

   @Deprecated
   protected HttpRequest getStandardRequest(String method, String path, String mediaType) {
      return getStandardRequest(method, URI.create(endpoint + path), VCloudDirectorMediaType.ANY);
   }

   @Deprecated
   protected HttpRequest getStandardPayloadRequest(String method, String command, String relativeFilePath, 
         String postMediaType) {
      return getStandardPayloadRequest(method, URI.create(endpoint + command), relativeFilePath, postMediaType);
   }
   
   @Deprecated
   protected HttpRequest getStandardPayloadRequest(String method, URI uri, String relativeFilePath, 
         String postMediaType) {
      return getStandardRequestWithPayload(method, uri, VCloudDirectorMediaType.ANY, relativeFilePath, postMediaType);
   }
   
   @Deprecated
   protected HttpRequest getStandardRequest(String method, URI uri, String mediaType) {
      return HttpRequest.builder()
            .method(method)
            .endpoint(uri)
            .headers(ImmutableMultimap.<String, String> builder()
                  .put("Accept", mediaType)
                  .put("x-vcloud-authorization", token)
                  .build())
            .build();
   }
   
   @Deprecated
   protected HttpRequest getStandardRequestWithPayload(String method, String path, String relativeFilePath, String mediaType) {
      return getStandardRequestWithPayload(method, path, VCloudDirectorMediaType.ANY, relativeFilePath, mediaType);
   }
   
   @Deprecated
   protected HttpRequest getStandardRequestWithPayload(String method, URI uri, String relativeFilePath, String mediaType) {
      return getStandardRequestWithPayload(method, uri, VCloudDirectorMediaType.ANY, relativeFilePath, mediaType);
   }

   @Deprecated
   protected HttpRequest getStandardRequestWithPayload(String method, String path, String acceptType, String relativeFilePath, String mediaType) {
      URI uri = URI.create(endpoint + path);
      return getStandardRequestWithPayload(method, uri, acceptType, relativeFilePath, mediaType);
   }

   @Deprecated
   protected HttpRequest getStandardRequestWithPayload(String method, URI uri, String acceptType, String relativeFilePath, String mediaType) {
      return HttpRequest.builder()
            .method(method)
            .endpoint(uri)
            .headers(ImmutableMultimap.<String, String> builder()
                  .put("Accept", acceptType)
                  .put("x-vcloud-authorization", token)
                  .build())
            .payload(payloadFromResourceWithContentType(relativeFilePath, mediaType))
            .build();
   }

   @Deprecated
   protected HttpResponse getStandardPayloadResponse(String relativeFilePath, String mediaType) {
      return getStandardPayloadResponse(200, relativeFilePath, mediaType);
   }

   @Deprecated
   protected HttpResponse getStandardPayloadResponse(int statusCode, String relativeFilePath, String mediaType) {
      return HttpResponse.builder()
            .statusCode(statusCode)
            .payload(payloadFromResourceWithContentType(relativeFilePath, mediaType + ";version=1.5"))
            .build();
   }
      
   /**
    * Implicitly adds x-vcloud-authorization header with token. 
    * Provides convenience methods for priming a HttpRequest.Builder for vCloud testing
    *
    * @author danikov
    */
   protected class VcloudHttpRequestPrimer {
      private Multimap<String, String> headers = LinkedListMultimap.create();
      private HttpRequest.Builder builder = HttpRequest.builder();
      
      public VcloudHttpRequestPrimer() {
      }

      public VcloudHttpRequestPrimer apiCommand(String method, String command) {
         builder.method(method).endpoint(URI.create(endpoint + command));
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
      
      public HttpRequest.Builder httpRequestBuilder() {
         header("x-vcloud-authorization", token);
         builder.headers(headers);
         return builder;
      }
   }
   
   protected class VcloudHttpResponsePrimer {
      private HttpResponse.Builder builder = HttpResponse.builder();

      public VcloudHttpResponsePrimer() {
      }

      public VcloudHttpResponsePrimer xmlFilePayload(String relativeFilePath, String mediaType) {
         builder.payload(payloadFromResourceWithContentType(relativeFilePath, mediaType));
         return this;
      }
      public HttpResponse.Builder httpResponseBuilder() {
         return builder;
      }
   }
   
   public URI toAdminUri(Reference ref) {
      return toAdminUri(ref.getHref());
   }
   
   public URI toAdminUri(URI uri) {
      return Reference.builder().href(uri).build().toAdminReference(endpoint).getHref();
   }
}
