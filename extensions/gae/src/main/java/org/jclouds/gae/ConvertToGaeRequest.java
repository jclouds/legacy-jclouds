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
package org.jclouds.gae;

import static com.google.appengine.api.urlfetch.FetchOptions.Builder.disallowTruncate;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.io.ByteStreams.toByteArray;
import static com.google.common.io.Closeables.closeQuietly;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.core.HttpHeaders;

import org.jclouds.encryption.EncryptionService;
import org.jclouds.http.HttpRequest;
import org.jclouds.io.Payload;

import com.google.appengine.api.urlfetch.FetchOptions;
import com.google.appengine.api.urlfetch.HTTPHeader;
import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.repackaged.com.google.common.base.Throwables;
import com.google.common.base.Function;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class ConvertToGaeRequest implements Function<HttpRequest, HTTPRequest> {
   public static final String USER_AGENT = "jclouds/1.0 urlfetch/1.3.5";

   private final EncryptionService encryptionService;

   @Inject
   public ConvertToGaeRequest(EncryptionService encryptionService) {
      this.encryptionService = encryptionService;
   }

   /**
    * byte [] content is replayable and the only content type supportable by
    * GAE. As such, we convert the original request content to a byte array.
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

      HTTPRequest gaeRequest = new HTTPRequest(url, HTTPMethod.valueOf(request.getMethod().toString()), options);

      for (String header : request.getHeaders().keySet()) {
         for (String value : request.getHeaders().get(header)) {
            if (!"Transfer-Encoding".equals(header))
               gaeRequest.addHeader(new HTTPHeader(header, value));
         }
      }
      gaeRequest.addHeader(new HTTPHeader(HttpHeaders.USER_AGENT, USER_AGENT));
      /**
       * byte [] content is replayable and the only content type supportable by
       * GAE. As such, we convert the original request content to a byte array.
       */
      if (request.getPayload() != null) {
         InputStream input = request.getPayload().getInput();
         try {
            byte[] array = toByteArray(input);
            if (!request.getPayload().isRepeatable()) {
               Payload oldPayload = request.getPayload();
               request.setPayload(array);
               if (oldPayload.getContentMD5() != null)
                  request.getPayload().setContentMD5(oldPayload.getContentMD5());
               if (oldPayload.getContentType() != null)
                  request.getPayload().setContentType(oldPayload.getContentType());
            }
            gaeRequest.setPayload(array);
         } catch (IOException e) {
            Throwables.propagate(e);
         } finally {
            closeQuietly(input);
         }
         if (request.getPayload().getContentMD5() != null)
            gaeRequest.setHeader(new HTTPHeader("Content-MD5", encryptionService.base64(request.getPayload()
                  .getContentMD5())));
         if (request.getPayload().getContentType() != null)
            gaeRequest.setHeader(new HTTPHeader(HttpHeaders.CONTENT_TYPE, request.getPayload().getContentType()));
         Long length = checkNotNull(request.getPayload().getContentLength(), "payload.getContentLength");
         gaeRequest.setHeader(new HTTPHeader(HttpHeaders.CONTENT_LENGTH, length.toString()));
      } else {
         gaeRequest.setHeader(new HTTPHeader(HttpHeaders.CONTENT_LENGTH, "0"));
      }
      return gaeRequest;
   }
}