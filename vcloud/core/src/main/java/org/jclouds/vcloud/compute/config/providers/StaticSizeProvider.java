/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.vcloud.compute.config.providers;

import java.util.Set;

import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.compute.domain.Size;
import org.jclouds.compute.domain.internal.SizeImpl;
import org.jclouds.compute.predicates.ImagePredicates;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

/**
 * @author Adrian Cole
 */
@Singleton
public class StaticSizeProvider implements Provider<Set<? extends Size>> {

   @Override
   public Set<? extends Size> get() {
      Set<Size> sizes = Sets.newHashSet();
      for (int cpus : new int[] { 1, 2, 4, 8 })
         for (int ram : new int[] { 512, 1024, 2048, 4096, 8192, 16384 }) {
            String id = String.format("cpu=%d,ram=%s,disk=%d", cpus, ram, 10);
            sizes
                  .add(new SizeImpl(id, null, id, null, null, ImmutableMap
                        .<String, String> of(), cpus, ram, 10, ImagePredicates
                        .any()));
         }
      return sizes;
   }

}