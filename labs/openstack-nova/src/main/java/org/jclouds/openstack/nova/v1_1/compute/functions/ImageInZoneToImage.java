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
package org.jclouds.openstack.nova.v1_1.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Map;

import javax.inject.Inject;

import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.domain.Location;
import org.jclouds.openstack.nova.v1_1.compute.domain.ImageInZone;

import com.google.common.base.Function;
import com.google.common.base.Supplier;

/**
 * A function for transforming a nova-specific Image into a generic Image object.
 * 
 * @author Matt Stephenson
 */
public class ImageInZoneToImage implements Function<ImageInZone, Image> {
   private final Function<org.jclouds.openstack.nova.v1_1.domain.Image, OperatingSystem> imageToOs;
   private final Supplier<Map<String, Location>> locationIndex;

   @Inject
   public ImageInZoneToImage(Function<org.jclouds.openstack.nova.v1_1.domain.Image, OperatingSystem> imageToOs,
            Supplier<Map<String, Location>> locationIndex) {
      this.imageToOs = checkNotNull(imageToOs, "imageToOs");
      this.locationIndex = checkNotNull(locationIndex, "locationIndex");
   }

   @Override
   public Image apply(ImageInZone imageInZone) {
      Location location = locationIndex.get().get(imageInZone.getZone());
      checkState(location != null, "location %s not in locationIndex: %s", imageInZone.getZone(), locationIndex.get());
      org.jclouds.openstack.nova.v1_1.domain.Image image = imageInZone.getImage();
      return new ImageBuilder()
               .id(imageInZone.slashEncode()).providerId(image.getId()).name(image.getName()).operatingSystem(
                        imageToOs.apply(image)).description(image.getName()).location(location).build();
   }
}
