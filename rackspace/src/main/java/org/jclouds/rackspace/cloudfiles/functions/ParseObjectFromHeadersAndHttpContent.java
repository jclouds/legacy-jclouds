/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.rackspace.cloudfiles.functions;

import javax.inject.Inject;
import javax.ws.rs.core.HttpHeaders;

import org.jclouds.blobstore.functions.ParseSystemAndUserMetadataFromHeaders;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpResponse;
import org.jclouds.rackspace.cloudfiles.domain.CFObject;
import org.jclouds.rest.InvocationContext;
import org.jclouds.rest.internal.GeneratedHttpRequest;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;

/**
 * Parses response headers and creates a new CFObject from them and the HTTP content.
 * 
 * @see ParseMetadataFromHeaders
 * @author Adrian Cole
 */
public class ParseObjectFromHeadersAndHttpContent implements Function<HttpResponse, CFObject>,
         InvocationContext {

   private final ParseObjectInfoFromHeaders infoParser;
   private final CFObject.Factory objectProvider;

   @Inject
   public ParseObjectFromHeadersAndHttpContent(ParseObjectInfoFromHeaders infoParser,
            CFObject.Factory objectProvider) {
      this.infoParser = infoParser;
      this.objectProvider = objectProvider;
   }

   /**
    * First, calls {@link ParseSystemAndUserMetadataFromHeaders}.
    * 
    * Then, sets the object size based on the Content-Length header and adds the content to the
    * {@link CFObject} result.
    * 
    * @throws org.jclouds.http.HttpException
    */
   public CFObject apply(HttpResponse from) {
      CFObject object = objectProvider.create(infoParser.apply(from));
      addAllHeadersTo(from, object);
      object.setPayload(from.getContent());
      attemptToParseSizeAndRangeFromHeaders(from, object);
      return object;
   }

   @VisibleForTesting
   void attemptToParseSizeAndRangeFromHeaders(HttpResponse from, CFObject object)
            throws HttpException {
      String contentLength = from.getFirstHeaderOrNull(HttpHeaders.CONTENT_LENGTH);
      String contentRange = from.getFirstHeaderOrNull("Content-Range");

      if (contentLength != null) {
         object.setContentLength(Long.parseLong(contentLength));
      }

      if (contentRange == null && contentLength != null) {
         object.getInfo().setBytes(object.getContentLength());
      } else if (contentRange != null) {
         object.getInfo().setBytes(
                  Long.parseLong(contentRange.substring(contentRange.lastIndexOf('/') + 1)));
      }
   }

   @VisibleForTesting
   void addAllHeadersTo(HttpResponse from, CFObject object) {
      object.getAllHeaders().putAll(from.getHeaders());
   }

   public void setContext(GeneratedHttpRequest<?> request) {
      infoParser.setContext(request);
   }

}