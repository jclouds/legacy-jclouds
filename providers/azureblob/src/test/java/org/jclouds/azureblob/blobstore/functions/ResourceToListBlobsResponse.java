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
package org.jclouds.azureblob.blobstore.functions;

import java.util.SortedSet;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.azureblob.domain.BlobProperties;
import org.jclouds.azureblob.domain.ListBlobsResponse;
import org.jclouds.azureblob.domain.MutableBlobProperties;
import org.jclouds.azureblob.domain.internal.HashSetListBlobsResponse;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.domain.StorageType;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * @author Adrian Cole
 */
@Singleton
public class ResourceToListBlobsResponse implements
         Function<PageSet<? extends StorageMetadata>, ListBlobsResponse> {
   private final BlobMetadataToBlobProperties blob2ObjectMd;

   @Inject
   public ResourceToListBlobsResponse(BlobMetadataToBlobProperties blob2ObjectMd) {
      this.blob2ObjectMd = blob2ObjectMd;
   }

   public ListBlobsResponse apply(PageSet<? extends StorageMetadata> list) {

      Iterable<BlobProperties> contents = Iterables.transform(Iterables.filter(list,
               new Predicate<StorageMetadata>() {

                  public boolean apply(StorageMetadata input) {
                     return input.getType() == StorageType.BLOB;
                  }

               }), new Function<StorageMetadata, BlobProperties>() {

         public MutableBlobProperties apply(StorageMetadata from) {
            return blob2ObjectMd.apply((BlobMetadata) from);
         }

      });

      SortedSet<String> commonPrefixes = Sets.newTreeSet(Iterables.transform(Iterables.filter(list,
               new Predicate<StorageMetadata>() {

                  public boolean apply(StorageMetadata input) {
                     return input.getType() == StorageType.RELATIVE_PATH;
                  }

               }), new Function<StorageMetadata, String>() {

         public String apply(StorageMetadata from) {
            return from.getName();
         }

      }));
      return new HashSetListBlobsResponse(contents, null, null, null, null, list.getNextMarker(),
               "/", commonPrefixes);
   }
}
