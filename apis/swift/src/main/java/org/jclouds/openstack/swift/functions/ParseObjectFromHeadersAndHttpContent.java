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
package org.jclouds.openstack.swift.functions;

import javax.inject.Inject;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.swift.domain.MutableObjectInfoWithMetadata;
import org.jclouds.openstack.swift.domain.SwiftObject;
import org.jclouds.rest.InvocationContext;

import com.google.common.base.Function;

/**
 * Parses response headers and creates a new SwiftObject from them and the HTTP content.
 * 
 * @see ParseMetadataFromHeaders
 * @author Adrian Cole
 */
public class ParseObjectFromHeadersAndHttpContent implements Function<HttpResponse, SwiftObject>,
         InvocationContext<ParseObjectFromHeadersAndHttpContent> {

   private final ParseObjectInfoFromHeaders infoParser;
   private final SwiftObject.Factory objectProvider;

   @Inject
   public ParseObjectFromHeadersAndHttpContent(ParseObjectInfoFromHeaders infoParser,
            SwiftObject.Factory objectProvider) {
      this.infoParser = infoParser;
      this.objectProvider = objectProvider;
   }

   public SwiftObject apply(HttpResponse from) {
      MutableObjectInfoWithMetadata metadata = infoParser.apply(from);
      if (metadata.getHash() != null)
         from.getPayload().getContentMetadata().setContentMD5(metadata.getHash());
      SwiftObject object = objectProvider.create(metadata);
      object.getAllHeaders().putAll(from.getHeaders());
      object.setPayload(from.getPayload());
      return object;
   }

   @Override
   public ParseObjectFromHeadersAndHttpContent setContext(HttpRequest request) {
      infoParser.setContext(request);
      return this;
   }
}
