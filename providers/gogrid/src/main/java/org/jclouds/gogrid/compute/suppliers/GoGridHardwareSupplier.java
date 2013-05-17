/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.gogrid.compute.suppliers;

import java.util.Set;

import javax.inject.Singleton;

import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.domain.internal.VolumeImpl;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class GoGridHardwareSupplier implements Supplier<Set<? extends Hardware>> {
   public static final Hardware H16384 = new HardwareBuilder().ids("6").ram(16384)
         .processors(ImmutableList.of(new Processor(16, 1.0)))
         .volumes(ImmutableList.<Volume> of(new VolumeImpl(800.0f, true, true))).build();
   public static final Hardware H8192 = new HardwareBuilder().ids("5").ram(8192)
         .processors(ImmutableList.of(new Processor(8, 1.0)))
         .volumes(ImmutableList.<Volume> of(new VolumeImpl(400.0f, true, true))).build();
   public static final Hardware H4096 = new HardwareBuilder().ids("4").ram(4096)
         .processors(ImmutableList.of(new Processor(4, 1.0)))
         .volumes(ImmutableList.<Volume> of(new VolumeImpl(200.0f, true, true))).build();
   public static final Hardware H2048 = new HardwareBuilder().ids("3").ram(2048)
         .processors(ImmutableList.of(new Processor(2, 1.0)))
         .volumes(ImmutableList.<Volume> of(new VolumeImpl(100.0f, true, true))).build();
   public static final Hardware H1024 = new HardwareBuilder().ids("2").ram(1024)
         .processors(ImmutableList.of(new Processor(1, 1.0)))
         .volumes(ImmutableList.<Volume> of(new VolumeImpl(50.0f, true, true))).build();
   public static final Hardware H512 = new HardwareBuilder().ids("1").ram(512)
         .processors(ImmutableList.of(new Processor(0.5, 1.0)))
         .volumes(ImmutableList.<Volume> of(new VolumeImpl(25.0f, true, true))).build();

   public static final ImmutableSet<Hardware> H_ALL = ImmutableSet.of(H512, H1024, H2048, H4096, H8192, H16384);

   @Override
   public Set<? extends Hardware> get() {
      return H_ALL;
   }
}
