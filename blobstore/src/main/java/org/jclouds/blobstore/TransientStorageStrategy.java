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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;

import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.domain.Location;

public class TransientStorageStrategy implements LocalStorageStrategy {
   private final ConcurrentMap<String, ConcurrentMap<String, Blob>> containerToBlobs = new ConcurrentHashMap<String, ConcurrentMap<String, Blob>>();
   private final ConcurrentMap<String, Location> containerToLocation = new ConcurrentHashMap<String, Location>();
   private final Supplier<Location> defaultLocation;

   public TransientStorageStrategy(final Supplier<Location> defaultLocation) {
      this.defaultLocation = Preconditions.checkNotNull(defaultLocation);
   }

   public Iterable<String> getAllContainerNames() {
      return containerToBlobs.keySet();
   }

   public boolean containerExists(final String containerName) {
      return containerToBlobs.containsKey(containerName);
   }

   public void clearContainer(final String containerName) {
      containerToBlobs.get(containerName).clear();
   }

   @Override
   public void clearContainer(String container, ListContainerOptions options) {
      // TODO implement options
      clearContainer(container);
   }

   public boolean createContainerInLocation(final String containerName, final Location location) {
      ConcurrentMap<String, Blob> origValue = containerToBlobs.putIfAbsent(
            containerName, new ConcurrentHashMap<String, Blob>());
      if (origValue != null) {
         return false;
      }
      containerToLocation.put(containerName, location != null ? location : defaultLocation.get());
      return true;
   }

   public void deleteContainer(final String containerName) {
      containerToBlobs.remove(containerName);
   }

   public boolean blobExists(final String containerName, final String blobName) {
      Map<String, Blob> map = containerToBlobs.get(containerName);
      return map != null && map.containsKey(blobName);
   }

   public Blob getBlob(final String containerName, final String blobName) {
      Map<String, Blob> map = containerToBlobs.get(containerName);
      return map == null ? null : map.get(blobName);
   }

   public void putBlob(final String containerName, final Blob blob) {
      Map<String, Blob> map = containerToBlobs.get(containerName);
      map.put(blob.getMetadata().getName(), blob);
   }

   public void removeBlob(final String containerName, final String blobName) {
      Map<String, Blob> map = containerToBlobs.get(containerName);
      if (map != null)
         map.remove(blobName);
   }

   public Iterable<String> getBlobKeysInsideContainer(final String containerName) {
      return containerToBlobs.get(containerName).keySet();
   }

   @Override
   public Location getLocation(final String containerName) {
      return containerToLocation.get(containerName);
   }
}
