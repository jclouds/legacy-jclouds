/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 */
package org.jclouds.rackspace.cloudfiles.functions;

import javax.inject.Inject;

import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.functions.ParseSystemAndUserMetadataFromHeaders;
import org.jclouds.http.HttpResponse;
import org.jclouds.rackspace.cloudfiles.blobstore.functions.ResourceToObjectInfo;
import org.jclouds.rackspace.cloudfiles.domain.MutableObjectInfoWithMetadata;
import org.jclouds.rest.InvocationContext;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.util.EncryptionService;

import com.google.common.base.Function;

/**
 * This parses @{link {@link MutableObjectInfoWithMetadata} from HTTP headers.
 * 
 * @author Adrian Cole
 */
public class ParseObjectInfoFromHeaders implements
         Function<HttpResponse, MutableObjectInfoWithMetadata>, InvocationContext {
   private final ParseSystemAndUserMetadataFromHeaders blobMetadataParser;
   private final ResourceToObjectInfo blobToObjectInfo;
   private final EncryptionService encryptionService;

   @Inject
   public ParseObjectInfoFromHeaders(ParseSystemAndUserMetadataFromHeaders blobMetadataParser,
            ResourceToObjectInfo blobToObjectInfo, EncryptionService encryptionService) {
      this.blobMetadataParser = blobMetadataParser;
      this.blobToObjectInfo = blobToObjectInfo;
      this.encryptionService = encryptionService;
   }

   /**
    * parses the http response headers to create a new {@link MutableObjectInfoWithMetadata} object.
    */
   public MutableObjectInfoWithMetadata apply(HttpResponse from) {
      BlobMetadata base = blobMetadataParser.apply(from);
      MutableObjectInfoWithMetadata to = blobToObjectInfo.apply(base);
      String eTagHeader = from.getFirstHeaderOrNull("Etag");
      if (eTagHeader != null) {
         to.setHash(encryptionService.fromHexString(eTagHeader.replaceAll("\"", "")));
      }
      return to;
   }

   public void setContext(GeneratedHttpRequest<?> request) {
      blobMetadataParser.setContext(request);
   }

}