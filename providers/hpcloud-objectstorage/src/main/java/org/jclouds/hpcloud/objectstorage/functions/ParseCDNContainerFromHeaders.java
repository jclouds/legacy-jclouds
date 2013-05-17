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
package org.jclouds.hpcloud.objectstorage.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

import java.net.URI;
import java.util.List;

import org.jclouds.hpcloud.objectstorage.domain.CDNContainer;
import org.jclouds.hpcloud.objectstorage.reference.HPCloudObjectStorageHeaders;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.InvocationContext;

import com.google.common.base.Function;
import com.google.common.base.Splitter;

/**
 * This parses {@link AccountMetadata} from HTTP headers.
 * 
 * @author James Murty
 */
public class ParseCDNContainerFromHeaders implements
         Function<HttpResponse, CDNContainer>, InvocationContext<ParseCDNContainerFromHeaders> {

   private HttpRequest request;

   /**
    * parses the http response headers to create a new {@link CDNContainer} object.
    */
   public CDNContainer apply(final HttpResponse from) {
      String cdnUri = checkNotNull(from.getFirstHeaderOrNull(HPCloudObjectStorageHeaders.CDN_URI),
               HPCloudObjectStorageHeaders.CDN_URI);
      String cdnTTL = checkNotNull(from.getFirstHeaderOrNull(HPCloudObjectStorageHeaders.CDN_TTL),
               HPCloudObjectStorageHeaders.CDN_TTL);
      String cdnEnabled = checkNotNull(from.getFirstHeaderOrNull(HPCloudObjectStorageHeaders.CDN_ENABLED),
               HPCloudObjectStorageHeaders.CDN_ENABLED);
      if (cdnUri == null) {
         // CDN is not enabled for this container.
         return null;
      } else {
         // just need the name from the path
         List<String> parts = newArrayList(Splitter.on('/').split(request.getEndpoint().getPath()));

         return CDNContainer.builder().name(parts.get(parts.size() - 1))
               .CDNEnabled(Boolean.parseBoolean(cdnEnabled)).ttl(Long.parseLong(cdnTTL)).CDNUri(URI.create(cdnUri))
               .build();
      }
   }

   @Override
   public ParseCDNContainerFromHeaders setContext(HttpRequest request) {
      this.request = request;
      return this;
   }
}
