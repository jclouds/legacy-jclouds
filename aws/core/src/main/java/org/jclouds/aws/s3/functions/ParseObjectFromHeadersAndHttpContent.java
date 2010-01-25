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
package org.jclouds.aws.s3.functions;

import javax.inject.Inject;
import javax.ws.rs.core.HttpHeaders;

import org.jclouds.aws.s3.domain.S3Object;
import org.jclouds.blobstore.functions.ParseSystemAndUserMetadataFromHeaders;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.InvocationContext;
import org.jclouds.rest.internal.GeneratedHttpRequest;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;

/**
 * Parses response headers and creates a new S3Object from them and the HTTP content.
 * 
 * @see ParseMetadataFromHeaders
 * @author Adrian Cole
 */
public class ParseObjectFromHeadersAndHttpContent implements Function<HttpResponse, S3Object>,
         InvocationContext {

   private final ParseObjectMetadataFromHeaders metadataParser;
   private final S3Object.Factory objectProvider;

   @Inject
   public ParseObjectFromHeadersAndHttpContent(ParseObjectMetadataFromHeaders metadataParser,
            S3Object.Factory objectProvider) {
      this.metadataParser = metadataParser;
      this.objectProvider = objectProvider;
   }

   /**
    * First, calls {@link ParseSystemAndUserMetadataFromHeaders}.
    * 
    * Then, sets the object size based on the Content-Length header and adds the content to the
    * {@link S3Object} result.
    * 
    * @throws org.jclouds.http.HttpException
    */
   public S3Object apply(HttpResponse from) {
      S3Object object = objectProvider.create(metadataParser.apply(from));
      addAllHeadersTo(from, object);
      if (from.getContent() != null)
         object.setPayload(from.getContent());
      attemptToParseSizeAndRangeFromHeaders(from, object);
      return object;
   }

   @VisibleForTesting
   void attemptToParseSizeAndRangeFromHeaders(HttpResponse from, S3Object object)
            throws HttpException {
      String contentLength = from.getFirstHeaderOrNull(HttpHeaders.CONTENT_LENGTH);
      String contentRange = from.getFirstHeaderOrNull("Content-Range");

      if (contentLength != null) {
         object.setContentLength(Long.parseLong(contentLength));
      }

      if (contentRange == null && contentLength != null) {
         object.getMetadata().setSize(object.getContentLength());
      } else if (contentRange != null) {
         object.getMetadata().setSize(
                  Long.parseLong(contentRange.substring(contentRange.lastIndexOf('/') + 1)));
      }
   }

   @VisibleForTesting
   void addAllHeadersTo(HttpResponse from, S3Object object) {
      object.getAllHeaders().putAll(from.getHeaders());
   }

   public void setContext(GeneratedHttpRequest<?> request) {
      metadataParser.setContext(request);
   }

}