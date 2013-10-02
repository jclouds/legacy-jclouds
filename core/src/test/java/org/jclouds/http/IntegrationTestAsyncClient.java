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
package org.jclouds.http;

import static com.google.common.util.concurrent.Futures.immediateFuture;

import java.io.Closeable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.jclouds.Fallbacks.FalseOnNotFoundOr404;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.options.HttpRequestOptions;
import org.jclouds.io.Payload;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.rest.binders.BindToJsonPayload;
import org.jclouds.rest.binders.BindToStringPayload;
import org.jclouds.util.Strings2;

import com.google.common.base.Function;
import com.google.common.collect.Multimap;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Provides;

/**
 * Sample test for the behaviour of our Integration Test jetty server.
 * 
 * @see IntegrationTestClient
 * @author Adrian Cole
 */
public interface IntegrationTestAsyncClient extends Closeable {
   @Target({ ElementType.METHOD })
   @Retention(RetentionPolicy.RUNTIME)
   @HttpMethod("ROWDY")
   public @interface ROWDY {
   }

   @ROWDY
   @Path("/objects/{id}")
   ListenableFuture<String> rowdy(@PathParam("id") String path);

   @HEAD
   @Path("/objects/{id}")
   @Fallback(FalseOnNotFoundOr404.class)
   ListenableFuture<Boolean> exists(@PathParam("id") String path);

   @GET
   @Path("/objects/{id}")
   ListenableFuture<String> download(@PathParam("id") String id);

   ListenableFuture<HttpResponse> invoke(HttpRequest request);
   
   @GET
   @Path("/{path}")
   ListenableFuture<String> synch(@PathParam("path") String id);

   @GET
   @Path("/objects/{id}")
   @Fallback(FooOnException.class)
   ListenableFuture<String> downloadException(@PathParam("id") String id, HttpRequestOptions options);

   static class FooOnException implements org.jclouds.Fallback<String> {
      public ListenableFuture<String> create(Throwable t) throws Exception {
         return immediateFuture("foo");
      }

      public String createOrPropagate(Throwable t) throws Exception {
         return "foo";
      }
   }

   @GET
   @Path("/objects/{id}")
   @Fallback(FooOnException.class)
   ListenableFuture<String> synchException(@PathParam("id") String id, @HeaderParam("Range") String header);

   @PUT
   @Path("/objects/{id}")
   ListenableFuture<String> upload(@PathParam("id") String id, @BinderParam(BindToStringPayload.class) String toPut);

   @POST
   @Path("/objects/{id}")
   ListenableFuture<String> post(@PathParam("id") String id, @BinderParam(BindToStringPayload.class) String toPut);

   @POST
   @Path("/objects/{id}")
   ListenableFuture<String> postAsInputStream(@PathParam("id") String id,
         @BinderParam(BindToInputStreamPayload.class) String toPut);

   static class BindToInputStreamPayload extends BindToStringPayload {
      @Override
      public <R extends HttpRequest> R bindToRequest(R request, Object payload) {
         request.setPayload(Strings2.toInputStream(payload.toString()));
         request.getPayload().getContentMetadata().setContentLength((long) payload.toString().getBytes().length);
         return request;
      }
   }

   @Singleton
   static class ResponsePayload implements Function<HttpResponse, Multimap<String, String>> {

      public Multimap<String, String> apply(HttpResponse from) {
         return from.getHeaders();
      }

   }

   @POST
   @Path("/objects/{id}")
   @ResponseParser(ResponsePayload.class)
   ListenableFuture<Multimap<String, String>> postPayloadAndReturnHeaders(@PathParam("id") String id, Payload payload);

   @POST
   @Path("/objects/{id}")
   @MapBinder(BindToJsonPayload.class)
   ListenableFuture<String> postJson(@PathParam("id") String id, @PayloadParam("key") String toPut);

   @GET
   @Path("/objects/{id}")
   @RequestFilters(Filter.class)
   ListenableFuture<String> downloadFilter(@PathParam("id") String id, @HeaderParam("filterme") String header);

   static class Filter implements HttpRequestFilter {
      public HttpRequest filter(HttpRequest request) throws HttpException {
         if (request.getHeaders().containsKey("filterme")) {
            request = request.toBuilder().replaceHeader("test", "test").build();
         }
         return request;
      }
   }

   @GET
   @Path("/objects/{id}")
   ListenableFuture<String> download(@PathParam("id") String id, @HeaderParam("test") String header);

   @GET
   @Path("/objects/{id}")
   @XMLResponseParser(BarHandler.class)
   ListenableFuture<String> downloadAndParse(@PathParam("id") String id);

   public static class BarHandler extends ParseSax.HandlerWithResult<String> {

      private String bar = null;
      private StringBuilder currentText = new StringBuilder();

      @Override
      public void endElement(String uri, String name, String qName) {
         if (qName.equals("bar")) {
            bar = currentText.toString();
         }
         currentText = new StringBuilder();
      }

      @Override
      public void characters(char ch[], int start, int length) {
         currentText.append(ch, start, length);

      }

      @Override
      public String getResult() {
         return bar;
      }

   }

   @PUT
   @Path("/objects/{id}")
   ListenableFuture<Void> putNothing(@PathParam("id") String id);

   @Provides
   StringBuilder newStringBuilder();

}
