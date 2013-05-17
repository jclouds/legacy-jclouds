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
import static org.testng.Assert.fail;

import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.IterableWithMarkers;
import org.jclouds.reflect.Invocation;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.Invokable;

/**
 * @author Adrian Cole
 */
@Test(testName = "Arg0ToPagedIterableTest")
public class Arg0ToPagedIterableTest {

   private abstract static class TestArg0 extends Arg0ToPagedIterable<String, TestArg0> {
      private TestArg0(GeneratedHttpRequest in) {
         this.setContext(in);
      }
   }

   @Test
   public void testWhenNextMarkerAbsentDoesntAdvance() {
      GeneratedHttpRequest request = args(ImmutableList.of());

      TestArg0 converter = new TestArg0(request) {

         @Override
         protected Function<Object, IterableWithMarker<String>> markerToNextForArg0(Optional<Object> arg0) {
            fail();
            return null;
         }

      };

      assertEquals(converter.apply(IterableWithMarkers.from(ImmutableSet.of("foo", "bar"))).concat().toSet(),
            ImmutableSet.of("foo", "bar"));
   }

   @Test
   public void testWhenNextMarkerPresentButNoArgsMarkerToNextForArg0ParamIsAbsent() {
      GeneratedHttpRequest request = args(ImmutableList.of());
      final IterableWithMarker<String> next = IterableWithMarkers.from(ImmutableSet.of("baz"));

      TestArg0 converter = new TestArg0(request) {

         @Override
         protected Function<Object, IterableWithMarker<String>> markerToNextForArg0(Optional<Object> arg0) {
            assertEquals(arg0, Optional.absent());
            return Functions.constant(next);
         }

      };

      assertEquals(converter.apply(IterableWithMarkers.from(ImmutableSet.of("foo", "bar"), "marker")).concat().toSet(),
            ImmutableSet.of("foo", "bar", "baz"));
   }

   @Test
   public void testWhenNextMarkerPresentWithArgsMarkerToNextForArg0ParamIsPresent() {
      GeneratedHttpRequest request = args(ImmutableList.<Object> of("path"));
      final IterableWithMarker<String> next = IterableWithMarkers.from(ImmutableSet.of("baz"));

      TestArg0 converter = new TestArg0(request) {

         @Override
         protected Function<Object, IterableWithMarker<String>> markerToNextForArg0(Optional<Object> arg0) {
            assertEquals(arg0, Optional.of("path"));
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

   private abstract static class TestCallerArg0 extends Arg0ToPagedIterable.FromCaller<String, TestCallerArg0> {
      private TestCallerArg0(GeneratedHttpRequest in) {
         this.setContext(in);
      }
   }

   @Test
   public void testFromCallerWhenNextMarkerPresentButNoArgsMarkerToNextForArg0ParamIsAbsent() {
      GeneratedHttpRequest request = callerArgs(ImmutableList.of());
      final IterableWithMarker<String> next = IterableWithMarkers.from(ImmutableSet.of("baz"));

      TestCallerArg0 converter = new TestCallerArg0(request) {

         @Override
         protected Function<Object, IterableWithMarker<String>> markerToNextForArg0(Optional<Object> arg0) {
            assertEquals(arg0, Optional.absent());
            return Functions.constant(next);
         }

      };

      assertEquals(converter.apply(IterableWithMarkers.from(ImmutableSet.of("foo", "bar"), "marker")).concat().toSet(),
            ImmutableSet.of("foo", "bar", "baz"));
   }

   @Test
   public void testFromCallerWhenNextMarkerPresentWithArgsMarkerToNextForArg0ParamIsPresent() {
      GeneratedHttpRequest request = callerArgs(ImmutableList.<Object> of("path"));
      final IterableWithMarker<String> next = IterableWithMarkers.from(ImmutableSet.of("baz"));

      TestCallerArg0 converter = new TestCallerArg0(request) {

         @Override
         protected Function<Object, IterableWithMarker<String>> markerToNextForArg0(Optional<Object> arg0) {
            assertEquals(arg0, Optional.of("path"));
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
