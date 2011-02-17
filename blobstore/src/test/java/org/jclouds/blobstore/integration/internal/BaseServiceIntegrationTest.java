/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.blobstore.integration.internal;

import static org.testng.Assert.assertEquals;

import java.util.Set;

import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.domain.internal.MutableStorageMetadataImpl;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationScope;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * 
 * @author Adrian Cole
 */
public class BaseServiceIntegrationTest extends BaseBlobStoreIntegrationTest {

   @Test(groups = { "integration", "live" })
   void containerDoesntExist() {
      Set<? extends StorageMetadata> list = context.getBlobStore().list();
      assert !list.contains(new MutableStorageMetadataImpl());
   }

   @Test(groups = { "integration", "live" })
   public void testAllLocations() throws InterruptedException {
      for (final Location location : context.getBlobStore().listAssignableLocations()) {
         final String containerName = getScratchContainerName();
         try {
            System.err.printf(" >> creating container in location %s%n", location);
            context.getBlobStore().createContainerInLocation(location, containerName);
            System.err.printf(" << call complete.. checking%n");

            assertConsistencyAware(new Runnable() {

               @Override
               public void run() {
                  PageSet<? extends StorageMetadata> list = context.getBlobStore().list();
                  assert Iterables.any(list, new Predicate<StorageMetadata>() {
                     public boolean apply(StorageMetadata md) {
                        return containerName.equals(md.getName()) && location.equals(md.getLocation());
                     }
                  }) : String.format("container %s/%s not found in list %s", location, containerName, list);
               }

            });
         } finally {
            recycleContainer(containerName);
         }
      }
   }

   protected Set<String> getIso3166Codes() {
      return ImmutableSet.<String> of();
   }

   @Test(groups = { "integration", "live" })
   public void testGetAssignableLocations() throws Exception {
      assertProvider(context.getProviderSpecificContext());
      for (Location location : context.getBlobStore().listAssignableLocations()) {
         System.err.printf("location %s%n", location);
         assert location.getId() != null : location;
         assert location != location.getParent() : location;
         assert location.getScope() != null : location;
         switch (location.getScope()) {
            case PROVIDER:
               assertProvider(location);
               break;
            case REGION:
               assertProvider(location.getParent());
               assert location.getIso3166Codes().size() == 0
                        || location.getParent().getIso3166Codes().containsAll(location.getIso3166Codes()) : location
                        + " ||" + location.getParent();
               break;
            case ZONE:
               Location provider = location.getParent().getParent();
               // zone can be a direct descendant of provider
               if (provider == null)
                  provider = location.getParent();
               assertProvider(provider);
               assert location.getIso3166Codes().size() == 0
                        || location.getParent().getIso3166Codes().containsAll(location.getIso3166Codes()) : location
                        + " ||" + location.getParent();
               break;
            case HOST:
               Location provider2 = location.getParent().getParent().getParent();
               // zone can be a direct descendant of provider
               if (provider2 == null)
                  provider2 = location.getParent().getParent();
               assertProvider(provider2);
               break;
         }
      }
   }

   void assertProvider(Location provider) {
      assertEquals(provider.getScope(), LocationScope.PROVIDER);
      assertEquals(provider.getParent(), null);
      assertEquals(provider.getIso3166Codes(), getIso3166Codes());
   }

}