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

package org.jclouds.vsphere.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.compute.util.ComputeServiceUtils.parseOsFamilyOrUnrecognized;
import static org.jclouds.compute.util.ComputeServiceUtils.parseVersionOrReturnEmptyString;

import java.util.Map;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.Image.Status;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.vmware.vim25.VirtualMachinePowerState;
import com.vmware.vim25.mo.VirtualMachine;

@Singleton
public class VirtualMachineToImage implements Function<VirtualMachine, Image> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final Map<VirtualMachinePowerState, Status> toPortableImageStatus;
   private final Map<OsFamily, Map<String, String>> osVersionMap;

   @Inject
   public VirtualMachineToImage(Map<VirtualMachinePowerState, Status> toPortableImageStatus, Map<OsFamily, Map<String, String>> osVersionMap) {
      this.toPortableImageStatus = checkNotNull(toPortableImageStatus, "toPortableImageStatus");
      this.osVersionMap = checkNotNull(osVersionMap, "osVersionMap");
   }

   @Override
   public Image apply(@Nullable VirtualMachine from) {      
      if (from == null)
         return null;
      String annotation =  from.getConfig().getAnnotation();
      if(Strings.isNullOrEmpty(annotation)) {
          String msg = String.format("Please provide an OS annotation on 'Annotations' of vm %s (in the format OSfamily-version, like ubuntu-12.04)", from.getName());
          logger.error(msg);
          throw new RuntimeException(msg);
      }
      OsFamily family = parseOsFamilyOrUnrecognized(annotation);
      String version = parseVersionOrReturnEmptyString(family, annotation, osVersionMap);
      OperatingSystem os = OperatingSystem.builder().description(annotation).family(family)
               .version(version).is64Bit(true).arch("x86").build();
      
      return new ImageBuilder()
                  .id(from.getName())
                  .name(from.getName())
                  .description(from.getName())
                  .operatingSystem(os).status(toPortableImageStatus.get(from.getRuntime().getPowerState()))
               .build();
   }

}
