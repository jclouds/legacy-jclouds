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
package org.jclouds.azure.storage.blob.functions;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.core.HttpHeaders;

import org.jclouds.azure.storage.blob.domain.AzureBlob;
import org.jclouds.blobstore.functions.ParseSystemAndUserMetadataFromHeaders;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.InvocationContext;
import org.jclouds.rest.internal.GeneratedHttpRequest;

import com.google.common.base.Function;

/**
 * Parses response headers and creates a new AzureBlob from them and the HTTP content.
 * 
 * @see ParseMetadataFromHeaders
 * @author Adrian Cole
 */
@Singleton
public class ParseBlobFromHeadersAndHttpContent implements Function<HttpResponse, AzureBlob>,
         InvocationContext {

   private final ParseBlobPropertiesFromHeaders metadataParser;
   private final AzureBlob.Factory objectProvider;

   @Inject
   public ParseBlobFromHeadersAndHttpContent(ParseBlobPropertiesFromHeaders metadataParser,
            AzureBlob.Factory objectProvider) {
      this.metadataParser = metadataParser;
      this.objectProvider = objectProvider;
   }

   /**
    * First, calls {@link ParseSystemAndUserMetadataFromHeaders}.
    * 
    * Then, sets the object size based on the Content-Length header and adds the content to the
    * {@link AzureBlob} result.
    * 
    * @throws org.jclouds.http.HttpException
    */
   public AzureBlob apply(HttpResponse from) {
      AzureBlob object = objectProvider.create(metadataParser.apply(from));
      object.getAllHeaders().putAll(from.getHeaders());

      if (from.getContent() != null) {
         object.setPayload(from.getContent());
      } else if (new Long(0).equals(object.getProperties().getContentLength())) {
         object.setPayload(new byte[0]);
      } else {
         assert false : "no content in " + from;
      }

      String contentLength = from.getFirstHeaderOrNull(HttpHeaders.CONTENT_LENGTH);
      String contentRange = from.getFirstHeaderOrNull("Content-Range");

      if (contentLength != null) {
         object.getPayload().setContentLength(Long.parseLong(contentLength));
      }

      if (contentRange == null && contentLength != null) {
         object.getProperties().setContentLength(object.getPayload().getContentLength());
      } else if (contentRange != null) {
         object.getProperties().setContentLength(
                  Long.parseLong(contentRange.substring(contentRange.lastIndexOf('/') + 1)));
      }
      return object;
   }

   public void setContext(GeneratedHttpRequest<?> request) {
      metadataParser.setContext(request);
   }

}