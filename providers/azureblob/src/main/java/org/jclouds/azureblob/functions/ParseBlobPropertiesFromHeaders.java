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
package org.jclouds.azureblob.functions;

import static com.google.common.base.Preconditions.checkArgument;

import javax.inject.Inject;

import org.jclouds.azureblob.blobstore.functions.BlobMetadataToBlobProperties;
import org.jclouds.azureblob.domain.MutableBlobProperties;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.functions.ParseSystemAndUserMetadataFromHeaders;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.InvocationContext;
import org.jclouds.rest.internal.GeneratedHttpRequest;

import com.google.common.base.Function;

/**
 * This parses @{link {@link org.jclouds.azureblob.domain.BlobProperties} from HTTP
 * headers.
 * 
 * 
 * @author Adrian Cole
 */
public class ParseBlobPropertiesFromHeaders implements Function<HttpResponse, MutableBlobProperties>,
      InvocationContext<ParseBlobPropertiesFromHeaders> {
   private final ParseSystemAndUserMetadataFromHeaders blobMetadataParser;
   private final BlobMetadataToBlobProperties blobToBlobProperties;
   private String container;

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
      to.setContainer(container);
      return to;
   }


   @Override
   public ParseBlobPropertiesFromHeaders setContext(HttpRequest request) {
      checkArgument(request instanceof GeneratedHttpRequest, "note this handler requires a GeneratedHttpRequest");
      blobMetadataParser.setContext(request);
      return setContainer(GeneratedHttpRequest.class.cast(request).getInvocation().getArgs().get(0).toString());
   }

   private ParseBlobPropertiesFromHeaders setContainer(String container) {
      this.container = container;
      return this;
   }
}
