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
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertTrue;

import java.util.Set;

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
@Test(groups = "live", testName = "OSImageApiLiveTest")
public class OSImageApiLiveTest extends BaseAzureManagementApiLiveTest {

   private ImmutableSet<String> locations;

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
   }

   @Test
   protected void testList() {
      Set<OSImage> response = api().list();

      for (OSImage image : response) {
         checkOSImage(image);
      }
   }

   private void checkOSImage(OSImage image) {
      checkNotNull(image.getLabel(), "Label cannot be null for OSImage %s", image);
      checkNotNull(image.getName(), "Name cannot be null for OSImage %s", image.getLabel());
      checkNotNull(image.getOS(), "OS cannot be null for OSImage: %s", image);
      assertNotEquals(image.getOS(), OSType.UNRECOGNIZED, "Status cannot be UNRECOGNIZED for OSImage: " + image);

      checkNotNull(image.getCategory(), "While Category can be null for OSImage, its Optional wrapper cannot: %s",
               image);
      if (image.getCategory().isPresent())
         assertNotEquals("", image.getCategory().get().trim(), "Invalid Category: " + image.toString());

      checkNotNull(image.getLogicalSizeInGB(),
               "While LogicalSizeInGB can be null for OSImage, its Optional wrapper cannot: %s", image);

      if (image.getLogicalSizeInGB().isPresent())
         assertTrue(image.getLogicalSizeInGB().get() > 0,
                  "LogicalSizeInGB should be positive, if set" + image.toString());

      checkNotNull(image.getMediaLink(), "While MediaLink can be null for OSImage, its Optional wrapper cannot: %s",
               image);

      if (image.getMediaLink().isPresent())
         assertTrue(ImmutableSet.of("http", "https").contains(image.getMediaLink().get().getScheme()),
                  "MediaLink should be an http(s) url" + image.toString());

      checkNotNull(image.getDescription(),
               "While Description can be null for OSImage, its Optional wrapper cannot: %s", image);

      checkNotNull(image.getLocation(), "While Location can be null for OSImage, its Optional wrapper cannot: %s",
               image);
      if (image.getLocation().isPresent()) {
         assertTrue(locations.contains(image.getLocation().get()),
                  "Location not in " + locations + " :" + image.toString());
      }
      
      checkNotNull(image.getEula(), "While Eula can be null for OSImage, its Optional wrapper cannot: %s",
               image);
      if (image.getEula().isPresent()) {
         assertTrue(ImmutableSet.of("http", "https").contains(image.getEula().get().getScheme()),
                  "Eula should be an http(s) url" + image.toString());
      }

      checkNotNull(image.getAffinityGroup(),
               "While AffinityGroup can be null for OSImage, its Optional wrapper cannot: %s", image);
      if (image.getAffinityGroup().isPresent()) {
         // TODO: list getAffinityGroups and check if there
      }
   }

   protected OSImageApi api() {
      return context.getApi().getOSImageApi();
   }
}
