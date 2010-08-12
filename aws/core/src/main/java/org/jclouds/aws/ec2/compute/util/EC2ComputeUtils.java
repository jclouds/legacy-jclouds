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

package org.jclouds.aws.ec2.compute.util;

import javax.inject.Singleton;

import org.jclouds.aws.ec2.domain.RunningInstance;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationScope;

import com.google.common.base.Function;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class EC2ComputeUtils {

   public static Function<RunningInstance, String> instanceToId = new Function<RunningInstance, String>() {
      @Override
      public String apply(RunningInstance from) {
         return from.getId();
      }
   };

   public static String getRegionFromLocationOrNull(Location location) {
      return location.getScope() == LocationScope.ZONE ? location.getParent().getId() : location
               .getId();
   }

   public static String getZoneFromLocationOrNull(Location location) {
      return location.getScope() == LocationScope.ZONE ? location.getId() : null;
   }

}