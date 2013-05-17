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
package org.jclouds.rest.suppliers;

import static org.testng.Assert.assertEquals;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

import org.jclouds.rest.AuthorizationException;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.util.concurrent.Atomics;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplierTest")
public class SetAndThrowAuthorizationExceptionSupplierTest {
   @Test
   public void testNormal() {
      AtomicReference<AuthorizationException> authException = Atomics.newReference();
      assertEquals(
            new SetAndThrowAuthorizationExceptionSupplier<String>(Suppliers.ofInstance("foo"), authException).get(),
            "foo");
      assertEquals(authException.get(), null);
   }

   @Test(expectedExceptions = AuthorizationException.class)
   public void testThrowsAuthorizationExceptionAndAlsoSetsExceptionType() {
      AtomicReference<AuthorizationException> authException = Atomics.newReference();
      try {
         new SetAndThrowAuthorizationExceptionSupplier<String>(new Supplier<String>() {

            @Override
            public String get() {
               throw new AuthorizationException();
            }
         }, authException).get();
      } finally {
         assertEquals(authException.get().getClass(), AuthorizationException.class);
      }
   }

   @Test(expectedExceptions = AuthorizationException.class)
   public void testThrowsAuthorizationExceptionAndAlsoSetsExceptionTypeWhenNested() {
      AtomicReference<AuthorizationException> authException = Atomics.newReference();
      try {
         new SetAndThrowAuthorizationExceptionSupplier<String>(new Supplier<String>() {

            @Override
            public String get() {
               throw new RuntimeException(new ExecutionException(new AuthorizationException()));
            }
         }, authException).get();
      } finally {
         assertEquals(authException.get().getClass(), AuthorizationException.class);
      }
   }

   @Test(expectedExceptions = RuntimeException.class)
   public void testThrowsOriginalExceptionAndAlsoSetsExceptionTypeWhenNestedAndNotAuthorizationException() {
      AtomicReference<AuthorizationException> authException = Atomics.newReference();
      try {
         new SetAndThrowAuthorizationExceptionSupplier<String>(new Supplier<String>() {

            @Override
            public String get() {
               throw new RuntimeException(new IllegalArgumentException("foo"));
            }
         }, authException).get();
      } finally {
         assertEquals(authException.get().getClass(), RuntimeException.class);
      }
   }

}
