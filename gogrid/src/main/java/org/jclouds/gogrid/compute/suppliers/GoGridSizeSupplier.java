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

package org.jclouds.gogrid.compute.suppliers;

import static org.jclouds.compute.predicates.ImagePredicates.any;

import java.util.Set;

import javax.inject.Singleton;

import org.jclouds.compute.domain.Size;
import org.jclouds.compute.domain.internal.SizeImpl;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class GoGridSizeSupplier implements Supplier<Set<? extends Size>> {

   @Override
   public Set<? extends Size> get() {
      final Set<Size> sizes = Sets.newHashSet();

      sizes.add(new SizeImpl("1", "1", "1", null, null, ImmutableMap.<String, String> of(), 0.5, 512, 30, any()));
      sizes.add(new SizeImpl("2", "2", "2", null, null, ImmutableMap.<String, String> of(), 1, 1024, 60, any()));
      sizes.add(new SizeImpl("3", "3", "3", null, null, ImmutableMap.<String, String> of(), 2, 2048, 120, any()));
      sizes.add(new SizeImpl("4", "4", "4", null, null, ImmutableMap.<String, String> of(), 4, 4096, 240, any()));
      sizes.add(new SizeImpl("5", "5", "5", null, null, ImmutableMap.<String, String> of(), 8, 8192, 480, any()));
      return sizes;
   }
}