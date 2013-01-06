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
package org.jclouds.reflect;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.reflect.Invokable;
import com.google.common.reflect.Reflection;

/**
 * 
 * @author Adrian Cole
 */
@Test
public class AbstractInvocationHandlerTest {

   @Test(expectedExceptions = UnsupportedOperationException.class)
   public void testNullArgsAreAllowedAndUnmodifiable() throws IOException {
      Reflection.newProxy(Appendable.class, new AbstractInvocationHandler() {
         protected Object handleInvocation(Object proxy, Invokable<?, ?> method, List<Object> args) throws Throwable {
            assertNotNull(args);
            assertNull(args.get(0));
            args.add("foo");
            throw new AssertionError("shouldn't be able to mutate the list!");
         }
      }).append(null);
   }

   @Test(expectedExceptions = UnsupportedOperationException.class)
   public void testImmutableListWhenArgsAreNotNull() throws IOException {
      Reflection.newProxy(Appendable.class, new AbstractInvocationHandler() {
         protected Object handleInvocation(Object proxy, Invokable<?, ?> method, List<Object> args) throws Throwable {
            assertNotNull(args);
            assertTrue(args instanceof ImmutableList);
            assertEquals(args.get(0), "foo");
            args.add("bar");
            throw new AssertionError("shouldn't be able to mutate the list!");
         }
      }).append("foo");
   }
}
