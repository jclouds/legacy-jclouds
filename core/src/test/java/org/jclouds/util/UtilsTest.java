/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.util;

import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.createNiceMock;
import static org.testng.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.jclouds.domain.Credentials;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpResponseException;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import com.google.inject.ProvisionException;
import com.google.inject.spi.Message;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "jclouds.UtilsTest")
public class UtilsTest {
   public void testRenameKeyWhenNotFound() {
      Map<String, String> nothing = ImmutableMap.of();
      assertEquals(Utils.renameKey(nothing, "foo", "bar"), nothing);
   }

   public void testRenameKeyWhenFound() {
      Map<String, String> nothing = ImmutableMap.of("foo", "bar");
      assertEquals(Utils.renameKey(nothing, "foo", "bar"), ImmutableMap.of("bar", "bar"));
   }

   public void testOverridingCredentialsWhenOverridingIsNull() {
      Credentials defaultCredentials = new Credentials("foo", "bar");
      Credentials overridingCredentials = null;
      assertEquals(Utils.overrideCredentialsIfSupplied(defaultCredentials, overridingCredentials), defaultCredentials);
   }

   public void testOverridingCredentialsWhenOverridingLoginIsNull() {
      Credentials defaultCredentials = new Credentials("foo", "bar");
      Credentials overridingCredentials = new Credentials(null, "baz");
      assertEquals(Utils.overrideCredentialsIfSupplied(defaultCredentials, overridingCredentials), new Credentials(
            "foo", "baz"));
   }

   public void testOverridingCredentialsWhenOverridingCredentialIsNull() {
      Credentials defaultCredentials = new Credentials("foo", "bar");
      Credentials overridingCredentials = new Credentials("fooble", null);
      assertEquals(Utils.overrideCredentialsIfSupplied(defaultCredentials, overridingCredentials), new Credentials(
            "fooble", "bar"));
   }

   public void testOverridingCredentialsWhenOverridingCredentialsAreNull() {
      Credentials defaultCredentials = new Credentials("foo", "bar");
      Credentials overridingCredentials = new Credentials(null, null);
      assertEquals(Utils.overrideCredentialsIfSupplied(defaultCredentials, overridingCredentials), new Credentials(
            "foo", "bar"));
   }

   public void testGetCause() {
      AuthorizationException aex = createMock(AuthorizationException.class);
      Message message = new Message(ImmutableList.of(), "test", aex);
      ProvisionException pex = new ProvisionException(ImmutableSet.of(message));
      assertEquals(Utils.getFirstThrowableOfType(pex, AuthorizationException.class), aex);
   }

   public void testGetFirstThrowableOfTypeOuter() {
      AuthorizationException aex = createMock(AuthorizationException.class);
      assertEquals(Utils.getFirstThrowableOfType(aex, AuthorizationException.class), aex);
   }

   public void testGetFirstThrowableOfTypeInner() {
      AuthorizationException aex = createMock(AuthorizationException.class);
      Message message = new Message(ImmutableList.of(), "test", aex);
      ProvisionException pex = new ProvisionException(ImmutableSet.of(message));
      assertEquals(Utils.getFirstThrowableOfType(pex, AuthorizationException.class), aex);
   }

   public void testGetFirstThrowableOfTypeFail() {
      TimeoutException aex = createMock(TimeoutException.class);
      Message message = new Message(ImmutableList.of(), "test", aex);
      ProvisionException pex = new ProvisionException(ImmutableSet.of(message));
      assertEquals(Utils.getFirstThrowableOfType(pex, AuthorizationException.class), null);
   }

   public void testGetFirstThrowableOfTypeWhenCauseIsNull() {
      Message message = new Message(ImmutableList.of(), "test", null);
      ProvisionException pex = new ProvisionException(ImmutableSet.of(message));
      assertEquals(Utils.getFirstThrowableOfType(pex, AuthorizationException.class), null);
   }

   public void testReplaceTokens() throws UnsupportedEncodingException {
      assertEquals(Utils.replaceTokens("hello {where}", ImmutableMap.of("where", "world")), "hello world");
   }

   public void testMultiMax() {
      Iterable<String> values = ImmutableList.of("1", "2", "2", "3", "3");
      assertEquals(Utils.multiMax(Ordering.natural(), values), ImmutableList.of("3", "3"));
   }

   public void testMultiMax1() {
      Iterable<String> values = ImmutableList.of("1", "2", "2", "3");
      assertEquals(Utils.multiMax(Ordering.natural(), values), ImmutableList.of("3"));
   }

   @Test
   public void testSupportedProviders() {
      Iterable<String> providers = Utils.getSupportedProviders();
      assertEquals(Sets.newLinkedHashSet(providers), ImmutableSet.<String> of());
   }

   static class TestException extends Exception {

      /**
       * 
       */
      private static final long serialVersionUID = 1L;

   }

   @Test
   public void testReturnExceptionThatsInList() throws Exception {
      Exception e = new TestException();
      assertEquals(
            Utils.returnFirstExceptionIfInListOrThrowStandardExceptionOrCause(new Class[] { TestException.class }, e),
            e);
      assertEquals(Utils.returnFirstExceptionIfInListOrThrowStandardExceptionOrCause(
            new Class[] { TestException.class }, new RuntimeException(e)), e);
   }

   @Test(expectedExceptions = TestException.class)
   public void testThrowExceptionNotInList() throws Exception {
      Exception e = new TestException();
      Utils.returnFirstExceptionIfInListOrThrowStandardExceptionOrCause(new Class[] {}, e);
   }

   @Test(expectedExceptions = IllegalStateException.class)
   public void testPropagateStandardExceptionIllegalStateException() throws Exception {
      Exception e = new IllegalStateException();
      assertEquals(
            Utils.returnFirstExceptionIfInListOrThrowStandardExceptionOrCause(new Class[] {}, new RuntimeException(e)),
            e);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testPropagateStandardExceptionIllegalArgumentException() throws Exception {
      Exception e = new IllegalArgumentException();
      assertEquals(
            Utils.returnFirstExceptionIfInListOrThrowStandardExceptionOrCause(new Class[] {}, new RuntimeException(e)),
            e);
   }

   @Test(expectedExceptions = AuthorizationException.class)
   public void testPropagateStandardExceptionAuthorizationException() throws Exception {
      Exception e = new AuthorizationException();
      assertEquals(
            Utils.returnFirstExceptionIfInListOrThrowStandardExceptionOrCause(new Class[] {}, new RuntimeException(e)),
            e);
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testPropagateStandardExceptionResourceNotFoundException() throws Exception {
      Exception e = new ResourceNotFoundException();
      assertEquals(
            Utils.returnFirstExceptionIfInListOrThrowStandardExceptionOrCause(new Class[] {}, new RuntimeException(e)),
            e);
   }

   @Test(expectedExceptions = HttpResponseException.class)
   public void testPropagateStandardExceptionHttpResponseException() throws Exception {
      Exception e = new HttpResponseException("goo", createNiceMock(HttpCommand.class), null);
      assertEquals(
            Utils.returnFirstExceptionIfInListOrThrowStandardExceptionOrCause(new Class[] {}, new RuntimeException(e)),
            e);
   }

}
