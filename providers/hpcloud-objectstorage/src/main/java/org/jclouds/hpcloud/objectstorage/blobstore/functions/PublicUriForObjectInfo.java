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
package org.jclouds.hpcloud.objectstorage.blobstore.functions;

import java.net.URI;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.ws.rs.core.UriBuilder;

import org.jclouds.openstack.swift.domain.ObjectInfo;

import com.google.common.base.Function;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * @author Adrian Cole
 */
@Singleton
public class PublicUriForObjectInfo implements Function<ObjectInfo, URI> {
   private final LoadingCache<String, URI> cdnContainer;
   private final Provider<UriBuilder> uriBuilders;

   @Inject
   public PublicUriForObjectInfo(LoadingCache<String, URI> cdnContainer, Provider<UriBuilder> uriBuilders) {
      this.cdnContainer = cdnContainer;
      this.uriBuilders = uriBuilders;
   }

   private static final URI NEGATIVE_ENTRY = URI.create("http://127.0.0.1");

   public URI apply(ObjectInfo from) {
      if (from == null)
         return null;
      try {
         URI uri = cdnContainer.getUnchecked(from.getContainer());
         if (uri == NEGATIVE_ENTRY) {  // intentionally use reference equality
            // TODO: GetCDNMetadata.load returns null on failure cases.  We use
            // a negative entry to avoid repeatedly issuing failed CDN queries.
            // The LoadingCache removes this value after its normal expiry.
            return null;
         }
         return uriBuilders.get().uri(uri).path(from.getName()).replaceQuery("")
                  .build();
      } catch (CacheLoader.InvalidCacheLoadException e) {
         // nulls not permitted from cache loader
         cdnContainer.put(from.getContainer(), NEGATIVE_ENTRY);
         return null;
      } catch (NullPointerException e) {
         // nulls not permitted from cache loader
         // TODO this shouldn't occur when the above exception is reliably presented
         return null;
      }
   }
}
