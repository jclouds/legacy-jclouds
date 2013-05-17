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

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.azureblob.domain.AzureBlob;
import org.jclouds.azureblob.domain.MutableBlobProperties;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.InvocationContext;

import com.google.common.base.Function;

/**
 * Parses response headers and creates a new AzureBlob from them and the HTTP content.
 * 
 * @see ParseMetadataFromHeaders
 * @author Adrian Cole
 */
@Singleton
public class ParseBlobFromHeadersAndHttpContent implements Function<HttpResponse, AzureBlob>,
      InvocationContext<ParseBlobFromHeadersAndHttpContent> {

   private final ParseBlobPropertiesFromHeaders metadataParser;
   private final AzureBlob.Factory blobFactory;

   @Inject
   public ParseBlobFromHeadersAndHttpContent(ParseBlobPropertiesFromHeaders metadataParser,
         AzureBlob.Factory blobFactory) {
      this.metadataParser = metadataParser;
      this.blobFactory = blobFactory;
   }

   public AzureBlob apply(HttpResponse from) {
      MutableBlobProperties metadata = metadataParser.apply(from);
      AzureBlob blob = blobFactory.create(metadata);
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
