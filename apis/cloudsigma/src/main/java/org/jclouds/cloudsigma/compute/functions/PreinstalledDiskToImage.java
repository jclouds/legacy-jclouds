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
package org.jclouds.cloudsigma.compute.functions;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.cloudsigma.domain.DriveInfo;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.Image.Status;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OperatingSystem.Builder;
import org.jclouds.compute.domain.OsFamilyVersion64Bit;
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
      Builder builder = OperatingSystem.builder();
      OsFamilyVersion64Bit parsed = imageParser.apply(drive.getName());
      builder.name(drive.getName()).description(description)
            .is64Bit(drive.getBits() != null ? drive.getBits() == 64 : parsed.is64Bit).version(parsed.version)
            .family(parsed.family);
      return new ImageBuilder().ids(drive.getUuid())
            .userMetadata(ImmutableMap.<String, String> of("size", drive.getSize() / 1024 / 1024 / 1024 + ""))
            .location(locationSupplier.get()).name(drive.getName()).description(description)
            .operatingSystem(builder.build()).status(Status.AVAILABLE).version("").build();
   }
}
