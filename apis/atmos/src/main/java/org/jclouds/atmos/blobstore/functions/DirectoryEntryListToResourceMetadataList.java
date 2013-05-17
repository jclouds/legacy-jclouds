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
package org.jclouds.atmos.blobstore.functions;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.atmos.domain.BoundedSet;
import org.jclouds.atmos.domain.DirectoryEntry;
import org.jclouds.atmos.domain.FileType;
import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.domain.StorageType;
import org.jclouds.blobstore.domain.internal.BlobMetadataImpl;
import org.jclouds.blobstore.domain.internal.PageSetImpl;
import org.jclouds.blobstore.domain.internal.StorageMetadataImpl;
import org.jclouds.domain.Location;
import org.jclouds.io.payloads.BaseMutableContentMetadata;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

/**
 * @author Adrian Cole
 */
@Singleton
public class DirectoryEntryListToResourceMetadataList implements
         Function<BoundedSet<? extends DirectoryEntry>, PageSet<? extends StorageMetadata>> {
   private Supplier<Location> defaultLocation;

   @Inject
   DirectoryEntryListToResourceMetadataList(Supplier<Location> defaultLocation) {
      this.defaultLocation = defaultLocation;
   }

   public PageSet<? extends StorageMetadata> apply(BoundedSet<? extends DirectoryEntry> from) {

      return new PageSetImpl<StorageMetadata>(Iterables.transform(from,
               new Function<DirectoryEntry, StorageMetadata>() {

                  public StorageMetadata apply(DirectoryEntry from) {
                     StorageType type = from.getType() == FileType.DIRECTORY ? StorageType.FOLDER : StorageType.BLOB;
                     if (type == StorageType.FOLDER)
                        return new StorageMetadataImpl(type, from.getObjectID(), from.getObjectName(), defaultLocation
                                 .get(), null, null, null, null, ImmutableMap.<String,String>of());
                     else
                        return new BlobMetadataImpl(from.getObjectID(), from.getObjectName(), defaultLocation.get(),
                                 null, null, null, null, ImmutableMap.<String,String>of(), null,
                                 null, new BaseMutableContentMetadata());
                  }

               }), from.getToken());

   }
}
