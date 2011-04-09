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
package org.jclouds.savvis.vpdc.compute.functions;

import java.util.Map.Entry;

import javax.inject.Singleton;

import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.domain.VolumeBuilder;
import org.jclouds.savvis.vpdc.domain.VMSpec;

import com.google.common.base.Function;

/**
 * @author Adrian Cole
 */
@Singleton
public class VMSpecToHardware implements Function<VMSpec, Hardware> {

   @Override
   public Hardware apply(VMSpec from) {
      HardwareBuilder builder = new HardwareBuilder();
      builder.ids(from.toString());
      for (int i = 0; i < from.getProcessorCount(); i++)
         builder.processor(new Processor(1, 3.0));
      builder.ram(from.getMemoryInGig() * 1024);
      builder.volume(new VolumeBuilder().type(Volume.Type.LOCAL).device(from.getBootDeviceName()).size(
               new Float(from.getBootDiskSize())).bootDevice(true).durable(true).build());
      for (Entry<String, Integer> disk : from.getDataDiskDeviceNameToSizeInGig().entrySet())
         builder.volume(new VolumeBuilder().type(Volume.Type.LOCAL).device(disk.getKey()).size(
                  new Float(disk.getValue())).durable(true).build());
      return builder.build();
   }

}
