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
package org.jclouds.blobstore.strategy.internal;

import java.util.List;

import javax.inject.Singleton;

import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.internal.BlobRuntimeException;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.blobstore.options.ListContainerOptions.ImmutableListContainerOptions;
import org.jclouds.blobstore.strategy.ListContainerStrategy;

import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.inject.Inject;

/**
 * Retrieves all metadata in the blobstore by the most efficient means possible.
 * 
 * @author Adrian Cole
 */
@Singleton
public class ConcatenateContainerLists implements ListContainerStrategy {

   protected final BlobStore connection;

   @Inject
   public ConcatenateContainerLists(BlobStore connection) {
      this.connection = connection;
   }

   @Override
   public Iterable<? extends StorageMetadata> execute(String container, ListContainerOptions options) {
      try {
         boolean truncated = true;
         List<PageSet<? extends StorageMetadata>> listings = Lists.newArrayList();
         while (truncated) {
            PageSet<? extends StorageMetadata> listing = connection.list(container, options);
            truncated = listing.getNextMarker() != null;
            if (truncated) {
               options = options instanceof ImmutableListContainerOptions ? options.clone()
                        .afterMarker(listing.getNextMarker()) : options.afterMarker(listing
                        .getNextMarker());
            }
            listings.add(listing);
         }
         return Iterables.concat(listings);
      } catch (Exception e) {
         Throwables.propagateIfPossible(e, BlobRuntimeException.class);
         throw new BlobRuntimeException("Error getting resource metadata in container: "
                  + container, e);
      }
   }
}
