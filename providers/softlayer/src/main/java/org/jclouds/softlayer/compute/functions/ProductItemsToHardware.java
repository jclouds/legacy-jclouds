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
package org.jclouds.softlayer.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.get;
import static com.google.common.collect.Iterables.getOnlyElement;
import static org.jclouds.softlayer.predicates.ProductItemPredicates.categoryCode;
import static org.jclouds.softlayer.predicates.ProductItemPredicates.categoryCodeMatches;
import static org.jclouds.softlayer.predicates.ProductItemPredicates.matches;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.domain.internal.VolumeImpl;
import org.jclouds.softlayer.domain.ProductItem;
import org.jclouds.softlayer.domain.ProductItemPrice;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

/**
 * Converts a set of ProductItems to Hardware. All cores have a speed of 2.0Ghz The Hardware Id will
 * be a comma separated list containing the prices ids: cpus,ram,volume
 * 
 * @author Jason King
 */
@Singleton
public class ProductItemsToHardware implements Function<Iterable<ProductItem>, Hardware> {

   private static final String GUEST_DISK_CATEGORY_REGEX =  "guest_disk[0-9]";
   private static final String FIRST_GUEST_DISK = "guest_disk0";
   private static final String STORAGE_AREA_NETWORK = "SAN";

   private static final String RAM_CATEGORY = "ram";

   private static final String CPU_DESCRIPTION_REGEX = "(Private )?[0-9]+ x ([.0-9]+) GHz Core[s]?";
   private static final double DEFAULT_CORE_SPEED = 2.0;

   private final Pattern cpuDescriptionRegex;
   private final Pattern diskCategoryRegex;

   @Inject
   public ProductItemsToHardware() {
      this(Pattern.compile(CPU_DESCRIPTION_REGEX), Pattern.compile(GUEST_DISK_CATEGORY_REGEX));
   }

   public ProductItemsToHardware(Pattern cpuDescriptionRegex, Pattern diskCategoryRegex) {
      this.cpuDescriptionRegex = checkNotNull(cpuDescriptionRegex, "cpuDescriptionRegex");
      this.diskCategoryRegex = checkNotNull(diskCategoryRegex, "diskCategoryRegex");
   }

   @Override
   public Hardware apply(Iterable<ProductItem> items) {

      ProductItem coresItem = getOnlyElement(filter(items, matches(cpuDescriptionRegex)));
      ProductItem ramItem = getOnlyElement(filter(items, categoryCode(RAM_CATEGORY)));
      ProductItem volumeItem = get(filter(items, categoryCode(FIRST_GUEST_DISK)), 0);

      String hardwareId = hardwareId().apply(ImmutableList.of(coresItem, ramItem, volumeItem));
      double cores = ProductItems.capacity().apply(coresItem).doubleValue();
      Matcher cpuMatcher = cpuDescriptionRegex.matcher(coresItem.getDescription());
      double coreSpeed = (cpuMatcher.matches()) ? Double.parseDouble(cpuMatcher.group(cpuMatcher.groupCount())) : DEFAULT_CORE_SPEED;
      int ram = ProductItems.capacity().apply(ramItem).intValue();

      return new HardwareBuilder().ids(hardwareId).processors(ImmutableList.of(new Processor(cores, coreSpeed))).ram(
               ram)
               .hypervisor("XenServer")
               .volumes(
                  Iterables.transform(filter(items, categoryCodeMatches(diskCategoryRegex)),
                        new Function<ProductItem, Volume>() {
                           @Override
                           public Volume apply(ProductItem item) {
                              float volumeSize = ProductItems.capacity().apply(item);
                              return new VolumeImpl(
                                       item.getId() + "",
                                       item.getDescription().indexOf(STORAGE_AREA_NETWORK) != -1 ? Volume.Type.SAN : Volume.Type.LOCAL,
                                       volumeSize, null, categoryCode(FIRST_GUEST_DISK).apply(item), false);
                           }
                        })).build();
   }

   /**
    * Generates a hardwareId based on the priceId's of the items in the list
    *
    * @return comma separated list of priceid's
    */
   public static Function<List<ProductItem>, String> hardwareId() {
      return new Function<List<ProductItem>, String>() {
         @Override
         public String apply(List<ProductItem> productItems) {
            StringBuilder builder = new StringBuilder();
            for (ProductItem item : productItems) {
               ProductItemPrice price = ProductItems.price().apply(item);
               builder.append(price.getId()).append(",");
            }
            return builder.toString().substring(0, builder.lastIndexOf(","));
         }
      };
   }
}
