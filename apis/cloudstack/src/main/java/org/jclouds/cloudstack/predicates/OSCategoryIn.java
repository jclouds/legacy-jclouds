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
package org.jclouds.cloudstack.predicates;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.cloudstack.CloudStackClient;
import org.jclouds.cloudstack.domain.OSType;
import org.jclouds.cloudstack.domain.Template;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Maps;

/**
 * 
 * 
 * @author Adrian Cole
 */
@Singleton
public class OSCategoryIn implements Function<Set<String>, Predicate<Template>> {
   private final Supplier<Map<String, String>> categoriesSupplier;
   private final Supplier<Set<OSType>> osTypesSupplier;

   @Inject
   public OSCategoryIn(CloudStackClient client) {
      this(Suppliers.ofInstance(checkNotNull(client, "client").getGuestOSClient().listOSCategories()), Suppliers
            .ofInstance(client.getGuestOSClient().listOSTypes()));
   }

   public OSCategoryIn(Supplier<Map<String, String>> categoriesSupplier, Supplier<Set<OSType>> osTypesSupplier) {
      this.categoriesSupplier = checkNotNull(categoriesSupplier, "categoriesSupplier");
      this.osTypesSupplier = checkNotNull(osTypesSupplier, "osTypesSupplier");
   }

   @Override
   public Predicate<Template> apply(final Set<String> acceptableCategories) {
      final Map<String, String> categories = categoriesSupplier.get();
      final Set<String> acceptableOSTypeIds = Maps.filterValues(
            Maps.transformValues(Maps.uniqueIndex(osTypesSupplier.get(), new Function<OSType, String>() {

               @Override
               public String apply(OSType input) {
                  return input.getId();
               }

            }), new Function<OSType, String>() {

               @Override
               public String apply(OSType input) {
                  return categories.get(input.getOSCategoryId());
               }

            }), Predicates.in(acceptableCategories)).keySet();
      return new Predicate<Template>() {

         @Override
         public boolean apply(Template input) {
            return Predicates.in(acceptableOSTypeIds).apply(input.getOSTypeId());
         }

         @Override
         public String toString() {
            return "OSCategoryIn(" + acceptableCategories + ")";
         }
      };
   }
}
