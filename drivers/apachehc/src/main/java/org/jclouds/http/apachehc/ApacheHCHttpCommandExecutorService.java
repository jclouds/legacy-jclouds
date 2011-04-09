/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.http.apachehc;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ExecutorService;

import javax.inject.Named;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.jclouds.Constants;
import org.jclouds.crypto.CryptoStreams;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpUtils;
import org.jclouds.http.IOExceptionRetryHandler;
import org.jclouds.http.handlers.DelegatingErrorHandler;
import org.jclouds.http.handlers.DelegatingRetryHandler;
import org.jclouds.http.internal.BaseHttpCommandExecutorService;
import org.jclouds.http.internal.HttpWire;
import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import org.jclouds.rest.internal.RestAnnotationProcessor;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;

/**
 * Simple implementation of a {@link HttpFutureCommandClient}, Apache Components HttpClient 4.x.
 * 
 * @author Sam Tunnicliffe
 * @author Adrian Cole
 */
public class ApacheHCHttpCommandExecutorService extends BaseHttpCommandExecutorService<HttpUriRequest> {
   private final HttpClient client;

   @Inject
   ApacheHCHttpCommandExecutorService(HttpUtils utils,
         @Named(Constants.PROPERTY_IO_WORKER_THREADS) ExecutorService ioWorkerExecutor,
         DelegatingRetryHandler retryHandler, IOExceptionRetryHandler ioRetryHandler,
         DelegatingErrorHandler errorHandler, HttpWire wire, HttpClient client) {
      super(utils, ioWorkerExecutor, retryHandler, ioRetryHandler, errorHandler, wire);
      this.client = client;
   }

   @Override
   protected HttpUriRequest convert(HttpRequest request) throws IOException {
      HttpUriRequest returnVal = ApacheHCUtils.convertToApacheRequest(request);
      if (request.getPayload() != null && request.getPayload().getContentMetadata().getContentMD5() != null)
         returnVal.addHeader("Content-MD5", CryptoStreams.md5Base64(request.getPayload()));
      return returnVal;
   }

   @Override
   protected HttpResponse invoke(HttpUriRequest nativeRequest) throws IOException {
      org.apache.http.HttpResponse apacheResponse = executeRequest(nativeRequest);

      Payload payload = null;
      if (apacheResponse.getEntity() != null)
         try {
            payload = Payloads.newInputStreamPayload(consumeOnClose(apacheResponse.getEntity().getContent()));
            if (apacheResponse.getEntity().getContentLength() >= 0)
               payload.getContentMetadata().setContentLength(apacheResponse.getEntity().getContentLength());
            if (apacheResponse.getEntity().getContentType() != null)
               payload.getContentMetadata().setContentType(apacheResponse.getEntity().getContentType().getValue());
         } catch (IOException e) {
            logger.warn(e, "couldn't receive payload for request: %s", nativeRequest.getRequestLine());
            throw e;
         }
      Multimap<String, String> headers = LinkedHashMultimap.create();
      for (Header header : apacheResponse.getAllHeaders()) {
         headers.put(header.getName(), header.getValue());
      }
      if (payload != null)
         payload.getContentMetadata().setPropertiesFromHttpHeaders(headers);
      return new HttpResponse(apacheResponse.getStatusLine().getStatusCode(), apacheResponse.getStatusLine()
            .getReasonPhrase(), payload, RestAnnotationProcessor.filterOutContentHeaders(headers));
   }

   private org.apache.http.HttpResponse executeRequest(HttpUriRequest nativeRequest) throws IOException,
         ClientProtocolException {
      URI endpoint = URI.create(nativeRequest.getRequestLine().getUri());
      HttpHost host = new HttpHost(endpoint.getHost(), endpoint.getPort(), endpoint.getScheme());
      org.apache.http.HttpResponse nativeResponse = client.execute(host, nativeRequest);
      return nativeResponse;
   }

   @Override
   protected void cleanup(HttpUriRequest nativeResponse) {
      // No cleanup necessary
   }
}
