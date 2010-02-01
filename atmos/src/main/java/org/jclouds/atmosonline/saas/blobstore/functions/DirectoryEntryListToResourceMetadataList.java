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
package org.jclouds.atmosonline.saas.blobstore.functions;

import javax.inject.Singleton;

import org.jclouds.atmosonline.saas.domain.BoundedSet;
import org.jclouds.atmosonline.saas.domain.DirectoryEntry;
import org.jclouds.atmosonline.saas.domain.FileType;
import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.domain.StorageType;
import org.jclouds.blobstore.domain.internal.BlobMetadataImpl;
import org.jclouds.blobstore.domain.internal.PageSetImpl;
import org.jclouds.blobstore.domain.internal.StorageMetadataImpl;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

/**
 * @author Adrian Cole
 */
@Singleton
public class DirectoryEntryListToResourceMetadataList implements
         Function<BoundedSet<? extends DirectoryEntry>, PageSet<? extends StorageMetadata>> {

   public PageSet<? extends StorageMetadata> apply(BoundedSet<? extends DirectoryEntry> from) {

      return new PageSetImpl<StorageMetadata>(Iterables.transform(from,
               new Function<DirectoryEntry, StorageMetadata>() {

                  public StorageMetadata apply(DirectoryEntry from) {
                     StorageType type = from.getType() == FileType.DIRECTORY ? StorageType.FOLDER
                              : StorageType.BLOB;
                     if (type == StorageType.FOLDER)
                        return new StorageMetadataImpl(type, from.getObjectID(), from
                                 .getObjectName(), null, null, null, null, null, Maps
                                 .<String, String> newHashMap());
                     else
                        return new BlobMetadataImpl(from.getObjectID(), from.getObjectName(), null,
                                 null, null, null, null, Maps.<String, String> newHashMap(), null,
                                 null);
                  }

               }), from.getToken());

   }
}