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
package org.jclouds.hpcloud.objectstorage.functions;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Map.Entry;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.swift.domain.ContainerMetadata;
import org.jclouds.openstack.swift.reference.SwiftHeaders;
import org.jclouds.rest.InvocationContext;
import org.jclouds.rest.internal.GeneratedHttpRequest;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;

/**
 * This parses @{link {@link org.jclouds.hpcloud.objectstorage.domain.ContainerMetadata} from
 * HTTP headers.
 * 
 * @author Jeremy Daggett
 */
public class ParseContainerMetadataFromHeaders implements Function<HttpResponse, ContainerMetadata>,
      InvocationContext<ParseContainerMetadataFromHeaders> {
   private GeneratedHttpRequest<?> request;

   public ContainerMetadata apply(HttpResponse from) {
      ContainerMetadata to = new ContainerMetadata();
      
      to.setName(request.getArgs().get(0).toString());
      
      to.setReadACL(from.getFirstHeaderOrNull(SwiftHeaders.CONTAINER_READ));
 
      to.setBytes(new Long(from.getFirstHeaderOrNull(SwiftHeaders.CONTAINER_BYTES_USED)));
      to.setCount(new Long(from.getFirstHeaderOrNull(SwiftHeaders.CONTAINER_OBJECT_COUNT)));
      
      addUserMetadataTo(from, to);
      
      return to;
   }

   
   @VisibleForTesting
   void addUserMetadataTo(HttpResponse from, ContainerMetadata metadata) {
      for (Entry<String, String> header : from.getHeaders().entries()) {
         if (header.getKey() != null && header.getKey().startsWith(SwiftHeaders.CONTAINER_METADATA_PREFIX))
            metadata.getMetadata().put((header.getKey().substring(SwiftHeaders.CONTAINER_METADATA_PREFIX.length())).toLowerCase(),
                  header.getValue());
      }
   }
   
   @Override
   public ParseContainerMetadataFromHeaders setContext(HttpRequest request) {
      checkArgument(request instanceof GeneratedHttpRequest<?>, "note this handler requires a GeneratedHttpRequest");
      this.request = (GeneratedHttpRequest<?>) request;
      return this;
   }

}
