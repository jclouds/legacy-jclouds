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

package org.jclouds.cloudsigma.compute.functions;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.cloudsigma.domain.DriveInfo;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.OperatingSystemBuilder;
import org.jclouds.compute.domain.os.OsFamilyVersion64Bit;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;

/**
 * @author Adrian Cole
 */
@Singleton
public class PreinstalledDiskToImage implements Function<DriveInfo, Image> {
   private final Supplier<Location> locationSupplier;
   private final Function<String, OsFamilyVersion64Bit> imageParser;

   @Inject
   public PreinstalledDiskToImage(Supplier<Location> locationSupplier,
            Function<String, OsFamilyVersion64Bit> imageParser) {
      this.locationSupplier = locationSupplier;
      this.imageParser = imageParser;
   }

   @Override
   public Image apply(DriveInfo drive) {
      if (drive.getName() == null)
         return null;
      String description = drive.getDescription() != null ? drive.getDescription() : drive.getName();
      OperatingSystemBuilder builder = new OperatingSystemBuilder();
      OsFamilyVersion64Bit parsed = imageParser.apply(drive.getName());
      builder.name(drive.getName()).description(description).is64Bit(parsed.is64Bit).version(parsed.version).family(
               parsed.family);
      return new ImageBuilder().ids(drive.getUuid()).adminPassword("cloudsigma").userMetadata(
               ImmutableMap.<String, String> of("size", drive.getSize() / 1024 / 1024 / 1024 + "")).defaultCredentials(
               new Credentials("cloudsigma", "cloudsigma")).location(locationSupplier.get()).name(drive.getName())
               .description(description).operatingSystem(builder.build()).version("").build();
   }
}