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
package org.jclouds.collect.internal;

import static com.google.common.base.Throwables.propagate;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.util.List;

import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.IterableWithMarkers;
import org.jclouds.reflect.Invocation;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.Invokable;

/**
 * @author Ignasi Barrera
 */
@Test(testName = "ArgsToPagedIterableTest")
public class ArgsToPagedIterableTest {

   private abstract static class TestArgs extends ArgsToPagedIterable<String, TestArgs> {
      private TestArgs(GeneratedHttpRequest in) {
         this.setContext(in);
      }
   }

   @Test
   public void testWhenNextMarkerAbsentDoesntAdvance() {
      GeneratedHttpRequest request = args(ImmutableList.of());

      TestArgs converter = new TestArgs(request) {

         @Override
         protected Function<Object, IterableWithMarker<String>> markerToNextForArgs(List<Object> args) {
            fail("The Iterable should not advance");
            return null;
         }

      };

      assertEquals(converter.apply(IterableWithMarkers.from(ImmutableSet.of("foo", "bar"))).concat().toSet(),
            ImmutableSet.of("foo", "bar"));
   }

   @Test
   public void testWhenNextMarkerPresentButNoArgsMarkerToNextForArgsParamIsAbsent() {
      GeneratedHttpRequest request = args(ImmutableList.of());
      final IterableWithMarker<String> next = IterableWithMarkers.from(ImmutableSet.of("baz"));

      TestArgs converter = new TestArgs(request) {

         @Override
         protected Function<Object, IterableWithMarker<String>> markerToNextForArgs(List<Object> args) {
            assertTrue(args.isEmpty());
            return Functions.constant(next);
         }

      };

      assertEquals(converter.apply(IterableWithMarkers.from(ImmutableSet.of("foo", "bar"), "marker")).concat().toSet(),
            ImmutableSet.of("foo", "bar", "baz"));
   }

   @Test
   public void testWhenNextMarkerPresentWithArgsMarkerToNextForArgsParamIsPresent() {
      GeneratedHttpRequest request = args(ImmutableList.<Object> of("path"));
      final IterableWithMarker<String> next = IterableWithMarkers.from(ImmutableSet.of("baz"));

      TestArgs converter = new TestArgs(request) {

         @Override
         protected Function<Object, IterableWithMarker<String>> markerToNextForArgs(List<Object> args) {
            assertEquals(args.get(0), "path");
            return Functions.constant(next);
         }

      };

      assertEquals(converter.apply(IterableWithMarkers.from(ImmutableSet.of("foo", "bar"), "marker")).concat().toSet(),
            ImmutableSet.of("foo", "bar", "baz"));
   }

   private GeneratedHttpRequest args(ImmutableList<Object> args) {
      try {
         return GeneratedHttpRequest.builder().method("GET").endpoint("http://localhost")
               .invocation(Invocation.create(Invokable.from(String.class.getMethod("toString")), args)).build();
      } catch (Exception e) {
         throw propagate(e);
      }
   }

   private abstract static class TestCallerArgs extends ArgsToPagedIterable.FromCaller<String, TestCallerArgs> {
      private TestCallerArgs(GeneratedHttpRequest in) {
         this.setContext(in);
      }
   }

   @Test
   public void testFromCallerWhenNextMarkerPresentButNoArgsMarkerToNextForArgsParamIsAbsent() {
      GeneratedHttpRequest request = callerArgs(ImmutableList.of());
      final IterableWithMarker<String> next = IterableWithMarkers.from(ImmutableSet.of("baz"));

      TestCallerArgs converter = new TestCallerArgs(request) {

         @Override
         protected Function<Object, IterableWithMarker<String>> markerToNextForArgs(List<Object> args) {
            assertTrue(args.isEmpty());
            return Functions.constant(next);
         }

      };

      assertEquals(converter.apply(IterableWithMarkers.from(ImmutableSet.of("foo", "bar"), "marker")).concat().toSet(),
            ImmutableSet.of("foo", "bar", "baz"));
   }

   @Test
   public void testFromCallerWhenNextMarkerPresentWithArgsMarkerToNextForArgsParamIsPresent() {
      GeneratedHttpRequest request = callerArgs(ImmutableList.<Object> of("path"));
      final IterableWithMarker<String> next = IterableWithMarkers.from(ImmutableSet.of("baz"));

      TestCallerArgs converter = new TestCallerArgs(request) {

         @Override
         protected Function<Object, IterableWithMarker<String>> markerToNextForArgs(List<Object> args) {
            assertEquals(args.get(0), "path");
            return Functions.constant(next);
         }

      };

      assertEquals(converter.apply(IterableWithMarkers.from(ImmutableSet.of("foo", "bar"), "marker")).concat().toSet(),
            ImmutableSet.of("foo", "bar", "baz"));
   }

   private GeneratedHttpRequest callerArgs(ImmutableList<Object> args) {
      try {
         return GeneratedHttpRequest.builder().method("GET").endpoint("http://localhost")
               .invocation(Invocation.create(Invokable.from(String.class.getMethod("toString"))))
               .caller(Invocation.create(Invokable.from(String.class.getMethod("toString")), args)).build();
      } catch (Exception e) {
         throw propagate(e);
      }
   }
}
