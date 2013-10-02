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

import javax.inject.Singleton;

import org.jclouds.atmos.domain.BoundedSet;
import org.jclouds.atmos.domain.DirectoryEntry;
import org.jclouds.atmos.domain.FileType;
import org.jclouds.atmos.domain.internal.BoundedLinkedHashSet;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.domain.StorageType;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

/**
 * @author Adrian Cole
 */
@Singleton
public class ResourceMetadataListToDirectoryEntryList
         implements
         Function<org.jclouds.blobstore.domain.PageSet<? extends StorageMetadata>, BoundedSet<? extends DirectoryEntry>> {

   public BoundedSet<DirectoryEntry> apply(
            org.jclouds.blobstore.domain.PageSet<? extends StorageMetadata> from) {

      return new BoundedLinkedHashSet<DirectoryEntry>(Iterables.transform(from,
               new Function<StorageMetadata, DirectoryEntry>() {
                  public DirectoryEntry apply(StorageMetadata from) {
                     FileType type = (from.getType() == StorageType.FOLDER || from.getType() == StorageType.RELATIVE_PATH) ? FileType.DIRECTORY
                              : FileType.REGULAR;
                     return new DirectoryEntry(from.getProviderId(), type, from.getName());
                  }

               }), from.getNextMarker());

   }
}
