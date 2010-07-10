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
package org.jclouds.atmosonline.saas.functions;

import static org.jclouds.http.HttpUtils.attemptToParseSizeAndRangeFromHeaders;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.atmosonline.saas.domain.AtmosObject;
import org.jclouds.blobstore.functions.ParseSystemAndUserMetadataFromHeaders;
import org.jclouds.http.HttpResponse;

import com.google.common.base.Function;

/**
 * Parses response headers and creates a new AtmosObject from them and the HTTP content.
 * 
 * @see ParseMetadataFromHeaders
 * @author Adrian Cole
 */
@Singleton
public class ParseObjectFromHeadersAndHttpContent implements Function<HttpResponse, AtmosObject> {

   private final ParseSystemMetadataFromHeaders systemMetadataParser;
   private final ParseUserMetadataFromHeaders userMetadataParser;
   private final AtmosObject.Factory objectProvider;

   @Inject
   public ParseObjectFromHeadersAndHttpContent(ParseSystemMetadataFromHeaders metadataParser,
            ParseUserMetadataFromHeaders userMetadataParser, AtmosObject.Factory objectProvider) {
      this.systemMetadataParser = metadataParser;
      this.userMetadataParser = userMetadataParser;
      this.objectProvider = objectProvider;
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
      AtmosObject object = objectProvider.create(systemMetadataParser.apply(from),
               userMetadataParser.apply(from));
      object.getAllHeaders().putAll(from.getHeaders());
      object.setPayload(from.getPayload());
      object.getContentMetadata().setContentLength(attemptToParseSizeAndRangeFromHeaders(from));
      return object;
   }
}