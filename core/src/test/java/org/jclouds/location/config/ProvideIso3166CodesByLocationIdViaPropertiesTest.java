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
package org.jclouds.location.config;

import static org.testng.Assert.assertEquals;

import java.util.Map;
import java.util.Set;

import org.jclouds.location.suppliers.LocationIdToIso3166CodesSupplier;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Provides;

/**
 * 
 * @author Adrian Cole
 */
@Test(testName = "ProvideIso3166CodesByLocationIdViaPropertiesTest")
public class ProvideIso3166CodesByLocationIdViaPropertiesTest {

   public void testEmptyWhenNoLocationsBound() {
      LocationIdToIso3166CodesSupplier fn = createWithValue(ImmutableMap.<String, String> of());

      assertEquals(fn.get(), ImmutableMap.<String, Set<String>> of());
   }

   public void testEmptyWhenRegionsAndZonesBoundButNoIsoCodes() {

      LocationIdToIso3166CodesSupplier fn = createWithValue(ImmutableMap.<String, String> of(
            "jclouds.regions", "us-east", "jclouds.zones", "us-easta"));

      assertEquals(fn.get(), ImmutableMap.<String, Set<String>> of());
   }

   public void testIsoCodesWhenRegionsAndZonesBoundWithIsoCodes() {

      LocationIdToIso3166CodesSupplier fn = createWithValue(ImmutableMap.<String, String> of(
            "jclouds.regions", "us-east", "jclouds.region.us-east.iso3166-codes", "US", "jclouds.zones", "us-easta",
            "jclouds.zone.us-easta.iso3166-codes", "US-CA"));

      assertEquals(Maps.transformValues(fn.get(), Suppliers.<Set<String>> supplierFunction()), ImmutableMap
               .<String, Set<String>> of("us-east", ImmutableSet.of("US"), "us-easta", ImmutableSet.of("US-CA")));
   }

   private LocationIdToIso3166CodesSupplier createWithValue(final ImmutableMap<String, String> value) {
      LocationIdToIso3166CodesSupplier fn = Guice.createInjector(new AbstractModule() {
         @Provides
         Function<Predicate<String>, Map<String, String>> provide() {
            return new Function<Predicate<String>, Map<String, String>>() {

               @Override
               public Map<String, String> apply(Predicate<String> input) {
                  return Maps.filterKeys(value, input);
               };
            };
         }

         @Override
         protected void configure() {
         }

      }).getInstance(LocationIdToIso3166CodesSupplier.class);
      return fn;
   }

}
