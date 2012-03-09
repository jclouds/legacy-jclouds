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

import com.google.common.base.Function;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import javax.inject.Inject;

/**
 * A function for transforming a nova-specific Image into a generic Image object.
 *
 * @author Matt Stephenson
 */
public class NovaImageToImage implements Function<org.jclouds.openstack.nova.v1_1.domain.Image, Image>
{
   private final Function<org.jclouds.openstack.nova.v1_1.domain.Image, OperatingSystem> imageToOs;

   @Inject
   public NovaImageToImage(Function<org.jclouds.openstack.nova.v1_1.domain.Image, OperatingSystem> imageToOs)
   {
      this.imageToOs = imageToOs;
   }

   @Override
   public Image apply(org.jclouds.openstack.nova.v1_1.domain.Image image)
   {
      return new ImageBuilder()
         .id(image.getId())
         .name(image.getName())
         .operatingSystem(imageToOs.apply(image))
         .description(image.getName())
         .build();
   }
}
