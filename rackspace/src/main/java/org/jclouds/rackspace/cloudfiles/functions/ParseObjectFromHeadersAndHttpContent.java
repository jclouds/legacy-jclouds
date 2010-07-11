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
package org.jclouds.rackspace.cloudfiles.functions;

import javax.inject.Inject;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.rackspace.cloudfiles.domain.CFObject;
import org.jclouds.rackspace.cloudfiles.domain.MutableObjectInfoWithMetadata;
import org.jclouds.rest.InvocationContext;

import com.google.common.base.Function;

/**
 * Parses response headers and creates a new CFObject from them and the HTTP content.
 * 
 * @see ParseMetadataFromHeaders
 * @author Adrian Cole
 */
public class ParseObjectFromHeadersAndHttpContent implements Function<HttpResponse, CFObject>,
         InvocationContext {

   private final ParseObjectInfoFromHeaders infoParser;
   private final CFObject.Factory objectProvider;

   @Inject
   public ParseObjectFromHeadersAndHttpContent(ParseObjectInfoFromHeaders infoParser,
            CFObject.Factory objectProvider) {
      this.infoParser = infoParser;
      this.objectProvider = objectProvider;
   }

   public CFObject apply(HttpResponse from) {
      MutableObjectInfoWithMetadata metadata = infoParser.apply(from);
      if (metadata.getHash() != null)
         from.getPayload().setContentMD5(metadata.getHash());
      CFObject object = objectProvider.create(metadata);
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