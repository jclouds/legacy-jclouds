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
package org.jclouds.ec2.compute.functions;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Maps.uniqueIndex;

import java.util.Map;

import javax.inject.Singleton;

import org.jclouds.compute.domain.Image;
import org.jclouds.ec2.compute.domain.RegionAndName;

import com.google.common.base.Function;

/**
 * @author Adrian Cole
 */
@Singleton
public class ImagesToRegionAndIdMap implements Function<Iterable<? extends Image>, Map<RegionAndName, ? extends Image>> {

   public static Map<RegionAndName, ? extends Image> imagesToMap(Iterable<? extends Image> input) {
      return new ImagesToRegionAndIdMap().apply(input);
   }

   @Override
   public Map<RegionAndName, ? extends Image> apply(Iterable<? extends Image> input) {
      return uniqueIndex(input, new Function<Image, RegionAndName>() {

         @Override
         public RegionAndName apply(Image from) {
            checkState(from.getLocation() != null,
                     "in ec2, image locations cannot be null; typically, they are Region-scoped");
            return new RegionAndName(from.getLocation().getId(), from.getProviderId());
         }

      });
   }

}
