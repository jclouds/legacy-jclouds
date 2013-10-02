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

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createNiceMock;
import static org.jclouds.reflect.Reflection2.typeToken;
import static org.jclouds.util.Throwables2.getFirstThrowableOfType;
import static org.jclouds.util.Throwables2.propagateIfPossible;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.net.SocketException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import org.jclouds.concurrent.TransformParallelException;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpResponseException;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.InsufficientResourcesException;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.TypeToken;
import com.google.inject.CreationException;
import com.google.inject.ProvisionException;
import com.google.inject.spi.Message;

/**
 * @author Adrian Cole
 */
@Test
public class Throwables2Test {

   public void testGetFirstThrowableOfTypeSubclass() {
      SocketException aex = createMock(SocketException.class);
      assertEquals(getFirstThrowableOfType(aex, IOException.class), aex);
   }

   public void testGetFirstThrowableOfTypeOuter() {
      AuthorizationException aex = createMock(AuthorizationException.class);
      assertEquals(getFirstThrowableOfType(aex, AuthorizationException.class), aex);
   }

   public void testGetCause() {
      AuthorizationException aex = createMock(AuthorizationException.class);
      Message message = new Message(ImmutableList.of(), "test", aex);
      ProvisionException pex = new ProvisionException(ImmutableSet.of(message));
      assertEquals(getFirstThrowableOfType(pex, AuthorizationException.class), aex);
   }

   public void testGetFirstThrowableOfTypeInner() {
      AuthorizationException aex = createMock(AuthorizationException.class);
      Message message = new Message(ImmutableList.of(), "test", aex);
      ProvisionException pex = new ProvisionException(ImmutableSet.of(message));
      assertEquals(getFirstThrowableOfType(pex, AuthorizationException.class), aex);
   }

   public void testGetFirstThrowableOfTypeFail() {
      TimeoutException aex = createMock(TimeoutException.class);
      Message message = new Message(ImmutableList.of(), "test", aex);
      ProvisionException pex = new ProvisionException(ImmutableSet.of(message));
      assertEquals(getFirstThrowableOfType(pex, AuthorizationException.class), null);
   }

   public void testGetFirstThrowableOfTypeWhenCauseIsNull() {
      Message message = new Message(ImmutableList.of(), "test", null);
      ProvisionException pex = new ProvisionException(ImmutableSet.of(message));
      assertEquals(getFirstThrowableOfType(pex, AuthorizationException.class), null);
   }

   public void testGetCauseCreation() {
      AuthorizationException aex = createMock(AuthorizationException.class);
      Message message = new Message(ImmutableList.of(), "test", aex);
      CreationException pex = new CreationException(ImmutableSet.of(message));
      assertEquals(getFirstThrowableOfType(pex, AuthorizationException.class), aex);
   }

   public void testGetFirstThrowableOfTypeInnerCreation() {
      AuthorizationException aex = createMock(AuthorizationException.class);
      Message message = new Message(ImmutableList.of(), "test", aex);
      CreationException pex = new CreationException(ImmutableSet.of(message));
      assertEquals(getFirstThrowableOfType(pex, AuthorizationException.class), aex);
   }

   public void testGetFirstThrowableOfTypeFailCreation() {
      TimeoutException aex = createMock(TimeoutException.class);
      Message message = new Message(ImmutableList.of(), "test", aex);
      CreationException pex = new CreationException(ImmutableSet.of(message));
      assertEquals(getFirstThrowableOfType(pex, AuthorizationException.class), null);
   }

   public void testGetFirstThrowableOfTypeWhenCauseIsNullCreation() {
      Message message = new Message(ImmutableList.of(), "test", null);
      CreationException pex = new CreationException(ImmutableSet.of(message));
      assertEquals(getFirstThrowableOfType(pex, AuthorizationException.class), null);
   }

   public void testGetCauseTransformParallel() {
      Exception aex = createMock(AuthorizationException.class);
      TransformParallelException pex = new TransformParallelException(ImmutableMap.<Object, Future<?>> of(),
            ImmutableMap.of("bad", aex), "test");
      assertEquals(getFirstThrowableOfType(pex, AuthorizationException.class), aex);
   }

   public void testGetFirstThrowableOfTypeInnerTransformParallel() {
      Exception aex = createMock(AuthorizationException.class);
      TransformParallelException pex = new TransformParallelException(ImmutableMap.<Object, Future<?>> of(),
            ImmutableMap.of("bad", (Exception) new ExecutionException(aex)), "test");
      assertEquals(getFirstThrowableOfType(pex, AuthorizationException.class), aex);
   }

   public void testGetFirstThrowableOfTypeOuterTransformParallel() {
      Exception aex = createMock(AuthorizationException.class);
      TransformParallelException pex = new TransformParallelException(ImmutableMap.<Object, Future<?>> of(),
            ImmutableMap.of("bad", aex), "test");
      assertEquals(getFirstThrowableOfType(new ExecutionException(pex), AuthorizationException.class), aex);
   }

   public void testGetFirstThrowableOfTypeFailTransformParallel() {
      Exception aex = createMock(TimeoutException.class);
      TransformParallelException pex = new TransformParallelException(ImmutableMap.<Object, Future<?>> of(),
            ImmutableMap.of("bad", aex), "test");
      assertEquals(getFirstThrowableOfType(pex, AuthorizationException.class), null);
   }

   @Test(expectedExceptions = TestException.class)
   public void testPropagateExceptionThatsInList() throws Throwable {
      Exception e = new TestException();
      propagateIfPossible(e, ImmutableSet.<TypeToken<? extends Throwable>> of(typeToken(TestException.class)));
   }

