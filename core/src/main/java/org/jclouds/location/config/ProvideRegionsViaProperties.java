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

package org.jclouds.location.config;

import static org.jclouds.location.reference.LocationConstants.PROPERTY_REGIONS;

import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.location.Region;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class ProvideRegionsViaProperties implements javax.inject.Provider<Set<String>> {

   private final Set<String> regions;

   @Inject
   ProvideRegionsViaProperties(@Named(PROPERTY_REGIONS) String regions) {
      this.regions = ImmutableSet.copyOf(Splitter.on(',').split(regions));
   }

   @Singleton
   @Region
   @Override
   public Set<String> get() {
      return regions;
   }

}