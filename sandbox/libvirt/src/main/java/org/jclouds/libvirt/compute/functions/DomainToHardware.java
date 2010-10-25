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

import java.util.List;

import javax.inject.Singleton;

import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Processor;
import org.libvirt.Domain;
import org.libvirt.LibvirtException;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

/**
 * @author Adrian Cole
 */
@Singleton
public class DomainToHardware implements Function<Domain, Hardware> {

   @Override
   public Hardware apply(Domain from) {
      HardwareBuilder builder = new HardwareBuilder();
      try {
         builder.id(from.getUUIDString());

         builder.providerId(from.getID() + "");
         builder.name(from.getName());
         List<Processor> processors = Lists.newArrayList();
         for (int i = 0; i < from.getInfo().nrVirtCpu; i++) {
            processors.add(new Processor(i + 1, 1));
         }
         builder.processors(processors);

         builder.ram((int) from.getInfo().maxMem);
         // TODO volumes
      } catch (LibvirtException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      return builder.build();
   }

}
