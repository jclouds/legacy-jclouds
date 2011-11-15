/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
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
 */
package org.jclouds.hpcloud.object.storage.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

import java.util.List;
import java.net.URI;

import org.jclouds.hpcloud.object.storage.domain.ContainerCDNMetadata;
import org.jclouds.hpcloud.object.storage.reference.HPCloudObjectStorageHeaders;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.swift.domain.AccountMetadata;
import org.jclouds.rest.InvocationContext;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

/**
 * This parses {@link AccountMetadata} from HTTP headers.
 * 
 * @author James Murty
 */
public class ParseContainerCDNMetadataFromHeaders implements
         Function<HttpResponse, ContainerCDNMetadata>, InvocationContext<ParseContainerCDNMetadataFromHeaders> {

   private HttpRequest request;

   /**
    * parses the http response headers to create a new {@link ContainerCDNMetadata} object.
    */
   public ContainerCDNMetadata apply(final HttpResponse from) {
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

         return new ContainerCDNMetadata(parts.get(parts.size()-1), Boolean
                  .parseBoolean(cdnEnabled), Long.parseLong(cdnTTL), URI.create(cdnUri));
      }
   }

   @Override
   public ParseContainerCDNMetadataFromHeaders setContext(HttpRequest request) {
      this.request = request;
      return this;
   }
}
