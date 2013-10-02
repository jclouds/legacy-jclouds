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
package org.jclouds.gae;

import static com.google.appengine.api.urlfetch.FetchOptions.Builder.disallowTruncate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map.Entry;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.core.HttpHeaders;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpUtils;
import org.jclouds.io.ContentMetadataCodec;
import org.jclouds.io.Payload;

import com.google.appengine.api.urlfetch.FetchOptions;
import com.google.appengine.api.urlfetch.HTTPHeader;
import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.repackaged.com.google.common.base.Throwables;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Closeables;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class ConvertToGaeRequest implements Function<HttpRequest, HTTPRequest> {
   public static final String USER_AGENT = "jclouds/1.0 urlfetch/1.4.3";
   // http://code.google.com/appengine/docs/java/urlfetch/overview.html
   public final Set<String> prohibitedHeaders = ImmutableSet.of("Accept-Encoding", "Content-Length", "Host", "Var",
         "X-Forwarded-For");

   protected final HttpUtils utils;
   protected final ContentMetadataCodec contentMetadataCodec;

   @Inject
   ConvertToGaeRequest(HttpUtils utils, ContentMetadataCodec contentMetadataCodec) {
      this.utils = utils;
      this.contentMetadataCodec = contentMetadataCodec;
   }

   /**
    * byte [] content is replayable and the only content type supportable by GAE. As such, we
    * convert the original request content to a byte array.
    */
   @Override
   public HTTPRequest apply(HttpRequest request) {
      URL url = null;
      try {
         url = request.getEndpoint().toURL();
      } catch (MalformedURLException e) {
         Throwables.propagate(e);
      }

      FetchOptions options = disallowTruncate();
      options.doNotFollowRedirects();
      if (utils.relaxHostname() || utils.trustAllCerts())
         options.doNotFollowRedirects();
      options.setDeadline(10.0);

      HTTPRequest gaeRequest = new HTTPRequest(url, HTTPMethod.valueOf(request.getMethod().toString()), options);

      for (Entry<String, String> entry : request.getHeaders().entries()) {
         String header = entry.getKey();
         if (!prohibitedHeaders.contains(header))
            gaeRequest.addHeader(new HTTPHeader(header, entry.getValue()));
      }
      gaeRequest.addHeader(new HTTPHeader(HttpHeaders.USER_AGENT, USER_AGENT));
      /**
       * byte [] content is replayable and the only content type supportable by GAE. As such, we
       * convert the original request content to a byte array.
       */
      if (request.getPayload() != null) {
         InputStream input = request.getPayload().getInput();
         try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            request.getPayload().writeTo(out);
            byte[] array = out.toByteArray();
            if (!request.getPayload().isRepeatable()) {
               Payload oldPayload = request.getPayload();
               request.setPayload(array);
               HttpUtils.copy(oldPayload.getContentMetadata(), request.getPayload().getContentMetadata());
            }
            gaeRequest.setPayload(array);
            if (array.length > 0) {
               gaeRequest.setHeader(new HTTPHeader("Expect", "100-continue"));
            }
         } catch (IOException e) {
            Throwables.propagate(e);
         } finally {
            Closeables.closeQuietly(input);
         }

         for (Entry<String, String> header : contentMetadataCodec.toHeaders(
               request.getPayload().getContentMetadata()).entries()) {
            if (!prohibitedHeaders.contains(header.getKey()))
               gaeRequest.setHeader(new HTTPHeader(header.getKey(), header.getValue()));
         }
      }
      return gaeRequest;
   }
}
