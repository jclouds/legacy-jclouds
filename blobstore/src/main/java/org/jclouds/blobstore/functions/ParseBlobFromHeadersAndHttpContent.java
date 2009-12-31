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
package org.jclouds.blobstore.functions;

import javax.inject.Inject;
import javax.ws.rs.core.HttpHeaders;

import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.MutableBlobMetadata;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.InvocationContext;
import org.jclouds.rest.internal.GeneratedHttpRequest;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;

/**
 * Parses response headers and creates a new Blob from them and the HTTP content.
 * 
 * @see ParseSystemAndUserMetadataFromHeaders
 * @author Adrian Cole
 */
public class ParseBlobFromHeadersAndHttpContent implements Function<HttpResponse, Blob>,
         InvocationContext {
   private final ParseSystemAndUserMetadataFromHeaders metadataParser;
   private final Blob.Factory blobFactory;

   @Inject
   public ParseBlobFromHeadersAndHttpContent(ParseSystemAndUserMetadataFromHeaders metadataParser,
            Blob.Factory blobFactory) {
      this.metadataParser = metadataParser;
      this.blobFactory = blobFactory;
   }

   /**
    * First, calls {@link ParseSystemAndUserMetadataFromHeaders}.
    * 
    * Then, sets the object size based on the Content-Length header and adds the content to the
    * {@link Blob} result.
    * 
    * @throws org.jclouds.http.HttpException
    */
   public Blob apply(HttpResponse from) {
      MutableBlobMetadata metadata = metadataParser.apply(from);
      Blob object = blobFactory.create(metadata);
      addAllHeadersTo(from, object);
      object.setPayload(from.getContent());
      assert object.getMetadata() == metadata;
      attemptToParseSizeAndRangeFromHeaders(from, object);
      return object;
   }

   @VisibleForTesting
   void attemptToParseSizeAndRangeFromHeaders(HttpResponse from, Blob object) throws HttpException {
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
   void addAllHeadersTo(HttpResponse from, Blob object) {
      object.getAllHeaders().putAll(from.getHeaders());
   }

   public void setContext(GeneratedHttpRequest<?> request) {
      metadataParser.setContext(request);
   }

}