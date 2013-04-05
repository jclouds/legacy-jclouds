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

package org.jclouds.blobstore.management;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.domain.Location;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.management.JcloudsManagedBean;
import org.jclouds.management.functions.ToCompositeData;
import org.jclouds.management.functions.ToTabularData;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.TabularData;

public class BlobStoreManagement implements BlobStoreManagementMBean, JcloudsManagedBean {

   private final BlobStore blobStore;

   private final ToTabularData<Location> locationToTabular = ToTabularData.from(Location.class);
   private final ToCompositeData<Blob> blobToComposite = ToCompositeData.from(Blob.class);
   private final ToTabularData<StorageMetadata> storageMdToTabular = ToTabularData.from(StorageMetadata.class);
   private final ToCompositeData<BlobMetadata> blobMdToComposite = ToCompositeData.from(BlobMetadata.class);


   public BlobStoreManagement(BlobStoreContext context) {
      this.blobStore = context.getBlobStore();
   }


   /**
    * {@inheritDoc}
    */
   @Override
   public TabularData listAssignableLocations() throws OpenDataException {
      return locationToTabular.apply(blobStore.listAssignableLocations());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public TabularData list() throws OpenDataException {
      return storageMdToTabular.apply(blobStore.list());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public TabularData list(String container) throws OpenDataException {
      return storageMdToTabular.apply(blobStore.list(container));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public CompositeData blobMetadata(String container, String name) throws OpenDataException {
      return blobMdToComposite.apply(blobStore.blobMetadata(container, name));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public CompositeData getBlob(String container, String name) throws OpenDataException {
      return blobToComposite.apply(blobStore.getBlob(container, name));
   }


   /**
    * {@inheritDoc}
    */
   @Override
   public boolean containerExists(String container) {
      return blobStore.containerExists(container);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean createContainerInLocation(@Nullable String locationId, String container) {
      Optional<? extends Location> location = Iterables.tryFind(blobStore.listAssignableLocations(), new LocationPredicate(locationId));

      if (location.isPresent()) {
         return blobStore.createContainerInLocation(location.get(), container);
      } else {
         return false;
      }
   }


   /**
    * {@inheritDoc}
    */
   @Override
   public void clearContainer(String container) {
      blobStore.clearContainer(container);
   }


   /**
    * {@inheritDoc}
    */
   @Override
   public void deleteContainer(String container) {
      blobStore.deleteContainer(container);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean directoryExists(String container, String directory) {
      return blobStore.directoryExists(container, directory);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void createDirectory(String container, String directory) {
      blobStore.createDirectory(container, directory);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void deleteDirectory(String containerName, String name) {
      blobStore.deleteDirectory(containerName, name);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean blobExists(String container, String name) {
      return blobStore.blobExists(container, name);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void removeBlob(String container, String name) {
      blobStore.removeBlob(container, name);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public long countBlobs(String container) {
      return blobStore.countBlobs(container);
   }

   /**
    * Returns the type of the MBean.
    *
    * @return
    */
   @Override
   public String getType() {
      return "blobstore";
   }

   /**
    * Returns the name of the MBean.
    *
    * @return
    */
   @Override
   public String getName() {
      return blobStore.getContext().unwrap().getName();
   }

   private static final class LocationPredicate implements Predicate<Location> {
      private final String id;

      private LocationPredicate(String id) {
         this.id = id;
      }

      @Override
      public boolean apply(@Nullable Location input) {
         return input.getId().equals(id);
      }
   }
}
