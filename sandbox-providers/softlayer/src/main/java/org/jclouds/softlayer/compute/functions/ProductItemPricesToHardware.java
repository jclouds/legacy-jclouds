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
package org.jclouds.softlayer.compute.functions;

import java.util.Set;

import javax.inject.Singleton;

import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.softlayer.domain.ProductItemPrice;

import com.google.common.base.Function;

/**
 * @author Adrian Cole
 */
@Singleton
public class ProductItemPricesToHardware implements Function<Set<ProductItemPrice>, Hardware> {

   @Override
   public Hardware apply(Set<ProductItemPrice> from) {
      HardwareBuilder builder = new HardwareBuilder();
//      builder.ids(from.id + "");
//      builder.name(from.name);
//      builder.processors(ImmutableList.of(new Processor(from.cores, 1.0)));
//      builder.ram(from.ram);
//      builder.volumes(ImmutableList.<Volume> of(new VolumeImpl(from.disk, true, false)));
      return builder.build();
   }

}
