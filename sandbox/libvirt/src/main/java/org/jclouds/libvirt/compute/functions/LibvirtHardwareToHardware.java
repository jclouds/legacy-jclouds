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

package org.jclouds.libvirt.compute.functions;

import javax.inject.Singleton;

import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.domain.internal.VolumeImpl;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;

/**
 * @author Adrian Cole
 */
@Singleton
public class LibvirtHardwareToHardware implements Function<org.jclouds.libvirt.Hardware, Hardware> {

   @Override
   public Hardware apply(org.jclouds.libvirt.Hardware from) {
      HardwareBuilder builder = new HardwareBuilder();
      builder.ids(from.id + "");
      builder.name(from.name);
      builder.processors(ImmutableList.of(new Processor(from.cores, 1.0)));
      builder.ram(from.ram);
      builder.volumes(ImmutableList.<Volume> of(new VolumeImpl(from.disk, true, false)));
      return builder.build();
   }

}
