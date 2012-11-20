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
package org.jclouds.util;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createNiceMock;
import static org.jclouds.util.Throwables2.getFirstThrowableOfType;
import static org.jclouds.util.Throwables2.returnFirstExceptionIfInListOrThrowStandardExceptionOrCause;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.net.SocketException;
import java.util.Map;
import java.util.concurrent.ExecutionException;
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

   @SuppressWarnings("unchecked")
   public void testGetCauseTransformParallel() {
      Exception aex = createMock(AuthorizationException.class);
      TransformParallelException pex = new TransformParallelException((Map) ImmutableMap.of(), ImmutableMap.of("bad",
               aex), "test");
      assertEquals(getFirstThrowableOfType(pex, AuthorizationException.class), aex);
   }

   @SuppressWarnings("unchecked")
   public void testGetFirstThrowableOfTypeInnerTransformParallel() {
      Exception aex = createMock(AuthorizationException.class);
      TransformParallelException pex = new TransformParallelException((Map) ImmutableMap.of(), ImmutableMap.of("bad",
               (Exception) new ExecutionException(aex)), "test");
      assertEquals(getFirstThrowableOfType(pex, AuthorizationException.class), aex);
   }

   @SuppressWarnings("unchecked")
   public void testGetFirstThrowableOfTypeOuterTransformParallel() {
      Exception aex = createMock(AuthorizationException.class);
      TransformParallelException pex = new TransformParallelException((Map) ImmutableMap.of(), ImmutableMap.of("bad",
               (Exception) aex), "test");
      assertEquals(getFirstThrowableOfType(new ExecutionException(pex), AuthorizationException.class), aex);
   }

   @SuppressWarnings("unchecked")
   public void testGetFirstThrowableOfTypeFailTransformParallel() {
      Exception aex = createMock(TimeoutException.class);
      TransformParallelException pex = new TransformParallelException((Map) ImmutableMap.of(), ImmutableMap.of("bad",
               aex), "test");
      assertEquals(getFirstThrowableOfType(pex, AuthorizationException.class), null);
   }

   @Test
   public void testReturnExceptionThatsInList() throws Exception {
      Exception e = new TestException();
      assertEquals(returnFirstExceptionIfInListOrThrowStandardExceptionOrCause(new Class[] { TestException.class }, e),
               e);
      assertEquals(returnFirstExceptionIfInListOrThrowStandardExceptionOrCause(new Class[] { TestException.class },
               new RuntimeException(e)), e);
   }

   @Test(expectedExceptions = TestException.class)
   public void testThrowExceptionNotInList() throws Exception {
      Exception e = new TestException();
      returnFirstExceptionIfInListOrThrowStandardExceptionOrCause(new Class[] {}, e);
   }

   @Test(expectedExceptions = IllegalStateException.class)
   public void testPropagateStandardExceptionIllegalStateException() throws Exception {
      Exception e = new IllegalStateException();
      returnFirstExceptionIfInListOrThrowStandardExceptionOrCause(new Class[] {}, new RuntimeException(e));
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testPropagateStandardExceptionIllegalArgumentException() throws Exception {
      Exception e = new IllegalArgumentException();
      returnFirstExceptionIfInListOrThrowStandardExceptionOrCause(new Class[] {}, new RuntimeException(e));
   }

   @Test(expectedExceptions = UnsupportedOperationException.class)
   public void testPropagateStandardExceptionUnsupportedOperationException() throws Exception {
      Exception e = new UnsupportedOperationException();
      returnFirstExceptionIfInListOrThrowStandardExceptionOrCause(new Class[] {}, new RuntimeException(e));
   }

   @Test(expectedExceptions = AssertionError.class)
   public void testPropagateStandardExceptionAssertionError() throws Exception {
      AssertionError e = new AssertionError();
      returnFirstExceptionIfInListOrThrowStandardExceptionOrCause(new Class[] {}, new RuntimeException(e));
   }

   @Test(expectedExceptions = AuthorizationException.class)
   public void testPropagateStandardExceptionAuthorizationException() throws Exception {
      Exception e = new AuthorizationException();
      returnFirstExceptionIfInListOrThrowStandardExceptionOrCause(new Class[] {}, new RuntimeException(e));
   }

   @Test(expectedExceptions = AuthorizationException.class)
   public void testPropagateProvisionExceptionAuthorizationException() throws Exception {
      Exception e = new AuthorizationException();
      returnFirstExceptionIfInListOrThrowStandardExceptionOrCause(new Class[] {}, new ProvisionException(ImmutableSet.of(new Message(
               ImmutableList.of(), "Error in custom provider",e))));
   }

   @Test(expectedExceptions = AuthorizationException.class)
   public void testPropagateCreationExceptionAuthorizationException() throws Exception {
      Exception e = new AuthorizationException();
      returnFirstExceptionIfInListOrThrowStandardExceptionOrCause(new Class[] {}, new CreationException(ImmutableSet.of(new Message(
               ImmutableList.of(), "Error in custom provider",e))));
   }

   @Test(expectedExceptions = InsufficientResourcesException.class)
   public void testPropagateStandardExceptionInsufficientResourcesException() throws Exception {
      Exception e = new InsufficientResourcesException();
      returnFirstExceptionIfInListOrThrowStandardExceptionOrCause(new Class[] {}, new RuntimeException(e));
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testPropagateStandardExceptionResourceNotFoundException() throws Exception {
      Exception e = new ResourceNotFoundException();
      returnFirstExceptionIfInListOrThrowStandardExceptionOrCause(new Class[] {}, new RuntimeException(e));
   }

   @Test(expectedExceptions = IllegalStateException.class)
   public void testPropagateStandardExceptionIllegalStateExceptionNestedInHttpResponseException() throws Exception {
      Exception e = new IllegalStateException();
      returnFirstExceptionIfInListOrThrowStandardExceptionOrCause(new Class[] {}, new HttpResponseException("goo",
               createNiceMock(HttpCommand.class), null, e));
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testPropagateStandardExceptionIllegalArgumentExceptionNestedInHttpResponseException() throws Exception {
      Exception e = new IllegalArgumentException();
      returnFirstExceptionIfInListOrThrowStandardExceptionOrCause(new Class[] {}, new HttpResponseException("goo",
               createNiceMock(HttpCommand.class), null, e));
   }

   @Test(expectedExceptions = UnsupportedOperationException.class)
   public void testPropagateStandardExceptionUnsupportedOperationExceptionNestedInHttpResponseException()
            throws Exception {
      Exception e = new UnsupportedOperationException();
      returnFirstExceptionIfInListOrThrowStandardExceptionOrCause(new Class[] {}, new HttpResponseException("goo",
               createNiceMock(HttpCommand.class), null, e));
   }

   @Test(expectedExceptions = AuthorizationException.class)
   public void testPropagateStandardExceptionAuthorizationExceptionNestedInHttpResponseException() throws Exception {
      Exception e = new AuthorizationException();
      returnFirstExceptionIfInListOrThrowStandardExceptionOrCause(new Class[] {}, new HttpResponseException("goo",
               createNiceMock(HttpCommand.class), null, e));
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testPropagateStandardExceptionResourceNotFoundExceptionNestedInHttpResponseException() throws Exception {
      Exception e = new ResourceNotFoundException();
      returnFirstExceptionIfInListOrThrowStandardExceptionOrCause(new Class[] {}, new HttpResponseException("goo",
               createNiceMock(HttpCommand.class), null, e));
   }

   @Test(expectedExceptions = HttpResponseException.class)
   public void testPropagateStandardExceptionHttpResponseException() throws Exception {
      Exception e = new HttpResponseException("goo", createNiceMock(HttpCommand.class), null);
      returnFirstExceptionIfInListOrThrowStandardExceptionOrCause(new Class[] {}, new RuntimeException(e));
   }

   static class TestException extends Exception {

   }

}
