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
package org.jclouds.ec2.compute.suppliers;

import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.ec2.compute.domain.RegionAndName;
import org.jclouds.compute.domain.Image;

import com.google.common.base.Supplier;
import com.google.common.collect.Sets;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class EC2ImageSupplier implements Supplier<Set<? extends Image>> {
   private final Supplier<Map<RegionAndName, ? extends Image>> map;

   @Inject
   EC2ImageSupplier(Supplier<Map<RegionAndName, ? extends Image>> map) {
      this.map = map;
   }

   @Override
   public Set<? extends Image> get() {
      return Sets.newLinkedHashSet(map.get().values());
   }

}
