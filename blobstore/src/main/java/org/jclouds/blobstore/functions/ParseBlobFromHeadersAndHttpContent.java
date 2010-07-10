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

import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.MutableBlobMetadata;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.InvocationContext;
import org.jclouds.rest.internal.GeneratedHttpRequest;

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

   public Blob apply(HttpResponse from) {
      MutableBlobMetadata metadata = metadataParser.apply(from);
      Blob object = blobFactory.create(metadata);
      object.getAllHeaders().putAll(from.getHeaders());
      object.setPayload(from.getPayload());
      return object;
   }

   public void setContext(GeneratedHttpRequest<?> request) {
      metadataParser.setContext(request);
   }

}