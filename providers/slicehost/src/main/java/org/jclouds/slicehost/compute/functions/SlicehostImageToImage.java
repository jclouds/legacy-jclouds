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
package org.jclouds.slicehost.compute.functions;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.OperatingSystem;

import com.google.common.base.Function;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class SlicehostImageToImage implements Function<org.jclouds.slicehost.domain.Image, Image> {
   private final Function<org.jclouds.slicehost.domain.Image, OperatingSystem> imageToOs;

   @Inject
   SlicehostImageToImage(Function<org.jclouds.slicehost.domain.Image, OperatingSystem> imageToOs) {
      this.imageToOs = imageToOs;
   }

   public Image apply(org.jclouds.slicehost.domain.Image from) {
      ImageBuilder builder = new ImageBuilder();
      builder.ids(from.getId() + "");
      builder.name(from.getName());
      builder.description(from.getName());
      builder.operatingSystem(imageToOs.apply(from));
      builder.status(Image.Status.AVAILABLE);
      Image image = builder.build();
      return image;
   }
}
