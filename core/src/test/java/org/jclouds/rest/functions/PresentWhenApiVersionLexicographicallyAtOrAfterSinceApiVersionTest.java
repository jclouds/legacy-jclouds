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
package org.jclouds.rest.functions;

import static com.google.common.base.Throwables.propagate;
import static org.jclouds.reflect.Reflection2.method;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.jclouds.reflect.Invocation;
import org.jclouds.reflect.InvocationSuccess;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.rest.annotations.SinceApiVersion;
import org.jclouds.rest.functions.PresentWhenApiVersionLexicographicallyAtOrAfterSinceApiVersion.Loader;
import org.testng.annotations.Test;

import com.google.common.base.Optional;
import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableList;

/**
 * Allows you to use simple api version comparison to determine if a feature is
 * available.
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class PresentWhenApiVersionLexicographicallyAtOrAfterSinceApiVersionTest {

   // feature present in base api
   static interface KeyPairAsyncApi {

   }

   @SinceApiVersion("2010-08-31")
   static interface TagAsyncApi {

   }

   @SinceApiVersion("2011-01-01")
   static interface VpcAsyncApi {

   }

   static interface EC2AsyncApi {

      @Delegate
      Optional<TagAsyncApi> getTagApiForRegion(String region);

      @Delegate
      Optional<KeyPairAsyncApi> getKeyPairApiForRegion(String region);

      @Delegate
      Optional<VpcAsyncApi> getVpcApiForRegion(String region);

   }

   public void testPresentWhenSinceApiVersionUnset() {
      ImplicitOptionalConverter fn = forApiVersion("2011-07-15");
      assertEquals(fn.apply(getKeyPairApi()), Optional.of("present"));
      assertEquals(fn.apply(getFloatingIPApi()), Optional.of("present"));
      assertEquals(fn.apply(getVpcApi()), Optional.of("present"));
   }

   public void testPresentWhenSinceApiVersionUnsetOrEqualToApiVersion() {
      ImplicitOptionalConverter fn = forApiVersion("2011-01-01");
      assertEquals(fn.apply(getKeyPairApi()), Optional.of("present"));
      assertEquals(fn.apply(getFloatingIPApi()), Optional.of("present"));
      assertEquals(fn.apply(getVpcApi()), Optional.of("present"));
   }

   public void testNotPresentWhenSinceApiVersionSetAndGreaterThanApiVersion() throws SecurityException,
         NoSuchMethodException {
      ImplicitOptionalConverter fn = forApiVersion("2006-06-26");
      assertEquals(fn.apply(getKeyPairApi()), Optional.of("present"));
      assertEquals(fn.apply(getFloatingIPApi()), Optional.absent());
      assertEquals(fn.apply(getVpcApi()), Optional.absent());
   }

   private ImplicitOptionalConverter forApiVersion(String apiVersion) {
      return new PresentWhenApiVersionLexicographicallyAtOrAfterSinceApiVersion(apiVersion);
   }

   public void testLoaderPresentWhenSinceApiVersionUnset() {
      Loader fn = new Loader("2011-07-15");
      assertEquals(fn.load(getKeyPairApi()), Optional.of("present"));
      assertEquals(fn.load(getFloatingIPApi()), Optional.of("present"));
      assertEquals(fn.load(getVpcApi()), Optional.of("present"));
   }

   public void testLoaderPresentWhenSinceApiVersionUnsetOrEqualToApiVersion() {
      Loader fn = new Loader("2011-01-01");
      assertEquals(fn.load(getKeyPairApi()), Optional.of("present"));
      assertEquals(fn.load(getFloatingIPApi()), Optional.of("present"));
      assertEquals(fn.load(getVpcApi()), Optional.of("present"));
   }

   public void testLoaderNotPresentWhenSinceApiVersionSetAndGreaterThanApiVersion() throws SecurityException,
         NoSuchMethodException {
      Loader fn = new Loader("2006-06-26");
      assertEquals(fn.load(getKeyPairApi()), Optional.of("present"));
      assertEquals(fn.load(getFloatingIPApi()), Optional.absent());
      assertEquals(fn.load(getVpcApi()), Optional.absent());
   }

   public void testCacheIsFasterWhenNoAnnotationPresent() {
      InvocationSuccess keyPairApi = getKeyPairApi();
      ImplicitOptionalConverter fn = forApiVersion("2011-07-15");
      Stopwatch watch = new Stopwatch().start();
      fn.apply(keyPairApi);
      long first = watch.stop().elapsed(TimeUnit.MICROSECONDS);
      watch.reset().start();
      fn.apply(keyPairApi);
      long cached = watch.stop().elapsed(TimeUnit.MICROSECONDS);
      assertTrue(cached < first, String.format("cached [%s] should be less than initial [%s]", cached, first));
      Logger.getAnonymousLogger().info(
            "lookup cache saved " + (first - cached) + " microseconds when no annotation present");
   }

   public void testCacheIsFasterWhenAnnotationPresent() {
      InvocationSuccess floatingIpApi = getKeyPairApi();
      ImplicitOptionalConverter fn = forApiVersion("2011-07-15");
      Stopwatch watch = new Stopwatch().start();
      fn.apply(floatingIpApi);
      long first = watch.stop().elapsed(TimeUnit.MICROSECONDS);
      watch.reset().start();
      fn.apply(floatingIpApi);
      long cached = watch.stop().elapsed(TimeUnit.MICROSECONDS);
      assertTrue(cached < first, String.format("cached [%s] should be less than initial [%s]", cached, first));
      Logger.getAnonymousLogger().info(
            "lookup cache saved " + (first - cached) + " microseconds when annotation present");

   }

   InvocationSuccess getFloatingIPApi() {
      return getApi("Tag", TagAsyncApi.class);
   }

   InvocationSuccess getKeyPairApi() {
      return getApi("KeyPair", KeyPairAsyncApi.class);
   }

   InvocationSuccess getVpcApi() {
      return getApi("Vpc", VpcAsyncApi.class);
   }

   InvocationSuccess getApi(String name, Class<?> target) {
      try {
         return InvocationSuccess.create(
               Invocation.create(method(EC2AsyncApi.class, "get" + name + "ApiForRegion", String.class),
                     ImmutableList.<Object> of("region")), "present");
      } catch (Exception e) {
         throw propagate(e);
      }
   }

}
