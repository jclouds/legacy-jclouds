/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.http;

import java.io.File;
import java.util.Collections;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.HttpHeaders;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.options.HttpRequestOptions;
import org.jclouds.rest.Binder;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.MapPayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.rest.binders.BindMapToMatrixParams;
import org.jclouds.rest.binders.BindToJsonPayload;
import org.jclouds.rest.binders.BindToStringPayload;
import org.jclouds.rest.internal.RestAnnotationProcessorTest.Localhost;
import org.jclouds.util.Utils;

import com.google.common.base.Function;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * Sample test for the behaviour of our Integration Test jetty server.
 * 
 * @author Adrian Cole
 */
@Endpoint(Localhost.class)
public interface IntegrationTestAsyncClient {

   @HEAD
   @Path("objects/{id}")
   ListenableFuture<Boolean> exists(@PathParam("id") String path);

   @GET
   @Path("objects/{id}")
   ListenableFuture<String> download(@PathParam("id") String id);

   @GET
   @Path("{path}")
   ListenableFuture<String> synch(@PathParam("path") String id);

   @GET
   @Path("objects/{id}")
   @ExceptionParser(FooOnException.class)
   ListenableFuture<String> downloadException(@PathParam("id") String id, HttpRequestOptions options);

   static class FooOnException implements Function<Exception, String> {

      public String apply(Exception from) {
         return "foo";
      }

   }

   @GET
   @Path("objects/{id}")
   @ExceptionParser(FooOnException.class)
   ListenableFuture<String> synchException(@PathParam("id") String id,
            @HeaderParam("Range") String header);

   @PUT
   @Path("objects/{id}")
   ListenableFuture<String> upload(@PathParam("id") String id,
            @BinderParam(BindToStringPayload.class) String toPut);

   @POST
   @Path("objects/{id}")
   ListenableFuture<String> post(@PathParam("id") String id,
            @BinderParam(BindToStringPayload.class) String toPut);

   @POST
   @Path("objects/{id}")
   ListenableFuture<String> postAsInputStream(@PathParam("id") String id,
            @BinderParam(BindToInputStreamPayload.class) String toPut);

   static class BindToInputStreamPayload extends BindToStringPayload {
      @Override
      public void bindToRequest(HttpRequest request, Object payload) {
         super.bindToRequest(request, payload);
         request.setPayload(Utils.toInputStream(payload.toString()));
      }
   }

   @POST
   @Path("objects/{id}")
   ListenableFuture<String> postWithMd5(@PathParam("id") String id,
            @HeaderParam("Content-MD5") String base64MD5,
            @BinderParam(BindToFilePayload.class) File file);

   static class BindToFilePayload implements Binder {
      @Override
      public void bindToRequest(HttpRequest request, Object payload) {
         File f = (File) payload;
         if (request.getFirstHeaderOrNull(HttpHeaders.CONTENT_TYPE) == null)
            request.getHeaders().put(HttpHeaders.CONTENT_TYPE, "application/unknown");
         request.getHeaders().replaceValues(HttpHeaders.CONTENT_LENGTH,
                  Collections.singletonList(f.length() + ""));
         request.setPayload(f);
      }
   }

   @POST
   @Path("objects/{id}")
   @MapBinder(BindToJsonPayload.class)
   ListenableFuture<String> postJson(@PathParam("id") String id,
            @MapPayloadParam("key") String toPut);

   @POST
   @Path("objects/{id}/action/{action}")
   ListenableFuture<String> action(@PathParam("id") String id, @PathParam("action") String action,
            @BinderParam(BindMapToMatrixParams.class) Map<String, String> options);

   @GET
   @Path("objects/{id}")
   @RequestFilters(Filter.class)
   ListenableFuture<String> downloadFilter(@PathParam("id") String id,
            @HeaderParam("filterme") String header);

   static class Filter implements HttpRequestFilter {
      public void filter(HttpRequest request) throws HttpException {
         if (request.getHeaders().containsKey("filterme")) {
            request.getHeaders().put("test", "test");
         }
      }
   }

   @GET
   @Path("objects/{id}")
   ListenableFuture<String> download(@PathParam("id") String id, @HeaderParam("test") String header);

   @GET
   @Path("objects/{id}")
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

   StringBuffer newStringBuffer();

}
