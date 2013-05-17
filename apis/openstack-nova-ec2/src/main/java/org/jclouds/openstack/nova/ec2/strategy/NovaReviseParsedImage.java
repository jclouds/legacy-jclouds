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
package org.jclouds.openstack.nova.ec2.strategy;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.ec2.compute.strategy.ReviseParsedImage;
import org.jclouds.openstack.nova.v2_0.domain.Image;

import com.google.common.base.Function;

/**
 * @author Adrian Cole
 */
@Singleton
public class NovaReviseParsedImage implements ReviseParsedImage {

   private final Function<Image, OperatingSystem> imageToOs;

   @Inject
   public NovaReviseParsedImage(Function<Image, OperatingSystem> imageToOs) {
      this.imageToOs = checkNotNull(imageToOs, "imageToOs");
   }

   @Override
   public void reviseParsedImage(org.jclouds.ec2.domain.Image from, ImageBuilder builder, OsFamily family,
            OperatingSystem.Builder osBuilder) {
      Image image = Image.builder().id(from.getId()).name(from.getName()).build();
      OperatingSystem os = imageToOs.apply(image);
      osBuilder.description(os.getDescription());
      osBuilder.family(os.getFamily());
      osBuilder.name(os.getName());
      osBuilder.is64Bit(os.is64Bit());
      osBuilder.version(os.getVersion());
      // arch is accurate already
   }
}
