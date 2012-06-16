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
package org.jclouds.ec2.config;

import static org.testng.Assert.assertEquals;

import java.net.URI;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.jclouds.ec2.internal.BaseEC2ExpectTest;
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
@Test(groups = "unit", testName = "EC2RestClientModuleExpectTest")
public class EC2RestClientModuleExpectTest extends BaseEC2ExpectTest<Injector> {
   private Injector injector;

   public EC2RestClientModuleExpectTest() {
      Builder<HttpRequest, HttpResponse> builder = ImmutableMap.<HttpRequest, HttpResponse> builder();
      builder.put(describeRegionsRequest, describeRegionsResponse);
      builder.putAll(describeAvailabilityZonesRequestResponse);

      injector = requestsSendResponses(builder.build());
   }

   public void testLocationIdAndURIBindings() {

      assertEquals(injector.getInstance(Key.get(new TypeLiteral<Supplier<Set<String>>>() {
      }, Region.class)).get(), ImmutableSet.<String> of("sa-east-1", "ap-northeast-1", "eu-west-1", "us-east-1",
            "us-west-1", "us-west-2", "ap-southeast-1"));

      assertEquals(injector.getInstance(Key.get(new TypeLiteral<Supplier<Set<String>>>() {
      }, Zone.class)).get(), ImmutableSet.<String> of("sa-east-1a", "sa-east-1b", "ap-northeast-1a", "ap-northeast-1b",
            "eu-west-1a", "eu-west-1b", "eu-west-1c", "us-east-1a", "us-east-1b", "us-east-1c", "us-east-1d",
            "us-east-1e", "us-west-1a", "us-west-1b", "us-west-1c", "us-west-2a", "us-west-2b", "us-west-2c",
            "ap-southeast-1a", "ap-southeast-1b"));

      Map<String, Supplier<URI>> regionToURISupplier = injector.getInstance(
            Key.get(new TypeLiteral<Supplier<Map<String, Supplier<URI>>>>() {
            }, Region.class)).get();

      assertEquals(regionToURISupplier.get("sa-east-1").get(), URI.create("https://ec2.sa-east-1.amazonaws.com"));
      assertEquals(regionToURISupplier.get("ap-northeast-1").get(),
            URI.create("https://ec2.ap-northeast-1.amazonaws.com"));
      assertEquals(regionToURISupplier.get("eu-west-1").get(), URI.create("https://ec2.eu-west-1.amazonaws.com"));
      assertEquals(regionToURISupplier.get("us-east-1").get(), URI.create("https://ec2.us-east-1.amazonaws.com"));
      assertEquals(regionToURISupplier.get("us-west-1").get(), URI.create("https://ec2.us-west-1.amazonaws.com"));
      assertEquals(regionToURISupplier.get("us-west-2").get(), URI.create("https://ec2.us-west-2.amazonaws.com"));
      assertEquals(regionToURISupplier.get("ap-southeast-1").get(),
            URI.create("https://ec2.ap-southeast-1.amazonaws.com"));

      Map<String, Supplier<Set<String>>> regionToZoneIdSupplier = injector.getInstance(
            Key.get(new TypeLiteral<Supplier<Map<String, Supplier<Set<String>>>>>() {
            }, Zone.class)).get();

      assertEquals(regionToZoneIdSupplier.get("sa-east-1").get(), ImmutableSet.of("sa-east-1a", "sa-east-1b"));
      assertEquals(regionToZoneIdSupplier.get("ap-northeast-1").get(),
            ImmutableSet.of("ap-northeast-1a", "ap-northeast-1b"));
      assertEquals(regionToZoneIdSupplier.get("eu-west-1").get(),
            ImmutableSet.of("eu-west-1a", "eu-west-1b", "eu-west-1c"));
      assertEquals(regionToZoneIdSupplier.get("us-east-1").get(),
            ImmutableSet.of("us-east-1a", "us-east-1b", "us-east-1c", "us-east-1d", "us-east-1e"));
      assertEquals(regionToZoneIdSupplier.get("us-west-1").get(),
            ImmutableSet.of("us-west-1a", "us-west-1b", "us-west-1c"));
      assertEquals(regionToZoneIdSupplier.get("us-west-2").get(),
            ImmutableSet.of("us-west-2a", "us-west-2b", "us-west-2c"));
      assertEquals(regionToZoneIdSupplier.get("ap-southeast-1").get(),
            ImmutableSet.of("ap-southeast-1a", "ap-southeast-1b"));

      Map<String, Supplier<URI>> zoneToURISupplier = injector.getInstance(
            Key.get(new TypeLiteral<Supplier<Map<String, Supplier<URI>>>>() {
            }, Zone.class)).get();

      assertEquals(zoneToURISupplier.get("sa-east-1a").get(), URI.create("https://ec2.sa-east-1.amazonaws.com"));

      assertEquals(zoneToURISupplier.get("ap-northeast-1a").get(),
            URI.create("https://ec2.ap-northeast-1.amazonaws.com"));

      assertEquals(zoneToURISupplier.get("eu-west-1a").get(), URI.create("https://ec2.eu-west-1.amazonaws.com"));

      assertEquals(zoneToURISupplier.get("us-east-1a").get(), URI.create("https://ec2.us-east-1.amazonaws.com"));

      assertEquals(zoneToURISupplier.get("us-west-1a").get(), URI.create("https://ec2.us-west-1.amazonaws.com"));

      assertEquals(zoneToURISupplier.get("us-west-2a").get(), URI.create("https://ec2.us-west-2.amazonaws.com"));

      assertEquals(zoneToURISupplier.get("ap-southeast-1a").get(),
            URI.create("https://ec2.ap-southeast-1.amazonaws.com"));

   }

   public void testZoneToEndpoint() {
      assertEquals(injector.getInstance(ZoneToEndpoint.class).apply("us-west-2a"),
            URI.create("https://ec2.us-west-2.amazonaws.com"));
   }
   
   public void testRegionToEndpointOrProviderIfNull() {
      assertEquals(injector.getInstance(RegionToEndpointOrProviderIfNull.class).apply("us-west-2"),
            URI.create("https://ec2.us-west-2.amazonaws.com"));
   }
   
   @Override
   public Injector createClient(Function<HttpRequest, HttpResponse> fn, Module module, Properties props) {
      return createInjector(fn, module, props);
   }

}
