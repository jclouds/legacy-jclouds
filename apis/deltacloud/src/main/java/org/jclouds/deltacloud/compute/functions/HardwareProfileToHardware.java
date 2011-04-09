/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.deltacloud.compute.functions;

import javax.inject.Singleton;

import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.domain.VolumeBuilder;
import org.jclouds.deltacloud.domain.HardwareProfile;
import org.jclouds.deltacloud.domain.HardwareProperty;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;

/**
 * @author Adrian Cole
 */
@Singleton
public class HardwareProfileToHardware implements Function<HardwareProfile, Hardware> {

   @Override
   public Hardware apply(HardwareProfile from) {
      HardwareBuilder builder = new HardwareBuilder();
      builder.ids(from.getId());
      builder.name(from.getName());
      builder.uri(from.getHref());

      for (HardwareProperty property : from.getProperties()) {
         if (property.getName().equals("memory")) {
            builder.ram(Integer.parseInt(property.getValue().toString()));
         } else if (property.getName().equals("storage")) {
            Float gigs = new Float(property.getValue().toString());
            builder.processors(ImmutableList.of(new Processor(gigs / 10.0, 1.0)));
            builder.volume(new VolumeBuilder().type(Volume.Type.LOCAL).device("/").size(gigs).bootDevice(true).durable(
                     true).build());
         }
      }

      return builder.build();
   }
}
