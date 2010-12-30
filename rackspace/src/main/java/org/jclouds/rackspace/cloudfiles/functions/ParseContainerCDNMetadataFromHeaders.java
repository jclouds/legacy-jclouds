/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.rackspace.cloudfiles.domain.AccountMetadata;
import org.jclouds.rackspace.cloudfiles.domain.ContainerCDNMetadata;
import org.jclouds.rackspace.cloudfiles.reference.CloudFilesHeaders;
import org.jclouds.rest.InvocationContext;

import com.google.common.base.Function;

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
      String cdnUri = checkNotNull(from.getFirstHeaderOrNull(CloudFilesHeaders.CDN_URI),
               CloudFilesHeaders.CDN_URI);
      String cdnTTL = checkNotNull(from.getFirstHeaderOrNull(CloudFilesHeaders.CDN_TTL),
               CloudFilesHeaders.CDN_TTL);
      String cdnEnabled = checkNotNull(from.getFirstHeaderOrNull(CloudFilesHeaders.CDN_ENABLED),
               CloudFilesHeaders.CDN_ENABLED);
      if (cdnUri == null) {
         // CDN is not, and has never, been enabled for this container.
         return null;
      } else {
         return new ContainerCDNMetadata(request.getEndpoint().getPath(), Boolean
                  .parseBoolean(cdnEnabled), Long.parseLong(cdnTTL), URI.create(cdnUri));
      }
   }

   @Override
   public ParseContainerCDNMetadataFromHeaders setContext(HttpRequest request) {
      this.request = request;
      return this;
   }
}