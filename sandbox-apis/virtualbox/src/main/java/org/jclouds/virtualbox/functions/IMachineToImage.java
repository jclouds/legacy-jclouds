/*
 * *
 *  * Licensed to jclouds, Inc. (jclouds) under one or more
 *  * contributor license agreements.  See the NOTICE file
 *  * distributed with this work for additional information
 *  * regarding copyright ownership.  jclouds licenses this file
 *  * to you under the Apache License, Version 2.0 (the
 *  * "License"); you may not use this file except in compliance
 *  * with the License.  You may obtain a copy of the License at
 *  *
 *  *   http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing,
 *  * software distributed under the License is distributed on an
 *  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  * KIND, either express or implied.  See the License for the
 *  * specific language governing permissions and limitations
 *  * under the License.
 *
 */

package org.jclouds.virtualbox.functions;

import com.google.common.base.Function;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.javax.annotation.Nullable;
import org.virtualbox_4_1.IGuestOSType;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.VirtualBoxManager;

import javax.inject.Inject;


public class IMachineToImage implements Function<IMachine, Image> {

   private static final String UBUNTU = "Ubuntu";

   private VirtualBoxManager virtualboxManager;

   @Inject
   public IMachineToImage(VirtualBoxManager virtualboxManager) {
      this.virtualboxManager = virtualboxManager;
   }

   @Override
   public Image apply(@Nullable IMachine from) {

      IGuestOSType guestOSType = virtualboxManager.getVBox().getGuestOSType(from.getOSTypeId());

      OsFamily family = osFamily().apply(guestOSType.getDescription());
      OperatingSystem os = OperatingSystem.builder()
              .description(guestOSType.getDescription())
              .family(family)
              .version(osVersion().apply(guestOSType.getDescription()))
              .is64Bit(guestOSType.getIs64Bit())
              .build();

      return new ImageBuilder()
              .id("" + from.getId())
              .description(from.getDescription())
              .operatingSystem(os)
              .build();
   }

   /**
    * Parses the item description to determine the OSFamily
    *
    * @return the @see OsFamily or OsFamily.UNRECOGNIZED
    */
   public static Function<String, OsFamily> osFamily() {

      return new Function<String, OsFamily>() {
         @Override
         public OsFamily apply(String osDescription) {
            if (osDescription.startsWith("linux")) return OsFamily.LINUX;
            return OsFamily.UNRECOGNIZED;
         }
      };
   }

   /**
    * Parses the item description to determine the os version
    *
    * @return the version, empty if not found
    */
   public static Function<String, String> osVersion() {
      return new Function<String, String>() {
         @Override
         public String apply(String osDescription) {
            OsFamily family = osFamily().apply(osDescription);
            if (family.equals(OsFamily.UBUNTU))
               return parseVersion(osDescription, UBUNTU);
            else
               return "";
         }
      };
   }

   private static String parseVersion(String description, String os) {
      String noOsName = description.replaceFirst(os, "").trim();
      return noOsName.split(" ")[0];
   }
}