   @Test(expectedExceptions = TestException.class)
   public void testPropagateWrappedExceptionThatsInList() throws Throwable {
      Exception e = new TestException();
      propagateIfPossible(new RuntimeException(e),
            ImmutableSet.<TypeToken<? extends Throwable>> of(typeToken(TestException.class)));
   }

   public void testPropagateIfPossibleDoesnThrowExceptionNotInList() throws Throwable {
      Exception e = new TestException();
      propagateIfPossible(e, ImmutableSet.<TypeToken<? extends Throwable>> of());
   }

   @Test(expectedExceptions = IllegalStateException.class)
   public void testPropagateStandardExceptionIllegalStateException() throws Throwable {
      Exception e = new IllegalStateException();
      propagateIfPossible(new RuntimeException(e), ImmutableSet.<TypeToken<? extends Throwable>> of());
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testPropagateStandardExceptionIllegalArgumentException() throws Throwable {
      Exception e = new IllegalArgumentException();
      propagateIfPossible(new RuntimeException(e), ImmutableSet.<TypeToken<? extends Throwable>> of());
   }

   @Test(expectedExceptions = UnsupportedOperationException.class)
   public void testPropagateStandardExceptionUnsupportedOperationException() throws Throwable {
      Exception e = new UnsupportedOperationException();
      propagateIfPossible(new RuntimeException(e), ImmutableSet.<TypeToken<? extends Throwable>> of());
   }

   @Test(expectedExceptions = AssertionError.class)
   public void testPropagateStandardExceptionAssertionError() throws Throwable {
      AssertionError e = new AssertionError();
      propagateIfPossible(new RuntimeException(e), ImmutableSet.<TypeToken<? extends Throwable>> of());
   }

   @Test(expectedExceptions = AuthorizationException.class)
   public void testPropagateStandardExceptionAuthorizationException() throws Throwable {
      Exception e = new AuthorizationException();
      propagateIfPossible(new RuntimeException(e), ImmutableSet.<TypeToken<? extends Throwable>> of());
   }

   @Test(expectedExceptions = AuthorizationException.class)
   public void testPropagateProvisionExceptionAuthorizationException() throws Throwable {
      Exception e = new AuthorizationException();
      propagateIfPossible(
            new ProvisionException(ImmutableSet.of(new Message(ImmutableList.of(), "Error in custom provider", e))),
            ImmutableSet.<TypeToken<? extends Throwable>> of());
   }

   @Test(expectedExceptions = AuthorizationException.class)
   public void testPropagateCreationExceptionAuthorizationException() throws Throwable {
      Exception e = new AuthorizationException();
      propagateIfPossible(
            new CreationException(ImmutableSet.of(new Message(ImmutableList.of(), "Error in custom provider", e))),
            ImmutableSet.<TypeToken<? extends Throwable>> of());
   }

   @Test(expectedExceptions = InsufficientResourcesException.class)
   public void testPropagateStandardExceptionInsufficientResourcesException() throws Throwable {
      Exception e = new InsufficientResourcesException();
      propagateIfPossible(new RuntimeException(e), ImmutableSet.<TypeToken<? extends Throwable>> of());
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testPropagateStandardExceptionResourceNotFoundException() throws Throwable {
      Exception e = new ResourceNotFoundException();
      propagateIfPossible(new RuntimeException(e), ImmutableSet.<TypeToken<? extends Throwable>> of());
   }

   @Test(expectedExceptions = IllegalStateException.class)
   public void testPropagateStandardExceptionIllegalStateExceptionNestedInHttpResponseException() throws Throwable {
      Exception e = new IllegalStateException();
      propagateIfPossible(new HttpResponseException("goo", createNiceMock(HttpCommand.class), null, e),
            ImmutableSet.<TypeToken<? extends Throwable>> of());
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testPropagateStandardExceptionIllegalArgumentExceptionNestedInHttpResponseException() throws Throwable {
      Exception e = new IllegalArgumentException();
      propagateIfPossible(new HttpResponseException("goo", createNiceMock(HttpCommand.class), null, e),
            ImmutableSet.<TypeToken<? extends Throwable>> of());
   }

   @Test(expectedExceptions = UnsupportedOperationException.class)
   public void testPropagateStandardExceptionUnsupportedOperationExceptionNestedInHttpResponseException()
         throws Throwable {
      Exception e = new UnsupportedOperationException();
      propagateIfPossible(new HttpResponseException("goo", createNiceMock(HttpCommand.class), null, e),
            ImmutableSet.<TypeToken<? extends Throwable>> of());
   }

   @Test(expectedExceptions = AuthorizationException.class)
   public void testPropagateStandardExceptionAuthorizationExceptionNestedInHttpResponseException() throws Throwable {
      Exception e = new AuthorizationException();
      propagateIfPossible(new HttpResponseException("goo", createNiceMock(HttpCommand.class), null, e),
            ImmutableSet.<TypeToken<? extends Throwable>> of());
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testPropagateStandardExceptionResourceNotFoundExceptionNestedInHttpResponseException() throws Throwable {
      Exception e = new ResourceNotFoundException();
      propagateIfPossible(new HttpResponseException("goo", createNiceMock(HttpCommand.class), null, e),
            ImmutableSet.<TypeToken<? extends Throwable>> of());
   }

   @Test(expectedExceptions = HttpResponseException.class)
   public void testPropagateStandardExceptionHttpResponseException() throws Throwable {
      Exception e = new HttpResponseException("goo", createNiceMock(HttpCommand.class), null);
      propagateIfPossible(new RuntimeException(e), ImmutableSet.<TypeToken<? extends Throwable>> of());
   }

   static class TestException extends Exception {
      private static final long serialVersionUID = 1L;
   }
}
