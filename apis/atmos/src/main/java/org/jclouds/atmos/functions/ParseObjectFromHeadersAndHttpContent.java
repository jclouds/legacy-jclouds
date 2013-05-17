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
package org.jclouds.atmos.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.http.HttpUtils.attemptToParseSizeAndRangeFromHeaders;

import java.net.URI;

import javax.inject.Inject;

import org.jclouds.atmos.domain.AtmosObject;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.InvocationContext;
import org.jclouds.rest.internal.GeneratedHttpRequest;

import com.google.common.base.Function;

/**
 * Parses response headers and creates a new AtmosObject from them and the HTTP content.
 * 
 * @see ParseMetadataFromHeaders
 * @author Adrian Cole
 */
public class ParseObjectFromHeadersAndHttpContent implements Function<HttpResponse, AtmosObject>,
         InvocationContext<ParseObjectFromHeadersAndHttpContent> {

   private final ParseSystemMetadataFromHeaders systemMetadataParser;
   private final ParseUserMetadataFromHeaders userMetadataParser;
   private final AtmosObject.Factory objectProvider;
   private URI uri;
   private String path;

   @Inject
   public ParseObjectFromHeadersAndHttpContent(ParseSystemMetadataFromHeaders systemMetadataParser,
            ParseUserMetadataFromHeaders userMetadataParser, AtmosObject.Factory objectProvider) {
      this.systemMetadataParser = checkNotNull(systemMetadataParser, "systemMetadataParser");
      this.userMetadataParser = checkNotNull(userMetadataParser, "userMetadataParser");
      this.objectProvider = checkNotNull(objectProvider, "objectProvider");
   }

   /**
    * First, calls {@link ParseSystemAndUserMetadataFromHeaders}.
    * 
    * Then, sets the object size based on the Content-Length header and adds the content to the
    * {@link AtmosObject} result.
    * 
    * @throws org.jclouds.http.HttpException
    */
   public AtmosObject apply(HttpResponse from) {
      checkNotNull(from, "http response");
      AtmosObject object = objectProvider.create(systemMetadataParser.apply(from), userMetadataParser.apply(from));
      object.getContentMetadata().setName(object.getSystemMetadata().getObjectName());
      object.getContentMetadata().setPath(path);
      object.getContentMetadata().setUri(uri);
      object.getAllHeaders().putAll(from.getHeaders());
      object.setPayload(from.getPayload());
      object.getContentMetadata().setContentLength(attemptToParseSizeAndRangeFromHeaders(from));
      return object;
   }

   @Override
   public ParseObjectFromHeadersAndHttpContent setContext(HttpRequest request) {
      this.uri = request.getEndpoint();
      return setPath(GeneratedHttpRequest.class.cast(request).getInvocation().getArgs().get(0).toString());
   }

   private ParseObjectFromHeadersAndHttpContent setPath(String path) {
      this.path = path;
      return this;
   }
}
