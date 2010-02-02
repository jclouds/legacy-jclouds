/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.blobstore.strategy.internal;

import java.util.List;

import javax.inject.Singleton;

import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.domain.StorageType;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.blobstore.strategy.ListBlobsInContainer;
import org.jclouds.blobstore.strategy.ListContainerStrategy;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

/**
 * Retrieves all blobs in the blobstore by the most efficient means possible.
 * 
 * @author Adrian Cole
 */
@Singleton
public class ListContainerAndRecurseThroughFolders implements ListBlobsInContainer {

   protected final ListContainerStrategy lister;

   @Inject
   ListContainerAndRecurseThroughFolders(ListContainerStrategy lister) {
      this.lister = lister;
   }

   @Override
   public Iterable<? extends BlobMetadata> execute(final String containerName,
            final ListContainerOptions options) {
      final List<Iterable<? extends BlobMetadata>> lists = Lists.newArrayList();
      Iterable<? extends StorageMetadata> pwdList = lister.execute(containerName, options);
      for (StorageMetadata md : Iterables.filter(pwdList, new Predicate<StorageMetadata>() {
         @Override
         public boolean apply(StorageMetadata input) {
            return (input.getType() == StorageType.FOLDER || input.getType() == StorageType.RELATIVE_PATH)
                     && options.isRecursive();
         }
      })) {
         String directory = (options.getDir() != null) ? options.getDir() + "/" + md.getName() : md
                  .getName();
         lists.add(execute(containerName, options.clone().inDirectory(directory)));
      }
      lists.add(Iterables.transform(Iterables.filter(pwdList, new Predicate<StorageMetadata>() {
         @Override
         public boolean apply(StorageMetadata input) {
            return input.getType() == StorageType.BLOB;
         }
      }), new Function<StorageMetadata, BlobMetadata>() {
         @Override
         public BlobMetadata apply(StorageMetadata from) {
            return (BlobMetadata) from;
         }
      }));
      return Sets.newHashSet(Iterables.concat(lists));
   }
}