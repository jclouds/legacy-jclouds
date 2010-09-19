/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

import org.jclouds.azure.storage.blob.blobstore.functions.BlobMetadataToBlobProperties;
import org.jclouds.azure.storage.blob.domain.MutableBlobProperties;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.functions.ParseSystemAndUserMetadataFromHeaders;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.InvocationContext;

import com.google.common.base.Function;

/**
 * This parses @{link {@link org.jclouds.azure.storage.blob.domain.BlobProperties} from HTTP
 * headers.
 * 
 * 
 * @author Adrian Cole
 */
public class ParseBlobPropertiesFromHeaders implements Function<HttpResponse, MutableBlobProperties>, InvocationContext {
   private final ParseSystemAndUserMetadataFromHeaders blobMetadataParser;
   private final BlobMetadataToBlobProperties blobToBlobProperties;

   @Inject
   public ParseBlobPropertiesFromHeaders(ParseSystemAndUserMetadataFromHeaders blobMetadataParser,
            BlobMetadataToBlobProperties blobToBlobProperties) {
      this.blobMetadataParser = blobMetadataParser;
      this.blobToBlobProperties = blobToBlobProperties;
   }

   /**
    * parses the http response headers to create a new {@link MutableBlobProperties} object.
    */
   public MutableBlobProperties apply(HttpResponse from) {
      BlobMetadata base = blobMetadataParser.apply(from);
      MutableBlobProperties to = blobToBlobProperties.apply(base);
      return to;
   }

   @Override
   public ParseBlobPropertiesFromHeaders setContext(HttpRequest request) {
      blobMetadataParser.setContext(request);
      return this;
   }

}