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

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.domain.internal.VolumeImpl;
import org.jclouds.softlayer.domain.ProductItem;
import org.jclouds.softlayer.domain.ProductItemPrice;

import javax.inject.Singleton;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.regex.Pattern;

import static org.jclouds.softlayer.predicates.ProductItemPredicates.categoryCode;
import static org.jclouds.softlayer.predicates.ProductItemPredicates.units;

/**
 * Converts a set of ProductItems to Hardware.
 * All cores have a speed of 2.0Ghz
 * The Hardware Id will be a comma separated list containing the price ids:
 * cpus,ram,volume
 *
 * @author Jason King
 */
@Singleton
public class ProductItemsToHardware implements Function<Set<ProductItem>, Hardware> {

   private static final double CORE_SPEED = 2.0;
   private static final Pattern SAN_REGEX = Pattern.compile(".*GB \\(SAN\\).*");

   @Override
   public Hardware apply(Set<ProductItem> from) {

      ProductItem coresItem = getCoresItem(from);
      ProductItem ramItem = getRamItem(from);
      ProductItem volumeItem = bootVolume().apply(from);

      final String hardwareId = hardwareId().apply(ImmutableList.of(coresItem, ramItem, volumeItem));
      final double cores = getCores(coresItem);
      final int ram = getRam(ramItem);
      final float volumeSize = ProductItems.capacity().apply(volumeItem);

      return new HardwareBuilder()
                  .id(hardwareId)
                  .processors(ImmutableList.of(new Processor(cores, CORE_SPEED)))
                  .ram(ram)
                  .volumes(ImmutableList.<Volume> of(new VolumeImpl(volumeSize, true, false)))
                  .build();
   }

   /**
    * Generates a hardwareId based on the priceId's of the items in the list
    * @return comma separated list of priceid's
    */
   public static Function<List<ProductItem>,String> hardwareId() {
      return new Function<List<ProductItem>,String>() {
         @Override
         public String apply(List<ProductItem> productItems) {
            StringBuilder builder = new StringBuilder();
            for(ProductItem item:productItems) {
               ProductItemPrice price = ProductItems.price().apply(item);
               builder.append(price.getId())
                      .append(",");
            }
            return builder.toString().substring(0,builder.lastIndexOf(","));
         }
      };
   }

    /**
    * Finds an item that is usable as the hardware volume (is a SAN)
    * @return The product item
    * @throws java.util.NoSuchElementException if the item cannot be found
    */
   public static Function<Set<ProductItem>,ProductItem> bootVolume() {
      return new Function<Set<ProductItem>,ProductItem>() {
         @Override
         public ProductItem apply(Set<ProductItem> productItems) {
            for(ProductItem item: productItems) {
               String description = item.getDescription();
               if (SAN_REGEX.matcher(description).matches()) {
                  return item;
               }
            }
            throw new NoSuchElementException("cannot find suitable boot volume item");
         }
      };
   }

   private ProductItem getCoresItem(Set<ProductItem> from) {
      Iterable<ProductItem> cpuItems = Iterables.filter(from, units("PRIVATE_CORE"));
      Map<Float, ProductItem> coresToProductItem = Maps.uniqueIndex(cpuItems, ProductItems.capacity());
      assert coresToProductItem.size() == 1 : "Must have 1 cpu product item:"+coresToProductItem;

     return coresToProductItem.entrySet().iterator().next().getValue();
   }

   private double getCores(ProductItem from) {
      return ProductItems.capacity().apply(from).doubleValue();
   }

   private ProductItem getRamItem(Set<ProductItem> from) {
      Iterable<ProductItem> ramItems = Iterables.filter(from,categoryCode("ram"));
      Map<Float, ProductItem> ramToProductItem = Maps.uniqueIndex(ramItems, ProductItems.capacity());
      assert ramToProductItem.size() == 1 : "Must have 1 ram product item:"+ramToProductItem;

      return ramToProductItem.entrySet().iterator().next().getValue();
   }

   private int getRam(ProductItem from) {
      return ProductItems.capacity().apply(from).intValue();
   }
}
