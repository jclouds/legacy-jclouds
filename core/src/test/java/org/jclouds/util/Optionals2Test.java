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
package org.jclouds.util;
import static org.jclouds.reflect.Reflection2.method;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import com.google.common.base.Optional;
import com.google.common.reflect.Invokable;

/**
 * @author Adrian Cole
 */
@Test
public class Optionals2Test {

   public static interface Test {
      Optional<String> getOptional();

      String getNotOptional();
   }

   public void testReturnTypeOrTypeOfOptionalWhenOptional() throws SecurityException, NoSuchMethodException {
      Invokable<?, ?> invoked = method(Test.class, "getOptional");

      assertEquals(Optionals2.unwrapIfOptional(invoked.getReturnType()), String.class);
   }

   public void testReturnTypeOrTypeOfOptionalWhenNotOptional() throws SecurityException, NoSuchMethodException {
      Invokable<?, ?> invoked = method(Test.class, "getNotOptional");

      assertEquals(Optionals2.unwrapIfOptional(invoked.getReturnType()), String.class);
   }

   public void testIsReturnTypeOptionalWhenOptional() throws SecurityException, NoSuchMethodException {
      Invokable<?, ?> invoked = method(Test.class, "getOptional");

      assertTrue(Optionals2.isReturnTypeOptional(invoked));
   }

   public void testIsReturnTypeOptionalWhenNotOptional() throws SecurityException, NoSuchMethodException {
      Invokable<?, ?> invoked = method(Test.class, "getNotOptional");

      assertFalse(Optionals2.isReturnTypeOptional(invoked));
   }
}
