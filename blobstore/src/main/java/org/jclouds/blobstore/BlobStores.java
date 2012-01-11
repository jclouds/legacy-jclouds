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
package org.jclouds.blobstore;

import java.util.Iterator;

import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.options.ListContainerOptions;

import com.google.common.annotations.Beta;
import com.google.common.collect.AbstractIterator;

public class BlobStores {

   /**
    * A variant of BlobStore.list(String, ListContainerOptions) that
    * produces an Iterable over the entire set of results, not just one
    * page, making multiple calls to BlobStore.list as needed.
    */
   @Beta
   public static Iterable<StorageMetadata> listAll(final BlobStore blobStore, final String container,
            final ListContainerOptions options) {

      return new Iterable<StorageMetadata>() {
         public Iterator<StorageMetadata> iterator() {
            return new AbstractIterator<StorageMetadata>() {
               private Iterator<? extends StorageMetadata> iterator;
               private String marker;

               public StorageMetadata computeNext() {
                  while (true) {
                     if (iterator == null) {
                        ListContainerOptions nextOptions = marker == null ? options : options.clone().afterMarker(marker);
                        PageSet<? extends StorageMetadata> list = blobStore.list(container, nextOptions);
                        iterator = list.iterator();
                        marker = list.getNextMarker();
                     }
                     if (iterator.hasNext()) {
                        return iterator.next();
                     }
                     if (marker == null) {
                        return endOfData();
                     }
                     iterator = null;
                  }
               }
            };
         }
      };
   }

}
