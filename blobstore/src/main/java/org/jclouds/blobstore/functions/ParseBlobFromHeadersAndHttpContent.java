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
package org.jclouds.blobstore.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;

import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.MutableBlobMetadata;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.InvocationContext;

import com.google.common.base.Function;

/**
 * Parses response headers and creates a new Blob from them and the HTTP content.
 * 
 * @see ParseSystemAndUserMetadataFromHeaders
 * @author Adrian Cole
 */
public class ParseBlobFromHeadersAndHttpContent implements Function<HttpResponse, Blob>,
      InvocationContext<ParseBlobFromHeadersAndHttpContent> {
   private final ParseSystemAndUserMetadataFromHeaders metadataParser;
   private final Blob.Factory blobFactory;

   @Inject
   public ParseBlobFromHeadersAndHttpContent(ParseSystemAndUserMetadataFromHeaders metadataParser,
         Blob.Factory blobFactory) {
      this.metadataParser = checkNotNull(metadataParser, "metadataParser");
      this.blobFactory = checkNotNull(blobFactory, "blobFactory");
   }

   public Blob apply(HttpResponse from) {
      checkNotNull(from, "request");
      MutableBlobMetadata metadata = metadataParser.apply(from);
      Blob blob = blobFactory.create(metadata);
      blob.getAllHeaders().putAll(from.getHeaders());
      blob.setPayload(from.getPayload());
      return blob;
   }

   @Override
   public ParseBlobFromHeadersAndHttpContent setContext(HttpRequest request) {
      metadataParser.setContext(request);
      return this;
   }

}
