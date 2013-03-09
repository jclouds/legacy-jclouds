/*
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

package org.jclouds.googlecompute.compute.functions;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.googlecompute.GoogleComputeConstants;
import org.jclouds.googlecompute.domain.Image;

import java.util.List;

import static com.google.common.base.Joiner.on;
import static com.google.common.collect.Iterables.getLast;
import static com.google.common.collect.Iterables.limit;
import static com.google.common.collect.Iterables.skip;
import static org.jclouds.compute.domain.Image.Status;
import static org.jclouds.googlecompute.GoogleComputeConstants.GOOGLE_PROVIDER_LOCATION;

/**
 * Transforms a google compute domain specific image to a generic Image object.
 *
 * @author David Alves
 */
public class GoogleComputeImageToImage implements Function<Image, org.jclouds.compute.domain.Image> {


   @Override
   public org.jclouds.compute.domain.Image apply(Image image) {
      ImageBuilder builder = new ImageBuilder()
              .id(image.getName())
              .name(image.getName())
              .providerId(image.getId())
              .description(image.getDescription().orNull())
              .status(Status.AVAILABLE)
              .location(GOOGLE_PROVIDER_LOCATION)
              .uri(image.getSelfLink());

      List<String> splits = Lists.newArrayList(image.getName().split("-"));
      OperatingSystem.Builder osBuilder = defaultOperatingSystem(image);
      if (splits == null || splits.size() == 0 || splits.size() < 3) {
         return builder.operatingSystem(osBuilder.build()).build();
      }

      OsFamily family = OsFamily.fromValue(splits.get(0));
      if (family != OsFamily.UNRECOGNIZED) {
         osBuilder.family(family);
      }

      String version = on(".").join(limit(skip(splits, 1), splits.size() - 2));
      osBuilder.version(version);

      builder.version(getLast(splits));
      return builder.operatingSystem(osBuilder.build()).build();
   }

   private OperatingSystem.Builder defaultOperatingSystem(Image image) {
      return OperatingSystem.builder()
              .family(OsFamily.LINUX)
              .is64Bit(true)
              .description(image.getName());
   }

}
