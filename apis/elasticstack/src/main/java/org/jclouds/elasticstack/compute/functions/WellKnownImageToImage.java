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
package org.jclouds.elasticstack.compute.functions;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.Image.Status;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.elasticstack.domain.DriveInfo;
import org.jclouds.elasticstack.domain.WellKnownImage;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;

/**
 * @author Adrian Cole
 */
@Singleton
public class WellKnownImageToImage implements Function<DriveInfo, Image> {
   private final Supplier<Location> locationSupplier;
   private final Map<String, WellKnownImage> preinstalledImages;
   private final Map<String, Credentials> credentialStore;

   @Inject
   public WellKnownImageToImage(Supplier<Location> locationSupplier, Map<String, WellKnownImage> preinstalledImages, Map<String, Credentials> credentialStore) {
      this.locationSupplier = locationSupplier;
      this.preinstalledImages = preinstalledImages;
      this.credentialStore = credentialStore;
   }

   @Override
   public Image apply(DriveInfo drive) {
      WellKnownImage input = preinstalledImages.get(drive.getUuid());
      // set credentials in the store here, as opposed to directly modifying the image. we need to
      // set credentials on the image outside of this function so that they can be for example
      // overridden by properties
      credentialStore.put("image#" + drive.getUuid(), LoginCredentials.builder().user(input.getLoginUser()).build());
      return new ImageBuilder()
            .ids(drive.getUuid())
            .userMetadata(
                  ImmutableMap.<String, String> builder().putAll(drive.getUserMetadata())
                        .put("size", input.getSize() + "").build())
            .location(locationSupplier.get())
            .name(input.getDescription())
            .description(drive.getName())
            .status(Status.AVAILABLE)
            .operatingSystem(
                  new OperatingSystem.Builder().family(input.getOsFamily()).version(input.getOsVersion())
                        .name(input.getDescription()).description(drive.getName()).is64Bit(input.is64bit()).build())
            .version("").build();
   }
}
