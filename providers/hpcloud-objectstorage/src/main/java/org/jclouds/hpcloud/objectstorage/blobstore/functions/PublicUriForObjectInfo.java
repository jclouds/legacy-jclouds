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
package org.jclouds.hpcloud.objectstorage.blobstore.functions;

import static org.jclouds.http.Uris.uriBuilder;

import java.net.URI;

import javax.inject.Inject;
import javax.inject.Singleton;

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

   @Inject
   public PublicUriForObjectInfo(LoadingCache<String, URI> cdnContainer) {
      this.cdnContainer = cdnContainer;
   }

   private static final URI NEGATIVE_ENTRY = URI.create("http://127.0.0.1");

   public URI apply(ObjectInfo from) {
      if (from == null)
         return null;
      String containerName = from.getContainer();
      if (containerName == null)
         return null;
      try {
         URI uri = cdnContainer.getUnchecked(containerName);
         if (uri == NEGATIVE_ENTRY) {  // intentionally use reference equality
            // TODO: GetCDNMetadata.load returns null on failure cases.  We use
            // a negative entry to avoid repeatedly issuing failed CDN queries.
            // The LoadingCache removes this value after its normal expiry.
            return null;
         }
         return uriBuilder(uri).clearQuery().appendPath(from.getName()).build();
      } catch (CacheLoader.InvalidCacheLoadException e) {
         // nulls not permitted from cache loader
         cdnContainer.put(containerName, NEGATIVE_ENTRY);
         return null;
      } catch (NullPointerException e) {
         // nulls not permitted from cache loader
         // TODO this shouldn't occur when the above exception is reliably presented
         return null;
      }
   }
}
