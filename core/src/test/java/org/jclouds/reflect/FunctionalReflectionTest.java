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
package org.jclouds.reflect;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.io.Closeable;
import java.io.IOException;
import java.util.Set;

import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;

/**
 * 
 * @author Adrian Cole
 */
@Test(singleThreaded = true)
public class FunctionalReflectionTest {
   
   @SuppressWarnings("unchecked")
   @Test(expectedExceptions = UnsupportedOperationException.class)
   public void testNullArgsAreAllowedAndUnmodifiable() {
      final Function<Invocation, Object> test = new Function<Invocation, Object>() {
         public Object apply(Invocation e) {
            assertNotNull(e.getArgs());
            assertNull(e.getArgs().get(0));
            e.getArgs().add("foo");
            throw new AssertionError("shouldn't be able to mutate the list!");
         }
      };
      FunctionalReflection.newProxy(Set.class, test).add(null);
   }

   @SuppressWarnings("unchecked")
   @Test(expectedExceptions = UnsupportedOperationException.class)
   public void testImmutableListWhenArgsAreNotNull() {
      final Function<Invocation, Object> test = new Function<Invocation, Object>() {
         public Object apply(Invocation e) {
            assertNotNull(e.getArgs());
            assertTrue(e.getArgs() instanceof ImmutableList);
            assertEquals(e.getArgs().get(0), "foo");
            e.getArgs().add("bar");
            throw new AssertionError("shouldn't be able to mutate the list!");
         }
      };
      FunctionalReflection.newProxy(Set.class, test).add("foo");
   }

   @Test(expectedExceptions = IOException.class, expectedExceptionsMessageRegExp = "io")
   public void testPropagatesDeclaredException() throws IOException {
      final Function<Invocation, Object> test = new Function<Invocation, Object>() {
         public Object apply(Invocation e) {
            throw new RuntimeException(new IOException("io"));
         }
      };
      Closeable closeable = FunctionalReflection.newProxy(Closeable.class, test);
      closeable.close();
   }

   /**
    * for example, someone could have enabled assertions, or there could be a recoverable ServiceConfigurationError
    */
   @Test(expectedExceptions = AssertionError.class, expectedExceptionsMessageRegExp = "assert")
   public void testPropagatesError() throws IOException {
      final Function<Invocation, Object> test = new Function<Invocation, Object>() {
         public Object apply(Invocation e) {
            throw new AssertionError("assert");
         }
      };
      Closeable closeable = FunctionalReflection.newProxy(Closeable.class, test);
      closeable.close();
   }

   public void testToStringEqualsFunction() {
      final Function<Invocation, Object> test = new Function<Invocation, Object>() {
         public Object apply(Invocation e) {
            return "foo";
         }

         public String toString() {
            return "bar";
         }
      };
      Closeable closeable = FunctionalReflection.newProxy(Closeable.class, test);
      assertEquals(closeable.toString(), "bar");
   }

   public void testHashCodeDifferentiatesOnInterface() {
      final Function<Invocation, Object> test = new Function<Invocation, Object>() {
         public Object apply(Invocation e) {
            return null;
         }

         public int hashCode() {
            return 1111;
         }
      };
      Appendable appendable1 = FunctionalReflection.newProxy(Appendable.class, test);
      Appendable appendable2 = FunctionalReflection.newProxy(Appendable.class, test);
      assertEquals(appendable1.hashCode(), appendable2.hashCode());

      Closeable closeable = FunctionalReflection.newProxy(Closeable.class, test);
      assertNotEquals(appendable1.hashCode(), closeable.hashCode());
   }
}
