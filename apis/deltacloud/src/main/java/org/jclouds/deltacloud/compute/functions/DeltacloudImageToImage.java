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
package org.jclouds.deltacloud.compute.functions;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageBuilder;

import com.google.common.base.Function;

/**
 * @author Adrian Cole
 */
@Singleton
public class DeltacloudImageToImage implements Function<org.jclouds.deltacloud.domain.Image, Image> {
   private final DeltacloudImageToOperatingSystem imageToOperatingSystem;

   @Inject
   public DeltacloudImageToImage(DeltacloudImageToOperatingSystem imageToOperatingSystem) {
      this.imageToOperatingSystem = imageToOperatingSystem;
   }

   @Override
   public Image apply(org.jclouds.deltacloud.domain.Image from) {
      ImageBuilder builder = new ImageBuilder();
      builder.id(from.getHref().toASCIIString());
      builder.providerId(from.getId());
      builder.uri(from.getHref());
      builder.name(from.getName());
      builder.description(from.getDescription());
      builder.operatingSystem(imageToOperatingSystem.apply(from));
      return builder.build();
   }
}
