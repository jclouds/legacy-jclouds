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

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.JcloudsVersion;
import org.jclouds.concurrent.SingleThreaded;
import org.jclouds.http.HttpCommandExecutorService;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpUtils;
import org.jclouds.http.IOExceptionRetryHandler;
import org.jclouds.http.handlers.DelegatingErrorHandler;
import org.jclouds.http.handlers.DelegatingRetryHandler;
import org.jclouds.http.internal.BaseHttpCommandExecutorService;
import org.jclouds.http.internal.HttpWire;
import org.jclouds.io.ContentMetadataCodec;

import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.util.concurrent.ListeningExecutorService;

/**
 * Google App Engine version of {@link HttpCommandExecutorService}
 * 
 * @author Adrian Cole
 */
@SingleThreaded
@Singleton
public class GaeHttpCommandExecutorService extends BaseHttpCommandExecutorService<HTTPRequest> {
   //TODO: look up gae version
   public static final String USER_AGENT = String.format("jclouds/%s urlfetch/%s", JcloudsVersion.get(), "1.6.5");
   
   private final URLFetchService urlFetchService;
   private final ConvertToGaeRequest convertToGaeRequest;
   private final ConvertToJcloudsResponse convertToJcloudsResponse;

   @Inject
   public GaeHttpCommandExecutorService(URLFetchService urlFetchService, HttpUtils utils,
            ContentMetadataCodec contentMetadataCodec,
            @Named(Constants.PROPERTY_IO_WORKER_THREADS) ListeningExecutorService ioExecutor,
            IOExceptionRetryHandler ioRetryHandler, DelegatingRetryHandler retryHandler,
            DelegatingErrorHandler errorHandler, HttpWire wire, ConvertToGaeRequest convertToGaeRequest,
            ConvertToJcloudsResponse convertToJcloudsResponse) {
      super(utils, contentMetadataCodec, ioExecutor, retryHandler, ioRetryHandler, errorHandler, wire);
      this.urlFetchService = urlFetchService;
      this.convertToGaeRequest = convertToGaeRequest;
      this.convertToJcloudsResponse = convertToJcloudsResponse;
   }

   @VisibleForTesting
   protected HttpResponse convert(HTTPResponse gaeResponse) {
      return convertToJcloudsResponse.apply(gaeResponse);
   }

   @VisibleForTesting
   protected HTTPRequest convert(HttpRequest request) throws IOException {
      return convertToGaeRequest.apply(request);
   }

   /**
    * nothing to clean up.
    */
   @Override
   protected void cleanup(HTTPRequest nativeRequest) {
   }

   @Override
   protected HttpResponse invoke(HTTPRequest request) throws IOException {
      return convert(urlFetchService.fetch(request));
   }
}
