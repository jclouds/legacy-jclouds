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
package org.jclouds.eucalyptus.config;

import static org.testng.Assert.assertEquals;

import java.net.URI;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.jclouds.eucalyptus.internal.BaseEucalyptusExpectTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.location.Region;
import org.jclouds.location.Zone;
import org.jclouds.location.functions.RegionToEndpointOrProviderIfNull;
import org.jclouds.location.functions.ZoneToEndpoint;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "EucalyptusRestClientModuleExpectTest")
public class EucalyptusRestClientModuleExpectTest extends BaseEucalyptusExpectTest<Injector> {
   private Injector injector;

   public EucalyptusRestClientModuleExpectTest() {
      Builder<HttpRequest, HttpResponse> builder = ImmutableMap.<HttpRequest, HttpResponse> builder();
      builder.put(describeRegionsRequest, describeRegionsResponse);
      builder.put(describeAZRequest, describeAZResponse);

      injector = requestsSendResponses(builder.build());
   }

   public void testLocationIdAndURIBindings() {

      assertEquals(injector.getInstance(Key.get(new TypeLiteral<Supplier<Set<String>>>() {
      }, Region.class)).get(), ImmutableSet.<String> of("eucalyptus"));

      assertEquals(injector.getInstance(Key.get(new TypeLiteral<Supplier<Set<String>>>() {
      }, Zone.class)).get(), ImmutableSet.<String> of("partner01"));

      Map<String, Supplier<URI>> regionToURISupplier = injector.getInstance(
            Key.get(new TypeLiteral<Supplier<Map<String, Supplier<URI>>>>() {
            }, Region.class)).get();

      assertEquals(regionToURISupplier.get("eucalyptus").get(), URI.create("http://eucalyptus.partner.eucalyptus.com:8773/services/Eucalyptus"));

      Map<String, Supplier<Set<String>>> regionToZoneIdSupplier = injector.getInstance(
            Key.get(new TypeLiteral<Supplier<Map<String, Supplier<Set<String>>>>>() {
            }, Zone.class)).get();

      assertEquals(regionToZoneIdSupplier.get("eucalyptus").get(), ImmutableSet.of("partner01"));

      Map<String, Supplier<URI>> zoneToURISupplier = injector.getInstance(
            Key.get(new TypeLiteral<Supplier<Map<String, Supplier<URI>>>>() {
            }, Zone.class)).get();

      assertEquals(zoneToURISupplier.get("partner01").get(), URI.create("http://eucalyptus.partner.eucalyptus.com:8773/services/Eucalyptus"));

   }

   public void testZoneToEndpoint() {
      assertEquals(injector.getInstance(ZoneToEndpoint.class).apply("partner01"),
            URI.create("http://eucalyptus.partner.eucalyptus.com:8773/services/Eucalyptus"));

   }
   
   public void testRegionToEndpointOrProviderIfNull() {
      assertEquals(injector.getInstance(RegionToEndpointOrProviderIfNull.class).apply("eucalyptus"),
            URI.create("http://eucalyptus.partner.eucalyptus.com:8773/services/Eucalyptus"));
   }
   
   @Override
   public Injector createClient(Function<HttpRequest, HttpResponse> fn, Module module, Properties props) {
      return createInjector(fn, module, props);
   }

}
