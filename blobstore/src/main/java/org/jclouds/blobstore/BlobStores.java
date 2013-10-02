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
package org.jclouds.blobstore;

import java.util.Iterator;

import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.options.ListAllOptions;
import org.jclouds.blobstore.options.ListContainerOptions;

import com.google.common.annotations.Beta;
import com.google.common.collect.AbstractIterator;

/**
 * Utilities for using Blob Stores.
 * 
 * @author Aled Sage
 * @since 1.3
 */
public class BlobStores {

   /**
    * @see listAll(BlobStore, String, ListContainerOptions, ListAllOptions)
    */
   @Beta
   public static Iterable<StorageMetadata> listAll(BlobStore blobStore, String container,
            ListContainerOptions containerOptions) {
      return listAll(blobStore, container, containerOptions, ListAllOptions.NONE);
   }
   
   /**
    * A variant of BlobStore.list(String, ListContainerOptions) that
    * produces an Iterable over the entire set of results, not just one
    * page, making multiple calls to BlobStore.list as needed.
    * 
    * Note that if listAllOptions.isEager, then the first page will be fetched
    * immediately and cached. Repeatedly iterating will not re-fetch (and thus
    * will not refresh) the first page.
    *  
    * @throws ContainerNotFoundException If listAllOptions.isEager and container cannot be found
    */
   @Beta
   public static Iterable<StorageMetadata> listAll(final BlobStore blobStore, final String container,
            final ListContainerOptions containerOptions, final ListAllOptions listAllOptions) {
      final boolean eager = listAllOptions.isEager();
      final PageSet<? extends StorageMetadata> firstList;
      final String firstMarker;

      if (eager) {
         firstList = blobStore.list(container, containerOptions);
         firstMarker = firstList.getNextMarker();
      } else {
         firstList = null;
         firstMarker = null;
      }
      
      return new Iterable<StorageMetadata>() {
         public Iterator<StorageMetadata> iterator() {
            return new AbstractIterator<StorageMetadata>() {
               private Iterator<? extends StorageMetadata> iterator;
               private String marker;

               public StorageMetadata computeNext() {
                  while (true) {
                     if (iterator == null) {
                        PageSet<? extends StorageMetadata> list;
                        if (eager && marker == null) {
                           list = firstList;
                           marker = firstMarker;
                        } else {
                           ListContainerOptions nextOptions = marker == null ? containerOptions : containerOptions.clone().afterMarker(marker);
                           list = blobStore.list(container, nextOptions);
                           marker = list.getNextMarker();
                        }
                        iterator = list.iterator();
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
