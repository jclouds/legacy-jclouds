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
package org.jclouds.s3.functions;

import javax.inject.Inject;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.InvocationContext;
import org.jclouds.s3.domain.MutableObjectMetadata;
import org.jclouds.s3.domain.S3Object;

import com.google.common.base.Function;

/**
 * Parses response headers and creates a new S3Object from them and the HTTP content.
 * 
 * @see ParseMetadataFromHeaders
 * @author Adrian Cole
 */
public class ParseObjectFromHeadersAndHttpContent implements Function<HttpResponse, S3Object>,
      InvocationContext<ParseObjectFromHeadersAndHttpContent> {

   private final ParseObjectMetadataFromHeaders metadataParser;
   private final S3Object.Factory objectProvider;

   @Inject
   public ParseObjectFromHeadersAndHttpContent(ParseObjectMetadataFromHeaders metadataParser,
         S3Object.Factory objectProvider) {
      this.metadataParser = metadataParser;
      this.objectProvider = objectProvider;
   }

   public S3Object apply(HttpResponse from) {
      MutableObjectMetadata metadata = metadataParser.apply(from);
      S3Object object = objectProvider.create(metadata);
      object.getAllHeaders().putAll(from.getHeaders());
      object.setPayload(from.getPayload());
      return object;
   }

   @Override
   public ParseObjectFromHeadersAndHttpContent setContext(HttpRequest request) {
      metadataParser.setContext(request);
      return this;
   }

}
