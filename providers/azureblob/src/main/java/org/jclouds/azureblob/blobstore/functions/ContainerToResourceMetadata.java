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

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.azureblob.domain.ContainerProperties;
import org.jclouds.blobstore.domain.MutableStorageMetadata;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.domain.StorageType;
import org.jclouds.blobstore.domain.internal.MutableStorageMetadataImpl;
import org.jclouds.domain.Location;

import com.google.common.base.Function;
import com.google.common.base.Supplier;

/**
 * @author Adrian Cole
 */
@Singleton
public class ContainerToResourceMetadata implements Function<ContainerProperties, StorageMetadata> {
   private Supplier<Location> defaultLocation;

   @Inject
   ContainerToResourceMetadata(Supplier<Location> defaultLocation) {
      this.defaultLocation = defaultLocation;
   }

   public StorageMetadata apply(ContainerProperties from) {
      MutableStorageMetadata to = new MutableStorageMetadataImpl();
      to.setName(from.getName());
      to.setLocation(defaultLocation.get());
      to.setETag(from.getETag());
      to.setLastModified(from.getLastModified());
      to.setUri(from.getUrl());
      to.setType(StorageType.CONTAINER);
      to.setUserMetadata(from.getMetadata());
      return to;
   }
}
