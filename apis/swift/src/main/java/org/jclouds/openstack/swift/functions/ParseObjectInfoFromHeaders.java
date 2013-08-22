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

import static com.google.common.base.Preconditions.checkArgument;
import static org.jclouds.http.HttpUtils.attemptToParseSizeAndRangeFromHeaders;

import javax.inject.Inject;
import javax.ws.rs.core.HttpHeaders;

import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.functions.ParseSystemAndUserMetadataFromHeaders;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.swift.blobstore.functions.ResourceToObjectInfo;
import org.jclouds.openstack.swift.domain.MutableObjectInfoWithMetadata;
import org.jclouds.openstack.swift.utils.ETagUtils;
import org.jclouds.rest.InvocationContext;
import org.jclouds.rest.internal.GeneratedHttpRequest;

import com.google.common.base.Function;

/**
 * This parses @{link {@link MutableObjectInfoWithMetadata} from HTTP headers.
 * 
 * @author Adrian Cole
 */
public class ParseObjectInfoFromHeaders implements Function<HttpResponse, MutableObjectInfoWithMetadata>,
         InvocationContext<ParseObjectInfoFromHeaders> {
   private final ParseSystemAndUserMetadataFromHeaders blobMetadataParser;
   private final ResourceToObjectInfo blobToObjectInfo;
   private String container;

   @Inject
   public ParseObjectInfoFromHeaders(ParseSystemAndUserMetadataFromHeaders blobMetadataParser,
            ResourceToObjectInfo blobToObjectInfo) {
      this.blobMetadataParser = blobMetadataParser;
      this.blobToObjectInfo = blobToObjectInfo;
   }

   /**
    * parses the http response headers to create a new {@link MutableObjectInfoWithMetadata} object.
    */
   public MutableObjectInfoWithMetadata apply(HttpResponse from) {
      BlobMetadata base = blobMetadataParser.apply(from);
      MutableObjectInfoWithMetadata to = blobToObjectInfo.apply(base);
      to.setBytes(attemptToParseSizeAndRangeFromHeaders(from));
      to.setContainer(container);
      to.setUri(base.getUri());
      String eTagHeader = from.getFirstHeaderOrNull(HttpHeaders.ETAG);
      if (eTagHeader != null) {
         to.setHash(ETagUtils.convertHexETagToByteArray(eTagHeader));
      }
      to.setObjectManifest(from.getFirstHeaderOrNull("X-Object-Manifest"));

      return to;
   }

   @Override
   public ParseObjectInfoFromHeaders setContext(HttpRequest request) {
      blobMetadataParser.setContext(request);
      checkArgument(request instanceof GeneratedHttpRequest, "note this handler requires a GeneratedHttpRequest");
      return setContainer(GeneratedHttpRequest.class.cast(request).getInvocation().getArgs().get(0).toString());
   }

   private ParseObjectInfoFromHeaders setContainer(String container) {
      this.container = container;
      return this;
   }

}
