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
package org.jclouds.azure.management.features;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.transform;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import org.jclouds.azure.management.domain.Disk;
import org.jclouds.azure.management.domain.Location;
import org.jclouds.azure.management.domain.OSImage;
import org.jclouds.azure.management.domain.OSType;
import org.jclouds.azure.management.internal.BaseAzureManagementApiLiveTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "DiskApiLiveTest")
public class DiskApiLiveTest extends BaseAzureManagementApiLiveTest {

   private ImmutableSet<String> locations;
   private ImmutableSet<String> images;

   @BeforeClass(groups = { "integration", "live" })
   public void setupContext() {
      super.initializeContext();

      locations = ImmutableSet.copyOf(transform(context.getApi().getLocationApi().list(),
               new Function<Location, String>() {
                  @Override
                  public String apply(Location in) {
                     return in.getName();
                  }
               }));
      images = ImmutableSet.copyOf(transform(context.getApi().getOSImageApi().list(), new Function<OSImage, String>() {
         @Override
         public String apply(OSImage in) {
            return in.getName();
         }
      }));
   }

   @Test
   protected void testList() {
      Set<Disk> response = api().list();

      for (Disk disk : response) {
         checkDisk(disk);
      }
   }

   private void checkDisk(Disk disk) {
      checkNotNull(disk.getName(), "Name cannot be null for Disk %s", disk.getLabel());
      checkNotNull(disk.getOS(), "OS cannot be null for Disk: %s", disk);
      assertNotEquals(disk.getOS(), OSType.UNRECOGNIZED, "Status cannot be UNRECOGNIZED for Disk: " + disk);

      checkNotNull(disk.getAttachedTo(), "While AttachedTo can be null for Disk, its Optional wrapper cannot: %s", disk);
      if (disk.getAttachedTo().isPresent()) {
         // TODO: verify you can lookup the role
      }

      checkNotNull(disk.getLogicalSizeInGB(),
               "While LogicalSizeInGB can be null for Disk, its Optional wrapper cannot: %s", disk);

      if (disk.getLogicalSizeInGB().isPresent())
         assertTrue(disk.getLogicalSizeInGB().get() > 0, "LogicalSizeInGB should be positive, if set" + disk.toString());

      checkNotNull(disk.getMediaLink(), "While MediaLink can be null for Disk, its Optional wrapper cannot: %s", disk);

      if (disk.getMediaLink().isPresent())
         assertTrue(ImmutableSet.of("http", "https").contains(disk.getMediaLink().get().getScheme()),
                  "MediaLink should be an http(s) url" + disk.toString());
      
      checkNotNull(disk.getLabel(), "While Label can be null for Disk, its Optional wrapper cannot: %s",
               disk);
      
      checkNotNull(disk.getDescription(), "While Description can be null for Disk, its Optional wrapper cannot: %s",
               disk);

      checkNotNull(disk.getLocation(), "While Location can be null for Disk, its Optional wrapper cannot: %s", disk);
      if (disk.getLocation().isPresent()) {
         assertTrue(locations.contains(disk.getLocation().get()),
                  "Location not in " + locations + " :" + disk.toString());
      }

      checkNotNull(disk.getSourceImage(), "While SourceImage can be null for Disk, its Optional wrapper cannot: %s",
               disk);
      if (disk.getSourceImage().isPresent()) {
         assertTrue(images.contains(disk.getSourceImage().get()),
                  "SourceImage not in " + images + " :" + disk.toString());
      }

      checkNotNull(disk.getAffinityGroup(),
               "While AffinityGroup can be null for Disk, its Optional wrapper cannot: %s", disk);
      if (disk.getAffinityGroup().isPresent()) {
         // TODO: list getAffinityGroups and check if there
      }
   }

   protected DiskApi api() {
      return context.getApi().getDiskApi();
   }
}
