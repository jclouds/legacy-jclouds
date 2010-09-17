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

import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.internal.HardwareImpl;
import org.jclouds.compute.domain.internal.VolumeImpl;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class GoGridHardwareSupplier implements Supplier<Set<? extends Hardware>> {

   public static final HardwareImpl H8192 = new HardwareImpl("5", "5", "5", null, null, ImmutableMap
            .<String, String> of(), ImmutableList.of(new Processor(8, 1.0)), 8192, ImmutableList.of(new VolumeImpl(
            480.0f, true, true)), any());
   public static final HardwareImpl H4096 = new HardwareImpl("4", "4", "4", null, null, ImmutableMap
            .<String, String> of(), ImmutableList.of(new Processor(4, 1.0)), 4096, ImmutableList.of(new VolumeImpl(
            240.0f, true, true)), any());
   public static final HardwareImpl H2048 = new HardwareImpl("3", "3", "3", null, null, ImmutableMap
            .<String, String> of(), ImmutableList.of(new Processor(2, 1.0)), 2048, ImmutableList.of(new VolumeImpl(
            120.0f, true, true)), any());
   public static final HardwareImpl H1024 = new HardwareImpl("2", "2", "2", null, null, ImmutableMap
            .<String, String> of(), ImmutableList.of(new Processor(1, 1.0)), 1024, ImmutableList.of(new VolumeImpl(
            60.0f, true, true)), any());
   public static final HardwareImpl H512 = new HardwareImpl("1", "1", "1", null, null, ImmutableMap
            .<String, String> of(), ImmutableList.of(new Processor(0.5, 1.0)), 512, ImmutableList.of(new VolumeImpl(
            30.0f, true, true)), any());

   public static final ImmutableSet<HardwareImpl> H_ALL = ImmutableSet.of(H512, H1024, H2048, H4096, H8192);

   @Override
   public Set<? extends Hardware> get() {
      return H_ALL;
   }
}